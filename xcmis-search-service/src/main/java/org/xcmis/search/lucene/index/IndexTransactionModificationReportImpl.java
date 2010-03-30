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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndexTransactionModificationReportImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class IndexTransactionModificationReportImpl implements IndexTransactionModificationReport
{
   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(IndexTransactionModificationReportImpl.class);

   /**
    * Set of uuid of successfully added documents
    */
   private final Set<String> addedDocuments;

   /**
    * Set of uuid of successfully removed documents
    */
   private final Set<String> removedDocuments;

   /**
    * Set of uuid of successfully added documents
    */
   private final Set<String> updatedDocuments;

   /**
    * @param addedDocuments
    * @param removedDocuments
    * @param updatedDocuments
    */
   public IndexTransactionModificationReportImpl(Set<String> addedDocuments, Set<String> removedDocuments,
      Set<String> updatedDocuments)
   {
      super();
      this.addedDocuments = addedDocuments;
      this.removedDocuments = removedDocuments;
      this.updatedDocuments = updatedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getAddedDocuments()
   {
      return addedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getRemovedDocuments()
   {
      return removedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getUpdatedDocuments()
   {
      return updatedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isModifed()
   {
      return addedDocuments.size() > 0 || removedDocuments.size() > 0 || updatedDocuments.size() > 0;
   }
}
