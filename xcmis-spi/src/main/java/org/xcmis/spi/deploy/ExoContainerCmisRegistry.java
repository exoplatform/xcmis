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
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.picocontainer.Startable;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.CmisRegistryFactory;
import org.xcmis.spi.RenditionManager;

import java.util.Iterator;

/**
 * @version $Id:$
 */
public class ExoContainerCmisRegistry extends CmisRegistry implements Startable, CmisRegistryFactory
{

   //   private static final Log LOG = ExoLogger.getLogger(ExoContainerCmisRegistry.class);

   protected final InitParams initParams;

   public ExoContainerCmisRegistry(InitParams initParams)
   {
      this.initParams = initParams;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void start()
   {
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
      RenditionManager manager = RenditionManager.getInstance();
      manager.addRenditionProviders(providers);
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
