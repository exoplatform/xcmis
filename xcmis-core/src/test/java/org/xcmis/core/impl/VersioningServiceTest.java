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

package org.xcmis.core.impl;

import org.xcmis.core.CmisObjectType;
import org.xcmis.core.VersioningService;
import org.xcmis.core.impl.VersioningServiceImpl;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;
import org.xcmis.spi.utils.CmisUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VersioningServiceTest.java 2057 2009-07-10 06:39:15Z andrew00x
 *          $
 */
public class VersioningServiceTest extends BaseTest
{

   private VersioningService versioningService;

   public void setUp() throws Exception
   {
      super.setUp();
      versioningService = new VersioningServiceImpl(repositoryService, propertyService);
   }

   public void testCheckOut() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      String docId = doc.getObjectId();
      CmisObjectType pwc = versioningService.checkout(repositoryId, docId);
      String pwcId = CmisUtils.getObjectId(pwc);
      try
      {
         repository.getObjectById(pwcId);
      }
      catch (ObjectNotFoundException e)
      {
         fail("PWC not found.");
      }
   }

   public void testCheckOutFail() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      versioningService.checkout(repositoryId, doc.getObjectId());
      try
      {
         versioningService.checkout(repositoryId, doc.getObjectId());
         fail("One document in version series checked-out, second check-out should be fail.");
      }
      catch (VersioningException e)
      {
      }
   }

   public void testCheckIn() throws Exception
   {
      ContentStream data = new BaseContentStream("test".getBytes("UTF-8"), "test", "text/plain");
      Entry doc = createDocument(testFolder, "doc1", data);
      VersionSeries vs = repository.getVersionSeries(doc.getVersionSeriesId());
      String pwcId = vs.checkout(doc.getObjectId()).getObjectId();
      data = new BaseContentStream("test111".getBytes("UTF-8"), "test", "text/plain");
      CmisObjectType version =
         versioningService.checkin(repositoryId, pwcId, false, null, data, "checkin comment", null, null, null);
      ContentStream cs2 = repository.getObjectById(CmisUtils.getObjectId(version)).getContent(null);
      byte[] b = new byte[128];
      int r = cs2.getStream().read(b);
      assertEquals("test111", new String(b, 0, r));
   }

   public void testCancelCheckOut() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      String pwcId = CmisUtils.getObjectId(versioningService.checkout(repositoryId, doc.getObjectId()));
      String seriesId = doc.getVersionSeriesId();
      VersionSeries series = repository.getVersionSeries(seriesId);
      assertNotNull(series.getCheckedOut());
      versioningService.cancelCheckout(repositoryId, pwcId);
      assertNull(series.getCheckedOut());
   }

   public void testGetAllVersion() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      String docId = doc.getObjectId();
      String versionSeriesId = doc.getVersionSeriesId();
      List<CmisObjectType> versions = versioningService.getAllVersions(repositoryId, versionSeriesId, false, null);
      assertEquals(1, versions.size());
      assertEquals(docId, CmisUtils.getObjectId(versions.get(0)));
      String pwcId = CmisUtils.getObjectId(versioningService.checkout(repositoryId, docId));
      versioningService.checkin(repositoryId, pwcId, false, null, null, null, null, null, null);

      versions = versioningService.getAllVersions(repositoryId, versionSeriesId, false, null);
      assertEquals(2, versions.size());
   }

   public void testGetAllCheckedOutDocuments() throws Exception
   {
      Entry folder1 = createFolder(testFolder, "folder1");
      createDocument(testFolder, "doc", null);
      Entry doc1 = createDocument(folder1, "doc1", null);
      Entry folder2 = createFolder(folder1, "folder2");
      createDocument(folder2, "doc2", null);
      Entry doc3 = createDocument(folder2, "doc3", null);
      Entry folder3 = createFolder(folder2, "folder3");
      createDocument(folder3, "doc4", null);
      createDocument(folder3, "doc5", null);
      Entry doc6 = createDocument(folder3, "doc6", null);
      String pwcId6 = CmisUtils.getObjectId(versioningService.checkout(repositoryId, doc6.getObjectId()));
      String pwcId1 = CmisUtils.getObjectId(versioningService.checkout(repositoryId, doc1.getObjectId()));
      String pwcId3 = CmisUtils.getObjectId(versioningService.checkout(repositoryId, doc3.getObjectId()));
      Iterator<Entry> iter = repository.getCheckedOutDocuments(null);
      List<String> l = new ArrayList<String>();
      while (iter.hasNext())
         l.add(iter.next().getObjectId());
      assertEquals(3, l.size());
      assertTrue("Not found expected PWC.", l.contains(pwcId6));
      assertTrue("Not found expected PWC.", l.contains(pwcId1));
      assertTrue("Not found expected PWC.", l.contains(pwcId3));
   }

   public void testGetCheckedOutDocumentsFromFolder() throws Exception
   {
      Entry folder1 = createFolder(testFolder, "folder1");
      createDocument(testFolder, "doc", null);
      Entry doc1 = createDocument(folder1, "doc1", null);
      Entry folder2 = createFolder(folder1, "folder2");
      createDocument(folder2, "doc2", null);
      Entry doc22 = createDocument(folder2, "doc3", null);
      Entry folder3 = createFolder(folder2, "folder3");
      createDocument(folder3, "doc4", null);
      createDocument(folder3, "doc5", null);
      Entry doc33 = createDocument(folder3, "doc6", null);
      versioningService.checkout(repositoryId, doc33.getObjectId());
      versioningService.checkout(repositoryId, doc1.getObjectId());
      // Only this should be in iterator.
      CmisObjectType pwc = versioningService.checkout(repositoryId, doc22.getObjectId());
      String pwcId = CmisUtils.getObjectId(pwc);
      Iterator<Entry> iter = repository.getCheckedOutDocuments(folder2.getObjectId());
      assertEquals(pwcId, iter.next().getObjectId());
      assertFalse(iter.hasNext());
   }

}
