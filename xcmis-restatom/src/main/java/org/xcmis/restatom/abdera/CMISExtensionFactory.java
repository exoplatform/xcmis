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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.impl.PropertyDefinitionImpl;
import org.xcmis.spi.object.impl.BooleanProperty;
import org.xcmis.spi.object.impl.CmisObjectImpl;
import org.xcmis.spi.object.impl.DateTimeProperty;
import org.xcmis.spi.object.impl.DecimalProperty;
import org.xcmis.spi.object.impl.HtmlProperty;
import org.xcmis.spi.object.impl.IdProperty;
import org.xcmis.spi.object.impl.IntegerProperty;
import org.xcmis.spi.object.impl.StringProperty;
import org.xcmis.spi.object.impl.UriProperty;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CMISExtensionFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CMISExtensionFactory extends AbstractExtensionFactory
{

   /** The  classes2QName relation map. */
   private static final Map<Class<?>, QName> classes2QName = new HashMap<Class<?>, QName>();

   /**
    * Instantiates a new CMIS extension factory.
    */
   public CMISExtensionFactory()
   {
      super(CMIS.CMIS_NS_URI);
      addImpl(AtomCMIS.REPOSITORY_INFO, RepositoryInfoTypeElement.class);
      addImpl(AtomCMIS.CAPABILITIES, RepositoryCapabilitiesTypeElement.class);
      addImpl(AtomCMIS.TYPE, TypeDefinitionTypeElement.class);
      // OK
      addImpl(AtomCMIS.CHOICE_BOOLEAN, ChoiceBooleanElement.class);
      addImpl(AtomCMIS.CHOICE_DATE_TIME, ChoiceDateTimeElement.class);
      addImpl(AtomCMIS.CHOICE_DECIMAL, ChoiceDecimalElement.class);
      addImpl(AtomCMIS.CHOICE_HTML, ChoiceHtmlElement.class);
      addImpl(AtomCMIS.CHOICE_ID, ChoiceIdElement.class);
      addImpl(AtomCMIS.CHOICE_INTEGER, ChoiceIntegerElement.class);
      addImpl(AtomCMIS.CHOICE_STRING, ChoiceStringElement.class);
      addImpl(AtomCMIS.CHOICE_URI, ChoiceUriElement.class);

      addImpl(AtomCMIS.PERMISSION, AccessControlEntryTypeElement.class);
      addImpl(AtomCMIS.PERMISSIONS, PermissionDefinitionElement.class);
      // OK
      addImpl(AtomCMIS.PROPERTY_BOOLEAN_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_DATE_TIME_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_DECIMAL_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_HTML_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_ID_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_INTEGER_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_STRING_DEFINITION, PropertyDefinitionTypeElement.class);
      addImpl(AtomCMIS.PROPERTY_URI_DEFINITION, PropertyDefinitionTypeElement.class);

      // OK
      addImpl(AtomCMIS.OBJECT, ObjectTypeElement.class);
      addImpl(AtomCMIS.RELATIOSNHIP, ObjectTypeElement.class);
      // OK
      addImpl(AtomCMIS.PROPERTY_BOOLEAN, PropertyBooleanElement.class);
      addImpl(AtomCMIS.PROPERTY_DATE_TIME, PropertyDateTimeElement.class);
      addImpl(AtomCMIS.PROPERTY_DECIMAL, PropertyDecimalElement.class);
      addImpl(AtomCMIS.PROPERTY_HTML, PropertyHtmlElement.class);
      addImpl(AtomCMIS.PROPERTY_ID, PropertyIdElement.class);
      addImpl(AtomCMIS.PROPERTY_INTEGER, PropertyIntegerElement.class);
      addImpl(AtomCMIS.PROPERTY_STRING, PropertyStringElement.class);
      addImpl(AtomCMIS.PROPERTY_URI, PropertyUriElement.class);

      // OK
      addImpl(AtomCMIS.ALLOWABLE_ACTIONS, AllowableActionsElement.class);
      
      addImpl(AtomCMIS.QUERY, QueryTypeElement.class);
      addImpl(AtomCMIS.CHANGE_EVENT_INFO, ChangeEventTypeElement.class);
      addImpl(AtomCMIS.PERMISSIONS, PermissionDefinitionElement.class);
      addImpl(AtomCMIS.MAPPING, PermissionMappingElement.class);
      addImpl(AtomCMIS.ACL_CAPABILITY, ACLCapabilityTypeElement.class);
      addImpl(AtomCMIS.POLICY_IDS, ListOfIdsTypeElement.class);
      addImpl(AtomCMIS.RENDITION, RenditionTypeElement.class);
      addImpl(AtomCMIS.URITEMPLATE, UriTemplateTypeElement.class);
      addImpl(AtomCMIS.CONTENT, ContentTypeElement.class);

      // fill the MAP
      classes2QName.put(CmisRepositoryEntryType.class, AtomCMIS.REPOSITORY_ENTRY);
      classes2QName.put(CmisRepositoryInfoType.class, AtomCMIS.REPOSITORY_INFO);
      classes2QName.put(CmisRepositoryCapabilitiesType.class, AtomCMIS.CAPABILITIES);
      classes2QName.put(CmisTypeDefinitionType.class, AtomCMIS.TYPE);

      classes2QName.put(CmisChoiceBoolean.class, AtomCMIS.CHOICE_BOOLEAN);
      classes2QName.put(CmisChoiceDateTime.class, AtomCMIS.CHOICE_DATE_TIME);
      classes2QName.put(CmisChoiceDecimal.class, AtomCMIS.CHOICE_DECIMAL);
      classes2QName.put(CmisChoiceHtml.class, AtomCMIS.CHOICE_HTML);
      classes2QName.put(CmisChoiceId.class, AtomCMIS.CHOICE_ID);
      classes2QName.put(CmisChoiceInteger.class, AtomCMIS.CHOICE_INTEGER);
      classes2QName.put(CmisChoiceString.class, AtomCMIS.CHOICE_STRING);
      classes2QName.put(CmisChoiceUri.class, AtomCMIS.CHOICE_URI);

      classes2QName.put(CmisPermissionDefinition.class, AtomCMIS.PERMISSIONS);
      classes2QName.put(CmisAccessControlEntryType.class, AtomCMIS.PERMISSION);

      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_BOOLEAN_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_DATE_TIME_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_DECIMAL_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_HTML_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_ID_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_INTEGER_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_STRING_DEFINITION);
      classes2QName.put(PropertyDefinitionImpl.class, AtomCMIS.PROPERTY_URI_DEFINITION);

      classes2QName.put(CmisObjectImpl.class, AtomCMIS.OBJECT);
      classes2QName.put(CmisObjectImpl.class, AtomCMIS.RELATIOSNHIP);

      classes2QName.put(BooleanProperty.class, AtomCMIS.PROPERTY_BOOLEAN);
      classes2QName.put(DateTimeProperty.class, AtomCMIS.PROPERTY_DATE_TIME);
      classes2QName.put(DecimalProperty.class, AtomCMIS.PROPERTY_DECIMAL);
      classes2QName.put(HtmlProperty.class, AtomCMIS.PROPERTY_HTML);
      classes2QName.put(IdProperty.class, AtomCMIS.PROPERTY_ID);
      classes2QName.put(IntegerProperty.class, AtomCMIS.PROPERTY_INTEGER);
      classes2QName.put(StringProperty.class, AtomCMIS.PROPERTY_STRING);
      classes2QName.put(UriProperty.class, AtomCMIS.PROPERTY_URI);

      classes2QName.put(CmisAllowableActionsType.class, AtomCMIS.ALLOWABLE_ACTIONS);
      classes2QName.put(CmisQueryType.class, AtomCMIS.QUERY);
      classes2QName.put(CmisChangeEventType.class, AtomCMIS.CHANGE_EVENT_INFO);
      classes2QName.put(CmisPermissionDefinition.class, AtomCMIS.PERMISSIONS);
      classes2QName.put(PermissionMappingElement.class, AtomCMIS.MAPPING);
      classes2QName.put(CmisACLCapabilityType.class, AtomCMIS.ACL_CAPABILITY);
      classes2QName.put(CmisListOfIdsType.class, AtomCMIS.POLICY_IDS);
      classes2QName.put(CmisRenditionType.class, AtomCMIS.RENDITION);
      classes2QName.put(CmisUriTemplateType.class, AtomCMIS.URITEMPLATE);
      classes2QName.put(ContentTypeElement.class, AtomCMIS.CONTENT);
   }

   /**
    * Gets the element name.
    * 
    * @param elementClazz the element clazz
    * @return the element name
    */
   public static QName getElementName(Class<?> elementClazz)
   {
      return classes2QName.get(elementClazz);
   }

}
