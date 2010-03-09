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

import org.xcmis.search.lucene.TransactionLogException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by The eXo Platform SAS. <br/>Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: ReentrantDirectoryFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ReentrantDirectoryFactory implements ConcurrentDirectoryFactory
{

   /**
    * Map of Directory path and Lock.
    */
   private final ConcurrentHashMap<String, Lock> locks;

   /**
    * Constructor.
    */
   public ReentrantDirectoryFactory()
   {
      locks = new ConcurrentHashMap<String, Lock>();
   }

   /**
    * {@inheritDoc}
    */
   public File createFile(File dir, String fileName, int dirCount) throws IOException, TransactionLogException
   {

      File file = new File(dir, fileName);
      if (file.exists())
         throw new TransactionLogException("File already exist " + file.getAbsolutePath());

      // make array of parent directories
      File[] dirs = new File[dirCount];

      File d = dir;
      for (int i = 0; i < dirCount; i++)
      {
         if (d != null)
         {
            dirs[dirCount - 1 - i] = d;
            d = d.getParentFile();
         }
         else
         {
            throw new NullPointerException("Parent directory is null.");
         }
      }

      if (!d.exists())
      {
         throw new TransactionLogException("Storage " + d.getAbsolutePath() + " not exist. ");
      }

      // make all parent directories and create file on finish
      return createFile(dirs, fileName, 0);
   }

   /**
    * Create file with <code>fileName</code>.
    * 
    * @param dirs list of parent directories
    * @param fileName - file name
    * @param currentIndex - index of current directory in list
    * @return created file
    * @throws IOException - I/O exception
    * @throws TransactionLogException - if parent directory was not created
    */
   private File createFile(File[] dirs, String fileName, int currentIndex) throws IOException, TransactionLogException
   {

      if (currentIndex < dirs.length)
      {
         File dir = dirs[currentIndex];

         lock(dir.getAbsolutePath());

         try
         {
            // create directory
            if (!dir.exists())
            {
               if (!dir.mkdir())
               {
                  throw new TransactionLogException(" Directory " + dir.getAbsolutePath() + " was not created. ");
               }
            }

            return createFile(dirs, fileName, currentIndex + 1);

         }
         finally
         {
            unlock(dir.getAbsolutePath());
         }
      }
      else
      {
         // there is no more parent directories - make a file
         File file = new File(dirs[dirs.length - 1], fileName);
         file.createNewFile();
         return file;
      }
   }

   /**
    * Returns Lock according to directory. Creates new lock if map do not
    * contains record for directory path.
    * 
    * @param dir - directory
    * @return Lock
    */
   private Lock getLockByPath(String dir)
   {
      Lock l = locks.get(dir);
      if (l == null)
      {
         Lock lock = new ReentrantLock();
         l = locks.putIfAbsent(dir, lock);
         if (l == null)
         {
            l = lock;
         }
      }
      return l;
   }

   /**
    * {@inheritDoc}
    */
   public void lock(String dir)
   {
      Lock l = getLockByPath(dir);
      l.lock();
   }

   /**
    * {@inheritDoc}
    */
   public void lockRead(String dir)
   {
      throw new UnsupportedOperationException("Reentrant lock implementation do   not supports read locks");
   }

   /**
    * {@inheritDoc}
    */
   public void removeDirectory(File dir, int dirCount) throws TransactionLogException
   {
      // try to delete parents
      if (dirCount > 0)
      {
         String dirPath = dir.getAbsolutePath();

         if (tryLock(dirPath))
         {
            try
            {
               if (dir.exists() && dir.list().length == 0)
               {

                  File parent = dir.getParentFile();

                  if (!dir.delete())
                  {
                     throw new TransactionLogException("Directory was not removed " + dir.getAbsolutePath());
                  }

                  removeDirectory(parent, dirCount - 1);
               }

            }
            finally
            {
               unlock(dirPath);
            }
         }
         else
         {
            // stop remove
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean tryLock(String dir)
   {
      Lock l = getLockByPath(dir);
      return l.tryLock();
   }

   /**
    * {@inheritDoc}
    */
   public void unlock(String dir)
   {
      Lock l = getLockByPath(dir);
      l.unlock();
   }

   /**
    * {@inheritDoc}
    */
   public void unlockRead(String dir)
   {
      throw new UnsupportedOperationException("Reentrant lock implementation do not supports read locks");
   }
}
