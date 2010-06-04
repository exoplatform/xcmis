/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.object.impl;

import org.xcmis.client.gwt.model.AllowableActions;
import org.xcmis.client.gwt.model.Rendition;
import org.xcmis.client.gwt.model.acl.AccessControlEntry;
import org.xcmis.client.gwt.model.property.CmisProperties;
import org.xcmis.client.gwt.model.property.Property;
import org.xcmis.client.gwt.object.CmisObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CmisObjectImpl implements CmisObject 
{

   private CmisProperties properties;

   private List<AccessControlEntry> acl;

   private boolean exactACL;

   private Set<String> policyIds;

   private List<CmisObjectImpl> relationships;

   private List<Rendition> renditions;

   private AllowableActions allowableActions;

   private ChangeInfo changeInfo;

   private ObjectInfo objectInfo;

   private String pathSegment;

   public CmisObjectImpl()
   {
   }

   public CmisObjectImpl(Map<String, Property<?>> properties, List<AccessControlEntry> acl, boolean exactACL, Set<String> policyIds,
      List<CmisObjectImpl> relationships, List<Rendition> renditions, AllowableActions allowableActions,
      ChangeInfo changeInfo, ObjectInfo objectInfo, String pathSegment)
   {
      this.properties = new CmisProperties(properties);
      this.acl = acl;
      this.exactACL = exactACL;
      this.policyIds = policyIds;
      this.relationships = relationships;
      this.renditions = renditions;
      this.allowableActions = allowableActions;
      this.changeInfo = changeInfo;
      this.objectInfo = objectInfo;
      this.pathSegment = pathSegment;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getACL()
    */
   public List<AccessControlEntry> getACL()
   {
      if (acl == null)
         acl = new ArrayList<AccessControlEntry>();
      return acl;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getAllowableActions()
    */
   public AllowableActions getAllowableActions()
   {
      return allowableActions;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getChangeInfo()
    */
   public ChangeInfo getChangeInfo()
   {
      return changeInfo;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getObjectInfo()
    */
   public ObjectInfo getObjectInfo()
   {
      if (objectInfo == null){
         objectInfo = new ObjectInfo();
      }
      return objectInfo;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getPathSegment()
    */
   public String getPathSegment()
   {
      return pathSegment;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getPolicyIds()
    */
   public Collection<String> getPolicyIds()
   {
      if (policyIds == null)
         policyIds = new HashSet<String>();
      return policyIds;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getProperties()
    */
   public CmisProperties getProperties()
   {
      if (properties == null)
         properties = new CmisProperties(new HashMap<String, Property<?>>());
      return properties;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setProperties(org.xcmis.client.gwt.model.property.CmisProperties)
    */
   public void setProperties(CmisProperties properties)
   {
      this.properties = properties;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getRelationship()
    */
   public List<CmisObjectImpl> getRelationship()
   {
      if (relationships == null)
         relationships = new ArrayList<CmisObjectImpl>();
      return relationships;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#getRenditions()
    */
   public List<Rendition> getRenditions()
   {
      if (renditions == null)
         renditions = new ArrayList<Rendition>();
      return renditions;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#isExactACL()
    */
   public boolean isExactACL()
   {
      return exactACL;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setAllowableActions(org.xcmis.client.gwt.model.AllowableActions)
    */
   public void setAllowableActions(AllowableActions allowableActions)
   {
      this.allowableActions = allowableActions;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setChangeInfo(org.xcmis.client.gwt.client.object.impl.ChangeInfo)
    */
   public void setChangeInfo(ChangeInfo changeInfo)
   {
      this.changeInfo = changeInfo;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setObjectInfo(org.xcmis.client.gwt.client.object.impl.ObjectInfo)
    */
   public void setObjectInfo(ObjectInfo objectInfo)
   {
      this.objectInfo = objectInfo;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setExactACL(boolean)
    */
   public void setExactACL(boolean exactACL)
   {
      this.exactACL = exactACL;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisObject#setPathSegment(java.lang.String)
    */
   public void setPathSegment(String pathSegment)
   {
      this.pathSegment = pathSegment;
   }

}
