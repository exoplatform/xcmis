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

import org.xcmis.core.CmisAccessControlEntryType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.version.Version;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: EntryVersion.java 61 2010-02-09 11:24:36Z andrew00x $
 */
public class EntryVersion extends EntryImpl
{

   /**
    * Instantiates a new entry version.
    * 
    * @param version the version
    * @throws InvalidArgumentException the invalid argument exception
    * @throws javax.jcr.RepositoryException the repository exception
    */
   public EntryVersion(Version version) throws InvalidArgumentException, javax.jcr.RepositoryException
   {
      super(version);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<CmisAccessControlEntryType> addPermissions(List<CmisAccessControlEntryType> add)
      throws ConstraintException, RepositoryException
   {
      throw new ConstraintException("Operation is not permited for non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void applyPolicy(Entry policy) throws RepositoryException
   {
      throw new ConstraintException("Operation is not permited for non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canAddPolicy()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canApplyACL()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canCheckIn()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canCheckOut()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canDelete()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canDeleteContent()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canMove()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canRemovePolicy()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canSetContent()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canUpdateProperties()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void delete() throws RepositoryException
   {
      throw new ConstraintException("Operation is not permited for non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() throws RepositoryException
   {
      return getVersionSeries().getLatestVersion().getName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Entry> getParents() throws RepositoryException
   {
      return Collections.singletonList(getParent((EntryImpl)getVersionSeries().getLatestVersion()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getString(String name) throws RepositoryException
   {
      // TODO : How to use common properties/attributes/etc to avoid getting latest version.
      if (CMIS.VERSION_SERIES_CHECKED_OUT_ID.equals(name))
      {
         return getVersionSeries().getLatestVersion().getCheckedOutId();
      }
      else if ((CMIS.VERSION_SERIES_CHECKED_OUT_BY.equals(name)))
      {
         return getVersionSeries().getLatestVersion().getCheckedOutBy();
      }
      else if ((CMIS.VERSION_LABEL.equals(name)))
      {
         try
         {
            return node.getParent().getName();
         }
         catch (javax.jcr.RepositoryException re)
         {
            String msg = "Unable get version label. " + re.getMessage();
            throw new RepositoryException(msg);
         }
      }

      return super.getString(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isLatest() throws RepositoryException
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isLatestMajor() throws RepositoryException
   {
      return isMajor() && this.equals(getVersionSeries().getLatestMajorVersion());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isVersionable() throws RepositoryException
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<CmisAccessControlEntryType> removePermissions(List<CmisAccessControlEntryType> remove)
      throws ConstraintException, RepositoryException
   {
      throw new ConstraintException("Operation is not permited for non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removePolicy(Entry policy) throws RepositoryException
   {
      throw new ConstraintException("Operation is not permited for non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   @Override
   public Entry setBoolean(String name, boolean value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setBooleans(String name, boolean[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setContent(ContentStream content) throws IOException, StreamNotSupportedException, RepositoryException
   {
      throw new VersioningException("Can't update content stream of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setDate(String name, Calendar value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setDates(String name, Calendar[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setDecimal(String name, BigDecimal value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setDecimals(String name, BigDecimal[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setHTML(String name, String value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setHTMLs(String name, String[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setInteger(String name, BigInteger value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setIntegers(String name, BigInteger[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setName(String name) throws RepositoryException
   {
      throw new VersioningException("Can't update non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setString(String name, String value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setStrings(String name, String[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersionLabel() throws RepositoryException
   {
      try
      {
         return node.getParent().getName();
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get version label. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setURI(String name, URI value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Entry setURIs(String name, URI[] value) throws RepositoryException
   {
      throw new VersioningException("Can't update property of non-latest version of object.");
   }

   @Override
   public VersionSeries getVersionSeries() throws RepositoryException
   {
      try
      {
         return new VersionSeriesImpl(((Version)node.getParent()).getContainingHistory());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get version series.";
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void init(Node node) throws javax.jcr.RepositoryException
   {
      Node frozen = node.getNode(JcrCMIS.JCR_FROZEN_NODE);
      super.init(frozen, //
         getTypeDefinition(getNodeType(frozen.getProperty(JcrCMIS.JCR_FROZEN_PRIMARY_TYPE).getString()), true));
      this.id = ((ExtendedNode)node).getIdentifier(); // ??
   }

}
