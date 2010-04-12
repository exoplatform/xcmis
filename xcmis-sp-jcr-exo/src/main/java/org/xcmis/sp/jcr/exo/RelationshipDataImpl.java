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

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class RelationshipDataImpl extends BaseObjectData implements RelationshipData
{

   protected ObjectData source;

   protected ObjectData target;

   /**
    * New unsaved instance of relationship.
    *
    * @param type type definition
    * @param source source of relationship
    * @param target target of relationship
    * @param name name
    * @see StorageImpl#createRelationship(ObjectData, ObjectData, String)
    */
   public RelationshipDataImpl(TypeDefinition type, ObjectData source, ObjectData target, Session session)
   {
      super(type, null, session);
      this.source = source;
      this.target = target;
   }

   /**
    * Create already saved instance of relationship.
    *
    * @param type type definition
    * @param node back-end JCR node
    * @see StorageImpl#getObject(String)
    * @see StorageImpl#getObjectByPath(String)
    */
   public RelationshipDataImpl(TypeDefinition type, Node node)
   {
      super(type, node);
   }

   /**
    * {@inheritDoc}
    */
   public String getSourceId()
   {
      if (isNew())
      {
         return source.getObjectId();
      }
      return getString(CmisConstants.SOURCE_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getTargetId()
   {
      if (isNew())
      {
         return target.getObjectId();
      }
      return getString(CmisConstants.TARGET_ID);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FolderData getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<FolderData> getParents()
   {
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void create() throws StorageException, NameConstraintViolationException
   {
      try
      {
         if (name == null || name.length() == 0)
         {
            throw new NameConstraintViolationException("Name for new relationship must be provided.");
         }

         Node relationships =
            (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_RELATIONSHIPS);

         if (relationships.hasNode(name))
         {
            throw new NameConstraintViolationException("Relationship with name " + name + " already exists.");
         }

         Node relationship = relationships.addNode(name, type.getLocalName());

         relationship.setProperty(CmisConstants.OBJECT_TYPE_ID, //
            type.getId());
         relationship.setProperty(CmisConstants.BASE_TYPE_ID, //
            type.getBaseId().value());
         relationship.setProperty(CmisConstants.CREATED_BY, //
            session.getUserID());
         relationship.setProperty(CmisConstants.CREATION_DATE, //
            Calendar.getInstance());
         relationship.setProperty(CmisConstants.LAST_MODIFIED_BY, //
            session.getUserID());
         relationship.setProperty(CmisConstants.LAST_MODIFICATION_DATE, //
            Calendar.getInstance());
         relationship.setProperty(CmisConstants.SOURCE_ID, //
            ((BaseObjectData)source).getNode());
         relationship.setProperty(CmisConstants.TARGET_ID, //
            ((BaseObjectData)target).getNode());

         for (Property<?> property : properties.values())
         {
            setProperty(relationship, property);
         }

         if (policies != null && policies.size() > 0)
         {
            for (PolicyData policy : policies)
            {
               applyPolicy(relationship, policy);
            }
         }

         if (acl != null && acl.size() > 0)
         {
            setACL(relationship, acl);
         }

         relationships.save();

         name = null;
         policies = null;
         acl = null;
         properties.clear();

         node = relationship;
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable create new relationship. " + re.getMessage(), re);
      }
   }
}
