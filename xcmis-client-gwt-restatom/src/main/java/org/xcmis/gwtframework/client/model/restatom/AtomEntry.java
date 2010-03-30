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

package org.xcmis.gwtframework.client.model.restatom;

import org.xcmis.gwtframework.client.model.CmisObjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class AtomEntry
{
   /**
    * Entry info.
    */
   private EntryInfo entryInfo;

   /**
    * Object.
    */
   private CmisObjectType object;

   /**
    * Children.
    */
   private List<AtomEntry> children;

   /**
    * Accessor for atom entry cmis object.
    * 
    * @return the object
    */
   public CmisObjectType getObject()
   {
      return object;
   }

   /**
    * @return {@link EntryInfo}
    */
   public EntryInfo getEntryInfo()
   {
      return entryInfo;
   }

   /**
    * @param entryInfo entryInfo
    */
   public void setEntryInfo(EntryInfo entryInfo)
   {
      this.entryInfo = entryInfo;
   }

   /**
    * Mutator for atom entry cmis object.
    * 
    * @param object
    *            the object to set
    */
   public void setObject(CmisObjectType object)
   {
      this.object = object;
   }

   /**
    * @return List containing {@link AtomEntry}
    */
   public List<AtomEntry> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<AtomEntry>();
      }
      return children;
   }
}
