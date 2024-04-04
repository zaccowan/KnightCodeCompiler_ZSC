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
    public void loadStringOperand(String operand) {
        if( symbolTable.contains(operand)) {
            generator.mv.visitVarInsn(Opcodes.ALOAD, symbolTable.getEntry(operand).getVarIndex());
        }else {
            generator.mv.visitLdcInsn(operand);
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
    public Object visitDeclare(KnightCodeParser.DeclareContext ctx) {
        return super.visitDeclare(ctx);
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
    public Object visitIdentifier(KnightCodeParser.IdentifierContext ctx) {
        System.out.println(ctx.ID().getText());
        return super.visitIdentifier(ctx);
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
    public Object visitVartype(KnightCodeParser.VartypeContext ctx) {
        return super.visitVartype(ctx);
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
    public Object visitBody(KnightCodeParser.BodyContext ctx) {
        return super.visitBody(ctx);
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
    public Object visitStat(KnightCodeParser.StatContext ctx) {
        return super.visitStat(ctx);
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

        if( ctx.getChild(3).getChildCount() >0 && ctx.expr().getChildCount() == 1 ) {
            String value = ctx.expr().getText();
            loadIntegerOperand(value);
            generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
        }
        else if( ctx.getChild(3).getText().charAt(0) == '\"') {
            String value = ctx.getChild(3).getText().substring(1, ctx.getChild(3).getText().length()-1);
            loadStringOperand(value);
            generator.mv.visitVarInsn(Opcodes.ASTORE, varToSet.getVarIndex());
        }
        else if( ctx.expr().getChildCount() == 3) {
            String value1 = ctx.expr().getChild(0).getText();
            String value2 = ctx.expr().getChild(2).getText();

            loadIntegerOperand(value1);
            loadIntegerOperand(value2);

            String operation = ctx.expr().getChild(1).getText();
            switch (operation){
                case "+":
                    generator.mv.visitInsn(Opcodes.IADD);
                    break;
                case "-":
                    generator.mv.visitInsn(Opcodes.ISUB);
                    break;
                case "*":
                    generator.mv.visitInsn(Opcodes.IMUL);
                    break;
                case "/":
                    generator.mv.visitInsn(Opcodes.IDIV);
                    break;
                case ">":
                    {
                        System.out.print("comparison");
                        Label elseLabel = new Label();
                        Label endLabel = new Label();
                        generator.mv.visitJumpInsn(Opcodes.IF_ICMPLE, elseLabel);

                        generator.mv.visitLdcInsn(1);
                        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

                        generator.mv.visitLabel(elseLabel);
                        generator.mv.visitLdcInsn(0);

                        generator.mv.visitLabel(endLabel);
                        generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
                    }
                    break;
                case "<":
                    {
                        Label elseLabel = new Label();
                        Label endLabel = new Label();
                        generator.mv.visitJumpInsn(Opcodes.IF_ICMPGE, elseLabel);

                        generator.mv.visitLdcInsn(1);
                        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

                        generator.mv.visitLabel(elseLabel);
                        generator.mv.visitLdcInsn(0);

                        generator.mv.visitLabel(endLabel);
                        generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
                    }
                    break;
                case "=":
                    {
                        Label elseLabel = new Label();
                        Label endLabel = new Label();
                        generator.mv.visitJumpInsn(Opcodes.IF_ICMPNE, elseLabel);

                        generator.mv.visitLdcInsn(1);
                        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

                        generator.mv.visitLabel(elseLabel);
                        generator.mv.visitLdcInsn(0);

                        generator.mv.visitLabel(endLabel);
                        generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
                    }
                    break;
                case "<>":
                    {
                        Label elseLabel = new Label();
                        Label endLabel = new Label();
                        generator.mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elseLabel);

                        generator.mv.visitLdcInsn(1);
                        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

                        generator.mv.visitLabel(elseLabel);
                        generator.mv.visitLdcInsn(0);

                        generator.mv.visitLabel(endLabel);
                        generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
                    }
                    break;
            }

            generator.mv.visitVarInsn(Opcodes.ISTORE, varToSet.getVarIndex());
        }
        return super.visitSetvar(ctx);
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
        return super.visitParenthesis(ctx);
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
        return super.visitMultiplication(ctx);
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
        return super.visitAddition(ctx);
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
        return super.visitSubtraction(ctx);
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
        return super.visitComparison(ctx);
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
        return super.visitDivision(ctx);
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
        return super.visitComp(ctx);
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
        switch( operand){
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

        visit(ctx.getChild(5));
        generator.mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        generator.mv.visitLabel(elseLabel);
        visit(ctx.getChild(7).getChild(0));

        generator.mv.visitLabel(endLabel);





        return super.visitDecision(ctx);
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
        return super.visitLoop(ctx);
    }
}