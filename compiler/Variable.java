package compiler;

public class Variable {

    // Built in Variable Types
    protected static final int TYPE_INTEGER = 1;
    protected static final int TYPE_STRING = 2;

    private final int varType;
    private Object value;


    public Variable(Object val, int VAR_TYPE) {
        this.value = new Object();
        this.varType = VAR_TYPE;
        switch(VAR_TYPE) {
            case TYPE_INTEGER:
                this.value = (int) val;
                break;
            case TYPE_STRING:
                this.value = (String) val;
                break;
        }
    }

    public int getVarType() {
        return varType;
    }

    public Object getValue() {
        return value;
    }
}
