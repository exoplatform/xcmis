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

package org.xcmis.sp.jcr.exo.query;

import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id$
 */
public class QueryUsecasesTest extends BaseQueryTest
{

   private final static String CONTENT_TYPE = "text/plain";

   private final static String PROPERTY_BOOSTER = "exo:Booster";

   private final static String PROPERTY_COMMANDER = "exo:Commander";

   /**
    * Simple test.
    * <p>
    * Initial data:
    * <ul>
    * <li>document1: <b>Title</b> - node1
    * <li>document2: <b>Title</b> - node2
    * </ul>
    * <p>
    * Query : Select all CMIS_DOCUMENTS.
    * <p>
    * Expected result: document1 and document1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testSimpleQuery() throws Exception
   {
      EntryImpl document1 = this.createDocument(root, "node1", "hello world".getBytes(), "text/plain");
      EntryImpl document2 = this.createDocument(root, "node2", "hello world second".getBytes(), "text/plain");

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value();
      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      checkResult(result, new EntryImpl[]{document1, document2});
   }

   /**
    * Test query with one 'and' two or' constraints.
    * <p>
    * Initial data:
    * <ul>
    * <li>document1: <b>Title</b> - Apollo 7 <b>exo:Booster</b> - Saturn 1B
    * <b>exo:Commander</b> - Walter M. Schirra</li>
    * <li>document2: <b>Title</b> - Apollo 8 <b>exo:Booster</b> - Saturn V
    * <b>exo:Commander</b> - Frank F. Borman, II</li>
    * <li>document3: <b>Title</b> - Apollo 13<b> exo:Booster</b> - Saturn V
    * <b>exo:Commander</b> - James A. Lovell, Jr.</li>
    * </ul>
    * <p>
    * Query : Select all documents where exo:Booster is 'Saturn V' and
    * exo:Commander is Frank F. Borman, II or James A. Lovell, Jr.
    * <p>
    * Expected result: document2 and document3
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testAndOrConstraint() throws Exception
   {
      // create data
      EntryImpl folder = createFolder(root, "testAndOrConstraint");
      List<EntryImpl> appolloContent = createApolloContent(folder);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT * ");
      sql.append("FROM ");
      sql.append(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      sql.append(" WHERE ");
      sql.append(PROPERTY_BOOSTER + " = " + "'Saturn V'");
      sql.append(" AND ( " + PROPERTY_COMMANDER + " = 'Frank F. Borman, II' ");
      sql.append("       OR " + PROPERTY_COMMANDER + " = 'James A. Lovell, Jr.' )");

      Query query = new Query(sql.toString(), true);

      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      // check results
      checkResult(result, new Entry[]{appolloContent.get(1), appolloContent.get(2)});
   }

   /**
    * Create content for Apollo program.
    * 
    * @param folder
    * @return
    * @throws Exception
    */
   private List<EntryImpl> createApolloContent(EntryImpl folder) throws Exception
   {
      List<EntryImpl> result = new ArrayList<EntryImpl>();
      EntryImpl doc1 =
         createDocument(folder.getObjectId(), "Apollo 7",
            ("Apollo 7 (October 11-22, 1968) was the first manned mission "
               + "in the Apollo program to be launched. It was an eleven-day "
               + "Earth-orbital mission, the first manned launch of the "
               + "Saturn IB launch vehicle, and the first three-person " + "American space mission").getBytes(),
            CONTENT_TYPE);
      doc1.setString(PROPERTY_COMMANDER, "Walter M. Schirra");
      doc1.setString(PROPERTY_BOOSTER, "Saturn 1B");
      doc1.save();
      result.add(doc1);

      EntryImpl doc2 =
         createDocument(folder.getObjectId(), "Apollo 8", ("Apollo 8 was the first "
            + "manned space voyage to achieve a velocity sufficient to allow escape from the "
            + "gravitational field of planet Earth; the first to escape from the gravitational "
            + "field of another celestial body; and the first manned voyage to return to planet Earth "
            + "from another celestial body - Earth's Moon").getBytes(), CONTENT_TYPE);
      doc2.setString(PROPERTY_COMMANDER, "Frank F. Borman, II");
      doc2.setString(PROPERTY_BOOSTER, "Saturn V");
      doc2.save();
      result.add(doc2);

      EntryImpl doc3 =
         createDocument(folder.getObjectId(), "Apollo 13", ("Apollo 13 was the third "
            + "manned mission by NASA intended to land on the Moon, but a mid-mission technical "
            + "malfunction forced the lunar landing to be aborted. ").getBytes(), CONTENT_TYPE);
      doc3.setString(PROPERTY_COMMANDER, "James A. Lovell, Jr.");
      doc3.setString(PROPERTY_BOOSTER, "Saturn V");
      doc3.save();
      result.add(doc3);

      return result;
   }

   /**
    * Constraints with multi-valued properties is not supported.
    * 
    * @throws Exception
    */
   // XXX temporary excluded
   public void _testAnyInConstraint() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setDecimals("multivalueLong", new BigDecimal[]{new BigDecimal(3), new BigDecimal(5), new BigDecimal(10)});
      doc1.setStrings("multivalueString", new String[]{"bla-bla"});
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setDecimals("multivalueLong", new BigDecimal[]{new BigDecimal(15), new BigDecimal(10)});
      doc2.setStrings("multivalueString", new String[]{"bla-bla"});
      doc2.save();

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE ANY multivalueLong IN ( 3 , 5, 6 ) ";

      Query query = new Query(statement, true);

      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check results
      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test fulltext constraint.
    * <p>
    * Initial data:
    * <p>
    * see createApolloContent()
    * <p>
    * Query : Select all documents where data contains "moon" word.
    * <p>
    * Expected result: document1 and document2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testFulltextConstraint() throws Exception
   {
      EntryImpl folder = createFolder(root, "testFulltextConstraint");

      List<EntryImpl> appolloContent = createApolloContent(folder);

      String statement1 = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE CONTAINS(\"moon\")";
      Query query = new Query(statement1, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(2, result.size());
      checkResult(result, new Entry[]{appolloContent.get(1), appolloContent.get(2)});

      String statement2 = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE CONTAINS(\"Moon\")";
      query = new Query(statement2, true);
      ItemsIterator<Result> result2 = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(2, result2.size());
      checkResult(result2, new Entry[]{appolloContent.get(1), appolloContent.get(2)});
   }

   /**
    * Test fulltext constraint.
    * <p>
    * Initial data:
    * <p>
    * see createApolloContent()
    * <p>
    * Query : Select all documents where data contains "moon" word.
    * <p>
    * Expected result: document1 and document2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testUpdateFulltextConstraint() throws Exception
   {
      EntryImpl folder = createFolder(root, "testFulltextConstraint");

      EntryImpl doc3 =
         createDocument(folder.getObjectId(), "Apollo 13", ("Apollo 13 was the third "
            + "manned mission by NASA intended to land on the Moon, but a mid-mission technical "
            + "malfunction forced the lunar landing to be aborted. ").getBytes(), CONTENT_TYPE);
      doc3.setString(PROPERTY_COMMANDER, "James A. Lovell, Jr.");
      doc3.setString(PROPERTY_BOOSTER, "Saturn V");
      doc3.save();

      String statement1 = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE CONTAINS(\"moon\")";
      Query query = new Query(statement1, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(1, result.size());
      checkResult(result, new Entry[]{doc3});

      //replace content
      ContentStream cs = new BaseContentStream("Sun".getBytes(), "test", CONTENT_TYPE);
      doc3.setContent(cs);

      doc3.save();

      //check old one
      result = cmisRepository.getQueryHandler().handleQuery(query);
      assertEquals(0, result.size());
      //check new  content
      String statement2 = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE CONTAINS(\"Sun\")";
      query = new Query(statement2, true);
      result = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(1, result.size());
      checkResult(result, new Entry[]{doc3});

   }

   /**
    * Test 'IN' constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>document1: <b>Title</b> - node1 <b>name</b> - supervisor
    * <li>document2: <b>Title</b> - node2 <b>name</b> - anyname
    * </ul>
    * <p>
    * Query : Select all documents where name is in set {'admin' , 'supervisor' ,
    * 'Vasya' }.
    * <p>
    * Expected result: document2 and document3
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testINConstraint() throws Exception
   {

      EntryImpl document1 = createDocument(root, "node1", "hello world".getBytes(), "text/plain");
      document1.setString("employee", "supervisor");
      document1.save();

      EntryImpl document2 = createDocument(root, "node2", "hello world".getBytes(), "text/plain");
      document2.setString("employee", "anyname");
      document2.save();

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()
            + " WHERE employee IN ('admin', 'supervisor', 'Vasya')";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      assertEquals(1, result.size());
      checkResult(result, new Entry[]{document1});
   }

   /**
    * Test IN_FOLDER constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>folder1:
    * <li>doc1: <b>Title</b> - node1
    * <li>folder2
    * <li>doc2: <b>Title</b> - node2
    * </ul>
    * <p>
    * Query : Select all documents that are in folder1.
    * <p>
    * Expected result: doc1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testDocumentInFolderConstrain() throws Exception
   {
      // create data
      EntryImpl folder1 = this.createFolder(root, "folder1");
      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      EntryImpl folder2 = this.createFolder(root, "folder2");
      EntryImpl doc2 = createDocument(folder2.getObjectId(), "node2", "hello world".getBytes(), "text/plain");

      EntryImpl folder3 = this.createFolder(folder1.getObjectId(), "folder2");
      EntryImpl doc3 = createDocument(folder3.getObjectId(), "node3", "hello world".getBytes(), "text/plain");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE IN_FOLDER( '" + folder1.getObjectId()
            + "')";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check results
      checkResult(result, new Entry[]{doc1});

   }

   /**
    * Test IN_FOLDER constraint.
    * <p>
    * Initial data:
    * <p>
    * folder1:
    * <p>
    * -doc1: <b>Title</b> - node1
    * <p>
    * folder2:
    * <p>
    * -doc2: <b>Title</b> - node2
    * <p>
    * -folder3:
    * <p>
    * --folder4 </ul>
    * <p>
    * Query : Select all folders that are in folder1.
    * <p>
    * Expected result: folder3
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testFolderInFolderConstrain() throws Exception
   {
      // create data
      EntryImpl folder1 = this.createFolder(root, "folder1");
      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      EntryImpl folder2 = this.createFolder(root, "folder2");
      EntryImpl doc2 = createDocument(folder2.getObjectId(), "node2", "hello world".getBytes(), "text/plain");

      EntryImpl folder3 = this.createFolder(folder1.getObjectId(), "folder3");
      EntryImpl doc3 = createDocument(folder3.getObjectId(), "node3", "hello world".getBytes(), "text/plain");

      EntryImpl folder4 = this.createFolder(folder3.getObjectId(), "folder4");

      String statement =
         "SELECT * FROM " + JcrCMIS.NT_FOLDER + " WHERE IN_FOLDER( '" + folder1.getObjectId() + "')";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check results
      checkResult(result, new Entry[]{folder3});

   }

   /**
    * Test JOIN with condition constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>folder1: <b>folderName</b> - folderOne
    * <li>doc1: <b>Title</b> - node1 <b>parentFolderName</b> - folderOne
    * <li>folder2: b>folderName</b> - folderTwo
    * <li>doc2: <b>Title</b> - node2 <b>parentFolderName</b> - folderThree
    * </ul>
    * <p>
    * Query : Select all documents and folders where folders folderName equal to
    * document parentFolderName.
    * <p>
    * Expected result: doc1 and folder1
    * 
    * @throws Exception if an unexpected error occurs
    */
   // XXX temporary excluded
   public void _testJoinWithCondition() throws Exception
   {
      // create data
      EntryImpl folder1 = this.createFolder(root, "folder1");
      folder1.setString("folderName", "folderOne");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");
      doc1.setString("parentFolderName", "folderOne");

      EntryImpl folder2 = this.createFolder(root, "folder2");
      folder2.setString("folderName", "folderTwo");

      EntryImpl doc2 = createDocument(folder2.getObjectId(), "node1", "hello world".getBytes(), "text/plain");
      doc2.setString("parentFolderName", "folderThree");

      String statement =
         "SELECT doc.* FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " AS doc LEFT JOIN "
            + JcrCMIS.NT_FOLDER + " AS folder ON (doc.parentFolderName = folder.folderName)";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(1, result.size());
      // TODO check results - must doc1 and folder1
   }

   /**
    * Test LIKE constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>prop</b> - administrator
    * <li>doc2: <b>Title</b> - node2 <b>prop</b> - admin
    * <li>doc3: <b>Title</b> - node2 <b>prop</b> - radmin
    * </ul>
    * <p>
    * Query : Select all documents where prop begins with "ad".
    * <p>
    * Expected result: doc1, doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testLIKEConstraint() throws Exception
   {
      EntryImpl doc1 = createDocument(root, "node1", "hello world".getBytes(), "text/plain");
      doc1.setString("prop", "administrator");

      EntryImpl doc2 = createDocument(root, "node2", "hello world".getBytes(), "text/plain");
      doc2.setString("prop", "admin");

      EntryImpl doc3 = createDocument(root, "node3", "hello world".getBytes(), "text/plain");
      doc3.setString("prop", "radmin");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " AS doc WHERE prop LIKE 'ad%'";

      Query query = new Query(statement, true);

      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check results
      assertEquals(2, result.size());
      checkResult(result, new Entry[]{doc1, doc2});
   }

   /**
    * Test LIKE constraint with escape symbols.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>prop</b> - ad%min master
    * <li>doc2: <b>Title</b> - node2 <b>prop</b> - admin operator
    * <li>doc3: <b>Title</b> - node2 <b>prop</b> - radmin
    * </ul>
    * <p>
    * Query : Select all documents where prop like 'ad\\%min%'.
    * <p>
    * Expected result: doc1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testLIKEConstraintEscapeSymbols() throws Exception
   {
      EntryImpl doc1 = createDocument(root, "node1", "hello world".getBytes(), "text/plain");
      doc1.setString("prop", "ad%min master");

      EntryImpl doc2 = createDocument(root, "node2", "hello world".getBytes(), "text/plain");
      doc2.setString("prop", "admin operator");

      EntryImpl doc3 = createDocument(root, "node3", "hello world".getBytes(), "text/plain");
      doc3.setString("prop", "radmin");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " AS doc WHERE prop LIKE 'ad\\%min%'";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check results
      assertEquals(1, result.size());
      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test NOT constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>content</b> - hello world
    * <li>doc2: <b>Title</b> - node2 <b>content</b> - hello
    * </ul>
    * <p>
    * Query : Select all documents that not contains "world" word.
    * <p>
    * Expected result: doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testNOTConstraint() throws Exception
   {

      EntryImpl folder1 = createFolder(root, "folfer1");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      EntryImpl folder2 = createFolder(root, "folder2");
      EntryImpl doc2 = createDocument(folder2.getObjectId(), "node2", "hello".getBytes(), "text/plain");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE NOT CONTAINS(\"world\")";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test NOT IN constraint.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
    * </ul>
    * <p>
    * Query : Select all documents where long property not in set {15 , 20}.
    * <p>
    * Expected result: doc1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testNotINConstraint() throws Exception
   {

      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = this.createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setDecimal("long", new BigDecimal(3));

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setDecimal("long", new BigDecimal(15));

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE long NOT IN (15, 20)";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test NOT NOT (not counteraction).
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
    * </ul>
    * <p>
    * Query : Select all documents where long property in set {15 , 20} (NOT NOT
    * IN).
    * <p>
    * Expected result: doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testNotNotINConstraint() throws Exception
   {

      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setDecimal("long", new BigDecimal(3));

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setDecimal("long", new BigDecimal(15));

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE NOT (long NOT IN (15, 20))";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test Order By desc.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
    * </ul>
    * <p>
    * Query : Order by exo:Commander property value
    * <p>
    * Expected result: doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testOrderByFieldDesc() throws Exception
   {

      EntryImpl folder = createFolder(root, "testColumn");
      List<EntryImpl> appolloContent = createApolloContent(folder);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT  ");
      sql.append(CMIS.LAST_MODIFIED_BY + " , ");
      sql.append(CMIS.OBJECT_ID + " , ");
      sql.append(CMIS.LAST_MODIFICATION_DATE);
      sql.append(" FROM ");
      sql.append(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      sql.append(" ORDER BY ");
      sql.append(PROPERTY_COMMANDER);
      sql.append(" DESC");

      String statement = sql.toString();

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      // Walter M. Schirra (0)
      // James A. Lovell, Jr. (2)
      // Frank F. Borman, II (1)

      checkResultOrder(result, new Entry[]{appolloContent.get(0), appolloContent.get(2), appolloContent.get(1)});
   }

   /**
    * Test ORDER BY ASC.
    * <p>
    * Initial data: see createApolloContent
    * <p>
    * Query : Select all documents and order by propertyComander values
    * ascending.
    * <p>
    * Expected result: doc2, doc3, doc1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testOrderByFieldAsk() throws Exception
   {

      EntryImpl folder = createFolder(root, "testColumn");
      List<EntryImpl> appolloContent = createApolloContent(folder);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append(CMIS.LAST_MODIFIED_BY + ", ");
      sql.append(CMIS.OBJECT_ID + ", ");
      sql.append(CMIS.LAST_MODIFICATION_DATE);
      sql.append(" FROM ");
      sql.append(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      sql.append(" ORDER BY ");
      sql.append(PROPERTY_COMMANDER);

      String statement = sql.toString();

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      // Frank F. Borman, II (1)
      // James A. Lovell, Jr. (2)
      // Walter M. Schirra (0)

      checkResultOrder(result, new Entry[]{appolloContent.get(1), appolloContent.get(2), appolloContent.get(0)});
   }

   /**
    * Test ORDER BY default.
    * <p>
    * Initial data: see createApolloContent
    * <p>
    * Query : Select all documents and order by propertyComander values
    * ascending.
    * <p>
    * Expected result: doc3, doc1, doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testOrderByDefault() throws Exception
   {

      EntryImpl folder = createFolder(root, "testColumn");
      List<EntryImpl> appolloContent = createApolloContent(folder);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append(CMIS.LAST_MODIFIED_BY + ", ");
      sql.append(CMIS.OBJECT_ID + ", ");
      sql.append(CMIS.LAST_MODIFICATION_DATE);
      sql.append(" FROM ");
      sql.append(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());

      String statement = sql.toString();

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      // Apollo 13 (2)
      // Apollo 7 (0)
      // Apollo 8 (1)
      checkResultOrder(result, new Entry[]{appolloContent.get(2), appolloContent.get(0), appolloContent.get(1)});
   }

   /**
    * Test ORDER BY SCORE().
    * <p>
    * Initial data: see createApolloContent
    * <p>
    * Query : Select all documents and order by propertyComander values
    * ascending.
    * <p>
    * Expected result: doc3, doc1, doc2
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testOrderByScore() throws Exception
   {

      EntryImpl folder = createFolder(root, "testColumn");
      List<EntryImpl> appolloContent = createApolloContent(folder);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append(" SCORE() AS scoreCol, ");
      sql.append(CMIS.LAST_MODIFIED_BY + ", ");
      sql.append(CMIS.OBJECT_ID + ", ");
      sql.append(CMIS.LAST_MODIFICATION_DATE);
      sql.append(" FROM ");
      sql.append(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      sql.append(" WHERE CONTAINS(\"moon\") ");
      sql.append(" ORDER BY SCORE() ");

      String statement = sql.toString();

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      // Apollo 13 (2)
      // Apollo 8 (1)
      checkResultOrder(result, new Entry[]{appolloContent.get(1), appolloContent.get(2)});
   }

   /**
    * Test property existence constraint (IS [NOT] NULL) .
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>prop</b> - test string
    * <li>doc2: <b>Title</b> - node2
    * </ul>
    * <p>
    * Query : Select all documents that has "prop" property (IS NOT NULL).
    * <p>
    * Expected result: doc1
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testPropertyExistence() throws Exception
   {
      EntryImpl folder1 = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");
      doc1.setString("prop", "test string");

      EntryImpl doc2 = createDocument(folder1.getObjectId(), "node2", "hello".getBytes(), "text/plain");

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE prop IS NOT NULL";
      Query query = new Query(statement, true);

      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test SCORE as column.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>content</b> - hello world
    * <li>doc2: <b>Title</b> - node2 <b>content</b> - hello
    * </ul>
    * <p>
    * Query : Select all documents that contains hello or world words, and show
    * search score .
    * <p>
    * Expected result: doc1 and doc2 score numbers.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testScoreAsColumn() throws Exception
   {
      EntryImpl folder1 = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      EntryImpl doc2 = createDocument(folder1.getObjectId(), "node2", "hello".getBytes(), "text/plain");

      String statement =
         "SELECT SCORE() AS scoreCol , " + CMIS.NAME + " AS id FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()
            + " WHERE CONTAINS(\"hello OR world\") ORDER BY SCORE()";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      // check result
      while (result.hasNext())
      {
         Result next = result.next();
         assertTrue(next.getScore().getScoreValue().doubleValue() > 0);
      }

   }

   /**
    * Test IN_TREE constraint.
    * <p>
    * Initial data:
    * <p>
    * folder1
    * <p>
    * - document doc1
    * <p>
    * - folder2
    * <p>
    * -- document doc2
    * <p>
    * Query : Select all documents that are in tree of folder1.
    * <p>
    * Expected result: doc1,doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testTreeConstrain() throws Exception
   {
      // create data
      EntryImpl folder1 = createFolder(root, "folder1");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      EntryImpl subfolder1 = createFolder(folder1.getObjectId(), "folder2");

      EntryImpl doc2 = createDocument(subfolder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE IN_TREE('" + folder1.getObjectId()
            + "')";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new EntryImpl[]{doc1, doc2});
   }

   /**
    * Test not equal comparison (<>).
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
    * </ul>
    * <p>
    * Query : Select all documents property long not equal to 3.
    * <p>
    * Expected result: doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testNotEqualDecimal() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "NotEqualDecimal");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setDecimal("long", new BigDecimal(3));
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setDecimal("long", new BigDecimal(15));
      doc2.save();

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE long <> 3";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test more than comparison (>).
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>long</b> - 3
    * <li>doc2: <b>Title</b> - node2 <b>long</b> - 15
    * </ul>
    * <p>
    * Query : Select all documents property long more than 5.
    * <p>
    * Expected result: doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testMoreThanDecimal() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = this.createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setDecimal("long", new BigDecimal(3));
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setDecimal("long", new BigDecimal(15));
      doc2.save();

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE long > 5";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test not equal comparison (<>) string.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>strprop</b> - test word first
    * <li>doc2: <b>Title</b> - node2 <b>strprop</b> - test word second
    * </ul>
    * <p>
    * Query : Select all documents property strprop not equal to
    * "test word second".
    * <p>
    * Expected result: doc1.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testNotEqualString() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setString("strprop", "test word first");

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setString("strprop", "test word second");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE strprop <> 'test word second'";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test fulltext search from jcr:content.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>content</b> - "There must be test word"
    * <li>doc2: <b>Title</b> - node2 <b>content</b> - " Test word is not here"
    * </ul>
    * <p>
    * Query : Select all documents that contains "here" word.
    * <p>
    * Expected result: doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testSimpleFulltext() throws Exception
   {
      // create data
      String name1 = "fileFirst";
      String name2 = "fileSecond";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "SimpleFullTextTest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name1, new byte[0], contentType);
      doc1.setString("strprop", "There must be test word");
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setString("strprop", " Test word is not here");
      doc2.save();

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE CONTAINS(\"here\")";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test complex fulltext query.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>strprop</b> - "There must be test word"
    * <li>doc2: <b>Title</b> - node2 <b>strprop</b> -
    * " Test word is not here. Another check-word."
    * <li>doc3: <b>Title</b> - node3 <b>strprop</b> - "There must be check-word."
    * </ul>
    * <p>
    * Query : Select all documents that contains "There must" phrase and do not
    * contain "check-word" word.
    * <p>
    * Expected result: doc1.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testExtendedFulltext() throws Exception
   {
      // create data
      String name1 = "fileCS1.doc";
      String name2 = "fileCS2.doc";
      String name3 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name1, new byte[0], contentType);
      doc1.setString("strprop", "There must be test word");

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setString("strprop", " Test word is not here. Another check-word.");

      EntryImpl doc3 = createDocument(folder.getObjectId(), name3, new byte[0], contentType);
      doc3.setString("strprop", "There must be check-word.");

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()
            + " WHERE CONTAINS(\"\\\"There must\\\" -\\\"check\\-word\\\"\")";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Same as testNOTConstraint.
    */
   public void testNotContains() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "NotContains");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setString("strprop", "There must be test word");
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setString("strprop", " Test word is not here");
      doc2.save();

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE NOT CONTAINS(\"here\")";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc1});
   }

   /**
    * Test comparison of boolean property.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>boolprop</b> - true
    * <li>doc2: <b>Title</b> - node2 <b>boolprop</b> - false
    * </ul>
    * <p>
    * Query : Select all documents where boolprop equal to false.
    * <p>
    * Expected result: doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testBooleanConstraint() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = this.createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);
      doc1.setBoolean("boolprop", true);
      doc1.save();

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);
      doc2.setBoolean("boolprop", false);
      doc2.save();

      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value() + " WHERE (boolprop = FALSE )";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc2});
   }

   /**
    * Test comparison of date property.
    * <p>
    * Initial data:
    * <ul>
    * <li>doc1: <b>Title</b> - node1 <b>dateProp</b> - 2009-08-08
    * <li>doc2: <b>Title</b> - node2 <b>dateProp</b> - 2009-08-08
    * </ul>
    * <p>
    * Query : Select all documents where dateProp more than 2007-01-01.
    * <p>
    * Expected result: doc2.
    * 
    * @throws Exception if an unexpected error occurs
    */
   public void testDateConstraint() throws Exception
   {
      // create data
      String name = "fileCS2.doc";
      String name2 = "fileCS3.doc";
      String contentType = "text/plain";

      EntryImpl folder = createFolder(root, "CASETest");

      EntryImpl doc1 = createDocument(folder.getObjectId(), name, new byte[0], contentType);

      EntryImpl doc2 = createDocument(folder.getObjectId(), name2, new byte[0], contentType);

      String statement =
         "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()
            + " WHERE ( cmis:lastModificationDate >= TIMESTAMP '2007-01-01T00:00:00.000Z' )";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      checkResult(result, new Entry[]{doc1, doc2});
   }

}
