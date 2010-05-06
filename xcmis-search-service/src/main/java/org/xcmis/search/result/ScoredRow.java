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
package org.xcmis.search.result;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.SearchServiceException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: ScoredRow.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ScoredRow
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   private final Map<String, String> nodesMap;

   private final float score;

   /**
    * @param nodesMap
    * @param score
    */
   public ScoredRow(Map<String, String> nodesMap, float score)
   {
      super();
      this.nodesMap = nodesMap;
      this.score = score;
   }

   public ScoredRow(String selectorName, String nodeIdentifer, float score)
   {
      super();
      this.nodesMap = new LinkedHashMap<String, String>();
      this.score = score;
      this.nodesMap.put(selectorName, nodeIdentifer);
   }

   public void addNode(String selectorName, String nodeIdentifer)
   {
      this.nodesMap.put(selectorName, nodeIdentifer);
   }

   public int getLength()
   {
      return nodesMap.size();
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Score:" + score + " nodes" + nodesMap;
   }

   /**
    * {@inheritDoc}
    */
   public String getNodeIdentifer(String selectorName)
   {
      return nodesMap.get(selectorName);
   }

   public float getScore()
   {
      return score;
   }

   public String[] getSelectorNames()
   {
      String[] result = new String[nodesMap.size()];
      nodesMap.keySet().toArray(result);
      return result;
   }

   public static ScoredRow merge(ScoredRow nodes1, ScoredRow nodes2, float newscore) throws SearchServiceException
   {
      Map<String, String> newNodesMap = new LinkedHashMap<String, String>();
      String[] names1 = nodes1.getSelectorNames();
      for (int i = 0; i < names1.length; i++)
      {
         newNodesMap.put(names1[i], nodes1.getNodeIdentifer(names1[i]));
      }
      String[] names2 = nodes2.getSelectorNames();
      for (int i = 0; i < names1.length; i++)
      {
         newNodesMap.put(names2[i], nodes2.getNodeIdentifer(names2[i]));
      }
      return new ScoredRow(newNodesMap, newscore);
   }
}
