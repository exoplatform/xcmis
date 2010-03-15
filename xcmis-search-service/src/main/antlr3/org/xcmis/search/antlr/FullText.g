/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
grammar FullText;

tokens {
	OR = 'OR';
	NOT = '-';
	AND;
}

@parser::header {
package  org.xcmis.search.antlr;
import  org.xcmis.search.lucene2.content.ErrorReporter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
}

@lexer::header {
package  org.xcmis.search.antlr;
import  org.xcmis.search.lucene2.content.ErrorReporter;
}

@members{
private ErrorReporter reporter = null;
private Query query=null;
private List<String> fields;
private Analyzer analyzer;

public void setErrorReporter(ErrorReporter errorReporter) {
        this.reporter = errorReporter;
}

public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
  String hdr = getErrorHeader(e);
  if (tokenNames != null) {
    String msg = getErrorMessage(e, tokenNames);
    reporter.reportMessage(hdr + " - " + msg);
  } else {
    reporter.reportMessage(hdr);
  }
}
    
public Query getQuery(){
  return query;
}  


// private String processEscSymb(String string){
//    int cp = 0;
//    while((cp = string.indexOf('\\', cp))!=-1){
//      switch(string.charAt(cp+1)){
//      case '\\':
//      case '-':
//      case '"':
//        if(cp>0){
//          string = string.substring(0, cp) + string.substring(cp+1);
//        }else{
//          string = string.substring(1);
//        }
//        break;
//      default:
        // do nothing
//        break;
//      }
//      cp++;
//    }
//    }

   /**
   * This method check string fro QueryParser syntax special symbols, and escape it.
   * Esacped symbols arrived from parser (\\ , \- , \") left without changes.
   * Double quotes left without changes (used for quoted term whish is interpreded as phrase).
   */
  private String processEscSymb(String string){
    // process QueryParser escape symbols + - && || ! ( ) { } [ ] ^ " ~ * ? : \
    char[] chars = string.toCharArray();

    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    for (char c : chars) {

      if(!escaped){
        switch (c) {
        case '-': // there must be no minus (there a checked in parser)
        case '+':
        case '&':
        case '|':
        case '!':
        case '(':
        case ')':
        case '{':
        case '}':
        case '[':        
        case ']':        
        case '^':        
        // case '"':        left for QueryParser as special symbol
        case '~':
        case '*':
        case '?':
        case ':':                
          sb.append('\\');
          sb.append(c);
          break;
        case '\\':
          sb.append('\\');
          escaped = true;
          break;
        default:
          sb.append(c);
          break;
        }
      }else{
          // all escaped symbols goes to QueryParser without changes
          sb.append(c);
          escaped=false;
      } 
    }
    string = sb.toString();
    return string;
  }

}

@lexer::members{
private ErrorReporter reporter = null;

public void setErrorReporter(ErrorReporter errorReporter) {
        this.reporter = errorReporter;
}

public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
  String hdr = getErrorHeader(e);
  if (tokenNames != null) {
    String msg = getErrorMessage(e, tokenNames);
    reporter.reportMessage(hdr + " - " + msg);
  } else {
    reporter.reportMessage(hdr);
  }
}
}

fulltext[List<String> fd, Analyzer an]
 	@init{
	fields = fd;
	analyzer = an;
	}
  	:	WS* expr WS* {query=$expr.q;} 
  	|	{BooleanQuery bq = new BooleanQuery();}
		WS* a=expr         {bq.add($a.q, BooleanClause.Occur.SHOULD);}
		(WS+ OR WS+ b=expr {bq.add($b.q, BooleanClause.Occur.SHOULD);})+ WS* 
		{query=bq;}
	;

expr returns[Query q]
	:	a=signedterm { q = $a.q;}
	| 	{BooleanQuery bq = new BooleanQuery();}
        	a=signedterm      {bq.add($a.q, BooleanClause.Occur.MUST);}
	        (WS+ b=signedterm {bq.add($b.q, BooleanClause.Occur.MUST);})+  
		{$q = bq;}
	;
      
      
   
signedterm returns[Query q]
	:	term {$q = $term.q;}
	|	NOT term   
		{
		BooleanQuery bq1 = new BooleanQuery();
	 	for(String field : fields)
	 	{
	 	 BooleanQuery bq = new BooleanQuery();
     bq.add(new WildcardQuery(new Term(field,"*")),BooleanClause.Occur.SHOULD);
		 //bq.add(new MatchAllDocsQuery(),BooleanClause.Occur.SHOULD);
		 bq.add($term.q, BooleanClause.Occur.MUST_NOT);
     bq1.add(bq ,BooleanClause.Occur.SHOULD);
		}
		$q=bq1;  
		}
	;

term returns[Query q]
	: 	word {$q = $word.q;}
	|	qoutedterm {$q = $qoutedterm.q;}
	;

qoutedterm returns[Query q] 
	:	QOUTED_SENTENCE 
		{
		BooleanQuery bq = new BooleanQuery();
	 	for(String field : fields)
	 	{
		 QueryParser parser = new QueryParser(field,analyzer);
		 try
		   {
		   bq.add(parser.parse(processEscSymb($qoutedterm.text)) ,BooleanClause.Occur.SHOULD); 
		   }catch(org.apache.lucene.queryParser.ParseException e){
		     reporter.reportMessage(e.getMessage());
		   } 
		 }
		 $q=bq; 
		}
	;
	
word returns[Query q]
	:	WORD 
		{
	
		BooleanQuery bq = new BooleanQuery();
	        for(String field : fields)	  		   
	 	{
	 	  QueryParser parser = new QueryParser(field,analyzer);
		  try{
//	 	  bq.add( new TermQuery(new Term(field, processEscSymb($word.text))),BooleanClause.Occur.SHOULD);
		    bq.add(parser.parse(processEscSymb($word.text)) ,BooleanClause.Occur.SHOULD); 
	 	  }catch(org.apache.lucene.queryParser.ParseException e){
	 	    reporter.reportMessage(e.getMessage());
		  } 
		}
		$q = bq;		
		}
	;

QUOTES 	:'"';	//higher pcedence

QOUTED_SENTENCE
	:(QUOTES WS* WORD (WS+ WORD)* WS* QUOTES) ;	

WORD 	:(CHAR+);

fragment CHAR	:~(WS|ESCAPE_SYMBOLS)|VALID_FORM_OF_ESCAPE_SYMBOLS;	

fragment ESCAPE_SYMBOLS
	:	'\\' | '"' | NOT;
	
fragment VALID_FORM_OF_ESCAPE_SYMBOLS
	:	'\\'ESCAPE_SYMBOLS;

WS :  ('\t' | ' ' | '\r' | '\n'| '\u000C');


