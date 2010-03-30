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

package org.xcmis.gwtframework.client.model;

import org.xcmis.gwtframework.client.util.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id:
 *
 */
public class CmisAllowableActionsType
{

   /**
    * Can delete object.
    */
   protected Boolean canDeleteObject;

   /**
    * Can update properties.
    */
   protected Boolean canUpdateProperties;

   /**
    * Can get folder tree.
    */
   protected Boolean canGetFolderTree;

   /**
    * Can get properties.
    */
   protected Boolean canGetProperties;

   /**
    * Can get object relationships.
    */
   protected Boolean canGetObjectRelationships;

   /**
    * Can get object parents.
    */
   protected Boolean canGetObjectParents;

   /**
    * Can get folder parent.
    */
   protected Boolean canGetFolderParent;

   /**
    * Can get descendants.
    */
   protected Boolean canGetDescendants;

   /**
    * Can move object.
    */
   protected Boolean canMoveObject;

   /**
    * Can delete content stream.
    */
   protected Boolean canDeleteContentStream;

   /**
    * Can check out.
    */
   protected Boolean canCheckOut;

   /**
    * Can cancel check out.
    */
   protected Boolean canCancelCheckOut;

   /**
    * Can check in.
    */
   protected Boolean canCheckIn;

   /**
    * Can set content stream.
    */
   protected Boolean canSetContentStream;

   /**
    * Can get all versions.
    */
   protected Boolean canGetAllVersions;

   /**
    * Can add object to folder.
    */
   protected Boolean canAddObjectToFolder;

   /**
    * Can remove object from folder.
    */
   protected Boolean canRemoveObjectFromFolder;

   /**
    * Can get content stream.
    */
   protected Boolean canGetContentStream;

   /**
    * Can apply policy.
    */
   protected Boolean canApplyPolicy;

   /**
    * Can get applied policies.
    */
   protected Boolean canGetAppliedPolicies;

   /**
    * Can remove policy.
    */
   protected Boolean canRemovePolicy;

   /**
    * Can get children.
    */
   protected Boolean canGetChildren;

   /**
    * Can create document.
    */
   protected Boolean canCreateDocument;

   /**
    * Can create folder.
    */
   protected Boolean canCreateFolder;

   /**
    * Can create relationship.
    */
   protected Boolean canCreateRelationship;

   /**
    * Can delete tree.
    */
   protected Boolean canDeleteTree;

   /**
    * Can get renditions.
    */
   protected Boolean canGetRenditions;

   /**
    * Can get ACL.
    */
   protected Boolean canGetACL;

   /**
    * Can apply ACL.
    */
   protected Boolean canApplyACL;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Map other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the canDeleteObject property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanDeleteObject()
   {
      return canDeleteObject;
   }

   /**
    * Sets the value of the canDeleteObject property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanDeleteObject(Boolean value)
   {
      this.canDeleteObject = value;
   }

   /**
    * Gets the value of the canUpdateProperties property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanUpdateProperties()
   {
      return canUpdateProperties;
   }

   /**
    * Sets the value of the canUpdateProperties property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanUpdateProperties(Boolean value)
   {
      this.canUpdateProperties = value;
   }

   /**
    * Gets the value of the canGetFolderTree property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetFolderTree()
   {
      return canGetFolderTree;
   }

   /**
    * Sets the value of the canGetFolderTree property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetFolderTree(Boolean value)
   {
      this.canGetFolderTree = value;
   }

   /**
    * Gets the value of the canGetProperties property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetProperties()
   {
      return canGetProperties;
   }

   /**
    * Sets the value of the canGetProperties property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetProperties(Boolean value)
   {
      this.canGetProperties = value;
   }

   /**
    * Gets the value of the canGetObjectRelationships property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetObjectRelationships()
   {
      return canGetObjectRelationships;
   }

   /**
    * Sets the value of the canGetObjectRelationships property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetObjectRelationships(Boolean value)
   {
      this.canGetObjectRelationships = value;
   }

   /**
    * Gets the value of the canGetObjectParents property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetObjectParents()
   {
      return canGetObjectParents;
   }

   /**
    * Sets the value of the canGetObjectParents property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetObjectParents(Boolean value)
   {
      this.canGetObjectParents = value;
   }

   /**
    * Gets the value of the canGetFolderParent property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetFolderParent()
   {
      return canGetFolderParent;
   }

   /**
    * Sets the value of the canGetFolderParent property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetFolderParent(Boolean value)
   {
      this.canGetFolderParent = value;
   }

   /**
    * Gets the value of the canGetDescendants property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetDescendants()
   {
      return canGetDescendants;
   }

   /**
    * Sets the value of the canGetDescendants property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetDescendants(Boolean value)
   {
      this.canGetDescendants = value;
   }

   /**
    * Gets the value of the canMoveObject property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanMoveObject()
   {
      return canMoveObject;
   }

   /**
    * Sets the value of the canMoveObject property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanMoveObject(Boolean value)
   {
      this.canMoveObject = value;
   }

   /**
    * Gets the value of the canDeleteContentStream property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanDeleteContentStream()
   {
      return canDeleteContentStream;
   }

   /**
    * Sets the value of the canDeleteContentStream property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanDeleteContentStream(Boolean value)
   {
      this.canDeleteContentStream = value;
   }

   /**
    * Gets the value of the canCheckOut property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCheckOut()
   {
      return canCheckOut;
   }

   /**
    * Sets the value of the canCheckOut property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCheckOut(Boolean value)
   {
      this.canCheckOut = value;
   }

   /**
    * Gets the value of the canCancelCheckOut property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCancelCheckOut()
   {
      return canCancelCheckOut;
   }

   /**
    * Sets the value of the canCancelCheckOut property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCancelCheckOut(Boolean value)
   {
      this.canCancelCheckOut = value;
   }

   /**
    * Gets the value of the canCheckIn property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCheckIn()
   {
      return canCheckIn;
   }

   /**
    * Sets the value of the canCheckIn property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCheckIn(Boolean value)
   {
      this.canCheckIn = value;
   }

   /**
    * Gets the value of the canSetContentStream property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanSetContentStream()
   {
      return canSetContentStream;
   }

   /**
    * Sets the value of the canSetContentStream property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanSetContentStream(Boolean value)
   {
      this.canSetContentStream = value;
   }

   /**
    * Gets the value of the canGetAllVersions property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetAllVersions()
   {
      return canGetAllVersions;
   }

   /**
    * Sets the value of the canGetAllVersions property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetAllVersions(Boolean value)
   {
      this.canGetAllVersions = value;
   }

   /**
    * Gets the value of the canAddObjectToFolder property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanAddObjectToFolder()
   {
      return canAddObjectToFolder;
   }

   /**
    * Sets the value of the canAddObjectToFolder property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanAddObjectToFolder(Boolean value)
   {
      this.canAddObjectToFolder = value;
   }

   /**
    * Gets the value of the canRemoveObjectFromFolder property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanRemoveObjectFromFolder()
   {
      return canRemoveObjectFromFolder;
   }

   /**
    * Sets the value of the canRemoveObjectFromFolder property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanRemoveObjectFromFolder(Boolean value)
   {
      this.canRemoveObjectFromFolder = value;
   }

   /**
    * Gets the value of the canGetContentStream property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetContentStream()
   {
      return canGetContentStream;
   }

   /**
    * Sets the value of the canGetContentStream property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetContentStream(Boolean value)
   {
      this.canGetContentStream = value;
   }

   /**
    * Gets the value of the canApplyPolicy property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanApplyPolicy()
   {
      return canApplyPolicy;
   }

   /**
    * Sets the value of the canApplyPolicy property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanApplyPolicy(Boolean value)
   {
      this.canApplyPolicy = value;
   }

   /**
    * Gets the value of the canGetAppliedPolicies property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetAppliedPolicies()
   {
      return canGetAppliedPolicies;
   }

   /**
    * Sets the value of the canGetAppliedPolicies property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetAppliedPolicies(Boolean value)
   {
      this.canGetAppliedPolicies = value;
   }

   /**
    * Gets the value of the canRemovePolicy property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanRemovePolicy()
   {
      return canRemovePolicy;
   }

   /**
    * Sets the value of the canRemovePolicy property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanRemovePolicy(Boolean value)
   {
      this.canRemovePolicy = value;
   }

   /**
    * Gets the value of the canGetChildren property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetChildren()
   {
      return canGetChildren;
   }

   /**
    * Sets the value of the canGetChildren property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetChildren(Boolean value)
   {
      this.canGetChildren = value;
   }

   /**
    * Gets the value of the canCreateDocument property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCreateDocument()
   {
      return canCreateDocument;
   }

   /**
    * Sets the value of the canCreateDocument property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCreateDocument(Boolean value)
   {
      this.canCreateDocument = value;
   }

   /**
    * Gets the value of the canCreateFolder property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCreateFolder()
   {
      return canCreateFolder;
   }

   /**
    * Sets the value of the canCreateFolder property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCreateFolder(Boolean value)
   {
      this.canCreateFolder = value;
   }

   /**
    * Gets the value of the canCreateRelationship property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanCreateRelationship()
   {
      return canCreateRelationship;
   }

   /**
    * Sets the value of the canCreateRelationship property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanCreateRelationship(Boolean value)
   {
      this.canCreateRelationship = value;
   }

   /**
    * Gets the value of the canDeleteTree property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanDeleteTree()
   {
      return canDeleteTree;
   }

   /**
    * Sets the value of the canDeleteTree property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanDeleteTree(Boolean value)
   {
      this.canDeleteTree = value;
   }

   /**
    * Gets the value of the canGetRenditions property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetRenditions()
   {
      return canGetRenditions;
   }

   /**
    * Sets the value of the canGetRenditions property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetRenditions(Boolean value)
   {
      this.canGetRenditions = value;
   }

   /**
    * Gets the value of the canGetACL property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanGetACL()
   {
      return canGetACL;
   }

   /**
    * Sets the value of the canGetACL property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanGetACL(Boolean value)
   {
      this.canGetACL = value;
   }

   /**
    * Gets the value of the canApplyACL property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public Boolean isCanApplyACL()
   {
      return canApplyACL;
   }

   /**
    * Sets the value of the canApplyACL property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public void setCanApplyACL(Boolean value)
   {
      this.canApplyACL = value;
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
    * {@link Object }
    * {@link Element }
    * 
    * @return a list containing Object 
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

}
