package compiler;

import java.util.HashMap;

public class SymbolTable {
    protected HashMap<String, Variable> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<>();
    }
}
