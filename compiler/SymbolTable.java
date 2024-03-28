package compiler;

import java.util.HashMap;

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

    public void updateSymbolValue(String symbol, String value) {
        Variable var = symbolTable.get(symbol);
        var.setValue(value);
        symbolTable.replace(symbol, var);
    }

    public void printAll() {
        for( String key : symbolTable.keySet()) {
            System.out.println("\nSymbol: " + key + ",\tValue: " + symbolTable.get(key).getValue().toString() + ",\tType: " + symbolTable.get(key).getVarType());
        }
    }


}
