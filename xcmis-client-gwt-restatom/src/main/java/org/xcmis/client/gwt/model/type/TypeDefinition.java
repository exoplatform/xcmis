/*
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

package org.xcmis.client.gwt.model.type;

import org.xcmis.client.gwt.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.model.EnumContentStreamAllowed;
import org.xcmis.client.gwt.model.property.PropertyDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple plain implementation of {@link TypeDefinition}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class TypeDefinition 
{
   private String id;

   private EnumBaseObjectTypeIds baseId;

   private String queryName;

   private String localName;

   private String localNamespace;

   private String parentId;

   private String displayName;

   private String description;

   private boolean creatable;

   private boolean fileable;

   private boolean queryable;

   private boolean fulltextIndexed;

   private boolean includedInSupertypeQuery;

   private boolean controllablePolicy;

   private boolean controllableACL;

   private boolean versionable;

   private String[] allowedSourceTypes;

   private String[] allowedTargetTypes;

   private EnumContentStreamAllowed contentStreamAllowed;

   private Map<String, PropertyDefinition<?>> propertyDefinitions;

   public TypeDefinition()
   {
   }

   public TypeDefinition(String id, EnumBaseObjectTypeIds baseId, String queryName, String localName, String localNamespace,
      String parentId, String displayName, String description, boolean creatable, boolean fileable, boolean queryable,
      boolean fulltextIndexed, boolean includedInSupertypeQuery, boolean controllablePolicy, boolean controllableACL,
      boolean versionable, String[] allowedSourceTypes, String[] allowedTargetTypes,
      EnumContentStreamAllowed contentStreamAllowed, Map<String, PropertyDefinition<?>> propertyDefinitions)
   {
      this.id = id;
      this.baseId = baseId;
      this.queryName = queryName;
      this.localName = localName;
      this.localNamespace = localNamespace;
      this.parentId = parentId;
      this.displayName = displayName;
      this.description = description;
      this.creatable = creatable;
      this.fileable = fileable;
      this.queryable = queryable;
      this.fulltextIndexed = fulltextIndexed;
      this.includedInSupertypeQuery = includedInSupertypeQuery;
      this.controllablePolicy = controllablePolicy;
      this.controllableACL = controllableACL;
      this.versionable = versionable;
      this.allowedSourceTypes = allowedSourceTypes;
      this.allowedTargetTypes = allowedTargetTypes;
      this.contentStreamAllowed = contentStreamAllowed;
      this.propertyDefinitions = propertyDefinitions;
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public EnumBaseObjectTypeIds getBaseId()
   {
      return baseId;
   }

   /**
    * {@inheritDoc}
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }

   /**
    * {@inheritDoc}
    */
   public String getParentId()
   {
      return parentId;
   }

   /**
    * {@inheritDoc}
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCreatable()
   {
      return creatable;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isFileable()
   {
      return fileable;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isQueryable()
   {
      return queryable;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isFulltextIndexed()
   {
      return fulltextIndexed;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isIncludedInSupertypeQuery()
   {
      return includedInSupertypeQuery;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isControllablePolicy()
   {
      return controllablePolicy;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isControllableACL()
   {
      return controllableACL;
   }

   /**
    * {@inheritDoc}
    */
   public EnumContentStreamAllowed getContentStreamAllowed()
   {
      return contentStreamAllowed;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getAllowedSourceTypes()
   {
      return allowedSourceTypes;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getAllowedTargetTypes()
   {
      return allowedTargetTypes;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isVersionable()
   {
      return versionable;
   }

   
   /**
    * @return the propertyDefinitions
    */
   public Map<String, PropertyDefinition<?>> getPropertyDefinitions()
   {
      if (propertyDefinitions == null){
         propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      }
      return propertyDefinitions;
   }

   /**
    * {@inheritDoc}
    */
   public PropertyDefinition<?> getPropertyDefinition(String id)
   {
      if (propertyDefinitions != null)
         return propertyDefinitions.get(id);
      return null;
   }

   // --- Setters.

   public void setId(String id)
   {
      this.id = id;
   }

   public void setBaseId(EnumBaseObjectTypeIds baseId)
   {
      this.baseId = baseId;
   }

   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
   }

   public void setLocalName(String localName)
   {
      this.localName = localName;
   }

   public void setLocalNamespace(String localNamespace)
   {
      this.localNamespace = localNamespace;
   }

   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public void setCreatable(boolean creatable)
   {
      this.creatable = creatable;
   }

   public void setFileable(boolean fileable)
   {
      this.fileable = fileable;
   }

   public void setQueryable(boolean queryable)
   {
      this.queryable = queryable;
   }

   public void setFulltextIndexed(boolean fulltextIndexed)
   {
      this.fulltextIndexed = fulltextIndexed;
   }

   public void setIncludedInSupertypeQuery(boolean includedInSupertypeQuery)
   {
      this.includedInSupertypeQuery = includedInSupertypeQuery;
   }

   public void setControllablePolicy(boolean controllablePolicy)
   {
      this.controllablePolicy = controllablePolicy;
   }

   public void setControllableACL(boolean controllableACL)
   {
      this.controllableACL = controllableACL;
   }

   public void setVersionable(boolean versionable)
   {
      this.versionable = versionable;
   }

   public void setAllowedSourceTypes(String[] allowedSourceTypes)
   {
      this.allowedSourceTypes = allowedSourceTypes;
   }

   public void setAllowedTargetTypes(String[] allowedTargetTypes)
   {
      this.allowedTargetTypes = allowedTargetTypes;
   }

   public void setContentStreamAllowed(EnumContentStreamAllowed contentStreamAllowed)
   {
      this.contentStreamAllowed = contentStreamAllowed;
   }

   public void setPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions)
   {
      this.propertyDefinitions = new HashMap<String, PropertyDefinition<?>>(propertyDefinitions);
   }

}
