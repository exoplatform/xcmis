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
package org.xcmis.search.lucene.index;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.Property;
import org.xcmis.search.content.Property.BinaryValue;
import org.xcmis.search.value.PropertyType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Testing Tika based lucene indexer.
 */
public class LuceneIndexerTest
{
   private IndexConfiguration indexConfiguration;

   private LuceneIndexer nodeIndex;

   @Before
   public void beforeEach()
   {
      indexConfiguration = new IndexConfiguration();
      indexConfiguration.setRootParentUuid(UUID.randomUUID().toString());
      indexConfiguration.setRootUuid(UUID.randomUUID().toString());
      nodeIndex = new LuceneIndexer(new Tika(), indexConfiguration);
   }

   /**
    * Test autodetecting of content type in doc file
    * 
    * @throws IOException
    */
   @Test
   public void testReaderExtractingFromDoc() throws IOException
   {
      assertIndexed("../../../../../testEn.doc", "application/msword", "dolor");
   }

   /**
    * Test autodetecting of content type in pdf file
    * 
    * @throws IOException
    */
   @Test
   public void testReaderExtractingFromFileEn() throws IOException
   {

      assertIndexed("../../../../../testEn.pdf", "application/pdf", "dolor");
   }

   /**
    * Test content type in html file
    * 
    * @throws IOException
    */
   @Test
   public void testReaderExtractingFromHtml() throws IOException
   {
      assertIndexed("../../../../../testEn.html", "text/html", "paragraph");
   }

   /**
    * Test autodetecting of content type in odt file
    * 
    * @throws IOException
    */
   @Test
   public void testReaderExtractingFromOdt() throws IOException
   {
         assertIndexed("../../../../../testEn.odt", "application/vnd.oasis.opendocument.text", "dolor");
   }

   /**
    * Test extracting reader from file with English text and in UTF-8 encoding.
    * 
    * @throws IOException
    */
   @Test
   public void testReaderExtractingFromTxt() throws IOException
   {
      assertIndexed("../../../../../testEnUtf-8.txt", "text/plain", "dn8dolor");
   }

   private void assertIndexed(String filePath, String contentType, String testString) throws CorruptIndexException, LockObtainFailedException, IOException
   {
      //prepare content entry
      Property<BinaryValue>[] property = new Property[1];
      byte[] buf = IOUtils.toByteArray(this.getClass().getResourceAsStream(filePath));
      BinaryValue binaryValue = new BinaryValue(new ByteArrayInputStream(buf), contentType, null, -1);
      property[0] = new Property(PropertyType.BINARY, "content", binaryValue);

      ContentEntry contentEntry =
         new ContentEntry("test", new String[]{"testTable"}, UUID.randomUUID().toString(),
            new String[]{indexConfiguration.getRootUuid()}, property);
      //create lucene document
      Document document = nodeIndex.createDocument(contentEntry);

      Assert.assertNotNull(document);
      RAMDirectory directory = new RAMDirectory();

         //write document to th
         IndexWriter writer = new IndexWriter(directory, new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
         writer.addDocument(document);
         writer.commit();
         writer.close();
         IndexSearcher searcher = new IndexSearcher(directory);
         TopDocs docs =
            searcher.search(new TermQuery(new Term(FieldNames.createFullTextFieldName("content"), testString)), 10);
         Assert.assertEquals(1, docs.totalHits);
    

   }
}
