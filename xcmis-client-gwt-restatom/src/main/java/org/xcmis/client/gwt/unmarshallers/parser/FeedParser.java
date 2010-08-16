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
import org.xcmis.client.gwt.model.restatom.AtomAcceptType;
import org.xcmis.client.gwt.model.restatom.AtomAuthor;
import org.xcmis.client.gwt.model.restatom.AtomCollectionType;
import org.xcmis.client.gwt.model.restatom.AtomEntry;
import org.xcmis.client.gwt.model.restatom.FeedInfo;
import org.xcmis.client.gwt.model.restatom.FeedType;
import org.xcmis.client.gwt.model.util.DateUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FeedParser
{

   /**
    * Constructor.
    */
   protected FeedParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse xml feed to {@link FeedType}.
    * 
    * @param response response
    * @return {@link FeedType}
    */
   public static FeedType parse(Document response)
   {
      FeedType feed = new FeedType();
      NodeList nodeList = response.getElementsByTagName(CMIS.FEED).item(0).getChildNodes();
      FeedInfo feedInfo = getFeedInfo(nodeList);
      feed.setFeedInfo(feedInfo);

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);

         if (item.getNodeName().equals(CMIS.ATOM_ENTRY))
         {
            AtomEntry entry = new AtomEntry();
            AtomEntryParser.parse(item, entry);
            feed.getEntries().add(entry);
         }
      }
      return feed;
   }

   /**
    * Parse feed info from xml to {@link FeedInfo}.
    * 
    * @param nodeList node list
    * @return {@link FeedInfo}
    */
   public static FeedInfo getFeedInfo(NodeList nodeList)
   {
      FeedInfo feedInfo = new FeedInfo();
      // Go throw all feed nodes
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         if (item.getNodeName().equals(CMIS.ATOM_TITLE))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            feedInfo.setTitle(nodeValue);
         }
         else if (item.getNodeName().equals(CMIS.ATOM_UPDATED))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            feedInfo.setUpdated(DateUtil.parseDate(nodeValue));
         }
         else if (item.getNodeName().equals(CMIS.ATOM_PUBLISHED))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            feedInfo.setPublished(DateUtil.parseDate(nodeValue));
         }
         else if (item.getNodeName().equals(CMIS.ATOM_UPDATED))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            feedInfo.setUpdated(DateUtil.parseDate(nodeValue));
         }
         else if (item.getNodeName().equals(CMIS.ATOM_AUTHOR))
         {
            AtomAuthor author = AtomAuthorParser.parse(item);
            feedInfo.setAuthor(author);
         }
         else if (item.getNodeName().equals(CMIS.ATOM_ID))
         {
            String nodeValue = item.getFirstChild().getNodeValue();
            feedInfo.setId(nodeValue);
         }
         else if (item.getNodeName().equals(CMIS.APP_COLLECTION))
         {
            AtomCollectionType collection = new AtomCollectionType();
            NodeList collectionNodes = item.getChildNodes();
            NamedNodeMap collectionAttributes = item.getAttributes();
            String href = collectionAttributes.item(0).getFirstChild().getNodeValue();
            collection.setHref(href);
            for (int j = 0; j < collectionNodes.getLength(); j++)
            {
               Node collectionNode = collectionNodes.item(j);
               if (collectionNode.getNodeName().equals(CMIS.ATOM_TITLE))
               {
                  String attribValue = collectionNode.getFirstChild().getNodeValue();
                  collection.setTittle(attribValue);
               }
               else if (collectionNode.getNodeName().equals(CMIS.APP_ACCEPT))
               {
                  String attribValue = collectionNode.getFirstChild().getNodeValue();
                  AtomAcceptType accept = new AtomAcceptType();
                  accept.setValue(attribValue);
                  collection.getAccepts().add(accept);
               }
            }
         }
         else if (item.getNodeName().equals(CMIS.ATOM_LINK))
         {
            feedInfo.getLinks().add(AtomLinkParser.parse(item));
         }
      }
      return feedInfo;
   }
}
