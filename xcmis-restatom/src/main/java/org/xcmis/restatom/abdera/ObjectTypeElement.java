/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.object.ChangeInfo;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.BooleanProperty;
import org.xcmis.spi.object.impl.CmisObjectImpl;
import org.xcmis.spi.object.impl.DateTimeProperty;
import org.xcmis.spi.object.impl.DecimalProperty;
import org.xcmis.spi.object.impl.HtmlProperty;
import org.xcmis.spi.object.impl.IdProperty;
import org.xcmis.spi.object.impl.IntegerProperty;
import org.xcmis.spi.object.impl.StringProperty;
import org.xcmis.spi.object.impl.UriProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ObjectTypeElement.java 218 2010-02-15 07:38:06Z andrew00x $
 */
public class ObjectTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new object type element.
    * 
    * @param internal the internal
    */
   public ObjectTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new object type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ObjectTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Gets the allowable actions element.
    * 
    * @return the allowable actions element
    */
   public AllowableActionsElement getAllowableActionsElement()
   {
      return getExtension(AtomCMIS.ALLOWABLE_ACTIONS);
   }

   /**
    * Gets the object.
    * 
    * @return the object
    */
   public CmisObject getObject()
   {
      CmisObjectImpl object = new CmisObjectImpl();
      ExtensibleElementWrapper propertiesElement = getExtension(AtomCMIS.PROPERTIES);
      if (propertiesElement != null)
      {
         Map<String, Property<?>> properties = object.getProperties();
         List<PropertyElement<?>> ll = propertiesElement.getElements();
         for (PropertyElement<?> propertyElement : ll)
         {
            properties.put(propertyElement.getProperty().getId(), propertyElement.getProperty());
         }
      }
      // TODO
      //      object.getACL()
      //      object.getPolicyIds()
      //      object.getRelationship()
      //      object.getRenditions()
      //      object.setAllowableActions(allowableActions)
      //      object.setChangeInfo(changeInfo)
      //      object.setExactACL(exactACL)
      //      object.setObjectInfo(objectInfo)
      //      object.setPathSegment(pathSegment)

      // XXX At the moment do not process other stuff from XML.
      // Don't need this now. It is not clear from specification 
      // how to process (apply) policies.
      return object;
   }

   /**
    * Builds the element.
    * 
    * @param objectType the object type
    * @param filter the filter
    */
   public void build(CmisObject objectType, PropertyFilter filter)
   {
      if (objectType != null)
      {
         // XXX: Workaround to get work updating properties under 'Cmis Connector Firefox plugin'.
         // Plugin miss namespace when create entry for updating. Namespace for prefix 'cmisra' 
         // declared in entry tag. But this tag is overwritten in plugin and has no namespace
         // declaration any more. 
         setAttributeValue("xmlns:cmisra", "http://docs.oasis-open.org/ns/cmis/restatom/200908/");

         // PROPERTIES
         Map<String, Property<?>> properties = objectType.getProperties();
         if (properties != null && !properties.isEmpty())
         {
            ExtensibleElementWrapper propertiesElement = addExtension(AtomCMIS.PROPERTIES);
            Set<String> keys = properties.keySet();
            for (String key : keys)
            {
               Property<?> prop = properties.get(key);
               PropertyType propertyType = prop.getType();

               switch (propertyType)
               {
                  case BOOLEAN : {
                     PropertyBooleanElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_BOOLEAN);
                     propElement.build((BooleanProperty)prop);
                     break;
                  }
                  case DATETIME : {
                     PropertyDateTimeElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_DATE_TIME);
                     propElement.build((DateTimeProperty)prop);
                     break;
                  }
                  case DECIMAL : {
                     PropertyDecimalElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_DECIMAL);
                     propElement.build((DecimalProperty)prop);
                     break;
                  }
                  case HTML : {
                     PropertyHtmlElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_HTML);
                     propElement.build((HtmlProperty)prop);
                     break;
                  }
                  case ID : {
                     PropertyIdElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_ID);
                     propElement.build((IdProperty)prop);
                     break;
                  }
                  case STRING : {
                     PropertyStringElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_STRING);
                     propElement.build((StringProperty)prop);
                     break;
                  }
                  case INTEGER : {
                     PropertyIntegerElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_INTEGER);
                     propElement.build((IntegerProperty)prop);
                     break;
                  }
                  case URI : {
                     PropertyUriElement propElement = propertiesElement.addExtension(AtomCMIS.PROPERTY_URI);
                     propElement.build((UriProperty)prop);
                     break;
                  }
                  default :
                     // Should never happen. Exception will throw early.
                     throw new InvalidArgumentException("Unknown property type " + propertyType.value());
               }
            }
         }

         // ALLOWABLE_ACTIONS
         AllowableActions allowableActions = objectType.getAllowableActions();
         if (allowableActions != null)
         {
            AllowableActionsElement actionsElement = addExtension(AtomCMIS.ALLOWABLE_ACTIONS);
            actionsElement.build(allowableActions);
         }

         // RELATIOSNHIP
         List<CmisObject> relationship = objectType.getRelationship();
         if (relationship != null && relationship.size() > 0)
         {
            ObjectTypeElement relationshipElement = addExtension(AtomCMIS.RELATIOSNHIP);
            for (CmisObject cmisObject : relationship)
               relationshipElement.build(cmisObject);
         }

         // ChangeEventInfo
         ChangeInfo changeInfo = objectType.getChangeInfo();
         if (changeInfo != null)
         {
            ChangeEventTypeElement changeEventInfoElement = addExtension(AtomCMIS.CHANGE_EVENT_INFO);
            changeEventInfoElement.build(changeInfo);
         }

         // acl
         List<AccessControlEntry> accessControlList = objectType.getACL();
         if (accessControlList != null && accessControlList.size() > 0)
         {
            ExtensibleElementWrapper accessControlListTypeElement = addExtension(AtomCMIS.ACL);
            for (AccessControlEntry element : accessControlList)
            {
               AccessControlEntryTypeElement ace = accessControlListTypeElement.addExtension(AtomCMIS.PERMISSION);
               ace.build(element);
            }
         }

         // exactACL
         addSimpleExtension(AtomCMIS.EXACT_ACL, Boolean.valueOf(objectType.isExactACL()).toString());

         // policyIds
         Collection<String> policyIds = objectType.getPolicyIds();
         if (policyIds != null && policyIds.size() > 0)
         {
            ExtensibleElementWrapper listOfIdsTypeTypeElement = addExtension(AtomCMIS.POLICY_IDS);
            for (Element element : listOfIdsTypeTypeElement)
            {
               listOfIdsTypeTypeElement.addSimpleExtension(AtomCMIS.ID, element.getText());
            }
         }

         // rendition
         List<Rendition> listRendition = objectType.getRenditions();
         if (listRendition != null && listRendition.size() > 0)
         {
            RenditionTypeElement renditionElement = addExtension(AtomCMIS.RENDITION);
            for (Rendition rendition : listRendition)
               renditionElement.build(rendition);
         }
      }
   }

   /**
    * Builds the element.
    * 
    * @param objectType the object type
    */
   public void build(CmisObject objectType)
   {
      build(objectType, null);
   }
}
