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

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.xcmis.core.EnumVersioningState;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

public abstract class BaseTest extends TestCase
{

   private static final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected final String wsName = "ws1";

   protected final String jcrRepositoryName = "db1";

   protected String cmisRepositoryId = "cmis1";

   protected String testRootFolderId;

   protected StandaloneContainer container;

   protected SessionImpl session;

   protected RepositoryImpl repository;

   protected CredentialsImpl credentials;

   protected RepositoryService repositoryService;

   protected Node root;

   protected EntryImpl testRootFolder;

   protected Node relationshipsNode;

   protected org.xcmis.spi.RepositoriesManager cmisRepositoriesManager;

   protected org.xcmis.sp.jcr.exo.RepositoryImpl cmisRepository;

   protected ThreadLocalSessionProviderService sessionProviderService;

   private volatile static boolean shoutDown;

   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      String loginConf = Thread.currentThread().getContextClassLoader().getResource("login.conf").toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", loginConf);

      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      cmisRepositoriesManager =
         (org.xcmis.spi.RepositoriesManager)container
            .getComponentInstanceOfType(org.xcmis.spi.RepositoriesManager.class);
      repository = (RepositoryImpl)repositoryService.getRepository(jcrRepositoryName);

      session = (SessionImpl)repository.login(credentials, wsName);

      root = session.getRootNode();
      relationshipsNode = root.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_RELATIONSHIPS);

      testRootFolder = createFolder(JcrCMIS.ROOT_FOLDER_ID, "testRoot");
      testRootFolderId = testRootFolder.getObjectId();

      sessionProviderService =
         (ThreadLocalSessionProviderService)container
            .getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      assertNotNull(sessionProviderService);
      sessionProviderService.setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("root"))));
      cmisRepository = (org.xcmis.sp.jcr.exo.RepositoryImpl)cmisRepositoriesManager.getRepository(cmisRepositoryId);

      if (!shoutDown)
      {
         Runtime.getRuntime().addShutdownHook(new Thread()
         {
            public void run()
            {
               // database.close();
               container.stop();
               System.out.println("The container are stopped");
            }
         });
         shoutDown = true;
      }
   }

   protected void tearDown() throws Exception
   {
      try
      {
         session.refresh(false);
         Node rootNode = session.getRootNode();
         for (NodeIterator relationships =
            rootNode.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_RELATIONSHIPS).getNodes(); relationships
            .hasNext();)
         {
            relationships.nextNode().remove();
         }
         session.save();
         for (NodeIterator wc = rootNode.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_WORKING_COPIES).getNodes(); wc
            .hasNext();)
         {
            wc.nextNode().remove();
         }
         session.save();
         if (rootNode.hasNodes())
         {
            // clean test root
            for (NodeIterator children = rootNode.getNodes(); children.hasNext();)
            {
               Node node = children.nextNode();
               if (!node.getPath().startsWith("/jcr:system") //
                  && !node.getPath().startsWith("/exo:audit") //
                  && !node.getPath().startsWith("/exo:organization") //
                  && !node.getPath().equals("/" + JcrCMIS.CMIS_SYSTEM))
               {
                  node.remove();
               }
            }
            session.save();
         }
      }
      catch (Exception e)
      {
         LOG.error("Exception in tearDown() ", e);
      }
      finally
      {
         session.logout();
      }

      super.tearDown();
   }

   protected EntryImpl createDocument(String parentId, String name, byte[] data, String contentType) throws Exception
   {
      return createDocument(parentId, name, JcrCMIS.NT_FILE, data, contentType);
   }

   protected EntryImpl createDocument(String parentId, String name, String type, byte[] data, String contentType)
      throws Exception
   {
      return createDocument(((ExtendedSession)session).getNodeByIdentifier(parentId), name, type, data, contentType,
         EnumVersioningState.MAJOR);
   }

   protected EntryImpl createDocument(String parentId, String name, String type, byte[] data, String contentType,
      EnumVersioningState versioningState) throws Exception
   {
      return createDocument(((ExtendedSession)session).getNodeByIdentifier(parentId), name, type, data, contentType,
         versioningState);
   }

   protected EntryImpl createDocument(Node parent, String name, byte[] data, String contentType) throws Exception
   {

      return createDocument(parent, name, JcrCMIS.NT_FILE, data, contentType, EnumVersioningState.MAJOR);
   }

   protected EntryImpl createDocument(Node parent, String name, String type, byte[] data, String contentType,
      EnumVersioningState versioningState) throws Exception
   {
      Node doc = parent.addNode(name, type);
      doc.addMixin(JcrCMIS.CMIS_MIX_DOCUMENT);
      doc.addMixin("mix:versionable");
      String docId = ((ExtendedNode)doc).getIdentifier();
      doc.setProperty(CMIS.NAME, doc.getName());
      doc.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:document");
      doc.setProperty(CMIS.BASE_TYPE_ID, "cmis:document");
      doc.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      doc.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      doc.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      doc.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      doc.setProperty(CMIS.IS_IMMUTABLE, false);

      doc.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, contentType);
      doc.setProperty(CMIS.CONTENT_STREAM_LENGTH, data.length);
      doc.setProperty(CMIS.CONTENT_STREAM_FILE_NAME, doc.getName());
      doc.setProperty(CMIS.CONTENT_STREAM_ID, docId);

      doc.setProperty(CMIS.VERSION_SERIES_ID, doc.getProperty("jcr:versionHistory").getString());
      doc.setProperty(CMIS.IS_LATEST_VERSION, true);
      doc.setProperty(CMIS.VERSION_LABEL, versioningState == EnumVersioningState.CHECKEDOUT ? "pwc" : "current");
      doc.setProperty(CMIS.IS_MAJOR_VERSION, versioningState == EnumVersioningState.MAJOR);
      if (versioningState == EnumVersioningState.CHECKEDOUT)
      {
         doc.addMixin("cmis:pwc");
         doc.setProperty(JcrCMIS.CMIS_LATEST_VERSION, doc);
         doc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)doc).getIdentifier());
         doc.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, session.getUserID());
      }

      Node content = doc.addNode(JcrCMIS.JCR_CONTENT, "nt:resource");
      content.setProperty(JcrCMIS.JCR_MIMETYPE, contentType);
      content.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
      content.setProperty(JcrCMIS.JCR_DATA, new ByteArrayInputStream(data));

      session.save();
      return new EntryImpl(doc);
   }

   protected EntryImpl createPolicy(String parentId, String name, String policyText) throws Exception
   {
      return createPolicy(((ExtendedSession)session).getNodeByIdentifier(parentId), name, policyText);
   }

   protected EntryImpl createPolicy(Node parent, String name, String policyText) throws Exception
   {
      Node policy = parent.addNode(name, JcrCMIS.CMIS_NT_POLICY);
      policy.setProperty(CMIS.NAME, policy.getName());
      //      policy.setProperty(CMIS.OBJECT_ID, idResolver.getNodeIdentifier(policy));
      policy.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:policy");
      policy.setProperty(CMIS.BASE_TYPE_ID, "cmis:policy");
      policy.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      policy.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      policy.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      policy.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      policy.setProperty(CMIS.POLICY_TEXT, policyText);

      session.save();
      return new EntryImpl(policy);
   }

   protected EntryImpl createFolder(String parentId, String name) throws Exception
   {
      return createFolder(((ExtendedSession)session).getNodeByIdentifier(parentId), name);
   }

   protected EntryImpl createFolder(Node parent, String name) throws Exception
   {
      Node folder = parent.addNode(name, JcrCMIS.NT_FOLDER);
      folder.addMixin(JcrCMIS.CMIS_MIX_FOLDER);
      folder.setProperty(CMIS.NAME, folder.getName());
      //      folder.setProperty(CMIS.OBJECT_ID, idResolver.getNodeIdentifier(folder));
      folder.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:folder");
      folder.setProperty(CMIS.BASE_TYPE_ID, "cmis:folder");
      folder.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      folder.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      folder.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      folder.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      session.save();
      return new EntryImpl(folder);
   }

   protected EntryImpl createRelationship(String sourceId, String targetId) throws Exception
   {
      Node relationshipHierarchy = null;
      if (relationshipsNode.hasNode(sourceId))
         relationshipHierarchy = relationshipsNode.getNode(sourceId);
      else
         relationshipHierarchy = relationshipsNode.addNode(sourceId, JcrCMIS.NT_UNSTRUCTURED);
      Node relationship = relationshipHierarchy.addNode(targetId, JcrCMIS.CMIS_NT_RELATIONSHIP);
      relationship.setProperty(CMIS.NAME, relationship.getName());
      //      relationship.setProperty(CMIS.OBJECT_ID, idResolver.getNodeIdentifier(relationship));
      relationship.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:relationship");
      relationship.setProperty(CMIS.BASE_TYPE_ID, "cmis:relationship");
      relationship.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      relationship.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      relationship.setProperty(CMIS.SOURCE_ID, ((ExtendedSession)session).getNodeByIdentifier(sourceId));
      relationship.setProperty(CMIS.TARGET_ID, ((ExtendedSession)session).getNodeByIdentifier(targetId));
      relationship.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      relationship.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      session.save();
      return new EntryImpl(relationship);
   }

}
