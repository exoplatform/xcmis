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

package org.xcmis.restatom.collections;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.commons.codec.binary.Base64;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.BinaryResponseContext;
import org.xcmis.restatom.abdera.ContentTypeElement;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.restatom.types.CmisContentType;
import org.xcmis.restatom.types.EnumReturnVersion;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisObjectCollection.java 218 2010-02-15 07:38:06Z andrew00x $
 */
public abstract class CmisObjectCollection extends AbstractCmisCollection<CmisObject>
{

   private class HttpConnectionStream extends InputStream
   {
      private final HttpURLConnection httpConnection;

      private InputStream inStream;

      private boolean closed;

      public HttpConnectionStream(HttpURLConnection httpConnection)
      {
         this.httpConnection = httpConnection;
      }

      public int read() throws IOException
      {
         if (inStream == null)
         {
            inStream = httpConnection.getInputStream();
         }
         int i = inStream.read();
         if (i == -1)
         {
            if (!closed)
            {
               try
               {
                  inStream.close();
                  httpConnection.disconnect();
                  closed = true;
               }
               catch (IOException e)
               {
                  LOG.error(e.getMessage(), e);
               }
            }
         }
         return i;
      }

      @Override
      protected void finalize() throws Throwable
      {
         if (!closed)
         {
            try
            {
               inStream.close();
               httpConnection.disconnect();
            }
            catch (IOException e)
            {
               LOG.error(e.getMessage(), e);
            }
         }
         super.finalize();
      }
   }

   private static final Logger LOG = Logger.getLogger(CmisObjectCollection.class);

   /** The Constant SPACES_AIR_SPECIFIC_REFERER. */
   protected static final String SPACES_AIR_SPECIFIC_REFERER = "app:/CMISSpacesAir.swf";

   public CmisObjectCollection(Connection connection)
   {
      super(connection);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteEntry(String objectId, RequestContext request) throws ResponseContextException
   {
      try
      {
         Connection connection = getConnection(request);
         connection.deleteObject(objectId, getBooleanParameter(request, AtomCMIS.PARAM_ALL_VERSIONS, true));
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (StorageException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (UpdateConflictException uce)
      {
         throw new ResponseContextException(createErrorResponse(uce, 409));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Exception t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext deleteMedia(RequestContext request)
   {
      try
      {
         Connection connection = getConnection(request);
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         String objectId = connection.deleteContentStream(getId(request), changeTokenHolder /*changeToken*/);
         CmisObject object = connection.getProperties(objectId, true, CmisConstants.CHANGE_TOKEN);
         @SuppressWarnings("unchecked")
         Property<String> changeToken = (Property<String>)getProperty(object, CmisConstants.CHANGE_TOKEN);
         ResponseContext response = new EmptyResponseContext(204);
         if (changeToken != null && changeToken.getValues().size() > 0)
         {
            response.setEntityTag(changeToken.getValues().get(0));
         }
         return response;
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (StorageException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (UpdateConflictException uce)
      {
         return createErrorResponse(uce, 409);
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (Exception t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteMedia(String documentId, RequestContext request) throws ResponseContextException
   {
      try
      {
         Connection connection = getConnection(request);
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         connection.deleteContentStream(documentId, changeTokenHolder);
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (StorageException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (UpdateConflictException uce)
      {
         throw new ResponseContextException(createErrorResponse(uce, 409));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Exception t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getAuthor(RequestContext request) throws ResponseContextException
   {
      Principal principal = request.getPrincipal();
      if (principal != null)
      {
         return principal.getName();
      }
      return ANONYMOUS;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Person> getAuthors(CmisObject object, RequestContext request) throws ResponseContextException
   {
      String author = object.getObjectInfo().getCreatedBy();
      Person p = request.getAbdera().getFactory().newAuthor();
      if (author != null)
      {
         p.setName(author);
      }
      else
      {
         p.setName(SYSTEM);
      }
      return Collections.singletonList(p);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType(CmisObject object)
   {
      String contentType = object.getObjectInfo().getContentStreamMimeType();
      if (contentType != null //
         && !"".equals(contentType))
      {
         return contentType;
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CmisObject getEntry(String id, RequestContext request) throws ResponseContextException
   {
      try
      {
         boolean includeAllowableActions =
            getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
         String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
         boolean includePolicies = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_POLICY_IDS, false);
         boolean includeACL = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ACL, false);
         String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
         IncludeRelationships includeRelationships;
         try
         {
            includeRelationships =
               request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null
                  || request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS).length() == 0
                  ? IncludeRelationships.NONE : IncludeRelationships.fromValue(request
                     .getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
         }
         catch (IllegalArgumentException iae)
         {
            String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
            throw new ResponseContextException(createErrorResponse(msg, 400));
         }

         Connection connection = getConnection(request);

         CmisObject object;
         if (id.charAt(0) != '/')
         {
            // Get by id.
            object =
               connection.getObject(id, includeAllowableActions, includeRelationships, includePolicies, includeACL,
                  true, propertyFilter, renditionFilter);
         }
         else
         {
            // Get by path.
            object =
               connection.getObjectByPath(id, includeAllowableActions, includeRelationships, includePolicies,
                  includeACL, true, propertyFilter, renditionFilter);
         }
         BaseType type = getBaseObjectType(object);
         if (type == BaseType.DOCUMENT)
         {
            String returnVersion = request.getParameter(AtomCMIS.PARAM_RETURN_VERSION);
            if (returnVersion == null || returnVersion.length() == 0)
            {
               return object;
            }
            EnumReturnVersion enumReturnVersion;
            try
            {
               enumReturnVersion = EnumReturnVersion.fromValue(returnVersion);
            }
            catch (IllegalArgumentException iae)
            {
               String msg = "Invalid parameter " + returnVersion;
               throw new ResponseContextException(createErrorResponse(msg, 400));
            }
            if (enumReturnVersion == EnumReturnVersion.THIS)
            {
               return object;
            }

            if (enumReturnVersion == EnumReturnVersion.LATEST && object.getObjectInfo().isLatestVersion())
            {
               return object;
            }

            if (enumReturnVersion == EnumReturnVersion.LATESTMAJOR && object.getObjectInfo().isLatestMajorVersion())
            {
               return object;
            }

            // Find latest in Version series.
            String versionSeriesId = object.getObjectInfo().getVersionSeriesId();
            return connection.getObjectOfLatestVersion(//
               versionSeriesId, //
               enumReturnVersion == EnumReturnVersion.LATESTMAJOR, //
               includeAllowableActions, //
               includeRelationships, //
               includePolicies, //
               includeACL, //
               true, //
               propertyFilter, //
               renditionFilter);
         }

         // Is not document.
         return object;
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 400));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Exception t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getId();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId(RequestContext request)
   {
      return request.getTarget().getParameter("objectid");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext getMedia(RequestContext request)
   {
      try
      {
         Connection connection = getConnection(request);
         // TODO : resolve (optional) offset, length
         ContentStream content = connection.getContentStream(getId(request), getStreamId(request));
         ResponseContext response = new BinaryResponseContext(content.getStream(), 200);
         response.setContentType(content.getMediaType().toString());
         response.setContentLength(content.length());
         response.setHeader(AtomCMIS.CONTENT_DISPOSITION_HEADER, //
            "attachment; filename=\"" + content.getFileName() + "\"");
         return response;
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (Exception t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getName();
   }

   /**
    * {@inheritDoc}
    */
   public String getStreamId(RequestContext request)
   {
      return request.getTarget().getParameter("streamid");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getUpdated(CmisObject object) throws ResponseContextException
   {
      return getLastModificationDate(object).getTime();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext putEntry(RequestContext request)
   {
      Entry entry;
      try
      {
         entry = getEntryFromRequest(request);
      }
      catch (ResponseContextException rce)
      {
         //         rce.printStackTrace();
         return rce.getResponseContext();
      }

      try
      {
         Connection connection = getConnection(request);
         ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
         CmisObject object = objectElement != null ? objectElement.getObject() : new CmisObject();
         updatePropertiesFromEntry(object, entry);

         Map<String, Property<?>> properties = object.getProperties();
         Collection<String> policyIds = object.getPolicyIds();
         List<AccessControlEntry> acl = object.getACL();
         ContentStream contentStream = getContentStream(entry, request);

         boolean checkin = getBooleanParameter(request, AtomCMIS.PARAM_CHECKIN, false);
         String updatedId = null;
         if (checkin)
         {
            // If 'checkin' param is TRUE, execute checkin() service.
            boolean major = getBooleanParameter(request, AtomCMIS.PARAM_MAJOR, true);
            String checkinComment = request.getParameter(AtomCMIS.PARAM_CHECKIN_COMMENT);
            // TODO : ACEs for removing. Not clear from specification how to
            // pass (obtain) ACEs for adding and removing from one object.
            updatedId =
               connection.checkin(getId(request), major, properties, contentStream, checkinComment, acl, null,
                  policyIds);
         }
         else
         {
            // If 'checkin' param is FALSE, execute updateProperties() service.
            // Get 'if-match' header as is, according to HTTP specification :
            // http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3
            // ------------------------------------------
            // Clients MAY issue simple (non-subrange) GET requests with either
            // weak validators or strong validators. Clients MUST NOT use weak
            // validators in other forms of request.
            // ------------------------------------------
            // Method is PUT - use strong comparison.
            CmisObject cmisObject = connection.getProperties(getId(request), true, CmisConstants.BASE_TYPE_ID);
            BaseType baseType = cmisObject.getObjectInfo().getBaseType();

            ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
            changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
            updatedId = connection.updateProperties(getId(request), changeTokenHolder, properties);
            if (baseType == BaseType.DOCUMENT && contentStream != null)
            {
               updatedId = connection.setContentStream(getId(request), contentStream, changeTokenHolder, true);
            }

         }

         CmisObject updated = connection.getProperties(updatedId, true, CmisConstants.WILDCARD);
         entry = request.getAbdera().getFactory().newEntry();
         addEntryDetails(request, entry, request.getResolvedUri(), updated);
         return buildGetEntryResponse(request, entry);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (NameConstraintViolationException nce)
      {
         return createErrorResponse(nce, 409);
      }
      catch (StorageException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (UpdateConflictException uce)
      {
         return createErrorResponse(uce, 409);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }
      catch (Exception t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * Put media.
    *
    * @param entryObj the entry obj
    * @param contentType the content type
    * @param slug the slug
    * @param inputStream the input stream
    * @param request the request
    *
    * @throws ResponseContextException the response context exception
    */
   @Override
   public void putMedia(CmisObject entryObj, MimeType contentType, String slug, InputStream inputStream,
      RequestContext request) throws ResponseContextException
   {
      try
      {
         Connection connection = getConnection(request);
         ContentStream content = new BaseContentStream(inputStream, null, convertMimeType(contentType));
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         boolean overwriteFlag = getBooleanParameter(request, AtomCMIS.PARAM_OVERWRITE_FLAG, true);
         connection.setContentStream(getId(request), content, changeTokenHolder, overwriteFlag);
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (StorageException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 409));
      }
      catch (ContentAlreadyExistsException ce)
      {
         throw new ResponseContextException(createErrorResponse(ce, 409));
      }
      catch (StreamNotSupportedException se)
      {
         // XXX : specification says 403, is it correct ?
         throw new ResponseContextException(createErrorResponse(se, 400));
      }
      catch (UpdateConflictException uce)
      {
         throw new ResponseContextException(createErrorResponse(uce, 409));
      }
      catch (Exception t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext putMedia(RequestContext request)
   {
      try
      {
         Connection connection = getConnection(request);
         ContentStream content =
            new BaseContentStream(request.getInputStream(), null, convertMimeType(request.getContentType()));
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         boolean overwriteFlag = getBooleanParameter(request, AtomCMIS.PARAM_OVERWRITE_FLAG, true);
         String updatedId = connection.setContentStream(getId(request), content, changeTokenHolder, overwriteFlag);
         CmisObject updated = connection.getProperties(updatedId, true, CmisConstants.CHANGE_TOKEN);
         ResponseContext response = new EmptyResponseContext(201);
         String contentLink = getContentLink(getId(request), request);
         response.setHeader(HttpHeaders.CONTENT_LOCATION, contentLink);
         response.setHeader(HttpHeaders.LOCATION, contentLink);
         String changeToken = updated.getObjectInfo().getChangeToken();
         if (changeToken != null)
         {
            response.setEntityTag(changeToken);
         }
         return response;
      }
      catch (IOException ioe)
      {
         return createErrorResponse(ioe, 500);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (StorageException re)
      {
         return createErrorResponse(re, 409);
      }
      catch (ContentAlreadyExistsException ce)
      {
         return createErrorResponse(ce, 409);
      }
      catch (StreamNotSupportedException se)
      {
         // XXX : specification says 403, is it correct ?
         return createErrorResponse(se, 400);
      }
      catch (UpdateConflictException uce)
      {
         return createErrorResponse(uce, 409);
      }
      catch (Exception t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * Process rendition links.
    *
    * @param entry the entry
    * @param object the object
    * @param request the request
    *
    * @throws ResponseContextException the response context exception
    */
   private void processRenditionLinks(Entry entry, CmisObject object, RequestContext request)
      throws ResponseContextException
   {
      String baseRenditionHref = getBaseRenditionHref(getId(object), request);

      List<Rendition> renditionList = object.getRenditions();
      for (Rendition rendition : renditionList)
      {
         Link link = entry.addLink(//
            baseRenditionHref + "/" + rendition.getStreamId(), //
            AtomCMIS.LINK_ALTERNATE, //
            rendition.getMimeType(), //
            rendition.getTitle(), //
            null, //
            rendition.getLength());
         link.setAttributeValue(AtomCMIS.RENDITION_KIND, rendition.getKind());
         entry.addLink(link);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String addEntryDetails(RequestContext request, Entry entry, IRI feedIri, CmisObject object)
      throws ResponseContextException
   {
      String objectId = getId(object);
      entry.setId(objectId);
      // Updated and published is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      entry.setPublished(AtomUtils.getAtomDate(getCreationDate(object)));
      entry.setUpdated(AtomUtils.getAtomDate(getLastModificationDate(object)));
      entry.setSummary("");
      for (Person person : getAuthors(object, request))
      {
         entry.addAuthor(person);
      }

      entry.setTitle(getTitle(object));

      // Service link.
      String service = getServiceLink(request);
      entry.addLink(service, AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      String self = getObjectLink(objectId, request);
      // Self link.
      entry.addLink(self, AtomCMIS.LINK_SELF);
      // Edit link.
      entry.addLink(self, AtomCMIS.LINK_EDIT);

      // Alternate links.
      processRenditionLinks(entry, object, request);

      // Object type link.
      String typeId = object.getObjectInfo().getTypeId();
      entry.addLink(getObjectTypeLink(typeId, request), AtomCMIS.LINK_DESCRIBEDBY, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null,
         null, -1);

      // Allowable actions link.
      entry.addLink(getAllowableActionsLink(objectId, request), AtomCMIS.LINK_CMIS_ALLOWABLEACTIONS,
         AtomCMIS.MEDIATYPE_ALLOWABLE_ACTIONS, null, null, -1);

      BaseType baseType = getBaseObjectType(object);
      if (baseType == BaseType.FOLDER)
      {
         // Relationships link.
         String relationships = getRelationshipsLink(objectId, request);
         entry.addLink(relationships, AtomCMIS.LINK_CMIS_RELATIONSHIPS, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Policies link.
         String policies = getPoliciesLink(objectId, request);
         entry.addLink(policies, AtomCMIS.LINK_CMIS_POLICIES, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // ACL link.
         String acl = getACLLink(objectId, request);
         entry.addLink(acl, AtomCMIS.LINK_CMIS_ACL, AtomCMIS.MEDIATYPE_ACL, null, null, -1);

         // Children link.
         String children = getChildrenLink(objectId, request);
         entry.addLink(children, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Descendants link. Provided only if repository support descendants feature.
         String descendants = getDescendantsLink(objectId, request);
         if (descendants != null)
         {
            entry.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);
         }

         // Folder tree. link. Provided only if repository support folder tree feature.
         String folderTree = getFolderTreeLink(objectId, request);
         if (folderTree != null)
         {
            entry.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);
         }

         // Parent link.
         String parentId = object.getObjectInfo().getParentId();
         if (parentId != null)
         {
            // Not provided for root folder.
            String parent = getObjectLink(parentId, request);
            entry.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
         }

         // Must have 'content' element to conform Atom specification.
         String name = object.getObjectInfo().getName();
         entry.setContent(name);
      }
      else if (baseType == BaseType.DOCUMENT)
      {
         // Relationship link.
         String relationships = getRelationshipsLink(objectId, request);
         entry.addLink(relationships, AtomCMIS.LINK_CMIS_RELATIONSHIPS, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Policies link
         String policies = getPoliciesLink(objectId, request);
         entry.addLink(policies, AtomCMIS.LINK_CMIS_POLICIES, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // ACL link
         String acl = getACLLink(objectId, request);
         entry.addLink(acl, AtomCMIS.LINK_CMIS_ACL, AtomCMIS.MEDIATYPE_ACL, null, null, -1);

         // All versions
         String versionSeriesId = object.getObjectInfo().getVersionSeriesId();
         String allVersions = getAllVersionsLink(versionSeriesId, request);
         entry.addLink(allVersions, AtomCMIS.LINK_VERSION_HISTORY, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Latest version link
         StringBuilder sb = new StringBuilder();
         sb.append(self).append('?').append(AtomCMIS.PARAM_RETURN_VERSION).append('=').append(
            EnumReturnVersion.LATEST.value());
         entry.addLink(sb.toString(), AtomCMIS.LINK_CURRENT_VERSION, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

         // PWC link if it exists.
         String checkedoutProperty = object.getObjectInfo().getVersionSeriesCheckedOutId();
         if (checkedoutProperty != null)
         {
            String pwcLink = getObjectLink(checkedoutProperty, request);
            entry.addLink(pwcLink, AtomCMIS.LINK_WORKING_COPY, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
         }

         // Parents link.
         String parent = getParentsLink(objectId, request);
         entry.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Edit-media link.
         String contentLink = getContentLink(objectId, request);
         entry.addLink(contentLink, AtomCMIS.LINK_EDIT_MEDIA);

         // Content element.
         String contentType = getContentType(object);
         if (contentType != null)
         {
            entry.setContent(new IRI(contentLink), contentType);
         }
         else
         {
            Factory factory = request.getAbdera().getFactory();
            Content content = factory.newContent();
            content.setContentType(null);
            content.setSrc(contentLink);
            entry.setContentElement(content);
         }
      }
      else if (baseType == BaseType.POLICY)
      {
         // Relationships link.
         String relationships = getRelationshipsLink(objectId, request);
         entry.addLink(relationships, AtomCMIS.LINK_CMIS_RELATIONSHIPS, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Policy link.
         String policies = getPoliciesLink(objectId, request);
         entry.addLink(policies, AtomCMIS.LINK_CMIS_POLICIES, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // ACL link.
         String acl = getACLLink(objectId, request);
         entry.addLink(acl, AtomCMIS.LINK_CMIS_ACL, AtomCMIS.MEDIATYPE_ACL, null, null, -1);

         // Parents link.
         String parent = getParentsLink(objectId, request);
         entry.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Must have 'content' element to conform Atom specification.
         String name = object.getObjectInfo().getName();
         entry.setContent(name);
      }
      else if (baseType == BaseType.RELATIONSHIP)
      {
         // Relationship source link.
         String sourceId = object.getObjectInfo().getSourceId();
         entry.addLink(getObjectLink(sourceId, request), AtomCMIS.LINK_CMIS_SOURCE, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
            null, null, -1);

         // Relationship target link.
         String targetId = object.getObjectInfo().getTargetId();
         entry.addLink(getObjectLink(targetId, request), AtomCMIS.LINK_CMIS_TARGET, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
            null, null, -1);

         // Must have 'content' element to conform Atom specification.
         entry.setContent(getName(object));
      }

      ObjectTypeElement objectElement = new ObjectTypeElement(request.getAbdera().getFactory(), AtomCMIS.OBJECT);
      objectElement.build(object);
      entry.addExtension(objectElement);

      return self;
   }

   @SuppressWarnings("unchecked")
   protected org.xcmis.spi.utils.MimeType convertMimeType(MimeType abderaMimeType)
   {
      if (abderaMimeType == null)
      {
         return new org.xcmis.spi.utils.MimeType();
      }
      MimeTypeParameterList abderaParameters = abderaMimeType.getParameters();
      Map<String, String> paremeters = new HashMap<String, String>();
      for (Enumeration<String> names = abderaParameters.getNames(); names.hasMoreElements();)
      {
         String name = names.nextElement();
         paremeters.put(name, abderaParameters.get(name));
      }
      return new org.xcmis.spi.utils.MimeType(abderaMimeType.getPrimaryType(), abderaMimeType.getSubType(), paremeters);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Factory factory = request.getAbdera().getFactory();
      Feed feed = factory.newFeed();
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addAuthor(getAuthor(request));
      // Updated is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance())); // TODO proper date

      String service = getServiceLink(request);
      feed.addLink(service, AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      String self = getSelfLink(getId(request), request);
      feed.addLink(self, AtomCMIS.LINK_SELF, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      String via = getObjectLink(getId(request), request);
      feed.addLink(via, AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

      return feed;
   }

   /**
    * Get link to AtomPub Document that describes object's ACL.
    *
    * @param id object id
    * @param request request context
    * @return link to allowable actions document
    */
   protected String getACLLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "objacl");
      params.put("id", id);
      String actions = request.absoluteUrlFor(TargetType.ENTRY, params);
      return actions;
   }

   /**
    * Get link to AtomPub Document that describes object's allowable actions.
    *
    * @param id object id
    * @param request request context
    * @return link to allowable actions document
    */
   protected String getAllowableActionsLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "allowableactions");
      params.put("id", id);
      String actions = request.absoluteUrlFor(TargetType.ENTRY, params);
      return actions;
   }

   /**
    * Get link to AtomPub Document that describes object's versions.
    *
    * @param id objects id
    * @param request request context
    * @return link to AtomPub Document that describes object's versions
    */
   protected String getAllVersionsLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "versions");
      params.put("id", id);
      String parents = request.absoluteUrlFor(TargetType.ENTRY, params);
      return parents;
   }

   /**
    * Get object's base type.
    *
    * @param object object
    * @return object's base type
    */
   protected BaseType getBaseObjectType(CmisObject object)
   {
      return object.getObjectInfo().getBaseType();
   }

   /**
    * Get link to express renditions.
    *
    * @param id objects id
    * @param request request context
    * @return link to AtomPub Document that describes object's parent(s)
    */
   protected String getBaseRenditionHref(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "alternate");
      params.put("id", id);
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return link;
   }

   /**
    * Get link to AtomPub document that describes folder's children.
    *
    * @param id folder id
    * @param request the request context
    * @return link to AtomPub document that describes folder's children
    */
   protected String getChildrenLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "children");
      params.put("id", id);
      String children = request.absoluteUrlFor(TargetType.ENTRY, params);
      return children;
   }

   /**
    * Get self link which provides the URI to retrieve this resource again.
    *
    * The atom:link with relation self MUST be generated to return the URI of
    * the feed. If paging or any other mechanism is used to filter, sort, or
    * change the representation of the feed, the URI MUST point back a resource
    * with the same representation.
    *
    * @param id the object id
    * @param request the request context
    * @return link which provides the URI to retrieve this resource again.
    */
   protected String getSelfLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", getHref().substring(1));
      params.put("id", id);
      String children = request.absoluteUrlFor(TargetType.ENTRY, params) + "?" + request.getUri().getQuery();
      return children;
   }

   /**
    * Get link to document content.
    *
    * @param id document id
    * @param request request context
    * @return link to document content
    */
   protected String getContentLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "file");
      params.put("id", id);
      String content = request.absoluteUrlFor(TargetType.ENTRY, params);
      return content;
   }

   /**
    * Get content stream from entry.
    *
    * @param entry source entry
    * @param request request context
    * @return content stream as <code>ContentStream</code> or null if there is
    *         no 'content' in entry
    * @throws IOException if any i/o error occurs
    * @throws ResponseContextException other errors
    */
   protected ContentStream getContentStream(Entry entry, RequestContext request) throws IOException,
      ResponseContextException
   {
      ContentStream contentStream = null;
      ContentTypeElement cmisContent = entry.getExtension(AtomCMIS.CONTENT);
      if (cmisContent != null)
      {
         CmisContentType content = cmisContent.getContent();
         
         InputStream is = content.getBase64();
         contentStream =
               new BaseContentStream(is, is.available(), null, org.xcmis.spi.utils.MimeType.fromString(content.getMediatype()));
      }
      else
      {
         Content content = entry.getContentElement();
         if (content != null)
         {
            final IRI src = content.getSrc();
            if (src != null)
            {
               if (src.equals(new IRI(getContentLink(getId(request), request))))
               {
                  // If 'src' attribute provides URI is the same to current
                  // object (document). This may happen when client does 'check-in'
                  // or 'check-out' operation.
               }
               else
               {
                  URL url = null;
                  try
                  {
                     url = src.toURL();
                  }
                  catch (URISyntaxException e)
                  {
                     String msg = "Invalid src attribute: " + src;
                     throw new ResponseContextException(createErrorResponse(msg, 400));
                  }
                  // HTTP only
                  HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                  httpConnection.setRequestMethod("GET");
                  httpConnection.setDoOutput(false);
                  httpConnection.setDoInput(true);
                  int status = httpConnection.getResponseCode();
                  if (200 == status)
                  {
                     contentStream =
                        new BaseContentStream(new HttpConnectionStream(httpConnection), null,
                           org.xcmis.spi.utils.MimeType.fromString(httpConnection.getHeaderField("Content-Type")));
                  }
                  else
                  {
                     httpConnection.disconnect();
                     String msg = "Unable get content from URI : " + src.toString() + ". Response status is " + status;
                     throw new ResponseContextException(createErrorResponse(msg, 500));
                  }
               }
            }
            else
            {
               Type contentType = content.getContentType();
               org.xcmis.spi.utils.MimeType mediaType;
               if (contentType == Type.XML || contentType == Type.XHTML || contentType == Type.HTML
                  || contentType == Type.TEXT)
               {
                  switch (contentType)
                  {
                     case XHTML :
                        mediaType = new org.xcmis.spi.utils.MimeType("application", "xhtml+xml");
                        break;
                     case HTML :
                        mediaType = new org.xcmis.spi.utils.MimeType("text", "html");
                        break;
                     case TEXT :
                        mediaType = new org.xcmis.spi.utils.MimeType("text", "plain");
                        break;
                     case XML :
                        mediaType = convertMimeType(content.getMimeType());
                        break;
                     default :
                        // Must never happen.
                        mediaType = new org.xcmis.spi.utils.MimeType();
                  }

                  byte[] data;
                  // XXX CMISSpaces sends XML content as Base64 encoded but
                  // Abdera waits for plain text.
                  // Done just for research work. Find good solution to fix this.
                  if (SPACES_AIR_SPECIFIC_REFERER.equalsIgnoreCase(request.getHeader("referer")))
                  {
                     data = Base64.decodeBase64(content.getText().getBytes());
                  }
                  else
                  {
                     String charset = mediaType.getParameter(CmisConstants.CHARSET);
                     if (charset == null)
                     {
                        // workaround
                        mediaType.getParameters().put(CmisConstants.CHARSET, "UTF-8");
                     }
                     data = content.getValue().getBytes(charset == null ? "UTF-8" : charset);

                  }

                  contentStream = new BaseContentStream(data, null, mediaType);
               }
               else
               {
                  contentStream =
                     new BaseContentStream(content.getDataHandler().getInputStream(), null, convertMimeType(content
                        .getMimeType()));
               }
            }
         }
      }
      return contentStream;
   }

   /**
    * Get object creation date.
    *
    * @param object source object
    * @return creation date
    */
   protected Calendar getCreationDate(CmisObject object)
   {
      Calendar creationDate = object.getObjectInfo().getCreationDate();
      if (creationDate == null)
      {
         creationDate = Calendar.getInstance();
      }
      return creationDate;
   }

   /**
    * Get link to AtomPub document that describes folder's descendants. If
    * repository does not support capability 'getDescendants' this method will
    * return null.
    *
    * @param id folder id
    * @param request request context
    * @return link to AtomPub document that describes folder's descendants or
    *         null if capability 'getDescendants' is not supported.
    * @see RepositoryInfo
    */
   protected String getDescendantsLink(String id, /*RepositoryInfo repoInfo,*/RequestContext request)
   {
      String children = null;
      Connection connection = getConnection(request);
      RepositoryCapabilities capabilities = connection.getStorage().getRepositoryInfo().getCapabilities();
      if (capabilities.isCapabilityGetDescendants())
      {
         Map<String, String> params = new HashMap<String, String>();
         params.put("repoid", getRepositoryId(request));
         params.put("atomdoctype", "descendants");
         params.put("id", id);
         children = request.absoluteUrlFor("feed", params);
      }
      return children;
   }

   /**
    * Get link to AtomPub document that describes folder's tree. If repository
    * does not support capability 'getFolderTree' this method will return null.
    *
    * @param id folder id
    * @param request request context
    * @return link to AtomPub document that describes folder's tree or null if
    *         capability 'getFolderTree' is not supported.
    */
   protected String getFolderTreeLink(String id, RequestContext request)
   {
      String children = null;
      Connection conn = getConnection(request);
      RepositoryCapabilities capabilities = conn.getStorage().getRepositoryInfo().getCapabilities();
      if (capabilities.isCapabilityGetFolderTree())
      {
         Map<String, String> params = new HashMap<String, String>();
         params.put("repoid", getRepositoryId(request));
         params.put("atomdoctype", "foldertree");
         params.put("id", id);
         children = request.absoluteUrlFor("feed", params);
      }
      return children;
   }

   /**
    * Get object creation date.
    *
    * @param object source object
    * @return creation date
    */
   protected Calendar getLastModificationDate(CmisObject object)
   {
      Calendar lastModification = object.getObjectInfo().getLastModificationDate();
      if (lastModification == null)
      {
         lastModification = Calendar.getInstance();
      }
      return lastModification;
   }

   /**
    * Get link to AtomPub Document that describes object with <code>id</code>.
    *
    * @param id object id
    * @param request request context
    * @return link to AtomPub Document that describes object with
    *         <code>id</code>
    */
   protected String getObjectLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "object");
      params.put("id", id);
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return link;
   }

   /**
    * Get link to AtomPub Document that describes object's parent(s).
    *
    * @param id objects id
    * @param request request context
    * @return link to AtomPub Document that describes object's parent(s)
    */
   protected String getParentsLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "parents");
      params.put("id", id);
      String parents = request.absoluteUrlFor("feed", params);
      return parents;
   }

   /**
    * Get link to AtomPub Document that describes object's policies.
    *
    * @param id objects id
    * @param request request context
    * @return link to AtomPub Document that describes policies applied to object
    */
   protected String getPoliciesLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "policies");
      params.put("id", id);
      String parents = request.absoluteUrlFor("feed", params);
      return parents;
   }

   /**
    * Get object's property.
    *
    * @param object object
    * @param propertyName property name
    * @return property or null if property does not exist
    */
   protected Property<?> getProperty(CmisObject object, String propertyName)
   {
      Map<String, Property<?>> properties = object.getProperties();
      Property<?> property = null;
      if (properties != null && !properties.isEmpty())
      {
         //         for (Property<?> prop : properties.values())
         //         {
         //            if (propertyName.equals(prop.getId()))
         //            {
         //               return prop;
         //            }
         //         }
         property = properties.get(propertyName);
      }
      return property;
   }

   /**
    * Get link to AtomPub Document that describes object's relationships.
    *
    * @param id objects id
    * @param request request context
    * @return link to AtomPub Document that describes object's relationships
    */
   protected String getRelationshipsLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "relationships");
      params.put("id", id);
      String parents = request.absoluteUrlFor("feed", params);
      return parents;
   }

   /**
    * Get's the name of the specific resource requested.
    *
    * @param request RequestContext
    * @return string resource name
    */
   @Override
   protected String getResourceName(RequestContext request)
   {
      String path = request.getTarget().getParameter("path");
      if (path != null)
      {
         try
         {
            path = URLDecoder.decode(path, "UTF-8");
         }
         catch (UnsupportedEncodingException ignored)
         {
         }
         return path.charAt(0) == '/' ? path : ('/' + path);
      }
      return super.getResourceName(request);
   }

   /**
    * From specification (1.0-cd06), section 3.5.2 Entries. When POSTing an Atom
    * Document, the Atom elements MUST take precedence over the corresponding
    * writable CMIS property. For example, atom:title will overwrite cmis:name.
    *
    * @param object CMIS object
    * @param entry entry that delivered CMIS object.
    */
   protected void updatePropertiesFromEntry(CmisObject object, Entry entry)
   {
      // SPEC.: 3.5.2 Entries
      // atom:title MUST be the cmis:name property
      String title = entry.getTitle();
      if (title != null && title.length() > 0)
      {
         // Should never be null, but check it to avoid overwriting existed cmis:name property.
         StringProperty prop = (StringProperty)getProperty(object, CmisConstants.NAME);
         if (prop == null)
         {
            prop = new StringProperty();
            prop.setId(CmisConstants.NAME);
            prop.setLocalName(CmisConstants.NAME);
            prop.getValues().add(title);
            object.getProperties().put(prop.getId(), prop);
         }
         else
         {
            prop.getValues().clear();
            prop.getValues().add(title);
         }
      }
      // atom:author/atom:name MUST be cmis:createdBy
      String author = entry.getAuthor() != null ? entry.getAuthor().getName() : null;
      if (author != null && author.length() > 0)
      {
         StringProperty prop = (StringProperty)getProperty(object, CmisConstants.CREATED_BY);
         if (prop == null)
         {
            prop = new StringProperty();
            prop.setId(CmisConstants.CREATED_BY);
            prop.setLocalName(CmisConstants.CREATED_BY);
            prop.getValues().add(author);
            object.getProperties().put(prop.getId(), prop);
         }
         else
         {
            prop.getValues().clear();
            prop.getValues().add(author);
         }
      }
   }

}
