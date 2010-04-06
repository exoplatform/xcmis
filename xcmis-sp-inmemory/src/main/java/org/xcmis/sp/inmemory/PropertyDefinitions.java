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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.CMIS;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Choice;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.impl.PropertyDefinitionImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping for known CMIS object properties.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyDefinitions.java 578 2010-04-02 12:25:27Z andrew00x $
 */
public final class PropertyDefinitions
{

   private static final Map<String, Map<String, PropertyDefinition<?>>> all =
      new HashMap<String, Map<String, PropertyDefinition<?>>>();

   static
   {
      for (BaseType objectType : BaseType.values())
      {
         // Common properties.
         add(objectType.value(), createPropertyDefinition(CMIS.BASE_TYPE_ID, PropertyType.ID, CMIS.BASE_TYPE_ID,
            CMIS.BASE_TYPE_ID, null, CMIS.BASE_TYPE_ID, false, false, false, false, false, Updatability.READONLY,
            "Base type id.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.OBJECT_TYPE_ID, PropertyType.ID, CMIS.OBJECT_TYPE_ID,
            CMIS.OBJECT_TYPE_ID, null, CMIS.OBJECT_TYPE_ID, false, false, false, false, false, Updatability.READONLY,
            "Object type id.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.OBJECT_ID, PropertyType.ID, CMIS.OBJECT_ID,
            CMIS.OBJECT_ID, null, CMIS.OBJECT_ID, false, false, false, false, false, Updatability.READONLY,
            "Object id.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.NAME, PropertyType.STRING, CMIS.NAME, CMIS.NAME, null,
            CMIS.NAME, true, false, false, false, false, Updatability.READWRITE, "Object name.", true, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.CREATED_BY, PropertyType.STRING, CMIS.CREATED_BY,
            CMIS.CREATED_BY, null, CMIS.CREATED_BY, false, false, false, false, false, Updatability.READONLY,
            "User who created the object.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.CREATION_DATE, PropertyType.DATETIME,
            CMIS.CREATION_DATE, CMIS.CREATION_DATE, null, CMIS.CREATION_DATE, false, false, false, false, false,
            Updatability.READONLY, "DateTime when the object was created.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.LAST_MODIFIED_BY, PropertyType.STRING,
            CMIS.LAST_MODIFIED_BY, CMIS.LAST_MODIFIED_BY, null, CMIS.LAST_MODIFIED_BY, false, false, false, false,
            false, Updatability.READONLY, "User who last modified the object.", null, null, null));

         add(objectType.value(),
            createPropertyDefinition(CMIS.LAST_MODIFICATION_DATE, PropertyType.DATETIME, CMIS.LAST_MODIFICATION_DATE,
               CMIS.LAST_MODIFICATION_DATE, null, CMIS.LAST_MODIFICATION_DATE, false, false, false, false, false,
               Updatability.READONLY, "DateTime when the object was last modified.", null, null, null));

         add(objectType.value(), createPropertyDefinition(CMIS.CHANGE_TOKEN, PropertyType.STRING, CMIS.CHANGE_TOKEN,
            CMIS.CHANGE_TOKEN, null, CMIS.CHANGE_TOKEN, false, false, false, false, false, Updatability.READONLY,
            "Opaque token used for optimistic locking.", null, null, null));

         // Type specific.
         if (objectType == BaseType.DOCUMENT)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.IS_IMMUTABLE, PropertyType.BOOLEAN,
               CMIS.IS_IMMUTABLE, CMIS.IS_IMMUTABLE, null, CMIS.IS_IMMUTABLE, false, false, false, false, false,
               Updatability.READONLY,
               "TRUE if the repository MUST throw an error at any attempt to update or delete the object.", null, null,
               null));

            add(objectType.value(), createPropertyDefinition(CMIS.IS_LATEST_VERSION, PropertyType.BOOLEAN,
               CMIS.IS_LATEST_VERSION, CMIS.IS_LATEST_VERSION, null, CMIS.IS_LATEST_VERSION, false, false, false,
               false, false, Updatability.READONLY, "TRUE if object represents latest version of object.", null, null,
               null));

            add(objectType.value(), createPropertyDefinition(CMIS.IS_MAJOR_VERSION, PropertyType.BOOLEAN,
               CMIS.IS_MAJOR_VERSION, CMIS.IS_MAJOR_VERSION, null, CMIS.IS_MAJOR_VERSION, false, false, false, false,
               false, Updatability.WHENCHECKEDOUT, "TRUE if object represents major version of object.", null, null,
               null));

            add(objectType.value(), createPropertyDefinition(CMIS.IS_LATEST_MAJOR_VERSION, PropertyType.BOOLEAN,
               CMIS.IS_LATEST_MAJOR_VERSION, CMIS.IS_LATEST_MAJOR_VERSION, null, CMIS.IS_LATEST_MAJOR_VERSION, false,
               false, false, false, false, Updatability.READONLY,
               "TRUE if object represents latest major version of object.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_LABEL, PropertyType.STRING,
               CMIS.VERSION_LABEL, CMIS.VERSION_LABEL, null, CMIS.VERSION_LABEL, false, false, false, false, false,
               Updatability.READONLY, "Version label.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_ID, PropertyType.ID,
               CMIS.VERSION_SERIES_ID, CMIS.VERSION_SERIES_ID, null, CMIS.VERSION_SERIES_ID, false, false, false,
               false, false, Updatability.READONLY, "ID of version series.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.IS_VERSION_SERIES_CHECKED_OUT, PropertyType.BOOLEAN,
               CMIS.IS_VERSION_SERIES_CHECKED_OUT, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null,
               CMIS.IS_VERSION_SERIES_CHECKED_OUT, false, false, false, false, false, Updatability.READONLY,
               "TRUE if some document in version series is checkedout.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_BY, PropertyType.STRING,
               CMIS.VERSION_SERIES_CHECKED_OUT_BY, CMIS.VERSION_SERIES_CHECKED_OUT_BY, null,
               CMIS.VERSION_SERIES_CHECKED_OUT_BY, false, false, false, false, false, Updatability.READONLY,
               "User who checkedout document.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_ID, PropertyType.ID,
               CMIS.VERSION_SERIES_CHECKED_OUT_ID, CMIS.VERSION_SERIES_CHECKED_OUT_ID, null,
               CMIS.VERSION_SERIES_CHECKED_OUT_ID, false, false, false, false, false, Updatability.READONLY,
               "ID of checkedout document.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.CHECKIN_COMMENT, PropertyType.STRING,
               CMIS.CHECKIN_COMMENT, CMIS.CHECKIN_COMMENT, null, CMIS.CHECKIN_COMMENT, false, false, false, false,
               false, Updatability.WHENCHECKEDOUT, "Check-In comment.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_LENGTH, PropertyType.INTEGER,
               CMIS.CONTENT_STREAM_LENGTH, CMIS.CONTENT_STREAM_LENGTH, null, CMIS.CONTENT_STREAM_LENGTH, false, false,
               false, false, false, Updatability.READONLY, "Length of document content in bytes.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_MIME_TYPE, PropertyType.STRING,
               CMIS.CONTENT_STREAM_MIME_TYPE, CMIS.CONTENT_STREAM_MIME_TYPE, null, CMIS.CONTENT_STREAM_MIME_TYPE,
               false, false, false, false, false, Updatability.READONLY, "Media type of document content.", null, null,
               null));

            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_FILE_NAME, PropertyType.STRING,
               CMIS.CONTENT_STREAM_FILE_NAME, CMIS.CONTENT_STREAM_FILE_NAME, null, CMIS.CONTENT_STREAM_FILE_NAME,
               false, false, false, false, false, Updatability.READONLY, "Document's content file name.", null, null,
               null));

            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_ID, PropertyType.ID,
               CMIS.CONTENT_STREAM_ID, CMIS.CONTENT_STREAM_ID, null, CMIS.CONTENT_STREAM_ID, false, false, false,
               false, false, Updatability.READONLY, "Document's content stream ID.", null, null, null));
         }
         else if (objectType == BaseType.FOLDER)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.PARENT_ID, PropertyType.ID, CMIS.PARENT_ID,
               CMIS.PARENT_ID, null, CMIS.PARENT_ID, false, false, false, false, false, Updatability.READONLY,
               "ID of parent folder.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, true, Updatability.READONLY,
               "Set of allowed child types for folder.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.PATH, PropertyType.STRING, CMIS.PATH, CMIS.PATH,
               null, CMIS.PATH, false, false, false, false, false, Updatability.READONLY,
               "Full path to folder object.", null, null, null));
         }
         else if (objectType == BaseType.POLICY)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.POLICY_TEXT, PropertyType.STRING, CMIS.POLICY_TEXT,
               CMIS.POLICY_TEXT, null, CMIS.POLICY_TEXT, true, false, false, false, false, Updatability.ONCREATE,
               "User-friendly description of the policy.", null, null, null));
         }
         else if (objectType == BaseType.RELATIONSHIP)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.SOURCE_ID, PropertyType.ID, CMIS.SOURCE_ID,
               CMIS.SOURCE_ID, null, CMIS.SOURCE_ID, false, false, false, false, false, Updatability.READONLY,
               "ID of relationship's source object.", null, null, null));

            add(objectType.value(), createPropertyDefinition(CMIS.TARGET_ID, PropertyType.ID, CMIS.TARGET_ID,
               CMIS.TARGET_ID, null, CMIS.TARGET_ID, false, false, false, false, false, Updatability.READONLY,
               "ID of relationship's target object.", null, null, null));
         }
      }
   }

   //   /**
   //    * Get all property definitions for <code>objectTypeId</code>.
   //    *
   //    * @param objectTypeId object type id
   //    * @return set of object property definitions
   //    */
   //   public static Collection<PropertyDefinition<?>> getAll(String objectTypeId)
   //   {
   //      Map<String, PropertyDefinition<?>> defs = all.get(objectTypeId);
   //      if (defs == null)
   //         return Collections.emptyList();
   //      return Collections.unmodifiableCollection(defs.values());
   //   }

   /**
    * Get all property definitions for <code>objectTypeId</code>.
    *
    * @param objectTypeId object type id
    * @return set of object property definitions
    */
   public static Map<String, PropertyDefinition<?>> getAll(String objectTypeId)
   {
      Map<String, PropertyDefinition<?>> defs = all.get(objectTypeId);
      if (defs == null)
      {
         return Collections.emptyMap();
      }
      return Collections.unmodifiableMap(defs);
   }

   /**
    * Get all property IDs supported for <code>objectTypeId</code>.
    *
    * @param objectTypeId object type id
    * @return set of object property definition IDs.
    */
   public static Set<String> getPropertyIds(String objectTypeId)
   {
      Map<String, PropertyDefinition<?>> defs = all.get(objectTypeId);
      if (defs == null)
      {
         return Collections.emptySet();
      }
      return Collections.unmodifiableSet(defs.keySet());
   }

   /**
    * Get one property definition with <code>propDefId</code> for
    * <code>objectTypeId</code>.
    *
    * @param objectTypeId object type id
    * @param propDefId property definition id
    * @return property definition or null
    */
   public static PropertyDefinition<?> getPropertyDefinition(String objectTypeId, String propDefId)
   {
      Map<String, PropertyDefinition<?>> defs = all.get(objectTypeId);
      if (defs == null)
      {
         return null;
      }
      return defs.get(propDefId);
   }

   ///////

   private static void add(String typeId, PropertyDefinition<?> propDef)
   {
      Map<String, PropertyDefinition<?>> defs = all.get(typeId);
      if (defs == null)
      {
         defs = new HashMap<String, PropertyDefinition<?>>();
         all.put(typeId, defs);
      }
      defs.put(propDef.getId(), propDef);
   }

   private static <T> PropertyDefinition<T> createPropertyDefinition(String id, PropertyType propertyType,
      String queryName, String localName, String localNamespace, String displayName, boolean required,
      boolean queryable, boolean orderable, boolean inherited, boolean isMultivalued, Updatability updatability,
      String description, Boolean openChoice, List<Choice<T>> choices, T[] defValue)
   {
      PropertyDefinition<T> propertyDefinition =
         new PropertyDefinitionImpl<T>(id, queryName, localName, localNamespace, displayName, description,
            propertyType, updatability, inherited, required, queryable, orderable, openChoice, isMultivalued, choices,
            defValue);

      return propertyDefinition;
   }

   /**
    * Not instantiable.
    */
   private PropertyDefinitions()
   {
   }
}
