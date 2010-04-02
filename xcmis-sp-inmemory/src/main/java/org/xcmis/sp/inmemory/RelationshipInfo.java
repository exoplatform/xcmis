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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
final class RelationshipInfo
{

   public static final int SOURCE = 0;

   public static final int TARGET = 1;

   private final String relationshipId;

   private final int direction;

   public RelationshipInfo(String relationshipId, int direction)
   {
      if (relationshipId == null)
      {
         throw new IllegalArgumentException("Relationship id may not be null.");
      }
      if (direction != 0 && direction != 1)
      {
         throw new IllegalArgumentException("Unknown direction attribute.");
      }
      this.relationshipId = relationshipId;
      this.direction = direction;
   }

   public String getRelationshipId()
   {
      return relationshipId;
   }

   public int getDirection()
   {
      return direction;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      RelationshipInfo other = (RelationshipInfo)obj;
      return this.relationshipId.equals(other.relationshipId) && this.direction == other.direction;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash += direction;
      hash = hash * 31 + relationshipId.hashCode();
      return hash;
   }

}
