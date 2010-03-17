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

import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class NavigationServices
{
   /**
    * Instance.
    */
   private static NavigationServices instance;

   /**
    * @return {@link NavigationServices}
    */
   public static NavigationServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link NavigationServices}.
    */
   protected NavigationServices()
   {
      instance = this;
   }
   
   /**
    * Gets the list of child objects contained in the specified folder.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.navigation.event.ChildrenReceivedEvent 
    * ChildrenReceivedEvent} event is fired 
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
   public abstract void getChildren(String url, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, boolean includeAllowableActions,
      boolean includePathSegment);

   /**
    * Gets the set of descendant objects contained in the specified folder or any of its childfolders.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.navigation.event.DescendantsReceivedEvent 
    * DescendantsReceivedEvent} event is fired
    * 
    * @param url url
    * @param depth depth
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   public abstract void getDescendants(String url, int depth, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, boolean includeAllowableActions,
      boolean includePathSegment);

   /**
    * Gets the set of descendant folder objects contained in the specified folder.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.navigation.event.FolderTreeReceivedEvent 
    * FolderTreeReceivedEvent} event is fired. 
    * 
    * @param url url
    * @param depth depth
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includePathSegment includePathSegment
    */
   public abstract void getFolderTree(String url, int depth, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, boolean includeAllowableActions,
      boolean includePathSegment);

   /**
    * Gets the parent folder object for the specified folder object.
    * 
    * On success response received, FolderParentReceivedEvent event is fired.
    * 
    * @param url url
    * @param filter filter
    */
   public abstract void getFolderParent(String url, String filter);

   /**
    * Gets the parent folder(s) for the specified non-folder, fileable object.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.navigation.event.ObjectParentsReceivedEvent 
    * ObjectParentsReceivedEvent} event is fired.
    * 
    * @param url url
    * @param filter filter
    * @param includeEnumIncludeRelationships includeEnumIncludeRelationships
    * @param renditionFilter renditionFilter
    * @param includeAllowableActions includeAllowableActions
    * @param includeRelativePathSegment includeRelativePathSegment
    */
   public abstract void getObjectParents(String url, String filter,
      EnumIncludeRelationships includeEnumIncludeRelationships, String renditionFilter,
      boolean includeAllowableActions, boolean includeRelativePathSegment);

   /**
    * Gets the list of documents that are checked out that the user has access to.
    * 
    * On success response received, CheckedOutReceivedEvent event is fired.
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
   public abstract void getCheckedOut(String url, String folderId, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, boolean includeAllowableActions);

   /**
    * To support paging and get next page with items.
    * 
    * On success response received, NextPageReceivedEvent event is fired.
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
   public abstract void getNextPage(String url, int maxItems, int skipCount, String filter,
      EnumIncludeRelationships includeRelationships, String renditionFilter, boolean includeAllowableActions,
      boolean includePathSegment);
}
