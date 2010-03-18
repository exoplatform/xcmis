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
package org.xcmis.sp.jcr.exo.object;

import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ObjectNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public class MultifilingTest extends BaseTest
{

   String mutifiling_path = "/"+ JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS;
   String unfiling_path =  "/" +  JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS;

   
   public void testAddParent() throws Exception
   {
      String testContent = "test content";

      createDocument(testRootFolderId, "doc", testContent.getBytes(), "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(testContent.equalsIgnoreCase(fold1.getNode().getNode("doc").getProperty(JcrCMIS.JCR_CONTENT).getNode()
         .getProperty(JcrCMIS.JCR_DATA).getValue().getString()));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(session.getRootNode().hasNode("testRoot/doc"));
      assertTrue(session.getRootNode().getNode("testRoot/doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"));
   }

   public void testRemoveAllParents() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));

      assertTrue(session.getRootNode().getNode("testRoot/doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      removeAllParents(session.getRootNode().getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"), false);
      assertFalse(fold1.getNode().hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
      assertTrue(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS + "/doc"));
      assertFalse(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"));
   }
   
   public void testRemoveAllParentsWithDelete() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));

      assertTrue(session.getRootNode().getNode("testRoot/doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      removeAllParents(session.getRootNode().getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"), true);
      assertFalse(fold1.getNode().hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
      assertFalse(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS + "/doc"));
      assertFalse(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"));
   }

   public void testRemoveAllParentsAnotherCase() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));

      assertTrue(session.getRootNode().getNode("testRoot/doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      removeAllParents(session.getRootNode().getNode("testRoot/test1/doc"), false);
      assertFalse(fold1.getNode().hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
      assertTrue(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS + "/doc"));
      assertFalse(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"));
   }
   
   
   public void testRemoveParent() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      //Removing from fold2
      removeParent(session.getRootNode().getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(session.getRootNode().getNode("testRoot").hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
   }
   
   public void testRemoveParentAnotherCase() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      //Removing from fold2, but giving also reference node as a param
      removeParent(session.getRootNode().getNode("testRoot/test1/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(session.getRootNode().getNode("testRoot").hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
   }

   
   public void testRemoveLastParent() throws Exception
   {
      createDocument(testRootFolderId, "doc", new byte[0], "");
      EntryImpl fold1 = createFolder(testRootFolderId, "test1");
      EntryImpl fold2 = createFolder(testRootFolderId, "test2");
      addParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      addParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      assertTrue(fold1.getNode().hasNode("doc"));
      assertTrue(fold1.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
      assertTrue(fold2.getNode().hasNode("doc"));
      assertTrue(fold2.getNode().getNode("doc").isNodeType(JcrCMIS.NT_LINKEDFILE));
     
      removeParent(session.getRootNode().getNode("testRoot/doc"), fold1.getNode().getPath());
      removeParent(session.getRootNode().getNode("testRoot/doc"), fold2.getNode().getPath());
      removeParent(session.getRootNode().getNode("testRoot/doc"), "/testRoot");
      assertFalse(fold1.getNode().hasNode("doc"));
      assertFalse(session.getRootNode().getNode("testRoot").hasNode("doc"));
      assertFalse(fold2.getNode().hasNode("doc"));
      assertTrue(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS + "/doc"));
      assertFalse(session.getRootNode().hasNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/doc"));

   }

   private void removeParent(Node entryNode, String parent)
   {
      PropertyIterator it = null;
      try
      {
         if (!entryNode.getPrimaryNodeType().getName().equalsIgnoreCase(JcrCMIS.NT_LINKEDFILE))
         {
            it = entryNode.getNode(JcrCMIS.JCR_CONTENT).getReferences();
            System.out.println("#:" + it.getSize());
            int count = Long.valueOf(it.getSize()).intValue();
            while (it.hasNext())
            {
               Property one = (Property)it.nextProperty();
               Node ref = one.getParent();
               if (ref.getParent().getPath().equals(parent))
               {
                  ref.remove();
                  count--;
               }
            }

            //Moving into unfilings if no more references;
            if (count < 1)
               session.getWorkspace().move(entryNode.getPath(), unfiling_path + "/" + entryNode.getName());
            session.save();
         }
         else
         {
            Node main = session.getNodeByUUID(entryNode.getProperty(JcrCMIS.JCR_CONTENT).getValue().getString());
            it = main.getReferences();
            System.out.println("#:" + it.getSize());
            int count = Long.valueOf(it.getSize()).intValue();
            while (it.hasNext())
            {
               Property one = (Property)it.nextProperty();
               Node ref = one.getParent();
               if (ref.getParent().getPath().equals(parent))
               {
                  ref.remove();
                  count--;
               }
            }
            //Moving into unfilings if no more references;
            if (count < 1)
               session.getWorkspace().move(main.getParent().getPath(), unfiling_path + "/" + entryNode.getName());
            session.save();

         }
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
      }
   }

   private void removeAllParents(Node entryNode, boolean deleteOriginal)
   {
      PropertyIterator it = null;
      try
      {
         if (!entryNode.isNodeType(JcrCMIS.NT_LINKEDFILE))
         {
            it = entryNode.getNode(JcrCMIS.JCR_CONTENT).getReferences();
            System.out.println("#:" + it.getSize());
            while (it.hasNext())
            {
               Property one = (Property)it.nextProperty();
               Node ref = one.getParent();
               if (!ref.isSame(entryNode))
               {
                  ref.remove();
               }
            }
            //Moving to unfileds
            if (!deleteOriginal)
                session.getWorkspace().move(entryNode.getPath(), unfiling_path + "/" + entryNode.getName());
            else
               entryNode.remove();
            session.save();
         }
         else
         {
            Node main = session.getNodeByUUID(entryNode.getProperty(JcrCMIS.JCR_CONTENT).getValue().getString());
            it = main.getReferences();

            while (it.hasNext())
            {
               Property one = (Property)it.nextProperty();
               Node ref = one.getParent();
               if (!ref.isSame(main.getParent()))
               {
                  ref.remove();
               }
            }
            //Moving to unfileds
            if (!deleteOriginal)
                session.getWorkspace().move(main.getParent().getPath(), unfiling_path + "/" + main.getParent().getName());
            else
               main.getParent().remove();
            session.save();
         }
      }
      catch (PathNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
      }
   }

   private void addParent(Node entryNode, String parent)
   {
      try
      {
         if (!entryNode.isNodeType(JcrCMIS.NT_LINKEDFILE))
         {
            Node old_parent = entryNode.getParent();

            session.getWorkspace().move(entryNode.getPath(),  mutifiling_path + "/" + entryNode.getName());
            //Creating new nt:linkedFile node;   
            Node copy = old_parent.addNode(entryNode.getName(), JcrCMIS.NT_LINKEDFILE);
            copy.setProperty(JcrCMIS.JCR_CONTENT, session.getRootNode().getNode(
               JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS + "/" + entryNode.getName()).getNode(JcrCMIS.JCR_CONTENT).getUUID());
            //Adding 
            session.getWorkspace().copy(copy.getPath(), parent + "/" + copy.getName());
            session.save();
         }
         else
         {
            session.getWorkspace().copy(entryNode.getPath(), parent + "/" + entryNode.getName());
            session.save();
         }
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
      }
      catch (ObjectNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (ConstraintException e)
      {
         e.printStackTrace();
      }
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
      try
      {
         session.refresh(false);
         Node rootNode = session.getRootNode();
         for (NodeIterator multifilings =
            rootNode.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_MULTIFILINGS).getNodes(); multifilings.hasNext();)
         {
            multifilings.nextNode().remove();
         }
         session.save();
         for (NodeIterator wc = rootNode.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_UNFILINGS).getNodes(); wc
            .hasNext();)
         {
            wc.nextNode().remove();
         }
         session.save();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
