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
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumPropertiesRelationship;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.wssoap.impl.TypeConverter;

import java.util.Iterator;
import java.util.List;

public abstract class BaseTest extends TestCase
{

   protected final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected String repositoryId = "cmis1";

   protected StandaloneContainer container;

   protected Connection conn;

   protected String rootFolderId;

   protected String testFolderId;

   private static final String SP_CONF_DEFAULT = "/conf/standalone/test-sp-inmemory-configuration.xml";

   @Override
   public void setUp() throws Exception
   {
      String propertySpConf = System.getProperty("sp.conf");

      String sp_conf =
         propertySpConf == null || propertySpConf.length() == 0 || propertySpConf.equalsIgnoreCase("${sp.conf}")
            ? SP_CONF_DEFAULT : propertySpConf;

      String containerConf = getClass().getResource(sp_conf).toString();
      StandaloneContainer.setConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      ConversationState state = new ConversationState(new Identity("root"));
      ConversationState.setCurrent(state);

      conn = CmisRegistry.getInstance().getConnection(repositoryId);

      rootFolderId = conn.getStorage().getRepositoryInfo().getRootFolderId();
      try
      {
         testFolderId = createFolder(rootFolderId, "testFolder");
      }
      catch (Exception e)
      {
         clearRoot();
         testFolderId = createFolder(rootFolderId, "testFolder");
      }

   }

   /**
    * Complex deploy service.
    *
    * @param address string service address
    * @param object service object
    * @param inInterceptors List<AbstractPhaseInterceptor> in interceptors
    * @param outInterceptors List<AbstractPhaseInterceptor> out interceptorss
    * @param wrapped boolean
    * @return server instance
    */
   protected Server complexDeployService(String address, Object object,
      List<AbstractPhaseInterceptor<?>> inInterceptors, List<AbstractPhaseInterceptor<?>> outInterceptors,
      boolean wrapped)
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Starting Service: object = " + object + " at the address = " + address);
      }

      JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
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
         {
            serverFactory.getServiceFactory().getService().getInInterceptors().add(in);
         }
      }

      if (outInterceptors != null && outInterceptors.size() > 0)
      {
         for (AbstractPhaseInterceptor<?> out : outInterceptors)
         {
            serverFactory.getServiceFactory().getService().getOutInterceptors().add(out);
         }
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
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add(name);

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      return conn.createDocument(parentId, TypeConverter.getPropertyMap(props), null, null, null, null,
         VersioningState.MAJOR);
   }

   protected String createFolder(String parentId, String name) throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add(name);

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      return conn.createFolder(parentId, TypeConverter.getPropertyMap(props), null, null, null);

   }

   protected String createPolicy(String parentId, String name, String policyText) throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.setLocalName(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.setLocalName(CmisConstants.NAME);
      propName.getValue().add(name + "1");
      // text
      CmisPropertyString propText = new CmisPropertyString();
      propText.setPropertyDefinitionId(CmisConstants.POLICY_TEXT);
      propText.setLocalName(CmisConstants.POLICY_TEXT);
      propText.getValue().add(name + "2");

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      props.getProperty().add(propText);
      return conn.createPolicy(parentId, TypeConverter.getPropertyMap(props), null, null, null);
   }

   protected String createRelationship(String source, String target) throws Exception
   {
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("relation1" + source);
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

      return conn.createRelationship(TypeConverter.getPropertyMap(props), null, null, null);
   }

   protected String getObjectId(CmisObjectType cmis)
   {
      return ((CmisPropertyId)getProperty(cmis, CmisConstants.OBJECT_ID)).getValue().get(0);
   }

   protected CmisProperty getProperty(CmisObjectType cmis, String propName)
   {
      List<CmisProperty> props = cmis.getProperties().getProperty();
      for (CmisProperty prop : props)
      {
         if (prop.getPropertyDefinitionId().equals(propName))
         {
            return prop;
         }
      }
      return null;
   }

   private void deleteObject(CmisObject obj)
   {
      String objId = obj.getObjectInfo().getId();
      try
      {
         if (obj.getObjectInfo().getBaseType().value().equals(BaseType.FOLDER.value()))
         {
            for (Iterator<CmisObject> iter = getChildren(objId).getItems().iterator(); iter.hasNext();)
            {
               CmisObject obj2 = iter.next();
               deleteObject(obj2);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      try
      {
         conn.deleteObject(objId, null);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   protected ItemsList<CmisObject> getChildren(String folderId) throws ObjectNotFoundException, FilterNotValidException
   {
      return conn.getChildren(folderId, false, null, false, true, CmisConstants.WILDCARD, null, null, -1, 0);
   }

   @Override
   protected void tearDown() throws Exception
   {
      clearRoot();
      super.tearDown();
      conn.close();
   }

   private void clearRoot()
   {
      try
      {
         conn.deleteTree(testFolderId, true, null, true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
