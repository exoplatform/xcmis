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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndexInfo.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class IndexInfo
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(IndexInfo.class);

   /**
    * For new segment names.
    */
   private int counter = 0;

   /**
    * Flag that indicates if index infos needs to be written to disk.
    */
   private boolean dirty = false;

   /**
    * List of index names
    */
   private List<String> indexes = new ArrayList<String>();

   /**
    * Set of names for quick lookup.
    */
   private Set<String> names = new HashSet<String>();

   /**
    * Name of the file where the infos are stored.
    */
   private final String name;

   /**
    * Creates a new IndexInfos using <code>fileName</code>.
    * 
    * @param fileName the name of the file where infos are stored.
    */
   public IndexInfo(String fileName)
   {
      this.name = fileName;
   }

   /**
    * Returns <code>true</code> if this index infos exists in <code>dir</code>.
    * 
    * @param dir the directory where to look for the index infos.
    * @return <code>true</code> if it exists; <code>false</code> otherwise.
    */
   public boolean exists(File dir)
   {
      return new File(dir, name).exists();
   }

   /**
    * Returns the name of the file where infos are stored.
    * 
    * @return the name of the file where infos are stored.
    */
   public String getFileName()
   {
      return name;
   }

   /**
    * Reads the index infos.
    * 
    * @param dir the directory from where to read the index infos.
    * @throws IOException if an error occurs.
    */
   public void read(File dir) throws IndexException
   {
      try
      {
         InputStream in = new FileInputStream(new File(dir, name));
         try
         {
            DataInputStream di = new DataInputStream(in);
            counter = di.readInt();
            for (int i = di.readInt(); i > 0; i--)
            {
               String indexName = di.readUTF();
               indexes.add(indexName);
               names.add(indexName);
            }
         }
         finally
         {
            in.close();
         }
      }
      catch (FileNotFoundException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Writes the index infos to disk if they are dirty.
    * 
    * @param dir the directory where to write the index infos.
    * @throws IOException if an error occurs.
    */
   public void write(File dir) throws IOException
   {
      // do not write if not dirty
      if (!dirty)
      {
         return;
      }

      File nu = new File(dir, name + ".new");
      OutputStream out = new FileOutputStream(nu);
      try
      {
         DataOutputStream dataOut = new DataOutputStream(out);
         dataOut.writeInt(counter);
         dataOut.writeInt(indexes.size());
         for (int i = 0; i < indexes.size(); i++)
         {
            dataOut.writeUTF(getName(i));
         }
      }
      finally
      {
         out.close();
      }
      // delete old
      File old = new File(dir, name);
      if (old.exists() && !old.delete())
      {
         throw new IOException("Unable to delete file: " + old.getAbsolutePath());
      }
      if (!nu.renameTo(old))
      {
         throw new IOException("Unable to rename file: " + nu.getAbsolutePath());
      }
      dirty = false;
   }

   /**
    * Returns the index name at position <code>i</code>.
    * 
    * @param i the position.
    * @return the index name.
    */
   public String getName(int i)
   {
      return indexes.get(i);
   }

   /**
    * Returns the number of index names.
    * 
    * @return the number of index names.
    */
   public int size()
   {
      return indexes.size();
   }

   /**
    * Adds a name to the index infos.
    * 
    * @param name the name to add.
    */
   public void addName(String indexName)
   {
      if (names.contains(indexName))
      {
         throw new IllegalArgumentException("already contains: " + indexName);
      }
      indexes.add(indexName);
      names.add(indexName);
      dirty = true;
   }

   /**
    * Removes the name from the index infos.
    * 
    * @param name the name to remove.
    */
   public void removeName(String indexName)
   {
      indexes.remove(indexName);
      names.remove(indexName);
      dirty = true;
   }

   /**
    * Removes the name from the index infos.
    * 
    * @param i the position.
    */
   public void removeName(int i)
   {
      String indexName = indexes.remove(i);
      names.remove(indexName);
      dirty = true;
   }

   /**
    * Returns <code>true</code> if <code>name</code> exists in this
    * <code>IndexInfos</code>; <code>false</code> otherwise.
    * 
    * @param name the name to test existence.
    * @return <code>true</code> it is exists in this <code>IndexInfos</code>.
    */
   public boolean contains(String indexName)
   {
      return names.contains(indexName);
   }

   /**
    * Returns a new unique name for an index folder.
    * 
    * @return a new unique name for an index folder.
    */
   public String newName()
   {
      dirty = true;
      return "_" + Integer.toString(counter++, Character.MAX_RADIX);
   }
}
