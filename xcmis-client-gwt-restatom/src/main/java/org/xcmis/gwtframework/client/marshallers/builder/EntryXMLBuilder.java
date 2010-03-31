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

package org.xcmis.gwtframework.client.marshallers.builder;

import org.xcmis.gwtframework.client.CmisNameSpace;
import org.xcmis.gwtframework.client.rest.QName;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Is used to create entry node in xml document.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class EntryXMLBuilder
{
   /**
    * Constructor.
    */
   protected EntryXMLBuilder()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * XML.
    */
   public static final String XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

   /**
    * XMLNS.
    */
   public static final QName XMLNS = new QName("xmlns", "http://www.w3.org/2005/Atom");

   /**
    * XMLNS CMIS.
    */
   public static final QName XMLNS_CMIS = new QName("xmlns:cmis", "http://docs.oasis-open.org/ns/cmis/core/200908/");

   /**
    * XMLNS CMISRA.
    */
   public static final QName XMLNS_CMISRA =
      new QName("xmlns:cmisra", "http://docs.oasis-open.org/ns/cmis/restatom/200908/");

   /**
    * XMLNS CMISM.
    */
   public static final QName XMLNS_CMISM =
      new QName("xmlns:cmism", "http://docs.oasis-open.org/ns/cmis/messaging/200908/");

   /**
    * XMLNS ATOM.
    */
   public static final QName XMLNS_ATOM = new QName("xmlns:atom", "http://www.w3.org/2005/Atom");

   /**
    * XMLNS APP.
    */
   public static final QName XMLNS_APP = new QName("xmlns:app", "http://www.w3.org/2007/app");

   /**
    * Create entry node with its attributes.
    *    
    * @param doc doc
    * @return {@link Element}
    */
   public static Element createEntryElement(Document doc)
   {
      Element entry = doc.createElement(CmisNameSpace.ENTRY);
      //Set entry xml element attributes
      entry.setAttribute(XMLNS.getLocalName(), XMLNS.getNamespaceURI());
      entry.setAttribute(XMLNS_CMIS.getPrefix() + ":" + XMLNS_CMIS.getLocalName(), XMLNS_CMIS.getNamespaceURI());
      entry.setAttribute(XMLNS_CMISRA.getPrefix() + ":" + XMLNS_CMISRA.getLocalName(), XMLNS_CMISRA.getNamespaceURI());
      return entry;
   }

   /**
    * Create full xml request.
    * 
    * @param document document
    * @return String
    */
   public static String createStringRequest(Document document)
   {
      //Check "xmlns" attribute is mentioned (FF cuts it), if not - put it manually
      String request = XML + document.toString().trim();
      if (request.indexOf(XMLNS.getNamespaceURI()) == -1)
      {
         return request.replaceAll("<" + CmisNameSpace.ENTRY, "<" + CmisNameSpace.ENTRY + " " + XMLNS.getLocalName() + "=" + "\"" + XMLNS.getNamespaceURI() + "\" ");
      }

      GWT.log(request, null);
      return request;
   }

}
