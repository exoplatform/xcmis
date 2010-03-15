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

import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResultImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ResultImpl implements Result
{

   private final String id;

   private final String[] properties;

   private final Score score;

   public ResultImpl(String id, String[] properties, Score score)
   {
      this.id = id;
      this.properties = properties;
      this.score = score;
   }

   public String[] getPropertyNames()
   {
      return properties;
   }

   public String getObjectId()
   {
      return id;
   }

   public Score getScore()
   {
      return score;
   }

}
