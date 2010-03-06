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


/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: NotEqualToOperatorVisitor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@Deprecated
public class NotEqualToOperatorVisitor extends OperatorVisitor
{
//   /**
//    * Class logger.
//    */
//   private final Log log = ExoLogger.getLogger(getClass().getName());
//
//   public NotEqualToOperatorVisitor(final Value staticOperandValue, final LocationFactory locationFactory)
//   {
//      super(staticOperandValue, locationFactory);
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final LengthImpl node, final Object context) throws Exception
//   {
//
//      String value;
//      try
//      {
//         value = NumberTools.longToString(getValue().getLong());
//      }
//      catch (final ValueFormatException e)
//      {
//         throw new InvalidQueryException("Fail to convert value " + e.getLocalizedMessage(), e);
//      }
//      final BooleanQuery b = new BooleanQuery();
//      // property exists
//      b.add(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyValue().getPropertyName())),
//         BooleanClause.Occur.SHOULD);
//
//      b.add(
//         new TermQuery(new Term(FieldNames.createFieldLengthName(node.getPropertyValue().getPropertyName()), value)),
//         BooleanClause.Occur.MUST_NOT);
//
//      return b;
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final NodeLocalNameImpl node, final Object context) throws Exception
//   {
//      final int valueType = getStaticOperandType();
//      if (valueType != PropertyType.STRING && valueType != PropertyType.NAME /*&& valueType != PropertyType.URI*/)
//      {
//         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
//            + " for NotEqualToOperatorVisitor operator");
//      }
//
//      String staticStingValue = getStaticOperandValue();
//      //      if (getValue().getType() == PropertyType.URI)
//      //      {
//      //         if (staticStingValue.startsWith("./"))
//      //         {
//      //            staticStingValue = staticStingValue.substring(2);
//      //         }
//      //         // need to decode
//      //         try
//      //         {
//      //            staticStingValue = URLDecoder.decode(staticStingValue, "UTF-8");
//      //         }
//      //         catch (final UnsupportedEncodingException e)
//      //         {
//      //            throw new RepositoryException(e);
//      //         }
//      //      }
//
//      final BooleanQuery b = new BooleanQuery();
//      // property exists
//      b.add(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
//      // property not equal to
//      if (this.caseInsensitiveSearch)
//      {
//         throw new InvalidQueryException("Unsupported operation " + PropertyType.nameFromValue(valueType)
//            + " for NotEqualTo operator");
//      }
//      
//      final BooleanQuery q = new BooleanQuery();
//      q.add(new WildcardQuery(new Term(FieldNames.LABEL, "*?:" + staticStingValue)), BooleanClause.Occur.SHOULD);
//      q.add(new TermQuery(new Term(FieldNames.LABEL, staticStingValue)), BooleanClause.Occur.SHOULD);
//      
//      b.add(q, BooleanClause.Occur.MUST_NOT);
//
//      return b;
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final NodeNameImpl node, final Object context) throws Exception
//   {
//      final int valueType = getStaticOperandType();
//      if (valueType != PropertyType.STRING && valueType != PropertyType.NAME /*&& valueType != PropertyType.URI*/)
//      {
//         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
//            + " for GreaterThan operator");
//      }
//
//      String staticStingValue = getStaticOperandValue();
//      //      if (getValue().getType() == PropertyType.URI)
//      //      {
//      //         if (staticStingValue.startsWith("./"))
//      //         {
//      //            staticStingValue = staticStingValue.substring(2);
//      //         }
//      //         // need to decode
//      //         try
//      //         {
//      //            staticStingValue = URLDecoder.decode(staticStingValue, "UTF-8");
//      //         }
//      //         catch (final UnsupportedEncodingException e)
//      //         {
//      //            throw new RepositoryException(e);
//      //         }
//      //      }
//      final BooleanQuery b = new BooleanQuery();
//      // property exists
//      b.add(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
//      // property not equal to
//      if (this.caseInsensitiveSearch)
//      {
//         b
//            .add(new CaseInsensitiveTermQuery(new Term(FieldNames.LABEL, staticStingValue)),
//               BooleanClause.Occur.MUST_NOT);
//
//      }
//      else
//      {
//         b.add(new TermQuery(new Term(FieldNames.LABEL, staticStingValue)), BooleanClause.Occur.MUST_NOT);
//      }
//      return b;
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final PropertyValueImpl node, final Object context) throws Exception
//   {
//      final BooleanQuery b = new BooleanQuery();
//      // property exists
//      b.add(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName())), BooleanClause.Occur.SHOULD);
//      // property not equal to
//      if (this.caseInsensitiveSearch)
//      {
//         b.add(new CaseInsensitiveTermQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()),
//            getStaticOperandValue())), BooleanClause.Occur.MUST_NOT);
//      }
//      else
//      {
//         b.add(new TermQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()),
//            getStaticOperandValue())), BooleanClause.Occur.MUST_NOT);
//      }
//
//      return b;
//   }

}
