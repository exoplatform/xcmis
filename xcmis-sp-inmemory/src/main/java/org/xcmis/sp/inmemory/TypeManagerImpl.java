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
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumDateTimeResolution;
import org.xcmis.core.EnumPropertiesBase;
import org.xcmis.core.EnumPropertiesDocument;
import org.xcmis.core.EnumPropertiesFolder;
import org.xcmis.core.EnumPropertiesPolicy;
import org.xcmis.core.EnumPropertiesRelationship;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypeManagerImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class TypeManagerImpl implements org.xcmis.spi.TypeManager
{

   /** The map ID > types. */
   protected final Map<String, CmisTypeDefinitionType> types;

   /** The map ID > PropertyDefinitions. */
   protected final Map<String, List<CmisPropertyDefinitionType>> propertyDefinitions;

   /**
    * Instantiates a new type manager impl.
    */
   public TypeManagerImpl()
   {
      types = new ConcurrentHashMap<String, CmisTypeDefinitionType>();
      propertyDefinitions = new ConcurrentHashMap<String, List<CmisPropertyDefinitionType>>();
      init();
   }

   /**
    * {@inheritDoc}
    */
   public void addType(CmisTypeDefinitionType type) throws RepositoryException
   {
      if (type.getBaseId() == null)
         throw new InvalidArgumentException("Base type id must be specified.");
      if (type.getParentId() == null)
         throw new InvalidArgumentException("Unable add root type. Parent type id must be specified");
      if (types.get(type.getId()) != null)
         throw new RepositoryException("Type " + type.getId() + " already exists.");
      // Check new type does not use known property IDs.
      for (CmisPropertyDefinitionType pd : type.getPropertyDefinition())
      {
         // TODO : can get smarter ??
         if (pd.getId() == null)
            throw new InvalidArgumentException("Properties ID not found.");
         for (EnumPropertiesBase i : EnumPropertiesBase.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesDocument i : EnumPropertiesDocument.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesFolder i : EnumPropertiesFolder.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesPolicy i : EnumPropertiesPolicy.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesRelationship i : EnumPropertiesRelationship.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
      }

      List<CmisPropertyDefinitionType> list = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(type.getBaseId(), list);
      list.addAll(type.getPropertyDefinition());
      propertyDefinitions.put(type.getId(), Collections.unmodifiableList(list));

      // NOTE: keeps property definitions in separate map.
      type.getPropertyDefinition().clear();
      types.put(type.getId(), type);
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId) throws TypeNotFoundException, RepositoryException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException
   {
      CmisTypeDefinitionType type = types.get(typeId);
      if (type == null)
         throw new TypeNotFoundException("Type not found " + typeId);

      // Source types is untouchable.
      CmisTypeDefinitionType copy = getCopy(type);
      if (includePropertyDefinition)
         copy.getPropertyDefinition().addAll(propertyDefinitions.get(typeId));
      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, RepositoryException
   {
      getTypeDefinition(typeId, false); // Throws exception if type is not exists.
      types.remove(typeId);
      propertyDefinitions.remove(typeId);
   }

   /**
    * Commons property definitions.
    * 
    * @param type the type
    * @param list the list
    */
   private void commonsPropertyDefinitions(EnumBaseObjectTypeIds type, List<CmisPropertyDefinitionType> list)
   {
      list.add(propertyDefinition(CMIS.BASE_TYPE_ID, EnumPropertyType.ID, CMIS.BASE_TYPE_ID, CMIS.BASE_TYPE_ID, null,
         CMIS.BASE_TYPE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Base type id.", null, null, null));
      list.add(propertyDefinition(CMIS.OBJECT_TYPE_ID, EnumPropertyType.ID, CMIS.OBJECT_TYPE_ID, CMIS.OBJECT_TYPE_ID,
         null, CMIS.OBJECT_TYPE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Object type id.", null, null, null));
      list.add(propertyDefinition(CMIS.OBJECT_ID, EnumPropertyType.ID, CMIS.OBJECT_ID, CMIS.OBJECT_ID, null,
         CMIS.OBJECT_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Object id.",
         null, null, null));
      list.add(propertyDefinition(CMIS.NAME, EnumPropertyType.STRING, CMIS.NAME, CMIS.NAME, null, CMIS.NAME, true,
         false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READWRITE, "Object name.", true, null, null));
      list.add(propertyDefinition(CMIS.CREATED_BY, EnumPropertyType.STRING, CMIS.CREATED_BY, CMIS.CREATED_BY, null,
         CMIS.CREATED_BY, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "User who created the object.", null, null, null));
      list.add(propertyDefinition(CMIS.CREATION_DATE, EnumPropertyType.DATETIME, CMIS.CREATION_DATE,
         CMIS.CREATION_DATE, null, CMIS.CREATION_DATE, false, false, false, false, EnumCardinality.SINGLE,
         EnumUpdatability.READONLY, "DateTime when the object was created.", null, null, null));
      list.add(propertyDefinition(CMIS.LAST_MODIFIED_BY, EnumPropertyType.STRING, CMIS.LAST_MODIFIED_BY,
         CMIS.LAST_MODIFIED_BY, null, CMIS.LAST_MODIFIED_BY, false, false, false, false, EnumCardinality.SINGLE,
         EnumUpdatability.READONLY, "User who last modified the object.", null, null, null));
      list.add(propertyDefinition(CMIS.LAST_MODIFICATION_DATE, EnumPropertyType.DATETIME, CMIS.LAST_MODIFICATION_DATE,
         CMIS.LAST_MODIFICATION_DATE, null, CMIS.LAST_MODIFICATION_DATE, false, false, false, false,
         EnumCardinality.SINGLE, EnumUpdatability.READONLY, "DateTime when the object was last modified.", null, null,
         null));
      list.add(propertyDefinition(CMIS.CHANGE_TOKEN, EnumPropertyType.STRING, CMIS.CHANGE_TOKEN, CMIS.CHANGE_TOKEN,
         null, CMIS.CHANGE_TOKEN, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Opaque token used for optimistic locking.", null, null, null));

      if (type == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         list.add(propertyDefinition(CMIS.IS_IMMUTABLE, EnumPropertyType.BOOLEAN, CMIS.IS_IMMUTABLE, CMIS.IS_IMMUTABLE,
            null, CMIS.IS_IMMUTABLE, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "TRUE if the repository MUST throw an error at any attempt to update or delete the object.", null, null,
            null));
         list.add(propertyDefinition(CMIS.IS_LATEST_VERSION, EnumPropertyType.BOOLEAN, CMIS.IS_LATEST_VERSION,
            CMIS.IS_LATEST_VERSION, null, CMIS.IS_LATEST_VERSION, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if object represents latest version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_MAJOR_VERSION, EnumPropertyType.BOOLEAN, CMIS.IS_MAJOR_VERSION,
            CMIS.IS_MAJOR_VERSION, null, CMIS.IS_MAJOR_VERSION, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if object represents major version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_LATEST_MAJOR_VERSION, EnumPropertyType.BOOLEAN,
            CMIS.IS_LATEST_MAJOR_VERSION, CMIS.IS_LATEST_MAJOR_VERSION, null, CMIS.IS_LATEST_MAJOR_VERSION, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "TRUE if object represents latest major version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_LABEL, EnumPropertyType.STRING, CMIS.VERSION_LABEL,
            CMIS.VERSION_LABEL, null, CMIS.VERSION_LABEL, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Version label.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_ID, EnumPropertyType.ID, CMIS.VERSION_SERIES_ID,
            CMIS.VERSION_SERIES_ID, null, CMIS.VERSION_SERIES_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "ID of version series.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_VERSION_SERIES_CHECKED_OUT, EnumPropertyType.BOOLEAN,
            CMIS.IS_VERSION_SERIES_CHECKED_OUT, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null,
            CMIS.IS_VERSION_SERIES_CHECKED_OUT, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if some document in version series is checkedout.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_BY, EnumPropertyType.STRING,
            CMIS.VERSION_SERIES_CHECKED_OUT_BY, CMIS.VERSION_SERIES_CHECKED_OUT_BY, null,
            CMIS.VERSION_SERIES_CHECKED_OUT_BY, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "User who checkedout document.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_ID, EnumPropertyType.ID,
            CMIS.VERSION_SERIES_CHECKED_OUT_ID, CMIS.VERSION_SERIES_CHECKED_OUT_ID, null,
            CMIS.VERSION_SERIES_CHECKED_OUT_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "ID of checkedout document.", null, null, null));
         list.add(propertyDefinition(CMIS.CHECKIN_COMMENT, EnumPropertyType.STRING, CMIS.CHECKIN_COMMENT,
            CMIS.CHECKIN_COMMENT, null, CMIS.CHECKIN_COMMENT, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Check-In comment.", null, null, null));
         list
            .add(propertyDefinition(CMIS.CONTENT_STREAM_LENGTH, EnumPropertyType.INTEGER, CMIS.CONTENT_STREAM_LENGTH,
               CMIS.CONTENT_STREAM_LENGTH, null, CMIS.CONTENT_STREAM_LENGTH, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Length of document content in bytes.", null, null,
               null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_MIME_TYPE, EnumPropertyType.STRING,
            CMIS.CONTENT_STREAM_MIME_TYPE, CMIS.CONTENT_STREAM_MIME_TYPE, null, CMIS.CONTENT_STREAM_MIME_TYPE, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Media type of document content.",
            null, null, null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_FILE_NAME, EnumPropertyType.STRING,
            CMIS.CONTENT_STREAM_FILE_NAME, CMIS.CONTENT_STREAM_FILE_NAME, null, CMIS.CONTENT_STREAM_FILE_NAME, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Document's content file name.",
            null, null, null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_ID, EnumPropertyType.ID, CMIS.CONTENT_STREAM_ID,
            CMIS.CONTENT_STREAM_ID, null, CMIS.CONTENT_STREAM_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Document's content stream ID.", null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         list.add(propertyDefinition(CMIS.PARENT_ID, EnumPropertyType.ID, CMIS.PARENT_ID, CMIS.PARENT_ID, null,
            CMIS.PARENT_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of parent folder.", null, null, null));
         list.add(propertyDefinition(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, EnumPropertyType.ID,
            CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
            CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, EnumCardinality.MULTI,
            EnumUpdatability.READONLY, "Set of allowed child types for folder.", null, null, null));
         list.add(propertyDefinition(CMIS.PATH, EnumPropertyType.STRING, CMIS.PATH, CMIS.PATH, null, CMIS.PATH, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Full path to folder object.",
            null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_POLICY)
      {
         list.add(propertyDefinition(CMIS.POLICY_TEXT, EnumPropertyType.STRING, CMIS.POLICY_TEXT, CMIS.POLICY_TEXT,
            null, CMIS.POLICY_TEXT, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "User-friendly description of the policy.", null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
      {
         list.add(propertyDefinition(CMIS.SOURCE_ID, EnumPropertyType.ID, CMIS.SOURCE_ID, CMIS.SOURCE_ID, null,
            CMIS.SOURCE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of relationship's source object.", null, null, null));
         list.add(propertyDefinition(CMIS.TARGET_ID, EnumPropertyType.ID, CMIS.TARGET_ID, CMIS.TARGET_ID, null,
            CMIS.TARGET_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of relationship's target object.", null, null, null));
      }
   }

   /**
    * Property definition.
    * 
    * @param id the id
    * @param propertyType the property type
    * @param queryName the query name
    * @param localName the local name
    * @param localNamespace the local namespace
    * @param displayName the display name
    * @param required the required
    * @param queryable the queryable
    * @param orderable the orderable
    * @param inherited the inherited
    * @param cardinality the cardinality
    * @param updatability the updatability
    * @param description the description
    * @param openChoice the open choice
    * @param choices the choices
    * @param defValue the def value
    * 
    * @return the cmis property definition type
    */
   @SuppressWarnings("unchecked")
   private <T extends CmisChoice, V extends CmisProperty> CmisPropertyDefinitionType propertyDefinition(//
      String id, //
      EnumPropertyType propertyType, //
      String queryName, //
      String localName, //
      String localNamespace, //
      String displayName, //
      boolean required, //
      boolean queryable, //
      boolean orderable, //
      boolean inherited, //
      EnumCardinality cardinality, //
      EnumUpdatability updatability, // 
      String description, //
      Boolean openChoice, //
      List<T> choices, //
      V defValue //
   )
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
    * Init type manager.
    */
   protected void init()
   {
      CmisTypeDocumentDefinitionType docType = new CmisTypeDocumentDefinitionType();
      docType.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      docType.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      docType.setControllableACL(true);
      docType.setControllablePolicy(true);
      docType.setCreatable(true);
      docType.setDescription("Cmis Document Type");
      docType.setDisplayName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setFileable(true);
      docType.setFulltextIndexed(true);
      docType.setId(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setIncludedInSupertypeQuery(true);
      docType.setLocalName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setParentId(null);
      docType.setQueryable(true);
      docType.setQueryName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setVersionable(true);
      // Document's property definitions.
      List<CmisPropertyDefinitionType> docPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_DOCUMENT, docPropDef);
      propertyDefinitions.put(docType.getId(), Collections.unmodifiableList(docPropDef));
      types.put(docType.getId(), docType);

      CmisTypeFolderDefinitionType folderType = new CmisTypeFolderDefinitionType();
      folderType.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      folderType.setControllableACL(true);
      folderType.setControllablePolicy(true);
      folderType.setCreatable(true);
      folderType.setDescription("Cmis Folder Type");
      folderType.setDisplayName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setFileable(true);
      folderType.setFulltextIndexed(false);
      folderType.setId(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setIncludedInSupertypeQuery(true);
      folderType.setLocalName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setParentId(null);
      folderType.setQueryable(true);
      folderType.setQueryName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // Folder's property definitions.
      List<CmisPropertyDefinitionType> folderPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_FOLDER, folderPropDef);
      propertyDefinitions.put(folderType.getId(), Collections.unmodifiableList(folderPropDef));
      types.put(folderType.getId(), folderType);

      CmisTypePolicyDefinitionType policyType = new CmisTypePolicyDefinitionType();
      policyType.setBaseId(EnumBaseObjectTypeIds.CMIS_POLICY);
      policyType.setControllableACL(true);
      policyType.setControllablePolicy(true);
      policyType.setCreatable(true);
      policyType.setDescription("Cmis Policy Type");
      policyType.setDisplayName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setFileable(true);
      policyType.setFulltextIndexed(false);
      policyType.setId(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setIncludedInSupertypeQuery(true);
      policyType.setLocalName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setParentId(null);
      policyType.setQueryable(false);
      policyType.setQueryName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // Policy property definitions.
      List<CmisPropertyDefinitionType> policyPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_POLICY, policyPropDef);
      propertyDefinitions.put(policyType.getId(), Collections.unmodifiableList(policyPropDef));
      types.put(policyType.getId(), policyType);

      CmisTypeRelationshipDefinitionType relationshipType = new CmisTypeRelationshipDefinitionType();
      relationshipType.setBaseId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      relationshipType.setControllableACL(false);
      relationshipType.setControllablePolicy(false);
      relationshipType.setCreatable(true);
      relationshipType.setDescription("Cmis Relationship Type");
      relationshipType.setDisplayName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setFileable(false);
      relationshipType.setFulltextIndexed(false);
      relationshipType.setId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setIncludedInSupertypeQuery(false);
      relationshipType.setLocalName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setParentId(null);
      relationshipType.setQueryable(false);
      relationshipType.setQueryName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // Relationship's property definitions.
      List<CmisPropertyDefinitionType> relationshipPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, relationshipPropDef);
      propertyDefinitions.put(relationshipType.getId(), Collections.unmodifiableList(relationshipPropDef));
      types.put(relationshipType.getId(), relationshipType);
   }

   private CmisTypeDefinitionType getCopy(CmisTypeDefinitionType orig)
   {
      CmisTypeDefinitionType copy = null;
      if (orig instanceof CmisTypeDocumentDefinitionType)
      {
         CmisTypeDocumentDefinitionType docDef = new CmisTypeDocumentDefinitionType();
         docDef.setVersionable(((CmisTypeDocumentDefinitionType)orig).isVersionable());
         docDef.setContentStreamAllowed(((CmisTypeDocumentDefinitionType)orig).getContentStreamAllowed());
         copy = docDef;
      }
      else if (orig instanceof CmisTypeFolderDefinitionType)
      {
         CmisTypeFolderDefinitionType folderDef = new CmisTypeFolderDefinitionType();
         copy = folderDef;
      }
      else if (orig instanceof CmisTypePolicyDefinitionType)
      {
         CmisTypePolicyDefinitionType policyDef = new CmisTypePolicyDefinitionType();
         copy = policyDef;
      }
      else if (orig instanceof CmisTypeRelationshipDefinitionType)
      {
         CmisTypeRelationshipDefinitionType relspDef = new CmisTypeRelationshipDefinitionType();
         relspDef.getAllowedSourceTypes().addAll(((CmisTypeRelationshipDefinitionType)orig).getAllowedSourceTypes());
         relspDef.getAllowedTargetTypes().addAll(((CmisTypeRelationshipDefinitionType)orig).getAllowedTargetTypes());
         copy = relspDef;
      }
      else
      {
         // Must never happen.
         copy = new CmisTypeDefinitionType();
      }

      copy.setId(orig.getId());
      copy.setLocalName(orig.getLocalName());
      copy.setLocalNamespace(orig.getLocalNamespace());
      copy.setDisplayName(orig.getDisplayName());
      copy.setQueryName(orig.getQueryName());
      copy.setDescription(orig.getDescription());
      copy.setBaseId(orig.getBaseId());
      copy.setParentId(orig.getParentId());
      copy.setCreatable(orig.isCreatable());
      copy.setFileable(orig.isFileable());
      copy.setQueryable(orig.isQueryable());
      copy.setFulltextIndexed(orig.isFulltextIndexed());
      copy.setIncludedInSupertypeQuery(orig.isIncludedInSupertypeQuery());
      copy.setControllablePolicy(orig.isControllablePolicy());
      copy.setControllableACL(orig.isControllableACL());
      copy.getAny().addAll(orig.getAny());
      return copy;
   }

}
