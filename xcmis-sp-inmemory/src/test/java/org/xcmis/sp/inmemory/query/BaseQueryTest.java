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

package org.xcmis.sp.inmemory.query;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.inmemory.BaseTest;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.Document;
import org.xcmis.spi.Folder;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.PropertyDefinitionImpl;
import org.xcmis.spi.model.impl.TypeDefinitionImpl;
import org.xcmis.spi.query.Result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 *
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: BaseQueryTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseQueryTest extends BaseTest
{
   private static final Log LOG = ExoLogger.getLogger(BaseQueryTest.class.getName());

   /**
    * @see org.xcmis.sp.inmemory.BaseTest#setUp()
    */
   @Override
   public void setUp() throws Exception
   {

      super.setUp();
      Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      propertyDefinitions.put("cmis:booster-name", new PropertyDefinitionImpl("cmis:booster-name", "cmis:booster-name",
         "cmis:booster-name", null, "cmis:booster-name", "cmis:booster-name", PropertyType.STRING,
         Updatability.READWRITE, true, false, false, false, false, true, null, null));

      propertyDefinitions.put("cmis:commander", new PropertyDefinitionImpl("cmis:commander", "cmis:commander",
         "cmis:commander", null, "cmis:commander", "cmis:commander", PropertyType.STRING, Updatability.READWRITE, true,
         false, false, false, false, true, null, null));

      propertyDefinitions.put("cmis:command-module-pilot", new PropertyDefinitionImpl("cmis:command-module-pilot",
         "cmis:command-module-pilot", "cmis:command-module-pilot", null, "cmis:command-module-pilot",
         "cmis:command-module-pilot", PropertyType.STRING, Updatability.READWRITE, true, false, false, false, false,
         true, null, null));
      propertyDefinitions.put("cmis:lunar-module-pilot", new PropertyDefinitionImpl("cmis:lunar-module-pilot",
         "cmis:lunar-module-pilot", "cmis:lunar-module-pilot", null, "cmis:lunar-module-pilot",
         "cmis:lunar-module-pilot", PropertyType.STRING, Updatability.READWRITE, true, false, false, false, false,
         true, null, null));

      propertyDefinitions.put("cmis:booster-mass", new PropertyDefinitionImpl("cmis:booster-mass", "cmis:booster-mass",
         "cmis:booster-mass", null, "cmis:booster-mass", "cmis:booster-mass", PropertyType.DECIMAL,
         Updatability.READWRITE, true, false, false, false, false, true, null, null));

      propertyDefinitions.put("cmis:sample-returned", new PropertyDefinitionImpl("cmis:sample-returned",
         "cmis:sample-returned", "cmis:sample-returned", null, "cmis:sample-returned", "cmis:sample-returned",
         PropertyType.DECIMAL, Updatability.READWRITE, true, false, false, false, false, true, null, null));

      propertyDefinitions.put("cmis:status", new PropertyDefinitionImpl("cmis:status", "cmis:status", "cmis:status",
         null, "cmis:status", "cmis:status", PropertyType.BOOLEAN, Updatability.READWRITE, true, false, false, false,
         false, true, null, null));

      storage.addType(new TypeDefinitionImpl("cmis:nasa-mission", BaseType.DOCUMENT, "cmis:nasa-mission",
         "cmis:nasa-mission", "", "cmis:document", "cmis:nasa-mission", "Cmis Document Type", true, true, true, true,
         true, true, true, true, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions));

      storage.addType(new TypeDefinitionImpl("cmis:article", BaseType.DOCUMENT, "cmis:article", "cmis:article", "",
         "cmis:document", "cmis:article", "Cmis article", true, true, true, true, true, true, true, true, null, null,
         ContentStreamAllowed.ALLOWED, new HashMap<String, PropertyDefinition<?>>()));

      storage.addType(new TypeDefinitionImpl("cmis:article-sports", BaseType.DOCUMENT, "cmis:article-sports",
         "cmis:article-sports", "", "cmis:article", "cmis:article-sports", "Cmis cmis:article-sports", true, true,
         true, true, true, true, true, true, null, null, ContentStreamAllowed.ALLOWED,
         new HashMap<String, PropertyDefinition<?>>()));
      storage.addType(new TypeDefinitionImpl("cmis:article-animals", BaseType.DOCUMENT, "cmis:article-animals",
         "cmis:article-animals", "", "cmis:article", "cmis:article-animals", "Cmis cmis:article-animals", true, true,
         true, true, true, true, true, true, null, null, ContentStreamAllowed.ALLOWED,
         new HashMap<String, PropertyDefinition<?>>()));
   }

   protected Document createDocument(Folder folder, String name, String typeId, byte[] content, String mimeType)
      throws Exception
   {

      return createDocument(folder, name, typeId, new BaseContentStream(content, null, mimeType), null);
   }

   protected Document createDocument(Folder folder, String name, String typeId, ContentStream content,
      VersioningState versioningState)

   /**
   //    * Test NOT IN constraint.
   //    * <p>
   //    * Initial data:
   //    * <ul>
   //    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
   //    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
   //    * </ul>
   //    * <p>
   //    * Query : Select all documents where long property not in set {15 , 20}.
   //    * <p>
   //    * Expected result: doc1
   //    *
   //    * @throws Exception if an unexpected error occurs
   //    */
   //   public void testNotINConstraint() throws Exception
   //   {
   //
   //      // create data
   //      String name = "fileCS2.doc";
   //      String name2 = "fileCS3.doc";
   //      String contentType = "text/plain";
   //
   //      Document doc1 = createDocument(testRoot, name, new byte[0], contentType);
   //      doc1.setDecimal("long", new BigDecimal(3));
   //
   //      Document doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
   //      doc2.setDecimal("long", new BigDecimal(15));
   //
   //      String statement = "SELECT * FROM " + NASA_DOCUMENT + " WHERE long NOT IN (15, 20)";
   //
   //      Query query = new Query(statement, true);
   //      ItemsIterator<Result> result = storage.query(query);
   //
   //      checkResult(result, new Document[]{doc1});
   //   }
   {
      Document document =
         storage.createDocument(folder, typeId, versioningState == null ? VersioningState.MAJOR : versioningState);
      document.setName(name);
      document.setContentStream(content);
      // storage.saveObject(document);
      //document.save();
      return document;
   }

   protected Folder createFolder(Folder folder, String name, String typeId)
   {
      Folder newFolder = storage.createFolder(folder, typeId);
      newFolder.setName(name);
      storage.saveObject(newFolder);
      return newFolder;
   }

   protected void checkResult(ItemsIterator<Result> result, String[] columns, String[][] expectedResults)
      throws Exception
   {
      // expected
      Set<String> expectedPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }
      for (int i = 0; i < expectedResults.length; i++)
      {
         String newRow = "";
         for (int j = 0; j < expectedResults[i].length; j++)
         {
            newRow += "|" + expectedResults[i][j];
         }
         expectedPaths.add(newRow);
         if (LOG.isDebugEnabled())
         {
            LOG.debug(newRow);
         }
      }
      // actual

      Set<String> resultPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         String objectId = result.next().getObjectId();
         String resultRow = "";
         for (String column : columns)
         {
            resultRow += "|" + getProperty(objectId, column);

         }
         resultPaths.add(resultRow);
         if (LOG.isDebugEnabled())
         {
            LOG.debug(resultRow);
         }
      }

      checkResults(expectedPaths, resultPaths);
   }

   protected void checkResult(ItemsIterator<Result> result, ObjectData[] nodes)
   {
      // collect rows
      Set<String> expectedPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }

      for (ObjectData node : nodes)
      {
         expectedPaths.add(node.getObjectId());
         if (LOG.isDebugEnabled())
         {
            LOG.debug(node.getObjectId());
         }
      }

      Set<String> resultPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         Result next = result.next();
         String id = next.getObjectId();
         resultPaths.add(id);
         ObjectData object = storage.getObject(id);
         LOG.debug("id:=" + id + " path:=" + object.getParent().getPath() + "/" + object.getName());
      }

      checkResults(expectedPaths, resultPaths);
   }

   protected void checkResultOrder(ItemsIterator<Result> result, ObjectData[] nodes)
   {
      // collect rows
      String expectedPaths = "";
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }

      for (ObjectData node : nodes)
      {
         expectedPaths += ":" + node.getObjectId();
         if (LOG.isDebugEnabled())
         {
            LOG.debug(node.getObjectId());
         }
      }

      String resultPaths = "";
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         String id = result.next().getObjectId();
         resultPaths += ":" + id;
         if (LOG.isDebugEnabled())
         {
            LOG.debug(id);
         }
      }

      assertEquals(expectedPaths, resultPaths);
   }

   private void checkResults(Set<String> expectedRowResults, Set<String> actualRowResults)
   {
      // check if all expected are in result
      for (String path : expectedRowResults)
      {
         assertTrue(path + " is not part of the result set " + actualRowResults, actualRowResults.contains(path));
      }
      // check result does not contain more than expected
      for (String path : actualRowResults)
      {
         assertTrue(path + " is not expected to be part of the result set", expectedRowResults.contains(path));
      }
   }

   private String getProperty(String objectId, String propertyId) throws Exception
   {
      ObjectData entry = storage.getObject(objectId);
      return entry.getProperty(propertyId).toString();
   }

}
