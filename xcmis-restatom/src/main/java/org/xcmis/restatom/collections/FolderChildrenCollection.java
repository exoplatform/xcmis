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
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;

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

   /**
    * Instantiates a new folder children collection.
    * @param storageProvider TODO
    */
   public FolderChildrenCollection(StorageProvider storageProvider)
   {
      super(storageProvider);
      setHref("/children");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
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
         throw new ResponseContextException(msg, 400);
      }
      int maxItems = getIntegerParameter(request, AtomCMIS.PARAM_MAX_ITEMS, CmisConstants.MAX_ITEMS);
      int skipCount = getIntegerParameter(request, AtomCMIS.PARAM_SKIP_COUNT, CmisConstants.SKIP_COUNT);
      boolean includePathSegments = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_PATH_SEGMENT, false);
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      String orderBy = request.getParameter(AtomCMIS.PARAM_ORDER_BY);
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         String objectId = getId(request);
         ItemsList<CmisObject> list =
            conn.getChildren(objectId, includeAllowableActions, includeRelationships, includePathSegments, true,
               propertyFilter, renditionFilter, orderBy, maxItems, skipCount);
         addPageLinks(objectId, feed, "children", maxItems, skipCount, list.getNumItems(), list.isHasMoreItems(),
            request);
         if (list.getItems().size() > 0)
         {
            if (list.getNumItems() != -1)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(Integer.toString(list.getNumItems()));
            }

            for (CmisObject oif : list.getItems())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oif, request));
               addEntryDetails(request, e, feedIri, oif);
            }
         }
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
         throw new ResponseContextException(createErrorResponse(iae, 404));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
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

      String sourceFolderId = request.getParameter(AtomCMIS.PARAM_SOURCE_FOLDER_ID);

      String typeId = null;
      String id = null;
      CmisObject newObject = null;

      ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
      boolean hasCMISElement = objectElement != null;
      CmisObject object = hasCMISElement ? object = objectElement.getObject() : new CmisObject();
      updatePropertiesFromEntry(object, entry);
      if (hasCMISElement)
      {
         for (Property<?> p : object.getProperties().values())
         {
            String pName = p.getId();
            if (CmisConstants.OBJECT_TYPE_ID.equals(pName))
            {
               typeId = ((IdProperty)p).getValues().get(0);
            }
            else if (CmisConstants.OBJECT_ID.equals(pName))
            {
               id = ((IdProperty)p).getValues().get(0);
            }

         }
      }
      else
      {
         typeId = CmisConstants.DOCUMENT;
         IdProperty idProperty = new IdProperty();
         idProperty.setId(CmisConstants.OBJECT_TYPE_ID);
         idProperty.getValues().add(typeId);
         object.getProperties().put(idProperty.getId(), idProperty);
      }

      Connection conn = null;
      try
      {
         conn = getConnection(request);
         String objectId = null;
         String targetFolderId = getId(request);
         if (id != null)
         {
            if (sourceFolderId == null)
            {
               conn.addObjectToFolder(id, targetFolderId, false);
            }
            else
            {
               // move object
               objectId = conn.moveObject(id, targetFolderId, sourceFolderId);
            }
         }
         else
         {
            List<AccessControlEntry> addACL = object.getACL();
            // TODO : ACEs for removing. Not clear from specification how to
            // pass (obtain) ACEs for adding and removing from one object.
            List<AccessControlEntry> removeACL = null;
            List<String> policies = null;
            if (object.getPolicyIds() != null && object.getPolicyIds().size() > 0)
            {
               policies = (List<String>)object.getPolicyIds();
            }

            TypeDefinition type = null;
            type = conn.getTypeDefinition(typeId);

            if (type.getBaseId() == BaseType.DOCUMENT)
            {
               String versioningStateParam = request.getParameter(AtomCMIS.PARAM_VERSIONING_STATE);
               VersioningState versioningState;
               try
               {
                  versioningState =
                     versioningStateParam == null || versioningStateParam.length() == 0 ? VersioningState.MAJOR
                        : VersioningState.fromValue(versioningStateParam);
               }
               catch (IllegalArgumentException iae)
               {
                  return createErrorResponse("Invalid argument " + versioningStateParam, 400);
               }
               objectId =
                  conn.createDocument(getId(request), object.getProperties(), getContentStream(entry, request), addACL,
                     removeACL, policies, versioningState);
            }
            else if (type.getBaseId() == BaseType.FOLDER)
            {
               objectId = conn.createFolder(getId(request), object.getProperties(), addACL, removeACL, policies);
            }
            else if (type.getBaseId() == BaseType.POLICY)
            {
               objectId = conn.createPolicy(getId(request), object.getProperties(), addACL, removeACL, policies);
            }

         }
         // TODO do we need to fill the perameters ?
         boolean isIncludeAllowableActions = false;
         IncludeRelationships isIncludeRelationships = null;
         boolean isIncludePolicyIDs = false;
         String renditionFilter = null;
         String propertyFilter = CmisConstants.WILDCARD;
         boolean isIncludeAcl = false;
         newObject =
            conn.getObject(objectId, isIncludeAllowableActions, isIncludeRelationships, isIncludePolicyIDs,
               isIncludeAcl, true, propertyFilter, renditionFilter);

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
         {
            conn.close();
         }
      }

      entry = request.getAbdera().getFactory().newEntry();
      try
      {
         // updated object
         addEntryDetails(request, entry, request.getResolvedUri(), newObject);
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
      feed.addLink(getChildrenLink(id, request), AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      // Descendants link.
      String descendants = getDescendantsLink(id, request);
      if (descendants != null)
      {
         feed.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);
      }

      // Folder tree link.
      String folderTree = getFolderTreeLink(id, request);
      if (folderTree != null)
      {
         feed.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);
      }

      // Parent link for not root folder.
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         if (!id.equals(conn.getStorage().getRepositoryInfo().getRootFolderId()))
         {
            try
            {
               CmisObject parent = conn.getFolderParent(id, true, null);
               feed.addLink(getObjectLink(getId(parent), request), AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
                  null, null, -1);
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
         }
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
      return feed;
   }

}
