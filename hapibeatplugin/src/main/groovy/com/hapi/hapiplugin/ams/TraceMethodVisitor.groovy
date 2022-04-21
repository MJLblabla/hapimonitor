package com.hapi.hapiplugin.ams

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

class TraceMethodVisitor extends AdviceAdapter {

    def cost = 0
    def needBeat = 0
    def methodKey = ""

    protected TraceMethodVisitor(String className,int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
        methodKey = className+":"+name
        println("TraceMethodVisitor   -------   $name")
    }

    @Override
    void onMethodEnter() {
        println("onMethodEnter ")
        super.onMethodEnter()
        needBeat = newLocal(Type.BOOLEAN_TYPE)
        cost = newLocal(Type.INT_TYPE)

        visitMethodInsn(INVOKESTATIC, "com/hapi/blockmonitor/MethodBeater", "checkDeep", "()Z", false);
        visitVarInsn(ISTORE, needBeat);

        visitInsn(ICONST_0);
        visitVarInsn(ISTORE, cost);

        visitVarInsn(ILOAD, needBeat);
        Label label3 = new Label();
        visitJumpInsn(IFEQ, label3);
        visitMethodInsn(INVOKESTATIC, "com/hapi/blockmonitor/MethodBeater", "getTime", "()I", false);
        visitInsn(INEG);
        visitVarInsn(ISTORE, cost);
        visitLabel(label3);
    }


    @Override
    void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        println("onMethodExit  ")

        visitVarInsn(ILOAD, needBeat);

        Label label6 = new Label(); //{
        visitJumpInsn(IFEQ, label6);
//        Label label7 = new Label();
//        visitLabel(label7);
//        visitLineNumber(20, label7);
        visitMethodInsn(INVOKESTATIC, "com/hapi/blockmonitor/MethodBeater", "getTime", "()I", false);
        visitVarInsn(ILOAD, cost);
        visitInsn(IADD);
        visitVarInsn(ISTORE, cost);
//        Label label8 = new Label();
//        visitLabel(label8);
//        visitLineNumber(21, label8);


        visitTypeInsn(NEW, "com/hapi/monitor/Beat");
        visitInsn(DUP);
        visitVarInsn(ILOAD, cost);

        visitLdcInsn(methodKey);


        def beat = newLocal(Type.getObjectType("com/hapi/monitor/Beat"))

        visitMethodInsn(INVOKESPECIAL, "com/hapi/monitor/Beat", "<init>", "(ILjava/lang/String;)V", false);
        visitVarInsn(ASTORE, beat);
//        Label label9 = new Label();
//        visitLabel(label9);
//        visitLineNumber(22, label9);
        visitVarInsn(ALOAD, beat);
        visitMethodInsn(INVOKESTATIC, "com/hapi/blockmonitor/MethodBeater", "addBeat", "(Lcom/hapi/monitor/Beat;)V", false);
        visitLabel(label6);
        visitLineNumber(24, label6); //}
       // visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      //  visitInsn(RETURN);

    }

}