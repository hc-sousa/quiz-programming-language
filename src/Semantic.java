
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class Semantic extends QuestionBankBaseVisitor<Object> {
    public ArrayList<String> errors = new ArrayList<>();
    public ArrayList<String> ids = new ArrayList<>();
    public ArrayList<String> optIds = new ArrayList<>();

   @Override public Object visitProgram(QuestionBankParser.ProgramContext ctx) {
        visitChildren(ctx);
        return errors;
    }

   @Override public Object visitTheme(QuestionBankParser.ThemeContext ctx) {
        return visitChildren(ctx);
    }

   @Override public Object visitQuestions_list(QuestionBankParser.Questions_listContext ctx) {
        return visitChildren(ctx);

    }

   @Override public Object visitVisitMultiChc(QuestionBankParser.VisitMultiChcContext ctx) {
        return visitMultiple_choice(ctx.multiple_choice());
    }

    @Override public Object visitVisitMatching(QuestionBankParser.VisitMatchingContext ctx) {
        return visitMatching(ctx.matching());
    }

   @Override public Object visitMultiple_choice(QuestionBankParser.Multiple_choiceContext ctx) {
        optIds.clear();
        String id = (String)visit(ctx.id());
        if(ids.contains(id)) 
            errors.add("Invalid Question ID at " + id + " since it has been used before");
        else 
            ids.add(id);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(id.substring(1,id.length() - 1));
        if (matcher.find()){
            errors.add("Invalid ID format at " + id + ". May only contain letters, numbers and \"_\"");
        } 
        return visitChildren(ctx);
    }

   @Override public Object visitOpts(QuestionBankParser.OptsContext ctx) {
        String id = (String)visit(ctx.id());
        if(optIds.contains(id)) 
            errors.add("Invalid Question ID at " + id + " since it has been used before");
        else 
            optIds.add(id);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(id.substring(1,id.length() - 1));
        if (matcher.find()){
            errors.add("Invalid ID format at " + id + ". May only contain letters, numbers and \"_\"");
        } 
        return visitChildren(ctx);
    }

   @Override public Object visitMatching(QuestionBankParser.MatchingContext ctx) {
        optIds.clear();
        String id = (String)visit(ctx.id());
        if(ids.contains(id)) 
            errors.add("Invalid Question ID at " + id + " since it has been used before");
        else 
            ids.add(id);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(id.substring(1,id.length() - 1));
        if (matcher.find()){
            errors.add("Invalid ID format at " + id + ". May only contain letters, numbers and \"_\"");
        } 
        return visitChildren(ctx);
    }

   @Override public Object visitColumn(QuestionBankParser.ColumnContext ctx) {
        return visitChildren(ctx);
    }

   @Override public Object visitMatch(QuestionBankParser.MatchContext ctx) {
        return visitChildren(ctx);
    }

   @Override public Object visitElement(QuestionBankParser.ElementContext ctx) {
        String id = (String)visit(ctx.id());
        if(optIds.contains(id)) 
            errors.add("Invalid Question ID at " + id + " since it has been used before");
        else 
            optIds.add(id);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(id.substring(1,id.length() - 1));
        if (matcher.find()){
            errors.add("Invalid ID format at " + id + ". May only contain letters, numbers and \"_\"");
        } 
      return visitChildren(ctx);
    }

   @Override public Object visitMt(QuestionBankParser.MtContext ctx) {
       for (int i = 0 ; i<2; i++){
            String id = (String)visit(ctx.id(i));
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]+");
            Matcher matcher = pattern.matcher(id.substring(1,id.length() - 1));
            if (matcher.find()){
                errors.add("Invalid ID format at " + id + ". May only contain letters, numbers and \"_\"");
            } 
        }
      return visitChildren(ctx);
    }

   @Override public Object visitId(QuestionBankParser.IdContext ctx) {
        return (String)ctx.TEXT().getText();
    }

   @Override public Object visitDescription(QuestionBankParser.DescriptionContext ctx) {
        return visitChildren(ctx);
    }

   @Override public Object visitDifficulty(QuestionBankParser.DifficultyContext ctx) {
       
       return visitChildren(ctx);
    }
}

