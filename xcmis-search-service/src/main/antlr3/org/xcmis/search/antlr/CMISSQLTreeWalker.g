tree grammar CMISSQLTreeWalker;

options {
  tokenVocab   = CMISSQL;
  ASTLabelType = CommonTree;
}

@header {
package  org.xcmis.search.antlr;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.exoplatform.commons.utils.ISO8601;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.And;
import org.xcmis.search.model.constraint.ChildNode;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.constraint.DescendantNode;
import org.xcmis.search.model.constraint.FullTextSearch;
import org.xcmis.search.model.constraint.Not;
import org.xcmis.search.model.constraint.Operator;
import org.xcmis.search.model.constraint.Or;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.operand.DynamicOperand;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Literal;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.ordering.Order;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Join;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.Source;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.JoinCondition;
import org.xcmis.search.model.source.join.JoinType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



}

@members {
  private static final String MORE_THAN_ONE_STR = "Query contains more than one selector. Set selector name for";
  
  class TreeWalkerException extends RecognitionException {

    private String    message;
    private Throwable cause;

    public TreeWalkerException(String message, Throwable cause) {
      this.message = message;
      this.cause = cause;
    }

    public TreeWalkerException(String message) {
      this.message = message;
    }

    public Throwable getCause() {
      return (cause == this ? null : cause);
    }

    public String getMessage() {
      return message;
    }
  }
  

  /**
   * Map selector name , associated queryName.
   */
  Map<SelectorName, SelectorName> selectors = new HashMap<SelectorName, SelectorName>();

 /**
   * Register queryName and return associated selectorName
   * 
   * @param queryName - String queryname name
   * @return selectorName
   * @throws TreeWalkerException if name already defined
   */
  private SelectorName registerQueryName(SelectorName queryName) throws TreeWalkerException {

    if (selectors.containsValue(queryName)) {
      throw new TreeWalkerException("queryName [" + queryName + "] already defined. Use selector name.");
    } else {
      selectors.put(queryName, queryName);
      return queryName;
    }
  }

  /**
   * Register queryName and return associated selectorName
   * 
   * @param queryName - String queryName name
   * @param selectorName - selector name
   * @return selectorName
   * @throws TreeWalkerException if name already defined
   */
  private SelectorName registerQueryName(SelectorName queryName, SelectorName selectorName) throws TreeWalkerException {
    if (selectors.containsKey(queryName)) {
      throw new TreeWalkerException("There is same table [" + queryName
          + "] without selector name. Set selector name for previous definition.");
    }
    if (selectors.containsKey(selectorName)) {
      throw new TreeWalkerException("Selector name [" + selectorName
          + "] already defined. Use another selector name.");
    }
    selectors.put(selectorName, queryName);
    return selectorName;
  }

  /**
   * Get default selector name. If there is more than one table defined,
   * exception will be thrown.
   * 
   * @return SelectorName selector name
   * @throws TreeWalkerException if there is more than one selector.
   */
  private SelectorName getDefaultSelectorName() throws TreeWalkerException {
    if (selectors.size() == 1) {
      SelectorName selectorName = selectors.keySet().iterator().next();
      return selectorName;
    } else {
      return null;
      //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
    }
  }

  /**
   * Get selector name for qualifer. Qualifier may be selector name or table
   * name. Method returns actual selector name.
   * 
   * @param qualifierName - selector or table name
   * @return selector name
   * @throws TreeWalkerException if there is no such selector or selector name.
   */
  private SelectorName getSelectorName(SelectorName qualifierName) throws TreeWalkerException {
    if (selectors.containsKey(qualifierName)) {
      return qualifierName;
    } else if (selectors.containsValue(qualifierName)) {
      Iterator<SelectorName> it = selectors.keySet().iterator();
      // find selectorName associated with queryName name
       while (it.hasNext()) {
        SelectorName selName = it.next();
        SelectorName ntName = selectors.get(selName);
        if ((!selName.equals(qualifierName)) && (ntName.equals(qualifierName))) {
          return selName;
        }
      }
      throw new TreeWalkerException("queryName name [" + qualifierName + "] has no associated selector name.");
    } else {
      throw new TreeWalkerException("There is no selector name or table name [" + qualifierName + "]");
    }

  }


  private String removeQuotes(String src, char quote) throws TreeWalkerException{
    //check is string quoted
    
    if(src.charAt(0)==quote && src.charAt(src.length()-1)==quote){
      // cut quotes
      src = src.substring(1, src.length()-1);
      src = src.replace(String.valueOf(new char[] { quote, quote }), String.valueOf(quote));
    }
    
    return src;
  }
  
  private void unsupEx(String str){
    throw new UnsupportedOperationException(str);
  }

    
  public String processLiteral(String src) throws TreeWalkerException {

    // cut quotes
    src = src.substring(1, src.length()-1);

    char[] chars = src.toCharArray();

    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    for (char c : chars) {

      if (escaped) {
        switch (c) {
        case '"':
          sb.append('"');
          break;
        case '\'':
          sb.append('\'');
          break;
        case '\\':
          sb.append('\\');
          break;
        case '\%':
          sb.append('\%');
          break;
        case '_':
          sb.append('_');
          break;
        default:
          // unknown escape symbol
          sb.append('\\');
          sb.append(c);
          throw new TreeWalkerException("Literal <" + src
              + "> contains unexpected escape symbol <\\" + c + ">.");
        }
        escaped = false;
      } else {
        if (c == '\\') {
          escaped = true;
        } else {
          sb.append(c);
        }
      }

    }
    if (escaped) {
      throw new TreeWalkerException("Literal <" + src
          + "> ended with not complete escape symbol <\\>, use<\\\\>.");
    }

    return sb.toString();
  }

public String processLIKELiteral(String src) throws TreeWalkerException {

    // cut quotes
    src = src.substring(1, src.length()-1);

    char[] chars = src.toCharArray();

    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    for (char c : chars) {

      if (escaped) {
        switch (c) {
        case '"':
          sb.append('"');
          break;
        case '\'':
          sb.append('\'');
          break;
        case '\\':
          sb.append('\\');
          break;
        case '\%':
          sb.append('\\');
          sb.append('\%');
          break;
        case '_':
          sb.append('\\');
          sb.append('_');
          break;
        default:
          // unknown escape symbol
          sb.append('\\');
          sb.append(c);
          throw new TreeWalkerException("Literal <" + src
              + "> contains unexpected escape symbol <\\" + c + ">.");
        }
        escaped = false;
      } else {
        if (c == '\\') {
          escaped = true;
        } else {
          sb.append(c);
        }
      }

    }
    if (escaped) {
      throw new TreeWalkerException("Literal <" + src
          + "> ended with not complete escape symbol <\\>, use<\\\\>.");
    }

    return sb.toString();
  }

public String processFulltextLiteral(String src) throws TreeWalkerException {

    // cut quotes
    src = src.substring(1, src.length()-1);

    char[] chars = src.toCharArray();

    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    
    for (char c: chars) {
      if(escaped){
        switch(c){
        case('"'):
          sb.append('"');
          break;
        case('\''):
          sb.append('\'');
          break;
        case('-'):
          sb.append('\\');
          sb.append('-');
          break;
        case('\\'):
          sb.append('\\');
          break;
        default:
          // unknown escape symbol
          sb.append('\\');
          sb.append(c);
          throw new TreeWalkerException("FulltextExpression <" + src
              + "> contains unexpected escape symbol <\\" + c + ">.");
        }
        escaped = false;
      }else {
        if(c == '\\'){
          escaped =true;
        }else{
          sb.append(c);
        }
      }
    }
    
    if (escaped) {
      throw new TreeWalkerException("Literal <" + src
          + "> ended with not complete escape symbol <\\>, use<\\\\>.");
    }
    
    //Replase not escaped quotes by doublequote
    chars = sb.toString().toCharArray();

    sb = new StringBuilder();
    escaped = false;
    
    for (char c: chars) {
    
      if(escaped){
        sb.append(c);
        escaped = false;
      }else {
        switch(c){
        case('\\'):
          sb.append(c);
          escaped = true;
          break;
        case('\''):
          sb.append('"');
          break;
        default:
          sb.append(c);
        }
      }
    }


    return sb.toString();
  }


}

query[] returns [Query q]
  :
  {
	   	Source src=null;	
	   	List<Column> col=null;
	   	Constraint cons=null;
	   	List<Ordering> ord=null;
	 	}
  ^(
    QUERY s=source {src = $s.res;} cl=columnList {col = $cl.res;}
    (
      c=searchCondition {cons = $c.res;}
    )?
    (
      o=orderings {ord = $o.res;}
    )?
   )
  { 
		q =new Query(src,cons,ord,col,Limit.NONE); 
		}
  ;

catch [RecognitionException e] {
		throw e;
}

columnList returns [List<Column> res]
  :
  ^(COLUMNS ALL)
  {
   List<Column> l = new ArrayList<Column>();
   Iterator<SelectorName> it = selectors.keySet().iterator();
		
		while (it.hasNext()) {
		  SelectorName selName = it.next();
         	  l.add(new Column(selName));
                }
                
                res = l;
                }
  | { List<Column> l = new ArrayList<Column>();}
  ^(
    COLUMNS
    (
      c=column {l.add($c.res);}
    )+
   )
  {res = l;};
catch [RecognitionException e] {
          throw e;
}

column returns [Column res]
  :
  ^(COLUMN q=qualifier ALL)
  {res = new Column($q.res);}
  | {SelectorName qname = null;
	 	String pname = null;
		String cname = null;}
  ^(
    COLUMN
    (
      q=qualifier {qname=$q.res;}
    )?
    (
      p=propertyName {pname=$p.res;}
    )
    (
      cn=columnName {cname=$cn.res;}
    )?
   )
  {
		SelectorName sname;
		if(qname!=null){
		  sname = getSelectorName(qname);
		}else{
		  sname = getDefaultSelectorName();
		  if(sname == null)
		   throw new TreeWalkerException("There is more than one table. Use selector or table name. Column " + pname);
		}
		
		res= new Column(sname, pname, (cname!=null)?cname:pname);
		}
  |
  (
    ^(COLUMN cf=columnFunction)
  )
  {res = $cf.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

columnFunction returns [Column res]
  :
  {String columnName = null;} SCORE
  (
    cn=columnName {columnName = $cn.res;}
  )?
  {	SelectorName sname = getDefaultSelectorName();
		if(sname == null)
		  throw new TreeWalkerException("There is more than one table. Use selector or table name. Column SCORE()");
		res = new Column(sname, columnName, columnName);  
	}

  //	|	^(UPPER qualifier? propertyName)
  //	|       ^(LOWER qualifier? propertyName)
  ;
catch [RecognitionException e] {
          throw e;
}

source returns [Source res]
  :
  (
    t=table {res = $t.res;}
  )
  | {JoinCondition jc = null;}
  ^(
    JOIN jt=jointype ls=source rs=source
    (
      c=joincondition {jc=$c.res;}
    )?
   )
  { 
		if(jc == null){
		  unsupEx("JOIN without join condition not supported.");
		}
		
		res = new Join($ls.res, $jt.res,$rs.res,  jc);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

jointype returns [JoinType res]
  :
  (
    INNER {res = JoinType.INNER;}
    | LEFT_OUTER {res = JoinType.LEFT_OUTER;}
  )
  ;

joincondition returns [JoinCondition res]
  :
  {
		SelectorName sn1=null;
		SelectorName sn2=null;		
		}
  ^(
    JOINCONDITION
    (
      s1=qualifier {sn1=$s1.res;}
    )?
    p1=propertyName
    (
      s2=qualifier {sn2=$s2.res;}
    )?
    p2=propertyName
   )
  {
		//TODO which one selectorname use if qualifier is not set
		if( sn1==null || sn2==null){
		  unsupEx("JoinCondition PROPRTY EQUALITY do not contains qualifiers(selector names). Don't know what to do.");
		}
		sn1 = getSelectorName(sn1);
		sn2 = getSelectorName(sn2);
		
		res = new EquiJoinCondition(sn1, $p1.res, sn2, $p2.res);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

table returns [Source res]
  :
  {
		SelectorName tname=null;
		SelectorName sname=null;
		}
  ^(
    TABLE
    (
      t=tableName {tname=$t.res;}
    )
    (
      s=correlationName {sname=$s.res;}
    )?
   )
  {
		if(sname!=null){
		  sname = registerQueryName(tname, sname);
		}else{
		  sname = registerQueryName(tname);
		}
		res = new Selector(tname, sname);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

searchCondition returns [Constraint res]
  :
  ^(CONSTRAINT c=constraint)
  {res=$c.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

constraint returns [Constraint res]
  :
  p=predicate {res = $p.res;}
  |
  ^(NOT c=constraint)
  {res = new Not($c.res);}
  |
  ^(OR a=constraint b=constraint)
  {res=new Or($a.res, $b.res);}
  |
  ^(AND a=constraint b=constraint)
  {res=new And($a.res, $b.res);}
  ;
catch [RecognitionException e] {
          throw e;
}

predicate returns [Constraint res]
  :
  cp=comparisonPredicate {res=$cp.res;}
  | in=inPredicate {res=$in.res;}
  | like=likePredicate {res=$like.res;}
  | n=nullPredicate {res=$n.res;}
  | qcp=quantifiedComparisonPredicate {res=$qcp.res;}
  | qIn=quantifiedInPredicate {res=$qIn.res;}
  | full=fulltextsearch {res=$full.res;}
  | inF=inFolder {res=$inF.res;}
  | inT=inTree {res=$inT.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

comparisonPredicate returns [Constraint res]
  :
  ^(EQUAL ve=valueExpression l=literal)
   {res=new Comparison($ve.res, Operator.EQUAL_TO, $l.res);}
  |
  ^(NOTEQUAL ve=valueExpression l=literal)
  {res=new Comparison($ve.res, Operator.NOT_EQUAL_TO, $l.res);}
  |
  ^(LESS ve=valueExpression l=literal)
   {res=new Comparison($ve.res, Operator.LESS_THAN, $l.res);}
  |
  ^(LESSEQUAL ve=valueExpression l=literal)
  {res=new Comparison($ve.res, Operator.LESS_THAN_OR_EQUAL_TO, $l.res);}
  |
  ^(MORE ve=valueExpression l=literal)
  {res=new Comparison($ve.res, Operator.GREATER_THAN, $l.res);}
  |
  ^(MOREEQUAL ve=valueExpression l=literal)
    {res=new Comparison($ve.res, Operator.GREATER_THAN_OR_EQUAL_TO, $l.res);}
  ;
catch [RecognitionException e] {
          throw e;
}


quantifiedComparisonPredicate returns [Constraint res]
  :
  ^(EQUAL ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  |
  ^(NOTEQUAL ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  |
  ^(LESS ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  |
  ^(MORE ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  |
  ^(MOREEQUAL ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  |
  ^(LESSEQUAL ANY cr=columnReference l=literal)
  {unsupEx("Multivalue ANY not supported yet.");}
  ;
catch [RecognitionException e] {
          throw e;
}

inPredicate returns [Constraint res]
  :
  { 
		  PropertyValue pv = null;
		  boolean isNot = false;
		  Constraint cons = null;  
		}
  ^(
    IN
    (
      NOT {isNot=true;}
    )?
    c=columnReference {pv=$c.res;} l=literal {cons = new Comparison(pv, ((isNot)?Operator.NOT_EQUAL_TO:Operator.EQUAL_TO), $l.res);}
    (
      sl=literal {
		if(isNot){
		  cons = new And(cons, new Comparison(pv, Operator.NOT_EQUAL_TO, $sl.res));
		}else{
		  cons = new Or(cons, new Comparison(pv, Operator.EQUAL_TO, $sl.res));
		}
		}
    )*
    {
		  res = cons;
		}
   )
  ;
catch [RecognitionException e] {
          throw e;
}


quantifiedInPredicate returns [Constraint res]
  :
  ^(IN ANY NOT? columnReference literal+)
  {
	res=null;
	unsupEx("ANY IN not supported yet.");
	}
  ;
catch [RecognitionException e] {
          throw e;
}

likePredicate returns [Constraint res]
  :
  ^(LIKE cr=columnReference l=STRING_LITERAL)
  {
	String val = $l.getText();
	val = processLIKELiteral(val);
	res=new Comparison($cr.res, Operator.LIKE, new Literal(val));
	}
  ;
catch [RecognitionException e] {
          throw e;
}


nullPredicate returns [Constraint res]
  :
  {
	  SelectorName selName=null;
	}
  (
    ^(
      IS_NULL
      (
        s=qualifier {selName=$s.res;}
      )?
      p=propertyName
     )
    {
		
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		  if(selName == null)
		    throw new TreeWalkerException("There is more than one table. Use selector or table name. IS NULL " + $p.res);
		}
		res = new Not(new PropertyExistence(selName,$p.res));
		}
    |
    ^(
      IS_NOT_NULL
      (
        s=qualifier {selName=$s.res;}
      )?
      p=propertyName
     )
    {
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		}
		res = new PropertyExistence(selName,$p.res);
		}
  )
  ;
catch [RecognitionException e] {
          throw e;
}


fulltextsearch returns [Constraint res]
  :
  {
		SelectorName selName=null;
		String expr = null;
		}
  ^(
    CONTAINS
    (
      q=qualifier {selName=$q.res;}
    )?
    (
      (
        fe=FULLTEXT_EXPRESSION {expr = processFulltextLiteral($fe.getText());}
      )
      |
      (
        sl=STRING_LITERAL {expr = processLiteral($sl.getText());}
      )
    )
   )
  {
		
		//TODO contains may search by all nodes
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		  if(selName == null)
  		    throw new TreeWalkerException("There is more than one table. Use selector or table name. CONTAINS..");
		}
		//String expr = processFulltextLiteral($sl.getText());
		
		res = new FullTextSearch(selName, null , expr);
		}
  ;
catch [RecognitionException e] {
          throw e;
}


inFolder returns [Constraint res]
  :
  {SelectorName selName = null;}
  ^(
    IN_FOLDER
    (
      q=qualifier {selName=$q.res;}
    )?
    STRING_LITERAL
   )
  {
		
		//strip quotes
		String uuid = $STRING_LITERAL.getText();
		uuid = uuid.substring(1, uuid.length()-1);
		
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		  if(selName == null)
		    throw new TreeWalkerException("There is more than one table. Use selector or table name. IN_FOLDER(..");
		}
                res = new ChildNode(selName, uuid );
		}
  ;
catch [RecognitionException e] {
          throw e;
}


inTree returns [Constraint res]
  :
  {SelectorName selName = null;}
  ^(
    IN_TREE
    (
      q=qualifier {selName=$q.res;}
    )?
    STRING_LITERAL
   )
  {
		//strip quotes
		String uuid = $STRING_LITERAL.getText();
		uuid = uuid.substring(1, uuid.length()-1);
		
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		  if(selName==null)
		    throw new TreeWalkerException("There is more than one table. Use selector or table name. IN_TREE..");
		}
                res = new DescendantNode(selName, uuid);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

// analogue Dynamic Operand

valueExpression returns [DynamicOperand res]
  :
  cr=columnReference {res = $cr.res;}
  //	|	SCORE
  //		{
  //		String selName = getDefaultSelectorName();
  //		res=f.fullTextSearchScore(selName);
  //		}
  //	|	^(UPPER cr=columnReference){res=f.upperCase($cr.res);}
  //	|       ^(LOWER cr=columnReference){res=f.lowerCase($cr.res);}
  ;
//	catch[RepositoryException e]{
//	  throw new TreeWalkerException(e.getMessage(), e);
//	}
catch [RecognitionException e] {
          throw e;
}

columnReference returns [PropertyValue res]
  :
  p=propertyName {
		SelectorName selName = getDefaultSelectorName();
		if(selName == null)
		  throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		res=new PropertyValue(selName,$p.res);
		}
  | s=qualifier p=propertyName {
		SelectorName selName = getSelectorName($s.res);
		res=new PropertyValue(selName , $p.res);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

// ORDER BY

orderings returns [List<Ordering> res]
  :
  { List<Ordering> l = new ArrayList<Ordering>();}
  ^(
    ORDERINGS
    (
      o=ordering {l.add($o.res);}
    )+
   )
  {
		res = l;
		}
  ;
catch [RecognitionException e] {
          throw e;
}

ordering returns [Ordering res]
  :
  pv=propertyval {
		res = $pv.res;
		}
  | of=orderfunc {
		res = $of.res;
		}
  ;
catch [RecognitionException e] {
          throw e;
}

propertyval returns [Ordering res]
  :
  ^(ASC n=columnName)
  {
		SelectorName selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = new Ordering(new PropertyValue(selName, $n.res), Order.ASCENDING);
		}
  |
  ^(DESC n=columnName)
  {
        	SelectorName selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = new Ordering(new PropertyValue(selName, $n.res), Order.DESCENDING);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

orderfunc returns [Ordering res]
  :
  ^(ASC SCORE)
  {
		SelectorName selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = new Ordering(new FullTextSearchScore(selName), Order.ASCENDING);
		}
  |
  ^(DESC SCORE)
  {
        	SelectorName selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = new Ordering(new FullTextSearchScore(selName), Order.DESCENDING);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

literal returns [Literal res]
  :
  sl=stringLiteral {res=$sl.res;}
  | nl=numericLiteral {res=$nl.res;}
  | bl=booleanLiteral {res=$bl.res;}
  | dl=dateTimeLiteral {res =$dl.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

stringLiteral returns [Literal res]
  :
  l=STRING_LITERAL {
		String val = $l.getText();
		val = processLiteral(val);
		//val = removeQuotes(val,'\'');
		res = new Literal(val);
		}
  ;
catch [RecognitionException e] {
          throw e;
}

numericLiteral returns [Literal res]
  :
  l=NUMERIC_LITERAL {
		String val = $l.getText();
		val = removeQuotes(val,'\'');
		res = new Literal(Double.parseDouble(val));
		}
  ;
catch [RecognitionException e] {
          throw e;
}

booleanLiteral returns [Literal res]
  :
  TRUE {res = new Literal(new Boolean(true));}
  | FALSE {res = new Literal(new Boolean(false));}
  ;
catch [RecognitionException e] {
          throw e;
}

dateTimeLiteral returns [Literal res]
  :
  TIMESTAMP sl=STRING_LITERAL {
		String date = processLiteral($sl.getText());
		{res =  new Literal(ISO8601.parse(date));}
	}
  ;
catch [RecognitionException e] {
          throw e;
}

qualifier returns [SelectorName res]
  :
  ^(QUALIFIER i=id)
  {res=new SelectorName($i.res);}
  ;
catch [RecognitionException e] {
          throw e;
}

tableName returns [SelectorName res]
  :
  ^(TABLE_NAME i=id)
  {res=new SelectorName($i.res);}
  ;
catch [RecognitionException e] {
          throw e;
}

correlationName returns [SelectorName res]
  :
  ^(CORRELATION_NAME i=id)
  {res=new SelectorName($i.res);}
  ;
catch [RecognitionException e] {
          throw e;
}

propertyName returns [String res]
  :
  ^(PROPERTY_NAME i=id)
  {res=$i.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

columnName returns [String res]
  :
  ^(COLUMN_NAME i=id)
  {res=$i.res;}
  ;
catch [RecognitionException e] {
          throw e;
}

id returns [String res]
  :
  i=IDENTIFIER {
		String val = $i.getText();
		//res = removeQuotes(val,'"');
		res = val;
		}
  ;
catch [RecognitionException e] {
          throw e;
}
