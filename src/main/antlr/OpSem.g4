grammar OpSem;
start
    : semRule* EOF
    ;

semRule: LINEBREAK?
    RULE VARIABLE LBRACE
    topCondLine* LINEBREAK
    TILDE
    bottomCondLine*
    LINEBREAK? RBRACE
    LINEBREAK?
    ;
topCondLine : condLine;
bottomCondLine : condLine;
condLine: (LINEBREAK cond (RARROW cond)?);
cond : exprs (COMMA exprs)*  ;

exprs: expr+;
expr: VARIABLE
    | LPAREN exprs RPAREN;


variable
    : VARIABLE
    ;

RULE : 'rule';

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

LINEBREAK : [\r\n]+;
WS
    : [ \t]+ -> skip
    ;