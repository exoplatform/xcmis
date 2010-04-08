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

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.EnumPropertyType;
import org.xcmis.client.gwt.client.model.acl.AccessControlEntry;
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
import org.xcmis.client.gwt.client.model.property.CmisProperties;
import org.xcmis.client.gwt.client.model.property.Property;

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
      throw new UnsupportedOperationException(); // prevents calls from
      // subclass
   }

   /**
    * Create document request.
    * 
    * @param createDocument
    *            createDocument
    * @return String
    */
   public static String createDocument(CreateDocument createDocument)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      // Get new document's name from its properties

      String name = createDocument.getProperties().getString(CMIS.CMIS_NAME);

      createPropertiesElement(createDocument.getProperties(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CMIS.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CMIS.SUMMARY);
      summary.appendChild(doc.createTextNode("Document creation"));
      Element content = doc.createElement(CMIS.CONTENT);

      entry.appendChild(title);
      entry.appendChild(content);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create document from source request.
    * 
    * @param createDocumentFromSource
    *            createDocumentFromSource
    * @return String
    */
   public static String createDocumentFromSource(CreateDocumentFromSource createDocumentFromSource)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      // Get new document's name from its properties
      String name = createDocumentFromSource.getProperties().getString(CMIS.CMIS_NAME);

      createPropertiesElement(createDocumentFromSource.getProperties(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CMIS.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CMIS.SUMMARY);
      summary.appendChild(doc.createTextNode("Document from source creation"));
      Element content = doc.createElement(CMIS.CONTENT);

      entry.appendChild(title);
      entry.appendChild(content);

      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create folder request.
    * 
    * @param createFolder
    *            createFolder
    * @return String
    */
   public static String createFolder(CreateFolder createFolder)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      // Get new folder's name from its properties
      String name = createFolder.getProperties().getString(CMIS.CMIS_NAME);

      /* new folder's parent id element */
      Element parentId =
         createPropertyElement(doc, CMIS.CMIS_PROPERTY_ID, CMIS.CMIS_PARENT_ID, createFolder.getFolderId());
      properties.appendChild(parentId);

      createPropertiesElement(createFolder.getProperties(), properties, doc);
      object.appendChild(properties);

      /* Title must also contain the name of new folder */
      Element title = doc.createElement(CMIS.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CMIS.SUMMARY);
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
    * @param createPolicy
    *            createPolicy
    * @return String
    */
   public static String createPolicy(CreatePolicy createPolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      // Get new policy's name from its properties
      String name = createPolicy.getProperties().getString(CMIS.CMIS_NAME);

      createPropertiesElement(createPolicy.getProperties(), properties, doc);
      object.appendChild(properties);

      Element summary = doc.createElement(CMIS.SUMMARY);
      summary.appendChild(doc.createTextNode("summary"));

      // Object name should also be pointed in title
      Element title = doc.createElement(CMIS.TITLE);
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
    * @param createRelationship
    *            createRelationship
    * @return String
    */
   public static String createRelationship(CreateRelationship createRelationship)
   {
      // Get new relationship's name from its properties
      String name = createRelationship.getProperties().getString(CMIS.CMIS_NAME);

      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);
      entry.setAttribute(EntryXMLBuilder.XMLNS_CMISM.getPrefix() + ":" + EntryXMLBuilder.XMLNS_CMISM.getLocalName(),
         EntryXMLBuilder.XMLNS_CMISM.getNamespaceURI());

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      createPropertiesElement(createRelationship.getProperties(), properties, doc);
      object.appendChild(properties);

      // Object name should also be pointed in title
      Element title = doc.createElement(CMIS.TITLE);
      title.appendChild(doc.createTextNode(name));
      Element summary = doc.createElement(CMIS.SUMMARY);
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
    * @param removePolicy
    *            removePolicy
    * @return String
    */
   public static String removePolicy(RemovePolicy removePolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);
      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      Element policyIdElement =
         createPropertyElement(doc, CMIS.CMIS_PROPERTY_ID, CMIS.CMIS_OBJECT_ID, removePolicy.getPolicyId());
      properties.appendChild(policyIdElement);

      object.appendChild(properties);

      Element summary = doc.createElement(CMIS.SUMMARY);
      summary.appendChild(doc.createTextNode("Remove policy"));

      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create update properties request.
    * 
    * @param updateProperties
    *            updateProperties
    * @return String
    */
   public static String updateProperties(UpdateProperties updateProperties)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      String name = updateProperties.getProperties().getString(CMIS.CMIS_NAME);

      createPropertiesElement(updateProperties.getProperties(), properties, doc);

      if (updateProperties.getChangeToken() != null && updateProperties.getChangeToken().length() > 0)
      {
         Element changeToken =
            createPropertyElement(doc, CMIS.CMIS_PROPERTY_STRING, CMIS.CMIS_CHANGE_TOKEN, updateProperties
               .getChangeToken());
         properties.appendChild(changeToken);
      }

      object.appendChild(properties);
      Element title = doc.createElement(CMIS.TITLE);
      title.appendChild(doc.createTextNode(name));
      entry.appendChild(title);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create move object request.
    * 
    * @param moveObject
    *            moveObject
    * @return String
    */
   public static String moveItem(MoveObject moveObject)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);
      Element objectId =
         createPropertyElement(doc, CMIS.CMIS_PROPERTY_ID, CMIS.CMIS_OBJECT_ID, moveObject.getObjectId());

      properties.appendChild(objectId);
      object.appendChild(properties);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create apply policy request.
    * 
    * @param applyPolicy
    *            applyPolicy
    * @return String
    */
   public static String applyPolicy(ApplyPolicy applyPolicy)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      Element policyIdElement =
         createPropertyElement(doc, CMIS.CMIS_PROPERTY_ID, CMIS.CMIS_OBJECT_ID, applyPolicy.getPolicyId());
      properties.appendChild(policyIdElement);

      object.appendChild(properties);

      Element summary = doc.createElement(CMIS.SUMMARY);
      summary.appendChild(doc.createTextNode("Apply policy"));

      entry.appendChild(summary);
      entry.appendChild(object);
      doc.appendChild(entry);

      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create check in request.
    * 
    * @param checkIn
    *            checkIn
    * @return String
    */
   public static String checkin(CheckIn checkIn)
   {
      Document doc = XMLParser.createDocument();
      Element entry = EntryXMLBuilder.createEntryElement(doc);

      Element object = doc.createElement(CMIS.CMISRA_OBJECT);
      Element properties = doc.createElement(CMIS.CMIS_PROPERTIES);

      createPropertiesElement(checkIn.getProperties(), properties, doc);
      object.appendChild(properties);
      entry.appendChild(object);
      doc.appendChild(entry);
      return EntryXMLBuilder.createStringRequest(doc);
   }

   /**
    * Create apply ACL request.
    * 
    * @param applyACL
    *            applyACL
    * @return String
    */
   public static String applyACL(ApplyACL applyACL)
   {
      Document doc = XMLParser.createDocument();
      Element acl = doc.createElement(CMIS.CMIS_ACL);
      acl.setAttribute("xmlns:cmis", EntryXMLBuilder.XMLNS_CMIS.getNamespaceURI());
      acl.setAttribute("xmlns:atom", EntryXMLBuilder.XMLNS_ATOM.getNamespaceURI());
      acl.setAttribute("xmlns:app", EntryXMLBuilder.XMLNS_APP.getNamespaceURI());
      acl.setAttribute("xmlns:cmism", EntryXMLBuilder.XMLNS_CMISM.getNamespaceURI());
      acl.setAttribute("xmlns:cmisra", EntryXMLBuilder.XMLNS_CMISRA.getNamespaceURI());

      for (AccessControlEntry ace : applyACL.getAddACEs().getPermission())
      {
         Element aceElement = doc.createElement(CMIS.CMIS_PERMISSION);
         Element directElement = doc.createElement(CMIS.CMIS_DIRECT);
         directElement.appendChild(doc.createTextNode(String.valueOf(ace.isDirect())));

         Element principalElement = doc.createElement(CMIS.CMIS_PRINCIPAL);
         Element principalIdElement = doc.createElement(CMIS.CMIS_PRINCIPAL_ID);
         principalIdElement.appendChild(doc.createTextNode(ace.getPrincipal().getPrincipalId()));
         principalElement.appendChild(principalIdElement);
         aceElement.appendChild(principalElement);
         aceElement.appendChild(directElement);

         for (String permission : ace.getPermissions())
         {
            Element permissionElement = doc.createElement(CMIS.CMIS_PERMISSION);
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
    * @param properties
    *            properties
    * @param propertiesElement
    *            propertiesElement
    * @param doc
    *            doc
    */
   private static void createPropertiesElement(CmisProperties properties, Element propertiesElement, Document doc)
   {
      for (Property<?> property : properties.getProperties().values())
      {
         Element propertyElement =
            createPropertyElement(doc, getPropertyNameByType(property.getType()), property.getId(), property
               .getValues());
         propertiesElement.appendChild(propertyElement);

      }
   }

   /**
    * @param doc xml document
    * @param type type
    * @param name name
    * @param values values
    * @return {@link Element} property xml element
    */
   private static Element createPropertyElement(Document doc, String type, String name, List<?> values)
   {
      Element property = doc.createElement(type);
      property.setAttribute(CMIS.PROPERTY_DEFINITION_ID, name);

      if (values.size() > 0)
      {
         for (int i = 0; i < values.size(); i++)
         {
            Element valueElement = doc.createElement(CMIS.CMIS_VALUE);
            valueElement.appendChild(doc.createTextNode(String.valueOf(values.get(i))));
            property.appendChild(valueElement);
         }
      }
      return property;
   }

  
   /**
    * @param doc xml document
    * @param type property type
    * @param name property name 
    * @param value value
    * @return {@link Element} property xml element
    */
   private static Element createPropertyElement(Document doc, String type, String name, String value)
   {
      Element property = doc.createElement(type);
      property.setAttribute(CMIS.PROPERTY_DEFINITION_ID, name);
      Element valueElement = doc.createElement(CMIS.CMIS_VALUE);
      valueElement.appendChild(doc.createTextNode(value));
      property.appendChild(valueElement);
      return property;
   }

   
   /**
    * @param propertyType property type
    * @return {@link String} property type (name of thee xml element)
    */
   private static String getPropertyNameByType(EnumPropertyType propertyType)
   {
      String type = propertyType.value();
      String firstLetter = type.substring(0, 1).toUpperCase();
      String tail = type.substring(1, type.length());
      return CMIS.CMIS_PROPERTY + firstLetter + tail;
   }
}
