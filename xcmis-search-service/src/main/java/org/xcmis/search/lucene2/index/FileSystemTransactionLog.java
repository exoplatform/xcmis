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
package org.xcmis.search.lucene2.index;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.IndexConstants;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: FileSystemTransactionLog.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class FileSystemTransactionLog implements TransactionLog
{

   public static final int LOG_DIR_COUNT = 2;

   private static final int ADDED_DOCUMENTS = 1;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(FileSystemTransactionLog.class);

   private static final int REMOVED_DOCUMENTS = 3;

   private static final int UPDATED_DOCUMENTS = 2;

   private static final int UUID_SIZE = 32;

   private Set<String> addedDocumentsUuids; // =

   private File file;

   private Set<String> removedDocumentsUuids; // =

   private final FSIndexTransactionService storage;

   /**
    * Constructor for recovery case.
    * 
    * @param srcFile - source file.
    * @param storage - transaction storage.
    */
   public FileSystemTransactionLog(final File srcFile, final FSIndexTransactionService storage)
      throws TransactionLogException
   {
      this.storage = storage;
      this.file = srcFile;
      if (!this.file.exists())
      {
         throw new TransactionLogException("File not exist " + this.file.getAbsolutePath());
      }
      // this.load();
   }

   /**
    * Constructor for ordinary case.
    * 
    * @param indexTransaction - IndexTransaction
    * @param storage - transaction storage.
    * @param rootDir - Root directory file.
    */
   public FileSystemTransactionLog(final Set<String> addedDocumentsUuids, final Set<String> removedDocumentsUuids,
      final FSIndexTransactionService storage)
   {
      this.addedDocumentsUuids = addedDocumentsUuids;
      this.removedDocumentsUuids = removedDocumentsUuids;
      this.storage = storage;

   }

   /**
    * {@inheritDoc}
    * 
    * @throws TransactionLogException
    */
   public Set<String> getAddedList() throws TransactionLogException
   {
      if (this.addedDocumentsUuids == null)
      {
         this.load();
      }
      return this.addedDocumentsUuids;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws TransactionLogException
    */
   public Set<String> getRemovedList() throws TransactionLogException
   {
      if (this.removedDocumentsUuids == null)
      {
         this.load();
      }
      return this.removedDocumentsUuids;
   }

   /**
    * {@inheritDoc}
    */
   public void load() throws TransactionLogException
   {
      this.addedDocumentsUuids = new HashSet<String>();

      this.removedDocumentsUuids = new HashSet<String>();
      FileInputStream in = null;
      try
      {
         in = new FileInputStream(this.file);
         int type;
         while ((type = in.read()) != -1)
         {
            final byte[] buf = new byte[FileSystemTransactionLog.UUID_SIZE];
            if (in.read(buf) != FileSystemTransactionLog.UUID_SIZE)
            {
               throw new TransactionLogException(" TransactionLog file is corrupted. Unexpected end of file: "
                  + this.file.getAbsolutePath());
            }
            final String uuid = new String(buf, IndexConstants.DEFAULT_ENCODING);
            switch (type)
            {
               case ADDED_DOCUMENTS :
                  this.addedDocumentsUuids.add(uuid);
                  break;
               case REMOVED_DOCUMENTS :
                  this.removedDocumentsUuids.add(uuid);
                  break;
               default :

                  throw new TransactionLogException(" TransactionLog file is corrupted. Unexpected type of record "
                     + type + ". file : " + this.file.getAbsolutePath());
            }
         }
      }
      catch (final EOFException e)
      {
         throw new TransactionLogException(" TransactionLog file is uncomplete: " + this.file.getAbsolutePath());

      }
      catch (final IOException e)
      {
         throw new TransactionLogException("TransactionLog read exception: " + e.getMessage(), e);
      }
      finally
      {
         if (in != null)
         {
            try
            {
               in.close();
            }
            catch (final IOException e)
            {
               FileSystemTransactionLog.LOG.warn("Can not close log file " + this.file.getAbsolutePath());
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void log() throws TransactionLogException
   {
      OutputStream out = null;
      try
      {
         //TODO Check uuid
         final String fileName = UUID.randomUUID().toString();

         final File dir =
            new File(this.storage.getStorageDir().getAbsoluteFile()
               + this.buildPathFromName(fileName, FileSystemTransactionLog.LOG_DIR_COUNT));

         this.file = this.storage.getLockFactory().createFile(dir, fileName, FileSystemTransactionLog.LOG_DIR_COUNT);

         out = new BufferedOutputStream(new FileOutputStream(this.file));

         this.writeSet(out, this.addedDocumentsUuids, FileSystemTransactionLog.ADDED_DOCUMENTS);
         this.writeSet(out, this.removedDocumentsUuids, FileSystemTransactionLog.REMOVED_DOCUMENTS);
         out.flush();
      }
      catch (final IOException e)
      {
         throw new TransactionLogException("TransactionLog write exception: " + e.getMessage(), e);
      }
      finally
      {
         if (out != null)
         {
            try
            {
               out.close();
            }
            catch (final IOException e)
            {
               FileSystemTransactionLog.LOG.warn("Can not close log file" + this.file.getAbsolutePath());
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void removeLog() throws TransactionLogException
   {
      if (!this.file.exists())
      {
         FileSystemTransactionLog.LOG.warn("TransactionLog file not exist. " + this.file.getAbsolutePath());
      }
      if (!this.removeLogFile(this.file))
      {
         throw new TransactionLogException("TransactionLog file was not delete. " + this.file.getAbsolutePath());
      }
      else
      {
         if (FileSystemTransactionLog.LOG.isDebugEnabled())
         {
            FileSystemTransactionLog.LOG.debug("Transaction log removed " + this.file.getAbsolutePath());
         }
      }
   }

   /**
    * Create path to file using its name.
    * 
    * @param fileName - fileName as a source to generated path
    * @param dirCount - parent dirs count
    * @return String - generated path to file.
    */
   private String buildPathFromName(final String fileName, final int dirCount)
   {
      final char[] chs = fileName.toCharArray();
      String path = "";
      for (int i = 1; i <= dirCount; i++)
      {
         path += File.separator + chs[chs.length - i];
      }
      return path;
   }

   /**
    * Remove log file. Depends from creation file algorithm. See builfPathX2C1
    * method.
    * 
    * @param file to remove.
    * @return <code>true</code> if remove successfull.
    */
   private boolean removeLogFile(final File file) throws TransactionLogException
   {
      final File dir = file.getParentFile();
      if (!file.delete())
      {
         return false;
      }
      else
      {
         // try to delete parents
         this.storage.getLockFactory().removeDirectory(dir, FileSystemTransactionLog.LOG_DIR_COUNT);
         return true;

      }
   }

   /**
    * Write set of string to out with flag.
    * 
    * @param out - OutputStream
    * @param keys - Set of String to Store
    * @param flag - ADDED_DOCUMENTS, UPDATED_DOCUMENTS or REMOVED_DOCUMENTS
    * @throws IOException if write to file IOException occurs.
    */
   private void writeSet(final OutputStream out, final Set<String> keys, final int flag) throws IOException
   {
      for (final String key : keys)
      {
         out.write(flag);
         out.write(key.getBytes(IndexConstants.DEFAULT_ENCODING));
      }
   }

}
