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

package org.xcmis.gwtframework.client.model.acl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisAccessControlEntryType
{

   /**
    * Principal.
    */
   protected CmisAccessControlPrincipalType principal;

   /**
    * Permission.
    */
   protected List<String> permission;

   /**
    * Direct.
    */
   protected boolean direct;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Gets the value of the principal property.
    * 
    * @return
    *     possible object is
    *     {@link CmisAccessControlPrincipalType }
    *     
    */
   public CmisAccessControlPrincipalType getPrincipal()
   {
      return principal;
   }

   /**
    * Sets the value of the principal property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisAccessControlPrincipalType }
    *     
    */
   public void setPrincipal(CmisAccessControlPrincipalType value)
   {
      this.principal = value;
   }

   /**
    * Gets the value of the permission property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the permission property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getPermission().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link String }
    * 
    * @return List containg String
    * 
    */
   public List<String> getPermission()
   {
      if (permission == null)
      {
         permission = new ArrayList<String>();
      }
      return this.permission;
   }

   /**
    * Gets the value of the direct property.
    * 
    * @return boolean
    */
   public boolean isDirect()
   {
      return direct;
   }

   /**
    * Sets the value of the direct property.
    * 
    * @param value value
    */
   public void setDirect(boolean value)
   {
      this.direct = value;
   }

   /**
    * Gets the value of the any property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the any property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getAny().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link Element }
    * {@link Object }
    * 
    * @return List containing Object
    * 
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

}
