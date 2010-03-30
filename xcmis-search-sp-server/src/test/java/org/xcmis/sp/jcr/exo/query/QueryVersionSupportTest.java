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

package org.xcmis.sp.jcr.exo.query;

import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.VersionSeries;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: QueryVersionSupportTest.java 27 2010-02-08 07:49:20Z andrew00x $
 */
public class QueryVersionSupportTest extends BaseQueryTest
{

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   /**
    * Query must return all versions of documents that fulfill condition.
    * 
    * @throws Exception
    */
   public void testAllVersions() throws Exception
   {
      String contentType = "text/plain";
      String propertyBooster = "exo:Booster";
      String propertyCommander = "exo:Commander";

      Entry folder = createFolder(root, "folder");

      Entry doc1 = createDocument(folder.getObjectId(), "Apollo 7", new byte[0], contentType);
      doc1.setString(propertyCommander, "Walter M. Schirra");
      doc1.setString(propertyBooster, "Saturn 1B");
      doc1.save();

      Entry doc2 = createDocument(folder.getObjectId(), "Apollo 8", new byte[0], contentType);
      doc2.setString(propertyCommander, "Frank F. Borman, II");
      doc2.setString(propertyBooster, "Saturn V");
      doc2.save();

      Entry doc3 = createDocument(folder.getObjectId(), "Apollo 13", new byte[0], contentType);
      doc3.setString(propertyCommander, "James A. Lovell, Jr.");
      doc3.setString(propertyBooster, "Saturn V");
      doc3.save();

      // make few versions

      String docId = doc2.getObjectId();
      VersionSeries vs1 = cmisRepository.getVersionSeries(doc2.getVersionSeriesId());

      Entry pwc1 = vs1.checkout(docId);

      pwc1.setString(propertyBooster, "Saturn V second version");
      pwc1.save();

      // XXX : no search in version history 
      //            Entry ver = vs1.checkin(true, "comment");

      String statement =
         "SELECT * FROM " + JcrCMIS.CMIS_MIX_DOCUMENT + " WHERE (exo:Commander = 'Frank F. Borman, II' )";

      Query query = new Query(statement, true);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);

      assertEquals(2, result.size());

      checkResult(result, new Entry[]{doc2, pwc1});
   }

   //   public void testLastVersion() throws Exception
   //   {
   //      String contentType = "text/plain";
   //      String propertyBooster = "exo:Booster";
   //      String propertyCommander = "exo:Commander";
   //
   //      EntryImpl folder = createFolder(root, "folder");
   //      folder.save();
   //
   //      Entry doc1 = createDocument(folder.getNode(), "Apollo 7", new byte[0], contentType);
   //      doc1.setString(propertyCommander, "Walter M. Schirra");
   //      doc1.setString(propertyBooster, "Saturn 1B");
   //      doc1.save();
   //
   //      EntryImpl doc2 = createDocument(folder.getNode(), "Apollo 8", new byte[0], contentType);
   //      doc2.setString(propertyCommander, "Frank F. Borman, II");
   //      doc2.setString(propertyBooster, "Saturn V");
   //      doc2.save();
   //
   //      EntryImpl doc3 = createDocument(folder.getNode(), "Apollo 13", new byte[0], contentType);
   //      doc3.setString(propertyCommander, "James A. Lovell, Jr.");
   //      doc3.setString(propertyBooster, "Saturn V");
   //      doc3.save();
   //
   //      // make few versions
   //
   //      String docId = doc2.getObjectId();
   //      VersionSeries vs1 = cmisRepository.getVersionSeries(doc2.getVersionSeriesId());
   //      
   //      Entry pwc = vs1.checkout(docId);
   //
   //      pwc.setString(propertyBooster, "Saturn V second version");
   //      pwc.save();
   //
   //      vs1.checkin(false, "comment");
   //
   //      String statement =
   //         "SELECT * FROM " + JcrCMIS.NT_CMIS_DOCUMENT + " WHERE (exo:Commander = 'Frank F. Borman, II' )";
   //
   //      Query query = new Query(statement, true);
   //      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
   //
   //      assertEquals(1, result.size());
   //
   //      checkResult(result, new Entry[]{doc2});
   //   }

}
