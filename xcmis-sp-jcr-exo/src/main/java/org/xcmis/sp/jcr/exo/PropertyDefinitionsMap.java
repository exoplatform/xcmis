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

package org.xcmis.sp.jcr.exo;

import org.xcmis.core.CmisChoice;
import org.xcmis.core.CmisChoiceBoolean;
import org.xcmis.core.CmisChoiceDateTime;
import org.xcmis.core.CmisChoiceDecimal;
import org.xcmis.core.CmisChoiceHtml;
import org.xcmis.core.CmisChoiceId;
import org.xcmis.core.CmisChoiceInteger;
import org.xcmis.core.CmisChoiceString;
import org.xcmis.core.CmisChoiceUri;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyBooleanDefinitionType;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyHtml;
import org.xcmis.core.CmisPropertyHtmlDefinitionType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyIdDefinitionType;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisPropertyUri;
import org.xcmis.core.CmisPropertyUriDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumDateTimeResolution;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.CMIS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping for known CMIS object properties.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyDefinitionsMap.java 2 2010-02-04 17:21:49Z andrew00x $
 */
final class PropertyDefinitionsMap
{

   private static final Map<String, Map<String, CmisPropertyDefinitionType>> all =
      new HashMap<String, Map<String, CmisPropertyDefinitionType>>();

   static
   {
      for (EnumBaseObjectTypeIds objectType : EnumBaseObjectTypeIds.values())
      {
         // Common properties.
         add(objectType.value(), createPropertyDefinition(CMIS.BASE_TYPE_ID, EnumPropertyType.ID, CMIS.BASE_TYPE_ID,
            CMIS.BASE_TYPE_ID, null, CMIS.BASE_TYPE_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Base type id.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.OBJECT_TYPE_ID, EnumPropertyType.ID,
            CMIS.OBJECT_TYPE_ID, CMIS.OBJECT_TYPE_ID, null, CMIS.OBJECT_TYPE_ID, false, false, false, false,
            EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Object type id.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.OBJECT_ID, EnumPropertyType.ID, CMIS.OBJECT_ID,
            CMIS.OBJECT_ID, null, CMIS.OBJECT_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Object id.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.NAME, EnumPropertyType.STRING, CMIS.NAME, CMIS.NAME,
            null, CMIS.NAME, true, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READWRITE,
            "Object name.", true, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.CREATED_BY, EnumPropertyType.STRING, CMIS.CREATED_BY,
            CMIS.CREATED_BY, null, CMIS.CREATED_BY, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "User who created the object.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.CREATION_DATE, EnumPropertyType.DATETIME,
            CMIS.CREATION_DATE, CMIS.CREATION_DATE, null, CMIS.CREATION_DATE, false, false, false, false,
            EnumCardinality.SINGLE, EnumUpdatability.READONLY, "DateTime when the object was created.", null, null,
            null));
         add(objectType.value(), createPropertyDefinition(CMIS.LAST_MODIFIED_BY, EnumPropertyType.STRING,
            CMIS.LAST_MODIFIED_BY, CMIS.LAST_MODIFIED_BY, null, CMIS.LAST_MODIFIED_BY, false, false, false, false,
            EnumCardinality.SINGLE, EnumUpdatability.READONLY, "User who last modified the object.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.LAST_MODIFICATION_DATE, EnumPropertyType.DATETIME,
            CMIS.LAST_MODIFICATION_DATE, CMIS.LAST_MODIFICATION_DATE, null, CMIS.LAST_MODIFICATION_DATE, false, false,
            false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "DateTime when the object was last modified.", null, null, null));
         add(objectType.value(), createPropertyDefinition(CMIS.CHANGE_TOKEN, EnumPropertyType.STRING,
            CMIS.CHANGE_TOKEN, CMIS.CHANGE_TOKEN, null, CMIS.CHANGE_TOKEN, false, false, false, false,
            EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Opaque token used for optimistic locking.", null, null,
            null));

         // Type specific.
         if (objectType == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.IS_IMMUTABLE, EnumPropertyType.BOOLEAN,
               CMIS.IS_IMMUTABLE, CMIS.IS_IMMUTABLE, null, CMIS.IS_IMMUTABLE, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "TRUE if the repository MUST throw an error at any attempt to update or delete the object.", null, null,
               null));
            add(objectType.value(), createPropertyDefinition(CMIS.IS_LATEST_VERSION, EnumPropertyType.BOOLEAN,
               CMIS.IS_LATEST_VERSION, CMIS.IS_LATEST_VERSION, null, CMIS.IS_LATEST_VERSION, false, false, false,
               false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "TRUE if object represents latest version of object.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.IS_MAJOR_VERSION, EnumPropertyType.BOOLEAN,
               CMIS.IS_MAJOR_VERSION, CMIS.IS_MAJOR_VERSION, null, CMIS.IS_MAJOR_VERSION, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY, "TRUE if object represents major version of object.",
               null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.IS_LATEST_MAJOR_VERSION, EnumPropertyType.BOOLEAN,
               CMIS.IS_LATEST_MAJOR_VERSION, CMIS.IS_LATEST_MAJOR_VERSION, null, CMIS.IS_LATEST_MAJOR_VERSION, false,
               false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "TRUE if object represents latest major version of object.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_LABEL, EnumPropertyType.STRING,
               CMIS.VERSION_LABEL, CMIS.VERSION_LABEL, null, CMIS.VERSION_LABEL, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Version label.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_ID, EnumPropertyType.ID,
               CMIS.VERSION_SERIES_ID, CMIS.VERSION_SERIES_ID, null, CMIS.VERSION_SERIES_ID, false, false, false,
               false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "ID of version series.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.IS_VERSION_SERIES_CHECKED_OUT,
               EnumPropertyType.BOOLEAN, CMIS.IS_VERSION_SERIES_CHECKED_OUT, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null,
               CMIS.IS_VERSION_SERIES_CHECKED_OUT, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "TRUE if some document in version series is checkedout.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_BY,
               EnumPropertyType.STRING, CMIS.VERSION_SERIES_CHECKED_OUT_BY, CMIS.VERSION_SERIES_CHECKED_OUT_BY, null,
               CMIS.VERSION_SERIES_CHECKED_OUT_BY, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "User who checkedout document.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_ID, EnumPropertyType.ID,
               CMIS.VERSION_SERIES_CHECKED_OUT_ID, CMIS.VERSION_SERIES_CHECKED_OUT_ID, null,
               CMIS.VERSION_SERIES_CHECKED_OUT_ID, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "ID of checkedout document.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.CHECKIN_COMMENT, EnumPropertyType.STRING,
               CMIS.CHECKIN_COMMENT, CMIS.CHECKIN_COMMENT, null, CMIS.CHECKIN_COMMENT, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Check-In comment.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_LENGTH, EnumPropertyType.INTEGER,
               CMIS.CONTENT_STREAM_LENGTH, CMIS.CONTENT_STREAM_LENGTH, null, CMIS.CONTENT_STREAM_LENGTH, false, false,
               false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Length of document content in bytes.",
               null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_MIME_TYPE, EnumPropertyType.STRING,
               CMIS.CONTENT_STREAM_MIME_TYPE, CMIS.CONTENT_STREAM_MIME_TYPE, null, CMIS.CONTENT_STREAM_MIME_TYPE,
               false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "Media type of document content.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_FILE_NAME, EnumPropertyType.STRING,
               CMIS.CONTENT_STREAM_FILE_NAME, CMIS.CONTENT_STREAM_FILE_NAME, null, CMIS.CONTENT_STREAM_FILE_NAME,
               false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "Document's content file name.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.CONTENT_STREAM_ID, EnumPropertyType.ID,
               CMIS.CONTENT_STREAM_ID, CMIS.CONTENT_STREAM_ID, null, CMIS.CONTENT_STREAM_ID, false, false, false,
               false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Document's content stream ID.", null, null,
               null));
         }
         else if (objectType == EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.PARENT_ID, EnumPropertyType.ID, CMIS.PARENT_ID,
               CMIS.PARENT_ID, null, CMIS.PARENT_ID, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "ID of parent folder.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, EnumPropertyType.ID,
               CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, EnumCardinality.MULTI,
               EnumUpdatability.READONLY, "Set of allowed child types for folder.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.PATH, EnumPropertyType.STRING, CMIS.PATH, CMIS.PATH,
               null, CMIS.PATH, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
               "Full path to folder object.", null, null, null));
         }
         else if (objectType == EnumBaseObjectTypeIds.CMIS_POLICY)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.POLICY_TEXT, EnumPropertyType.STRING,
               CMIS.POLICY_TEXT, CMIS.POLICY_TEXT, null, CMIS.POLICY_TEXT, true, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.ONCREATE, "User-friendly description of the policy.", null,
               null, null));
         }
         else if (objectType == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
         {
            add(objectType.value(), createPropertyDefinition(CMIS.SOURCE_ID, EnumPropertyType.ID, CMIS.SOURCE_ID,
               CMIS.SOURCE_ID, null, CMIS.SOURCE_ID, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "ID of relationship's source object.", null, null, null));
            add(objectType.value(), createPropertyDefinition(CMIS.TARGET_ID, EnumPropertyType.ID, CMIS.TARGET_ID,
               CMIS.TARGET_ID, null, CMIS.TARGET_ID, false, false, false, false, EnumCardinality.SINGLE,
               EnumUpdatability.READONLY, "ID of relationship's target object.", null, null, null));
         }
      }
   }

   /**
    * Get all property definitions for <code>objectTypeId</code>.
    * 
    * @param objectTypeId object type id
    * @return set of object property definitions
    */
   public static Collection<CmisPropertyDefinitionType> getAll(String objectTypeId)
   {
      Map<String, CmisPropertyDefinitionType> defs = all.get(objectTypeId);
      if (defs == null)
         return Collections.emptyList();
      return Collections.unmodifiableCollection(defs.values());
   }

   /**
    * Get all property IDs supported for <code>objectTypeId</code>.
    * 
    * @param objectTypeId object type id
    * @return set of object property definition IDs.
    */
   public static Set<String> getPropertyIds(String objectTypeId)
   {
      Map<String, CmisPropertyDefinitionType> defs = all.get(objectTypeId);
      if (defs == null)
         return Collections.emptySet();
      return Collections.unmodifiableSet(defs.keySet());
   }

   /**
    * Get one property definition with <code>propDefId</code> for <code>objectTypeId</code>.
    * 
    * @param objectTypeId object type id
    * @param propDefId property definition id
    * @return property definition or null 
    */
   public static CmisPropertyDefinitionType getPropertyDefinition(String objectTypeId, String propDefId)
   {
      Map<String, CmisPropertyDefinitionType> defs = all.get(objectTypeId);
      if (defs == null)
         return null;
      return defs.get(propDefId);
   }

   private static void add(String typeId, CmisPropertyDefinitionType propDef)
   {
      Map<String, CmisPropertyDefinitionType> defs = all.get(typeId);
      if (defs == null)
      {
         defs = new HashMap<String, CmisPropertyDefinitionType>();
         all.put(typeId, defs);
      }
      defs.put(propDef.getId(), propDef);
   }

   @SuppressWarnings("unchecked")
   private static <T extends CmisChoice, V extends CmisProperty> CmisPropertyDefinitionType createPropertyDefinition(
      String id, EnumPropertyType propertyType, String queryName, String localName, String localNamespace,
      String displayName, boolean required, boolean queryable, boolean orderable, boolean inherited,
      EnumCardinality cardinality, EnumUpdatability updatability, String description, Boolean openChoice,
      List<T> choices, V defValue)
   {
      CmisPropertyDefinitionType def = null;
      // property type specific.
      switch (propertyType)
      {
         case BOOLEAN :
            CmisPropertyBooleanDefinitionType bool = new CmisPropertyBooleanDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               bool.setOpenChoice(openChoice);
               bool.getChoice().addAll((List<CmisChoiceBoolean>)choices);
            }
            bool.setDefaultValue((CmisPropertyBoolean)defValue);
            def = bool;
            break;
         case DATETIME :
            CmisPropertyDateTimeDefinitionType date = new CmisPropertyDateTimeDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               date.setOpenChoice(openChoice);
               date.getChoice().addAll((List<CmisChoiceDateTime>)choices);
            }
            date.setDefaultValue((CmisPropertyDateTime)defValue);
            date.setResolution(EnumDateTimeResolution.TIME);
            def = date;
            break;
         case DECIMAL :
            CmisPropertyDecimalDefinitionType dec = new CmisPropertyDecimalDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               dec.setOpenChoice(openChoice);
               dec.getChoice().addAll((List<CmisChoiceDecimal>)choices);
            }
            dec.setDefaultValue((CmisPropertyDecimal)defValue);
            dec.setMaxValue(CMIS.MAX_DECIMAL_VALUE);
            dec.setMinValue(CMIS.MIN_DECIMAL_VALUE);
            dec.setPrecision(CMIS.PRECISION);
            def = dec;
            break;
         case HTML :
            CmisPropertyHtmlDefinitionType html = new CmisPropertyHtmlDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               html.setOpenChoice(openChoice);
               html.getChoice().addAll((List<CmisChoiceHtml>)choices);
            }
            html.setDefaultValue((CmisPropertyHtml)defValue);
            def = html;
            break;
         case ID :
            CmisPropertyIdDefinitionType i = new CmisPropertyIdDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               i.setOpenChoice(openChoice);
               i.getChoice().addAll((List<CmisChoiceId>)choices);
            }
            i.setDefaultValue((CmisPropertyId)defValue);
            def = i;
            break;
         case INTEGER :
            CmisPropertyIntegerDefinitionType integ = new CmisPropertyIntegerDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               integ.setOpenChoice(openChoice);
               integ.getChoice().addAll((List<CmisChoiceInteger>)choices);
            }
            integ.setDefaultValue((CmisPropertyInteger)defValue);
            integ.setMaxValue(CMIS.MAX_INTEGER_VALUE);
            integ.setMinValue(CMIS.MIN_INTEGER_VALUE);
            def = integ;
            break;
         case STRING :
            CmisPropertyStringDefinitionType str = new CmisPropertyStringDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               str.setOpenChoice(openChoice);
               str.getChoice().addAll((List<CmisChoiceString>)choices);
            }
            str.setDefaultValue((CmisPropertyString)defValue);
            str.setMaxLength(CMIS.MAX_STRING_LENGTH);
            def = str;
            break;
         case URI :
            CmisPropertyUriDefinitionType uri = new CmisPropertyUriDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               uri.setOpenChoice(openChoice);
               uri.getChoice().addAll((List<CmisChoiceUri>)choices);
            }
            uri.setDefaultValue((CmisPropertyUri)defValue);
            def = uri;
            break;
      }
      // commons
      def.setCardinality(cardinality);
      def.setDescription(description);
      def.setDisplayName(displayName);
      def.setId(id);
      def.setInherited(inherited);
      def.setLocalName(localName);
      def.setLocalNamespace(localNamespace);
      def.setOrderable(orderable);
      def.setPropertyType(propertyType);
      def.setQueryable(queryable);
      def.setQueryName(queryName);
      def.setRequired(required);
      def.setUpdatability(updatability);
      return def;
   }

   /**
    * Not instantiable.
    */
   private PropertyDefinitionsMap()
   {
   }
}
