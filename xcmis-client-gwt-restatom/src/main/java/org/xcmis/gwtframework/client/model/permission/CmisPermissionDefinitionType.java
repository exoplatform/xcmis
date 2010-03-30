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
 * Java class for cmisPermissionDefinitionType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;cmisPermissionDefinitionType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;description&quot; type=&quot;
 *             {http://www.w3.org/2001/XMLSchema}language&quot; maxOccurs=&quot;unbounded&quot; 
 *             minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;aggregating&quot; type=&quot;
 *             {http://docs.oasis-open.org/ns/cmis/core/200901}cmisPermissionDefinitionType&quot; 
 *             maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;aggregated&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; /&gt;
 *       &lt;attribute name=&quot;abstract&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public class CmisPermissionDefinitionType
{

   /**
    * Description.
    */
   protected List<String> description;

   /**
    * Aggregating.
    */
   protected List<CmisPermissionDefinitionType> aggregating;

   /**
    * Name.
    */
   protected String name;

   /**
    * Aggregated.
    */
   protected Boolean aggregated;

   /**
    * Abstract.
    */
   protected Boolean isAbstract;

   /**
    * Gets the value of the description property. For example, to add a new
    * item, do as follows:
    * 
    * <pre>
    * getDescription().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link String }
    * 
    * @return List containing {@link String}
    */
   public List<String> getDescription()
   {
      if (description == null)
      {
         description = new ArrayList<String>();
      }
      return this.description;
   }

   /**
    * Gets the value of the aggregating property.
    * 
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getAggregating().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisPermissionDefinitionType }
    * 
    * @return List containing {@link CmisPermissionDefinitionType} 
    */
   public List<CmisPermissionDefinitionType> getAggregating()
   {
      if (aggregating == null)
      {
         aggregating = new ArrayList<CmisPermissionDefinitionType>();
      }
      return this.aggregating;
   }

   /**
    * Gets the value of the name property.
    * 
    * @return possible object is {@link String }
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets the value of the name property.
    * 
    * @param value
    *            allowed object is {@link String }
    */
   public void setName(String value)
   {
      this.name = value;
   }

   /**
    * Gets the value of the aggregated property.
    * 
    * @return possible object is {@link Boolean }
    */
   public Boolean isAggregated()
   {
      return aggregated;
   }

   /**
    * Sets the value of the aggregated property.
    * 
    * @param value
    *            allowed object is {@link Boolean }
    */
   public void setAggregated(Boolean value)
   {
      this.aggregated = value;
   }

   /**
    * Gets the value of the abstract property.
    * 
    * @return possible object is {@link Boolean }
    */
   public Boolean isAbstract()
   {
      return isAbstract;
   }

   /**
    * Sets the value of the abstract property.
    * 
    * @param value
    *            allowed object is {@link Boolean }
    */
   public void setAbstract(Boolean value)
   {
      this.isAbstract = value;
   }

}
