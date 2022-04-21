package com.hapi.hapiplugin.ams

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import com.hapi.hapiplugin.ams.TraceMethodVisitor

class TraceClassVisitor extends ClassVisitor implements Opcodes {

    String className
    ClassVisitor cv

    TraceClassVisitor(ClassVisitor cv) {
        super(ASM5, cv)
        this.cv = cv;
    }
    //拿到类的信息， 然后对满足条件的类进行过滤
    @Override
    void visit(int version,
               int access,
               String name,
               String signature,
               String superName,
               String[] interfaces) {

        super.visit(version, access, name, signature, superName, interfaces)
        println("visit  start" + name)
        className = name
    }

    //类的方法信息, 拿到需要修改的方法，然后进行修改操作
    @Override
    MethodVisitor visitMethod(int access, String name,
                              String desc,
                              String signature,
                              String[] exceptions) {

        def mv = cv.visitMethod(access, name, desc, signature, exceptions)
        boolean isHandler = (name != "<init>" && name != "<clinit>")
        println("visitMethod name:$name,className:${className} "+isHandler)
        if (!isHandler) {
            return mv
        }
        println(" go TraceMethodVisitor")
        return new TraceMethodVisitor(className, ASM5, mv, access, name, desc)
    }

    @Override
    void visitEnd() {
        println("$className ....visitEnd")
        super.visitEnd()
    }
}