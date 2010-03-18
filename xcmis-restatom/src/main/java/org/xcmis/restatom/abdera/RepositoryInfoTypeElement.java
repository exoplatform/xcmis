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
import org.xcmis.spi.BaseType;
import org.xcmis.spi.RepositoryInfo;

import java.util.Collection;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryInfoTypeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class RepositoryInfoTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new repository info type element.
    * 
    * @param internal the internal
    */
   public RepositoryInfoTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new repository info type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public RepositoryInfoTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param repoInfo the repo info
    */
   public void build(RepositoryInfo repoInfo)
   {
      if (repoInfo != null)
      {
         addSimpleExtension(AtomCMIS.REPOSITORY_ID, repoInfo.getRepositoryId());
         addSimpleExtension(AtomCMIS.REPOSITORY_NAME, repoInfo.getRepositoryName());
         addSimpleExtension(AtomCMIS.REPOSITORY_DESCRIPTION, repoInfo.getRepositoryDescription());
         addSimpleExtension(AtomCMIS.VENDOR_NAME, repoInfo.getVendorName());
         addSimpleExtension(AtomCMIS.PRODUCT_NAME, repoInfo.getProductName());
         addSimpleExtension(AtomCMIS.PRODUCT_VERSION, repoInfo.getProductVersion());
         addSimpleExtension(AtomCMIS.ROOT_FOLDER_ID, repoInfo.getRootFolderId());
         addSimpleExtension(AtomCMIS.LATEST_CHANGE_LOG_TOKEN, repoInfo.getLatestChangeLogToken());
         addSimpleExtension(AtomCMIS.CMIS_VERSION_SUPPORTED, repoInfo.getCmisVersionSupported());
         addSimpleExtension(AtomCMIS.THIN_CLIENT_URI, repoInfo.getThinClientURI());
         addSimpleExtension(AtomCMIS.CHANGES_INCOMPLETE, Boolean.toString(repoInfo.isChangesIncomplete()));
         Collection<BaseType> listChangesOnType = repoInfo.getChangesOnType();
         if (listChangesOnType != null && listChangesOnType.size() > 0)
         {
            for (BaseType baseType : listChangesOnType)
            {
               addSimpleExtension(AtomCMIS.CHANGES_ON_TYPE, baseType.value());
            }
         }
         addSimpleExtension(AtomCMIS.PRINCIPAL_ANONYMOUS, repoInfo.getPrincipalAnonymous());
         addSimpleExtension(AtomCMIS.PRINCIPAL_ANYONE, repoInfo.getPrincipalAnyone());
         RepositoryCapabilitiesTypeElement repoCb = addExtension(AtomCMIS.CAPABILITIES);
         repoCb.build(repoInfo.getCapabilities());
         ACLCapabilityTypeElement aclCb = addExtension(AtomCMIS.ACL_CAPABILITY);
         aclCb.build(repoInfo.getAclCapability());
      }
   }

}
