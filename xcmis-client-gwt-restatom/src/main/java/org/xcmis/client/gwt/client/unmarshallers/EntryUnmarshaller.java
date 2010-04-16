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

package org.xcmis.client.gwt.client.unmarshallers;

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;
import org.xcmis.client.gwt.client.object.ObjectData;
import org.xcmis.client.gwt.client.rest.Unmarshallable;
import org.xcmis.client.gwt.client.unmarshallers.parser.AtomEntryParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class EntryUnmarshaller implements Unmarshallable
{
   /**
    * Response with entry.
    */
   private AtomEntry entry;

   /**
    * @param entry entry
    */
   public EntryUnmarshaller(AtomEntry entry)
   {
      this.entry = entry;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(java.lang.String)
    * 
    * @param body body
    */
   public void unmarshal(String body)
   {
      GWT.log("Entry " + body, null);
      Document doc = XMLParser.parse(body);
      NodeList nodeList = doc.getElementsByTagName(CMIS.ENTRY);
      if (nodeList != null && nodeList.getLength() > 0)
      {
         Node entryNode = nodeList.item(0);
         AtomEntryParser.parse(entryNode, entry);
         ObjectData.extractData(entry);
      }
   }
}
