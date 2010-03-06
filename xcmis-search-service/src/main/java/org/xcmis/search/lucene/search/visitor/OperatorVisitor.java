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
 * @version $Id: OperatorVisitor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@Deprecated
public abstract class OperatorVisitor 
{
//   protected boolean caseInsensitiveSearch = false;
//
//   private final LocationFactory locationFactory;
//
//   /**
//    * Class logger.
//    */
//   private final Log log = ExoLogger.getLogger(getClass().getName());
//
//   private final Value staticOperandValue;
//
//   /**
//    * @param staticOperandValue
//    * @param locationFactory TODO
//    */
//   public OperatorVisitor(final Value staticOperandValue, final LocationFactory locationFactory)
//   {
//      super();
//      this.staticOperandValue = staticOperandValue;
//      this.locationFactory = locationFactory;
//
//   }
//
//   @Override
//   public Object visit(final LowerCaseImpl node, final Object context) throws Exception
//   {
//      final String value = getStaticOperandValue();
//      if (!caseInsensitiveSearch && !checkCase(value, true))
//      {
//         return new BooleanQuery();// search nothing
//      }
//
//      final DynamicOperandImpl dynamicOperand = (DynamicOperandImpl)node.getOperand();
//      caseInsensitiveSearch = true;
//
//      return dynamicOperand.accept(this, context);
//   }
//
//   @Override
//   public Object visit(final UpperCaseImpl node, final Object context) throws Exception
//   {
//      final String value = getStaticOperandValue();
//      if (!caseInsensitiveSearch && !checkCase(value, false))
//      {
//         return new BooleanQuery();// search nothing
//      }
//
//      final DynamicOperandImpl dynamicOperand = (DynamicOperandImpl)node.getOperand();
//      caseInsensitiveSearch = true;
//      return dynamicOperand.accept(this, context);
//   }
//
//   protected boolean checkCase(final String string, final boolean isLowerCase)
//   {
//
//      for (int i = 0; i < string.length(); i++)
//      {
//         if (isLowerCase)
//         {
//            if (Character.isUpperCase(string.charAt(i)))
//            {
//               return false;
//            }
//         }
//         else
//         {
//            if (Character.isLowerCase(string.charAt(i)))
//            {
//               return false;
//            }
//         }
//      }
//      return true;
//   }
//
//   protected int getStaticOperandType()
//   {
//      return staticOperandValue.getType();
//   }
//
//   /**
//    * @return
//    * @throws RepositoryException
//    */
//   protected String getStaticOperandValue() throws RepositoryException
//   {
//      switch (staticOperandValue.getType())
//      {
//         case PropertyType.BINARY :
//            return staticOperandValue.getString();
//         case PropertyType.BOOLEAN :
//            return staticOperandValue.getString();
//         case PropertyType.DATE :
//            return DateTools.dateToString(staticOperandValue.getDate().getTime(), DateTools.Resolution.MILLISECOND);
//         case PropertyType.DOUBLE :
//            return JCRNumberTools.doubleToString(staticOperandValue.getDouble());
//         case PropertyType.LONG :
//            return NumberTools.longToString(staticOperandValue.getLong());
//         case PropertyType.NAME :
//            // return this.locationFactory.createJCRName(((NameValue)
//            // this.staticOperandValue).getQName())
//            // .getAsString();
//            // check
//            // remaping
//         case PropertyType.PATH :
//            // return this.locationFactory.createJCRPath(((PathValue)
//            // this.staticOperandValue).getQPath())
//            // .getAsString(false);
//         case PropertyType.REFERENCE :
//            return staticOperandValue.getString();
//            //          Not supported in JCR 1.X
//            //         case PropertyType.WEAKREFERENCE :
//            //            return staticOperandValue.getString();
//         case PropertyType.STRING :
//            return staticOperandValue.getString();
//            // Not supported in JCR 1.X
//            //         case PropertyType.URI :
//            //            return staticOperandValue.getString();
//            //
//            //         case PropertyType.DECIMAL :
//            //            return JCRNumberTools.bigDecimalToString(staticOperandValue.getDecimal());
//         default :
//            // TODO: support for new types defined in JSR 283
//            throw new InvalidQueryException("Unsupported property type "
//               + PropertyType.nameFromValue(staticOperandValue.getType()));
//      }
//   }
//
//   protected Value getValue()
//   {
//      return staticOperandValue;
//   }
//
//   protected Object modifyCase(final Object visit)
//   {
//      return null;
//   }

}
