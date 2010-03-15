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
package org.xcmis.search;

import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.IndexConfuguration;
import org.xcmis.search.index.IndexException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: FileBasedNamespaceAccessor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class FileBasedNamespaceAccessor implements NamespaceAccessor
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(FileBasedNamespaceAccessor.class);

   private final String NS_FILE = "ns.map";

   /**
    * Key prefix -> uri map.
    */
   private final ConcurrentHashMap<String, String> prefixToUriMap;

   /**
    * Namespace mapping storage.
    */
   private final File storage;

   /**
    * Key uri -> prefix map.
    */
   private final ConcurrentHashMap<String, String> uriToPrefixMap;

   /**
    * @param storage
    * @throws IOException
    */
   public FileBasedNamespaceAccessor(final File storage) throws IOException
   {
      super();
      this.storage = storage;
      this.prefixToUriMap = new ConcurrentHashMap<String, String>();
      this.uriToPrefixMap = new ConcurrentHashMap<String, String>();
      load();
   }

   /**
    * @param storage
    * @throws IOException
    * @throws IndexConfigurationException
    * @throws IndexException
    */
   public FileBasedNamespaceAccessor(final IndexConfuguration indexConfuguration) throws IOException,
      IndexConfigurationException, IndexException
   {
      final File indexDir = new File(indexConfuguration.getIndexDir());
      if (!indexDir.exists() && !indexDir.mkdirs())
      {
         throw new IndexConfigurationException("Fail to create index directory : " + indexDir.getAbsolutePath());
      }
      this.storage = new File(indexDir, this.NS_FILE);
      if (!this.storage.exists())
      {
         if (!this.storage.createNewFile())
         {
            throw new IndexConfigurationException("Fail to create file: " + this.storage.getAbsolutePath());
         }
      }
      this.prefixToUriMap = new ConcurrentHashMap<String, String>();
      this.uriToPrefixMap = new ConcurrentHashMap<String, String>();
      load();
   }

   /**
    * {@inheritDoc}
    */
   public String[] getAllNamespacePrefixes() throws RepositoryException
   {
      final Set<String> keys = this.prefixToUriMap.keySet();
      return keys.toArray(new String[keys.size()]);
   }

   /**
    * {@inheritDoc}
    */
   public String getNamespacePrefixByURI(final String uri) throws NamespaceException, RepositoryException
   {
      String prefix = this.uriToPrefixMap.get(uri);
      if (prefix == null)
      {
         synchronized (this)
         {
            // to reduce size of result names
            prefix = uri.length() == 0 ? "" : new Integer(this.uriToPrefixMap.size()).toString();
            this.prefixToUriMap.put(prefix, uri);
            this.uriToPrefixMap.put(uri, prefix);
            try
            {
               save();
            }
            catch (final IOException e)
            {
               throw new RepositoryException("Fail to save mapping " + this.storage.getAbsolutePath());
            }
         }
      }
      return prefix;
   }

   /**
    * {@inheritDoc}
    */
   public String getNamespaceURIByPrefix(final String prefix) throws NamespaceException, RepositoryException
   {
      final String uri = this.prefixToUriMap.get(prefix);
      if (uri == null)
      {
         throw new NamespaceException("URI for prefix " + prefix + " not found");
      }

      return uri;
   }

   /**
    * Loads currently known mappings from a .properties file.
    * 
    * @throws IOException if an error occurs while reading from the file.
    */
   private void load() throws IOException
   {
      if (this.storage.exists())
      {
         final InputStream in = new FileInputStream(this.storage);
         try
         {
            final Properties props = new Properties();
            this.log.debug("loading namespace mappings...");
            props.load(in);

            // read mappings from properties
            final Iterator iter = props.keySet().iterator();
            while (iter.hasNext())
            {
               final String prefix = (String)iter.next();
               // to reduce size of result names
               final String uri = prefix.length() == 0 ? "" : props.getProperty(prefix);
               this.log.debug(prefix + " -> " + uri);
               this.prefixToUriMap.put(prefix, uri);
               this.uriToPrefixMap.put(uri, prefix);
            }

            this.log.debug("namespace mappings loaded.");
         }
         finally
         {
            in.close();
         }
      }
   }

   /**
    * Writes the currently known mappings into a .properties file.
    * 
    * @throws IOException if an error occurs while writing the file.
    */
   private void save() throws IOException
   {
      final Properties props = new Properties();

      // store mappings in properties
      final Iterator<String> iter = this.prefixToUriMap.keySet().iterator();
      while (iter.hasNext())
      {
         final String prefix = iter.next();
         final String uri = this.prefixToUriMap.get(prefix);
         props.setProperty(prefix, uri);
      }

      OutputStream out = new FileOutputStream(this.storage);
      try
      {
         out = new BufferedOutputStream(out);
         props.store(out, null);
      }
      finally
      {
         // make sure stream is closed
         out.close();
      }
   }
}
