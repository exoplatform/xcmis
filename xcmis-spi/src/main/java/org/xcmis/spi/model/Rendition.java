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

package org.xcmis.spi.model;

/**
 * CMIS rendition.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Rendition
{

   /**
    * Identifies the rendition stream ID.
    * 
    * @return rendition stream ID
    */
   String getStreamId();

   /**
    * The mime-type of the rendition stream.
    * 
    * @return rendition stream mime-type
    */
   String getMimeType();

   /**
    * @return length of the rendition stream in bytes. This attribute is
    *         optional
    */
   long getLength();

   /**
    * A categorization String associated with the rendition.
    * 
    * @return rendition kind
    */
   String getKind();

   /**
    * Optional human readable information about the rendition.
    * 
    * @return rendition title or <code>null</code> if not provided
    */
   String getTitle();

   /**
    * Typically used for 'image' renditions (expressed as pixels). Should be
    * provided if kind of rendition is 'cmis:thumbnail'.
    * 
    * @return rendition height
    */
   int getHeight();

   /**
    * Typically used for 'image' renditions (expressed as pixels). Should be
    * provided if kind of rendition is 'cmis:thumbnail'.
    * 
    * @return rendition weight
    */
   int getWidth();

   /**
    * If specified, then the rendition can also be accessed as a document object
    * in the CMIS services. If not set, then the rendition can only be accessed
    * via the rendition services.
    * 
    * @return rendition document ID or <code>null</code> if rendition is not
    *         provided as separate document
    */
   String getRenditionDocumentId();

}
