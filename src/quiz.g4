grammar quiz;
quiz: SEPARATOR* (instruction)* ENDQUIZ EOF; 
//  Defino o quiz como o conjunto de um token de criação do questionario e 0+ instruções (definição de instrução abaixo) e dps um EOF
//def_question | assign | method_add | methods | structure_function | loops)* EOF
  
instruction: 
              def_question       
            | assign             
            | method_add         
            | method_remove     
            | method_shuffle  
            | method_execute   
            | methods            
            //| structure_function 
            | def_function
            | function_call SEPARATOR+
            //| function_ret */
            | loops              
            | def_block          
            | def_quiz 
            | def_blockquiz
            | function_ret      
;

def_quiz: CREATE 'quiz' VAR SEPARATOR+ #defQuiz;

//def_block: CREATE 'block' VAR ':' SEPARATOR* (def_question)* END SEPARATOR+ #defBlock;
def_block: CREATE 'block' VAR SEPARATOR+ #defBlock;

def_question:
              def_qmc #defQuestionQMC
            | def_qm  #defQuestionQM
;

def_blockquiz: CREATE 'blockquiz' VAR ':' SEPARATOR* (blockQuizList+=BLOCKQUIZ_ATTR '=' exprList+=expr SEPARATOR+)* END SEPARATOR+ #defBlockquiz;

method_add: VAR ADD expr SEPARATOR+ #methodAdd;   //so pra quiz, list e block
method_remove: VAR REMOVE expr SEPARATOR+  #methodRemove;   //só pra quiz, list e block
method_shuffle: VAR SHUFFLE SEPARATOR+  #methodShuffle;
method_execute: VAR EXECUTE SEPARATOR+     #methodExecute; // so para blockquiz


def_qmc_attr:
  (Q_ATTR '=' expr SEPARATOR+)      #def_qmc_q_attr
  | (QMC_ATTR '=' expr SEPARATOR+)  #def_qmc_qmc_attr
;

def_qmc:
    CREATE QMC VAR ':' SEPARATOR*
    def_qmc_attr*
    END SEPARATOR+
;

def_qm_attr:
  (Q_ATTR '=' expr SEPARATOR+)     #def_qm_q_attr
  | (QM_ATTR '=' expr SEPARATOR+)  #def_qm_qm_attr
;

def_qm:
    CREATE QM VAR ':' SEPARATOR*
    def_qm_attr*
    END SEPARATOR+
;

assign:
      number_assign   
    | text_assign     
    | bool_assign     
    | group_assign    
    | generic_assign 
    | attr_assign
;

generic_assign: 
      VAR '=' expr SEPARATOR+  #assignGenEquals
    | VAR '+=' expr SEPARATOR+ #assignGenPlusEquals
    | VAR '-=' expr SEPARATOR+ #assignGenMinusEquals
;

number_assign: 'number' VAR '=' expr SEPARATOR+ #numberAssign;
text_assign: 'text' VAR '=' expr SEPARATOR+ #textAssign;
group_assign: LIST VAR '=' expr SEPARATOR+ #listAssign;
bool_assign: 'bool' VAR '=' expr SEPARATOR+ #boolAssign;
attr_assign: expr ('.' attr=(ALL_ATTR | Q_ATTR | QMC_ATTR | QM_ATTR| BLOCKQUIZ_ATTR))+ '=' expr SEPARATOR+ #attrAssign;


methods:
    print     #methodPrint
    | println #methodPrintln
    | load    #methodLoad
;

load: 'load' 'from' expr ':' SEPARATOR*
        (VAR 'as' varsLoad+=VAR SEPARATOR+)+
      END SEPARATOR+;

print: 'show' '(' expr? ')' SEPARATOR+    ;
println: 'showln' '(' expr? ')' SEPARATOR+ ; 

expr returns[String var = null]:
      expr OPERATOR_ALG expr #exprOperator
    | expr ('.' attr=(ALL_ATTR | Q_ATTR | QMC_ATTR | QM_ATTR | BLOCKQUIZ_ATTR))+   #exprAttrAccess
    | INPUT expr             #exprInput
    | VAR Q_CHECKANSWER expr #exprQCheckanswer
    | TYPE                   #exprType
    | group                  #exprGroup
    | VAR                    #exprVar
    | function_call         #exprFunctionCall
    | expr '[' expr ']'      #exprIdxAccess
    | expr '->' expr         #exprOption
    | expr ':' expr          #exprMatch
;

loops: 
      structure_for #loopsStructFor
    | structure_if  #loopsStructIf
;

 // function myFunction(number a, number b, list c): ... code ...
function_header:
  retType=(LIST | 'number' | 'text' | 'bool' | 'quiz' | 'block' | 'question' | 'nothing')
  VAR '(' (type1=(LIST | 'number' | 'text' | 'bool' | 'quiz' | 'block' | 'question') VAR)? 
  (',' typeRest+=(LIST | 'number' | 'text' | 'bool' | 'quiz' | 'block' | 'question') VAR)* ')';
def_function: 'function' function_header ':' SEPARATOR* instruction* END SEPARATOR+ #defFunction;
function_call: VAR '(' (singleExpr=expr)? (',' manyExprs+=expr)* ')' #functionCall; // myFunction(a, b, c)
function_ret: 'ret' expr SEPARATOR+;

structure_if: 'if' ifcond=condition_if ':' SEPARATOR* (ifInst+=expr_ifor)+ ('else' ':' SEPARATOR+ (elseInst+=expr_ifor)+ )? END SEPARATOR+ ;

condition_if: NOT? expr OPERATOR expr (ANDOR extraExp1+=expr OPERATOR extraExp2+=expr)* #condIf;

structure_for: 'foreach' forT=('number' | 'text' | 'bool' | 'quiz' | 'block' | 'blockquiz' | 'question') VAR 'in' expr ':' SEPARATOR* (instruction|NEWLINE)+ END SEPARATOR+ ;

expr_ifor: expr      
           | generic_assign
           | methods 
           | loops   
; 


group: 'group' ':' SEPARATOR* (expr SEPARATOR+)* END SHUFFLE?;

SHUFFLE: 'shuffle';
LIST: 'list';
ADD: 'add';
REMOVE: 'remove';
EXECUTE: 'execute';
END: 'end';
CREATE: 'create';
INPUT: 'input';
QMC: 'qmc';
QM: 'qm';
RET: 'ret';


TYPEQUESTION: QMC | QM;
QMC_ATTR: 'options' | 'correct';      // multiple choice exclusive attributes
QM_ATTR: 'left' | 'right' | 'match';  // matching exclusive attributes
Q_ATTR: 'description' | 'theme' | 'difficulty' | 'score'; // general question attributes
BLOCKQUIZ_ATTR: 'goback' | 'userscore' | 'quizscore';
Q_CHECKANSWER: 'checkanswer';
ALL_ATTR: QMC_ATTR | QM_ATTR | Q_ATTR | 'size';

//SEPARATOR+: NEWLINE+ | (';'+ NEWLINE*); // semicolon or newline(s)
SEPARATOR: (NEWLINE | ';')+; // TODO: nao da pra ter no meio de linhas assim
OPERATOR_ALG: '+'|'-'|'*'|'/'|'%';
ANDOR: 'and' | 'or';
OPERATOR: '>' | '>=' | '<' | '<=' | '==' | 'is';
NOT: 'not';

ENDQUIZ: 'endprogram' NEWLINE*;
TYPE: STRING | BOOL | NUMBER; //| LIST;
BOOL: 't' | 'f'; // boolean values: t (true), f (false)
VAR: [a-zA-Z] [A-Za-z0-9_]*; // variable names


STRING: '"'.*?'"'; // anything between quotes

NUMBER: INTEGER | DOUBLE;
INTEGER: '-'?[0-9]+;
DOUBLE: '-'?[0-9]+('.'[0-9]+)?;
NEWLINE: '\r'? '\n';
//TODO: comment single line
// COMMENT_1LINE: '//' .*? NEWLINE -> skip;
COMMENT_NLINES: '/*' .*? '*/' -> skip;
//COMMENT_NLINES: '/*' .*? '*/' NEWLINE* -> type(SEPARATOR+); // TODO: porque é que isto precisa dum SEPARATOR+ depois?
WS: [ \t]+ -> skip;
