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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.query.Result;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.PathNotFoundException;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: BaseQueryTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseQueryTest extends BaseTest
{
   private static final Log LOG = ExoLogger.getLogger(BaseQueryTest.class.getName());

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   protected void checkResult(ItemsIterator<Result> result, String[] columns, String[][] expectedResults)
      throws Exception
   {
      // expected
      Set<String> expectedPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }
      for (int i = 0; i < expectedResults.length; i++)
      {
         String newRow = "";
         for (int j = 0; j < expectedResults[i].length; j++)
         {
            newRow += "|" + expectedResults[i][j];
         }
         expectedPaths.add(newRow);
         if (LOG.isDebugEnabled())
         {
            LOG.debug(newRow);
         }
      }
      // actual

      Set<String> resultPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         String objectId = result.next().getObjectId();
         String resultRow = "";
         for (String column : columns)
         {
            resultRow += "|" + getProperty(objectId, column);

         }
         resultPaths.add(resultRow);
         if (LOG.isDebugEnabled())
         {
            LOG.debug(resultRow);
         }
      }

      checkResults(expectedPaths, resultPaths);
   }

   protected void checkResult(ItemsIterator<Result> result, Entry[] nodes)
   {
      // collect rows
      Set<String> expectedPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }

      for (Entry node : nodes)
      {
         expectedPaths.add(node.getObjectId());
         if (LOG.isDebugEnabled())
         {
            LOG.debug(node.getObjectId());
         }
      }

      Set<String> resultPaths = new HashSet<String>();
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         String id = result.next().getObjectId();
         resultPaths.add(id);
         if (LOG.isDebugEnabled())
         {
            LOG.debug(id);
         }
      }

      checkResults(expectedPaths, resultPaths);
   }

   protected void checkResultOrder(ItemsIterator<Result> result, Entry[] nodes)
   {
      // collect rows
      String expectedPaths = "";
      if (LOG.isDebugEnabled())
      {
         LOG.debug("expected:");
      }

      for (Entry node : nodes)
      {
         expectedPaths += node.getObjectId();
         if (LOG.isDebugEnabled())
         {
            LOG.debug(node.getObjectId());
         }
      }

      String resultPaths = "";
      if (LOG.isDebugEnabled())
      {
         LOG.debug("result:");
      }
      while (result.hasNext())
      {
         String id = result.next().getObjectId();
         resultPaths += id;
         if (LOG.isDebugEnabled())
         {
            LOG.debug(id);
         }
      }

      assertEquals(expectedPaths, resultPaths);
   }

   private void checkResults(Set<String> expectedRowResults, Set<String> actualRowResults)
   {
      // check if all expected are in result
      for (String path : expectedRowResults)
      {
         assertTrue(path + " is not part of the result set " + actualRowResults, actualRowResults.contains(path));
      }
      // check result does not contain more than expected
      for (String path : actualRowResults)
      {
         assertTrue(path + " is not expected to be part of the result set", expectedRowResults.contains(path));
      }
   }

   private String getProperty(String objectId, String propertyId) throws Exception
   {
      EntryImpl entry = (EntryImpl)cmisRepository.getObjectById(objectId);
      try
      {
         return entry.getNode().getProperty(propertyId).getString();
      }
      catch (PathNotFoundException e)
      {
         return null;
      }
   }

}
