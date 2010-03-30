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
package org.xcmis.sp.jcr.exo.index;

import org.apache.lucene.document.Document;
import org.xcmis.search.lucene.index.IndexDataKeeper;
import org.xcmis.search.lucene.index.IndexRestoreService;
import org.xcmis.spi.Storage;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisRestoreService implements IndexRestoreService
{
   private final Storage storage;

   /**
    * @param storage
    */
   public CmisRestoreService(Storage storage)
   {
      super();
      this.storage = storage;
   }

   /**
    * @see org.xcmis.search.lucene.index.IndexRestoreService#restoreIndex(org.xcmis.search.lucene.index.IndexDataKeeper)
    */
   public void restoreIndex(IndexDataKeeper<Document> indexDataKeeper)
   {

   }

}
