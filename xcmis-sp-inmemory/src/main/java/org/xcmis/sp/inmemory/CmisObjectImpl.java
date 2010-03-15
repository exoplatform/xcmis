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

import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.object.ContentStream;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CmisObjectImpl implements CmisObject
{

   class UpdatesImpl implements Updates
   {

      private Set<String> addPolicies;

      private Set<String> removePolicies;

      private Map<String, Set<String>> addPermissions;

      private Map<String, Set<String>> removePermissions;

      private Map<String, CmisProperty> updateProperties;

      private CmisTypeDefinitionType type;

      private boolean isNew;

      private boolean hasContent;

      public UpdatesImpl(CmisTypeDefinitionType type, boolean isNew, boolean hasContent)
      {
         this.type = type;
         this.isNew = isNew;
         this.hasContent = hasContent;
      }

      public void addPermissions(String principal, Set<String> permissions)
      {
         if (!type.isControllableACL())
            throw new ConstraintException("Type " + type.getId() + " is not controllable by ACL.");

         if (principal == null)
            throw new NullPointerException("Principal ID may not be null.");

         if (permissions == null || permissions.size() == 0)
            return;

         if (addPermissions == null)
            addPermissions = new HashMap<String, Set<String>>();

         Set<String> v = addPermissions.get(principal);
         if (v == null)
         {
            v = new HashSet<String>();
            addPermissions.put(principal, v);
         }

         v.addAll(permissions);
      }

      public void addPolicies(Set<String> policies)
      {
         if (!type.isControllablePolicy())
            throw new ConstraintException("Type " + type.getId() + " is not controllable by Policy.");

         if (policies == null || policies.size() == 0)
            return;

         if (addPolicies == null)
            addPolicies = new HashSet<String>();

         addPolicies.addAll(policies);
      }

      public Map<String, Set<String>> getAddedPermissions()
      {
         return addPermissions;
      }

      public Set<String> getAddedPolicies()
      {
         return addPolicies;
      }

      public Map<String, Set<String>> getRemovedPermissions()
      {
         return removePermissions;
      }

      public Set<String> getRemovedPolicies()
      {
         return removePolicies;
      }

      public CmisPropertiesType getUpdatedProperties()
      {
         if (updateProperties == null || updateProperties.size() == 0)
            return null;
         CmisPropertiesType properties = new CmisPropertiesType();
         properties.getProperty().addAll(updateProperties.values());
         return properties;
      }

      public void removePermissions(String principal, Set<String> permissions)
      {
         if (!type.isControllableACL())
            throw new ConstraintException("Type " + type.getId() + " is not controllable by ACL.");

         if (principal == null)
            throw new NullPointerException("Principal ID may not be null.");

         if (permissions == null || permissions.size() == 0)
            return;

         if (removePermissions == null)
            removePermissions = new HashMap<String, Set<String>>();

         Set<String> v = removePermissions.get(principal);
         if (v == null)
         {
            v = new HashSet<String>();
            removePermissions.put(principal, v);
         }

         v.addAll(permissions);
      }

      public void removePolicies(Set<String> policies)
      {
         if (!type.isControllablePolicy())
            throw new ConstraintException("Type " + type.getId() + " is not controllable by Policy.");

         if (policies == null || policies.size() == 0)
            return;

         if (removePolicies == null)
            removePolicies = new HashSet<String>();

         removePolicies.addAll(policies);
      }

      public void setContentStream(ContentStream contentStream, boolean overwrite)
      {
         if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            if ((contentStream != null && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() == EnumContentStreamAllowed.NOTALLOWED)
               || (contentStream == null && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() == EnumContentStreamAllowed.REQUIRED))
               ;
         }
         if (hasContent || !overwrite)
            throw new ConstraintException("Content already exists and 'overwrite flag' is false.");
      }

      public void updateProperty(CmisProperty property)
      {
         CmisPropertyDefinitionType propertyDefinition = getPropertyDefinition(property.getPropertyDefinitionId());
         if (propertyDefinition == null)
            throw new ConstraintException("Property " + property.getPropertyDefinitionId()
               + " is not supported by type " + type.getId());

         // XXX : skip properties that we can't update
         if ((isNew && propertyDefinition.getUpdatability() == EnumUpdatability.ONCREATE)
            || propertyDefinition.getUpdatability() == EnumUpdatability.READWRITE)
         {
            if (updateProperties == null)
               updateProperties = new HashMap<String, CmisProperty>();
            updateProperties.put(property.getPropertyDefinitionId(), property);
         }
      }

      private CmisPropertyDefinitionType getPropertyDefinition(String propertyId)
      {
         List<CmisPropertyDefinitionType> propertyDefinitions = type.getPropertyDefinition();
         if (propertyDefinitions != null && propertyDefinitions.size() > 0)
         {
            for (CmisPropertyDefinitionType propDef : propertyDefinitions)
            {
               if (propDef.getId().equals(propertyId))
                  return propDef;
            }
         }
         return null;
      }

   }

   ///////////////////////////////////

   private String objectId;

   private Collection<CmisProperty> properties;

   private Map<String, Set<String>> acl;

   private Set<String> policies;

   private CmisTypeDefinitionType type;

   private boolean hasContent;

   public CmisObjectImpl(CmisTypeDefinitionType type, String objectId, Collection<CmisProperty> properties,
      Map<String, Set<String>> acl, Set<String> policies, boolean hasContent)
   {
      if (type == null)
         throw new NullPointerException("Type may not be null.");
      this.type = type;
      this.objectId = objectId;
      this.properties = properties;
      this.acl = acl;
      this.policies = policies;
      this.hasContent = hasContent;
   }

   private Set<String> removePolicies;

   private Map<String, Set<String>> removeAcl;

   private Set<String> updateProperties;

   public void addPermissions(String principal, Set<String> permissions)
   {
      if (!type.isControllableACL())
         throw new ConstraintException("Type " + type.getId() + " is not controllable by ACL.");

      if (principal == null)
         throw new NullPointerException("Principal ID may not be null.");

      if (permissions == null || permissions.size() == 0)
         return;

      if (acl == null)
         acl = new HashMap<String, Set<String>>();

      Set<String> v = acl.get(principal);
      if (v == null)
      {
         v = new HashSet<String>();
         acl.put(principal, v);
      }

      v.addAll(permissions);
   }

   public void addPolicies(Set<String> policies)
   {
      if (!type.isControllablePolicy())
         throw new ConstraintException("Type " + type.getId() + " is not controllable by Policy.");

      if (policies == null || policies.size() == 0)
         return;

      if (this.policies == null)
         this.policies = new HashSet<String>();

      this.policies.addAll(policies);
   }

   public Map<String, Set<String>> getRemovedPermissions()
   {
      return removeAcl;
   }

   public Set<String> getRemovedPolicies()
   {
      return removePolicies;
   }

   public Set<String> getUpdatedProperties()
   {
      if (updateProperties == null)
         return Collections.emptySet();
      return Collections.unmodifiableSet(updateProperties);
   }

   public void removePermissions(String principal, Set<String> permissions)
   {
      if (!type.isControllableACL())
         throw new ConstraintException("Type " + type.getId() + " is not controllable by ACL.");

      if (principal == null)
         throw new NullPointerException("Principal ID may not be null.");

      if (permissions == null || permissions.size() == 0)
         return;

      if (removeAcl == null)
         removeAcl = new HashMap<String, Set<String>>();

      Set<String> v = removeAcl.get(principal);
      if (v == null)
      {
         v = new HashSet<String>();
         removeAcl.put(principal, v);
      }

      v.addAll(permissions);
   }

   public void removePolicies(Set<String> policies)
   {
      if (!type.isControllablePolicy())
         throw new ConstraintException("Type " + type.getId() + " is not controllable by Policy.");

      if (policies == null || policies.size() == 0)
         return;

      if (removePolicies == null)
         removePolicies = new HashSet<String>();

      removePolicies.addAll(policies);
   }

   public void setContentStream(ContentStream contentStream, boolean overwrite)
   {
      if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         if ((contentStream != null && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() == EnumContentStreamAllowed.NOTALLOWED)
            || (contentStream == null && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() == EnumContentStreamAllowed.REQUIRED))
            ;
      }
      if (hasContent || !overwrite)
         throw new ConstraintException("Content already exists and 'overwrite flag' is false.");
   }

   public void setProperty(CmisProperty property)
   {
      CmisPropertyDefinitionType propertyDefinition = getPropertyDefinition(property.getPropertyDefinitionId());
      if (propertyDefinition == null)
         throw new ConstraintException("Property " + property.getPropertyDefinitionId() + " is not supported by type "
            + type.getId());

      // XXX : skip properties that we can't update
      if ((isNew() && propertyDefinition.getUpdatability() == EnumUpdatability.ONCREATE)
         || propertyDefinition.getUpdatability() == EnumUpdatability.READWRITE)
      {
         if (updateProperties == null)
            updateProperties = new HashSet<String>();
         updateProperties.add(property.getPropertyDefinitionId());
      }
   }

   private CmisPropertyDefinitionType getPropertyDefinition(String propertyId)
   {
      List<CmisPropertyDefinitionType> propertyDefinitions = type.getPropertyDefinition();
      if (propertyDefinitions != null && propertyDefinitions.size() > 0)
      {
         for (CmisPropertyDefinitionType propDef : propertyDefinitions)
         {
            if (propDef.getId().equals(propertyId))
               return propDef;
         }
      }
      return null;
   }

   public CmisAllowableActionsType getAllowableActions()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getObjectId()
   {
      return objectId;
   }

   public Map<String, Set<String>> getPermissions()
   {
      if (acl == null)
         return Collections.emptyMap();
      return Collections.unmodifiableMap(acl);
   }

   public Set<String> getPolicies()
   {
      if (policies == null)
         Collections.emptySet();
      return Collections.unmodifiableSet(policies);
   }

   public Collection<CmisProperty> getProperties()
   {
      if (properties == null)
         return Collections.emptyList();
      return Collections.unmodifiableCollection(properties);
   }

   public CmisTypeDefinitionType getTypeDefinition()
   {
      return type;
   }

   public Updates getUpdates()
   {
      return new UpdatesImpl(type, isNew(), hasContent);
   }

   public boolean isNew()
   {
      return objectId == null;
   }

   public boolean hasContent()
   {
      return hasContent;
   }

}
