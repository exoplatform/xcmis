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

package org.xcmis.sp.jcr.exo;

import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.object.Property;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PolicyImpl extends BaseObjectData implements Policy
{

   public PolicyImpl(TypeDefinition type, Folder parent, String name)
   {
      super(type, parent, name);
   }

   public PolicyImpl(TypeDefinition type, Node node)
   {
      super(type, node);
   }

   /**
    * {@inheritDoc}
    */
   public void delete() throws StorageException
   {
      try
      {
         // Check is policy applied to at least one object.
         for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
         {
            Node controllable = iter.nextProperty().getParent();
            if (controllable.isNodeType(JcrCMIS.NT_FILE) //
               || controllable.isNodeType(JcrCMIS.NT_FOLDER) //
               || controllable.isNodeType(JcrCMIS.CMIS_NT_POLICY))
            {
               String msg = "Unable to delete applied policy.";
               throw new ConstraintException(msg);
            }
         }

      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable delete object. " + re.getMessage(), re);
      }

      // If not applied to any object
      super.delete();
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // No renditions for Policy object.
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getPolicyText()
   {
      return getString(CMIS.POLICY_TEXT);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void save() throws StorageException, NameConstraintViolationException, UpdateConflictException
   {
      if (isNew())
      {
         try
         {
            if (name == null)
            {
               Property<?> nameProperty = properties.get(CMIS.NAME);
               if (nameProperty != null)
                  name = (String)nameProperty.getValues().get(0);
            }
            if (name == null || name.length() == 0)
               throw new NameConstraintViolationException("Name for new policy must be provided.");

            Node parentNode = ((FolderImpl)parent).getNode();

            if (parentNode.hasNode(name))
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in specified folder.");

            Node newPolicy = parentNode.addNode(name, type.getLocalName());

            //            newPolicy.setProperty(CMIS.OBJECT_ID, //
            //               ((ExtendedNode)newPolicy).getIdentifier());
            //            newPolicy.setProperty(CMIS.NAME, //
            //               name);
            newPolicy.setProperty(CMIS.OBJECT_TYPE_ID, //
               type.getId());
            newPolicy.setProperty(CMIS.BASE_TYPE_ID, //
               type.getBaseId().value());
            newPolicy.setProperty(CMIS.CREATED_BY, //
               parentNode.getSession().getUserID());
            newPolicy.setProperty(CMIS.CREATION_DATE, //
               Calendar.getInstance());
            newPolicy.setProperty(CMIS.LAST_MODIFIED_BY, //
               parentNode.getSession().getUserID());
            newPolicy.setProperty(CMIS.LAST_MODIFICATION_DATE, //
               Calendar.getInstance());

            for (Property<?> property : properties.values())
               setProperty(newPolicy, property);

            if (policies != null && policies.size() > 0)
            {
               for (Policy policy : policies)
                  applyPolicy(newPolicy, policy);
            }

            if (acl != null && acl.size() > 0)
               setACL(newPolicy, acl);

            parentNode.save();

            name = null;
            policies = null;
            acl = null;
            properties.clear();

            node = newPolicy;
         }
         catch (RepositoryException re)
         {
            throw new StorageException("Unable create new policy. " + re.getMessage(), re);
         }
      }
      else
      {
         super.save();
      }
   }

}
