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

package org.xcmis.core.impl.property;

import org.xcmis.core.CmisAction;
import org.xcmis.core.CmisProperty;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

import java.util.List;

/**
 * Manage object's properties.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface PropertyService
{

   /**
    * Get all properties.
    * 
    * @param cmisEntry CMIS entry to discovering properties
    * @return object's properties
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   List<CmisProperty> getProperties(Entry cmisEntry) throws RepositoryException;

   /**
    * Get object's property with specified name.
    * 
    * @param cmisEntry CMIS entry
    * @param propertyName property name
    * @return object's property
    * @throws UnsupportedPropertyException if property with specified name not
    *           found
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisProperty getProperty(Entry cmisEntry, String propertyName) throws UnsupportedPropertyException,
      RepositoryException;

   /**
    * Set single CMIS property.
    * 
    * @param cmisEntry entry
    * @param property property
    * @param cmisAction CMIS action type. It may be used to is in valid to
    *           update property at this moment.
    * @throws UnsupportedPropertyException if property is not supported by object
    *           type
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   void setProperty(Entry cmisEntry, CmisProperty property, CmisAction cmisAction) throws UnsupportedPropertyException,
      RepositoryException;

}
