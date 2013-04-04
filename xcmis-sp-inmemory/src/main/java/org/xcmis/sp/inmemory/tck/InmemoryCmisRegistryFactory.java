/**
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

package org.xcmis.sp.inmemory.tck;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.tika.exception.TikaException;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.CmisRegistryFactory;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.utils.Logger;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: InmemoryCmisRegistryFactory.java 1500 2010-08-05 09:00:58Z
 *          andrew00x $
 */
public class InmemoryCmisRegistryFactory implements CmisRegistryFactory
{

   private static final Logger LOG = Logger.getLogger(InmemoryCmisRegistryFactory.class);

   private CmisRegistry reg = new CmisRegistry();

   public InmemoryCmisRegistryFactory() throws TikaException
   {
      ClassLoader cl = null;
      try
      {
         cl = Thread.currentThread().getContextClassLoader();
      }
      catch (Exception e)
      {
         LOG.error("Unable get context class loader. " + e.getMessage());
      }

      boolean fconfig = false;
      if (cl != null)
      {
         InputStream in = cl.getResourceAsStream("xcmis-storage.properties");
         Properties properties = new Properties();
         if (in != null)
         {
            try
            {
               properties.load(new BufferedInputStream(in));
               fconfig = true;
            }
            catch (IOException ioe)
            {
               LOG.error(ioe.getMessage(), ioe);
            }
         }

         String renditionProvider = (String)properties.get("org.xcmis.storage.renditionProvider");
         if (renditionProvider != null)
         {
            RenditionManager renditionManager = RenditionManager.getInstance();
            renditionManager.addRenditionProviders(Arrays.asList(renditionProvider.split(",")));
         }

         String sids = (String)properties.get("org.xcmis.storage.id");
         if (sids != null)
         {
            for (String s : sids.split(","))
            {
               String id = s.trim();
               if (id.length() > 0)
               {
                  String name = (String)properties.get("org.xcmis.storage." + id + ".name");
                  String description = (String)properties.get("org.xcmis.storage." + id + ".description");
                  String sMaxItemsNum = (String)properties.get("org.xcmis.storage." + id + ".maxItemsNum");
                  long maxItemsNum = -1;
                  if (sMaxItemsNum != null && sMaxItemsNum.length() > 0)
                  {
                     try
                     {
                        maxItemsNum = Long.parseLong(sMaxItemsNum);
                     }
                     catch (NumberFormatException ne)
                     {
                        LOG.error("Unable convert '" + sMaxItemsNum + "' to long. ");
                     }
                  }
                  String sMaxMem = (String)properties.get("org.xcmis.storage." + id + ".maxMem");
                  long maxMem = -1;
                  if (sMaxMem != null && sMaxMem.length() > 0)
                  {
                     try
                     {
                        maxMem = Long.parseLong(sMaxMem);
                     }
                     catch (NumberFormatException ne)
                     {
                        LOG.error("Unable convert '" + sMaxMem + "' to long. ");
                     }
                  }
                  reg.addStorage(new org.xcmis.sp.inmemory.StorageProviderImpl(id, //
                     name != null && name.length() > 0 ? name : id, //
                     description != null && description.length() > 0 ? description : id, //
                     maxMem, //
                     maxItemsNum));
                  LOG.info("Register storage " + id);
               }
            }
         }
      }
      if (!fconfig)
      {
         // Default
         reg.addStorage(new org.xcmis.sp.inmemory.StorageProviderImpl("cmis1", "cmis1", "", -1L, -1L));
      }
   }

   public CmisRegistry getRegistry()
   {
      return reg;
   }

}
