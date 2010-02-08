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
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionSeriesImpl implements VersionSeries
{

   /** The version series id. */
   private final String id;

   private final Session session;

   private final VersionHistory jcrVersionHistory;

   private static Set<String> skipedProperties = new HashSet<String>();
   static
   {
      skipedProperties.add(CMIS.NAME);
      skipedProperties.add(CMIS.OBJECT_ID);
      skipedProperties.add(CMIS.OBJECT_TYPE_ID);
      skipedProperties.add(CMIS.BASE_TYPE_ID);
      skipedProperties.add(CMIS.CREATED_BY);
      skipedProperties.add(CMIS.CREATION_DATE);
      skipedProperties.add(CMIS.LAST_MODIFIED_BY);
      skipedProperties.add(CMIS.LAST_MODIFICATION_DATE);
      skipedProperties.add(CMIS.CHANGE_TOKEN);
      skipedProperties.add(CMIS.IS_IMMUTABLE);
      skipedProperties.add(CMIS.IS_LATEST_VERSION);
      skipedProperties.add(CMIS.IS_MAJOR_VERSION);
      skipedProperties.add(CMIS.IS_LATEST_MAJOR_VERSION);
      skipedProperties.add(CMIS.VERSION_LABEL);
      skipedProperties.add(CMIS.VERSION_SERIES_ID);
      skipedProperties.add(CMIS.IS_VERSION_SERIES_CHECKED_OUT);
      skipedProperties.add(CMIS.VERSION_SERIES_CHECKED_OUT_BY);
      skipedProperties.add(CMIS.VERSION_SERIES_CHECKED_OUT_ID);
      skipedProperties.add(CMIS.CHECKIN_COMMENT);
      skipedProperties.add(CMIS.CONTENT_STREAM_FILE_NAME);
      skipedProperties.add(CMIS.CONTENT_STREAM_ID);
      skipedProperties.add(JcrCMIS.CMIS_LATEST_VERSION);
   }

   /**
    * Instantiates a new version series impl.
    * 
    * @param vh JCR version history
    * @throws javax.jcr.RepositoryException the repository exception
    */
   public VersionSeriesImpl(VersionHistory vh) throws javax.jcr.RepositoryException
   {
      this.session = vh.getSession();
      this.jcrVersionHistory = vh;
      this.id = ((ExtendedNode)vh).getIdentifier();
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout() throws ConstraintException, RepositoryException
   {
      // Document MAY be created in checked-out state and PWC is exactly
      // one document in this Version Series.
      Entry pwc = getCheckedOut();
      if (pwc != null)
         pwc.delete();
   }

   /**
    * {@inheritDoc}
    */
   public Entry checkin(boolean major, String checkinComment) throws ConstraintException, VersioningException,
      RepositoryException
   {
      try
      {
         Entry pwc = getCheckedOut();
         if (pwc == null)
         {
            String msg = "There is no private working copy in version series.";
            throw new VersioningException(msg);
         }
         EntryImpl latest = (EntryImpl)getLatestVersion();
         // Document MAY be created in checked-out state. In this case PWC is
         // exactly one version in version series.
         if (!pwc.equals(latest))
         {
            // Save current state in version history.
            latest.getNode().checkin();
            latest.getNode().checkout();
            updateFromPwc();
            pwc.delete();
         }
         else
         {
            // PWC is just one version of document.
            // Do not remove it just turn off PWC state.
            latest.getNode().removeMixin("cmis:pwc");
            latest.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, false);
            latest.setString(CMIS.VERSION_SERIES_CHECKED_OUT_ID, null);
            latest.setString(CMIS.VERSION_SERIES_CHECKED_OUT_BY, null);
         }
         latest.setBoolean(CMIS.IS_MAJOR_VERSION, major);
         latest.setString(CMIS.CHECKIN_COMMENT, checkinComment);
         latest.save();
         return latest;
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
   public Entry checkout(String documentId) throws ConstraintException, VersioningException, RepositoryException
   {
      // DocumentId is not in use. Only Latest version may be checked-out.
      try
      {
         EntryImpl pwc = (EntryImpl)getCheckedOut();
         if (pwc != null)
         {
            String msg = "One object in version series already checked-out.";
            throw new VersioningException(msg);
         }

         Node pwcNode = createPwc();
         pwcNode.addMixin("cmis:pwc");
         EntryImpl latest = (EntryImpl)getLatestVersion();
         pwcNode.setProperty(JcrCMIS.CMIS_LATEST_VERSION, latest.getNode());
         pwc = new EntryImpl(pwcNode);
         // Update latest version's properties
         latest.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
         latest.setString(CMIS.VERSION_SERIES_CHECKED_OUT_ID, pwc.getObjectId());
         latest.setString(CMIS.VERSION_SERIES_CHECKED_OUT_BY, pwc.getCheckedOutBy());

         session.save();

         return pwc;
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
         Node pwcNode = getPwcNode();
         if (pwcNode != null)
            return new EntryImpl(pwcNode);
         Node latestNode = getLatestVersionNode();
         if (latestNode.isNodeType("cmis:pwc"))
            // Document created in checked-out state.
            return new EntryImpl(latestNode);
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
         return new EntryImpl(getLatestVersionNode());
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
      String destPath = getPwcLocation();
      session.getWorkspace().copy(getLatestVersionNode().getPath(), destPath);
      Node pwc = (Node)session.getItem(destPath);
      // Set required CMIS properties.
      pwc.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
      // PWC must not be considered as 'latest major version'.
      pwc.setProperty(CMIS.IS_LATEST_VERSION, false);
      pwc.setProperty(CMIS.IS_MAJOR_VERSION, false);
      pwc.setProperty(CMIS.VERSION_LABEL, EntryImpl.pwcLabel);
      pwc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)pwc).getIdentifier());
      pwc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, session.getUserID());
      return pwc;
   }

   private Node getLatestVersionNode() throws javax.jcr.RepositoryException
   {
      return ((ExtendedSession)session).getNodeByIdentifier(jcrVersionHistory.getVersionableUUID());
   }

   /**
    * Get Private Working Copy (PWC) for this Version Series.
    * 
    * @return PWC or <code>null</code> if there is not PWC for this version series.
    * @throws javax.jcr.RepositoryException
    */
   private Node getPwcNode() throws javax.jcr.RepositoryException
   {
      String pwcLocation = getPwcLocation();
      if (session.itemExists(pwcLocation))
         return (Node)session.getItem(pwcLocation);
      return null;
   }

   private String getPwcLocation() throws javax.jcr.RepositoryException
   {
      return new StringBuilder().append('/').append(JcrCMIS.CMIS_SYSTEM).append('/')
         .append(JcrCMIS.CMIS_WORKING_COPIES).append('/').append(id).toString();
   }

   private void updateFromPwc() throws javax.jcr.RepositoryException
   {
      Node pwc = getPwcNode();
      Node latest = getLatestVersionNode();
      // Update properties from PWC
      for (PropertyIterator iter = pwc.getProperties(); iter.hasNext();)
      {
         Property prop = iter.nextProperty();
         if (prop.getDefinition().isProtected())
            continue;
         String name = prop.getName();
         if (skipedProperties.contains(name))
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
    * Gets the versions list.
    * 
    * @return the all document versions
    * @throws javax.jcr.RepositoryException the repository exception
    */
   private List<Entry> versions() throws javax.jcr.RepositoryException
   {
      LinkedList<Entry> list = new LinkedList<Entry>();
      VersionIterator iter = jcrVersionHistory.getAllVersions();
      iter.nextVersion(); // skip jcr:rootVersion
      while (iter.hasNext())
         list.addFirst(new EntryVersion(iter.nextVersion()));
      list.addFirst(new EntryImpl(getLatestVersionNode()));
      Node pwc = getPwcNode();
      if (pwc != null)
         list.addFirst(new EntryImpl(pwc));
      return list;
   }

}
