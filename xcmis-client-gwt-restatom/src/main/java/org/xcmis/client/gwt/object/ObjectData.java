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
package org.xcmis.client.gwt.object;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.model.restatom.AtomEntry;
import org.xcmis.client.gwt.object.impl.CmisDocumentImpl;
import org.xcmis.client.gwt.object.impl.CmisFolderImpl;
import org.xcmis.client.gwt.object.impl.CmisPolicyImpl;
import org.xcmis.client.gwt.object.impl.CmisRelationshipImpl;
import org.xcmis.client.gwt.object.impl.ObjectInfo;
import org.xcmis.client.gwt.rest.ServerException;
import org.xcmis.client.gwt.rest.UnmarshallerException;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ${date} ${time} $
 *
 */
public class ObjectData
{

   /**
    * @param entryList list of entries
    * @throws UnmarshallerException 
    */
   public static void extractData(List<AtomEntry> entryList) throws UnmarshallerException
   {
      for (AtomEntry entry : entryList)
      {
         extractData(entry);
      }
   }

   /**
    * @param entry entry
    * @throws ServerException 
    * @throws UnmarshallerException 
    */
   public static void extractData(AtomEntry entry) throws UnmarshallerException
   {
      CmisObject object = entry.getObject();

      ObjectInfo objectInfo = new ObjectInfo();
      String baseTypeId = object.getProperties().getId(CMIS.CMIS_BASE_TYPE_ID);

      try
      {
         objectInfo.setBaseType(EnumBaseObjectTypeIds.fromValue(baseTypeId));
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Base object type must not be empty.");
      }
      objectInfo.setTypeId(object.getProperties().getId(CMIS.CMIS_OBJECT_TYPE_ID));
      objectInfo.setId(object.getProperties().getId(CMIS.CMIS_OBJECT_ID));
      objectInfo.setName(object.getProperties().getString(CMIS.CMIS_NAME));
      objectInfo.setCreatedBy(object.getProperties().getString(CMIS.CMIS_CREATED_BY));
      objectInfo.setCreationDate(object.getProperties().getDate(CMIS.CMIS_CREATION_DATE));
      objectInfo.setLastModifiedBy(object.getProperties().getString(CMIS.CMIS_LAST_MODIFIED_BY));
      objectInfo.setLastModificationDate(object.getProperties().getDate(CMIS.CMIS_LAST_MODIFICATION_DATE));
      objectInfo.setChangeToken(object.getProperties().getString(CMIS.CMIS_CHANGE_TOKEN));

      // Object is  cmis:folder - create instance of Folder
      if (baseTypeId != null && objectInfo.getBaseType().equals(EnumBaseObjectTypeIds.CMIS_FOLDER))
      {
         CmisFolderImpl newObject = new CmisFolderImpl(object, object.getProperties().getId(CMIS.CMIS_PARENT_ID));
         entry.setObject(newObject);
      }
      // Object is cmis:document - create instance of Document
      else if (baseTypeId != null && objectInfo.getBaseType().equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT))
      {
         CmisDocumentImpl newObject = new CmisDocumentImpl(object);
         newObject.setLatestVersion(object.getProperties().getBoolean(CMIS.CMIS_IS_LATEST_VERSION));
         newObject.setMajorVersion(object.getProperties().getBoolean(CMIS.CMIS_IS_MAJOR_VERSION));
         newObject.setLatestMajorVersion(object.getProperties().getBoolean(CMIS.CMIS_IS_LATEST_MAJOR_VERSION));
         newObject
            .setVersionSeriesCheckedOut(object.getProperties().getBoolean(CMIS.CMIS_IS_VERSION_SERIES_CHECKEDOUT));
         newObject.setVersionSeriesId(object.getProperties().getString(CMIS.CMIS_VERSION_SERIES_ID));
         newObject.setVersionSeriesCheckedOutId(object.getProperties()
            .getString(CMIS.CMIS_VERSION_SERIES_CHECKEDOUT_ID));
         newObject.setVersionSeriesCheckedOutBy(object.getProperties()
            .getString(CMIS.CMIS_VERSION_SERIES_CHECKEDOUT_BY));
         newObject.setVersionLabel(object.getProperties().getString(CMIS.CMIS_VERSION_LABEL));
         newObject.setContentStreamMimeType(object.getProperties().getString(CMIS.CMIS_CONTENT_STREAM_MIME_TYPE));
         newObject.setContentStreamLenght(object.getProperties().getInteger(CMIS.CMIS_CONTENT_STREAM_LENGTH));
         entry.setObject(newObject);

      }
      //Object is policy - create instance of Policy
      else if ((baseTypeId != null && objectInfo.getBaseType().equals(EnumBaseObjectTypeIds.CMIS_POLICY)))
      {
         CmisPolicyImpl newObject = new CmisPolicyImpl(object, object.getProperties().getString(CMIS.CMIS_POLICY_TEXT));
         entry.setObject(newObject);
      }
      //Object is policy - create instance of Relationship
      else if ((baseTypeId != null && objectInfo.getBaseType().equals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)))
      {
         CmisRelationshipImpl newObject =
            new CmisRelationshipImpl(object, object.getProperties().getId(CMIS.CMIS_SOURCE_ID), object.getProperties()
               .getId(CMIS.CMIS_TARGET_ID));
         entry.setObject(newObject);
      }
      entry.getObject().setObjectInfo(objectInfo);
   }
}
