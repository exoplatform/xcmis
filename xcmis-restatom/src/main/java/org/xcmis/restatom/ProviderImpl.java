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

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.impl.AbstractProvider;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;
import org.xcmis.core.AccessControlService;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.MultifilingService;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.restatom.collections.AllVersionsCollection;
import org.xcmis.restatom.collections.CheckedOutCollection;
import org.xcmis.restatom.collections.FolderChildrenCollection;
import org.xcmis.restatom.collections.FolderDescentantsCollection;
import org.xcmis.restatom.collections.FolderTreeCollection;
import org.xcmis.restatom.collections.ParentsCollection;
import org.xcmis.restatom.collections.PoliciesCollection;
import org.xcmis.restatom.collections.QueryCollection;
import org.xcmis.restatom.collections.RelationshipsCollection;
import org.xcmis.restatom.collections.TypesChildrenCollection;
import org.xcmis.restatom.collections.TypesDescendantsCollection;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ProviderImpl extends AbstractProvider
{

   /** The manager. */
   private AbstractWorkspaceManager manager;

   /** The target builder. */
   private TemplateTargetBuilder targetBuilder;

   /** The resolver. */
   private RegexTargetResolver resolver;

   /**
    * Instantiates a new provider impl.
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
   public ProviderImpl(RepositoryService repositoryService, //
      ObjectService objectService, //
      NavigationService navigationService, //
      RelationshipService relationshipService, //
      PolicyService policyService, //
      AccessControlService aclService, //
      DiscoveryService queryService, //
      MultifilingService multifilingService, //
      VersioningService versioningService)
   {
      targetBuilder = new TemplateTargetBuilder();
      targetBuilder.setTemplate(TargetType.ENTRY, "{target_base}/cmisatom/{repoid}/{atomdoctype}/{id}");
      targetBuilder.setTemplate(TargetType.SERVICE, "{target_base}/cmisatom/{repoid}");
      targetBuilder.setTemplate("feed",
         "{target_base}/cmisatom/{repoid}/{atomdoctype}/{id}{-opt|?|maxItems,skipCount}{-join|&|maxItems,skipCount}");

      resolver = new RegexTargetResolver();

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/typebyid(/)?([^/?]+)?(\\??.*)?", //
         TargetType.TYPE_ENTRY, //
         "repoid", //
         "typeid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/types(/)?([^/?]+)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "slash", // No slash if 'typeid' is absent. 
         "typeid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+?)/typedescendants(/)?([^/?]+)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "slash", // No slash if 'typeid' is absent. 
         "typeid");

      resolver.setPattern("/cmisatom/([^/]+)/checkedout(/)?([^/?]+)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "slash", // No slash if 'objectid' is absent. 
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/children/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+?)/object/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_ENTRY, //
         "repoid", //
         "objectid");

      resolver.setPattern("/cmisatom/([^/]+?)/objectbypath(.?)*", //
         TargetType.TYPE_ENTRY, //
         "repoid");

      resolver.setPattern("/cmisatom/([^/]+)/parents/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/relationships/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/descendants/([^/?]+)(\\??.*)?", // 
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/cmisatom/([^/]+)/versions/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "versionSeriesId");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/foldertree/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/cmisatom/([^/]+)/query(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid"); //

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/file/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_MEDIA, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/policies/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid"); //

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/alternate/([^/?]+)/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid", //
         "streamid");

      SimpleWorkspaceInfo wInfo = new SimpleWorkspaceInfo();
      wInfo.addCollection(new FolderChildrenCollection(repositoryService, objectService, versioningService,
         navigationService));
      wInfo
         .addCollection(new ParentsCollection(repositoryService, objectService, versioningService, navigationService));
      wInfo.addCollection(new RelationshipsCollection(repositoryService, objectService, versioningService,
         relationshipService));
      wInfo.addCollection(new FolderDescentantsCollection(repositoryService, objectService, versioningService,
         navigationService));
      wInfo.addCollection(new FolderTreeCollection(repositoryService, objectService, versioningService,
         navigationService));
      wInfo.addCollection(new TypesChildrenCollection(repositoryService));
      wInfo.addCollection(new TypesDescendantsCollection(repositoryService));
      wInfo.addCollection(new CheckedOutCollection(repositoryService, objectService, versioningService,
         navigationService));
      wInfo.addCollection(new AllVersionsCollection(repositoryService, objectService, versioningService));
      wInfo.addCollection(new QueryCollection(repositoryService, objectService, versioningService, queryService));
      wInfo.addCollection(new PoliciesCollection(repositoryService, objectService, versioningService, policyService));
      // The other described patterns according collections by WorkspaceManagerImpl#getCollectionAdapter 
      manager = new WorkspaceManagerImpl();
      manager.addWorkspace(wInfo);
   }

   /**
    * {@inheritDoc}
    */
   protected TargetBuilder getTargetBuilder(RequestContext request)
   {
      return targetBuilder;
   }

   /**
    * {@inheritDoc}
    */
   protected Resolver<Target> getTargetResolver(RequestContext request)
   {
      return resolver;
   }

   /**
    * {@inheritDoc}
    */
   public WorkspaceManager getWorkspaceManager(RequestContext request)
   {
      return manager;
   }

}
