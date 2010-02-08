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

package org.xcmis.sp.jcr.exo.object;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * Iterator over JCR NodeIterator. Iterator skips nodes unsupported by CMIS.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FolderChildrenIterator implements ItemsIterator<Entry>
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(FolderChildrenIterator.class.getName());

   /** JCR node iterator. */
   protected final NodeIterator iter;

   /** Next CMIS item instance. */
   protected Entry next;
   
   static Set<String> skipItems = new HashSet<String>();
   
   static
   {
      skipItems.add("jcr:system");
      skipItems.add("cmis:system");
   }

   /**
    * @param iter back-end NodeIterator
    */
   public FolderChildrenIterator(NodeIterator iter)
   {
      this.iter = iter;
      fetchNext();
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
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return next != null;
   }

   /**
    * {@inheritDoc}
    */
   public Entry next()
   {
      if (next == null)
         throw new NoSuchElementException();
      Entry n = next;
      fetchNext();
      return n;
   }

   /**
    * To fetch next <code>Entry</code>.
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
               continue;
//            Entry e = new EntryImpl(node);
//            if (e.isLatest())
            next = new EntryImpl(node);
         }
         catch (InvalidArgumentException iae)
         {
            if (LOG.isDebugEnabled())
            {
               // May be a lot of warn when unsupported by CMIS nodes met.
               String msg = "Unable get next object . " + iae.getMessage();
               LOG.warn(msg);
            }
         }
         catch (javax.jcr.RepositoryException re)
         {
            String msg = "Unexpected error. Failed get next CMIS object. " + re.getMessage();
            LOG.warn(msg);
         }
      }
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
      return iter.getSize();
   }

}
