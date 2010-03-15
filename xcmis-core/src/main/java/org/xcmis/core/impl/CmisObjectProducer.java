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

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;

import java.util.Iterator;

/**
 * Represent internal implementation of CMIS object in form that can
 * be used by CMIS services in responses.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisObjectProducer.java 2 2010-02-04 17:21:49Z andrew00x $
 */
abstract class CmisObjectProducer
{

   /** Property service. */
   protected final PropertyService propertyService;

   /** 
    * Create instance of CmisObjectProducerImpl. 
    *
    * @param propertyService the instance of PropertyService to set.
    */
   public CmisObjectProducer(PropertyService propertyService)
   {
      this.propertyService = propertyService;
   }

   /**
    * Get internal representation of CMIS object in form that can be used for in
    * responses of CMIS services.
    * 
    * @param entry the internal representation of CMIS object
    * @param includeAllowableActions if TRUE then include allowable actions for object
    * @param includeRelationships indicates what relationships of object must be returned
    * @param includePolicyIds if TRUE then include the policies IDs applied to the object
    * @param includeACL if TRUE then include the ACLs applied to the object
    * @param propertyFilter the property filter
    * @param renditionFilter the rendition filter
    * @param renditionManager the rendition manager
    * @return CmisObjectType 
    * @throws RepositoryException if any repository error occurs
    */
   public CmisObjectType getCmisObject(Entry entry, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      PropertyFilter propertyFilter, RenditionFilter renditionFilter, RenditionManager renditionManager)
      throws RepositoryException
   {
      CmisObjectType cmis = new CmisObjectType();

      // allowable actions
      if (includeAllowableActions && entry.canGetAppliedPolicies())
         cmis.setAllowableActions(getAllowableActions(entry));

      // object's relationship
      if (entry.canGetRelationships())
      {
         ItemsIterator<Entry> relationships = null;
         if (includeRelationships == EnumIncludeRelationships.BOTH)
            relationships = entry.getRelationships(EnumRelationshipDirection.EITHER, true, null);
         else if (includeRelationships == EnumIncludeRelationships.SOURCE)
            relationships = entry.getRelationships(EnumRelationshipDirection.SOURCE, true, null);
         else if (includeRelationships == EnumIncludeRelationships.TARGET)
            relationships = entry.getRelationships(EnumRelationshipDirection.TARGET, true, null);
         if (relationships != null)
         {
            while (relationships.hasNext())
            {
               CmisObjectType relationship =
                  getCmisObject(relationships.next(), false, EnumIncludeRelationships.NONE, false, false,
                     PropertyFilter.ALL, RenditionFilter.NONE, renditionManager);
               cmis.getRelationship().add(relationship);
            }
         }
      }

      // applied policies
      if (includePolicyIds && entry.canGetAppliedPolicies())
      {
         cmis.setPolicyIds(new CmisListOfIdsType());
         for (Iterator<Entry> iter = entry.getAppliedPolicies().iterator(); iter.hasNext();)
            cmis.getPolicyIds().getId().add(iter.next().getObjectId());
      }

      // ACL
      if (includeACL && entry.canGetACL())
      {
         cmis.setAcl(new CmisAccessControlListType());
         for (Iterator<CmisAccessControlEntryType> iter = entry.getPermissions().iterator(); iter.hasNext();)
            cmis.getAcl().getPermission().add(iter.next());
      }

      // properties
      cmis.setProperties(new CmisPropertiesType());
      for (Iterator<CmisPropertyDefinitionType> iter = entry.getType().getPropertyDefinition().iterator(); iter
         .hasNext();)
      {
         CmisPropertyDefinitionType propertyDefinition = iter.next();
         if (propertyFilter.accept(propertyDefinition.getQueryName()))
         {
            CmisProperty property = propertyService.getProperty(entry, propertyDefinition.getId());
            cmis.getProperties().getProperty().add(property);
         }
      }

      // renditions
      if (renditionManager != null)
      {
         for (ItemsIterator<CmisRenditionType> iter = renditionManager.getRenditions(entry); iter.hasNext();)
         {
            CmisRenditionType item = iter.next();
            if (renditionFilter.accept(item))
               cmis.getRenditions().add(item);
         }
      }
      return cmis;
   }

   /**
    * Get allowable actions for specified object.
    * 
    * @param entry CMISEntry
    * @return allowable actions for <code>entry</code>
    */
   protected CmisAllowableActionsType getAllowableActions(Entry entry)
   {
      CmisAllowableActionsType actions = new CmisAllowableActionsType();
      actions.setCanAddObjectToFolder(entry.canAddToFolder());
      actions.setCanApplyACL(entry.canApplyACL());
      actions.setCanApplyPolicy(entry.canAddPolicy());
      actions.setCanCancelCheckOut(entry.canCancelCheckOut());
      actions.setCanCheckIn(entry.canCheckIn());
      actions.setCanCheckOut(entry.canCheckOut());
      actions.setCanCreateDocument(entry.canCreateDocument());
      actions.setCanCreateFolder(entry.canCreateFolder());
      actions.setCanCreateRelationship(entry.canCreateRelationship());
      actions.setCanDeleteObject(entry.canDelete());
      actions.setCanDeleteContentStream(entry.canDeleteContent());
      actions.setCanDeleteTree(entry.canDeleteTree());
      actions.setCanGetACL(entry.canGetACL());
      actions.setCanGetAllVersions(entry.canGetAllVersions());
      actions.setCanGetAppliedPolicies(entry.canGetAppliedPolicies());
      actions.setCanGetChildren(entry.canGetChildren());
      actions.setCanGetContentStream(entry.canGetContent());
      actions.setCanGetDescendants(entry.canGetDescendants());
      actions.setCanGetFolderParent(entry.canGetFolderParent());
      actions.setCanGetFolderTree(entry.canGetFolderTree());
      actions.setCanGetObjectParents(entry.canGetParents());
      actions.setCanGetObjectRelationships(entry.canGetRelationships());
      actions.setCanGetProperties(entry.canGetProperties());
      actions.setCanGetRenditions(entry.canGetRenditions());
      actions.setCanMoveObject(entry.canMove());
      actions.setCanRemoveObjectFromFolder(entry.canRemoveFromFolder());
      actions.setCanRemovePolicy(entry.canRemovePolicy());
      actions.setCanSetContentStream(entry.canSetContent());
      actions.setCanUpdateProperties(entry.canUpdateProperties());
      return actions;
   }

}
