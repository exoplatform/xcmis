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

package org.xcmis.client.gwt.client.marshallers.builder;

import org.xcmis.client.gwt.client.CmisNameSpace;
import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.EnumContentStreamAllowed;
import org.xcmis.client.gwt.client.model.property.CmisPropertyBooleanDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDateTimeDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDecimalDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyHtmlDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyIdDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyIntegerDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyStringDefinitionType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyUriDefinitionType;
import org.xcmis.client.gwt.client.model.type.CmisTypeDefinitionType;

import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeXMLBuilder
{
   
   /**
    * Constructor.
    */
   protected TypeXMLBuilder()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Create request for creating new type.
    * 
    * @param type type
    * @return String
    */
   public static String createType(CmisTypeDefinitionType type)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element id = doc.createElement(CmisNameSpace.ID);
      id.appendChild(doc.createTextNode(type.getId()));
      entry.appendChild(id);

      Element typeElement = doc.createElement(CmisNameSpace.CMISRA_TYPE);
      typeElement.setAttribute("xmlns:cmis", EntryXMLBuilder.XMLNS_CMIS.getNamespaceURI());
      entry.appendChild(typeElement);

      Element typeId = doc.createElement(CmisNameSpace.CMIS_ID);
      typeId.appendChild(doc.createTextNode(type.getId()));
      typeElement.appendChild(typeId);

      Element typeDescription = doc.createElement(CmisNameSpace.CMIS_DESCRIPTION);
      typeDescription.appendChild(doc.createTextNode(type.getDescription()));
      typeElement.appendChild(typeDescription);

      Element typeBaseType = doc.createElement(CmisNameSpace.CMIS_BASE_ID);
      typeBaseType.appendChild(doc.createTextNode(type.getBaseId().value()));
      typeElement.appendChild(typeBaseType);

      Element typeParentId = doc.createElement(CmisNameSpace.CMIS_PARENT_ID);
      typeParentId.appendChild(doc.createTextNode(type.getParentId()));
      typeElement.appendChild(typeParentId);

      if (type.getBaseId().equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT))
      {
         Element contentStreamAllowed = doc.createElement(CmisNameSpace.CMIS_CONTENT_STREAM_ALLOWED);
         contentStreamAllowed.appendChild(doc.createTextNode(EnumContentStreamAllowed.ALLOWED.value()));
         typeElement.appendChild(contentStreamAllowed);
      }

      addPropertyDefinition(doc, typeElement, type.getPropertyDefinition());

      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create xml element for new property definition.
    * 
    * @param doc doc
    * @param typeElement typeElement
    * @param propertyList propertyList
    */
   public static void addPropertyDefinition(Document doc, Element typeElement,
      List<CmisPropertyDefinitionType> propertyList)
   {
      for (CmisPropertyDefinitionType property : propertyList)
      {
         Element propertyElement;
         if (property instanceof CmisPropertyIdDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_ID_DEFINITION);
         }
         else if (property instanceof CmisPropertyStringDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_STRING_DEFINITION);
            Element maxLenght = doc.createElement(CmisNameSpace.CMIS_MAX_LENGHT);
            maxLenght.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyStringDefinitionType)property)
               .getMaxLength())));
            propertyElement.appendChild(maxLenght);
         }
         else if (property instanceof CmisPropertyBooleanDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_BOOLEAN_DEFINITION);
         }
         else if (property instanceof CmisPropertyDateTimeDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_DATETIME_DEFINITION);
         }
         else if (property instanceof CmisPropertyDecimalDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_DECIMAL_DEFINITION);

            Element maxValue = doc.createElement(CmisNameSpace.CMIS_MAX_VALUE);
            maxValue.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyDecimalDefinitionType)property)
               .getMaxValue())));
            propertyElement.appendChild(maxValue);

            Element minValue = doc.createElement(CmisNameSpace.CMIS_MIN_VALUE);
            minValue.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyDecimalDefinitionType)property)
               .getMinValue())));
            propertyElement.appendChild(minValue);

            Element precision = doc.createElement(CmisNameSpace.CMIS_PRECISION);
            precision.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyDecimalDefinitionType)property)
               .getPrecision())));
            propertyElement.appendChild(precision);
         }
         else if (property instanceof CmisPropertyHtmlDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_HTML_DEFINITION);
         }
         else if (property instanceof CmisPropertyUriDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_URI_DEFINITION);
         }
         else if (property instanceof CmisPropertyIntegerDefinitionType)
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_INTEGER_DEFINITION);
            Element maxValue = doc.createElement(CmisNameSpace.CMIS_MAX_VALUE);
            maxValue.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyIntegerDefinitionType)property)
               .getMaxValue())));
            propertyElement.appendChild(maxValue);

            Element minValue = doc.createElement(CmisNameSpace.CMIS_MIN_VALUE);
            minValue.appendChild(doc.createTextNode(String.valueOf(((CmisPropertyIntegerDefinitionType)property)
               .getMinValue())));
            propertyElement.appendChild(minValue);

         }
         else
         {
            propertyElement = doc.createElement(CmisNameSpace.CMIS_PROPERTY_DEFINITION);
         }

         Element id = doc.createElement(CmisNameSpace.CMIS_ID);
         id.appendChild(doc.createTextNode(property.getId()));
         propertyElement.appendChild(id);

         Element description = doc.createElement(CmisNameSpace.CMIS_DESCRIPTION);
         description.appendChild(doc.createTextNode(property.getDescription()));
         propertyElement.appendChild(description);

         Element displayName = doc.createElement(CmisNameSpace.CMIS_DISPLAY_NAME);
         displayName.appendChild(doc.createTextNode(property.getDisplayName()));
         propertyElement.appendChild(displayName);

         Element propertyType = doc.createElement(CmisNameSpace.CMIS_PROPERTY_TYPE);
         propertyType.appendChild(doc.createTextNode(property.getPropertyType().value()));
         propertyElement.appendChild(propertyType);

         Element cardinality = doc.createElement(CmisNameSpace.CMIS_CARDINALITY);
         cardinality.appendChild(doc.createTextNode(property.getCardinality().value()));
         propertyElement.appendChild(cardinality);

         Element updatability = doc.createElement(CmisNameSpace.CMIS_UPDATABILITY);
         updatability.appendChild(doc.createTextNode(property.getUpdatability().value()));
         propertyElement.appendChild(updatability);

         Element inherited = doc.createElement(CmisNameSpace.CMIS_INHERITED);
         inherited.appendChild(doc.createTextNode(String.valueOf(property.isInherited())));
         propertyElement.appendChild(inherited);

         Element queryable = doc.createElement(CmisNameSpace.CMIS_QUERYABLE);
         queryable.appendChild(doc.createTextNode(String.valueOf(property.isQueryable())));
         propertyElement.appendChild(queryable);

         Element orderable = doc.createElement(CmisNameSpace.CMIS_ORDERABLE);
         orderable.appendChild(doc.createTextNode(String.valueOf(property.isOrderable())));
         propertyElement.appendChild(orderable);

         Element openChoice = doc.createElement(CmisNameSpace.CMIS_OPEN_CHOICE);
         openChoice.appendChild(doc.createTextNode(String.valueOf(property.isOpenChoice())));
         propertyElement.appendChild(openChoice);

         Element required = doc.createElement(CmisNameSpace.CMIS_REQUIRED);
         required.appendChild(doc.createTextNode(String.valueOf(property.isRequired())));
         propertyElement.appendChild(required);

         typeElement.appendChild(propertyElement);
      }
   }

}
