import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;


public class Execute extends QuestionBankBaseVisitor<Object> {
   public Theme thm;
   public String theme_name;
   public Question q;

   @Override public HashMap<String , Question> visitProgram(QuestionBankParser.ProgramContext ctx) {
      //Theme thm = visitTheme(ctx.theme());
      Theme thm = (Theme)visit(ctx.theme());
      return thm.getQuestions();
   }

   @Override public Theme visitTheme(QuestionBankParser.ThemeContext ctx) {
      this.theme_name = ctx.TEXT().getText().split("\"")[1];
      HashMap<String , Question> questions = (HashMap)visit(ctx.questions_list());
      Theme t = new Theme(ctx.TEXT().getText().split("\"")[1],questions);
      return t;
   }

   @Override public HashMap<String , Question> visitQuestions_list(QuestionBankParser.Questions_listContext ctx) {
      HashMap<String, Question> questions = new HashMap<>();
      for (QuestionBankParser.QuestionContext question : ctx.question()){
         Question q = (Question)visit(question);
         questions.put(q.getId(), q);
      }
      return questions;
   }

   @Override public Question visitVisitMultiChc(QuestionBankParser.VisitMultiChcContext ctx) {
      return visitMultiple_choice(ctx.multiple_choice());
   }

   @Override public Question visitVisitMatching(QuestionBankParser.VisitMatchingContext ctx) {
      return visitMatching(ctx.matching());
   }

   @Override public Question visitMultiple_choice(QuestionBankParser.Multiple_choiceContext ctx) {
      QMC q = new QMC();
      String id = (String)visit(ctx.id());
      int difficulty = Integer.parseInt((String)visit(ctx.difficulty()));
      String description = (String)visit(ctx.description());
      List<Option> options = new ArrayList<>();
      for (QuestionBankParser.OptsContext option : ctx.opts()){
         Object[] opt = (Object[])visit(option);
         options.add((Option)opt[0]);
         if ( (boolean)opt[1] == true)
            q.setCorrectOption((Option)opt[0]);
      }
      q.setId(id.replaceAll("\"", ""));
      q.setDescription(description.replaceAll("\"", ""));
      q.setDifficulty(difficulty);
      q.setTheme(this.theme_name);
      q.setOptions(options);

      return q;
   }

   @Override public Object[] visitOpts(QuestionBankParser.OptsContext ctx) {
      String id = (String)visit(ctx.id());
      String description = ctx.TEXT().getText();
      char value = ctx.value.getText().charAt(0);
      boolean val;
      if(value == 't') val = true;
      else val = false;
      Option op = new Option(id.replaceAll("\"", ""), description.replaceAll("\"", ""));
      Object[] ans = new Object[2];
      ans[0] = op; ans[1] = val;
      return ans;
   }

   @Override public Question visitMatching(QuestionBankParser.MatchingContext ctx) {
      QM q = new QM();
      this.q = q;
      String id = (String)visit(ctx.id());
      int difficulty = Integer.parseInt((String)visit(ctx.difficulty()));
      String description = (String)visit(ctx.description());
      
      q.setId(id.replaceAll("\"",""));
      q.setDescription(description.replaceAll("\"", ""));
      q.setTheme(this.theme_name);
      q.setDifficulty(difficulty);
      q.setLeftOptions((List)visit(ctx.column(0)));
      q.setRightOptions((List)visit(ctx.column(1)));
      Map<Option,Option> matches = (HashMap)visit(ctx.match()); 
      q.setMatches(matches);
      return q;
   }

   @Override public List<Option> visitColumn(QuestionBankParser.ColumnContext ctx) {
      List<Option> elems = new ArrayList<>();
      String id, desc;
      for (QuestionBankParser.ElementContext elem : ctx.element()){
         id = (String)visit(elem.id());
         desc = elem.TEXT().getText();
         elems.add(new Option(id.replaceAll("\"", "" ), desc.replaceAll("\"", "" ))); 
      }
      return elems;
   }

   @Override public Map<Option,Option> visitMatch(QuestionBankParser.MatchContext ctx) {
      Map<Option,Option> match = new HashMap<>();
      Object[] ob = new Object[2];
      QM q = (QM)this.q;
      for (QuestionBankParser.MtContext m : ctx.mt()){
         String id1,id2;
         id1 = (String)visit(m.id(0));
         id2 = (String)visit(m.id(1));
         ob[0] = getOpt(id1.replaceAll("\"", ""), q.getLeftOptions(), q.getRightOptions());
         ob[1] = getOpt(id2.replaceAll("\"", ""), q.getLeftOptions(), q.getRightOptions());
         match.put((Option)ob[0], (Option)ob[1]);
      }
      return match;
   }

   @Override public Object visitElement(QuestionBankParser.ElementContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitMt(QuestionBankParser.MtContext ctx) {
      return visitChildren(ctx);
   }

   @Override public String visitId(QuestionBankParser.IdContext ctx) {
      return ctx.TEXT().getText();
   }

   @Override public String visitDescription(QuestionBankParser.DescriptionContext ctx) {
      return ctx.TEXT().getText();
   }

   @Override public String visitDifficulty(QuestionBankParser.DifficultyContext ctx) {
      return ctx.Integer().getText();
   }

   public Option getOpt(String id , List<Option> left , List<Option> right){
      for( Option op : left){
         if(op.getId().equals(id)) return op;
      }
      for( Option op : right){
         if(op.getId().equals(id)) return op;
      }
      System.out.println("Matching failed.");
      return null;
   }
}
