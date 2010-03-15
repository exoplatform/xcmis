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
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.VersionSeries;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ExistedDataTest.java 27 2010-02-08 07:49:20Z andrew00x $
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

      VersionSeries versionSeries = new VersionSeriesImpl(file.getVersionHistory());
      EntryImpl latest = new EntryImpl(file);
      assertEquals(latest.getObjectId(), versionSeries.getLatestVersion().getObjectId());
      List<Entry> allVersions = versionSeries.getAllVersions();
      assertEquals(4, allVersions.size());
      assertEquals(latest.getObjectId(), allVersions.get(0).getObjectId());
      assertEquals(v3.getObjectId(), allVersions.get(1).getObjectId());
      assertEquals(v2.getObjectId(), allVersions.get(2).getObjectId());
      assertEquals(v1.getObjectId(), allVersions.get(3).getObjectId());
   }

}
