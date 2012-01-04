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

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.commons.io.IOUtils;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.types.CmisContentType;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ContentTypeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ContentTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new content type element.
    * 
    * @param internal the internal
    */
   public ContentTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new content type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ContentTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Gets the content.
    * 
    * @return content CmisContentType
    * @throws IOException 
    */
   public CmisContentType getContent() throws IOException
   {
      CmisContentType content = new CmisContentType();
      content.setMediatype(getSimpleExtension(AtomCMIS.MEDIATYPE));

      ContentOutputStream contentOutput = new ContentOutputStream();
      try {
        ((FOMExtensibleElement) getExtension(AtomCMIS.BASE64)).writeTo(contentOutput);
      } finally {
        contentOutput.close();
      }
      
      InputStream inputStream = contentOutput.getInputStream();
      content.setBase64(inputStream);
      return content;
   }

   /**
    * Builds the element.
    * 
    * @param contentType the content type
    * @deprecated
    */
   public void build(CmisContentType contentType)
   {
      if (contentType != null)
      {
         addSimpleExtension(AtomCMIS.MEDIATYPE, contentType.getMediatype());
         String base64 = "";
         try {
            byte[] b = IOUtils.toByteArray(contentType.getBase64());
            base64 = new String(b);
         } catch (IOException e) {
         }
         addSimpleExtension(AtomCMIS.BASE64, base64);
      }
   }

}
