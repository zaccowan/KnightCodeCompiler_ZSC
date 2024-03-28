package compiler;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Variable> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<>();
    }

    public void setSymbol(String symbol) {
        symbolTable.put(symbol, new Variable());
    }

    public void addEntry(String symbol, Object value, int VAR_TYPE ) {
        symbolTable.put(symbol, new Variable(value, VAR_TYPE));
    }


}
