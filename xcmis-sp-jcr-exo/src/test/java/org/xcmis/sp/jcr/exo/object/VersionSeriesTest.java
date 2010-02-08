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

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.version.Version;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionSeriesTest extends BaseTest
{

   public void testCancelCheckout() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());

      Entry pwc = series.checkout(doc.getObjectId());
      String pwcId = pwc.getObjectId();

      assertTrue(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      assertTrue(pwc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));

      assertEquals(pwcId, doc.getCheckedOutId());
      assertEquals(pwcId, pwc.getCheckedOutId());

      assertFalse(doc.canCheckOut());
      assertTrue(doc.canCheckIn());

      assertNotNull(series.getCheckedOut());
      assertEquals(pwc, series.getCheckedOut());

      series.cancelCheckout();

      assertNull(series.getCheckedOut());
      assertNull(doc.getCheckedOutId());

      try
      {
         cmisRepository.getObjectById(pwcId);
         fail("PWC must be removed.");
      }
      catch (ObjectNotFoundException e)
      {
      }
      assertTrue(doc.canCheckOut());
      assertFalse(doc.canCheckIn());
   }

   public void testCheckin() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      Entry pwc = series.checkout(doc.getObjectId());
      String pwcId = pwc.getObjectId();

      assertEquals(2, series.getAllVersions().size());

      // Update PWC.
      pwc.setContent(new BaseContentStream("to be or not to be".getBytes(), null, "text/plain"));
      pwc.setString("test1", "to be");
      pwc.setStrings("test2", new String[]{"to be", "or not to be"});
      pwc.save();

      Entry newVer = series.checkin(true, "test1");
      assertEquals(2, series.getAllVersions().size());

      // check content of document.
      byte[] b = new byte[128];
      ContentStream content = newVer.getContent(null);
      assertEquals("text/plain", content.getMediaType());
      int rd = content.getStream().read(b);
      assertEquals("to be or not to be", new String(b, 0, rd));
      // check properties
      assertEquals("to be", doc.getString("test1"));
      assertTrue(Arrays.equals(new String[]{"to be", "or not to be"}, doc.getStrings("test2")));
      //
      assertTrue(newVer.isLatest());
      assertTrue(newVer.isMajor());
      assertTrue(newVer.isLatestMajor());

      // Version series should be ready to checkout again.
      assertNull(series.getCheckedOut());
      assertNull(doc.getCheckedOutId());

      try
      {
         cmisRepository.getObjectById(pwcId);
         fail("PWC must be removed.");
      }
      catch (ObjectNotFoundException e)
      {
      }
      assertTrue(doc.canCheckOut());
      assertFalse(doc.canCheckIn());
   }

   public void testCheckout() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", "test".getBytes(), "text/plain");
      assertFalse(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      Entry pwc = series.checkout(doc.getObjectId());

      //      List<String> l = new ArrayList<String>();
      //      for (NodeIterator iter = root.getNode("cmis:system/cmis:workingCopies").getNodes(); iter.hasNext();)
      //         l.add(iter.nextNode().getPath());
      //      assertEquals(1, l.size());
      //      assertEquals("/cmis:system/cmis:workingCopies/" + series.getVersionSeriesId(), l.get(0));

      assertFalse(pwc.isLatest());
      assertTrue(doc.isLatest());

      assertTrue(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      assertTrue(pwc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));

      assertEquals(pwc.getObjectId(), doc.getString(CMIS.VERSION_SERIES_CHECKED_OUT_ID));
      assertEquals(pwc.getObjectId(), pwc.getString(CMIS.VERSION_SERIES_CHECKED_OUT_ID));

      assertEquals("pwc", pwc.getVersionLabel());
      assertEquals("current", doc.getVersionLabel());

      assertEquals(2, series.getAllVersions().size());
      assertEquals(doc, series.getLatestVersion());

      assertNotNull(series.getLatestMajorVersion());
      assertFalse(pwc.equals(series.getLatestMajorVersion()));
      assertEquals(pwc, series.getCheckedOut());

      pwc.delete();
      doc.delete(); // <<<<<<<<<<<<<<<<<
   }

   public void testRemoveCheckout() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", "test".getBytes(), "text/plain");
      assertFalse(doc.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      Entry pwc = series.checkout(doc.getObjectId());

      try
      {
         doc.delete();
         fail("org.xcmis.ConstraintException must be thrown, doc has checkedout version.");
      }
      catch (org.xcmis.spi.ConstraintException e)
      {
         // OK
      }
   }

   public void testDocumentInCheckedOutState() throws Exception
   {
      EntryImpl doc =
         createDocument(testRootFolderId, "doc", "nt:file", new byte[0], "", EnumVersioningState.CHECKEDOUT);
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      assertEquals(doc.getObjectId(), series.getCheckedOut().getObjectId());
      assertEquals(1, series.getAllVersions().size());
      //      assertFalse(doc.isLatest());
      //      assertNull(series.getLatestVersion());
      assertNull(series.getLatestMajorVersion());
      Entry v = series.checkin(true, "comment");
      assertNull(v.getCheckedOutId());
      assertNull(v.getCheckedOutBy());
      assertFalse(v.getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT));
      assertNull(series.getCheckedOut());
   }

   public void testGetAllVersions() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      // Create versions via JCR API.
      Node node = ((EntryImpl)doc).getNode();
      //      node.addMixin(JcrCMIS.MIX_VERSIONABLE);
      node.save();
      Version v1 = node.checkin();
      node.checkout();
      Version v2 = node.checkin();
      node.checkout();
      Version v3 = node.checkin();
      node.checkout();

      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      List<Entry> versions = series.getAllVersions();
      assertEquals(4, versions.size());
      assertEquals(((ExtendedNode)node).getIdentifier(), versions.get(0).getObjectId());
      assertEquals(((ExtendedNode)v3).getIdentifier(), versions.get(1).getObjectId());
      assertEquals(((ExtendedNode)v2).getIdentifier(), versions.get(2).getObjectId());
      assertEquals(((ExtendedNode)v1).getIdentifier(), versions.get(3).getObjectId());
      
      series.checkout(null);
   }

   public void testGetCheckedOut() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      assertNull(series.getCheckedOut());
      String pwcid = series.checkout(doc.getObjectId()).getObjectId();
      assertNotNull(series.getCheckedOut());
      assertEquals(pwcid, series.getCheckedOut().getObjectId());
   }

   public void testGetLatestMajorVersion() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", "nt:file", new byte[0], "", EnumVersioningState.MINOR);
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      assertNull(series.getLatestMajorVersion());
      // Set current version a major.
      doc.setBoolean(CMIS.IS_MAJOR_VERSION, true);
      doc.save();

      assertEquals(doc, series.getLatestMajorVersion());

      series.checkout(doc.getObjectId());
      series.checkin(false, "version 1"); // New version will be NOT marked as major.

      // Expect version "1" as latest major.
      assertEquals(series.getAllVersions().get(1), series.getLatestMajorVersion());

      series.checkout(doc.getObjectId());
      series.checkin(true, "version 2"); // New version will be marked as major.

      // Latest Version has the same ID all the time. 
      assertEquals(doc, series.getLatestMajorVersion());
   }

   public void testGetLatestVersion() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      assertEquals(1, series.getAllVersions().size());
      assertEquals(doc, series.getLatestVersion());
      series.checkout(doc.getObjectId());
      // Latest version and PWC.
      assertEquals(2, series.getAllVersions().size());
      assertEquals(doc, series.getLatestVersion());

      series.checkin(false, "version 1");
      // Two versions.
      assertEquals(2, series.getAllVersions().size());
      // Latest Version has the same ID all the time. 
      assertEquals(doc, series.getLatestVersion());

      series.checkout(doc.getObjectId());
      assertEquals(3, series.getAllVersions().size());
      assertEquals(doc, series.getLatestVersion());
   }

   public void testGetVersionSeriesId() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      VersionSeries series = cmisRepository.getVersionSeries(doc.getVersionSeriesId());
      assertEquals(doc.getVersionSeriesId(), series.getVersionSeriesId());
   }

}
