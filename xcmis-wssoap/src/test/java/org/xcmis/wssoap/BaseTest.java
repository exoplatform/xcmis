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

package org.xcmis.wssoap;

import junit.framework.TestCase;

import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.BeanInvoker;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.xcmis.core.AccessControlService;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumPropertiesRelationship;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.Repository;
import org.xcmis.spi.object.Entry;

import java.util.Iterator;
import java.util.List;

public abstract class BaseTest extends TestCase
{

   protected final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected String repositoryId = "cmis1";

   protected StandaloneContainer container;

   public RepositoryService cmisRepositoryService;

   public ObjectService objectService;

   public NavigationService navigationService;

   public RelationshipService relationshipService;

   public VersioningService versioningService;

   public PolicyService policyService;

   public AccessControlService aclService;

   protected String testFolderId;

   public Repository repository;

   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      ConversationState state = new ConversationState(new Identity("root"));
      ConversationState.setCurrent(state);

      cmisRepositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      objectService = (ObjectService)container.getComponentInstanceOfType(ObjectService.class);
      navigationService = (NavigationService)container.getComponentInstanceOfType(NavigationService.class);
      relationshipService = (RelationshipService)container.getComponentInstanceOfType(RelationshipService.class);
      versioningService = (VersioningService)container.getComponentInstanceOfType(VersioningService.class);
      policyService = (PolicyService)container.getComponentInstanceOfType(PolicyService.class);
      aclService = (AccessControlService)container.getComponentInstanceOfType(AccessControlService.class);

      repository = cmisRepositoryService.getRepository(repositoryId);
      testFolderId = createFolder(repository.getRepositoryInfo().getRootFolderId(), "testFolder");
   }

   /**
    * Complex deploy service.
    * 
    * @param address string service address
    * @param object service object
    * @param inInterceptors List<AbstractPhaseInterceptor> in interceptors
    * @param outInterceptors  List<AbstractPhaseInterceptor> out interceptorss
    * @param wrapped boolean
    * @return server instance
    */
   protected Server complexDeployService(String address, Object object,
      List<AbstractPhaseInterceptor<?>> inInterceptors, List<AbstractPhaseInterceptor<?>> outInterceptors,
      boolean wrapped)
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Starting Service: object = " + object + " at the address = " + address);

      JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
      //serverFactory.setBindingFactory(new HttpBindingInfoFactoryBean());
      serverFactory.getServiceFactory().setDataBinding(new JAXBDataBinding());
      serverFactory.setServiceClass(object.getClass());
      serverFactory.setAddress(address);
      serverFactory.setBus(CXFBusFactory.getDefaultBus());
      Server server = serverFactory.create();
      if (LOG.isDebugEnabled())
      {
         serverFactory.getServiceFactory().getService().getInInterceptors().add(new LoggingInInterceptor());
         serverFactory.getServiceFactory().getService().getOutInterceptors().add(new LoggingOutInterceptor());
      }
      if (inInterceptors != null && inInterceptors.size() > 0)
      {
         for (AbstractPhaseInterceptor<?> in : inInterceptors)
            serverFactory.getServiceFactory().getService().getInInterceptors().add(in);
      }

      if (outInterceptors != null && outInterceptors.size() > 0)
      {
         for (AbstractPhaseInterceptor<?> out : outInterceptors)
            serverFactory.getServiceFactory().getService().getOutInterceptors().add(out);
      }

      if (wrapped)
      {
         serverFactory.getServiceFactory().setAnonymousWrapperTypes(true);
         serverFactory.getServiceFactory().setQualifyWrapperSchema(true);
         serverFactory.getServiceFactory().setWrapped(true);
      }
      Service service = server.getEndpoint().getService();

      service.setInvoker(new BeanInvoker(object));
      server.start();
      return server;
   }

   protected String createDocument(String parentId, String name) throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add(name);

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      CmisObjectType document =
         objectService.createDocument(repositoryId, parentId, props, null, EnumVersioningState.MAJOR, null, null,
            null);
      return getObjectId(document);
   }

   protected String createFolder(String parentId, String name) throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add(name);

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      CmisObjectType folder = objectService.createFolder(repositoryId, parentId, props, null, null, null);
      return getObjectId(folder);
   }

   protected String createPolicy(String parentId, String name, String policyText) throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add(name);
      // text
      CmisPropertyString propText = new CmisPropertyString();
      propText.setPropertyDefinitionId(CMIS.POLICY_TEXT);
      propText.getValue().add(name);

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      props.getProperty().add(propText);
      CmisObjectType policy = objectService.createPolicy(repositoryId, parentId, props, null, null, null);
      return getObjectId(policy);
   }

   protected String createRelationship(String source, String target) throws Exception
   {
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add("relation1");
      // sourceId
      CmisPropertyId sourceId = new CmisPropertyId();
      sourceId.setPropertyDefinitionId(EnumPropertiesRelationship.CMIS_SOURCE_ID.value());
      sourceId.getValue().add(source);
      // targetId
      CmisPropertyId targetId = new CmisPropertyId();
      targetId.setPropertyDefinitionId(EnumPropertiesRelationship.CMIS_TARGET_ID.value());
      targetId.getValue().add(target);

      CmisPropertiesType props = new CmisPropertiesType();
      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      props.getProperty().add(sourceId);
      props.getProperty().add(targetId);

      CmisObjectType relationship = objectService.createRelationship(repositoryId, props, null, null, null);
      return getObjectId(relationship);
   }

   protected String getObjectId(CmisObjectType cmis)
   {
      return ((CmisPropertyId)getProperty(cmis, CMIS.OBJECT_ID)).getValue().get(0);
   }

   protected CmisProperty getProperty(CmisObjectType cmis, String propName)
   {
      List<CmisProperty> props = cmis.getProperties().getProperty();
      for (CmisProperty prop : props)
      {
         if (prop.getPropertyDefinitionId().equals(propName))
            return prop;
      }
      return null;
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
      for (Iterator<Entry> iter = repository.getCheckedOutDocuments(null); iter.hasNext();)
         iter.next().delete();
      for (Iterator<Entry> iter = repository.getRootFolder().getChildren(); iter.hasNext();)
         iter.next().delete();
   }

}
