package com.hapi.hapiplugin.ams


import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarOutputStream


class AMSInjectBeat {

    static void transformSingleFile(File inputFile, File outputFile, String srcBaseDir) {
        traceClass(inputFile, outputFile)
    }

    static void traceJarClass(InputStream inputStream, JarOutputStream jarOutputStream) {

        def cr = new ClassReader(inputStream)
        def cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
        //真正处理字节码，LifecycClassVisitor类里面有被处理类的信息
        cr.accept(new TraceClassVisitor(cw), ClassReader.EXPAND_FRAMES)
        //处理后的字节码
        def bytes = cw.toByteArray()
        jarOutputStream.write(bytes)
    }

    //处理文件
    private static void traceClass(File input, File output) {
        println("traceClass:" + input.absolutePath)
        println("output:" + output.absolutePath + "  " + output.exists())

        def fis = new FileInputStream(input)
        def cr = new ClassReader(fis)
        def cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
        //真正处理字节码，LifecycClassVisitor类里面有被处理类的信息
        cr.accept(new TraceClassVisitor(cw), ClassReader.EXPAND_FRAMES)
        //处理后的字节码
        def bytes = cw.toByteArray()

        def fos = new FileOutputStream(output)
        fos.write(bytes)
        fos.close()
    }
}
