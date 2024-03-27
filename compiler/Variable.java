package compiler;

public class Variable {

    // Built in Variable Types
    protected static final int TYPE_INTEGER = 1;
    protected static final int TYPE_STRING = 2;

    private final int varType;
    private final Object value;

    public Variable(String val) {
        this.value = val;
        this.varType = TYPE_STRING;
    }
    public Variable(int val ) {
        this.value = val;
        this.varType = TYPE_INTEGER;
    }

    public int getVarType() {
        return varType;
    }

    public Object getValue() {
        return value;
    }
}
