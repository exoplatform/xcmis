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

import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AbderaResponseEntityProvider.java 44 2010-02-08 17:36:56Z
 *          andrew00x $
 */
@Provider
public class AbderaResponseEntityProvider implements MessageBodyWriter<ResponseContext>
{

   /**
    * {@inheritDoc}
    */
   public long getSize(ResponseContext t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return ResponseContext.class.isAssignableFrom(type);
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(ResponseContext t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
      WebApplicationException
   {
      if (t.hasEntity())
      {
         if (t instanceof BinaryResponseContext)
            t.writeTo(entityStream);
         else
            t.writeTo(entityStream, new XmlWriter());
      }
      else
      {
         if (t instanceof EmptyResponseContext)
         {
            String text = t.getStatusText();
            if (text != null)
            {
               String csname = mediaType.getParameters().get("charset");
               Charset cs = csname != null ? Charset.forName(csname) : Charset.forName("UTF-8");
               Writer w = new OutputStreamWriter(entityStream, cs);
               try
               {
                  w.write(text);
               }
               finally
               {
                  w.flush();
               }
            }
         }
      }
   }

}
