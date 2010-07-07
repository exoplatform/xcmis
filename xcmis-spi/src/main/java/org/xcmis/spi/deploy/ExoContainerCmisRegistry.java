package org.xcmis.spi.deploy;

/*
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
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.CmisRegistryFactory;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.StorageProvider;

import java.util.Iterator;
import java.util.List;

/**
 * @version $Id:$
 */
public class ExoContainerCmisRegistry extends CmisRegistry implements Startable, CmisRegistryFactory
{

   private final ExoContainerContext containerContext;

   private static final Log LOG = ExoLogger.getLogger(ExoContainerCmisRegistry.class);

   public ExoContainerCmisRegistry(ExoContainerContext containerContext, InitParams initParams)
   {
      this.containerContext = containerContext;

      if (initParams != null)
      {
         Iterator<ValuesParam> vparams = initParams.getValuesParamIterator();
         while (vparams.hasNext())
         {
            ValuesParam next = vparams.next();
            if (next.getName().equalsIgnoreCase("renditionProviders"))
            {
               this.providers.addAll(next.getValues());
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      ExoContainer container = containerContext.getContainer();

      @SuppressWarnings("unchecked")
      List<StorageProvider> sps = container.getComponentInstancesOfType(StorageProvider.class);
      RenditionManager manager = RenditionManager.getInstance();
      manager.addRenditionProviders(providers);

      for (StorageProvider sp : sps)
      {
         addStorage(sp);
      }
      setFactory(this);
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
   }

   public CmisRegistry getRegistry()
   {
      return (CmisRegistry)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(CmisRegistry.class);
   }

}
