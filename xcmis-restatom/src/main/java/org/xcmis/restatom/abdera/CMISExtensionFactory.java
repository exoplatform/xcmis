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

import org.apache.abdera.util.AbstractExtensionFactory;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.model.PropertyType;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CMISExtensionFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CMISExtensionFactory extends AbstractExtensionFactory
{

   /** The classes2QNamePropertyType property types map. */
   private static final Map<PropertyType, QName> classes2QNamePropertyType = new HashMap<PropertyType, QName>();

   /**
    * Instantiates a new CMIS extension factory.
    */
   public CMISExtensionFactory()
   {
      super(CmisConstants.CMIS_NS_URI);
      addImpl(AtomCMIS.REPOSITORY_INFO, RepositoryInfoTypeElement.class);
      addImpl(AtomCMIS.CAPABILITIES, RepositoryCapabilitiesTypeElement.class);
      addImpl(AtomCMIS.TYPE, TypeDefinitionTypeElement.class);
      addImpl(AtomCMIS.CHOICE, ChoiceElement.class);
      addImpl(AtomCMIS.PERMISSION, AccessControlEntryTypeElement.class);
      addImpl(AtomCMIS.PERMISSIONS, PermissionDefinitionElement.class);
      addImpl(AtomCMIS.PROPERTY_BOOLEAN_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_DATE_TIME_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_DECIMAL_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_HTML_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_ID_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_INTEGER_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_STRING_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_URI_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.OBJECT, ObjectTypeElement.class);
      addImpl(AtomCMIS.RELATIOSNHIP, ObjectTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_BOOLEAN, PropertyBooleanElement.class);
      addImpl(AtomCMIS.PROPERTY_DATE_TIME, PropertyDateTimeElement.class);
      addImpl(AtomCMIS.PROPERTY_DECIMAL, PropertyDecimalElement.class);
      addImpl(AtomCMIS.PROPERTY_HTML, PropertyHtmlElement.class);
      addImpl(AtomCMIS.PROPERTY_ID, PropertyIdElement.class);
      addImpl(AtomCMIS.PROPERTY_INTEGER, PropertyIntegerElement.class);
      addImpl(AtomCMIS.PROPERTY_STRING, PropertyStringElement.class);
      addImpl(AtomCMIS.PROPERTY_URI, PropertyUriElement.class);
      addImpl(AtomCMIS.ALLOWABLE_ACTIONS, AllowableActionsElement.class);
      addImpl(AtomCMIS.QUERY, QueryTypeElement.class);
      addImpl(AtomCMIS.CHANGE_EVENT_INFO, ChangeEventTypeElement.class);
      addImpl(AtomCMIS.MAPPING, PermissionMappingElement.class);
      addImpl(AtomCMIS.ACL_CAPABILITY, ACLCapabilityTypeElement.class);
      addImpl(AtomCMIS.RENDITION, RenditionTypeElement.class);
      addImpl(AtomCMIS.URITEMPLATE, UriTemplateTypeElement.class);
      addImpl(AtomCMIS.CONTENT, ContentTypeElement.class);

      classes2QNamePropertyType.put(PropertyType.BOOLEAN, AtomCMIS.PROPERTY_BOOLEAN_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.DATETIME, AtomCMIS.PROPERTY_DATE_TIME_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.DECIMAL, AtomCMIS.PROPERTY_DECIMAL_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.HTML, AtomCMIS.PROPERTY_HTML_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.ID, AtomCMIS.PROPERTY_ID_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.INTEGER, AtomCMIS.PROPERTY_INTEGER_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.STRING, AtomCMIS.PROPERTY_STRING_DEFINITION);
      classes2QNamePropertyType.put(PropertyType.URI, AtomCMIS.PROPERTY_URI_DEFINITION);

   }

   /**
    * Gets the Property Definition Type element name.
    * 
    * @param propertyType the PropertyType
    * @return the QName element name
    */
   public static QName getPropertyDefinitionTypeElementName(PropertyType propertyType)
   {
      return classes2QNamePropertyType.get(propertyType);
   }

}
