/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.marshallers;

import org.xcmis.client.gwt.marshallers.builder.ObjectXMLBuilder;
import org.xcmis.client.gwt.model.actions.CreateDocument;
import org.xcmis.client.gwt.rest.Marshallable;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class CreateDocumentMarshaller implements Marshallable
{

   /**
    * Data for creating new document.
    */
   private CreateDocument createDocument;
   
  
   /**
    * Location of content stream source (optional).
    */
   private String contentSourceUrl;

   /**
    * @param createDocument createDocument
    */
   public CreateDocumentMarshaller(CreateDocument createDocument)
   {
      this.createDocument = createDocument;
   }
   
   public CreateDocumentMarshaller(CreateDocument createDocument, String contentSourceUrl)
   {
      this.createDocument = createDocument;
      this.contentSourceUrl = contentSourceUrl;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    * @return String xml request
    */
   public String marshal()
   {
      return ObjectXMLBuilder.createDocument(createDocument, contentSourceUrl);
   }
}
