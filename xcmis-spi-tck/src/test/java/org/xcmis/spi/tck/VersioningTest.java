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
import org.junit.Test;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.utils.MimeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VersioningTest extends BaseTest
{

   private static String testRootFolderId;

   private static TypeDefinition documentTypeVersionable;

   private static TypeDefinition documentTypeNotVersionable;

   private String principal = "root";

   @BeforeClass
   public static void start() throws Exception
   {
      testRootFolderId = createFolder(rootFolderID, CmisConstants.FOLDER, "versioning_testroot", null, null, null);
      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      List<ItemsTree<TypeDefinition>> allDocs = connection.getTypeDescendants(documentType.getId(), -1, true);
      if (documentType.isVersionable())
      {
         documentTypeVersionable = documentType;
      }
      else
      {
         documentTypeNotVersionable = documentType;
      }
      if (documentTypeNotVersionable == null)
      {
         documentTypeNotVersionable = getNotVersionableDocType(allDocs);
      }
      if (documentTypeVersionable == null)
      {
         documentTypeVersionable = getVersionableDocType(allDocs);
      }
      System.out.println("Running Versioning Service tests");
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
    * 2.2.7.1 checkout.
    * <p>
    * Create a private working copy of the document.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckOut() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      String pwc = connection.checkout(document);
      CmisObject pwcObject = null;
      try
      {
         pwcObject =
            connection.getObject(pwc, false, IncludeRelationships.NONE, false, false, true, null, RenditionFilter.NONE);
      }
      catch (ObjectNotFoundException e)
      {
         fail("PWC not found. ");
      }
      assertNotNull(pwcObject);
      validateVersionSeries(pwcObject.getObjectInfo().getVersionSeriesId(), document, pwc);
      validateCheckedOutState(document, pwc);
      validateCheckedOutState(pwc, pwc);

   }

   /**
    * 2.2.7.1 checkout.
    * <p>
    * {@link ConstraintException} must be throw if document type is not
    * versionable.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckOut_ConstraintException() throws Exception
   {
      if (documentTypeNotVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeNotVersionable.getId(), generateName(documentTypeNotVersionable,
            null), null, null, null, null, null);
      try
      {
         connection.checkout(document);
         fail("ConstraintException must be thrown since document type is not versionable. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.7.2 cancelCheckout.
    * <p>
    * Removes the private working copy of the checked-out document, allowing
    * other documents in the version series to be checked out again. Call
    * cancelCheckout on PWC.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCancelCheckOut_PWC() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);

      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      connection.cancelCheckout(pwc);

      validateVersionSeries(vs, document);
      validateCheckedInState(document);
      try
      {
         connection.getObject(pwc, false, IncludeRelationships.NONE, false, false, true, null, RenditionFilter.NONE);
         fail("PWC must be removed. ");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   /**
    * 2.2.7.2 cancelCheckout.
    * <p>
    * Removes the private working copy of the checked-out document, allowing
    * other documents in the version series to be checked out again. Call
    * cancelCheckout on original document.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCancelCheckOut_Document() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      connection.cancelCheckout(document);

      validateVersionSeries(vs, document);
      validateCheckedInState(document);

      try
      {
         connection.getObject(pwc, false, IncludeRelationships.NONE, false, false, true, null, RenditionFilter.NONE);
         fail("PWC must be removed. ");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   /**
    * 2.2.7.2 cancelCheckout.
    * <p>
    * {@link ConstraintException} must be throw if document type is not
    * versionable.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCancelCheckOut_ConstraintException() throws Exception
   {
      if (documentTypeNotVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeNotVersionable.getId(), generateName(documentTypeNotVersionable,
            null), null, null, null, null, null);
      try
      {
         connection.cancelCheckout(document);
         fail("ConstraintException must be thrown since document type is not versionable. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.7.3 checkin.
    * <p>
    * Checks-in the Private Working Copy of document.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckIn() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, null);

      validateVersionSeries(vs, document, v1);
      validateCheckedInState(document);
   }

   /**
    * 2.2.7.3 checkin.
    * <p>
    * Checks-in the Private Working Copy of document and apply ACL to it.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckIn_ApplyACL() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      // Different behavior dependent to storage capabilities.
      if (capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         try
         {
            connection.checkin(pwc, true, null, null, "testCheckIn", acl, null, null);
            fail("NotSupportedException must be thrown, managin ACL is not supported. ");
         }
         catch (NotSupportedException e)
         {
         }
      }
      else if (!documentTypeVersionable.isControllableACL())
      {
         try
         {
            connection.checkin(pwc, true, null, null, "testCheckIn", acl, null, null);
            fail("ConstraintException must be thrown, type is not controllable by ACL. ");
         }
         catch (ConstraintException e)
         {
         }
      }
      else
      {
         String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", acl, null, null);
         validateVersionSeries(vs, document, v1);
         validateCheckedInState(document);
         List<AccessControlEntry> actualACL = connection.getACL(v1, false);
         validateACL(actualACL);
         checkACL(acl, actualACL);
      }
   }

   /**
    * 2.2.7.3 checkin.
    * <p>
    * Checks-in the Private Working Copy of document and apply ACL to it.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckIn_ApplyPolicy() throws Exception
   {
      if (documentTypeVersionable == null || !isPoliciesSupported)
      {
         return;
      }

      TypeDefinition policyType = connection.getTypeDefinition(CmisConstants.POLICY);
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);

      // Different behavior dependent to storage capabilities.
      if (!documentTypeVersionable.isControllablePolicy())
      {
         try
         {
            connection.checkin(pwc, true, null, null, "testCheckIn", null, null, Arrays.asList(policy));
            fail("ConstraintException must be thrown, type is not controllable by policy. ");
         }
         catch (ConstraintException e)
         {
         }
      }
      else
      {
         String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, Arrays.asList(policy));
         validateVersionSeries(vs, document, v1);
         validateCheckedInState(document);
         List<CmisObject> policies = connection.getAppliedPolicies(v1, true, null);
         assertTrue(policies.size() >= 1);
         Set<String> policiesId = new HashSet<String>(policies.size());
         for (CmisObject o : policies)
         {
            policiesId.add(o.getObjectInfo().getId());
         }
         assertTrue("Expected policy is not found. ", policiesId.contains(policy));
      }
   }

   /**
    * 2.2.7.3 checkin.
    * <p>
    * Update content when checkin. {@link ConstraintException} must be thrown if
    * the "contentStreamAllowed" attribute of the object type definition
    * specified by the cmis:objectTypeId property value is set to "not allowed"
    * and a contentStream input parameter is provided.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testCheckIn_UpdateContent() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);

      if (documentTypeVersionable.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED)
      {
         try
         {
            connection.checkin(pwc, true, null, TEST_CONTENT_STREAM, "testCheckIn", null, null, null);
            fail("ConstraintException must be thrown, content stream is not allowed. ");
         }
         catch (StreamNotSupportedException e)
         {
         }
      }
      else
      {
         byte[] newContent = "__CONTENT__".getBytes();
         String v1 =
            connection.checkin(pwc, true, null, new BaseContentStream(newContent, "", new MimeType("text", "plain")),
               "testCheckIn", null, null, null);

         validateVersionSeries(vs, document, v1);
         validateCheckedInState(document);

         ContentStream content = connection.getContentStream(v1, null);

         assertEquals(content.getMediaType(), content.getMediaType());
         byte[] buf = new byte[1024];
         int read = content.getStream().read(buf);
         byte[] res = new byte[read];
         System.arraycopy(buf, 0, res, 0, read);
         assertArrayEquals(newContent, res);
      }
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * Get latest version of document in the version series without any
    * additional info about relationships, policies, renditions.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, null);
      pwc = connection.checkout(v1);
      String v2 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, null);
      CmisObject latest =
         connection.getObjectOfLatestVersion(vs, false, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertNotNull(latest);
      assertEquals(v2, latest.getObjectInfo().getId());
      assertNull(latest.getAllowableActions());
      assertEquals(0, latest.getPolicyIds().size());
      assertEquals(0, latest.getRelationship().size());
      assertEquals(0, latest.getRenditions().size());
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * Get latest version of document in the version series with additional info
    * about allowable actions.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_AllowableActions() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, null);
      CmisObject latest =
         connection.getObjectOfLatestVersion(vs, false, true, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertNotNull(latest);
      assertEquals(v1, latest.getObjectInfo().getId());
      assertNotNull(latest.getAllowableActions());
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * Get latest version of document in the version series with additional info
    * about policies applied to object.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_IncludePolicies() throws Exception
   {
      if (documentTypeVersionable == null || !isPoliciesSupported || !documentTypeVersionable.isControllablePolicy())
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);

      TypeDefinition policyType = connection.getTypeDefinition(CmisConstants.POLICY);
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);
      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, Arrays.asList(policy));

      CmisObject latest =
         connection.getObjectOfLatestVersion(vs, false, false, IncludeRelationships.NONE, true, false, true, null,
            RenditionFilter.NONE);

      assertNotNull(latest);
      assertEquals(v1, latest.getObjectInfo().getId());
      assertTrue(latest.getPolicyIds().size() >= 1);
      assertTrue("Expected policy " + policy + " not found in result. ", latest.getPolicyIds().contains(policy));
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * Get latest version of document in the version series with additional info
    * about ACL.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_IncludeACL() throws Exception
   {
      if (documentTypeVersionable == null || capabilities.getCapabilityACL() != CapabilityACL.MANAGE
         || !documentTypeVersionable.isControllableACL())
      {
         return;
      }

      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);

      List<AccessControlEntry> acl = createACL(principal, "cmis:all");

      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", acl, null, null);

      CmisObject latest =
         connection.getObjectOfLatestVersion(vs, false, false, IncludeRelationships.NONE, false, true, true, null,
            RenditionFilter.NONE);

      assertNotNull(latest);
      assertEquals(v1, latest.getObjectInfo().getId());
      List<AccessControlEntry> actualACL = latest.getACL();
      validateACL(actualACL);
      checkACL(acl, actualACL);
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * If parameter "major" is true then latest major version must be returned.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_Major() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, null);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      String v1 = connection.checkin(pwc, true, null, null, "testCheckIn", null, null, null);
      pwc = connection.checkout(v1);
      String v2 = connection.checkin(pwc, false, null, null, "testCheckIn", null, null, null);
      CmisObject latest =
         connection.getObjectOfLatestVersion(vs, true, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      assertNotNull(latest);
      assertEquals(v1, latest.getObjectInfo().getId()); // v1 is latest major, v2 is not major.
   }

   /**
    * 2.2.7.4 getObjectOfLatestVersion.
    * <p>
    * {@link ObjectNotFoundException} must be thrown if parameter "major" is
    * true and version series doe not have any major version.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_Major_ObjectNotFoundException() throws Exception
   {
      if (documentTypeVersionable == null)
      {
         return;
      }
      String document =
         createDocument(testRootFolderId, documentTypeVersionable.getId(), generateName(documentTypeVersionable, null),
            null, null, null, null, VersioningState.MINOR);
      CmisObject documentObject =
         connection.getObject(document, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
      String vs = documentObject.getObjectInfo().getVersionSeriesId();

      String pwc = connection.checkout(document);
      String v1 = connection.checkin(pwc, false, null, null, "testCheckIn", null, null, null);
      pwc = connection.checkout(v1);
      String v2 = connection.checkin(pwc, false, null, null, "testCheckIn", null, null, null);
      try
      {
         connection.getObjectOfLatestVersion(vs, true, false, IncludeRelationships.NONE, false, false, true, null,
            RenditionFilter.NONE);
         fail("ObjectNotFoundException must be thrown, there is not major versions in version series.");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   private void validateCheckedOutState(String d, String pwc) throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT);
      b.append(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID);
      CmisObject object = connection.getProperties(d, true, b.toString());
      for (Map.Entry<String, Property<?>> p : object.getProperties().entrySet())
      {
         if (p.getKey().equals(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT))
         {
            Boolean checkedout = (Boolean)p.getValue().getValues().get(0);
            assertTrue(checkedout);
         }
         if (p.getKey().equals(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID))
         {
            String pwc0 = (String)p.getValue().getValues().get(0);
            assertEquals(pwc, pwc0);
         }
      }
   }

   private void validateCheckedInState(String d) throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT);
      b.append(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID);
      CmisObject object = connection.getProperties(d, true, b.toString());
      for (Map.Entry<String, Property<?>> p : object.getProperties().entrySet())
      {
         if (p.getKey().equals(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT))
         {
            Boolean checkedout = (Boolean)p.getValue().getValues().get(0);
            assertFalse(checkedout);
         }
         if (p.getKey().equals(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID))
         {
            assertTrue("cmis:versionSeriesCheckedOutId must not be set. ", p.getValue().getValues().isEmpty());
         }
      }
   }

   private void validateVersionSeries(String vs, String... exp) throws Exception
   {
      List<CmisObject> versions = connection.getAllVersions(vs, false, true, null);
      assertEquals(exp.length, versions.size());
      Set<String> ids = new HashSet<String>(versions.size());
      for (CmisObject v : versions)
      {
         assertEquals(vs, v.getObjectInfo().getVersionSeriesId());
         ids.add(v.getObjectInfo().getId());
      }
      for (String v : exp)
      {
         assertTrue("Expected version " + v + "not found in version series.", ids.contains(v));
      }
   }

}
