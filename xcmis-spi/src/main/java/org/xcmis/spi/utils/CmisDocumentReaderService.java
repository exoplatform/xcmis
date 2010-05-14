/*
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
package org.xcmis.spi.utils;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.document.DocumentReader;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.document.HandlerNotFoundException;
import org.exoplatform.services.document.impl.BaseDocumentReader;
import org.exoplatform.services.document.impl.HTMLDocumentReader;
import org.exoplatform.services.document.impl.MSExcelDocumentReader;
import org.exoplatform.services.document.impl.MSOutlookDocumentReader;
import org.exoplatform.services.document.impl.MSWordDocumentReader;
import org.exoplatform.services.document.impl.OpenOfficeDocumentReader;
import org.exoplatform.services.document.impl.PPTDocumentReader;
import org.exoplatform.services.document.impl.TextPlainDocumentReader;
import org.exoplatform.services.document.impl.XMLDocumentReader;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link DocumentReaderService} with predefined parsers.
 */
public class CmisDocumentReaderService implements DocumentReaderService
{

   private static final Log LOG = ExoLogger.getLogger(CmisDocumentReaderService.class);

   private Map<String, BaseDocumentReader> readers;

   /**
    * Instantiates a new cmis document reader sercice.
    */
   public CmisDocumentReaderService()
   {
      this.readers = new HashMap<String, BaseDocumentReader>();
      addDocumentReader(new CmisPDFDocumentReader());
      addDocumentReader(new MSWordDocumentReader());
      addDocumentReader(new MSExcelDocumentReader());
      addDocumentReader(new MSOutlookDocumentReader());
      addDocumentReader(new PPTDocumentReader());
      addDocumentReader(new HTMLDocumentReader(null));
      addDocumentReader(new XMLDocumentReader());
      addDocumentReader(new OpenOfficeDocumentReader());
      addDocumentReader(new TextPlainDocumentReader(new InitParams()));

   }

   /**
    * @see org.exoplatform.services.document.DocumentReaderService#getContentAsText(java.lang.String, java.io.InputStream)
    */
   public String getContentAsText(String mimeType, InputStream is) throws Exception
   {
      BaseDocumentReader reader = readers.get(mimeType.toLowerCase());
      if (reader != null)
      {
         return reader.getContentAsText(is);
      }
      throw new Exception("Cannot handle the document type: " + mimeType);
   }

   /**
    * @see org.exoplatform.services.document.DocumentReaderService#getDocumentReader(java.lang.String)
    */
   public DocumentReader getDocumentReader(String mimeType) throws HandlerNotFoundException
   {
      BaseDocumentReader reader = readers.get(mimeType.toLowerCase());
      if (reader != null)
      {
         return reader;
      }
      else
      {
         throw new HandlerNotFoundException("No appropriate properties extractor for " + mimeType);
      }

   }

   /**
    * @param plugin ComponentPlugin
    */
   public void addDocumentReader(ComponentPlugin plugin)
   {
      BaseDocumentReader reader = (BaseDocumentReader)plugin;
      for (String mimeType : reader.getMimeTypes())
      {
         readers.put(mimeType.toLowerCase(), reader);
      }
   }
}
