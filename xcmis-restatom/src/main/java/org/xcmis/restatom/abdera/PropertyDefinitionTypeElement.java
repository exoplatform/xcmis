/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.Choice;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.impl.PropertyDefinitionImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisPropertyDefinitionTypeElementWrapper.java 2279 2009-07-23
 *          11:47:50Z sunman $ Jul 15, 2009
 */
public class PropertyDefinitionTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new property definition type element.
    * 
    * @param internal the internal
    */
   public PropertyDefinitionTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new property definition type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertyDefinitionTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Gets the property definition.
    * 
    * @return the property definition
    */
   public PropertyDefinition<?> getPropertyDefinition()
   {
      String propertyTypeName = getSimpleExtension(AtomCMIS.PROPERTY_TYPE);
      PropertyType propertyType;
      try
      {
         propertyType = PropertyType.fromValue(propertyTypeName);
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException("Unable to parse Property Definition element. Unsupported property type: "
            + propertyTypeName);
      }
      PropertyDefinitionImpl<?> propDef = null;

      switch (propertyType)
      {
         case BOOLEAN : {
            PropertyDefinitionImpl<Boolean> defImpl = new PropertyDefinitionImpl<Boolean>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            Boolean[] arrayDefs = new Boolean[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               arrayDefs[i] = Boolean.parseBoolean(element.getText());
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceBooleanElement> choicesElements = getExtensions(AtomCMIS.CHOICE_BOOLEAN);
            for (ChoiceBooleanElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         case DATETIME : {
            PropertyDefinitionImpl<Calendar> defImpl = new PropertyDefinitionImpl<Calendar>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            Calendar[] arrayDefs = new Calendar[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               arrayDefs[i] = AtomUtils.parseCalendar(element.getText());
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceDateTimeElement> choicesElements = getExtensions(AtomCMIS.CHOICE_DATE_TIME);
            for (ChoiceDateTimeElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         case DECIMAL : {
            PropertyDefinitionImpl<BigDecimal> defImpl = new PropertyDefinitionImpl<BigDecimal>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            BigDecimal[] arrayDefs = new BigDecimal[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               arrayDefs[i] = new BigDecimal(element.getText());
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceDecimalElement> choicesElements = getExtensions(AtomCMIS.CHOICE_DECIMAL);
            for (ChoiceDecimalElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         case HTML :
         case ID :
         case STRING : {
            PropertyDefinitionImpl<String> defImpl = new PropertyDefinitionImpl<String>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            String[] arrayDefs = new String[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               arrayDefs[i] = element.getText();
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceStringElement> choicesElements = getExtensions(AtomCMIS.CHOICE_STRING);
            for (ChoiceStringElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         case INTEGER : {
            PropertyDefinitionImpl<BigInteger> defImpl = new PropertyDefinitionImpl<BigInteger>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            BigInteger[] arrayDefs = new BigInteger[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               arrayDefs[i] = new BigInteger(element.getText());
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceIntegerElement> choicesElements = getExtensions(AtomCMIS.CHOICE_INTEGER);
            for (ChoiceIntegerElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         case URI : {
            PropertyDefinitionImpl<URI> defImpl = new PropertyDefinitionImpl<URI>();
            // DEFAULT VALUE
            ExtensibleElementWrapper defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
            URI[] arrayDefs = new URI[elements.size()];
            int i = 0;
            for (Element element : elements)
            {
               try
               {
                  arrayDefs[i] = new URI(element.getText());
               }
               catch (URISyntaxException e)
               {
               }
               i++;
            }
            defImpl.setDefaultValue(arrayDefs);
            // CHOICE
            List<ChoiceUriElement> choicesElements = getExtensions(AtomCMIS.CHOICE_URI);
            for (ChoiceUriElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(choiceElement.getChoice());
            }
            propDef = defImpl;
            break;
         }
         default :
            // Should never happen. Exception will throw early.
            throw new InvalidArgumentException("Unknown property type " + propertyType.value());
      }
      propDef.setId(getSimpleExtension(AtomCMIS.ID));

      if (getSimpleExtension(AtomCMIS.LOCAL_NAME) != null)
         propDef.setLocalName(getSimpleExtension(AtomCMIS.LOCAL_NAME));
      if (getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE) != null)
         propDef.setLocalNamespace(getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE));

      if (getSimpleExtension(AtomCMIS.DISPLAY_NAME) != null)
         propDef.setDisplayName(getSimpleExtension(AtomCMIS.DISPLAY_NAME));
      propDef.setQueryName(getSimpleExtension(AtomCMIS.QUERY_NAME));

      if (getSimpleExtension(AtomCMIS.DESCRIPTION) != null)
         propDef.setDescription(getSimpleExtension(AtomCMIS.DESCRIPTION));
      propDef.setPropertyType(propertyType);

      String cardinality = getSimpleExtension(AtomCMIS.CARDINALITY);
      try
      {
         propDef.setMultivalued(cardinality == null ? false : Boolean.parseBoolean(cardinality));
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException(
            "Unable to parse Property Definition element. Unsupported 'cardinality' attribute: " + cardinality);
      }

      String updatability = getSimpleExtension(AtomCMIS.UPDATABILITY);
      try
      {
         propDef.setUpdatability(Updatability.fromValue(updatability));
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException(
            "Unable to parse Property Definition element. Unsupported 'updatability' attribute: " + updatability);
      }

      /* flags */
      propDef.setInherited(getSimpleExtension(AtomCMIS.INHERITED) == null ? false : Boolean
         .parseBoolean(getSimpleExtension(AtomCMIS.INHERITED)));
      propDef.setRequired(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.REQUIRED)));
      propDef.setQueryable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.QUERYABLE)));
      propDef.setOrderable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.ORDERABLE)));
      propDef.setOpenChoice(getSimpleExtension(AtomCMIS.OPEN_CHOICE) == null ? false : Boolean
         .parseBoolean(getSimpleExtension(AtomCMIS.OPEN_CHOICE)));
      return propDef;

   }

   /**
    * Builds the element.
    * 
    * @param propdef the propdef
    */
   public void build(PropertyDefinition<?> propdef)
   {
      if (propdef != null)
      {
         addSimpleExtension(AtomCMIS.ID, propdef.getId());
         addSimpleExtension(AtomCMIS.PROPERTY_TYPE, propdef.getPropertyType().value());
         addSimpleExtension(AtomCMIS.CARDINALITY, propdef.isMultivalued() ? "multi" : "single");
         addSimpleExtension(AtomCMIS.UPDATABILITY, propdef.getUpdatability().value());
         addSimpleExtension(AtomCMIS.QUERY_NAME, propdef.getQueryName());
         if (propdef.getLocalName() != null)
            addSimpleExtension(AtomCMIS.LOCAL_NAME, propdef.getLocalName());
         if (propdef.getLocalNamespace() != null)
            addSimpleExtension(AtomCMIS.LOCAL_NAMESPACE, propdef.getLocalNamespace());
         if (propdef.getDisplayName() != null)
            addSimpleExtension(AtomCMIS.DISPLAY_NAME, propdef.getDisplayName());
         if (propdef.getDescription() != null)
            addSimpleExtension(AtomCMIS.DESCRIPTION, propdef.getDescription());

         /* flags */
         addSimpleExtension(AtomCMIS.INHERITED, propdef.getInherited() == null ? "false" : Boolean.toString(propdef
            .getInherited()));
         addSimpleExtension(AtomCMIS.REQUIRED, Boolean.toString(propdef.isRequired()));
         addSimpleExtension(AtomCMIS.QUERYABLE, Boolean.toString(propdef.isQueryable()));
         addSimpleExtension(AtomCMIS.ORDERABLE, Boolean.toString(propdef.isOrderable()));

         // From spec. : Is only applicable to properties that provide a value for the "Choices" attribute.
         // Do not decide here provide or not this attribute. Back-end must be care about this. 
         if (propdef.isOpenChoice())
            addSimpleExtension(AtomCMIS.OPEN_CHOICE, Boolean.toString(propdef.isOpenChoice()));

         PropertyType propertyType = propdef.getPropertyType();
         switch (propertyType)
         {
            case BOOLEAN : {
               PropertyDefinitionImpl<Boolean> defImpl = (PropertyDefinitionImpl<Boolean>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (Boolean el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<Boolean>> choiceList = defImpl.getChoices();
                  for (Choice<Boolean> choice : choiceList)
                  {
                     (new ChoiceBooleanElement(this)).build(choice);
                  }
               }
               break;
            }
            case DATETIME : {
               PropertyDefinitionImpl<Calendar> defImpl = (PropertyDefinitionImpl<Calendar>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (Calendar el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<Calendar>> choiceList = defImpl.getChoices();
                  for (Choice<Calendar> choice : choiceList)
                  {
                     (new ChoiceDateTimeElement(this)).build(choice);
                  }
               }
               break;
            }
            case DECIMAL : {
               PropertyDefinitionImpl<BigDecimal> defImpl = (PropertyDefinitionImpl<BigDecimal>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (BigDecimal el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<BigDecimal>> choiceList = defImpl.getChoices();
                  for (Choice<BigDecimal> choice : choiceList)
                  {
                     (new ChoiceDecimalElement(this)).build(choice);
                  }
               }
               break;
            }
            case HTML :
            case ID :
            case STRING : {
               PropertyDefinitionImpl<String> defImpl = (PropertyDefinitionImpl<String>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (String el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<String>> choiceList = defImpl.getChoices();
                  for (Choice<String> choice : choiceList)
                  {
                     (new ChoiceStringElement(this)).build(choice);
                  }
               }
               break;
            }
            case INTEGER : {
               PropertyDefinitionImpl<BigInteger> defImpl = (PropertyDefinitionImpl<BigInteger>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (BigInteger el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<BigInteger>> choiceList = defImpl.getChoices();
                  for (Choice<BigInteger> choice : choiceList)
                  {
                     (new ChoiceIntegerElement(this)).build(choice);
                  }
               }
               break;
            }
            case URI : {
               PropertyDefinitionImpl<URI> defImpl = (PropertyDefinitionImpl<URI>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  ExtensibleElementWrapper defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
                  for (URI el : defImpl.getDefaultValue())
                  {
                     defValueElement.addSimpleExtension(AtomCMIS.VALUE, el.toString());
                  }
               }
               // CHOICE
               if (defImpl.getChoices() != null && defImpl.getChoices().size() != 0)
               {
                  List<Choice<URI>> choiceList = defImpl.getChoices();
                  for (Choice<URI> choice : choiceList)
                  {
                     (new ChoiceUriElement(this)).build(choice);
                  }
               }
               break;
            }
            default :
               // Should never happen. Exception will throw early.
               throw new InvalidArgumentException("Unknown property type " + propertyType.value());
         }

      }
   }
}
