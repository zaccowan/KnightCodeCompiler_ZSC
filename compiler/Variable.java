package compiler;

/**
 * Variable is a representation of a variables type and JVM var index.
 * @author Zac Cowan
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 **/
public class Variable {

    // Built in Variable Types
    // Chose to represent the types as integers instead of an enum for no particular reason.
    protected static final int TYPE_INTEGER = 1;
    protected static final int TYPE_STRING = 2;

    // Type of variable
    private int varType;
    // Variable index of variable for storing and loading in bytecode.
    private final int varIndex;

    /**
     * Constructor to create a variable with type and "memory" location
     * @param VAR_TYPE Type of variable stored
     * @param varIndex Index of variable
     */
    public Variable(int VAR_TYPE, int varIndex) {
        this.varType = VAR_TYPE;
        this.varIndex = varIndex;
    }


    /**
     * Get the type of variable.
     * @return The type of given variable.
     */
    public int getVarType() {
        return varType;
    }

    /**
     * Get the index of variable for access.
     * @return The varIndex of the variable.
     */
    public int getVarIndex() {
        return varIndex;
    }
}
