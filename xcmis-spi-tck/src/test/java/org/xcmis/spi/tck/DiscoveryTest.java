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

import org.junit.Test;
import org.junit.Before;
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

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
   }

   /**
    * 2.2.6.1 query.
    * 
    * Description: Executes a CMIS query statement against the contents of the Repository.
    */
   @Test
   public void testQuery() throws Exception
   {
      String testname = "testQuery";
      System.out.print("Running " + testname + "....                                                      ");
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
         skip("DiscoveryTest.testQuery");
      String errSms = "\n 2.2.6.1 query. Doesn't work Query (Discovery service) with cmis:document to search content.";
      FolderData parentFolder = null;
      try
      {
         parentFolder = createFolder(rootFolder, "folder1");
         DocumentData documentData = createDocument(parentFolder, "doc1", "Hello World!");
         String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
         ItemsList<CmisObject> query = null;

         query =
            getConnection().query(statement, true, false, IncludeRelationships.BOTH, true, RenditionFilter.ANY, -1, 0);

         if (query == null)
            doFail(testname, "Quary failed;");
         if (query.getItems() == null)
            doFail(testname, "Quary failed - no items;");
         if (query.getItems().size() != 1)
            doFail(testname, "Quary failed -  incorrect items number;");

         List<CmisObject> result = query.getItems();
         for (CmisObject cmisObject : result)
         {
            if (cmisObject == null)
               doFail(testname, "Query result not found;");
            if (cmisObject.getObjectInfo() == null)
               doFail(testname, "ObjectInfo not found in query result;");
            if (cmisObject.getObjectInfo().getId() == null)
               doFail(testname, "ObjectId not found in query result;");
            if (!documentData.getObjectId().equals(cmisObject.getObjectInfo().getId()))
               doFail(testname, "ObjectId's does not match;");
            if (!documentData.getName().equals(cmisObject.getObjectInfo().getName()))
               doFail(testname, "Object names does not match;");
         }
         pass(testname);
      }
      finally
      {
         clear(parentFolder.getObjectId());
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
      String testname = "testQuery2";
      System.out.print("Running " + testname + "....                                                     ");
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
         skip("testQuery2");
      FolderData parentFolder = null;
      try
      {
         parentFolder = createFolder(rootFolder, "folder1");;
         DocumentData documentData = createDocument(parentFolder, "doc1", "Hello World!");
         String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
         ItemsList<CmisObject> query = null;

         query =
            getConnection().query(statement, false, false, IncludeRelationships.BOTH, true, RenditionFilter.ANY, -1, 0);

         if (query == null)
            doFail(testname, "Quary failed;");
         if (query.getItems() == null)
            doFail(testname, "Quary failed - no items;");
         if (query.getItems().size() == 0)
            doFail(testname, "Quary failed - no items;");

         List<CmisObject> result = query.getItems();
         for (CmisObject cmisObject : result)
         {
            if (cmisObject == null)
               doFail(testname, "Query result not found;");
            if (cmisObject.getObjectInfo() == null)
               doFail(testname, "ObjectInfo not found in query result;");
            if (cmisObject.getObjectInfo().getId() == null)
               doFail(testname, "ObjectId not found in query result;");
            if (!documentData.getObjectId().equals(cmisObject.getObjectInfo().getId()))
               doFail(testname, "ObjectId's does not match;");
            if (!documentData.getName().equals(cmisObject.getObjectInfo().getName()))
               doFail(testname, "Object names does not match;");
         }
         pass(testname);
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         clear(parentFolder.getObjectId());
      }
   }

   
   
   /**
    * 2.2.6.1 query.
    * 
    * Description: Executes a CMIS query statement against the contents of the Repository.
    */
   @Test
   public void testContentChanges() throws Exception
   {
      String testname = "testContentChanges";
      System.out.print("Running " + testname + "....                                             ");
      if (getStorage().getRepositoryInfo().getCapabilities().getCapabilityQuery().equals(CapabilityQuery.NONE))
         skip("testQuery2");
      FolderData parentFolder = null;
      try
      {
         parentFolder = createFolder(rootFolder, "folder1");
         DocumentData documentData = createDocument(parentFolder, "doc1", "Hello World!");
         String statement = "SELECT * FROM " + CmisConstants.DOCUMENT + " WHERE CONTAINS(\"Hello\")";
         ItemsList<CmisObject> query = null;

         query =
            getConnection().getContentChanges(null, true, PropertyFilter.ALL, true, true, true, -1);

         if (query == null)
            doFail(testname, "Quary failed;");
         if (query.getItems() == null)
            doFail(testname, "Quary failed - no items;");
         pass(testname);
      }
      catch (NotSupportedException nse){
         skip("DiscoveryTest." +testname);
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         clear(parentFolder.getObjectId());
      }
   }
   
   
   protected void pass(String method) throws Exception
   {
      super.pass("DiscoveryTest." + method);
   }
   
   protected void doFail(String method,  String message) throws Exception
   {
      super.doFail("DiscoveryTest." + method,  message);
   }

}
