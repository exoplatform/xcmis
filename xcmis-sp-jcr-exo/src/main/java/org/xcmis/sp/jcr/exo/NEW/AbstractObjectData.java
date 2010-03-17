/**
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

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.Property;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class AbstractObjectData implements ObjectData, Properties
{

   protected final Node node;
   
   protected final TypeDefinition type;
   
   public AbstractObjectData(Node node, TypeDefinition type)
   {
      this.node = node;
      this.type = type;
   }

   public void accept(CmisVisitor visitor)
   {
      visitor.visit(this);
   }

   public void applyPolicy(PolicyData policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BaseType getBaseType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getChangeToken()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ContentStream getContentStream(String streamId)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getCreatedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getCreationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getLastModificationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLastModifiedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getObjectId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public FolderData getParent() throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<FolderData> getParents()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<PolicyData> getPolicies()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Properties getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public TypeDefinition getTypeDefinition()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getTypeId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isNew()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void removePolicy(PolicyData policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void setACL(List<AccessControlEntry> acl) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   // Properties
   
   public Map<String, Property<?>> getAll()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Boolean getBoolean(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Boolean[] getBooleans(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getDate(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar[] getDates(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigDecimal getDecimal(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigDecimal[] getDecimals(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getHTML(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getHTMLs(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getId(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getIds(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigInteger getInteger(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigInteger[] getIntegers(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Property<?> getProperty(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getString(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getStrings(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Map<String, Property<?>> getSubset(PropertyFilter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public URI getURI(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public URI[] getURIs(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void setBoolean(String id, Boolean value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setBooleans(String id, Boolean[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setDate(String id, Calendar value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setDates(String id, Calendar[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setDecimal(String id, BigDecimal value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setDecimals(String id, BigDecimal[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setHTML(String id, String value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setHTMLs(String id, String[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setIds(String id, String value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setIds(String id, String[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setInteger(String id, BigInteger value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setIntegers(String id, BigInteger[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setString(String id, String value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setStrings(String id, String[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setURI(String id, URI value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setURIs(String id, URI[] value)
   {
      // TODO Auto-generated method stub
      
   }

   public void setValues(Map<String, Property<?>> properties) throws ConstraintException,
      NameConstraintViolationException
   {
      // TODO Auto-generated method stub
      
   }
   
   // --------------- Implementation -------------------
   
   public void delete() throws StorageException
   {
      try
      {
         Node parent = node.getParent();
         node.remove();
         parent.save();
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }
   }

}
