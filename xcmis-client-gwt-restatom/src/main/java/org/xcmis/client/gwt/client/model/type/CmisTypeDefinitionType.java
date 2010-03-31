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

package org.xcmis.client.gwt.client.model.type;

import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDefinitionType;
import org.xcmis.client.gwt.client.rest.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CmisTypeDefinitionType
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
    * Base id.
    */
   protected EnumBaseObjectTypeIds baseId;

   /**
    * Parent id.
    */
   protected String parentId;

   /**
    * Creatable.
    */
   protected boolean creatable;

   /**
    * Fileable.
    */
   protected boolean fileable;

   /**
    * Queryable.
    */
   protected boolean queryable;

   /**
    * Fulltext indexed.
    */
   protected boolean fulltextIndexed;

   /**
    * Included in supertype query.
    */
   protected boolean includedInSupertypeQuery;

   /**
    * Controllable policy.
    */
   protected boolean controllablePolicy;

   /**
    * Controllable ACL.
    */
   protected boolean controllableACL;

   /**
    * Property definition.
    */
   protected List<CmisPropertyDefinitionType> propertyDefinition;

   /**
    * Any.
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
    * Gets the value of the baseId property.
    * 
    * @return
    *     possible object is
    *     {@link EnumBaseObjectTypeIds }
    *     
    */
   public EnumBaseObjectTypeIds getBaseId()
   {
      return baseId;
   }

   /**
    * Sets the value of the baseId property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumBaseObjectTypeIds }
    *     
    */
   public void setBaseId(EnumBaseObjectTypeIds value)
   {
      this.baseId = value;
   }

   /**
    * Gets the value of the parentId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getParentId()
   {
      return parentId;
   }

   /**
    * Sets the value of the parentId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setParentId(String value)
   {
      this.parentId = value;
   }

   /**
    * Gets the value of the creatable property.
    * 
    * @return boolean
    */
   public boolean isCreatable()
   {
      return creatable;
   }

   /**
    * Sets the value of the creatable property.
    * 
    * @param value value
    */
   public void setCreatable(boolean value)
   {
      this.creatable = value;
   }

   /**
    * Gets the value of the fileable property.
    * 
    * @return boolean
    */
   public boolean isFileable()
   {
      return fileable;
   }

   /**
    * Sets the value of the fileable property.
    * 
    * @param value value
    */
   public void setFileable(boolean value)
   {
      this.fileable = value;
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
    * Gets the value of the fulltextIndexed property.
    * 
    * @return boolean
    */
   public boolean isFulltextIndexed()
   {
      return fulltextIndexed;
   }

   /**
    * Sets the value of the fulltextIndexed property.
    * 
    * @param value value
    */
   public void setFulltextIndexed(boolean value)
   {
      this.fulltextIndexed = value;
   }

   /**
    * Gets the value of the includedInSupertypeQuery property.
    * 
    * @return boolean
    */
   public boolean isIncludedInSupertypeQuery()
   {
      return includedInSupertypeQuery;
   }

   /**
    * Sets the value of the includedInSupertypeQuery property.
    * 
    * @param value value
    */
   public void setIncludedInSupertypeQuery(boolean value)
   {
      this.includedInSupertypeQuery = value;
   }

   /**
    * Gets the value of the controllablePolicy property.
    * 
    * @return boolean
    */
   public boolean isControllablePolicy()
   {
      return controllablePolicy;
   }

   /**
    * Sets the value of the controllablePolicy property.
    * 
    * @param value value
    */
   public void setControllablePolicy(boolean value)
   {
      this.controllablePolicy = value;
   }

   /**
    * Gets the value of the controllableACL property.
    * 
    * @return boolean
    */
   public boolean isControllableACL()
   {
      return controllableACL;
   }

   /**
    * Sets the value of the controllableACL property.
    * 
    * @param value value
    */
   public void setControllableACL(boolean value)
   {
      this.controllableACL = value;
   }

 
   /**
    * @return List containing {@link CmisPropertyDefinitionType}
    */
   public List<CmisPropertyDefinitionType> getPropertyDefinition()
   {
      if (propertyDefinition == null)
      {
         propertyDefinition = new ArrayList<CmisPropertyDefinitionType>();
      }
      return this.propertyDefinition;
   }

  
   /**
    * @return List containing {@link Object}
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
    * @return always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
