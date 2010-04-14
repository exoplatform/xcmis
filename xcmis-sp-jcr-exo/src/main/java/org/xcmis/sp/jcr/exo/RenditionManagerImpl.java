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

import org.xcmis.renditions.RenditionContentStream;
import org.xcmis.renditions.RenditionProvider;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.utils.CmisUtils;
import org.xcmis.spi.utils.MimeType;

import java.io.IOException;
import java.util.Map;

public class RenditionManagerImpl implements RenditionManager
{

   /** The streams map. */

   private final Map<MimeType, RenditionProvider> renditionProviders;

   /** Logger. */
   //   private static final Log LOG = ExoLogger.getLogger(RenditionManagerImpl.class);

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
            if (obj.getBaseType() == BaseType.DOCUMENT && obj.getContentStream(null) != null)
            {
               MimeType contentType = MimeType.fromString(((DocumentDataImpl)obj).getContentStreamMimeType());
               RenditionProvider prov = renditionProviders.get(contentType);
               if (prov != null && !prov.canStoreRendition())
               {
                  Rendition rendition = new Rendition();
                  rendition.setStreamId(encode(contentType.toString()));
                  rendition.setKind("cmis:thumbnail");
                  return new RenditionIterator(rendition);
               }
            }
            return CmisUtils.emptyItemsIterator();
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get renditions for object " + obj.getObjectId() + " Unexpected error " + re.getMessage();
         throw new StorageException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public RenditionContentStream getStream(ObjectData obj, String streamId)
   {
      for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
      {
         if (e.getKey().match(MimeType.fromString(decode(streamId))))
         {
            RenditionProvider renditionProvider = e.getValue();
            RenditionContentStream renditionContentStream = null;
            if (!renditionProvider.canStoreRendition())
            {
               try
               {
                  renditionContentStream = renditionProvider.getRenditionStream(obj.getContentStream(null));
               }
               catch (IOException ioe)
               {
                  String msg =
                     "Unable get renditions for object " + obj.getObjectId() + " Unexpected error " + ioe.getMessage();
                  throw new StorageException(msg, ioe);
               }
               return renditionContentStream;
            }
         }
      }
      return null;
   }

   private static String encode(String in)
   {
      StringBuffer out = new StringBuffer();
      for (int i = 0; i < in.length(); i++)
      {
         out.append(Integer.toHexString(in.charAt(i)));
      }
      return out.toString();
   }

   private static String decode(String in)
   {
      StringBuffer out = new StringBuffer();
      int offset = 0;
      while (offset < in.length())
      {
         int part = Integer.parseInt(in.substring(offset, offset + 2), 16);
         out.append((char)part);
         offset = offset + 2;
      }
      return out.toString();
   }

}