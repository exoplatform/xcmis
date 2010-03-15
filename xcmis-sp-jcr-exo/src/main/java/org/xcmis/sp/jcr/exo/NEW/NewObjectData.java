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

package org.xcmis.sp.jcr.exo.NEW;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.impl.PropertyFilter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class NewObjectData implements ObjectData
{

   private final ObjectData parent;

   private final CmisTypeDefinitionType type;

   private final Map<String, PropertyData<Object>> properties = new HashMap<String, PropertyData<Object>>();

   private Collection<ObjectData> policies;

   private CmisAccessControlListType acl;

   public NewObjectData(ObjectData parent, CmisTypeDefinitionType type)
   {
      this.parent = parent;
      this.type = type;
   }

   public void accept(CmisVisitor visitor)
   {
      throw new UnsupportedOperationException("accept");
   }

   public void applyPolicy(ObjectData policy)
   {
      getPolicies().add(policy);
   }

   public CmisAccessControlListType getAcl(boolean onlyBasicPermissions)
   {
      return acl;
   }

   public EnumBaseObjectTypeIds getBaseType()
   {
      return type.getBaseId();
   }

   public Boolean getBoolean(String id)
   {
      return getValue(id, EnumPropertyType.BOOLEAN);
   }

   public Boolean[] getBooleans(String id)
   {
      return getValues(id, EnumPropertyType.BOOLEAN);
   }

   public String getChangeToken()
   {
      return null;
   }

   public String getCreatedBy()
   {
      return null;
   }

   public Calendar getCreationDate()
   {
      return null;
   }

   public Calendar getDate(String id)
   {
      return getValue(id, EnumPropertyType.DATETIME);
   }

   public Calendar[] getDates(String id)
   {
      return getValues(id, EnumPropertyType.DATETIME);
   }

   public BigDecimal getDecimal(String id)
   {
      return getValue(id, EnumPropertyType.DECIMAL);
   }

   public BigDecimal[] getDecimals(String id)
   {
      return getValues(id, EnumPropertyType.DECIMAL);
   }

   public String getHTML(String id)
   {
      return getValue(id, EnumPropertyType.HTML);
   }

   public String[] getHTMLs(String id)
   {
      return getValues(id, EnumPropertyType.HTML);
   }

   public String getId(String id)
   {
      return getValue(id, EnumPropertyType.ID);
   }

   public String[] getIds(String id)
   {
      return getValues(id, EnumPropertyType.ID);
   }

   public BigInteger getInteger(String id)
   {
      return getValue(id, EnumPropertyType.INTEGER);
   }

   public BigInteger[] getIntegers(String id)
   {
      return getValues(id, EnumPropertyType.INTEGER);
   }

   public String getLastModifiedBy()
   {
      return null;
   }

   public Calendar getLatsModificationDate()
   {
      return null;
   }

   public String getName()
   {
      return getString(CMIS.NAME);
   }

   public String getObjectId()
   {
      // No id for new object.
      return null;
   }

   public ObjectData getParent()
   {
      return parent;
   }

   public Collection<ObjectData> getParents()
   {
      return Collections.singleton(parent);
   }

   public Collection<ObjectData> getPolicies()
   {
      if (policies == null)
         policies = new ArrayList<ObjectData>();
      return policies;
   }

   @Override
   public Map<String, CmisProperty> getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, CmisProperty> getProperties(PropertyFilter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public CmisProperty getProperty(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ItemsIterator<ObjectData> getRelationships(EnumRelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getString(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String[] getStrings(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getTypeId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public URI getURI(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public URI[] getURIs(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getVersionLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getVersionSeriesCheckedOutBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getVersionSeriesCheckedOutId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getVersionSeriesId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isLatestMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isLatestVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isNew()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isVersionSeriesCheckedOut()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void removePolicy(ObjectData policy)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setAcl(CmisAccessControlListType acl)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setBoolean(String id, Boolean... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDate(String id, Calendar... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDecimal(String id, BigDecimal... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setHTML(String id, String... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setId(String id, String... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setInteger(String id, BigInteger... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setProperty(CmisProperty property)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setString(String id, String... value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setURI(String id, URI... value)
   {
      // TODO Auto-generated method stub

   }

   private <T> T getValue(String id, EnumPropertyType type)
   {
      T[] values = getValues(id, type);
      if (values != null && values.length > 0)
         return values[0];
      return null;
   }

   private <T> T[] getValues(String id, EnumPropertyType type)
   {
      PropertyData<T> propertyData =  (PropertyData<T>)properties.get(id);
      if (propertyData != null && propertyData.getPropertyType() == type)
         return (T[])propertyData.getValue();
      return null;
   }
   
}
