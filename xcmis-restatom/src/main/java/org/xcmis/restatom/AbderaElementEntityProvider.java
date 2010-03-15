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

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.exoplatform.services.rest.provider.EntityProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Created by The eXo Platform SAS .
 * 
 * @version $Id: AbderaElementEntityProvider.java 2192 2009-07-17 13:19:12Z
 *          sunman $
 */
@Provider
@Consumes({MediaType.APPLICATION_ATOM_XML})
@Produces({MediaType.APPLICATION_ATOM_XML, "application/cmis+xml;type=allowableActions", "application/cmisacl+xml"})
public class AbderaElementEntityProvider implements EntityProvider<Element>
{

   /**
    * {@inheritDoc}
    */
   public long getSize(Element element, Class<?> clazz, Type type, Annotation[] anno, MediaType mediaType)
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(Class<?> clazz, Type type, Annotation[] anno, MediaType mediaType)
   {
      return Element.class.isAssignableFrom(clazz);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno, MediaType mediaType)
   {
      return Element.class.isAssignableFrom(clazz);
   }

   /**
    * {@inheritDoc}
    */
   public Element readFrom(Class<Element> clazz, Type type, Annotation[] anno, MediaType mediaType,
      MultivaluedMap<String, String> headers, InputStream is) throws IOException, WebApplicationException
   {
      Parser parser = AbderaFactory.getInstance().getFactory().newParser();
      Document<Element> doc = parser.parse(is);
      return doc.getRoot();
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(Element element, Class<?> clazz, Type type, Annotation[] anno, MediaType mediaType,
      MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException
   {
      element.writeTo(new XmlWriter(), os);
   }

}
