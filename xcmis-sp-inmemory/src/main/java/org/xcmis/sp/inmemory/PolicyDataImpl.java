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

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: PolicyDataImpl.java 1197 2010-05-28 08:15:37Z
 *          alexey.zavizionov@gmail.com $
 */
class PolicyDataImpl extends BaseObjectData implements PolicyData
{

   public PolicyDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // no content or renditions for policy
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FolderData getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<FolderData> getParents()
   {
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public String getPolicyText()
   {
      return getString(CmisConstants.POLICY_TEXT);
   }

   /**
    * {@inheritDoc}
    */
   protected void delete() throws UpdateConflictException, VersioningException, StorageException
   {
      String objectId = getObjectId();

      for (Iterator<Entry> iterator = storage.entries.values().iterator(); iterator.hasNext();)
      {
         Entry next = iterator.next();
         if (next.getPolicies().contains(objectId))
         {
            throw new StorageException("Unable delete applied policy");
         }
      }

      ItemsIterator<RelationshipData> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
      if (relationships.hasNext())
      {
         throw new StorageException("Object can't be deleted cause to storage referential integrity. "
            + "Object is source or target at least one Relationship.");
      }

      storage.parents.remove(objectId);
   }
}
