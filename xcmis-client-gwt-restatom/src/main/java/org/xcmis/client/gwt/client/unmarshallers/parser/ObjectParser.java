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
import org.xcmis.client.gwt.client.model.AllowableActions;
import org.xcmis.client.gwt.client.model.property.Property;
import org.xcmis.client.gwt.client.object.CmisObject;
import org.xcmis.client.gwt.client.object.impl.CmisObjectImpl;

import java.util.Map;

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
   public static CmisObject parse(Node objectNode)
   {
      CmisObjectImpl cmisObject = new CmisObjectImpl();

      // Getting properties and allowableActions separated
      NodeList nodeList = objectNode.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node node = nodeList.item(i);
         //Found properties element to parse
         if (node.getNodeName().equals(CMIS.CMIS_PROPERTIES))
         {
            Map<String, Property<?>> properties = PropertiesParser.parse(node);
            cmisObject.getProperties().setProperties(properties);
         }
         //Found allowable action to parse
         else if (node.getNodeName().equals(CMIS.CMIS_ALLOWABLE_ACTIONS))
         {
            AllowableActions allowableActions = new AllowableActions();
            AllowableActionsParser.parse(node, allowableActions);
            cmisObject.setAllowableActions(allowableActions);
         }
      }
      return cmisObject;
   }
}
