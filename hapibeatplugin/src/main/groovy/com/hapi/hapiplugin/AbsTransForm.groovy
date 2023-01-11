package com.hapi.hapiplugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.concurrent.Callable

abstract class AbsTransForm extends Transform {

    protected Project mProject

    protected WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();

    AbsTransForm(Project project) {
        this.mProject = project
    }

    //输入类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //生命周期
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //增量
    @Override
    boolean isIncremental() {
        return true
    }

    abstract void transformJarInput(JarInput jarInput, TransformOutputProvider outputProvider)

    abstract void transformSingleFile(String baseClassPath, File file, File out)

    void processDirectoryInputWithIncremental(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        println("输入目录 " + directoryInput.file.absolutePath)
        File dest = outputProvider.getContentLocation(
                directoryInput.getFile().getAbsolutePath(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY)
        if (isIncremental) {
            //处理增量编译
            processDirectoryInputWhenIncremental(directoryInput, dest)
        } else {
            processDirectoryInput(directoryInput, dest)
        }
    }

    //处理非增量文件
    void processDirectoryInput(DirectoryInput directoryInput, File dest) {
        String srcDirPath = directoryInput.getFile().getAbsolutePath()
        String destDirPath = dest.getAbsolutePath()

        ((File) (directoryInput.getFile())).eachFileRecurse { file ->
            waitableExecutor.execute(new Callable<Void>() {
                @Override
                Void call() {
                    if (file.isDirectory()) {
                        return null
                    } else {
                        String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath)
                        File destFile = new File(destFilePath)
                        FileUtils.touch(destFile)
                        transformSingleFile(srcDirPath, file, destFile)
                        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                        // FileUtils.copyFile(file, destFile);
                    }
                    return null
                }
            })
        }
    }

    //处理增量编译
    void processDirectoryInputWhenIncremental(DirectoryInput directoryInput, File dest) {
        FileUtils.forceMkdir(dest)

        String srcDirPath = directoryInput.getFile().getAbsolutePath()
        String destDirPath = dest.getAbsolutePath()
        //变更的文件
        Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()

        fileStatusMap.each { Map.Entry<File, Status> entry ->

            File inputFile = entry.getKey()
            Status status = entry.getValue()
            String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath)
            File destFile = new File(destFilePath)

            switch (status) {
                case Status.NOTCHANGED:
                    break
                case Status.REMOVED:
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    FileUtils.touch(destFile)
                    transformSingleFile(srcDirPath, inputFile, destFile)
                    //  FileUtils.copyFile(inputFile, destFile);
                    break
            }
        }
    }

    void processJarInputWithIncremental(JarInput jarInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR)
        if (isIncremental) {
            //处理增量编译
            switch (jarInput.status) {
                case Status.NOTCHANGED:
                    break
                case Status.ADDED:
                    waitableExecutor.execute(new Callable<Void>() {
                        @Override
                        Void call() {
                            transformJarInput(jarInput, outputProvider)
                            return null
                        }
                    })
                    break
                case Status.CHANGED:
                    //处理有变化的
                    waitableExecutor.execute(new Callable<Void>() {
                        @Override
                        Void call() {
                            transformJarInput(jarInput, outputProvider)
                            return null
                        }
                    })
                    break
                case Status.REMOVED:
                    //移除Removed
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    break
            }

        } else {
            //不处理增量编译
            waitableExecutor.execute(new Callable<Void>() {

                @Override
                Void call() {
                    transformJarInput(jarInput, outputProvider)
                    return null
                }
            })
        }
    }

    boolean needTransform() {
        def hapi = mProject.hapi
        def isOpen = hapi.isOpen
        return isOpen && !isReleaseBuildType()
    }

    boolean isReleaseBuildType() {
        for (String s : mProject.gradle.startParameter.taskNames) {
            if (s.contains("Release") | s.contains("release")) {
                return true
            }
        }
        return false
    }

    boolean needJarTransform(JarInput jarInput) {

        Set<QualifiedContent.Scope> soups = jarInput.scopes
        def soup = soups.toArray()[0]
        def hapi = mProject.hapi
        def isOpen = hapi.isOpen
        String[] whiteJarArray
        whiteJarArray = hapi.jarList.split(",")
        Utils.jarList = whiteJarArray
        if (whiteJarArray != null && whiteJarArray.length != 0) {
            for (String s : whiteJarArray) {
                def eq = (jarInput.file.name.startsWith(s) || jarInput.file.name == s) && !s.isEmpty()
                def eq2 = soup == QualifiedContent.Scope.SUB_PROJECTS && jarInput.file.absolutePath.contains(s) && !s.isEmpty()
                //  println("jarInput.name " + jarInput.file.name + "    s " + s + " " + eq)
                if (eq || eq2) {
                    println("jarInput.name " + jarInput.file.name + "    s " + s + " " + (eq || eq2))
                    return true
                }
            }
        }
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("AbsTransForm  start transform")

        if (needTransform()) {
            boolean isIncremental = transformInvocation.isIncremental()
            //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
            if (!isIncremental) {
                //不需要增量编译，先清除全部
                outputProvider.deleteAll()
            }

            transformInvocation.getInputs().each { TransformInput input ->
                input.jarInputs.each { JarInput jarInput ->
                    //处理Jar
                    // if (false) {
                    println("输入jar " + jarInput.name + "  " + jarInput.file.absolutePath)
                    if (this.needJarTransform(jarInput)) {
                        processJarInputWithIncremental(jarInput, outputProvider, isIncremental)
                    } else {
                        //不处理jar文件
                        //  重命名输出文件（同目录copyFile会冲突）
                        def jarName = jarInput.name
                        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        FileUtils.copyFile(jarInput.file, dest)
                    }
                }

                input.directoryInputs.each { DirectoryInput directoryInput ->
                    //处理文件
                    processDirectoryInputWithIncremental(directoryInput, outputProvider, isIncremental)
                }
            }

        } else {

            boolean isIncremental = transformInvocation.isIncremental();
            //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
            Collection<TransformInput> inputs = transformInvocation.getInputs();
            //引用型输入，无需输出。
            Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
            //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

            for (TransformInput input : inputs) {
                for (JarInput jarInput : input.getJarInputs()) {
                    //输出路径
                    File dest = outputProvider.getContentLocation(
                            jarInput.getFile().getAbsolutePath(),
                            jarInput.getContentTypes(),
                            jarInput.getScopes(),
                            Format.JAR);
                    //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                    FileUtils.copyFile(jarInput.getFile(), dest);
                }
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {

                    File dest = outputProvider.getContentLocation(directoryInput.getName(),
                            directoryInput.getContentTypes(), directoryInput.getScopes(),
                            Format.DIRECTORY);
                    //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                }
            }
        }

        waitableExecutor.waitForTasksWithQuickFail(true);
    }

}