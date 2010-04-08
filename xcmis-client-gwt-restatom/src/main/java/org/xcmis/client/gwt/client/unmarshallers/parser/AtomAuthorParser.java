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

package org.xcmis.client.gwt.client.unmarshallers.parser;

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.restatom.AtomAuthor;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AtomAuthorParser
{
   /**
    * Constructor.
    */
   protected AtomAuthorParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * Parse xml document and return {@link AtomAuthor}.
    * 
    * @param response response
    * @return {@link AtomAuthor}
    */
   public static AtomAuthor parse(Node response)
   {
      AtomAuthor author = new AtomAuthor();
      NodeList nodeList = response.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         String nodeValue = item.getFirstChild().getNodeValue();
         if (item.getNodeName().equals(CMIS.ATOM_NAME))
         {
            author.setName(nodeValue);
         }
         else if (item.getNodeName().equals(CMIS.ATOM_EMAIL))
         {
            author.setEmail(nodeValue);
         }
         else if (item.getNodeName().equals(CMIS.ATOM_URI))
         {
            author.setUri(nodeValue);
         }
      }
      return author;
   }
}
