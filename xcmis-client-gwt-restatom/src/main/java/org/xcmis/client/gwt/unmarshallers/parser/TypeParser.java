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
import org.xcmis.client.gwt.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.model.property.PropertyDefinition;
import org.xcmis.client.gwt.model.restatom.TypeEntry;
import org.xcmis.client.gwt.model.type.TypeDefinition;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeParser
{

   /**
    * Constructor.
    */
   protected TypeParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * @param response response
    * @return List containing {@link CmisTypeDefinitionType}
    */
   public static List<TypeDefinition> getTypeList(Document response)
   {
      List<TypeDefinition> typeList = new ArrayList<TypeDefinition>();
      NodeList elementList = response.getElementsByTagName(CMIS.TYPE);

      if (elementList != null && elementList.getLength() > 0)
      {
         for (int i = 0; i < elementList.getLength(); i++)
         {
            Node node = elementList.item(i);
            typeList.add(getCmisTypeDefinitionType(node));
         }
      }
      return typeList;
   }

   /**
    * @param response response
    * @return List containing {@link TypeEntry}
    */
   public static List<TypeEntry> getTypes(Document response)
   {
      List<TypeEntry> types = new ArrayList<TypeEntry>();
      NodeList feedInfoList = response.getElementsByTagName(CMIS.FEED).item(0).getChildNodes();
      for (int i = 0; i < feedInfoList.getLength(); i++)
      {
         if (feedInfoList.item(i).getNodeName().equals(CMIS.ENTRY))
         {
            TypeEntry typeEntry = new TypeEntry();
            getTypeEntry(feedInfoList.item(i), typeEntry);
            types.add(typeEntry);
         }
      }
      return types;
   }

   /**
    * Retrieve data for {@link TypeEntry} from xml.
    * 
    * @param entryNode entry node
    * @param typeEntry type entry
    */
   public static void getTypeEntry(Node entryNode, TypeEntry typeEntry)
   {
      NodeList nodeList = entryNode.getChildNodes();
      typeEntry.setLinks(AtomEntryParser.getEntryInfo(nodeList).getLinks());

      Node children = null;

      for (int j = 0; j < nodeList.getLength(); j++)
      {
         Node item = nodeList.item(j);
         if (item.getNodeName().equals(CMIS.CMISRA_TYPE))
         {
            typeEntry.setTypeCmisTypeDefinition(getCmisTypeDefinitionType(item));
         }
         else if (item.getNodeName().equals(CMIS.CMISRA_CHILDREN))
         {
            children = item;
         }
      }
      if (children != null)
      {
         setTypeChildren(typeEntry, children.getChildNodes());
      }
   }

   /**
    * Set children type's data to their parent.
    * 
    * @param parent parent
    * @param children children
    */
   private static void setTypeChildren(TypeEntry parent, NodeList children)
   {
      for (int i = 0; i < children.getLength(); i++)
      {
         Node child = children.item(i);
         if (child.getNodeName().equals(CMIS.ENTRY))
         {
            TypeEntry typeEntry = new TypeEntry();
            typeEntry.setLinks(AtomEntryParser.getEntryInfo(child.getChildNodes()).getLinks());
            NodeList childChildren = null;
            for (int j = 0; j < child.getChildNodes().getLength(); j++)
            {
               Node typeItem = child.getChildNodes().item(j);
               if (typeItem.getNodeName().equals(CMIS.CMISRA_TYPE))
               {
                  typeEntry.setTypeCmisTypeDefinition(getCmisTypeDefinitionType(typeItem));
               }
               else if (typeItem.getNodeName().equals(CMIS.CMISRA_CHILDREN))
               {
                  childChildren = typeItem.getChildNodes();
               }
               parent.getChildren().add(typeEntry);
            }
            if (childChildren != null)
            {
               setTypeChildren(typeEntry, childChildren);
            }
         }
      }
   }

   /**
    * Parse xml element to get {@link CmisTypeDefinitionType}.
    * 
    * @param node node
    * @return {@link CmisTypeDefinitionType}
    */
   public static TypeDefinition getCmisTypeDefinitionType(Node node)
   {
      TypeDefinition typeDefinition = new TypeDefinition();
      NodeList entries = node.getChildNodes();
      for (int j = 0; j < entries.getLength(); j++)
      {
         Node item = entries.item(j);
         String value = null;
         if (item.getFirstChild() == null)
         {
            value = null;
         }
         else
         {
            value = item.getFirstChild().getNodeValue();
         }

         if (item.getNodeName().equals(CMIS.CMIS_ID))
         {
            typeDefinition.setId(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_QUERY_NAME))
         {
            typeDefinition.setQueryName(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_DISPLAY_NAME))
         {
            typeDefinition.setDisplayName(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_LOCAL_NAME))
         {
            typeDefinition.setLocalName(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_LOCAL_NAMESPACE))
         {
            typeDefinition.setLocalNamespace(value);
         }

         else if (item.getNodeName().equals(CMIS.CMIS_BASE_ID))
         {
            typeDefinition.setBaseId(EnumBaseObjectTypeIds.fromValue(value));
         }

         else if (item.getNodeName().equals(CMIS.CMIS_PARENT_ID))
         {
            typeDefinition.setParentId(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_DESCRIPTION))
         {
            typeDefinition.setDescription(value);
         }
         else if (item.getNodeName().equals(CMIS.CMIS_FILEABLE))
         {
            typeDefinition.setFileable(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CMIS.CMIS_CREATABLE))
         {
            typeDefinition.setCreatable(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CMIS.CMIS_FULL_TEXT_INDEXED))
         {
            typeDefinition.setFulltextIndexed(Boolean.valueOf(value));
         }

         else if (item.getNodeName().equals(CMIS.CMIS_QUERYABLE))
         {
            typeDefinition.setQueryable(Boolean.valueOf(value));
         }

         else if (item.getNodeName().equals(CMIS.CMIS_CONTROLLABLE_POLICY))
         {
            typeDefinition.setControllablePolicy(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CMIS.CMIS_CONTROLLABLE_ACL))
         {
            typeDefinition.setControllableACL(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CMIS.CMIS_INCLUDED_IN_SUPERTYPE_QUERY))
         {
            typeDefinition.setIncludedInSupertypeQuery(Boolean.valueOf(value));
         }
         else if ((item.getNodeName().equals(CMIS.CMIS_PROPERTY_ID_DEFINITION))
            || (item.getNodeName().equals(CMIS.CMIS_PROPERTY_BOOLEAN_DEFINITION))
            || (item.getNodeName().equals(CMIS.CMIS_PROPERTY_STRING_DEFINITION))
            || (item.getNodeName().equals(CMIS.CMIS_PROPERTY_INTEGER_DEFINITION))
            || (item.getNodeName().equals(CMIS.CMIS_PROPERTY_DECIMAL_DEFINITION))
            || (item.getNodeName().equals(CMIS.CMIS_PROPERTY_DATETIME_DEFINITION)))
         {
            PropertyDefinition<?> propertyDefinition = PropertyDefinitionParser.parse(item);
            typeDefinition.getPropertyDefinitions().put(propertyDefinition.getId(), propertyDefinition);
         }
      }
      return typeDefinition;
   }
}
