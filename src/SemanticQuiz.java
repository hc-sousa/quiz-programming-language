import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class SemanticQuiz extends quizBaseVisitor<Object> {

   @Override public Object visitQuiz(quizParser.QuizContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitInstruction(quizParser.InstructionContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitDefQuiz(quizParser.DefQuizContext ctx) {
      String varName = ctx.VAR().getText();

      if (StaticValues.semanticVars.keySet().contains(varName)) {
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }
      StaticValues.semanticVars.put(varName, Type.QUIZ);
      return Type.QUIZ;    
   }

   @Override public Object visitDefBlock(quizParser.DefBlockContext ctx) {
      String varName = ctx.VAR().getText();

      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      StaticValues.semanticVars.put(varName, Type.BLOCK);
      return Type.BLOCK;
   }

   @Override public Object visitDefQuestionQMC(quizParser.DefQuestionQMCContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitDefQuestionQM(quizParser.DefQuestionQMContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitDefBlockquiz(quizParser.DefBlockquizContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " is already defined.");
         return null;
      }
      int counter=0;
      for (quizParser.ExprContext exp:ctx.exprList){
         if(ctx.blockQuizList.get(counter).getText().equals("goback")){
            Object typeV = visit(exp);
            if(typeV==null){
               StaticValues.semanticErrors.add("Expression cannot be null");
               return null;
            }
            Type typeValue = (Type) typeV;
            if(typeV!=Type.BOOL) {
               StaticValues.semanticErrors.add("You can only assign a Bool to 'Go Back'");
               return null;
            }
         }
         else if(ctx.blockQuizList.get(counter).getText().equals("userscore")) {
            Object typeV = visit(exp);
            if(typeV==null){
               StaticValues.semanticErrors.add("Expression cannot be null");
               return null;
            }
            Type typeValue = (Type) typeV;
            if(typeV!=Type.NUMBER) {
               StaticValues.semanticErrors.add("You can only assign a Number to 'Userscore'");
               return null;
            }
         }
         else if(ctx.blockQuizList.get(counter).getText().equals("quizscore")) {
            Object typeV = visit(exp);
            if(typeV==null){
               StaticValues.semanticErrors.add("Expression cannot be null");
               return null;
            }
            Type typeValue = (Type) typeV;
            if(typeV!=Type.NUMBER) {
               StaticValues.semanticErrors.add("You can only assign a Number to 'Quizscore'");
               return null;
            }
         }
         counter++;
      }
      StaticValues.semanticVars.put(varName, Type.BLOCKQUIZ);
      return Type.BLOCKQUIZ;
   }

   @Override public Object visitMethodAdd(quizParser.MethodAddContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exists.");
         return null;
      } 
      Type typeValue = StaticValues.semanticVars.get(varName);
      if (typeValue != Type.LIST && typeValue != Type.BLOCK && typeValue != Type.QUIZ && typeValue != Type.BLOCKQUIZ) {
         StaticValues.semanticErrors.add(varName + " isn't a List, Block or Quiz. You can't use the method add.");
         return null;
      }
         Type typeExpr = (Type) visit(ctx.expr());
      if (typeValue == Type.BLOCK) {
         if (typeExpr != Type.QUESTION)  {
            StaticValues.semanticErrors.add("You only can add Question in Block.");
            return null;
         }
      }
      if (typeValue == Type.BLOCKQUIZ) {
         if (typeExpr != Type.QUESTION && typeExpr != Type.BLOCK )  {
            StaticValues.semanticErrors.add("You only can add Question in BlockQuiz.");
            return null;
         }
      }
      if (typeValue == Type.QUIZ) {
         if (typeExpr != Type.QUESTION)  {
            StaticValues.semanticErrors.add("You only can add Question in Quiz.");
            return null;
         }
      }
      return typeValue;
   }

   @Override public Object visitMethodRemove(quizParser.MethodRemoveContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exist.");
         return null;
      } 
      Type typeValue = StaticValues.semanticVars.get(varName);
      if (typeValue != Type.LIST && typeValue != Type.BLOCK && typeValue != Type.QUIZ) {
         StaticValues.semanticErrors.add(varName + " isn't a List, Block or Quiz. You can't use the method add.");
         return null;
      }
      Type typeExpr = (Type) visit(ctx.expr());
      if (typeValue == Type.BLOCK) {
         if (typeExpr != Type.QUESTION)  {
            StaticValues.semanticErrors.add("You only can remove Question in Block.");
            return null;
         }
      }
      if (typeValue == Type.QUIZ) {
         if (typeExpr != Type.QUESTION && typeExpr != Type.BLOCK)  {
            StaticValues.semanticErrors.add("You can only remove Question or Blocks in Quiz.");
            return null;
         }
      }

      return typeValue;
   }

   @Override public Object visitMethodShuffle(quizParser.MethodShuffleContext ctx) {
      boolean errorFound = false;
      String varName = ctx.VAR().getText();
      
      if (!StaticValues.semanticVars.keySet().contains(varName)) {
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exist.");
         return null;
      } 
      Type typeValue = StaticValues.semanticVars.get(varName);
      if (typeValue != Type.QUESTION && typeValue != Type.BLOCK ) {
         StaticValues.semanticErrors.add(varName + " isn't a Block or Question. You can't use the method shuffle.");
         return null;
      }
      return typeValue; 
   }

   @Override public Object visitMethodExecute(quizParser.MethodExecuteContext ctx) { 
      boolean errorFound = false;
      String varName = ctx.VAR().getText();
      
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exist.");
         errorFound = true;
         return null;
      } 

      Type typeValue = StaticValues.semanticVars.get(varName);
      if (typeValue != Type.BLOCKQUIZ) {
         StaticValues.semanticErrors.add(varName + " isn't a BlockQuiz. You can't use the method execute.");
         errorFound = true;
         return null;
      }
      return typeValue; 
   }

   @Override public Object visitDef_qmc_q_attr(quizParser.Def_qmc_q_attrContext ctx) {
      String attribute = ctx.Q_ATTR().getText();
      Type typeExpr = (Type) visit(ctx.expr());
      if (attribute.equals("description")) {
         if (typeExpr != Type.TEXT) {
          StaticValues.semanticErrors.add("Description must be a Text.");
          return null;
         }
      } else if (attribute.equals("theme")) {
         if (typeExpr != Type.TEXT) {
          StaticValues.semanticErrors.add("Description must be a Text.");
          return null;
         }
      } else if (attribute.equals("difficulty")) {
         if (typeExpr != Type.NUMBER) {
          StaticValues.semanticErrors.add("Difficulty must be a Number.");
          return null;
         }
      } else if (attribute.equals("score")) {
         if (typeExpr != Type.NUMBER) {
            StaticValues.semanticErrors.add("Score must be a Number.");
            return null;
           }
      } else {
       StaticValues.semanticErrors.add("QMC atribbutes must be Options, Theme, Difficulty, Description or Score.");
       return null;
      }

      return typeExpr;
   }

   @Override public Object visitDef_qmc_qmc_attr(quizParser.Def_qmc_qmc_attrContext ctx) {
      String attribute = ctx.QMC_ATTR().getText();
      Type typeExpr = (Type) visit(ctx.expr());
      if (attribute.equals("options")) {
         if (typeExpr != Type.LIST) {
          StaticValues.semanticErrors.add("Options must be a List.");
          return null;
         }
      }
      else if (attribute.equals("correct")) {
         if (typeExpr != Type.TEXT) {
          StaticValues.semanticErrors.add("'Correct' must be of type Text.");
          return null;
         }
      }
      else {
       StaticValues.semanticErrors.add("QMC atribbutes must be Options, Theme, Difficulty, Description or Score.");
       return null; 
      }
      return typeExpr;
   }

   @Override public Object visitDef_qmc(quizParser.Def_qmcContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      for (quizParser.Def_qmc_attrContext it : ctx.def_qmc_attr()){
         visit(it);
      }
     
     StaticValues.semanticVars.put(varName, Type.QUESTION);
     return Type.QUESTION;
   }

   @Override public Object visitDef_qm_q_attr(quizParser.Def_qm_q_attrContext ctx) {
      String attribute = ctx.Q_ATTR().getText();
      Type typeExpr = (Type) visit(ctx.expr());
      if (attribute.equals("description")) {
         if (typeExpr != Type.TEXT) {
          StaticValues.semanticErrors.add("Description must be a Text.");
          return null;
         }
      } else if (attribute.equals("theme")) {
         if (typeExpr != Type.TEXT) {
          StaticValues.semanticErrors.add("Description must be a Text.");
          return null;
         }
      } else if (attribute.equals("difficulty")) {
         if (typeExpr != Type.NUMBER) {
          StaticValues.semanticErrors.add("Difficulty must be a Number.");
          return null;
         }
      } else if (attribute.equals("score")) {
         if (typeExpr != Type.NUMBER) {
            StaticValues.semanticErrors.add("Score must be a Number.");
            return null;
           }
      } else {
       StaticValues.semanticErrors.add("QM atribbutes must be Options, Theme, Difficulty, Description or Score.");
       return null;
      }

      return typeExpr;
   }

   @Override public Object visitDef_qm_qm_attr(quizParser.Def_qm_qm_attrContext ctx) {
      String attribute = ctx.QM_ATTR().getText();
      Type typeExpr = (Type) visit(ctx.expr());
      if (attribute.equals("left")) {
         if (typeExpr != Type.LIST) {
          StaticValues.semanticErrors.add("Left must be a List.");
          return null;
         }
      } else if (attribute.equals("right")) {
         if (typeExpr != Type.LIST) {
          StaticValues.semanticErrors.add("Right must be a List.");
          return null;
         }
      } else if (attribute.equals("match")) {
         if (typeExpr != Type.LIST) {
          StaticValues.semanticErrors.add("Match must be a List.");
          return null;
         }
      } else {
       StaticValues.semanticErrors.add("QM atribbutes must be Options, Theme, Difficulty, Description or Score.");
       return null; 
      }
      return typeExpr;
   }

   @Override public Object visitDef_qm(quizParser.Def_qmContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      for (quizParser.Def_qm_attrContext it : ctx.def_qm_attr()){
        visit(it);
      }
     
     StaticValues.semanticVars.put(varName, Type.QUESTION);
     return Type.QUESTION;
   }

   @Override public Object visitAssign(quizParser.AssignContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitAssignGenEquals(quizParser.AssignGenEqualsContext ctx) {
      String varName = ctx.VAR().getText();
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exists.");
         return null;
      }
      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue != StaticValues.semanticVars.get(varName)) {
         StaticValues.semanticErrors.add("Cannot assign to " + typeValue + " type var a different type.");
         return null;
      }
     
      StaticValues.semanticVars.put(varName, typeValue);
      return typeValue;
   }

   @Override public Object visitAssignGenPlusEquals(quizParser.AssignGenPlusEqualsContext ctx) {
      String varName = ctx.VAR().getText();
      
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exist.");
         return null;
      }
      
      Type typeVar = StaticValues.semanticVars.get(varName);
      Type typeValue = (Type) visit(ctx.expr());
      if (typeVar == Type.NUMBER) {
         if(typeValue!=Type.NUMBER) {
            StaticValues.semanticErrors.add("Cannot add " + typeValue + " value to a " + typeVar + " variable.");
            return null;
         }
      } else if (typeVar == Type.TEXT) {
         if (typeValue != Type.NUMBER && typeValue != Type.TEXT) {
            StaticValues.semanticErrors.add("Cannot add  " + typeValue + " value to a " + typeVar + " variable.");            
            return null;
         }
      } else {
         StaticValues.semanticErrors.add("Can only add values to Number or Text variables.");  
         return null;
      }
      
      StaticValues.semanticVars.put(varName, typeVar);
      return typeVar;
   }

   @Override public Object visitAssignGenMinusEquals(quizParser.AssignGenMinusEqualsContext ctx) {
      String varName = ctx.VAR().getText();
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + "doesn't exists.");
         return null;
      }
      Type typeVar = StaticValues.semanticVars.get(varName);
      Type typeValue = (Type) visit(ctx.expr());
      if (typeVar == Type.NUMBER) {
         if(typeValue!=Type.NUMBER) {
            StaticValues.semanticErrors.add("Cannot decrement " + typeValue + " value of a " + typeVar + " variable.");
            return null;
         }
      
      } else {
         StaticValues.semanticErrors.add("Can only decrement values to Number variables.");  
         return null;
      }

      StaticValues.semanticVars.put(varName, typeVar);
      return typeVar;
   }

   @Override public Object visitNumberAssign(quizParser.NumberAssignContext ctx) {
      String varName = ctx.VAR().getText();
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      } 
      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue !=  Type.NUMBER) {
         StaticValues.semanticErrors.add("Cannot assign this variable type to  " + varName + " because it is not a Number.");
         return null;
      }
      StaticValues.semanticVars.put(varName, Type.NUMBER);
      return Type.NUMBER;
   }

   @Override public Object visitTextAssign(quizParser.TextAssignContext ctx) {
      String varName = ctx.VAR().getText();
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue !=  Type.TEXT) {
         StaticValues.semanticErrors.add("Cannot assign this variable type to  " + varName + " because it is not a Text.");
         return null;  
      }
      
      StaticValues.semanticVars.put(varName, Type.TEXT);
      return Type.TEXT;
   }

   @Override public Object visitListAssign(quizParser.ListAssignContext ctx) {
     String varName = ctx.VAR().getText();
     if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue !=  Type.LIST) {
         StaticValues.semanticErrors.add("Cannot assign this variable type to  " + varName + " because it is not a List.");
         return null;
      }
      StaticValues.semanticVars.put(varName, Type.LIST);
      return Type.LIST;
   }

   @Override public Object visitBoolAssign(quizParser.BoolAssignContext ctx) {
      String varName = ctx.VAR().getText();
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
         return null;
      }

      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue !=  Type.BOOL) {
         StaticValues.semanticErrors.add("Cannot assign this variable type to  " + varName + " because it is not a Bool.");
         return null;
      }
      
      StaticValues.semanticVars.put(varName, Type.BOOL);
      return Type.BOOL;
   }

   @Override public Object visitAttrAssign(quizParser.AttrAssignContext ctx) { 
      Object typeExpr = visit(ctx.expr(0));

      if (typeExpr == null) {
         StaticValues.semanticErrors.add("Expression cannot be null.");
         return null;
      }

      String attribute = ctx.attr.getText();
      
      Object typeExpr2 = visit(ctx.expr(1));
      if (typeExpr2 == null) {
         StaticValues.semanticErrors.add("Expression cannot be null.");
         return null;
      }

      if (attribute.equals("options")) {
         if ((Type) typeExpr != Type.QUESTION) {  
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.LIST ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Options type must be a List.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("correct")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.TEXT ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Correct type must be a Text.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("left")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.LIST ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Left type must be a List.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("right")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.LIST ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Right type must be a List.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("match")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.LIST ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Match type must be a List.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("userscore")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         if ((Type) typeExpr2 != Type.NUMBER ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Userscore type must be a Number.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("quizscore")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         if ((Type) typeExpr2 != Type.NUMBER ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Quizscore type must be a Number.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("goback")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         if ((Type) typeExpr2 != Type.BOOL) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Goback type must be a Bool.");
            return null;
         }
         return Type.BOOL;
      } else if (attribute.equals("score")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.NUMBER ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Score type must be a Number.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("theme")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.TEXT ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Theme type must be a Text.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("description")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.TEXT ) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Description type must be a Text.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("difficulty")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         if ((Type) typeExpr2 != Type.NUMBER) {
            StaticValues.semanticErrors.add("Cannot assign this var type. Difficulty type must be a Number.");
            return null;
         }
         return Type.NUMBER;
      } else {
         StaticValues.semanticErrors.add("Attribute invalid.");
         return null;
      }
   }

   @Override public Object visitMethodPrint(quizParser.MethodPrintContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitMethodPrintln(quizParser.MethodPrintlnContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitMethodLoad(quizParser.MethodLoadContext ctx) {
      return visitChildren(ctx);
   }


   @Override public Object visitLoad(quizParser.LoadContext ctx) { 
      Type typeExpr = (Type) visit(ctx.expr());
      if (typeExpr != Type.TEXT) {
         StaticValues.semanticErrors.add("Expression must be a Text.");
         return null;
      }
      
      for (int i=0; i<ctx.varsLoad.size(); i++) {
         String varName = ctx.varsLoad.get(i).getText();

         if (StaticValues.semanticVars.keySet().contains(varName)) { 
            StaticValues.semanticErrors.add("Variable " + varName + " already exists.");
            return null;
         } 
         StaticValues.semanticVars.put(varName, Type.QUESTION);
      }
      return Type.NONE;
   }

   @Override public Object visitPrint(quizParser.PrintContext ctx) {
      Object typeValue = visit(ctx.expr());

      if (typeValue == null) {
        StaticValues.semanticErrors.add("Cannot show object of type " + typeValue);
        return null;
      }
      return Type.NONE;
   }

   @Override public Object visitPrintln(quizParser.PrintlnContext ctx) {
      Object typeValue = visit(ctx.expr());

      if (typeValue == null) {
         StaticValues.semanticErrors.add("Cannot show object of type " + typeValue);
         return null;
      }
      return Type.NONE;
   }

   @Override public Object visitExprType(quizParser.ExprTypeContext ctx) {
      String type = ctx.TYPE().getText();
      if ((type.charAt(0)==('t') && type.length()==1) || (type.charAt(0)==('f') && type.length()==1)){
         return Type.BOOL;
      } else if (Character.isDigit(type.charAt(0))) {
         return Type.NUMBER;
      } else if(type.startsWith("\"")){
         return Type.TEXT;
      } else {
         return null;
      }
   }

   @Override public Object visitExprOperator(quizParser.ExprOperatorContext ctx) {
      
      Type typeValue_0 = (Type) visit(ctx.expr(0));
      Type typeValue_1 = (Type) visit(ctx.expr(1));
      String op = ctx.OPERATOR_ALG().getText();

      if (op.equals("+")) {
         if (typeValue_0 == Type.NUMBER) {
            if (typeValue_1 != Type.NUMBER && typeValue_1 != Type.TEXT) {
               StaticValues.semanticErrors.add("Can only do algebric operation + with Number and Text types.");
               return null;
            }
         }
         if (typeValue_0 == Type.TEXT) {
            if (typeValue_1 != Type.NUMBER && typeValue_1 != Type.TEXT) {
               StaticValues.semanticErrors.add("Can only do algebric operation + with Number and Text types.");
               return null;
            }
         }
      } else {
         if (typeValue_0 != Type.NUMBER && typeValue_1 != Type.NUMBER) {
            StaticValues.semanticErrors.add("Can only do algebric operation -, /, * with Number and Text types.");
               return null;
         }
      }
      
      if (typeValue_0 == Type.TEXT || typeValue_1 == Type.TEXT) {
         return Type.TEXT;
      }
      return Type.NUMBER;
   }

   @Override public Object visitExprInput(quizParser.ExprInputContext ctx) {
      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue == null) {
         StaticValues.semanticErrors.add("Input cannot be null.");
         return null;
      }
      return typeValue;
   }

   @Override public Object visitExprAttrAccess(quizParser.ExprAttrAccessContext ctx) { 
      Object typeExpr = visit(ctx.expr());

      if (typeExpr == null) {
         StaticValues.semanticErrors.add("Expression cannot be null.");
         return null;
      }

      String attribute = ctx.attr.getText();
      if (attribute.equals("options")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("correct")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("left")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("right")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("match")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.LIST;
      } else if (attribute.equals("userscore")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("quizscore")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("goback")) {
         if ((Type) typeExpr != Type.BLOCKQUIZ) {
            StaticValues.semanticErrors.add("Expression must be a Blockquiz.");
            return null;
         }
         return Type.BOOL;
      } else if (attribute.equals("score")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.NUMBER;
      } else if (attribute.equals("theme")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("description")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.TEXT;
      } else if (attribute.equals("difficulty")) {
         if ((Type) typeExpr != Type.QUESTION) {
            StaticValues.semanticErrors.add("Expression must be a Question.");
            return null;
         }
         return Type.NUMBER;
      } else {
         StaticValues.semanticErrors.add("Attribute invalid.");
         return null;
      }      
   }

   @Override public Object visitExprGroup(quizParser.ExprGroupContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitExprIdxAccess(quizParser.ExprIdxAccessContext ctx) {
      Type typeValue_0 = (Type) visit(ctx.expr(0));
      Type typeValue_1 = (Type) visit(ctx.expr(1));
      if (typeValue_0 != Type.TEXT && typeValue_0 != Type.LIST) {
         StaticValues.semanticErrors.add("Only can access Text or List with index.");
         return null;
      } else {
         if (typeValue_1 != Type.NUMBER) {
            StaticValues.semanticErrors.add("Index must be a Number.");
            return null;
         }
      }

      return Type.NUMBER;
   }

   @Override public Object visitExprVar(quizParser.ExprVarContext ctx) {
      String varName = ctx.VAR().getText();
      
       if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exists.");
         return null;
      }
      return StaticValues.semanticVars.get(varName);
   }

   @Override public Object visitExprMatch(quizParser.ExprMatchContext ctx) { 
      Type typeValue_0 = (Type) visit(ctx.expr(0));
      Type typeValue_1 = (Type) visit(ctx.expr(1));
      if (typeValue_0 != Type.TEXT || typeValue_1 != Type.TEXT) {
         StaticValues.semanticErrors.add("Expression must be a Text.");
         return null;
      }
      return Type.TEXT;
   }

   @Override public Object visitExprQCheckanswer(quizParser.ExprQCheckanswerContext ctx) { 
      String varName = ctx.VAR().getText();
      if (!StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Variable " + varName + " doesn't exists.");
         return null;
      } 
      Type varType = StaticValues.semanticVars.get(varName);
      if (varType != Type.QUESTION){
         StaticValues.semanticErrors.add("Variable " + varName + " isn't a question.");
         return null;
      }
      Type typeValue = (Type) visit(ctx.expr());
      if (typeValue !=  Type.TEXT) {
         StaticValues.semanticErrors.add("Expression must be a Number");
         return null;  
      }
      return Type.NUMBER;
   }

   @Override public Object visitExprOption(quizParser.ExprOptionContext ctx) { 
      Type typeValue_0 = (Type) visit(ctx.expr(0));
      Type typeValue_1 = (Type) visit(ctx.expr(1));
      if (typeValue_0 != Type.TEXT || typeValue_1 != Type.TEXT) {
         StaticValues.semanticErrors.add("Expression must be a Text.");
         return null;
      }
      return Type.TEXT;
   }

   @Override public Object visitExprFunctionCall(quizParser.ExprFunctionCallContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitLoopsStructFor(quizParser.LoopsStructForContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitLoopsStructIf(quizParser.LoopsStructIfContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitFunction_header(quizParser.Function_headerContext ctx) { 
      
      Map<String,Type> varsLocais = new HashMap<>();
      Map<String,Type> semanticVarsBackup = new HashMap();
      StaticValues.funcType = getTheType(ctx.retType.getText());

      String var1Name = ctx.VAR(0).getText();
      if (StaticValues.semanticVars.keySet().contains(var1Name)) { 
         StaticValues.semanticErrors.add("Cannot create duplicate function " + var1Name);
         return null;
      } 
      else{
         StaticValues.nomeFuncao=var1Name;
         StaticValues.semanticVars.put(var1Name,Type.NONE);
      }
      semanticVarsBackup = StaticValues.semanticVars;
      int counter=0;
      if (ctx.type1!=null)
      {
         counter++;
         String var2Name = ctx.VAR(1).getText();
         if(varsLocais.keySet().contains(var2Name))
         {
            StaticValues.semanticErrors.add("Variable " + var2Name+" was already defined locally");
            return null;
         }
         Type varType = getTheType(ctx.type1.getText());
         varsLocais.put(var2Name, varType);
         for(int i=0;i<ctx.typeRest.size();i++)
         {
            counter++;
            String varN = ctx.VAR(i+2).getText();
            Type TypeN = getTheType(ctx.typeRest.get(i).getText());
            varsLocais.put(varN,TypeN);
         }
         StaticValues.numOfCalls.put(var1Name,counter);
         for(String var:varsLocais.keySet())
         {
            StaticValues.semanticVars.put(var,varsLocais.get(var));
         }
         ArrayList<Type> x = new ArrayList<Type>();
         x.add(varType);
         for (int i=0;i<ctx.typeRest.size();i++)
         {
            String t = ctx.typeRest.get(i).getText();
            x.add(getTheType(t));
         }
         StaticValues.argTypes.put(var1Name,x);
      }
      

      return semanticVarsBackup;
   }

   @Override public Object visitDefFunction(quizParser.DefFunctionContext ctx) {
      Map<String,Type> varsLocais = new HashMap<>();
      Map<String,Type> semanticVarsBackup = new HashMap();
      String nomeFunc;
      Type vRet=Type.NONE;
      Type fType = Type.NONE;
      Object fHeader = visit(ctx.function_header());
      if(fHeader==null)
      {
         StaticValues.semanticErrors.add("Function Header is Invalid");
         return null;
      }
      else{
         varsLocais = (Map) fHeader;
         nomeFunc = StaticValues.nomeFuncao;
         StaticValues.nomeFuncao = "";
      }
      semanticVarsBackup = StaticValues.semanticVars;
      int hasReturn=0;
      for(quizParser.InstructionContext inst : ctx.instruction())
      {
         Object typeV = visit(inst);
         if(typeV == null)
         {
            StaticValues.semanticErrors.add("Instruction cannot be null");
            return null;
         }
         else if((Type) typeV == Type.FUNCT_RET)
         {
            vRet = StaticValues.valRet;
            StaticValues.valRet = Type.NONE;
            hasReturn=1;
         }
      }
      fType = StaticValues.funcType;
      StaticValues.funcType=Type.NONE;
      if(fType!=Type.NONE)
      {
         if(hasReturn==0)
         {
            StaticValues.semanticErrors.add("Function must return "+fType);
            return null;
         }
         else{
            if(vRet != fType)
            {
               StaticValues.semanticErrors.add("Function must return "+fType+", not "+vRet);
               return null;
            }
         }
      }
      StaticValues.semanticVars = semanticVarsBackup;
      StaticValues.semanticVars.remove(nomeFunc);
      StaticValues.semanticVars.put(nomeFunc,vRet);
      return vRet;
   }

   @Override public Object visitFunctionCall(quizParser.FunctionCallContext ctx) { 
      String funcName = ctx.VAR().getText();
      if (!StaticValues.semanticVars.keySet().contains(funcName)) { 
         StaticValues.semanticErrors.add("Function "+funcName+" doesn't exist so it can't be called");
         return null;
      } 
      if(ctx.singleExpr!=null)
      {
         if (visit(ctx.singleExpr)==null)
         {
            StaticValues.semanticErrors.add("Expression cannot be null");
            return null;
         }
         else{
            int counter=1;
            for(int i=0;i<ctx.manyExprs.size();i++)
            {
               if(visit(ctx.manyExprs.get(i))==null)
               {
                  StaticValues.semanticErrors.add("Expression cannot be null");
                  return null;
               }
               counter++;
            }
            if(StaticValues.numOfCalls.get(funcName)!=counter)
            {
               StaticValues.semanticErrors.add("Function has "+StaticValues.numOfCalls.get(funcName)+" arguments but you're only giving "+counter);
               return null;
            }
            for(int i=0;i<StaticValues.argTypes.get(funcName).size();i++)
            {
               if(i==0)
               {
                  if(visit(ctx.singleExpr)!=StaticValues.argTypes.get(funcName).get(i))
                  {
                     StaticValues.semanticErrors.add("Function needs a "+StaticValues.argTypes.get(funcName).get(i)+" type argument but you're giving "+visit(ctx.singleExpr));
                     return null;
                  }
               }
               else{
                  if(visit(ctx.manyExprs.get(i-1))!=StaticValues.argTypes.get(funcName).get(i))
                  {
                     StaticValues.semanticErrors.add("Function needs a "+StaticValues.argTypes.get(funcName).get(i)+" type argument but you're giving "+visit(ctx.manyExprs.get(i-1)));
                     return null;
                  }
               }
            }
         }
      }
      return StaticValues.semanticVars.get(funcName);
   }

   @Override public Object visitFunction_ret(quizParser.Function_retContext ctx) { 
      Object retorno = visit(ctx.expr());
      if(retorno==null)
      {
         StaticValues.semanticErrors.add("Return value cannot be null");
         return null;
      }
      Type retType = (Type) retorno;
      StaticValues.valRet = retType;
      return (Type.FUNCT_RET);
   }

   @Override public Object visitStructure_if(quizParser.Structure_ifContext ctx) { 
      Object ifCondition = visit(ctx.ifcond);
      if (ifCondition==null)
      {
         StaticValues.semanticErrors.add("If condition cannot be null");
         return null;
      }
      for (quizParser.Expr_iforContext exp: ctx.ifInst)
      {
         Object tExp = visit(exp);
         if(tExp == null)
         {
            StaticValues.semanticErrors.add("If expression cannot be null");
            return null;
         }
      }

      for (quizParser.Expr_iforContext exp: ctx.elseInst)
      {
         Object tExp = visit(exp);
         if(tExp == null)
         {
            StaticValues.semanticErrors.add("Else expression cannot be null");
            return null;
         }
      }



      return Type.NONE;
   }

   @Override public Object visitCondIf(quizParser.CondIfContext ctx) { 
      Object tExp1 = visit(ctx.expr(0));
      Object tExp2 = visit(ctx.expr(0));
      if (tExp1 == null || tExp2 == null)
      {
         StaticValues.semanticErrors.add("Expression cannot be of type null");
         return null;
      }
      Type typeExp1 = (Type) tExp1;
      Type typeExp2 = (Type) tExp2;

      if(!(typeExp1.equals(typeExp2)))
      {
         StaticValues.semanticErrors.add("Expressions must be of the same type");
         return null;
      }

      int counter=0;
      for (quizParser.ExprContext exp : ctx.extraExp1)
      {
         Object tExp1Extra = visit(exp);
         Object tExp2Extra = visit(ctx.extraExp2.get(counter));
         if (tExp1Extra == null || tExp2Extra == null)
         {
            StaticValues.semanticErrors.add("Expression cannot be of type null");
            return null;
         }
         Type typeExp1Extra = (Type) tExp1Extra;
         Type typeExp2Extra = (Type) tExp2Extra;

         if(!(typeExp1Extra.equals(typeExp2Extra)))
         {
            StaticValues.semanticErrors.add("Expressions must be of the same type");
            return null;
         } 
         counter++;
      }

      return Type.NONE;
   }

   @Override public Object visitStructure_for(quizParser.Structure_forContext ctx) {

      Type forType= getTheType(ctx.forT.getText());
      boolean errorFound = false;
      String varName = ctx.VAR().getText();
      if (StaticValues.semanticVars.keySet().contains(varName)) { 
         StaticValues.semanticErrors.add("Cannot declare variable " + varName + " in for cycle because it already exists.");

      } 
      else{
         Object typeV = visit(ctx.expr());
         if (typeV==null)
         {
            return null;
         }
         else{ 
            Type typeValue = (Type) typeV;
            if(typeValue!=Type.NUMBER && typeValue!=Type.LIST && typeValue!=Type.BLOCK && typeValue!=Type.QUIZ  && typeValue!=Type.BLOCKQUIZ)
            {
               StaticValues.semanticErrors.add("Cannot iterate over "+varName+" because the type "+typeValue+ " is not iteratable.");

               return null;
            }
            StaticValues.semanticVars.put(varName, forType);
         }
      }
      for(quizParser.InstructionContext inst: ctx.instruction()){
         Object typeV = visit(inst);
         if(typeV == null)
         {
            StaticValues.semanticErrors.add("Instruction cannot be null");
            return null;
         }
      }
      StaticValues.semanticVars.remove(varName);

      return Type.NONE;
   }

   @Override public Object visitExpr_ifor(quizParser.Expr_iforContext ctx) {
      return visitChildren(ctx);
   }

   @Override public Object visitGroup(quizParser.GroupContext ctx) {
      if(ctx.expr().size()!=0){
         Object tipoGeral = visit(ctx.expr().get(0));

         if (tipoGeral == null) {
            StaticValues.semanticErrors.add("Group cannot contain null values.");
            return null;
            }
            Type tip = (Type) tipoGeral;
            for (quizParser.ExprContext exp : ctx.expr()){ 
               Object typeValue = visit(exp);
               if (typeValue == null) {
                  StaticValues.semanticErrors.add("Group cannot contain null values.");
                  return null;
               }
               if(visit(exp)!=tip)
               {
                  StaticValues.semanticErrors.add("Cannot add different types to group");
                  return null;
               }
            }
      }

      return Type.LIST;
   }

   public Type getTheType(String tipo)
   {
      switch(tipo)
      {
         case "list":
         return Type.LIST;
         case "number":
         return Type.NUMBER;
         case "text":
         return Type.TEXT;
         case "bool":
         return Type.BOOL;
         case "quiz":
         return Type.QUIZ;
         case "block":
         return Type.BLOCK;
         case "question":
         return Type.QUESTION;
         case "blockquiz":
         return Type.BLOCKQUIZ;
         case "nothing":
         return Type.NONE;
         default:
            return null;
      }
   }
}