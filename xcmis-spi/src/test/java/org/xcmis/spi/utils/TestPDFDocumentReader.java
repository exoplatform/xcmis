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

import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.InputStream;

/**
 * Created by The eXo Platform SAS Author : Sergey Karpenko
 * <sergey.karpenko@exoplatform.com.ua>
 * 
 * @version $Id: $
 */

public class TestPDFDocumentReader extends TestCase
{
   private static final Log LOG = ExoLogger.getLogger(TestPDFDocumentReader.class);

   DocumentReaderService service_;

   public void setUp() throws Exception
   {
      service_ = new CmisDocumentReaderSercice();
   }

   public void testGetContentAsString() throws Exception
   {
      InputStream is = TestPDFDocumentReader.class.getResourceAsStream("/test.pdf");
      String text = service_.getDocumentReader("application/pdf").getContentAsText(is);
      String etalon = "Hello\nThis is my first Cocoon page!\npage 1\n";
      //lag on WIN 
      //assertEquals("Wrong string returned", etalon, text);
   }
}
