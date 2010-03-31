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

package org.xcmis.gwtframework.client.unmarshallers;

import org.xcmis.gwtframework.client.model.restatom.TypeCollection;
import org.xcmis.gwtframework.client.rest.Unmarshallable;
import org.xcmis.gwtframework.client.unmarshallers.parser.TypeParser;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * Types' collection unmarshaller.
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeDescendantsUnmarshaller implements Unmarshallable
{
   /**
    * Response with collection of types.
    */
   private TypeCollection typeCollection;

   /**
    * @param typeCollection type collection
    */
   public TypeDescendantsUnmarshaller(TypeCollection typeCollection)
   {
      this.typeCollection = typeCollection;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(java.lang.String)
    * 
    * @param body body
    */
   public void unmarshal(String body)
   {
      Document doc = XMLParser.parse(body);
      typeCollection.setTypes(TypeParser.getTypes(doc));
   }
}
