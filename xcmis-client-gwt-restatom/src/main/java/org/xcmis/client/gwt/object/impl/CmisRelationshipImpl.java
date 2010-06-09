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
package org.xcmis.client.gwt.object.impl;

import java.util.HashSet;

import org.xcmis.client.gwt.object.CmisObject;
import org.xcmis.client.gwt.object.CmisRelationship;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CmisRelationshipImpl extends CmisObjectImpl implements CmisRelationship
{
   /**
    * Source object of the relationship
    */
   private String sourceId;

   /**
    * Target object of the relationship
    */
   private String targetId;
   
   public CmisRelationshipImpl(CmisObject object, String sourceId, String targetId)
   {
      super(object.getProperties().getProperties(), object.getACL(), object.isExactACL(), 
         new HashSet<String>(object.getPolicyIds()), object.getRelationship(), object.getRenditions(), 
         object.getAllowableActions(), object.getChangeInfo(), object.getObjectInfo(), 
         object.getPathSegment());
      
      this.sourceId = sourceId;
      this.targetId = targetId;
   }
   
   
   /**
    * @see org.xcmis.client.gwt.object.CmisRelationship#getSourceId()
    */
   public String getSourceId()
   {
      return sourceId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisRelationship#getTargetId()
    */
   public String getTargetId()
   {
      return targetId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisRelationship#setSourceId(java.lang.String)
    */
   public void setSourceId(String sourceId)
   {
      this.sourceId = sourceId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisRelationship#setTargetId(java.lang.String)
    */
   public void setTargetId(String targetId)
   {
      this.targetId = targetId;
   }

}
