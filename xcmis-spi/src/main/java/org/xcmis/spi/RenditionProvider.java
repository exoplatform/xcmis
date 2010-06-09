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

import org.xcmis.spi.utils.MimeType;

import java.io.IOException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface RenditionProvider
{

   /**
    * Create content stream of rendition for specified object.
    *
    * @param stream source stream
    * @return rendition stream
    * @throws IOException if any i/o error occurs
    */
   RenditionContentStream getRenditionStream(ContentStream stream) throws IOException;

   /**
    * Get a list of source media types supported by this provider. For example:
    * [image/*] says provider is able to create renditions for all images.
    *
    * @return set of supported media types
    */
   MimeType[] getSupportedMediaType();

   /**
    * Get media type of produced rendition content stream
    *
    * @return produced content stream
    */
   MimeType getProducedMediaType();

   /**
    * Get rendition kind. At the moment just one expected kind 'cmis:thumbnail'.
    *
    * @return rendition kind
    */
   String getKind();

   /**
    * Get rendition height. It should provider height of thumbnail if rendition
    * type is 'cmis:thumbnail'.
    *
    * @return rendition height or -1 if not able to determine height or
    *         rendition kind is not 'cmis:thumbnail
    */
   int getHeight();

   /**
    * Get rendition width. It should provider width of thumbnail if rendition
    * type is 'cmis:thumbnail'.
    *
    * @return rendition width or -1 if not able to determine width or rendition
    *         kind is not 'cmis:thumbnail
    */
   int getWidth();
}
