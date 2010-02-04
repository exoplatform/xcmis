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
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisTypeDefinitionTypeElementWrapper.java 2192 2009-07-17
 *          13:19:12Z sunman $ Jul 14, 2009
 */
public class TypeDefinitionTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new type definition type element.
    * 
    * @param internal the internal
    */
   public TypeDefinitionTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new type definition type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public TypeDefinitionTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Gets the properties element.
    * 
    * @return the properties element
    */
   public List<CmisPropertyDefinitionType> getPropertyDefinitions()
   {
      List<CmisPropertyDefinitionType> propDefs = new ArrayList<CmisPropertyDefinitionType>();
      for (QName q : AtomCMIS.PROPERTY_DEFINITIONS)
      {
         PropertyDefinitionTypeElement<CmisPropertyDefinitionType> propDefEl = getExtension(q);
         if (propDefEl != null)
         {
            CmisPropertyDefinitionType propDef = propDefEl.getPropertyDefinition();
            propDefs.add(propDef);
         }
      }
      return propDefs;
   }

   /**
    * Gets the type definition.
    * 
    * @return the type definition
    */
   public CmisTypeDefinitionType getTypeDefinition()
   {
      String baseId = getSimpleExtension(AtomCMIS.BASE_ID);
      EnumBaseObjectTypeIds baseType;
      try
      {
         baseType = EnumBaseObjectTypeIds.fromValue(getSimpleExtension(AtomCMIS.BASE_ID));
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException("Unknown baseTypeId " + baseId);
      }

      CmisTypeDefinitionType type = null;
      try
      {
         switch (baseType)
         {
            case CMIS_DOCUMENT :
               type = new CmisTypeDocumentDefinitionType();
               ((CmisTypeDocumentDefinitionType)type).setVersionable(Boolean
                  .parseBoolean(getSimpleExtension(AtomCMIS.VERSIONABLE)));
               ((CmisTypeDocumentDefinitionType)type).setContentStreamAllowed(EnumContentStreamAllowed
                  .fromValue(getSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED)));
               break;
            case CMIS_FOLDER :
               type = new CmisTypeFolderDefinitionType();
               break;
            case CMIS_POLICY :
               type = new CmisTypePolicyDefinitionType();
               break;
            case CMIS_RELATIONSHIP :
               type = new CmisTypeRelationshipDefinitionType();
               ((CmisTypeRelationshipDefinitionType)type).getAllowedSourceTypes().add(
                  getSimpleExtension(AtomCMIS.ALLOWED_SOURCE_TYPES));
               ((CmisTypeRelationshipDefinitionType)type).getAllowedTargetTypes().add(
                  getSimpleExtension(AtomCMIS.ALLOWED_TARGET_TYPES));
               break;
         }
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException("Invalid argument " + e.getMessage());
      }

      if (type == null)
      {
         String msg = "Specified baseType does not match with any allowed BaseTypeId";
         throw new InvalidArgumentException(msg);
      }

      type.setId(getSimpleExtension(AtomCMIS.ID));
      type.setLocalName(getSimpleExtension(AtomCMIS.LOCAL_NAME));
      type.setLocalNamespace(getSimpleExtension(AtomCMIS.LOCAL_NAMESPACE));
      type.setDisplayName(getSimpleExtension(AtomCMIS.DISPLAY_NAME));
      type.setQueryName(getSimpleExtension(AtomCMIS.QUERY_NAME));
      type.setDescription(getSimpleExtension(AtomCMIS.DESCRIPTION));
      type.setBaseId(baseType);

      type.setParentId(getSimpleExtension(AtomCMIS.PARENT_ID));
      type.setCreatable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.CREATABLE)));
      type.setFileable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.FILEABLE)));
      type.setQueryable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.QUERYABLE)));
      type.setFulltextIndexed(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.FULLTEXT_INDEXED)));
      type.setIncludedInSupertypeQuery(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.INCLUDED_IN_SUPERTYPE_QUERY)));

      type.setControllablePolicy(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.CONTROLLABLE_POLICY)));
      type.setControllableACL(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.CONTROLLABLE)));

      //Property definitions
      List<CmisPropertyDefinitionType> propDefs = getPropertyDefinitions();
      type.getPropertyDefinition().addAll(propDefs);

      return type;
   }

   /**
    * Builds the element.
    * 
    * @param type the type
    */
   public void build(CmisTypeDefinitionType type)
   {
      if (type != null)
      {
         addSimpleExtension(AtomCMIS.ID, type.getId());
         addSimpleExtension(AtomCMIS.LOCAL_NAME, type.getLocalName());
         addSimpleExtension(AtomCMIS.LOCAL_NAMESPACE, type.getLocalNamespace());
         addSimpleExtension(AtomCMIS.DISPLAY_NAME, type.getDisplayName());
         addSimpleExtension(AtomCMIS.QUERY_NAME, type.getQueryName());
         addSimpleExtension(AtomCMIS.DESCRIPTION, type.getDescription());

         /* base type */
         if (type.getBaseId() != null)
            addSimpleExtension(AtomCMIS.BASE_ID, type.getBaseId().value());

         /* parent */
         addSimpleExtension(AtomCMIS.PARENT_ID, type.getParentId());

         /* flags */
         addSimpleExtension(AtomCMIS.CREATABLE, Boolean.toString(type.isCreatable()));
         addSimpleExtension(AtomCMIS.FILEABLE, Boolean.toString(type.isFileable()));
         addSimpleExtension(AtomCMIS.QUERYABLE, Boolean.toString(type.isQueryable()));
         addSimpleExtension(AtomCMIS.FULLTEXT_INDEXED, Boolean.toString(type.isFulltextIndexed()));
         addSimpleExtension(AtomCMIS.INCLUDED_IN_SUPERTYPE_QUERY, Boolean.toString(type.isIncludedInSupertypeQuery()));

         /* controllable */
         addSimpleExtension(AtomCMIS.CONTROLLABLE_POLICY, Boolean.toString(type.isControllablePolicy()));
         addSimpleExtension(AtomCMIS.CONTROLLABLE, Boolean.toString(type.isControllableACL()));

         if (type instanceof CmisTypeDocumentDefinitionType)
         {
            setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeDocumentDefinitionType");

            addSimpleExtension(AtomCMIS.VERSIONABLE, Boolean.toString(((CmisTypeDocumentDefinitionType)type)
               .isVersionable()));
            if (((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() != null)
               addSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED, ((CmisTypeDocumentDefinitionType)type)
                  .getContentStreamAllowed().value());
            else
               addSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED, EnumContentStreamAllowed.ALLOWED.value());
         }
         else if (type instanceof CmisTypeRelationshipDefinitionType)
         {
            setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeRelationshipDefinitionType");

            List<String> listAllowedSource = ((CmisTypeRelationshipDefinitionType)type).getAllowedSourceTypes();
            if (listAllowedSource != null && listAllowedSource.size() > 0)
            {
               for (String string : listAllowedSource)
                  addSimpleExtension(AtomCMIS.ALLOWED_SOURCE_TYPES, string);
            }

            List<String> listAllowedTarget = ((CmisTypeRelationshipDefinitionType)type).getAllowedTargetTypes();
            if (listAllowedTarget != null && listAllowedTarget.size() > 0)
            {
               for (String string : listAllowedTarget)
                  addSimpleExtension(AtomCMIS.ALLOWED_TARGET_TYPES, string);
            }
         }
         else if (type instanceof CmisTypeFolderDefinitionType)
         {
            setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeFolderDefinitionType");
         }
         else if (type instanceof CmisTypePolicyDefinitionType)
         {
            setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypePolicyDefinitionType");
         }

         /* property definitions */
         if (type.getPropertyDefinition() != null && type.getPropertyDefinition().size() > 0)
         {
            for (CmisPropertyDefinitionType propertyDefinition : type.getPropertyDefinition())
            {
               PropertyDefinitionTypeElement<CmisPropertyDefinitionType> propDefEl =
                  addExtension(CMISExtensionFactory.getElementName(propertyDefinition.getClass()));
               propDefEl.build(propertyDefinition);
            }
         }
      }
   }
}
