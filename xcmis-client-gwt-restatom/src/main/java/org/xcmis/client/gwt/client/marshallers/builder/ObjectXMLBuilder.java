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

package org.xcmis.client.gwt.client.marshallers.builder;

import org.xcmis.client.gwt.client.CmisNameSpace;
import org.xcmis.client.gwt.client.model.acl.CmisAccessControlEntryType;
import org.xcmis.client.gwt.client.model.actions.ApplyACL;
import org.xcmis.client.gwt.client.model.actions.ApplyPolicy;
import org.xcmis.client.gwt.client.model.actions.CheckIn;
import org.xcmis.client.gwt.client.model.actions.CreateDocument;
import org.xcmis.client.gwt.client.model.actions.CreateDocumentFromSource;
import org.xcmis.client.gwt.client.model.actions.CreateFolder;
import org.xcmis.client.gwt.client.model.actions.CreatePolicy;
import org.xcmis.client.gwt.client.model.actions.CreateRelationship;
import org.xcmis.client.gwt.client.model.actions.MoveObject;
import org.xcmis.client.gwt.client.model.actions.RemovePolicy;
import org.xcmis.client.gwt.client.model.actions.UpdateProperties;
import org.xcmis.client.gwt.client.model.property.CmisProperty;
import org.xcmis.client.gwt.client.model.property.CmisPropertyBoolean;
import org.xcmis.client.gwt.client.model.property.CmisPropertyDateTime;
import org.xcmis.client.gwt.client.model.property.CmisPropertyId;
import org.xcmis.client.gwt.client.model.property.CmisPropertyInteger;
import org.xcmis.client.gwt.client.model.property.CmisPropertyString;
import org.xcmis.client.gwt.client.model.property.CmisPropertyUri;

import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class ObjectXMLBuilder
{
   
   /**
    * Constructor.
    */
   protected ObjectXMLBuilder()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Create document request.
    * 
    * @param createDocument createDocument
    * @return String
    */
   public static String createDocument(CreateDocument createDocument)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      // Get new document's name from its properties
      String name = "";
      for (CmisProperty property : createDocument.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equalsIgnoreCase(CmisNameSpace.CMIS_NAME))
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            name = propertyString.getValue().get(0);
         }
      }

      createPropertiesElement(createDocument.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Document creation"));
      Element content = doc.createElement(CmisNameSpace.CONTENT);

      entry.appendChild(title);
      entry.appendChild(content);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create document from source request.
    * 
    * @param createDocumentFromSource createDocumentFromSource
    * @return String
    */
   public static String createDocumentFromSource(CreateDocumentFromSource createDocumentFromSource)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      // Get new document's name from its properties
      String name = "";
      for (CmisProperty property : createDocumentFromSource.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equalsIgnoreCase(CmisNameSpace.CMIS_NAME))
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            name = propertyString.getValue().get(0);
         }
      }

      createPropertiesElement(createDocumentFromSource.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Document from source creation"));
      Element content = doc.createElement(CmisNameSpace.CONTENT);

      entry.appendChild(title);
      entry.appendChild(content);

      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create folder request.
    * 
    * @param createFolder createFolder
    * @return String
    */
   public static String createFolder(CreateFolder createFolder)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      // Get new folder's name from its properties
      String name = "";
      for (CmisProperty property : createFolder.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equalsIgnoreCase(CmisNameSpace.CMIS_NAME))
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            name = propertyString.getValue().get(0);
         }
      }

      /* new folder's parent id element */
      Element parentId =
         createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_ID, CmisNameSpace.CMIS_PARENT_ID, createFolder
            .getFolderId());
      properties.appendChild(parentId);

      createPropertiesElement(createFolder.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);

      /* Title must also contain the name of new folder */
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Folder creation"));

      entry.appendChild(title);
      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create policy request.
    * 
    * @param createPolicy createPolicy
    * @return String
    */
   public static String createPolicy(CreatePolicy createPolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      // Get new policy's name from its properties
      String name = "";
      for (CmisProperty property : createPolicy.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equalsIgnoreCase(CmisNameSpace.CMIS_NAME))
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            name = propertyString.getValue().get(0);
         }
      }

      createPropertiesElement(createPolicy.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);

      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("summary"));

      // Object name should also be pointed in title
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      entry.appendChild(title);
      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create relationship request.
    * 
    * @param createRelationship createRelationship
    * @return String
    */
   public static String createRelationship(CreateRelationship createRelationship)
   {
      // Get new relationship's name from its properties
      String name = "";
      for (CmisProperty property : createRelationship.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equalsIgnoreCase(CmisNameSpace.CMIS_NAME))
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            name = propertyString.getValue().get(0);
         }
      }

      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);
      entry.setAttribute(EntryXMLBuilder.XMLNS_CMISM.getPrefix() + ":" + EntryXMLBuilder.XMLNS_CMISM.getLocalName(),
         EntryXMLBuilder.XMLNS_CMISM.getNamespaceURI());

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      createPropertiesElement(createRelationship.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Realtionship creation"));
      entry.appendChild(title);
      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create remove policy request.
    * 
    * @param removePolicy removePolicy
    * @return String
    */
   public static String removePolicy(RemovePolicy removePolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);
      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      Element policyIdElement =
         createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_ID, CmisNameSpace.CMIS_OBJECT_ID, removePolicy
            .getPolicyId());
      properties.appendChild(policyIdElement);

      object.appendChild(properties);

      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Remove policy"));

      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create update properties request.
    * 
    * @param updateProperties updateProperties
    * @return String
    */
   public static String updateProperties(UpdateProperties updateProperties)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      String name = "";

      for (CmisProperty property : updateProperties.getProperties().getProperty())
      {
         if (property.getPropertyDefinitionId().equals(CmisNameSpace.CMIS_NAME))
         {
            name = ((CmisPropertyString)property).getValue().get(0);
         }
      }

      createPropertiesElement(updateProperties.getProperties().getProperty(), properties, doc);

      if (updateProperties.getChangeToken() != null && updateProperties.getChangeToken().length() > 0)
      {
         Element changeToken =
            createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_STRING, CmisNameSpace.CMIS_CHANGE_TOKEN,
               updateProperties.getChangeToken());
         properties.appendChild(changeToken);
      }

      object.appendChild(properties);
      Element title = doc.createElement(CmisNameSpace.TITLE);
      title.appendChild(doc.createTextNode(name));
      entry.appendChild(title);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create move object request.
    * 
    * @param moveObject moveObject
    * @return String
    */
   public static String moveItem(MoveObject moveObject)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);
      Element objectId =
         createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_ID, CmisNameSpace.CMIS_OBJECT_ID, moveObject
            .getObjectId());

      properties.appendChild(objectId);
      object.appendChild(properties);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create apply policy request.
    * 
    * @param applyPolicy applyPolicy
    * @return String
    */
   public static String applyPolicy(ApplyPolicy applyPolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      Element policyIdElement =
         createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_ID, CmisNameSpace.CMIS_OBJECT_ID, applyPolicy
            .getPolicyId());
      properties.appendChild(policyIdElement);

      object.appendChild(properties);

      Element summary = doc.createElement(CmisNameSpace.SUMMARY);
      summary.appendChild(doc.createTextNode("Apply policy"));

      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create check in request.
    * 
    * @param checkIn checkIn
    * @return String
    */
   public static String checkin(CheckIn checkIn)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CmisNameSpace.CMISRA_OBJECT);
      Element properties = doc.createElement(CmisNameSpace.CMIS_PROPERTIES);

      createPropertiesElement(checkIn.getProperties().getProperty(), properties, doc);
      object.appendChild(properties);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create apply ACL request.
    * 
    * @param applyACL applyACL
    * @return String
    */
   public static String applyACL(ApplyACL applyACL)
   {
      Document doc = XMLParser.createDocument();
      Element acl = doc.createElement(CmisNameSpace.CMIS_ACL);
      acl.setAttribute("xmlns:cmis", EntryXMLBuilder.XMLNS_CMIS.getNamespaceURI());
      acl.setAttribute("xmlns:atom", EntryXMLBuilder.XMLNS_ATOM.getNamespaceURI());
      acl.setAttribute("xmlns:app", EntryXMLBuilder.XMLNS_APP.getNamespaceURI());
      acl.setAttribute("xmlns:cmism", EntryXMLBuilder.XMLNS_CMISM.getNamespaceURI());
      acl.setAttribute("xmlns:cmisra", EntryXMLBuilder.XMLNS_CMISRA.getNamespaceURI());

      for (CmisAccessControlEntryType ace : applyACL.getAddACEs().getPermission())
      {
         Element aceElement = doc.createElement(CmisNameSpace.CMIS_PERMISSION);
         Element directElement = doc.createElement(CmisNameSpace.CMIS_DIRECT);
         directElement.appendChild(doc.createTextNode(String.valueOf(ace.isDirect())));

         Element principalElement = doc.createElement(CmisNameSpace.CMIS_PRINCIPAL);
         Element principalIdElement = doc.createElement(CmisNameSpace.CMIS_PRINCIPAL_ID);
         principalIdElement.appendChild(doc.createTextNode(ace.getPrincipal().getPrincipalId()));
         principalElement.appendChild(principalIdElement);
         aceElement.appendChild(principalElement);
         aceElement.appendChild(directElement);

         for (String permission : ace.getPermission())
         {
            Element permissionElement = doc.createElement(CmisNameSpace.CMIS_PERMISSION);
            permissionElement.appendChild(doc.createTextNode(permission));
            aceElement.appendChild(permissionElement);
         }
         acl.appendChild(aceElement);
      }
      doc.appendChild(acl);
      return doc.toString();
   }

   /**
    * Form properties elements from property list.
    * 
    * @param properties properties
    * @param propertiesElement propertiesElement
    * @param doc doc
    */
   private static void createPropertiesElement(List<CmisProperty> properties, Element propertiesElement, Document doc)
   {
      for (CmisProperty property : properties)
      {
         if (property instanceof CmisPropertyString)
         {
            CmisPropertyString propertyString = (CmisPropertyString)property;
            String value = propertyString.getValue().get(0);
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_STRING, propertyString.getPropertyDefinitionId(),
                  value);
            propertiesElement.appendChild(propertyElement);
         }
         else if (property instanceof CmisPropertyInteger)
         {
            CmisPropertyInteger propertyInteger = (CmisPropertyInteger)property;
            String value = String.valueOf(propertyInteger.getValue().get(0));
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_INTEGER, propertyInteger
                  .getPropertyDefinitionId(), value);
            propertiesElement.appendChild(propertyElement);
         }
         else if (property instanceof CmisPropertyId)
         {
            CmisPropertyId propertyId = (CmisPropertyId)property;
            String value = propertyId.getValue().get(0);
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_ID, propertyId.getPropertyDefinitionId(), value);
            propertiesElement.appendChild(propertyElement);
         }
         else if (property instanceof CmisPropertyBoolean)
         {
            CmisPropertyBoolean propertyBoolean = (CmisPropertyBoolean)property;
            String value = String.valueOf(propertyBoolean.getValue().get(0));
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_BOOLEAN, propertyBoolean
                  .getPropertyDefinitionId(), value);
            propertiesElement.appendChild(propertyElement);
         }
         else if (property instanceof CmisPropertyDateTime)
         {
            CmisPropertyDateTime propertyDateTime = (CmisPropertyDateTime)property;
            String value = String.valueOf(propertyDateTime.getValue().get(0));
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_DATE_TIME, propertyDateTime
                  .getPropertyDefinitionId(), value);
            propertiesElement.appendChild(propertyElement);
         }
         else if (property instanceof CmisPropertyUri)
         {
            CmisPropertyUri propertyUri = (CmisPropertyUri)property;
            String value = propertyUri.getValue().get(0);
            Element propertyElement =
               createPropertyElement(doc, CmisNameSpace.CMIS_PROPERTY_URI, 
                  propertyUri.getPropertyDefinitionId(), value);
            propertiesElement.appendChild(propertyElement);
         }
      }
   }

   /**
    * Creates property element of pointed type, with pointed name and value.
    * 
    * @param doc doc
    * @param type type
    * @param name name
    * @param value value
    * @return {@link Element}
    */
   private static Element createPropertyElement(Document doc, String type, String name, String value)
   {
      Element property = doc.createElement(type);
      property.setAttribute(CmisNameSpace.PROPERTY_DEFINITION_ID, name);
      Element valueElement = doc.createElement(CmisNameSpace.CMIS_VALUE);
      valueElement.appendChild(doc.createTextNode(value));
      property.appendChild(valueElement);
      return property;
   }

}
