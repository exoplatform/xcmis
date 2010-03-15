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
package org.xcmis.search.result;

import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.LowerCaseImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.operand.UpperCaseImpl;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class DummyResultSorterFactory extends AbstractResultSorterFactory
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(FullTextSearchScoreImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(LengthImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(LowerCaseImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(UpperCaseImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ResultSorter getDefaultResultSorter(String[] selectorNames)
   {
      return null;

   }

}
