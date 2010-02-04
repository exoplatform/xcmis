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

import org.xcmis.core.EnumBaseObjectTypeIds;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.sp.jcr.exo.object.EntryVersion;
import org.xcmis.sp.jcr.exo.object.VersionSeriesImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ExistedDataTest extends BaseTest
{

   private Node file;

   public void setUp() throws Exception
   {
      super.setUp();
      file = root.addNode("file", "nt:file");
      Node content = file.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream("nt:file test".getBytes()));
      session.save();
   }

   public void testNtFileAsEntry() throws Exception
   {
      EntryImpl document = new EntryImpl(file);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT, document.getScope());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), document.getType().getId());
      assertEquals("file", document.getName());
      ContentStream content = document.getContent(null);
      assertEquals("file", content.getFileName());
      assertEquals("text/plain", content.getMediaType());
      byte[] buf = new byte[128];
      int r = content.getStream().read(buf);
      assertEquals("nt:file test", new String(buf, 0, r));
   }

   public void testRename() throws Exception
   {
      EntryImpl document = new EntryImpl(file);
      document.setName("renamed-file");
      assertEquals("renamed-file", document.getName());
   }

   public void testSetContent() throws Exception
   {
      EntryImpl document = new EntryImpl(file);
      ContentStream content = new BaseContentStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes(),null,"text/xml");
      document.setContent(content);
      document.save();

      Node jcrContent = file.getNode("jcr:content");
      assertEquals("text/xml", jcrContent.getProperty("jcr:mimeType").getString());
      assertEquals("<?xml version='1.0' encoding='UTF-8'?>", jcrContent.getProperty("jcr:data").getString());
   }

   public void testSetContentInCheckedInState() throws Exception
   {
      file.addMixin("mix:versionable");
      session.save();
      file.checkin();
      testSetContent();
      assertTrue(file.isCheckedOut());
   }

   public void testVersioning() throws Exception
   {
      // Check CMIS with existed version history.
      file.addMixin("mix:versionable");
      session.save();
      // create 3 versions.
      EntryVersion v1 = new EntryVersion(file.checkin());
      file.checkout();

      EntryVersion v2 = new EntryVersion(file.checkin());
      file.checkout();

      EntryVersion v3 = new EntryVersion(file.checkin());
      file.checkout();

      VersionSeries versionSeries = new VersionSeriesImpl(file);
      EntryImpl latest = new EntryImpl(file);
      assertEquals(latest.getObjectId(), versionSeries.getLatestVersion().getObjectId());
      List<Entry> allVersions = versionSeries.getAllVersions();
      assertEquals(4, allVersions.size());
      assertEquals(latest.getObjectId(), allVersions.get(0).getObjectId());
      assertEquals(v3.getObjectId(), allVersions.get(1).getObjectId());
      assertEquals(v2.getObjectId(), allVersions.get(2).getObjectId());
      assertEquals(v1.getObjectId(), allVersions.get(3).getObjectId());
   }

   public void testCheckout() throws Exception
   {
      // Check CMIS versioning with simple nt:file.
      VersionSeries versionSeries = new VersionSeriesImpl(file);
      // Not need document id in fact. Only current node may be in use.
      Node pwc = ((EntryImpl)versionSeries.checkout(null)).getNode();

      String latestId = ((ExtendedNode)file).getIdentifier();
      String pwcId = ((ExtendedNode)pwc).getIdentifier();

      assertEquals("/cmis:workingCopies/" + versionSeries.getVersionSeriesId(), pwc.getPath());

      assertEquals(false, pwc.getProperty(CMIS.IS_LATEST_VERSION).getBoolean());
      assertEquals(false, pwc.getProperty(CMIS.IS_MAJOR_VERSION).getBoolean());
      assertEquals(true, pwc.getProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT).getBoolean());
      assertEquals(pwcId, pwc.getProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID).getString());
      assertEquals("pwc", pwc.getProperty(CMIS.VERSION_LABEL).getString());
      assertEquals(latestId, pwc.getProperty(JcrCMIS.CMIS_LATEST_VERSION).getString());
      assertEquals(latestId, pwc.getProperty(CMIS.VERSION_SERIES_ID).getString());

      assertEquals(true, file.getProperty(CMIS.IS_LATEST_VERSION).getBoolean());
      assertEquals(false, file.getProperty(CMIS.IS_MAJOR_VERSION).getBoolean());
      assertEquals(true, file.getProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT).getBoolean());
      assertEquals(pwcId, file.getProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID).getString());
      assertEquals("latest", file.getProperty(CMIS.VERSION_LABEL).getString());
      assertEquals(latestId, file.getProperty(JcrCMIS.CMIS_LATEST_VERSION).getString());
      assertEquals(latestId, file.getProperty(CMIS.VERSION_SERIES_ID).getString());

      try
      {
         file.remove();
         session.save();
         fail("Must not be able to delete node.");
      }
      catch (javax.jcr.ReferentialIntegrityException e)
      {
         // OK
      }
   }

   public void testCancelCheckout() throws Exception
   {
      VersionSeries versionSeries = new VersionSeriesImpl(file);
      file.addMixin(JcrCMIS.CMIS_VERSIONABLE);
      file.setProperty(JcrCMIS.CMIS_LATEST_VERSION, file);
      file.setProperty(JcrCMIS.IS_LATEST_VERSION, true);
      file.setProperty(JcrCMIS.VERSION_LABEL, EntryImpl.latestLabel);
      file.setProperty(JcrCMIS.VERSION_SERIES_ID, versionSeries.getVersionSeriesId());
      session.save();

      String pwcPath = "/cmis:workingCopies/" + versionSeries.getVersionSeriesId();
      session.getWorkspace().copy(file.getPath(), pwcPath);
      Node pwcNode = (Node)session.getItem(pwcPath);

      file.setProperty(JcrCMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)pwcNode).getIdentifier());
      file.setProperty(JcrCMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
      session.save();

      assertNotNull(versionSeries.getCheckedOut());
      EntryImpl pwc = (EntryImpl)versionSeries.getCheckedOut();
      assertEquals(((ExtendedNode)pwcNode).getIdentifier(), ((ExtendedNode)pwc.getNode()).getIdentifier());

      versionSeries.cancelCheckout();

      assertEquals(false, session.itemExists(pwcPath));
      assertEquals(false, file.getProperty(JcrCMIS.IS_VERSION_SERIES_CHECKED_OUT).getBoolean());
      assertEquals(false, file.hasProperty(JcrCMIS.VERSION_SERIES_CHECKED_OUT_ID));
   }

   public void testCheckin() throws Exception
   {
      VersionSeries versionSeries = new VersionSeriesImpl(file);
      file.addMixin(JcrCMIS.CMIS_VERSIONABLE);
      file.setProperty(JcrCMIS.CMIS_LATEST_VERSION, file);
      file.setProperty(JcrCMIS.IS_LATEST_VERSION, true);
      file.setProperty(JcrCMIS.VERSION_LABEL, EntryImpl.latestLabel);
      file.setProperty(JcrCMIS.VERSION_SERIES_ID, versionSeries.getVersionSeriesId());
      session.save();

      String pwcPath = "/cmis:workingCopies/" + versionSeries.getVersionSeriesId();
      session.getWorkspace().copy(file.getPath(), pwcPath);
      Node pwcNode = (Node)session.getItem(pwcPath);

      file.setProperty(JcrCMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)pwcNode).getIdentifier());
      file.setProperty(JcrCMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
      session.save();

      versionSeries.checkin(true, "to be or not to be");
      assertEquals(false, session.itemExists(pwcPath));
      assertEquals(false, file.getProperty(JcrCMIS.IS_VERSION_SERIES_CHECKED_OUT).getBoolean());
      assertEquals(false, file.hasProperty(JcrCMIS.VERSION_SERIES_CHECKED_OUT_ID));

      assertNull(versionSeries.getCheckedOut());
      assertEquals(2, versionSeries.getAllVersions().size());

      VersionHistory vh = file.getVersionHistory();
      Version v1 = vh.getVersion("1");
      Entry e1 = new EntryVersion(v1);
      assertEquals("1", e1.getVersionLabel());
      assertEquals(false, e1.isLatest());
      assertEquals(false, e1.isMajor());

      Entry latest = new EntryImpl(file);
      assertEquals("latest", latest.getVersionLabel());
      assertEquals(true, latest.isLatest());
      assertEquals(true, latest.isMajor());
      assertEquals(true, latest.isLatestMajor());
      assertEquals("to be or not to be", latest.getCheckInComment());
   }
}
