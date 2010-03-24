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
import org.apache.abdera.protocol.server.context.MediaResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.commons.codec.binary.Base64;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.ContentTypeElement;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.restatom.types.CmisContentType;
import org.xcmis.restatom.types.EnumReturnVersion;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.IncludeRelationships;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.RepositoryCapabilities;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.CmisObjectImpl;
import org.xcmis.spi.object.impl.StringProperty;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisObjectCollection.java 218 2010-02-15 07:38:06Z andrew00x $
 */
public abstract class CmisObjectCollection extends AbstractCmisCollection<CmisObject>
{

   //   private static final Log LOG = ExoLogger.getLogger(CmisObjectCollection.class);
   /** The Constant SPACES_AIR_SPECIFIC_REFERER. */
   protected static final String SPACES_AIR_SPECIFIC_REFERER = "app:/CMISSpacesAir.swf";

   /** The Constant ANONYMOUS. */
   protected static final String ANONYMOUS = "anonymous";

   /** The Constant SYSTEM. */
   protected static final String SYSTEM = "system";

   /**
    * Instantiates a new cmis object collection.
    * @param storageProvider TODO
    */
   public CmisObjectCollection(StorageProvider storageProvider)
   {
      super(storageProvider);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteEntry(String objectId, RequestContext request) throws ResponseContextException
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         /*
         CmisObject doc =
            conn.getObjectById(objectId, false, IncludeRelationships.NONE,
               false, false, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null);
         BooleanProperty checkedOut = (BooleanProperty)getProperty(doc, CMIS.IS_VERSION_SERIES_CHECKED_OUT);
         if (checkedOut != null && checkedOut.getValues().size() > 0 && checkedOut.getValues().get(0))
            conn.cancelCheckout(objectId);
         else
            conn.deleteObject(objectId, true);
         */
         conn.deleteObject(objectId, true);
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
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext deleteMedia(RequestContext request)
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         // TODO : Is it correct to use 'If-Match' header ?
         String objectId = conn.deleteContentStream(getId(request), //
            changeTokenHolder // changeToken
            );
         CmisObject object = conn.getProperties(objectId, true, CMIS.CHANGE_TOKEN);
         Property<String> changeToken = (Property<String>)getProperty(object, CMIS.CHANGE_TOKEN);
         // TODO : 204, is it correct ? It used by default when delete content of ATOM resource
         ResponseContext response = new EmptyResponseContext(204);
         if (changeToken != null && changeToken.getValues().size() > 0)
            response.setEntityTag(changeToken.getValues().get(0));
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
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteMedia(String documentId, RequestContext request) throws ResponseContextException
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         conn.deleteContentStream(documentId, //
            changeTokenHolder);
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
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getAuthor(RequestContext request) throws ResponseContextException
   {
      Principal principal = request.getPrincipal();
      if (principal != null)
         return principal.getName();
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
         p.setName(author);
      else
         p.setName(SYSTEM);
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
   public CmisObject getEntry(String id, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      boolean includePolicies = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_POLICY_IDS));
      boolean includeACL = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ACL));
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      IncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null
               || request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS).length() == 0 ? IncludeRelationships.NONE
               : IncludeRelationships.fromValue(request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(createErrorResponse(msg, 400));
      }
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         CmisObject object;
         if (id.charAt(0) != '/')
            // Get by id. 
            object =
               conn.getObject(id, includeAllowableActions, includeRelationships, includePolicies, includeACL, true,
                  propertyFilter, renditionFilter);
         else
            // Get by path.
            object =
               conn.getObjectByPath(id, includeAllowableActions, includeRelationships, includePolicies, includeACL,
                  true, propertyFilter, renditionFilter);
         BaseType type = getBaseObjectType(object);
         if (type == BaseType.DOCUMENT)
         {
            String returnVersion = request.getParameter(AtomCMIS.PARAM_RETURN_VERSION);
            if (returnVersion == null || returnVersion.length() == 0)
               return object;
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
               return object;

            if (enumReturnVersion == EnumReturnVersion.LATEST && object.getObjectInfo().isLatestVersion())
               return object;

            if (enumReturnVersion == EnumReturnVersion.LATESTMAJOR && object.getObjectInfo().isLatestMajorVersion())
               return object;

            // Find latest in Version series.
            String versionSeriesId = object.getObjectInfo().getVersionSeriesId();
            return conn.getObjectOfLatestVersion(//
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
      catch (StorageException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
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
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getId(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getId();
   }

   /**
    * {@inheritDoc}
    */
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
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         // TODO : resolve (optional) offset, length 
         ContentStream content = conn.getContentStream(//
            //
            getId(request), //
            getStreamId(request), //
            0, // 
            Long.MAX_VALUE);
         /*         if (content == null)
                     return new EmptyResponseContext(200);
         */
         ResponseContext response = new MediaResponseContext(content.getStream(), 200);
         response.setContentType(content.getMediaType());
         response.setContentLength(content.length());
         response.setHeader(AtomCMIS.CONTENT_DISPOSITION_HEADER, //
            "attachment; filename=\"" + content.getFileName() + "\"");
         // TODO : need ETag here ?
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
      catch (StorageException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
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
   public String getTitle(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getName();
   }

   /**
    * {@inheritDoc}
    */
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

      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
         CmisObject object = objectElement != null ? object = objectElement.getObject() : new CmisObjectImpl();
         updatePropertiesFromEntry(object, entry);

         Map<String, Property<?>> properties = object.getProperties();
         List<String> policyIds = (List<String>)object.getPolicyIds();
         List<AccessControlEntry> acl = object.getACL();
         ContentStream contentStream = getContentStream(entry, request);

         boolean checkin = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_CHECKIN));
         String updatedId = null;
         if (checkin)
         {
            boolean major = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_MAJOR));
            String checkinComment = request.getParameter(AtomCMIS.PARAM_CHECKIN_COMMENT);
            // TODO : ACEs for removing. Not clear from specification how to
            // pass (obtain) ACEs for adding and removing from one object.
            updatedId =
               conn.checkin(getId(request), major, properties, contentStream, checkinComment, acl, null, policyIds);

         }
         else
         {
            // TODO : is correct to use 'if-match' header?
            // Get 'if-match' header as is, according to HTTP specification :
            // http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3
            // ------------------------------------------
            // Clients MAY issue simple (non-subrange) GET requests with either
            // weak validators or strong validators. Clients MUST NOT use weak
            // validators in other forms of request.
            // ------------------------------------------
            // Method is PUT - use strong comparison. 
            ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
            changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
            updatedId = conn.updateProperties(getId(request), changeTokenHolder, properties);
            if (contentStream != null)
               updatedId = conn.setContentStream(getId(request), contentStream, changeTokenHolder, true);

         }

         CmisObject updated = conn.getProperties(updatedId, true, CMIS.WILDCARD);
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
         return createErrorResponse(nce, 400);
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
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
      finally
      {
         if (conn != null)
            conn.close();
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
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ContentStream content = new BaseContentStream(inputStream, null, contentType.getBaseType());
         // TODO : is correct ?
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         String overwriteFlagParameter = request.getParameter(AtomCMIS.PARAM_OVERWRITE_FLAG);
         boolean overwriteFlag =
            overwriteFlagParameter == null || overwriteFlagParameter.length() == 0 ? true : Boolean
               .parseBoolean(overwriteFlagParameter);
         conn.setContentStream(getId(request), content, changeTokenHolder, overwriteFlag);
      }
      catch (IOException ioe)
      {
         throw new ResponseContextException(createErrorResponse(ioe, 500));
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
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext putMedia(RequestContext request)
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ContentStream content = new BaseContentStream(request.getInputStream(), //
            null, //
            request.getContentType() == null ? "application/octet-stream" : request.getContentType().getBaseType());

         // TODO : is correct ?
         ChangeTokenHolder changeTokenHolder = new ChangeTokenHolder();
         changeTokenHolder.setValue(request.getHeader(HttpHeaders.IF_MATCH));
         String overwriteFlagParameter = request.getParameter(AtomCMIS.PARAM_OVERWRITE_FLAG);
         boolean overwriteFlag =
            overwriteFlagParameter == null || overwriteFlagParameter.length() == 0 ? true : Boolean
               .parseBoolean(overwriteFlagParameter);
         String updatedId = conn.setContentStream(getId(request), content, changeTokenHolder, overwriteFlag);
         CmisObject updated = conn.getProperties(updatedId, true, CMIS.CHANGE_TOKEN);
         ResponseContext response = new EmptyResponseContext(201);
         String contentLink = getContentLink(getId(request), request);
         response.setHeader(HttpHeaders.CONTENT_LOCATION, contentLink);
         response.setHeader(HttpHeaders.LOCATION, contentLink);
         String changeToken = updated.getObjectInfo().getChangeToken();
         if (changeToken != null)
            response.setEntityTag(changeToken);
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
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
      finally
      {
         if (conn != null)
            conn.close();
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
      // FIXME updated and published is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      entry.setPublished(AtomUtils.getAtomDate(getCreationDate(object)));
      entry.setUpdated(AtomUtils.getAtomDate(getLastModificationDate(object)));
      entry.setSummary("");
      for (Person person : getAuthors(object, request))
         entry.addAuthor(person);

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
            entry.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);

         // Folder tree. link. Provided only if repository support folder tree feature.
         String folderTree = getFolderTreeLink(objectId, request);
         if (folderTree != null)
            entry.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

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

      // Apply property filter for serialized object.
      String filter = request.getParameter(AtomCMIS.PARAM_FILTER);
      try
      {
         objectElement.build(object, new PropertyFilter(filter));
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(400, fe);
      }
      entry.addExtension(objectElement);

      return self;
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
      // FIXME updated is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance())); // TODO proper date

      String service = getServiceLink(request);
      feed.addLink(service, AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      String self = getObjectLink(getId(request), request);
      feed.addLink(self, AtomCMIS.LINK_SELF, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);
      feed.addLink(self, AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

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
    * @param request request context
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
    *            no 'content' in entry
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
         String base64 = content.getBase64();
         byte[] data = null;
         if (base64 != null)
            data = Base64.decodeBase64(base64.getBytes());
         contentStream = new BaseContentStream(data, null, content.getMediatype());
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
                  // TODO : need to do anything ??
               }
               else
               {
                  HTTPConnection connection = new HTTPConnection(//
                     src.getScheme(), // 
                     src.getHost(), //
                     src.getPort());
                  // Disable user interaction.
                  connection.setAllowUserInteraction(false);
                  String path = src.getQuery() != null //
                     ? src.getPath() + "?" + src.getQuery() //
                     : src.getPath();
                  try
                  {
                     HTTPResponse response = connection.Get(path);
                     int status = response.getStatusCode();
                     if (200 == status)
                     {
                        String mediaType = null;
                        String ct = response.getHeader("Content-Type");
                        if (ct != null)
                           mediaType = new MimeType(ct).getBaseType();
                        contentStream = new BaseContentStream(response.getInputStream(), null, mediaType);
                     }
                     else
                     {
                        String msg =
                           "Unable get content from URI : " + src.toString() + ". Response status is " + status;
                        throw new ResponseContextException(createErrorResponse(msg, status));
                     }
                  }
                  catch (ModuleException ce)
                  {
                     throw new ResponseContextException(createErrorResponse(ce, 500));
                  }
                  catch (MimeTypeParseException mte)
                  {
                     throw new ResponseContextException(createErrorResponse(mte, 500));
                  }
               }
            }
            else
            {
               Type contentType = content.getContentType();
               String mediaType;
               if (contentType == Type.XML || contentType == Type.XHTML || contentType == Type.HTML
                  || contentType == Type.TEXT)
               {
                  switch (contentType)
                  {
                     case XHTML :
                        mediaType = "application/xhtml+xml";
                        break;
                     case HTML :
                        mediaType = "text/html";
                        break;
                     case TEXT :
                        mediaType = "text/plain";
                        break;
                     case XML :
                        mediaType = content.getMimeType().getBaseType();
                        break;
                     default :
                        // Must never happen.
                        mediaType = null;
                  }

                  byte[] data;
                  // XXX CMISSpaces sends XML content as Base64 encoded but
                  // Abdera waits for plain text.
                  // TODO Done just for research work. Find good solution to fix this. 
                  if (SPACES_AIR_SPECIFIC_REFERER.equalsIgnoreCase(request.getHeader("referer")))
                     data = Base64.decodeBase64(content.getText().getBytes());
                  else
                     data = content.getValue().getBytes("UTF-8");

                  contentStream = new BaseContentStream(data, null, mediaType);
               }
               else
               {
                  //                  mediaType = content.getMimeType().toString();
                  mediaType = content.getMimeType().getBaseType();
                  contentStream = new BaseContentStream(content.getDataHandler().getInputStream(), null, mediaType);
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
      if (creationDate != null)
         return creationDate;
      else
         return Calendar.getInstance();
   }

   /**
    * Get link to AtomPub document that describes folder's descendants.
    * If repository does not support capability 'getDescendants' this method will
    * return null.
    * 
    * @param id folder id
    * @param request request context
    * @return link to AtomPub document that describes folder's descendants or null
    *            if capability 'getDescendants' is not supported. 
    */
   protected String getDescendantsLink(String id, RequestContext request)
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         RepositoryCapabilities capabilities = conn.getStorage().getRepositoryInfo().getCapabilities();
         if (capabilities.isCapabilityGetFolderTree())
         {
            Map<String, String> params = new HashMap<String, String>();
            params.put("repoid", getRepositoryId(request));
            params.put("atomdoctype", "descendants");
            params.put("id", id);
            String children = request.absoluteUrlFor("feed", params);
            return children;
         }
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
      return null;
   }

   /**
    * Get link to AtomPub document that describes folder's tree.
    * If repository does not support capability 'getFolderTree' this method will
    * return null.
    * 
    * @param id folder id
    * @param request request context
    * @return link to AtomPub document that describes folder's tree or null
    *            if capability 'getFolderTree' is not supported. 
    */
   protected String getFolderTreeLink(String id, RequestContext request)
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         RepositoryCapabilities capabilities = conn.getStorage().getRepositoryInfo().getCapabilities();
         if (capabilities.isCapabilityGetFolderTree())
         {
            Map<String, String> params = new HashMap<String, String>();
            params.put("repoid", getRepositoryId(request));
            params.put("atomdoctype", "foldertree");
            params.put("id", id);
            String children = request.absoluteUrlFor("feed", params);
            return children;
         }
      }
      finally
      {
         if (conn != null)
            conn.close();
      }
      return null;
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
      if (lastModification != null)
         return lastModification;
      else
         return Calendar.getInstance();
   }

   /**
    * Get link to AtomPub Document that describes object with <code>id</code>.
    * 
    * @param id object id
    * @param request request context
    * @return link to AtomPub Document that describes object with <code>id</code>
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
    * @return property or null if property does not exists
    */
   protected Property<?> getProperty(CmisObject object, String propertyName)
   {
      Map<String, Property<?>> properties = object.getProperties();
      if (properties != null && !properties.isEmpty())
      {
         for (Property<?> prop : properties.values())
         {
            if (propertyName.equals(prop.getId()))
               return prop;
         }
      }
      return null;
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
    * Get's the name of the specific resource requested
    */
   protected String getResourceName(RequestContext request)
   {
      // TODO : can get smarter?
      String path;
      if ((path = request.getTarget().getParameter("objectpath")) != null)
         return path.charAt(0) == '/' ? path : ('/' + path);
      return super.getResourceName(request);
   }

   /**
    * From specification (1.0-cd06), section 3.5.2 Entries.
    * When POSTing an Atom Document, the Atom elements MUST take precedence over
    * the corresponding writable CMIS property.
    * For example, atom:title will overwrite cmis:name.
    * 
    * @param object CMIS object
    * @param entry entry that delivered CMIS object.
    */
   protected void updatePropertiesFromEntry(CmisObject object, Entry entry)
   {
      String title = entry.getTitle();
      if (title != null)
      {
         // Should never be null, but check it to avoid overwriting existed cmis:name property.
         StringProperty name = (StringProperty)getProperty(object, CMIS.NAME);
         if (name == null)
         {
            name = new StringProperty();
            name.setId(CMIS.NAME);
            name.getValues().add(title);
            object.getProperties().put(name.getId(), name);
         }
         else
         {
            name.getValues().clear();
            name.getValues().add(title);
         }
      }
      // TODO : check about other properties.
   }

}
