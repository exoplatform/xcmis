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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class PWC extends DocumentImpl
{
   /** Latest version of document. */
   private final DocumentImpl document;

   public PWC(Document document, Session session)
   {
      super(document.getTypeDefinition(), null, session, null);
      this.document = (DocumentImpl)document;
   }

   public PWC(TypeDefinition type, Node node, Document document)
   {
      super(type, node);
      this.document = (DocumentImpl)document;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void cancelCheckout() throws StorageException
   {
      try
      {
         Node docNode = document.getNode();

         docNode.setProperty(CMIS.IS_LATEST_VERSION, //
            true);
         docNode.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            false);
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
            (String)null);
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
            (String)null);

         node.remove();

         session.save();

      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable cancel checkout Document. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      try
      {
         Node docNode = document.getNode();

         docNode.checkin();
         docNode.checkout();

         docNode.setProperty(CMIS.IS_LATEST_VERSION, //
            true);
         docNode.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            false);
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
            (String)null);
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
            (String)null);
         // Update creation date & last modification date
         // to emulate creation new version.
         docNode.setProperty(CMIS.CREATED_BY, //
            session.getUserID());
         docNode.setProperty(CMIS.CREATION_DATE, //
            Calendar.getInstance());
         docNode.setProperty(CMIS.LAST_MODIFIED_BY, //
            session.getUserID());
         docNode.setProperty(CMIS.LAST_MODIFICATION_DATE, //
            Calendar.getInstance());
         //
         docNode.setProperty(CMIS.IS_MAJOR_VERSION, //
            major);
         if (checkinComment != null)
         {
            docNode.setProperty(CMIS.CHECKIN_COMMENT, //
               checkinComment);
         }

         try
         {
            // TODO : Need to check if contents are the same then not was
            // not updated not need to change
            setContentStream(docNode, getContentStream());
         }
         catch (IOException ioe)
         {
            throw new CmisRuntimeException("Unable copy content for new document. " + ioe.getMessage(), ioe);
         }

         node.getParent().remove();

         session.save();

         return document;
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable checkin Document. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   void delete() throws StorageException
   {
      cancelCheckout();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Folder getParent() throws ConstraintException
   {
      return document.getParent();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<Folder> getParents()
   {
      return document.getParents();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isPWC()
   {
      return true;
   }

   @Override
   protected void create() throws StorageException, NameConstraintViolationException
   {
      try
      {
         name = document.getName();

         Node workingCopies =
            (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_WORKING_COPIES);

         Node wc = workingCopies.addNode(document.getObjectId(), "xcmis:workingCopy");

         Node pwc = wc.addNode(name, type.getLocalName());

         if (!pwc.isNodeType(JcrCMIS.CMIS_MIX_DOCUMENT))
         {
            pwc.addMixin(JcrCMIS.CMIS_MIX_DOCUMENT);
         }
         if (pwc.canAddMixin(JcrCMIS.MIX_VERSIONABLE))
         {
            pwc.addMixin(JcrCMIS.MIX_VERSIONABLE);
         }

         pwc.setProperty(CMIS.OBJECT_TYPE_ID, //
            type.getId());
         pwc.setProperty(CMIS.BASE_TYPE_ID, //
            type.getBaseId().value());
         pwc.setProperty(CMIS.CREATED_BY, //
            session.getUserID());
         pwc.setProperty(CMIS.CREATION_DATE, //
            Calendar.getInstance());
         pwc.setProperty(CMIS.LAST_MODIFIED_BY, //
            session.getUserID());
         pwc.setProperty(CMIS.LAST_MODIFICATION_DATE, //
            Calendar.getInstance());
         pwc.setProperty(CMIS.VERSION_SERIES_ID, //
            document.getVersionSeriesId());
         pwc.setProperty(CMIS.IS_LATEST_VERSION, //
            true);
         pwc.setProperty(CMIS.IS_MAJOR_VERSION, //
            false);
         pwc.setProperty(CMIS.VERSION_LABEL, //
            pwcLabel);
         pwc.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            true);
         pwc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
            ((ExtendedNode)pwc).getIdentifier());
         pwc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
            session.getUserID());

         pwc.setProperty("xcmis:latestVersionId", //
            document.getObjectId());

         // TODO : copy the other properties from document.

         try
         {
            // TODO : use native JCR ??
            setContentStream(pwc, document.getContentStream());
         }
         catch (IOException ioe)
         {
            throw new CmisRuntimeException("Unable copy content for new document. " + ioe.getMessage(), ioe);
         }

         // Update source document.
         Node docNode = document.getNode();
         docNode.setProperty(CMIS.IS_LATEST_VERSION, //
            false);
         docNode.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            true);
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
            ((ExtendedNode)pwc).getIdentifier());
         docNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
            session.getUserID());

         session.save();

         name = null;

         node = pwc;
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable save Document. " + re.getMessage(), re);
      }
   }

}
