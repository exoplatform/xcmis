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
import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;
import org.xcmis.gwtframework.client.util.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 
 * @version $Id: 
 *
 */
public class Query
{

   /**
    * Repository id.
    */
   protected String repositoryId;

   /**
    * Statement.
    */
   protected String statement;

   /**
    * Search all versions.
    */
   protected boolean searchAllVersions;

   /**
    * Include allowable actions.
    */
   protected boolean includeAllowableActions;

   /**
    * Include relationships.
    */
   protected EnumIncludeRelationships includeRelationships;

   /**
    * Rendition filter.
    */
   protected String renditionFilter;

   /**
    * Max items.
    */
   protected Long maxItems;

   /**
    * Skip count.
    */
   protected Long skipCount;

   /**
    * Extension.
    */
   protected CmisExtensionType extension;

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
    * Gets the value of the statement property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getStatement()
   {
      return statement;
   }

   /**
    * Sets the value of the statement property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setStatement(String value)
   {
      this.statement = value;
   }

   /**
   * @return boolean
   */
   public boolean getSearchAllVersions()
   {
      return searchAllVersions;
   }

   /**
   * @param value value
   */
   public void setSearchAllVersions(boolean value)
   {
      this.searchAllVersions = value;
   }

   /**
   * @return boolean
   */
   public boolean getIncludeAllowableActions()
   {
      return includeAllowableActions;
   }

   /**
   * @param value value
   */
   public void setIncludeAllowableActions(boolean value)
   {
      this.includeAllowableActions = value;
   }

   /**
   * @return {@link EnumIncludeRelationships}
   */
   public EnumIncludeRelationships getIncludeRelationships()
   {
      return includeRelationships;
   }

   /**
   * @param value value
   */
   public void setIncludeRelationships(EnumIncludeRelationships value)
   {
      this.includeRelationships = value;
   }

   /**
   * @return String
   */
   public String getRenditionFilter()
   {
      return renditionFilter;
   }

   /**
   * @param value value
   */
   public void setRenditionFilter(String value)
   {
      this.renditionFilter = value;
   }

   /**
   * @return Long
   */
   public Long getMaxItems()
   {
      return maxItems;
   }

   /**
    * @param value value
    */
   public void setMaxItems(Long value)
   {
      this.maxItems = value;
   }

   /**
   * @return {@link Long}
   */
   public Long getSkipCount()
   {
      return skipCount;
   }

   /**
   * @param value value
   */
   public void setSkipCount(Long value)
   {
      this.skipCount = value;
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

   /**
   * @return Map
   */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
