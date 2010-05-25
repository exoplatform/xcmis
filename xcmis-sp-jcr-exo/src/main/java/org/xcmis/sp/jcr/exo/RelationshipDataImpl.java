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

import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Collection;
import java.util.Collections;

import javax.jcr.Node;
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
    * @param session session
    * @param node TODO
    * @see StorageImpl#createRelationship(ObjectData, ObjectData, String)
    */
   public RelationshipDataImpl(TypeDefinition type, ObjectData source, ObjectData target, Session session, Node node,
      IndexListener indexListener)
   {
      super(type, null, session, node, indexListener);
      this.source = source;
      this.target = target;
   }

   /**
    * Create already saved instance of relationship.
    *
    * @param type type definition
    * @param node back-end JCR node
    * @param indexListener inde listener
    * @see StorageImpl#getObjectById(String)
    * @see StorageImpl#getObjectByPath(String)
    */
   public RelationshipDataImpl(TypeDefinition type, Node node, IndexListener indexListener)
   {
      super(type, node, indexListener);
   }

   /**
    * {@inheritDoc}
    */
   public String getSourceId()
   {
      return getString(CmisConstants.SOURCE_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getTargetId()
   {
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

}
