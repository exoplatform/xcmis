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

import org.xcmis.core.CmisRenditionType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.object.ItemsIterator;

import java.math.BigInteger;
import java.util.NoSuchElementException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;

/**
 * Iterator over set of object's renditions.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RenditionIterator.java 281 2010-03-05 12:16:10Z ur3cma $
 */
class RenditionIterator implements ItemsIterator<CmisRenditionType>
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RenditionIterator.class);

   /** Source NodeIterator. */
   protected final NodeIterator iter;

   /** Next rendition. */
   protected CmisRenditionType next;

   /**
    * Create RenditionIterator instance.
    * 
    * @param iter the node iterator
    */
   public RenditionIterator(NodeIterator iter)
   {
      this.iter = iter;
      fetchNext();
   }
   
   /**
    * Create RenditionIterator instance with elready defined element.
    * 
    * @param iter the node iterator
    */
   public RenditionIterator (CmisRenditionType type){
      this.iter = null;
      this.next = type;
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
   public CmisRenditionType next()
   {
      if (next == null)
         throw new NoSuchElementException();
      CmisRenditionType n = next;
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
   public long size()
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public void skip(long skip) throws NoSuchElementException
   {
      while (skip-- > 0)
         iter.next();
   }

   /**
    * Fetching next renditions. If during iteration rendition that is unsupported
    * by <code>renditionFilter</code> is met than this method will trying to find
    * another one acceptable by filter.
    */
   protected void fetchNext()
   {
      next = null;
      if (iter == null) // iter can be null if rendition is created in runtime and not stored in JCR
         return;
      while (next == null && iter.hasNext())
      {
         Node node = iter.nextNode();
         try
         {
            if (node.isNodeType(JcrCMIS.CMIS_NT_RENDITION))
            {
               CmisRenditionType rendition = new CmisRenditionType();
               rendition.setStreamId(node.getName());
               rendition.setKind(node.getProperty(JcrCMIS.CMIS_RENDITION_KIND).getString());
               rendition.setMimetype(node.getProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE).getString());
               rendition.setLength(BigInteger.valueOf(node.getProperty(JcrCMIS.CMIS_RENDITION_STREAM).getLength()));
               try
               {
                  rendition.setHeight(BigInteger.valueOf(node.getProperty(JcrCMIS.CMIS_RENDITION_HEIGHT).getLong()));
                  rendition.setWidth(BigInteger.valueOf(node.getProperty(JcrCMIS.CMIS_RENDITION_WIDTH).getLong()));
               }
               catch (PathNotFoundException pnfe)
               {
                  // Height & Width is optional 
               }
               next = rendition;
            }
         }
         catch (javax.jcr.RepositoryException re)
         {
            String msg = "Unexpected error. Failed get next CMIS object. " + re.getMessage();
            LOG.warn(msg);
         }
      }
   }

}
