grammar CMISSQL;

options{
   output = AST;
   backtrack=true;
}

tokens{
  QUERY;
  COLUMNS;
  COLUMN;
  CONSTRAINT;
  ORDERINGS;
  ASC;
  DESC;
  JOINCONDITION;
  QUALIFIER;
  TABLE;
  JOIN;
  INNER;
  LEFT_OUTER;
  IS_NULL;
  IS_NOT_NULL;
  PROPERTY_NAME;
  COLUMN_NAME;
  TABLE_NAME;
  CORRELATION_NAME;
}

@parser::header {
package org.xcmis.search.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
}

@lexer::header{
package org.xcmis.search.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
}

@parser::members{
  private final List<String>    msgList        = new ArrayList<String>();
  
  private void reportExceptionMessage(String msg){
    msgList.add(msg);
  }
  
  public boolean hasExceptions(){
    return (msgList.size() > 0);
  }
  
  public String getExceptionMessage(){
    if(hasExceptions()){
      StringBuilder b = new StringBuilder("There is CMIS SQL parser errors: \n");
      Iterator<String> it = msgList.iterator();
      while (it.hasNext()) {
        b.append(it.next() + '\n');
      }

      return b.toString();
    }else{
      return null;
    }
  }

  public void emitErrorMessage(String msg) {
    super.emitErrorMessage("pars "+msg);
    reportExceptionMessage(msg);
  }
}

@lexer::members{
  private final List<String>    msgList        = new ArrayList<String>();
  
  private void reportExceptionMessage(String msg){
    msgList.add(msg);
  }
  
  public boolean hasExceptions(){
    return (msgList.size() > 0);
  }
  
  public String getExceptionMessage(){
    if(hasExceptions()){
      StringBuilder b = new StringBuilder("There is CMIS SQL lexer errors: \n");
      Iterator<String> it = msgList.iterator();
      while (it.hasNext()) {
        b.append(it.next() + '\n');
      }

      return b.toString();
    }else{
      return null;
    }
  }

  public void emitErrorMessage(String msg) {
    super.emitErrorMessage("lex " + msg);
    reportExceptionMessage(msg);
  }
}
query	:
		SELECT columnList FROM source (WHERE searchCondition)? (ORDER_BY orderings)? EOF
		-> ^(QUERY source columnList searchCondition? orderings?)
	;
	
columnList
	:	ALL -> ^(COLUMNS ALL)
	|	column (COMMA column)* -> ^(COLUMNS column+)
	;

fragment column
	:	qualifier DOT ALL -> ^(COLUMN qualifier ALL)
	|	valueExpression (AS columnName)? -> ^(COLUMN valueExpression columnName?)
//	|       SCORE LPAR RPAR (AS columnName)? -> ^(SCORE columnName?)
//	|	UPPER LPAR columnReference RPAR (AS columnName)? -> ^(UPPER columnReference columnName?)
//	|       LOWER LPAR columnReference RPAR (AS columnName)? -> ^(LOWER columnReference columnName?)
	;

source	:
	        quotedJoinTable (jointype^ source ( ON! joincondition)?)*
	;
	
		
fragment quotedJoinTable
	:	(table | (LPAR! quotedJoinTable (jointype^ source ( ON! joincondition)?)* RPAR!)+)
	;
	
jointype:	
	(
		T_INNER_JOIN -> ^(JOIN INNER)
	|	T_LEFT_OUTER_JOIN -> ^(JOIN LEFT_OUTER)
        )
        ;



joincondition
	:	(LPAR a=columnReference EQUAL b=columnReference RPAR) -> ^(JOINCONDITION $a $b)
	;

table
	:	(tableName (AS correlationName)?) -> ^(TABLE tableName correlationName?)
	;
	
valueExpression
	:	columnReference
	|	SCORE LPAR RPAR -> ^(SCORE)
//	|	UPPER LPAR columnReference RPAR -> ^(UPPER columnReference)
//	|       LOWER LPAR columnReference RPAR -> ^(LOWER columnReference)
	;	

fragment columnReference
	:	(qualifier DOT!)? propertyName
	;

	

	
searchCondition
	: 	constraint -> ^(CONSTRAINT constraint)	
	;

constraint
	:        orConstraint
	;
	
orConstraint
	:	(andConstraint (OR^ andConstraint)*)
	;
		
andConstraint
	:	((signedConstraint) (AND^ (signedConstraint))*)
	;
	
signedConstraint
	:	(NOT^ )?  (predicate| quotedConstraint)
	;

quotedConstraint
	:	LPAR! constraint RPAR!
	;	

predicate
	:	comparisonPredicate 
	|	inPredicate
	|	likePredicate
	|	nullPredicate
	|	quantifiedComparisonPredicate
	|	quantifiedInPredicate
	|	fulltextsearch
	|	inFolder
	|	inTree
	;
	
comparisonPredicate
	:	columnReference  (EQUAL^ | NOTEQUAL^ | LESS^ | LESSEQUAL^ | MORE^ | MOREEQUAL^) literal;
	
quantifiedComparisonPredicate
	:	literal EQUAL ANY columnReference -> ^(EQUAL ANY columnReference literal)
	|	literal NOTEQUAL ANY columnReference -> ^(NOTEQUAL ANY columnReference literal)
	|	literal LESS ANY columnReference -> ^(LESS ANY columnReference literal)
	|	literal LESSEQUAL ANY columnReference -> ^(LESSEQUAL ANY columnReference literal)
	|	literal MORE ANY columnReference -> ^(MORE ANY columnReference literal)
	|	literal MOREEQUAL ANY columnReference -> ^(MOREEQUAL ANY columnReference literal)
	;	
	
literal
	:	(NUMERIC_LITERAL 
	|	STRING_LITERAL
	|	TRUE
	|	FALSE
	|	(TIMESTAMP STRING_LITERAL)
	)
	;
	
inPredicate
	:	columnReference (NOT)? IN in_value_list -> ^(IN NOT? columnReference in_value_list)
	;
	
quantifiedInPredicate
	:	ANY columnReference (NOT)? IN in_value_list -> ^(IN ANY NOT? columnReference in_value_list)
	;
	
likePredicate
	:	columnReference LIKE STRING_LITERAL -> ^(LIKE columnReference STRING_LITERAL)
	|	columnReference NOT LIKE STRING_LITERAL -> ^(NOT ^(LIKE columnReference STRING_LITERAL))
	;

nullPredicate
	:	columnReference IS NULL -> ^(IS_NULL columnReference)
	|	columnReference IS NOT NULL -> ^(IS_NOT_NULL columnReference)
	;
		
fulltextsearch
	:	CONTAINS LPAR (qualifier COMMA )? fulltextSearchExpression RPAR -> ^(CONTAINS qualifier? fulltextSearchExpression)
	;
	
//TODO CMIS-Spaces Flex client send incorrect statement, so contains use both string literal and fulltextexpression
fragment fulltextSearchExpression
	:	FULLTEXT_EXPRESSION | STRING_LITERAL;
	
inFolder:	IN_FOLDER LPAR (qualifier COMMA)? STRING_LITERAL RPAR -> ^(IN_FOLDER qualifier? STRING_LITERAL)
	;

inTree	:	IN_TREE LPAR (qualifier COMMA)? STRING_LITERAL RPAR -> ^(IN_TREE qualifier? STRING_LITERAL)
	;
	
fragment in_value_list
	:	LPAR! literal ( COMMA! literal)* RPAR!
	;

// ORDER BY
orderings
	:	(ordering (COMMA ordering)*) -> ^(ORDERINGS ordering+);
	
ordering
	:	(columnName
		(     	T_ASC? -> ^(ASC columnName)
        	|	 T_DESC -> ^(DESC columnName)
        	))
        |	
        	(orderFunction
		(     	T_ASC? -> ^(ASC orderFunction)
        	|	 T_DESC -> ^(DESC orderFunction)
        	))
        	
        ; 
        
orderFunction
	:	SCORE LPAR! RPAR!
	;

        
//TODO do we really need it?
qualifier 
	:	IDENTIFIER  -> ^(QUALIFIER IDENTIFIER)//tableName | correlationName
	;
        
tableName
	:	IDENTIFIER -> ^(TABLE_NAME IDENTIFIER);

correlationName
	:	IDENTIFIER -> ^(CORRELATION_NAME IDENTIFIER);
	
propertyName
	:	IDENTIFIER -> ^(PROPERTY_NAME IDENTIFIER); 
       
columnName
	:	IDENTIFIER -> ^(COLUMN_NAME IDENTIFIER);

	

SELECT	:	(S E L E C T);//'SELECT';
FROM	:	(F R O M); //'FROM';
WHERE	:	(W H E R E); //'WHERE';
ORDER_BY
	:	(O R D E R WS_SYMBOL+ B Y);
AS	:	(A S);//'AS';

ALL	:	'*';
DOT	:	'.';
COMMA	:	',';
LPAR	:	'(';
RPAR	:	')';
EQUAL 	:	'=';
LESS	:	'<';
MORE	:	'>';

NOTEQUAL:	LESS MORE;
LESSEQUAL
	:	LESS EQUAL;
MOREEQUAL
	:	MORE EQUAL;

//UPPER	:	'UPPER';
//LOWER	:	'LOWER';
SCORE	:	(S C O R E);//'SCORE';
CONTAINS:	(C O N T A I N S);//'CONTAINS';
IN_FOLDER
	:	(I N UNDERSCORE F O L D E R);//'IN_FOLDER';
IN_TREE	:	(I N UNDERSCORE T R E E);//'IN_TREE';

T_INNER_JOIN
	:	((I N N E R WS_SYMBOL+)? J O I N)
	;
T_LEFT_OUTER_JOIN
	:	(L E F T (WS_SYMBOL+ O U T E R)? WS_SYMBOL+ J O I N)
	;

IN	:	(I N);//'IN';
ON	:	(O N);//'ON';
OR	:	(O R);//'OR';
AND	:	(A N D);//'AND';
NOT	:	(N O T);//'NOT';
LIKE	:	(L I K E);//'LIKE';
IS	:	(I S);//'IS';
NULL	:	(N U L L);//'NULL';
ANY	:	(A N Y);//'ANY';
T_ASC	:	(A S C);//'ASC';
T_DESC	:	(D E S C);//'DESC';

TRUE	:	(T R U E);//'TRUE' | 'true';
FALSE	:	(F A L S E);//'FALSE' | 'false';
TIMESTAMP
	:	(T I M E S T A M P);//'TIMESTAMP';


fragment QUOTE
	:	'\'';
fragment DQUOTE
	:	'"';
fragment UNDERSCORE
	:	'_';
fragment PERCENTAGE
	:	'%';
fragment SLASH
	:	'\\';
fragment FULLTEXT_NOT
	:	'-';

STRING_LITERAL
	:	QUOTE ((~(ESCAPE_SYMBOLS))|(SLASH (ESCAPE_SYMBOLS | PERCENTAGE | UNDERSCORE)))* QUOTE
	;
	
FULLTEXT_EXPRESSION
	:	DQUOTE ((~(ESCAPE_SYMBOLS)) | (SLASH (ESCAPE_SYMBOLS | FULLTEXT_NOT)))* DQUOTE;
	
fragment ESCAPE_SYMBOLS
	:	(QUOTE | DQUOTE | SLASH );

NUMERIC_LITERAL
	:	('+'|'-')? DIDGIT+ (DOT DIDGIT+)?
	|	('+'|'-')? DOT DIDGIT+
	|	(DIDGIT+ (DOT DIDGIT+)?) ('E'|'e') ('+'|'-') DIDGIT+
	;

IDENTIFIER
	: (~(WS_SYMBOL | DOT | COMMA | QUOTE | DQUOTE | LPAR | RPAR | ALL | LESS | MORE | EQUAL))+
	//	(LETTER  (UNDERSCORE | D | LETTER)*)
        //|       (DQUOTE  (~DQUOTE | (DQUOTE DQUOTE))*  DQUOTE)
        ;

WS	:	(WS_SYMBOL+ {$channel=HIDDEN;});

//fragment LETTER
//	:	~( DOT | COMMA | LPAR | RPAR | ALL| QUOTE | DQUOTE | WS_SYMBOL | D | UNDERSCORE | LESS | MORE | EQUAL)
//	;

fragment DIDGIT
	:	'0'..'9'
	;

fragment WS_SYMBOL
	:	('\t' | ' ' | '\r' | '\n'| '\u000C');

// LATIN ALPHABET

fragment A	:	'A'|'a';
fragment B	:	'B'|'b';
fragment C	:	'C'|'c';
fragment D	:	'D'|'d';
fragment E	:	'E'|'e';
fragment F	:	'F'|'f';
fragment G	:	'G'|'g';
fragment H	:	'H'|'h';
fragment I	:	'I'|'i';
fragment J	:	'J'|'j';
fragment K	:	'K'|'k';
fragment L	:	'L'|'l';
fragment M	:	'M'|'m';
fragment N	:	'N'|'n';
fragment O	:	'O'|'o';
fragment P	:	'P'|'p';
fragment Q	:	'Q'|'q';
fragment R	:	'R'|'r';
fragment S	:	'S'|'s';
fragment T	:	'T'|'t';
fragment U	:	'U'|'u';
fragment V	:	'V'|'v';
fragment X	:	'X'|'x';
fragment Y	:	'Y'|'y';
fragment W	:	'W'|'w';
fragment Z	:	'Z'|'z';






