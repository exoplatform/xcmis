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

import org.apache.lucene.document.NumberTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.lucene.search.CaseInsensitiveTermQuery;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.InvalidQueryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: EqualToOperatorVisitor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class EqualToOperatorVisitor extends OperatorVisitor
{

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   public EqualToOperatorVisitor(final Value staticOperandValue, final LocationFactory locationFactory)
   {
      super(staticOperandValue, locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final LengthImpl node, final Object context) throws Exception
   {

      String value;
      try
      {
         value = NumberTools.longToString(getValue().getLong());
      }
      catch (final ValueFormatException e)
      {
         throw new InvalidQueryException("Fail to convert value " + e.getLocalizedMessage(), e);
      }

      return new TermQuery(new Term(FieldNames.createFieldLengthName(node.getPropertyValue().getPropertyName()), value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final NodeLocalNameImpl node, final Object context) throws Exception
   {
      final int valueType = getStaticOperandType();

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
      if (this.caseInsensitiveSearch)
      {
         throw new InvalidQueryException("Unsupported operation " + PropertyType.nameFromValue(valueType)
            + " for EqualTo operator");
      }
      final BooleanQuery q = new BooleanQuery();
      q.add(new WildcardQuery(new Term(FieldNames.LABEL, "*?:" + staticStingValue)), BooleanClause.Occur.SHOULD);
      q.add(new TermQuery(new Term(FieldNames.LABEL, staticStingValue)), BooleanClause.Occur.SHOULD);
      query = q;

      return query;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final NodeNameImpl node, final Object context) throws Exception
   {

      Query query = null;
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

      if (this.caseInsensitiveSearch)
      {
         query = new CaseInsensitiveTermQuery(new Term(FieldNames.LABEL, staticStingValue.toLowerCase()));
      }
      else
      {
         query = new TermQuery(new Term(FieldNames.LABEL, staticStingValue));
      }
      return query;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final PropertyValueImpl node, final Object context) throws Exception
   {
      Query query = null;
      if (this.caseInsensitiveSearch)
      {
         query =
            new CaseInsensitiveTermQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()),
               getStaticOperandValue()));
      }
      else
      {
         query =
            new TermQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), getStaticOperandValue()));
      }
      return query;
   }

}
