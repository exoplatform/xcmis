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

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.Choice;
import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.EnumContentStreamAllowed;
import org.xcmis.client.gwt.client.model.property.BooleanPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.DateTimePropertyDefinition;
import org.xcmis.client.gwt.client.model.property.DecimalPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.HtmlPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.IdPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.IntegerPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.PropertyDefinition;
import org.xcmis.client.gwt.client.model.property.StringPropertyDefinition;
import org.xcmis.client.gwt.client.model.property.UriPropertyDefinition;
import org.xcmis.client.gwt.client.model.type.TypeDefinition;
import org.xcmis.client.gwt.client.model.util.DateUtil;

import java.util.Date;
import java.util.Map;

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
   public static String createType(TypeDefinition type)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element id = doc.createElement(CMIS.ID);
      id.appendChild(doc.createTextNode(type.getId()));
      entry.appendChild(id);

      Element typeElement = doc.createElement(CMIS.CMISRA_TYPE);
      typeElement.setAttribute("xmlns:cmis", EntryXMLBuilder.XMLNS_CMIS.getNamespaceURI());
      entry.appendChild(typeElement);

      Element typeId = doc.createElement(CMIS.CMIS_ID);
      typeId.appendChild(doc.createTextNode(type.getId()));
      typeElement.appendChild(typeId);

      Element typeLocalName = doc.createElement(CMIS.CMIS_LOCAL_NAME);
      typeLocalName.appendChild(doc.createTextNode(type.getLocalName()));
      typeElement.appendChild(typeLocalName);

      Element typeDisplayName = doc.createElement(CMIS.CMIS_DISPLAY_NAME);
      typeDisplayName.appendChild(doc.createTextNode(type.getDisplayName()));
      typeElement.appendChild(typeDisplayName);

      Element typeQueryName = doc.createElement(CMIS.CMIS_QUERY_NAME);
      typeQueryName.appendChild(doc.createTextNode(type.getQueryName()));
      typeElement.appendChild(typeQueryName);

      Element typeDescription = doc.createElement(CMIS.CMIS_DESCRIPTION);
      typeDescription.appendChild(doc.createTextNode(type.getDescription()));
      typeElement.appendChild(typeDescription);

      Element typeBaseType = doc.createElement(CMIS.CMIS_BASE_ID);
      typeBaseType.appendChild(doc.createTextNode(type.getBaseId().value()));
      typeElement.appendChild(typeBaseType);

      Element typeParentId = doc.createElement(CMIS.CMIS_PARENT_ID);
      typeParentId.appendChild(doc.createTextNode(type.getParentId()));
      typeElement.appendChild(typeParentId);

      Element typeCreatable = doc.createElement(CMIS.CMIS_CREATABLE);
      typeCreatable.appendChild(doc.createTextNode(String.valueOf(type.isCreatable())));
      typeElement.appendChild(typeCreatable);

      Element typeFileable = doc.createElement(CMIS.CMIS_FILEABLE);
      typeFileable.appendChild(doc.createTextNode(String.valueOf(type.isFileable())));
      typeElement.appendChild(typeFileable);

      Element typeQueryable = doc.createElement(CMIS.CMIS_QUERYABLE);
      typeQueryable.appendChild(doc.createTextNode(String.valueOf(type.isQueryable())));
      typeElement.appendChild(typeQueryable);

      Element typeFulltextIndexed = doc.createElement(CMIS.CMIS_FULL_TEXT_INDEXED);
      typeFulltextIndexed.appendChild(doc.createTextNode(String.valueOf(type.isFulltextIndexed())));
      typeElement.appendChild(typeFulltextIndexed);

      Element typeIncludedInSupertypeQuery = doc.createElement(CMIS.CMIS_INCLUDED_IN_SUPERTYPE_QUERY);
      typeIncludedInSupertypeQuery.appendChild(doc.createTextNode(String.valueOf(type.isIncludedInSupertypeQuery())));
      typeElement.appendChild(typeIncludedInSupertypeQuery);

      Element typeControllablePolicy = doc.createElement(CMIS.CMIS_CONTROLLABLE_POLICY);
      typeControllablePolicy.appendChild(doc.createTextNode(String.valueOf(type.isControllablePolicy())));
      typeElement.appendChild(typeControllablePolicy);

      Element typeControllableACL = doc.createElement(CMIS.CMIS_CONTROLLABLE_ACL);
      typeControllableACL.appendChild(doc.createTextNode(String.valueOf(type.isControllableACL())));
      typeElement.appendChild(typeControllableACL);

      if (type.getBaseId().equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT))
      {
         Element contentStreamAllowed = doc.createElement(CMIS.CMIS_CONTENT_STREAM_ALLOWED);
         contentStreamAllowed.appendChild(doc.createTextNode(EnumContentStreamAllowed.ALLOWED.value()));
         typeElement.appendChild(contentStreamAllowed);
      }

      addPropertyDefinition(doc, typeElement, type.getPropertyDefinitions());

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
      Map<String, PropertyDefinition<?>> propertyList)
   {
      for (PropertyDefinition<?> propertyDefinition : propertyList.values())
      {
         Element propertyElement;
         if (propertyDefinition instanceof IdPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_ID_DEFINITION);
         }
         else if (propertyDefinition instanceof StringPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_STRING_DEFINITION);
            Element maxLenght = doc.createElement(CMIS.CMIS_MAX_LENGHT);
            maxLenght.appendChild(doc.createTextNode(String.valueOf(((StringPropertyDefinition)propertyDefinition)
               .getMaxLength())));
            propertyElement.appendChild(maxLenght);
         }
         else if (propertyDefinition instanceof BooleanPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_BOOLEAN_DEFINITION);
         }
         else if (propertyDefinition instanceof DateTimePropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_DATETIME_DEFINITION);
            Element resolution = doc.createElement(CMIS.CMIS_RESOLUTION);
            resolution.appendChild(doc.createTextNode(((DateTimePropertyDefinition)propertyDefinition)
               .getDateTimeResolution().value()));
            propertyElement.appendChild(resolution);
         }
         else if (propertyDefinition instanceof DecimalPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_DECIMAL_DEFINITION);

            Element maxValue = doc.createElement(CMIS.CMIS_MAX_VALUE);
            maxValue.appendChild(doc.createTextNode(String.valueOf(((DecimalPropertyDefinition)propertyDefinition)
               .getMaxDecimal())));
            propertyElement.appendChild(maxValue);

            Element minValue = doc.createElement(CMIS.CMIS_MIN_VALUE);
            minValue.appendChild(doc.createTextNode(String.valueOf(((DecimalPropertyDefinition)propertyDefinition)
               .getMinDecimal())));
            propertyElement.appendChild(minValue);

            Element precision = doc.createElement(CMIS.CMIS_PRECISION);
            precision.appendChild(doc.createTextNode(String.valueOf(((DecimalPropertyDefinition)propertyDefinition)
               .getPrecision())));
            propertyElement.appendChild(precision);
         }
         else if (propertyDefinition instanceof HtmlPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_HTML_DEFINITION);
         }
         else if (propertyDefinition instanceof UriPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_URI_DEFINITION);
         }
         else if (propertyDefinition instanceof IntegerPropertyDefinition)
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_INTEGER_DEFINITION);
            Element maxValue = doc.createElement(CMIS.CMIS_MAX_VALUE);
            maxValue.appendChild(doc.createTextNode(String.valueOf(((IntegerPropertyDefinition)propertyDefinition)
               .getMaxInteger())));
            propertyElement.appendChild(maxValue);

            Element minValue = doc.createElement(CMIS.CMIS_MIN_VALUE);
            minValue.appendChild(doc.createTextNode(String.valueOf(((IntegerPropertyDefinition)propertyDefinition)
               .getMinInteger())));
            propertyElement.appendChild(minValue);

         }
         else
         {
            propertyElement = doc.createElement(CMIS.CMIS_PROPERTY_DEFINITION);
         }

         Element id = doc.createElement(CMIS.CMIS_ID);
         id.appendChild(doc.createTextNode(propertyDefinition.getId()));
         propertyElement.appendChild(id);

         Element description = doc.createElement(CMIS.CMIS_DESCRIPTION);
         description.appendChild(doc.createTextNode(propertyDefinition.getDescription()));
         propertyElement.appendChild(description);

         Element displayName = doc.createElement(CMIS.CMIS_DISPLAY_NAME);
         displayName.appendChild(doc.createTextNode(propertyDefinition.getDisplayName()));
         propertyElement.appendChild(displayName);

         Element localName = doc.createElement(CMIS.CMIS_LOCAL_NAME);
         localName.appendChild(doc.createTextNode(propertyDefinition.getLocalName()));
         propertyElement.appendChild(localName);

         Element queryName = doc.createElement(CMIS.CMIS_QUERY_NAME);
         queryName.appendChild(doc.createTextNode(propertyDefinition.getQueryName()));
         propertyElement.appendChild(queryName);

         Element propertyType = doc.createElement(CMIS.CMIS_PROPERTY_TYPE);
         propertyType.appendChild(doc.createTextNode(propertyDefinition.getPropertyType().value()));
         propertyElement.appendChild(propertyType);

         Element cardinality = doc.createElement(CMIS.CMIS_CARDINALITY);
         cardinality.appendChild(doc.createTextNode(propertyDefinition.getCardinality().value()));
         propertyElement.appendChild(cardinality);

         Element updatability = doc.createElement(CMIS.CMIS_UPDATABILITY);
         updatability.appendChild(doc.createTextNode(propertyDefinition.getUpdatability().value()));
         propertyElement.appendChild(updatability);

         Element inherited = doc.createElement(CMIS.CMIS_INHERITED);
         inherited.appendChild(doc.createTextNode(String.valueOf(propertyDefinition.isInherited())));
         propertyElement.appendChild(inherited);

         Element queryable = doc.createElement(CMIS.CMIS_QUERYABLE);
         queryable.appendChild(doc.createTextNode(String.valueOf(propertyDefinition.isQueryable())));
         propertyElement.appendChild(queryable);

         Element orderable = doc.createElement(CMIS.CMIS_ORDERABLE);
         orderable.appendChild(doc.createTextNode(String.valueOf(propertyDefinition.isOrderable())));
         propertyElement.appendChild(orderable);

         Element openChoice = doc.createElement(CMIS.CMIS_OPEN_CHOICE);
         openChoice.appendChild(doc.createTextNode(String.valueOf(propertyDefinition.isOpenChoice())));
         propertyElement.appendChild(openChoice);

         Element required = doc.createElement(CMIS.CMIS_REQUIRED);
         required.appendChild(doc.createTextNode(String.valueOf(propertyDefinition.isRequired())));
         propertyElement.appendChild(required);

         addDefaultValueElement(propertyDefinition, propertyElement, doc);
         addChoicesElements(propertyDefinition, propertyElement, doc);

         typeElement.appendChild(propertyElement);
      }
   }

   /**
    * Add default value xml element to property definition xml element.
    * 
    * @param propertyDefinition property definition
    * @param propertyDefinitionElement property definition xml element
    * @param doc xml document
    */
   private static void addDefaultValueElement(PropertyDefinition<?> propertyDefinition,
      Element propertyDefinitionElement, Document doc)
   {
      if (propertyDefinition.getDefaultValue() != null && propertyDefinition.getDefaultValue().length > 0)
      {
         Element defaultValueElement = doc.createElement(CMIS.CMIS_DEFAULT_VALUE);
         for (int i = 0; i < propertyDefinition.getDefaultValue().length; i++)
         {
            Element valueElement = doc.createElement(CMIS.CMIS_VALUE);
            String value = "";
            if (propertyDefinition instanceof DateTimePropertyDefinition)
            {
               value = DateUtil.getDate((Date)propertyDefinition.getDefaultValue()[i]);
            }
            else
            {
               value = String.valueOf(propertyDefinition.getDefaultValue()[i]);
            }
            valueElement.appendChild(doc.createTextNode(value));
            defaultValueElement.appendChild(valueElement);
         }
         propertyDefinitionElement.appendChild(defaultValueElement);
      }
   }

   /**
    * Add choices xml element to property definition xml element.
    * 
    * @param propertyDefinition property definition
    * @param propertyDefinitionElement property definition xml element
    * @param doc xml document
    */
   private static void addChoicesElements(PropertyDefinition<?> propertyDefinition, Element propertyDefinitionElement,
      Document doc)
   {
      for (Choice<?> choice : propertyDefinition.getChoices())
      {
         Element choiceElement = doc.createElement(CMIS.CMIS_CHOICE);
         String displayName = (choice.getDisplayName() == null) ? "" : choice.getDisplayName();
         choiceElement.setAttribute(CMIS.DISPLAY_NAME, displayName);
         for (int i = 0; i < choice.getValues().length; i++)
         {
            Element valueElement = doc.createElement(CMIS.CMIS_VALUE);
            String value = "";
            if (propertyDefinition instanceof DateTimePropertyDefinition)
            {
               value = DateUtil.getDate((Date)choice.getValues()[i]);
            }
            else
            {
               value = String.valueOf(choice.getValues()[i]);
            }
            valueElement.appendChild(doc.createTextNode(value));
            choiceElement.appendChild(valueElement);
         }
         propertyDefinitionElement.appendChild(choiceElement);
      }
   }
}
