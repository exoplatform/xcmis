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

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.CapabilityQuery;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;

import java.util.List;

/**
 * 2.2.6 Discovery Services
 * The Discovery Services (query) are used to search for query-able objects within the Repository.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id$
 */
public class DiscoveryTest extends BaseTest
{

   static FolderData testroot = null;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      testroot =
         getStorage().createFolder(rootFolder, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "discovery_testroot"), null, null);
      System.out.print("Running Discovery Service tests....");
   }

   /**
    * 2.2.6.1 query.
    * 
    * Description: Executes a CMIS query statement against the contents of the Repository.
    */
   @Test
   public void testQuery() throws Exception
   {
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
      {
         //SKIP
         return;
      }
      DocumentData documentData = createDocument(testroot, "testQuery1", "Hello World!");
      String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
      ItemsList<CmisObject> query = null;

      query =
         getConnection().query(statement, true, false, IncludeRelationships.BOTH, true, RenditionFilter.ANY, -1, 0);

      assertNotNull("Quary failed.", query);
      assertNotNull("Quary failed - no items.", query.getItems());
      assertTrue("Quary failed -  incorrect items number.", query.getItems().size() == 1);

      List<CmisObject> result = query.getItems();
      for (CmisObject cmisObject : result)
      {
         assertNotNull("Query result not found.", cmisObject);
         assertNotNull("ObjectInfo not found in query result.", cmisObject.getObjectInfo());
         assertNotNull("ObjectId not found in query result.", cmisObject.getObjectInfo().getId());
         assertTrue("ObjectId's does not match.", documentData.getObjectId().equals(cmisObject.getObjectInfo().getId()));
         assertTrue("Object names does not match.", documentData.getName().equals(cmisObject.getObjectInfo().getName()));
         getStorage().deleteObject(documentData, true);
      }
   }

   /**
    * 2.2.6.1 query.
    * 
    * Description: Executes a CMIS query statement against the contents of the Repository.
    */
   @Test
   public void testQuery2() throws Exception
   {
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
      {
         //SKIP
         return;
      }
      DocumentData documentData = createDocument(testroot, "testQuery2", "Hello World!");
      String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
      ItemsList<CmisObject> query = null;
      query =
         getConnection().query(statement, false, false, IncludeRelationships.BOTH, true, RenditionFilter.ANY, -1, 0);

      assertNotNull("Quary failed.", query);
      assertNotNull("Quary failed - no items.", query.getItems());
      if (query.getItems().size() == 0)
         fail("Quary failed - no items.");
      List<CmisObject> result = query.getItems();
      for (CmisObject cmisObject : result)
      {
         assertNotNull("Query result not found.", cmisObject);
         assertNotNull("ObjectInfo not found in query result.", cmisObject.getObjectInfo());

         assertNotNull("ObjectId not found in query result.", cmisObject.getObjectInfo().getId());
         assertTrue("ObjectId's does not match.", documentData.getObjectId().equals(cmisObject.getObjectInfo().getId()));
         assertTrue("Object names does not match.", documentData.getName().equals(cmisObject.getObjectInfo().getName()));
      }
      getStorage().deleteObject(documentData, true);
   }

   /**
    * 2.2.6.1 query.
    * 
    * Description: Executes a CMIS query statement against the contents of the Repository.
    */
   @Test
   public void testContentChanges() throws Exception
   {
      DocumentData documentData = null;
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
      {
         //SKIP
      }
      try
      {
         documentData = createDocument(testroot, "testContentChanges", "Hello World!");
         String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
         ItemsList<CmisObject> query = null;

         query = getConnection().getContentChanges(null, true, PropertyFilter.ALL, true, true, true, -1);
         assertNotNull("Quary failed.", query);
         assertNotNull("Quary failed - no items.", query.getItems());
      }
      catch (NotSupportedException nse)
      {
         //SKIP
      }
      finally
      {
         getStorage().deleteObject(documentData, true);
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testroot != null)
         clear(testroot.getObjectId());
      if (BaseTest.conn != null)
         BaseTest.conn.close();
      System.out.println("done;");
   }
}
