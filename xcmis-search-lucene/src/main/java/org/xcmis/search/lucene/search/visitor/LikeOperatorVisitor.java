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
package org.xcmis.search.lucene.search.visitor;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.regex.RegexQuery;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.lucene.search.CaseInsensitiveRegexCapImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: LikeOperatorVisitor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class LikeOperatorVisitor extends OperatorVisitor
{
   public static final char ESCAPE_CHAR = '\\';

   // private final static Pattern MATCH_ONE_CHAR = Pattern.compile("^.$");

   public static final char MATCH_ONE_CHAR = '_';

   public static final char MATCH_ZERO_OR_MORE_CHAR = '%';

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * @param staticOperandValue
    */
   public LikeOperatorVisitor(final Value staticOperandValue, final LocationFactory locationFactory)
   {
      super(staticOperandValue, locationFactory);
   }

   @Override
   public Object visit(final LengthImpl node, final Object context) throws Exception
   {
      final int valueType = getStaticOperandType();
      throw new InvalidQueryException("Unsupported operation " + PropertyType.nameFromValue(valueType)
         + " for LessThanOrEqual operator");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final NodeLocalNameImpl node, final Object context) throws Exception
   {
      final int valueType = getStaticOperandType();
      if (valueType != PropertyType.STRING)
      {
         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
            + " for LessThanOrEqualTo operator");
      }
      Query query = null;
      final String likeExpression = getStaticOperandValue();
      if (likeExpression.equals("%"))
      {
         // property exists
         query = new MatchAllDocsQuery();
      }
      else
      {
         final String term = "(.+:)?" + likePatternToRegex(likeExpression);
         query = new RegexQuery(new Term(FieldNames.LABEL, term));
         if (this.caseInsensitiveSearch)
         {
            ((RegexQuery)query).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
         }
      }
      return query;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final NodeNameImpl node, final Object context) throws Exception
   {
      final int valueType = getStaticOperandType();
      if (valueType != PropertyType.STRING && valueType != PropertyType.NAME /*&& valueType != PropertyType.URI*/)
      {
         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
            + " for GreaterThan operator");
      }

      String staticStingValue = getStaticOperandValue();
      //      if (getValue().getType() == PropertyType.URI)
      //      {
      //         if (staticStingValue.startsWith("./"))
      //         {
      //            staticStingValue = staticStingValue.substring(2);
      //         }
      //         // need to decode
      //         try
      //         {
      //            staticStingValue = URLDecoder.decode(staticStingValue, "UTF-8");
      //         }
      //         catch (final UnsupportedEncodingException e)
      //         {
      //            throw new RepositoryException(e);
      //         }
      //      }
      Query query = null;
      final String likeExpression = staticStingValue;
      if (likeExpression.equals("%"))
      {
         // property exists
         query = new MatchAllDocsQuery();
      }
      else
      {
         final String term = likePatternToRegex(likeExpression);
         query = new RegexQuery(new Term(FieldNames.LABEL, term));
         if (this.caseInsensitiveSearch)
         {
            ((RegexQuery)query).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
         }
      }
      return query;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final PropertyValueImpl node, final Object context) throws Exception
   {
      final int valueType = getStaticOperandType();
      if (valueType != PropertyType.STRING)
      {
         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
            + " for LessThanOrEqualTo operator");
      }
      Query query = null;
      final String likeExpression = getStaticOperandValue();

      if (likeExpression.equals("%"))
      {
         // property exists
         query = new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName()));
      }
      else
      {
         final String term = likePatternToRegex(likeExpression);
         query = new RegexQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), term));
         if (this.caseInsensitiveSearch)
         {
            ((RegexQuery)query).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
         }
      }
      return query;
   }

   /**
    * Transform Like pattern to regular expression.
    * 
    * @param String Like pattern
    * @return String regular expression
    */
   private String likePatternToRegex(final String pattern)
   {
      // - escape all non alphabetic characters
      // - escape constructs like \<alphabetic char> into \\<alphabetic char>
      // - replace non escaped _ % into . and .*
      final StringBuffer regexp = new StringBuffer();
      regexp.append("^");
      boolean escaped = false;
      for (int i = 0; i < pattern.length(); i++)
      {
         if (pattern.charAt(i) == ESCAPE_CHAR)
         {
            if (escaped)
            {
               regexp.append("\\\\");
               escaped = false;
            }
            else
            {
               escaped = true;
            }
         }
         else
         {
            if (Character.isLetterOrDigit(pattern.charAt(i)))
            {
               if (escaped)
               {
                  regexp.append(pattern.charAt(i)); // append("\\\\")
                  escaped = false;
               }
               else
               {
                  regexp.append(pattern.charAt(i));
               }
            }
            else
            {
               if (escaped)
               {
                  regexp.append('\\').append(pattern.charAt(i));
                  escaped = false;
               }
               else
               {
                  switch (pattern.charAt(i))
                  {
                     case MATCH_ONE_CHAR :
                        regexp.append('.');
                        break;
                     case MATCH_ZERO_OR_MORE_CHAR :
                        regexp.append(".*");
                        break;
                     default :
                        regexp.append('\\').append(pattern.charAt(i));
                  }
               }
            }
         }
      }
      regexp.append("$");
      return regexp.toString();
   }

}
