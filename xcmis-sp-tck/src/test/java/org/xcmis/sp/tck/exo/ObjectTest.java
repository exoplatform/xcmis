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
package org.xcmis.sp.tck.exo;

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.utils.MimeType;

public class ObjectTest extends BaseTest
{
   
   /**
    * createDocument() test suite;
    * 
    */
   
   public void testCreateDocumentCheckContent() throws Exception{
      System.out.print("Running testCreateDocumentCheckContent....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try {
         String docId =   getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null, null, null, VersioningState.MAJOR);
         ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
         assertEquals(cs.getMediaType(), c.getMediaType());
         byte[] before = new byte[15];
         byte[] after =  new byte[15];
         
         cs.getStream().read(before);
         c.getStream().read(after);
         String bf = new String(before);
         String aft = new String(after);
         assertEquals(bf, aft);
         pass();
      }catch (Exception e){
         e.printStackTrace();
         doFail(e.getMessage());
      }
      
   }

}

