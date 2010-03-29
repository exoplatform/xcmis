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

package org.xcmis.sp.jcr.exo;

import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.impl.core.value.BooleanValue;
import org.exoplatform.services.jcr.impl.core.value.DateValue;
import org.exoplatform.services.jcr.impl.core.value.DoubleValue;
import org.exoplatform.services.jcr.impl.core.value.LongValue;
import org.exoplatform.services.jcr.impl.core.value.StringValue;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.Permission.BasicPermissions;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.AccessControlEntryImpl;
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.BooleanProperty;
import org.xcmis.spi.object.impl.DateTimeProperty;
import org.xcmis.spi.object.impl.DecimalProperty;
import org.xcmis.spi.object.impl.HtmlProperty;
import org.xcmis.spi.object.impl.IdProperty;
import org.xcmis.spi.object.impl.IntegerProperty;
import org.xcmis.spi.object.impl.StringProperty;
import org.xcmis.spi.object.impl.UriProperty;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class BaseObjectData implements ObjectData
{

   private static final Log LOG = ExoLogger.getLogger(BaseObjectData.class);

   /** Object's type definition. */
   protected final TypeDefinition type;

   /**
    * Temporary storage for object properties. For newly create object all
    * properties will be stored here before calling {@link #save()}.
    */
   protected final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

   /**
    * Temporary storage for policies applied to object. For newly created all
    * policies will be stored in here before calling {@link #save()}.
    */
   protected Set<Policy> policies;

   /**
    * Temporary storage for ACL applied to object. For newly created all ACL
    * will be stored in here before calling {@link #save()}.
    */
   protected List<AccessControlEntry> acl;

   /**
    * Parent folder id for newly created fileable objects.
    */
   protected FolderImpl parent;

   /**
    * Back-end JCR node, it is <code>null</code> for newly created object.
    */
   protected Node node;

   /**
    * May store new name of object. If <code>null</code> it minds 'name not set
    * yet' for newly created unsaved objects or 'no new name' for already
    * persisted object.
    */
   protected String name;

   /**
    * JCR session.
    */
   protected Session session;

   /**
    * Create new unsaved instance of CMIS object. This object should be saved,
    * {@link #save()}.
    * 
    * @param type type definition for new object
    * @param parent parent folder
    * @param session JCR session
    */
   public BaseObjectData(TypeDefinition type, Folder parent, Session session)
   {
      this.type = type;
      this.parent = (FolderImpl)parent;
      this.session = session;
      this.node = null;
   }

   /**
    * Create new instance of persisted CMIS object.
    * 
    * @param type object's type
    * @param node back-end JCR node
    */
   public BaseObjectData(TypeDefinition type, Node node)
   {
      this.type = type;
      this.node = node;
   }

   /**
    * {@inheritDoc}
    */
   public void accept(CmisVisitor visitor)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("accept");
      }
      visitor.visit(this);
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(Policy policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");
      }

      if (policy.isNew())
      {
         throw new CmisRuntimeException("Unable apply newly created policy.");
      }

      if (isNew())
      {
         if (policies == null)
         {
            policies = new HashSet<Policy>();
         }
         policies.add(policy);
      }
      else
      {
         try
         {
            applyPolicy(node, policy);
         }
         catch (javax.jcr.RepositoryException re)
         {
            throw new CmisRuntimeException("Unable to apply policy. " + re.getMessage(), re);
         }
      }
   }

   public void delete() throws StorageException
   {
      try
      {
         Node parent = node.getParent();
         node.remove();
         parent.save();
      }
      catch (javax.jcr.ReferentialIntegrityException rie)
      {
         throw new ConstraintException("Object can't be deleted cause to storage referential integrity. "
            + "Probably this object is source or target at least one Relationship. "
            + "Those Relationship should be delted before.");
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      if (!type.isControllableACL())
      {
         return Collections.emptyList();
      }

      if (isNew())
      {
         if (acl == null)
         {
            return Collections.emptyList();
         }
         return Collections.unmodifiableList(acl);
      }
      try
      {
         return getACL();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object's ACL. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public BaseType getBaseType()
   {
      return type.getBaseId();
   }

   /**
    * {@inheritDoc}
    */
   public String getChangeToken()
   {
      if (isNew())
      {
         return null;
      }
      return getString(CMIS.CHANGE_TOKEN);
   }

   /**
    * {@inheritDoc}
    */
   public String getCreatedBy()
   {
      if (isNew())
      {
         return null;
      }
      return getString(CMIS.CREATED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getCreationDate()
   {
      if (isNew())
      {
         return null;
      }
      return getDate(CMIS.CREATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getLastModificationDate()
   {
      if (isNew())
      {
         return null;
      }
      return getDate(CMIS.LAST_MODIFICATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public String getLastModifiedBy()
   {
      if (isNew())
      {
         return null;
      }
      return getString(CMIS.LAST_MODIFIED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      if (isNew() || name != null)
      {
         return name;
      }

      try
      {
         return node.getName();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object name. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      if (isNew())
      {
         return null;
      }

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
   public Folder getParent() throws ConstraintException
   {
      if (isNew())
      {
         return parent;
      }

      try
      {
         if (node.getDepth() == 0)
         {
            throw new ConstraintException("Unable get parent of root folder.");
         }

         Node parentNode = node.getParent();
         TypeDefinition parentType = JcrTypeHelper.getTypeDefinition(parentNode.getPrimaryNodeType(), true);
         return new FolderImpl(parentType, parentNode);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object parent. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Collection<Folder> getParents()
   {
      if (isNew())
      {
         Folder parent = getParent();
         if (parent != null)
         {
            return Collections.singletonList(parent);
         }
         return Collections.emptyList();
      }

      try
      {
         if (node.getDepth() == 0)
         {
            Collections.emptyList();
         }

         Set<Folder> parents = new HashSet<Folder>();
         for (PropertyIterator iterator = node.getReferences(); iterator.hasNext();)
         {
            Node link = iterator.nextProperty().getParent();
            if (link.isNodeType("nt:linkedFile"))
            {
               Node parent = link.getParent();
               parents.add(new FolderImpl(JcrTypeHelper.getTypeDefinition(parent.getPrimaryNodeType(), true), parent));
            }
         }

         if (parents.size() == 0)
         {
            Node parent = node.getParent();
            parents.add(new FolderImpl(JcrTypeHelper.getTypeDefinition(parent.getPrimaryNodeType(), true), parent));
         }
         return parents;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object parent. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Collection<Policy> getPolicies()
   {
      if (!type.isControllablePolicy())
      {
         return Collections.emptyList();
      }

      if (isNew())
      {
         if (policies == null)
         {
            return Collections.emptySet();
         }
         return Collections.unmodifiableSet(policies);
      }

      try
      {
         return getAppliedPolicies();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get applied policies. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getProperties()
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      for (PropertyDefinition<?> def : type.getPropertyDefinitions())
      {
         properties.put(def.getId(), getProperty(def));
      }

      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public Property<?> getProperty(String id)
   {
      PropertyDefinition<?> def = type.getPropertyDefinition(id);
      if (def == null)
      {
         return null; // TODO : need to throw exception ??
      }

      return getProperty(def);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Relationship> getRelationships(RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes)
   {
      if (isNew())
      {
         return CmisUtils.emptyItemsIterator();
      }

      try
      {
         // Can met one relationship twice if object has relation to it self.
         Set<Relationship> cache = new HashSet<Relationship>();

         for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
         {
            javax.jcr.Property prop = iter.nextProperty();

            String propName = prop.getName();

            if ((direction == RelationshipDirection.EITHER && (propName.equals(CMIS.SOURCE_ID) || propName
               .equals(CMIS.TARGET_ID))) //
               || (direction == RelationshipDirection.SOURCE && propName.equals(CMIS.SOURCE_ID)) //
               || (direction == RelationshipDirection.TARGET && propName.equals(CMIS.TARGET_ID)))
            {

               Node relNode = prop.getParent();

               NodeType nodeType = relNode.getPrimaryNodeType();

               if (nodeType.getName().equals(type.getLocalName()) //
                  || (includeSubRelationshipTypes && nodeType.isNodeType(type.getLocalName())))
               {
                  RelationshipImpl relationship =
                     new RelationshipImpl(JcrTypeHelper.getTypeDefinition(nodeType, true), relNode);
                  boolean added = cache.add(relationship);
                  if (LOG.isDebugEnabled() && added)
                  {
                     LOG.debug("Add relationship " + relationship.getName());
                  }
               }
            }
         }
         return new BaseItemsIterator<Relationship>(cache);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get relationships. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getSubset(PropertyFilter filter)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> def : type.getPropertyDefinitions())
      {
         String queryName = def.getQueryName();
         if (!filter.accept(queryName))
         {
            continue;
         }
         String id = def.getId();
         properties.put(id, getProperty(def));
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   public String getTypeId()
   {
      return type.getId();
   }

   /**
    * {@inheritDoc}
    */
   public boolean isNew()
   {
      return node == null;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(Policy policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");
      }

      if (isNew())
      {
         // If not saved yet simply remove from temporary storage
         policies.remove(policy);
      }
      else
      {
         try
         {
            node.setProperty(policy.getObjectId(), (Node)null);
         }
         catch (javax.jcr.RepositoryException re)
         {
            throw new CmisRuntimeException("Unable remove policy. " + re.getMessage(), re);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void save() throws StorageException, NameConstraintViolationException, UpdateConflictException
   {
      if (isNew())
      {
         create();
      }
      else
      {
         try
         {
            Node parentNode = node.getParent();

            // New name was set. Need rename Document.
            // See setName(String), setProperty(Node, Property<?>). 
            if (name != null)
            {
               if (name.length() == 0)
               {
                  throw new NameConstraintViolationException("Name is empty.");
               }

               if (parentNode.hasNode(name))
               {
                  throw new NameConstraintViolationException("Object with name " + name + " already exists.");
               }

               String srcPath = node.getPath();
               String destPath = srcPath.substring(0, srcPath.lastIndexOf('/') + 1) + name;

               node.getSession().move(srcPath, destPath);
            }

            node.setProperty(CMIS.LAST_MODIFICATION_DATE,//
               Calendar.getInstance());
            node.setProperty(CMIS.LAST_MODIFIED_BY, //
               node.getSession().getUserID());
            node.setProperty(CMIS.CHANGE_TOKEN, //
               IdGenerator.generate());

            parentNode.save();
         }
         catch (RepositoryException re)
         {
            throw new StorageException("Unable save object. " + re.getMessage(), re);
         }

      }
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> aces) throws ConstraintException
   {
      if (!type.isControllableACL())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by ACL.");
      }

      if (isNew())
      {
         if (this.acl != null)
         {
            this.acl.clear(); // Not merged, just replaced.
         }

         if (aces != null && aces.size() > 0)
         {
            if (this.acl == null)
            {
               this.acl = new ArrayList<AccessControlEntry>();
            }
            this.acl.addAll(aces);
         }
      }
      else
      {
         try
         {
            setACL(node, aces);
         }
         catch (RepositoryException re)
         {
            throw new CmisRuntimeException("Unable to apply ACL. " + re.getMessage(), re);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setName(String name) throws NameConstraintViolationException
   {
      // Name will be used when method {@link #save()} called. Then node will be moved.
      this.name = name;
   }

   /**
    * {@inheritDoc}
    */
   public void setProperties(Map<String, Property<?>> properties) throws ConstraintException,
      NameConstraintViolationException
   {
      for (Property<?> property : properties.values())
      {
         setProperty(property);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setProperty(Property<?> property) throws ConstraintException
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(property.getId());

      if (definition == null)
      {
         throw new ConstraintException("Property " + property.getId() + " is not in property definitions list of type "
            + type.getId());
      }

      if (property.getType() != definition.getPropertyType())
      {
         throw new ConstraintException("Property type is not match.");
      }

      if (!definition.isMultivalued() && property.getValues().size() > 1)
      {
         throw new ConstraintException("Property " + property.getId() + " is not multi-valued.");
      }

      if (definition.isRequired() && property.getValues().size() == 0)
      {
         throw new ConstraintException("Required property " + property.getId() + " can't be removed.");
      }

      for (Object v : property.getValues())
      {
         if (v == null)
         {
            throw new ConstraintException("Null value not allowed. List must not contains null items.");
         }
      }

      // TODO : validate min/max/length etc.

      Updatability updatability = definition.getUpdatability();
      if (updatability == Updatability.READWRITE //
         || (updatability == Updatability.ONCREATE && isNew()) //
         || (updatability == Updatability.WHENCHECKEDOUT && getBaseType() == BaseType.DOCUMENT && ((Document)this)
            .isPWC()))
      {
         if (property.getId().equals(CMIS.NAME))
         {
            // Special property for JCR back-end.
            name = (String)property.getValues().get(0);
         }
         else
         {
            if (isNew())
            {
               properties.put(property.getId(), property);
            }
            else
            {
               setProperty(node, property);
            }
         }
      }
      else
      {
         // Some clients may send all properties even need update only one.
         if (LOG.isDebugEnabled())
         {
            LOG.debug("Property " + property.getId() + " not updatable at the moment.");
         }
      }
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
      {
         aces.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
      }

      return aces;
   }

   private Property<?> createProperty(PropertyDefinition<?> def, Value[] values) throws RepositoryException
   {
      if (def.getPropertyType() == PropertyType.BOOLEAN)
      {
         List<Boolean> v = new ArrayList<Boolean>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(values[i].getBoolean());
         }

         return new BooleanProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.DATETIME)
      {
         List<Calendar> v = new ArrayList<Calendar>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(values[i].getDate());
         }

         return new DateTimeProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.DECIMAL)
      {
         List<BigDecimal> v = new ArrayList<BigDecimal>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(BigDecimal.valueOf(values[i].getDouble()));
         }

         return new DecimalProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.HTML)
      {
         List<String> v = new ArrayList<String>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(values[i].getString());
         }

         return new HtmlProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.ID)
      {
         List<String> v = new ArrayList<String>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(values[i].getString());
         }

         return new IdProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.INTEGER)
      {
         List<BigInteger> v = new ArrayList<BigInteger>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(BigInteger.valueOf(values[i].getLong()));
         }

         return new IntegerProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.STRING)
      {
         List<String> v = new ArrayList<String>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            v.add(values[i].getString());
         }

         return new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else if (def.getPropertyType() == PropertyType.URI)
      {
         List<URI> v = new ArrayList<URI>(values.length);

         for (int i = 0; i < values.length; i++)
         {
            try
            {
               v.add(new URI(values[i].getString()));
            }
            catch (URISyntaxException ue)
            {
               LOG.error(ue.getMessage(), ue);
            }
         }

         return new UriProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), v);
      }
      else
      {
         throw new CmisRuntimeException("Unknown property type.");
      }
   }

   private List<AccessControlEntry> getACL() throws RepositoryException
   {
      if (node.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
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
            {
               cmisACE.getPermissions().add(BasicPermissions.CMIS_ALL.value());
            }
            else if (values.contains(PermissionType.READ) && values.contains(PermissionType.ADD_NODE))
            {
               cmisACE.getPermissions().add(BasicPermissions.CMIS_READ.value());
            }
            else if (values.contains(PermissionType.SET_PROPERTY) && values.contains(PermissionType.REMOVE))
            {
               cmisACE.getPermissions().add(BasicPermissions.CMIS_WRITE.value());
            }

            cmisACL.add(cmisACE);
         }
         return Collections.unmodifiableList(cmisACL);
      }

      // Node has not "exo:privilegeable" mixin.
      return Collections.emptyList();
   }

   private Collection<Policy> getAppliedPolicies() throws RepositoryException
   {
      Set<Policy> policies = new HashSet<Policy>();

      for (PropertyIterator iter = node.getProperties(); iter.hasNext();)
      {
         javax.jcr.Property prop = iter.nextProperty();
         if (prop.getType() == javax.jcr.PropertyType.REFERENCE)
         {
            try
            {
               Node pol = prop.getNode();

               if (pol.getPrimaryNodeType().isNodeType(JcrCMIS.CMIS_NT_POLICY))
               {
                  boolean added =
                     policies.add(new PolicyImpl(JcrTypeHelper.getTypeDefinition(pol.getPrimaryNodeType(), true), pol));

                  if (LOG.isDebugEnabled() && added)
                  {
                     LOG.debug("Add policy " + prop.getName());
                  }
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

   private Property<?> getProperty(PropertyDefinition<?> definition)
   {
      if (isNew())
      {
         return properties.get(definition.getId());
      }
      else
      {
         try
         {
            try
            {
               javax.jcr.Property jcrProperty = node.getProperty(definition.getLocalName());

               return createProperty(definition, //
                  definition.isMultivalued() ? jcrProperty.getValues() : new Value[]{jcrProperty.getValue()});
            }
            catch (PathNotFoundException pnf)
            {
               if (LOG.isDebugEnabled())
               {
                  LOG.debug("Property " + definition.getId() + " is not set.");
               }

               if (definition.getId().equals(CMIS.OBJECT_ID))
               {
                  return new IdProperty(definition.getId(), definition.getQueryName(), definition.getLocalName(),
                     definition.getDisplayName(), getObjectId());
               }
               else if (definition.getId().equals(CMIS.NAME))
               {
                  return new StringProperty(definition.getId(), definition.getQueryName(), definition.getLocalName(),
                     definition.getDisplayName(), getName());
               }
               else if (definition.getId().equals(CMIS.PATH))
               {
                  return new StringProperty(definition.getId(), definition.getQueryName(), definition.getLocalName(),
                     definition.getDisplayName(), node.getPath());
               }
               else if (definition.getId().equals(CMIS.PARENT_ID) && node.getDepth() != 0)
               {
                  return new IdProperty(definition.getId(), definition.getQueryName(), definition.getLocalName(),
                     definition.getDisplayName(), ((ExtendedNode)node.getParent()).getIdentifier());
               }
               else if (definition.getId().equals(CMIS.CONTENT_STREAM_FILE_NAME))
               {
                  if (((Document)this).hasContent())
                  {
                     return new StringProperty(definition.getId(), definition.getQueryName(),
                        definition.getLocalName(), definition.getDisplayName(), getName());
                  }
               }

               // TODO : need more virtual properties ?? 

               // Property is valid but not set in back-end.
               // Return property in 'value not set' state. 
               return createProperty(definition, new Value[0]);
            }
         }
         catch (RepositoryException re)
         {
            throw new CmisRuntimeException("Unable get property " + definition.getId() + ". " + re.getMessage(), re);
         }
      }
   }

   protected void applyPolicy(Node data, Policy policy) throws RepositoryException
   {
      String policyId = policy.getObjectId();
      if (!data.hasProperty(policyId))
      {
         data.setProperty(policyId, ((PolicyImpl)policy).getNode());
      }
   }

   protected abstract void create() throws StorageException, NameConstraintViolationException;

   protected Boolean getBoolean(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return node.getProperty(id).getBoolean();
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Boolean[] getBooleans(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         Value[] values = node.getProperty(id).getValues();
         Boolean[] res = new Boolean[values.length];
         for (int i = 0; i < values.length; i++)
         {
            res[i] = values[i].getBoolean();
         }
         return res;
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Calendar getDate(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return node.getProperty(id).getDate();
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Calendar[] getDates(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         Value[] values = node.getProperty(id).getValues();
         Calendar[] res = new Calendar[values.length];
         for (int i = 0; i < values.length; i++)
         {
            res[i] = values[i].getDate();
         }
         return res;
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Double getDouble(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return node.getProperty(id).getDouble();
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Double[] getDoubles(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         Value[] values = node.getProperty(id).getValues();
         Double[] res = new Double[values.length];
         for (int i = 0; i < values.length; i++)
         {
            res[i] = values[i].getDouble();
         }
         return res;
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Long getLong(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return node.getProperty(id).getLong();
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected Long[] getLongs(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         Value[] values = node.getProperty(id).getValues();
         Long[] res = new Long[values.length];
         for (int i = 0; i < values.length; i++)
         {
            res[i] = values[i].getLong();
         }
         return res;
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected String getString(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return node.getProperty(id).getString();
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected String[] getStrings(String id)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         Value[] values = node.getProperty(id).getValues();
         String[] res = new String[values.length];
         for (int i = 0; i < values.length; i++)
         {
            res[i] = values[i].getString();
         }
         return res;
      }
      catch (PathNotFoundException pe)
      {
         // does not exists
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get property " + id + ". " + re.getMessage(), re);
      }
   }

   protected void setACL(Node data, List<AccessControlEntry> aces) throws RepositoryException
   {
      if (!data.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
      {
         data.addMixin(JcrCMIS.EXO_PRIVILEGABLE);
      }

      ExtendedNode extNode = (ExtendedNode)data;

      // Not merge ACL overwrite it.
      extNode.clearACL();
      if (aces != null && aces.size() > 0)
      {
         extNode.setPermissions(createPermissionMap(aces));
      }
   }

   @SuppressWarnings("unchecked")
   protected void setProperty(Node data, Property<?> property)
   {
      // Type and value should be already checked.
      // 1. Allowed property for this type.
      // 2. Type matched to type definition.
      // 3. Required property has value(s).
      // 4. Single-valued property does not contains more then one value.

      try
      {
         if (property.getType() == PropertyType.BOOLEAN)
         {
            List<Boolean> booleans = (List<Boolean>)property.getValues();
            if (booleans.size() == 0)
            {
               data.setProperty(property.getLocalName(), (Boolean)null);
            }
            else if (booleans.size() == 1)
            {
               data.setProperty(property.getLocalName(), booleans.get(0));
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new BooleanValue(booleans.get(i));
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
         else if (property.getType() == PropertyType.DATETIME)
         {
            List<Calendar> datetime = (List<Calendar>)property.getValues();
            if (datetime.size() == 0)
            {
               data.setProperty(property.getLocalName(), (Calendar)null);
            }
            else if (datetime.size() == 1)
            {
               data.setProperty(property.getLocalName(), datetime.get(0));
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new DateValue(datetime.get(i));
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
         else if (property.getType() == PropertyType.DECIMAL)
         {
            List<BigDecimal> doubles = (List<BigDecimal>)property.getValues();
            if (doubles.size() == 0)
            {
               data.setProperty(property.getLocalName(), (Double)null);
            }
            else if (doubles.size() == 1)
            {
               data.setProperty(property.getLocalName(), doubles.get(0).doubleValue());
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new DoubleValue(doubles.get(i).doubleValue());
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
         else if (property.getType() == PropertyType.INTEGER)
         {
            List<BigInteger> integers = (List<BigInteger>)property.getValues();
            if (integers.size() == 0)
            {
               data.setProperty(property.getLocalName(), (Long)null);
            }
            else if (integers.size() == 1)
            {
               data.setProperty(property.getLocalName(), integers.get(0).longValue());
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new LongValue(integers.get(i).longValue());
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
         /*
          * TODO : need to use different type, 'name' should be acceptable.
          * ID (at least) must not be mixed with STRING and other it is
          * important property type. 
          */
         else if (property.getType() == PropertyType.HTML //
            || property.getType() == PropertyType.ID //  
            || property.getType() == PropertyType.STRING)
         {
            List<String> text = (List<String>)property.getValues();
            if (text.size() == 0)
            {
               data.setProperty(property.getLocalName(), (String)null);
            }
            else if (text.size() == 1)
            {
               data.setProperty(property.getLocalName(), text.get(0));
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new StringValue(text.get(i));
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
         else if (property.getType() == PropertyType.URI)
         {
            List<URI> uris = (List<URI>)property.getValues();
            if (uris.size() == 0)
            {
               data.setProperty(property.getLocalName(), (String)null);
            }
            else if (uris.size() == 1)
            {
               data.setProperty(property.getLocalName(), uris.get(0).toString());
            }
            else
            {
               Value[] jcrValue = new Value[property.getValues().size()];

               for (int i = 0; i < jcrValue.length; i++)
               {
                  jcrValue[i] = new StringValue(uris.get(i).toString());
               }

               data.setProperty(property.getLocalName(), jcrValue);
            }
         }
      }
      catch (IOException io)
      {
         throw new CmisRuntimeException("Failed set or update property " + property.getId() + ". " + io.getMessage(),
            io);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Failed set or update property " + property.getId() + ". " + re.getMessage(),
            re);
      }
   }

   Node getNode()
   {
      return node;
   }

}
