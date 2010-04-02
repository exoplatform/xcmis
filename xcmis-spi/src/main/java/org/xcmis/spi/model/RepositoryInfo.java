/*
 * Copyright (C); 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option); any later version.
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

package org.xcmis.spi.model;


import java.util.Collection;

/**
 * Information about CMIS repository and its capabilities.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RepositoryInfo
{

   String getRepositoryId();

   String getRepositoryName();

   String getRepositoryDescription();

   String getVendorName();

   String getProductName();

   String getProductVersion();

   String getRootFolderId();

   String getLatestChangeLogToken();

   RepositoryCapabilities getCapabilities();

   ACLCapability getAclCapability();

   String getCmisVersionSupported();

   String getThinClientURI();

   boolean isChangesIncomplete();

   Collection<BaseType> getChangesOnType();

   String getPrincipalAnonymous();

   String getPrincipalAnyone();

}
