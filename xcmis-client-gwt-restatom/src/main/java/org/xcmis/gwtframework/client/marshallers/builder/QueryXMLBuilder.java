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
import org.xcmis.gwtframework.client.model.actions.Query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id: 
 */
public class QueryXMLBuilder
{
   
   /**
    * Constructor.
    */
   protected QueryXMLBuilder()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Create request to perform query.
    * 
    * @param query query
    * @return String
    * 
    */
   public static String query(Query query)
   {
      Document doc = XMLParser.createDocument();
      Element entry = doc.createElement(CmisNameSpace.CMIS_QUERY);
      entry.setAttribute(EntryXMLBuilder.XMLNS.getLocalName(), EntryXMLBuilder.XMLNS.getNamespaceURI());
      entry.setAttribute(EntryXMLBuilder.XMLNS_CMIS.getPrefix() + ":" + EntryXMLBuilder.XMLNS_CMIS.getLocalName(),
         EntryXMLBuilder.XMLNS_CMIS.getNamespaceURI());

      Element statement = doc.createElement(CmisNameSpace.CMIS_STATEMENT);
      statement.appendChild(doc.createTextNode(query.getStatement()));

      Element searchAllVersions = doc.createElement(CmisNameSpace.CMIS_SEARCH_ALL_VERSIONS);
      searchAllVersions.appendChild(doc.createTextNode(String.valueOf(query.getSearchAllVersions())));

      Element maxItems = doc.createElement(CmisNameSpace.CMIS_MAX_ITEMS);
      maxItems.appendChild(doc.createTextNode(String.valueOf(query.getMaxItems())));

      Element returnAllowableActions = doc.createElement(CmisNameSpace.CMIS_RETURN_ALLOWABLE_ACTIONS);
      returnAllowableActions.appendChild(doc.createTextNode("true"));

      Element skipCount = doc.createElement(CmisNameSpace.CMIS_SKIP_COUNT);
      skipCount.appendChild(doc.createTextNode(String.valueOf(query.getSkipCount())));

      entry.appendChild(statement);
      entry.appendChild(searchAllVersions);
      entry.appendChild(returnAllowableActions);
      entry.appendChild(maxItems);
      entry.appendChild(skipCount);
      doc.appendChild(entry);

      String request = EntryXMLBuilder.XML + doc.toString().trim();
      GWT.log(request, null);
      return request;
   }
}
