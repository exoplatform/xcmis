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

package org.xcmis.renditions;

import junit.framework.TestCase;

import org.xcmis.renditions.impl.ImageRenditionProvider;
import org.xcmis.renditions.impl.PDFDocumentRenditionProvider;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.RenditionContentStream;
import org.xcmis.spi.utils.MimeType;

import java.io.InputStream;

public class RenditionsTest extends TestCase
{

   private String pdfname = "081111.pdf";

   private String jpgname = "test.jpg";

   private String kind = "cmis:thumbnail";

   @Override
   public void setUp() throws Exception
   {
   }

   public void testPDF()
   {
      try
      {
         InputStream pdf = Thread.currentThread().getContextClassLoader().getResourceAsStream(pdfname);
         PDFDocumentRenditionProvider prov = new PDFDocumentRenditionProvider();
         BaseContentStream stream = new BaseContentStream(pdf, pdfname, new MimeType("application", "pdf"));
         RenditionContentStream out = prov.getRenditionStream(stream);
         assertNotNull(out);
         assertNotNull(out.getStream());
         assertEquals(kind, out.getKind());
         pdf.close();
      }
      catch (java.io.IOException ex)
      {
         fail();
      }
   }

   public void testJPG()
   {
      try
      {
         InputStream jpg = getClass().getResource("/" + jpgname).openStream();
         ImageRenditionProvider prov = new ImageRenditionProvider();
         BaseContentStream stream = new BaseContentStream(jpg, jpgname, new MimeType("image","jpg"));
         RenditionContentStream out = prov.getRenditionStream(stream);
         assertNotNull(out);
         assertNotNull(out.getStream());
         assertEquals(kind, out.getKind());
         jpg.close();
      }
      catch (java.io.IOException ex)
      {
         fail();
      }
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
   }
}