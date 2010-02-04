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
import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisPermissionDefinition;
import org.xcmis.core.CmisPermissionMapping;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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
   public void build(CmisACLCapabilityType aclCapability)
   {
      if (aclCapability != null)
      {
         if (aclCapability.getPropagation() != null)
            addSimpleExtension(AtomCMIS.PROPAGATION, aclCapability.getPropagation().value());
         if (aclCapability.getSupportedPermissions() != null)
            addSimpleExtension(AtomCMIS.SUPPORTED_PERMISSIONS, aclCapability.getSupportedPermissions().value());

         List<CmisPermissionDefinition> listCmisPermissionDefinition = aclCapability.getPermissions();
         if (listCmisPermissionDefinition != null && listCmisPermissionDefinition.size() > 0)
         {
            for (CmisPermissionDefinition cmisPermissionDefinition : listCmisPermissionDefinition)
            {
               PermissionDefinitionElement permissionDefinition = addExtension(AtomCMIS.PERMISSIONS);
               permissionDefinition.build(cmisPermissionDefinition);
            }
         }

         List<CmisPermissionMapping> listCmisPermissionMapping = aclCapability.getMapping();
         if (listCmisPermissionMapping != null && listCmisPermissionMapping.size() > 0)
         {
            for (CmisPermissionMapping cmisPermissionMapping : listCmisPermissionMapping)
            {
               PermissionMappingElement permissionMapping = addExtension(AtomCMIS.MAPPING);
               permissionMapping.build(cmisPermissionMapping);
            }
         }
      }
   }
}
