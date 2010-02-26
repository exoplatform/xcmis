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
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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
    * Gets the properties element.
    * 
    * @return the properties element
    */
   public PropertiesTypeElement getPropertiesElement()
   {
      return getExtension(AtomCMIS.PROPERTIES);
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
   public CmisObjectType getObject()
   {
      CmisObjectType object = new CmisObjectType();
      PropertiesTypeElement propertiesElement = getPropertiesElement();
      if (propertiesElement != null)
         object.setProperties(propertiesElement.getProperties());
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
   public void build(CmisObjectType objectType, PropertyFilter filter)
   {
      if (objectType != null)
      {
         // XXX: Workaround to get work updating properties under 'Cmis Connector Firefox plugin'.
         // Plugin miss namespace when create entry for updating. Namespace for prefix 'cmisra' 
         // declared in entry tag. But this tag is overwritten in plugin and has no namespace
         // declaration any more. 
         setAttributeValue("xmlns:" + AtomCMIS.CMISRA_PREFIX, AtomCMIS.CMISRA_NS_URI);

         // Properties
         CmisPropertiesType properties = objectType.getProperties();
         if (properties != null)
         {
            PropertiesTypeElement propertiesElement = addExtension(AtomCMIS.PROPERTIES);
            propertiesElement.build(properties, filter);
         }

         // AllowableActions
         CmisAllowableActionsType allowableActions = objectType.getAllowableActions();
         if (allowableActions != null)
         {
            AllowableActionsElement actionsElement = addExtension(AtomCMIS.ALLOWABLE_ACTIONS);
            actionsElement.build(allowableActions);
         }

         // Relationship
         List<CmisObjectType> relationship = objectType.getRelationship();
         if (relationship != null)
         {
            // TODO How to implement that relationship type element extension?
            // Would it contain full information of element or just ID info.
            // java.lang.ClassCastException: org.apache.abdera.parser.stax.FOMExtensibleElement 
            // cannot be cast to org.xcmis.restatom.abdera.ObjectTypeElement
            //            ObjectTypeElement relationshipElement = addExtension(AtomCMIS.RELATIOSNHIP);
            //            for (CmisObjectType cmisObjectType : relationship)
            //               relationshipElement.build(cmisObjectType);
         }

         // ChangeEventInfo
         CmisChangeEventType changeEventInfo = objectType.getChangeEventInfo();
         if (changeEventInfo != null)
         {
            ChangeEventTypeElement changeEventInfoElement = addExtension(AtomCMIS.CHANGE_EVENT_INFO);
            changeEventInfoElement.build(changeEventInfo);
         }

         // ACL
         CmisAccessControlListType accessControlList = objectType.getAcl();
         if (accessControlList != null)
         {
            AccessControlListTypeElement accessControlListTypeElement = addExtension(AtomCMIS.ACL);
            accessControlListTypeElement.build(accessControlList);
         }

         // exactACL
         if (objectType.isExactACL() != null)
            addSimpleExtension(AtomCMIS.EXACT_ACL, objectType.isExactACL().toString());

         // policyIds
         CmisListOfIdsType policyIds = objectType.getPolicyIds();
         if (policyIds != null)
         {
            ListOfIdsTypeElement listOfIdsTypeTypeElement = addExtension(AtomCMIS.POLICY_IDS);
            listOfIdsTypeTypeElement.build(policyIds);
         }

         // rendition
         List<CmisRenditionType> listRendition = objectType.getRendition();
         if (listRendition != null)
         {
            RenditionTypeElement renditionElement = addExtension(AtomCMIS.RENDITION);
            for (CmisRenditionType rendition : listRendition)
               renditionElement.build(rendition);
         }
      }
   }

   /**
    * Builds the element.
    * 
    * @param objectType the object type
    */
   public void build(CmisObjectType objectType)
   {
      build(objectType, null);
   }
}
