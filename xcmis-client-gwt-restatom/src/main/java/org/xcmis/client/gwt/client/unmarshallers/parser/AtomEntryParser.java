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

import org.xcmis.client.gwt.client.CmisNameSpace;
import org.xcmis.client.gwt.client.model.CmisObjectType;
import org.xcmis.client.gwt.client.model.restatom.AtomAuthor;
import org.xcmis.client.gwt.client.model.restatom.AtomContentType;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;
import org.xcmis.client.gwt.client.model.restatom.EntryInfo;
import org.xcmis.client.gwt.client.model.util.DateUtil;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AtomEntryParser
{
   /**
    * Constructor.
    */
   protected AtomEntryParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * Parse entry xml document to {@link AtomEntry}.
    * 
    * @param response response
    * @param entry entry
    */
   public static void parse(Node response, AtomEntry entry)
   {
      NodeList nodeList = response.getChildNodes();
      entry.setEntryInfo(getEntryInfo(nodeList));
      Node childrenNode = null;
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         if (item.getNodeName().equals(CmisNameSpace.CMISRA_OBJECT))
         {
            CmisObjectType cmisObject = ObjectParser.parse(item);
            entry.setObject(cmisObject);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMISRA_CHILDREN))
         {
            childrenNode = item;
         }
      }
      if (childrenNode != null)
      {
         // entry has children node - parse it's children
         setChildren(entry, childrenNode);
      }
   }

   /**
    * Parse children node and set children entries to it's parent.
    * 
    * @param parent parent
    * @param childrenNode children node
    */
   private static void setChildren(AtomEntry parent, Node childrenNode)
   {
      if (childrenNode.getChildNodes() != null && childrenNode.getChildNodes().getLength() > 0)
      {
         for (int i = 0; i < childrenNode.getChildNodes().getLength(); i++)
         {
            if (childrenNode.getChildNodes().item(i).getNodeName().equals(CmisNameSpace.FEED))
            {
               childrenNode = childrenNode.getChildNodes().item(i);
            }
         }
         // Go throw all nodes of feed to find entries (children of parent entry)
         for (int i = 0; i < childrenNode.getChildNodes().getLength(); i++)
         {
            Node node = childrenNode.getChildNodes().item(i);
            if (node.getNodeName().equals(CmisNameSpace.ENTRY))
            {
               AtomEntry entry = new AtomEntry();
               parse(node, entry);
               parent.getChildren().add(entry);
            }
         }
      }
   }

   /**
    * @param nodeList node list
    * @return {@link EntryInfo}
    */
   public static EntryInfo getEntryInfo(NodeList nodeList)
   {
      EntryInfo entryInfo = new EntryInfo();
      int i = nodeList.getLength() - 1;
      //parse entry nodes to form entry info
      while ((i >= 0) && (nodeList.getLength() > 0))
      {
         Node item = nodeList.item(i);
         if (item.getNodeName().equals(CmisNameSpace.ATOM_AUTHOR))
         {
            AtomAuthor author = AtomAuthorParser.parse(item);
            entryInfo.setAuthor(author);
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_CONTENT))
         {
            AtomContentType content = new AtomContentType();
            for (int k = 0; k < item.getAttributes().getLength(); k++)
            {
               if (item.getAttributes().item(k).getNodeName().equals(CmisNameSpace.SOURCE))
               {
                  content.setSource(item.getAttributes().item(k).getFirstChild().getNodeValue());
               }
               else if (item.getAttributes().item(k).getNodeName().equals(CmisNameSpace.TYPE))
               {
                  content.setType(item.getAttributes().item(k).getFirstChild().getNodeValue());
               }
            }
            entryInfo.setContent(content);
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_ID))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            entryInfo.setId(nodeValue);
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_SUMMARY))
         {
            if (item.getFirstChild() != null)
            {
               String nodeValue = item.getFirstChild().getNodeValue();
               entryInfo.setSummary(nodeValue);
            }
            else
            {
               entryInfo.setSummary("");
            }
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_TITLE))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            entryInfo.setTitle(nodeValue);
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_UPDATED))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            entryInfo.setUpdated(DateUtil.parseDate(nodeValue));
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_PUBLISHED))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            entryInfo.setPublished(DateUtil.parseDate(nodeValue));
            item.getParentNode().removeChild(item);
         }
         else if (item.getNodeName().equals(CmisNameSpace.ATOM_LINK))
         {
            entryInfo.getLinks().add(AtomLinkParser.parse(item));
            item.getParentNode().removeChild(item);
         }
         i--;
      }
      return entryInfo;
   }

}
