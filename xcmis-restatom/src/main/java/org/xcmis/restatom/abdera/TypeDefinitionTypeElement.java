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
import org.xcmis.spi.ContentStreamAllowed;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.impl.TypeDefinitionImpl;

import java.util.HashMap;
import java.util.Map;

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
   public Map<String, PropertyDefinition<?>> getPropertyDefinitions()
   {
      Map<String, PropertyDefinition<?>> propDefs = new HashMap<String, PropertyDefinition<?>>();
      for (QName q : AtomCMIS.PROPERTY_DEFINITIONS)
      {
         PropertyDefinitionTypeElement propDefEl = getExtension(q);
         if (propDefEl != null)
         {
            PropertyDefinition<?> propDef = propDefEl.getPropertyDefinition();
            propDefs.put(propDef.getId(), propDef);
         }
      }
      return propDefs;
   }

   /**
    * Gets the type definition.
    * 
    * @return the type definition
    */
   public TypeDefinition getTypeDefinition()
   {
      String baseId = getSimpleExtension(AtomCMIS.BASE_ID);
      BaseType baseType;
      try
      {
         baseType = BaseType.fromValue(baseId);
      }
      catch (IllegalArgumentException e)
      {
         throw new InvalidArgumentException("Unable to parse Type Definition element. Unknown baseTypeId " + baseId);
      }

      TypeDefinitionImpl type = new TypeDefinitionImpl();
      switch (baseType)
      {
         case DOCUMENT :
            type.setVersionable(Boolean.parseBoolean(getSimpleExtension(AtomCMIS.VERSIONABLE)));
            String contentAllowed = getSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED);
            try
            {
               type.setContentStreamAllowed(ContentStreamAllowed.fromValue(contentAllowed));
            }
            catch (IllegalArgumentException e)
            {
               throw new InvalidArgumentException(
                  "Unable to parse Type Definition element. Unsupported 'content stream allowed attribute': "
                     + contentAllowed);
            }
            break;
         case FOLDER :
            break;
         case POLICY :
            break;
         case RELATIONSHIP :
            type.setAllowedSourceTypes(new String[]{getSimpleExtension(AtomCMIS.ALLOWED_SOURCE_TYPES)});
            type.setAllowedTargetTypes(new String[]{getSimpleExtension(AtomCMIS.ALLOWED_TARGET_TYPES)});
            break;
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
      Map<String, PropertyDefinition<?>> propDefs = getPropertyDefinitions();
      type.setPropertyDefinitions(propDefs);

      return type;
   }

   /**
    * Builds the element.
    * 
    * @param type the type
    */
   public void build(TypeDefinition type)
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

         switch (type.getBaseId())
         {
            case DOCUMENT :
               setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeDocumentDefinitionType");

               addSimpleExtension(AtomCMIS.VERSIONABLE, Boolean.toString(type.isVersionable()));
               if (type.getContentStreamAllowed() != null)
                  addSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED, type.getContentStreamAllowed().value());
               else
                  addSimpleExtension(AtomCMIS.CONTENT_STREAM_ALLOWED, ContentStreamAllowed.ALLOWED.value());

               break;
            case RELATIONSHIP :
               setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeRelationshipDefinitionType");

               String[] arrayAllowedSource = type.getAllowedSourceTypes();
               if (arrayAllowedSource != null && arrayAllowedSource.length > 0)
               {
                  for (String string : arrayAllowedSource)
                     addSimpleExtension(AtomCMIS.ALLOWED_SOURCE_TYPES, string);
               }

               String[] arrayAllowedTarget = type.getAllowedTargetTypes();
               if (arrayAllowedTarget != null && arrayAllowedTarget.length > 0)
               {
                  for (String string : arrayAllowedTarget)
                     addSimpleExtension(AtomCMIS.ALLOWED_TARGET_TYPES, string);
               }
               break;
            case FOLDER :
               setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypeFolderDefinitionType");
               break;

            case POLICY :
               setAttributeValue(AtomCMIS.X_TYPE, "cmis:cmisTypePolicyDefinitionType");
               break;
         }

         if (type.getBaseId() == null)
         {
            String msg = "Specified baseType does not match with any allowed BaseTypeId";
            throw new InvalidArgumentException(msg);
         }

         /* property definitions */
         if (type.getPropertyDefinitions() != null && type.getPropertyDefinitions().size() > 0)
         {
            for (PropertyDefinition<?> propertyDefinition : type.getPropertyDefinitions())
            {
               PropertyDefinitionTypeElement propDefEl =
                  addExtension(CMISExtensionFactory.getPropertyDefinitionTypeElementName(propertyDefinition
                     .getPropertyType()));
               propDefEl.build(propertyDefinition);
            }
         }
      }
   }
}
