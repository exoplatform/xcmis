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

package org.xcmis.spi.data;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.impl.PropertyFilter;
import org.xcmis.spi.ItemsIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface ObjectData
{

   void accept(CmisVisitor visitor);

   // ACL

   void setAcl(CmisAccessControlListType acl);

   CmisAccessControlListType getAcl(boolean onlyBasicPermissions);

   // Policies

   void applyPolicy(ObjectData policy);

   Collection<ObjectData> getPolicies();

   void removePolicy(ObjectData policy);

   //
   boolean isNew();
   // 

   EnumBaseObjectTypeIds getBaseType();

   String getChangeToken();

   String getCreatedBy();

   Calendar getCreationDate();

   String getLastModifiedBy();

   Calendar getLatsModificationDate();

   String getName();

   String getObjectId();

   ObjectData getParent();

   Collection<ObjectData> getParents();

   ItemsIterator<ObjectData> getRelationships(EnumRelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes);

   String getTypeId();

   String getVersionLabel();

   String getVersionSeriesCheckedOutBy();

   String getVersionSeriesCheckedOutId();

   String getVersionSeriesId();

   boolean isLatestMajorVersion();

   boolean isLatestVersion();

   boolean isMajorVersion();

   boolean isVersionSeriesCheckedOut();

   void setName(String name) throws NameConstraintViolationException;

   // Properties

   Map<String, CmisProperty> getProperties();

   Map<String, CmisProperty> getProperties(PropertyFilter filter);

   CmisProperty getProperty(String name);
   
   void setProperty(CmisProperty property);

   boolean getBoolean(String name);

   boolean[] getBooleans(String name);

   Calendar getDate(String name);

   Calendar[] getDates(String name);

   BigDecimal getDecimal(String name);

   BigDecimal[] getDecimals(String name);

   String getHTML(String name);

   String[] getHTMLs(String name);

   String getId(String name);

   String[] getIds(String name);

   BigInteger getInteger(String name);

   BigInteger[] getIntegers(String name);

   String getString(String name);

   String[] getStrings(String name);

   URI getURI(String name);

   URI[] getURIs(String name);

}
