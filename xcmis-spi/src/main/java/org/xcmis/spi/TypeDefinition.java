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

package org.xcmis.spi;


import java.util.Collection;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface TypeDefinition
{

   /**
    * Type unique identifier.
    * 
    * @return type id
    */
   String getId();

   /**
    * Local (internal) type name.
    * 
    * @return local type name
    */
   String getLocalName();

   /**
    * Local (internal) name-space for type.
    * 
    * @return local name-space for type
    */
   String getLocalNamespace();

   /**
    * Optional type display name. It may be used in representation purposes.
    * 
    * @return display name or <code>null</code> if not provided
    */
   String getDisplayName();

   /**
    * Type Query name. It is used in SQL queries as table name.
    * 
    * @return type query name
    */
   String getQueryName();

   /**
    * Optional type description.
    * 
    * @return type description or <code>null</code> if not provided
    */
   String getDescription();

   /**
    * Base type ID.
    * 
    * @return base type id
    * @see BaseType
    */
   BaseType getBaseId();

   /**
    * Parent type ID. Must be <code>null</code> for root types.
    * 
    * @return parent type id or <code>null</code>
    */
   String getParentId();

   /**
    * Indicates is type creatable or not. If type is not creatable then
    * repository may contains the object of this type but it is not allowed
    * create new objects of this type.
    * 
    * @return <code>true</code> if creatable <code>false</code> otherwise
    */
   boolean isCreatable();

   /**
    * Indicates is type is fileable or not. If type is fileable than it may
    * child of Folder(s)
    * 
    * @return <code>true</code> if fileable <code>false</code> otherwise
    */
   boolean isFileable();

   /**
    * Is type queryable. It indicates can it be used in <code>FROM</code> clause
    * of SQL statement.
    * 
    * @return <code>true</code> if type queryable <code>false</code> otherwise
    */
   boolean isQueryable();

   /**
    * Indicates can it be used in fulltext queries via SQL
    * <code>CONTAINS(...)</code> clause.
    * 
    * @return <code>true</code> if type fulltextIndexed <code>false</code>
    *         otherwise
    */
   boolean isFulltextIndexed();

   /**
    * Indicates is type included in queries of super-types.
    * 
    * @return <code>true</code> if type included in super types queries
    *         <code>false</code> otherwise
    */
   boolean isIncludedInSupertypeQuery();

   /**
    * Indicates can be CMIS policies applied to object of this type.
    * 
    * @return <code>true</code> if type controllable by policies
    *         <code>false</code> otherwise
    */
   boolean isControllablePolicy();

   /**
    * Indicates can be CMIS ACL applied to object of this type.
    * 
    * @return <code>true</code> if type controllable by ACL <code>false</code>
    *         otherwise
    */
   boolean isControllableACL();

   /**
    * Indicate is this type versionable or not.
    * 
    * @return <code>true</code> if type versionable <code>false</code> otherwise
    */
   boolean isVersionable();

   /**
    * @return property definitions or <code>null</code> if type was retrieved
    *         with parameter 'includePropertyDefintions' as <code>false</code>,
    *         see {@link Connection#getTypeDefinition(String, boolean)}
    */
   Collection<PropertyDefinition<?>> getPropertyDefinitions();

   /**
    * Get {@link PropertyDefinition} by ID.
    * 
    * @param id property definition id
    * @return property definition or <code>null</code> if type was retrieved
    *         with parameter 'includePropertyDefintions' as <code>false</code>,
    *         see {@link Connection#getTypeDefinition(String, boolean)} or if
    *         property definition with specified ID does not exists
    */
   PropertyDefinition<?> getPropertyDefinition(String id);

   /**
    * Set of allowed type to be source of relationship. There is not sense of
    * this attribute for types other then cmis:relationship and method should
    * return <code>null</code>. For relationship <code>null</code> minds there
    * is no any restriction for source types.
    * 
    * @return set of allowed source type or <code>null</code>
    */
   String[] getAllowedSourceTypes();

   /**
    * Set of allowed type to be target of relationship. There is not sense of
    * this attribute for types other then cmis:relationship and method should
    * return <code>null</code>. For relationship <code>null</code> minds there
    * is no any restriction for target types.
    * 
    * @return set of allowed source type or <code>null</code>
    */
   String[] getAllowedTargetTypes();

   /**
    * Indicates is content stream allowed or not allowed or needed for this
    * type. Must be {@link ContentStreamAllowed#NOT_ALLOWED} for type other then
    * cmis:document.
    * 
    * @return type content stream rules
    */
   ContentStreamAllowed getContentStreamAllowed();

}
