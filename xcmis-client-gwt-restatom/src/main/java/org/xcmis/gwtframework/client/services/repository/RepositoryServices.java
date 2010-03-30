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

package org.xcmis.gwtframework.client.services.repository;

import org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class RepositoryServices
{
   /**
    * Instance.
    */
   private static RepositoryServices instance;

   /**
    * @return {@link RepositoryServices}
    */
   public static RepositoryServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link RepositoryServices}.
    */
   protected RepositoryServices()
   {
      instance = this;
   }


   /**
    * Get a list of CMIS repositories available from this CMIS service endpoint.
    * 
    * On success response received, RepositoriesReceivedEvent event is fired
    * 
    * @param url url
    */
   public abstract void getRepositories(String url);

   /**
    * Get information about the CMIS repository, the optional capabilities it supports and its 
    * Access Control information if applicable.
    * 
    * On success response received, RepositoryInfoReceivedEvent event is fired
    * 
    * @param url url
    */
   public abstract void getRepositoryInfo(String url);

   /**
    * Returns the list of Object-Types defined for the Repository that are children of the specified type.
    * 
    * On success response received, TypeChildrenReceivedEvent event is fired
    * 
    * @param url url
    * @param includePropertyDefinitions include property definitions
    * @param maxItems max items
    * @param skipCount skip count
    */
   public abstract void getTypeChildren(String url, boolean includePropertyDefinitions, int maxItems,
      int skipCount);

   /**
    * Get the set of descendant Object-Types defined for the Repository under the specified type.
    * 
    * On success response received, TypeDescendantsRecievedEvent event is fired
    * 
    * @param url url
    * @param typeId type id
    * @param depth depth
    * @param includePropertyDefinition include property definition
    */
   public abstract void getTypeDescendants(String url, String typeId, int depth, 
      boolean includePropertyDefinition);

   /**
    * Gets the definition of the specified Object-Type.
    * 
    * On success response received, TypeDefinitionReceivedEvent event is fired
    * 
    * @param url url
    */
   public abstract void getTypeDefinition(String url);
   
   /**
    * Get types as a plain list.
    * 
    * On success response received, TypeListReceivedEvent event is fired
    * 
    * @param url url
    * @param includePropertyDefinition include property definition
    */
   public abstract void getTypeList(String url, boolean includePropertyDefinition);

   /**
    * Get base types of the repository.
    * 
    * On success response received, BaseTypesReceivedEvent event is fired
    * 
    * @param url url
    * @param includePropertyDefinition include property definition
    */
   public abstract void getBaseTypes(String url, boolean includePropertyDefinition);

   /**
    * Create type.
    * 
    * On success response received, TypeCreatedEvent event is fired
    * 
    * @param url url
    * @param type type
    */
   public abstract void addType(String url, CmisTypeDefinitionType type);
   
   /**
    * Delete type by url.
    * 
    * On success response received, TypeDeletedEvent event is fired
    * 
    * @param url url
    */
   public abstract void deleteType(String url);
}
