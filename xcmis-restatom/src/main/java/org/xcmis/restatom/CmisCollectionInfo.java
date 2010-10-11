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

package org.xcmis.restatom;

import org.apache.abdera.model.Collection;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

/**
 * Provides information used to construct an app:collection element. Such
 * element will be add in Atompub Service Document (section 3.6 of CMIS
 * specification).
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class CmisCollectionInfo implements CollectionInfo
{

   private static final String[] accept = new String[]{AtomCMIS.MEDIATYPE_ATOM_ENTRY};

   private static final CategoriesInfo[] noCategories = new CategoriesInfo[0];

   private static final String[] noSegments = new String[0];

   private final String title;

   private final String relativeHref;

   private final String collectionType;

   CmisCollectionInfo(String title, String relativeHref, String collectionType)
   {
      this.title = title;
      this.relativeHref = relativeHref;
      this.collectionType = collectionType;
   }

   /**
    * {@inheritDoc}
    */
   public Collection asCollectionElement(RequestContext request)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Convert this to an instance of the FOM Collection interface.
    *
    * @param request request
    * @param baseUri base URI. It will be used to provider href attribute in
    *        collection element
    * @param segments additional path segments. It will be used in href
    *        attribute of collection element, e.g.
    *        <code>baseUri + relativeHref + segments</code>
    * @return collection element
    */
   public Collection asCollectionElement(RequestContext request, String baseUri, String... segments)
   {
      Collection collection = request.getAbdera().getFactory().newCollection();
      collection.setHref(getHref(request, baseUri, segments));
      collection.setTitle(getTitle(request));
      collection.addSimpleExtension(AtomCMIS.COLLECTION_TYPE, collectionType);
      return collection;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getAccepts(RequestContext request)
   {
      return accept;
   }

   /**
    * {@inheritDoc}
    */
   public CategoriesInfo[] getCategoriesInfo(RequestContext request)
   {
      return noCategories;
   }

   /**
    * CMIS collection type.
    *
    * @return collection type
    */
   public String getCollectionType()
   {
      return collectionType;
   }

   /**
    * {@inheritDoc}
    */
   public String getHref(RequestContext request)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Get the value of the app:collection element's href attribute.
    *
    * @param request request
    * @param baseUri base URI. It will be used to provider href attribute in
    *        collection element
    * @param segments additional path segments. It will be used in href
    *        attribute of collection element, e.g.
    *        <code>baseUri + relativeHref + segments</code>
    * @return href attribute of collection element
    */
   public String getHref(RequestContext request, String baseUri, String... segments)
   {
      URI uri =
         UriBuilder.fromUri(baseUri).path(relativeHref).segment(segments == null ? noSegments : segments).build();
      return uri.toString();
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return title;
   }

}
