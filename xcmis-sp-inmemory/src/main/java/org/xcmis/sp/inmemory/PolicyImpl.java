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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Calendar;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PolicyImpl extends BaseObjectData implements Policy
{

   public PolicyImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   public PolicyImpl(Folder parent, TypeDefinition type, StorageImpl storage)
   {
      super(parent, type, storage);
   }

   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      // TODO : check policies same names
      if (getString(CMIS.POLICY_TEXT) == null)
      {
         throw new ConstraintException("Required property 'cmis:policyText' is not set.");
      }

      if (isNew())
      {
         String id = StorageImpl.generateId();

         entry.setValue(CMIS.OBJECT_ID, //
            new StringValue(id));
         entry.setValue(CMIS.CREATED_BY, //
            new StringValue(""));
         entry.setValue(CMIS.CREATION_DATE, //
            new DateValue(Calendar.getInstance()));

         storage.policies.add(id);
         storage.parents.put(id, StorageImpl.EMPTY_PARENTS);
      }

      entry.setValue(CMIS.LAST_MODIFIED_BY, //
         new StringValue(""));
      entry.setValue(CMIS.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.CHANGE_TOKEN, //
         new StringValue(StorageImpl.generateId()));

      storage.entries.put(entry.getId(), entry);
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
   public ContentStream getContentStream(String streamId)
   {
      // no content or renditions for policy
      return null;
   }

}
