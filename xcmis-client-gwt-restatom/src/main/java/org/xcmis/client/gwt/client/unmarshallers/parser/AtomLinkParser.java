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
import org.xcmis.client.gwt.client.model.restatom.AtomLink;
import org.xcmis.client.gwt.client.model.restatom.EnumLinkRelation;

import com.google.gwt.http.client.URL;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AtomLinkParser
{
   
   /**
    * Constructor.
    */
   protected AtomLinkParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse link xml node to get {@link AtomLink}.
    * 
    * @param item item
    * @return {@link AtomLink}
    */
   public static AtomLink parse(Node item)
   {
      // in link attributes there are type, href, rel elements
      NamedNodeMap attributesList = item.getAttributes();
      AtomLink link = new AtomLink();
      for (int j = 0; j < attributesList.getLength(); j++)
      {
         String type = "";
         String href = "";
         String rel = "";

         Node attribute = attributesList.item(j);
         
         String attributeValue = attribute.getNodeValue();
         String attributeName = attribute.getNodeName();
         if (attributeName.equals(CMIS.RELATION))
         {
            rel = attributeValue;
            link.setRelation(EnumLinkRelation.fromValue(rel));
         }
         else if (attributeName.equals(CMIS.HREF))
         {
            href = URL.decodeComponent(attributeValue);
            link.setHref(href);
         }
         else if (attributeName.equals(CMIS.TYPE))
         {
            type = attributeValue;
            link.setType(type);
         }
      } //end cycle 
      return link;
   }
   
}
