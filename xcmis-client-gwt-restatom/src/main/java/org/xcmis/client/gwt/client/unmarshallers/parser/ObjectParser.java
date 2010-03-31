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
import org.xcmis.client.gwt.client.model.CmisAllowableActionsType;
import org.xcmis.client.gwt.client.model.CmisObjectType;
import org.xcmis.client.gwt.client.model.property.CmisPropertiesType;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ObjectParser
{

   /**
    * Constructor.
    */
   protected ObjectParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Retrieve data from xml element to {@link CmisObjectType}.
    * 
    * @param objectNode object node
    * @return {@link CmisObjectType}
    */
   public static CmisObjectType parse(Node objectNode)
   {
      CmisObjectType cmisObjectType = new CmisObjectType();

      // Getting properties and allowableActions separated
      NodeList nodeList = objectNode.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node node = nodeList.item(i);
         //Found properties element to parse
         if (node.getNodeName().equals(CmisNameSpace.CMIS_PROPERTIES))
         {
            CmisPropertiesType cmisProperties = PropertiesParser.parse(node);
            cmisObjectType.setProperties(cmisProperties);
         }
         //Found allowable action to parse
         else if (node.getNodeName().equals(CmisNameSpace.CMIS_ALLOWABLE_ACTIONS))
         {
            CmisAllowableActionsType allowableActions = new CmisAllowableActionsType();
            AllowableActionsParser.parse(node, allowableActions);
            cmisObjectType.setAllowableActions(allowableActions);
         }
      }
      return cmisObjectType;
   }
}
