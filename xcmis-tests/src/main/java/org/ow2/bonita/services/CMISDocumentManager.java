package org.ow2.bonita.services;
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.ow2.bonita.DocumentAlreadyExistsException;
import org.ow2.bonita.DocumentNotFoundException;
import org.ow2.bonita.DocumentationCreationException;
import org.ow2.bonita.FolderAlreadyExistsException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * CMIS implementation of the document manager
 * 
 * @author Baptiste Mesta
 * 
 */
public class CMISDocumentManager implements DocumentationManager {

  private static ThreadLocal<SimpleDateFormat> CMIS_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {

    protected synchronized SimpleDateFormat initialValue() {
      SimpleDateFormat gmtTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
      gmtTime.setTimeZone(TimeZone.getTimeZone("GMT"));
      return gmtTime;
    }

  };

  private final String binding;
  private final String url;
  private final String repositoryId;
  private final String defaultUsername;
  private final String defaultPassword;
  private String rootFolderId;
  private Session session;
  private final Map<String,Session> sessionsMap = new HashMap<String, Session>();
  private final boolean isServerUseLocalTime;
  private final Map<ProcessDefinitionUUID,String> processDefinitionMap = new HashMap<ProcessDefinitionUUID, String>();
  private final Map<ProcessInstanceUUID,String> processInstanceMap = new HashMap<ProcessInstanceUUID, String>();

  private static final Logger LOGGER = LoggerFactory.getLogger(CMISDocumentManager.class);


  public CMISDocumentManager(final String binding, final String url, final String repositoryId, final Boolean isServerUseLocalTime, String defaultUsername, String defaultPassword) {
    this.binding = binding;
    this.url = url;
    this.repositoryId = repositoryId;
    this.isServerUseLocalTime = isServerUseLocalTime;
    this.defaultUsername = defaultUsername;
    this.defaultPassword = defaultPassword;
    java.net.CookieManager cm = new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL);
    java.net.CookieHandler.setDefault(cm);
  }

  public synchronized Session createSessionById(final String repositoryId, String userId2) {
    final SessionFactory f = SessionFactoryImpl.newInstance();
//    String userId;
//    if (userId2 == null) {
//      userId = BonitaConstants.SYSTEM_USER;
//      try {
//        userId = EnvTool.getUserId();
//      } catch (Exception e) {}
//    } else {
//      userId = userId2;
//    }
//    String password = "";
//    if (!BonitaConstants.SYSTEM_USER.equals(userId)) {
//      final IdentityService identityService = EnvTool.getIdentityService();
//      final User user = identityService.findUserByUsername(userId);
//      if (user == null) {
//        throw new BonitaRuntimeException(userId + " not found");
//      }
//      password = user.getPassword();
//
//      StringBuilder usernameAndDomain = new StringBuilder();
//      usernameAndDomain.append(userId);
//      usernameAndDomain.append("@");
//      usernameAndDomain.append(DomainOwner.getDomain());
//      userId = usernameAndDomain.toString();
//    }
    
    String userId = null;
    String password = defaultPassword;
    
    if (userId2 != null) {
       userId = userId2;
    } else {
       userId = defaultUsername;
    }
    
    Session session = sessionsMap.get(userId);
//    if (session == null) {
      final Map<String, String> parameter = fixParameters(userId, password);
      parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
      session = f.createSession(parameter);
      if ( rootFolderId == null ) {
        final CmisObject rootFolder = session.getObjectByPath("/");
        this.rootFolderId = rootFolder.getId();
      }
      sessionsMap.put(userId, session);
//    }
    return session;
  }

  public synchronized Session getSession() {
    session = createSessionById(repositoryId, null);
    return session;
  }

  public synchronized Session getSession(String userId) {
    session = createSessionById(repositoryId, userId);
    return session;
  }

  protected Map<String, String> fixParameters(final String username, final String password) {
    final Map<String, String> parameter = new HashMap<String, String>();

    if ("ATOM".equals(binding)) {
      parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    } else if (binding.toLowerCase().startsWith("WebService".toLowerCase())) {
      parameter.put(SessionParameter.WEBSERVICES_ACL_SERVICE, url + "/ACLService/AccessControlServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, url + "/DiscoveryService/DiscoveryServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, url + "/MultiFilingService/MultiFilingServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, url + "/NavigationService/NavigationServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, url + "/ObjectService/ObjectServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, url + "/PolicyService/PolicyServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, url + "/RelationshipService/RelationshipServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, url + "/RepositoryService/RepositoryServicePort?wsdl");
      parameter.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, url + "/VersioningService/VersioningServicePort?wsdl");
      parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
      parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
      parameter.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
    }
    parameter.put(SessionParameter.ATOMPUB_URL, url);
    parameter.put(SessionParameter.USER, username);
    parameter.put(SessionParameter.PASSWORD, password);

    return parameter;
  }

  public boolean folderExists(final String folderName) {
    try {
      final CmisObject object = getSession().getObjectByPath("/" + folderName);
      return object instanceof Folder;
    } catch (final CmisBaseException e) {
      return false;
    }
  }

  public boolean folderExists(final String folderName, final String parentFolderId) {
    try {
      final Session session2 = getSession();
      final Folder parentFolder = (Folder) session2.getObject(session2.createObjectId(parentFolderId));
      for (final CmisObject object : parentFolder.getChildren()) {
        if (object instanceof Folder && folderName.equals(object.getName())) {
          return true;
        }
      }
    } catch (final CmisBaseException e) {
      e.printStackTrace();
    }
    return false;
  }

  public org.ow2.bonita.services.Folder createFolder(final String folderName) throws FolderAlreadyExistsException {
    getSession();
    try {
      final org.ow2.bonita.services.Folder createFolder = createFolder(folderName, rootFolderId);
      return createFolder;
    } catch (final CmisRuntimeException e) {
      throw new FolderAlreadyExistsException(folderName, e);
    }

  }

  public org.ow2.bonita.services.Folder createFolder(final String folderName, final String parentFolderId) throws FolderAlreadyExistsException {
    final Session session = getSession();
    final Folder folder;
    try{
      folder = (Folder) session.getObject(session.createObjectId(parentFolderId));
    }catch(CmisRuntimeException e) {
      throw new FolderAlreadyExistsException(folderName, e);
    }
    final HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put(PropertyIds.NAME, folderName);
    properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
    properties.put(PropertyIds.PARENT_ID, parentFolderId);
    try {
      final Folder child = folder.createFolder(properties, null, null, null, session.getDefaultContext());
      return convertFolder(child);
    } catch (final CmisRuntimeException e) {
      throw new FolderAlreadyExistsException(folderName, e);
    }
  }

  public Document createDocument(final String name, final String parentFolderId)
  throws DocumentationCreationException, DocumentAlreadyExistsException {
    final Session session = getSession();
    return createDocument(session, name, parentFolderId);
  }

  /**
   * @param session
   * @param name
   * @param parentFolderId
   * @return
   * @throws DocumentationCreationException
   */
  private Document createDocument(final Session session, final String name, final String parentFolderId)
      throws DocumentationCreationException {
    final Folder folder = (Folder) session.getObject(session.createObjectId(parentFolderId));
    final Map<String, String> newDocProps = new HashMap<String, String>();
    newDocProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
    newDocProps.put(PropertyIds.NAME, name);
    try {
      final org.apache.chemistry.opencmis.client.api.Document doc =
        folder.createDocument(newDocProps, null, null, null, null, null, session.getDefaultContext());
      return convertDocument(doc);
    } catch(final CmisBaseException e){
      throw new DocumentationCreationException("Document may alreadyExists: "+name, e);
    }
  }

  public Document getDocument(final String documentId) throws DocumentNotFoundException {
    final Session session2 = getSession();
    try {
      final org.apache.chemistry.opencmis.client.api.Document doc =
        (org.apache.chemistry.opencmis.client.api.Document) session2.getObject(session2.createObjectId(documentId));
      return convertDocument(doc);
    } catch (final CmisObjectNotFoundException e) {
      throw new DocumentNotFoundException(documentId, e);
    }
  }

  // TODO throws a DocumentAlreadyExistsException when it exists
  public Document createDocument(final String name, final String parentFolderId, final String fileName, final String contentMimeType, final byte[] fileContent) throws DocumentationCreationException {
    final Session session = getSession();
    return createDocument(session, name, parentFolderId, fileName, contentMimeType, fileContent);
  }

  /**
   * @param session
   * @param name
   * @param parentFolderId
   * @param fileName
   * @param contentMimeType
   * @param contentSize
   * @param fileContent
   * @return
   * @throws DocumentationCreationException
   */
  private Document createDocument(final Session session, final String name, final String parentFolderId, final String fileName,
      final String contentMimeType, final byte[] fileContent) throws DocumentationCreationException {
    if(contentMimeType != null) {
      try {
        new MimeType(contentMimeType);
      } catch (final MimeTypeParseException e1) {
        throw new DocumentationCreationException("Mime type not valid",e1);
      }
    }
    final Folder folder = (Folder) session.getObject(session.createObjectId(parentFolderId));

    final Map<String, String> newDocProps = new HashMap<String, String>();
    newDocProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
    newDocProps.put(PropertyIds.NAME, name);
    newDocProps.put(PropertyIds.CONTENT_STREAM_FILE_NAME, fileName);

    BigInteger length;
    if (fileContent != null && fileContent.length > 0) {
      length = BigInteger.valueOf(fileContent.length);
    } else {
      length = null;
    }
    final ContentStream contentStream;
    if(fileContent == null || fileContent.length <=0){
      contentStream = null;
    }else{      
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);
      try {
        contentStream = new ContentStreamImpl(fileName, length, contentMimeType, byteArrayInputStream);
      } catch(final CmisBaseException e){
        throw new DocumentationCreationException("Can't create the content of the document "+name, e);
      } finally {
        try {
          byteArrayInputStream.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
    try {
      final String id = folder.createDocument(newDocProps, contentStream, null, null, null, null, session.getDefaultContext()).getId();
      return convertDocument((org.apache.chemistry.opencmis.client.api.Document) session.getObject(session.createObjectId(id)));
    } catch(final CmisBaseException e){
      throw new DocumentationCreationException("Document may alreadyExists: "+name, e);
    }
  }

  private org.ow2.bonita.services.Folder convertFolder(final Folder cmisFolder) {
    final List<Folder> parents = cmisFolder.getParents();

    String parentId;
    if (parents != null && parents.size() > 0) {
      parentId = parents.get(0).getId();
    } else {
      parentId = null;
    }
    final FolderImpl folderImpl = new FolderImpl(cmisFolder.getName(), parentId);
    folderImpl.setId(cmisFolder.getId());
    return folderImpl;
  }

  private org.ow2.bonita.services.Document convertDocument(final org.apache.chemistry.opencmis.client.api.Document document) {
    Boolean latestVersion = document.isLatestVersion();
    Boolean majorVersion = document.isMajorVersion();
    Folder folder = document.getParents().get(0);
    String path = folder.getPath().substring(1);
    String[] split = path.split("/");
    ProcessInstanceUUID processInstanceUUID = null; 
    ProcessDefinitionUUID processDefinitionUUID = null; 
    if(split.length >= 2){
      processDefinitionUUID = new ProcessDefinitionUUID(split[0]);
      processInstanceUUID = new ProcessInstanceUUID(split[1]);
    }
    final DocumentImpl doc = new DocumentImpl(document.getName(),
        folder.getId(),
        document.getCreatedBy(),
        convertDate(document.getCreationDate()),
        convertDate(document.getLastModificationDate()),
        latestVersion != null ? latestVersion : false,
        majorVersion != null ? majorVersion : false,
        document.getVersionLabel(),
        document.getVersionSeriesId(),
        document.getContentStreamFileName(),
        document.getContentStreamMimeType(),
        document.getContentStreamLength(),
        processDefinitionUUID, processInstanceUUID);
    doc.setId(document.getId());
    return doc;
  }

  private Date convertDate(GregorianCalendar creationDate) {
    Date convertedDate;
    if (creationDate != null) {
      int rawOffset = 0;
      long timeInMillis = creationDate.getTimeInMillis();
      if (isServerUseLocalTime) {
        //!\ this is a hack, the server should be at GMT 0 and DST = 0 but it's on the local time
        TimeZone defaultTimeZone = TimeZone.getDefault();
        rawOffset = defaultTimeZone.getRawOffset() + defaultTimeZone.getDSTSavings();
      }
      convertedDate = new Date (timeInMillis - rawOffset);
    } else {
      convertedDate = null;
    }
    return convertedDate;
  }

  private Date localToServerDate(Date localDate) {
    if (!isServerUseLocalTime) { //convert it to GMT 0
      TimeZone defaultTimeZone = TimeZone.getDefault();
      return new Date(localDate.getTime() - defaultTimeZone.getRawOffset() - defaultTimeZone.getDSTSavings());
    } else {
      return localDate;
    }
  }

  public List<Document> getChildrenDocuments(final String folderId) {
    final Session session2 = getSession();
    final Folder folder = (Folder) session2.getObject(session2.createObjectId(folderId));
    final List<Document> documents = new ArrayList<Document>();
    for (final CmisObject children : folder.getChildren()) {
      if (children instanceof org.apache.chemistry.opencmis.client.api.Document) {
        documents.add(convertDocument((org.apache.chemistry.opencmis.client.api.Document) children));
      }
    }
    return documents;
  }

  public List<org.ow2.bonita.services.Folder> getChildrenFolder(final String folderId) {
    final Session session2 = getSession();
    final Folder folder = (Folder) session2.getObject(session2.createObjectId(folderId));
    final List<org.ow2.bonita.services.Folder> subFolders = new ArrayList<org.ow2.bonita.services.Folder>();
    for (final CmisObject children : folder.getChildren()) {
      if (children instanceof Folder) {
        subFolders.add(convertFolder((Folder) children));
      }
    }
    return subFolders;
  }

  public org.ow2.bonita.services.Folder getRootFolder() {
    final Session session2 = getSession();
    final Folder folder = (Folder) session2.getObject(session2.createObjectId(rootFolderId));
    return convertFolder(folder);
  }

  public void deleteFolder(final org.ow2.bonita.services.Folder folder) {
    final Session session2 = getSession();
    String id = folder.getId();
    session2.getObject(session2.createObjectId(id)).delete(true);// FIXME
                                                                             // check
                                                                             // child
                                                                             // not
                                                                             // exists
    Entry<ProcessDefinitionUUID, String> entryToDelete = null;
    for (Entry<ProcessDefinitionUUID, String> entry : processDefinitionMap.entrySet()) {
      if(id.equals(entry.getValue())){
        entryToDelete = entry;
        break;
      }
    }
    if(entryToDelete != null){
      processDefinitionMap.remove(entryToDelete.getKey());
      return;
    }
    Entry<ProcessInstanceUUID, String> entryToDelete2 = null;
    for (Entry<ProcessInstanceUUID, String> entry : processInstanceMap.entrySet()) {
      if(id.equals(entry.getValue())){
        entryToDelete2 = entry;
        break;
      }
    }
    if(entryToDelete2 != null){
      processInstanceMap.remove(entryToDelete2.getKey());
      return;
    }
  }

  public void deleteDocument(final String documentId, final boolean allVersions) throws DocumentNotFoundException {
    try {
      getSession().getBinding().getObjectService().deleteObject(repositoryId, documentId, allVersions, null);
    } catch (final CmisObjectNotFoundException e) {
      throw new DocumentNotFoundException(documentId, e);
    }
  }

  private static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = null;
    try {
      output = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      long count = 0;
      int n = 0;
      while (-1 != (n = input.read(buffer))) {
        output.write(buffer, 0, n);
        count += n;
      }
      return output.toByteArray();
    } finally {
      if (output != null) {
        output.close();
      }
    }
  }

  public byte[] getContent(final Document document) throws DocumentNotFoundException {
    final Session session2 = getSession();
    final org.apache.chemistry.opencmis.client.api.Document doc =
      (org.apache.chemistry.opencmis.client.api.Document) session2.getObject(session2.createObjectId(document.getId()));
    final ContentStream contentStream = doc.getContentStream();
    if (contentStream != null) {
      final InputStream stream = contentStream.getStream();
      byte[] byteArray;
      try {
        byteArray = toByteArray(stream);
        return byteArray;
      } catch (final IOException e) {
        e.printStackTrace();
      } finally {
        try {
          stream.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public String getDocumentPath(final String documentId) {
    final Session session2 = getSession();
    final org.apache.chemistry.opencmis.client.api.Document object =
      (org.apache.chemistry.opencmis.client.api.Document) session2.getObject(session2.createObjectId(documentId));
    return object.getParents().get(0).getPath() + "/" + object.getName();
  }

  public Document createVersion(final String documentId, final boolean isMajorVersion) {
    final Session session2 = getSession();
    return createVersion(session2, documentId, isMajorVersion);
  }

  /**
   * @param session
   * @param documentId
   * @param isMajorVersion
   * @return
   */
  private Document createVersion(final Session session, final String documentId, final boolean isMajorVersion) {
    final org.apache.chemistry.opencmis.client.api.Document cmisDoc =
      (org.apache.chemistry.opencmis.client.api.Document) session.getObject(session.createObjectId(documentId));
    final ObjectId pwcid = cmisDoc.checkOut();
    ObjectId newVersion = null;
    try {
      final org.apache.chemistry.opencmis.client.api.Document pwc =
        (org.apache.chemistry.opencmis.client.api.Document) session.getObject(pwcid);

      final Map<String, Object> newDocProps = new HashMap<String, Object>();
      newDocProps.put(PropertyIds.NAME, cmisDoc.getName());
      newDocProps.put(PropertyIds.IS_MAJOR_VERSION, isMajorVersion);
      newVersion = pwc.checkIn(true, newDocProps, null, "");// TODO

    } catch (Throwable t) {
      session.getBinding().getVersioningService().cancelCheckOut(repositoryId, pwcid.getId(), null);
      throw new RuntimeException(t);
    }
    // check
    // in
    // comment
    session.clear();//must clear it because xcmis change ids of documents
    return convertDocument((org.apache.chemistry.opencmis.client.api.Document) session.getObject(newVersion));
  }

  public Document createVersion(final String documentId, final boolean isMajorVersion, final String fileName, final String mimeType, final byte[] content)  throws DocumentationCreationException {
    final Session session2 = getSession();
    return createVersion(session2, documentId, isMajorVersion, fileName, mimeType, content);
  }

  /**
   * @param session
   * @param documentId
   * @param isMajorVersion
   * @param fileName
   * @param mimeType
   * @param content
   * @return
   */
  private Document createVersion(final Session session, final String documentId, final boolean isMajorVersion, final String fileName,
      final String mimeType, final byte[] content) {
    final org.apache.chemistry.opencmis.client.api.Document cmisDoc =
      (org.apache.chemistry.opencmis.client.api.Document) session.getObject(session.createObjectId(documentId));
    final ObjectId pwcid = cmisDoc.checkOut();
    ObjectId newVersion = null;
    try {
      final org.apache.chemistry.opencmis.client.api.Document pwc =
        (org.apache.chemistry.opencmis.client.api.Document) session.getObject(pwcid);
      if (content != null && content.length > 0) {
        if(mimeType != null) {
          try {
            new MimeType(mimeType);
          } catch (final MimeTypeParseException e1) {
            throw new DocumentationCreationException("Mime type not valid",e1);
          }
        }
        final ByteArrayInputStream insputStream = new ByteArrayInputStream(content);
        try {
          final ContentStream contentStream = session.getBinding().getObjectFactory()
              .createContentStream(fileName, BigInteger.valueOf(content.length), mimeType, insputStream);
          pwc.setContentStream(contentStream, true);
        } finally {
          try {
            insputStream.close();
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
      }
      final Map<String, Object> newDocProps = new HashMap<String, Object>();
      newDocProps.put(PropertyIds.NAME, cmisDoc.getName());
      newDocProps.put(PropertyIds.CONTENT_STREAM_FILE_NAME, fileName);
      newDocProps.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, mimeType);
      newDocProps.put(PropertyIds.IS_MAJOR_VERSION, isMajorVersion);

      newVersion = pwc.checkIn(isMajorVersion, newDocProps, null, "");// TODO
    } catch (Throwable t) {
      session.getBinding().getVersioningService().cancelCheckOut(repositoryId, pwcid.getId(), null);
      throw new RuntimeException(t);
    }
    // check
    // in
    // comment
    session.clear();//must clear it because xcmis change ids of documents
    return convertDocument((org.apache.chemistry.opencmis.client.api.Document) session.getObject(newVersion));
  }

  public List<org.ow2.bonita.services.Folder> getFolders(final String folderName) {
    final Session session2 = getSession();
    final String statement = "SELECT * FROM cmis:folder WHERE cmis:name = '" + folderName + "'";
    final ItemIterable<QueryResult> query = session2.query(statement, true);
    final ArrayList<org.ow2.bonita.services.Folder> folders = new ArrayList<org.ow2.bonita.services.Folder>();
    try {
      for (final QueryResult queryResult : query) {
        final PropertyData<Object> propertyById = queryResult.getPropertyById("cmis:objectId");
        final String objectId = (String) propertyById.getValues().get(0);
        final Folder cmisFolder = (Folder) session2.getObject(session2.createObjectId(objectId));
        folders.add(convertFolder(cmisFolder));
      }
    } catch (final CmisObjectNotFoundException e) {
      LOGGER.debug("can't find object with query: " + statement, e);
    }
    return folders;
  }

  public List<Document> getVersionsOfDocument(final String documentId) {
    final Session session2 = getSession();
    // List<ObjectData> allVersions =
    // session2.getBinding().getVersioningService()
    // .getAllVersions(repositoryId, document.getId(),
    // document.getVersionSeriesId(), null, false, null);
    final List<Document> versions = new ArrayList<Document>();
    final List<org.apache.chemistry.opencmis.client.api.Document> allVersions2 =
      ((org.apache.chemistry.opencmis.client.api.Document) session2.getObject(session2.createObjectId(documentId))).getAllVersions();
    for (final org.apache.chemistry.opencmis.client.api.Document oldDoc : allVersions2) {
      versions.add(convertDocument(oldDoc));
    }
    return versions;
  }

  public Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID)
  throws DocumentationCreationException, DocumentAlreadyExistsException {
    Session session = getSession();
    final String folderId = createPath(session, definitionUUID, instanceUUID);
    return createDocument(session,name, folderId);
  }

  public Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID, final String fileName,
      final String contentMimeType, final byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException {
    Session session = getSession();
    final String subFolder = createPath(session, definitionUUID, instanceUUID);
    return createDocument(session,name, subFolder, fileName, contentMimeType, fileContent);
  }

  private String createPath(Session session, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID)
  throws DocumentationCreationException {
    String mainFolderId = null;
    if (processDefinitionMap.containsKey(definitionUUID)) {
      mainFolderId = processDefinitionMap.get(definitionUUID);
    } else {
      final String processDefUUIDValue = definitionUUID.getValue();
      if(rootFolderId == null){//FIXME better handling of sessions
        getSession();
      }
      List<org.ow2.bonita.services.Folder> childrenFolder = getChildrenFolder(rootFolderId);
      for (org.ow2.bonita.services.Folder folder : childrenFolder) {
        if (processDefUUIDValue.equals(folder.getName())) {
          mainFolderId = folder.getId();
          break;
        }
      }
      if (mainFolderId == null) {
        try {
          mainFolderId = createFolder(processDefUUIDValue, rootFolderId).getId();
        } catch (final FolderAlreadyExistsException e) {
          e.printStackTrace();
        }
      }
      processDefinitionMap.put(definitionUUID, mainFolderId);
    }
    if (instanceUUID == null) {
      return mainFolderId;
    }
    String subFolderId = null;
    if (processInstanceMap.containsKey(instanceUUID)) {
      subFolderId = processInstanceMap.get(instanceUUID);
    } else {
      final String processInstValue = instanceUUID.getValue();
      List<org.ow2.bonita.services.Folder> childrenFolder = getChildrenFolder(mainFolderId);
      for (org.ow2.bonita.services.Folder folder : childrenFolder) {
        if (processInstValue.equals(folder.getName())) {
          subFolderId = folder.getId();
          break;
        }
      }
      if (subFolderId == null) {
        try {
          subFolderId = createFolder(processInstValue, mainFolderId).getId();
        } catch (final FolderAlreadyExistsException e) {
          throw new DocumentationCreationException("Folder already exists", e);
        }
      }
      processInstanceMap.put(instanceUUID, subFolderId);
    }
    return subFolderId;
  }

  public SearchResult search(final DocumentSearchBuilder builder, final int fromResult, final int maxResults) {
    final Session session2 = getSession();
    final StringBuilder whereClause = new StringBuilder();
    whereClause.append("SELECT * FROM cmis:document");
    final List<Object> query = builder.getQuery();
    if (!query.isEmpty()) {
      whereClause.append(" WHERE ");
    }
    for (final Object object : query) {
      if (object instanceof DocumentCriterion) {
        final DocumentCriterion criterion = (DocumentCriterion) object;
        switch (criterion.getField()) {
          case ID:
            createEqualsOrInClause(whereClause, criterion, "cmis:objectId");
            break;
          case PROCESS_DEFINITION_UUID:
            String idOfProcessDefinitionUUID = getIdOfProcessDefinitionUUID(criterion);
            if(idOfProcessDefinitionUUID == null){
              final List<Document> list = Collections.emptyList();
              return new SearchResult(list, 0);
            }
            whereClause.append(" IN_TREE('");
            whereClause.append(idOfProcessDefinitionUUID);
            whereClause.append("') ");
            break;
          case PROCESS_DEFINITION_UUID_WITHOUT_INSTANCES:
            String idOfProcessDefinitionUUID2 = getIdOfProcessDefinitionUUID(criterion);
            if(idOfProcessDefinitionUUID2 == null){
              final List<Document> list = Collections.emptyList();
              return new SearchResult(list, 0);
            }
            whereClause.append(" IN_FOLDER('");
            whereClause.append(idOfProcessDefinitionUUID2);
            whereClause.append("') ");
            break;
          case PROCESS_INSTANCE_UUID:
            ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID((String) criterion.getValue());
            final String id2;
            if(processInstanceMap.containsKey(processInstanceUUID)){
              id2 = processInstanceMap.get(processInstanceUUID);
            }else{
              final List<org.ow2.bonita.services.Folder> folders2 = getFolders(processInstanceUUID.getValue());
              if (folders2.size() == 0) {
                final List<Document> list = Collections.emptyList();
                return new SearchResult(list, 0);
              }
              id2 = folders2.get(0).getId();
            }
            whereClause.append(" IN_FOLDER('");
            whereClause.append(id2);
            whereClause.append("') ");
            break;
          case NAME:
            createEqualsOrInClause(whereClause, criterion, "cmis:name");
            break;
          case FILENAME:
            createEqualsOrInClause(whereClause, criterion, "cmis:contentStreamFileName");
            break;
          case CREATION_DATE:
            getTimeComparison(whereClause, criterion, "cmis:creationDate");
            break;
          case AUTHOR:
            createEqualsOrInClause(whereClause, criterion, "cmis:createdBy");
            break;
          case LAST_MODIFICATION_DATE:
            getTimeComparison(whereClause, criterion, "cmis:lastModificationDate");
            break;
        }
      } else {
        whereClause.append(" ").append(object).append(" ");
      }
    }
    final ItemIterable<QueryResult> queryResult = session2.query(whereClause.toString(), builder.isSearchAllVersions());
    queryResult.skipTo(fromResult);
    final ItemIterable<QueryResult> page = queryResult.getPage(maxResults);
    final List<Document> documents = new ArrayList<Document>();
    for (final QueryResult queryResult2 : page) {
      final PropertyData<Object> propertyById = queryResult2.getPropertyById("cmis:objectId");
      final org.apache.chemistry.opencmis.client.api.Document doc = (org.apache.chemistry.opencmis.client.api.Document) session2
          .getObject(session2.createObjectId((String) propertyById.getValues().get(0)));
      documents.add(convertDocument(doc));
    }
    final SearchResult result = new SearchResult(documents, (int) page.getPageNumItems());
    return result;
  }

  private void createEqualsOrInClause(final StringBuilder whereClause, final DocumentCriterion criterion, String field) {
    if(criterion.getValues() != null){
      whereClause.append(" " + field + " IN (");
      Collection<?> values = criterion.getValues();
      for (Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
        Object object2 = (Object) iterator.next();
        whereClause.append("'");
        whereClause.append(object2);
        whereClause.append("'");
        if(iterator.hasNext()){
          whereClause.append(",");
        }
      }
      whereClause.append(") ");
    }else{
      whereClause.append(" " + field + " = '");
      whereClause.append(criterion.getValue());
      whereClause.append("' ");
    }
  }

  /**
   * @param criterion
   * @return
   */
  private String getIdOfProcessDefinitionUUID(final DocumentCriterion criterion) {
    final String id;
    ProcessDefinitionUUID processDef = new ProcessDefinitionUUID((String) criterion.getValue());
    if(processDefinitionMap.containsKey(processDef)){
      id = processDefinitionMap.get(processDef);
    }else{
      final List<org.ow2.bonita.services.Folder> folders = getFolders(processDef.getValue());
      if (folders.size() == 0) {
        return null;
      }
      id = folders.get(0).getId();
    }
    return id;
  }

  private void getTimeComparison(final StringBuilder whereClause, final DocumentCriterion criterion, final String attribute) {
    final SimpleDateFormat cmisDateFormat = CMISDocumentManager.CMIS_DATE_FORMAT.get();
    if(criterion.getValue() != null) {
      whereClause.append(attribute);
      whereClause.append(" = TIMESTAMP '");
      final Date value = (Date) criterion.getValue();
      final String fromDate = cmisDateFormat.format(localToServerDate(value));
      whereClause.append(fromDate);
      whereClause.append("' ");
    } else {
      whereClause.append(" (");
      whereClause.append(attribute);
      whereClause.append(" >= TIMESTAMP '");
      final Date from = (Date) criterion.getFrom();
      final String fromDate = cmisDateFormat.format(localToServerDate(from));
      whereClause.append(fromDate);
      whereClause.append("' AND ");
      whereClause.append(attribute);
      whereClause.append(" <= TIMESTAMP '");
      final Date to = (Date) criterion.getTo();
      final String toDate = cmisDateFormat.format(localToServerDate(to));
      whereClause.append(toDate);
      whereClause.append("') ");
    }
  }

  public void clear() throws DocumentNotFoundException {
    clear(getRootFolder());
    sessionsMap.clear();
    processDefinitionMap.clear();
    processInstanceMap.clear();
    session = null;
  }

  public void clear(org.ow2.bonita.services.Folder folder) throws DocumentNotFoundException {
    for (org.ow2.bonita.services.Folder subFolder : getChildrenFolder(folder.getId())) {
      clear(subFolder);
      deleteFolder(subFolder);
    }
    for (Document doc : getChildrenDocuments(folder.getId())) {
      deleteDocument(doc.getId(), true);
    }
  }

  public void updateDocumentContent(String documentId, String fileName, String mimeType, int size, byte[] content)
  throws DocumentNotFoundException {
    Session session2 = getSession();
    org.apache.chemistry.opencmis.client.api.Document document = null;
    try {
      document = (org.apache.chemistry.opencmis.client.api.Document) session2
          .getObject(session2.createObjectId(documentId));
    } catch (Exception e) {
      throw new DocumentNotFoundException(documentId, e);
    }
    if (content != null) {
      ByteArrayInputStream inputStream = null;
      try {
        inputStream = new ByteArrayInputStream(content);
        ContentStream contentStream = session
            .getBinding()
            .getObjectFactory()
            .createContentStream(fileName, BigInteger.valueOf(size), mimeType,
                inputStream);
        document.setContentStream(contentStream, true);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public boolean documentExists(final ProcessDefinitionUUID processDefinitionUUID, final String name) {
    try{
      CmisObject objectByPath = getSession().getObjectByPath("/"+processDefinitionUUID.getValue()+"/"+name);
      return objectByPath != null && objectByPath instanceof org.apache.chemistry.opencmis.client.api.Document;
    } catch (CmisRuntimeException e) {
      //not found
    }
    return false;
  }

  public boolean documentExists(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final String name) {
    try{
      CmisObject objectByPath = getSession().getObjectByPath("/"+processDefinitionUUID.getValue()+"/"+processInstanceUUID.getValue()+"/"+name);
      return objectByPath != null && objectByPath instanceof org.apache.chemistry.opencmis.client.api.Document;
    } catch (CmisRuntimeException e) {
      //not found
    }
    return false;
  }

  public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID, String documentId) throws DocumentNotFoundException {
    attachDocumentTo(processDefinitionUUID,null, documentId);
  }

  public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID, ProcessInstanceUUID processInstanceUUID, String documentId)
  throws DocumentNotFoundException {
    Session session2 = getSession();
    try {
      String parentFolder = createPath(session, processDefinitionUUID, processInstanceUUID);
      org.apache.chemistry.opencmis.client.api.Document document;
      document = (org.apache.chemistry.opencmis.client.api.Document) session2
          .getObject(session2.createObjectId(documentId));
      Folder cmisFolder = (Folder) session2.getObject(session2.createObjectId(parentFolder));
      document.addToFolder(cmisFolder, true);
    } catch (CmisRuntimeException e) {
      throw new DocumentNotFoundException(documentId);
    } catch (DocumentationCreationException dce) {
      throw new DocumentNotFoundException(documentId, dce);
    }
  }

  public Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID, final String author, Date versionDate)
  throws DocumentationCreationException, DocumentAlreadyExistsException {
    Session session = getSession(author);
    final String folderId = createPath(session, definitionUUID, instanceUUID);
    return createDocument(session,name, folderId);
  }

  public Document createDocument(final String name, final ProcessDefinitionUUID definitionUUID, final ProcessInstanceUUID instanceUUID, final String author, Date versionDate, final String fileName, final String mimeType, final byte[] content)
  throws DocumentationCreationException, DocumentAlreadyExistsException {
    Session session = getSession(author);
    final String subFolder = createPath(session, definitionUUID, instanceUUID);
    return createDocument(session,name, subFolder, fileName, mimeType, content);
  }

  public Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate) throws DocumentationCreationException {
    //versionDate can't be set manually
    final Session session2 = getSession(author);
    return createVersion(session2, documentId, isMajorVersion);
  }

  public Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate, String fileName, String mimeType, byte[] content)
      throws DocumentationCreationException {
    final Session session2 = getSession(author);
    return createVersion(session2, documentId, isMajorVersion, fileName, mimeType, content);
  }

 
}
