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

package org.xcmis.core.impl.property;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.CmisAction;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PropertyServiceImpl implements PropertyService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(PropertyServiceImpl.class.getName());

   /** Common property extractors. */
   protected final Map<EnumPropertyType, PropertyExtractor> propertyExtractors =
      new HashMap<EnumPropertyType, PropertyExtractor>();

   /** Property setters. */
   protected final Map<String, PropertySetter> propertySetters = new HashMap<String, PropertySetter>();

   /** Default property setter. */
   protected final PropertySetter defaultPropertySetter = new PropertySetterImpl();

   /**
    * Default constructor.
    */
   public PropertyServiceImpl()
   {
      propertyExtractors.put(EnumPropertyType.BOOLEAN, new BooleanPropertyExtractor());
      propertyExtractors.put(EnumPropertyType.DATETIME, new DatePropertyExtractor());
      propertyExtractors.put(EnumPropertyType.DECIMAL, new DecimalPropertyExtractor());
      propertyExtractors.put(EnumPropertyType.INTEGER, new IntegerPropertyExtractor());
      propertyExtractors.put(EnumPropertyType.STRING, new StringPropertyExtractor());
      propertyExtractors.put(EnumPropertyType.ID, new IdPropertyExtractor());
      propertyExtractors.put(EnumPropertyType.URI, new UriPropertyExtractor());

      // TODO : Make it configurable.
      propertySetters.put(CMIS.NAME, new ObjectNamePropertySetter());
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisProperty> getProperties(Entry entry)
   {
      CmisTypeDefinitionType type = entry.getType();
      List<CmisProperty> properties = new ArrayList<CmisProperty>();
      for (CmisPropertyDefinitionType propertyDefinition : type.getPropertyDefinition())
      {
         String propertyId = propertyDefinition.getId();
         EnumPropertyType propertyType = propertyDefinition.getPropertyType();
         PropertyExtractor extractor = propertyExtractors.get(propertyType);
         if (extractor != null)
         {
            try
            {
               properties.add(extractor.getProperty(entry, propertyDefinition));
            }
            catch (RepositoryException re)
            {
               String msg = "Unable to retrieval property " + propertyId;
               LOG.error(msg, re);
               throw new RuntimeException(msg, re);
            }
         }
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public CmisProperty getProperty(Entry entry, String propertyId) throws UnsupportedPropertyException
   {
      CmisTypeDefinitionType type = entry.getType();
      for (CmisPropertyDefinitionType propertyDefinition : type.getPropertyDefinition())
      {
         if (propertyId.equals(propertyDefinition.getId()))
         {
            PropertyExtractor extractor = propertyExtractors.get(propertyDefinition.getPropertyType());
            if (extractor != null)
            {
               try
               {
                  return extractor.getProperty(entry, propertyDefinition);
               }
               catch (RepositoryException re)
               {
                  String msg = "Unable to retrieval property " + propertyId;
                  LOG.error(msg, re);
                  throw new RuntimeException(msg, re);
               }
            }
         }
      }
      String msg = "Property " + propertyId + " is not supported by object type " + type.getDisplayName();
      LOG.warn(msg);
      throw new UnsupportedPropertyException(msg);
   }

   /**
    * {@inheritDoc}
    */
   public void setProperty(Entry entry, CmisProperty property, CmisAction cmisAction) throws RepositoryException
   {
      String propertyId = property.getPropertyDefinitionId();
      CmisPropertyDefinitionType propertyDefinition = null;
      for (Iterator<CmisPropertyDefinitionType> iter = entry.getType().getPropertyDefinition().iterator(); iter
         .hasNext()
         && propertyDefinition == null;)
      {
         CmisPropertyDefinitionType item = iter.next();
         if (propertyId.equals(item.getId()))
            propertyDefinition = item;
      }
      if (propertyDefinition == null)
      {
         String msg = "Property " + propertyId + " is not supported by object type " + entry.getType().getDisplayName();
         throw new UnsupportedPropertyException(msg);
      }
      // Be sure property contains local name.
      if (property.getLocalName() == null)
         property.setLocalName(propertyDefinition.getLocalName());

      EnumUpdatability updatability = propertyDefinition.getUpdatability();

      boolean canUpdate = updatability != EnumUpdatability.READONLY //
         && ((cmisAction == CmisAction.UPDATE_PWC_PROPERTIES && updatability == EnumUpdatability.WHENCHECKEDOUT) //
            || (cmisAction == CmisAction.CREATE && updatability == EnumUpdatability.ONCREATE) //
         || (cmisAction == CmisAction.UPDATE_OBJECT_PROPERTIES && updatability == EnumUpdatability.READWRITE));

      if (!canUpdate)
      {
         if (LOG.isDebugEnabled())
         {
            String msg =
               "Property " + propertyId + " is " + updatability.value() + ". Unable to change it in "
                  + cmisAction.value() + " action.";
            LOG.warn(msg);
         }
         return;
      }
      EnumPropertyType propertyType = propertyDefinition.getPropertyType();
      getPropertySetter(propertyId).setProperty(propertyType, entry, property);
   }

   /**
    * Retrieves a property setter for a given PropertyId.
    * 
    * @param propertyId string propertyId
    * @return PropertySetter
    */
   protected PropertySetter getPropertySetter(String propertyId)
   {
      PropertySetter setter = propertySetters.get(propertyId);
      if (setter == null)
         return defaultPropertySetter;
      return setter;
   }

}
