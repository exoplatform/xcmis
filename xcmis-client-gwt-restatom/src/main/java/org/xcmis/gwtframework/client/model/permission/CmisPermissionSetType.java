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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for cmisPermissionSetType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;cmisPermissionSetType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;permission&quot; type=&quot;
 *                {http://docs.oasis-open.org/ns/cmis/core/200901}cmisPermissionDefinitionType&quot; 
 *                maxOccurs=&quot;unbounded&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public class CmisPermissionSetType
{

   /**
    * Permission.
    */
   protected List<CmisPermissionDefinitionType> permission;

   /**
    * Gets the value of the permission property.
    * 
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getPermission().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisPermissionDefinitionType }
    * 
    * @return List containing {@link CmisPermissionDefinitionType}
    */
   public List<CmisPermissionDefinitionType> getPermission()
   {
      if (permission == null)
      {
         permission = new ArrayList<CmisPermissionDefinitionType>();
      }
      return this.permission;
   }

}
