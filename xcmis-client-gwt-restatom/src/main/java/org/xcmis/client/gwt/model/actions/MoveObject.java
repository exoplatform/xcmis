/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.model.actions;

import org.xcmis.client.gwt.model.CmisExtensionType;

/**
 * @author 
 * @version $Id: 
 *
 */
public class MoveObject
{

   /**
    * Repository id.
    */
   protected String repositoryId;

   /**
    * Object id.
    */
   protected String objectId;

   /**
    * Target folder id.
    */
   protected String targetFolderId;

   /**
    * Source folder id.
    */
   protected String sourceFolderId;

   /**
    * Extension.
    */
   protected CmisExtensionType extension;

   /**
    * Gets the value of the repositoryId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getRepositoryId()
   {
      return repositoryId;
   }

   /**
    * Sets the value of the repositoryId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setRepositoryId(String value)
   {
      this.repositoryId = value;
   }

   /**
    * Gets the value of the objectId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getObjectId()
   {
      return objectId;
   }

   /**
    * Sets the value of the objectId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setObjectId(String value)
   {
      this.objectId = value;
   }

   /**
    * Gets the value of the targetFolderId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getTargetFolderId()
   {
      return targetFolderId;
   }

   /**
    * Sets the value of the targetFolderId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setTargetFolderId(String value)
   {
      this.targetFolderId = value;
   }

   /**
    * Gets the value of the sourceFolderId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getSourceFolderId()
   {
      return sourceFolderId;
   }

   /**
    * Sets the value of the sourceFolderId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setSourceFolderId(String value)
   {
      this.sourceFolderId = value;
   }

   /**
   * @return {@link CmisExtensionType}
   */
   public CmisExtensionType getExtension()
   {
      return extension;
   }

   /**
   * @param value value
   */
   public void setExtension(CmisExtensionType value)
   {
      this.extension = value;
   }

}
