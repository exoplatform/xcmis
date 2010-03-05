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

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.sp.jcr.exo.rendition.RenditionProvider;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.utils.MimeType;

public class UpdateListener implements EventListener
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(UpdateListener.class.getName());

   Map<MimeType, RenditionProvider> renditionProviders;

   private final String workspace;

   private final Repository repository;
   
   SessionProvider prov = null;
   
   Session session = null;

   /**
    * Instantiates a new update listener.
    * 
    * @param repository the repository
    * @param workspace the workspace
    * @param renditionProviders the rendition providers
    */
   public UpdateListener(Repository repository, String workspace, Map<MimeType, RenditionProvider> renditionProviders)
   {
      this.repository = repository;
      this.workspace = workspace;
      this.renditionProviders = renditionProviders;
   }

   /**
    * {@inheritDoc}
    */
   public void onEvent(EventIterator eventIterator)
   {
      
      try
      {
         while (eventIterator.hasNext())
         {
            Event event = eventIterator.nextEvent();
            String path = event.getPath();
            if (event.getPath().endsWith("jcr:content"))
            {
               if (prov == null)
                  prov = SessionProvider.createSystemProvider();
               if (session == null)
              session = prov.getSession(workspace, (ManageableRepository)repository);

               Node node = session.getItem(path).getParent();
              //Entry entry = new EntryImpl(node);
               ContentStream content;
               //content = entry.getContent(null);
               Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);
               Property fileContent = contentNode.getProperty(JcrCMIS.JCR_DATA);
               long length = fileContent.getLength();
               if (length == 0)
                  content =  null; // No content, but node has empty stream.
               content = new BaseContentStream(fileContent.getStream(), //
                  length, //
                  node.getDepth() == 0 ? CMIS.ROOT_FOLDER_NAME : node.getName(), //
                  contentNode.getProperty(JcrCMIS.JCR_MIMETYPE).getString());
               
               
               if (content != null)
               {
                  int count = 0;
                  for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
                  {
                     if (e.getKey().match(MimeType.fromString(content.getMediaType())))
                     {
                        RenditionProvider renditionProvider = e.getValue();
                        if (renditionProvider.canStoreRendition())
                        {
                           RenditionContentStream renditionContentStream = renditionProvider.getRenditionStream(content);
                           Node rendition =
                              node.addNode(IdGenerator.generate(), JcrCMIS.CMIS_NT_RENDITION);
                           rendition.setProperty(JcrCMIS.CMIS_RENDITION_STREAM, renditionContentStream.getStream());
                           rendition.setProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE, renditionContentStream
                              .getMediaType());
                           rendition.setProperty(JcrCMIS.CMIS_RENDITION_KIND, renditionContentStream.getKind());
                           rendition.setProperty(JcrCMIS.CMIS_RENDITION_HEIGHT, renditionContentStream.getHeight());
                           rendition.setProperty(JcrCMIS.CMIS_RENDITION_WIDTH, renditionContentStream.getWidth());
                           count++;
                        }
                     }
                     if (count > 0)
                   {
                      node.save();
                   }
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         LOG.error("Creating rendition on event failed. ", e);
      }

   }

}
