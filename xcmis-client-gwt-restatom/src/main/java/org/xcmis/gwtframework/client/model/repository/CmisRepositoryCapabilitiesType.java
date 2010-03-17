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

package org.xcmis.gwtframework.client.model.repository;

import org.xcmis.gwtframework.client.model.EnumCapabilityACL;
import org.xcmis.gwtframework.client.model.EnumCapabilityChanges;
import org.xcmis.gwtframework.client.model.EnumCapabilityContentStreamUpdates;
import org.xcmis.gwtframework.client.model.EnumCapabilityJoin;
import org.xcmis.gwtframework.client.model.EnumCapabilityQuery;
import org.xcmis.gwtframework.client.model.EnumCapabilityRendition;
import org.xcmis.gwtframework.client.util.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.xml.client.Element;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisRepositoryCapabilitiesType
{
   /**
    * Capability ACL.
    */
   protected EnumCapabilityACL capabilityACL;

   /**
    * Capability all versions searchable.
    */
   protected boolean capabilityAllVersionsSearchable;

   /**
    * Capability changes.
    */
   protected EnumCapabilityChanges capabilityChanges;

   /**
    * Capability content stream updatability.
    */
   protected EnumCapabilityContentStreamUpdates capabilityContentStreamUpdatability;

   /**
    * Capability get descendants.
    */
   protected boolean capabilityGetDescendants;

   /**
    * Capability get folder tree.
    */
   protected boolean capabilityGetFolderTree;

   /**
    * Capability multifiling.
    */
   protected boolean capabilityMultifiling;

   /**
    * Capability PWC searchable.
    */
   protected boolean capabilityPWCSearchable;

   /**
    * Capability PWC Updatable.
    */
   protected boolean capabilityPWCUpdatable;

   /**
    * Capability query.
    */
   protected EnumCapabilityQuery capabilityQuery;

   /**
    * Capability renditions.
    */
   protected EnumCapabilityRendition capabilityRenditions;

   /**
    * Capability unfiling.
    */
   protected boolean capabilityUnfiling;

   /**
    * Capability version specific filing.
    */
   protected boolean capabilityVersionSpecificFiling;

   /**
    * Capability join.
    */
   protected EnumCapabilityJoin capabilityJoin;

   /**
    * List any.
    */
   protected List<Element> any;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the capabilityACL property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityACL }
    *     
    */
   public EnumCapabilityACL getCapabilityACL()
   {
      return capabilityACL;
   }

   /**
    * Sets the value of the capabilityACL property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityACL }
    *     
    */
   public void setCapabilityACL(EnumCapabilityACL value)
   {
      this.capabilityACL = value;
   }

   /**
    * Gets the value of the capabilityAllVersionsSearchable property.
    * 
    * @return boolean
    */
   public boolean isCapabilityAllVersionsSearchable()
   {
      return capabilityAllVersionsSearchable;
   }

   /**
    * Sets the value of the capabilityAllVersionsSearchable property.
    * 
    * @param value value
    */
   public void setCapabilityAllVersionsSearchable(boolean value)
   {
      this.capabilityAllVersionsSearchable = value;
   }

   /**
    * Gets the value of the capabilityChanges property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityChanges }
    *     
    */
   public EnumCapabilityChanges getCapabilityChanges()
   {
      return capabilityChanges;
   }

   /**
    * Sets the value of the capabilityChanges property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityChanges }
    *     
    */
   public void setCapabilityChanges(EnumCapabilityChanges value)
   {
      this.capabilityChanges = value;
   }

   /**
    * Gets the value of the capabilityContentStreamUpdatability property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityContentStreamUpdates }
    *     
    */
   public EnumCapabilityContentStreamUpdates getCapabilityContentStreamUpdatability()
   {
      return capabilityContentStreamUpdatability;
   }

   /**
    * Sets the value of the capabilityContentStreamUpdatability property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityContentStreamUpdates }
    *     
    */
   public void setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates value)
   {
      this.capabilityContentStreamUpdatability = value;
   }

   /**
    * Gets the value of the capabilityGetDescendants property.
    * 
    * @return boolean
    */
   public boolean isCapabilityGetDescendants()
   {
      return capabilityGetDescendants;
   }

   /**
    * Sets the value of the capabilityGetDescendants property.
    * 
    * @param value value
    */
   public void setCapabilityGetDescendants(boolean value)
   {
      this.capabilityGetDescendants = value;
   }

   /**
    * Gets the value of the capabilityGetFolderTree property.
    * 
    * @return boolean
    */
   public boolean isCapabilityGetFolderTree()
   {
      return capabilityGetFolderTree;
   }

   /**
    * Sets the value of the capabilityGetFolderTree property.
    * 
    * @param value value
    */
   public void setCapabilityGetFolderTree(boolean value)
   {
      this.capabilityGetFolderTree = value;
   }

   /**
    * Gets the value of the capabilityMultifiling property.
    * 
    * @return boolean
    */
   public boolean isCapabilityMultifiling()
   {
      return capabilityMultifiling;
   }

   /**
    * Sets the value of the capabilityMultifiling property.
    * 
    * @param value value
    */
   public void setCapabilityMultifiling(boolean value)
   {
      this.capabilityMultifiling = value;
   }

   /**
    * Gets the value of the capabilityPWCSearchable property.
    * 
    * @return boolean
    */
   public boolean isCapabilityPWCSearchable()
   {
      return capabilityPWCSearchable;
   }

   /**
    * Sets the value of the capabilityPWCSearchable property.
    * 
    * @param value value
    */
   public void setCapabilityPWCSearchable(boolean value)
   {
      this.capabilityPWCSearchable = value;
   }

   /**
    * Gets the value of the capabilityPWCUpdatable property.
    * 
    * @return boolean
    */
   public boolean isCapabilityPWCUpdatable()
   {
      return capabilityPWCUpdatable;
   }

   /**
    * Sets the value of the capabilityPWCUpdatable property.
    * 
    * @param value value
    */
   public void setCapabilityPWCUpdatable(boolean value)
   {
      this.capabilityPWCUpdatable = value;
   }

   /**
    * Gets the value of the capabilityQuery property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityQuery }
    *     
    */
   public EnumCapabilityQuery getCapabilityQuery()
   {
      return capabilityQuery;
   }

   /**
    * Sets the value of the capabilityQuery property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityQuery }
    *     
    */
   public void setCapabilityQuery(EnumCapabilityQuery value)
   {
      this.capabilityQuery = value;
   }

   /**
    * Gets the value of the capabilityRenditions property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityRendition }
    *     
    */
   public EnumCapabilityRendition getCapabilityRenditions()
   {
      return capabilityRenditions;
   }

   /**
    * Sets the value of the capabilityRenditions property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityRendition }
    *     
    */
   public void setCapabilityRenditions(EnumCapabilityRendition value)
   {
      this.capabilityRenditions = value;
   }

   /**
    * Gets the value of the capabilityUnfiling property.
    * 
    * @return boolean
    */
   public boolean isCapabilityUnfiling()
   {
      return capabilityUnfiling;
   }

   /**
    * Sets the value of the capabilityUnfiling property.
    * 
    * @param value value
    */
   public void setCapabilityUnfiling(boolean value)
   {
      this.capabilityUnfiling = value;
   }

   /**
    * Gets the value of the capabilityVersionSpecificFiling property.
    * 
    * @return boolean
    */
   public boolean isCapabilityVersionSpecificFiling()
   {
      return capabilityVersionSpecificFiling;
   }

   /**
    * Sets the value of the capabilityVersionSpecificFiling property.
    * 
    * @param value value
    */
   public void setCapabilityVersionSpecificFiling(boolean value)
   {
      this.capabilityVersionSpecificFiling = value;
   }

   /**
    * Gets the value of the capabilityJoin property.
    * 
    * @return
    *     possible object is
    *     {@link EnumCapabilityJoin }
    *     
    */
   public EnumCapabilityJoin getCapabilityJoin()
   {
      return capabilityJoin;
   }

   /**
    * Sets the value of the capabilityJoin property.
    * 
    * @param value
    *     allowed object is
    *     {@link EnumCapabilityJoin }
    *     
    */
   public void setCapabilityJoin(EnumCapabilityJoin value)
   {
      this.capabilityJoin = value;
   }

   
   /**
    * @return List containing {@link Element}
    */
   public List<Element> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Element>();
      }
      return this.any;
   }

   
   /**
    * @return always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
