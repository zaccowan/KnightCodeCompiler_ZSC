package compiler;

import lexparse.KnightCodeLexer;
import lexparse.KnightCodeParser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

/**
 * kcc is the compiler class that brings all components of the compiler together into one application.
 * @author Zac Cowan
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 **/
public class kcc {
    /**
     * Main method used to run compiler on a given .KC file.
     * @param args args[0] is the KC file to compile, args[1] is an optional output location and name.
     * @throws IOException exception for invalid IO
     */
    public static void main(String[] args) throws IOException {


        String file = args[0];

        CharStream charStream = CharStreams.fromFileName(file);
        KnightCodeLexer lexer = new KnightCodeLexer(charStream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        KnightCodeParser parser = new KnightCodeParser(tokenStream);

        ParseTree tree = parser.file();

        KnightVisitor visitor;
        if(args.length == 2) {
            visitor = new KnightVisitor(args[1]);
        } else {
            visitor = new KnightVisitor();
        }
        visitor.visit(tree);

        visitor.end(); //Writes Bytecode out to file
        visitor.printAll();
    }
}
