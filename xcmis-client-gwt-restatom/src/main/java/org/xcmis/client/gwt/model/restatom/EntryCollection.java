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

 
package org.xcmis.client.gwt.model.restatom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class EntryCollection
{
   /**
    * Feed information
    */
   private FeedInfo feedInfo;
   
   /**
    * Entries.
    */
   private List<AtomEntry> entries = new ArrayList<AtomEntry>();
   
   /**
    * @return List containing {@link CmisItem}
    */
   public List<AtomEntry> getEntries()
   {
      return entries;
   }
   
   /**
    * @param items items
    */
   public void setEntries(List<AtomEntry> items)
   {
      this.entries = items;
   }

   /**
    * @return the feedInfo
    */
   public FeedInfo getFeedInfo()
   {
      return feedInfo;
   }

   /**
    * @param feedInfo the feedInfo to set
    */
   public void setFeedInfo(FeedInfo feedInfo)
   {
      this.feedInfo = feedInfo;
   }
   
}
