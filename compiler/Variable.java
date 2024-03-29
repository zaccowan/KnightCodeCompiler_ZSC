package compiler;

public class Variable {

    // Built in Variable Types
    protected static final int TYPE_INTEGER = 1;
    protected static final int TYPE_STRING = 2;

    private int varType;
    private final int varIndex;

    public Variable(int VAR_TYPE, int varIndex) {
        this.varType = VAR_TYPE;
        this.varIndex = varIndex;
    }

    public Variable(String val, int VAR_TYPE, int varIndex) {
        this.varType = VAR_TYPE;
        this.varIndex = varIndex;
    }

    public int getVarType() {
        return varType;
    }

    public int getVarIndex() {
        return varIndex;
    }
}
