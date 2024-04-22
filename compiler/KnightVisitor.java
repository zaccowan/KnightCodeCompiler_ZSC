package compiler;

import lexparse.KnightCodeBaseVisitor;
import lexparse.KnightCodeParser;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * KnightVisitor is responsible for walking the antlr generated parse tree and respective ASM calls to create bytecode.
 * @author Zac Cowan
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 **/
public class KnightVisitor extends KnightCodeBaseVisitor<Object> {

    /**
     * Default constructor
     */
    KnightVisitor() {}
    /**
     *Constructor to use if and output file location and name is specified in command line call
     */
    KnightVisitor(String outputFile) {
        this.outputFile = outputFile;
    }



    /**
     * ASM generation object used to access method visitor and make bytecode write calls.
     */
    private AsmGen generator;
    /**
     * Symbol table used to store variable representations
     */
    private SymbolTable symbolTable = new SymbolTable();
    /**
     * Output file location a name that a kcc command line user may supply.
     */
    private String outputFile = null;

    /**
     * Takes in a symbol and either loads it using the symbol table representation or load it as a constant.
     */
    public void loadIntegerOperand(String operand) {
        // if the symbol is in the symbol table, get its varIndex and load it as an integer.
        if( symbolTable.contains(operand)) {
            generator.mv.visitVarInsn(Opcodes.ILOAD, symbolTable.getEntry(operand).getVarIndex());
        }
        // If a symbol is not in the symbol table, try to load it as an integer constant
        else {
            generator.mv.visitLdcInsn(Integer.valueOf(operand));
        }
    }

    /**
     * Prints all elements in symbol table. Is not critical to compiler, I just think it's cool.
     */
    public void printAll() {
        symbolTable.printAll();
    }

    /**
     * Var Index value that is incremented every time a String or Integer is initialized.
     * Starts at 3 to reserve spots at the beginning for instances of scanners, ...
     * Note: if bigger data types were added you would probably want to increment by a larger value.
     */
    int addressIndex = 3;


    /**
     * Ends the code generation by calling the generators class .writeToFile()
     * Allows for a command line user to supply and optional output location and name.
     */
    public void end() {
        //
        if( outputFile == null) {
            generator.writeToFile();
        } else {
            generator.writeToFile(outputFile);
        }
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFile(KnightCodeParser.FileContext ctx) {
        // Get the program name from the file header and use it to initialize compiled .class files constructor in AsmGen
        String programName = ctx.getChild(1).getText();
        generator = new AsmGen(programName);
        return super.visitFile(ctx);
    }

    /**
     * Collect the variable types and symbols and put them in the symbol table with an index provided by addressIndex.
     * Nothing is stored at the var index by default, but the declaration reserves it a sport for later initialization.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitVariable(KnightCodeParser.VariableContext ctx) {
        // Increment address index to prevent collisions of two variables.
        addressIndex++;

        // Get the type of the variable from the parse tree and initialize variable correspondingly.
        int type = Variable.TYPE_STRING;
        switch (ctx.vartype().getText()) {
            case "INTEGER":
                type = Variable.TYPE_INTEGER;
                break;
            case "STRING":
                type = Variable.TYPE_STRING;
                break;
            case "LONG":
                // For implementing long type in future.
                break;
            // Continue pattern to expand variable types

        }
        // Add new variable to symbol table.
        symbolTable.declareEntry(ctx.identifier().getText(), type, addressIndex);
        return super.visitVariable(ctx);
    }


    /**
     * Where setting of declared variables occurs. Stores values at var index defined by the symbol tables entry.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSetvar(KnightCodeParser.SetvarContext ctx) {
        // Get the variable symbol program wants to set
        String symbolToSet = ctx.getChild(1).getText();
        // Get the symbols Variable object.
        Variable varToSet = symbolTable.getEntry(symbolToSet);

        // If the third child of the parse tree has 0 children, it is a String literal.
        // Store it as so.
        if( ctx.getChild(3).getChildCount() == 0 ) {
            String operand = ctx.getChild(3).getText();
            if( operand.charAt(0) == '\"') {
                String value = ctx.getChild(3).getText().substring(1, ctx.getChild(3).getText().length()-1);
                generator.mv.visitLdcInsn(value);
            } else {
                generator.mv.visitVarInsn(Opcodes.ALOAD, symbolTable.getEntry(operand).getVarIndex());
            }
            generator.mv.visitVarInsn(Opcodes.ASTORE, varToSet.getVarIndex());
        }
        // Otherwise, visit the assignment value.
        // Note: this will call whatever visit is present in the expr. For example, if a number is counted visitNumber is called.
        else {
            visit(ctx.getChild(3));
            // Since Strings will not be found in an expression, and only integers are allowed,
            // store the result of the recursively visited parse tree at the index of our desired set variable.
            generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
        }
        return null;
    }


    /**
     *
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitParenthesis(KnightCodeParser.ParenthesisContext ctx) {
        /* Parenthesis auto group operator and operands in the parse tree into an expr. So visit the expr */
        visit(ctx.getChild(1));
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitMultiplication(KnightCodeParser.MultiplicationContext ctx) {

        // Visit the two operands to load them.
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
        // Call IMUL to multiply the two loaded integers.
        generator.mv.visitInsn(Opcodes.IMUL);
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitAddition(KnightCodeParser.AdditionContext ctx) {
        // Visit the two operands to load them.
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
        // Call IADD to add the two loaded integers.
        generator.mv.visitInsn(Opcodes.IADD);
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSubtraction(KnightCodeParser.SubtractionContext ctx) {
        // Visit the two operands to load them.
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
        // Call ISUB to subtract the two loaded integers.
        generator.mv.visitInsn(Opcodes.ISUB);
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitDivision(KnightCodeParser.DivisionContext ctx) {
        // Visit the two operands to load them.
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
        // Call IDIV to divide the two loaded integers.
        generator.mv.visitInsn(Opcodes.IDIV);
        return null;
    }

    /**
     * Visit variable and load it from its varIndex.
     * String loading is handled in PRINT since that is the only time we ever load a STRING in KnightCode.
     * If you added string operations, you would want to pull that logic here.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitId(KnightCodeParser.IdContext ctx) {
        String symbol = ctx.getText();
        if( symbolTable.contains(symbol)) {
            generator.mv.visitVarInsn(Opcodes.ILOAD, symbolTable.getEntry(symbol).getVarIndex());
        } else {
            System.err.println("Variable, " +  symbol + ", not declared.");
        }
        return super.visitId(ctx);
    }

    /**
     * Visit Number loads a number when needed, be it an expression or assignment.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitNumber(KnightCodeParser.NumberContext ctx) {
        // Load a number as constant
        // What happens with the number depends on the surrounding context of the parse tree.
        String value = ctx.getText();
        generator.mv.visitLdcInsn(Integer.valueOf(value));
        return super.visitNumber(ctx);
    }

    /**
     * Performs comparisons with two numbers and loads either 1 or 0 based on true or false.
     * Allows integer variables to be assigned a value by a comparison.
     * For example, SET x:= 4 (less than) 1 would set x to 1, since the statement is true.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitComparison(KnightCodeParser.ComparisonContext ctx) {
        // Load the operands by visiting the node.
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));

        Label elseLabel = new Label();
        Label endLabel = new Label();
        // Performs the comparison operation based on the operator in the token.
        switch (ctx.getChild(1).getText()) {
            case ">":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPLE, elseLabel);
                break;
            case "<":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPGE, elseLabel);
                break;
            case "=":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPNE, elseLabel);
                break;
            case "<>":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elseLabel);
                break;
        }
        //Loads a 1 or 0 depending on truth of the statement.
        generator.mv.visitLdcInsn(1);
        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        generator.mv.visitLabel(elseLabel);
        generator.mv.visitLdcInsn(0);
        generator.mv.visitLabel(endLabel);
        return null;
    }




    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitPrint(KnightCodeParser.PrintContext ctx) {

        // Handle cases where a variable is the value of print call
        if( symbolTable.contains(ctx.getChild(1).getText()) ) {
            // Get and load the variable.
            Variable variable = symbolTable.getEntry( ctx.getChild(1).getText());
            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

            // Call println based on the data type.
            switch(variable.getVarType()) {
                case Variable.TYPE_INTEGER:
                    generator.mv.visitVarInsn(Opcodes.ILOAD, variable.getVarIndex());
                    generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
                    break;
                case Variable.TYPE_STRING:
                    generator.mv.visitVarInsn(Opcodes.ALOAD, variable.getVarIndex());
                    generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    break;
                default:
                    break;
            }

        }
        // Handle cases when a string literal is given.
        else {
            addressIndex++;
            // Strip the literal of quotation marks
            String printVal = ctx.getChild(1).getText().substring(1, ctx.getChild(1).getText().length()-1);

            // Load and print the string literal as a constant.
            // Does not store the literal anywhere.
            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            generator.mv.visitLdcInsn(printVal);
            generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        return super.visitPrint(ctx);
    }

    /**
     * Visit Read allows a variable to be set based on user input.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitRead(KnightCodeParser.ReadContext ctx) {

        if( symbolTable.contains(ctx.getChild(1).getText()) ) {
            Variable variable = symbolTable.getEntry( ctx.getChild(1).getText());

            // Create a scanner object and store it in reserved space set apart by addressIndex starting at 3
            generator.mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
            generator.mv.visitInsn(Opcodes.DUP);

            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;" );
            generator.mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
            generator.mv.visitVarInsn(Opcodes.ASTORE, 0); // Stores the Scanner object at index 0 in the reserved space.

            // Scans user keyboard input based on the type of the variable to be set.
            switch(variable.getVarType()) {
                case Variable.TYPE_INTEGER:
                    generator.mv.visitVarInsn(Opcodes.ALOAD, 0);
                    generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
                    generator.mv.visitVarInsn(Opcodes.ISTORE, variable.getVarIndex());
                    break;
                case Variable.TYPE_STRING:
                    generator.mv.visitVarInsn(Opcodes.ALOAD, 0);
                    generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
                    generator.mv.visitVarInsn(Opcodes.ASTORE, variable.getVarIndex());
                    break;
                default:
                    break;
            }

        }
        return super.visitRead(ctx);
    }

    /**
     * Handles conditional branches. Does not support ELSE IF, only IF alone or both IF and ELSE.
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitDecision(KnightCodeParser.DecisionContext ctx) {
        // Get the operands and operator from the tree.
        String operand1 = ctx.getChild(1).getText();
        String operand2 = ctx.getChild(3).getText();
        String operand = ctx.getChild(2).getText();

        // Manually load the numbers because for some reason visiting the node would not call visitNumber or visitId
        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

        Label elseLabel = new Label();
        Label endLabel = new Label();

        // Call the operation that corresponds to the conditional operator.
        switch( operand ){
            case ">":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPLE, elseLabel);
                break;
            case "<":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPGE, elseLabel);
                break;
            case "=":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPNE, elseLabel);
                break;
            case "<>":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elseLabel);
                break;
        }

        // Determine the range of children for the if block in the parse tree.
        int endOfIfBlock = 5;
        while( !ctx.getChild(endOfIfBlock).getText().equals("ELSE") && !ctx.getChild(endOfIfBlock).getText().equals("ENDIF") ){
            endOfIfBlock++;
        }

        // Calls the children inside the if block.
        for(int i = 5 ; i < endOfIfBlock ; i++) {
            visit(ctx.getChild(i));
        }

        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        generator.mv.visitLabel(elseLabel);
        // Calls the children of the else block, if they exist.
        for(int i = endOfIfBlock ; i < ctx.getChildCount()-1 ; i++) {
            visit(ctx.getChild(i));
        }

        generator.mv.visitLabel(endLabel);

        return null;
    }

    /**
     * Performs repetition for KnightCode
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitLoop(KnightCodeParser.LoopContext ctx) {
        // Load the conditions for the loop.
        String operand1 = ctx.getChild(1).getText();
        String operand2 = ctx.getChild(3).getText();
        String operand = ctx.getChild(2).getText();

        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

        Label endLabel = new Label();
        Label whileStartLabel = new Label();

        // Start of the while loop label
        generator.mv.visitLabel(whileStartLabel);
        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

        // check condition of while loop conditional
        switch( operand ){
            case ">":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPLE, endLabel);
                break;
            case "<":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPGE, endLabel);
                break;
            case "=":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPNE, endLabel);
                break;
            case "<>":
                generator.mv.visitJumpInsn(Opcodes.IF_ICMPEQ, endLabel);
                break;
        }

        // Visit the children of the loop.
        for(int i = 5 ; i < ctx.getChildCount()-1 ; i++) {
            visit(ctx.getChild(i));
        }

        // Jump back to start if another loop needs to occur.
        generator.mv.visitJumpInsn(Opcodes.GOTO, whileStartLabel);

        generator.mv.visitLabel(endLabel);

        generator.mv.visitInsn(Opcodes.RETURN);

        return null;
    }
}