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
import org.apache.abdera.protocol.server.impl.SimpleSubjectResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import javax.security.auth.Subject;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ProviderImpl.java 216 2010-02-12 17:19:50Z andrew00x $
 */
public class ProviderImpl extends AbstractProvider
{

   /** The manager. */
   private final AbstractWorkspaceManager manager;

   /** The target builder. */
   private final TemplateTargetBuilder targetBuilder;

   /** The resolver. */
   private final RegexTargetResolver resolver;

   /**
    * Instantiates a new provider impl.
    */
   public ProviderImpl()
   {
      targetBuilder = new TemplateTargetBuilder();
      targetBuilder.setTemplate(TargetType.ENTRY, "{target_base}/cmisatom/{repoid}/{atomdoctype}/{id}");
      targetBuilder.setTemplate(TargetType.SERVICE, "{target_base}/cmisatom/{repoid}");
      targetBuilder
         .setTemplate(
            "feed",
            "{target_base}/cmisatom/{repoid}/{atomdoctype}/{id}{-opt|?|q,maxItems,skipCount,changeLogToken}{-join|&|q,maxItems,skipCount,changeLogToken}");

      resolver = new DecodeRegexTargetResolver();

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

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/checkedout(/)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "slash"); // Slash.

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/children/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+?)/object/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_ENTRY, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+?)/objectbypath(/)?(\\??.*)?", //
         TargetType.TYPE_ENTRY, //
         "repoid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/parents/([^/?]+)(\\??.*)?", //
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

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/versions/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "versionSeriesId");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/foldertree/([^/?]+)(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "objectid");

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/query(/)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid",//
         "slash"); // Slash.

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

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/unfiled(/)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid", //
         "slash"); // Slash.

      resolver.setPattern("/" + AtomCMIS.CMIS_REST_RESOURCE_PATH + "/([^/]+)/changes(/)?(\\??.*)?", //
         TargetType.TYPE_COLLECTION, //
         "repoid",//
         "slash"); // Slash.

      SimpleWorkspaceInfo wInfo = new SimpleWorkspaceInfo();

      wInfo.addCollection(new CmisCollectionInfo("Folder Children", "/children", AtomCMIS.COLLECTION_TYPE_ROOT));
      wInfo.addCollection(new CmisCollectionInfo("Types Children", "/types", AtomCMIS.COLLECTION_TYPE_TYPES));
      wInfo.addCollection(new CmisCollectionInfo("Checkedout", "/checkedout", AtomCMIS.COLLECTION_TYPE_CHECKEDOUT));
      wInfo.addCollection(new CmisCollectionInfo("Query", "/query", AtomCMIS.COLLECTION_TYPE_QUERY));
      wInfo.addCollection(new CmisCollectionInfo("Unfiled", "/unfiled", AtomCMIS.COLLECTION_TYPE_UNFILED));

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

   protected Resolver<Subject> getSubjectResolver(RequestContext request)
   {
      return new SubjectResolver();
   }

   // SecurityManager problem when use org.apache.abdera.protocol.server.servlet.ServletRequestContext.
   // java.security.AccessControlException when try to use
   // org.apache.abdera.protocol.server.impl.SimpleSubjectResolver.resolve(Request)
   private class SubjectResolver extends SimpleSubjectResolver
   {

      @SuppressWarnings("unchecked")
      @Override
      public Subject resolve(Principal principal)
      {
         Set<Principal> principals = new HashSet<Principal>(1);
         principals.add(principal != null ? principal : ANONYMOUS);
         Subject subject = new Subject(false, principals, new HashSet(), new HashSet());
         return subject;
      }

   }

   private class DecodeRegexTargetResolver extends RegexTargetResolver
   {
      @Override
      protected Target getTarget(TargetType type, RequestContext request, Matcher matcher, String[] fields)
      {
         return new RegexTarget(type, request, matcher, fields)
         {
            @Override
            public String getParameter(String name)
            {
               String parameter = super.getParameter(name);
               if (parameter != null)
               {
                  try
                  {
                     return URLDecoder.decode(parameter, "UTF-8");
                  }
                  catch (UnsupportedEncodingException e)
                  {
                  }
               }
               return parameter;
            }
         };
      }
   }
}
