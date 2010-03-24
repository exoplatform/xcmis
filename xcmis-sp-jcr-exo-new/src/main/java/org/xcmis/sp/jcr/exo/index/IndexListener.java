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

package org.xcmis.sp.jcr.exo.index;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.EventJournal;
import org.xcmis.spi.data.ObjectData;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class IndexListener implements EventJournal
{

   private static final Log LOG = ExoLogger.getLogger(IndexListener.class);

   public IndexListener(String storage)
   {
   }

   public void created(ObjectData object)
   {
      LOG.info("created " + object.getObjectId());

      // TODO Auto-generated method stub
   }

   public void removed(String id)
   {
      LOG.info("removed " + id);

      // TODO Auto-generated method stub
   }

   public void updated(ObjectData object)
   {
      LOG.info("updated " + object.getObjectId());

      // TODO Auto-generated method stub
   }

}
