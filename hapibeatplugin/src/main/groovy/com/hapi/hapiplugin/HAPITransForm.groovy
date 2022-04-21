package com.hapi.hapiplugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import com.hapi.hapiplugin.ams.AMSInjectBeat
import org.apache.commons.compress.utils.IOUtils
import org.gradle.api.Project
import java.io.File
import org.apache.commons.io.FileUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class HAPITransForm extends AbsTransForm {

    HAPITransForm(Project project) {
        super(project)
    }

    @Override
    public String getName() {
        return "HAPITransForm"
    }

    @Override
    void transformJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        def jarName = jarInput.name.substring(0, jarInput.name.length() - 4)
        File dest = outputProvider.getContentLocation(jarName + "_dest", jarInput.contentTypes,
                jarInput.scopes, Format.JAR)
        FileUtils.copyFile(jarInput.file, dest)
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(dest))

        JarFile jarFile = new JarFile(jarInput.file)
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries()
        while (null != jarEntryEnumeration && jarEntryEnumeration.hasMoreElements()) {
            JarEntry entry = jarEntryEnumeration.nextElement()
            if (null == entry) {
                continue
            }
            JarEntry newEntry = new JarEntry(entry.getName())
            jos.putNextEntry(newEntry)
            if (entry.isDirectory()) {
                continue
            }

            println("transformJarInput    entry.name "+   entry.name+" "+ entry.class.name+" "+jarInput.file.name+"\n"+jarInput.file.absolutePath)
            InputStream inputStream = jarFile.getInputStream(entry)
            if (Utils.checkStr(entry.name)) {
                AMSInjectBeat.traceJarClass(inputStream, jos)
            }else {
                inputStream.transferTo(jos)
            }
        }
        jos.finish()
        jos.close()
        jarFile.close()

    }

    @Override
    void transformSingleFile(String baseClassPath, File file, File out) {
        println("transformSingleFile src:$file.absolutePath ,\nout:${out.absolutePath} ")
        if (Utils.check(file)) {
            AMSInjectBeat.transformSingleFile(file, out, baseClassPath)
        } else {
            FileUtils.copyFile(file, out);
        }
        // AMSInjectBeat.transformSingleFile(file, out, baseClassPath)
    }
}