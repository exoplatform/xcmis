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

package org.xcmis.gwtframework.client.model;

import org.xcmis.gwtframework.client.model.property.CmisPropertiesType;
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
public class CmisObjectType
{

   /**
    * Properties.
    */
   protected CmisPropertiesType properties;

   /**
    * Allowable actions.
    */
   protected CmisAllowableActionsType allowableActions;

   /**
    * Relationship.
    */
   protected List<CmisObjectType> relationship;

   /**
    * Child.
    */
   protected List<CmisObjectType> child;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Map other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the properties property.
    * 
    * @return possible object is {@link CmisPropertiesType }
    * 
    */
   public CmisPropertiesType getProperties()
   {
      return properties;
   }

   /**
    * Sets the value of the properties property.
    * 
    * @param value
    *            allowed object is {@link CmisPropertiesType }
    * 
    */
   public void setProperties(CmisPropertiesType value)
   {
      this.properties = value;
   }

   /**
    * Gets the value of the allowableActions property.
    * 
    * @return possible object is {@link CmisAllowableActionsType }
    * 
    */
   public CmisAllowableActionsType getAllowableActions()
   {
      return allowableActions;
   }

   /**
    * Sets the value of the allowableActions property.
    * 
    * @param value
    *            allowed object is {@link CmisAllowableActionsType }
    * 
    */
   public void setAllowableActions(CmisAllowableActionsType value)
   {
      this.allowableActions = value;
   }

   /**
    * Gets the value of the relationship property.
    * 
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getRelationship().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObjectType }
    * 
    *  @return list containing CmisObjectType
    */
   public List<CmisObjectType> getRelationship()
   {
      if (relationship == null)
      {
         relationship = new ArrayList<CmisObjectType>();
      }
      return this.relationship;
   }

   /**
    * Gets the value of the child property.
    * 
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getChild().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObjectType }
    * 
    * @return list containing CmisObjectType
    * 
    */
   public List<CmisObjectType> getChild()
   {
      if (child == null)
      {
         child = new ArrayList<CmisObjectType>();
      }
      return this.child;
   }

   /**
    * Gets the value of the any property.
    * 
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getAny().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list {@link Element }
    * {@link Object }
    * 
    * @return list containing Object
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
    * Gets a map that contains attributes that aren't bound to any typed
    * property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and the value is the string
    * value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute by
    * updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
