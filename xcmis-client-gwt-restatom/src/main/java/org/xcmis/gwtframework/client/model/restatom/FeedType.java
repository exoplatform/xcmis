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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class FeedType
{
   /**
    * Feed info.
    */
   protected FeedInfo feedInfo;

   /**
    * Entries.
    */
   protected List<AtomEntry> entries;

   /**
    * @return {@link FeedInfo}
    */
   public FeedInfo getFeedInfo()
   {
      return feedInfo;
   }

   /**
    * @param feedInfo feedInfo
    */
   public void setFeedInfo(FeedInfo feedInfo)
   {
      this.feedInfo = feedInfo;
   }

   /**
    * @return the entries
    */
   public List<AtomEntry> getEntries()
   {
      if (entries == null)
      {
         entries = new ArrayList<AtomEntry>();
      }
      return entries;
   }
   
   /**
    * @param entries entries
    */
   public void setEntries(List<AtomEntry> entries)
   {
      this.entries = entries;
   }

}
