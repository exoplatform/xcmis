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

import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndexTransaction.java 2 2010-02-04 17:21:49Z andrew00x $
 * @param <D> Document class.
 */
public interface IndexTransaction<D>
{
   /**
    * Remove modifications f
    * 
    * @param report
    */
   IndexTransaction<D> apply(final IndexTransactionModificationReport report);

   /**
    * Map of documents what should be added to index.
    * 
    * @return map of documents.
    */
   Map<String, D> getAddedDocuments();

   /**
    * @return size of transaction in bytes
    */
   long getAddedDocumentSizeInBytes();

   /**
    * Set of identifiers of documents what should be removed from index.
    * 
    * @return set of identifiers.
    */
   Set<String> getRemovedDocuments();

   /**
    * @return true if where is AddedDocuments
    */
   boolean hasAddedDocuments();

   /**
    * @return true if where is UpdatedDocuments or RemovedDocuments
    */
   boolean hasModifacationsDocuments();
}
