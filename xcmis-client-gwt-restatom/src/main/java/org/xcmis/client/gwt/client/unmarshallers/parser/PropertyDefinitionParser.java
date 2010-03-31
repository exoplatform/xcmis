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

package org.xcmis.client.gwt.client.unmarshallers.parser;

import org.xcmis.client.gwt.client.CmisNameSpace;
import org.xcmis.client.gwt.client.model.EnumCardinality;
import org.xcmis.client.gwt.client.model.EnumPropertyType;
import org.xcmis.client.gwt.client.model.EnumUpdatability;
import org.xcmis.client.gwt.client.model.property.CmisPropertyBooleanDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDateTimeDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDecimalDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyIdDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyIntegerDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyStringDefinitionType;

import com.google.gwt.xml.client.Node;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class PropertyDefinitionParser
{

   /**
    * Constructor.
    */
   protected PropertyDefinitionParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse xml element to get {@link CmisPropertyBooleanDefinitionType}.
    * 
    * @param node node
    * @return {@link CmisPropertyBooleanDefinitionType}
    */

   public static CmisPropertyDefinitionType parse(Node node)
   {

      CmisPropertyDefinitionType propertyDefinitionType = new CmisPropertyDefinitionType();
      String nodeName = node.getNodeName();

      String precision = "";
      String maxValue = "1024";
      String minValue = "0";
      String maxLength = "1024";

      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node property = node.getChildNodes().item(i);
         String value = "";
         if (property.getFirstChild() != null)
         {
            value = property.getFirstChild().getNodeValue();
         }
         if (property.getNodeName().equals(CmisNameSpace.CMIS_ID))
         {
            propertyDefinitionType.setId(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_DESCRIPTION))
         {
            propertyDefinitionType.setDescription(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_LOCAL_NAME))
         {
            propertyDefinitionType.setLocalName(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_LOCAL_NAMESPACE))
         {
            propertyDefinitionType.setLocalNamespace(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_DISPLAY_NAME))
         {
            propertyDefinitionType.setDisplayName(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_QUERY_NAME))
         {
            propertyDefinitionType.setQueryName(value);
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_TYPE))
         {
            propertyDefinitionType.setPropertyType(EnumPropertyType.fromValue(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_CARDINALITY))
         {
            propertyDefinitionType.setCardinality(EnumCardinality.fromValue(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_UPDATABILITY))
         {
            propertyDefinitionType.setUpdatability(EnumUpdatability.fromValue(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_INHERITED))
         {
            propertyDefinitionType.setInherited(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_REQUIRED))
         {
            propertyDefinitionType.setRequired(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_QUERYABLE))
         {
            propertyDefinitionType.setQueryable(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_ORDERABLE))
         {
            propertyDefinitionType.setOrderable(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_OPEN_CHOICE))
         {
            propertyDefinitionType.setOpenChoice(Boolean.parseBoolean(value));
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_MIN_VALUE))
         {
            minValue = value;
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_MAX_VALUE))
         {
            maxValue = value;
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_MAX_LENGHT))
         {
            maxLength = value;
         }
         else if (property.getNodeName().equals(CmisNameSpace.CMIS_PRECISION))
         {
            precision = value;
         }
      }

      if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_ID_DEFINITION))
      {
         CmisPropertyIdDefinitionType cmisPropertyIdDefinitionType =
            (CmisPropertyIdDefinitionType)setPropertyDefinitionTypeValues(new CmisPropertyIdDefinitionType(),
               propertyDefinitionType);

         return cmisPropertyIdDefinitionType;

      }
      else if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_BOOLEAN_DEFINITION))
      {
         CmisPropertyBooleanDefinitionType cmisBooleanDefinitionType =
            (CmisPropertyBooleanDefinitionType)setPropertyDefinitionTypeValues(new CmisPropertyBooleanDefinitionType(),
               propertyDefinitionType);

         return cmisBooleanDefinitionType;

      }
      else if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_STRING_DEFINITION))
      {
         CmisPropertyStringDefinitionType cmisStringDefinitionType =
            (CmisPropertyStringDefinitionType)setPropertyDefinitionTypeValues(new CmisPropertyStringDefinitionType(),
               propertyDefinitionType);
         cmisStringDefinitionType.setMaxLength(Integer.parseInt(maxLength));

         return cmisStringDefinitionType;

      }
      else if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_INTEGER_DEFINITION))
      {
         CmisPropertyIntegerDefinitionType cmisIntegerDefinitionType =
            (CmisPropertyIntegerDefinitionType)setPropertyDefinitionTypeValues(new CmisPropertyIntegerDefinitionType(),
               propertyDefinitionType);
         cmisIntegerDefinitionType.setMaxValue(Long.parseLong(maxValue));
         cmisIntegerDefinitionType.setMinValue(Long.parseLong(minValue));

         return cmisIntegerDefinitionType;

      }
      else if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_DECIMAL_DEFINITION))
      {
         CmisPropertyDecimalDefinitionType cmisDecimalDefinitionType =
            (CmisPropertyDecimalDefinitionType)setPropertyDefinitionTypeValues(new CmisPropertyDecimalDefinitionType(),
               propertyDefinitionType);
         cmisDecimalDefinitionType.setMaxValue(Double.parseDouble(maxValue));
         cmisDecimalDefinitionType.setMinValue(Double.parseDouble(minValue));
         cmisDecimalDefinitionType.setPrecision(Integer.parseInt(precision));

         return cmisDecimalDefinitionType;

      }
      else if (nodeName.equals(CmisNameSpace.CMIS_PROPERTY_DATETIME_DEFINITION))
      {
         return (CmisPropertyDateTimeDefinitionType)setPropertyDefinitionTypeValues(
            new CmisPropertyDateTimeDefinitionType(), propertyDefinitionType);
      }
      else
      {
         return propertyDefinitionType;
      }
   }

   /**
    * @param updateableDefinitionType definitionType to update
    * @param propertyDefinitionType propertyDefinitionType
    * 
    * @return {@link CmisPropertyDefinitionType}
    */
   private static CmisPropertyDefinitionType setPropertyDefinitionTypeValues(
      CmisPropertyDefinitionType updateableDefinitionType, CmisPropertyDefinitionType propertyDefinitionType)
   {
      updateableDefinitionType.setId(propertyDefinitionType.getId());
      updateableDefinitionType.setDescription(propertyDefinitionType.getDescription());
      updateableDefinitionType.setLocalName(propertyDefinitionType.getLocalName());
      updateableDefinitionType.setLocalNamespace(propertyDefinitionType.getLocalNamespace());
      updateableDefinitionType.setDisplayName(propertyDefinitionType.getDisplayName());
      updateableDefinitionType.setQueryName(propertyDefinitionType.getQueryName());
      updateableDefinitionType.setPropertyType(propertyDefinitionType.getPropertyType());
      updateableDefinitionType.setCardinality(propertyDefinitionType.getCardinality());
      updateableDefinitionType.setUpdatability(propertyDefinitionType.getUpdatability());
      updateableDefinitionType.setInherited(propertyDefinitionType.isInherited());
      updateableDefinitionType.setOpenChoice(propertyDefinitionType.isOpenChoice());
      updateableDefinitionType.setRequired(propertyDefinitionType.isRequired());
      updateableDefinitionType.setOrderable(propertyDefinitionType.isOrderable());
      updateableDefinitionType.setQueryable(propertyDefinitionType.isQueryable());

      return updateableDefinitionType;
   }
}
