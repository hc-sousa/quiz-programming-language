
import java.lang.module.ResolutionException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.stringtemplate.v4.*;

public class CompilerQuiz extends quizBaseVisitor<ST> {
   private static final String INTREGEX = "-?\\d+";
   private static Map<String, String> typeToJava = Stream.of(new String[][]{
      {"text", "String"},
      {"number", "Double"},
      {"quiz", "Quiz"},
      {"qmc", "QMC"},
      {"qm", "QM"},
      {"list", "ArrayList<Object>"},
      {"bool", "boolean"},
      {"nothing", "void"},
      {"block", "Block"},
      {"question", "Question"}
   }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

   @Override public ST visitQuiz(quizParser.QuizContext ctx) {
      ST res = templates.getInstanceOf("module");
      Iterator<quizParser.InstructionContext> list = ctx.instruction().iterator();
      while (list.hasNext()) {
         quizParser.InstructionContext inst = list.next();
         if (inst.getText().startsWith("function")) res.add("func", visit(inst));
         else res.add("instruction", visit(inst));
      }
      return res;
   }

   @Override public ST visitInstruction(quizParser.InstructionContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitDefQuiz(quizParser.DefQuizContext ctx) {
      ST res = templates.getInstanceOf("decl");
      res.add("type", "Quiz");
      String newVarName = newVar()+"_"+ctx.VAR().getText();
      StaticValues.VarToVar.put(ctx.VAR().getText(), newVarName);
      res.add("var", newVarName);
      res.add("value", "new Quiz(\"" + ctx.VAR().getText() + "\")");
      return res;
   }

   @Override public ST visitDefBlock(quizParser.DefBlockContext ctx) {
      ST res = templates.getInstanceOf("decl");
      res.add("type", "Block");
      String newVarName = newVar()+"_"+ctx.VAR().getText();
      StaticValues.VarToVar.put(ctx.VAR().getText(), newVarName);
      res.add("var", newVarName);
      res.add("value", "new Block(\"" + ctx.VAR().getText() + "\")");
      return res;
   }

   @Override public ST visitDefBlockquiz(quizParser.DefBlockquizContext ctx) {
      ST res = templates.getInstanceOf("decl");
      res.add("type", "BlockQuiz");
      String newVarName = newVar()+"_"+ctx.VAR().getText();
      StaticValues.VarToVar.put(ctx.VAR().getText(), newVarName);
      res.add("var", newVarName);
      String val = ctx.expr(0).getText().equals("t") ? "true" : "false";
      res.add("value", "new BlockQuiz(" + "\"" + ctx.VAR().getText() + "\", " + val + ")");
      return res;
   }

   @Override public ST visitMethodAdd(quizParser.MethodAddContext ctx) {
      ST res = templates.getInstanceOf("quizAdd");
      String varName = StaticValues.VarToVar.get(ctx.VAR().getText());
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitMethodRemove(quizParser.MethodRemoveContext ctx) {
      ST res = templates.getInstanceOf("quizRm");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("value",( StaticValues.VarToVar.containsKey(ctx.expr().getText()) ? StaticValues.VarToVar.get(ctx.expr().getText()) : ctx.expr().getText() ));
      return res;
   }

   @Override public ST visitMethodShuffle(quizParser.MethodShuffleContext ctx) {
      ST res = templates.getInstanceOf("questShuffle");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      return res;
   }

   @Override public ST visitMethodExecute(quizParser.MethodExecuteContext ctx) {
      ST res = templates.getInstanceOf("execute");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      return res;
   }

   @Override public ST visitDef_qmc(quizParser.Def_qmcContext ctx) {
      ST res = templates.getInstanceOf("QMCcreate");

      for (int i = 0; i < ctx.def_qmc_attr().size(); i++)
         res.add("attr", visit(ctx.def_qmc_attr(i)));

      String VARtext = ctx.VAR().getText();
      String newVarName = newVar() + "_" + VARtext;
      StaticValues.VarToVar.put(VARtext, newVarName);
      res.add("var", newVarName);
      res.add("id", "\"" + VARtext + "\"");
      return res;
   }

   @Override public ST visitDef_qmc_q_attr(quizParser.Def_qmc_q_attrContext ctx) {
      ST res = templates.getInstanceOf("QMCcreateAttr");
      res.add("v_" + ctx.Q_ATTR().getText(), visit(ctx.expr()));
      return res;
   }

   @Override public ST visitDef_qmc_qmc_attr(quizParser.Def_qmc_qmc_attrContext ctx) {
      ST res = templates.getInstanceOf("QMCcreateAttr");
      res.add("v_" + ctx.QMC_ATTR().getText(), visit(ctx.expr()));
      return res;
   }

   @Override public ST visitDef_qm(quizParser.Def_qmContext ctx) {
      ST res = templates.getInstanceOf("QMcreate");

      for (int i = 0; i < ctx.def_qm_attr().size(); i++)
         res.add("attr", visit(ctx.def_qm_attr(i)));

      String VARtext = ctx.VAR().getText();
      String newVarName = newVar() + "_" + VARtext;
      StaticValues.VarToVar.put(VARtext, newVarName);
      res.add("var", newVarName);
      res.add("id", "\"" + VARtext + "\"");
      return res;
   }

   @Override public ST visitDef_qm_q_attr(quizParser.Def_qm_q_attrContext ctx) {
      ST res = templates.getInstanceOf("QMcreateAttr");
      res.add("v_" + ctx.Q_ATTR().getText(), visit(ctx.expr()));
      return res;
   }

   @Override public ST visitDef_qm_qm_attr(quizParser.Def_qm_qm_attrContext ctx) {
      ST res = templates.getInstanceOf("QMcreateAttr");
      res.add("v_" + ctx.QM_ATTR().getText(), visit(ctx.expr()));
      return res;
   }

   @Override public ST visitAssign(quizParser.AssignContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitAssignGenEquals(quizParser.AssignGenEqualsContext ctx) {
      ST res = templates.getInstanceOf("assign");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitAssignGenPlusEquals(quizParser.AssignGenPlusEqualsContext ctx) {
      ST res = templates.getInstanceOf("plusEquals");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitAssignGenMinusEquals(quizParser.AssignGenMinusEqualsContext ctx) {
      ST res = templates.getInstanceOf("minusEquals");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitNumberAssign(quizParser.NumberAssignContext ctx) {
      ST res = templates.getInstanceOf("decl");
      ctx.expr().var = newVar(ctx.VAR().getText());
      res.add("type", "Double");
      res.add("var", ctx.expr().var);
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitTextAssign(quizParser.TextAssignContext ctx) {
      ST res = templates.getInstanceOf("decl");
      ctx.expr().var = newVar(ctx.VAR().getText());
      res.add("type", "String");
      res.add("var", ctx.expr().var);
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitListAssign(quizParser.ListAssignContext ctx) {
      ST res = templates.getInstanceOf("declGroup");
      String type;
      ctx.expr().var = newVar(ctx.VAR().getText());
      res.add("type", "List<Object>");
      res.add("var", ctx.expr().var);
      res.add("expr", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitBoolAssign(quizParser.BoolAssignContext ctx) {
      ST res = templates.getInstanceOf("decl");
      res.add("type", "boolean");
      ctx.expr().var = newVar(ctx.VAR().getText());
      res.add("var", ctx.expr().var);
      String val = ctx.expr().getText().equals("t") ? "true" : "false";
      res.add("value", val);
      return res;
   }

   @Override public ST visitAttrAssign(quizParser.AttrAssignContext ctx) {
      ST res = templates.getInstanceOf("attrAssign");
      res.add("expr", visit(ctx.expr(0)));
      res.add("attr", ctx.attr.getText());
      res.add("val", visit(ctx.expr(1)));
      return res;
   }

   @Override public ST visitMethodPrint(quizParser.MethodPrintContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitMethodPrintln(quizParser.MethodPrintlnContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitMethodLoad(quizParser.MethodLoadContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitLoad(quizParser.LoadContext ctx) {
      String fileName = visit(ctx.expr()).render();
      QuestionBankMain.load("../examples/" + fileName.replaceAll("\"", ""));
      ST res = templates.getInstanceOf("stats");
      for (int i = 0; i < ctx.VAR().size() - 1; i += 2) {
         String bankId = ctx.VAR(i).getText();
         String qId = ctx.VAR(i + 1).getText();
         String qIdVar = newVar(qId);
         res.add("instruction", QuestionBankMain.qts.get(bankId).getDefinition(qId, qIdVar));
      }
      return res;
   }

   @Override public ST visitPrint(quizParser.PrintContext ctx) {
      ST res = templates.getInstanceOf("print");
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitPrintln(quizParser.PrintlnContext ctx) {
      ST res = templates.getInstanceOf("println");
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitExprType(quizParser.ExprTypeContext ctx) {
      ST res = templates.getInstanceOf("value");
      String text = ctx.getText();
      if (text.matches(INTREGEX))
         text += ".0";
      res.add("val", text);
      return res;
   }

   @Override public ST visitExprOperator(quizParser.ExprOperatorContext ctx) {
      ST res = templates.getInstanceOf("exprOp");
      res.add("e1", visit(ctx.expr(0)));
      res.add("op", ctx.OPERATOR_ALG().getText());
      res.add("e2", visit(ctx.expr(1)));
      return res;
   }

   @Override public ST visitExprInput(quizParser.ExprInputContext ctx) {
      ST res = templates.getInstanceOf("inp");
      res.add("prompt", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitExprAttrAccess(quizParser.ExprAttrAccessContext ctx) {
      ST res = templates.getInstanceOf("attrAccess");
      res.add("expr", visit(ctx.expr()));
      res.add("attr", ctx.attr.getText());
      return res;
   }

   @Override public ST visitExprIdxAccess(quizParser.ExprIdxAccessContext ctx) {
      ST res = templates.getInstanceOf("indexAccess");
      res.add("e1", visit(ctx.expr(0)));
      res.add("e2", visit(ctx.expr(1)));
      return res;
   }

   @Override public ST visitExprVar(quizParser.ExprVarContext ctx) {
      ST res = templates.getInstanceOf("value");
      if (StaticValues.VarToVar.containsKey(ctx.getText())) res.add("val", StaticValues.VarToVar.get(ctx.getText()));
      else {
         String newVarName = newVar(ctx.getText());
         StaticValues.VarToVar.put(ctx.getText(), newVarName);
         res.add("val", StaticValues.VarToVar.get(ctx.getText()));
      }
      return res;
   }

   @Override public ST visitExprQCheckanswer(quizParser.ExprQCheckanswerContext ctx) {
      ST res = templates.getInstanceOf("checkAnswer");
      res.add("var", StaticValues.VarToVar.get(ctx.VAR().getText()));
      res.add("expr", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitExprOption(quizParser.ExprOptionContext ctx) {
      ST res = templates.getInstanceOf("newOption");
      res.add("id", ctx.expr(0).getText());
      res.add("desc", visit(ctx.expr(1)));
      return res;
   }

   @Override public ST visitExprMatch(quizParser.ExprMatchContext ctx) {
      ST res = templates.getInstanceOf("match");
      res.add("optId1", visit(ctx.expr(0)));
      res.add("optId2", visit(ctx.expr(1)));
      return res;
   }

   @Override public ST visitExprFunctionCall(quizParser.ExprFunctionCallContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitLoopsStructFor(quizParser.LoopsStructForContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitLoopsStructIf(quizParser.LoopsStructIfContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitFunction_header(quizParser.Function_headerContext ctx) {
      int varCount = 1;
      ST res = templates.getInstanceOf("funcHeader");
      if (ctx.type1 != null) {
         res.add("args", typeToJava.get(ctx.type1.getText()) + " " + newVar(ctx.VAR(varCount++).getText()));
         for (int i = 0; i < ctx.typeRest.size(); i++) {
            res.add("args", typeToJava.get(ctx.typeRest.get(i).getText()) + " " + newVar(ctx.VAR(varCount++).getText()));
         }
      }
      return res;
   }

   @Override public ST visitDefFunction(quizParser.DefFunctionContext ctx) {
      StaticValues.VarToVarTemp = new HashMap<>(StaticValues.VarToVar);
      ST res = templates.getInstanceOf("func");
      ret = false;
      res.add("name", ctx.function_header().VAR(0));
      res.add("args", visit(ctx.function_header()));
      for (quizParser.InstructionContext val : ctx.instruction()){
         if (val.getText().startsWith("ret"))
            ret = true;
         res.add("instruction", visit(val));
      }
      if (ret)
         res.add("return", typeToJava.get(ctx.function_header().retType.getText()));
      else
         res.add("return", "void");
      StaticValues.VarToVar = new HashMap<>(StaticValues.VarToVarTemp);
      return res;
   }

   @Override public ST visitFunctionCall(quizParser.FunctionCallContext ctx) {
      ST res = templates.getInstanceOf("funcCall");
      res.add("func", ctx.VAR().getText());
      for (int i = 0; i < ctx.expr().size(); i++)
         res.add("args", visit(ctx.expr(i)));
      return res;
   }

   @Override public ST visitFunction_ret(quizParser.Function_retContext ctx) {
      ST res = templates.getInstanceOf("ret");
      res.add("value", visit(ctx.expr()));
      return res;
   }

   @Override public ST visitStructure_if(quizParser.Structure_ifContext ctx) {
      ST res = templates.getInstanceOf("ifCondition");
      String fullCond = "";
      count = 0;
      operation = 0;

      res.add("condition", visit(ctx.ifcond));
      for(quizParser.Expr_iforContext val: ctx.ifInst)
         res.add("instruction", visit(val));
      fullCond += res.render();

      if(ctx.elseInst.size() > 0){
         res = templates.getInstanceOf("elseCondition");
         for(quizParser.Expr_iforContext val: ctx.elseInst)
            res.add("instruction", visit(val));
         fullCond += res.render();
      }

      res = templates.getInstanceOf("value");
      res.add("val", fullCond);
      
      return res;
   }

   @Override public ST visitCondIf(quizParser.CondIfContext ctx) {
      ST res = templates.getInstanceOf("condition");
      String value = "";
      operation = 0;
      count = 0;
      for (int i = 0; i <= ctx.ANDOR().size(); i++) {
         if (ctx.NOT() != null)
            value += "!(";
         value += visit(ctx.expr(count++)).render() + " ";
         value += ctx.OPERATOR(operation++).getText() + " ";
         value += visit(ctx.expr(count++)).render(); 
         if (ctx.ANDOR().size() > i) {
            if (ctx.ANDOR(i).getText().equals("and"))
               value += " && ";
            else
               value += " || ";
         }
         if (ctx.NOT() != null) value += ")"; 
      }
      res.add("condition", value);
      return res;
   }

   @Override public ST visitStructure_for(quizParser.Structure_forContext ctx) {
      try {
         Double range = Double.parseDouble(ctx.expr().getText());
         ST res = templates.getInstanceOf("fori");
         String newVarName = newVar(ctx.VAR().getText());
         StaticValues.VarToVar.put(ctx.VAR().getText(), newVarName);
         res.add("var1", StaticValues.VarToVar.get(ctx.VAR().getText()));
         res.add("var2", visit(ctx.expr()));
         for (quizParser.InstructionContext val: ctx.instruction())
            res.add("instruction", visit(val));
         return res;
      } catch (Exception e) {
         ST res = templates.getInstanceOf("foreach");
         res.add("type", typeToJava.get(ctx.forT.getText()));
         String newVarName = newVar(ctx.VAR().getText());
         StaticValues.VarToVar.put(ctx.VAR().getText(), newVarName);
         res.add("var1", StaticValues.VarToVar.get(ctx.VAR().getText()));
         String varToIterate = visit(ctx.expr()).render();
         int under_idx = varToIterate.indexOf("_");
         String varToIterateNoUS = varToIterate.substring(under_idx + 1);
         Type type = StaticValues.semanticVars.get(varToIterateNoUS);
         if (type == Type.QUIZ || type == Type.BLOCK)
            varToIterate += ".questions";

         res.add("var2", varToIterate);
         for (quizParser.InstructionContext val: ctx.instruction())
            res.add("instruction", visit(val));
         return res;
      }
      
   }

   @Override public ST visitExpr_ifor(quizParser.Expr_iforContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitGroup(quizParser.GroupContext ctx) {
      ST res = templates.getInstanceOf("arraysAsList");
      for (int i = 0; i < ctx.expr().size(); i++)
         res.add("value", visit(ctx.expr(i)));
      return res;
   }

   private String newVar(){
      return "v" + ++numVars;
   }

   private String newVar(String v){
      String newVarName ="v" + ++numVars + "_" + v;
      StaticValues.VarToVar.put(v, newVarName);
      return newVarName;
   }

   private int numVars = 0;
   private int count = 0;
   private int operation = 0;
   private boolean ret = false;
   private STGroup templates = new STGroupFile("quiz.stg");
}
