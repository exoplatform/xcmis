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
import org.xcmis.spi.impl.RenditionImpl;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.utils.MimeType;

public class RenditionManager
{

   /** The streams map. */
   private Map<String, ContentStream> streamsMap;

   private final Map<MimeType, RenditionProvider> renditionProviders;

   private Session session;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RenditionManager.class);

   public RenditionManager(Map<MimeType, RenditionProvider> renditionProviders, Session session)
   {
      this.renditionProviders = renditionProviders;
      this.streamsMap = new HashMap<String, ContentStream>();
      this.session = session;
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
         else if (streamsMap.containsKey(obj.getObjectId()))
         {
            String id = obj.getObjectId();
            RenditionContentStream renditionContentStream = getStream(id);
            RenditionImpl rendition = new RenditionImpl();
            rendition.setStreamId(id);
            rendition.setKind(renditionContentStream.getKind());
            rendition.setMimeType(renditionContentStream.getMediaType());
            rendition.setLength(renditionContentStream.length());
            rendition.setHeight(Long.valueOf(renditionContentStream.getHeight()).intValue());
            rendition.setWidth(Long.valueOf(renditionContentStream.getWidth()).intValue());
            return new RenditionIterator(rendition);
         }
         else
         {
            MimeType contentType = MimeType.fromString(((DocumentImpl)obj).getContentStreamMimeType());
            for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
            {
               if (e.getKey().match(contentType))
               {
                  RenditionProvider renditionProvider = e.getValue();
                  RenditionContentStream renditionContentStream =
                     renditionProvider.getRenditionStream(obj.getContentStream(null));
                  //String id = IdGenerator.generate();
                  String id = obj.getObjectId();
                  streamsMap.put(id, renditionContentStream);
                  RenditionImpl rendition = new RenditionImpl();
                  rendition.setStreamId(id);
                  rendition.setKind(renditionContentStream.getKind());
                  rendition.setMimeType(renditionContentStream.getMediaType());
                  rendition.setLength(renditionContentStream.length());
                  rendition.setHeight(Long.valueOf(renditionContentStream.getHeight()).intValue());
                  rendition.setWidth(Long.valueOf(renditionContentStream.getWidth()).intValue());
                  return new RenditionIterator(rendition);
               }
            }
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
   public ItemsIterator<Rendition> getRenditions(String objectId) throws ObjectNotFoundException, StorageException
   {
      return getRenditions(getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   public RenditionContentStream getStream(String streamId)
   {
      RenditionContentStream str = (RenditionContentStream)streamsMap.get(streamId);
      return str;
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(ObjectData obj) throws StorageException
   {
      try
      {
         int count = 0;
         for (NodeIterator iter = ((BaseObjectData)obj).getNode().getNodes(); iter.hasNext();)
         {
            Node item = iter.nextNode();
            if (item.isNodeType(JcrCMIS.CMIS_NT_RENDITION))
            {
               item.remove();
               count++;
            }
         }
         if (count > 0)
            ((BaseObjectData)obj).getNode().save();
         else
            streamsMap.remove(obj.getObjectId());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to remove renditions for object " + obj.getObjectId() + ". " + re.getMessage();
         throw new StorageException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(String objectId) throws ObjectNotFoundException, StorageException
   {
      removeRenditions(getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   private ObjectData getObjectById(String objectId) throws ObjectNotFoundException, StorageException
   {
      Node node;
      try
      {
         node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);
         if (type.getBaseId() == BaseType.DOCUMENT)
            return new DocumentImpl(type, node);
      }
      catch (ItemNotFoundException nfe)
      {
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException(re.getMessage(), re);
      }
      // If not a document
      throw new CmisRuntimeException("Invalid object type.");
   }

}
