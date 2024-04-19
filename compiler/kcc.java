package compiler;

import lexparse.KnightCodeLexer;
import lexparse.KnightCodeParser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.regex.Pattern;

public class kcc {
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
