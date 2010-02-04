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

package org.xcmis.sp.jcr.exo.object;

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionSeriesImpl implements VersionSeries
{

   //   /** Logger. */
   //   private static final Log LOG = ExoLogger.getLogger(VersionSeriesImpl.class);

   /** The id. */
   private final String id;

   private final Session session;

   private final Node latest;

   private static final Set<String> IGNORED_PROPERTIES = new HashSet<String>();
   static
   {
      IGNORED_PROPERTIES.add(CMIS.NAME);
      IGNORED_PROPERTIES.add(CMIS.OBJECT_ID);
      IGNORED_PROPERTIES.add(CMIS.OBJECT_TYPE_ID);
      IGNORED_PROPERTIES.add(CMIS.BASE_TYPE_ID);
      IGNORED_PROPERTIES.add(CMIS.CREATED_BY);
      IGNORED_PROPERTIES.add(CMIS.CREATION_DATE);
      IGNORED_PROPERTIES.add(CMIS.LAST_MODIFIED_BY);
      IGNORED_PROPERTIES.add(CMIS.LAST_MODIFICATION_DATE);
      IGNORED_PROPERTIES.add(CMIS.CHANGE_TOKEN);
      IGNORED_PROPERTIES.add(CMIS.IS_IMMUTABLE);
      IGNORED_PROPERTIES.add(CMIS.IS_LATEST_VERSION);
      IGNORED_PROPERTIES.add(CMIS.IS_MAJOR_VERSION);
      IGNORED_PROPERTIES.add(CMIS.IS_LATEST_MAJOR_VERSION);
      IGNORED_PROPERTIES.add(CMIS.VERSION_LABEL);
      IGNORED_PROPERTIES.add(CMIS.VERSION_SERIES_ID);
      IGNORED_PROPERTIES.add(CMIS.IS_VERSION_SERIES_CHECKED_OUT);
      IGNORED_PROPERTIES.add(CMIS.VERSION_SERIES_CHECKED_OUT_BY);
      IGNORED_PROPERTIES.add(CMIS.VERSION_SERIES_CHECKED_OUT_ID);
      IGNORED_PROPERTIES.add(CMIS.CHECKIN_COMMENT);
      IGNORED_PROPERTIES.add(CMIS.CONTENT_STREAM_FILE_NAME);
      IGNORED_PROPERTIES.add(CMIS.CONTENT_STREAM_ID);
   }

   /**
    * Instantiates a new version series impl.
    * 
    * @param document the document
    * @throws javax.jcr.RepositoryException the repository exception
    */
   public VersionSeriesImpl(Node document) throws javax.jcr.RepositoryException
   {
      session = document.getSession();
      if (document.isNodeType(JcrCMIS.NT_VERSION))
         latest =
            ((ExtendedSession)session).getNodeByIdentifier(((Version)document).getContainingHistory()
               .getVersionableUUID());
      else if (document.isNodeType(JcrCMIS.CMIS_VERSIONABLE))
         latest = document.getProperty(JcrCMIS.CMIS_LATEST_VERSION).getNode();
      else
         this.latest = document;
      // VersionSeries id is latest versions.
      id = ((ExtendedNode)latest).getIdentifier();
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout() throws ConstraintException, RepositoryException
   {
      try
      {
         Entry checkedOut = getCheckedOut();
         if (checkedOut != null)
         {
            Node pwc = ((EntryImpl)checkedOut).getNode();
            if (pwc.equals(latest))
            {
               // Document was created in checked-out state.
               // Simple remove it, don't need update any properties.
               pwc.remove();
            }
            else
            {
               pwc.remove();
               // Update latest version.
               latest.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
               latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, (String)null);
               latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, (String)null);
            }
            session.save();
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable cancel checkout. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry checkin(boolean major, String checkinComment) throws ConstraintException, VersioningException,
      RepositoryException
   {
      try
      {
         EntryImpl checkedOut = (EntryImpl)getCheckedOut();
         if (checkedOut == null)
         {
            String msg = "There is no private working copy in version series.";
            throw new VersioningException(msg);
         }
         if (checkedOut.getNode().equals(latest))
         {
            // If document was created in checked-out state we have not
            // PWC in fact. Just reset checked-out state property. 
            latest.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
            latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, (String)null);
            latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, (String)null);
            latest.setProperty(CMIS.IS_MAJOR_VERSION, major);
            latest.setProperty(CMIS.CHECKIN_COMMENT, checkinComment);
         }
         else
         {
            latest.checkin();
            latest.checkout();

            updateCurrent();

            latest.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
            latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, (String)null);
            latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, (String)null);
            latest.setProperty(CMIS.IS_MAJOR_VERSION, major);
            latest.setProperty(CMIS.CHECKIN_COMMENT, checkinComment);

            checkedOut.getNode().remove();
         }
         session.save();
         return new EntryImpl(latest);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Checkin document failed. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry checkout(String documentId) throws ConstraintException, VersioningException,
      RepositoryException
   {
      try
      {
         Node pwc = getPwc();
         if (pwc != null)
         {
            String msg = "One object in version series already checked-out.";
            throw new VersioningException(msg);
         }

         if (!latest.isNodeType(JcrCMIS.CMIS_VERSIONABLE))
         {
            latest.addMixin(JcrCMIS.CMIS_VERSIONABLE);
            latest.setProperty(JcrCMIS.CMIS_LATEST_VERSION, latest);
            latest.setProperty(JcrCMIS.VERSION_SERIES_ID, id);
            latest.setProperty(JcrCMIS.IS_LATEST_VERSION, true);
            latest.setProperty(JcrCMIS.VERSION_LABEL, EntryImpl.latestLabel);
            session.save();
         }

         pwc = createPwc();
         String pwcId = ((ExtendedNode)pwc).getIdentifier();

         pwc.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
         // PWC must not be considered as 'latest major version'.
         pwc.setProperty(CMIS.IS_LATEST_VERSION, false);
         pwc.setProperty(CMIS.IS_MAJOR_VERSION, false);
         pwc.setProperty(CMIS.VERSION_LABEL, EntryImpl.pwcLabel);
         pwc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, pwcId);
         // Update latest version's properties
         latest.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
         latest.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, pwcId);

         session.save();

         return new EntryImpl(pwc);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Checkout document failed. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void delete() throws RepositoryException
   {
      throw new UnsupportedOperationException("delete");
   }

   /**
    * {@inheritDoc}
    */
   public void deleteVersion(String objectId) throws RepositoryException
   {
      throw new UnsupportedOperationException("deleteVersion");
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getAllVersions() throws RepositoryException
   {
      try
      {
         return versions();
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get document versions. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry getCheckedOut() throws RepositoryException
   {
      try
      {
         Node pwcNode = getPwc();
         if (pwcNode != null)
            return new EntryImpl(pwcNode);
         if (latest.hasProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID)
            && latest.getProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID).getString().equals(
               ((ExtendedNode)latest).getIdentifier()))
         {
            // Document created in checked-out state.
            // TODO : Need better solution for this.
            return new EntryImpl(latest);
         }
         return null;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get PWC of document. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry getLatestMajorVersion() throws RepositoryException
   {
      Entry latest = getLatestVersion();
      if (latest == null)
         // No latest version minds version series contains exactly one document that
         // was created in checked-out state. This document may not be major version.
         return null;

      if (latest.isMajor())
         return latest;
      try
      {
         List<Entry> versions = versions();
         for (Entry entry : versions)
         {
            if (entry.isMajor())
               return entry;
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get latest major version of document. " + re.getMessage();
         throw new RepositoryException(msg);
      }
      // no major version
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Entry getLatestVersion() throws RepositoryException
   {
      try
      {
         return new EntryImpl(latest);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get latest version of document. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return id;
   }
   
   
   private Node createPwc() throws javax.jcr.RepositoryException
   {
      String destPath = "/cmis:workingCopies/" + id;
      session.getWorkspace().copy(latest.getPath(), destPath);
      Node pwc = (Node)session.getItem(destPath);
      return pwc;
   }

   /**
    * {@inheritDoc}
    */
   private Node getPwc() throws javax.jcr.RepositoryException
   {
      String pwcLocation = "/cmis:workingCopies/" + id;
      if (session.itemExists(pwcLocation))
         return (Node)session.getItem(pwcLocation);
      return null;
   }

   /**
    * Update current version.
    * 
    * @throws javax.jcr.RepositoryException the repository exception
    */
   private void updateCurrent() throws javax.jcr.RepositoryException
   {
      Node pwc = getPwc();
      // Update properties from PWC
      for (PropertyIterator iter = pwc.getProperties(); iter.hasNext();)
      {
         Property prop = iter.nextProperty();
         if (prop.getDefinition().isProtected())
            continue;
         String name = prop.getName();
         if (IGNORED_PROPERTIES.contains(name))
            continue;
         if (prop.getDefinition().isMultiple())
            latest.setProperty(name, prop.getValues());
         else
            latest.setProperty(name, prop.getValue());
      }
      // Update content.
      Node currentContent = latest.getNode(JcrCMIS.JCR_CONTENT);
      for (PropertyIterator iter = pwc.getNode(JcrCMIS.JCR_CONTENT).getProperties(); iter.hasNext();)
      {
         Property prop = iter.nextProperty();
         if (prop.getDefinition().isProtected())
            continue;
         if (prop.getDefinition().isMultiple())
            currentContent.setProperty(prop.getName(), prop.getValues());
         else
            currentContent.setProperty(prop.getName(), prop.getValue());
      }
   }

   /**
    * Gets the lersions list.
    * 
    * @return the list< entry>
    * 
    * @throws javax.jcr.RepositoryException the repository exception
    */
   private List<Entry> versions() throws javax.jcr.RepositoryException
   {
      LinkedList<Entry> list = new LinkedList<Entry>();
      if (latest.isNodeType(JcrCMIS.MIX_VERSIONABLE))
      {
         VersionIterator iter = latest.getVersionHistory().getAllVersions();
         iter.nextVersion(); // skip jcr:rootVersion
         while (iter.hasNext())
            list.addFirst(new EntryVersion(iter.nextVersion()));
      }
      list.addFirst(new EntryImpl(latest));
      Node pwc = getPwc();
      if (pwc != null)
         list.addFirst(new EntryImpl(pwc));
      return list;
   }

}
