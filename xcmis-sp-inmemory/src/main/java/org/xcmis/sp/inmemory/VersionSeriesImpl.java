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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionSeriesImpl implements VersionSeries
{

   /** The id. */
   private final String id;

   /** The storage. */
   private Storage storage;

   /** The Constant c - comaprator. */
   public static final java.util.Comparator<Entry> c = new java.util.Comparator<Entry>()
   {

      public int compare(Entry entry1, Entry entry2)
      {
         try
         {
            Calendar c1 = entry1.getDate(CMIS.LAST_MODIFICATION_DATE);
            Calendar c2 = entry2.getDate(CMIS.LAST_MODIFICATION_DATE);
            return c2.compareTo(c1);
         }
         catch (InvalidArgumentException e1)
         {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         catch (RepositoryException e1)
         {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         return 0;
      }

   };

   /**
    * Instantiates a new version series impl.
    * 
    * @param id the id
    * @param storage the storage
    */
   public VersionSeriesImpl(String id, Storage storage)
   {
      this.id = id;
      this.storage = storage;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout() throws ConstraintException/*, UpdateConflictException*/, RepositoryException
   {
      Entry checkedout = getCheckedOut();
      if (checkedout != null)
      {
         for (Entry e : getAllVersions())
         {
            e.setString(CMIS.VERSION_SERIES_CHECKED_OUT_ID, null);
            e.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry checkin(boolean major, String checkinComment) throws ConstraintException, VersioningException,
      RepositoryException
   {
      Entry checkedout = getCheckedOut();
      if (checkedout == null)
         throw new VersioningException("No checkedout object in version series.");
      for (Entry e : getAllVersions())
      {
         e.setString(CMIS.VERSION_SERIES_CHECKED_OUT_ID, null);
         e.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
      }
      checkedout.setBoolean(CMIS.IS_MAJOR_VERSION, true);
      checkedout.setString(CMIS.CHECKIN_COMMENT, checkinComment);
      return checkedout;
   }

   /**
    * {@inheritDoc}
    */
   public Entry checkout(String documentId) throws ConstraintException, VersioningException,
      RepositoryException
   {
      if (getLatestVersion().getCheckedOutId() != null)
         throw new VersioningException("One document in version series already checkedout.");

      if (storage.getObjects().get(documentId) == null)
         throw new ObjectNotFoundException("Object " + documentId + " does not exists.");
      Entry pwc = new EntryImpl(documentId, storage).copy();
      
      for (String pID : storage.getParents().get(documentId))
         storage.getChildren().get(pID).add(pwc.getObjectId());

      Set<String> copyParents = new CopyOnWriteArraySet<String>();
      copyParents.addAll(storage.getParents().get(documentId));
      storage.getParents().put(pwc.getObjectId(), copyParents);

      storage.getVersions().get(id).add(pwc.getObjectId());

      for (Entry e : getAllVersions())
      {
         e.setString(CMIS.VERSION_SERIES_CHECKED_OUT_ID, pwc.getObjectId());
         e.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
      }
      return pwc;
   }

   /**
    * {@inheritDoc}
    */
   public void delete() throws RepositoryException
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   public void deleteVersion(String versionId) throws RepositoryException
   {
      storage.getObjects().remove(versionId);
      storage.getVersions().remove(versionId);
      storage.getContents().remove(versionId);
      storage.getParents().remove(versionId);
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getAllVersions() throws RepositoryException
   {
      List<Entry> l = new ArrayList<Entry>();
      for (String verId : storage.getVersions().get(id))
         l.add(new EntryImpl(verId, storage));
      Collections.sort(l, c);
      return l;
   }

   /**
    * {@inheritDoc}
    */
   public Entry getCheckedOut() throws RepositoryException
   {
      Entry latest = getLatestVersion();
      if (latest.getCheckedOutId() == null)
         return null;
      return new EntryImpl(latest.getCheckedOutId(), storage);
   }

   /**
    * {@inheritDoc}
    */
   public Entry getLatestMajorVersion() throws RepositoryException
   {
      for (Entry e : getAllVersions())
      {
         if (!e.getObjectId().equals(e.getCheckedOutId()) && e.isMajor())
            return e;
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Entry getLatestVersion() throws RepositoryException
   {
      for (Entry e : getAllVersions())
      {
         if (!e.getObjectId().equals(e.getCheckedOutId()))
            return e;
      }
      return getAllVersions().get(0);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return id;
   }

}
