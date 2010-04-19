package org.xcmis.wssoap.test.client;

import org.xcmis.soap.client.CmisContentStreamType;
import org.xcmis.soap.client.CmisException;
import org.xcmis.soap.client.CmisPropertiesType;
import org.xcmis.soap.client.CmisProperty;
import org.xcmis.soap.client.CmisPropertyId;
import org.xcmis.soap.client.CmisPropertyString;
import org.xcmis.soap.client.EnumBaseObjectTypeIds;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.RenditionFilter;

import java.util.List;

import javax.activation.DataHandler;

public class ObjectServicePortTest extends BaseTest
{

   public void testCreateDocument() throws Exception
   {
      System.out.println("Invoking createDocument...");
      String resultObjectId = null;

      org.xcmis.soap.client.CmisPropertiesType _createDocument_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreateDocument_document1_" + System.nanoTime());
      // fill the _createDocument_properties
      _createDocument_properties.getProperty().add(propTypeId);
      _createDocument_properties.getProperty().add(propName);

      java.lang.String _createDocument_folderId = rootFolderId;
      org.xcmis.soap.client.CmisContentStreamType _createDocument_contentStream = new CmisContentStreamType();
      _createDocument_contentStream.setStream(new DataHandler(new String("Hello xCMIS!!!"), "text/plain"));
      _createDocument_contentStream.setMimeType("text/plain");

      org.xcmis.soap.client.EnumVersioningState _createDocument_versioningState = null;
      java.util.List<java.lang.String> _createDocument_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocument_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocument_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createDocument_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createDocument_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createDocument_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createDocument_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createDocument(cmisRepositoryId, _createDocument_properties, _createDocument_folderId,
            _createDocument_contentStream, _createDocument_versioningState, _createDocument_policies,
            _createDocument_addACEs, _createDocument_removeACEs, _createDocument_extension, _createDocument_objectId);

         System.out.println("createDocument._createDocument_extension=" + _createDocument_extension.value);
         System.out.println("createDocument._createDocument_objectId=" + _createDocument_objectId.value);
         resultObjectId = _createDocument_objectId.value;
         assertTrue(hasObject(resultObjectId));
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(resultObjectId);
      }
   }

   public void testGetObject() throws Exception
   {
      System.out.println("Invoking getObject...");
      java.lang.String _getObject_objectId = createDocument("testGetObject_document_" + System.nanoTime(), rootFolderId);
      java.lang.String _getObject_filter = "*";
      java.lang.Boolean _getObject_includeAllowableActions = Boolean.TRUE;
      org.xcmis.soap.client.EnumIncludeRelationships _getObject_includeRelationships = null;
      java.lang.String _getObject_renditionFilter = RenditionFilter.NONE_FILTER;
      java.lang.Boolean _getObject_includePolicyIds = Boolean.FALSE;
      java.lang.Boolean _getObject_includeACL = Boolean.FALSE;
      org.xcmis.soap.client.CmisExtensionType _getObject_extension = null;
      try
      {
         org.xcmis.soap.client.CmisObjectType _getObject__return =
            object_port.getObject(cmisRepositoryId, _getObject_objectId, _getObject_filter,
               _getObject_includeAllowableActions, _getObject_includeRelationships, _getObject_renditionFilter,
               _getObject_includePolicyIds, _getObject_includeACL, _getObject_extension);
         System.out.println("getObject.result=" + _getObject__return);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_getObject_objectId);
      }
   }

   public void testCreateFolder() throws Exception
   {
      System.out.println("Invoking createFolder...");
      String resultFolderId = null;

      org.xcmis.soap.client.CmisPropertiesType _createFolder_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreateFolder_folder1_" + System.nanoTime());
      // fill the _createDocument_properties
      _createFolder_properties.getProperty().add(propTypeId);
      _createFolder_properties.getProperty().add(propName);

      java.lang.String _createFolder_folderId = rootFolderId;
      java.util.List<java.lang.String> _createFolder_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createFolder_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createFolder_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createFolder_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createFolder_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createFolder_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createFolder_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createFolder(cmisRepositoryId, _createFolder_properties, _createFolder_folderId,
            _createFolder_policies, _createFolder_addACEs, _createFolder_removeACEs, _createFolder_extension,
            _createFolder_objectId);

         System.out.println("createFolder._createFolder_extension=" + _createFolder_extension.value);
         System.out.println("createFolder._createFolder_objectId=" + _createFolder_objectId.value);
         resultFolderId = _createFolder_objectId.value;
         assertTrue(hasObject(resultFolderId));
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(resultFolderId);
      }
   }

   public void testCreatePolicy() throws Exception
   {
      System.out.println("Invoking createPolicy...");
      String policyId = null;

      org.xcmis.soap.client.CmisPropertiesType _createPolicy_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreatePolicy_policy1_" + System.nanoTime());
      // POLICY_TEXT
      CmisPropertyString propPolicyText = new CmisPropertyString();
      propPolicyText.setPropertyDefinitionId(CmisConstants.POLICY_TEXT);
      propPolicyText.getValue().add("policy23");
      // fill the _createDocument_properties
      _createPolicy_properties.getProperty().add(propTypeId);
      _createPolicy_properties.getProperty().add(propName);
      _createPolicy_properties.getProperty().add(propPolicyText);

      java.lang.String _createPolicy_folderId = rootFolderId;
      java.util.List<java.lang.String> _createPolicy_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createPolicy_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createPolicy_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createPolicy_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createPolicy_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createPolicy_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createPolicy_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createPolicy(cmisRepositoryId, _createPolicy_properties, _createPolicy_folderId,
            _createPolicy_policies, _createPolicy_addACEs, _createPolicy_removeACEs, _createPolicy_extension,
            _createPolicy_objectId);

         System.out.println("createPolicy._createPolicy_extension=" + _createPolicy_extension.value);
         System.out.println("createPolicy._createPolicy_objectId=" + _createPolicy_objectId.value);
         policyId = _createPolicy_objectId.value;
         assertTrue(hasObject(policyId));
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(policyId);
      }
   }

   public void testCreateRelationship() throws Exception
   {
      System.out.println("Invoking createRelationship...");
      String relationshipId = null;

      org.xcmis.soap.client.CmisPropertiesType _createRelationship_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreateRelationship_relation1_" + System.nanoTime());
      // SOURCE_ID
      CmisPropertyId propSourceId = new CmisPropertyId();
      propSourceId.setPropertyDefinitionId(CmisConstants.SOURCE_ID);
      String sourceObjectId = createDocument("testCreateRelationship_source_" + System.nanoTime(), rootFolderId);
      propSourceId.getValue().add(sourceObjectId);
      // TARGET_ID
      CmisPropertyId propTargetId = new CmisPropertyId();
      propTargetId.setPropertyDefinitionId(CmisConstants.TARGET_ID);
      String targetObjectId = createDocument("testCreateRelationship_target_" + System.nanoTime(), rootFolderId);
      propTargetId.getValue().add(targetObjectId);
      // fill the _createDocument_properties
      _createRelationship_properties.getProperty().add(propTypeId);
      _createRelationship_properties.getProperty().add(propName);
      _createRelationship_properties.getProperty().add(propSourceId);
      _createRelationship_properties.getProperty().add(propTargetId);

      java.util.List<java.lang.String> _createRelationship_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createRelationship_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createRelationship_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createRelationship_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createRelationship_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createRelationship_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createRelationship_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createRelationship(cmisRepositoryId, _createRelationship_properties, _createRelationship_policies,
            _createRelationship_addACEs, _createRelationship_removeACEs, _createRelationship_extension,
            _createRelationship_objectId);

         System.out.println("createRelationship._createRelationship_extension=" + _createRelationship_extension.value);
         System.out.println("createRelationship._createRelationship_objectId=" + _createRelationship_objectId.value);
         relationshipId = _createRelationship_objectId.value;
         assertTrue(hasObject(relationshipId));
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(relationshipId);
         deleteObject(sourceObjectId);
         deleteObject(targetObjectId);
      }
   }

   public void testGetProperties() throws Exception
   {
      System.out.println("Invoking getProperties...");
      java.lang.String _getProperties_objectId = createDocument("testGetProperties_document_" + System.nanoTime(), rootFolderId);
      java.lang.String _getProperties_filter = "*";
      org.xcmis.soap.client.CmisExtensionType _getProperties_extension = null;
      try
      {
         org.xcmis.soap.client.CmisPropertiesType _getProperties__return =
            object_port.getProperties(cmisRepositoryId, _getProperties_objectId, _getProperties_filter,
               _getProperties_extension);
         System.out.println("getProperties.result=" + _getProperties__return);
         List<CmisProperty> list = _getProperties__return.getProperty();
         System.out.println("_getProperties__return.getProperty() list = " + list);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_getProperties_objectId);
      }
   }

   public void testDeleteObject() throws Exception
   {
      System.out.println("Invoking deleteObject...");
      java.lang.String _deleteObject_objectId = createDocument("testDeleteObject_" + System.nanoTime(), rootFolderId);
      java.lang.Boolean _deleteObject_allVersions = null;
      org.xcmis.soap.client.CmisExtensionType _deleteObject_extension = null;
      try
      {
         org.xcmis.soap.client.CmisExtensionType _deleteObject__return =
            object_port.deleteObject(cmisRepositoryId, _deleteObject_objectId, _deleteObject_allVersions,
               _deleteObject_extension);
         System.out.println("deleteObject.result=" + _deleteObject__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      assertFalse(hasObject(_deleteObject_objectId));
   }

   public void testGetContentStream() throws Exception
   {
      System.out.println("Invoking getContentStream...");
      java.lang.String _getContentStream_objectId = createDocument("testGetContentStream_" + System.nanoTime(), rootFolderId);
      java.lang.String _getContentStream_streamId = null;
      java.math.BigInteger _getContentStream_offset = new java.math.BigInteger("0");
      java.math.BigInteger _getContentStream_length = new java.math.BigInteger("10");
      org.xcmis.soap.client.CmisExtensionType _getContentStream_extension = null;
      try
      {
         org.xcmis.soap.client.CmisContentStreamType _getContentStream__return =
            object_port.getContentStream(cmisRepositoryId, _getContentStream_objectId, _getContentStream_streamId,
               _getContentStream_offset, _getContentStream_length, _getContentStream_extension);
         System.out.println("getContentStream.result=" + _getContentStream__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_getContentStream_objectId);
      }
   }

   public void testGetAllowableActions() throws Exception
   {
      System.out.println("Invoking getAllowableActions...");
      java.lang.String _getAllowableActions_objectId = createDocument("testGetAllowableActions_" + System.nanoTime(), rootFolderId);
      org.xcmis.soap.client.CmisExtensionType _getAllowableActions_extension = null;
      try
      {
         org.xcmis.soap.client.CmisAllowableActionsType _getAllowableActions__return =
            object_port.getAllowableActions(cmisRepositoryId, _getAllowableActions_objectId,
               _getAllowableActions_extension);
         System.out.println("getAllowableActions.result=" + _getAllowableActions__return);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_getAllowableActions_objectId);
      }
   }

   public void testDeleteContentStream() throws Exception
   {
      System.out.println("Invoking deleteContentStream...");
      java.lang.String _deleteContentStream_objectIdVal =
         createDocument("testDeleteContentStream_" + System.nanoTime(), rootFolderId);
      javax.xml.ws.Holder<java.lang.String> _deleteContentStream_objectId =
         new javax.xml.ws.Holder<java.lang.String>(_deleteContentStream_objectIdVal);
      java.lang.String _deleteContentStream_changeTokenVal = "";
      javax.xml.ws.Holder<java.lang.String> _deleteContentStream_changeToken =
         new javax.xml.ws.Holder<java.lang.String>(_deleteContentStream_changeTokenVal);
      org.xcmis.soap.client.CmisExtensionType _deleteContentStream_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _deleteContentStream_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_deleteContentStream_extensionVal);
      try
      {
         object_port.deleteContentStream(cmisRepositoryId, _deleteContentStream_objectId,
            _deleteContentStream_changeToken, _deleteContentStream_extension);

         System.out.println("deleteContentStream._deleteContentStream_objectId=" + _deleteContentStream_objectId.value);
         System.out.println("deleteContentStream._deleteContentStream_changeToken="
            + _deleteContentStream_changeToken.value);
         System.out.println("deleteContentStream._deleteContentStream_extension="
            + _deleteContentStream_extension.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_deleteContentStream_objectIdVal);
      }
   }

   public void testCreateDocumentFromSource() throws Exception
   {
      System.out.println("Invoking createDocumentFromSource...");
      java.lang.String _createDocumentFromSource_sourceId =
         createDocument("testCreateDocumentFromSource_document_" + System.nanoTime(), rootFolderId);
      org.xcmis.soap.client.CmisPropertiesType _createDocumentFromSource_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreateDocument_document2_" + System.nanoTime());
      // fill the _createDocument_properties
      _createDocumentFromSource_properties.getProperty().add(propTypeId);
      _createDocumentFromSource_properties.getProperty().add(propName);

      java.lang.String _createDocumentFromSource_folderId = rootFolderId;
      org.xcmis.soap.client.EnumVersioningState _createDocumentFromSource_versioningState = null;
      java.util.List<java.lang.String> _createDocumentFromSource_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocumentFromSource_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocumentFromSource_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createDocumentFromSource_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createDocumentFromSource_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createDocumentFromSource_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createDocumentFromSource_objectId =
         new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createDocumentFromSource(cmisRepositoryId, _createDocumentFromSource_sourceId,
            _createDocumentFromSource_properties, _createDocumentFromSource_folderId,
            _createDocumentFromSource_versioningState, _createDocumentFromSource_policies,
            _createDocumentFromSource_addACEs, _createDocumentFromSource_removeACEs,
            _createDocumentFromSource_extension, _createDocumentFromSource_objectId);

         System.out.println("createDocumentFromSource._createDocumentFromSource_extension="
            + _createDocumentFromSource_extension.value);
         System.out.println("createDocumentFromSource._createDocumentFromSource_objectId="
            + _createDocumentFromSource_objectId.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_createDocumentFromSource_sourceId);
      }
   }

   public void testUpdateProperties() throws Exception
   {
      System.out.println("Invoking updateProperties...");
      java.lang.String _updateProperties_objectIdVal =
         createDocument("testUpdateProperties_document_" + System.nanoTime(), rootFolderId);
      javax.xml.ws.Holder<java.lang.String> _updateProperties_objectId =
         new javax.xml.ws.Holder<java.lang.String>(_updateProperties_objectIdVal);
      java.lang.String _updateProperties_changeTokenVal = "";
      javax.xml.ws.Holder<java.lang.String> _updateProperties_changeToken =
         new javax.xml.ws.Holder<java.lang.String>(_updateProperties_changeTokenVal);
      org.xcmis.soap.client.CmisPropertiesType _updateProperties_properties = new CmisPropertiesType();
      org.xcmis.soap.client.CmisExtensionType _updateProperties_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _updateProperties_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_updateProperties_extensionVal);
      try
      {
         object_port.updateProperties(cmisRepositoryId, _updateProperties_objectId, _updateProperties_changeToken,
            _updateProperties_properties, _updateProperties_extension);

         System.out.println("updateProperties._updateProperties_objectId=" + _updateProperties_objectId.value);
         System.out.println("updateProperties._updateProperties_changeToken=" + _updateProperties_changeToken.value);
         System.out.println("updateProperties._updateProperties_extension=" + _updateProperties_extension.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_updateProperties_objectIdVal);
      }
   }

   public void testMoveObject() throws Exception
   {
      System.out.println("Invoking moveObject...");
      java.lang.String _moveObject_objectIdVal = createDocument("testMoveObject_document_" + System.nanoTime(), rootFolderId);
      javax.xml.ws.Holder<java.lang.String> _moveObject_objectId =
         new javax.xml.ws.Holder<java.lang.String>(_moveObject_objectIdVal);
      java.lang.String _moveObject_targetFolderId = createFolder("moveObject_targetFolderId" + System.nanoTime());
      java.lang.String _moveObject_sourceFolderId = createFolder("moveObject_sourceFolderId" + System.nanoTime());
      org.xcmis.soap.client.CmisExtensionType _moveObject_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _moveObject_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_moveObject_extensionVal);
      try
      {
         object_port.moveObject(cmisRepositoryId, _moveObject_objectId, _moveObject_targetFolderId,
            _moveObject_sourceFolderId, _moveObject_extension);

         System.out.println("moveObject._moveObject_objectId=" + _moveObject_objectId.value);
         System.out.println("moveObject._moveObject_extension=" + _moveObject_extension.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
      finally
      {
         deleteObject(_moveObject_objectIdVal);
      }
   }

   public void testDeleteTree() throws Exception
   {
      System.out.println("Invoking deleteTree...");
      java.lang.String _deleteTree_folderId = "";
      java.lang.Boolean _deleteTree_allVersions = null;
      org.xcmis.soap.client.EnumUnfileObject _deleteTree_unfileObjects = null;
      java.lang.Boolean _deleteTree_continueOnFailure = null;
      org.xcmis.soap.client.CmisExtensionType _deleteTree_extension = null;
      try
      {
         org.xcmis.soap.client.DeleteTreeResponse.FailedToDelete _deleteTree__return =
            object_port.deleteTree(cmisRepositoryId, _deleteTree_folderId, _deleteTree_allVersions,
               _deleteTree_unfileObjects, _deleteTree_continueOnFailure, _deleteTree_extension);
         System.out.println("deleteTree.result=" + _deleteTree__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testGetRenditions() throws Exception
   {
      System.out.println("Invoking getRenditions...");
      java.lang.String _getRenditions_objectId = "";
      java.lang.String _getRenditions_renditionFilter = "";
      java.math.BigInteger _getRenditions_maxItems = new java.math.BigInteger("0");
      java.math.BigInteger _getRenditions_skipCount = new java.math.BigInteger("0");
      org.xcmis.soap.client.CmisExtensionType _getRenditions_extension = null;
      try
      {
         java.util.List<org.xcmis.soap.client.CmisRenditionType> _getRenditions__return =
            object_port.getRenditions(cmisRepositoryId, _getRenditions_objectId, _getRenditions_renditionFilter,
               _getRenditions_maxItems, _getRenditions_skipCount, _getRenditions_extension);
         System.out.println("getRenditions.result=" + _getRenditions__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testGetObjectByPath() throws Exception
   {
      System.out.println("Invoking getObjectByPath...");
      java.lang.String _getObjectByPath_path = "";
      java.lang.String _getObjectByPath_filter = "";
      java.lang.Boolean _getObjectByPath_includeAllowableActions = null;
      org.xcmis.soap.client.EnumIncludeRelationships _getObjectByPath_includeRelationships = null;
      java.lang.String _getObjectByPath_renditionFilter = "";
      java.lang.Boolean _getObjectByPath_includePolicyIds = null;
      java.lang.Boolean _getObjectByPath_includeACL = null;
      org.xcmis.soap.client.CmisExtensionType _getObjectByPath_extension = null;
      try
      {
         org.xcmis.soap.client.CmisObjectType _getObjectByPath__return =
            object_port.getObjectByPath(cmisRepositoryId, _getObjectByPath_path, _getObjectByPath_filter,
               _getObjectByPath_includeAllowableActions, _getObjectByPath_includeRelationships,
               _getObjectByPath_renditionFilter, _getObjectByPath_includePolicyIds, _getObjectByPath_includeACL,
               _getObjectByPath_extension);
         System.out.println("getObjectByPath.result=" + _getObjectByPath__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testSetContentStream() throws Exception
   {
      System.out.println("Invoking setContentStream...");
      java.lang.String _setContentStream_objectIdVal = "";
      javax.xml.ws.Holder<java.lang.String> _setContentStream_objectId =
         new javax.xml.ws.Holder<java.lang.String>(_setContentStream_objectIdVal);
      java.lang.Boolean _setContentStream_overwriteFlag = null;
      java.lang.String _setContentStream_changeTokenVal = "";
      javax.xml.ws.Holder<java.lang.String> _setContentStream_changeToken =
         new javax.xml.ws.Holder<java.lang.String>(_setContentStream_changeTokenVal);
      org.xcmis.soap.client.CmisContentStreamType _setContentStream_contentStream = null;
      org.xcmis.soap.client.CmisExtensionType _setContentStream_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _setContentStream_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_setContentStream_extensionVal);
      try
      {
         object_port.setContentStream(cmisRepositoryId, _setContentStream_objectId, _setContentStream_overwriteFlag,
            _setContentStream_changeToken, _setContentStream_contentStream, _setContentStream_extension);

         System.out.println("setContentStream._setContentStream_objectId=" + _setContentStream_objectId.value);
         System.out.println("setContentStream._setContentStream_changeToken=" + _setContentStream_changeToken.value);
         System.out.println("setContentStream._setContentStream_extension=" + _setContentStream_extension.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

}
