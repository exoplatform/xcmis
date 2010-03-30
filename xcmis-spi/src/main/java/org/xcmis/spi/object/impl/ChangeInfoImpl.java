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

package org.xcmis.spi.object.impl;

import org.xcmis.spi.ChangeType;
import org.xcmis.spi.object.ChangeInfo;

import java.util.Calendar;

/**
 * Simple plain implementation of {@link ChangeInfo}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ChangeInfoImpl implements ChangeInfo
{

   /** Change time. */
   private Calendar changeTime;

   /** Change type. */
   private ChangeType changeType;

   public ChangeInfoImpl()
   {
   }

   public ChangeInfoImpl(Calendar changeTime, ChangeType changeType)
   {
      this.changeTime = changeTime;
      this.changeType = changeType;
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getChangeTime()
   {
      return changeTime;
   }

   /**
    * {@inheritDoc}
    */
   public ChangeType getChangeType()
   {
      return changeType;
   }

   /**
    * Set date of change.
    * 
    * @param changeTime date of change
    */
   public void setChangeTime(Calendar changeTime)
   {
      this.changeTime = changeTime;
   }

   /**
    * Set type of change.
    * 
    * @param changeType change type
    */
   public void setChangeType(ChangeType changeType)
   {
      this.changeType = changeType;
   }

}
