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

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.impl.PropertyFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ObjectDataImpl implements ObjectData
{

   protected final Node data;

   protected final CmisTypeDefinitionType type;
   
   

   public ObjectDataImpl(Node data, CmisTypeDefinitionType type) throws RepositoryException
   {
      this.data = data;
      this.type = type;
   }

   public void accept(CmisVisitor visitor)
   {
      visitor.visit(this);
   }

   public void applyPolicy(ObjectData policy)
   {
      // TODO Auto-generated method stub

   }

   public CmisAccessControlListType getAcl(boolean onlyBasicPermissions)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public EnumBaseObjectTypeIds getBaseType()
   {
      return type.getBaseId();
   }

   public Boolean getBoolean(String name)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public Boolean[] getBooleans(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getChangeToken()
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

   public Calendar getDate(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar[] getDates(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigDecimal getDecimal(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigDecimal[] getDecimals(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getHTML(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getHTMLs(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getId(String name)
   {
      try
      {
         return data.getProperty(name).getString();
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
         throw new CmisRuntimeException("Unable get ID : " + name);
      }
   }

   public String[] getIds(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigInteger getInteger(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BigInteger[] getIntegers(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLastModifiedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getLatsModificationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

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

   public ObjectData getParent()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<ObjectData> getParents()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<ObjectData> getPolicies()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Map<String, CmisProperty> getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Map<String, CmisProperty> getProperties(PropertyFilter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CmisProperty getProperty(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<ObjectData> getRelationships(EnumRelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getString(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getStrings(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getTypeId()
   {
      return type.getId();
   }

   public URI getURI(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public URI[] getURIs(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesId()
   {
      return getId(CMIS.VERSION_SERIES_ID);
   }

   public boolean isLatestMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isLatestVersion()
   {
      // TODO Auto-generated method stub
      return true;
   }

   public boolean isMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isNew()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isVersionSeriesCheckedOut()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void removePolicy(ObjectData policy)
   {
      // TODO Auto-generated method stub

   }

   public void setAcl(CmisAccessControlListType acl)
   {
      // TODO Auto-generated method stub

   }

   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   public void setProperty(CmisProperty property)
   {
      // TODO Auto-generated method stub

   }

   // 

   public Node getNode()
   {
      return data;
   }

   public void save() throws NameConstraintViolationException, StorageException
   {
      try
      {
         try
         {
            // Update properties for nodes that have required mixin.
            if (data.isNodeType(JcrCMIS.CMIS_MIX_OBJECT))
            {
               Calendar date = Calendar.getInstance();
               data.setProperty(CMIS.LAST_MODIFICATION_DATE, date);
               data.setProperty(CMIS.CHANGE_TOKEN, IdGenerator.generate());
               data.setProperty(CMIS.LAST_MODIFIED_BY, data.getSession().getUserID());
               if (data.isNew())
               {
                  data.setProperty(CMIS.CREATION_DATE, date);
                  data.setProperty(CMIS.CREATED_BY, data.getSession().getUserID());
               }
            }
            Node parent = data.getParent();
            while (parent.isNew())
               // parent also may be not saved yet if object is 'cmis:relationship'
               parent = parent.getParent();
            parent.save();
         }
         catch (ItemExistsException ie)
         {
            throw new NameConstraintViolationException("Object with name " + getName() + " already exists in "
               + data.getParent().getName(), ie);
         }
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable save object. " + re.getMessage(), re);
      }

   }

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

   public void setContentStream(ContentStream content) throws IOException, StorageException
   {
      try
      {
         long contentLength = 0;
         // jcr:content
         Node contentNode =
            data.hasNode(JcrCMIS.JCR_CONTENT) ? data.getNode(JcrCMIS.JCR_CONTENT) : data.addNode(JcrCMIS.JCR_CONTENT,
               JcrCMIS.NT_RESOURCE);

         // Assumes if there is no content then no mime-type, if any unknown
         // content then should be 'application/octet-stream'
         contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, content == null ? "" : content.getMediaType());
         // Re-count content length
         contentLength =
            contentNode.setProperty(JcrCMIS.JCR_DATA,
               content == null ? new ByteArrayInputStream(new byte[0]) : content.getStream()).getLength();
         contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());

         // Update CMIS properties
         if (!data.hasProperty(CMIS.CONTENT_STREAM_ID)) // If new node
            data.setProperty(CMIS.CONTENT_STREAM_ID, ((ExtendedNode)contentNode).getIdentifier());
         data.setProperty(CMIS.CONTENT_STREAM_LENGTH, contentLength);
         data.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, content == null ? null : content.getMediaType());
         // Do not provide file name if there is no content.
         data.setProperty(CMIS.CONTENT_STREAM_FILE_NAME, content == null ? null : getName());
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable set content. " + re.getMessage(), re);
      }
   }

   public CmisTypeDefinitionType getType()
   {
      return type;
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

   public void delete() throws StorageException
   {
      try
      {
         Node parent = data.getParent();
         data.remove();
         parent.save();
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }
   }

   @Override
   public String toString()
   {
      return "[Type: " + getTypeId() + ", name: " + getName() + ", id: " + getObjectId() + "]";
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (getClass() != other.getClass())
         return false;
      return getObjectId().equals(((ObjectDataImpl)other).getObjectId());
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + getObjectId().hashCode();
      return hash;
   }

   @Override
   public void setBoolean(String name, Boolean... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setDate(String name, Calendar... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setDecimal(String name, BigDecimal... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setHTML(String name, String... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setId(String name, String... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setInteger(String name, BigInteger... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setString(String name, String... value)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setURI(String name, URI... value)
   {
      // TODO Auto-generated method stub
      
   }

}
