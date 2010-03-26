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

import org.apache.cxf.endpoint.Server;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.soap.VersioningServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.IncludeRelationships;
import org.xcmis.wssoap.impl.TypeConverter;
import org.xcmis.wssoap.impl.VersioningServicePortImpl;

import java.util.List;

import javax.xml.ws.Holder;

public class VersioningServiceTest extends BaseTest
{

   private VersioningServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "VersioningService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      server =
         complexDeployService(SERVICE_ADDRESS, new VersioningServicePortImpl(storageProvider), null, null, true);
      port = getVersioningService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testCheckOut() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      Holder<String> idHolder = new Holder<String>(id);
      Holder<Boolean> contentCopiedHolder = new Holder<Boolean>();
      port.checkOut(repositoryId, idHolder, new Holder<CmisExtensionType>(new CmisExtensionType()),
         contentCopiedHolder);
      CmisObjectListType checkedout = TypeConverter.getCmisObjectListType(conn.getCheckedOutDocs(//
         testFolderId, //
         false, // Allowable actions
         IncludeRelationships.NONE, //
         false,
         null, // Property filter
         null, // Rendition filter
         null, // Order by
         10, // Max items
         0 // Skip count
         ));
      assertEquals(1, checkedout.getObjects().size());
   }

   public void testCancelCheckOut() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      String pwcId  = conn.checkout(id);
      CmisObjectListType checkedout = TypeConverter.getCmisObjectListType(conn.getCheckedOutDocs(//
         testFolderId, //
         false, // Allowable actions
         IncludeRelationships.NONE, //
         false,
         null, // Property filter
         null, // Rendition filter
         null, // Order by
         10, // Max items
         0 // Skip count
         ));
      assertEquals(1, checkedout.getObjects().size());
      port.cancelCheckOut(repositoryId, pwcId, new CmisExtensionType());
      // No more checked-out documents
      checkedout = TypeConverter.getCmisObjectListType(conn.getCheckedOutDocs(//
         testFolderId, //
         false, // Allowable actions
         IncludeRelationships.NONE, //
         false,
         null, // Property filter
         null, // Rendition filter
         null, // Order by
         10, // Max items
         0 // Skip count
         ));
      assertEquals(0, checkedout.getObjects().size());
   }

   public void testCheckIn() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      String pwcId = conn.checkout(id);
      Holder<String> pwcHolder = new Holder<String>(pwcId);
      CmisObjectType pwc = TypeConverter.getCmisObjectType(conn.getObject(pwcId,false,IncludeRelationships.NONE, false, false,false,null,null));
      CmisPropertyId versionSeriesIdProp = (CmisPropertyId)getProperty(pwc, CMIS.VERSION_SERIES_ID);
      String versionSeriesId = versionSeriesIdProp.getValue().get(0);
      List<CmisObjectType> allVersions =
       TypeConverter.getListCmisObjectType(conn.getAllVersions(versionSeriesId, false, false, null));
      //      assertEquals(1, allVersions.size());
      assertEquals(2, allVersions.size());
      port.checkIn(//
         repositoryId, //
         pwcHolder, //
         true, // Major
         null, // Properties
         null, // Content stream
         "comment", // Check-in comment
         null, // Policies
         null, // Add ACL
         null, // Remove ACL
         new Holder<CmisExtensionType>() // Extensions
         );
      allVersions = TypeConverter.getListCmisObjectType(conn.getAllVersions(versionSeriesId, false, false, null));
      assertEquals(2, allVersions.size());
   }

   public void testGetAllVersions() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");
      String pwcId = conn.checkout(docId);
      conn.checkin(//
         pwcId, //
         true, // Major
         null, // Properties
         null, // Content stream
         "", // Check-in comment
         null, // Add ACL
         null, // Remove ACL
         null // Policies
         );
      CmisObjectType pwc = TypeConverter.getCmisObjectType(conn.getObject(pwcId,false,IncludeRelationships.NONE, false, false,false,null,null));
      CmisPropertyId versionSeriesIdProp = (CmisPropertyId)getProperty(pwc, CMIS.VERSION_SERIES_ID);
      String versionSeriesId = versionSeriesIdProp.getValue().get(0);
      List<CmisObjectType> allVersions = port.getAllVersions(repositoryId, versionSeriesId, null, false, null);
      assertEquals(2, allVersions.size());

   }

   public void testGetLatestVersionProperties() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      // XXX : Be sure creation document and PWC have different Last Modification dates.
      Thread.sleep(500);
      String pwcId = conn.checkout(id);
      String lv = conn.checkin(//
         pwcId, //
         true, // Major
         null, // Properties
         null, // Content stream
         "", // Check-in comment
         null, // Add ACL
         null, // Remove ACL
         null // Policies
         );
      CmisObjectType pwc = TypeConverter.getCmisObjectType(conn.getObject(pwcId,false,IncludeRelationships.NONE, false, false,false,null,null));
      CmisPropertyId versionSeriesIdProp = (CmisPropertyId)getProperty(pwc, CMIS.VERSION_SERIES_ID);
      String versionSeriesId = versionSeriesIdProp.getValue().get(0);

      CmisPropertiesType properties = port.getPropertiesOfLatestVersion(//
         repositoryId, //
         versionSeriesId, //
         false, //
         CMIS.OBJECT_ID, //
         new CmisExtensionType() //
         );
      assertEquals(lv, ((CmisPropertyId)properties.getProperty().get(0)).getValue().get(0));
   }

   private VersioningServicePort getVersioningService(String address)
   {
      try
      {
         org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
         client.setServiceClass(VersioningServicePort.class);
         client.setAddress(address);
         Object obj = client.create();
         return (VersioningServicePort)obj;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      return null;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }
}
