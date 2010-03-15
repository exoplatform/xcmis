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
import org.xcmis.core.CmisPropertyBooleanDefinitionType;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyHtmlDefinitionType;
import org.xcmis.core.CmisPropertyIdDefinitionType;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisPropertyUriDefinitionType;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.InvalidArgumentException;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisPropertyDefinitionTypeElementWrapper.java 2279 2009-07-23
 *          11:47:50Z sunman $ Jul 15, 2009
 */
public abstract class PropertyDefinitionTypeElement<T extends CmisPropertyDefinitionType> extends
   ExtensibleElementWrapper
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
   public CmisPropertyDefinitionType getPropertyDefinition()
   {
      String propertyTypeName = getSimpleExtension(AtomCMIS.PROPERTY_TYPE);
      EnumPropertyType propertyType;
      try
      {
         propertyType = EnumPropertyType.fromValue(propertyTypeName);
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException("Unable to parse Property Definition element. Unsupported property type: "
            + propertyTypeName);
      }
      CmisPropertyDefinitionType propDef = null;
      switch (propertyType)
      {
         // TODO : choices & default value for each property definition.
         case BOOLEAN :
            propDef = new CmisPropertyBooleanDefinitionType();
            break;
         case DATETIME :
            propDef = new CmisPropertyDateTimeDefinitionType();
            break;
         case DECIMAL :
            propDef = new CmisPropertyDecimalDefinitionType();
            break;
         case HTML :
            propDef = new CmisPropertyHtmlDefinitionType();
            break;
         case ID :
            propDef = new CmisPropertyIdDefinitionType();
            break;
         case INTEGER :
            propDef = new CmisPropertyIntegerDefinitionType();
            break;
         case STRING :
            propDef = new CmisPropertyStringDefinitionType();
            break;
         case URI :
            propDef = new CmisPropertyUriDefinitionType();
            break;
         default :
            // Should never happen. Exception will throw early.
            throw new InvalidArgumentException("Unknown property type " + propertyType.value());
      }
      propDef.setId(getSimpleExtension(AtomCMIS.ID));
      propDef.setPropertyType(propertyType);
      
      String cardinality = getSimpleExtension(AtomCMIS.CARDINALITY);
      try
      {
         propDef.setCardinality(EnumCardinality.fromValue(cardinality));
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException(
            "Unable to parse Property Definition element. Unsupported 'cardinality' attribute: " + cardinality);
      }
      
      String updatability = getSimpleExtension(AtomCMIS.UPDATABILITY);
      try
      {
         propDef.setUpdatability(EnumUpdatability.fromValue(updatability));
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException(
            "Unable to parse Property Definition element. Unsupported 'updatability' attribute: " + updatability);
      }
      
      propDef.setQueryName(getSimpleExtension(AtomCMIS.QUERY_NAME));
      if (getSimpleExtension(AtomCMIS.LOCAL_NAME) != null)
         propDef.setLocalName(getSimpleExtension(AtomCMIS.LOCAL_NAME));
      if (getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE) != null)
         propDef.setLocalNamespace(getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE));
      if (getSimpleExtension(AtomCMIS.DISPLAY_NAME) != null)
         propDef.setDisplayName(getSimpleExtension(AtomCMIS.DISPLAY_NAME));
      if (getSimpleExtension(AtomCMIS.DESCRIPTION) != null)
         propDef.setDescription(getSimpleExtension(AtomCMIS.DESCRIPTION));

      /* flags */
      propDef.setInherited(getSimpleExtension(AtomCMIS.INHERITED) != null ? false : Boolean
         .parseBoolean(getSimpleExtension(AtomCMIS.INHERITED)));
      propDef.setRequired(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.REQUIRED)));
      propDef.setQueryable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.QUERYABLE)));
      propDef.setOrderable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.ORDERABLE)));
      //      propDef.setOpenChoice(getSimpleExtension(AtomCMIS.OPEN_CHOICE) != null ? false : Boolean
      //         .parseBoolean(getSimpleExtension(AtomCMIS.OPEN_CHOICE)));
      return propDef;

   }

   /**
    * Builds the element.
    * 
    * @param propdef the propdef
    */
   public void build(T propdef)
   {
      if (propdef != null)
      {
         addSimpleExtension(AtomCMIS.ID, propdef.getId());
         addSimpleExtension(AtomCMIS.PROPERTY_TYPE, propdef.getPropertyType().value());
         addSimpleExtension(AtomCMIS.CARDINALITY, propdef.getCardinality().value());
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
         addSimpleExtension(AtomCMIS.INHERITED, propdef.isInherited() == null ? "false" : Boolean.toString(propdef
            .isInherited()));
         addSimpleExtension(AtomCMIS.REQUIRED, Boolean.toString(propdef.isRequired()));
         addSimpleExtension(AtomCMIS.QUERYABLE, Boolean.toString(propdef.isQueryable()));
         addSimpleExtension(AtomCMIS.ORDERABLE, Boolean.toString(propdef.isOrderable()));

         // From spec. : Is only applicable to properties that provide a value for the "Choices" attribute.
         // Do not decide here provide or not this attribute. Back-end must be care about this. 
         if (propdef.isOpenChoice() != null)
            addSimpleExtension(AtomCMIS.OPEN_CHOICE, Boolean.toString(propdef.isOpenChoice()));
         // TODO : choices & default value
      }
   }

}
