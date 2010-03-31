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
import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.restatom.TypeEntry;
import org.xcmis.client.gwt.client.model.type.CmisTypeDefinitionType;

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
   public static List<CmisTypeDefinitionType> getTypeList(Document response)
   {
      List<CmisTypeDefinitionType> typeList = new ArrayList<CmisTypeDefinitionType>();
      NodeList elementList = response.getElementsByTagName(CmisNameSpace.TYPE);

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
      NodeList feedInfoList = response.getElementsByTagName(CmisNameSpace.FEED).item(0).getChildNodes();
      for (int i = 0; i < feedInfoList.getLength(); i++)
      {
         if (feedInfoList.item(i).getNodeName().equals(CmisNameSpace.ENTRY))
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
         if (item.getNodeName().equals(CmisNameSpace.CMISRA_TYPE))
         {
            typeEntry.setTypeCmisTypeDefinition(getCmisTypeDefinitionType(item));
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMISRA_CHILDREN))
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
         if (child.getNodeName().equals(CmisNameSpace.ENTRY))
         {
            TypeEntry typeEntry = new TypeEntry();
            typeEntry.setLinks(AtomEntryParser.getEntryInfo(child.getChildNodes()).getLinks());
            NodeList childChildren = null;
            for (int j = 0; j < child.getChildNodes().getLength(); j++)
            {
               Node typeItem = child.getChildNodes().item(j);
               if (typeItem.getNodeName().equals(CmisNameSpace.CMISRA_TYPE))
               {
                  typeEntry.setTypeCmisTypeDefinition(getCmisTypeDefinitionType(typeItem));
               }
               else if (typeItem.getNodeName().equals(CmisNameSpace.CMISRA_CHILDREN))
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
   public static CmisTypeDefinitionType getCmisTypeDefinitionType(Node node)
   {
      CmisTypeDefinitionType typeDefinitionType = new CmisTypeDefinitionType();
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

         if (item.getNodeName().equals(CmisNameSpace.CMIS_ID))
         {
            typeDefinitionType.setId(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_QUERY_NAME))
         {
            typeDefinitionType.setQueryName(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_DISPLAY_NAME))
         {
            typeDefinitionType.setDisplayName(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_LOCAL_NAME))
         {
            typeDefinitionType.setLocalName(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_LOCAL_NAMESPACE))
         {
            typeDefinitionType.setLocalNamespace(value);
         }

         else if (item.getNodeName().equals(CmisNameSpace.CMIS_BASE_ID))
         {
            typeDefinitionType.setBaseId(EnumBaseObjectTypeIds.fromValue(value));
         }

         else if (item.getNodeName().equals(CmisNameSpace.CMIS_PARENT_ID))
         {
            typeDefinitionType.setParentId(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_DESCRIPTION))
         {
            typeDefinitionType.setDescription(value);
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_FILEABLE))
         {
            typeDefinitionType.setFileable(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_CREATABLE))
         {
            typeDefinitionType.setCreatable(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_FULL_TEXT_INDEXED))
         {
            typeDefinitionType.setFulltextIndexed(Boolean.valueOf(value));
         }

         else if (item.getNodeName().equals(CmisNameSpace.CMIS_QUERYABLE))
         {
            typeDefinitionType.setQueryable(Boolean.valueOf(value));
         }

         else if (item.getNodeName().equals(CmisNameSpace.CMIS_CONTROLLABLE_POLICY))
         {
            typeDefinitionType.setControllablePolicy(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_CONTROLLABLE_ACL))
         {
            typeDefinitionType.setControllableACL(Boolean.valueOf(value));
         }
         else if (item.getNodeName().equals(CmisNameSpace.CMIS_INCLUDED_IN_SUPERTYPE_QUERY))
         {
            typeDefinitionType.setIncludedInSupertypeQuery(Boolean.valueOf(value));
         }
         else if ((item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_ID_DEFINITION))
            || (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_BOOLEAN_DEFINITION))
            || (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_STRING_DEFINITION))
            || (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_INTEGER_DEFINITION))
            || (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_DECIMAL_DEFINITION))
            || (item.getNodeName().equals(CmisNameSpace.CMIS_PROPERTY_DATETIME_DEFINITION)))
         {
            typeDefinitionType.getPropertyDefinition().add(PropertyDefinitionParser.parse(item));
         }
         else
         {
            typeDefinitionType.getAny().add(item);
         }
      }
      return typeDefinitionType;
   }
}
