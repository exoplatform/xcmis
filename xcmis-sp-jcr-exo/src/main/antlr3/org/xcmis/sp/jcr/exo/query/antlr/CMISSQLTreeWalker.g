tree grammar CMISSQLTreeWalker;

options{
  tokenVocab = CMISSQL;
  ASTLabelType = CommonTree;
}

@header {
package org.xcmis.sp.jcr.exo.query.antlr;

import org.xcmis.search.value.ValueAdapter;
import org.xcmis.sp.jcr.exo.query.QueryNameResolver;
import org.xcmis.sp.jcr.exo.query.qom.CmisQueryObjectModel;
import org.xcmis.sp.jcr.exo.query.qom.CmisQueryObjectModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.Source;

}

@members{
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
  
  private CmisQueryObjectModelFactory f =null;
  private QueryNameResolver resolver = null;
  private ValueAdapter vf = null;
  /**
   * Map selector name , associated queryName.
   */
  Map<String, String> selectors = new HashMap<String, String>();

 /**
   * Register queryName and return associated selectorName
   * 
   * @param queryName - String queryname name
   * @return selectorName
   * @throws TreeWalkerException if name already defined
   */
  private String registerQueryName(String queryName) throws TreeWalkerException {

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
  private String registerQueryName(String queryName, String selectorName) throws TreeWalkerException {
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
   * @return String selector name
   * @throws TreeWalkerException if there is more than one selector.
   */
  private String getDefaultSelectorName() throws TreeWalkerException {
    if (selectors.size() == 1) {
      String selectorName = selectors.keySet().iterator().next();
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
  private String getSelectorName(String qualifierName) throws TreeWalkerException {
    if (selectors.containsKey(qualifierName)) {
      return qualifierName;
    } else if (selectors.containsValue(qualifierName)) {
      Iterator<String> it = selectors.keySet().iterator();
      // find selectorName associated with queryName name
       while (it.hasNext()) {
        String selName = it.next();
        String ntName = selectors.get(selName);
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


query[CmisQueryObjectModelFactory fact , QueryNameResolver nameResolver, ValueAdapter vfact] returns[CmisQueryObjectModel q] 
	:	{
		f = fact;
		resolver = nameResolver;
		vf = vfact;
	   	Source src=null;	
	   	Column[] col=null;
	   	Constraint cons=null;
	   	Ordering[] ord=null;
	 	}
		^(QUERY s=source{src = $s.res;}
			cl=columnList{col = $cl.res;}
		        (c=searchCondition{cons = $c.res;})?
		        (o=orderings{ord = $o.res;})?
		 )
		{
		q =fact.createQuery(src,cons,ord,col); 
		}
	;
	catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	catch[RecognitionException e]{
		throw e;
        }

columnList returns[Column[\] res]
	:	^(COLUMNS ALL)
		{
                List<Column> l = new ArrayList<Column>();
	        
	        Iterator<String> it = selectors.keySet().iterator();
		
		while (it.hasNext()) {
		  String selName = it.next();
         	  l.add(f.column(selName, null, null));
                }
                
                res = new Column[l.size()];
                l.toArray((Column[])res);
                }
	|	{ List<Column> l = new ArrayList<Column>();}	
		^(COLUMNS (c=column{l.add($c.res);})+)
		{res = new Column[l.size()];
                l.toArray((Column[])res);
                }
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}

column returns[Column res]
	:	^(COLUMN q=qualifier ALL)  {res = f.column($q.res, null, null);}
	|	{String qname = null;
	 	String pname = null;
		String cname = null;}
		^(COLUMN (q=qualifier{qname=$q.res;})? (p=propertyName{pname=$p.res;}) (cn=columnName{cname=$cn.res;})?) 
		{
		String sname;
		if(qname!=null){
		  sname = getSelectorName(qname);
		}else{
		  sname = getDefaultSelectorName();
		  if(sname == null)
		   throw new TreeWalkerException("There is more than one table. Use selector or table name. Column " + pname);
		}
		
		res=f.column(sname, pname, (cname!=null)?cname:pname);
		}
	|	(^(COLUMN cf=columnFunction )  )
	        {res = $cf.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}

columnFunction returns[Column res]
	:	{String columnName = null;}
		SCORE (cn=columnName {columnName = $cn.res;})? 
	{	String sname = getDefaultSelectorName();
		if(sname == null)
		  throw new TreeWalkerException("There is more than one table. Use selector or table name. Column SCORE()");
		res = f.scoreColumn(sname, columnName);  
	}
	
//	|	^(UPPER qualifier? propertyName)
//	|       ^(LOWER qualifier? propertyName)
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
source returns[Source res]	
	:	(t=table {res = $t.res;})
	|	
		{JoinCondition jc = null;}
		^(JOIN jt=jointype  ls=source rs=source (c=joincondition{jc=$c.res;})?)         
		{ 
		if(jc == null){
		  unsupEx("JOIN without join condition not supported.");
		}
		res = f.join($ls.res, $rs.res, $jt.res, jc);
		}
	;
	catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
jointype returns[String res] 
	:	
	(	INNER {res = QueryObjectModelConstants.JCR_JOIN_TYPE_INNER;}
	|	LEFT_OUTER {res = QueryObjectModelConstants.JCR_JOIN_TYPE_LEFT_OUTER;}
        )
        ;

joincondition returns[JoinCondition res]
	:	
		{
		String sn1=null;
		String sn2=null;		
		}
		^(JOINCONDITION (s1=qualifier{sn1=$s1.res;})? p1=propertyName (s2=qualifier{sn2=$s2.res;})? p2=propertyName)
		{
		//TODO which one selectorname use if qualifier is not set
		if( sn1==null || sn2==null){
		  unsupEx("JoinCondition PROPRTY EQUALITY do not contains qualifiers(selector names). Don't know what to do.");
		}
		sn1 = getSelectorName(sn1);
		sn2 = getSelectorName(sn2);
		
		res = f.equiJoinCondition(sn1, $p1.res, sn2, $p2.res);
		}
	;
	catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
table returns[Source res]
	:
		{
		String tname=null;
		String sname=null;
		}	
		^(TABLE (t=tableName{tname=$t.res;}) (s=correlationName{sname=$s.res;})?)
		{
		if(sname!=null){
		  sname = registerQueryName(tname, sname);
		}else{
		  sname = registerQueryName(tname);
		}
		res = f.selector(resolver.getNodeTypeByQueryName(tname), sname);
		}
	;
	catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}	
	
searchCondition returns[Constraint res]
	: 	^(CONSTRAINT c=constraint){res=$c.res;}
	;
	catch[RecognitionException e]{
          throw e;
        }

constraint returns[Constraint res]
	:       p=predicate {res = $p.res;}
	|	^(NOT c=constraint) {res = f.not($c.res);}
	|	^(OR a=constraint b=constraint){res=f.or($a.res, $b.res);}
	|	^(AND a=constraint b=constraint){res=f.and($a.res, $b.res);}
	;
	catch[RecognitionException e]{
          throw e;
        }
       	catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
predicate returns[Constraint res]
	:	cp=comparisonPredicate {res=$cp.res;}
	|	in=inPredicate {res=$in.res;}
	|	like=likePredicate {res=$like.res;}
	|	n=nullPredicate {res=$n.res;}
	|	qcp=quantifiedComparisonPredicate {res=$qcp.res;}
	|	qIn=quantifiedInPredicate {res=$qIn.res;}
	|	full=fulltextsearch {res=$full.res;}
	|	inF=inFolder {res=$inF.res;}
	|	inT=inTree {res=$inT.res;}
	;
	catch[RecognitionException e]{
          throw e;
        }

comparisonPredicate returns[Constraint res]
	:	^(EQUAL 	ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO, $l.res);}
	|	^(NOTEQUAL 	ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO, $l.res);}
	|	^(LESS 		ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN, $l.res);}
	|	^(LESSEQUAL 	ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO, $l.res);}
	|	^(MORE 		ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN, $l.res);}
	|	^(MOREEQUAL 	ve=valueExpression l=literal) {res=f.comparison($ve.res, QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO, $l.res);}
	;
        catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}

		
quantifiedComparisonPredicate returns[Constraint res]
	:	^(EQUAL 	ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	|	^(NOTEQUAL 	ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	|	^(LESS 		ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	|	^(MORE 		ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	|	^(MOREEQUAL 	ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	|	^(LESSEQUAL 	ANY cr=columnReference l=literal){unsupEx("Multivalue ANY not supported yet.");}
	;
        catch[RecognitionException e]{
          throw e;
        }
	
	
inPredicate returns[Constraint res]
	:
		{ 
		  PropertyValue pv = null;
		  boolean isNot = false;
		  Constraint cons = null;  
		}	
		^(IN (NOT{isNot=true;})? 
		c=columnReference{pv=$c.res;} 
		l=literal
		{cons = f.comparison(pv, ((isNot)?QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO:QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), $l.res);}
		(sl=literal
		{
		if(isNot){
		  cons = f.and(cons, f.comparison(pv, QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO, $sl.res));
		}else{
		  cons = f.or(cons, f.comparison(pv, QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO, $sl.res));
		}
		}
		)*
		{
		  res = cons;
		}
		) 
	;
        catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}
	
quantifiedInPredicate returns[Constraint res]
	:	^(IN ANY NOT? columnReference literal+) 
	{
	res=null;
	unsupEx("ANY IN not supported yet.");
	}
	;
        catch[RecognitionException e]{
          throw e;
        }
	
likePredicate returns[Constraint res]
	:	^(LIKE cr=columnReference l=STRING_LITERAL) 
	{
	String val = $l.getText();
	val = processLIKELiteral(val);
	res=f.comparison($cr.res, QueryObjectModelConstants.JCR_OPERATOR_LIKE, f.literal(vf.createValue(val)));
	}	
	;
        catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}



nullPredicate returns[Constraint res]
	:
	{
	  String selName=null;
	}
	(	^(IS_NULL (s=qualifier{selName=$s.res;})? p=propertyName ) 
		{
		
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		  if(selName == null)
		    throw new TreeWalkerException("There is more than one table. Use selector or table name. IS NULL " + $p.res);
		}
		res = f.not(f.propertyExistence(selName,$p.res));
		}
	|	^(IS_NOT_NULL (s=qualifier{selName=$s.res;})? p=propertyName ) 
		{
		if(selName!=null){
		  selName = getSelectorName(selName);
		}else{
		  selName = getDefaultSelectorName();
		}
		res = f.propertyExistence(selName,$p.res);
		}
	)
	;
        catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}

fulltextsearch returns[Constraint res]
	:
		{
		String selName=null;
		String expr = null;
		}
		^(CONTAINS (q=qualifier{selName=$q.res;})? ((fe=FULLTEXT_EXPRESSION{expr = processFulltextLiteral($fe.getText());}) | (sl=STRING_LITERAL{expr = processLiteral($sl.getText());})))
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
		
		res = f.fullTextSearch(selName, null,f.literal(vf.createValue(expr)));
		}
		
	;
	catch[RecognitionException e]{
          throw e;
	}
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}
	
inFolder returns[Constraint res]
	:
		{String selName = null;}
		^(IN_FOLDER (q=qualifier{selName=$q.res;})? STRING_LITERAL)
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
                res = f.inFolder(selName, uuid );
		}
	;
	catch[RecognitionException e]{
          throw e;
	}
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}

inTree returns[Constraint res]
	:	{String selName = null;}
		^(IN_TREE (q=qualifier{selName=$q.res;})? STRING_LITERAL)
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
                res = f.inTree(selName, uuid);
		}
	;
	catch[RecognitionException e]{
          throw e;
	}
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}

// analogue Dynamic Operand
valueExpression returns[DynamicOperand res]
	:	cr=columnReference
		{res = $cr.res;}
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
        catch[RecognitionException e]{
          throw e;
        }	
	
columnReference returns[PropertyValue res]
	:
		p=propertyName 
		{
		String selName = getDefaultSelectorName();
		if(selName == null)
		  throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		res=f.propertyValue(selName,$p.res);
		}
	|	s=qualifier p=propertyName 
		{
		String selName = getSelectorName($s.res);
		res=f.propertyValue(selName , $p.res);
		}
	;
        catch[RecognitionException e]{
          throw e;
        }
	catch[RepositoryException e]{
	  throw new TreeWalkerException(e.getMessage(), e);
	}


// ORDER BY
orderings returns[Ordering[\] res]
	:	{ List<Ordering> l = new ArrayList<Ordering>();}
		^(ORDERINGS (o=ordering{l.add($o.res);})+)
		{
		res = new Ordering[l.size()];
                l.toArray((Ordering[])res);
		}
	;
        catch[RecognitionException e]{
          throw e;
        }

ordering returns[Ordering res]
	:	
		pv = propertyval
		{
		res = $pv.res;
		}
	|	of = orderfunc
		{
		res = $of.res;
		}
        ; 
        catch[RecognitionException e]{
          throw e;
        }

propertyval returns[Ordering res]
	:	^(ASC n=columnName)
		{
		String selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = f.ascending(f.propertyValue(selName,$n.res));
		}
        |	^(DESC n=columnName)
        	{
        	String selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = f.descending(f.propertyValue(selName,$n.res));
		}
        ;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}

orderfunc returns[Ordering res]
	:	^(ASC SCORE)
		{
		String selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = f.ascending(f.fullTextSearchScore(selName));
		}
        |	^(DESC SCORE)
        	{
        	String selName = getDefaultSelectorName();
		if(selName == null)
		  //throw new TreeWalkerException("There is more than one table. Use selector or table name.");
		  unsupEx("Ordering with Joined virtual tables not supported yet.");
		  
		res = f.descending(f.fullTextSearchScore(selName));
		}
        ;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}


literal returns[Literal res]
	:	sl=stringLiteral {res=$sl.res;}
	|	nl=numericLiteral {res=$nl.res;}
	|	bl=booleanLiteral {res=$bl.res;}
	|	dl = dateTimeLiteral {res =$dl.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }

	
stringLiteral returns[Literal res]
	:	l=STRING_LITERAL 	
		{
		String val = $l.getText();
		val = processLiteral(val);
		//val = removeQuotes(val,'\'');
		res = f.literal(vf.createValue(val));
		}
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
numericLiteral returns[Literal res]
	:	l=NUMERIC_LITERAL
		{
		String val = $l.getText();
		val = removeQuotes(val,'\'');
		res = f.literal(vf.createValue(val, PropertyType.DOUBLE));
		}
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
	
booleanLiteral returns[Literal res]
	:	TRUE {res = f.literal(vf.createValue("true", PropertyType.BOOLEAN));}
	|	FALSE {res = f.literal(vf.createValue("false", PropertyType.BOOLEAN));}
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}

dateTimeLiteral returns[Literal res]
	:	TIMESTAMP sl=STRING_LITERAL
	{
		String date = processLiteral($sl.getText());
		{res = f.literal(vf.createValue(date, PropertyType.DATE));}
	}	
	;
        catch[RecognitionException e]{
          throw e;
        }
        catch[RepositoryException e]{
		throw new TreeWalkerException(e.getMessage(), e);
	}
        
qualifier returns[String res]
	:	^(QUALIFIER i=id){res=$i.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }
        
tableName returns[String res]
	:	^(TABLE_NAME i=id){res=$i.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }

correlationName returns[String res]
	:	^(CORRELATION_NAME i=id){res=$i.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }
	
propertyName returns[String res]
	:	^(PROPERTY_NAME i=id){res=$i.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }
       
columnName returns[String res]
	:	^(COLUMN_NAME i=id){res=$i.res;}
	;
        catch[RecognitionException e]{
          throw e;
        }
	
id returns[String res]
	:	i=IDENTIFIER
		{
		String val = $i.getText();
		//res = removeQuotes(val,'"');
		res = val;
		}
	;
        catch[RecognitionException e]{
          throw e;
        }
