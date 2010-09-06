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

package org.xcmis.client.gwt;

import java.util.List;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.EnumCapabilityACL;
import org.xcmis.client.gwt.model.EnumCapabilityChanges;
import org.xcmis.client.gwt.model.EnumCapabilityContentStreamUpdates;
import org.xcmis.client.gwt.model.EnumCapabilityJoin;
import org.xcmis.client.gwt.model.EnumCapabilityQuery;
import org.xcmis.client.gwt.model.EnumCapabilityRendition;
import org.xcmis.client.gwt.model.property.DateTimePropertyDefinition;
import org.xcmis.client.gwt.model.property.IdPropertyDefinition;
import org.xcmis.client.gwt.model.property.StringPropertyDefinition;
import org.xcmis.client.gwt.model.repository.CmisRepositoryInfo;
import org.xcmis.client.gwt.model.restatom.EnumCollectionType;
import org.xcmis.client.gwt.model.restatom.TypeEntry;
import org.xcmis.client.gwt.model.type.TypeDefinition;
import org.xcmis.client.gwt.rest.UnmarshallerException;
import org.xcmis.client.gwt.unmarshallers.parser.RepositoriesParser;
import org.xcmis.client.gwt.unmarshallers.parser.RepositoryInfoParser;
import org.xcmis.client.gwt.unmarshallers.parser.TypeParser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

public class GwtTestRepositoryService extends GWTTestCase
{

   private String repositoriesResponse =
      "<?xml version='1.0' encoding='UTF-8'?>"
         + "<service xmlns=\"http://www.w3.org/2007/app\" xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<workspace>"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">default</title>"
         + "<cmisra:repositoryInfo>"
         + "<cmis:repositoryId>default</cmis:repositoryId>"
         + "<cmis:repositoryName>default</cmis:repositoryName>"
         + "<cmis:repositoryDescription>Repository default</cmis:repositoryDescription>"
         + "<cmis:vendorName>Nuxeo</cmis:vendorName>"
         + "<cmis:productName>Nuxeo Repository</cmis:productName>"
         + "<cmis:productVersion>5.3.1-SNAPSHOT</cmis:productVersion>"
         + "<cmis:rootFolderId>4fb1b8a1-6dfd-4da4-95b0-4ef41c27b920</cmis:rootFolderId>"
         + "<cmis:latestChangeLogToken></cmis:latestChangeLogToken>"
         + "<cmis:capabilities>"
         + "<cmis:capabilityACL>none</cmis:capabilityACL>"
         + "<cmis:capabilityAllVersionsSearchable>true</cmis:capabilityAllVersionsSearchable>"
         + "<cmis:capabilityChanges>none</cmis:capabilityChanges>"
         + "<cmis:capabilityContentStreamUpdatability>anytime</cmis:capabilityContentStreamUpdatability>"
         + "<cmis:capabilityGetDescendants>false</cmis:capabilityGetDescendants>"
         + "<cmis:capabilityGetFolderTree>false</cmis:capabilityGetFolderTree>"
         + "<cmis:capabilityMultifiling>false</cmis:capabilityMultifiling>"
         + "<cmis:capabilityPWCSearchable>true</cmis:capabilityPWCSearchable>"
         + "<cmis:capabilityPWCUpdatable>true</cmis:capabilityPWCUpdatable>"
         + "<cmis:capabilityQuery>bothcombined</cmis:capabilityQuery>"
         + "<cmis:capabilityRenditions>none</cmis:capabilityRenditions>"
         + "<cmis:capabilityUnfiling>false</cmis:capabilityUnfiling>"
         + "<cmis:capabilityVersionSpecificFiling>false</cmis:capabilityVersionSpecificFiling>"
         + "<cmis:capabilityJoin>innerandouter</cmis:capabilityJoin>"
         + "</cmis:capabilities>"
         + "<cmis:cmisVersionSupported>1.0</cmis:cmisVersionSupported>"
         + "<cmis:changesIncomplete>false</cmis:changesIncomplete>"
         + "<cmis:changesOnType>cmis:policy</cmis:changesOnType>"
         + "<cmis:changesOnType>cmis:folder</cmis:changesOnType>"
         + "<cmis:changesOnType>cmis:relationship</cmis:changesOnType>"
         + "<cmis:changesOnType>cmis:document</cmis:changesOnType>"
         + "</cmisra:repositoryInfo>"
         + "<collection href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/children/4fb1b8a1-6dfd-4da4-95b0-4ef41c27b920\">"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">children collection</title>"
         + "<cmisra:collectionType>root</cmisra:collectionType>"
         + "</collection>"
         + "<collection href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typechildren\">"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">Types</title>"
         + "<cmisra:collectionType>types</cmisra:collectionType>"
         + "</collection>"
         + "<collection href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/checkedout\">"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">the checkedout</title>"
         + "<cmisra:collectionType>checkedout</cmisra:collectionType>"
         + "</collection>"
         + "<collection href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/unfiled\">"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">the unfiled</title>"
         + "<cmisra:collectionType>unfiled</cmisra:collectionType>"
         + "</collection>"
         + "<collection href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/query\">"
         + "<title xmlns=\"http://www.w3.org/2005/Atom\" type=\"text\">query collection</title>"
         + "<cmisra:collectionType>query</cmisra:collectionType>"
         + "</collection>"
         + "<link xmlns=\"http://www.w3.org/2005/Atom\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/typedescendants\" type=\"application/cmistree+xml\" href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typedescendants\" />"
         + "<link xmlns=\"http://www.w3.org/2005/Atom\" rel=\"http://docs.oasis-open.org/ns/cmis/link/200908/foldertree\" type=\"application/cmistree+xml\" href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/foldertree/4fb1b8a1-6dfd-4da4-95b0-4ef41c27b920\" />"
         + "<cmisra:uritemplate>"
         + "<cmisra:type>objectbyid</cmisra:type>"
         + "<cmisra:mediatype>application/atom+xml;type=entry</cmisra:mediatype>"
         + "<cmisra:template>http://cmis.demo.nuxeo.org/nuxeo/site/cmis/object/{id}?filter={filter}&amp;renditionFilter={renditionFilter}&amp;includeRelationships={includeRelationships}&amp;includeAllowableActions={includeAllowableActions}&amp;includePolicyIds={includePolicyIds}&amp;includeACL={includeACL}</cmisra:template></cmisra:uritemplate><cmisra:uritemplate><cmisra:type>objectbypath</cmisra:type><cmisra:mediatype>application/atom+xml;type=entry</cmisra:mediatype><cmisra:template>http://cmis.demo.nuxeo.org/nuxeo/site/cmis/object?path={path}&amp;filter={filter}&amp;renditionFilter={renditionFilter}&amp;includeRelationships={includeRelationships}&amp;includeAllowableActions={includeAllowableActions}&amp;includePolicyIds={includePolicyIds}&amp;includeACL={includeACL}</cmisra:template></cmisra:uritemplate><cmisra:uritemplate><cmisra:type>query</cmisra:type><cmisra:mediatype>application/atom+xml;type=feed</cmisra:mediatype><cmisra:template>http://cmis.demo.nuxeo.org/nuxeo/site/cmis/query?q={q}&amp;searchAllVersions={searchAllVersions}&amp;maxItems={maxItems}&amp;skipCount={skipCount}&amp;includeRelationships={includeRelationships}&amp;includeAllowableActions={includeAllowableActions}</cmisra:template></cmisra:uritemplate><cmisra:uritemplate><cmisra:type>typebyid</cmisra:type><cmisra:mediatype>application/atom+xml;type=feed</cmisra:mediatype><cmisra:template>http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/{id}</cmisra:template></cmisra:uritemplate></workspace></service>";

   private String typeChildrenResponse =
      "<?xml version='1.0' encoding='UTF-8'?>"
         + "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\" xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
         + "<id>urn:x-id:types</id>"
         + "<title type=\"text\">Types</title>"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typechildren?maxItems=20\" rel=\"self\" />"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<updated>2010-03-10T09:21:53.965Z</updated>"
         + "<entry>"
         + "<id>urn:x-tid:cmis:document</id>"
         + "<title type=\"text\">cmis:document</title>"
         + "<updated>2010-03-10T09:21:53.967Z</updated>"
         + "<summary type=\"text\">cmis:document</summary>"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Adocument\" rel=\"self\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Adocument\" rel=\"edit\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Adocument\" rel=\"alternate\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typechildren/cmis%3Adocument\" rel=\"down\" type=\"application/atom+xml; type=feed\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typedescendants/cmis%3Adocument\" rel=\"down\" type=\"application/cmistree+xml\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Adocument\" rel=\"describedby\" type=\"application/atom+xml; type=entry\" />"
         + "<cmisra:type cmisra:id=\"cmis:document\" xsi:type=\"cmis:cmisTypeDocumentDefinitionType\">"
         + "<cmis:id>cmis:document</cmis:id>"
         + "<cmis:localName>cmis:document</cmis:localName>"
         + "<cmis:localNamespace></cmis:localNamespace>"
         + "<cmis:queryName>cmis:document</cmis:queryName>"
         + "<cmis:displayName>cmis:document</cmis:displayName>"
         + "<cmis:baseId>cmis:document</cmis:baseId>"
         + "<cmis:parentId></cmis:parentId>"
         + "<cmis:description>cmis:document</cmis:description>"
         + "<cmis:creatable>true</cmis:creatable>"
         + "<cmis:fileable>true</cmis:fileable>"
         + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:controllablePolicy>true</cmis:controllablePolicy>"
         + "<cmis:controllableACL>true</cmis:controllableACL>"
         + "<cmis:fulltextIndexed>true</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>true</cmis:includedInSupertypeQuery>"
         + "<cmis:versionable>true</cmis:versionable>"
         + "<cmis:contentStreamAllowed>notallowed</cmis:contentStreamAllowed>"
         + "</cmisra:type>"
         + "</entry>"
         + "<entry>"
         + "<id>urn:x-tid:cmis:folder</id>"
         + "<title type=\"text\">cmis:folder</title>"
         + "<updated>2010-03-10T09:21:53.973Z</updated>"
         + "<summary type=\"text\">cmis:folder</summary>"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Afolder\" rel=\"self\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Afolder\" rel=\"edit\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Afolder\" rel=\"alternate\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typechildren/cmis%3Afolder\" rel=\"down\" type=\"application/atom+xml; type=feed\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typedescendants/cmis%3Afolder\" rel=\"down\" type=\"application/cmistree+xml\" />"
         + "<link href=\"http://cmis.demo.nuxeo.org/nuxeo/site/cmis/type/cmis%3Afolder\" rel=\"describedby\" type=\"application/atom+xml; type=entry\" />"
         + "<cmisra:type cmisra:id=\"cmis:folder\" xsi:type=\"cmis:cmisTypeFolderDefinitionType\">"
         + "<cmis:id>cmis:folder</cmis:id>" + "<cmis:localName>cmis:folder</cmis:localName>"
         + "<cmis:localNamespace></cmis:localNamespace>" + "<cmis:queryName>cmis:folder</cmis:queryName>"
         + "<cmis:displayName>cmis:folder</cmis:displayName>" + "<cmis:baseId>cmis:folder</cmis:baseId>"
         + "<cmis:parentId></cmis:parentId>" + "<cmis:description>cmis:folder</cmis:description>"
         + "<cmis:creatable>true</cmis:creatable>" + "<cmis:fileable>true</cmis:fileable>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:controllablePolicy>true</cmis:controllablePolicy>"
         + "<cmis:controllableACL>true</cmis:controllableACL>" + "<cmis:fulltextIndexed>true</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>true</cmis:includedInSupertypeQuery>"
         + "<cmis:versionable>false</cmis:versionable>"
         + "<cmis:contentStreamAllowed>notallowed</cmis:contentStreamAllowed>" + "</cmisra:type>" + "</entry>"
         + "</feed>";

   private String typeDescendantsResponse =
      "<?xml version=\"1.0\" ?>"
         + "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
         + "<id>cmis:folder</id>"
         + "<title type=\"text\">Type Descendants</title>"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<updated>2010-03-10T12:35:48.000Z</updated>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/cmis%3Afolder\" rel=\"via\" type=\"application/atom+xml; type=entry\"></link>"
         + "<entry xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<id>webdav:folder</id>"
         + "<title type=\"text\">webdav:folder</title>"
         + "<updated>2010-03-10T12:35:48.015Z</updated>"
         + "<published>2010-03-10T12:35:48.015Z</published>"
         + "<summary type=\"text\"></summary>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/webdav%3Afolder\" rel=\"self\" type=\"application/atom+xml; type=entry\"></link>"
         + "<cmisra:type xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"cmis:cmisTypeFolderDefinitionType\">"
         + "<cmis:id>webdav:folder</cmis:id>"
         + "<cmis:localName>webdav:folder</cmis:localName>"
         + "<cmis:localNamespace>http://www.exoplatform.com/jcr/cmis/1.0</cmis:localNamespace>"
         + "<cmis:displayName>webdav:folder</cmis:displayName>"
         + "<cmis:queryName>webdav:folder</cmis:queryName>"
         + "<cmis:description>Cmis Folder Type</cmis:description>"
         + "<cmis:baseId>cmis:folder</cmis:baseId>"
         + "<cmis:parentId>cmis:folder</cmis:parentId>"
         + "<cmis:creatable>true</cmis:creatable>"
         + "<cmis:fileable>true</cmis:fileable>"
         + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:fulltextIndexed>false</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>true</cmis:includedInSupertypeQuery>"
         + "<cmis:controllablePolicy>true</cmis:controllablePolicy>"
         + "<cmis:controllable>true</cmis:controllable>"
         + "</cmisra:type>"
         + "<cmisra:children>"
         + "<feed>"
         + "<id>ch:webdav:folder</id>"
         + "<title type=\"text\">Type Children</title>"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<updated>2010-03-10T12:35:48.015Z</updated>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/webdav%3Afolder\" rel=\"self\" type=\"application/atom+xml; type=entry\"></link>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/webdav%3Afolder\" rel=\"via\" type=\"application/atom+xml; type=entry\"></link>"
         + "</feed>"
         + "<cmisra:numItems>2</cmisra:numItems>"
         + "<entry>"
         + "<id>new</id>"
         + "<title type=\"text\">new</title>"
         + "<updated>2010-03-10T12:35:48.015Z</updated>"
         + "<published>2010-03-10T12:35:48.015Z</published>"
         + "<summary type=\"text\"></summary>"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typebyid/new\" rel=\"self\" type=\"application/atom+xml; type=entry\"></link>"
         + "<cmisra:type xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"cmis:cmisTypeFolderDefinitionType\">"
         + "<cmis:id>new</cmis:id>"
         + "<cmis:localName>new</cmis:localName>"
         + "<cmis:localNamespace>http://www.exoplatform.com/jcr/cmis/1.0</cmis:localNamespace>"
         + "<cmis:displayName>new</cmis:displayName>"
         + "<cmis:queryName>new</cmis:queryName>"
         + "<cmis:description>Cmis Folder Type</cmis:description>"
         + "<cmis:baseId>cmis:folder</cmis:baseId>"
         + "<cmis:parentId>webdav:folder</cmis:parentId>"
         + "<cmis:creatable>true</cmis:creatable>"
         + "<cmis:fileable>true</cmis:fileable>"
         + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:fulltextIndexed>false</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>true</cmis:includedInSupertypeQuery>"
         + "<cmis:controllablePolicy>true</cmis:controllablePolicy>"
         + "<cmis:controllable>true</cmis:controllable>"
         + "</cmisra:type>"
         + "</entry>"
         + "<entry>"
         + "<id>new2</id>"
         + "<title type=\"text\">new2</title>"
         + "<updated>2010-03-10T12:35:48.031Z</updated>"
         + "<published>2010-03-10T12:35:48.031Z</published>"
         + "<summary type=\"text\"></summary>"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/xcmis/rest/cmisatom/cmis1/typedescendants/new2\" rel=\"down\" type=\"application/cmistree+xml\"></link>"
         + "<cmisra:type xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"cmis:cmisTypeFolderDefinitionType\">"
         + "<cmis:id>new2</cmis:id>" + "<cmis:localName>new2</cmis:localName>"
         + "<cmis:localNamespace>http://www.exoplatform.com/jcr/cmis/1.0</cmis:localNamespace>"
         + "<cmis:displayName>new2</cmis:displayName>" + "<cmis:queryName>new2</cmis:queryName>"
         + "<cmis:description>Cmis Folder Type</cmis:description>" + "<cmis:baseId>cmis:folder</cmis:baseId>"
         + "<cmis:parentId>webdav:folder</cmis:parentId>" + "<cmis:creatable>true</cmis:creatable>"
         + "<cmis:fileable>true</cmis:fileable>" + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:fulltextIndexed>false</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>true</cmis:includedInSupertypeQuery>"
         + "<cmis:controllablePolicy>true</cmis:controllablePolicy>" + "<cmis:controllable>true</cmis:controllable>"
         + "</cmisra:type>" + "</entry>" + "</cmisra:children>" + "</entry>" + "</feed>";

   private String getTypeDefinitionIncludePropertyDefinition =
      "<?xml version=\"1.0\" ?>"
         + "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
         + "<id>cmis:types</id>"
         + "<title type=\"text\">Types Children</title>"
         + "<author>"
         + "<name>system</name>"
         + "</author>"
         + "<updated>2009-12-02T11:20:26.843Z</updated>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production/types/\" rel=\"self\" type=\"application/atom+xml; type=feed\"></link>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production/types/?maxItems=2147483647&amp;skipCount=0\"  rel=\"first\" type=\"application/atom+xml; type=feed\"></link>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production/typedescendants/\" rel=\"down\" type=\"application/cmistree+xml\"></link>"
         + "<entry xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<id>cmis:folder</id>"
         + "<title type=\"text\">Cmis Folder Type</title>"
         + "<updated>2009-12-02T11:20:26.953Z</updated>"
         + "<summary type=\"text\"></summary>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production\" rel=\"service\" type=\"application/atomsvc+xml\"></link>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production/types/cmis%3Afolder\" rel=\"self\" type=\"application/atom+xml; type=entry\"></link>"
         + "<link href=\"http://localhost:8888/portal/rest/cmis/repository%40production/typedescendants/cmis%3Afolder\" rel=\"down\" type=\"application/cmistree+xml\"></link>"
         + "<cmisra:type xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">"
         + "<cmis:id>cmis:relationship</cmis:id>" + "<cmis:localName>cmis:relationship</cmis:localName>"
         + "<cmis:localNamespace>http://www.exoplatform.com/jcr/cmis/1.0</cmis:localNamespace>"
         + "<cmis:displayName>cmis:relationship</cmis:displayName>"
         + "<cmis:queryName>cmis:relationship</cmis:queryName>"
         + "<cmis:description>Cmis Relationship Type</cmis:description>"
         + "<cmis:baseId>cmis:relationship</cmis:baseId>" + "<cmis:parentId></cmis:parentId>"
         + "<cmis:creatable>true</cmis:creatable>" + "<cmis:fileable>false</cmis:fileable>"
         + "<cmis:queryable>false</cmis:queryable>" + "<cmis:fulltextIndexed>false</cmis:fulltextIndexed>"
         + "<cmis:includedInSupertypeQuery>false</cmis:includedInSupertypeQuery>"
         + "<cmis:controllablePolicy>false</cmis:controllablePolicy>" + "<cmis:controllable>false</cmis:controllable>"

         + "<cmis:propertyIdDefinition>" + "<cmis:id>cmis:sourceId</cmis:id>"
         + "<cmis:propertyType>id</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>oncreate</cmis:updatability>" + "<cmis:queryName>cmis:sourceId</cmis:queryName>"
         + "<cmis:localName>cmis:sourceId</cmis:localName>" + "<cmis:displayName>cmis:sourceId</cmis:displayName>"
         + "<cmis:description>Relationship source ID.</cmis:description>" + "<cmis:inherited>false</cmis:inherited>"
         + "<cmis:required>true</cmis:required>" + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:orderable>true</cmis:orderable>" + "<cmis:openChoice>false</cmis:openChoice>"
         + "</cmis:propertyIdDefinition>" + "<cmis:propertyIdDefinition>" + "<cmis:id>cmis:targetId</cmis:id>"
         + "<cmis:propertyType>id</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>oncreate</cmis:updatability>" + "<cmis:queryName>cmis:targetId</cmis:queryName>"
         + "<cmis:localName>cmis:targetId</cmis:localName>" + "<cmis:displayName>cmis:targetId</cmis:displayName>"
         + "<cmis:description>Relationship target ID.</cmis:description>" + "<cmis:inherited>false</cmis:inherited>"
         + "<cmis:required>true</cmis:required>" + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:orderable>true</cmis:orderable>" + "<cmis:openChoice>false</cmis:openChoice>"
         + "</cmis:propertyIdDefinition>" + "<cmis:propertyDateTimeDefinition>"
         + "<cmis:id>cmis:lastModificationDate</cmis:id>" + "<cmis:propertyType>datetime</cmis:propertyType>"
         + "<cmis:cardinality>single</cmis:cardinality>" + "<cmis:updatability>readonly</cmis:updatability>"
         + "<cmis:queryName>cmis:lastModificationDate</cmis:queryName>"
         + "<cmis:localName>cmis:lastModificationDate</cmis:localName>"
         + "<cmis:displayName>cmis:lastModificationDate</cmis:displayName>"
         + "<cmis:description>Date when object was last modified.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "</cmis:propertyDateTimeDefinition>"
         + "<cmis:propertyIdDefinition>" + "<cmis:id>cmis:baseTypeId</cmis:id>"
         + "<cmis:propertyType>id</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:baseTypeId</cmis:queryName>"
         + "<cmis:localName>cmis:baseTypeId</cmis:localName>" + "<cmis:displayName>cmis:baseTypeId</cmis:displayName>"
         + "<cmis:description>Uniquely identifier for base object's type.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "</cmis:propertyIdDefinition>"
         + "<cmis:propertyStringDefinition>" + "<cmis:id>cmis:name</cmis:id>"
         + "<cmis:propertyType>string</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readwrite</cmis:updatability>" + "<cmis:queryName>cmis:name</cmis:queryName>"
         + "<cmis:localName>cmis:name</cmis:localName>" + "<cmis:displayName>cmis:name</cmis:displayName>"
         + "<cmis:description>Object's name.</cmis:description>" + "<cmis:inherited>false</cmis:inherited>"
         + "<cmis:required>false</cmis:required>" + "<cmis:queryable>true</cmis:queryable>"
         + "<cmis:orderable>true</cmis:orderable>" + "<cmis:openChoice>false</cmis:openChoice>"
         + "<cmis:maxLength>65536</cmis:maxLength>" + "</cmis:propertyStringDefinition>"
         + "<cmis:propertyStringDefinition>" + "<cmis:id>cmis:changeToken</cmis:id>"
         + "<cmis:propertyType>string</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:changeToken</cmis:queryName>"
         + "<cmis:localName>cmis:changeToken</cmis:localName>"
         + "<cmis:displayName>cmis:changeToken</cmis:displayName>"
         + "<cmis:description>Token used for optimistic locking and concurrency checking.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "<cmis:maxLength>65536</cmis:maxLength>"
         + "</cmis:propertyStringDefinition>" + "<cmis:propertyIdDefinition>" + "<cmis:id>cmis:objectId</cmis:id>"
         + "<cmis:propertyType>id</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:objectId</cmis:queryName>"
         + "<cmis:localName>cmis:objectId</cmis:localName>" + "<cmis:displayName>cmis:objectId</cmis:displayName>"
         + "<cmis:description>Uniquely object's identifier.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "</cmis:propertyIdDefinition>"
         + "<cmis:propertyDateTimeDefinition>" + "<cmis:id>cmis:creationDate</cmis:id>"
         + "<cmis:propertyType>datetime</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:creationDate</cmis:queryName>"
         + "<cmis:localName>jcr:created</cmis:localName>" + "<cmis:displayName>cmis:creationDate</cmis:displayName>"
         + "<cmis:description>Date when object was created.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "</cmis:propertyDateTimeDefinition>"
         + "<cmis:propertyStringDefinition>" + "<cmis:id>cmis:lastModifiedBy</cmis:id>"
         + "<cmis:propertyType>string</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:lastModifiedBy</cmis:queryName>"
         + "<cmis:localName>cmis:lastModifiedBy</cmis:localName>"
         + "<cmis:displayName>cmis:lastModifiedBy</cmis:displayName>"
         + "<cmis:description>User's name who last modified this object.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "<cmis:maxLength>65536</cmis:maxLength>"
         + "</cmis:propertyStringDefinition>" + "<cmis:propertyIdDefinition>" + "<cmis:id>cmis:objectTypeId</cmis:id>"
         + "<cmis:propertyType>id</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + "<cmis:updatability>oncreate</cmis:updatability>" + "<cmis:queryName>cmis:objectTypeId</cmis:queryName>"
         + "<cmis:localName>cmis:objectTypeId</cmis:localName>"
         + "<cmis:displayName>cmis:objectTypeId</cmis:displayName>"
         + "<cmis:description>Uniquely identifier for object's type.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>true</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "</cmis:propertyIdDefinition>"
         + "<cmis:propertyStringDefinition>" + "<cmis:id>cmis:createdBy</cmis:id>"
         + "<cmis:propertyType>string</cmis:propertyType>" + "<cmis:cardinality>single</cmis:cardinality>"
         + " <cmis:updatability>readonly</cmis:updatability>" + "<cmis:queryName>cmis:createdBy</cmis:queryName>"
         + "<cmis:localName>exo:owner</cmis:localName>" + "<cmis:displayName>cmis:createdBy</cmis:displayName>"
         + "<cmis:description>User's name who created this object.</cmis:description>"
         + "<cmis:inherited>false</cmis:inherited>" + "<cmis:required>false</cmis:required>"
         + "<cmis:queryable>true</cmis:queryable>" + "<cmis:orderable>true</cmis:orderable>"
         + "<cmis:openChoice>false</cmis:openChoice>" + "<cmis:maxLength>65536</cmis:maxLength>"
         + "</cmis:propertyStringDefinition>" + "</cmisra:type>" + "</entry>" + "</feed>";

   public String getModuleName()
   {
      return "org.xcmis.CmisClientFrameworkJUnit";
   }

   public void testGetRepositories()
   {
      Document doc = XMLParser.parse(repositoriesResponse);
      List<CmisRepositoryInfo> repositoryInfoList = RepositoriesParser.parse(doc);

      assertEquals(1, repositoryInfoList.size());
      //main info
      assertEquals("default", repositoryInfoList.get(0).getRepositoryId());
      assertEquals("default", repositoryInfoList.get(0).getRepositoryName());
      assertEquals("Repository default", repositoryInfoList.get(0).getRepositoryDescription());
      assertEquals("Nuxeo", repositoryInfoList.get(0).getVendorName());
      assertEquals("Nuxeo Repository", repositoryInfoList.get(0).getProductName());
      assertEquals("5.3.1-SNAPSHOT", repositoryInfoList.get(0).getProductVersion());
      assertEquals("4fb1b8a1-6dfd-4da4-95b0-4ef41c27b920", repositoryInfoList.get(0).getRootFolderId());
      assertEquals(null, repositoryInfoList.get(0).getLatestChangeLogToken());
      assertEquals(null, repositoryInfoList.get(0).getThinClientURI());
   }

   public void testGetRepositoryCapabilities()
   {
      Document doc = XMLParser.parse(repositoriesResponse);
      Node repositoryNode = doc.getElementsByTagName(CMIS.WORKSPACE).item(0);

      CmisRepositoryInfo repositoryInfo = new CmisRepositoryInfo();
      RepositoryInfoParser.parse(repositoryNode, repositoryInfo);

      //capabilities
      assertEquals(EnumCapabilityACL.NONE, repositoryInfo.getCapabilities().getCapabilityACL());
      assertTrue(repositoryInfo.getCapabilities().isCapabilityAllVersionsSearchable());
      assertEquals(EnumCapabilityChanges.NONE, repositoryInfo.getCapabilities().getCapabilityChanges());
      assertEquals(EnumCapabilityContentStreamUpdates.ANYTIME, repositoryInfo.getCapabilities()
         .getCapabilityContentStreamUpdatability());
      assertFalse(repositoryInfo.getCapabilities().isCapabilityGetDescendants());
      assertFalse(repositoryInfo.getCapabilities().isCapabilityGetFolderTree());
      assertFalse(repositoryInfo.getCapabilities().isCapabilityMultifiling());
      assertEquals(EnumCapabilityQuery.BOTHCOMBINED, repositoryInfo.getCapabilities().getCapabilityQuery());
      assertEquals(EnumCapabilityRendition.NONE, repositoryInfo.getCapabilities().getCapabilityRenditions());
      assertTrue(repositoryInfo.getCapabilities().isCapabilityPWCSearchable());
      assertTrue(repositoryInfo.getCapabilities().isCapabilityPWCUpdatable());
      assertFalse(repositoryInfo.getCapabilities().isCapabilityUnfiling());
      assertFalse(repositoryInfo.getCapabilities().isCapabilityVersionSpecificFiling());
      assertEquals(EnumCapabilityJoin.INNERANDOUTER, repositoryInfo.getCapabilities().getCapabilityJoin());
   }

   public void testGetRepositoryCollections()
   {
      Document doc = XMLParser.parse(repositoriesResponse);
      List<CmisRepositoryInfo> repositoryInfoList = RepositoriesParser.parse(doc);
      //collections
      assertEquals(EnumCollectionType.ROOT, repositoryInfoList.get(0).getCollections().get(0).getType());
      assertEquals("http://cmis.demo.nuxeo.org/nuxeo/site/cmis/children/4fb1b8a1-6dfd-4da4-95b0-4ef41c27b920",
         repositoryInfoList.get(0).getCollections().get(0).getHref());

      assertEquals(EnumCollectionType.TYPES, repositoryInfoList.get(0).getCollections().get(1).getType());
      assertEquals("http://cmis.demo.nuxeo.org/nuxeo/site/cmis/typechildren", repositoryInfoList.get(0)
         .getCollections().get(1).getHref());

      assertEquals(EnumCollectionType.CHECKEDOUT, repositoryInfoList.get(0).getCollections().get(2).getType());
      assertEquals("http://cmis.demo.nuxeo.org/nuxeo/site/cmis/checkedout", repositoryInfoList.get(0).getCollections()
         .get(2).getHref());

      assertEquals(EnumCollectionType.UNFILED, repositoryInfoList.get(0).getCollections().get(3).getType());
      assertEquals("http://cmis.demo.nuxeo.org/nuxeo/site/cmis/unfiled", repositoryInfoList.get(0).getCollections()
         .get(3).getHref());

      assertEquals(EnumCollectionType.QUERY, repositoryInfoList.get(0).getCollections().get(4).getType());
      assertEquals("http://cmis.demo.nuxeo.org/nuxeo/site/cmis/query", repositoryInfoList.get(0).getCollections()
         .get(4).getHref());
   }

   public void testTypeChildrenResponse() throws UnmarshallerException
   {
      Document doc = XMLParser.parse(typeChildrenResponse);
      List<TypeEntry> types = TypeParser.getTypes(doc);
      assertEquals(2, types.size());
      TypeEntry type = types.get(0);
      assertEquals(6, type.getLinks().size());

      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getId());
      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getLocalName());
      assertEquals(null, type.getTypeCmisTypeDefinition().getLocalNamespace());
      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getBaseId().value());
      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getDescription());
      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getDisplayName());
      assertEquals("cmis:document", type.getTypeCmisTypeDefinition().getQueryName());
      assertEquals(null, type.getTypeCmisTypeDefinition().getParentId());
      assertTrue(type.getTypeCmisTypeDefinition().isCreatable());
      assertTrue(type.getTypeCmisTypeDefinition().isQueryable());
      assertTrue(type.getTypeCmisTypeDefinition().isFileable());
      assertTrue(type.getTypeCmisTypeDefinition().isControllablePolicy());
      assertTrue(type.getTypeCmisTypeDefinition().isControllableACL());
      assertTrue(type.getTypeCmisTypeDefinition().isFulltextIndexed());
      assertTrue(type.getTypeCmisTypeDefinition().isIncludedInSupertypeQuery());

      type = types.get(1);
      assertEquals(6, type.getLinks().size());

      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getId());
      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getLocalName());
      assertEquals(null, type.getTypeCmisTypeDefinition().getLocalNamespace());
      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getBaseId().value());
      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getDescription());
      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getDisplayName());
      assertEquals("cmis:folder", type.getTypeCmisTypeDefinition().getQueryName());
      assertEquals(null, type.getTypeCmisTypeDefinition().getParentId());
      assertTrue(type.getTypeCmisTypeDefinition().isCreatable());
      assertTrue(type.getTypeCmisTypeDefinition().isQueryable());
      assertTrue(type.getTypeCmisTypeDefinition().isFileable());
      assertTrue(type.getTypeCmisTypeDefinition().isControllablePolicy());
      assertTrue(type.getTypeCmisTypeDefinition().isControllableACL());
      assertTrue(type.getTypeCmisTypeDefinition().isFulltextIndexed());
      assertTrue(type.getTypeCmisTypeDefinition().isIncludedInSupertypeQuery());

   }

  
   public void testGetTypesWithPropertyDefinition() throws UnmarshallerException
   {
      Document doc = XMLParser.parse(getTypeDefinitionIncludePropertyDefinition);
      Node entryNode = doc.getElementsByTagName(CMIS.ENTRY).item(0);

      TypeEntry typeEntry = new TypeEntry();

      TypeParser.getTypeEntry(entryNode, typeEntry);
      assertEquals(11, typeEntry.getTypeCmisTypeDefinition().getPropertyDefinitions().size());

      
      IdPropertyDefinition propertyDefinition = (IdPropertyDefinition)typeEntry.getTypeCmisTypeDefinition().getPropertyDefinition(CMIS.CMIS_SOURCE_ID);
      assertEquals("cmis:sourceId", propertyDefinition.getId());
      assertEquals("id", propertyDefinition.getPropertyType().value());
      assertEquals("single", propertyDefinition.getCardinality().value());
      assertEquals("oncreate", propertyDefinition.getUpdatability().value());
      assertEquals("cmis:sourceId", propertyDefinition.getQueryName());
      assertEquals("cmis:sourceId", propertyDefinition.getDisplayName());
      assertEquals("Relationship source ID.", propertyDefinition.getDescription());
      assertFalse(propertyDefinition.isInherited());
      assertTrue(propertyDefinition.isRequired());
      assertTrue(propertyDefinition.isQueryable());
      assertTrue(propertyDefinition.isOrderable());
      assertFalse(propertyDefinition.isOpenChoice());

      propertyDefinition = (IdPropertyDefinition)typeEntry.getTypeCmisTypeDefinition().getPropertyDefinition(CMIS.CMIS_TARGET_ID);
      assertEquals("cmis:targetId", propertyDefinition.getId());
      assertEquals("id", propertyDefinition.getPropertyType().value());
      assertEquals("single", propertyDefinition.getCardinality().value());
      assertEquals("oncreate", propertyDefinition.getUpdatability().value());
      assertEquals("cmis:targetId", propertyDefinition.getQueryName());
      assertEquals("cmis:targetId", propertyDefinition.getLocalName());
      assertEquals("cmis:targetId", propertyDefinition.getDisplayName());
      assertEquals("Relationship target ID.", propertyDefinition.getDescription());
      assertFalse(propertyDefinition.isInherited());
      assertTrue(propertyDefinition.isRequired());
      assertTrue(propertyDefinition.isQueryable());
      assertTrue(propertyDefinition.isOrderable());
      assertFalse(propertyDefinition.isOpenChoice());

      DateTimePropertyDefinition propertyDate = (DateTimePropertyDefinition)typeEntry.getTypeCmisTypeDefinition().getPropertyDefinition(CMIS.CMIS_LAST_MODIFICATION_DATE);
      assertEquals("cmis:lastModificationDate", propertyDate.getId());
      assertEquals("datetime", propertyDate.getPropertyType().value());
      assertEquals("single", propertyDate.getCardinality().value());
      assertEquals("readonly", propertyDate.getUpdatability().value());
      assertEquals("cmis:lastModificationDate", propertyDate.getQueryName());
      assertEquals("cmis:lastModificationDate", propertyDate.getLocalName());
      assertEquals("cmis:lastModificationDate", propertyDate.getDisplayName());
      assertEquals("Date when object was last modified.", propertyDate.getDescription());
      assertFalse(propertyDate.isInherited());
      assertFalse(propertyDate.isRequired());
      assertTrue(propertyDate.isQueryable());
      assertTrue(propertyDate.isOrderable());
      assertFalse(propertyDate.isOpenChoice());

      StringPropertyDefinition propertyString = (StringPropertyDefinition)typeEntry.getTypeCmisTypeDefinition().getPropertyDefinition(CMIS.CMIS_NAME);
      assertEquals("cmis:name", propertyString.getId());
      assertEquals("string", propertyString.getPropertyType().value());
      assertEquals("single", propertyString.getCardinality().value());
      assertEquals("readwrite", propertyString.getUpdatability().value());
      assertEquals("cmis:name", propertyString.getQueryName());
      assertEquals("cmis:name", propertyString.getLocalName());
      assertEquals("cmis:name", propertyString.getDisplayName());
      assertEquals("Object's name.", propertyString.getDescription());
      assertEquals(new Long(65536), propertyString.getMaxLength());
      assertFalse(propertyString.isInherited());
      assertFalse(propertyString.isRequired());
      assertTrue(propertyString.isQueryable());
      assertTrue(propertyString.isOrderable());
      assertFalse(propertyString.isOpenChoice());

      propertyString = (StringPropertyDefinition)typeEntry.getTypeCmisTypeDefinition().getPropertyDefinition(CMIS.CMIS_CREATED_BY);
      assertEquals("cmis:createdBy", propertyString.getId());
      assertEquals("string", propertyString.getPropertyType().value());
      assertEquals("single", propertyString.getCardinality().value());
      assertEquals("readonly", propertyString.getUpdatability().value());
      assertEquals("cmis:createdBy", propertyString.getQueryName());
      assertEquals("exo:owner", propertyString.getLocalName());
      assertEquals("cmis:createdBy", propertyString.getDisplayName());
      assertEquals("User's name who created this object.", propertyString.getDescription());
      assertEquals(new Long(65536), propertyString.getMaxLength());
      assertFalse(propertyString.isInherited());
      assertFalse(propertyString.isRequired());
      assertTrue(propertyString.isQueryable());
      assertTrue(propertyString.isOrderable());
      assertFalse(propertyString.isOpenChoice());
   }

   public void testGetTypeDescendants() throws UnmarshallerException
   {
      Document doc = XMLParser.parse(typeDescendantsResponse);
      List<TypeEntry> types = TypeParser.getTypes(doc);
      assertEquals(1, types.size());
      
      TypeEntry parentType = types.get(0);
      assertEquals(2, parentType.getLinks().size());
      assertEquals("webdav:folder", parentType.getTypeCmisTypeDefinition().getId());
      assertEquals("webdav:folder", parentType.getTypeCmisTypeDefinition().getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", parentType.getTypeCmisTypeDefinition().getLocalNamespace());
      assertEquals("webdav:folder", parentType.getTypeCmisTypeDefinition().getDisplayName());
      assertEquals("webdav:folder", parentType.getTypeCmisTypeDefinition().getQueryName());
      assertEquals("Cmis Folder Type", parentType.getTypeCmisTypeDefinition().getDescription());
      assertEquals("cmis:folder", parentType.getTypeCmisTypeDefinition().getBaseId().value());
      assertEquals("cmis:folder", parentType.getTypeCmisTypeDefinition().getParentId());
      assertTrue(parentType.getTypeCmisTypeDefinition().isCreatable());
      assertTrue(parentType.getTypeCmisTypeDefinition().isFileable());
      assertTrue(parentType.getTypeCmisTypeDefinition().isQueryable());
      assertTrue(parentType.getTypeCmisTypeDefinition().isIncludedInSupertypeQuery());
      assertTrue(parentType.getTypeCmisTypeDefinition().isControllablePolicy());
      assertFalse(parentType.getTypeCmisTypeDefinition().isFulltextIndexed());
      assertEquals(2, parentType.getChildren().size());
      
      
      TypeEntry child = parentType.getChildren().get(0);
      assertEquals(2, child.getLinks().size());
      assertEquals("new", child.getTypeCmisTypeDefinition().getId());
      assertEquals("new", child.getTypeCmisTypeDefinition().getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", child.getTypeCmisTypeDefinition().getLocalNamespace());
      assertEquals("new", child.getTypeCmisTypeDefinition().getDisplayName());
      assertEquals("new", child.getTypeCmisTypeDefinition().getQueryName());
      assertEquals("Cmis Folder Type", child.getTypeCmisTypeDefinition().getDescription());
      assertEquals("cmis:folder", child.getTypeCmisTypeDefinition().getBaseId().value());
      assertEquals("webdav:folder", child.getTypeCmisTypeDefinition().getParentId());
      assertTrue(child.getTypeCmisTypeDefinition().isCreatable());
      assertTrue(child.getTypeCmisTypeDefinition().isFileable());
      assertTrue(child.getTypeCmisTypeDefinition().isQueryable());
      assertTrue(child.getTypeCmisTypeDefinition().isIncludedInSupertypeQuery());
      assertTrue(child.getTypeCmisTypeDefinition().isControllablePolicy());
      assertFalse(child.getTypeCmisTypeDefinition().isFulltextIndexed());
      
      child = parentType.getChildren().get(1);
      assertEquals(2, child.getLinks().size());
      assertEquals("new2", child.getTypeCmisTypeDefinition().getId());
      assertEquals("new2", child.getTypeCmisTypeDefinition().getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", child.getTypeCmisTypeDefinition().getLocalNamespace());
      assertEquals("new2", child.getTypeCmisTypeDefinition().getDisplayName());
      assertEquals("new2", child.getTypeCmisTypeDefinition().getQueryName());
      assertEquals("Cmis Folder Type", child.getTypeCmisTypeDefinition().getDescription());
      assertEquals("cmis:folder", child.getTypeCmisTypeDefinition().getBaseId().value());
      assertEquals("webdav:folder", child.getTypeCmisTypeDefinition().getParentId());
      assertTrue(child.getTypeCmisTypeDefinition().isCreatable());
      assertTrue(child.getTypeCmisTypeDefinition().isFileable());
      assertTrue(child.getTypeCmisTypeDefinition().isQueryable());
      assertTrue(child.getTypeCmisTypeDefinition().isIncludedInSupertypeQuery());
      assertTrue(child.getTypeCmisTypeDefinition().isControllablePolicy());
      assertFalse(child.getTypeCmisTypeDefinition().isFulltextIndexed());
   }
   
   public void testGetTypeList() throws UnmarshallerException
   {
      Document doc = XMLParser.parse(typeDescendantsResponse);
      List<TypeDefinition> types = TypeParser.getTypeList(doc);
      assertEquals(3, types.size());
      
      TypeDefinition type = types.get(0);
      assertEquals("webdav:folder", type.getId());
      assertEquals("webdav:folder", type.getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", type.getLocalNamespace());
      assertEquals("webdav:folder", type.getDisplayName());
      assertEquals("webdav:folder", type.getQueryName());
      assertEquals("Cmis Folder Type", type.getDescription());
      assertEquals("cmis:folder", type.getBaseId().value());
      assertEquals("cmis:folder", type.getParentId());
      assertTrue(type.isCreatable());
      assertTrue(type.isFileable());
      assertTrue(type.isQueryable());
      assertTrue(type.isIncludedInSupertypeQuery());
      assertTrue(type.isControllablePolicy());
      assertFalse(type.isFulltextIndexed());
      
      type = types.get(1);
      assertEquals("new", type.getId());
      assertEquals("new", type.getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", type.getLocalNamespace());
      assertEquals("new", type.getDisplayName());
      assertEquals("new", type.getQueryName());
      assertEquals("Cmis Folder Type", type.getDescription());
      assertEquals("cmis:folder", type.getBaseId().value());
      assertEquals("webdav:folder", type.getParentId());
      assertTrue(type.isCreatable());
      assertTrue(type.isFileable());
      assertTrue(type.isQueryable());
      assertTrue(type.isIncludedInSupertypeQuery());
      assertTrue(type.isControllablePolicy());
      assertFalse(type.isFulltextIndexed());
      
      type = types.get(2);
      assertEquals("new2", type.getId());
      assertEquals("new2", type.getLocalName());
      assertEquals("http://www.exoplatform.com/jcr/cmis/1.0", type.getLocalNamespace());
      assertEquals("new2", type.getDisplayName());
      assertEquals("new2", type.getQueryName());
      assertEquals("Cmis Folder Type", type.getDescription());
      assertEquals("cmis:folder", type.getBaseId().value());
      assertEquals("webdav:folder", type.getParentId());
      assertTrue(type.isCreatable());
      assertTrue(type.isFileable());
      assertTrue(type.isQueryable());
      assertTrue(type.isIncludedInSupertypeQuery());
      assertTrue(type.isControllablePolicy());
      assertFalse(type.isFulltextIndexed());
   }
}
