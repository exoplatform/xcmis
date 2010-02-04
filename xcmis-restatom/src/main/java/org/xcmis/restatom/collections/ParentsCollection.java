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
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ParentsCollection extends CmisObjectCollection
{

   /** The navigation service. */
   protected NavigationService navigationService;

   /**
    * Instantiates a new parents collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param navigationService the navigation service
    */
   public ParentsCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, NavigationService navigationService)
   {
      super(repositoryService, objectService, versioningService);
      this.navigationService = navigationService;
      setHref("/parents");
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisObjectType> getEntries(RequestContext request) throws ResponseContextException
   {
      // To process hierarchically structure override addFeedDetails(Feed, RequestContext) method.
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Object Parents";
   }

   /**
    * Adds the feed details.
    * 
    * @param feed the feed
    * @param request the request
    * @throws ResponseContextException the response context exception
    */
   @Override
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
      boolean includeRelativePathSegment =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIVE_PATH_SEGMENT));
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      EnumIncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null ? EnumIncludeRelationships.NONE
               : EnumIncludeRelationships.fromValue(request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(msg, 400);
      }

      String repositoryId = getRepositoryId(request);
      try
      {
         String objectId = getId(request);
         CmisObjectType object =
            objectService.getObject(repositoryId, objectId, false, EnumIncludeRelationships.NONE, false, false,
               CMIS.BASE_TYPE_ID, null);

         switch (getBaseObjectType(object))
         {
            case CMIS_FOLDER :
               CmisObjectType folderParent = navigationService.getFolderParent(repositoryId, objectId, propertyFilter);
               if (folderParent != null)
               {
                  // add cmisra:numItems
                  Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
                  numItems.setText("1"); // Folder always has only one parent.
                  Entry e = feed.addEntry();
                  IRI feedIri = new IRI(getFeedIriForEntry(folderParent, request));
                  addEntryDetails(request, e, feedIri, folderParent);
                  CmisPropertyString name = (CmisPropertyString)getProperty(folderParent, CMIS.NAME);
                  if (name != null && name.getValue().size() > 0)
                  {
                     // add cmisra:relativePathSegment
                     Element pathSegment = e.addExtension(AtomCMIS.RELATIVE_PATH_SEGMENT);
                     pathSegment.setText(name.getValue().get(0));
                  }
               }
               break;
            default :
               List<CmisObjectParentsType> parents =
                  navigationService.getObjectParents(repositoryId, objectId, includeAllowableActions,
                     includeRelationships, includeRelativePathSegment, propertyFilter, renditionFilter);
               if (parents.size() > 0)
               {
                  // add cmisra:numItems
                  Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
                  numItems.setText(Integer.toString(parents.size()));

                  for (CmisObjectParentsType parent : parents)
                  {
                     Entry e = feed.addEntry();
                     IRI feedIri = new IRI(getFeedIriForEntry(parent.getObject(), request));
                     addEntryDetails(request, e, feedIri, parent.getObject());
                     if (parent.getRelativePathSegment() != null)
                     {
                        // add cmisra:relativePathSegment
                        Element pathSegment = e.addExtension(AtomCMIS.RELATIVE_PATH_SEGMENT);
                        pathSegment.setText(parent.getRelativePathSegment());
                     }
                  }
               }
               break;
         }
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 500));
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
