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

package org.xcmis.client.gwt.client.atom;

import java.util.List;
import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.property.CmisProperties;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;
import org.xcmis.client.gwt.client.unmarshallers.parser.FeedParser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class GwtTestNavigationService extends GWTTestCase {

	private String childrenResponse = "<?xml version=\"1.0\" ?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<id>00exo0jcr0root0uuid0000000000000</id>"
			+ "<title type=\"text\">Folder Children</title>"
			+ "<author>"
			+ "<name>anonymous</name>"
			+ "</author>"
			+ "<updated>2010-03-10T14:25:20.546Z</updated>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/children/00exo0jcr0root0uuid0000000000000?maxItems=20&amp;skipCount=0\" rel=\"first\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<entry xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<id>48091ca7c0a8001d0059cab8ec875af9</id>"
			+ "<published>2010-03-10T14:25:09.031Z</published>"
			+ "<updated>2010-03-10T14:25:09.031Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<title type=\"text\">policy1</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/allowableactions/48091ca7c0a8001d0059cab8ec875af9\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/allowableactions\" type=\"application/cmis+xml; type=allowableActions\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/relationships/48091ca7c0a8001d0059cab8ec875af9\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/relationships\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/policies/48091ca7c0a8001d0059cab8ec875af9\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/policies\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/objacl/48091ca7c0a8001d0059cab8ec875af9\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/acl\" type=\"application/cmisacl+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/parents/48091ca7c0a8001d0059cab8ec875af9\" rel=\"up\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<content type=\"text\">policy1</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>48091ca7c0a8001d01df1f78a46b9a6d</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:policy</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:policyText\" localName=\"cmis:policyText\">"
			+ "<cmis:value>some policy text</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:25:09.031Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>48091ca7c0a8001d0059cab8ec875af9</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:25:09.031Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>policy1</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:policy</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "</cmis:properties>"
			+ "<cmis:allowableActions xmlns:app=\"http://www.w3.org/2007/app\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
			+ "<cmis:canDeleteObject>true</cmis:canDeleteObject>"
			+ "<cmis:canUpdateProperties>true</cmis:canUpdateProperties>"
			+ "<cmis:canGetFolderTree>false</cmis:canGetFolderTree>"
			+ "<cmis:canGetProperties>true</cmis:canGetProperties>"
			+ "<cmis:canGetObjectRelationships>true</cmis:canGetObjectRelationships>"
			+ "<cmis:canGetObjectParents>true</cmis:canGetObjectParents>"
			+ "<cmis:canGetFolderParent>false</cmis:canGetFolderParent>"
			+ "<cmis:canGetDescendants>false</cmis:canGetDescendants>"
			+ "<cmis:canMoveObject>true</cmis:canMoveObject>"
			+ "<cmis:canDeleteContentStream>false</cmis:canDeleteContentStream>"
			+ "<cmis:canCheckOut>false</cmis:canCheckOut>"
			+ "<cmis:canCancelCheckOut>false</cmis:canCancelCheckOut>"
			+ "<cmis:canCheckIn>false</cmis:canCheckIn>"
			+ "<cmis:canSetContentStream>false</cmis:canSetContentStream>"
			+ "<cmis:canGetAllVersions>false</cmis:canGetAllVersions>"
			+ "<cmis:canAddObjectToFolder>false</cmis:canAddObjectToFolder>"
			+ "<cmis:canRemoveObjectFromFolder>false</cmis:canRemoveObjectFromFolder>"
			+ "<cmis:canGetContentStream>false</cmis:canGetContentStream>"
			+ "<cmis:canApplyPolicy>true</cmis:canApplyPolicy>"
			+ "<cmis:canGetAppliedPolicies>true</cmis:canGetAppliedPolicies>"
			+ "<cmis:canRemovePolicy>true</cmis:canRemovePolicy>"
			+ "<cmis:canGetChildren>false</cmis:canGetChildren>"
			+ "<cmis:canCreateDocument>false</cmis:canCreateDocument>"
			+ "<cmis:canCreateFolder>false</cmis:canCreateFolder>"
			+ "<cmis:canCreateRelationship>true</cmis:canCreateRelationship>"
			+ "<cmis:canDeleteTree>false</cmis:canDeleteTree>"
			+ "<cmis:canGetRenditions>false</cmis:canGetRenditions>"
			+ "<cmis:canGetACL>true</cmis:canGetACL>"
			+ "<cmis:canApplyACL>true</cmis:canApplyACL>"
			+ "</cmis:allowableActions>"
			+ "</cmisra:object>"
			+ "</entry>"
			+ "<entry xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<id>480947ddc0a8001d002b0ad6f8103d7f</id>"
			+ "<published>2010-03-10T14:25:20.109Z</published>"
			+ "<updated>2010-03-10T14:25:20.109Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<title type=\"text\">new folder</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/cmis%3Afolder\" rel=\"describedby\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/objacl/480947ddc0a8001d002b0ad6f8103d7f\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/acl\" type=\"application/cmisacl+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/children/480947ddc0a8001d002b0ad6f8103d7f\" rel=\"down\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/descendants/480947ddc0a8001d002b0ad6f8103d7f\" rel=\"down\" type=\"application/cmistree+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/foldertree/480947ddc0a8001d002b0ad6f8103d7f\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/foldertree\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/00exo0jcr0root0uuid0000000000000\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<content type=\"text\">new folder</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:parentId\" localName=\"cmis:parentId\">"
			+ "<cmis:value>00exo0jcr0root0uuid0000000000000</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:path\" localName=\"cmis:path\">"
			+ "<cmis:value>/new folder</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>480947edc0a8001d0165c6546a183b87</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:allowedChildObjectTypeIds\" localName=\"cmis:allowedChildObjectTypeIds\"></cmis:propertyId>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:25:20.109Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>480947ddc0a8001d002b0ad6f8103d7f</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:25:20.109Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>new folder</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "</cmis:properties>"
			+ "<cmis:allowableActions xmlns:app=\"http://www.w3.org/2007/app\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
			+ "<cmis:canDeleteObject>true</cmis:canDeleteObject>"
			+ "<cmis:canUpdateProperties>true</cmis:canUpdateProperties>"
			+ "<cmis:canGetFolderTree>true</cmis:canGetFolderTree>"
			+ "<cmis:canGetProperties>true</cmis:canGetProperties>"
			+ "<cmis:canGetObjectRelationships>true</cmis:canGetObjectRelationships>"
			+ "<cmis:canGetObjectParents>true</cmis:canGetObjectParents>"
			+ "<cmis:canGetFolderParent>true</cmis:canGetFolderParent>"
			+ "<cmis:canGetDescendants>true</cmis:canGetDescendants>"
			+ "<cmis:canMoveObject>true</cmis:canMoveObject>"
			+ "<cmis:canDeleteContentStream>false</cmis:canDeleteContentStream>"
			+ "<cmis:canCheckOut>false</cmis:canCheckOut>"
			+ "<cmis:canCancelCheckOut>false</cmis:canCancelCheckOut>"
			+ "<cmis:canCheckIn>false</cmis:canCheckIn>"
			+ "<cmis:canSetContentStream>false</cmis:canSetContentStream>"
			+ "<cmis:canGetAllVersions>false</cmis:canGetAllVersions>"
			+ "<cmis:canAddObjectToFolder>false</cmis:canAddObjectToFolder>"
			+ "<cmis:canRemoveObjectFromFolder>false</cmis:canRemoveObjectFromFolder>"
			+ "<cmis:canGetContentStream>false</cmis:canGetContentStream>"
			+ "<cmis:canApplyPolicy>true</cmis:canApplyPolicy>"
			+ "<cmis:canGetAppliedPolicies>true</cmis:canGetAppliedPolicies>"
			+ "<cmis:canRemovePolicy>true</cmis:canRemovePolicy>"
			+ "<cmis:canGetChildren>true</cmis:canGetChildren>"
			+ "<cmis:canCreateDocument>true</cmis:canCreateDocument>"
			+ "<cmis:canCreateFolder>true</cmis:canCreateFolder>"
			+ "<cmis:canCreateRelationship>true</cmis:canCreateRelationship>"
			+ "<cmis:canDeleteTree>true</cmis:canDeleteTree>"
			+ "<cmis:canGetRenditions>false</cmis:canGetRenditions>"
			+ "<cmis:canGetACL>true</cmis:canGetACL>"
			+ "<cmis:canApplyACL>true</cmis:canApplyACL>"
			+ "</cmis:allowableActions>" + "</cmisra:object>" + "</entry>"
			+ "</feed>";

	private String descendantsResponse = "<?xml version=\"1.0\" ?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<id>480947ddc0a8001d002b0ad6f8103d7f</id>"
			+ "<title type=\"text\">Folder Descendants</title>"
			+ "<author>"
			+ "<name>anonymous</name>"
			+ "</author>"
			+ "<updated>2010-03-10T14:53:05.093Z</updated>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/00exo0jcr0root0uuid0000000000000\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<cmisra:numItems>1</cmisra:numItems>"
			+ "<entry>"
			+ "<id>4821f203c0a8001d01287762111f2a24</id>"
			+ "<published>2010-03-10T14:52:16.515Z</published>"
			+ "<updated>2010-03-10T14:52:16.515Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<title type=\"text\">1</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/children/4821f203c0a8001d01287762111f2a24\" rel=\"down\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/480947ddc0a8001d002b0ad6f8103d7f\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<content type=\"text\">1</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:parentId\" localName=\"cmis:parentId\">"
			+ "<cmis:value>480947ddc0a8001d002b0ad6f8103d7f</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:path\" localName=\"cmis:path\">"
			+ "<cmis:value>/new folder/1</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>4821f203c0a8001d01cbf34880d7b920</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:allowedChildObjectTypeIds\" localName=\"cmis:allowedChildObjectTypeIds\"></cmis:propertyId>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:52:16.515Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>4821f203c0a8001d01287762111f2a24</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:52:16.515Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>1</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "</cmis:properties>"
			+ "</cmisra:object>"
			+ "<cmisra:pathSegment>1</cmisra:pathSegment>"
			+ "<cmisra:children>"
			+ "<feed>"
			+ "<id>ch:4821f203c0a8001d01287762111f2a24</id>"
			+ "<title type=\"text\">Folder Children</title>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<updated>2010-03-10T14:52:16.515Z</updated>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/4821f203c0a8001d01287762111f2a24\" rel=\"self\"></link>"
			+ "<entry>"
			+ "<id>48220bb5c0a8001d012f8e6ccc129f0b</id>"
			+ "<published>2010-03-10T14:52:23.093Z</published>"
			+ "<updated>2010-03-10T14:52:23.093Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<title type=\"text\">12</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/4821f203c0a8001d01287762111f2a24\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<content type=\"text\">12</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:parentId\" localName=\"cmis:parentId\">"
			+ "<cmis:value>4821f203c0a8001d01287762111f2a24</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:path\" localName=\"cmis:path\">"
			+ "<cmis:value>/new folder/1/12</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>48220bb5c0a8001d007a4839794993f2</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:allowedChildObjectTypeIds\" localName=\"cmis:allowedChildObjectTypeIds\"></cmis:propertyId>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:52:23.093Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>48220bb5c0a8001d012f8e6ccc129f0b</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:52:23.093Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>12</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "</cmis:properties>"
			+ "</cmisra:object>"
			+ "<cmisra:pathSegment>12</cmisra:pathSegment>"
			+ "<cmisra:children>"
			+ "<feed>"
			+ "<id>ch:48220bb5c0a8001d012f8e6ccc129f0b</id>"
			+ "<title type=\"text\">Folder Children</title>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<updated>2010-03-10T14:52:23.093Z</updated>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/4821f203c0a8001d01287762111f2a24\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/48220bb5c0a8001d012f8e6ccc129f0b\" rel=\"via\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<entry>"
			+ "<id>4822469bc0a8001d009d8ece43ad7742</id>"
			+ "<published>2010-03-10T14:52:38.171Z</published>"
			+ "<updated>2010-03-10T14:52:38.171Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<title type=\"text\">121</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/descendants/4822469bc0a8001d009d8ece43ad7742\" rel=\"down\" type=\"application/cmistree+xml\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/foldertree/4822469bc0a8001d009d8ece43ad7742\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/foldertree\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/48220bb5c0a8001d012f8e6ccc129f0b\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<content type=\"text\">121</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:parentId\" localName=\"cmis:parentId\">"
			+ "<cmis:value>48220bb5c0a8001d012f8e6ccc129f0b</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:path\" localName=\"cmis:path\">"
			+ "<cmis:value>/new folder/1/12/121</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>4822469bc0a8001d01c1698df689d276</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:allowedChildObjectTypeIds\" localName=\"cmis:allowedChildObjectTypeIds\"></cmis:propertyId>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:52:38.171Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>4822469bc0a8001d009d8ece43ad7742</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:52:38.171Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>121</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "</cmis:properties>"
			+ "</cmisra:object>"
			+ "<cmisra:pathSegment>121</cmisra:pathSegment>"
			+ "</entry>"
			+ "</feed>"
			+ "<cmisra:numItems>1</cmisra:numItems>"
			+ "</cmisra:children>"
			+ "</entry>"
			+ "<entry>"
			+ "<id>48221eb1c0a8001d002236a9b68d39f7</id>"
			+ "<published>2010-03-10T14:52:27.953Z</published>"
			+ "<updated>2010-03-10T14:52:27.953Z</updated>"
			+ "<summary type=\"text\"></summary>"
			+ "<author>"
			+ "<name>__anonim</name>"
			+ "</author>"
			+ "<title type=\"text\">13</title>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/foldertree/48221eb1c0a8001d002236a9b68d39f7\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/foldertree\" type=\"application/atom+xml; type=feed\"></link>"
			+ "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/object/4821f203c0a8001d01287762111f2a24\" rel=\"up\" type=\"application/atom+xml; type=entry\"></link>"
			+ "<content type=\"text\">13</content>"
			+ "<cmisra:object xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
			+ "<cmis:properties>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:parentId\" localName=\"cmis:parentId\">"
			+ "<cmis:value>4821f203c0a8001d01287762111f2a24</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:path\" localName=\"cmis:path\">"
			+ "<cmis:value>/new folder/1/13</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:changeToken\" localName=\"cmis:changeToken\">"
			+ "<cmis:value>48221eb1c0a8001d0049194c4e7e9cd3</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\" localName=\"cmis:objectTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:allowedChildObjectTypeIds\" localName=\"cmis:allowedChildObjectTypeIds\"></cmis:propertyId>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:lastModificationDate\" localName=\"cmis:lastModificationDate\">"
			+ "<cmis:value>2010-03-10T14:52:27.953Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:objectId\" localName=\"cmis:objectId\">"
			+ "<cmis:value>48221eb1c0a8001d002236a9b68d39f7</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:createdBy\" localName=\"cmis:createdBy\">"
			+ "<cmis:value>__anonim</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyDateTime propertyDefinitionId=\"cmis:creationDate\" localName=\"cmis:creationDate\">"
			+ "<cmis:value>2010-03-10T14:52:27.953Z</cmis:value>"
			+ "</cmis:propertyDateTime>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:name\" localName=\"cmis:name\">"
			+ "<cmis:value>13</cmis:value>"
			+ "</cmis:propertyString>"
			+ "<cmis:propertyId propertyDefinitionId=\"cmis:baseTypeId\" localName=\"cmis:baseTypeId\">"
			+ "<cmis:value>cmis:folder</cmis:value>"
			+ "</cmis:propertyId>"
			+ "<cmis:propertyString propertyDefinitionId=\"cmis:lastModifiedBy\" localName=\"cmis:lastModifiedBy\">"
			+ "<cmis:value>__anonim</cmis:value>" + "</cmis:propertyString>"
			+ "</cmis:properties>" + "</cmisra:object>"
			+ "<cmisra:pathSegment>13</cmisra:pathSegment>" + "</entry>"
			+ "</feed>" + "<cmisra:numItems>2</cmisra:numItems>"
			+ "</cmisra:children>" + "</entry>" + "</feed>";

	/**
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "org.xcmis.client.gwt.CmisClientFrameworkJUnit";
	}

	public void testGetChildren() {
		Document doc = XMLParser.parse(childrenResponse);
		List<AtomEntry> entryList = FeedParser.parse(doc).getEntries();

		assertEquals(2, entryList.size());

		AtomEntry entry = entryList.get(0);
		assertEquals(5, entry.getEntryInfo().getLinks().size());
		
		CmisProperties properties = entry.getObject().getProperties();
		assertEquals(10, properties.getProperties().size());
		assertEquals("48091ca7c0a8001d01df1f78a46b9a6d", properties.getString(CMIS.CMIS_CHANGE_TOKEN));
		assertEquals("cmis:policy", properties.getString(CMIS.CMIS_OBJECT_TYPE_ID));
		
		assertEquals("some policy text", properties.getString(CMIS.CMIS_POLICY_TEXT));
		assertEquals("48091ca7c0a8001d0059cab8ec875af9", properties.getId(CMIS.CMIS_OBJECT_ID));
		assertEquals("__anonim", properties.getString(CMIS.CMIS_CREATED_BY));
		assertEquals("policy1", properties.getString(CMIS.CMIS_NAME));
		assertEquals("cmis:policy", properties.getId(CMIS.CMIS_BASE_TYPE_ID));
		assertEquals("__anonim", properties.getString(CMIS.CMIS_LAST_MODIFIED_BY));	
		
		entry = entryList.get(1);
		assertEquals(6, entry.getEntryInfo().getLinks().size());
		assertEquals(12, entry.getObject().getProperties().getProperties().size());

		assertTrue(entry.getObject().getAllowableActions().isCanDeleteObject());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanUpdateProperties());
		assertTrue(entry.getObject().getAllowableActions().isCanGetFolderTree());
		assertTrue(entry.getObject().getAllowableActions().isCanGetProperties());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanGetObjectRelationships());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanGetObjectParents());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanGetFolderParent());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanGetDescendants());
		assertFalse(entry.getObject().getAllowableActions()
				.isCanDeleteContentStream());
		assertFalse(entry.getObject().getAllowableActions().isCanCheckOut());
		assertFalse(entry.getObject().getAllowableActions().isCanCheckIn());

		assertFalse(entry.getObject().getAllowableActions()
				.isCanGetAllVersions());
		assertFalse(entry.getObject().getAllowableActions()
				.isCanAddObjectToFolder());
		assertFalse(entry.getObject().getAllowableActions()
				.isCanRemoveObjectFromFolder());
		assertFalse(entry.getObject().getAllowableActions()
				.isCanGetContentStream());

		assertTrue(entry.getObject().getAllowableActions().isCanApplyPolicy());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanGetAppliedPolicies());
		assertTrue(entry.getObject().getAllowableActions().isCanRemovePolicy());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanCreateDocument());
		assertTrue(entry.getObject().getAllowableActions().isCanCreateFolder());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanCreateRelationship());
		assertTrue(entry.getObject().getAllowableActions().isCanDeleteTree());
		assertTrue(entry.getObject().getAllowableActions().isCanGetACL());
		assertTrue(entry.getObject().getAllowableActions()
				.isCanCreateRelationship());
	}

	public void testGetDescendants() {
		Document doc = XMLParser.parse(descendantsResponse);
		List<AtomEntry> entryList = FeedParser.parse(doc).getEntries();
		assertEquals(1, entryList.size());
		AtomEntry entry = entryList.get(0);
		assertEquals(2, entry.getEntryInfo().getLinks().size());
		assertEquals(12, entry.getObject().getProperties().getProperties()
				.size());

		AtomEntry child1 = entry.getChildren().get(0);
		assertEquals(1, child1.getChildren().size());
		assertEquals(2, child1.getEntryInfo().getLinks().size());
		assertEquals("48220bb5c0a8001d012f8e6ccc129f0b", child1.getEntryInfo()
				.getId());
		assertEquals(12, child1.getObject().getProperties().getProperties()
				.size());

		AtomEntry subchild1 = child1.getChildren().get(0);
		assertEquals(0, subchild1.getChildren().size());
		assertEquals(3, subchild1.getEntryInfo().getLinks().size());
		assertEquals("4822469bc0a8001d009d8ece43ad7742", subchild1
				.getEntryInfo().getId());
		assertEquals(12, subchild1.getObject().getProperties().getProperties()
				.size());

		AtomEntry child2 = entry.getChildren().get(1);
		assertEquals(0, child2.getChildren().size());
		assertEquals(2, child2.getEntryInfo().getLinks().size());
		assertEquals("48221eb1c0a8001d002236a9b68d39f7", child2.getEntryInfo()
				.getId());
		assertEquals(12, child2.getObject().getProperties().getProperties()
				.size());
		
	}
}
