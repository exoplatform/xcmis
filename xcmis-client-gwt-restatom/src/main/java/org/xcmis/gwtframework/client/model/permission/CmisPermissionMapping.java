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

package org.xcmis.gwtframework.client.model.permission;

import org.xcmis.gwtframework.client.model.EnumAllowableActionsKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisPermissionMapping
{

   /**
    * Key.
    */
   protected EnumAllowableActionsKey key;

   /**
    * Permission.
    */
   protected List<String> permission;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Gets the value of the key property.
    * 
    * @return
    *     possible object is
    *     {@link EnumAllowableActionsKey }
    *     
    */
   public EnumAllowableActionsKey getKey()
   {
      return key;
   }

   /**
    * Sets the value of the key property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumAllowableActionsKey }
    *     
    */
   public void setKey(EnumAllowableActionsKey value)
   {
      this.key = value;
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
    * @return List containing {@link String}
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
    * @return List containing {@link Object}
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
