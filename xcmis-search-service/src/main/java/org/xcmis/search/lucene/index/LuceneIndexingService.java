/**
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.xcmis.search.lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.lucene.IndexRecoveryTool;
import org.xcmis.spi.utils.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LuceneIndexingService extends TransactionableIndexDataManager
{

   /**
    * @param indexConfuguration
    * @param indexRecoveryTool
    * @throws IndexException
    * @throws IndexConfigurationException
    */
   public LuceneIndexingService(IndexConfiguration indexConfuguration, IndexRecoveryTool indexRecoveryTool)
      throws IndexException, IndexConfigurationException
   {
      super(indexConfuguration, indexRecoveryTool);
   }

   /** Class logger. */
   private static final Logger LOG = Logger.getLogger(LuceneIndexingService.class);

   /**
    * {@inheritDoc}
    */
   public boolean documentExists(final String uuid)
   {
      try
      {
         return super.getDocument(uuid) != null;
      }
      catch (IndexException e)
      {
      }
      return false;

   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getFieldNames() throws IndexException
   {
      final Set<String> fildsSet = new HashSet<String>();
      @SuppressWarnings("unchecked")
      final Collection fields = super.getIndexReader().getFieldNames(IndexReader.FieldOption.ALL);
      for (final Object field : fields)
      {
         fildsSet.add((String)field);
      }
      return fildsSet;
   }

   protected void softCleanIndex() throws IndexException
   {
      if (getDocumentCount() > 0)
      {
         final Directory dir = getDirectory();
         if (dir != null)
         {
            synchronized (dir)
            {
               try
               {
                  final IndexWriter writer =
                     new IndexWriter(super.getDirectory(), new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
                  writer.deleteDocuments(new MatchAllDocsQuery());
                  writer.commit();
                  writer.optimize();
                  writer.close();
               }
               catch (final CorruptIndexException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
               catch (final LockObtainFailedException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
               catch (final IOException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
            }
         }
      }
   }

}
