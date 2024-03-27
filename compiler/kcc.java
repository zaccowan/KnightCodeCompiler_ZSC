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

    /*
    Needs to:
        Generate a parse tree for the Visitor
     */

    public static void main(String[] args) throws IOException {


        String file = args[0];
        String programName = args[0].substring(file.indexOf('/')+1, file.indexOf('.'));
        programName = Pattern.compile("^.").matcher(programName).replaceFirst(m -> m.group().toUpperCase());

        System.out.println(programName);

        AsmGen generator = new AsmGen(programName);
        CharStream charStream = CharStreams.fromFileName(args[0]);
        KnightCodeLexer lexer = new KnightCodeLexer(charStream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        KnightCodeParser parser = new KnightCodeParser(tokenStream);

        ParseTree tree = parser.file();
        KnightVisitor visitor = new KnightVisitor(generator);
        visitor.visit(tree);

        generator.writeToFile();
    }

}
