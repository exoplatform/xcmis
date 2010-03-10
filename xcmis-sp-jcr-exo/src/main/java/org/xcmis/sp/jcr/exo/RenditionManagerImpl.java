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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.utils.MimeType;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.version.Version;

import org.xcmis.core.CmisRenditionType;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.sp.jcr.exo.object.EntryVersion;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.sp.jcr.exo.rendition.RenditionProvider;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;

public class RenditionManagerImpl implements RenditionManager
{

   /** The streams map. */
   private Map<String, ContentStream> streamsMap;

   private final Map<MimeType, RenditionProvider> renditionProviders;

   private Session session;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RenditionManagerImpl.class);

   public RenditionManagerImpl(Map<MimeType, RenditionProvider> renditionProviders, Session session)
   {
      this.renditionProviders = renditionProviders;
      this.streamsMap = new HashMap<String, ContentStream>();
      this.session = session;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<CmisRenditionType> getRenditions(Entry entry) throws RepositoryException
   {
      try
      {
         RenditionIterator it = new RenditionIterator(((EntryImpl)entry).getNode().getNodes());
         if (it.hasNext())
         {
            return it;
         }
         else
         {
            if (entry.getContent(null) != null)
            {
               MimeType contentType = MimeType.fromString(entry.getContent(null).getMediaType());
               for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
               {
                  if (e.getKey().match(contentType))
                  {
                     RenditionProvider renditionProvider = e.getValue();
                     RenditionContentStream renditionContentStream =
                        renditionProvider.getRenditionStream(entry.getContent(null));
                     //String id = IdGenerator.generate();
                     String id = entry.getObjectId();
                     streamsMap.put(id, renditionContentStream);
                     CmisRenditionType rendition = new CmisRenditionType();
                     rendition.setStreamId(id);
                     rendition.setKind(renditionContentStream.getKind());
                     rendition.setMimetype(renditionContentStream.getMediaType());
                     rendition.setLength(BigInteger.valueOf(renditionContentStream.length()));
                     rendition.setHeight(BigInteger.valueOf(renditionContentStream.getHeight()));
                     rendition.setWidth(BigInteger.valueOf(renditionContentStream.getWidth()));
                     return new RenditionIterator(rendition);
                  }
               }
            }
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg =
            "Unable get renditions for object " + entry.getObjectId() + " Unexpected error " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
      catch (IOException e)
      {
         String msg = "Unable get renditions for object " + entry.getObjectId() + " Unexpected error " + e.getMessage();
         throw new RepositoryException(msg, e);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<CmisRenditionType> getRenditions(String objectId) throws ObjectNotFoundException,
      RepositoryException
   {
      return getRenditions(getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getStream(String streamId)
   {
      RenditionContentStream str = (RenditionContentStream)streamsMap.get(streamId);
      return str;
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(Entry entry) throws RepositoryException
   {
      try
      {
         int count = 0;
         for (NodeIterator iter = ((EntryImpl)entry).getNode().getNodes(); iter.hasNext();)
         {
            Node item = iter.nextNode();
            if (item.isNodeType(JcrCMIS.CMIS_NT_RENDITION))
            {
               item.remove();
               count++;
            }
         }
         if (count > 0)
            ((EntryImpl)entry).getNode().save();
         else
            streamsMap.remove(entry.getObjectId());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to remove renditions for object " + entry.getObjectId() + ". " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(String objectId) throws ObjectNotFoundException, RepositoryException
   {
      removeRenditions(getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   private Entry getObjectById(String objectId) throws ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get object with id " + objectId);
      try
      {
         Node node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         Entry object = null;
         if (node.isNodeType(JcrCMIS.NT_VERSION))
            object = new EntryVersion((Version)node);
         else
            object = new EntryImpl(node);
         return object;
      }
      catch (ItemNotFoundException infe)
      {
         String msg = "Object " + objectId + " not found.";
         throw new ObjectNotFoundException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unexpected error. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

}
