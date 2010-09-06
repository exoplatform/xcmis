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
import org.xcmis.client.gwt.model.acl.AccessControlList;
import org.xcmis.client.gwt.model.property.CmisProperties;
import org.xcmis.client.gwt.model.property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CreatePolicy
{

   /**
    * Repository id.
    */
   protected String repositoryId;

   /**
    * Properties.
    */
   protected CmisProperties properties;

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
   protected AccessControlList addACEs;

   /**
    * Remove ACEs.
    */
   protected AccessControlList removeACEs;

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
   public CmisProperties getProperties()
   {
      if (properties == null)
      {
         properties = new CmisProperties(new HashMap<String, Property<?>>());
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
   public void setProperties(CmisProperties value)
   {
      this.properties = value;
   }

   /**
   * @return String
   */
   public String getFolderId()
   {
      return folderId;
   }

   /**
   * @param value value
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
   * @return {@link AccessControlList}
   */
   public AccessControlList getAddACEs()
   {
      return addACEs;
   }

   /**
   * @param value value
   */
   public void setAddACEs(AccessControlList value)
   {
      this.addACEs = value;
   }

   /**
   * @return {@link AccessControlList}
   */
   public AccessControlList getRemoveACEs()
   {
      return removeACEs;
   }

   /**
   * @param value value
   */
   public void setRemoveACEs(AccessControlList value)
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
