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
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RepositoryCapabilitiesTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new repository capabilities type element.
    * 
    * @param internal the internal
    */
   public RepositoryCapabilitiesTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new repository capabilities type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public RepositoryCapabilitiesTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param capabilities the capabilities
    */
   public void build(CmisRepositoryCapabilitiesType capabilities)
   {
      if (capabilities != null)
      {
         if (capabilities.getCapabilityACL() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_ACL, capabilities.getCapabilityACL().value());
         addSimpleExtension(AtomCMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE, Boolean.toString(capabilities
            .isCapabilityAllVersionsSearchable()));
         if (capabilities.getCapabilityChanges() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_CHANGES, capabilities.getCapabilityChanges().value());
         if (capabilities.getCapabilityContentStreamUpdatability() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_CONTENT_STREAM_UPDATABILITY, capabilities
               .getCapabilityContentStreamUpdatability().value());
         addSimpleExtension(AtomCMIS.CAPABILITY_GET_DESCENDANTS, Boolean.toString(capabilities
            .isCapabilityGetDescendants()));
         addSimpleExtension(AtomCMIS.CAPABILITY_GET_FOLDER_TREE, Boolean.toString(capabilities
            .isCapabilityGetFolderTree()));
         addSimpleExtension(AtomCMIS.CAPABILITY_MULTIFILLING, Boolean.toString(capabilities.isCapabilityMultifiling()));
         addSimpleExtension(AtomCMIS.CAPABILITY_PWC_SEARCHABLE, Boolean.toString(capabilities
            .isCapabilityPWCSearchable()));
         addSimpleExtension(AtomCMIS.CAPABILITY_PWC_UPDATEABLE, Boolean.toString(capabilities
            .isCapabilityPWCUpdatable()));
         if (capabilities.getCapabilityQuery() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_QUERY, capabilities.getCapabilityQuery().value());
         if (capabilities.getCapabilityRenditions() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_RENDITIONS, capabilities.getCapabilityRenditions().value());
         addSimpleExtension(AtomCMIS.CAPABILITY_UNFILING, Boolean.toString(capabilities.isCapabilityUnfiling()));
         addSimpleExtension(AtomCMIS.CAPABILITY_VERSION_SPECIFIC_FILING, Boolean.toString(capabilities
            .isCapabilityVersionSpecificFiling()));
         if (capabilities.getCapabilityJoin() != null)
            addSimpleExtension(AtomCMIS.CAPABILITY_JOIN, capabilities.getCapabilityJoin().value());
      }
   }

}
