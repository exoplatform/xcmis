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

package org.xcmis.restatom;

import org.xcmis.core.AccessControlService;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.MultifilingService;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisRestApplicationSingle.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CmisRestApplicationSingle extends Application
{

   /** The singleton. */
   private Set<Object> singleton = new HashSet<Object>();

   /**
    * Instantiates a new cmis rest application single.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param navigationService the navigation service
    * @param relationshipService the relationship service
    * @param policyService the policy service
    * @param aclService the acl service
    * @param queryService the query service
    * @param multifilingService the multifiling service
    * @param versioningService the versioning service
    */
   public CmisRestApplicationSingle(RepositoryService repositoryService, ObjectService objectService,
      NavigationService navigationService, RelationshipService relationshipService, PolicyService policyService,
      AccessControlService aclService, DiscoveryService queryService, MultifilingService multifilingService,
      VersioningService versioningService)
   {
      singleton.add(new AtomCmisService(//
         repositoryService, //
         objectService, //
         navigationService, //
         relationshipService, //
         policyService, //
         aclService, //
         queryService, //
         multifilingService, //
         versioningService));
      singleton.add(new AbderaResponseEntityProvider());
      singleton.add(new AbderaElementEntityProvider());
   }

   /**
    * {@inheritDoc}
    */
   public Set<Class<?>> getClasses()
   {
      return Collections.emptySet();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Object> getSingletons()
   {
      return singleton;
   }

}
