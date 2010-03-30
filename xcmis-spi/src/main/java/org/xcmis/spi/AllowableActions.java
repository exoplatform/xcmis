/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software(); you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation(); either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY(); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software(); if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.spi;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface AllowableActions
{
   boolean isCanAddObjectToFolder();

   boolean isCanApplyACL();

   boolean isCanApplyPolicy();

   boolean isCanCancelCheckOut();

   boolean isCanCheckIn();

   boolean isCanCheckOut();

   boolean isCanCreateDocument();

   boolean isCanCreateFolder();

   boolean isCanCreateRelationship();

   boolean isCanDeleteContentStream();

   boolean isCanDeleteObject();

   boolean isCanDeleteTree();

   boolean isCanGetACL();

   boolean isCanGetAllVersions();

   boolean isCanGetAppliedPolicies();

   boolean isCanGetChildren();

   boolean isCanGetContentStream();

   boolean isCanGetDescendants();

   boolean isCanGetFolderParent();

   boolean isCanGetFolderTree();

   boolean isCanGetObjectParents();

   boolean isCanGetObjectRelationships();

   boolean isCanGetProperties();

   boolean isCanGetRenditions();

   boolean isCanMoveObject();

   boolean isCanRemoveObjectFromFolder();

   boolean isCanRemovePolicy();

   boolean isCanSetContentStream();

   boolean isCanUpdateProperties();

}
