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

package org.xcmis.gwtframework.client.model.actions;

import org.xcmis.gwtframework.client.model.CmisExtensionType;
import org.xcmis.gwtframework.client.model.acl.CmisAccessControlListType;
import org.xcmis.gwtframework.client.model.property.CmisPropertiesType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CreateFolder
{

   /**
    * Repository id.
    */
   protected String repositoryId;

   /**
    * Properties.
    */
   protected CmisPropertiesType properties;

   /**
    * Folder id.
    */
   protected String folderId;

   /**
    * Policies.
    */
   protected List<String> policies;

   /**
    * Add ACEs.
    */
   protected CmisAccessControlListType addACEs;

   /**
    * Remove ACEs.
    */
   protected CmisAccessControlListType removeACEs;

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
    * Gets the value of the properties property.
    * 
    * @return
    *     possible object is
    *     {@link CmisPropertiesType }
    *     
    */
   public CmisPropertiesType getProperties()
   {
      if (properties == null)
      {
         properties = new CmisPropertiesType();
      }
      return properties;
   }

   /**
    * Sets the value of the properties property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisPropertiesType }
    *     
    */
   public void setProperties(CmisPropertiesType value)
   {
      this.properties = value;
   }

   /**
    * Gets the value of the folderId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getFolderId()
   {
      return folderId;
   }

   /**
    * Sets the value of the folderId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setFolderId(String value)
   {
      this.folderId = value;
   }

   /**
   * @return List containing String
   */
   public List<String> getPolicies()
   {
      if (policies == null)
      {
         policies = new ArrayList<String>();
      }
      return this.policies;
   }

   /**
   * @return {@link CmisAccessControlListType}
   */
   public CmisAccessControlListType getAddACEs()
   {
      return addACEs;
   }

   /**
   * @param value value
   */
   public void setAddACEs(CmisAccessControlListType value)
   {
      this.addACEs = value;
   }

   /**
   * @return {@link CmisAccessControlListType}
   */
   public CmisAccessControlListType getRemoveACEs()
   {
      return removeACEs;
   }

   /**
   * @param value value
   */
   public void setRemoveACEs(CmisAccessControlListType value)
   {
      this.removeACEs = value;
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
