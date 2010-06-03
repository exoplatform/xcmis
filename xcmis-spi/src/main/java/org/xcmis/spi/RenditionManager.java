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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.utils.CmisUtils;
import org.xcmis.spi.utils.MimeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manage object's renditions.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RenditionManager
{

   private static final Log LOG = ExoLogger.getLogger(RenditionManager.class);

   private static AtomicReference<RenditionManager> manager = new AtomicReference<RenditionManager>();

   public static RenditionManager getInstance()
   {
      RenditionManager s = manager.get();
      if (s == null)
      {
         manager.compareAndSet(null, new RenditionManager());
         s = manager.get();
      }
      return s;
   }

   public static void setInstance(RenditionManager inst)
   {
      manager.set(inst);
   }

   /**
    * Decode string from hex-string.
    * 
    * @param in the input string
    * @return string output
    */
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

   /**
    * Encode string into hex-string.
    * 
    * @param in the input string
    * @return the string hex-sequence
    */
   private static String encode(String in)
   {
      StringBuffer out = new StringBuffer();
      for (int i = 0; i < in.length(); i++)
      {
         out.append(Integer.toHexString(in.charAt(i)));
      }
      return out.toString();
   }

   protected Map<MimeType, RenditionProvider> renditionProviders = new TreeMap<MimeType, RenditionProvider>(
      new Comparator<MimeType>()
      {
         public int compare(MimeType m1, MimeType m2)
         {
            if (m1.getType().equals(CmisConstants.WILDCARD) && !m2.getType().equals(CmisConstants.WILDCARD))
            {
               return 1;
            }
            if (!m1.getType().equals(CmisConstants.WILDCARD) && m2.getType().equals(CmisConstants.WILDCARD))
            {
               return -1;
            }
            if (m1.getSubType().equals(CmisConstants.WILDCARD) && !m2.getSubType().equals(CmisConstants.WILDCARD))
            {
               return 1;
            }
            if (!m1.getSubType().equals(CmisConstants.WILDCARD) && m2.getSubType().equals(CmisConstants.WILDCARD))
            {
               return -1;
            }
            return m1.toString().compareToIgnoreCase(m2.toString());
         }
      });

   protected RenditionManager()
   {
   }

   public void addRenditionProviders(List<String> provs)
   {
      if (provs != null && !provs.isEmpty())
      {
         for (String one : provs)
         {
            try
            {
               RenditionProvider prov = (RenditionProvider)Class.forName(one).newInstance();
               for (MimeType mimeType : prov.getSupportedMediaType())
               {
                  renditionProviders.put(mimeType, prov);
               }
            }
            catch (Exception e)
            {
               LOG.error("Cannot instantiate rendition provider instance: ", e);
            }
         }
      }
   }

   /**
    * Get all renditions of specified entry.
    * 
    * @param object object for getting renditions
    * @return set of object renditions. If object has not renditions then empty
    *         iterator will be returned
    */
   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      // Not support other than document objects
      if (object.getBaseType() == BaseType.DOCUMENT && ((DocumentData)object).hasContent())
      {
         MimeType mime = MimeType.fromString(((DocumentData)object).getContentStreamMimeType());
         List<Rendition> renditions = new ArrayList<Rendition>();
         for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
         {
            if (e.getKey().match(mime))
            {
               Rendition rendition = new Rendition();
               // e.getKey() is unique because is key of map.
               // Use it as id for content stream.
               rendition.setStreamId(encode(e.getKey().toString()));
               rendition.setKind(e.getValue().getKind());
               rendition.setMimeType(e.getValue().getProducedMediaType().toString());
               renditions.add(rendition);
            }
         }
         return new BaseItemsIterator<Rendition>(renditions);
      }
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * Get rendition from content stream with known mime-type.
    * 
    * @param mime MimeType
    * @param stream ContentStream
    * @return Renditions content stream
    * @throws IOException if any I/O error occurs
    */
   public ContentStream getStream(ContentStream stream, MimeType mime) throws IOException
   {
      for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
      {
         if (e.getKey().match(mime))
         {
            RenditionProvider renditionProvider = e.getValue();
            ContentStream renditionContentStream = renditionProvider.getRenditionStream(stream);
            return renditionContentStream;
         }
      }
      return null;
   }

   /**
    * Get rendition stream for objects with specified stream id.
    * 
    * @param streamId stream id
    * @param obj ObjectData
    * @return Renditions content stream
    * @throws IOException if any I/O error occurs
    */
   public ContentStream getStream(ObjectData object, String streamId) throws IOException
   {
      // Assume streamId is encoded produces mime-type of RenditionProvider.
      // See RenditionProvider#getProducedMediaType().
      // Not support other than document objects
      if (object.getBaseType() == BaseType.DOCUMENT && ((DocumentData)object).hasContent())
      {
         MimeType mime = MimeType.fromString(decode(streamId));
         return getStream(((DocumentData)object).getContentStream(), mime);
      }
      return null;
   }
}
