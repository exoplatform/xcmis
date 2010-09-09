/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.xcmis.spi.utils.Logger;

public abstract class LuceneIndexDataKeeperFactory implements IndexDataKeeperFactory<Document, Analyzer, IndexReader>
{
   /**
    * Class logger.
    */
   private static final Logger LOG = Logger.getLogger(LuceneIndexDataKeeperFactory.class);

   /**
    * 
    */
   public LuceneIndexDataKeeperFactory()
   {
      super();
   }

   /**
    * {@inheritDoc}
    */
   public void dispose(final IndexDataKeeper<Document> indexDataKeeper) throws IndexException
   {
      indexDataKeeper.stop();
   }
}
