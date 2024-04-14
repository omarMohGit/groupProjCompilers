grammar PL;

@header {
import backend.*;
}

@members {
}

program returns [Expr program]
    : statements = statement*EOF{$program =new block(statements);}
    ;

statement returns [Expr statementResult]
    :assignment ';' {$statementResult= $assignment.var;}
    |expr ';' {$statementResult = $expr.exprvalue;}
    |loop {$statementResult= $loop.loopResult;}
    |ifStatement {$statementResult= $ifStatement.ifResult;}
    |funDef {$statementResult=$funDef.funcResult;}
    ;

assignment returns [Expr var]
    : 'let'? ID '=' expr {$var = new Assign($ID.text,$expr.exprValue);}
    ;
expr returns [Expr exprValue]
    : '(' expr ')' {$exprValue= $expr.exprValue;}
    | x= expr '+' y=expr {$exprValue = new Operations (Operator.ADD, $x.exprValue,$y.exprValue);}
    | x= expr '*' y=expr {$exprValue = new Operations (Operator.MULT, $x.exprValue,$y.exprValue);}
    | x= expr '/' y=expr {$exprValue = new Operations (Operator.DIV, $x.exprValue,$y.exprValue);}
    | x= expr '-' y=expr {$exprValue = new Operations (Operator.SUB, $x.exprValue,$y.exprValue);}
    | ID '(' argList ')' {$exprValue new Invoke ($ID.text, $argList.arglist);}
    | ID {$exprValue = new Deref($ID.text);}
    | NUMERIC {$exprValue = new IntegerLiteral($NUMERIC.text);}
    | STRING {$exprValue = new StringLiteral($STRING.text.substring(1, $STRING.text.length() - 1));}
    | BOOLEAN {$exprValue = new BooleanLiteral($BOOLEAN.text);}
    ;

loop returns [Expr loopResult]
    : 'for' '(' assignment ';' expr ';' assignment ')' '{' statement* '}' {$loopResult = new Loop ($expr.exprValue, new Block ($Statement));}
    ;
    
ifStatement returns [Expr ifResult]
    : 'if' '(' expr ')' '{' statement* '}' 'else' '{' statement* '}' {$ifResult= new ifStatment ($expr.exprValue, new Block ($ifTrue.statmeent), new Block ($ifFalse.statement));}
    ;

funDef returns [Expr funcResult]
    : 'function' ID '(' params ')' '{' statement* '}' { $funcResult = new FuncData($ID.text, $params.paramList, new Block($statement)); }
    ; 

params returns [List<String> paramList]
    : (ID (',' ID)*)? {...}
    ;

argList returns [List<Expr> argList]
    : expr (',' expr)* {...}
    ;






NUMERIC: ('0'..'9')+ ('.' ('0'..'9')*)?;
STRING: '"' ('\\"' | ~'"')* '"';
BOOLEAN: 'true' | 'false';
ID: ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;
COMMENT: '/*' .*? '*/' -> skip;
WHITESPACE: (' ' | '\t' | '\r' | '\n')+ -> skip;
