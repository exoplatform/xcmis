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

package org.xcmis.client.gwt.model.acl;

import org.xcmis.client.gwt.model.EnumACLPropagation;
import org.xcmis.client.gwt.model.permission.CmisPermissionDefinition;
import org.xcmis.client.gwt.model.permission.CmisPermissionMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class ACLCapabilityType
{

   /**
    * Propagation.
    */
   protected EnumACLPropagation propagation;

   /**
    * Permissions.
    */
   protected List<CmisPermissionDefinition> permissions;

   /**
    * Mapping.
    */
   protected List<CmisPermissionMapping> mapping;

   /**
    * Gets the value of the propagation property.
    * 
    * @return
    *     possible object is
    *     {@link EnumACLPropagation }
    *     
    */
   public EnumACLPropagation getPropagation()
   {
      return propagation;
   }

   /**
    * Sets the value of the propagation property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumACLPropagation }
    *     
    */
   public void setPropagation(EnumACLPropagation value)
   {
      this.propagation = value;
   }

   /**
    * Gets the value of the permissions property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the permissions property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getPermissions().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisPermissionDefinition }
    *  
    * @return List containing {@link CmisPermissionDefinition}
    * 
    */
   public List<CmisPermissionDefinition> getPermissions()
   {
      if (permissions == null)
      {
         permissions = new ArrayList<CmisPermissionDefinition>();
      }
      return this.permissions;
   }

   /**
    * Gets the value of the mapping property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the mapping property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getMapping().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisPermissionMapping }
    * 
    * @return List containing {@link CmisPermissionMapping}
    * 
    */
   public List<CmisPermissionMapping> getMapping()
   {
      if (mapping == null)
      {
         mapping = new ArrayList<CmisPermissionMapping>();
      }
      return this.mapping;
   }

}
