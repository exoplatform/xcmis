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

package org.xcmis.gwtframework.client.atom;

import org.xcmis.gwtframework.client.CmisNameSpace;
import org.xcmis.gwtframework.client.model.CmisAllowableActionsType;
import org.xcmis.gwtframework.client.model.property.CmisPropertiesType;
import org.xcmis.gwtframework.client.model.property.CmisPropertyBoolean;
import org.xcmis.gwtframework.client.model.property.CmisPropertyDateTime;
import org.xcmis.gwtframework.client.model.property.CmisPropertyId;
import org.xcmis.gwtframework.client.model.property.CmisPropertyInteger;
import org.xcmis.gwtframework.client.model.property.CmisPropertyString;
import org.xcmis.gwtframework.client.unmarshallers.parser.AllowableActionsParser;
import org.xcmis.gwtframework.client.unmarshallers.parser.PropertiesParser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class GwtTestObjectService extends GWTTestCase
{
   private String allowableActionsResponse =
      "<?xml version=\"1.0\" ?>"
         + "<cmis:allowableActions xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:app=\"http://www.w3.org/2007/app\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
         + "<cmis:canDeleteObject>true</cmis:canDeleteObject>"
         + "<cmis:canUpdateProperties>true</cmis:canUpdateProperties>"
         + "<cmis:canGetFolderTree>false</cmis:canGetFolderTree>"
         + "<cmis:canGetProperties>true</cmis:canGetProperties>"
         + "<cmis:canGetObjectRelationships>true</cmis:canGetObjectRelationships>"
         + "<cmis:canGetObjectParents>true</cmis:canGetObjectParents>"
         + "<cmis:canGetFolderParent>false</cmis:canGetFolderParent>"
         + "<cmis:canGetDescendants>false</cmis:canGetDescendants>" + "<cmis:canMoveObject>true</cmis:canMoveObject>"
         + "<cmis:canDeleteContentStream>false</cmis:canDeleteContentStream>"
         + "<cmis:canCheckOut>false</cmis:canCheckOut>" + "<cmis:canCancelCheckOut>false</cmis:canCancelCheckOut>"
         + "<cmis:canCheckIn>false</cmis:canCheckIn>" + "<cmis:canSetContentStream>false</cmis:canSetContentStream>"
         + "<cmis:canGetAllVersions>false</cmis:canGetAllVersions>"
         + "<cmis:canAddObjectToFolder>false</cmis:canAddObjectToFolder>"
         + "<cmis:canRemoveObjectFromFolder>false</cmis:canRemoveObjectFromFolder>"
         + "<cmis:canGetContentStream>false</cmis:canGetContentStream>"
         + "<cmis:canApplyPolicy>true</cmis:canApplyPolicy>"
         + "<cmis:canGetAppliedPolicies>true</cmis:canGetAppliedPolicies>"
         + "<cmis:canRemovePolicy>true</cmis:canRemovePolicy>" + "<cmis:canGetChildren>false</cmis:canGetChildren>"
         + "<cmis:canCreateDocument>false</cmis:canCreateDocument>"
         + "<cmis:canCreateFolder>false</cmis:canCreateFolder>"
         + "<cmis:canCreateRelationship>true</cmis:canCreateRelationship>"
         + "<cmis:canDeleteTree>false</cmis:canDeleteTree>" + "<cmis:canGetRenditions>false</cmis:canGetRenditions>"
         + "<cmis:canGetACL>true</cmis:canGetACL>"
         + "<cmis:canApplyACL>true</cmis:canApplyACL></cmis:allowableActions>";

   private String propertiesResponse =
      "<?xml version=\"1.0\" ?>"
         + "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<id>4c0ccf2bc0a8001d00b8232ccc2073b5</id>"
         + "<published>2010-03-11T09:07:40.203Z</published>"
         + "<updated>2010-03-11T09:29:49.734Z</updated>"
         + "<summary type=\"text\"></summary>"
         + "<author>"
         + "<name>__anonim</name>"
         + "</author>"
         + "<title type=\"text\">111</title>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<content type=\"image/png\" src=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/file/4c0ccf2bc0a8001d00b8232ccc2073b5\"></content>"
         + "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<cmis:properties>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
         + "<cmis:value>cmis:document</cmis:value>"
         + "</cmis:propertyId>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:checkinComment\" localName=\"cmis:checkinComment\"></cmis:propertyString>"
         + "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
         + "<cmis:value>2010-03-11T09:29:49.734Z</cmis:value>"
         + "</cmis:propertyDateTime>"
         + "<cmis:propertyBoolean propertyDefinitionId=\"cmis:isImmutable\" localName=\"cmis:isImmutable\">"
         + "<cmis:value>false</cmis:value>"
         + "</cmis:propertyBoolean>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:versionSeriesCheckedOutBy\" localName=\"cmis:versionSeriesCheckedOutBy\"></cmis:propertyString>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:versionLabel\" localName=\"cmis:versionLabel\">"
         + "<cmis:value>latest</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyBoolean propertyDefinitionId=\"cmis:isLatestVersion\" localName=\"cmis:isLatestVersion\">"
         + "<cmis:value>true</cmis:value>"
         + "</cmis:propertyBoolean>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
         + "<cmis:value>__anonim</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
         + "<cmis:value>cmis:document</cmis:value>"
         + "</cmis:propertyId>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
         + "<cmis:value>111</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:contentStreamId\" localName=\"cmis:contentStreamId\">"
         + "<cmis:value>4c0ccf2bc0a8001d01aea545d8553156</cmis:value>"
         + "</cmis:propertyId>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:contentStreamFileName\" localName=\"cmis:contentStreamFileName\">"
         + "<cmis:value>111</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:contentStreamMimeType\" localName=\"cmis:contentStreamMimeType\">"
         + "<cmis:value>image/png</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
         + "<cmis:value>4c2118a6c0a8001d01e435f51aac3df4</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyInteger propertyDefinitionId=\"cmis:contentStreamLength\" localName=\"cmis:contentStreamLength\">"
         + "<cmis:value>964</cmis:value>"
         + "</cmis:propertyInteger>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:versionSeriesCheckedOutId\" localName=\"cmis:versionSeriesCheckedOutId\"></cmis:propertyId>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:versionSeriesId\" localName=\"cmis:versionSeriesId\">"
         + "<cmis:value>4c0ccf2bc0a8001d007bf31a4afe9a65</cmis:value>"
         + "</cmis:propertyId>"
         + "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
         + "<cmis:value>4c0ccf2bc0a8001d00b8232ccc2073b5</cmis:value>"
         + "</cmis:propertyId>"
         + "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
         + "<cmis:value>2010-03-11T09:07:40.203Z</cmis:value>"
         + "</cmis:propertyDateTime>"
         + "<cmis:propertyBoolean propertyDefinitionId=\"cmis:isMajorVersion\" localName=\"cmis:isMajorVersion\">"
         + "<cmis:value>true</cmis:value>"
         + "</cmis:propertyBoolean>"
         + "<cmis:propertyBoolean propertyDefinitionId=\"cmis:isVersionSeriesCheckedOut\" localName=\"cmis:isVersionSeriesCheckedOut\">"
         + "<cmis:value>false</cmis:value>"
         + "</cmis:propertyBoolean>"
         + "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
         + "<cmis:value>__anonim</cmis:value>"
         + "</cmis:propertyString>"
         + "<cmis:propertyBoolean propertyDefinitionId=\"cmis:isLatestMajorVersion\" localName=\"cmis:isLatestMajorVersion\">"
         + "<cmis:value>true</cmis:value>" + "</cmis:propertyBoolean>" + "</cmis:properties>"
         + "</cmisra:object></entry>";

   @Override
   public String getModuleName()
   {
      return "org.xcmis.gwtframework.CmisClientFrameworkJUnit";
   }

   public void testGetAllowableActions()
   {
      Document doc = XMLParser.parse(allowableActionsResponse);
      Node node = doc.getElementsByTagName(CmisNameSpace.ALLOWABLE_ACTIONS).item(0);
      CmisAllowableActionsType allowableActions = new CmisAllowableActionsType();
      AllowableActionsParser.parse(node, allowableActions);
      assertTrue(allowableActions.isCanDeleteObject());
      assertTrue(allowableActions.isCanUpdateProperties());
      assertTrue(allowableActions.isCanGetProperties());
      assertTrue(allowableActions.isCanGetObjectRelationships());
      assertTrue(allowableActions.isCanGetObjectParents());
      assertFalse(allowableActions.isCanGetFolderTree());
      assertFalse(allowableActions.isCanGetFolderParent());
      assertFalse(allowableActions.isCanGetDescendants());
      assertFalse(allowableActions.isCanDeleteContentStream());
      assertFalse(allowableActions.isCanCheckIn());
      assertFalse(allowableActions.isCanCheckOut());
      assertFalse(allowableActions.isCanGetAllVersions());
      assertFalse(allowableActions.isCanAddObjectToFolder());
      assertFalse(allowableActions.isCanRemoveObjectFromFolder());
      assertFalse(allowableActions.isCanGetContentStream());
      assertTrue(allowableActions.isCanApplyPolicy());
      assertTrue(allowableActions.isCanRemovePolicy());
      assertTrue(allowableActions.isCanGetAppliedPolicies());
      assertTrue(allowableActions.isCanCreateRelationship());
      assertTrue(allowableActions.isCanGetACL());
      assertTrue(allowableActions.isCanApplyACL());
      assertFalse(allowableActions.isCanCreateDocument());
      assertFalse(allowableActions.isCanCreateFolder());
      assertFalse(allowableActions.isCanDeleteTree());
   }

   public void testGetProperties()
   {
      Document doc = XMLParser.parse(propertiesResponse);
      Node node = doc.getElementsByTagName("properties").item(0);
      CmisPropertiesType properties = PropertiesParser.parse(node);
      assertEquals(23, properties.getProperty().size());
      
      CmisPropertyId propertyId = (CmisPropertyId)properties.getProperty().get(0);
      assertEquals("cmis:objectTypeId", propertyId.getPropertyDefinitionId());
      assertEquals("cmis:document", propertyId.getValue().get(0));
      
      CmisPropertyString propertyString = (CmisPropertyString)properties.getProperty().get(1);
      assertEquals("cmis:checkinComment", propertyString.getPropertyDefinitionId());
      assertEquals(0, propertyString.getValue().size());
      
      CmisPropertyDateTime propertyDateTime = (CmisPropertyDateTime)properties.getProperty().get(2);
      assertEquals("cmis:lastModificationDate", propertyDateTime.getPropertyDefinitionId());
      
      CmisPropertyBoolean propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(3);
      assertEquals("cmis:isImmutable", propertyBoolean.getPropertyDefinitionId());
      assertFalse(propertyBoolean.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(4);
      assertEquals("cmis:versionSeriesCheckedOutBy", propertyString.getPropertyDefinitionId());
      assertEquals(0, propertyString.getValue().size());
      
      propertyString = (CmisPropertyString)properties.getProperty().get(5);
      assertEquals("cmis:versionLabel", propertyString.getPropertyDefinitionId());
      assertEquals("latest", propertyString.getValue().get(0));
      
      propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(6);
      assertEquals("cmis:isLatestVersion", propertyBoolean.getPropertyDefinitionId());
      assertTrue(propertyBoolean.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(7);
      assertEquals("cmis:createdBy", propertyString.getPropertyDefinitionId());
      assertEquals("__anonim", propertyString.getValue().get(0));
      
      propertyId = (CmisPropertyId)properties.getProperty().get(8);
      assertEquals("cmis:baseTypeId", propertyId.getPropertyDefinitionId());
      assertEquals("cmis:document", propertyId.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(9);
      assertEquals("cmis:name", propertyString.getPropertyDefinitionId());
      assertEquals("111", propertyString.getValue().get(0));
      
      propertyId = (CmisPropertyId)properties.getProperty().get(10);
      assertEquals("cmis:contentStreamId", propertyId.getPropertyDefinitionId());
      assertEquals("4c0ccf2bc0a8001d01aea545d8553156", propertyId.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(11);
      assertEquals("cmis:contentStreamFileName", propertyString.getPropertyDefinitionId());
      assertEquals("111", propertyString.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(12);
      assertEquals("cmis:contentStreamMimeType", propertyString.getPropertyDefinitionId());
      assertEquals("image/png", propertyString.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(13);
      assertEquals("cmis:changeToken", propertyString.getPropertyDefinitionId());
      assertEquals("4c2118a6c0a8001d01e435f51aac3df4", propertyString.getValue().get(0));
      
      CmisPropertyInteger propertyInteger = (CmisPropertyInteger)properties.getProperty().get(14);
      assertEquals("cmis:contentStreamLength", propertyInteger.getPropertyDefinitionId());
      assertEquals("964", String.valueOf(propertyInteger.getValue().get(0)));
      
      propertyId = (CmisPropertyId)properties.getProperty().get(15);
      assertEquals("cmis:versionSeriesCheckedOutId", propertyId.getPropertyDefinitionId());
      assertEquals("", propertyId.getValue().get(0));
      
      propertyId = (CmisPropertyId)properties.getProperty().get(16);
      assertEquals("cmis:versionSeriesId", propertyId.getPropertyDefinitionId());
      assertEquals("4c0ccf2bc0a8001d007bf31a4afe9a65", propertyId.getValue().get(0));
      
      propertyId = (CmisPropertyId)properties.getProperty().get(17);
      assertEquals("cmis:objectId", propertyId.getPropertyDefinitionId());
      assertEquals("4c0ccf2bc0a8001d00b8232ccc2073b5", propertyId.getValue().get(0));
      
      propertyDateTime = (CmisPropertyDateTime)properties.getProperty().get(18);
      assertEquals("cmis:creationDate", propertyDateTime.getPropertyDefinitionId());
      
      propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(19);
      assertEquals("cmis:isMajorVersion", propertyBoolean.getPropertyDefinitionId());
      assertTrue(propertyBoolean.getValue().get(0));
      
      propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(19);
      assertEquals("cmis:isMajorVersion", propertyBoolean.getPropertyDefinitionId());
      assertTrue(propertyBoolean.getValue().get(0));
      
      propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(20);
      assertEquals("cmis:isVersionSeriesCheckedOut", propertyBoolean.getPropertyDefinitionId());
      assertFalse(propertyBoolean.getValue().get(0));
      
      propertyString = (CmisPropertyString)properties.getProperty().get(21);
      assertEquals("cmis:lastModifiedBy", propertyString.getPropertyDefinitionId());
      assertEquals("__anonim", propertyString.getValue().get(0));
      
      propertyBoolean = (CmisPropertyBoolean)properties.getProperty().get(22);
      assertEquals("cmis:isLatestMajorVersion", propertyBoolean.getPropertyDefinitionId());
      assertTrue(propertyBoolean.getValue().get(0));
   }
   
}
