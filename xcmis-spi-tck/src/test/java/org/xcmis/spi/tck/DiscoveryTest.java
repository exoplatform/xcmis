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

package org.xcmis.spi.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityChanges;
import org.xcmis.spi.model.CapabilityQuery;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.MimeType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2.2.6 Discovery Services The Discovery Services (query) are used to search
 * for query-able objects within the Repository.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id$
 */
public class DiscoveryTest extends BaseTest
{

   private static String testRootFolderId;

   private static String name0 = "test query 000";

   private static String name1 = "test query 001";

   private static String name2 = "test query 002";

   private static String document0;

   private static String document1;

   private static String document2;

   private static TypeDefinition documentType;

   @BeforeClass
   public static void start() throws Exception
   {
      testRootFolderId = createFolder(rootFolderID, CmisConstants.FOLDER, "discovery_testroot", null, null, null);

      documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);

      document0 =
         createDocument(testRootFolderId, //
            documentType.getId(), //
            name0, //
            documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : new BaseContentStream(
               "test query 000".getBytes(), "", new MimeType("text", "plain")), //
            null, //
            null, //
            null, //
            null);
      document1 =
         createDocument(testRootFolderId, //
            documentType.getId(), //
            name1, //
            documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : new BaseContentStream(
               "test query 001".getBytes(), "", new MimeType("text", "plain")), //
            null, //
            null, //
            null, //
            null);
      document2 =
         createDocument(testRootFolderId, //
            documentType.getId(), //
            name2, //
            documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : new BaseContentStream(
               "test query 002".getBytes(), "", new MimeType("text", "plain")), //
            null, //
            null, //
            null, //
            null);
      System.out.println("Running Discovery Service tests");
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testRootFolderId != null)
      {
         clear(testRootFolderId);
      }
   }

   /**
    * 2.2.6.1 query.
    *
    * Description: Executes a CMIS query statement against the contents of the
    * Repository.
    */
   @Test
   public void testContentChanges() throws Exception
   {
      CapabilityChanges capabilityChanges = capabilities.getCapabilityChanges();
      if (capabilityChanges == CapabilityChanges.NONE)
      {
         return;
      }

      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null, null,
         null);
      String logToken = connection.getStorage().getRepositoryInfo().getLatestChangeLogToken();
      ChangeLogTokenHolder logTokenHolder = new ChangeLogTokenHolder();
      logTokenHolder.setValue(logToken);
      ItemsList<CmisObject> changes =
         connection.getContentChanges(logTokenHolder, //
            capabilityChanges == CapabilityChanges.ALL || capabilityChanges == CapabilityChanges.PROPERTIES ? true
               : false, //
            null, // implementation specific set of properties
            isPoliciesSupported ? true : false, //
            capabilities.getCapabilityACL() != CapabilityACL.NONE ? true : false, //
            true, //
            -1);
      assertEquals(1, changes.getNumItems());
   }

   /**
    * 2.2.6.1 query.
    *
    * Description: Executes a CMIS query statement against the contents of the
    * Repository.
    */
   @Test
   public void testQuery() throws Exception
   {
      if (capabilities.getCapabilityQuery() == CapabilityQuery.NONE)
      {
         return;
      }

      String statement = null;
      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED)
      {
         statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"test query 000\")";
      }
      else
      {
         statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE cmis:name='test query 000'";
      }
      ItemsList<CmisObject> query =
         connection.query(statement, //
            capabilities.isCapabilityAllVersionsSearchable() ? true : false, //
            true, //
            isRelationshipsSupported ? IncludeRelationships.BOTH : IncludeRelationships.NONE, //
            true, //
            capabilities.getCapabilityRenditions() != CapabilityRendition.NONE ? RenditionFilter.ANY
               : RenditionFilter.NONE, //
            -1, //
            0);

      assertNotNull(query);
      List<CmisObject> items = query.getItems();
      assertEquals(1, items.size());

      CmisObject item = items.get(0);
      assertNotNull(item);
      assertEquals(document0, item.getObjectInfo().getId());
   }

   /**
    * 2.2.6.1 query.
    *
    * Description: Executes a CMIS query statement against the contents of the
    * Repository.
    */
   @Test
   public void testQuery2() throws Exception
   {
      if (capabilities.getCapabilityQuery() == CapabilityQuery.NONE)
      {
         return;
      }

      String statement = null;
      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED)
      {
         statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"test query\")";
      }
      else
      {
         statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE cmis:name LIKE 'test query%'";
      }
      ItemsList<CmisObject> query =
         connection.query(statement, //
            capabilities.isCapabilityAllVersionsSearchable() ? true : false, //
            true, //
            isRelationshipsSupported ? IncludeRelationships.BOTH : IncludeRelationships.NONE, //
            true, //
            capabilities.getCapabilityRenditions() != CapabilityRendition.NONE ? RenditionFilter.ANY
               : RenditionFilter.NONE, //
            -1, //
            0);

      assertNotNull(query);
      List<CmisObject> items = query.getItems();
      assertEquals(3, items.size());
      Set<String> ids = new HashSet<String>(3);
      for (CmisObject item : items)
      {
         ids.add(item.getObjectInfo().getId());
      }
      assertTrue("Expected item " + document0 + " not found in result set. ", ids.contains(document0));
      assertTrue("Expected item " + document1 + " not found in result set. ", ids.contains(document1));
      assertTrue("Expected item " + document2 + " not found in result set. ", ids.contains(document2));
   }
}
