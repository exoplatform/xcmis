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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.object.ContentStream;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Storage.java 2 2010-02-04 17:21:49Z andrew00x $
 */
class Storage
{

   /** Map of id -> data.*/
   protected final Map<String, Map<String, Object[]>> objects;

   /** Map of id -> children IDs. */
   protected final Map<String, Set<String>> children;

   /** Map of id -> parent IDs, or null if unfiled. */
   protected final Map<String, Set<String>> parents;

   /** Map of id -> policies IDs. */
   protected final Map<String, Set<String>> policies;

   /** Map of id -> versions. */
   protected final Map<String, Set<String>> versions;

   /** Unfiled objects set. */
   protected final Set<String> unfiling;

   /** Map of id -> content. */
   protected final Map<String, ContentStream> contents;

   /** Map of id -> ACLs. */
   private final Map<String, Map<String, Set<String>>> acls;

   /**
    * Instantiates a new Storage.
    */
   public Storage()
   {
      this.objects = new ConcurrentHashMap<String, Map<String, Object[]>>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      this.policies = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, Set<String>>();
      this.unfiling = new HashSet<String>();
      this.contents = new ConcurrentHashMap<String, ContentStream>();
      this.acls = new ConcurrentHashMap<String, Map<String, Set<String>>>();
   }

   /**
    * Gets the children.
    * 
    * @return the children
    */
   public Map<String, Set<String>> getChildren()
   {
      return children;
   }

   /**
    * Gets the contents.
    * 
    * @return the contents
    */
   public Map<String, ContentStream> getContents()
   {
      return contents;
   }

   /**
    * Gets the objects.
    * 
    * @return the objects
    */
   public Map<String, Map<String, Object[]>> getObjects()
   {
      return objects;
   }

   /**
    * Gets the parents.
    * 
    * @return the parents
    */
   public Map<String, Set<String>> getParents()
   {
      return parents;
   }

   /**
    * Gets the policies.
    * 
    * @return the policies
    */
   public Map<String, Set<String>> getPolicies()
   {
      return policies;
   }

   /**
    * Gets the versions.
    * 
    * @return the versions
    */
   public Map<String, Set<String>> getVersions()
   {
      return versions;
   }

   /**
    * Gets the unfiling.
    * 
    * @return the unfiling
    */
   public Set<String> getUnfiling()
   {
      return unfiling;
   }

   /**
    * Gets the ACLs.
    * 
    * @return the aC ls
    */
   public Map<String, Map<String, Set<String>>> getACLs()
   {
      return acls;
   }

   /**
    * Checks for object.
    * 
    * @param objectId the object id
    * 
    * @return true, if successful
    */
   public boolean hasObject(String objectId)
   {
      if (objectId == null)
         return false;
      return getObjects().get(objectId) != null;
   }

}
