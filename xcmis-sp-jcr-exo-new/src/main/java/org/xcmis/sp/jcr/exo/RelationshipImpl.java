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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.object.Property;

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
public class RelationshipImpl extends BaseObjectData implements Relationship
{

   private static final String RELATIONSHIPS = "/" + JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_RELATIONSHIPS;

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
   public RelationshipImpl(TypeDefinition type, ObjectData source, ObjectData target, String name)
   {
      super(type, null, name);
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
   public RelationshipImpl(TypeDefinition type, Node node)
   {
      super(type, node);
   }

   public String getSourceId()
   {
      if (isNew())
         return source.getObjectId();
      return getString(CMIS.SOURCE_ID);
   }

   public String getTargetId()
   {
      if (isNew())
         return target.getObjectId();
      return getString(CMIS.TARGET_ID);
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
   public Folder getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<Folder> getParents()
   {
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void save() throws StorageException, NameConstraintViolationException, UpdateConflictException
   {
      if (isNew())
      {
         try
         {
            if (name == null)
            {
               Property<?> nameProperty = properties.get(CMIS.NAME);
               if (nameProperty != null)
                  name = (String)nameProperty.getValues().get(0);
            }
            if (name == null || name.length() == 0)
               throw new NameConstraintViolationException("Name for new relationship must be provided.");

            Session session = ((BaseObjectData)source).getNode().getSession();
            Node relationships = (Node)session.getItem(RELATIONSHIPS);

            String sourceId = source.getObjectId();
            Node containerNode = relationships.hasNode(sourceId) //
               ? relationships.getNode(sourceId) //
               : relationships.addNode(sourceId, JcrCMIS.NT_UNSTRUCTURED);

            if (containerNode.hasNode(name))
               throw new NameConstraintViolationException("Object with name " + name + " already exists.");

            Node relationship = containerNode.addNode(name, type.getLocalName());

            //            relationshipNode.setProperty(CMIS.OBJECT_ID, //
            //               ((ExtendedNode)relationshipNode).getIdentifier());
            //            relationshipNode.setProperty(CMIS.NAME, //
            //               name);
            relationship.setProperty(CMIS.OBJECT_TYPE_ID, //
               type.getId());
            relationship.setProperty(CMIS.BASE_TYPE_ID, //
               type.getBaseId().value());
            relationship.setProperty(CMIS.CREATED_BY, //
               session.getUserID());
            relationship.setProperty(CMIS.CREATION_DATE, //
               Calendar.getInstance());
            relationship.setProperty(CMIS.LAST_MODIFIED_BY, //
               session.getUserID());
            relationship.setProperty(CMIS.LAST_MODIFICATION_DATE, //
               Calendar.getInstance());
            relationship.setProperty(CMIS.SOURCE_ID, //
               ((BaseObjectData)source).getNode());
            relationship.setProperty(CMIS.TARGET_ID, //
               ((BaseObjectData)target).getNode());

            for (Property<?> property : properties.values())
               setProperty(relationship, property);

            if (policies != null && policies.size() > 0)
            {
               for (Policy policy : policies)
                  applyPolicy(relationship, policy);
            }

            if (acl != null && acl.size() > 0)
               setACL(relationship, acl);

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
      else
      {
         super.save();
      }
   }
}
