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

import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.TypeDefinition;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * Iterator over JCR NodeIterator. Iterator skips nodes unsupported by CMIS.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FolderChildrenIterator.java 426 2010-03-22 11:50:27Z andrew00x
 *          $
 */
class FolderChildrenIterator implements ItemsIterator<ObjectData>
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(FolderChildrenIterator.class);

   static Set<String> skipItems = new HashSet<String>();

   static
   {
      skipItems.add("jcr:system");
      skipItems.add("xcmis:system");
   }

   /** JCR node iterator. */
   protected final NodeIterator iter;

   /** Next CMIS item instance. */
   protected ObjectData next;

   private final IndexListener indexListener;

   /**
    * @param iter back-end NodeIterator
    */
   public FolderChildrenIterator(NodeIterator iter, IndexListener indexListener)
   {
      this.iter = iter;
      this.indexListener = indexListener;
      fetchNext();
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return next != null;
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData next()
   {
      if (next == null)
      {
         throw new NoSuchElementException();
      }
      ObjectData n = next;
      fetchNext();
      return n;
   }

   /**
    * {@inheritDoc}
    */
   public void remove()
   {
      throw new UnsupportedOperationException("remove");
   }

   /**
    * {@inheritDoc}
    */
   public int size()
   {
      return -1 /*iter.getSize()*/;
   }

   /**
    * {@inheritDoc}
    */
   public void skip(int skip) throws NoSuchElementException
   {
      while (skip-- > 0)
      {
         fetchNext();
         if (next == null)
         {
            throw new NoSuchElementException();
         }
      }
   }

   /**
    * To fetch next item.
    */
   protected void fetchNext()
   {
      next = null;
      while (next == null && iter.hasNext())
      {
         Node node = iter.nextNode();
         try
         {
            if (skipItems.contains(node.getName()))
            {
               continue;
            }

            if (!((NodeImpl)node).isValid())
            {
               continue; // TODO temporary
            }

            if (node.isNodeType("nt:linkedFile"))
            {
               node = node.getProperty("jcr:content").getNode();
            }

            TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);

            if (type.getBaseId() == BaseType.DOCUMENT)
            {
               if (!node.isNodeType(JcrCMIS.CMIS_MIX_DOCUMENT))
               {
                  next = new JcrFile(type, node, null, indexListener);
               }
               else
               {
                  next = new DocumentDataImpl(type, node, indexListener);
               }
            }
            else if (type.getBaseId() == BaseType.FOLDER)
            {
               if (!node.isNodeType(JcrCMIS.CMIS_MIX_FOLDER))
               {
                  next = new JcrFolder(type, node, indexListener);
               }
               else
               {
                  next = new FolderDataImpl(type, node, indexListener);
               }
            }

         }
         catch (NotSupportedNodeTypeException iae)
         {
            if (LOG.isDebugEnabled())
            {
               // Show only in debug mode. It may cause a lot of warn when
               // unsupported by xCMIS nodes met.
               LOG.warn("Unable get next object . " + iae.getMessage());
            }
         }
         catch (javax.jcr.RepositoryException re)
         {
            LOG.warn("Unexpected error. Failed get next CMIS object. " + re.getMessage());
         }
      }
   }

}
