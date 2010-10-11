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
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.model.Choice;
import org.xcmis.spi.model.DateResolution;
import org.xcmis.spi.model.Precision;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.Updatability;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
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
      PropertyDefinition<?> propDef = null;

      switch (propertyType)
      {
         case BOOLEAN : {
            PropertyDefinition<Boolean> defImpl = new PropertyDefinition<Boolean>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               Boolean[] arrayDefs = new Boolean[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = Boolean.parseBoolean(element.getText());
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceBooleanElement(choiceElement).getChoice());
            }
            propDef = defImpl;
            break;
         }
         case DATETIME : {
            PropertyDefinition<Calendar> defImpl = new PropertyDefinition<Calendar>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               Calendar[] arrayDefs = new Calendar[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = AtomUtils.parseCalendar(element.getText());
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceDateTimeElement(choiceElement).getChoice());
            }
            // RESOLUTION
            defImpl.setDateResolution(DateResolution.fromValue(getSimpleExtension(AtomCMIS.RESOLUTION)));
            propDef = defImpl;
            break;
         }
         case DECIMAL : {
            PropertyDefinition<BigDecimal> defImpl = new PropertyDefinition<BigDecimal>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               BigDecimal[] arrayDefs = new BigDecimal[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = new BigDecimal(element.getText());
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceDecimalElement(choiceElement).getChoice());
            }
            // MAX VALUE
            defImpl.setMaxDecimal(new BigDecimal(getSimpleExtension(AtomCMIS.MAX_VALUE)));
            // MIN VALUE
            defImpl.setMinDecimal(new BigDecimal(getSimpleExtension(AtomCMIS.MIN_VALUE)));
            // PRECISION
            defImpl.setDecimalPrecision(Precision.fromValue(Integer.parseInt(getSimpleExtension(AtomCMIS.PRECISION))));
            propDef = defImpl;
            break;
         }
         case HTML : {
            PropertyDefinition<String> defImpl = new PropertyDefinition<String>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               String[] arrayDefs = new String[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = element.getText();
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceHtmlElement(choiceElement).getChoice());
            }
            propDef = defImpl;
            break;
         }
         case ID : {
            PropertyDefinition<String> defImpl = new PropertyDefinition<String>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               String[] arrayDefs = new String[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = element.getText();
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceIdElement(choiceElement).getChoice());
            }
            propDef = defImpl;
            break;
         }
         case STRING : {
            PropertyDefinition<String> defImpl = new PropertyDefinition<String>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               String[] arrayDefs = new String[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = element.getText();
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceStringElement(choiceElement).getChoice());
            }
            // MAXLENGTH
            defImpl.setMaxLength(Integer.parseInt(getSimpleExtension(AtomCMIS.MAX_LENGTH)));
            propDef = defImpl;
            break;
         }
         case INTEGER : {
            PropertyDefinition<BigInteger> defImpl = new PropertyDefinition<BigInteger>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               BigInteger[] arrayDefs = new BigInteger[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = new BigInteger(element.getText());
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceIntegerElement(choiceElement).getChoice());
            }
            // MAX VALUE
            defImpl.setMaxInteger(new BigInteger(getSimpleExtension(AtomCMIS.MAX_VALUE)));
            // MIN VALUE
            defImpl.setMinInteger(new BigInteger(getSimpleExtension(AtomCMIS.MIN_VALUE)));
            propDef = defImpl;
            break;
         }
         case URI : {
            PropertyDefinition<String> defImpl = new PropertyDefinition<String>();
            // DEFAULT VALUE
            FOMExtensibleElement defValueElement = getExtension(AtomCMIS.DEFAULT_VALUE);
            if (defValueElement != null)
            {
               List<Element> elements = defValueElement.getExtensions(AtomCMIS.VALUE);
               String[] arrayDefs = new String[elements.size()];
               int i = 0;
               for (Element element : elements)
               {
                  arrayDefs[i] = element.getText();
                  i++;
               }
               defImpl.setDefaultValue(arrayDefs);
            }
            // CHOICE
            List<FOMExtensibleElement> choicesElements = getExtensions(AtomCMIS.CHOICE);
            for (FOMExtensibleElement choiceElement : choicesElements)
            {
               defImpl.getChoices().add(new ChoiceUriElement(choiceElement).getChoice());
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
      {
         propDef.setLocalName(getSimpleExtension(AtomCMIS.LOCAL_NAME));
      }
      if (getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE) != null)
      {
         propDef.setLocalNamespace(getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE));
      }

      if (getSimpleExtension(AtomCMIS.DISPLAY_NAME) != null)
      {
         propDef.setDisplayName(getSimpleExtension(AtomCMIS.DISPLAY_NAME));
      }
      propDef.setQueryName(getSimpleExtension(AtomCMIS.QUERY_NAME));

      if (getSimpleExtension(AtomCMIS.DESCRIPTION) != null)
      {
         propDef.setDescription(getSimpleExtension(AtomCMIS.DESCRIPTION));
      }
      propDef.setPropertyType(propertyType);

      String cardinality = getSimpleExtension(AtomCMIS.CARDINALITY);
      try
      {
         propDef.setMultivalued("multi".equalsIgnoreCase(cardinality) ? true : false);
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
         {
            addSimpleExtension(AtomCMIS.LOCAL_NAME, propdef.getLocalName());
         }
         if (propdef.getLocalNamespace() != null)
         {
            addSimpleExtension(AtomCMIS.LOCAL_NAMESPACE, propdef.getLocalNamespace());
         }
         if (propdef.getDisplayName() != null)
         {
            addSimpleExtension(AtomCMIS.DISPLAY_NAME, propdef.getDisplayName());
         }
         if (propdef.getDescription() != null)
         {
            addSimpleExtension(AtomCMIS.DESCRIPTION, propdef.getDescription());
         }

         /* flags */
         addSimpleExtension(AtomCMIS.INHERITED, propdef.getInherited() == null ? "false" : Boolean.toString(propdef
            .getInherited()));
         addSimpleExtension(AtomCMIS.REQUIRED, Boolean.toString(propdef.isRequired()));
         addSimpleExtension(AtomCMIS.QUERYABLE, Boolean.toString(propdef.isQueryable()));
         addSimpleExtension(AtomCMIS.ORDERABLE, Boolean.toString(propdef.isOrderable()));

         // From spec. : Is only applicable to properties that provide a value for the "Choices" attribute.
         // Do not decide here provide or not this attribute. Back-end must be care about this.
         if (propdef.isOpenChoice() != null && propdef.isOpenChoice())
         {
            addSimpleExtension(AtomCMIS.OPEN_CHOICE, Boolean.toString(propdef.isOpenChoice()));
         }

         PropertyType propertyType = propdef.getPropertyType();
         switch (propertyType)
         {
            case BOOLEAN : {
               PropertyDefinition<Boolean> defImpl = (PropertyDefinition<Boolean>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceBooleanElement(choiceElement)).build(choice);
                  }
               }
               break;
            }
            case DATETIME : {
               PropertyDefinition<Calendar> defImpl = (PropertyDefinition<Calendar>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceDateTimeElement(choiceElement)).build(choice);
                  }
               }
               // RESOLUTION
               if (defImpl.getDateResolution() != null)
               {
                  addSimpleExtension(AtomCMIS.RESOLUTION, defImpl.getDateResolution().value());
               }
               break;
            }
            case DECIMAL : {
               PropertyDefinition<BigDecimal> defImpl = (PropertyDefinition<BigDecimal>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceDecimalElement(choiceElement)).build(choice);
                  }
               }
               // MAX VALUE
               if (defImpl.getMaxDecimal() != null)
               {
                  addSimpleExtension(AtomCMIS.MAX_VALUE, defImpl.getMaxDecimal().toString());
               }
               // MIN VALUE
               if (defImpl.getMinDecimal() != null)
               {
                  addSimpleExtension(AtomCMIS.MIN_VALUE, defImpl.getMinDecimal().toString());
               }
               // PRECISION
               if (defImpl.getDecimalPrecision() != null)
               {
                  String precision = Integer.toString(defImpl.getDecimalPrecision().getValue());
                  addSimpleExtension(AtomCMIS.PRECISION, precision);
               }
               break;
            }
            case HTML : {
               PropertyDefinition<String> defImpl = (PropertyDefinition<String>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceHtmlElement(choiceElement)).build(choice);
                  }
               }
               break;
            }
            case ID : {
               PropertyDefinition<String> defImpl = (PropertyDefinition<String>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceIdElement(choiceElement)).build(choice);
                  }
               }
               break;
            }
            case STRING : {
               PropertyDefinition<String> defImpl = (PropertyDefinition<String>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceStringElement(choiceElement)).build(choice);
                  }
               }
               // MAXLENGTH
               addSimpleExtension(AtomCMIS.MAX_LENGTH, Integer.toString(defImpl.getMaxLength()));
               break;
            }
            case INTEGER : {
               PropertyDefinition<BigInteger> defImpl = (PropertyDefinition<BigInteger>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceIntegerElement(choiceElement)).build(choice);
                  }
               }
               // MAX VALUE
               if (defImpl.getMaxInteger() != null)
               {
                  addSimpleExtension(AtomCMIS.MAX_VALUE, defImpl.getMaxInteger().toString());
               }
               // MIN VALUE
               if (defImpl.getMinInteger() != null)
               {
                  addSimpleExtension(AtomCMIS.MIN_VALUE, defImpl.getMinInteger().toString());
               }
               break;
            }
            case URI : {
               PropertyDefinition<String> defImpl = (PropertyDefinition<String>)propdef;
               // DEFAULT VALUE
               if (defImpl.getDefaultValue() != null && defImpl.getDefaultValue().length != 0)
               {
                  FOMExtensibleElement defValueElement = addExtension(AtomCMIS.DEFAULT_VALUE);
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
                     FOMExtensibleElement choiceElement = addExtension(AtomCMIS.CHOICE);
                     (new ChoiceUriElement(choiceElement)).build(choice);
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
