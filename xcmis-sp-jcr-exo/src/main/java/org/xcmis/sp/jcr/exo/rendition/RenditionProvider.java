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

package org.xcmis.sp.jcr.exo.rendition;

import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.ContentStream;

import java.io.IOException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RenditionProvider.java 304 2010-03-09 10:07:06Z ur3cma $
 */
public interface RenditionProvider
{
   
   /**
    * Create rendition for specified entry.
    * 
    * @param entry source entry
    * @return newly created rendition stream
    * @throws IOException if any i/o error occurs
    * @throws RepositoryException if any other CMIS repository error occurs
    */
  RenditionContentStream getRenditionStream(ContentStream stream) throws IOException, RepositoryException;

   /**
    * Get a list of source media types supported by this provider.
    * For example: [image/*] says provider is able to create renditions for all images.
    * 
    * @return set of supported media types
    */
   String[] getSupportedMediaType();
   
   /**
    * Indicates whether rendition must be stored in JCR or generated in runtime.
    * 
    * @return boolean store in JCR
    */
   boolean canStoreRendition();

}
