grammar QuestionBank;

program: SEPARATOR? theme EOF;

theme: 'Theme' ':'  TEXT SEPARATOR questions_list 'end' SEPARATOR?;

questions_list: question*  ;

question: 'qmc' '-' multiple_choice SEPARATOR      #visitMultiChc
        | 'qm' '-' matching SEPARATOR              #visitMatching
        ; 

//Definição de perguntas de escolha múltipla
multiple_choice: id '-' difficulty '-' description ':' SEPARATOR opts*;
opts: id '-' TEXT ',' value=('t' | 'f') SEPARATOR;

//Definição de preguntas de matching
matching: id  '-' difficulty '-' description ':' SEPARATOR 'left' '-' column 'right' '-' column match;
column: element* SEPARATOR;
match: 'Match' '-' mt* SEPARATOR;
element: id '-' TEXT '+' 
       |id '-' TEXT;
mt: id '-' id ',' 
  |id '-' id ;

id: TEXT;

description: TEXT;
difficulty: Integer;

SEPARATOR: NEWLINE+ | (';'+ NEWLINE*);

NEWLINE: '\r'? '\n';
COMMENT_SINGLE: '//' .*? NEWLINE -> skip;
COMMENT_MULTI: '/*' .*? '*/' -> skip;
//ID: '"' [a-zA-Z0-9]+ '"' ; 
TEXT:'"'.*?'"' ; 
Integer: [0-9]+; 
WS: [ \t]+ -> skip;
