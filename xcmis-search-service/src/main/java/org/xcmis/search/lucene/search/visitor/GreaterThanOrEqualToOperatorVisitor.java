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
 * @version $Id: GreaterThanOrEqualToOperatorVisitor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@Deprecated
public class GreaterThanOrEqualToOperatorVisitor extends OperatorVisitor
{
//   /**
//    * Class logger.
//    */
//   private final Log log = ExoLogger.getLogger(getClass().getName());
//
//   /**
//    * @param staticOperandValue
//    */
//   public GreaterThanOrEqualToOperatorVisitor(final Value staticOperandValue, final LocationFactory locationFactory)
//   {
//      super(staticOperandValue, locationFactory);
//   }
//
//   @Override
//   public Object visit(final LengthImpl node, final Object context) throws Exception
//   {
//      String value;
//      try
//      {
//         value = NumberTools.longToString(getValue().getLong());
//      }
//      catch (final ValueFormatException e)
//      {
//         throw new InvalidQueryException("Fail to convert value " + e.getLocalizedMessage(), e);
//      }
//      return new RangeQuery(
//         new Term(FieldNames.createFieldLengthName(node.getPropertyValue().getPropertyName()), value), null, true);
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final NodeLocalNameImpl node, final Object context) throws Exception
//   {
//      final int valueType = getStaticOperandType();
//      throw new InvalidQueryException("Unsupported operation " + PropertyType.nameFromValue(valueType)
//         + " for GreaterThanOrEqual operator");
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
//      Query result = null;
//      if (this.caseInsensitiveSearch)
//      {
//
//         result = new CaseInsensitiveRangeQuery(new Term(FieldNames.LABEL, staticStingValue), null, true);
//
//      }
//      else
//      {
//
//         result = new RangeQuery(new Term(FieldNames.LABEL, staticStingValue), null, true);
//      }
//      return result;
//
//   }
//
//   /**
//    * {@inheritDoc}
//    */
//   @Override
//   public Object visit(final PropertyValueImpl node, final Object context) throws Exception
//   {
//      final int valueType = getStaticOperandType();
//      if (valueType != PropertyType.STRING && valueType != PropertyType.DATE && valueType != PropertyType.LONG
//         && valueType != PropertyType.DOUBLE)
//      {
//         throw new InvalidQueryException("Unsupported value type " + PropertyType.nameFromValue(valueType)
//            + " for GreaterThanOrEqualTo operator");
//      }
//      // String text = FieldNames.createNamedValue(node.getPropertyName(),
//      // getStaticOperandValue());
//
//      Query result = null;
//      if (this.caseInsensitiveSearch)
//      {
//         result =
//            new CaseInsensitiveRangeQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()),
//               getStaticOperandValue()),
//               new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), "\uFFFF"), true);
//
//      }
//      else
//      {
//
//         result =
//            new RangeQuery(
//               new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), getStaticOperandValue()), new Term(
//                  FieldNames.createPropertyFieldName(node.getPropertyName()), "\uFFFF"), true);
//      }
//      return result;
//   }
}
