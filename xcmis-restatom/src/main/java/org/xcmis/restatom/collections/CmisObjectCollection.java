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
import org.xcmis.atom.CmisContentType;
import org.xcmis.atom.EnumReturnVersion;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.CMISExtensionFactory;
import org.xcmis.restatom.abdera.ContentTypeElement;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;

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
 * @version $Id$
 */
public abstract class CmisObjectCollection extends AbstractCmisCollection<CmisObjectType>
{

   //   private static final Log LOG = ExoLogger.getLogger(CmisObjectCollection.class);
   /** The Constant SPACES_AIR_SPECIFIC_REFERER. */
   protected static final String SPACES_AIR_SPECIFIC_REFERER = "app:/CMISSpacesAir.swf";

   /** The Constant ANONYMOUS. */
   protected static final String ANONYMOUS = "anonymous";

   /** The Constant SYSTEM. */
   protected static final String SYSTEM = "system";

   /** The object service. */
   protected final ObjectService objectService;

   /** The repository service. */
   protected final RepositoryService repositoryService;

   /** The versioning service. */
   protected final VersioningService versioningService;

   /**
    * Instantiates a new cmis object collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    */
   public CmisObjectCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService)
   {
      super();
      this.repositoryService = repositoryService;
      this.objectService = objectService;
      this.versioningService = versioningService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteEntry(String objectId, RequestContext request) throws ResponseContextException
   {
      try
      {
         /*
         CmisObjectType doc =
            objectService.getObjectById(getRepositoryId(request), objectId, false, EnumIncludeRelationships.NONE,
               false, false, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null);
         CmisPropertyBoolean checkedOut = (CmisPropertyBoolean)getProperty(doc, CMIS.IS_VERSION_SERIES_CHECKED_OUT);
         if (checkedOut != null && checkedOut.getValue().size() > 0 && checkedOut.getValue().get(0))
            versioningService.cancelCheckout(getRepositoryId(request), objectId);
         else
            objectService.deleteObject(getRepositoryId(request), objectId, true);
         */
         objectService.deleteObject(getRepositoryId(request), objectId, true);
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (RepositoryException re)
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext deleteMedia(RequestContext request)
   {
      try
      {
         // TODO : Is it correct to use 'If-Match' header ?
         CmisObjectType object = objectService.deleteContentStream(//
            getRepositoryId(request), //
            getId(request), //
            request.getHeader(HttpHeaders.IF_MATCH) // changeToken
            );
         CmisPropertyString changeToken = (CmisPropertyString)getProperty(object, CMIS.CHANGE_TOKEN);
         // TODO : 204, is it correct ? It used by default when delete content of ATOM resource
         ResponseContext response = new EmptyResponseContext(204);
         if (changeToken != null && changeToken.getValue().size() > 0)
            response.setEntityTag(changeToken.getValue().get(0));
         return response;
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (RepositoryException re)
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteMedia(String documentId, RequestContext request) throws ResponseContextException
   {
      try
      {
         objectService.deleteContentStream(//
            getRepositoryId(request), //
            documentId, //
            request.getHeader(HttpHeaders.IF_MATCH) // changeToken
            );
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (RepositoryException re)
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
   public List<Person> getAuthors(CmisObjectType object, RequestContext request) throws ResponseContextException
   {
      CmisPropertyString created = (CmisPropertyString)getProperty(object, CMIS.CREATED_BY);
      String author = null;
      if (created != null && created.getValue().size() > 0)
         author = created.getValue().get(0); // single-valued property
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
   public String getContentType(CmisObjectType object)
   {
      CmisPropertyString contentTypeProperty = (CmisPropertyString)getProperty(object, CMIS.CONTENT_STREAM_MIME_TYPE);
      if (contentTypeProperty != null //
         && contentTypeProperty.getValue().size() > 0 //
         && contentTypeProperty.getValue().get(0) != null //
         && !"".equals(contentTypeProperty.getValue().get(0)))
      {
         String contentType =
            ((CmisPropertyString)getProperty(object, CMIS.CONTENT_STREAM_MIME_TYPE)).getValue().get(0);
         return contentType;
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getEntry(String id, RequestContext request) throws ResponseContextException
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
      EnumIncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null
               || request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS).length() == 0
               ? EnumIncludeRelationships.NONE : EnumIncludeRelationships.fromValue(request
                  .getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(createErrorResponse(msg, 400));
      }
      try
      {
         CmisObjectType object;
         if (id.charAt(0) != '/')
            // Get by id. 
            object =
               objectService.getObject(getRepositoryId(request), id, includeAllowableActions, includeRelationships,
                  includePolicies, includeACL, propertyFilter, renditionFilter);
         else
            // Get by path.
            object =
               objectService.getObjectByPath(getRepositoryId(request), id, includeAllowableActions,
                  includeRelationships, includePolicies, includeACL, propertyFilter, renditionFilter);
         EnumBaseObjectTypeIds type = getBaseObjectType(object);
         if (type == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
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

            if (enumReturnVersion == EnumReturnVersion.LATEST
               && ((CmisPropertyBoolean)getProperty(object, CMIS.IS_LATEST_VERSION)).getValue().get(0))
               return object;

            if (enumReturnVersion == EnumReturnVersion.LATESTMAJOR
               && ((CmisPropertyBoolean)getProperty(object, CMIS.IS_LATEST_MAJOR_VERSION)).getValue().get(0))
               return object;

            // Find latest in Version series.
            String versionSeriesId = ((CmisPropertyId)getProperty(object, CMIS.VERSION_SERIES_ID)).getValue().get(0);
            return versioningService.getObjectOfLatestVersion(//
               getRepositoryId(request), //
               versionSeriesId, //
               enumReturnVersion == EnumReturnVersion.LATESTMAJOR, //
               includeAllowableActions, //
               includeRelationships, //
               includePolicies, //
               includeACL, //
               propertyFilter, //
               renditionFilter);
         }

         // Is not document.
         return object;
      }
      catch (RepositoryException re)
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
   }

   /**
    * {@inheritDoc}
    */
   public String getId(CmisObjectType object) throws ResponseContextException
   {
      return ((CmisPropertyId)getProperty(object, CMIS.OBJECT_ID)).getValue().get(0);
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
      try
      {
         // TODO : resolve (optional) offset, length 
         ContentStream content = objectService.getContentStream(//
            getRepositoryId(request), //
            getId(request), //
            getStreamId(request), //
            0, // 
            Long.MAX_VALUE);
         if (content == null)
            return new EmptyResponseContext(200);

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
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getName(CmisObjectType object) throws ResponseContextException
   {
      return ((CmisPropertyString)getProperty(object, CMIS.NAME)).getValue().get(0);
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
   public String getTitle(CmisObjectType object) throws ResponseContextException
   {
      return ((CmisPropertyString)getProperty(object, CMIS.NAME)).getValue().get(0);
   }

   /**
    * {@inheritDoc}
    */
   public Date getUpdated(CmisObjectType object) throws ResponseContextException
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
         ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
         CmisObjectType object = objectElement != null ? object = objectElement.getObject() : new CmisObjectType();
         if (object.getProperties() == null)
            object.setProperties(new CmisPropertiesType());
         updatePropertiesFromEntry(object, entry);

         CmisPropertiesType properties = object.getProperties();
         CmisListOfIdsType policyIds = object.getPolicyIds();
         CmisAccessControlListType acl = object.getAcl();
         ContentStream contentStream = getContentStream(entry, request);

         boolean checkin = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_CHECKIN));
         CmisObjectType updated;
         if (checkin)
         {
            boolean major = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_MAJOR));
            String checkinComment = request.getParameter(AtomCMIS.PARAM_CHECKIN_COMMENT);
            // TODO : ACEs for removing. Not clear from specification how to
            // pass (obtain) ACEs for adding and removing from one object.
            updated =
               versioningService.checkin(getRepositoryId(request), getId(request), major, properties, contentStream,
                  checkinComment, acl, null, policyIds != null && policyIds.getId().size() > 0 ? policyIds.getId()
                     : null);
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
            String changeToken = request.getHeader(HttpHeaders.IF_MATCH);

            updated = objectService.updateProperties(getRepositoryId(request), getId(request), changeToken, properties);
            if (contentStream != null)
               updated =
                  objectService.setContentStream(getRepositoryId(request), getId(request), contentStream, changeToken,
                     true);
         }
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
      catch (RepositoryException re)
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
   public void putMedia(CmisObjectType entryObj, MimeType contentType, String slug, InputStream inputStream,
      RequestContext request) throws ResponseContextException
   {
      try
      {
         ContentStream content = new BaseContentStream(inputStream, null, contentType.getBaseType());
         // TODO : is correct ?
         String changeToken = request.getHeader(HttpHeaders.IF_MATCH);
         String overwriteFlagParameter = request.getParameter(AtomCMIS.PARAM_OVERWRITE_FLAG);
         boolean overwriteFlag =
            overwriteFlagParameter == null || overwriteFlagParameter.length() == 0 ? true : Boolean
               .parseBoolean(overwriteFlagParameter);
         objectService.setContentStream(getRepositoryId(request), getId(request), content, changeToken, overwriteFlag);
      }
      catch (IOException ioe)
      {
         throw new ResponseContextException(createErrorResponse(ioe, 500));
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (RepositoryException re)
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext putMedia(RequestContext request)
   {
      try
      {
         ContentStream content = new BaseContentStream(request.getInputStream(), //
            null, //
            request.getContentType() == null ? "application/octet-stream" : request.getContentType().getBaseType());

         // TODO : is correct ?
         String changeToken = request.getHeader(HttpHeaders.IF_MATCH);
         String overwriteFlagParameter = request.getParameter(AtomCMIS.PARAM_OVERWRITE_FLAG);
         boolean overwriteFlag =
            overwriteFlagParameter == null || overwriteFlagParameter.length() == 0 ? true : Boolean
               .parseBoolean(overwriteFlagParameter);
         CmisObjectType updated =
            objectService.setContentStream(getRepositoryId(request), getId(request), content, changeToken,
               overwriteFlag);
         ResponseContext response = new EmptyResponseContext(201);
         String contentLink = getContentLink(getId(request), request);
         response.setHeader(HttpHeaders.CONTENT_LOCATION, contentLink);
         response.setHeader(HttpHeaders.LOCATION, contentLink);
         CmisProperty changeTokenProperty = getProperty(updated, CMIS.CHANGE_TOKEN);
         changeToken = null;
         if (changeTokenProperty != null && ((CmisPropertyString)changeTokenProperty).getValue().size() > 0)
            changeToken = ((CmisPropertyString)changeTokenProperty).getValue().get(0);
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
      catch (RepositoryException re)
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
   private void processRenditionLinks(Entry entry, CmisObjectType object, RequestContext request)
      throws ResponseContextException
   {
      String baseRenditionHref = getBaseRenditionHref(getId(object), request);

      List<CmisRenditionType> renditionList = object.getRendition();
      for (CmisRenditionType cmisRenditionType : renditionList)
      {
         Link link = entry.addLink(//
            baseRenditionHref + "/" + cmisRenditionType.getStreamId(), //
            AtomCMIS.LINK_ALTERNATE, //
            cmisRenditionType.getMimetype(), //
            cmisRenditionType.getTitle(), //
            null, //
            cmisRenditionType.getLength().longValue());
         link.setAttributeValue(AtomCMIS.RENDITION_KIND, cmisRenditionType.getKind());
         entry.addLink(link);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String addEntryDetails(RequestContext request, Entry entry, IRI feedIri, CmisObjectType object)
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
      String typeId = ((CmisPropertyId)getProperty(object, CMIS.OBJECT_TYPE_ID)).getValue().get(0);
      entry.addLink(getObjectTypeLink(typeId, request), AtomCMIS.LINK_DESCRIBEDBY, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null,
         null, -1);

      // Allowable actions link.
      entry.addLink(getAllowableActionsLink(objectId, request), AtomCMIS.LINK_CMIS_ALLOWABLEACTIONS,
         AtomCMIS.MEDIATYPE_ALLOWABLE_ACTIONS, null, null, -1);

      EnumBaseObjectTypeIds baseType = getBaseObjectType(object);
      if (baseType == EnumBaseObjectTypeIds.CMIS_FOLDER)
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
         CmisPropertyId parentId = (CmisPropertyId)getProperty(object, CMIS.PARENT_ID);
         if (parentId.getValue().size() > 0)
         {
            // Not provided for root folder.
            String parent = getObjectLink(parentId.getValue().get(0), request);
            entry.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
         }

         // Must have 'content' element to conform Atom specification.
         CmisPropertyString nameProperty = (CmisPropertyString)getProperty(object, CMIS.NAME);
         entry.setContent(nameProperty.getValue().get(0));
      }
      else if (baseType == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
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
         String versionSeriesId = ((CmisPropertyId)getProperty(object, CMIS.VERSION_SERIES_ID)).getValue().get(0);
         String allVersions = getAllVersionsLink(versionSeriesId, request);
         entry.addLink(allVersions, AtomCMIS.LINK_VERSION_HISTORY, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         // Latest version link
         StringBuilder sb = new StringBuilder();
         sb.append(self).append('?').append(AtomCMIS.PARAM_RETURN_VERSION).append('=').append(
            EnumReturnVersion.LATEST.value());
         entry.addLink(sb.toString(), AtomCMIS.LINK_CURRENT_VERSION, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

         // PWC link if it exists.
         CmisPropertyId checkedoutProperty = (CmisPropertyId)getProperty(object, CMIS.VERSION_SERIES_CHECKED_OUT_ID);
         if (checkedoutProperty != null //
            && checkedoutProperty.getValue().size() > 0 //
            && checkedoutProperty.getValue().get(0) != null)
         {
            String pwcLink = getObjectLink(checkedoutProperty.getValue().get(0), request);
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
      else if (baseType == EnumBaseObjectTypeIds.CMIS_POLICY)
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
         CmisPropertyString nameProperty = (CmisPropertyString)getProperty(object, CMIS.NAME);
         entry.setContent(nameProperty.getValue().get(0));
      }
      else if (baseType == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
      {
         // Relationship source link.
         String sourceId = ((CmisPropertyId)getProperty(object, CMIS.SOURCE_ID)).getValue().get(0);
         entry.addLink(getObjectLink(sourceId, request), AtomCMIS.LINK_CMIS_SOURCE, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
            null, null, -1);

         // Relationship target link.
         String targetId = ((CmisPropertyId)getProperty(object, CMIS.TARGET_ID)).getValue().get(0);
         entry.addLink(getObjectLink(targetId, request), AtomCMIS.LINK_CMIS_TARGET, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
            null, null, -1);

         // Must have 'content' element to conform Atom specification.
         CmisPropertyString nameProperty = (CmisPropertyString)getProperty(object, CMIS.NAME);
         entry.setContent(nameProperty.getValue().get(0));
      }

      ObjectTypeElement objectElement =
         new ObjectTypeElement(request.getAbdera().getFactory(), CMISExtensionFactory
            .getElementName(CmisObjectType.class));

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
   protected EnumBaseObjectTypeIds getBaseObjectType(CmisObjectType object)
   {
      String type = ((CmisPropertyId)getProperty(object, CMIS.BASE_TYPE_ID)).getValue().get(0);
      return EnumBaseObjectTypeIds.fromValue(type);
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
   protected Calendar getCreationDate(CmisObjectType object)
   {
      CmisPropertyDateTime dateProperty = ((CmisPropertyDateTime)getProperty(object, CMIS.CREATION_DATE));
      if (dateProperty.getValue().size() > 0 && dateProperty.getValue().get(0) != null)
         return dateProperty.getValue().get(0).toGregorianCalendar();
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
      CmisRepositoryCapabilitiesType capabilities =
         repositoryService.getRepositoryInfo(getRepositoryId(request)).getCapabilities();
      if (capabilities.isCapabilityGetFolderTree())
      {
         Map<String, String> params = new HashMap<String, String>();
         params.put("repoid", getRepositoryId(request));
         params.put("atomdoctype", "descendants");
         params.put("id", id);
         String children = request.absoluteUrlFor("feed", params);
         return children;
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
      CmisRepositoryCapabilitiesType capabilities =
         repositoryService.getRepositoryInfo(getRepositoryId(request)).getCapabilities();
      if (capabilities.isCapabilityGetFolderTree())
      {
         Map<String, String> params = new HashMap<String, String>();
         params.put("repoid", getRepositoryId(request));
         params.put("atomdoctype", "foldertree");
         params.put("id", id);
         String children = request.absoluteUrlFor("feed", params);
         return children;
      }
      return null;
   }

   /**
    * Get object creation date.
    * 
    * @param object source object
    * @return creation date
    */
   protected Calendar getLastModificationDate(CmisObjectType object)
   {
      CmisPropertyDateTime dateProperty = ((CmisPropertyDateTime)getProperty(object, CMIS.LAST_MODIFICATION_DATE));
      if (dateProperty.getValue().size() > 0 && dateProperty.getValue().get(0) != null)
         return dateProperty.getValue().get(0).toGregorianCalendar();
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
   protected CmisProperty getProperty(CmisObjectType object, String propertyName)
   {
      CmisPropertiesType properties = object.getProperties();
      if (properties != null)
      {
         for (CmisProperty prop : properties.getProperty())
         {
            if (propertyName.equals(prop.getPropertyDefinitionId()))
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
   protected void updatePropertiesFromEntry(CmisObjectType object, Entry entry)
   {
      String title = entry.getTitle();
      if (title != null)
      {
         // Should never be null, but check it to avoid overwriting existed cmis:name property.
         CmisPropertyString name = (CmisPropertyString)getProperty(object, CMIS.NAME);
         if (name == null)
         {
            name = new CmisPropertyString();
            name.setPropertyDefinitionId(CMIS.NAME);
            name.getValue().add(title);
            object.getProperties().getProperty().add(name);
         }
         else
         {
            name.getValue().clear();
            name.getValue().add(title);
         }
      }
      // TODO : check about other properties.
   }

}
