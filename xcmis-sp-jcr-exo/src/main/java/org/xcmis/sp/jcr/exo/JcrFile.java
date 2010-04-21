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

package org.xcmis.sp.jcr.exo;


import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.MimeType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class JcrFile extends DocumentDataImpl
{

   public JcrFile(TypeDefinition type, Node node, RenditionManager manager, IndexListener indexListener)
   {
      super(type, node, manager, indexListener);
      try
      {
         if (type.isVersionable() && this.node.canAddMixin(JcrCMIS.MIX_VERSIONABLE))
         {
            this.node.addMixin(JcrCMIS.MIX_VERSIONABLE);
            session.save();
         }
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unexpected error. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void cancelCheckout() throws StorageException
   {
      // TODO
      throw new CmisRuntimeException("Not implemented for not CMIS type.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DocumentData checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      // TODO
      throw new CmisRuntimeException("Not implemented for not CMIS type.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DocumentData checkout() throws ConstraintException, VersioningException, StorageException
   {
      // TODO
      throw new CmisRuntimeException("Not implemented for not CMIS type.");
   }

   /**
    * {@inheritDoc}
    */
   void save() throws StorageException, NameConstraintViolationException, UpdateConflictException
   {
      try
      {
         Node parentNode = node.getParent();
         // New name was set. Need rename Document.
         // See setName(String), setProperty(Node, Property<?>).
         if (name != null)
         {
            if (name.length() == 0)
            {
               throw new NameConstraintViolationException("Name is empty.");
            }

            if (parentNode.hasNode(name))
            {
               throw new NameConstraintViolationException("Object with name " + name + " already exists.");
            }

            String srcPath = node.getPath();
            String destPath = srcPath.substring(0, srcPath.lastIndexOf('/') + 1) + name;

            session.move(srcPath, destPath);

            node = (Node)session.getItem(destPath);
         }

         session.save();
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable save object. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   protected void setContentStream(Node data, ContentStream content) throws RepositoryException, IOException
   {
      // jcr:content
      Node contentNode = data.getNode(JcrCMIS.JCR_CONTENT);

      if (content != null)
      {
         MimeType mediaType = content.getMediaType();
         contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, mediaType.getBaseType());
         if (mediaType.getParameter(CmisConstants.CHARSET) != null)
         {
            contentNode.setProperty(JcrCMIS.JCR_ENCODING, mediaType.getParameter(CmisConstants.CHARSET));
         }
         contentNode.setProperty(JcrCMIS.JCR_DATA, content.getStream()).getLength();
         contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
      }
      else
      {
         contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, "");
         contentNode.setProperty(JcrCMIS.JCR_ENCODING, (Value)null);
         contentNode.setProperty(JcrCMIS.JCR_DATA, new ByteArrayInputStream(new byte[0]));
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentStreamMimeType()
   {
      try
      {
         return node.getProperty("jcr:content/jcr:mimeType").getString();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get content stream mime type. " + re.getMessage(), re);
      }
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   @Override
   //   protected Long getContentStreamLength()
   //   {
   //      try
   //      {
   //         return node.getProperty("jcr:content/jcr:data").getLength();
   //      }
   //      catch (RepositoryException re)
   //      {
   //         throw new CmisRuntimeException("Unable get content stream length. " + re.getMessage(), re);
   //      }
   //   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersionLabel()
   {
      return latestLabel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersionSeriesId()
   {
      try
      {
         return node.getProperty(JcrCMIS.JCR_VERSION_HISTORY).getString();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get version series ID. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Calendar getCreationDate()
   {
      try
      {
         return node.getProperty(JcrCMIS.JCR_CREATED).getDate();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get cteation date. " + re.getMessage(), re);
      }
   }
}
