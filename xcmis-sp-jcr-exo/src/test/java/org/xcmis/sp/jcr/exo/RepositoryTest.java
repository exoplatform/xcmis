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

package org.xcmis.sp.jcr.exo;

import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.messaging.CmisTypeContainer;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeValue;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.RepositoryImpl;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.utils.CmisUtils;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RepositoryTest extends BaseTest
{

   private String typeId;

   public void setUp() throws Exception
   {
      super.setUp();
      typeId = "cmis:" + System.currentTimeMillis();
   }

   public void tearDown() throws Exception
   {
      try
      {
         ((ExtendedNodeTypeManager)session.getWorkspace().getNodeTypeManager()).unregisterNodeType(typeId);
      }
      catch (NoSuchNodeTypeException e)
      {

      }
   }

   public void testAddType() throws Exception
   {
      CmisTypeDocumentDefinitionType type = new CmisTypeDocumentDefinitionType();
      type.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      type.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      type.setControllableACL(true);
      type.setControllablePolicy(true);
      type.setCreatable(true);
      type.setDescription("");
      type.setDisplayName(typeId);
      type.setFileable(true);
      type.setFulltextIndexed(true);
      type.setId(typeId);
      type.setIncludedInSupertypeQuery(true);
      type.setLocalName(typeId);
      type.setLocalNamespace("");
      type.setParentId("cmis:document");
      type.setQueryable(true);
      type.setQueryName(typeId);
      type.setVersionable(true);

      long suff = System.currentTimeMillis();
      String strPropId = "cmis:str" + suff;
      CmisPropertyStringDefinitionType str = new CmisPropertyStringDefinitionType();
      str.setCardinality(EnumCardinality.MULTI);
      str.setId(strPropId);
      str.setPropertyType(EnumPropertyType.STRING);
      str.setUpdatability(EnumUpdatability.READONLY);
      str.setRequired(false);
      CmisPropertyString defStr = new CmisPropertyString();
      defStr.setPropertyDefinitionId(strPropId);
      defStr.getValue().add("to be or not to be");
      str.setDefaultValue(defStr);

      String decPropId = "cmis:dec" + suff;
      CmisPropertyDecimalDefinitionType dec = new CmisPropertyDecimalDefinitionType();
      dec.setCardinality(EnumCardinality.SINGLE);
      dec.setId(decPropId);
      dec.setPropertyType(EnumPropertyType.DECIMAL);
      dec.setRequired(false);
      CmisPropertyDecimal defDec = new CmisPropertyDecimal();
      defDec.setPropertyDefinitionId(strPropId);
      defDec.getValue().add(BigDecimal.valueOf(0.00000123D));
      dec.setDefaultValue(defDec);

      String datePropId = "cmis:date" + suff;
      CmisPropertyDateTimeDefinitionType date = new CmisPropertyDateTimeDefinitionType();
      date.setCardinality(EnumCardinality.MULTI);
      date.setId(datePropId);
      date.setPropertyType(EnumPropertyType.DATETIME);
      date.setRequired(true);
      CmisPropertyDateTime defDate = new CmisPropertyDateTime();
      defDate.setPropertyDefinitionId(strPropId);
      Calendar cal = Calendar.getInstance();
      defDate.getValue().add(CmisUtils.fromCalendar(cal));
      date.setDefaultValue(defDate);

      String intPropId = "cmis:int" + suff;
      CmisPropertyIntegerDefinitionType intg = new CmisPropertyIntegerDefinitionType();
      intg.setCardinality(EnumCardinality.SINGLE);
      intg.setId(intPropId);
      intg.setPropertyType(EnumPropertyType.INTEGER);
      intg.setRequired(false);

      type.getPropertyDefinition().add(str);
      type.getPropertyDefinition().add(dec);
      type.getPropertyDefinition().add(date);
      type.getPropertyDefinition().add(intg);

      cmisRepository.addType(type);

      NodeType nt = null;
      for (NodeTypeIterator iter = session.getWorkspace().getNodeTypeManager().getAllNodeTypes(); iter.hasNext();)
      {
         nt = iter.nextNodeType();
         if (typeId.equals(nt.getName()))
            break;
         nt = null;
      }
      assertNotNull(nt);

      int numPropDef = 0;
      for (PropertyDefinition propDef : nt.getPropertyDefinitions())
      {
         if (strPropId.equals(propDef.getName()))
         {
            numPropDef++;
            assertEquals(PropertyType.STRING, propDef.getRequiredType());
            assertTrue(propDef.isProtected());
            assertTrue(propDef.isAutoCreated());
            assertFalse(propDef.isMandatory());
            assertTrue(propDef.isMultiple());
            assertEquals(1, propDef.getDefaultValues().length);
            assertEquals("to be or not to be", propDef.getDefaultValues()[0].getString());
         }
         else if (decPropId.equals(propDef.getName()))
         {
            numPropDef++;
            assertEquals(PropertyType.DOUBLE, propDef.getRequiredType());
            assertFalse(propDef.isProtected());
            assertTrue(propDef.isAutoCreated());
            assertFalse(propDef.isMandatory());
            assertFalse(propDef.isMultiple());
            assertEquals(1, propDef.getDefaultValues().length);
            assertEquals(0.00000123D, propDef.getDefaultValues()[0].getDouble());
         }
         else if (datePropId.equals(propDef.getName()))
         {
            numPropDef++;
            assertEquals(PropertyType.DATE, propDef.getRequiredType());
            assertFalse(propDef.isProtected());
            assertTrue(propDef.isAutoCreated());
            assertTrue(propDef.isMandatory());
            assertTrue(propDef.isMultiple());
            assertEquals(1, propDef.getDefaultValues().length);
            assertEquals(cal, propDef.getDefaultValues()[0].getDate());
         }
         else if (intPropId.equals(propDef.getName()))
         {
            numPropDef++;
            assertEquals(PropertyType.LONG, propDef.getRequiredType());
            assertFalse(propDef.isProtected());
            assertFalse(propDef.isAutoCreated());
            assertFalse(propDef.isMandatory());
            assertFalse(propDef.isMultiple());
            assertEquals(0, propDef.getDefaultValues().length);
         }
      }

      assertEquals(4, numPropDef);
   }

   public void testRemoveType() throws Exception
   {
      ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager)session.getWorkspace().getNodeTypeManager();
      NodeTypeValue nodeTypeValue = new NodeTypeValue();
      List<String> declaredSupertypeNames = new ArrayList<String>();
      declaredSupertypeNames.add("nt:file");
      declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_DOCUMENT);
      nodeTypeValue.setDeclaredSupertypeNames(declaredSupertypeNames);
      nodeTypeValue.setMixin(false);
      nodeTypeValue.setName(typeId);
      nodeTypeValue.setOrderableChild(false);
      nodeTypeValue.setPrimaryItemName("");
      nodeTypeManager.registerNodeType(nodeTypeValue, ExtendedNodeTypeManager.FAIL_IF_EXISTS);
      
      cmisRepository.removeType(typeId);
      
      try
      {
         nodeTypeManager.getNodeType(typeId);
         fail("Type must be removed.");
      }
      catch (NoSuchNodeTypeException e)
      {
         // OK
      }
   }

   public void testGetObject() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      Entry object = cmisRepository.getObjectById(doc.getObjectId());
      assertNotNull(object);
      CmisTypeDefinitionType objType = object.getType();
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT, objType.getBaseId());
   }

   public void testGetTypeDefinition() throws Exception
   {
      assertNotNull(cmisRepository.getTypeDefinition("cmis:article"));
   }

   public void testGetTypeChildren() throws Exception
   {
      // All base object types
      ItemsIterator<CmisTypeDefinitionType> iter = cmisRepository.getTypeChildren(null, false);
      assertTrue(iter.hasNext());
      List<String> l = new ArrayList<String>();
      while (iter.hasNext())
      {
         l.add(iter.next().getId());
      }
      assertEquals(4, l.size());
      assertTrue(l.contains(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()));
      assertTrue(l.contains(EnumBaseObjectTypeIds.CMIS_FOLDER.value()));
      assertTrue(l.contains(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
      assertTrue(l.contains(EnumBaseObjectTypeIds.CMIS_POLICY.value()));
   }

   public void testGetTypeChildrenWithId() throws Exception
   {
      // Must be only direct children of type "cmis:article" .
      ItemsIterator<CmisTypeDefinitionType> iter = cmisRepository.getTypeChildren("cmis:article", false);
      assertTrue(iter.hasNext());
      List<String> l = new ArrayList<String>();
      while (iter.hasNext())
      {
         l.add(iter.next().getId());
      }
      assertEquals(2, l.size());
      assertTrue(l.contains("cmis:article-sports"));
      assertTrue(l.contains("cmis:article-animals"));
   }

   public void testGetTypeDescendants() throws Exception
   {
      // Must be only descendants of cmis:document type.
      List<CmisTypeContainer> list =
         cmisRepository.getTypeDescendants(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), -1, false);

      assertEquals(1, list.size());
      // Check first level of hierarchy.
      // Expect to found one child type "cmis:article".
      // See src/test/resources/test-cmis-nodetypes-config.xml.
      CmisTypeContainer parent = list.get(0);
      assertEquals("cmis:article", parent.getType().getId());
      List<String> children = new ArrayList<String>();
      getChildrenIds(parent, children);
      assertEquals(2, children.size());

      // Second level, expect to see "cmis:article-sports" and "cmis:article-animals"
      assertTrue(children.contains("cmis:article-sports"));
      assertTrue(children.contains("cmis:article-animals"));

      // Third level, expect to see "cmis:sport-football" as child of "cmis:article-sports"
      // and "cmis:animals-dogs" as child of "cmis:article-animals".
      for (CmisTypeContainer t : parent.getChildren())
      {
         if (t.getType().getId().equals("cmis:article-sports"))
         {
            getChildrenIds(t, children);
            assertEquals(1, children.size());
            assertEquals("cmis:sport-football", children.get(0));
            // Fours level, expected to see "cmis:football-DynamoK"
            assertEquals(1, t.getChildren().size());
            getChildrenIds(t.getChildren().get(0), children);
            assertEquals("cmis:football-DynamoK", children.get(0));
            // No more items in hierarchy.
            getChildrenIds(t.getChildren().get(0).getChildren().get(0), children);
            assertEquals(0, children.size());
         }
         else if (t.getType().getId().equals("cmis:article-animals"))
         {
            getChildrenIds(t, children);
            assertEquals(1, children.size());
            assertEquals("cmis:animals-dogs", children.get(0));
            // No more items in hierarchy.
            getChildrenIds(t.getChildren().get(0), children);
            assertEquals(0, children.size());
         }
      }
   }

   public void testGetAllTypes() throws Exception
   {
      List<CmisTypeContainer> list = cmisRepository.getTypeDescendants(null, -1, false);
      assertEquals(4, list.size());
      List<String> ids = new ArrayList<String>(4);
      for (CmisTypeContainer t : list)
         ids.add(t.getType().getId());
      // Check does contains all root types.
      assertTrue(ids.contains(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()));
      assertTrue(ids.contains(EnumBaseObjectTypeIds.CMIS_FOLDER.value()));
      assertTrue(ids.contains(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
      assertTrue(ids.contains(EnumBaseObjectTypeIds.CMIS_POLICY.value()));

      // Check descendants.
      for (int i = 0; i < list.size(); i++)
      {
         CmisTypeContainer parent = list.get(i);
         if (parent.getType().getId().equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()))
         {
            // Check first level of hierarchy.
            // Expect to found one child type "cmis:article". See src/test/resources/test-cmis-nodetypes-config.xml.
            List<String> children = new ArrayList<String>();
            getChildrenIds(parent, children);
            assertEquals(1, children.size());
            assertEquals("cmis:article", children.get(0));

            // Second level, expect to see "cmis:article-sports" and "cmis:article-animals"
            parent = parent.getChildren().get(0);
            getChildrenIds(parent, children);
            assertEquals(2, children.size());
            assertTrue(children.contains("cmis:article-sports"));
            assertTrue(children.contains("cmis:article-animals"));

            // Third level, expect to see "cmis:sport-football" as child of "cmis:article-sports"
            // and "cmis:animals-dogs" as child of "cmis:article-animals".
            for (CmisTypeContainer t : parent.getChildren())
            {
               if (t.getType().getId().equals("cmis:article-sports"))
               {
                  getChildrenIds(t, children);
                  assertEquals(1, children.size());
                  assertEquals("cmis:sport-football", children.get(0));
                  // Fours level, expected to see "cmis:football-DynamoK"
                  assertEquals(1, t.getChildren().size());
                  getChildrenIds(t.getChildren().get(0), children);
                  assertEquals("cmis:football-DynamoK", children.get(0));
                  // No more items in hierarchy.
                  getChildrenIds(t.getChildren().get(0).getChildren().get(0), children);
                  assertEquals(0, children.size());
               }
               else if (t.getType().getId().equals("cmis:article-animals"))
               {
                  getChildrenIds(t, children);
                  assertEquals(1, children.size());
                  assertEquals("cmis:animals-dogs", children.get(0));
                  // No more items in hierarchy.
                  getChildrenIds(t.getChildren().get(0), children);
                  assertEquals(0, children.size());
               }
            }
         }
         // Other type don't have children according to src/test/resources/test-cmis-nodetypes-config.xml
         else if (parent.getType().getId().equals(EnumBaseObjectTypeIds.CMIS_FOLDER.value()))
         {
            // Does not provide children types for cmis:folder .
            assertEquals(0, parent.getChildren().size());
         }
         else if (parent.getType().getId().equals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()))
         {
            // Does not provide children types for cmis:relationship .
            assertEquals(0, parent.getChildren().size());
         }
         else if (parent.getType().getId().equals(EnumBaseObjectTypeIds.CMIS_POLICY.value()))
         {
            // Does not provide children types for cmis:policy .
            assertEquals(0, parent.getChildren().size());
         }
      }
   }

   public void testNameProducer0() throws Exception
   {
      assertEquals("New Document", ((RepositoryImpl)cmisRepository).getEntryName(JcrCMIS.ROOT_FOLDER_ID,
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
      createDocument(JcrCMIS.ROOT_FOLDER_ID, "New Document", new byte[0], "");
      assertEquals("New Document (2)", ((RepositoryImpl)cmisRepository).getEntryName(JcrCMIS.ROOT_FOLDER_ID,
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
   }

   public void testNameProducer() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "New Folder");
      assertEquals("New Document", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
      createDocument(folder.getObjectId(), "New Document", new byte[0], "");
      assertEquals("New Document (2)", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
      createDocument(folder.getObjectId(), "New Document (2)", new byte[0], "");
      assertEquals("New Document (3)", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
      createDocument(folder.getObjectId(), "New Document (3)", new byte[0], "");
      assertEquals("New Document (4)", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));

      // check folder
      assertEquals("New Folder (2)", ((RepositoryImpl)cmisRepository).getEntryName(testRootFolderId,
         EnumBaseObjectTypeIds.CMIS_FOLDER, JcrCMIS.NT_FOLDER));

      // remove one document
      folder.getNode().getNode("New Document").remove();
      session.save();
      // next name must be the same even one item in folder removed.
      assertEquals("New Document (4)", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
      createDocument(folder.getObjectId(), "New Document (4)", new byte[0], "");
      // must be incremented
      assertEquals("New Document (5)", ((RepositoryImpl)cmisRepository).getEntryName(folder.getObjectId(),
         EnumBaseObjectTypeIds.CMIS_DOCUMENT, JcrCMIS.NT_FILE));
   }

   public void testMoveDocument() throws Exception
   {
      EntryImpl sourceFolder = createFolder(testRootFolderId, "source");
      EntryImpl targetFolder = createFolder(testRootFolderId, "target");
      EntryImpl doc = createDocument(sourceFolder.getObjectId(), "doc", new byte[0], "");
      cmisRepository.moveObject(doc.getObjectId(), targetFolder.getObjectId(), null);
      assertFalse(sourceFolder.getNode().hasNode("doc"));
      assertTrue(targetFolder.getNode().hasNode("doc"));
   }

   public void testMoveDocument2() throws Exception
   {
      // Try move document that is not inversion series
      EntryImpl sourceFolder = createFolder(testRootFolderId, "source");
      EntryImpl targetFolder = createFolder(testRootFolderId, "target");
      Node docNode = sourceFolder.getNode().addNode("doc", JcrCMIS.NT_FILE);
      docNode.addMixin("mix:referenceable");
      Node content = docNode.addNode(JcrCMIS.JCR_CONTENT, "nt:resource");
      content.setProperty(JcrCMIS.JCR_MIMETYPE, "");
      content.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
      content.setProperty(JcrCMIS.JCR_DATA, new ByteArrayInputStream(new byte[0]));
      session.save();
      cmisRepository.moveObject(new EntryImpl(docNode).getObjectId(), targetFolder.getObjectId(), null);
      assertFalse(sourceFolder.getNode().hasNode("doc"));
      assertTrue(targetFolder.getNode().hasNode("doc"));
   }

   public void testMoveFolder() throws Exception
   {
      EntryImpl sourceFolder = createFolder(testRootFolderId, "source");
      EntryImpl targetFolder = createFolder(testRootFolderId, "target");
      cmisRepository.moveObject(sourceFolder.getObjectId(), targetFolder.getObjectId(), null);
      assertFalse(session.getNodeByUUID(testRootFolderId).hasNode("source"));
      assertTrue(targetFolder.getNode().hasNode("source"));
   }

   private void getChildrenIds(CmisTypeContainer source, List<String> ids)
   {
      ids.clear();
      for (CmisTypeContainer ch : source.getChildren())
         ids.add(ch.getType().getId());
   }

}
