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

import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id$
 */
public class IncludedInSupertypeQueryTest extends BaseQueryTest
{

   public void testTwoDocTypes() throws Exception
   {
      // create data
      EntryImpl folder1 = this.createFolder(root, "folder1");

      EntryImpl doc1 = createDocument(folder1.getObjectId(), "node1", "hello world".getBytes(), "text/plain");
      EntryImpl doc2 =
         (EntryImpl)folder1.createChild(cmisRepository.getTypeDefinition("cmis:article"), "node2",
            EnumVersioningState.MAJOR);
      doc2.setContent(new BaseContentStream(" hello world".getBytes(), null, "text/plain"));
      doc2.save();
      String stat = "SELECT * FROM " + JcrCMIS.NT_CMIS_DOCUMENT + " WHERE IN_FOLDER( '" + folder1.getObjectId() + "')";

      Query query = new Query(stat, false);
      ItemsIterator<Result> result = cmisRepository.getQueryHandler().handleQuery(query);
      
      // check results
      checkResult(result, new Entry[]{doc1, doc2});
   }

}
