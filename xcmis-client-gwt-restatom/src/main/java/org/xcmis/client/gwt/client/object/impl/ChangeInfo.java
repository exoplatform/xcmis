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

package org.xcmis.client.gwt.client.object.impl;

import org.xcmis.client.gwt.client.model.EnumTypeOfChanges;

import java.util.Date;

/**
 * Simple plain implementation of {@link ChangeInfo}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ChangeInfo
{

   /** Change time. */
   private Date changeTime;

   /** Change type. */
   private EnumTypeOfChanges changeType;

   /**
    * Constructor
    */
   public ChangeInfo()
   {
   }

   /**
    * @param changeTime change time
    * @param changeType 
    */
   public ChangeInfo(Date changeTime, EnumTypeOfChanges changeType)
   {
      this.changeTime = changeTime;
      this.changeType = changeType;
   }

 
   /**
    * @return {@link Date}
    */
   public Date getChangeTime()
   {
      return changeTime;
   }

   /**
    * {@inheritDoc}
    */
   public EnumTypeOfChanges getChangeType()
   {
      return changeType;
   }

   /**
    * Set date of change.
    * 
    * @param changeTime date of change
    */
   public void setChangeTime(Date changeTime)
   {
      this.changeTime = changeTime;
   }

   /**
    * Set type of change.
    * 
    * @param changeType change type
    */
   public void setChangeType(EnumTypeOfChanges changeType)
   {
      this.changeType = changeType;
   }

}
