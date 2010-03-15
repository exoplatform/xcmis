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

import org.xcmis.search.index.TransactionLog;
import org.xcmis.search.index.TransactionLogException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: CompositeTransactionLog.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CompositeTransactionLog implements TransactionLog
{

   /**
    * TransactionLog list.
    */
   private final List<TransactionLog> logs;

   /**
    * Constructor.
    * 
    * @param logs - collection of Transaction Logs.
    * @throws TransactionLogException
    */
   public CompositeTransactionLog(final Collection<TransactionLog> logs) throws TransactionLogException
   {
      this.logs = new ArrayList<TransactionLog>();
      this.logs.addAll(logs);
   }

   /**
    * {@inheritDoc}
    * 
    * @throws TransactionLogException
    */
   public Set<String> getAddedList() throws TransactionLogException
   {
      final Set<String> added = new HashSet<String>();
      for (final TransactionLog log : this.logs)
      {
         added.addAll(log.getAddedList());
      }
      return added;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws TransactionLogException
    */
   public Set<String> getRemovedList() throws TransactionLogException
   {
      final Set<String> removed = new HashSet<String>();
      for (final TransactionLog log : this.logs)
      {
         removed.addAll(log.getRemovedList());
      }
      return removed;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws TransactionLogException
    */
   public Set<String> getUpdatedList() throws TransactionLogException
   {
      final Set<String> updated = new HashSet<String>();
      for (final TransactionLog log : this.logs)
      {
         updated.addAll(log.getUpdatedList());
      }
      return updated;
   }

   /**
    * {@inheritDoc}
    */
   public void load() throws TransactionLogException
   {
      for (final TransactionLog log : this.logs)
      {
         log.load();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void log() throws TransactionLogException
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("CompositeTransactionLog do not support log() method.");
   }

   /**
    * {@inheritDoc}
    */
   public void removeLog() throws TransactionLogException
   {
      for (final TransactionLog log : this.logs)
      {
         log.removeLog();
      }
   }

}
