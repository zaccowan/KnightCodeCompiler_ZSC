package compiler;

import lexparse.KnightCodeBaseVisitor;
import lexparse.KnightCodeParser;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class KnightVisitor extends KnightCodeBaseVisitor<Object> {

    AsmGen generator;
    SymbolTable symbolTable = new SymbolTable();

    public void loadIntegerOperand(String operand) {
        if( symbolTable.contains(operand)) {
            generator.mv.visitVarInsn(Opcodes.ILOAD, symbolTable.getEntry(operand).getVarIndex());
        }else {
            generator.mv.visitLdcInsn(Integer.valueOf(operand));
        }
    }

    public void printAll() {
        symbolTable.printAll();
    }

    int addressIndex = 3; // Starts at 3 to reserve spots at the beginning for instances of scanners, ...

    public void end() {
        generator.writeToFile();
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
        String programName = ctx.getChild(1).getText();
        generator = new AsmGen(programName);
        return super.visitFile(ctx);
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
    public Object visitVariable(KnightCodeParser.VariableContext ctx) {
        addressIndex++;
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
        symbolTable.declareEntry(ctx.identifier().getText(), type, addressIndex);
        return super.visitVariable(ctx);
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
    public Object visitSetvar(KnightCodeParser.SetvarContext ctx) {
        String symbolToSet = ctx.getChild(1).getText();
        Variable varToSet = symbolTable.getEntry(symbolToSet);

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
        else {
            visit(ctx.getChild(3));
            generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
        }

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
    public Object visitParenthesis(KnightCodeParser.ParenthesisContext ctx) {
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
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
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
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
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
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
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
    public Object visitNumber(KnightCodeParser.NumberContext ctx) {
        String value = ctx.getText();
        System.out.println(value);
        generator.mv.visitLdcInsn(Integer.valueOf(value));
//        loadIntegerOperand(value);
        return super.visitNumber(ctx);
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
    public Object visitComparison(KnightCodeParser.ComparisonContext ctx) {
        System.out.println("called");
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));

        Label elseLabel = new Label();
        Label endLabel = new Label();
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
    public Object visitDivision(KnightCodeParser.DivisionContext ctx) {
        visit(ctx.getChild(0));
        visit(ctx.getChild(2));
        generator.mv.visitInsn(Opcodes.IDIV);
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
    public Object visitId(KnightCodeParser.IdContext ctx) {
        String symbol = ctx.getText();
        System.out.println(symbol);
        if( symbolTable.contains(symbol)) {
            generator.mv.visitVarInsn(Opcodes.ILOAD, symbolTable.getEntry(symbol).getVarIndex());
        }
        return super.visitId(ctx);
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
    public Object visitComp(KnightCodeParser.CompContext ctx) {
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
        String printVal;

        if( symbolTable.contains(ctx.getChild(1).getText()) ) {
            Variable variable = symbolTable.getEntry( ctx.getChild(1).getText());
            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

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

        } else {
            addressIndex++;
            printVal = ctx.getChild(1).getText().substring(1, ctx.getChild(1).getText().length()-1);

            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            generator.mv.visitLdcInsn(printVal);
            generator.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        return super.visitPrint(ctx);
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
    public Object visitRead(KnightCodeParser.ReadContext ctx) {
        if( symbolTable.contains(ctx.getChild(1).getText()) ) {
            Variable variable = symbolTable.getEntry( ctx.getChild(1).getText());
            generator.mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
            generator.mv.visitInsn(Opcodes.DUP);

            generator.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;" );
            generator.mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
            generator.mv.visitVarInsn(Opcodes.ASTORE, 0);


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
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitDecision(KnightCodeParser.DecisionContext ctx) {
        String operand1 = ctx.getChild(1).getText();
        String operand2 = ctx.getChild(3).getText();
        String operand = ctx.getChild(2).getText();

        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

        Label elseLabel = new Label();
        Label endLabel = new Label();

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

        int endOfIfBlock = 5;
        while( !ctx.getChild(endOfIfBlock).getText().equals("ELSE") && !ctx.getChild(endOfIfBlock).getText().equals("ENDIF") ){
            endOfIfBlock++;
        }
        System.out.println(endOfIfBlock);

        for(int i = 5 ; i < endOfIfBlock ; i++) {
            visit(ctx.getChild(i));
        }

        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        generator.mv.visitLabel(elseLabel);
        for(int i = endOfIfBlock ; i < ctx.getChildCount()-1 ; i++) {
            visit(ctx.getChild(i));
        }

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
    public Object visitLoop(KnightCodeParser.LoopContext ctx) {
        String operand1 = ctx.getChild(1).getText();
        String operand2 = ctx.getChild(3).getText();
        String operand = ctx.getChild(2).getText();

        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

        Label endLabel = new Label();
        Label whileStartLabel = new Label();

        generator.mv.visitLabel(whileStartLabel);
        loadIntegerOperand(operand1);
        loadIntegerOperand(operand2);

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


        for(int i = 5 ; i < ctx.getChildCount()-1 ; i++) {
            visit(ctx.getChild(i));
        }
        generator.mv.visitJumpInsn(Opcodes.GOTO, whileStartLabel);

        generator.mv.visitLabel(endLabel);

        generator.mv.visitInsn(Opcodes.RETURN);

        return null;
    }
}