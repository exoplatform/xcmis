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
import org.xcmis.client.gwt.model.EnumACLPropagation;
import org.xcmis.client.gwt.model.acl.AccessControlList;

/**
 * @author 
 * @version $Id: 
 *
 */
public class ApplyACL
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
    * Add ACEs.
    */
   protected AccessControlList addACEs;

   /**
    * Remove ACEs.
    */
   protected AccessControlList removeACEs;

   /**
    * ACL propagation.
    */
   protected EnumACLPropagation aclPropagation;

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
    * Gets the value of the addACEs property.
    * 
    * @return
    *     possible object is
    *     {@link AccessControlList }
    *     
    */
   public AccessControlList getAddACEs()
   {
      return addACEs;
   }

   /**
    * Sets the value of the addACEs property.
    * 
    * @param value
    *     allowed object is
    *     {@link AccessControlList }
    *     
    */
   public void setAddACEs(AccessControlList value)
   {
      this.addACEs = value;
   }

   /**
    * Gets the value of the removeACEs property.
    * 
    * @return
    *     possible object is
    *     {@link AccessControlList }
    *     
    */
   public AccessControlList getRemoveACEs()
   {
      return removeACEs;
   }

   /**
    * Sets the value of the removeACEs property.
    * 
    * @param value
    *     allowed object is
    *     {@link AccessControlList }
    *     
    */
   public void setRemoveACEs(AccessControlList value)
   {
      this.removeACEs = value;
   }

   /**
    * @return {@link EnumACLPropagation}
    */
   public EnumACLPropagation getACLPropagation()
   {
      return aclPropagation;
   }

   /**
    * @param value value
    */
   public void setACLPropagation(EnumACLPropagation value)
   {
      this.aclPropagation = value;
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
