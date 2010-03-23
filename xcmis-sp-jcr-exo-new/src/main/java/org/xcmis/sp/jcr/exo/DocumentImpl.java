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

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.object.Property;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DocumentImpl extends BaseObjectData implements Document
{

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   private ContentStream content;

   protected final VersioningState versioningState;

   public DocumentImpl(TypeDefinition type, Folder parent, String name, VersioningState versioningState)
   {
      super(type, parent, name);
      this.versioningState = versioningState;
   }

   public DocumentImpl(TypeDefinition type, Node node)
   {
      super(type, node);
      versioningState = null; // no sense for not newly created Document
   }

   public void cancelCheckout() throws StorageException
   {
      // TODO Auto-generated method stub

   }

   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Document checkout() throws ConstraintException, VersioningException, StorageException
   {
      // TODO
      DocumentCopy copy = new DocumentCopy(this, getParent(), getName() + "_PWC", null);
      copy.save();
      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream()
   {
      if (isNew())
         throw new UnsupportedOperationException("getContentStream");

      try
      {
         // Main content
         Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);

         long contentLength = contentNode.getProperty(JcrCMIS.JCR_DATA).getLength();

         if (contentLength == 0)
            return null;

         return new BaseContentStream(contentNode.getProperty(JcrCMIS.JCR_DATA).getStream(), //
            getName(), //
            contentNode.getProperty(JcrCMIS.JCR_MIMETYPE).getString());
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get content stream. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      if (isNew())
         throw new UnsupportedOperationException("getContentStream");

      if (streamId == null || streamId.equals(getString(CMIS.CONTENT_STREAM_ID)))
         return getContentStream();

      try
      {
         Node rendition = null;
         try
         {
            rendition = node.getNode(streamId);
         }
         catch (PathNotFoundException pnfe)
         {
            return null;
         }

         javax.jcr.Property renditionContent = rendition.getProperty(JcrCMIS.CMIS_RENDITION_STREAM);

         return new RenditionContentStream(renditionContent.getStream(), renditionContent.getLength(), null, rendition
            .getProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE).getString(), rendition.getProperty(
            JcrCMIS.CMIS_RENDITION_KIND).getString());
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get content stream " + streamId + ". " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getContentStreamMimeType()
   {
      return getString(CMIS.CONTENT_STREAM_MIME_TYPE);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionLabel()
   {
      return getString(CMIS.VERSION_LABEL);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutBy()
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutId()
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return getString(CMIS.VERSION_SERIES_ID);
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasContent()
   {
      if (isNew())
         return false;

      try
      {
         // Main content
         Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);
         long contentLength = contentNode.getProperty(JcrCMIS.JCR_DATA).getLength();
         return contentLength > 0;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get content stream. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestMajorVersion()
   {
      return isLatestVersion() && isMajorVersion();
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestVersion()
   {
      Boolean latest = getBoolean(CMIS.IS_LATEST_VERSION);
      return latest == null ? true : latest;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMajorVersion()
   {
      Boolean major = getBoolean(CMIS.IS_MAJOR_VERSION);
      return major == null ? false : major;
   }

   public boolean isPWC()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isVersionSeriesCheckedOut()
   {
      Boolean checkout = getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT);
      return checkout == null ? false : checkout;
   }

   /**
    * {@inheritDoc}
    */
   protected void create() throws StorageException, NameConstraintViolationException
   {
      try
      {
         if (name == null && content != null)
            name = content.getFileName();

         if (name == null || name.length() == 0)
            throw new NameConstraintViolationException("Name for new document must be provided.");

         Node parentNode = ((FolderImpl)parent).getNode();

         if (parentNode.hasNode(name))
            throw new NameConstraintViolationException("Object with name " + name
               + " already exists in specified folder.");

         Node doc = parentNode.addNode(name, type.getLocalName());

         if (!doc.isNodeType(JcrCMIS.CMIS_MIX_DOCUMENT)) // May be already inherited.
            doc.addMixin(JcrCMIS.CMIS_MIX_DOCUMENT);
         if (doc.canAddMixin(JcrCMIS.MIX_VERSIONABLE)) // Document type is versionable.
            doc.addMixin(JcrCMIS.MIX_VERSIONABLE);

         doc.setProperty(CMIS.OBJECT_TYPE_ID, //
            type.getId());
         doc.setProperty(CMIS.BASE_TYPE_ID, //
            type.getBaseId().value());
         doc.setProperty(CMIS.CREATED_BY, //
            parentNode.getSession().getUserID());
         doc.setProperty(CMIS.CREATION_DATE, //
            Calendar.getInstance());
         doc.setProperty(CMIS.LAST_MODIFIED_BY, //
            parentNode.getSession().getUserID());
         doc.setProperty(CMIS.LAST_MODIFICATION_DATE, //
            Calendar.getInstance());
         doc.setProperty(CMIS.VERSION_SERIES_ID, //  
            doc.getProperty("jcr:versionHistory").getString());
         doc.setProperty(CMIS.IS_LATEST_VERSION, //
            true);
         doc.setProperty(CMIS.IS_MAJOR_VERSION, //
            versioningState == VersioningState.MAJOR);
         doc.setProperty(CMIS.VERSION_LABEL, //
            versioningState == VersioningState.CHECKEDOUT ? pwcLabel : latestLabel);
         doc.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            versioningState == VersioningState.CHECKEDOUT);
         if (versioningState == VersioningState.CHECKEDOUT)
         {
            doc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
               ((ExtendedNode)doc).getIdentifier());
            doc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
               parentNode.getSession().getUserID());
         }

         for (Property<?> property : properties.values())
            setProperty(doc, property);

         try
         {
            setContentStream(doc, content);
         }
         catch (IOException ioe)
         {
            throw new CmisRuntimeException("Unable add content for new document. " + ioe.getMessage(), ioe);
         }

         if (policies != null && policies.size() > 0)
         {
            for (Policy policy : policies)
               applyPolicy(doc, policy);
         }

         if (acl != null && acl.size() > 0)
            setACL(doc, acl);

         parentNode.save();

         name = null;
         policies = null;
         acl = null;
         properties.clear();
         content = null;

         node = doc;
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable save Document. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(ContentStream contentStream) throws ConstraintException
   {
      if (isNew())
      {
         this.content = contentStream;
      }
      else
      {
         try
         {
            setContentStream(node, contentStream);
         }
         catch (RepositoryException re)
         {
            throw new StorageException("Unable save document. " + re.getMessage(), re);
         }
         catch (IOException ioe)
         {
            throw new CmisRuntimeException("Unable save document content. " + ioe.getMessage(), ioe);
         }
      }
   }

   /**
    * Set new or remove (if <code>content == null</code>) content stream.
    * 
    * @param data node to which content stream should be set
    * @param content content
    * @throws RepositoryException if any JCR repository error occurs
    * @throws IOException if any i/o error occurs
    */
   protected void setContentStream(Node data, ContentStream content) throws RepositoryException, IOException
   {
      long contentLength = 0;
      // jcr:content
      Node contentNode =
         data.hasNode(JcrCMIS.JCR_CONTENT) ? data.getNode(JcrCMIS.JCR_CONTENT) : data.addNode(JcrCMIS.JCR_CONTENT,
            JcrCMIS.NT_RESOURCE);

      // Assumes if there is no content then no mime-type, if any unknown
      // content then should be 'application/octet-stream'
      contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, content == null ? "" : content.getMediaType());

      // Re-count content length
      contentLength = contentNode.setProperty(JcrCMIS.JCR_DATA, //
         content == null ? new ByteArrayInputStream(new byte[0]) : content.getStream()).getLength();

      contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, //
         Calendar.getInstance());

      // Update CMIS properties
      if (content != null && !data.hasProperty(CMIS.CONTENT_STREAM_ID))
      {
         // If new node
         data.setProperty(CMIS.CONTENT_STREAM_ID, //
            ((ExtendedNode)contentNode).getIdentifier());
      }
      data.setProperty(CMIS.CONTENT_STREAM_LENGTH, //
         contentLength);
      data.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, //
         content == null ? null : content.getMediaType());
      // Do not provide file name if there is no content.
      //      data.setProperty(CMIS.CONTENT_STREAM_FILE_NAME, //
      //         content == null ? null : getName());
   }

}
