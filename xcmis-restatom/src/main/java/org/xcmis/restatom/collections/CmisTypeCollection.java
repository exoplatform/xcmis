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

package org.xcmis.restatom.collections;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.RepositoryService;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.CMISExtensionFactory;
import org.xcmis.restatom.abdera.TypeDefinitionTypeElement;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeNotFoundException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisTypeCollection.java 47 2010-02-08 22:59:45Z andrew00x $
 */
public abstract class CmisTypeCollection extends AbstractCmisCollection<CmisTypeDefinitionType>
{

   /** The repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Instantiates a new cmis type collection.
    * 
    * @param repositoryService the repository service
    */
   public CmisTypeCollection(RepositoryService repositoryService)
   {
      super();
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public String getAuthor(RequestContext request) throws ResponseContextException
   {
      return "system";
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getEntry(String typeId, RequestContext request) throws ResponseContextException
   {
      try
      {
         return repositoryService.getTypeDefinition(getRepositoryId(request), typeId);
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (TypeNotFoundException tne)
      {
         throw new ResponseContextException(createErrorResponse(tne, 400));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getId(CmisTypeDefinitionType entry) throws ResponseContextException
   {
      return entry.getId();
   }

   /**
    * {@inheritDoc}
    */
   public String getId(RequestContext request)
   {
      String typeId = request.getTarget().getParameter("typeid");
      if (typeId == null)
         return "cmis:types";
      return typeId;
   }

   /**
    * {@inheritDoc}
    */
   public String getName(CmisTypeDefinitionType entry) throws ResponseContextException
   {
      return entry.getDisplayName();
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(CmisTypeDefinitionType entry) throws ResponseContextException
   {
      return entry.getDisplayName();
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Cmis Types";
   }

   /**
    * {@inheritDoc}
    */
   public Date getUpdated(CmisTypeDefinitionType entry) throws ResponseContextException
   {
      // TODO must be determined ones. Types are not created in runtime.
      return new Date();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext postEntry(RequestContext request)
   {
      Entry entry;
      try
      {
         entry = getEntryFromRequest(request);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      try
      {
         TypeDefinitionTypeElement typeElement = entry.getFirstChild(AtomCMIS.TYPE);
         CmisTypeDefinitionType type = typeElement.getTypeDefinition();
         String typeId = type.getId();
         Repository repository = repositoryService.getRepository(getRepositoryId(request));
         repository.addType(type);
         // Updated (formed) type definition.
         type = repository.getTypeDefinition(typeId);
         entry = request.getAbdera().getFactory().newEntry();
         addEntryDetails(request, entry, request.getResolvedUri(), type);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      Map<String, String> params = new HashMap<String, String>();
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return buildCreateEntryResponse(link, entry);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String addEntryDetails(RequestContext request, Entry entry, IRI feedIri, CmisTypeDefinitionType type)
      throws ResponseContextException
   {
      entry.setId(type.getId());
      entry.setTitle(type.getDisplayName());
      entry.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance()));
      entry.setPublished(AtomUtils.getAtomDate(Calendar.getInstance()));
      entry.setSummary("");
      Person p = request.getAbdera().getFactory().newAuthor();
      p.setName(getAuthor(request));
      entry.addAuthor(p);

      //Service link.
      entry.addLink(getServiceLink(request), AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      // Self link.
      String self = getObjectTypeLink(getId(type), request);
      entry.addLink(self, AtomCMIS.LINK_SELF, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

      // Parent type link.
      String parentTypeId = type.getParentId();
      if (parentTypeId != null)
      {
         // Does not provided for root types.
         String parent = getObjectTypeLink(parentTypeId, request);
         entry.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
      }

      // Down link is link to children types.
      String childrenLink = getTypeChildrenLink(type.getId(), request);
      entry.addLink(childrenLink, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      // Down link is link to descendant types.
      String descendatsLink = getTypeDescendantsLink(type.getId(), request);
      entry.addLink(descendatsLink, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);

      TypeDefinitionTypeElement objectElement =
         new TypeDefinitionTypeElement(request.getAbdera().getFactory(), CMISExtensionFactory
            .getElementName(CmisTypeDefinitionType.class));
      objectElement.build(type);

      entry.addExtension(objectElement);

      return self;
   }

   /**
    * Get link to AtomPub Document that describes direct descendants type for
    * specified type <code>id</code>.
    * 
    * @param typeId type id
    * @param request request context
    * @return link to AtomPub Document that describes direct descendants type for
    *         specified type <code>id</code>
    */
   protected String getTypeChildrenLink(String typeId, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "types");
      params.put("id", typeId);
      String type = request.absoluteUrlFor(TargetType.ENTRY, params);
      return type;
   }

   /**
    * Get link to AtomPub Document that describes all descendants type for
    * specified type <code>id</code>.
    * 
    * @param typeId type id
    * @param request request context
    * @return link to AtomPub Document that describes all descendants type for
    *         specified type <code>id</code>
    */
   protected String getTypeDescendantsLink(String typeId, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "typedescendants");
      params.put("id", typeId);
      String type = request.absoluteUrlFor(TargetType.ENTRY, params);
      return type;
   }

   /**
    * Delete entry.
    * 
    * @param typeId the type id
    * @param request the request
    * @throws ResponseContextException the response context exception
    */
   @Override
   public void deleteEntry(String typeId, RequestContext request) throws ResponseContextException
   {
      try
      {
         repositoryService.getRepository(getRepositoryId(request)).removeType(typeId);
      }
      catch (TypeNotFoundException tnfe)
      {
         createErrorResponse(tnfe, 404);
      }
      catch (InvalidArgumentException iae)
      {
         createErrorResponse(iae, 400);
      }
      catch (RepositoryException re)
      {
         createErrorResponse(re, 500);
      }
   }

}
