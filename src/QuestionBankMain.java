import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class QuestionBankMain {
   public static Map<String, Question> qts;
   public static String lastLoadedFile = "";
   // public static void main(String[] args) {
   //    load("../../examples/method.txt");
   //    System.out.println(qts);
   // }
   
   public static void load(String fileName) {
      if (lastLoadedFile.equals(fileName))
         return;
      
      try {
         CharStream input = CharStreams.fromFileName(fileName);
         QuestionBankLexer lexer = new QuestionBankLexer(input);
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         QuestionBankParser parser = new QuestionBankParser(tokens);
         ParseTree tree = parser.program();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            Semantic visitor0 = new Semantic();
            ArrayList<String> errors= (ArrayList<String>)visitor0.visit(tree);
            if(errors.size() > 0){
               for(String s : errors){
                  System.out.printf("Error: %s;\n",s);
               }
               System.exit(1);
            }
         }
         Execute visitor1 = new Execute();
         qts = (HashMap<String,Question>)visitor1.visit(tree);
         lastLoadedFile = fileName;
      }
      catch (FileNotFoundException e){
         System.out.println("File not found.");
         System.exit(1);
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
