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

package org.xcmis.restatom;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.apache.commons.fileupload.FileItem;
import org.xcmis.atom.CmisUriTemplateType;
import org.xcmis.core.AccessControlService;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.MultifilingService;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.xcmis.restatom.abdera.AccessControlListTypeElement;
import org.xcmis.restatom.abdera.AllowableActionsElement;
import org.xcmis.restatom.abdera.RepositoryInfoTypeElement;
import org.xcmis.restatom.abdera.UriTemplateTypeElement;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.UpdateConflictException;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeTypeParseException;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
@Path(AtomCMIS.CMIS_REST_RESOURCE_PATH)
public class AtomCmisService implements ResourceContainer
{

   /** The provider. */
   protected ProviderImpl provider;

   /** The repository service. */
   protected RepositoryService repositoryService;

   /** The object service. */
   protected ObjectService objectService;

   /** The acl service. */
   protected AccessControlService aclService;

   /**
    * Instantiates a new atom cmis service.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param navigationService the navigation service
    * @param relationshipService the relationship service
    * @param policyService the policy service
    * @param aclService the acl service
    * @param queryService the query service
    * @param multifilingService the multifiling service
    * @param versioningService the versioning service
    */
   public AtomCmisService(RepositoryService repositoryService, ObjectService objectService,
      NavigationService navigationService, RelationshipService relationshipService, PolicyService policyService,
      AccessControlService aclService, DiscoveryService queryService, MultifilingService multifilingService,
      VersioningService versioningService)
   {
      this.repositoryService = repositoryService;
      this.objectService = objectService;
      this.aclService = aclService;
      provider =
         new ProviderImpl(repositoryService, objectService, navigationService, relationshipService, policyService,
            aclService, queryService, multifilingService, versioningService);
      provider.init(AbderaFactory.getInstance(), new HashMap<String, String>());
   }

   @PUT
   @Path("{repositoryId}/addacl/{objectId}")
   @Produces("application/cmisacl+xml")
   public Response addACL(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId,
      @PathParam("objectId") String objectId)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      try
      {
         Document<AccessControlListTypeElement> doc = request.getDocument();
         AccessControlListTypeElement listEl = doc.getRoot();
         CmisAccessControlListType list = listEl.getACL();
         aclService.applyACL(repositoryId, objectId, list, new CmisAccessControlListType(),
            EnumACLPropagation.REPOSITORYDETERMINED);
      }
      catch (IOException io)
      {
         throw new WebApplicationException(io, createErrorResponse(io, 500));
      }
      catch (org.xcmis.spi.RepositoryException re)
      {
         throw new WebApplicationException(re, createErrorResponse(re, 500));
      }

      return Response.status(201).build();
   }

   @POST
   @Path("{repositoryId}/types")
   @RolesAllowed({"administrator"})
   public Response addType(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return createItem(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/policies/{objectId}")
   @Produces("application/atom+xml;type=entry")
   public Response applyPolicy(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return createItem(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/checkedout{rubbish:(/)?}{documentId:.*}")
   public Response checkOut(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
      throws Exception
   {
      return createItem(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/children/{folderId}")
   @Produces("application/atom+xml;type=entry")
   public Response createChild(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return createItem(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/object/{folderId}")
   @Produces("application/atom+xml;type=entry")
   public Response createChildObj(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      // Found some clients those use direct object (folder) link for adding child.
      return createItem(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/relationships/{objectId}")
   @Produces("application/atom+xml;type=entry")
   public Response createRelationship(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return createItem(repositoryId, httpRequest);
   }

   @DELETE
   @Path("{repositoryId}/file/{objectId}")
   public Response deleteContentStream(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      ResponseContext abderaResponse = ((AbstractCollectionAdapter)getCollection(request)).deleteMedia(request);
      return Response.status(abderaResponse.getStatus()).entity(abderaResponse).build();
   }

   @DELETE
   @Path("{repositoryId}/object/{objectId}")
   public Response deleteObject(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return deleteItem(repositoryId, httpRequest);
   }

   @DELETE
   @Path("{repositoryId}/descendants/{folderId}")
   public Response deleteTree(@PathParam("repositoryId") String repositoryId, @PathParam("folderId") String folderId,
      @QueryParam("unfileObject") String unfileNonfolderObjects,
      @DefaultValue("false") @QueryParam("continueOnFailure") boolean continueOnFailure)
   {
      EnumUnfileObject unfileObject;
      try
      {
         unfileObject =
            unfileNonfolderObjects == null ? EnumUnfileObject.DELETE : EnumUnfileObject
               .fromValue(unfileNonfolderObjects);
      }
      catch (IllegalArgumentException e)
      {
         throw new IllegalArgumentException("Unsupported 'unfileObject' attribute: " + unfileNonfolderObjects);
      }

      try
      {
         objectService.deleteTree(repositoryId, folderId, unfileObject, continueOnFailure);
         return Response.noContent().build();
      }
      catch (RepositoryException re)
      {
         throw new WebApplicationException(re, createErrorResponse(re, 500));
      }
      catch (UpdateConflictException uce)
      {
         throw new WebApplicationException(uce, createErrorResponse(uce, 409));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new WebApplicationException(onfe, createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new WebApplicationException(iae, createErrorResponse(iae, 400));
      }
      catch (Throwable t)
      {
         throw new WebApplicationException(t, createErrorResponse(t, 500));
      }
   }

   @DELETE
   @Path("{repositoryId}/types/{typeId}")
   @RolesAllowed({"administrator"})
   public Response deleteType(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return deleteItem(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/objacl/{objectId}")
   @Produces("application/cmisacl+xml")
   public Response getACL(@PathParam("repositoryId") String repositoryId, @PathParam("objectId") String objectId,
      @DefaultValue("true") @QueryParam("onlyBasicPermissions") boolean onlyBasicPermissions)
   {
      try
      {
         CmisAccessControlListType list = aclService.getACL(repositoryId, objectId, onlyBasicPermissions);
         AccessControlListTypeElement el = AbderaFactory.getInstance().getFactory().newElement(AtomCMIS.ACCESS_CONTROL);
         el.build(list);
         return Response.ok(el).header(HttpHeaders.CACHE_CONTROL, "no-cache").build();
      }
      catch (RepositoryException re)
      {
         throw new WebApplicationException(re, createErrorResponse(re, 500));
      }
      catch (Throwable others)
      {
         throw new WebApplicationException(others, createErrorResponse(others, 500));
      }
   }

   @GET
   @Path("{repositoryId}/allowableactions/{objectId}")
   @Produces("application/atom+xml;type=allowableActions")
   public Response getAllowableActions(@PathParam("repositoryId") String repositoryId,
      @PathParam("objectId") String objectId)
   {
      try
      {
         CmisAllowableActionsType result = objectService.getAllowableActions(repositoryId, objectId);
         AllowableActionsElement el = AbderaFactory.getInstance().getFactory().newElement(AtomCMIS.ALLOWABLE_ACTIONS);
         el.build(result);
         return Response.ok(el).header(HttpHeaders.CACHE_CONTROL, "no-cache").build();
      }
      catch (RepositoryException re)
      {
         throw new WebApplicationException(re, createErrorResponse(re, 500));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new WebApplicationException(onfe, createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new WebApplicationException(iae, createErrorResponse(iae, 400));
      }
      catch (Throwable others)
      {
         throw new WebApplicationException(others, createErrorResponse(others, 500));
      }
   }

   @GET
   @Path("{repositoryId}/typedescendants")
   @Produces("application/cmistree+xml")
   public Response getAllTypeDescendants(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      // The same jobs as 'getTypeDescendants()' but respect URL pattern.
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/types")
   @Produces("application/atom+xml;type=entry")
   public Response getAllTypes(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      // The same jobs as 'getTypes()' but respect URL pattern.
      return getTypes(httpRequest, repositoryId);
   }

   @GET
   @Path("{repositoryId}/versions/{versionSeriesId}")
   @Produces("application/atom+xml;type=entry")
   public Response getAllVersions(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/policies/{objectId}")
   public Response getAppliedPolicies(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/checkedout{rubbish:(/)?}{folderId:.*}")
   public Response getCheckedOut(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/children/{folderId}")
   @Produces("application/atom+xml;type=feed")
   public Response getChildren(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/file/{documentId}")
   public Response getContentStream(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      @SuppressWarnings("unchecked")
      ResponseContext abderaResponse = ((AbstractEntityCollectionAdapter)getCollection(request)).getMedia(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      builder.entity(abderaResponse);
      // Cache-Control headers ?
      return builder.build();
   }

   @GET
   @Path("{repositoryId}/descendants/{folderId}")
   @Produces("application/cmistree+xml")
   public Response getDescendants(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/foldertree/{folderId}")
   @Produces("application/atom+xml;type=feed")
   public Response getFolderTree(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/object/{objectId}")
   @Produces("application/atom+xml;type=entry")
   public Response getObjectById(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getEntry(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/objectbypath/{path:.*}")
   @Produces("application/atom+xml;type=entry")
   public Response getObjectByPath(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getEntry(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/parents/{objectId}")
   public Response getObjectParents(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/relationships/{objectId}")
   @Produces("application/atom+xml;type=entry")
   public Response getRelationships(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/alternate/{documentId}/{streamId}")
   public Response getRendition(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId, @PathParam("documentId") String documentId)
   {
      return getContentStream(httpRequest, repositoryId);
   }

   @GET
   public Response getRepositories(@Context HttpServletRequest httpRequest, @Context UriInfo uriInfo)
   {
      List<CmisRepositoryEntryType> entries = repositoryService.getRepositories();
      Service service = AbderaFactory.getInstance().getFactory().newService();
      service.declareNS(AtomCMIS.CMISRA_NS_URI, AtomCMIS.CMISRA_PREFIX);
      for (CmisRepositoryEntryType entry : entries)
      {
         String repositoryId = entry.getRepositoryId();
         addCmisRepository(httpRequest, service, repositoryId, uriInfo.getBaseUri());
      }
      return Response.ok().entity(service).header(HttpHeaders.CACHE_CONTROL, "no-cache").type(
         MediaType.APPLICATION_ATOM_XML).build();
   }

   @GET
   @Path("{repositoryId}")
   public Response getRepositoryInfo(@Context HttpServletRequest httpRequest, @Context UriInfo uriInfo,
      @PathParam("repositoryId") String repositoryId)
   {
      Service service = AbderaFactory.getInstance().getFactory().newService();
      service.declareNS(AtomCMIS.CMIS_NS_URI, AtomCMIS.CMIS_PREFIX);
      service.declareNS(AtomCMIS.CMISRA_NS_URI, AtomCMIS.CMISRA_PREFIX);
      addCmisRepository(httpRequest, service, repositoryId, uriInfo.getBaseUri());
      Document<Service> serviceDocument = service.getDocument();
      serviceDocument.setCharset("utf-8");
      ResponseContext abderaResponse = new BaseResponseContext<Document<Service>>(serviceDocument);
      abderaResponse.setStatus(200);
      return Response.ok(abderaResponse).header(HttpHeaders.CACHE_CONTROL, "no-cache").type(
         MediaType.APPLICATION_ATOM_XML).build();
   }

   @GET
   @Path("{repositoryId}/typebyid/{typeId}")
   @Produces("application/atom+xml;type=entry")
   public Response getTypeById(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getEntry(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/typedescendants/{typeId}")
   @Produces("application/cmistree+xml")
   public Response getTypeDescendants(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @GET
   @Path("{repositoryId}/types/{typeId}")
   @Produces("application/atom+xml;type=entry")
   public Response getTypes(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @POST
   @Path("{repositoryId}/query")
   public Response query(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return getFeed(repositoryId, httpRequest);
   }

   @DELETE
   @Path("{repositoryId}/policies/{objectId}")
   public Response removePolicy(@Context HttpServletRequest httpRequest, @PathParam("repositoryId") String repositoryId)
   {
      return deleteItem(repositoryId, httpRequest);
   }

   @PUT
   @Path("{repositoryId}/file/{objectId}")
   public Response setContentStream(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      @SuppressWarnings("unchecked")
      ResponseContext abderaResponse = ((AbstractEntityCollectionAdapter)getCollection(request)).putMedia(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      return builder.entity(abderaResponse).build();
   }

   @POST
   @Path("{repositoryId}/file/{objectId}")
   @Consumes("multipart/form-data")
   @SuppressWarnings("unchecked")
   public Response setContentStream(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId, @QueryParam(HttpHeaders.CONTENT_TYPE) String contentType,
      Iterator<FileItem> files)
   {
      if (files.hasNext())
      {
         FileItem file = files.next();
         // In fact expected to get just one file. Other items
         // in iterator may be simple form fields, and we are not
         // care about it. Just skip all of them.
         if (!file.isFormField())
         {
            try
            {
               // Content-Type passed as query parameter, we do not
               // wont to use passed by browser. But if parameter does
               // not exists then try to get media type passed by browser.
               if (contentType == null)
                  contentType = file.getContentType();

               RequestContext request = initRequestContext(repositoryId, httpRequest);
               ((AbstractEntityCollectionAdapter)getCollection(request)).putMedia(null, new javax.activation.MimeType(
                  contentType), null, file.getInputStream(), request);
               return Response.status(201).build();
            }
            catch (IOException ioe)
            {
               throw new WebApplicationException(ioe, createErrorResponse(ioe, 500));
            }
            catch (ResponseContextException rce)
            {
               throw new WebApplicationException(rce, createErrorResponse(rce, rce.getResponseContext().getStatus()));
            }
            catch (MimeTypeParseException mte)
            {
               throw new WebApplicationException(mte, createErrorResponse(mte, 400));
            }
         }
      }
      // XXX 
      throw new WebApplicationException(new InvalidArgumentException("Content of document is missing."), 400);
   }

   @PUT
   @Path("{repositoryId}/object/{objectId}")
   @Produces("application/atom+xml;type=entry")
   public Response updateProperties(@Context HttpServletRequest httpRequest,
      @PathParam("repositoryId") String repositoryId)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      @SuppressWarnings("unchecked")
      ResponseContext abderaResponse = ((AbstractEntityCollectionAdapter)getCollection(request)).putEntry(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      return builder.entity(abderaResponse).build();
   }

   private void copyAbderaHeaders(ResponseBuilder responseBuilder, ResponseContext abderaResponse)
   {
      for (String headerName : abderaResponse.getHeaderNames())
      {
         for (Object v : abderaResponse.getHeaders(headerName))
            // TODO : need avoid direct casting to String.
            // For now just be sure not get errors if RESTful framework.
            responseBuilder.header(headerName, v.toString());
      }
   }

   protected Workspace addCmisRepository(HttpServletRequest httpRequest, Service service, String repositoryId,
      URI baseUri)
   {
      CmisRepositoryInfoType repoInfo;
      try
      {
         repoInfo = repositoryService.getRepositoryInfo(repositoryId);
      }
      catch (InvalidArgumentException iae)
      {
         throw new WebApplicationException(iae, createErrorResponse(iae, 400));
      }
      catch (Throwable others)
      {
         throw new WebApplicationException(others, createErrorResponse(others, 500));
      }

      Workspace ws = service.addWorkspace(repositoryId);
      ws.setTitle(repoInfo.getRepositoryName());
      RepositoryInfoTypeElement repoInfoElement = ws.addExtension(AtomCMIS.REPOSITORY_INFO);
      repoInfoElement.build(repoInfo);

      RequestContext request = initRequestContext(repositoryId, httpRequest);
      String repoPath = UriBuilder.fromUri(baseUri).path(getClass()).path(repositoryId).build().toString();
      Collection<CollectionInfo> collectionsInfo = getCollectionsInfo(request);
      for (CollectionInfo collectionInfo : collectionsInfo)
      {
         AbstractCollectionAdapter collectionAdapter = (AbstractCollectionAdapter)collectionInfo;
         String href = collectionAdapter.getHref();
         String collectionType = null;
         String path = repoPath + href;
         if (href.equals("/children"))
         {
            collectionType = AtomCMIS.COLLECTION_TYPE_ROOT;
            path += '/' + repoInfo.getRootFolderId();
         }
         else if (href.equals("/types"))
         {
            collectionType = AtomCMIS.COLLECTION_TYPE_TYPES;
         }
         else if (href.equals("/checkedout"))
         {
            collectionType = AtomCMIS.COLLECTION_TYPE_CHECKEDOUT;
         }
         else if (href.equals("/query"))
         {
            collectionType = AtomCMIS.COLLECTION_TYPE_QUERY;
         }

         if (collectionType != null)
         {
            org.apache.abdera.model.Collection collection = ws.addCollection(collectionAdapter.getTitle(request), path);
            collection.addSimpleExtension(AtomCMIS.COLLECTION_TYPE, collectionType);
         }
      }

      // XXX : Does not support 'Unfiling' but need add it.
      org.apache.abdera.model.Collection collection = ws.addCollection("Unfiled collection", repoPath + "/unfiled");
      collection.addSimpleExtension(AtomCMIS.COLLECTION_TYPE, AtomCMIS.COLLECTION_TYPE_UNFILED);

      // objectbyid template
      CmisUriTemplateType objectById = new CmisUriTemplateType();
      objectById.setMediatype(AtomCMIS.MEDIATYPE_ATOM_ENTRY);
      objectById.setTemplate(new StringBuilder() //
         .append(repoPath) //
         .append("/object/{id}?") //
         .append("filter={filter}&amp;") //
         .append("includeAllowableActions={includeAllowableActions}&amp;") //
         .append("includePolicyIds={includePolicyIds}&amp;") //
         .append("includeRelationships={includeRelationships}&amp;") //
         .append("includeACL={includeACL}").toString());
      objectById.setType(AtomCMIS.URITEMPLATE_OBJECTBYID);
      UriTemplateTypeElement objectByIdElement = ws.addExtension(AtomCMIS.URITEMPLATE);
      objectByIdElement.build(objectById);

      // objectbypath template
      CmisUriTemplateType folderByPath = new CmisUriTemplateType();
      folderByPath.setMediatype(AtomCMIS.MEDIATYPE_ATOM_ENTRY);
      folderByPath.setTemplate(new StringBuilder() //
         .append(repoPath) //
         .append("/objectbypath/{objectpath}?")//
         .append("filter={filter}&amp;")//
         .append("includeAllowableActions={includeAllowableActions}&amp;")//
         .append("includePolicyIds={includePolicyIds}&amp;")//
         .append("includeRelationships={includeRelationships}&amp;")//
         .append("includeACL={includeACL}").toString());
      folderByPath.setType(AtomCMIS.URITEMPLATE_OBJECTBYPATH);
      UriTemplateTypeElement folderByPathElement = ws.addExtension(AtomCMIS.URITEMPLATE);
      folderByPathElement.build(folderByPath);

      //      // query template
      //      CmisUriTemplateType query = new CmisUriTemplateType();
      //      query.setMediatype(AtomCMIS.MEDIATYPE_ATOM_FEED);
      //      query.setTemplate(repoPath + "/query?"//
      //         + "q={q}&amp;"//
      //         + "searchAllVersions={searchAllVersions}&amp;"//
      //         + "maxItems={maxItems}&amp;skipCount={skipCount}&amp;"//
      //         + "includeAllowableActions={includeAllowableActions}=&amp;"//
      //         + "includeRelationships={includeRelationships}");
      //      query.setType(AtomCMIS.URITEMPLATE_QUERY);
      //      UriTemplateTypeElement queryElement = ws.addExtension(AtomCMIS.URITEMPLATE);
      //      queryElement.build(query);

      // typebyid template
      CmisUriTemplateType typeById = new CmisUriTemplateType();
      typeById.setMediatype(AtomCMIS.MEDIATYPE_ATOM_ENTRY);
      typeById.setTemplate(repoPath + "/typebyid/{id}");
      typeById.setType(AtomCMIS.URITEMPLATE_TYPEBYID);
      UriTemplateTypeElement typeByIdElement = ws.addExtension(AtomCMIS.URITEMPLATE);
      typeByIdElement.build(typeById);

      return ws;

   }

   protected Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }

   protected Response createItem(String repositoryId, HttpServletRequest httpRequest)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      ResponseContext abderaResponse = getCollection(request).postEntry(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      return builder.entity(abderaResponse).build();
   }

   protected Response deleteItem(String repositoryId, HttpServletRequest httpRequest)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      ResponseContext abderaResponse = getCollection(request).deleteEntry(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      return builder.entity(abderaResponse).build();
   }

   protected CollectionAdapter getCollection(RequestContext request)
   {
      return provider.getWorkspaceManager(request).getCollectionAdapter(request);
   }

   protected Collection<CollectionInfo> getCollectionsInfo(RequestContext request)
   {
      Collection<WorkspaceInfo> workspaces =
         ((ProviderImpl)provider).getWorkspaceManager(request).getWorkspaces(request);
      Collection<CollectionInfo> collections =
         (Collection<CollectionInfo>)workspaces.iterator().next().getCollections(request);
      return collections;
   }

   protected Response getEntry(String repositoryId, HttpServletRequest httpRequest)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      ResponseContext abderaResponse = getCollection(request).getEntry(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      builder.header(HttpHeaders.CACHE_CONTROL, "no-cache");
      return builder.entity(abderaResponse).build();
   }

   protected Response getFeed(String repositoryId, HttpServletRequest httpRequest)
   {
      RequestContext request = initRequestContext(repositoryId, httpRequest);
      ResponseContext abderaResponse = getCollection(request).getFeed(request);
      ResponseBuilder builder = Response.status(abderaResponse.getStatus());
      copyAbderaHeaders(builder, abderaResponse);
      builder.header(HttpHeaders.CACHE_CONTROL, "no-cache");
      return builder.entity(abderaResponse).build();
   }

   protected RequestContext initRequestContext(String repositoryId, HttpServletRequest httpRequest)
   {
      return new ServletRequestContext(provider, httpRequest)
      {

         @Override
         public String getTargetPath()
         {
            String uri = getUri().toString();
            String bpath = getTargetBasePath();
            return uri.substring(bpath.length());
         }

      };
   }

}
