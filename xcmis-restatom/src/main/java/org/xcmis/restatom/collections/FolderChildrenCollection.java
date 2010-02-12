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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectInFolderType;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FolderChildrenCollection.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $
 */
public class FolderChildrenCollection extends CmisObjectCollection
{

   //   private static final Log LOG = ExoLogger.getLogger(FolderChildrenCollection.class);

   /** The navigation service. */
   protected final NavigationService navigationService;

   /**
    * Instantiates a new folder children collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param navigationService the navigation service
    */
   public FolderChildrenCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, NavigationService navigationService)
   {
      super(repositoryService, objectService, versioningService);
      this.navigationService = navigationService;
      setHref("/children");
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
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
         throw new ResponseContextException(msg, 400);
      }
      int maxItems;
      try
      {
         maxItems =
            request.getParameter(AtomCMIS.PARAM_MAX_ITEMS) == null
               || request.getParameter(AtomCMIS.PARAM_MAX_ITEMS).length() == 0 ? CMIS.MAX_ITEMS : Integer
               .parseInt(request.getParameter(AtomCMIS.PARAM_MAX_ITEMS));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_MAX_ITEMS);
         throw new ResponseContextException(msg, 400);
      }
      int skipCount;
      try
      {
         skipCount =
            request.getParameter(AtomCMIS.PARAM_SKIP_COUNT) == null
               || request.getParameter(AtomCMIS.PARAM_SKIP_COUNT).length() == 0 ? 0 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_SKIP_COUNT));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_SKIP_COUNT);
         throw new ResponseContextException(msg, 400);
      }
      boolean includePathSegments = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_PATH_SEGMENT));
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      String orderBy = request.getParameter(AtomCMIS.PARAM_ORDER_BY);
      try
      {
         String objectId = getId(request);
         CmisObjectInFolderListType list =
            navigationService
               .getChildren(getRepositoryId(request), objectId, includeAllowableActions, includeRelationships,
                  includePathSegments, propertyFilter, renditionFilter, orderBy, maxItems, skipCount);
         addPageLinks(objectId, feed, "children", maxItems, skipCount, list.getNumItems() == null ? -1 : list
            .getNumItems().intValue(), list.isHasMoreItems(), request);
         if (list.getObjects().size() > 0)
         {
            if (list.getNumItems() != null)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(list.getNumItems().toString());
            }

            for (CmisObjectInFolderType oif : list.getObjects())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oif.getObject(), request));
               addEntryDetails(request, e, feedIri, oif.getObject());
            }
         }
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
         throw new ResponseContextException(createErrorResponse(iae, 404));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisObjectType> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext postEntry(RequestContext request)
   {
      Entry entry;
      try
      {
         entry = getEntryFromRequest(request);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      String typeId = null;
      String id = null;

      ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
      CmisObjectType object = objectElement.getObject();

      updatePropertiesFromEntry(object, entry);
      for (CmisProperty p : object.getProperties().getProperty())
      {
         String pName = p.getPropertyDefinitionId();
         if (CMIS.OBJECT_TYPE_ID.equals(pName))
            typeId = ((CmisPropertyId)p).getValue().get(0);
         else if (CMIS.OBJECT_ID.equals(pName))
            id = ((CmisPropertyId)p).getValue().get(0);
      }

      try
      {
         if (id != null)
         {
            // move object
            object = objectService.moveObject(getRepositoryId(request), id, getId(request), null);
         }
         else
         {
            CmisAccessControlListType addACL = object.getAcl();
            // TODO : ACEs for removing. Not clear from specification how to
            // pass (obtain) ACEs for adding and removing from one object.
            CmisAccessControlListType removeACL = null;
            List<String> policies = null;
            if (object.getPolicyIds() != null && object.getPolicyIds().getId().size() > 0)
               policies = object.getPolicyIds().getId();

            CmisTypeDefinitionType type = null;
            type = repositoryService.getTypeDefinition(getRepositoryId(request), typeId);

            if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
            {
               String versioningStateParam = request.getParameter(AtomCMIS.PARAM_VERSIONING_SSTATE);
               EnumVersioningState versioningState;
               try
               {
                  versioningState =
                     versioningStateParam == null || versioningStateParam.length() == 0 ? EnumVersioningState.MAJOR
                        : EnumVersioningState.fromValue(versioningStateParam);
               }
               catch (IllegalArgumentException iae)
               {
                  return createErrorResponse("Invalid argument " + versioningStateParam, 400);
               }
               object =
                  objectService.createDocument(getRepositoryId(request), getId(request), object.getProperties(),
                     getContentStream(entry, request), versioningState, addACL, removeACL, policies);
            }
            else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER)
            {
               object =
                  objectService.createFolder(getRepositoryId(request), getId(request), object.getProperties(), addACL,
                     removeACL, policies);
            }
            else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_POLICY)
            {
               object =
                  objectService.createPolicy(getRepositoryId(request), getId(request), object.getProperties(), addACL,
                     removeACL, policies);
            }
         }

      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (UpdateConflictException ue)
      {
         return createErrorResponse(ue, 409);
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (TypeNotFoundException te)
      {
         return createErrorResponse(te, 400);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (StreamNotSupportedException se)
      {
         return createErrorResponse(se, 400); // XXX in specification status is set as 403, correct ???
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }

      entry = request.getAbdera().getFactory().newEntry();
      try
      {
         // updated object
         addEntryDetails(request, entry, request.getResolvedUri(), object);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      Map<String, String> params = new HashMap<String, String>();
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return buildCreateEntryResponse(link, entry);
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Folder Children";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Feed feed = super.createFeedBase(request);
      // Add required links.
      String id = getId(request);

      // Children link.
      String repositoryId = getRepositoryId(request);
      feed.addLink(getChildrenLink(id, request), AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      // Descendants link.
      String descendants = getDescendantsLink(id, request);
      if (descendants != null)
         feed.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);

      // Folder tree link.
      String folderTree = getFolderTreeLink(id, request);
      if (folderTree != null)
         feed.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      // Parent link for not root folder.
      if (!id.equals(repositoryService.getRepositoryInfo(repositoryId).getRootFolderId()))
      {
         try
         {
            CmisObjectType parent = navigationService.getFolderParent(repositoryId, id, null);
            feed.addLink(getObjectLink(getId(parent), request), AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null,
               null, -1);
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
      return feed;
   }

}
