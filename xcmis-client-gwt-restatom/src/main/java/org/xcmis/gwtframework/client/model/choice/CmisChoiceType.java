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

package org.xcmis.gwtframework.client.model.choice;

import org.xcmis.gwtframework.client.util.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Java class for cmisChoiceType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;cmisChoiceType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref=&quot;{http://docs.oasis-open.org/ns/cmis/core/200901}choice&quot; 
 *                     maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref=&quot;{http://docs.oasis-open.org/ns/cmis/core/200901}cmisUndefinedAttribute&quot;/&gt;
 *       &lt;attribute ref=&quot;{http://docs.oasis-open.org/ns/cmis/core/200901}key&quot;/&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public abstract class CmisChoiceType
{

   /**
    * Choice.
    */
   protected List<CmisChoiceType> choice;

   /**
    * Key.
    */
   protected String key;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the choice property.
    * <p>
    * This accessor method returns a reference to the live list, not a
    * snapshot. Therefore any modification you make to the returned list will
    * be present inside the JAXB object. This is why there is not a
    * <CODE>set</CODE> method for the choice property.
    * <p>
    * For example, to add a new item, do as follows:
    * 
    * @return List containing {@link CmisChoiceType} 
    * 
    */
   public List<CmisChoiceType> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceType>();
      }
      return this.choice;
   }

   /**
    * Gets the value of the key property.
    * 
    * @return possible object is {@link String }
    */
   public String getKey()
   {
      return key;
   }

   /**
    * Sets the value of the key property.
    * 
    * @param value
    *            allowed object is {@link String }
    */
   public void setKey(String value)
   {
      this.key = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed
    * property on this class.
    * <p>
    * the map is keyed by the name of the attribute and the value is the string
    * value of the attribute. the map returned by this method is live, and you
    * can add new attribute by updating the map directly. Because of this
    * design, there's no setter.
    * 
    * @return always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
