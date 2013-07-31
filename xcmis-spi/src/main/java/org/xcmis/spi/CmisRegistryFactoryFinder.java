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

package org.xcmis.spi;

import org.xcmis.spi.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Find implementation of CmisRegistryFactory Class.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: CmisRegistryFactoryFinder.java 1677 2010-09-09 10:47:27Z ur3cma
 *          $
 */
class CmisRegistryFactoryFinder
{
   /** Logger. */
   private static final Logger LOG = Logger.getLogger(CmisRegistryFactoryFinder.class);

   /**
    * Find implementation of CmisRegistryFactory Class. Lookup done in next
    * order:
    * <ul>
    * <li>Check system property with name 'org.xcmis.CmisRegistryFactory'. If it
    * set then try lookup and instantiate class with the name as property value.
    * </li>
    * <li>Lookup resource
    * 'META-INF/services/xcmis/org.xcmis.CmisRegistryFactory'. Try read
    * implementation class name from resource if it was found. Lookup and
    * instantiate class with delivered name.</li>
    * </ul>
    *
    * @return CmisRegistryFactory implementation or <code>null</code> if it
    *         can't be resolved
    */
   public static CmisRegistryFactory findCmisRegistry()
   {
      String name = null;
      try
      {
         name = System.getProperty(CmisRegistry.XCMIS_REGISTRY_FACTORY);
         if (name != null)
         {
            return createInstance(null, name);
         }
      }
      catch (SecurityException se)
      {
          if (LOG.isDebugEnabled())
              LOG.error(se.getMessage(), se);
      }

      String file = "META-INF/services/xcmis/" + CmisRegistry.XCMIS_REGISTRY_FACTORY;
      InputStream in = null;
      ClassLoader classLoader = null;

      try
      {
         classLoader = Thread.currentThread().getContextClassLoader();
         in = classLoader.getResourceAsStream(file);
      }
      catch (Exception e)
      {
      }

      if (in == null)
      {
         try
         {
            classLoader = CmisRegistryFactoryFinder.class.getClassLoader();
            in = classLoader.getResourceAsStream(file);
         }
         catch (Exception e)
         {
             if (LOG.isDebugEnabled())
                 LOG.error(e.getMessage(), e);
         }
      }

      if (in == null)
      {
         in = ClassLoader.getSystemResourceAsStream(file);
      }

      if (in != null)
      {
         try
         {
            name = null;
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            name = rd.readLine();
            rd.close();
            return createInstance(classLoader, name);
         }
         catch (IOException e)
         {
            if (LOG.isDebugEnabled())
               LOG.error(e.getMessage(), e);
         }
      }

      return null;
   }

   private static CmisRegistryFactory createInstance(ClassLoader classLoader, String name)
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Try to create " + name);

      try
      {
         Class cls = null;
         if (classLoader != null)
         {
            cls = classLoader.loadClass(name);
         }
         else
         {
            cls = Class.forName(name);
         }
         return (CmisRegistryFactory)cls.newInstance();
      }
      catch (ClassNotFoundException e)
      {
         throw new CmisRuntimeException("Registry " + name + " not found. ", e);
      }
      catch (Exception e1)
      {
         throw new CmisRuntimeException("Can't instantiate factory " + name, e1);
      }
   }

}
