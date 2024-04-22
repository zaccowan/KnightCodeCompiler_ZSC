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

    // Class Writer used to write out bytecodes to class file.
    protected ClassWriter cw;
    // Method visitor to dictate what bytecodes are being written to the class.
    protected MethodVisitor mv;
    // Name of the program used for writing out to .class file and for naming the constructor.
    private final String programName;

    /**
     * Constructor to generate boilerplate code need for ASM generated .class to execute.
     * @param programName Name of program.
     */
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

        // Bytecode to establish main method: public static void main (String [] args) {}
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
    }

    /**
     * Used to call write final bytecode boilerplate after visitor has written program writer.
     * Calls final bytecodes to write and calls method to actually write out the file.
     */
    public void writeToFile() {
        mv.visitInsn(Opcodes.RETURN); // Return from main method.
        mv.visitMaxs(0, 0); // maximum stack size and max number of local variable for main.
        mv.visitEnd(); // end the main method.

        cw.visitEnd(); // Termination point for class writer.

        byte[] b = cw.toByteArray(); // Store data created by Class Writer to a byte array

        writeFile(b, "./output/" + programName + ".class"); // Write the byte array out to a class file

        System.out.println("Done!"); // Print Completion Message
    }


    /**
     * Used to call write final bytecode boilerplate after visitor has written program writer.
     * Calls final bytecodes to write and calls method to actually write out the file.
     * @param outputFile Name of file to write out.
     */
    public void writeToFile(String outputFile) {
        mv.visitInsn(Opcodes.RETURN); // Return from main method.
        mv.visitMaxs(0, 0); // maximum stack size and max number of local variable for main.
        mv.visitEnd(); // end the main method.

        cw.visitEnd(); // Termination point for class writer.

        byte[] b = cw.toByteArray(); // Store data created by Class Writer to a byte array

        writeFile(b, "./" + outputFile + ".class"); // Write the byte array out to a class file

        System.out.println("Done!"); // Print Completion Message
    }


    /**
     * Writes the actual bytes to a file.
     * @param bytearray Bytes to write out.
     * @param fileName Name used to write the file to the file system.
     */
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