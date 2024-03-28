package compiler;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Variable> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<>();
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


}
