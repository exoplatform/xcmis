/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.spi.utils;

import junit.framework.TestCase;

import org.exoplatform.commons.utils.ISO8601;
import org.exoplatform.services.document.DCMetaData;
import org.exoplatform.services.document.DocumentReader;
import org.exoplatform.services.document.DocumentReaderService;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TestPropertiesExtracting extends TestCase
{

   DocumentReaderService service_;

   public void setUp() throws Exception
   {
      service_ = new CmisDocumentReaderService();
   }

   public void testPDFDocumentReaderService() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/test.pdf");
      DocumentReader rdr = service_.getDocumentReader("application/pdf");
      Properties props = rdr.getProperties(is);
      printProps(props);
   }

   public void testPDFDocumentReaderServiceXMPMetadata() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/MyTest.pdf");
      DocumentReader rdr = service_.getDocumentReader("application/pdf");

      Properties testprops = rdr.getProperties(is);

      Properties etalon = new Properties();
      etalon.put(DCMetaData.TITLE, "Test de convertion de fichier tif");
      etalon.put(DCMetaData.CREATOR, "Christian Klaus");
      etalon.put(DCMetaData.SUBJECT, "20080901 TEST Christian Etat OK");
      Calendar c = ISO8601.parseEx("2008-09-01T08:01:10+00:00");;
      etalon.put(DCMetaData.DATE, c);

      evalProps(etalon, testprops);
   }

   public void testWordDocumentReaderService() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/test.doc");
      Properties props = service_.getDocumentReader("application/msword").getProperties(is);
      printProps(props);
   }

   public void testPPTDocumentReaderService() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/test.ppt");
      Properties props = service_.getDocumentReader("application/powerpoint").getProperties(is);
      printProps(props);
   }

   public void testExcelDocumentReaderService() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/test.xls");
      Properties props = service_.getDocumentReader("application/excel").getProperties(is);
      printProps(props);
   }

   public void testOODocumentReaderService() throws Exception
   {
      InputStream is = TestPropertiesExtracting.class.getResourceAsStream("/test.odt");
      Properties props = service_.getDocumentReader("application/vnd.oasis.opendocument.text").getProperties(is);
      printProps(props);
   }

   private void printProps(Properties props)
   {
      //      Iterator it = props.entrySet().iterator();
      //      props.toString();
      //      while (it.hasNext())
      //      {
      //         Map.Entry entry = (Map.Entry)it.next();
      //         System.out.println(" " + entry.getKey() + " -> [" + entry.getValue() + "]");
      //      }
   }

   private void evalProps(Properties etalon, Properties testedProps)
   {
      Iterator it = etalon.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry prop = (Map.Entry)it.next();
         Object tval = testedProps.get(prop.getKey());
         assertNotNull(prop.getKey() + " property not founded. ", tval);
         assertEquals(prop.getKey() + " property value is incorrect", prop.getValue(), tval);
      }
      assertEquals("size is incorrect", etalon.size(), testedProps.size());
   }

}
