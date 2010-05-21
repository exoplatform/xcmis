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
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Collection;
import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class PolicyDataImpl extends BaseObjectData implements PolicyData
{

   public PolicyDataImpl(TypeDefinition type, Session session, Node node, IndexListener indexListener)
   {
      super(type, null, session, node, indexListener);
   }

   public PolicyDataImpl(TypeDefinition type, Node node, IndexListener indexListener)
   {
      super(type, node, indexListener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   void delete() throws StorageException
   {
      try
      {
         // Check is policy applied to at least one object.
         for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
         {
            Node controllable = iter.nextProperty().getParent();
            if (controllable.isNodeType(JcrCMIS.NT_FILE) //
               || controllable.isNodeType(JcrCMIS.NT_FOLDER) //
               || controllable.isNodeType(JcrCMIS.CMIS_NT_POLICY))
            {
               String msg = "Unable to delete applied policy.";
               throw new ConstraintException(msg);
            }
         }

      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }

      // If not applied to any object
      super.delete();
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // No renditions for Policy object.
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
   public String getPolicyText()
   {
      return getString(CmisConstants.POLICY_TEXT);
   }

}
