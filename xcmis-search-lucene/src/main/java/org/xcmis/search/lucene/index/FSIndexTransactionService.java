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

import org.xcmis.search.index.IndexTransactionService;
import org.xcmis.search.index.TransactionLog;
import org.xcmis.search.index.TransactionLogException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id$
 */
public class FSIndexTransactionService implements IndexTransactionService
{

   private final ConcurrentDirectoryFactory lockFactory;

   private final File storageDir;

   public FSIndexTransactionService(final File storageDir, final ConcurrentDirectoryFactory lockFactory)
   {
      this.storageDir = storageDir;
      this.lockFactory = lockFactory;
   }

   public final ConcurrentDirectoryFactory getLockFactory()
   {
      return lockFactory;
   }

   public final File getStorageDir()
   {
      return storageDir;
   }

   public List<TransactionLog> getTransactionLogs() throws TransactionLogException
   {
      final List<File> logFileList = this.getFileList(storageDir);
      final List<TransactionLog> logs = new ArrayList<TransactionLog>();
      for (final File file : logFileList)
      {
         final TransactionLog log = new FileSystemTransactionLog(file, this);
         logs.add(log);
      }
      return logs;
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasUncommitedTransactions()
   {
      return this.hasFiles(storageDir);
   }

   /**
    * Return file list from storage.
    * 
    * @param dir - storage of files
    * @return Complete list of storage files.Or empty list if there is no files.
    */
   protected List<File> getFileList(final File dir)
   {
      final List<File> resList = new ArrayList<File>();

      final File[] list = dir.listFiles();

      for (final File file : list)
      {
         if (file.isFile())
         {
            resList.add(file);
         }
         else
         {
            resList.addAll(this.getFileList(file));
         }
      }
      return resList;
   }

   /**
    * Check directory for internal files.
    * 
    * @param dir - directory to check
    * @return <code>true</code> if directory has files, <code>false</code> in
    *         other case
    */
   private boolean hasFiles(final File dir)
   {
      if (dir.exists())
      {
         final File[] list = dir.listFiles();

         for (final File file : list)
         {
            if (file.isFile())
            {
               return true;
            }
            else
            {
               if (this.hasFiles(file))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }
}
