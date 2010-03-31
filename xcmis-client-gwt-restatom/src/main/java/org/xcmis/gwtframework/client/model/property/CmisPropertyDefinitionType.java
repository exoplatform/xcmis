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

package org.xcmis.gwtframework.client.model.property;

import org.xcmis.gwtframework.client.model.EnumCardinality;
import org.xcmis.gwtframework.client.model.EnumPropertyType;
import org.xcmis.gwtframework.client.model.EnumUpdatability;
import org.xcmis.gwtframework.client.rest.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisPropertyDefinitionType
{

   /**
    * Id.
    */
   protected String id;

   /**
    * Local name.
    */
   protected String localName;

   /**
    * Local namespace.
    */
   protected String localNamespace;

   /**
    * Display name.
    */
   protected String displayName;

   /**
    * Query name.
    */
   protected String queryName;

   /**
    * Description.
    */
   protected String description;

   /**
    * Property type.
    */
   protected EnumPropertyType propertyType;

   /**
    * Cardinality.
    */
   protected EnumCardinality cardinality;

   /**
    * Updatability.
    */
   protected EnumUpdatability updatability;

   /**
    * Inherited.
    */
   protected Boolean inherited;

   /**
    * Required.
    */
   protected boolean required;

   /**
    * Queryable.
    */
   protected boolean queryable;

   /**
    * Orderable.
    */
   protected boolean orderable;

   /**
    * Open choice.
    */
   protected Boolean openChoice;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the id property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getId()
   {
      return id;
   }

   /**
    * Sets the value of the id property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setId(String value)
   {
      this.id = value;
   }

   /**
    * Gets the value of the localName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * Sets the value of the localName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setLocalName(String value)
   {
      this.localName = value;
   }

   /**
    * Gets the value of the localNamespace property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }

   /**
    * Sets the value of the localNamespace property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setLocalNamespace(String value)
   {
      this.localNamespace = value;
   }

   /**
    * Gets the value of the displayName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Sets the value of the displayName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setDisplayName(String value)
   {
      this.displayName = value;
   }

   /**
    * Gets the value of the queryName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * Sets the value of the queryName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setQueryName(String value)
   {
      this.queryName = value;
   }

   /**
    * Gets the value of the description property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Sets the value of the description property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setDescription(String value)
   {
      this.description = value;
   }

   /**
    * Gets the value of the propertyType property.
    * 
    * @return
    *     possible object is
    *     {@link EnumPropertyType }
    *     
    */
   public EnumPropertyType getPropertyType()
   {
      return propertyType;
   }

   /**
    * Sets the value of the propertyType property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumPropertyType }
    *     
    */
   public void setPropertyType(EnumPropertyType value)
   {
      this.propertyType = value;
   }

   /**
    * Gets the value of the cardinality property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCardinality }
    *     
    */
   public EnumCardinality getCardinality()
   {
      return cardinality;
   }

   /**
    * Sets the value of the cardinality property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCardinality }
    *     
    */
   public void setCardinality(EnumCardinality value)
   {
      this.cardinality = value;
   }

   /**
    * Gets the value of the updatability property.
    * 
    * @return
    *     possible object is
    *     {@link EnumUpdatability }
    *     
    */
   public EnumUpdatability getUpdatability()
   {
      return updatability;
   }

   /**
    * Sets the value of the updatability property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumUpdatability }
    *     
    */
   public void setUpdatability(EnumUpdatability value)
   {
      this.updatability = value;
   }

   /**
    * Gets the value of the inherited property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isInherited()
   {
      return inherited;
   }

   /**
    * Sets the value of the inherited property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setInherited(Boolean value)
   {
      this.inherited = value;
   }

   /**
    * Gets the value of the required property.
    * 
    * @return boolean
    * 
    */
   public boolean isRequired()
   {
      return required;
   }

   /**
    * Sets the value of the required property.
    * 
    * @param value value
    * 
    */
   public void setRequired(boolean value)
   {
      this.required = value;
   }

   /**
    * Gets the value of the queryable property.
    * 
    * @return boolean
    */
   public boolean isQueryable()
   {
      return queryable;
   }

   /**
    * Sets the value of the queryable property.
    * 
    * @param value value
    */
   public void setQueryable(boolean value)
   {
      this.queryable = value;
   }

   /**
    * Gets the value of the orderable property.
    * 
    * @return boolean
    */
   public boolean isOrderable()
   {
      return orderable;
   }

   /**
    * Sets the value of the orderable property.
    * 
    * @param value value
    */
   public void setOrderable(boolean value)
   {
      this.orderable = value;
   }

   /**
    * Gets the value of the openChoice property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isOpenChoice()
   {
      return openChoice;
   }

   /**
    * Sets the value of the openChoice property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setOpenChoice(Boolean value)
   {
      this.openChoice = value;
   }

   /**
    * Gets the value of the any property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the any property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getAny().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link Element }
    * {@link Object }
    * 
    * @return List containing {@link Object}
    * 
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and 
    * the value is the string value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return
    *     always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
