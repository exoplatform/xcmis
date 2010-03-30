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

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.writer.WriterOptions;
import org.apache.axiom.om.OMElement;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Need this cause to problem in sjsxp-1.0. See CMIS-66 for details.
 * Remove this when change to sjsxp-1.0.1.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: XmlWriter.java 44 2010-02-08 17:36:56Z andrew00x $
 */
class XmlWriter extends PrettyWriter
{

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException
   {
      try
      {
         XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(out, options.getCharset());
         OMElement om =
            (base instanceof Document) ? getOMElement(((Document<Element>)base).getRoot()) : (OMElement)base;
         w.writeStartDocument();
         om.serialize(w);
         w.writeEndDocument();
         //         if (options.getAutoClose())
         out.close();
      }
      catch (XMLStreamException e)
      {
         throw new IOException(e.getMessage());
      }
   }

   /**
    * Gets the oM element.
    * 
    * @param el the el
    * @return the oM element
    */
   private OMElement getOMElement(Element el)
   {
      if (el instanceof ElementWrapper)
         return getOMElement(((ElementWrapper)el).getInternal());
      else
         return (OMElement)el;
   }
}
