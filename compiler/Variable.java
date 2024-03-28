package compiler;

public class Variable {

    // Built in Variable Types
    protected static final int TYPE_INTEGER = 1;
    protected static final int TYPE_STRING = 2;

    private int varType;
    private Object value;



    private final int stackIndex;

    public Variable(int VAR_TYPE, int stackIndex) {
        this.varType = VAR_TYPE;
        this.stackIndex = stackIndex;
    }

    public Variable(String val, int VAR_TYPE, int stackIndex) {
        this.value = new Object();
        this.varType = VAR_TYPE;
        this.stackIndex = stackIndex;
        switch(VAR_TYPE) {
            case TYPE_INTEGER:
                this.value = Integer.valueOf(val);
                break;
            case TYPE_STRING:
                this.value = val;
                break;
        }
    }

    public void setValue(String val) {
        switch(varType) {
            case TYPE_INTEGER:
                this.value = Integer.valueOf(val);
                break;
            case TYPE_STRING:
                this.value = val;
                break;
        }
    }

    public int getVarType() {
        return varType;
    }

    public Object getValue() {
        return value;
    }
    public int getStackIndex() {
        return stackIndex;
    }
}
