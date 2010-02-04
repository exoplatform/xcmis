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
package org.xcmis.search.qom;

import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.constraint.AndImpl;
import org.xcmis.search.qom.constraint.ChildNodeImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.ConstraintImpl;
import org.xcmis.search.qom.constraint.DescendantNodeImpl;
import org.xcmis.search.qom.constraint.FullTextSearchImpl;
import org.xcmis.search.qom.constraint.InFolderNode;
import org.xcmis.search.qom.constraint.InTreeNode;
import org.xcmis.search.qom.constraint.NotImpl;
import org.xcmis.search.qom.constraint.OrImpl;
import org.xcmis.search.qom.constraint.PropertyExistenceImpl;
import org.xcmis.search.qom.constraint.SameNodeImpl;
import org.xcmis.search.qom.operand.BindVariableValueImpl;
import org.xcmis.search.qom.operand.DynamicOperandImpl;
import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.LiteralImpl;
import org.xcmis.search.qom.operand.LowerCaseImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.operand.StaticOperandImpl;
import org.xcmis.search.qom.operand.UpperCaseImpl;
import org.xcmis.search.qom.ordering.OrderingImpl;
import org.xcmis.search.qom.source.JoinImpl;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.SourceImpl;
import org.xcmis.search.qom.source.join.ChildNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.DescendantNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.EquiJoinConditionImpl;
import org.xcmis.search.qom.source.join.JoinConditionImpl;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.StaticOperand;

public class StatementVisitor implements QueryObjectModelVisitor
{

   /**
    * Constants.
    */
   public static final String WS = " ";

   public static final String DOT = ".";

   public static final String COMMA = ",";

   public static final String QUOTE = "'";

   public static final String QUOTES = "\"";

   public static final String BIND = "$";

   public static final String STAR = "*";

   public static final String LPAR = "(";

   public static final String RPAR = ")";

   public static final String LBR = "[";

   public static final String RBR = "]";

   public static final String AND = "AND";

   public static final String OR = "OR";

   public static final String NOT = "NOT";

   public static final String IS_NOT_NULL = "IS NOT NULL";

   public static final String AS = "AS";

   public static final String ON = "ON";

   public static final String ISCHILDNODE = "ISCHILDNODE";

   public static final String ISDESCENDANTNODE = "ISDESCENDANTNODE";

   public static final String ISSAMENODE = "ISSAMENODE";

   public static final String CONTAINS = "CONTAINS";

   public static final String SCORE = "SCORE";

   public static final String LENGTH = "LENGTH";

   public static final String LOWER = "LOWER";

   public static final String UPPER = "UPPER";

   public static final String NAME = "NAME";

   public static final String LOCALNAME = "LOCALNAME";

   public static final String JOIN_TYPE_INNER = "INNER JOIN";

   public static final String JOIN_TYPE_LEFT_OUTER = "LEFT OUTER JOIN";

   public static final String JOIN_TYPE_RIGHT_OUTER = "RIGHT OUTER JOIN";

   public static final String EQUAL_TO = "=";

   public static final String NOT_EQUAL_TO = "<>";

   public static final String LESS_THAN = "<";

   public static final String LESS_THAN_OR_EQUAL_TO = "<=";

   public static final String GREATER_THAN = ">";

   public static final String GREATER_THAN_OR_EQUAL_TO = ">=";

   public static final String LIKE = "LIKE";

   public static final String ASC = "ASC";

   public static final String DESC = "DESC";

   public static final String SELECT = "SELECT";

   public static final String FROM = "FROM";

   public static final String WHERE = "WHERE";

   public static final String ORDER_BY = "ORDER BY";

   public static final String CAST = "CAST";

   public static final String STRING = "STRING";

   public static final String BINARY = "BINARY";

   public static final String DATE = "DATE";

   public static final String LONG = "LONG";

   public static final String DOUBLE = "DOUBLE";

   public static final String DECIMAL = "DECIMAL";

   public static final String BOOLEAN = "BOOLEAN";

   public static final String PATH = "PATH";

   public static final String REFERENCE = "REFERENCE";

   public static final String WEAKREFERENCE = "WEAKREFERENCE";

   public static final String URI = "URI";

   public static final String IN_TREE = "IN_TREE";

   public static final String IN_FOLDER = "IN_FOLDER";

   public Object visit(AndImpl node, Object context) throws Exception
   {
      String cons1 = (String)((ConstraintImpl)node.getConstraint1()).accept(this, context);
      String cons2 = (String)((ConstraintImpl)node.getConstraint2()).accept(this, context);
      String res = LPAR + cons1 + WS + AND + WS + cons2 + RPAR;
      return res;
   }

   public Object visit(BindVariableValueImpl node, Object context) throws Exception
   {
      String res = BIND + node.getBindVariableName();
      return res;
   }

   public Object visit(ChildNodeImpl node, Object context) throws Exception
   {
      String res = ISCHILDNODE + LPAR + WS;
      String selname = node.getSelectorName();
      if (selname != null)
      {
         res += validName(selname) + WS + COMMA + WS;
      }
      res += validName(node.getParentPath()) + WS + RPAR;
      return res;
   }

   public Object visit(ChildNodeJoinConditionImpl node, Object context) throws Exception
   {
      String res =
         ISCHILDNODE + LPAR + WS + validName(node.getChildSelectorName()) + " , "
            + validName(node.getParentSelectorName()) + WS + RPAR;
      return res;
   }

   public Object visit(ColumnImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String propname = node.getPropertyName();
      String colname = node.getColumnName();

      String res = "";
      if (selname != null)
      {
         res += validName(selname) + DOT;
      }
      if (propname != null)
      {
         res += validName(propname);
      }
      else
      {
         res += STAR;
      }
      if (colname != null)
      {
         res += WS + AS + WS + validName(colname);
      }
      return res;
   }

   public Object visit(ComparisonImpl node, Object context) throws Exception
   {
      String op1 = (String)((DynamicOperandImpl)node.getOperand1()).accept(this, context);
      String op2 = (String)((StaticOperandImpl)node.getOperand2()).accept(this, context);
      String operator = "";

      if (QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO.equals(node.getOperator()))
      {
         operator = EQUAL_TO;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO.equals(node.getOperator()))
      {
         operator = NOT_EQUAL_TO;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN.equals(node.getOperator()))
      {
         operator = LESS_THAN;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO.equals(node.getOperator()))
      {
         operator = LESS_THAN_OR_EQUAL_TO;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN.equals(node.getOperator()))
      {
         operator = GREATER_THAN;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO.equals(node.getOperator()))
      {
         operator = GREATER_THAN_OR_EQUAL_TO;
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LIKE.equals(node.getOperator()))
      {
         operator = LIKE;
      }
      else
      {
         throw new InvalidQueryException("Invalid operator " + node.getOperator());
      }

      String res = op1 + WS + operator + WS + op2;
      return res;
   }

   public Object visit(DynamicOperandImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Object visit(DescendantNodeImpl node, Object context) throws Exception
   {
      String res = ISDESCENDANTNODE + LPAR + WS;
      String selname = node.getSelectorName();
      if (selname != null)
      {
         res += validName(selname) + WS + COMMA + WS;
      }
      res += validName(node.getAncestorPath()) + WS + RPAR;
      return res;
   }

   public Object visit(DescendantNodeJoinConditionImpl node, Object context) throws Exception
   {
      String res =
         ISDESCENDANTNODE + LPAR + WS + validName(node.getDescendantSelectorName()) + WS + COMMA + WS
            + validName(node.getAncestorSelectorName()) + WS + RPAR;
      return res;
   }

   public Object visit(EquiJoinConditionImpl node, Object context) throws Exception
   {
      String res =
         validName(node.getSelector1Name()) + DOT + validName(node.getProperty1Name()) + WS + EQUAL_TO + WS
            + validName(node.getSelector2Name()) + DOT + validName(node.getProperty2Name());
      return res;
   }

   public Object visit(FullTextSearchImpl node, Object context) throws Exception
   {
      String res = CONTAINS + LPAR + WS;
      String selname = node.getSelectorName();
      String propname = node.getPropertyName();
      if (selname != null)
      {
         res += validName(selname) + DOT;
      }
      if (propname != null)
      {
         res += validName(propname);
      }
      else
      {
         res += STAR;
      }
      res += WS + COMMA + WS;
      StaticOperand so = node.getFullTextSearchExpression();
      if (so instanceof LiteralImpl)
      {
         res += QUOTE + processEscapeSymbols(((LiteralImpl)so).getLiteralValue().getString()) + QUOTE + WS + RPAR;
      }
      else if (so instanceof BindVariableValueImpl)
      {
         res += (String)((BindVariableValueImpl)so).accept(this, context);
      }

      return res;
   }

   public Object visit(FullTextSearchScoreImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String res = (selname != null) ? (SCORE + LPAR + WS + validName(selname) + WS + RPAR) : (SCORE + LPAR + RPAR);
      return res;
   }

   public Object visit(JoinImpl node, Object context) throws Exception
   {
      String left = (String)((SourceImpl)node.getLeft()).accept(this, context);
      String right = (String)((SourceImpl)node.getRight()).accept(this, context);
      String type = "";

      if (QueryObjectModelConstants.JCR_JOIN_TYPE_INNER.equals(node.getJoinType()))
      {
         type = JOIN_TYPE_INNER;
      }
      else if (QueryObjectModelConstants.JCR_JOIN_TYPE_LEFT_OUTER.equals(node.getJoinType()))
      {
         type = JOIN_TYPE_LEFT_OUTER;
      }
      else if (QueryObjectModelConstants.JCR_JOIN_TYPE_RIGHT_OUTER.equals(node.getJoinType()))
      {
         type = JOIN_TYPE_RIGHT_OUTER;
      }

      String cond = (String)((JoinConditionImpl)node.getJoinCondition()).accept(this, context);
      String res = left + WS + type + WS + right + WS + ON + WS + cond;
      return res;
   }

   public Object visit(LengthImpl node, Object context) throws Exception
   {
      String propval = (String)((PropertyValueImpl)node.getPropertyValue()).accept(this, context);
      String res = LENGTH + LPAR + WS + propval + WS + RPAR;
      return res;
   }

   public Object visit(LiteralImpl node, Object context) throws Exception
   {
      Value val = node.getLiteralValue();
      String v = processEscapeSymbols(val.getString());
      String res = CAST + LPAR + WS + QUOTES + v + QUOTES + WS + AS + WS;
      switch (val.getType())
      {
         case PropertyType.BINARY :
            res += BINARY;
            break;
         case PropertyType.BOOLEAN :
            res += BOOLEAN;
            break;
         case PropertyType.DATE :
            res += DATE;
            break;
         //         case PropertyType.DECIMAL :
         //            res += DECIMAL;
         //            break;
         case PropertyType.DOUBLE :
            res += DOUBLE;
            break;
         case PropertyType.LONG :
            res += LONG;
            break;
         case PropertyType.NAME :
            res += NAME;
            break;
         case PropertyType.PATH :
            res += PATH;
            break;
         case PropertyType.REFERENCE :
            res += REFERENCE;
            break;
         case PropertyType.STRING :
            res += STRING;
            break;
         //         case PropertyType.URI :
         //            res += URI;
         //            break;
         //         case PropertyType.WEAKREFERENCE :
         //            res += WEAKREFERENCE;
         //            break;
      }

      res += WS + RPAR;
      return res;
   }

   public Object visit(LowerCaseImpl node, Object context) throws Exception
   {
      String op = (String)((DynamicOperandImpl)node.getOperand()).accept(this, context);
      String res = LOWER + LPAR + WS + op + WS + RPAR;
      return res;
   }

   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String res;
      if (selname != null)
      {
         res = LOCALNAME + LPAR + WS + validName(selname) + WS + RPAR;
      }
      else
      {
         res = LOCALNAME + LPAR + RPAR;
      }
      return res;
   }

   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String res;
      if (selname != null)
      {
         res = NAME + LPAR + WS + validName(selname) + WS + RPAR;
      }
      else
      {
         res = NAME + LPAR + RPAR;
      }
      return res;
   }

   public Object visit(NotImpl node, Object context) throws Exception
   {
      String cons = (String)((ConstraintImpl)node.getConstraint()).accept(this, context);
      String res = NOT + WS + cons;
      return res;
   }

   public Object visit(OrderingImpl node, Object context) throws Exception
   {
      String res = (String)((DynamicOperandImpl)node.getOperand()).accept(this, context);
      if (QueryObjectModelConstants.JCR_ORDER_ASCENDING.equals(node.getOrder()))
      {
         res += WS + ASC;
      }
      else if (QueryObjectModelConstants.JCR_ORDER_DESCENDING.equals(node.getOrder()))
      {
         res += WS + DESC;
      }
      return res;
   }

   public Object visit(OrImpl node, Object context) throws Exception
   {
      String cons1 = (String)((ConstraintImpl)node.getConstraint1()).accept(this, context);
      String cons2 = (String)((ConstraintImpl)node.getConstraint2()).accept(this, context);
      String res = LPAR + cons1 + WS + OR + WS + cons2 + RPAR;
      return res;
   }

   public Object visit(PropertyExistenceImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String res;
      if (selname != null)
      {
         res = validName(selname) + DOT + validName(node.getPropertyName()) + WS + IS_NOT_NULL;
      }
      else
      {
         res = validName(node.getPropertyName()) + WS + IS_NOT_NULL;
      }

      return res;
   }

   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      String selname = node.getSelectorName();
      String propval;
      if (selname != null)
      {
         propval = validName(selname) + DOT + validName(node.getPropertyName());
      }
      else
      {
         propval = validName(node.getPropertyName());
      }
      return propval;
   }

   public Object visit(QueryObjectModel node, Object context) throws Exception
   {
      StringBuilder query = new StringBuilder(SELECT + WS);
      Column[] columns = node.getColumns();
      if (columns == null || columns.length == 0)
      {
         query.append(STAR);
      }
      else
      {
         query.append((String)((ColumnImpl)columns[0]).accept(this, context));
         for (int i = 1; i < columns.length; i++)
         {
            query.append(WS + COMMA + WS + (String)((ColumnImpl)columns[i]).accept(this, context));
         }
      }

      query.append(WS + FROM + WS);
      SourceImpl src = (SourceImpl)node.getSource();
      query.append((String)src.accept(this, context));

      ConstraintImpl cons = (ConstraintImpl)node.getConstraint();
      if (cons != null)
      {
         query.append(WS + WHERE + WS + (String)cons.accept(this, context));
      }

      Ordering[] ords = node.getOrderings();
      if (ords != null && ords.length != 0)
      {
         query.append(WS + ORDER_BY + WS);
         OrderingImpl ordering = (OrderingImpl)ords[0];
         query.append((String)ordering.accept(this, context));
         for (int i = 1; i < ords.length; i++)
         {
            ordering = (OrderingImpl)ords[i];
            query.append(WS + COMMA + WS + (String)ordering.accept(this, context));
         }
      }
      return query.toString();
   }

   public Object visit(SameNodeImpl node, Object context) throws Exception
   {
      String res = ISSAMENODE + LPAR + WS;
      String selname = node.getSelectorName();
      if (selname != null)
      {
         res += validName(selname) + WS + COMMA + WS;
      }
      res += validName(node.getPath()) + WS + RPAR;
      return res;
   }

   public Object visit(SameNodeJoinConditionImpl node, Object context) throws Exception
   {
      String res =
         ISSAMENODE + LPAR + WS + validName(node.getSelector1Name()) + WS + COMMA + WS
            + validName(node.getSelector2Name());
      String selpath = node.getSelector2Path();
      if (selpath != null)
      {
         res += WS + COMMA + WS + validName(selpath) + WS + RPAR;
      }
      else
      {
         res += WS + RPAR;
      }
      return res;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(SelectorImpl node, Object context) throws Exception
   {
      String ntname = node.getNodeTypeName();
      String selname = node.getSelectorName();

      String res = validName(ntname);
      if (selname != null && !selname.equals(ntname))
      {
         res += WS + AS + WS + validName(selname);
      }
      return res;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(SourceImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return context;
   }

   public Object visit(UpperCaseImpl node, Object context) throws Exception
   {
      String op = (String)((DynamicOperandImpl)node.getOperand()).accept(this, context);
      String res = UPPER + LPAR + WS + op + WS + RPAR;
      return res;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(InFolderNode node, Object context) throws Exception
   {
      String res = IN_FOLDER + LPAR + WS + node.getSelectorName() + WS + COMMA + WS + node.getFolderId() + WS + RPAR;
      return res;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(InTreeNode node, Object context) throws Exception
   {
      String res = IN_TREE + LPAR + WS + node.getSelectorName() + WS + COMMA + WS + node.getFolderId() + WS + RPAR;
      return res;
   }

   private boolean isSQLValid(String str)
   {
      if (str.indexOf('.') != -1)
      {
         return false;
      }
      if (str.indexOf(' ') != -1)
      {
         return false;
      }
      return true;
   }

   private String validName(String str)
   {
      // return isSQLValid(str) ? str : (LBR + str + RBR);
      return (LBR + str + RBR);
   }

   private String processEscapeSymbols(String str)
   {
      char[] chars = str.toCharArray();

      StringBuilder sb = new StringBuilder();
      for (char c : chars)
      {
         switch (c)
         {
            case '\b' :
               sb.append("\\b");
               break;
            case '\t' :
               sb.append("\\t");
               break;
            case '\n' :
               sb.append("\\n");
               break;
            case '\f' :
               sb.append("\\f");
               break;
            case '\r' :
               sb.append("\\r");
               break;
            case '"' :
               sb.append("\\\"");
               break;
            case '\'' :
               sb.append("\\'");
               break;
            case '\\' :
               sb.append("\\\\");
               break;
            default :
               sb.append(c);
         }
      }
      return sb.toString();
   }

}
