/*
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

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.Storage;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.Property;

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

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class NewObjectData implements ObjectData
{

   /** Type of new object. */
   protected final TypeDefinition type;

   /** Parent folder. */
   protected final FolderData parent;

   /**
    * Temporary storage for object properties. For newly create object all
    * properties will be stored here before calling
    * {@link Storage#saveObject(ObjectData)}.
    */
   protected final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

   /**
    * Temporary storage for policies applied to object. For newly created all
    * policies will be stored in here before calling
    * {@link Storage#saveObject(ObjectData)}.
    */
   protected Set<PolicyData> policies;

   /**
    * Temporary storage for ACL applied to object. For newly created all ACL
    * will be stored in here before calling
    * {@link Storage#saveObject(ObjectData)}.
    */
   protected List<AccessControlEntry> acl;

   protected boolean isNew;

   protected Node node;

   public NewObjectData(FolderData parent, TypeDefinition type)
   {
      this.parent = parent;
      this.type = type;
      this.isNew = true;
   }

   public void accept(CmisVisitor visitor)
   {
      if (isNew)
         throw new UnsupportedOperationException("accept");
      
      visitor.visit(this);
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
      if (isNew)
      return null;
      getStri
   }

   public ContentStream getContentStream(String streamId)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getCreatedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getCreationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getLastModificationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLastModifiedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getObjectId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public FolderData getParent() throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<FolderData> getParents()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Properties getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public TypeDefinition getTypeDefinition()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getTypeId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isNew()
   {
      return isNew;
   }

   public void removePolicy(PolicyData policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   // Policies

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(PolicyData policy)
   {
      if (!type.isControllablePolicy())
         throw new ConstraintException("Object is not controllable by Policy.");

      if (policies == null)
         policies = new HashSet<PolicyData>();

      policies.add(policy);
   }

   /**
    * {@inheritDoc}
    */
   public Collection<PolicyData> getPolicies()
   {
      if (!type.isControllablePolicy() || policies == null)
         return Collections.emptySet();

      return Collections.unmodifiableSet(policies);
   }

   // ACL

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      if (!type.isControllableACL() || acl == null)
         return Collections.emptyList();

      return Collections.unmodifiableList(acl);
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> acl) throws ConstraintException
   {
      if (!type.isControllableACL())
         throw new ConstraintException("Object is not controllable by ACL.");

      if (this.acl == null)
         this.acl = new ArrayList<AccessControlEntry>();
      else
         this.acl.clear(); // Not merged, just replaced.

      if (acl != null) // assumes if null then remove ACL.
         this.acl.addAll(acl);
   }

}
