/*
 * Copyright (C); 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option); any later version.
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

package org.xcmis.spi.model;


import org.xcmis.spi.Connection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class TypeDefinition
{

   private String id;

   private BaseType baseId;

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

   private ContentStreamAllowed contentStreamAllowed;

   private Map<String, PropertyDefinition<?>> propertyDefinitions;

   public TypeDefinition()
   {
   }

   public TypeDefinition(String id, BaseType baseId, String queryName, String localName, String localNamespace,
      String parentId, String displayName, String description, boolean creatable, boolean fileable, boolean queryable,
      boolean fulltextIndexed, boolean includedInSupertypeQuery, boolean controllablePolicy, boolean controllableACL,
      boolean versionable, String[] allowedSourceTypes, String[] allowedTargetTypes,
      ContentStreamAllowed contentStreamAllowed, Map<String, PropertyDefinition<?>> propertyDefinitions)
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
    * Type unique identifier.
    *
    * @return type id
    */
   public String getId()
   {
      return id;
   }

   /**
    * Base type ID.
    *
    * @return base type id
    * @see BaseType
    */
   public BaseType getBaseId()
   {
      return baseId;
   }

   /**
    * Type Query name. It is used in SQL queries as table name.
    *
    * @return type query name
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * Local (internal) type name.
    *
    * @return local type name
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * Local (internal) name-space for type.
    *
    * @return local name-space for type
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }

   /**
    * Parent type ID. Must be <code>null</code> for root types.
    *
    * @return parent type id or <code>null</code>
    */
   public String getParentId()
   {
      return parentId;
   }

   /**
    * Optional type display name. It may be used in representation purposes.
    *
    * @return display name or <code>null</code> if not provided
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Optional type description.
    *
    * @return type description or <code>null</code> if not provided
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Indicates is type creatable or not. If type is not creatable then
    * repository may contains the object of this type but it is not allowed
    * create new objects of this type.
    *
    * @return <code>true</code> if creatable <code>false</code> otherwise
    */
   public boolean isCreatable()
   {
      return creatable;
   }

   /**
    * Indicates is type is fileable or not. If type is fileable than it may
    * child of Folder(s)
    *
    * @return <code>true</code> if fileable <code>false</code> otherwise
    */
   public boolean isFileable()
   {
      return fileable;
   }

   /**
    * Is type queryable. It indicates can it be used in <code>FROM</code> clause
    * of SQL statement.
    *
    * @return <code>true</code> if type queryable <code>false</code> otherwise
    */
   public boolean isQueryable()
   {
      return queryable;
   }

   /**
    * Indicates can it be used in fulltext queries via SQL
    * <code>CONTAINS(...)</code> clause.
    *
    * @return <code>true</code> if type fulltextIndexed <code>false</code>
    *         otherwise
    */
   public boolean isFulltextIndexed()
   {
      return fulltextIndexed;
   }

   /**
    * Indicates is type included in queries of super-types.
    *
    * @return <code>true</code> if type included in super types queries
    *         <code>false</code> otherwise
    */
   public boolean isIncludedInSupertypeQuery()
   {
      return includedInSupertypeQuery;
   }

   /**
    * Indicates can be CMIS policies applied to object of this type.
    *
    * @return <code>true</code> if type controllable by policies
    *         <code>false</code> otherwise
    */
   public boolean isControllablePolicy()
   {
      return controllablePolicy;
   }

   /**
    * Indicates can be CMIS ACL applied to object of this type.
    *
    * @return <code>true</code> if type controllable by ACL <code>false</code>
    *         otherwise
    */
   public boolean isControllableACL()
   {
      return controllableACL;
   }

   /**
    * Indicates is content stream allowed or not allowed or needed for this
    * type. Must be {@link ContentStreamAllowed#NOT_ALLOWED} for type other then
    * cmis:document.
    *
    * @return type content stream rules
    */
   public ContentStreamAllowed getContentStreamAllowed()
   {
      return contentStreamAllowed;
   }

   /**
    * Set of allowed type to be source of relationship. There is not sense of
    * this attribute for types other then cmis:relationship and method should
    * return <code>null</code>. For relationship <code>null</code> minds there
    * is no any restriction for source types.
    *
    * @return set of allowed source type or <code>null</code>
    */
   public String[] getAllowedSourceTypes()
   {
      return allowedSourceTypes;
   }

   /**
    * Set of allowed type to be target of relationship. There is not sense of
    * this attribute for types other then cmis:relationship and method should
    * return <code>null</code>. For relationship <code>null</code> minds there
    * is no any restriction for target types.
    *
    * @return set of allowed source type or <code>null</code>
    */
   public String[] getAllowedTargetTypes()
   {
      return allowedTargetTypes;
   }

   /**
    * Indicate is this type versionable or not.
    *
    * @return <code>true</code> if type versionable <code>false</code> otherwise
    */
   public boolean isVersionable()
   {
      return versionable;
   }

   /**
    * @return property definitions or <code>null</code> if type was retrieved
    *         with parameter 'includePropertyDefintions' as <code>false</code>,
    *         see {@link Connection#getTypeDefinition(String, boolean)}
    */
   public Collection<PropertyDefinition<?>> getPropertyDefinitions()
   {
      if (propertyDefinitions != null)
      {
         return Collections.unmodifiableCollection(propertyDefinitions.values());
      }
      return null;
   }

   /**
    * Get {@link PropertyDefinition} by ID.
    *
    * @param id property definition id
    * @return property definition or <code>null</code> if type was retrieved
    *         with parameter 'includePropertyDefintions' as <code>false</code>,
    *         see {@link Connection#getTypeDefinition(String, boolean)} or if
    *         property definition with specified ID does not exists
    */
   public PropertyDefinition<?> getPropertyDefinition(String id)
   {
      if (propertyDefinitions != null)
      {
         return propertyDefinitions.get(id);
      }
      return null;
   }

   // --- Setters.

   public void setId(String id)
   {
      this.id = id;
   }

   public void setBaseId(BaseType baseId)
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

   public void setContentStreamAllowed(ContentStreamAllowed contentStreamAllowed)
   {
      this.contentStreamAllowed = contentStreamAllowed;
   }

   public void setPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions)
   {
      this.propertyDefinitions = new HashMap<String, PropertyDefinition<?>>(propertyDefinitions);
   }
















}
