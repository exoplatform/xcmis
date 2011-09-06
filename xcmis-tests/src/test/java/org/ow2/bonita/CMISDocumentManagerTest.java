package org.ow2.bonita;
/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.ow2.bonita.DocumentAlreadyExistsException;
import org.ow2.bonita.DocumentNotFoundException;
import org.ow2.bonita.DocumentationCreationException;
import org.ow2.bonita.FolderAlreadyExistsException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.CMISDocumentManager;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentImpl;
import org.ow2.bonita.services.DocumentIndex;
import org.ow2.bonita.services.DocumentSearchBuilder;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.SearchResult;

/**
 * @author Baptiste Mesta
 * 
 */
public class CMISDocumentManagerTest extends TestCase {

   protected static DocumentationManager manager;

   public static void setUpClass() {
   }

   public void setUp() {
      System.out.println("Test running = " + getName());
      
      String cmisRepositoryUrl = System.getProperty("cmisRepositoryUrl");
      String cmisRepositoryId = System.getProperty("cmisRepositoryId");
      String cmisUsername = System.getProperty("cmisUsername");
      String cmisPassword = System.getProperty("cmisPassword");

      if (cmisRepositoryUrl == null
            || "${cmisRepositoryUrl}".intern().equals(cmisRepositoryUrl)) {
         throw new RuntimeException(
               "setUp: you should set the system property 'cmisRepositoryUrl'");
      }
      if (cmisRepositoryId == null
            || "${cmisRepositoryId}".intern().equals(cmisRepositoryId)) {
         throw new RuntimeException(
               "setUp: you should set the system property 'cmisRepositoryId'");
      }
      if (cmisUsername == null
            || "${cmisUsername}".intern().equals(cmisUsername)) {
         throw new RuntimeException(
               "setUp: you should set the system property 'cmisUsername'");
      }
      if (cmisPassword == null
            || "${cmisPassword}".intern().equals(cmisPassword)) {
         throw new RuntimeException(
               "setUp: you should set the system property 'cmisPassword'");
      }

      System.out.println("\t cmisRepositoryUrl = " + cmisRepositoryUrl
            + ",\n\t repositoryId = " + cmisRepositoryId + ", userId = "
            + cmisUsername + ", password = " + cmisPassword);

      manager = new CMISDocumentManager("ATOM", cmisRepositoryUrl,
            cmisRepositoryId, true, cmisUsername, cmisPassword);
      Folder rootFolder = manager.getRootFolder();
      try { 
         manager.clear();
      } catch (DocumentNotFoundException e) {
         e.printStackTrace();
      }
   }

   public void testCreateFolder() throws FolderAlreadyExistsException {
      Folder folderId = manager.createFolder("testCreateFolder");
      assertNotNull(folderId);
      assertFalse("".equals(folderId.getId()));
   }

   public void testCreateTwoFolders() throws FolderAlreadyExistsException {
      Folder folderId1 = manager.createFolder("testCreateFolder1");
      Folder folderId2 = manager.createFolder("testCreateFolder2");
      assertNotSame(folderId1, folderId2);
   }

   public void testCannotCreateTwoFolderWithSameNameAtSameLevel()
         throws FolderAlreadyExistsException {

      manager.createFolder("testCannotCreateTwoFolderWithSameNameAtSameLevel");
      try {
         manager
               .createFolder("testCannotCreateTwoFolderWithSameNameAtSameLevel");
         fail("must not be able to create 2 document with same name in same folder");
      } catch (FolderAlreadyExistsException e) {
      }
   }

   public void testCreateSubFolder() throws FolderAlreadyExistsException {
      Folder folder1 = manager.createFolder("testCreateSubFolder");
      Folder subFodler = manager.createFolder("subFolder", folder1.getId());
      assertNotNull(subFodler);
      assertEquals(subFodler, manager.getChildrenFolder(folder1.getId()).get(0));
   }

   public void testCreateSubFolderWithSameName()
         throws FolderAlreadyExistsException {
      Folder folder1 = manager.createFolder("testCreateSubFolderWithSameName");
      Folder subFodler = manager.createFolder(
            "testCreateSubFolderWithSameName", folder1.getId());
      assertNotSame(folder1.getId(), subFodler.getId());
   }

   public void testFolderDoesNotExists() {
      assertFalse(manager.folderExists("testFolder2"));
   }

   public void testFolderExists() throws FolderAlreadyExistsException {
      manager.createFolder("testFolder");
      assertTrue(manager.folderExists("testFolder"));
   }

   public void testFolderExistsWithParent() throws FolderAlreadyExistsException {

      String parentId = manager.createFolder("testFolderExistsWithParent")
            .getId();
      manager.createFolder("childFolder", parentId);
      assertTrue(manager.folderExists("childFolder", parentId));
   }

   public void testGetFolderUsingName() throws FolderAlreadyExistsException {

      String folderName = "nameOfTheFolder";
      Folder folder1 = manager.createFolder(folderName);

      List<Folder> folders = manager.getFolders(folderName);
      assertNotNull(folders);
      assertEquals(1, folders.size());
      assertEquals(folder1, folders.get(0));// TODO implements equals on folder
      // object
   }

   public void testGetFolderUsingNameWithUnexistingFolder()
         throws FolderAlreadyExistsException {

      String folderName = "nameOfTheUnexistingFolder";
      List<Folder> folders = manager.getFolders(folderName);
      assertNotNull(folders);
      assertEquals(0, folders.size());
   }

   public void testGetSubFolder() throws FolderAlreadyExistsException {

      Folder folder1 = manager.createFolder("testGetSubFolder");
      Folder subFolder = manager.createFolder("subFolder", folder1.getId());

      List<Folder> subFolders = manager.getChildrenFolder(folder1.getId());
      assertNotNull(subFolders);
      assertEquals(1, subFolders.size());
      assertEquals(subFolder, subFolders.get(0));
   }

   public void testNoDocumentInFolder() throws FolderAlreadyExistsException {

      Folder folder1 = manager.createFolder("testGetDocument");

      List<Document> documents = manager.getChildrenDocuments(folder1.getId());
      assertEquals(0, documents.size());
   }

   // FIXME add test with document in root folder

   public void testCreateDocument() throws Exception {
      // String author = "john";
      String docName = "theDoc";
      Document doc = manager.createDocument(docName, new ProcessDefinitionUUID(
            "a"), new ProcessInstanceUUID("b"));
      assertNotNull(doc);
      assertNotNull(doc.getId());
      assertEquals(docName, doc.getName());
      assertNotNull(doc.getCreationDate());
      // assertEquals(doc.getCreationDate(),doc.getLastModificationDate());
      // assertEquals(author,doc.getAuthor());
      assertEquals(doc.getAuthor(), doc.getLastModifiedBy());
      assertNotNull(doc.getVersionLabel());
      assertNotNull(doc.getVersionSeriesId());
      assertTrue(doc.isMajorVersion());
      assertTrue(doc.isLatestVersion());
   }

   public void testCreateADocumentThatAlreadyExists() throws Exception {
      String docName = "myDocument";
      manager.createDocument(docName, new ProcessDefinitionUUID("a"),
            new ProcessInstanceUUID("b"));
      try {
         manager.createDocument(docName, new ProcessDefinitionUUID("a"),
               new ProcessInstanceUUID("b"));
         fail("must throw a document already exists exception");
      } catch (DocumentationCreationException e) {

      }
   }

   // FIXME test on delete folder + delete document

   public void testCreateDocumentWithContent() throws Exception {
      byte[] contents = "The doc contents".getBytes();
      String fileName = "testFile.txt";
      // String author = "john";
      String docName = "theDoc";
      String contentMimeType = "kikoo/text";
      ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID("a");
      ProcessInstanceUUID instanceUUID = new ProcessInstanceUUID("b");
      Document doc = manager.createDocument(docName, definitionUUID,
            instanceUUID, fileName, contentMimeType, contents);
      assertNotNull(doc);
      assertNotNull(doc.getId());
      assertEquals(docName, doc.getName());
      assertNotNull(doc.getCreationDate());
      // assertEquals(doc.getCreationDate(), doc.getLastModificationDate());
      // assertEquals(author, doc.getAuthor());
      assertEquals(doc.getAuthor(), doc.getLastModifiedBy());
      assertNotNull(doc.getVersionLabel());
      assertNotNull(doc.getVersionSeriesId());
      assertTrue(doc.isMajorVersion());
      assertEquals(fileName, doc.getContentFileName());// FIXME
      assertTrue(doc.isLatestVersion());
      assertNotNull(doc.getContentFileName());
      assertNotNull(doc.getContentMimeType());
      assertTrue(0 <= doc.getContentSize());
      assertEquals(contentMimeType, doc.getContentMimeType());// FIXME
      assertEquals(contents.length, doc.getContentSize());
      assertEquals(definitionUUID, doc.getProcessDefinitionUUID());
      assertEquals(instanceUUID, doc.getProcessInstanceUUID());
      assertEquals("The doc contents", new String(manager.getContent(doc)));
   }

   public void testCreateDocumentWithContentWithoutContentSize()
         throws Exception {
      byte[] contents = "The doc contents".getBytes();
      String fileName = "testFile.txt";
      String docName = "theDoc";
      String contentMimeType = "kikoo/text";
      Document doc = manager.createDocument(docName, new ProcessDefinitionUUID(
            "myprocessxx"), new ProcessInstanceUUID("instancexx"), fileName,
            contentMimeType, contents);
      assertEquals(contents.length, doc.getContentSize());
      assertEquals("The doc contents", new String(manager.getContent(doc)));
   }

   public void testCreateDocumentWithEmptyContent() throws Exception {
      byte[] contents = new byte[1];
      String fileName = "testFile.txt";
      // String author = "john";
      String docName = "theDoc";
      String contentMimeType = "plain/text";
      ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID("a");
      ProcessInstanceUUID instanceUUID = new ProcessInstanceUUID("b");
      Document doc = manager.createDocument(docName, definitionUUID,
            instanceUUID, fileName, contentMimeType, contents);
      assertNotNull(doc);
      assertNotNull(doc.getId());
      assertEquals(docName, doc.getName());
      assertNotNull(doc.getCreationDate());
      // assertEquals(doc.getCreationDate(), doc.getLastModificationDate());
      // assertEquals(author, doc.getAuthor());
      assertEquals(doc.getAuthor(), doc.getLastModifiedBy());
      assertNotNull(doc.getVersionLabel());
      assertNotNull(doc.getVersionSeriesId());
      assertTrue(doc.isMajorVersion());
      assertTrue(doc.isLatestVersion());
      assertEquals(fileName, doc.getContentFileName());// FIXME
      assertNotNull(doc.getContentFileName());
      assertNotNull(doc.getContentMimeType());
      assertTrue(0 <= doc.getContentSize());
      assertEquals(contentMimeType, doc.getContentMimeType());// FIXME
      assertEquals(definitionUUID, doc.getProcessDefinitionUUID());
      assertEquals(instanceUUID, doc.getProcessInstanceUUID());
      assertEquals(contents.length, manager.getContent(doc).length);
   }

   public void testGetDocument() throws Exception {
      Document doc = manager.createDocument("theDoc",
            new ProcessDefinitionUUID("a"), new ProcessInstanceUUID("b"),
            "testFile.txt", "plain/text", "The doc contents".getBytes());
      Document document = manager.getDocument(doc.getId());
      assertNotNull(document);
      assertEquals(doc, document);
   }

   public void testDeleteAllVersionsOfDocumentById() throws Exception {
      Document doc = manager.createDocument("theDoc",
            new ProcessDefinitionUUID("a"), new ProcessInstanceUUID("b"),
            "testFile.txt", "plain/text", "The doc contents".getBytes());
      manager.deleteDocument(doc.getId(), true);
      try {
         manager.getDocument(doc.getId());
         fail("should throw DocumentNotFoundException");
      } catch (DocumentNotFoundException e) {
      }
   }

   public void testCreateDocumentInSubFolder()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException {
      Folder folder = manager.createFolder("testCreateDocumentInSubFolder");
      Folder subFolder = manager.createFolder("subFolder", folder.getId());

      Document doc = manager.createDocument("theDoc",
            new ProcessDefinitionUUID("testCreateDocumentInSubFolder"),
            new ProcessInstanceUUID("subFolder"), "testFile.txt", "plain/text",
            "The doc contents".getBytes());
      assertEquals(subFolder.getId(), doc.getParentFolderId());
   }

   public void testCreateNewVersionOfDocument()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException, DocumentNotFoundException {
      byte[] contents = "The doc contents".getBytes();
      String fileName = "testFile.txt";
      String docName = "theDoc";
      String contentMimeType = "plain/text";
      Document doc = manager.createDocument(docName, new ProcessDefinitionUUID(
            "a"), new ProcessInstanceUUID("b"), fileName, contentMimeType,
            contents);

      // String newAuthor = "james";
      Document newDoc = manager.createVersion(doc.getId(), true,
            "testFile2.txt", "plain/text", "The new doc contents".getBytes());

      assertNotNull(newDoc);
      assertEquals(doc.getName(), newDoc.getName());
      // assertEquals("The doc contents", new String(manager.getContent(doc)));
      // //not supported by xCmis
      assertEquals("The new doc contents", new String(manager
            .getContent(newDoc)));
      assertEquals(doc.getAuthor(), newDoc.getAuthor());
      // assertEquals(newAuthor, newDoc.getLastModifiedBy());
      assertEquals("testFile2.txt", newDoc.getContentFileName());
      assertEquals("plain/text", newDoc.getContentMimeType());
   }

   public void testCreateNewVersionOfDocumentWithSameName() throws Exception {
      String fileName = "testFile.txt";
      String docName = "theDoc";
      String contentMimeType = "plain/text";
      Document doc = manager.createDocument(docName, new ProcessDefinitionUUID(
            "a"), new ProcessInstanceUUID("b"), fileName, contentMimeType,
            "The doc contents".getBytes());

      // String newAuthor = "james";
      Document newDoc = manager.createVersion(doc.getId(), true,
            "testFile2.txt", "plain/text", "The new doc contents".getBytes());

      assertNotNull(newDoc);
      assertEquals(doc.getName(), newDoc.getName());
      assertEquals(doc.getAuthor(), newDoc.getAuthor());
      // assertEquals(newAuthor, newDoc.getLastModifiedBy());
      assertEquals("testFile2.txt", newDoc.getContentFileName());
      assertEquals("plain/text", newDoc.getContentMimeType());
      assertEquals("The new doc contents", new String(manager
            .getContent(newDoc)));
   }

   public void testGetVersionsOfDocument()
         throws DocumentationCreationException, DocumentAlreadyExistsException,
         FolderAlreadyExistsException {
      
      Folder folder = manager.createFolder("testGetOldVersionOfDocument");
      String fileName = "testFile.txt";
      String docName = "theDoc";
      String contentMimeType = "plain/text";
      Document doc = manager.createDocument(docName, folder.getId(), fileName,
            contentMimeType, "The doc contents".getBytes());
      
      Document newDoc = manager.createVersion(doc.getId(), true,
            "testFile2.txt", "test/text", "The new doc contents".getBytes());

      List<Document> versions = manager.getVersionsOfDocument(newDoc.getId());
      // must
      // be
      // an
      // id
      assertEquals(2, versions.size());
      assertEquals(newDoc, versions.get(0));
      assertEquals(doc.getId(), versions.get(1).getId());
      assertTrue(doNotCheckId(doc, versions.get(1)));
       
      // test content
   }


   public void testSearchDocumentByName() throws FolderAlreadyExistsException,
         DocumentationCreationException, DocumentAlreadyExistsException {
      Folder folder = manager.createFolder("testSearchDocumentById");
      Document doc1 = manager.createDocument("theDoc1", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Document doc2 = manager.createDocument("theDoc2", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Document doc3 = manager.createDocument("theDoc3", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.NAME).equalsTo("theDoc2");
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(1, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(1, results.size());
      assertEquals(doc2, results.get(0));
   }

   public void testSearchWithNoResults() throws FolderAlreadyExistsException,
         DocumentationCreationException, DocumentAlreadyExistsException {
      Folder folder = manager.createFolder("testSearchWithNoResults");
      Document doc1 = manager.createDocument("theDoc1", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Document doc2 = manager.createDocument("theDoc2", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Document doc3 = manager.createDocument("theDoc3", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.NAME).equalsTo("theDoc4");
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(0, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(0, results.size());
   }

   public void testSearchDocumentByCreationDate()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException, InterruptedException {
      Folder folder = manager.createFolder("testSearchDocumentById");

      Document doc1 = manager.createDocument("theDoc1", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Thread.sleep(100);
      Date date1 = new Date();
      Thread.sleep(100);
      Document doc2 = manager.createDocument("theDoc2", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Thread.sleep(100);
      Date date2 = new Date();
      Thread.sleep(100);
      Document doc3 = manager.createDocument("theDoc3", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.CREATION_DATE)
            .between(date1, date2);
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(1, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(1, results.size());
      assertEquals(doc2, results.get(0));
   }

   public void testSearchDocumentByProcessDefUUID()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException, InterruptedException {
      Folder folder = manager.createFolder("theProcessDefUUID");
      Folder subFolder = manager.createFolder("theProcessInstUUID", folder
            .getId());
      Document doc1 = manager.createDocument("theDoc1", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Document doc2 = manager.createDocument("theDoc2", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Document doc3 = manager.createDocument("theDoc3", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.PROCESS_DEFINITION_UUID).equalsTo(
            "theProcessDefUUID");
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(3, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(3, results.size());
      assertEquals(doc1, results.get(0));
      assertEquals(doc2, results.get(1));
      assertEquals(doc3, results.get(2));
   }

   public void testSearchDocumentByProcessInstUUID()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException, InterruptedException {
      Folder folder = manager.createFolder("theProcessDefUUID");
      Folder subFolder = manager.createFolder("theProcessInstUUID", folder
            .getId());
      Document doc1 = manager.createDocument("theDoc1", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Document doc2 = manager.createDocument("theDoc2", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Document doc3 = manager.createDocument("theDoc3", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.PROCESS_INSTANCE_UUID).equalsTo(
            "theProcessInstUUID");
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(3, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(3, results.size());
      assertEquals(doc1, results.get(0));
      assertEquals(doc2, results.get(1));
      assertEquals(doc3, results.get(2));
   }

   // not supported with not modified authenticator
   // public void testSearchDocumentByAuthor() throws
   // FolderAlreadyExistsException, DocumentationCreationException,
   // DocumentAlreadyExistsException, InterruptedException{
   // //TODO login with john on bonita
   // Folder folder = manager.createFolder("testSearchDocumentByAuthor");
   // Document doc1 = manager.createDocument("theDoc1", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents1".getBytes());
   // Document doc2 = manager.createDocument("theDoc2", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents2".getBytes());
   // Document doc3 = manager.createDocument("theDoc3", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents3".getBytes());
   // DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
   // searchBuilder.criterion(DocumentIndex.AUTHOR).equalsTo("john");
   // SearchResult searchResult = manager.search(searchBuilder,0,10);
   // assertEquals(3, searchResult.getCount());
   // List<Document> results = searchResult.getDocuments();
   // assertEquals(3, results.size());
   // assertEquals(doc1, results.get(0));
   // assertEquals(doc2, results.get(1));
   // assertEquals(doc3, results.get(2));
   // }

   public void testSearchByProcessDefUUIDAndName()
         throws FolderAlreadyExistsException, DocumentationCreationException,
         DocumentAlreadyExistsException, InterruptedException {
      Folder proc1Folder = manager.createFolder("proc1");
      Folder proc1InstFolder = manager.createFolder("inst1", proc1Folder
            .getId());
      Folder proc2Folder = manager.createFolder("proc2");
      Folder proc2InstFolder = manager.createFolder("inst1", proc2Folder
            .getId());
      manager.createDocument("theDoc1", proc1InstFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents11".getBytes());
      manager.createDocument("theDoc2", proc1InstFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents12".getBytes());
      manager.createDocument("theDoc1", proc2InstFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents21".getBytes());
      Document doc22 = manager.createDocument("theDoc2", proc2InstFolder
            .getId(), "testFile.txt", "plain/text", "The doc contents22"
            .getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.PROCESS_DEFINITION_UUID).equalsTo(
            "proc2").and().criterion(DocumentIndex.NAME).equalsTo("theDoc2");
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(1, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(1, results.size());
      assertEquals(doc22, results.get(0));
   }

   public void testSearchOnLatestVersion() throws FolderAlreadyExistsException,
         DocumentationCreationException, DocumentAlreadyExistsException,
         InterruptedException {
      Folder folder = manager.createFolder("latestVersion");
      Document document = manager.createDocument("theDoc1", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents11".getBytes());
      Document lastVersion = manager.createVersion(document.getId(), true);

      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.PROCESS_DEFINITION_UUID).equalsTo("latestVersion").latestVersion();
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(1, searchResult.getCount());  // was:1
      List<Document> results = searchResult.getDocuments();
      assertEquals(1, results.size());  // was:1
      assertEquals(lastVersion, results.get(0));
   }

   public void testSearchWithInClause() throws DocumentAlreadyExistsException,
         DocumentationCreationException, FolderAlreadyExistsException {
      Folder folder = manager.createFolder("theProcessDefUUID");
      Folder subFolder = manager.createFolder("theProcessInstUUID", folder
            .getId());
      Document doc1 = manager.createDocument("theDoc1", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents1".getBytes());
      Document doc2 = manager.createDocument("theDoc2", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents2".getBytes());
      Document doc3 = manager.createDocument("theDoc3", subFolder.getId(),
            "testFile.txt", "plain/text", "The doc contents3".getBytes());
      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();

      searchBuilder.criterion(DocumentIndex.ID).in(
            Arrays.asList(new String[] { doc1.getId(), doc3.getId() }));
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(2, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(2, results.size());
      assertEquals(doc1, results.get(0));
      assertEquals(doc3, results.get(1));
   }

   // not supported with not modified authenticator
   // public void testSearchDocumentByAuthor() throws
   // FolderAlreadyExistsException, DocumentationCreationException,
   // DocumentAlreadyExistsException, InterruptedException{
   // //TODO login with john on bonita
   // Folder folder = manager.createFolder("testSearchDocumentByAuthor");
   // Document doc1 = manager.createDocument("theDoc1", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents1".getBytes());
   // Document doc2 = manager.createDocument("theDoc2", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents2".getBytes());
   // Document doc3 = manager.createDocument("theDoc3", folder.getId(),
   // "testFile.txt", "plain/text",
   // -1, "The doc contents3".getBytes());
   // DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
   // searchBuilder.criterion(DocumentIndex.AUTHOR).equalsTo("john");
   // SearchResult searchResult = manager.search(searchBuilder,0,10);
   // assertEquals(3, searchResult.getCount());
   // List<Document> results = searchResult.getDocuments();
   // assertEquals(3, results.size());
   // assertEquals(doc1, results.get(0));
   // assertEquals(doc2, results.get(1));
   // assertEquals(doc3, results.get(2));
   // }

   // Not supported
   // CMIS-277 Support of the search on versioned content
   public void testSearchOnAllVersion() throws FolderAlreadyExistsException,
         DocumentationCreationException, DocumentAlreadyExistsException,
         InterruptedException {
      Folder folder = manager.createFolder("allVersion");
      Document document = manager.createDocument("theDoc1", folder.getId(),
            "testFile.txt", "plain/text", "The doc contents11".getBytes());
      Document lastVersion = manager.createVersion(document.getId(), true);

      DocumentSearchBuilder searchBuilder = new DocumentSearchBuilder();
      searchBuilder.criterion(DocumentIndex.PROCESS_DEFINITION_UUID).equalsTo(
            "allVersion").allVersion();
      SearchResult searchResult = manager.search(searchBuilder, 0, 10);
      assertEquals(2, searchResult.getCount());
      List<Document> results = searchResult.getDocuments();
      assertEquals(2, results.size());
   }

   public void testCreationDate() throws DocumentAlreadyExistsException,
         DocumentationCreationException, InterruptedException {
      ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID("plop1");
      Document document = manager.createDocument("myDocument", definitionUUID,
            new ProcessInstanceUUID(definitionUUID, 1));
      Thread.sleep(10);
      long now = new Date().getTime();
      long docTime = document.getCreationDate().getTime();
      assertTrue(docTime < now);
   }

   public void testUploadBigFile() throws DocumentAlreadyExistsException,
         DocumentationCreationException, InterruptedException, IOException,
         DocumentNotFoundException {
      InputStream stream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("CMIS-spec-v1.0.bar");
      byte[] byteArray = IOUtils.toByteArray(stream);
      stream.close();
      ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID(
            "bigProcess");
      int length = byteArray.length;
      Document document = manager.createDocument("myDocument", definitionUUID,
            new ProcessInstanceUUID(definitionUUID, 1),
            "CMIS-spec-v1.0.bar", "octet/stream", byteArray);
      assertNotNull(document);
      byte[] content = manager.getContent(document);
      assertNotNull(content);
      assertEquals(length, content.length);
   }

   // Not supported
   // CMIS-509 Remove only one version of a document is not supported
   public void testDeleteOnlyOneVersion()
         throws DocumentAlreadyExistsException, DocumentationCreationException,
         InterruptedException, IOException, DocumentNotFoundException {
      byte[] contents = "The doc contents".getBytes();
      String fileName = "testFile.txt";
      String docName = "theDoc";
      String contentMimeType = "plain/text";
      long contentSize = contents.length;
      Document doc1 = manager.createDocument(docName, new ProcessDefinitionUUID(
            "c"), new ProcessInstanceUUID("d"), fileName, contentMimeType,
            "The doc contents".getBytes());

      // String newAuthor = "james";
      Document doc2 = manager.createVersion(doc1.getId(), true,
            "testFile2.txt", "plain/text", "The new doc contents2".getBytes());
      Document doc3 = manager.createVersion(doc2.getId(), true,
            "testFile3.txt", "plain/text", "The new doc contents3".getBytes());
      Document doc4 = manager.createVersion(doc3.getId(), true,
            "testFile4.txt", "plain/text", "The new doc contents4".getBytes());

      List<Document> versionsOfDocument = manager.getVersionsOfDocument(doc2
            .getId());

      assertEquals(4, versionsOfDocument.size());
      manager.deleteDocument(doc2.getId(), false);
      assertEquals(3, manager.getVersionsOfDocument(doc3.getId()).size());
   }

   public void testSearchOnDocumentsInMultiplesFolders()
         throws DocumentAlreadyExistsException, DocumentationCreationException,
         DocumentationCreationException, DocumentNotFoundException {
      ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID(
            "testSearchOnDocumentsInMultiplesFolders");
      ProcessInstanceUUID instanceUUID = new ProcessInstanceUUID(
            definitionUUID, 1);
      ProcessDefinitionUUID otherDef = new ProcessDefinitionUUID("event");
      ProcessInstanceUUID otherInst = new ProcessInstanceUUID(otherDef, 1);
      Document document = manager.createDocument("emptyDoc1", definitionUUID,
            instanceUUID);
      manager.attachDocumentTo(otherDef, otherInst, document.getId());
      DocumentSearchBuilder builder = new DocumentSearchBuilder();
      builder.criterion(DocumentIndex.PROCESS_INSTANCE_UUID).equalsTo(
            instanceUUID.getValue());

      SearchResult search = manager.search(builder, 0, 5);
      List<Document> documents = search.getDocuments();
      assertEquals(1, search.getCount());
      assertEquals(1, documents.size());
   }
   
   
   
   private boolean doNotCheckId(Document doc, Document obj) {

      if (doc == obj)
         return true;
      if (obj == null)
         return false;
      if (doc.getClass() != obj.getClass())
         return false;
      DocumentImpl other = (DocumentImpl) obj;
      if (doc.getAuthor() == null) {
         if (other.getAuthor() != null)
            return false;
      } else if (!doc.getAuthor().equals(other.getAuthor()))
         return false;
      if (doc.getContentFileName() == null) {
         if (other.getContentFileName() != null)
            return false;
      } else if (!doc.getContentFileName().equals(other.getContentFileName()))
         return false;
      if (doc.getContentMimeType() == null) {
         if (other.getContentMimeType() != null)
            return false;
      } else if (!doc.getContentMimeType().equals(other.getContentMimeType()))
         return false;
      if (doc.getContentSize() != other.getContentSize())
         return false;
      if (doc.getCreationDate() == null) {
         if (other.getCreationDate() != null)
            return false;
      } else if (!doc.getCreationDate().equals(other.getCreationDate()))// FIXME
         return false;
      if (doc.getParentFolderId() == null) {
         if (other.getParentFolderId() != null)
            return false;
      } else if (!doc.getParentFolderId().equals(other.getParentFolderId()))
         return false;
      if (doc.getLastModificationDate() == null) {
         if (other.getLastModificationDate() != null)
            return false;
      } else if (!doc.getLastModificationDate().equals(
            other.getLastModificationDate()))
         return false;
      if (doc.getLastModifiedBy() == null) {
         if (other.getLastModifiedBy() != null)
            return false;
      } else if (!doc.getLastModifiedBy().equals(other.getLastModifiedBy()))
         return false;
      if (doc.isLatestVersion() == other.isLatestVersion())
         return false;
      if (doc.isMajorVersion() != other.isMajorVersion())
         return false;
      if (doc.getName() == null) {
         if (other.getName() != null)
            return false;
      } else if (!doc.getName().equals(other.getName()))
         return false;
      if (doc.getVersionLabel() == null) {
         if (other.getVersionLabel() != null)
            return false;
      }// else if (!doc.getVersionLabel().equals(other.getVersionLabel()))
      // return false;
      if (doc.getVersionSeriesId() == null) {
         if (other.getVersionSeriesId() != null)
            return false;
      } else if (!doc.getVersionSeriesId().equals(other.getVersionSeriesId()))
         return false;
      return true;
   }


   // public void testStress() throws DocumentAlreadyExistsException,
   // DocumentationCreationException, InterruptedException {
   // System.out.println("start");
   // int nbFiles = 0;
   // byte[] bytes =
   // "fskqjsghnisrb,ùazel,f sdlkg,dlkgj aù dpfl;zaùfdskgdklmdslkngf dslkgn sdù akdgmqslgsd mskdgfd".getBytes();
   // long max0 = 10;
   // long max1 = 15;
   // long max2 = 10;
   // for (int i = 1; i <= max0; i++) {
   // System.out.println("-==== p " + i + "/" + max0 + " ====-");
   // ProcessDefinitionUUID pdef = new
   // ProcessDefinitionUUID("procName",String.valueOf(i));
   // for (int j = 1; j <= max1; j++) {
   // ProcessInstanceUUID idef = new ProcessInstanceUUID(pdef, j);
   // for (int k = 1; k <= max2; k++) {
   // manager.createDocument("myDocument"+i+j+k, pdef, idef,
   // "theFile"+i+j+k+".txt", "plain/text", bytes);
   // nbFiles ++ ;
   // }
   // System.out.println("-==== " + nbFiles + " files ====-");
   // }
   // }
   // }

}
