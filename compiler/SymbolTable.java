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
    private HashMap<String, Variable> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<>();
    }

    public void declareEntry(String symbol, int VAR_TYPE, int stackIndex) {
        symbolTable.put(symbol, new Variable(VAR_TYPE, stackIndex));
    }

    public void addEntry(String symbol, String value, int VAR_TYPE, int stackIndex ) {
        symbolTable.put(symbol, new Variable(value, VAR_TYPE, stackIndex));
    }

    public Variable getEntry(String symbol) {
        return symbolTable.get(symbol);
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public void printAll() {
        for( String key : symbolTable.keySet()) {
            System.out.println("\nSymbol: " + key + ",\tVar Index: " + symbolTable.get(key).getVarIndex() +  ",\tType: " + symbolTable.get(key).getVarType());
        }
    }


}
