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

        // Takes in characters from file
        CharStream charStream = CharStreams.fromFileName(file);
        // Creates an antlr generated lexer to do lexical analysis of file character stream.
        KnightCodeLexer lexer = new KnightCodeLexer(charStream);
        // Token Steam from the analysis done by the lexer.
        TokenStream tokenStream = new CommonTokenStream(lexer);
        // Creates an antlr generated parser to parse the tokens and build the parse tree.
        KnightCodeParser parser = new KnightCodeParser(tokenStream);
        // Store the parse tree as a ParseTree called tree.
        ParseTree tree = parser.file();

        // Delares a visitor that will walk the parse tree and generate non-boilerplate bytecode.
        // Note: the boiler plate ASM calls are done by the AsmGen class which is created inside of KnightVisitor.
        KnightVisitor visitor;

        // Instantiate the visitor.
        // If there are two arguments supplied when the compiler is called, retrieve the file output location.
        if(args.length == 2) {
            visitor = new KnightVisitor(args[1]);
        }
        // If the call does not supply and output location and name, default to output: "./ouput/<program_name>.class"
        else {
            visitor = new KnightVisitor();
        }
        // Visit the root of the tree to begin the visitors process of bytecode generation.
        visitor.visit(tree);

        // Calls some final boilerplate needed for a .class file and writes to specified location.
        visitor.end(); //Writes Bytecode out to file

        //System.out.println("Symbol table information because why not: ");
        //visitor.printAll();
    }
}
