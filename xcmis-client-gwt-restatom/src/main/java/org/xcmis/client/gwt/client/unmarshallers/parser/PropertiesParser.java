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
import org.xcmis.client.gwt.client.model.property.CmisPropertiesType;
import org.xcmis.client.gwt.client.model.property.CmisPropertyBoolean;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDateTime;
import org.xcmis.client.gwt.client.model.property.CmisPropertyId;
import org.xcmis.client.gwt.client.model.property.CmisPropertyInteger;
import org.xcmis.client.gwt.client.model.property.CmisPropertyString;
import org.xcmis.client.gwt.client.model.property.CmisPropertyUri;
import org.xcmis.client.gwt.client.model.util.DateUtil;

import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class PropertiesParser
{

   /**
    * Constructor.
    */
   protected PropertiesParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Property definition id.
    */
   private static String propertyDefinitionId = "";

   /**
    * Local name.
    */
   private static String localName = "";

   /**
    * Query name.
    */
   private static String queryName = "";

   /**
    * Display name.
    */
   private static String displayName = "";

   /**
    * Parse properties xml element to {@link CmisPropertiesType}.
    * 
    * @param node node
    * @return CmisPropertiesType CMIS properties type
    */
   public static CmisPropertiesType parse(Node node)
   {
      CmisPropertiesType properties = new CmisPropertiesType();

      NodeList nodeList = node.getChildNodes();

      //Go throw all properties
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         if (item.toString().trim().length() > 0)
         {
            //Keep property attributes temporary in fields
            getPropertyAttributes(item);

            String nodeValue = "";
            // Getting each property value
            NodeList listOfValues = item.getChildNodes();
            if (listOfValues.getLength() == 1)
            {
               if (listOfValues.item(0).getFirstChild() == null)
               {
                  nodeValue = "";
               }
               else
               {
                  nodeValue = listOfValues.item(0).getFirstChild().getNodeValue();
               }
            }
            if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_STRING))
            {
               CmisPropertyString property = new CmisPropertyString();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);

               for (int k = 0; k < listOfValues.getLength(); k++)
               {
                  if (listOfValues.item(k).getFirstChild() == null)
                  {
                     nodeValue = "";
                  }
                  else
                  {
                     nodeValue = listOfValues.item(k).getFirstChild().getNodeValue();
                  }
                  property.getValue().add(nodeValue);
               }
               properties.getProperty().add(property);
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_BOOLEAN))
            {
               CmisPropertyBoolean property = new CmisPropertyBoolean();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);
               property.getValue().add(Boolean.parseBoolean(nodeValue));
               properties.getProperty().add(property);

            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_INTEGER))
            {
               CmisPropertyInteger property = new CmisPropertyInteger();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);
               for (int k = 0; k < listOfValues.getLength(); k++)
               {
                  nodeValue = listOfValues.item(k).getFirstChild().getNodeValue();
                  property.getValue().add(Integer.parseInt(nodeValue));
               }
               properties.getProperty().add(property);
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_URI))
            {
               CmisPropertyUri property = new CmisPropertyUri();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);
               for (int k = 0; k < listOfValues.getLength(); k++)
               {
                  nodeValue = listOfValues.item(k).getFirstChild().getNodeValue();
                  property.getValue().add(nodeValue);
               }
               properties.getProperty().add(property);
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_ID))
            {
               CmisPropertyId property = new CmisPropertyId();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);
               property.getValue().add(nodeValue);
               properties.getProperty().add(property);
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_DATE_TIME))
            {
               CmisPropertyDateTime property = new CmisPropertyDateTime();
               property.setPropertyDefinitionId(propertyDefinitionId);
               property.setLocalName(localName);
               property.setQueryName(queryName);
               property.setDisplayName(displayName);
               property.getValue().add(DateUtil.parseDate(nodeValue));
               properties.getProperty().add(property);
            }
         }
      }
      return properties;
   }

   /**
    * Get  propertyDefinitionId, localName,
    * queryName, displayName from property xml element's attributes.
    * 
    * @param property property
    */
   private static void getPropertyAttributes(Node property)
   {
      String propertyDefinitionIdTmp = "";
      String localNameTmp = "";
      String queryNameTmp = "";
      String displayNameTmp = "";

      NamedNodeMap nodeListOfAttributes = property.getAttributes();
      for (int j = 0; j < nodeListOfAttributes.getLength(); j++)
      {
         if (nodeListOfAttributes.item(j).getNodeName().equals(CmisNameSpace.LOCAL_NAME))
         {
            localNameTmp = nodeListOfAttributes.item(j).getFirstChild().getNodeValue();
         }
         else if (nodeListOfAttributes.item(j).getNodeName().equals(CmisNameSpace.QUERY_NAME))
         {
            queryNameTmp = nodeListOfAttributes.item(j).getFirstChild().getNodeValue();
         }
         else if (nodeListOfAttributes.item(j).getNodeName().equals(CmisNameSpace.DISPLAY_NAME))
         {
            displayNameTmp = nodeListOfAttributes.item(j).getFirstChild().getNodeValue();
         }
         else if (nodeListOfAttributes.item(j).getNodeName().equals(CmisNameSpace.PROPERTY_DEFINITION_ID))
         {
            propertyDefinitionIdTmp = nodeListOfAttributes.item(j).getFirstChild().getNodeValue();
         }
      }

      propertyDefinitionId = propertyDefinitionIdTmp;
      localName = localNameTmp;
      queryName = queryNameTmp;
      displayName = displayNameTmp;
   }
}
