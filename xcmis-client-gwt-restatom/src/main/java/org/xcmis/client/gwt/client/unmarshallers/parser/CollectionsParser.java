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
import org.xcmis.client.gwt.client.model.repository.CmisCollection;
import org.xcmis.client.gwt.client.model.restatom.EnumCollectionType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.xml.client.Node;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CollectionsParser
{
   /**
    * Constructor.
    */
   protected CollectionsParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Retrieve information about repositories collections from xml. 
    * 
    * @param node node
    * @return List containing {@link CmisCollection}
    */
   public static List<CmisCollection> parse(Node node)
   {
      List<CmisCollection> collections = new ArrayList<CmisCollection>();
      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node collectionItem = node.getChildNodes().item(i);
         if (collectionItem.getNodeName().equals(CMIS.COLLECTION))
         {
            CmisCollection collection = new CmisCollection();
            for (int j = 0; j < collectionItem.getAttributes().getLength(); j++)
            {
               Node attribute = collectionItem.getAttributes().item(j);

               if (attribute.getNodeName().equals(CMIS.HREF))
               {
                  collection.setHref(attribute.getNodeValue());
               }
            }
            for (int j = 0; j < collectionItem.getChildNodes().getLength(); j++)
            {
               Node item = collectionItem.getChildNodes().item(j);
               if (item.getNodeName().equals(CMIS.CMISRA_COLLECTION_TYPE))
               {
                  collection.setType(EnumCollectionType.fromValue(item.getFirstChild().getNodeValue()));
               }
            }
            collections.add(collection);
         }
      }
      return collections;
   }
}
