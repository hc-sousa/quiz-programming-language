
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.stringtemplate.v4.*;
import java.util.*;

public class quizMain {
   public static void main(String[] args) {
      try {
         // create a CharStream that reads from standard input:
         CharStream input = CharStreams.fromStream(System.in);
         // create a lexer that feeds off of input CharStream:
         quizLexer lexer = new quizLexer(input);
         // create a buffer of tokens pulled from the lexer:
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         quizParser parser = new quizParser(tokens);
         // replace error listener:
         //parser.removeErrorListeners(); // remove ConsoleErrorListener
         //parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at quiz rule:
         ParseTree tree = parser.quiz();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            
            // Semantic
            SemanticQuiz visitor0 = new SemanticQuiz();
            visitor0.visit(tree);
            if (StaticValues.semanticErrors.size() > 0) System.out.println("Errors:");
            for (int i=0;i<StaticValues.semanticErrors.size();i++)
               System.out.println(StaticValues.semanticErrors.get(i));
            
            if(StaticValues.semanticErrors.size() > 0)
               System.exit(1);

            // Compiler
            CompilerQuiz visitor1 = new CompilerQuiz();
            ST result = visitor1.visit(tree);

            String fileName = "QuizOutput";
            result.add("name", fileName);
            try {
               FileWriter writer = new FileWriter(fileName + ".java");
               writer.write(result.render());
               writer.flush();
            } catch (IOException e) {
               System.err.println("Error creating or writing to file " + fileName);
               e.printStackTrace();
            }
         }
      }
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      catch(RecognitionException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
}
