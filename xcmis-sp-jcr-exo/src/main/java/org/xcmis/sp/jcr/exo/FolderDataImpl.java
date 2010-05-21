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

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.TypeDefinition;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FolderDataImpl extends BaseObjectData implements FolderData
{

   private static final Log LOG = ExoLogger.getLogger(FolderDataImpl.class);

   public FolderDataImpl(TypeDefinition type, FolderData parent, Session session, Node node, IndexListener indexListener)
   {
      super(type, parent, session, node, indexListener);
   }

   public FolderDataImpl(TypeDefinition type, Node node, IndexListener indexListener)
   {
      super(type, node, indexListener);
   }

   /**
    * {@inheritDoc}
    */
   public void addObject(ObjectData object) throws ConstraintException
   {
      try
      {
         Node data = ((BaseObjectData)object).getNode();
         if (data.getParent().isNodeType("xcmis:unfiledObject"))
         {
            // Object is in unfiled store. Move object in current folder.
            Node unfiled = data.getParent();
            String dataName = data.getName();
            String destPath = node.getPath();
            destPath += destPath.equals("/") ? dataName : ("/" + dataName);

            session.move(data.getPath(), destPath);

            // Remove unnecessary wrapper.
            unfiled.remove();
         }
         else
         {
            // Object (real object) is in some folder in repository.
            // Add link in current folder.
            Node link = node.addNode(object.getName(), "nt:linkedFile");
            link.setProperty("jcr:content", data);
         }

         session.save();
         indexListener.updated(object);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable add object to current folder. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<ObjectData> getChildren(String orderBy)
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get children " + getObjectId() + ", name " + getName());
      }

      try
      {
         return new FolderChildrenIterator(node.getNodes(), indexListener);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get children for folder " + getObjectId() + ". " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // TODO : renditions for Folder object.
      // It may be XML or HTML representation direct child or full tree.
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getPath()
   {
      try
      {
         return node.getPath();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get folder path. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasChildren()
   {
      try
      {
         // Weak solution. Even this method return true iterator over children may
         // be empty if folder contains only not CMIS object.
         return node.hasNodes();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unexpected error. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isAllowedChildType(String typeId)
   {
      // There is no any restriction about types. Any fileable objects supported.
      // Is type is fileable must be checked before calling this method.
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRoot()
   {
      try
      {
         return node.getDepth() == 0;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unexpected error. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void removeObject(ObjectData object)
   {
      try
      {
         Node data = ((BaseObjectData)object).getNode();

         if (((ExtendedNode)data.getParent()).getIdentifier().equals(((ExtendedNode)node).getIdentifier()))
         {
            // Node 'data' is filed in current folder directly.
            // Check links from other folders.
            Node link = null;
            for (PropertyIterator references = data.getReferences(); references.hasNext();)
            {
               Node next = references.nextProperty().getParent();
               if (next.isNodeType("nt:linkedFile"))
               {
                  link = next;
                  break; // Get a first one which met.
               }
            }

            // Determine where we should place object.
            String destPath;
            if (link != null)
            {
               // At least one link (object filed in more then one folder) exists.
               // Replace founded link by original object.
               destPath = link.getPath();
               link.remove();
            }
            else
            {
               // There is no any links for this node in other folders.
               // Move this node in unfiled store.
               Node unfiledStore =
                  (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_UNFILED);

               Node unfiled = unfiledStore.addNode(object.getObjectId(), "xcmis:unfiledObject");

               destPath = unfiled.getPath() + "/" + data.getName();
            }

            // Move object node from current folder.
            session.move(data.getPath(), destPath);
            data = (Node)session.getItem(destPath);

         }
         else
         {
            // Need find link in current folder.
            for (PropertyIterator references = data.getReferences(); references.hasNext();)
            {
               Node next = references.nextProperty().getParent();
               if (next.isNodeType("nt:linkedFile")
                  && ((ExtendedNode)next.getParent()).getIdentifier().equals(((ExtendedNode)node).getIdentifier()))
               {
                  next.remove();
                  break;
               }
            }
         }

         session.save();
         indexListener.updated(object);
      }
      catch (PathNotFoundException pe)
      {
         throw new InvalidArgumentException("Object " + object.getObjectId() + " is not filed in current folder.");
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable remove object from current folder. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   void delete() throws StorageException
   {
      if (isRoot())
      {
         throw new ConstraintException("Root folder can't be removed.");
      }
      if (hasChildren())
      {
         throw new ConstraintException("Failed delete object. Object " + getObjectId()
            + " is Folder and contains one or more objects.");
      }

      // Common delete.
      super.delete();
   }

}
