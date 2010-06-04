/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.unmarshallers.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.Choice;
import org.xcmis.client.gwt.model.EnumCardinality;
import org.xcmis.client.gwt.model.EnumPropertyType;
import org.xcmis.client.gwt.model.EnumUpdatability;
import org.xcmis.client.gwt.model.Precision;
import org.xcmis.client.gwt.model.property.BasePropertyDefinition;
import org.xcmis.client.gwt.model.property.BooleanPropertyDefinition;
import org.xcmis.client.gwt.model.property.DateTimePropertyDefinition;
import org.xcmis.client.gwt.model.property.DecimalPropertyDefinition;
import org.xcmis.client.gwt.model.property.HtmlPropertyDefinition;
import org.xcmis.client.gwt.model.property.IdPropertyDefinition;
import org.xcmis.client.gwt.model.property.IntegerPropertyDefinition;
import org.xcmis.client.gwt.model.property.PropertyDefinition;
import org.xcmis.client.gwt.model.property.StringPropertyDefinition;
import org.xcmis.client.gwt.model.property.UriPropertyDefinition;
import org.xcmis.client.gwt.model.util.DateUtil;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class PropertyDefinitionParser
{
   /**
    * Property Definition
    */
   private static BasePropertyDefinition<?> propertyDefinition;

   /**
    * Constructor.
    */
   protected PropertyDefinitionParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from
      // subclass
   }

   /**
    * Parse xml element to get {@link CmisPropertyBooleanDefinitionType}.
    * 
    * @param node
    *            node
    * @return {@link CmisPropertyBooleanDefinitionType}
    */

   public static PropertyDefinition<?> parse(Node node)
   {
      String nodeName = node.getNodeName();
      if (nodeName.equals(CMIS.CMIS_PROPERTY_BOOLEAN_DEFINITION))
      {
         propertyDefinition = new BooleanPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_DATETIME_DEFINITION))
      {
         propertyDefinition = new DateTimePropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_DECIMAL_DEFINITION))
      {
         propertyDefinition = new DecimalPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_HTML_DEFINITION))
      {
         propertyDefinition = new HtmlPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_ID_DEFINITION))
      {
         propertyDefinition = new IdPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_INTEGER_DEFINITION))
      {
         propertyDefinition = new IntegerPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_STRING_DEFINITION))
      {
         propertyDefinition = new StringPropertyDefinition();
      }
      else if (nodeName.equals(CMIS.CMIS_PROPERTY_URI_DEFINITION))
      {
         propertyDefinition = new UriPropertyDefinition();
      }

      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node property = node.getChildNodes().item(i);
         String value = "";
         if (property.getFirstChild() != null)
         {
            value = property.getFirstChild().getNodeValue();
         }
         if (property.getNodeName().equals(CMIS.CMIS_ID))
         {
            propertyDefinition.setId(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_LOCAL_NAME))
         {
            propertyDefinition.setLocalName(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_LOCAL_NAMESPACE))
         {
            propertyDefinition.setLocalNamespace(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_QUERY_NAME))
         {
            propertyDefinition.setQueryName(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_DISPLAY_NAME))
         {
            propertyDefinition.setDisplayName(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_DESCRIPTION))
         {
            propertyDefinition.setDescription(value);
         }
         else if (property.getNodeName().equals(CMIS.CMIS_CARDINALITY))
         {
            propertyDefinition.setCardinality(EnumCardinality.fromValue(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_UPDATABILITY))
         {
            propertyDefinition.setUpdatability(EnumUpdatability.fromValue(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_INHERITED))
         {
            propertyDefinition.setInherited(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_REQUIRED))
         {
            propertyDefinition.setRequired(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_QUERYABLE))
         {
            propertyDefinition.setQueryable(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_ORDERABLE))
         {
            propertyDefinition.setOrderable(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_OPEN_CHOICE))
         {
            propertyDefinition.setOpenChoice(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CMIS.CMIS_MIN_VALUE))
         {
            if (propertyDefinition.getPropertyType().equals(EnumPropertyType.INTEGER))
            {
               ((IntegerPropertyDefinition)propertyDefinition).setMinInteger(Long.valueOf(value));
            }
            else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.DECIMAL))
            {
               ((DecimalPropertyDefinition)propertyDefinition).setMinDecimal(Double.valueOf(value));
            }
         }
         else if (property.getNodeName().equals(CMIS.CMIS_MAX_VALUE))
         {
            if (propertyDefinition.getPropertyType().equals(EnumPropertyType.INTEGER))
            {
               ((IntegerPropertyDefinition)propertyDefinition).setMaxInteger(Long.valueOf(value));
            }
            else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.DECIMAL))
            {
               ((DecimalPropertyDefinition)propertyDefinition).setMaxDecimal(Double.valueOf(value));
            }
         }
         else if (property.getNodeName().equals(CMIS.CMIS_MAX_LENGHT))
         {
            if (propertyDefinition.getPropertyType().equals(EnumPropertyType.STRING))
            {
               ((StringPropertyDefinition)propertyDefinition).setMaxLength(Long.parseLong(value));
            }
         }
         else if (property.getNodeName().equals(CMIS.CMIS_PRECISION))
         {
            Precision pr = Precision.fromValue(Integer.valueOf(value).intValue());
            if (propertyDefinition.getPropertyType().equals(EnumPropertyType.DECIMAL))
            {
               ((DecimalPropertyDefinition)propertyDefinition).setPrecision(pr);
            }
         }
         else if (property.getNodeName().equals(CMIS.CMIS_CHOICE))
         {
            Node displayName = property.getAttributes().getNamedItem(CMIS.DISPLAY_NAME);
            String choiceDisplayName = (displayName == null) ? "" : displayName.getNodeValue();
            parseChoice(choiceDisplayName, property.getChildNodes());
         }
         else if (property.getNodeName().equals(CMIS.CMIS_DEFAULT_VALUE))
         {
            setDefaultValue(propertyDefinition, property.getChildNodes());
         }
      }
      return propertyDefinition;
   }

   /**
    * Parses default value node list and sets default value to propDefinition
    * 
    * @param propertyDefinition
    *            property definition
    * @param children
    *            node list, which contains default values
    */
   private static void setDefaultValue(BasePropertyDefinition<?> propertyDefinition, NodeList children)
   {
      List<String> stringValues = new ArrayList<String>();
      for (int i = 0; i < children.getLength(); i++)
      {
         Node node = children.item(i);
         if (node.getNodeName().equals(CMIS.CMIS_VALUE))
            stringValues.add((node.getFirstChild() == null) ? null : node.getFirstChild().getNodeValue());
      }
      if (propertyDefinition.getPropertyType().equals(EnumPropertyType.BOOLEAN))
      {
         Boolean[] arrayValues = new Boolean[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Boolean.parseBoolean(stringValues.get(i));
         }
         ((BooleanPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.DATETIME))
      {
         Date[] arrayValues = new Date[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = DateUtil.parseDate(stringValues.get(i));
         }
         ((DateTimePropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.DECIMAL))
      {
         Double[] arrayValues = new Double[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Double.valueOf(stringValues.get(i));
         }
         ((DecimalPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.HTML))
      {
         String[] arrayValues = new String[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = stringValues.get(i);
         }
         ((HtmlPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.ID))
      {
         String[] arrayValues = new String[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = stringValues.get(i);
         }
         ((IdPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.STRING))
      {
         String[] arrayValues = new String[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = stringValues.get(i);
         }
         ((StringPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.URI))
      {
         String[] arrayValues = new String[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = stringValues.get(i);
         }
         ((UriPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
      else if (propertyDefinition.getPropertyType().equals(EnumPropertyType.INTEGER))
      {
         Long[] arrayValues = new Long[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Long.valueOf(stringValues.get(i));
         }
         ((IntegerPropertyDefinition)propertyDefinition).setDefaultValue(arrayValues);
      }
   }

   /**
    * Parses choice node list and returns choice of required type.
    * 
    * @param displayName
    *            display name for choice
    * @param children
    *            node list of children
    * @param propertyType
    *            required property type
    */
   private static void parseChoice(String displayName, NodeList children)
   {
      List<String> stringValues = new ArrayList<String>();
      for (int i = 0; i < children.getLength(); i++)
      {
         Node node = children.item(i);
         if (node.getNodeName().equals(CMIS.CMIS_VALUE))
            stringValues.add((node.getFirstChild() == null) ? null : node.getFirstChild().getNodeValue());
      }

      if (propertyDefinition instanceof BooleanPropertyDefinition)
      {
         Boolean[] arrayValues = new Boolean[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Boolean.parseBoolean(stringValues.get(i));
         }
         ((BooleanPropertyDefinition)propertyDefinition).getChoices()
            .add(new Choice<Boolean>(arrayValues, displayName));
      }
      else if (propertyDefinition instanceof DateTimePropertyDefinition)
      {
         Date[] arrayValues = new Date[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = DateUtil.parseDate(stringValues.get(i));
         }
         ((DateTimePropertyDefinition)propertyDefinition).getChoices().add(new Choice<Date>(arrayValues, displayName));
      }
      else if (propertyDefinition instanceof DecimalPropertyDefinition)
      {
         Double[] arrayValues = new Double[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Double.valueOf(stringValues.get(i));
         }
         ((DecimalPropertyDefinition)propertyDefinition).getChoices().add(new Choice<Double>(arrayValues, displayName));
      }
      else if (propertyDefinition instanceof HtmlPropertyDefinition)
      {
         ((HtmlPropertyDefinition)propertyDefinition).getChoices().add(
            new Choice<String>(stringValues.toArray(new String[stringValues.size()]), displayName));
      }
      else if (propertyDefinition instanceof IdPropertyDefinition)
      {
         ((IdPropertyDefinition)propertyDefinition).getChoices().add(
            new Choice<String>(stringValues.toArray(new String[stringValues.size()]), displayName));
      }
      else if (propertyDefinition instanceof StringPropertyDefinition)
      {
         ((StringPropertyDefinition)propertyDefinition).getChoices().add(
            new Choice<String>(stringValues.toArray(new String[stringValues.size()]), displayName));
      }
      else if (propertyDefinition instanceof UriPropertyDefinition)
      {
         ((UriPropertyDefinition)propertyDefinition).getChoices().add(
            new Choice<String>(stringValues.toArray(new String[stringValues.size()]), displayName));
      }
      else if (propertyDefinition instanceof IntegerPropertyDefinition)
      {
         Long[] arrayValues = new Long[stringValues.size()];
         for (int i = 0; i < stringValues.size(); i++)
         {
            arrayValues[i] = Long.valueOf(stringValues.get(i));
         }
         ((IntegerPropertyDefinition)propertyDefinition).getChoices().add(new Choice<Long>(arrayValues, displayName));
      }
   }
}
