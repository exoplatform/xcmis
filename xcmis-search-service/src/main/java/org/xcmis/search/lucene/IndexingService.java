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
package org.xcmis.search.lucene;

import org.apache.lucene.document.Document;
import org.xcmis.search.query.QueryBuilder;

import java.io.IOException;
import java.util.Set;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public interface IndexingService
{
   /**
    * @return
    * @throws IOException
    * @throws IndexException
    */
   public Set<String> getFieldNames() throws IndexException;

   /**
    * Returns a <code>QueryBuilder</code> with which a JCR-JQOM query
    * can be built QueryBuilderQueryBuilder.
    * 
    * @return a <code>QueryObjectModelFactory</code> object
    * @since JCR 2.0
    */
   public abstract QueryBuilder getQOMFactory();

   /**
    * Return true if document with uuid exists in the index storage.
    * 
    * @param absPath
    * @return
    */
   boolean documentExists(String uuid);

   /**
    * Saves the IndexTransaction to this storage
    * 
    * @param changes
    * @throws IndexException
    */
   IndexTransactionModificationReport save(IndexTransaction<Document> changes) throws IndexException;
}
