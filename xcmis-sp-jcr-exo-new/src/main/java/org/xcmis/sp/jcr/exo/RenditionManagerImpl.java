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
package org.xcmis.sp.jcr.exo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.Rendition;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.sp.jcr.exo.rendition.RenditionProvider;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.impl.RenditionImpl;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.utils.MimeType;

public class RenditionManagerImpl implements RenditionManager
{

   /** The streams map. */

   private final Map<MimeType, RenditionProvider> renditionProviders;


   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RenditionManagerImpl.class);

   public RenditionManagerImpl(Map<MimeType, RenditionProvider> renditionProviders)
   {
      this.renditionProviders = renditionProviders;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Rendition> getRenditions(ObjectData obj) throws StorageException
   {
      try
      {
         RenditionIterator it = new RenditionIterator(((BaseObjectData)obj).getNode().getNodes());
         if (it.hasNext())
         {
            return it;
         }
         else
         {
            MimeType contentType = MimeType.fromString(((DocumentImpl)obj).getContentStreamMimeType());
            RenditionImpl rendition = new RenditionImpl();
            rendition.setStreamId(contentType.toString());
            rendition.setKind("cmis:thumbnail");
            return new RenditionIterator(rendition);
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get renditions for object " + obj.getObjectId() + " Unexpected error " + re.getMessage();
         throw new StorageException(msg, re);
      }
      catch (IOException e)
      {
         String msg = "Unable get renditions for object " + obj.getObjectId() + " Unexpected error " + e.getMessage();
         throw new StorageException(msg, e);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public RenditionContentStream getStream(ObjectData obj, String streamId)
   {
      for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
      {
         if (e.getKey().match(MimeType.fromString(streamId)))
         {
            RenditionProvider renditionProvider = e.getValue();
            if (!renditionProvider.canStoreRendition()) {
            RenditionContentStream renditionContentStream =
               renditionProvider.getRenditionStream(obj.getContentStream(null));
            return renditionContentStream;
            } else {
               return null;
            }
         }
      }
   }
}
