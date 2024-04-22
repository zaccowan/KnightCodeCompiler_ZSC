package compiler;

import java.util.HashMap;

/**
 * SymbolTable is responsible for storing Variable representations for access by the compiler.
 * @author Zac Cowan
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 **/
public class SymbolTable {

    /**
     * Hashmap used to store variable elements with a key provided by the parse tree node test.
     */
    private final HashMap<String, Variable> symbolTable;


    /**
     * Instantiates the hashmap used by the symbol table.
     */
    SymbolTable() {
        symbolTable = new HashMap<>();
    }

    /**
     * @param symbol Text of the variable retrieved from the parse tree.
     * @param VAR_TYPE Type of the variable to be stored.
     * @param stackIndex Index of the variable in JVM "memory"
     */
    public void declareEntry(String symbol, int VAR_TYPE, int stackIndex) {
        symbolTable.put(symbol, new Variable(VAR_TYPE, stackIndex));
    }


    /**
     * Get and entry from the hash map.
     * @param symbol Variable to retrieve from symbol table
     * @return Variable object representing the variable.
     */
    public Variable getEntry(String symbol) {
        return symbolTable.get(symbol);
    }


    /**
     * Determines if a given symbol has a representation in the symbol table.
     * @param symbol Variable to check existence of in symbol table.
     * @return true if the variable is present in the table.
     */
    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }


    /**
     * Print all the variables types and varIndices stored in the symbol table.
     * This is just interesting to see, it is not useful.
     */
    public void printAll() {
        for( String key : symbolTable.keySet()) {
            System.out.println("\nSymbol: " + key + ",\tVar Index: " + symbolTable.get(key).getVarIndex() +  ",\tType: " + symbolTable.get(key).getVarType());
        }
    }


}
