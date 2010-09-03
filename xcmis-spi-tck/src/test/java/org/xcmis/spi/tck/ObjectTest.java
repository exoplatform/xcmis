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
package org.xcmis.spi.tck;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectTest extends BaseTest
{

   private static String testRootFolderId;

   private static TypeDefinition folderType;

   private static TypeDefinition documentType;

   private static TypeDefinition folderTypeControllableAcl;

   private static TypeDefinition folderTypeNotControllableAcl;

   private static TypeDefinition folderTypeControllablePolicy;

   private static TypeDefinition folderTypeNotControllablePolicy;

   private static TypeDefinition documentTypeStreamNotSupported;

   private static TypeDefinition documentTypeStreamRequired;

   private static TypeDefinition documentTypeControllableAcl;

   private static TypeDefinition documentTypeNotControllableAcl;

   private static TypeDefinition documentTypeControllablePolicy;

   private static TypeDefinition documentTypeNotControllablePolicy;

   private static TypeDefinition documentTypeVersionable;

   private static TypeDefinition documentTypeNotVersionable;

   private static TypeDefinition policyType;

   private static TypeDefinition policyTypeControllableAcl;

   private static TypeDefinition policyTypeNotControllableAcl;

   private static TypeDefinition policyTypeControllablePolicy;

   private static TypeDefinition policyTypeNotControllablePolicy;

   private static TypeDefinition relationshipType;

   private static TypeDefinition relationshipTypeControllableAcl;

   private static TypeDefinition relationshipTypeNotControllableAcl;

   private static TypeDefinition relationshipTypeControllablePolicy;

   private static TypeDefinition relationshipTypeNotControllablePolicy;

   private String principal = "root";

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();

      folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
      List<ItemsTree<TypeDefinition>> allFolders = connection.getTypeDescendants(folderType.getId(), -1, true);
      if (folderType.isControllableACL())
      {
         folderTypeControllableAcl = folderType;
      }
      else
      {
         folderTypeNotControllableAcl = folderType;
      }
      if (folderTypeControllableAcl == null)
      {
         folderTypeControllableAcl = getControllableAclType(allFolders);
      }
      if (folderTypeNotControllableAcl == null)
      {
         folderTypeNotControllableAcl = getNotControllableAclType(allFolders);
      }
      if (folderType.isControllablePolicy())
      {
         folderTypeControllablePolicy = folderType;
      }
      else
      {
         folderTypeNotControllablePolicy = folderType;
      }
      if (folderTypeControllablePolicy == null)
      {
         folderTypeControllablePolicy = getControllablePolicyType(allFolders);
      }
      if (folderTypeNotControllablePolicy == null)
      {
         folderTypeNotControllablePolicy = getNotControllablePolicyType(allFolders);
      }

      documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      List<ItemsTree<TypeDefinition>> allDocs = connection.getTypeDescendants(documentType.getId(), -1, true);
      if (documentType.isControllableACL())
      {
         documentTypeControllableAcl = documentType;
      }
      else
      {
         documentTypeNotControllableAcl = documentType;
      }
      if (documentTypeControllableAcl == null)
      {
         documentTypeControllableAcl = getControllableAclType(allDocs);
      }
      if (documentTypeNotControllableAcl == null)
      {
         documentTypeNotControllableAcl = getNotControllableAclType(allDocs);
      }
      if (documentType.isControllablePolicy())
      {
         documentTypeControllablePolicy = documentType;
      }
      else
      {
         documentTypeNotControllablePolicy = documentType;
      }
      if (documentTypeControllablePolicy == null)
      {
         documentTypeControllablePolicy = getControllablePolicyType(allDocs);
      }
      if (documentTypeNotControllablePolicy == null)
      {
         documentTypeNotControllablePolicy = getNotControllablePolicyType(allDocs);
      }
      if (documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED)
      {
         documentTypeStreamNotSupported = documentType;
      }
      if (documentTypeStreamNotSupported == null)
      {
         documentTypeStreamNotSupported = getStreamNotSupportedDocType(allDocs);
      }
      if (documentType.getContentStreamAllowed() == ContentStreamAllowed.REQUIRED)
      {
         documentTypeStreamRequired = documentType;
      }
      if (documentTypeStreamRequired == null)
      {
         documentTypeStreamRequired = getStreamNotSupportedDocType(allDocs);
      }
      if (!documentType.isVersionable())
      {
         documentTypeNotVersionable = documentType;
      }
      else
      {
         documentTypeVersionable = documentType;
      }
      if (documentTypeNotVersionable == null)
      {
         documentTypeNotVersionable = getNotVersionableDocType(allDocs);
      }
      if (documentTypeVersionable == null)
      {
         documentTypeVersionable = getVersionableDocType(allDocs);
      }

      if (isPoliciesSupported)
      {
         policyType = connection.getTypeDefinition(CmisConstants.POLICY);
         List<ItemsTree<TypeDefinition>> allPolicies = connection.getTypeDescendants(policyType.getId(), -1, true);
         if (policyType.isControllableACL())
         {
            policyTypeControllableAcl = policyType;
         }
         else
         {
            policyTypeNotControllableAcl = policyType;
         }
         if (policyTypeControllableAcl == null)
         {
            policyTypeControllableAcl = getControllableAclType(allPolicies);
         }
         if (policyTypeNotControllableAcl == null)
         {
            policyTypeNotControllableAcl = getNotControllableAclType(allPolicies);
         }
         if (policyType.isControllablePolicy())
         {
            policyTypeControllablePolicy = policyType;
         }
         else
         {
            policyTypeNotControllablePolicy = policyType;
         }
         if (policyTypeControllablePolicy == null)
         {
            policyTypeControllablePolicy = getControllablePolicyType(allPolicies);
         }
         if (policyTypeNotControllablePolicy == null)
         {
            policyTypeNotControllablePolicy = getNotControllablePolicyType(allPolicies);
         }
      }

      if (isRelationshipsSupported)
      {
         relationshipType = connection.getTypeDefinition(CmisConstants.RELATIONSHIP);
         List<ItemsTree<TypeDefinition>> allRelationships =
            connection.getTypeDescendants(relationshipType.getId(), -1, true);

         if (relationshipType.isControllableACL())
         {
            relationshipTypeControllableAcl = relationshipType;
         }
         else
         {
            relationshipTypeNotControllableAcl = relationshipType;
         }
         if (relationshipTypeControllableAcl == null)
         {
            relationshipTypeControllableAcl = getControllableAclType(allRelationships);
         }
         if (relationshipTypeNotControllableAcl == null)
         {
            relationshipTypeNotControllableAcl = getNotControllableAclType(allRelationships);
         }
         if (relationshipType.isControllablePolicy())
         {
            relationshipTypeControllablePolicy = relationshipType;
         }
         else
         {
            relationshipTypeNotControllablePolicy = relationshipType;
         }
         if (relationshipTypeControllablePolicy == null)
         {
            relationshipTypeControllablePolicy = getControllablePolicyType(allRelationships);
         }
         if (relationshipTypeNotControllablePolicy == null)
         {
            relationshipTypeNotControllablePolicy = getNotControllablePolicyType(allRelationships);
         }
      }

      testRootFolderId = createFolder(rootFolderID, folderType.getId(), "object_testroot", null, null, null);

      System.out.println("Running Object Service tests");
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testRootFolderId != null)
      {
         clear(testRootFolderId);
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * Create document and set content stream for it. If content stream is not
    * allowed for document then content will be not set.
    * </p>
    */
   @Test
   public void testCreateDocument_Content() throws Exception
   {
      String template = "1234567890aBcDE";
      ContentStream content = null;
      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED)
      {
         byte[] before = template.getBytes();
         content = new BaseContentStream(before, null, new MimeType("text", "plain"));
      }
      Map<String, Property<?>> properties = createPropertyMap(documentType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_CheckContent.txt");
      }

      String docId =
         connection.createDocument(testRootFolderId, properties, content, null, null, null, documentType
            .isVersionable() ? VersioningState.MAJOR : VersioningState.NONE);

      // Check content it it exists.
      if (content != null)
      {
         ContentStream content1 = connection.getContentStream(docId, null);
         assertEquals(content.getMediaType(), content1.getMediaType());
         byte[] buf = new byte[1024];
         int read = content1.getStream().read(buf);
         assertEquals(template, new String(buf, 0, read));
      }
      else
      {
         // Content is not allowed, ConstraintException must be thrown.
         try
         {
            connection.getContentStream(docId, null);
            fail("ConstraintException must be throw, content stream not allowed. ");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * Create document and apply policies to the newly-created document object.
    * If policies is not supported then this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ApplyPolicy() throws Exception
   {
      if (documentTypeControllablePolicy == null)
      {
         return;
      }

      String policyId =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(documentTypeControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ApplyPolicy");
      }
      String docId =
         connection.createDocument(testRootFolderId, properties, documentTypeControllablePolicy
            .getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : BaseTest.TEST_CONTENT_STREAM, null,
            null, Arrays.asList(policyId), documentTypeControllablePolicy.isVersionable() ? VersioningState.MAJOR
               : VersioningState.NONE);

      List<CmisObject> policies = connection.getAppliedPolicies(docId, true, null);
      assertEquals(1, policies.size());
      assertEquals(policyId, policies.get(0).getObjectInfo().getId());
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * Create document and apply ACL for it. If ACL is not supported at all or
    * can't be managed (if method
    * {@link RepositoryCapabilities#getCapabilityACL()} returns something other
    * then CapabilityACL.MANAGE) this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ApplyACL() throws Exception
   {
      if (documentTypeControllableAcl == null || capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         return;
      }

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      Map<String, Property<?>> properties = createPropertyMap(documentTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ApplyACL");
      }
      String docId =
         connection.createDocument(testRootFolderId, properties,
            documentTypeControllableAcl.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
               : BaseTest.TEST_CONTENT_STREAM, acl, null, null, documentTypeControllableAcl.isVersionable()
               ? VersioningState.MAJOR : VersioningState.NONE);
      List<AccessControlEntry> actualACL = connection.getACL(docId, false);
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * If a violation are detected with the given cmis:name property value, the
    * repository MAY throw {@link NameConstraintViolationException} or chose a
    * name which does not conflict. Try create two documents with the same name
    * in one directory and expect to get
    * {@link NameConstraintViolationException} or at least second document must
    * get different name then provide when it created.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_NameConstraintViolationException() throws Exception
   {
      String name = "testCreateDocument_NameConstraintViolationException";
      Map<String, Property<?>> properties = createPropertyMap(documentType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      connection.createDocument(testRootFolderId, properties,
         documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
            : BaseTest.TEST_CONTENT_STREAM, null, null, null, documentType.isVersionable() ? VersioningState.MAJOR
            : VersioningState.NONE);
      try
      {
         String docId2 =
            connection.createDocument(testRootFolderId, properties,
               documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
                  : BaseTest.TEST_CONTENT_STREAM, null, null, null, documentType.isVersionable()
                  ? VersioningState.MAJOR : VersioningState.NONE);
         // If exception was not thrown then check name, it must be different.
         CmisObject doc2 =
            connection.getObject(docId2, false, IncludeRelationships.NONE, false, false, true, null,
               RenditionFilter.NONE);
         String name2 = doc2.getObjectInfo().getName();
         assertFalse("NameConstraintViolationException must be throw or different name chosen. ", name.equals(name2));
      }
      catch (NameConstraintViolationException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link StreamNotSupportedException} must be thrown if the
    * "contentStreamAllowed" attribute of the object type definition specified
    * by the cmis:objectTypeId property value is set to "not allowed" and a
    * content stream input parameter is provided. If there is no any type for
    * which content stream is not allowed this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_StreamNotSupportedException() throws Exception
   {
      if (documentTypeStreamNotSupported == null)
      {
         return;
      }

      Map<String, Property<?>> properties = createPropertyMap(documentTypeStreamNotSupported);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_StreamNotSupportedException");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties, BaseTest.TEST_CONTENT_STREAM, null, null, null,
            documentTypeStreamNotSupported.isVersionable() ? VersioningState.MAJOR : VersioningState.NONE);
         fail("StreamNotSupportedException must be thrown. ");
      }
      catch (StreamNotSupportedException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if cmis:objectTypeId property
    * value is not an object type whose baseType is cmis:document.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_ObjectType() throws Exception
   {
      Map<String, Property<?>> properties = createPropertyMap(documentType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_ObjectType");
      }
      IdProperty property = (IdProperty)properties.get(CmisConstants.OBJECT_TYPE_ID);
      property.getValues().clear();
      // Set cmis:folder instead cmis:document
      property.getValues().add(folderType.getId());

      try
      {
         connection.createDocument(testRootFolderId, properties,
            documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
               : BaseTest.TEST_CONTENT_STREAM, null, null, null, documentType.isVersionable() ? VersioningState.MAJOR
               : VersioningState.NONE);
         fail("ConstraintException must be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link StreamNotSupportedException} must be thrown if the
    * "contentStreamAllowed" attribute of the object type definition specified
    * by the cmis:objectTypeId property value is set to "required" and a content
    * stream input parameter is not provided. If there is no any type for which
    * content stream is "required" this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_ContentRequired() throws Exception
   {
      if (documentTypeStreamRequired == null)
      {
         return;
      }

      Map<String, Property<?>> properties = createPropertyMap(documentTypeStreamRequired);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_ContentRequired");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties, null, null, null, null, documentTypeStreamRequired
            .isVersionable() ? VersioningState.MAJOR : VersioningState.NONE);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if "versionable" attribute of
    * the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>true</code> and the value for the versioningState
    * input parameter is provided that is "none".
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_Versionable() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }

      Map<String, Property<?>> properties = createPropertyMap(documentTypeVersionable);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_Versionable");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties,
            documentTypeVersionable.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
               : BaseTest.TEST_CONTENT_STREAM, null, null, null, VersioningState.NONE);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if "versionable" attribute of
    * the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>false</code> and the value for the versioningState
    * input parameter is other then "none".
    * </p>
    *
    * @throws Exception
    */
   @Test
   @Ignore
   // Ignore it at the moment. Not clear from specification what must be exception
   // or ignoring versionState attribute for not versionable document.
   public void testCreateDocument_ConstraintException_NonVersionable() throws Exception
   {
      if (documentTypeNotVersionable == null)
      {
         return;
      }

      Map<String, Property<?>> properties = createPropertyMap(documentTypeNotVersionable);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_NonVersionable");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties,
            documentTypeNotVersionable.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
               : BaseTest.TEST_CONTENT_STREAM, null, null, null, VersioningState.MAJOR);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if "controllablePolicy"
    * attribute of the object type definition specified by the cmis:objectTypeId
    * property value is set to <code>false</code> and at least one policy is
    * provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_NotControllablePolicy() throws Exception
   {
      if (!isPoliciesSupported || documentTypeNotControllablePolicy == null)
      {
         return;
      }

      String policy =
         createPolicy(testRootFolderId, policyType.getId(), generateName(policyType, null), BaseTest.TEST_POLICY_TEXT,
            null, null, null);
      Map<String, Property<?>> properties = createPropertyMap(documentTypeNotControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_NotControllablePolicy");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties, documentTypeNotControllablePolicy
            .getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : BaseTest.TEST_CONTENT_STREAM, null,
            null, Arrays.asList(policy), documentTypeNotControllablePolicy.isVersionable() ? VersioningState.MAJOR
               : VersioningState.NONE);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if "controllableACL" attribute
    * of the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>false</code> and at least one ACE is provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_NotControllableACL() throws Exception
   {
      if (!capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE) || documentTypeNotControllableAcl == null)
      {
         return;
      }
      Map<String, Property<?>> properties = createPropertyMap(documentTypeNotControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateDocument_ConstraintException_NotControllableACL");
      }
      try
      {
         connection.createDocument(testRootFolderId, properties, documentTypeNotControllableAcl
            .getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : BaseTest.TEST_CONTENT_STREAM,
            createACL(principal, "cmis:all"), null, null, documentTypeNotControllableAcl.isVersionable()
               ? VersioningState.MAJOR : VersioningState.NONE);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.1 createDocument.
    * <p>
    * {@link ConstraintException} must be thrown if at least one of the
    * permissions is used in an ACE provided which is not supported by the
    * repository.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException_ACENotSupported() throws Exception
   {
      if (!capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE) || documentTypeControllableAcl == null)
      {
         return;
      }

      if (documentTypeControllableAcl != null)
      {
         Map<String, Property<?>> properties = createPropertyMap(documentTypeControllableAcl);
         StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
         if (nameProperty != null)
         {
            nameProperty.getValues().add("testCreateDocument_ConstraintException_ACENotSupported");
         }
         try
         {
            List<AccessControlEntry> acl = createACL(principal, "cmis:unknown");
            connection.createDocument(testRootFolderId, properties, documentTypeControllableAcl
               .getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null : BaseTest.TEST_CONTENT_STREAM,
               acl, null, null, documentTypeControllableAcl.isVersionable() ? VersioningState.MAJOR
                  : VersioningState.NONE);
            fail("ConstraintException must be thrown. ");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.4.2 createDocumentFromSource.
    * <p>
    * Creates a document object as a copy of the given source document in the
    * specified location.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_Content() throws Exception
   {
      String name = generateName(documentType, null);
      String document =
         createDocument(testRootFolderId, documentType.getId(), name,
            documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED ? null
               : BaseTest.TEST_CONTENT_STREAM, null, null, null, null);
      Map<String, Property<?>> properties = createPropertyMap(documentType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name + "_copy");
      }
      String documentCopy =
         connection.createDocumentFromSource(document, testRootFolderId, properties, null, null, null, documentType
            .isVersionable() ? VersioningState.MAJOR : VersioningState.NONE);
      try
      {
         connection.getObject(documentCopy, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Can't get newly created object. ");
      }

      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED)
      {
         ContentStream content = connection.getContentStream(documentCopy, null);
         byte[] buf = new byte[1024];
         int read = content.getStream().read(buf);
         byte[] res = new byte[read];
         System.arraycopy(buf, 0, res, 0, read);
         assertArrayEquals(TEST_CONTENT, res);
      }
      else
      {
         try
         {
            connection.getContentStream(documentCopy, null);
            fail("ConstraintException must be throw, content stream not allowed. ");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * Create a folder object of the specified type in the specified location
    * without any additional attributes, such as policies, ACL, etc.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder() throws Exception
   {
      Map<String, Property<?>> properties = createPropertyMap(folderType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateFolder");
      }
      String folderId = connection.createFolder(testRootFolderId, properties, null, null, null);
      // Get by id.
      try
      {
         connection.getObject(folderId, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Unable get newly create folder by id. ");
      }
      // Get by path.
      String path = "/object_testroot/testCreateFolder";
      try
      {
         connection.getObjectByPath(path, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Unable get newly create folder by path. ");
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * Create folder and apply policies to the newly-created folder object. If
    * policies is not supported then this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ApplyPolicy() throws Exception
   {
      if (folderTypeControllablePolicy == null || !isPoliciesSupported)
      {
         return;
      }

      String policyId =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(folderTypeControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateFolder_ApplyPolicy");
      }

      String folderId = connection.createFolder(testRootFolderId, properties, null, null, Arrays.asList(policyId));

      List<CmisObject> policies = connection.getAppliedPolicies(folderId, true, null);
      assertEquals(1, policies.size());
      assertEquals(policyId, policies.get(0).getObjectInfo().getId());
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * Create folder and apply ACL for it. If ACL is not supported at all or
    * can't be managed (if method
    * {@link RepositoryCapabilities#getCapabilityACL()} returns something other
    * then CapabilityACL.MANAGE) this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ApplyACL() throws Exception
   {
      if (folderTypeControllableAcl == null || capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         return;
      }

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      Map<String, Property<?>> properties = createPropertyMap(folderTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateFolder_ApplyACL");
      }
      String folderId = connection.createFolder(testRootFolderId, properties, acl, null, null);
      List<AccessControlEntry> actualACL = connection.getACL(folderId, false);
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * If a violation are detected with the given cmis:name property value, the
    * repository MAY throw {@link NameConstraintViolationException} or chose a
    * name which does not conflict. Try create two folders with the same name in
    * one directory and expect to get {@link NameConstraintViolationException}
    * or at least second folder must get different name then provide when it
    * created.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_NameConstraintViolationException() throws Exception
   {
      String name = "testCreateFolder_NameConstraintViolationException";
      Map<String, Property<?>> properties = createPropertyMap(folderType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      connection.createFolder(testRootFolderId, properties, null, null, null);
      try
      {
         String folderId2 = connection.createFolder(testRootFolderId, //
            properties, null, null, null);
         // If exception was not thrown then check name, it must be different.
         CmisObject folder2 =
            connection.getObject(folderId2, false, IncludeRelationships.NONE, false, false, true, null,
               RenditionFilter.NONE);
         String name2 = folder2.getObjectInfo().getName();
         assertFalse("NameConstraintViolationException must be throw or different name chosen. ", name.equals(name2));
      }
      catch (NameConstraintViolationException e)
      {
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * {@link ConstraintException} must be thrown if cmis:objectTypeId property
    * value is not an object type whose baseType is cmis:folder.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException_ObjectType() throws Exception
   {
      Map<String, Property<?>> properties = createPropertyMap(folderType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateFolder_ConstraintException_ObjectType");
      }
      IdProperty property = (IdProperty)properties.get(CmisConstants.OBJECT_TYPE_ID);
      property.getValues().clear();
      // Set cmis:document instead cmis:folder
      property.getValues().add(documentType.getId());

      try
      {
         connection.createFolder(testRootFolderId, properties, null, null, null);
         fail("ConstraintException must be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * {@link ConstraintException} must be thrown if "controllablePolicy"
    * attribute of the object type definition specified by the cmis:objectTypeId
    * property value is set to <code>false</code> and at least one policy is
    * provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException_NotControllablePolicy() throws Exception
   {
      if (!isPoliciesSupported || folderTypeNotControllablePolicy == null)
      {
         return;
      }

      if (folderTypeNotControllablePolicy != null)
      {
         String policy =
            createPolicy(testRootFolderId, policyType.getId(), generateName(policyType, null),
               BaseTest.TEST_POLICY_TEXT, null, null, null);
         Map<String, Property<?>> properties = createPropertyMap(folderTypeNotControllablePolicy);
         StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
         if (nameProperty != null)
         {
            nameProperty.getValues().add("testCreateFolder_ConstraintException_NotControllablePolicy");
         }
         try
         {
            connection.createFolder(testRootFolderId, properties, null, null, Arrays.asList(policy));
            fail("ConstraintException must be thrown. ");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * {@link ConstraintException} must be thrown if at least one of the
    * permissions is used in an ACE provided which is not supported by the
    * repository.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException_ACENotSupported() throws Exception
   {
      if (!capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE) || folderTypeControllableAcl == null)
      {
         return;
      }
      Map<String, Property<?>> properties = createPropertyMap(folderTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateFolder_ConstraintException_ACENotSupported");
      }
      try
      {
         List<AccessControlEntry> acl = createACL(principal, "cmis:unknown");
         connection.createFolder(testRootFolderId, properties, acl, null, null);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.3 createFolder.
    * <p>
    * {@link ConstraintException} must be thrown if "controllableACL" attribute
    * of the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>false</code> and at least one ACE is provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException_NotControllableACL() throws Exception
   {
      if (!capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE) || folderTypeNotControllableAcl == null)
      {
         return;
      }
      if (folderTypeNotControllableAcl != null)
      {
         Map<String, Property<?>> properties = createPropertyMap(folderTypeNotControllableAcl);
         StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
         if (nameProperty != null)
         {
            nameProperty.getValues().add("testCreateFolder_ConstraintException_NotControllableACL");
         }
         try
         {
            connection.createFolder(testRootFolderId, properties, createACL(principal, "cmis:all"), null, null);
            fail("ConstraintException must be thrown. ");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * Create a relationship object of the specified type without any additional
    * attributes, such as policies, ACL, etc.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);

      Map<String, Property<?>> properties = createPropertyMap(relationshipType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      String relationshipId = connection.createRelationship(properties, null, null, null);
      try
      {
         connection.getObject(relationshipId, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Unable get newly create relationship. ");
      }
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * Create a relationship object of the specified and apply policies to it.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ApplyPolicy() throws Exception
   {
      if (!isRelationshipsSupported || !isPoliciesSupported || relationshipTypeControllablePolicy == null)
      {
         return;
      }

      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(relationshipTypeControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ApplyPolicy");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      String relationshipId = connection.createRelationship(properties, null, null, Arrays.asList(policy));

      List<CmisObject> policies = connection.getAppliedPolicies(relationshipId, true, null);
      assertEquals(1, policies.size());
      assertEquals(policy, policies.get(0).getObjectInfo().getId());
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * Create relationship and apply ACL for it. If ACL is not supported at all
    * or can't be managed (if method
    * {@link RepositoryCapabilities#getCapabilityACL()} returns something other
    * then CapabilityACL.MANAGE) this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ApplyACL() throws Exception
   {
      if (!isRelationshipsSupported || relationshipTypeControllableAcl == null
         || capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         return;
      }

      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      Map<String, Property<?>> properties = createPropertyMap(relationshipTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ApplyACL");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      String relationshipId = connection.createRelationship(properties, acl, null, null);
      List<AccessControlEntry> actualACL = connection.getACL(relationshipId, false);
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * If a violation are detected with the given cmis:name property value, the
    * repository MAY throw {@link NameConstraintViolationException} or chose a
    * name which does not conflict. Try create two relationship with the same
    * name and expect to get {@link NameConstraintViolationException} or at
    * least second relationship must get different name then provide when it
    * created.
    * </p>
    *
    * @throws Exception
    */
   @Test
   @Ignore
   // Ignore since in some case storage may not throw exception or choose different name.
   // Relationship is not hierarchical not need (but may) to be care about names.
   // Enable this if need.
   public void testCreateRelationship_NameConstraintViolationException() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }

      String name = "testCreateRelationship_NameConstraintViolationException";
      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);

      Map<String, Property<?>> properties = createPropertyMap(relationshipType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }

      connection.createRelationship(properties, null, null, null);
      try
      {
         String relationshipId2 = connection.createRelationship(properties, null, null, null);

         // If exception was not thrown then check name, it must be different.
         CmisObject relationship2 =
            connection.getObject(relationshipId2, false, IncludeRelationships.NONE, false, false, true, null,
               RenditionFilter.NONE);
         String name2 = relationship2.getObjectInfo().getName();
         assertFalse("NameConstraintViolationException must be throw or different name chosen. ", name.equals(name2));
      }
      catch (NameConstraintViolationException e)
      {
      }
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * {@link ConstraintException} must be thrown if cmis:objectTypeId property
    * value is not an object type whose baseType is cmis:relationship.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException_ObjectType() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }

      String name = "testCreateRelationship_ConstraintException_ObjectType";
      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);

      Map<String, Property<?>> properties = createPropertyMap(relationshipType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      IdProperty property = (IdProperty)properties.get(CmisConstants.OBJECT_TYPE_ID);
      property.getValues().clear();
      // Set cmis:document instead cmis:relationship
      property.getValues().add(documentType.getId());

      try
      {
         connection.createRelationship(properties, null, null, null);
         fail("ConstraintException must be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   /*
    * TODO
    * Tests for throwing {@link ConstraintException} if the
    * sourceObjectId's ObjectType is not in the list of "allowedSourceTypes"
    * specified by the object type definition specified by cmis:objectTypeId
    * property value. Or if targetObjectId's ObjectType is not in the list of
    * "allowedTargetTypes" specified by the object type definition specified by
    * cmis:objectTypeId property value.
    */

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * {@link ConstraintException} must be thrown if "controllablePolicy"
    * attribute of the object type definition specified by the cmis:objectTypeId
    * property value is set to <code>false</code> and at least one policy is
    * provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException_NotControllablePolicy() throws Exception
   {
      if (!isRelationshipsSupported || !isPoliciesSupported || relationshipTypeNotControllablePolicy == null)
      {
         return;
      }

      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(relationshipTypeNotControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ConstraintException_NotControllablePolicy");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      try
      {
         connection.createRelationship(properties, null, null, Arrays.asList(policy));
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * {@link ConstraintException} must be thrown if "controllableACL" attribute
    * of the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>false</code> and at least one ACEs is provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException_NotControllableACL() throws Exception
   {
      if (!isRelationshipsSupported || capabilities.getCapabilityACL() != CapabilityACL.MANAGE
         || relationshipTypeNotControllableAcl == null)
      {
         return;
      }

      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      Map<String, Property<?>> properties = createPropertyMap(relationshipTypeNotControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ConstraintException_NotControllableACL");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      try
      {
         connection.createRelationship(properties, acl, null, null);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.4 createRelationship.
    * <p>
    * {@link ConstraintException} must be thrown if at least one of the
    * permissions is used in an ACE provided which is not supported by the
    * repository.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException_ACENotSupported() throws Exception
   {
      if (!isRelationshipsSupported || !capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE)
         || relationshipTypeControllableAcl == null)
      {
         return;
      }
      String source =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String target =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      List<AccessControlEntry> acl = createACL(principal, "cmis:unknown");

      Map<String, Property<?>> properties = createPropertyMap(relationshipTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ConstraintException_ACENotSupported");
      }
      IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
      if (sourceIdProperty != null)
      {
         sourceIdProperty.getValues().add(source);
      }
      IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
      if (targetIdProperty != null)
      {
         targetIdProperty.getValues().add(target);
      }
      try
      {
         connection.createRelationship(properties, acl, null, null);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * Create a relationship object of the specified type without any additional
    * attributes, such as policies, ACL, etc.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy() throws Exception
   {
      if (!isPoliciesSupported)
      {
         return;
      }

      String test = "test create policy";
      Map<String, Property<?>> properties = createPropertyMap(policyType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(test);
      }
      String policyId =
         connection.createPolicy(policyType.isFileable() ? testRootFolderId : null, properties, null, null, null);
      CmisObject policy = null;
      try
      {
         policy =
            connection.getObject(policyId, false, IncludeRelationships.NONE, false, false, true,
               CmisConstants.POLICY_TEXT, RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Unable get newly create policy. ");
      }
      assertNotNull(policy);
      assertEquals("testCreatePolicy", policy.getObjectInfo().getName());
      StringProperty textProperty = (StringProperty)policy.getProperties().get(CmisConstants.POLICY_TEXT);
      assertTrue("Unable get policy text. ", textProperty != null && textProperty.getValues().size() > 0);
      assertEquals(test, textProperty.getValues().get(0));
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * Create a policy object of the specified and apply policies to it.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ApplyPolicy() throws Exception
   {
      if (!isPoliciesSupported || policyTypeControllablePolicy == null)
      {
         return;
      }

      String policy1 =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(policyTypeControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy_ApplyPolicy");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      String policyId =
         connection.createPolicy(policyTypeControllablePolicy.isFileable() ? testRootFolderId : null, properties, null,
            null, Arrays.asList(policy1));

      List<CmisObject> policies = connection.getAppliedPolicies(policyId, true, null);
      assertEquals(1, policies.size());
      assertEquals(policy1, policies.get(0).getObjectInfo().getId());
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * Create policy and apply ACL for it. If ACL is not supported at all or
    * can't be managed (if method
    * {@link RepositoryCapabilities#getCapabilityACL()} returns something other
    * then CapabilityACL.MANAGE) this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ApplyACL() throws Exception
   {
      if (!isPoliciesSupported || policyTypeControllableAcl == null
         || capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         return;
      }

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      Map<String, Property<?>> properties = createPropertyMap(policyTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy_ApplyACL");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      String policyId =
         connection.createPolicy(policyTypeControllableAcl.isFileable() ? testRootFolderId : null, properties, acl,
            null, null);
      List<AccessControlEntry> actualACL = connection.getACL(policyId, false);
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * If a violation are detected with the given cmis:name property value, the
    * repository MAY throw {@link NameConstraintViolationException} or chose a
    * name which does not conflict. Try create two policies with the same name
    * in one directory and expect to get
    * {@link NameConstraintViolationException} or at least second policy must
    * get different name then provide when it created. <i>If policy type is not
    * fileable this test will be skipped since name may not be important for not
    * hierarchical object.</i>
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_NameConstraintViolationException() throws Exception
   {
      if (!isPoliciesSupported || !policyType.isFileable())
      {
         return;
      }

      String name = "testCreatePolicy_NameConstraintViolationException";
      Map<String, Property<?>> properties = createPropertyMap(policyType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }

      connection.createPolicy(policyType.isFileable() ? testRootFolderId : null, properties, null, null, null);
      try
      {
         String policyId2 =
            connection.createPolicy(policyType.isFileable() ? testRootFolderId : null, properties, null, null, null);;
         // If exception was not thrown then check name, it must be different.
         CmisObject policy2 =
            connection.getObject(policyId2, false, IncludeRelationships.NONE, false, false, true, null,
               RenditionFilter.NONE);
         String name2 = policy2.getObjectInfo().getName();
         assertFalse("NameConstraintViolationException must be throw or different name chosen. ", name.equals(name2));
      }
      catch (NameConstraintViolationException e)
      {
      }
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * {@link ConstraintException} must be thrown if cmis:objectTypeId property
    * value is not an object type whose baseType is cmis:policy.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException_ObjectType() throws Exception
   {
      if (!isPoliciesSupported)
      {
         return;
      }

      Map<String, Property<?>> properties = createPropertyMap(policyType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy_ConstraintException_ObjectType");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      IdProperty property = (IdProperty)properties.get(CmisConstants.OBJECT_TYPE_ID);
      property.getValues().clear();
      // Set cmis:document instead cmis:policy
      property.getValues().add(documentType.getId());

      try
      {
         connection.createPolicy(policyType.isFileable() ? testRootFolderId : null, properties, null, null, null);
         fail("ConstraintException must be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * {@link ConstraintException} must be thrown if "controllablePolicy"
    * attribute of the object type definition specified by the cmis:objectTypeId
    * property value is set to <code>false</code> and at least one policy is
    * provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException_NotControllablePolicy() throws Exception
   {
      if (!isPoliciesSupported || policyTypeNotControllablePolicy == null)
      {
         return;
      }

      String policy1 =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      Map<String, Property<?>> properties = createPropertyMap(policyTypeNotControllablePolicy);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy_ConstraintException_NotControllablePolicy");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      try
      {
         connection.createPolicy(policyTypeNotControllablePolicy.isFileable() ? testRootFolderId : null, properties,
            null, null, Arrays.asList(policy1));
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * {@link ConstraintException} must be thrown if "controllableACL" attribute
    * of the object type definition specified by the cmis:objectTypeId property
    * value is set to <code>false</code> and at least one ACEs is provided.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException_NotControllableACL() throws Exception
   {
      if (!isPoliciesSupported || policyTypeNotControllableAcl == null)
      {
         return;
      }

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");
      Map<String, Property<?>> properties = createPropertyMap(policyTypeNotControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreatePolicy_ConstraintException_NotControllablePolicy");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      try
      {
         connection.createPolicy(policyTypeNotControllableAcl.isFileable() ? testRootFolderId : null, properties, acl,
            null, null);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.5 createPolicy.
    * <p>
    * {@link ConstraintException} must be thrown if at least one of the
    * permissions is used in an ACE provided which is not supported by the
    * repository.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException_ACENotSupported() throws Exception
   {
      if (!isPoliciesSupported || !capabilities.getCapabilityACL().equals(CapabilityACL.MANAGE)
         || policyTypeControllableAcl == null)
      {
         return;
      }
      List<AccessControlEntry> acl = createACL(principal, "cmis:unknown");

      Map<String, Property<?>> properties = createPropertyMap(policyTypeControllableAcl);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testCreateRelationship_ConstraintException_ACENotSupported");
      }
      StringProperty policyText = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
      if (policyText != null)
      {
         policyText.getValues().add(TEST_POLICY_TEXT);
      }
      try
      {
         connection.createPolicy(policyTypeControllableAcl.isFileable() ? testRootFolderId : null, properties, acl,
            null, null);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   // TODO

   /**
    *
    * 2.2.4.6 getAllowableActions.
    * <p>
    * Gets the list of allowable actions for an object.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetAllowableActions_Simlpe() throws Exception
   {
      AllowableActions actions = connection.getAllowableActions(testRootFolderId);
      assertNotNull("Unable get allowable actions. ", actions);
   }

   /**
    * 2.2.4.7 getObject.
    * <p>
    * Get the specified information for the object without any additional info
    * about relationships, policies, renditions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject() throws Exception
   {
      CmisObject o =
         connection.getObject(testRootFolderId, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertEquals(testRootFolderId, o.getObjectInfo().getId());
      assertNull(o.getAllowableActions());
      assertEquals(0, o.getPolicyIds().size());
      assertEquals(0, o.getRelationship().size());
      assertEquals(0, o.getRenditions().size());
   }

   /**
    * 2.2.4.7 getObject.
    * <p>
    * Get object with id <code>testRootFolderId</code> and use filter for
    * properties. Result SHOULD contains only the properties specified in the
    * property filter if they exist on the object's type definition.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject_PropertyFiltered() throws Exception
   {
      CmisObject o =
         connection.getObject(testRootFolderId, false, IncludeRelationships.NONE, false, false, true,
            "cmis:name,cmis:path", RenditionFilter.NONE);
      BaseType baseType = o.getObjectInfo().getBaseType();
      Set<String> queryNames = new HashSet<String>();
      for (Map.Entry<String, Property<?>> e : o.getProperties().entrySet())
      {
         queryNames.add(e.getValue().getQueryName());
      }
      assertEquals(baseType, BaseType.FOLDER);
      // cmis:path and cmis:name must be in result.
      assertEquals(2, queryNames.size());
      for (String q : queryNames)
      {
         assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
            || q.equalsIgnoreCase("cmis:path"));
      }
   }

   /**
    * 2.2.4.7 getObject.
    *<p>
    * Get object with id <code>testRootFolderId</code> with additional
    * information about relationships.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeRelationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      String relationship =
         createRelationship(relationshipType.getId(), generateName(relationshipType, null), testRootFolderId,
            testRootFolderId, null, null, null);
      CmisObject o =
         connection.getObject(testRootFolderId, false, IncludeRelationships.BOTH, false, false, true, null,
            RenditionFilter.NONE);
      assertTrue(o.getRelationship().size() >= 1);
      Set<String> r = new HashSet<String>();
      for (CmisObject rel : o.getRelationship())
      {
         r.add(rel.getObjectInfo().getId());
      }
      assertTrue("Expected relationship " + relationship + " not found in result. ", r.contains(relationship));
   }

   /**
    * 2.2.4.7 getObject.
    *<p>
    * Get object with id <code>testRootFolderId</code> with additional
    * information about policies.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludePolicy() throws Exception
   {
      if (!isPoliciesSupported || !folderType.isControllablePolicy())
      {
         return;
      }
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(
            relationshipType, null), TEST_POLICY_TEXT, null, null, null);
      connection.applyPolicy(policy, testRootFolderId);
      CmisObject o =
         connection.getObject(testRootFolderId, false, IncludeRelationships.NONE, true, false, true, null,
            RenditionFilter.NONE);
      assertTrue(o.getPolicyIds().size() >= 1);
      assertTrue("Expected policy " + policy + " not found in result. ", o.getPolicyIds().contains(policy));
   }

   /**
    * 2.2.4.7 getObject.
    *<p>
    * Get object with id with additional information about ACL.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeACL() throws Exception
   {
      if (capabilities.getCapabilityACL() != CapabilityACL.MANAGE || documentTypeControllableAcl == null)
      {
         return;
      }

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");
      String documentId =
         createDocument(testRootFolderId, documentTypeControllableAcl.getId(), generateName(
            documentTypeControllableAcl, null), null, acl, null, null, null);
      CmisObject o =
         connection.getObject(documentId, false, IncludeRelationships.NONE, false, true, true, null,
            RenditionFilter.NONE);
      List<AccessControlEntry> actualACL = o.getACL();
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.4.7 getObject.
    *<p>
    * Get object with id with additional information about allowable actions.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeAllowableActions() throws Exception
   {
      String documentId =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      CmisObject o =
         connection.getObject(documentId, true, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertNotNull(o.getAllowableActions());
   }

   /**
    * 2.2.4.9 getObjectByPath.
    * <p>
    * Get the specified information for the object without any additional info
    * about relationships, policies, renditions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath() throws Exception
   {
      CmisObject o =
         connection.getObjectByPath("/object_testroot", false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertEquals(testRootFolderId, o.getObjectInfo().getId());
      assertNull(o.getAllowableActions());
      assertEquals(0, o.getPolicyIds().size());
      assertEquals(0, o.getRelationship().size());
      assertEquals(0, o.getRenditions().size());
   }

   /**
    * 2.2.4.7 getObjectByPath.
    * <p>
    * Get object by path and use filter for properties. Result SHOULD contains
    * only the properties specified in the property filter if they exist on the
    * object's type definition.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_PropertyFiltered() throws Exception
   {
      CmisObject o =
         connection.getObjectByPath("/object_testroot", false, IncludeRelationships.NONE, false, false, true,
            "cmis:name,cmis:path", RenditionFilter.NONE);
      BaseType baseType = o.getObjectInfo().getBaseType();
      Set<String> queryNames = new HashSet<String>();
      for (Map.Entry<String, Property<?>> e : o.getProperties().entrySet())
      {
         queryNames.add(e.getValue().getQueryName());
      }
      assertEquals(baseType, BaseType.FOLDER);
      // cmis:path and cmis:name must be in result.
      assertEquals(2, queryNames.size());
      for (String q : queryNames)
      {
         assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
            || q.equalsIgnoreCase("cmis:path"));
      }
   }

   /**
    * 2.2.4.7 getObjectByPath.
    *<p>
    * Get object by path with additional information about relationships.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludeRelationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      String relationship =
         createRelationship(relationshipType.getId(), generateName(relationshipType, null), testRootFolderId,
            testRootFolderId, null, null, null);
      CmisObject o =
         connection.getObjectByPath("/object_testroot", false, IncludeRelationships.BOTH, false, false, true, null,
            RenditionFilter.NONE);
      assertTrue(o.getRelationship().size() >= 1);
      Set<String> r = new HashSet<String>();
      for (CmisObject rel : o.getRelationship())
      {
         r.add(rel.getObjectInfo().getId());
      }
      assertTrue("Expected relationship " + relationship + " not found in result. ", r.contains(relationship));
   }

   /**
    * 2.2.4.7 getObjectByPath.
    *<p>
    * Get object by path with additional information about policies.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludePolicy() throws Exception
   {
      if (!isPoliciesSupported || !folderType.isControllablePolicy())
      {
         return;
      }
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(
            relationshipType, null), TEST_POLICY_TEXT, null, null, null);
      connection.applyPolicy(policy, testRootFolderId);
      CmisObject o =
         connection.getObjectByPath("/object_testroot", false, IncludeRelationships.NONE, true, false, true, null,
            RenditionFilter.NONE);
      assertTrue(o.getPolicyIds().size() >= 1);
      assertTrue("Expected policy " + policy + " not found in result. ", o.getPolicyIds().contains(policy));
   }

   /**
    * 2.2.4.7 getObjectByPath.
    *<p>
    * Get object by path with additional information about allowable actions.
    *</p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludeAllowableActions() throws Exception
   {
      CmisObject o =
         connection.getObjectByPath("/object_testroot", true, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertNotNull(o.getAllowableActions());
   }

   /**
    * 2.2.4.10 getContentStream.
    *
    * @throws Exception
    */
   @Test
   public void testGetContentStream() throws Exception
   {
      String template = "TEST_GET_CONTENT";
      ContentStream content = null;
      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED)
      {
         byte[] before = template.getBytes();
         content = new BaseContentStream(before, null, new MimeType("text", "plain"));
      }
      Map<String, Property<?>> properties = createPropertyMap(documentType);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add("testGetContentStream.txt");
      }
      String document =
         connection.createDocument(testRootFolderId, properties, content, null, null, null, documentType
            .isVersionable() ? VersioningState.MAJOR : VersioningState.NONE);
      if (content != null)
      {
         if (content != null)
         {
            ContentStream content1 = connection.getContentStream(document, null);
            assertEquals(content.getMediaType(), content1.getMediaType());
            byte[] buf = new byte[1024];
            int read = content1.getStream().read(buf);
            assertEquals(template, new String(buf, 0, read));
         }
         else
         {
            // Content is not allowed, ConstraintException must be thrown.
            try
            {
               connection.getContentStream(document, null);
               fail("ConstraintException must be throw, content stream not allowed. ");
            }
            catch (ConstraintException e)
            {
            }
         }
      }
   }

   /**
    * 2.2.4.17 deleteContentStream.
    *
    * @throws Exception
    */
   @Test
   public void testDeleteContentStream() throws Exception
   {
      if (documentType.getContentStreamAllowed() != ContentStreamAllowed.ALLOWED)
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), TEST_CONTENT_STREAM,
            null, null, null, null);
      Map<String, Property<?>> documentProperties =
         connection.getProperties(document, true, CmisConstants.CHANGE_TOKEN).getProperties();

      StringProperty changeTokenProperty = (StringProperty)documentProperties.get(CmisConstants.CHANGE_TOKEN);
      ChangeTokenHolder holder = new ChangeTokenHolder();
      if (changeTokenProperty != null && changeTokenProperty.getValues().size() > 0)
      {
         holder.setValue(changeTokenProperty.getValues().get(0));
      }
      connection.deleteContentStream(document, holder);
      try
      {
         connection.getContentStream(document, null);
         fail("ConstraintException must be thrown since document has not content stream. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.13 moveObject.
    * <p>
    * Move object from one folder to other.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testMove() throws Exception
   {
      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String folder =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      document = connection.moveObject(document, folder, testRootFolderId);
      List<ObjectParent> parents =
         connection.getObjectParents(document, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
      assertEquals(folder, parents.get(0).getObject().getObjectInfo().getId());
   }

   /**
    * 2.2.4.13 moveObject.
    * <p>
    * {@link InvalidArgumentException} must be thrown if sourceFolderId is
    * missing or the sourceFolderId doesn't match the specified object's.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testMove_InvalidArgumentException_Missing() throws Exception
   {
      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String folder =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      try
      {
         connection.moveObject(document, folder, null);
         fail("InvalidArgumentException must be thrown. ");
      }
      catch (InvalidArgumentException e)
      {
      }
   }

   /**
    * 2.2.4.13 moveObject.
    * <p>
    * {@link InvalidArgumentException} must be thrown if sourceFolderId is
    * missing or the sourceFolderId doesn't match the specified object's.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testMove_InvalidArgumentException_NotMatch() throws Exception
   {
      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String folder =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      try
      {
         connection.moveObject(document, folder, folder);
         fail("InvalidArgumentException must be thrown. ");
      }
      catch (InvalidArgumentException e)
      {
      }
   }

   /**
    * 2.2.4.14 deleteObject.
    *
    * @throws Exception
    */
   @Test
   public void testDeleteObject() throws Exception
   {
      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      connection.deleteObject(document, true);
      try
      {
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
         fail("Object must be removed. ");
      }
      catch (ObjectNotFoundException ex)
      {
      }
   }

   /**
    * 2.2.4.14 deleteObject.
    * <p>
    * {@link ConstraintException} must be thrown if the method is invoked on a
    * folder object that contains one or more objects.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testDeleteObject_ConstraintException() throws Exception
   {
      String folder =
         createFolder(testRootFolderId, folderType.getId(), generateName(documentType, null), null, null, null);
      createDocument(folder, documentType.getId(), generateName(documentType, null), null, null, null, null, null);
      try
      {
         connection.deleteObject(folder, true);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.4.15 deleteTree.
    *
    * @throws Exception
    */
   @Test
   public void testDeleteTree() throws Exception
   {
      String folder =
         createFolder(testRootFolderId, folderType.getId(), generateName(documentType, null), null, null, null);
      String document =
         createDocument(folder, documentType.getId(), generateName(documentType, null), null, null, null, null, null);
      connection.deleteTree(folder, true, UnfileObject.DELETE, false);
      try
      {
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
         fail("Document must be removed. ");
      }
      catch (ObjectNotFoundException e)
      {
      }
      try
      {
         connection.getObject(folder, false, IncludeRelationships.NONE, false, false, true, null, RenditionFilter.NONE);
         fail("Folder must be removed. ");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   /**
    * 2.2.4.16 setContentStream.
    *
    * @throws Exception
    */
   @Test
   public void testSetContentStream() throws Exception
   {
      if (documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED)
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, "txt"), null, null, null,
            null, null);
      byte[] newContent = "UPDATED_CONTENT".getBytes();
      document =
         connection.setContentStream(document, new BaseContentStream(newContent, "", new MimeType("text", "plain")),
            new ChangeTokenHolder(), true);
      ContentStream content = connection.getContentStream(document, null);
      assertEquals(content.getMediaType(), content.getMediaType());
      byte[] buf = new byte[1024];
      int read = content.getStream().read(buf);
      byte[] res = new byte[read];
      System.arraycopy(buf, 0, res, 0, read);
      assertArrayEquals(newContent, res);
   }

   /**
    * 2.2.4.16 setContentStream.
    * <p>
    *{@link ContentAlreadyExistsException} must be thrown if object has content
    * and overwriteFlag is <code>false</code>.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testSetContentStream_ContentAlreadyExistsException() throws Exception
   {
      if (documentType.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED)
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, "txt"), TEST_CONTENT_STREAM,
            null, null, null, null);
      byte[] newContent = "UPDATED_CONTENT".getBytes();
      try
      {
         connection.setContentStream(document, new BaseContentStream(newContent, "", new MimeType("text", "plain")),
            new ChangeTokenHolder(), false);
         fail("ContentAlreadyExistsException must be thrown. ");
      }
      catch (ContentAlreadyExistsException e)
      {
      }
   }

   /**
    * 2.2.4.16 setContentStream.
    * <p>
    * {@link StreamNotSupportedException} must be thrown if object
    * "contentStreamAllowed" attribute of the object type definition specified
    * by the cmis:objectTypeId property value of the given document is set to
    * NOT_ALLOWED.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testSetContentStream_ContentAlreadyExistsException_() throws Exception
   {
      if (documentTypeStreamNotSupported == null)
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentTypeStreamNotSupported.getId(), generateName(
            documentTypeStreamNotSupported, "txt"), null, null, null, null, null);
      byte[] newContent = "_CONTENT_".getBytes();
      try
      {
         connection.setContentStream(document, new BaseContentStream(newContent, "", new MimeType("text", "plain")),
            new ChangeTokenHolder(), false);
         fail("StreamNotSupportedException must be thrown. ");
      }
      catch (StreamNotSupportedException e)
      {
      }
   }

}
