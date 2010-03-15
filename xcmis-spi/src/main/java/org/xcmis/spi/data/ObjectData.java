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

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Property;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ObjectData.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface ObjectData
{

   void accept(CmisVisitor visitor);

   // ACL

   void setACL(List<AccessControlEntry> acl);

   List<AccessControlEntry> getACL(boolean onlyBasicPermissions);

   // Policies

   void applyPolicy(PolicyData policy);

   Collection<PolicyData> getPolicies();

   void removePolicy(PolicyData policy);

   //
   boolean isNew();

   // 

   BaseType getBaseType();

   String getChangeToken();

   String getCreatedBy();

   Calendar getCreationDate();

   String getLastModifiedBy();

   Calendar getLatsModificationDate();

   String getName();

   String getObjectId();

   FolderData getParent();

   Collection<FolderData> getParents();

   ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes);

   String getTypeId();

   TypeDefinition getTypeDefinition();

   void setName(String name) throws NameConstraintViolationException;

   // Properties

   Map<String, Property<?>> getProperties();

   Boolean getBoolean(String id);

   Boolean[] getBooleans(String id);

   Calendar getDate(String id);

   Calendar[] getDates(String id);

   BigDecimal getDecimal(String id);

   BigDecimal[] getDecimals(String id);

   String getHTML(String id);

   String[] getHTMLs(String id);

   String getId(String id);

   String[] getIds(String id);

   BigInteger getInteger(String id);

   BigInteger[] getIntegers(String id);

   String getString(String id);

   String[] getStrings(String id);

   URI getURI(String id);

   URI[] getURIs(String id);

   // 

   void setProperties(Map<String, Property<?>> properties);

   void setBoolean(String id, Boolean value);

   void setBooleans(String id, Boolean[] value);

   void setDate(String id, Calendar value);

   void setDates(String id, Calendar[] value);

   void setDecimal(String id, BigDecimal value);

   void setDecimals(String id, BigDecimal[] value);

   void setHTML(String id, String value);

   void setHTMLs(String id, String[] value);

   void setIds(String id, String value);

   void setIds(String id, String[] value);

   void setInteger(String id, BigInteger value);

   void setIntegers(String id, BigInteger[] value);

   void setString(String id, String value);

   void setStrings(String id, String[] value);

   void setURI(String id, URI value);

   void setURIs(String id, URI[] value);

   //

   ContentStream getContentStream(String streamId);

}
