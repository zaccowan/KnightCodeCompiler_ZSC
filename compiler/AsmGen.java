package compiler;

import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * AsmGen is responsible for generating the boilerplate bytecode for a class.
 * @author Zac Cowan
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 **/
public class AsmGen {

    protected ClassWriter cw;
    protected MethodVisitor mv;
    private final String programName;

    public AsmGen(String programName) {
        this.programName = programName;


        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // New ClassWriter where stack map frames are automatically computed from scratch. (Per ASM Documentation)
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, programName, null, "java/lang/Object", null);

        {   // Setup for Constructor.
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            mv.visitInsn(Opcodes.RETURN); // Return from class
            mv.visitMaxs(1, 1); // max stack size of 1 and max number of local variable of 1
            mv.visitEnd(); // visit end of class
        }

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
    }

    public void writeToFile() {
        mv.visitInsn(Opcodes.RETURN); // Return from main method.
        mv.visitMaxs(0, 0); // maximum stack size and max number of local variable for main.
        mv.visitEnd(); // end the main method.

        cw.visitEnd(); // Termination point for class writer.

        byte[] b = cw.toByteArray(); // Store data created by Class Writer to a byte array

        writeFile(b, "./output/" + programName + ".class"); // Write the byte array out to a class file

        System.out.println("Done!"); // Print Completion Message
    }

    public void writeToFile(String outputFile) {
        mv.visitInsn(Opcodes.RETURN); // Return from main method.
        mv.visitMaxs(0, 0); // maximum stack size and max number of local variable for main.
        mv.visitEnd(); // end the main method.

        cw.visitEnd(); // Termination point for class writer.

        byte[] b = cw.toByteArray(); // Store data created by Class Writer to a byte array

        writeFile(b, "./" + outputFile + ".class"); // Write the byte array out to a class file

        System.out.println("Done!"); // Print Completion Message
    }

    public static void writeFile(byte[] bytearray, String fileName) {

        try {
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(bytearray);
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

}