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

package org.xcmis.sp.jcr.exo.NEW;

import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.Permission.BasicPermissions;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.impl.AccessControlEntryImpl;
import org.xcmis.spi.object.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseObjectData extends AbstractObjectData
{

   private static final Log LOG = ExoLogger.getLogger(BaseObjectData.class);

   protected Node node;

   public BaseObjectData(TypeDefinition type, Node node)
   {
      super(type);
      this.node = node;
   }

   @Override
   protected void updateProperty(Property<?> value)
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      try
      {
         return ((ExtendedNode)node).getIdentifier();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object ID. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isNew()
   {
      return false;
   }

   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   public Property<?> getProperty(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public FolderData getParent() throws ConstraintException
   {
      try
      {
         if (node.getDepth() == 0)
            throw new ConstraintException("Unable get parent of root folder.");

         // TODO : check is multi-filed. Must throw ConstraintException if is.

         Node parent = node.getParent();
         TypeDefinition parentType = JcrTypeHelper.getTypeDefinition(parent.getPrimaryNodeType(), true);

         return new FolderDataImpl(parent, parentType);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object parent. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Collection<FolderData> getParents()
   {
      try
      {
         if (node.getDepth() == 0)
            Collections.emptyList();

         // TODO : check is unfiled. Must return empty list if is.

         Node parent = node.getParent();
         TypeDefinition parentType = JcrTypeHelper.getTypeDefinition(parent.getPrimaryNodeType(), true);

         List<FolderData> parents = new ArrayList<FolderData>(1);
         parents.add(new FolderDataImpl(parent, parentType));

         // TODO : multi-filing

         return parents;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object parent. " + re.getMessage(), re);
      }
   }

   // Policies

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(PolicyData policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");

      try
      {
         node.setProperty(policy.getObjectId(), ((PolicyDataImpl)policy).getNode());
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new CmisRuntimeException("Unable to apply policy. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Collection<PolicyData> getPolicies()
   {
      if (!type.isControllablePolicy())
         return Collections.emptyList();

      try
      {
         return getAppliedPolicies();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get applied policies. " + re.getMessage(), re);
      }
   }

   private Collection<PolicyData> getAppliedPolicies() throws RepositoryException
   {
      Set<PolicyData> policies = new HashSet<PolicyData>();

      for (PropertyIterator iter = node.getProperties(); iter.hasNext();)
      {
         javax.jcr.Property prop = iter.nextProperty();
         if (prop.getType() == PropertyType.REFERENCE)
         {
            try
            {
               Node n = prop.getNode();

               if (n.getPrimaryNodeType().isNodeType(JcrCMIS.CMIS_NT_POLICY))
               {
                  if (LOG.isDebugEnabled())
                     LOG.debug("Add policy " + prop.getName());
                  policies.add(new PolicyDataImpl(n, //
                     JcrTypeHelper.getTypeDefinition(n.getPrimaryNodeType(), true)));
               }
            }
            catch (ValueFormatException ignored)
            {
               // Can be thrown id met multi-valued property.
               // Not care about it cause policy reference may not be multi-valued.
            }
         }
      }

      return Collections.unmodifiableSet(policies);
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(PolicyData policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");

      try
      {
         node.setProperty(policy.getObjectId(), (Node)null);
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new CmisRuntimeException("Unable remove policy. " + re.getMessage(), re);
      }
   }

   // ACL

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      if (!type.isControllableACL())
         return Collections.emptyList();

      try
      {
         return getACL();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object's ACL. " + re.getMessage(), re);
      }
   }

   private List<AccessControlEntry> getACL() throws RepositoryException
   {
      if (!node.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
      {
         AccessControlList jcrACL = ((ExtendedNode)node).getACL();

         Map<String, Set<String>> cache = new HashMap<String, Set<String>>();

         // Merge JCR ACEs
         List<org.exoplatform.services.jcr.access.AccessControlEntry> jcrACEs = jcrACL.getPermissionEntries();
         for (org.exoplatform.services.jcr.access.AccessControlEntry ace : jcrACEs)
         {
            String identity = ace.getIdentity();

            Set<String> permissions = cache.get(identity);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               cache.put(identity, permissions);
            }

            permissions.add(ace.getPermission());
         }

         List<AccessControlEntry> cmisACL = new ArrayList<AccessControlEntry>(cache.size());

         for (String principal : cache.keySet())
         {
            AccessControlEntryImpl cmisACE = new AccessControlEntryImpl();
            cmisACE.setPrincipal(principal);

            Set<String> values = cache.get(principal);
            // Represent JCR ACEs as CMIS ACEs. 
            if (values.size() == PermissionType.ALL.length)
               cmisACE.getPermissions().add(BasicPermissions.CMIS_ALL.value());
            else if (values.contains(PermissionType.READ) && values.contains(PermissionType.ADD_NODE))
               cmisACE.getPermissions().add(BasicPermissions.CMIS_READ.value());
            else if (values.contains(PermissionType.SET_PROPERTY) && values.contains(PermissionType.REMOVE))
               cmisACE.getPermissions().add(BasicPermissions.CMIS_WRITE.value());

            cmisACL.add(cmisACE);
         }
         return Collections.unmodifiableList(cmisACL);
      }

      // Node has not "exo:privilegeable" mixin.
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> acl) throws ConstraintException
   {
      if (!type.isControllableACL())
         throw new ConstraintException("Type " + type.getId() + " is not controlable by ACL.");

      try
      {
         updateACL(acl);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable to apply ACL. " + re.getMessage(), re);
      }
   }

   private void updateACL(List<AccessControlEntry> acl) throws RepositoryException
   {
      if (!node.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
         node.addMixin(JcrCMIS.EXO_PRIVILEGABLE);

      ExtendedNode extNode = (ExtendedNode)node;

      // Not merge ACL overwrite it.
      extNode.clearACL();
      extNode.setPermissions(createPermissionMap(acl));
   }

   /**
    * Create permission map which can be passed to JCR node.
    * 
    * @param source source ACL
    * @return permission map
    * @throws ConstraintException if at least permission is unknown
    */
   private Map<String, String[]> createPermissionMap(List<AccessControlEntry> source) throws ConstraintException
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      for (AccessControlEntry ace : source)
      {
         String principal = ace.getPrincipal();
         Set<String> permissions = cache.get(principal);
         if (permissions == null)
         {
            permissions = new HashSet<String>();
            cache.put(principal, permissions);
         }
         for (String perm : ace.getPermissions())
         {
            if (BasicPermissions.CMIS_READ.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
               // In CMIS child may be add without write permission for parent.
               permissions.add(PermissionType.ADD_NODE);
            }
            else if (BasicPermissions.CMIS_WRITE.value().equals(perm))
            {
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
            else if (BasicPermissions.CMIS_ALL.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
               permissions.add(PermissionType.ADD_NODE);
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
            else
            {
               String msg = "Unknown permission " + perm;
               throw new ConstraintException(msg);
            }
         }
      }
      Map<String, String[]> aces = new HashMap<String, String[]>();

      for (Map.Entry<String, Set<String>> e : cache.entrySet())
         aces.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));

      return aces;
   }

   // Relationship

   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   // 

   public Node getNode()
   {
      return node;
   }

}
