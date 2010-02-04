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

package org.xcmis.sp.jcr.exo;

import org.xcmis.core.CmisPropertyBooleanDefinitionType;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyIdDefinitionType;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeManager;
import org.xcmis.spi.TypeNotFoundException;

import java.util.Set;

import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;

/**
 * Implementation of the type definition provider.
 *  
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypeDefinitionProviderImpl.java 2012 2009-07-07 16:28:45Z
 *          andrew00x $
 */
public abstract class TypeManagerImpl implements TypeManager
{

   /** Constant for CMIS property prefix. */
   private static final String CMIS_PROPERTY_NAME_PREFIX = "cmis:";

   /**
    * {@inheritDoc}
    */
   public void addType(CmisTypeDefinitionType type) throws RepositoryException
   {
      throw new UnsupportedOperationException("addType");
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, RepositoryException
   {
      throw new UnsupportedOperationException("removeType");
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId) throws TypeNotFoundException,
      org.xcmis.spi.RepositoryException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, org.xcmis.spi.RepositoryException
   {
      try
      {
         return getTypeDefinition(getNodeType(getNodeTypeName(typeId)), includePropertyDefinition);
      }
      catch (NoSuchNodeTypeException e)
      {
         String msg = "Type with id " + typeId + " not found in repository.";
         throw new TypeNotFoundException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get object type " + typeId;
         throw new org.xcmis.spi.RepositoryException(msg, re);
      }
   }

   /**
    * Add property definitions.
    * 
    * @param typeDefinition the object type definition
    * @param nt the JCR node type.
    */
   private void addPropertyDefinitions(CmisTypeDefinitionType typeDefinition, NodeType nt)
   {
      // Known described in spec. property definitions
      for (CmisPropertyDefinitionType propDef : PropertyDefinitionsMap.getAll(typeDefinition.getBaseId().value()))
         typeDefinition.getPropertyDefinition().add(propDef);

      Set<String> knownIds = PropertyDefinitionsMap.getPropertyIds(typeDefinition.getBaseId().value());
      for (javax.jcr.nodetype.PropertyDefinition pd : nt.getPropertyDefinitions())
      {
         String pdName = pd.getName();
         if (pdName.startsWith(CMIS_PROPERTY_NAME_PREFIX))
         {
            // Do not process known properties
            if (!knownIds.contains(pdName))
            {
               switch (pd.getRequiredType())
               {
                  case javax.jcr.PropertyType.BOOLEAN :
                     CmisPropertyBooleanDefinitionType boolDef = new CmisPropertyBooleanDefinitionType();
                     boolDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     boolDef.setDescription("");
                     boolDef.setDisplayName(pdName);
                     boolDef.setId(pdName);
                     boolDef.setInherited(false);
                     boolDef.setLocalName(pdName);
                     boolDef.setOrderable(true);
                     boolDef.setPropertyType(EnumPropertyType.BOOLEAN);
                     boolDef.setQueryable(true);
                     boolDef.setQueryName(pdName);
                     boolDef.setRequired(pd.isMandatory());
                     boolDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY : EnumUpdatability.READWRITE);

                     typeDefinition.getPropertyDefinition().add(boolDef);
                     break;
                  case javax.jcr.PropertyType.DATE :
                     CmisPropertyDateTimeDefinitionType dateDef = new CmisPropertyDateTimeDefinitionType();
                     dateDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     dateDef.setDescription("");
                     dateDef.setDisplayName(pdName);
                     dateDef.setId(pdName);
                     dateDef.setInherited(false);
                     dateDef.setLocalName(pdName);
                     dateDef.setLocalNamespace(JcrCMIS.CMIS_PREFIX);
                     dateDef.setOrderable(true);
                     dateDef.setPropertyType(EnumPropertyType.DATETIME);
                     dateDef.setQueryable(true);
                     dateDef.setQueryName(pdName);
                     dateDef.setRequired(pd.isMandatory());
                     dateDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY : EnumUpdatability.READWRITE);

                     typeDefinition.getPropertyDefinition().add(dateDef);
                     break;
                  case javax.jcr.PropertyType.DOUBLE :
                     CmisPropertyDecimalDefinitionType decimalDef = new CmisPropertyDecimalDefinitionType();
                     decimalDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     decimalDef.setDescription("");
                     decimalDef.setDisplayName(pdName);
                     decimalDef.setId(pdName);
                     decimalDef.setInherited(false);
                     decimalDef.setLocalName(pdName);
                     decimalDef.setOrderable(true);
                     decimalDef.setPrecision(CMIS.PRECISION);
                     decimalDef.setPropertyType(EnumPropertyType.DECIMAL);
                     decimalDef.setQueryable(true);
                     decimalDef.setQueryName(pdName);
                     decimalDef.setRequired(pd.isMandatory());
                     decimalDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY
                        : EnumUpdatability.READWRITE);

                     typeDefinition.getPropertyDefinition().add(decimalDef);
                     break;
                  case javax.jcr.PropertyType.LONG :
                     CmisPropertyIntegerDefinitionType integerDef = new CmisPropertyIntegerDefinitionType();
                     integerDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     integerDef.setDescription("");
                     integerDef.setDisplayName(pdName);
                     integerDef.setId(pdName);
                     integerDef.setInherited(false);
                     integerDef.setLocalName(pdName);
                     integerDef.setMaxValue(CMIS.MAX_INTEGER_VALUE);
                     integerDef.setMinValue(CMIS.MIN_INTEGER_VALUE);
                     integerDef.setOrderable(true);
                     integerDef.setPropertyType(EnumPropertyType.INTEGER);
                     integerDef.setQueryable(true);
                     integerDef.setQueryName(pdName);
                     integerDef.setRequired(pd.isMandatory());
                     integerDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY
                        : EnumUpdatability.READWRITE);

                     typeDefinition.getPropertyDefinition().add(integerDef);
                     break;
                  case javax.jcr.PropertyType.REFERENCE :
                     CmisPropertyIdDefinitionType idDef = new CmisPropertyIdDefinitionType();
                     idDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     idDef.setDescription("");
                     idDef.setDisplayName(pdName);
                     idDef.setId(pdName);
                     idDef.setInherited(false);
                     idDef.setLocalName(pdName);
                     idDef.setOrderable(true);
                     idDef.setPropertyType(EnumPropertyType.ID);
                     idDef.setQueryable(true);
                     idDef.setQueryName(pdName);
                     idDef.setRequired(pd.isMandatory());
                     idDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY : EnumUpdatability.READWRITE);

                     typeDefinition.getPropertyDefinition().add(idDef);
                     break;
                  case javax.jcr.PropertyType.STRING :
                  case javax.jcr.PropertyType.NAME :
                  case javax.jcr.PropertyType.PATH :
                  case javax.jcr.PropertyType.BINARY :
                  case javax.jcr.PropertyType.UNDEFINED :
                     CmisPropertyStringDefinitionType stringDef = new CmisPropertyStringDefinitionType();
                     stringDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
                     stringDef.setDescription("");
                     stringDef.setDisplayName(pdName);
                     stringDef.setId(pdName);
                     stringDef.setInherited(false);
                     stringDef.setLocalName(pdName);
                     stringDef.setOrderable(true);
                     stringDef.setPropertyType(EnumPropertyType.STRING);
                     stringDef.setQueryable(true);
                     stringDef.setQueryName(pdName);
                     stringDef.setRequired(pd.isMandatory());
                     stringDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY
                        : EnumUpdatability.READWRITE);
                     stringDef.setMaxLength(CMIS.MAX_STRING_LENGTH);

                     typeDefinition.getPropertyDefinition().add(stringDef);
                     break;
               }
            }
         }
      }
   }

   /**
    * Get CMIS object type id by the JCR node type name.
    * 
    * @param ntName the JCR node type name
    * @return CMIS object type id
    */
   protected String getCmisTypeId(String ntName)
   {
      if (ntName.equals(JcrCMIS.NT_CMIS_DOCUMENT))
         return EnumBaseObjectTypeIds.CMIS_DOCUMENT.value();
      if (ntName.equals(JcrCMIS.NT_CMIS_FOLDER) || ntName.equals(JcrCMIS.NT_UNSTRUCTURED))
         return EnumBaseObjectTypeIds.CMIS_FOLDER.value();
      return ntName;
   }

   /**
    * Document type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition false otherwise
    * @return document type definition
    */
   protected CmisTypeDocumentDefinitionType getDocumentDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeDocumentDefinitionType def = new CmisTypeDocumentDefinitionType();
      String typeId = getCmisTypeId(nt.getName());
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      def.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Document Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(true);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(typeId);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.NT_CMIS_DOCUMENT))
            {
               // Take first type that is super for cmis:document or is cmis:document.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(true);
      def.setQueryName(typeId);
      def.setVersionable(true);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Folder type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition false otherwise
    * @return folder type definition
    */
   protected CmisTypeFolderDefinitionType getFolderDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeFolderDefinitionType def = new CmisTypeFolderDefinitionType();
      String typeId = getCmisTypeId(nt.getName());
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Folder Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(typeId);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_FOLDER.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.NT_CMIS_FOLDER))
            {
               // Take first type that is super for cmis:folder or is cmis:folder.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(true);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   //   /**
   //    * Get JCR node type.
   //    * 
   //    * @param name node type name
   //    * @return JCR node type
   //    * @throws NoSuchNodeTypeException if node type not found
   //    * @throws javax.jcr.RepositoryException if other JCR repository errors
   //    */
   //   protected abstract void addNodeType(NodeType nodeType) throws NoSuchNodeTypeException, javax.jcr.RepositoryException;

   /**
    * Get JCR node type.
    * 
    * @param name node type name
    * @return JCR node type
    * @throws NoSuchNodeTypeException if node type not found
    * @throws javax.jcr.RepositoryException if other JCR repository errors
    */
   protected abstract NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException;

   /**
    * Get JCR node type name by the CMIS object type id.
    * 
    * @param typeId the CMIS base object type id
    * @return JCR string node type
    */
   protected String getNodeTypeName(String typeId)
   {
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()))
         return JcrCMIS.NT_CMIS_DOCUMENT;
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_FOLDER.value()))
         return JcrCMIS.NT_CMIS_FOLDER;
      return typeId;
   }

   /**
    * Get policy type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition false otherwise
    * @return type policy definition
    */
   protected CmisTypeDefinitionType getPolicyDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypePolicyDefinitionType def = new CmisTypePolicyDefinitionType();
      String typeId = getCmisTypeId(nt.getName());
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_POLICY);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Policy Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(typeId);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_POLICY.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.CMIS_POLICY))
            {
               // Take first type that is super for cmis:policy or is cmis:policy.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(false);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Get relationship type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition false otherwise
    * @return type relationship definition
    */
   protected CmisTypeRelationshipDefinitionType getRelationshipDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeRelationshipDefinitionType def = new CmisTypeRelationshipDefinitionType();
      String typeId = getCmisTypeId(nt.getName());
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      def.setControllableACL(false);
      def.setControllablePolicy(false);
      def.setCreatable(true);
      def.setDescription("Cmis Relationship Type");
      def.setDisplayName(typeId);
      def.setFileable(false);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(false);
      def.setLocalName(typeId);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.CMIS_RELATIONSHIP))
            {
               // Take first type that is super for cmis:relationship or is cmis:relationship.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(false);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Get object type definition.
    * 
    * @param nt JCR back-end node
    * @param includePropertyDefinition true if need include property definition false otherwise
    * @return object definition
    * @throws InvalidArgumentException if unsupported node type
    */
   protected CmisTypeDefinitionType getTypeDefinition(NodeType nt, boolean includePropertyDefinition)
      throws InvalidArgumentException
   {
      if (nt.isNodeType(JcrCMIS.NT_CMIS_DOCUMENT))
         return getDocumentDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.NT_CMIS_FOLDER) || nt.isNodeType(JcrCMIS.NT_UNSTRUCTURED))
         return getFolderDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.CMIS_RELATIONSHIP))
         return getRelationshipDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.CMIS_POLICY))
         return getPolicyDefinition(nt, includePropertyDefinition);
      else
      {
         String msg = "Unsupported node type " + nt.getName();
         throw new InvalidArgumentException(msg);
      }
   }

}
