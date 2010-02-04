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
package org.xcmis.search.qom.constraint;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.QueryObjectModelConstants;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public enum Operator {
   /**
    * Equal operator.
    */
   EQ(QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO, "=", "="),
   /**
    * Not equal operator.
    */
   NE(QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO, "<>", "&lt;&gt;"),
   /**
    * Greater than operator.
    */
   GT(QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN, ">", "&gt;"),
   /**
    * Greater than or equal to operator.
    */
   GE(QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO, ">=", "&gt;="),
   /**
    * Less than operator.
    */
   LT(QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN, "<", "&lt;"),
   /**
    * Less than or equal to operator.
    */
   LE(QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO, "<=", "&lt;="),
   /**
    * Like operator.
    */
   LIKE(QueryObjectModelConstants.JCR_OPERATOR_LIKE, "LIKE", "LIKE");

   /**
    * JCR name of this operator.
    */
   private final String name;

   /**
    * This operator in SQL syntax.
    */
   private final String sql;

   /**
    * This operator in html syntax.
    */
   private final String xml;

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the sql
    */
   public String getSql()
   {
      return sql;
   }

   /**
    * @return the xml
    */
   public String getXml()
   {
      return xml;
   }

   private Operator(String name, String op, String xml)
   {
      this.name = name;
      this.sql = op;
      this.xml = xml;
   }

   /**
    * Returns the JCR 2.0 name of this query operator.
    * 
    * @see QueryObjectModelConstants
    * @return JCR name of this operator
    */
   public String toString()
   {
      return sql;
   }

   /**
    * Returns an array of the names of all the JCR 2.0 query operators.
    * 
    * @return names of all query operators
    */
   public static String[] getAllQueryOperators()
   {
      return new String[]{EQ.toString(), NE.toString(), GT.toString(), GE.toString(), LT.toString(), LE.toString(),
         LIKE.toString()};
   }

   /**
    * Returns the operator with the given JCR name.
    * 
    * @param name JCR name of an operator
    * @return operator with the given name
    * @throws RepositoryException if the given name is unknown
    */
   public static Operator getOperatorByName(String name) throws RepositoryException
   {
      for (Operator operator : Operator.values())
      {
         if (operator.name.equals(name))
         {
            return operator;
         }
      }
      throw new RepositoryException("Unknown operator name: " + name);
   }

   /**
    * Returns the operator with the given JCR name.
    * 
    * @param name JCR name of an operator
    * @return operator with the given name
    * @throws RepositoryException if the given name is unknown
    */
   public static Operator getOperatorBySql(String sql) throws RepositoryException
   {
      for (Operator operator : Operator.values())
      {
         if (operator.sql.equals(sql))
         {
            return operator;
         }
      }
      throw new RepositoryException("Unknown operator sql: " + sql);
   }

}
