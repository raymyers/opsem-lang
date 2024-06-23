grammar OpSem;
start
    : block* EOF
    ;

block : semRule | latexBlock;

latexBlock : LATEX LBRACE latexRendering* LINEBREAK? RBRACE;

latexRendering: LINEBREAK VARIABLE+ EQUAL StringLiteral  ;

semRule: LINEBREAK?
    RULE VARIABLE semBlock
    ;

semBlock:
     LBRACE
     condLayer
     (LINEBREAK
     TILDE
     condLayer)+
     LINEBREAK? RBRACE
     LINEBREAK?
     ;
condLayer : condLine*;
condLine
    : LINEBREAK semBlock
    | LINEBREAK cond (RARROW cond)?
    ;

cond : exprs (COMMA exprs)*  ;

exprs: expr+;
expr: VARIABLE
    | LPAREN exprs RPAREN;


variable
    : VARIABLE
    ;

RULE : 'rule';

LATEX : 'latex';


VARIABLE
    : VALID_ID_START VALID_ID_CHAR*
    ;

fragment VALID_ID_START
    : 'a' .. 'z'
    | 'A' .. 'Z'
    | '_'
    ;

fragment VALID_ID_CHAR
    : VALID_ID_START
    | '0' .. '9'
    ;

StringLiteral
  : UnterminatedStringLiteral '"'
  ;

fragment UnterminatedStringLiteral
  : '"' (~["] | '\\' (. | EOF))*
  ;
fragment NUMBER
    : ('0' .. '9')+ ('.' ('0' .. '9')+)?
    ;

fragment UNSIGNED_INTEGER
    : ('0' .. '9')+
    ;

fragment E
    : 'E'
    | 'e'
    ;

fragment SIGN
    : '+'
    | '-'
    ;


TILDE
    : '~'
    ;

COMMA
    : ','
    ;

LBRACE
    : '{'
    ;

RBRACE
    : '}'
    ;

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;
RARROW
    : '=>'
    ;
EQUAL
    : '='
    ;
LINEBREAK : [\r\n]+;
WS
    : [ \t]+ -> skip
    ;