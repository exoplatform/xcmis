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

package org.xcmis.core.impl;

import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisObjectProducerTest.java 2094 2009-07-13 06:41:44Z andrew00x
 *          $
 */
public class CmisObjectProducerTest extends BaseTest
{

   private CmisObjectProducer objProducer;

   public void setUp() throws Exception
   {
      super.setUp();
      objProducer = new ObjectServiceImpl(repositoryService, propertyService);
   }

   public void testDocumentAllowalableActions() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      CmisObject cmis =
         objProducer.getCmisObject(doc, true, EnumIncludeRelationships.NONE, false, false, PropertyFilter.DEFAULT,
            RenditionFilter.NONE, repository.getRenditionManager(), false);
      assertNotNull(cmis.getAllowableActions());
      CmisAllowableActionsType actions = cmis.getAllowableActions();
      assertTrue(actions.isCanApplyPolicy());
      assertFalse(actions.isCanAddObjectToFolder());
      assertFalse(actions.isCanCancelCheckOut());
      assertFalse(actions.isCanCheckIn());
      assertTrue(actions.isCanCheckOut());
      assertFalse(actions.isCanCreateDocument());
      assertFalse(actions.isCanCreateFolder());
      //      assertFalse(actions.isCanCreatePolicy());
      assertTrue(actions.isCanCreateRelationship());
      assertTrue(actions.isCanDeleteObject());
      assertTrue(actions.isCanDeleteContentStream());
      assertFalse(actions.isCanDeleteTree());
      assertTrue(actions.isCanGetAllVersions());
      assertTrue(actions.isCanGetAppliedPolicies());
      assertFalse(actions.isCanGetChildren());
      assertFalse(actions.isCanGetDescendants());
      assertFalse(actions.isCanGetFolderParent());
      assertTrue(actions.isCanGetObjectParents());
      assertTrue(actions.isCanGetProperties());
      assertTrue(actions.isCanGetObjectRelationships());
      assertTrue(actions.isCanMoveObject());
      assertFalse(actions.isCanRemoveObjectFromFolder());
      assertTrue(actions.isCanRemovePolicy());
      assertTrue(actions.isCanSetContentStream());
      assertTrue(actions.isCanUpdateProperties());
      assertTrue(actions.isCanGetContentStream());
   }

   protected Entry prepareFolder() throws Exception
   {
      //      /
      //      testRoot
      //        |_ folder
      //           |_ folder1
      //           |  |_ folder2
      //           |  |  |_ doc5
      //           |  |  |_ doc6
      //           |  |  |_ doc7
      //           |  |  |_ doc8
      //           |  |  |_ doc9
      //           |  |  |_ doc10
      //           |  |_ doc3
      //           |  |_ doc4
      //           |_ doc1
      //           |_ doc2
      Entry folder = createFolder(testFolder, "folder");
      createDocument(folder, "doc1", null);
      createDocument(folder, "doc2", null);
      Entry folder1 = createFolder(folder, "folder1");
      createDocument(folder1, "doc3", null);
      createDocument(folder1, "doc4", null);
      Entry folder2 = createFolder(folder1, "folder2");
      createDocument(folder2, "doc5", null);
      createDocument(folder2, "doc6", null);
      createDocument(folder2, "doc7", null);
      createDocument(folder2, "doc8", null);
      createDocument(folder2, "doc9", null);
      createDocument(folder2, "doc10", null);
      return folder;
   }

   public void testProperties() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      CmisObject cmis =
         objProducer.getCmisObject(doc, true, EnumIncludeRelationships.NONE, false, false, new PropertyFilter("*"),
            RenditionFilter.NONE, repository.getRenditionManager(), false);
      List<CmisProperty> props = cmis.getProperties().getProperty();
      List<String> names = new ArrayList<String>(props.size());
      for (CmisProperty prop : props)
      {
         names.add(prop.getPropertyDefinitionId());
      }
      assertTrue(names.contains(CMIS.CHANGE_TOKEN));
      assertTrue(names.contains(CMIS.CHECKIN_COMMENT));
      assertTrue(names.contains(CMIS.CONTENT_STREAM_FILE_NAME));
      assertTrue(names.contains(CMIS.CONTENT_STREAM_LENGTH));
      assertTrue(names.contains(CMIS.CONTENT_STREAM_MIME_TYPE));
      assertTrue(names.contains(CMIS.CREATED_BY));
      assertTrue(names.contains(CMIS.CREATION_DATE));
      assertTrue(names.contains(CMIS.IS_IMMUTABLE));
      assertTrue(names.contains(CMIS.IS_LATEST_MAJOR_VERSION));
      assertTrue(names.contains(CMIS.IS_LATEST_VERSION));
      assertTrue(names.contains(CMIS.IS_MAJOR_VERSION));
      assertTrue(names.contains(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      assertTrue(names.contains(CMIS.LAST_MODIFICATION_DATE));
      assertTrue(names.contains(CMIS.NAME));
      assertTrue(names.contains(CMIS.OBJECT_ID));
      assertTrue(names.contains(CMIS.BASE_TYPE_ID));
      assertTrue(names.contains(CMIS.OBJECT_TYPE_ID));
      assertTrue(names.contains(CMIS.VERSION_LABEL));
      assertTrue(names.contains(CMIS.VERSION_SERIES_CHECKED_OUT_BY));
      assertTrue(names.contains(CMIS.VERSION_SERIES_CHECKED_OUT_ID));
      assertTrue(names.contains(CMIS.VERSION_SERIES_ID));
      // with filter
      String filter = CMIS.OBJECT_ID + "," + CMIS.OBJECT_TYPE_ID;
      cmis =
         objProducer.getCmisObject(doc, true, EnumIncludeRelationships.NONE, false, false, new PropertyFilter(filter),
            RenditionFilter.NONE, repository.getRenditionManager(), false);
      props = cmis.getProperties().getProperty();
      names.clear();
      for (CmisProperty prop : props)
      {
         names.add(prop.getPropertyDefinitionId());
      }
      //    assertEquals(2, names.size());
      assertTrue(names.contains(CMIS.OBJECT_ID));
      assertTrue(names.contains(CMIS.OBJECT_TYPE_ID));
   }

}
