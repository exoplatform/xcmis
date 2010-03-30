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

import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.spi.object.ContentStream;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CmisObject
{

   public interface Updates
   {
      Map<String, Set<String>> getAddedPermissions();

      Set<String> getAddedPolicies();

      Map<String, Set<String>> getRemovedPermissions();

      Set<String> getRemovedPolicies();

      CmisPropertiesType getUpdatedProperties();
      
      void addPermissions(String principal, Set<String> permissions);

      void removePermissions(String principal, Set<String> permissions);

      void addPolicies(Set<String> policy);

      void removePolicies(Set<String> policy);
      
      void updateProperty(CmisProperty property);
      
      void setContentStream(ContentStream contentStream, boolean overwrite);

   }

   Map<String, Set<String>> getPermissions();

   Set<String> getPolicies();

   Collection<CmisProperty> getProperties();

   String getObjectId();

   CmisTypeDefinitionType getTypeDefinition();

   Updates getUpdates();

   boolean isNew();
   
   boolean hasContent();
   
   CmisAllowableActionsType getAllowableActions();

}
