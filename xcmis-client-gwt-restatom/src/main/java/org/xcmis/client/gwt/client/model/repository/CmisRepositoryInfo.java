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

package org.xcmis.client.gwt.client.model.repository;

import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.acl.CmisACLCapabilityType;
import org.xcmis.client.gwt.client.model.restatom.EnumCollectionType;
import org.xcmis.client.gwt.client.rest.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisRepositoryInfo
{
   /**
    * Repository id.
    */
   protected String repositoryId;

   /**
    * Repository name.
    */
   protected String repositoryName;

   /**
    * Repository description.
    */
   protected String repositoryDescription;

   /**
    * Vendor name.
    */
   protected String vendorName;

   /**
    * Product name.
    */
   protected String productName;

   /**
    * Product version.
    */
   protected String productVersion;

   /**
    * Root folder id.
    */
   protected String rootFolderId;

   /**
    * Latest change log token.
    */
   protected String latestChangeLogToken;

   /**
    * Capabilities.
    */
   protected CmisRepositoryCapabilitiesType capabilities;

   /**
    * ACL capability.
    */
   protected CmisACLCapabilityType aclCapability;

   /**
    * CMIS version supported.
    */
   protected String cmisVersionSupported;

   /**
    * Thin client URI.
    */
   protected String thinClientURI;

   /**
    * Changes incomplete.
    */
   protected Boolean changesIncomplete;

   /**
    * Changes on type.
    */
   protected List<EnumBaseObjectTypeIds> changesOnType;

   /**
    * Principal anonymous.
    */
   protected String principalAnonymous;

   /**
    * Principal anyone.
    */
   protected String principalAnyone;

   /**
    * Collections.
    */
   protected List<CmisCollection> collections;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
    * Gets the value of the repositoryName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getRepositoryName()
   {
      return repositoryName;
   }

   /**
    * Sets the value of the repositoryName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setRepositoryName(String value)
   {
      this.repositoryName = value;
   }

   /**
    * Gets the value of the repositoryDescription property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getRepositoryDescription()
   {
      return repositoryDescription;
   }

   /**
    * Sets the value of the repositoryDescription property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setRepositoryDescription(String value)
   {
      this.repositoryDescription = value;
   }

   /**
    * Gets the value of the vendorName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getVendorName()
   {
      return vendorName;
   }

   /**
    * Sets the value of the vendorName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setVendorName(String value)
   {
      this.vendorName = value;
   }

   /**
    * Gets the value of the productName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getProductName()
   {
      return productName;
   }

   /**
    * Sets the value of the productName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setProductName(String value)
   {
      this.productName = value;
   }

   /**
    * Gets the value of the productVersion property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getProductVersion()
   {
      return productVersion;
   }

   /**
    * Sets the value of the productVersion property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setProductVersion(String value)
   {
      this.productVersion = value;
   }

   /**
    * Gets the value of the rootFolderId property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getRootFolderId()
   {
      return rootFolderId;
   }

   /**
    * Sets the value of the rootFolderId property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setRootFolderId(String value)
   {
      this.rootFolderId = value;
   }

   /**
    * Gets the value of the latestChangeLogToken property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getLatestChangeLogToken()
   {
      return latestChangeLogToken;
   }

   /**
    * Sets the value of the latestChangeLogToken property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setLatestChangeLogToken(String value)
   {
      this.latestChangeLogToken = value;
   }

   /**
    * Gets the value of the capabilities property.
    * 
    * @return
    *     possible object is
    *     {@link CmisRepositoryCapabilitiesType }
    *     
    */
   public CmisRepositoryCapabilitiesType getCapabilities()
   {
      return capabilities;
   }

   /**
    * Sets the value of the capabilities property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisRepositoryCapabilitiesType }
    *     
    */
   public void setCapabilities(CmisRepositoryCapabilitiesType value)
   {
      this.capabilities = value;
   }

   /**
    * Gets the value of the aclCapability property.
    * 
    * @return
    *     possible object is
    *     {@link CmisACLCapabilityType }
    *     
    */
   public CmisACLCapabilityType getAclCapability()
   {
      return aclCapability;
   }

   /**
    * Sets the value of the aclCapability property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisACLCapabilityType }
    *     
    */
   public void setAclCapability(CmisACLCapabilityType value)
   {
      this.aclCapability = value;
   }

   /**
    * Gets the value of the cmisVersionSupported property.
    * 
    * @return
    *     possible object is
    *     {@link BigDecimal }
    *     
    */
   public String getCmisVersionSupported()
   {
      return cmisVersionSupported;
   }

   /**
    * Sets the value of the cmisVersionSupported property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigDecimal }
    *     
    */
   public void setCmisVersionSupported(String value)
   {
      this.cmisVersionSupported = value;
   }

   /**
    * Gets the value of the thinClientURI property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getThinClientURI()
   {
      return thinClientURI;
   }

   /**
    * Sets the value of the thinClientURI property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setThinClientURI(String value)
   {
      this.thinClientURI = value;
   }

   /**
    * Gets the value of the changesIncomplete property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isChangesIncomplete()
   {
      return changesIncomplete;
   }

   /**
    * Sets the value of the changesIncomplete property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setChangesIncomplete(Boolean value)
   {
      this.changesIncomplete = value;
   }

   /**
    * Gets the value of the changesOnType property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the changesOnType property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getChangesOnType().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link EnumBaseObjectTypeIds }
    * 
    * @return List containing {@link EnumBaseObjectTypeIds}
    * 
    */
   public List<EnumBaseObjectTypeIds> getChangesOnType()
   {
      if (changesOnType == null)
      {
         changesOnType = new ArrayList<EnumBaseObjectTypeIds>();
      }
      return this.changesOnType;
   }

   /**
    * Gets the value of the principalAnonymous property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getPrincipalAnonymous()
   {
      return principalAnonymous;
   }

   /**
    * Sets the value of the principalAnonymous property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setPrincipalAnonymous(String value)
   {
      this.principalAnonymous = value;
   }

   /**
    * Gets the value of the principalAnyone property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getPrincipalAnyone()
   {
      return principalAnyone;
   }

   /**
    * Sets the value of the principalAnyone property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setPrincipalAnyone(String value)
   {
      this.principalAnyone = value;
   }

  
   /**
    * @return List containing {@link Object}
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and 
    * the value is the string value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return
    *     always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

   /**
    * @return List containing {@link CmisCollection}
    */
   public List<CmisCollection> getCollections()
   {
      if (collections == null)
      {
         collections = new ArrayList<CmisCollection>();
      }
      return collections;
   }

   /**
    * @param collections collections
    */
   public void setCollections(List<CmisCollection> collections)
   {
      this.collections = collections;
   }

   /**
    * @param type type
    * @return String
    */
   public String getCollectionValue(EnumCollectionType type)
   {
      for (CmisCollection collectionItem : collections)
      {
         if (collectionItem.getType().equals(type))
         {
            return collectionItem.getHref();
         }
      }
      return null;
   }

}
