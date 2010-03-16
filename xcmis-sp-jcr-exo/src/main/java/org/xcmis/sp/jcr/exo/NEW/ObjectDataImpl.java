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

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.impl.PropertyFilter;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.PropertyData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ObjectDataImpl implements ObjectData, PropertyData
{

   protected final Node data;

   protected final TypeDefinition type;

   public ObjectDataImpl(Node data, TypeDefinition type) throws RepositoryException
   {
      this.data = data;
      this.type = type;
   }

   /**
    * {@inheritDoc}
    */
   public void accept(CmisVisitor visitor)
   {
      visitor.visit(this);
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      try
      {
         return ((ExtendedNode)data).getIdentifier();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object ID. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getTypeId()
   {
      return type.getId();
   }

   /**
    * {@inheritDoc}
    */
   public BaseType getBaseType()
   {
      return type.getBaseId();
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      try
      {
         return data.getName();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object name. " + re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition()
   {
      return type;
   }

   //
   
   public PropertyData getPropertyData()
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      try
      {
         if (!data.isNodeType(JcrCMIS.NT_FILE))
            return null; // TODO : Rendition stream for Folders
         ExtendedNode contentNode = (ExtendedNode)data.getNode(JcrCMIS.JCR_CONTENT);
         if (streamId == null || streamId.equals(contentNode.getIdentifier()))
         {
            // Main content
            String contentType = contentNode.getProperty(JcrCMIS.JCR_MIMETYPE).getString();
            // Assumes if empty content type then no content. Content type should
            // be at least 'application/octet-stream' if it can't be determined
            // but never empty string.
            if (contentType.length() == 0)
               return null;
            return new BaseContentStream(contentNode.getProperty(JcrCMIS.JCR_DATA).getStream(), getName(), contentType);
         }
         // TODO : renditions!!!
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get content stream. " + re.getMessage(), re);
      }
   }

   // Policies
   
   @Override
   public void applyPolicy(PolicyData policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removePolicy(PolicyData policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public Collection<PolicyData> getPolicies()
   {
      // TODO Auto-generated method stub
      return null;
   }

   // ACL
   
   @Override
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setACL(List<AccessControlEntry> acl) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   // Relationship
   
   @Override
   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getChangeToken()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getCreatedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Calendar getCreationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getLastModifiedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Calendar getLatsModificationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public FolderData getParent() throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Collection<FolderData> getParents()
   {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public boolean isNew()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public Boolean getBoolean(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Boolean[] getBooleans(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Calendar getDate(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Calendar[] getDates(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public BigDecimal getDecimal(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public BigDecimal[] getDecimals(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getHTML(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String[] getHTMLs(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getId(String id)
   {
      try
      {
         return data.getProperty(id).getString();
      }
      catch (ValueFormatException e)
      {
         return null;
      }
      catch (PathNotFoundException e)
      {
         return null;
      }
      catch (RepositoryException e)
      {
         throw new CmisRuntimeException("Unable get ID : " + id);
      }
   }

   @Override
   public String[] getIds(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public BigInteger getInteger(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public BigInteger[] getIntegers(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, Property<?>> getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Property<?> getProperty(String id)
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
   public PropertyData getSubset(PropertyFilter filter)
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
   public void setBoolean(String id, Boolean value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setBooleans(String id, Boolean[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDate(String id, Calendar value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDates(String id, Calendar[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDecimal(String id, BigDecimal value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setDecimals(String id, BigDecimal[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setHTML(String id, String value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setHTMLs(String id, String[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setIds(String id, String value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setIds(String id, String[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setInteger(String id, BigInteger value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setIntegers(String id, BigInteger[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setProperties(Map<String, Property<?>> properties) throws ConstraintException,
      NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setString(String id, String value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setStrings(String id, String[] value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setURI(String id, URI value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setURIs(String id, URI[] value)
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (getClass() != other.getClass())
         return false;
      return getObjectId().equals(((ObjectDataImpl)other).getObjectId());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + getObjectId().hashCode();
      return hash;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "[Type: " + getTypeId() + ", name: " + getName() + ", id: " + getObjectId() + "]";
   }

   // Implementation specific 

   public Node getNode()
   {
      return data;
   }

   public String getPath()
   {
      try
      {
         return data.getPath();
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object path. " + re.getMessage(), re);
      }
   }

   //   public void delete() throws StorageException
   //   {
   //      try
   //      {
   //         Node parent = data.getParent();
   //         data.remove();
   //         parent.save();
   //      }
   //      catch (RepositoryException re)
   //      {
   //         throw new StorageException("Unable delete object. " + re.getMessage(), re);
   //      }
   //   }

   //   public void save() throws NameConstraintViolationException, StorageException
   //   {
   //      try
   //      {
   //         try
   //         {
   //            // Update properties for nodes that have required mixin.
   //            if (data.isNodeType(JcrCMIS.CMIS_MIX_OBJECT))
   //            {
   //               Calendar date = Calendar.getInstance();
   //               data.setProperty(CMIS.LAST_MODIFICATION_DATE, date);
   //               data.setProperty(CMIS.CHANGE_TOKEN, IdGenerator.generate());
   //               data.setProperty(CMIS.LAST_MODIFIED_BY, data.getSession().getUserID());
   //               if (data.isNew())
   //               {
   //                  data.setProperty(CMIS.CREATION_DATE, date);
   //                  data.setProperty(CMIS.CREATED_BY, data.getSession().getUserID());
   //               }
   //            }
   //            Node parent = data.getParent();
   //            while (parent.isNew())
   //               // parent also may be not saved yet if object is 'cmis:relationship'
   //               parent = parent.getParent();
   //            parent.save();
   //         }
   //         catch (ItemExistsException ie)
   //         {
   //            throw new NameConstraintViolationException("Object with name " + getName() + " already exists in "
   //               + data.getParent().getName(), ie);
   //         }
   //      }
   //      catch (RepositoryException re)
   //      {
   //         throw new StorageException("Unable save object. " + re.getMessage(), re);
   //      }
   //
   //   }

   //   public void setContentStream(ContentStream content) throws IOException, StorageException
   //   {
   //      try
   //      {
   //         long contentLength = 0;
   //         // jcr:content
   //         Node contentNode =
   //            data.hasNode(JcrCMIS.JCR_CONTENT) ? data.getNode(JcrCMIS.JCR_CONTENT) : data.addNode(JcrCMIS.JCR_CONTENT,
   //               JcrCMIS.NT_RESOURCE);
   //
   //         // Assumes if there is no content then no mime-type, if any unknown
   //         // content then should be 'application/octet-stream'
   //         contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, content == null ? "" : content.getMediaType());
   //         // Re-count content length
   //         contentLength =
   //            contentNode.setProperty(JcrCMIS.JCR_DATA,
   //               content == null ? new ByteArrayInputStream(new byte[0]) : content.getStream()).getLength();
   //         contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
   //
   //         // Update CMIS properties
   //         if (!data.hasProperty(CMIS.CONTENT_STREAM_ID)) // If new node
   //            data.setProperty(CMIS.CONTENT_STREAM_ID, ((ExtendedNode)contentNode).getIdentifier());
   //         data.setProperty(CMIS.CONTENT_STREAM_LENGTH, contentLength);
   //         data.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, content == null ? null : content.getMediaType());
   //         // Do not provide file name if there is no content.
   //         data.setProperty(CMIS.CONTENT_STREAM_FILE_NAME, content == null ? null : getName());
   //      }
   //      catch (RepositoryException re)
   //      {
   //         throw new StorageException("Unable set content. " + re.getMessage(), re);
   //      }
   //   }
}
