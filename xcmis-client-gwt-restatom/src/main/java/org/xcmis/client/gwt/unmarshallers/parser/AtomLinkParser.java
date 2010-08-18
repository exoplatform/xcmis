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

package org.xcmis.client.gwt.unmarshallers.parser;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.restatom.AtomLink;
import org.xcmis.client.gwt.model.restatom.EnumLinkRelation;

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
         Node attribute = attributesList.item(j);
         
         String attributeValue = attribute.getNodeValue();
         String attributeName = attribute.getNodeName();
         if (attributeName.equals(CMIS.RELATION))
         {
            link.setRelation(EnumLinkRelation.fromValue(attributeValue));
         }
         else if (attributeName.equals(CMIS.HREF))
         {
            link.setHref(attributeValue);
         }
         else if (attributeName.equals(CMIS.TYPE))
         {
            link.setType(attributeValue);
         }
      } //end cycle 
      return link;
   }
   
}
