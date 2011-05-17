package org.ow2.bonita.services;


import java.util.Date;
import java.util.List;

import org.ow2.bonita.DocumentAlreadyExistsException;
import org.ow2.bonita.DocumentNotFoundException;
import org.ow2.bonita.DocumentationCreationException;
import org.ow2.bonita.FolderAlreadyExistsException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

public interface DocumentationManager {

  /**
   * Checks whether the folder exist in the root of the repository according to
   * its name.
   * 
   * @param folderName
   *          the folder name
   * @return true if the folder exists; false otherwise
   */
  boolean folderExists(final String folderName);

  /**
   * Checks whether the folder exist in the parent folder according to its name.
   * 
   * @param folderName
   *          the folder name
   * @return true if the folder exists; false otherwise
   */
  boolean folderExists(final String folderName, final String parentFolderId);

  /**
   * Creates a folder according to its identifier.
   * 
   * @param folderName
   *          the folder name.
   * @return the folder identifier
   */
  Folder createFolder(final String folderName) throws FolderAlreadyExistsException;

  /**
   * Creates a sub-folder into a parent folder. If the parent is null, the
   * folder is created as a root folder.
   * 
   * @param folderName
   *          the folder name.
   * @param parentFolderId
   *          the parent folder identifier
   * @return the sub-folder identifier
   */
  Folder createFolder(final String folderName, final String parentFolderId) throws FolderAlreadyExistsException;

  /**
   * Creates a document into the given folder.
   */
  Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID) throws DocumentationCreationException, DocumentAlreadyExistsException;

  /**
   * Creates a document into the given folder and store the content of the file
   */
  Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID, String fileName, String contentMimeType, final byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException;

  Document createDocument(final String name, final String folderId) throws DocumentationCreationException, DocumentAlreadyExistsException;

  Document createDocument(final String name, final String folderId, String fileName, String contentMimeType, final byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException;
  
  Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate) throws DocumentationCreationException, DocumentAlreadyExistsException;

  Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate, String fileName, String mimeType, byte[] content) throws DocumentationCreationException, DocumentAlreadyExistsException;

  /**
   * Gets the document according to its identifier.
   * 
   * @param documentID
   *          the document identifier
   * @return the document
   */
  Document getDocument(final String documentId) throws DocumentNotFoundException;

  /**
   * Deletes a document and its version if allVersions is true.
   * 
   * @param document
   */
  void deleteDocument(final String documentId, boolean allVersions) throws DocumentNotFoundException;

  /**
   * Delete a folder.
   * 
   * @param folder
   *          the folder to delete
   */
  void deleteFolder(Folder folder);

  /**
   * Get the contents of a document.
   * 
   * @param document
   * @return the contents of the document
   */
  byte[] getContent(final Document document) throws DocumentNotFoundException;

  /**
   * Get all the folder having the name folderName
   * 
   * @param folderName
   * @return a list of folders having the name folderName
   */
  List<Folder> getFolders(String folderName);

  /**
   * @return the root folder of the repository
   */
  Folder getRootFolder();

  /**
   * @param folderId
   * @return
   */
  List<Document> getChildrenDocuments(String folderId);

  /**
   * @param folderId
   * @return
   */
  List<Folder> getChildrenFolder(String folderId);
  
  /**
   * @param documentId
   * @return
   */
  List<Document> getVersionsOfDocument(String documentId);

  /**
   * Returns the path where the document is store. The path looks like "firstFodler/sub/Folder/..."
   * @param documentId
   * @return the path
   */
  String getDocumentPath(String documentId);

  Document createVersion(String documentId, boolean isMajorVersion) throws DocumentationCreationException;
  
  Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate) throws DocumentationCreationException;

  Document createVersion(String documentId, boolean isMajorVersion, String fileName, String mimeType, byte[] content) throws DocumentationCreationException;
  
  Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate, String fileName, String mimeType, byte[] content) throws DocumentationCreationException;

  SearchResult search(DocumentSearchBuilder builder, int fromResult, int maxResults);

  void clear() throws DocumentNotFoundException;

  void updateDocumentContent(final String documentId, final String fileName, final String mimeType, final int size, final byte[] content) throws DocumentNotFoundException;

  public abstract boolean documentExists(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final String name);

  public abstract boolean documentExists(final ProcessDefinitionUUID processDefinitionUUID, final String name);

  public void attachDocumentTo(final ProcessDefinitionUUID processDefinitionUUID, final String documentId) throws DocumentNotFoundException;
  
  public void attachDocumentTo(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final String documentId) throws DocumentNotFoundException;



}
