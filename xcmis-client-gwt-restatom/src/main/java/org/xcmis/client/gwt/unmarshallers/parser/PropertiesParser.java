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
import org.xcmis.client.gwt.model.property.BooleanProperty;
import org.xcmis.client.gwt.model.property.DateTimeProperty;
import org.xcmis.client.gwt.model.property.DecimalProperty;
import org.xcmis.client.gwt.model.property.HtmlProperty;
import org.xcmis.client.gwt.model.property.IdProperty;
import org.xcmis.client.gwt.model.property.IntegerProperty;
import org.xcmis.client.gwt.model.property.Property;
import org.xcmis.client.gwt.model.property.StringProperty;
import org.xcmis.client.gwt.model.property.UriProperty;
import org.xcmis.client.gwt.model.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class PropertiesParser
{

   /**
    * Constructor.
    */
   protected PropertiesParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from
      // subclass
   }

   /**
    * Parse properties xml element to {@link CmisPropertiesType}.
    * 
    * @param node
    *            node
    * @return CmisPropertiesType CMIS properties type
    */
   public static Map<String, Property<?>> parse(Node node)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      NodeList nodeList = node.getChildNodes();

      if (nodeList.getLength() <= 0)
      {
         return properties;
      }

      // Go throw all properties
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         if (item.toString().trim().length() > 0)
         {
            NamedNodeMap nodeListOfAttributes = item.getAttributes();

            String propertyDefinitionId =
               (nodeListOfAttributes.getNamedItem(CMIS.PROPERTY_DEFINITION_ID) != null) ? nodeListOfAttributes
                  .getNamedItem(CMIS.PROPERTY_DEFINITION_ID).getNodeValue() : null;
            String localName =
               (nodeListOfAttributes.getNamedItem(CMIS.LOCAL_NAME) != null) ? nodeListOfAttributes.getNamedItem(
                  CMIS.LOCAL_NAME).getNodeValue() : null;
            String queryName =
               (nodeListOfAttributes.getNamedItem(CMIS.QUERY_NAME) != null) ? nodeListOfAttributes.getNamedItem(
                  CMIS.QUERY_NAME).getNodeValue() : null;
            String displayName =
               (nodeListOfAttributes.getNamedItem(CMIS.DISPLAY_NAME) != null) ? nodeListOfAttributes.getNamedItem(
                  CMIS.DISPLAY_NAME).getNodeValue() : null;

            List<String> values = getValues(item);

            if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_STRING))
            {
               StringProperty property =
                  new StringProperty(propertyDefinitionId, queryName, localName, displayName, values);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_BOOLEAN))
            {
               List<Boolean> booleanValues = new ArrayList<Boolean>();
               for (String value : values)
               {
                  booleanValues.add(Boolean.parseBoolean(value));
               }
               BooleanProperty property =
                  new BooleanProperty(propertyDefinitionId, queryName, localName, displayName, booleanValues);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_INTEGER))
            {
               List<Long> longValues = new ArrayList<Long>();
               for (String value : values)
               {
                  longValues.add(Long.parseLong(value));
               }
               IntegerProperty property =
                  new IntegerProperty(propertyDefinitionId, queryName, localName, displayName, longValues);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_URI))
            {
               UriProperty property = new UriProperty(propertyDefinitionId, queryName, localName, displayName, values);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_ID))
            {
               IdProperty property = new IdProperty(propertyDefinitionId, queryName, localName, displayName, values);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_DATE_TIME))
            {
               List<Date> dateValues = new ArrayList<Date>();
               for (String value : values)
               {
                  dateValues.add(DateUtil.parseDate(value));
               }
               DateTimeProperty property =
                  new DateTimeProperty(propertyDefinitionId, queryName, localName, displayName, dateValues);
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_DECIMAL))
            {
               List<Double> doubleValues = new ArrayList<Double>();
               for (String value : values)
               {
                  doubleValues.add(Double.parseDouble(value));
               }

               DecimalProperty property =
                  new DecimalProperty(propertyDefinitionId, queryName, localName, displayName, Double.valueOf("1"));
               properties.put(propertyDefinitionId, property);
            }
            else if (item.getNodeName().equals(CMIS.CMIS_PROPERTY_HTML))
            {
               HtmlProperty property =
                  new HtmlProperty(propertyDefinitionId, queryName, localName, displayName, values);
               properties.put(propertyDefinitionId, property);
            }
         }
      }
      return properties;
   }

   private static List<String> getValues(Node property)
   {
      List<String> values = new ArrayList<String>();
      NodeList nodeList = property.getChildNodes();
      if (nodeList.getLength() < 0)
      {
         return values;
      }
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node valueNode = nodeList.item(i);
         if (valueNode.getNodeName().equals(CMIS.CMIS_VALUE))
         {
            if (valueNode.getFirstChild() != null)
            {
               values.add(valueNode.getFirstChild().getNodeValue());
            }
         }
      }
      return values;
   }
}
