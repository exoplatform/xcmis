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

package org.xcmis.core.impl;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAction;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VersioningServiceImpl.java 2118 2009-07-13 20:40:48Z andrew00x
 *          $
 */
public class VersioningServiceImpl extends CmisObjectProducer implements VersioningService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(VersioningServiceImpl.class.getName());

   /** @see RepositoryService. */
   protected final RepositoryService repositoryService;

   /**
    * Create instance of <code>VersioningServiceImpl</code>.
    * 
    * @param repositoryService the repository service
    * @param propertyService the property service
    */
   public VersioningServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout(String repositoryId, String documentId) throws UpdateConflictException,
      VersioningException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In cancelCheckOut repository " + repositoryId + ", documentId " + documentId);
      Repository repository = repositoryService.getRepository(repositoryId);
      repository.getVersionSeries(repository.getObjectById(documentId).getVersionSeriesId()).cancelCheckout();
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType checkin(String repositoryId, String documentId, boolean major, CmisPropertiesType properties,
      ContentStream content, String checkinComment, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies) throws UpdateConflictException, VersioningException,
      StreamNotSupportedException, IOException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In checkIn repository " + repositoryId + ", documentId " + documentId);
      Repository repository = repositoryService.getRepository(repositoryId);

      VersionSeries versionSeries =
         repository.getVersionSeries(repository.getObjectById(documentId).getVersionSeriesId());
      Entry pwc = versionSeries.getCheckedOut();
      if (pwc == null)
      {
         String msg = "There is no private working copy in version series.";
         throw new VersioningException(msg);
      }
      if (properties != null)
      {
         for (CmisProperty prop : properties.getProperty())
            propertyService.setProperty(pwc, prop, CmisAction.UPDATE_PWC_PROPERTIES);
      }
      if (content != null)
         pwc.setContent(content);
      if (policies != null)
      {
         for (String policyId : policies)
            pwc.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         pwc.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         pwc.addPermissions(addACL.getPermission());
      pwc.save();
      Entry newDoc = versionSeries.checkin(major, checkinComment);
      return getCmisObject(newDoc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType checkout(String repositoryId, String documentId) throws RepositoryException,
      UpdateConflictException, VersioningException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In checkOut repository " + repositoryId + ", documentId " + documentId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry checkedOut =
         repository.getVersionSeries(repository.getObjectById(documentId).getVersionSeriesId()).checkout(documentId);
      return getCmisObject(checkedOut, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectType> getAllVersions(String repositoryId, String versionSeriesId,
      boolean includeAllowableActions, String propertyFilter) throws RepositoryException, FilterNotValidException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In getAllVersions repository " + repositoryId + ", versionSeriesId " + versionSeriesId);
      Repository repository = repositoryService.getRepository(repositoryId);
      VersionSeries versionSeries = repository.getVersionSeries(versionSeriesId);
      List<Entry> entries = versionSeries.getAllVersions();
      List<CmisObjectType> list = new ArrayList<CmisObjectType>(entries.size());
      for (Entry entry : entries)
         list.add(getCmisObject(entry, includeAllowableActions, EnumIncludeRelationships.NONE, false, false,
            new PropertyFilter(propertyFilter), RenditionFilter.NONE, repository.getRenditionManager()));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObjectOfLatestVersion(String repositoryId, String versionSeriesId, boolean major,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePolicyIds,
      boolean includeACL, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In getObjectOfLatestVersion repository " + repositoryId + ", versionSeriesId " + versionSeriesId);
      Repository repository = repositoryService.getRepository(repositoryId);
      VersionSeries versionSeries = repository.getVersionSeries(versionSeriesId);
      Entry entry = major ? entry = versionSeries.getLatestMajorVersion() : versionSeries.getLatestVersion();
      // May be null if there is no major version or version series has just
      // one document in checked-out state.
      if (entry == null)
         throw new ObjectNotFoundException();
      return getCmisObject(entry, includeAllowableActions, includeRelationships, includePolicyIds, includeACL,
         new PropertyFilter(propertyFilter), new RenditionFilter(renditionFilter), repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getPropertiesOfLatestVersion(String repositoryId, String versionSeriesId, boolean major,
      String propertyFilter) throws FilterNotValidException, ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In getPropertiesOfLatestVersion repository " + repositoryId + ", versionSeriesId "
            + versionSeriesId);
      return getObjectOfLatestVersion(repositoryId, versionSeriesId, major, false, EnumIncludeRelationships.NONE,
         false, false, propertyFilter, null).getProperties();
   }

}
