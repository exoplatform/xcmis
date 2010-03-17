/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.services.navigation;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;
import org.xcmis.gwtframework.client.model.restatom.AtomEntry;
import org.xcmis.gwtframework.client.model.restatom.EntryCollection;
import org.xcmis.gwtframework.client.services.navigation.event.CheckedOutReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.ChildrenReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.DescendantsReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.FolderParentReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.FolderTreeReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.NextPageReceivedEvent;
import org.xcmis.gwtframework.client.services.navigation.event.ObjectParentsReceivedEvent;
import org.xcmis.gwtframework.client.unmarshallers.EntryCollectionUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.EntryUnmarshaller;
import org.xcmis.gwtframework.client.util.AsyncRequest;
import org.xcmis.gwtframework.client.util.AsyncRequestCallback;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class NavigationServicesImp extends NavigationServices
{
   
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public NavigationServicesImp(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   
   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getChildren(
    * java.lang.String, int, int, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, 
    * java.lang.String, boolean, boolean)
    * 
    * @param url url
    * @param maxItems maxItems
    * @param skipCount skipCount
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   @Override
   public void getChildren(String url, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, 
      boolean includeAllowableActions,
      boolean includePathSegment)
   {
      EntryCollection entryCollection = new EntryCollection();
      ChildrenReceivedEvent event = new ChildrenReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_PATH_SEGMENT + "=" + includePathSegment;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getNextPage(
    * java.lang.String, int, int, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, 
    * java.lang.String, boolean, boolean)
    * 
    * @param url url
    * @param maxItems maxItems
    * @param skipCount skipCount
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   @Override
   public void getNextPage(String url, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, 
      boolean includeAllowableActions,
      boolean includePathSegment)
   {
      EntryCollection entryCollection = new EntryCollection();
      NextPageReceivedEvent event = new NextPageReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_PATH_SEGMENT + "=" + includePathSegment + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getDescendants(
    * java.lang.String, int, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, 
    * java.lang.String, boolean, boolean)
    * 
    * @param url url
    * @param depth depth
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   @Override
   public void getDescendants(String url, int depth, String filter, 
      EnumIncludeRelationships includeRelationships,
      String renditionFilter, boolean includeAllowableActions, boolean includePathSegment)
   {
      EntryCollection entryCollection = new EntryCollection();
      DescendantsReceivedEvent event = new DescendantsReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = "";
      params += (depth < -1) ? "" : CmisArguments.DEPTH + "=" + depth + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_PATH_SEGMENT + "=" + includePathSegment;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getFolderTree(
    * java.lang.String, int, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, 
    * java.lang.String, boolean, boolean)
    * 
    * @param url url
    * @param depth depth
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   @Override
   public void getFolderTree(String url, int depth, String filter, 
      EnumIncludeRelationships includeRelationships,
      String renditionFilter, boolean includeAllowableActions, boolean includePathSegment)
   {
      EntryCollection entryCollection = new EntryCollection();
      FolderTreeReceivedEvent event = new FolderTreeReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = "";
      params += (depth < -1) ? "" : CmisArguments.DEPTH + "=" + depth + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_PATH_SEGMENT + "=" + includePathSegment;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getFolderParent(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param filter filter
    */
   @Override
   public void getFolderParent(String url, String filter)
   {
      AtomEntry entry = new AtomEntry();
      FolderParentReceivedEvent event = new FolderParentReceivedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      String params = (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getObjectParents(
    * java.lang.String, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, 
    * java.lang.String, boolean, boolean)
    * 
    * @param url url
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includeRelativePathSegment includeRelativePathSegment
    */
   @Override
   public void getObjectParents(String url, String filter, EnumIncludeRelationships includeRelationships,
      String renditionFilter, boolean includeAllowableActions, boolean includeRelativePathSegment)
   {
      EntryCollection entryCollection = new EntryCollection();
      ObjectParentsReceivedEvent event = new ObjectParentsReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = "";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_RELATIVE_PATH_SEGMENT + "=" + includeRelativePathSegment;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.NavigationServices#getCheckedOut(
    * java.lang.String, java.lang.String, int, int, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, java.lang.String, boolean)
    * 
    * @param url url
    * @param folderId folderId
    * @param maxItems maxItems
    * @param skipCount skipCount
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    */
   @Override
   public void getCheckedOut(String url, String folderId, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, 
      boolean includeAllowableActions)
   {
      EntryCollection entryCollection = new EntryCollection();
      CheckedOutReceivedEvent event = new CheckedOutReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      url += (folderId == null || folderId.length() < 0) ? "" : "/" + folderId;

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

}
