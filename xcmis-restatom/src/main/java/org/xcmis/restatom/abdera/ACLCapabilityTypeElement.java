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

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.PermissionMapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ACLCapabilityTypeElement.java 2 2010-02-04 17:21:49Z andrew00x
 *          $
 */
public class ACLCapabilityTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new aCL capability type element.
    *
    * @param internal the internal
    */
   public ACLCapabilityTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new aCL capability type element.
    *
    * @param factory the factory
    * @param qname the qname
    */
   public ACLCapabilityTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    *
    * @param aclCapability the acl capability
    */
   public void build(ACLCapability aclCapability)
   {
      if (aclCapability != null)
      {
         if (aclCapability.getPropagation() != null)
         {
            addSimpleExtension(AtomCMIS.PROPAGATION, aclCapability.getPropagation().value());
         }
         if (aclCapability.getSupportedPermissions() != null)
         {
            addSimpleExtension(AtomCMIS.SUPPORTED_PERMISSIONS, aclCapability.getSupportedPermissions().value());
         }

         List<Permission> listPermission = aclCapability.getPermissions();
         if (listPermission != null && listPermission.size() > 0)
         {
            for (Permission permission : listPermission)
            {
               PermissionDefinitionElement permissionDefinitionElement = addExtension(AtomCMIS.PERMISSIONS);
               permissionDefinitionElement.build(permission);
            }
         }

         PermissionMapping permissionMapping = aclCapability.getMapping();
         if (permissionMapping != null && permissionMapping.getAll().size() > 0)
         {
            for (Map.Entry<String, Collection<String>> e : permissionMapping.getAll().entrySet())
            {
               if (e.getValue() != null && e.getValue().size() > 0)
               {
                  PermissionMappingElement me = addExtension(AtomCMIS.MAPPING);
                  me.build(e.getKey(), e.getValue());

               }
            }
         }
      }
   }
}
