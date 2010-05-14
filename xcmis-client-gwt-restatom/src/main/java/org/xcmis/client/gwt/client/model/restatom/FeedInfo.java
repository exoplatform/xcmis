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

package org.xcmis.client.gwt.client.model.restatom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class FeedInfo
{
   /**
    * Title.
    */
   protected String title;

   /**
    * Author.
    */
   protected AtomAuthor author;

   /**
    * Updated.
    */
   protected Date updated;

   /**
    * Published.
    */
   protected Date published;

   /**
    * Id.
    */
   protected String id;

   /**
    * Links.
    */
   protected List<AtomLink> links;

   /**
    * Collection.
    */
   protected AtomCollectionType collection;

   /**
    * @return String
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title title
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return {@link AtomAuthor}
    */
   public AtomAuthor getAuthor()
   {
      return author;
   }

   /**
    * @param author author
    */
   public void setAuthor(AtomAuthor author)
   {
      this.author = author;
   }

   /**
    * @return {@link Date}
    */
   public Date getUpdated()
   {
      return updated;
   }

   /**
    * @param updated updated
    */
   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   /**
    * @return {@link Date}
    */
   public Date getPublished()
   {
      return published;
   }

   /**
    * @param published published
    */
   public void setPublished(Date published)
   {
      this.published = published;
   }

   /**
    * @return String
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id id
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return List containing {@link AtomLink}
    */
   public List<AtomLink> getLinks()
   {
      if (links == null)
      {
         links = new ArrayList<AtomLink>();
      }
      return links;
   }

   /**
    * @param links links
    */
   public void setLinks(List<AtomLink> links)
   {
      this.links = links;
   }

   /**
    * @return {@link AtomCollectionType}
    */
   public AtomCollectionType getCollection()
   {
      return collection;
   }

   /**
    * @param collection collection
    */
   public void setCollection(AtomCollectionType collection)
   {
      this.collection = collection;
   }
}
