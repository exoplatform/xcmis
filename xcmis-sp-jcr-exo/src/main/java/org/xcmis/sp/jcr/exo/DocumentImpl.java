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
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStreamAllowed;
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
import org.xcmis.spi.object.RenditionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

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

   private RenditionManager renditionManager;

   public DocumentImpl(TypeDefinition type, Folder parent, Session session, VersioningState versioningState)
   {
      super(type, parent, session);
      this.versioningState = versioningState;
   }

   public DocumentImpl(TypeDefinition type, Node node)
   {
      super(type, node);
      versioningState = null; // no sense for not newly created Document
   }

   public DocumentImpl(TypeDefinition type, Node node, RenditionManager manager)
   {
      super(type, node);
      versioningState = null; // no sense for not newly created Document
      this.renditionManager = manager;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout() throws StorageException
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("Unable cancel checkout newly created Document.");
      }

      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      if (!isVersionSeriesCheckedOut())
      {
         throw new ConstraintException("There is no Private Working Copy in version series.");
      }

      try
      {
         Node pwcNode = ((ExtendedSession)session).getNodeByIdentifier(getVersionSeriesCheckedOutId());
         PWC pwc = new PWC(type, pwcNode, this);
         pwc.delete();
      }
      catch (ItemNotFoundException e)
      {
         throw new ConstraintException("There is no Private Working Copy in version series.");
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable cancel checkout. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   // Will be overridden in PWC
   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      throw new ConstraintException("Current object is not Private Working Copy.");
   }

   /**
    * {@inheritDoc}
    */
   public Document checkout() throws ConstraintException, VersioningException, StorageException
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("Unable checkout newly created Document.");
      }

      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      if (isVersionSeriesCheckedOut())
      {
         throw new VersioningException("Version series already checked-out. "
            + "Not allowed have more then one PWC for version series at a time.");
      }

      PWC pwc = new PWC(this, session);
      pwc.save();
      return pwc;
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream()
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("getContentStream");
      }

      try
      {
         // Main content
         Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);

         long contentLength = contentNode.getProperty(JcrCMIS.JCR_DATA).getLength();

         if (contentLength == 0)
         {
            return null;
         }

         return new BaseContentStream(contentNode.getProperty(JcrCMIS.JCR_DATA).getStream(), //
            contentLength, getName(), //
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
      {
         throw new UnsupportedOperationException("getContentStream");
      }

      if (streamId == null || streamId.equals(getString(CMIS.CONTENT_STREAM_ID)))
      {
         return getContentStream();
      }

      Node rendition = null;
      try
      {
         rendition = node.getNode(streamId);
         javax.jcr.Property renditionContent = rendition.getProperty(JcrCMIS.CMIS_RENDITION_STREAM);

         return new RenditionContentStream(renditionContent.getStream(), renditionContent.getLength(), null, rendition
            .getProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE).getString(), rendition.getProperty(
            JcrCMIS.CMIS_RENDITION_KIND).getString());
      }
      catch (PathNotFoundException pnfe)
      {
         try
         {
            return renditionManager.getStream(this, streamId);
         }
         catch (Exception e)
         {
            throw new CmisRuntimeException("Unable get rendition stream. " + e.getMessage(), e);
         }
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get rendition stream. " + re.getMessage(), re);
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
      {
         return false;
      }

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

   /**
    * {@inheritDoc}
    */
   public boolean isPWC()
   {
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
   @Override
   protected void create() throws StorageException, NameConstraintViolationException
   {
      try
      {
         if (name == null && content != null)
         {
            name = content.getFileName();
         }

         if (name == null || name.length() == 0)
         {
            throw new NameConstraintViolationException("Name for new document must be provided.");
         }

         Node doc = null;
         if (parent != null)
         {
            Node parentNode = parent.getNode();

            if (parentNode.hasNode(name))
            {
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in specified folder.");
            }

            doc = parentNode.addNode(name, type.getLocalName());
         }
         else
         {
            Node unfiledStore = (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_UNFILED);
            // wrapper around Document node with unique name.
            Node unfiled = unfiledStore.addNode(IdGenerator.generate(), "xcmis:unfiledObject");
            doc = unfiled.addNode(name, type.getLocalName());
         }

         if (!doc.isNodeType(JcrCMIS.CMIS_MIX_DOCUMENT))
         {
            doc.addMixin(JcrCMIS.CMIS_MIX_DOCUMENT);
         }
         if (doc.canAddMixin(JcrCMIS.MIX_VERSIONABLE))
         {
            doc.addMixin(JcrCMIS.MIX_VERSIONABLE);
         }

         doc.setProperty(CMIS.OBJECT_TYPE_ID, //
            type.getId());
         doc.setProperty(CMIS.BASE_TYPE_ID, //
            type.getBaseId().value());
         doc.setProperty(CMIS.CREATED_BY, //
            session.getUserID());
         doc.setProperty(CMIS.CREATION_DATE, //
            Calendar.getInstance());
         doc.setProperty(CMIS.LAST_MODIFIED_BY, //
            session.getUserID());
         doc.setProperty(CMIS.LAST_MODIFICATION_DATE, //
            Calendar.getInstance());
         doc.setProperty(CMIS.VERSION_SERIES_ID, //
            doc.getProperty(JcrCMIS.JCR_VERSION_HISTORY).getString());
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
               session.getUserID());
         }

         for (Property<?> property : properties.values())
         {
            setProperty(doc, property);
         }

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
            {
               applyPolicy(doc, policy);
            }
         }

         if (acl != null && acl.size() > 0)
         {
            setACL(doc, acl);
         }

         session.save();

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
      if (type.getContentStreamAllowed() == ContentStreamAllowed.REQUIRED && contentStream == null)
      {
         throw new ConstraintException("Content stream required for object of type " + getTypeId()
            + ", it can't be null.");
      }
      if (type.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED && contentStream != null)
      {
         throw new ConstraintException("Content stream not allowed for object of type " + getTypeId());
      }

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
      // jcr:content
      Node contentNode =
         data.hasNode(JcrCMIS.JCR_CONTENT) ? data.getNode(JcrCMIS.JCR_CONTENT) : data.addNode(JcrCMIS.JCR_CONTENT,
            JcrCMIS.NT_RESOURCE);

      contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, //
         content == null ? "" : content.getMediaType());

      // Re-count content length
      long contentLength = contentNode.setProperty(JcrCMIS.JCR_DATA, //
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   void delete() throws StorageException
   {
      try
      {
         // Check is Document node has any references.
         // It minds Document is multfiled, need remove all links first.
         for (PropertyIterator references = node.getReferences(); references.hasNext();)
         {
            Node next = references.nextProperty().getParent();
            if (next.isNodeType("nt:linkedFile"))
            {
               next.remove();
            }
         }
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }

      // Common delete.
      super.delete();
   }

}
