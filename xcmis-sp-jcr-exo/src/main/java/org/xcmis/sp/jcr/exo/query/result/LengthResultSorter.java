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

package org.xcmis.sp.jcr.exo.query.result;

import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.jcr.datamodel.ValueData;
import org.exoplatform.services.jcr.impl.core.value.BaseValue;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.AbstractPersistedValueData;
import org.exoplatform.services.jcr.impl.dataflow.TransientValueData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.result.ScoredRow;

import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.qom.Ordering;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LengthResultSorter extends AbstractItemDataResultSorter
{
   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(LengthResultSorter.class);

   /** The ordering. */
   private final Ordering ordering;

   /** The value factory impl. */
   private final ValueFactoryImpl valueFactoryImpl;

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param itemCache the item cache
    * @param valueFactoryImpl the value factory impl
    * @param ordering the ordering
    */
   public LengthResultSorter(ItemDataConsumer itemDataConsumer, Map<String, ItemData> itemCache,
      ValueFactoryImpl valueFactoryImpl, Ordering ordering)
   {
      super(itemDataConsumer, itemCache);
      this.valueFactoryImpl = valueFactoryImpl;
      this.ordering = ordering;
   }

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param valueFactoryImpl the value factory impl
    * @param ordering the ordering
    */
   public LengthResultSorter(ItemDataConsumer itemDataConsumer, ValueFactoryImpl valueFactoryImpl, Ordering ordering)
   {
      super(itemDataConsumer);
      this.valueFactoryImpl = valueFactoryImpl;
      this.ordering = ordering;
   }

   /**
    * {@inheritDoc}
    */
   public int compare(ScoredRow o1, ScoredRow o2)
   {
      LengthImpl lengthOrdering = (LengthImpl)ordering.getOperand();
      PropertyValueImpl propertyValueOrdering = (PropertyValueImpl)lengthOrdering.getPropertyValue();
      String uuid1 = o1.getNodeIdentifer(propertyValueOrdering.getSelectorName());
      String uuid2 = o2.getNodeIdentifer(propertyValueOrdering.getSelectorName());

      if (uuid1 == null && uuid2 == null)
         return 0;
      if (uuid1 == null)
         return -1;
      if (uuid2 == null)
         return 1;

      try
      {
         NodeData nodeData1 = getNodeData(uuid1);
         NodeData nodeData2 = getNodeData(uuid2);

         if (nodeData1 == null && nodeData2 == null)
            return 0;
         if (nodeData1 == null)
            return -1;
         if (nodeData2 == null)
            return 1;

         PropertyData propertyData1 =
            getPropertyData(nodeData1, new QPathEntry(propertyValueOrdering.getPropertyQName(), 1));
         PropertyData propertyData2 =
            getPropertyData(nodeData2, new QPathEntry(propertyValueOrdering.getPropertyQName(), 1));

         if (propertyData1 == null && propertyData2 == null)
            return 0;
         if (propertyData1 == null)
            return -1;
         if (propertyData2 == null)
            return 1;
         if (propertyData1.getType() != propertyData2.getType() || propertyData1.isMultiValued()
            || propertyData2.isMultiValued())
            return 0;

         Value value1 = loadValue(propertyData1.getValues().get(0), propertyData1.getType());
         Value value2 = loadValue(propertyData2.getValues().get(0), propertyData2.getType());

         return new Long(getLength(value1, propertyData1.getType())).compareTo(getLength(value2, propertyData2
            .getType()));
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug(e.getLocalizedMessage());
      }
      return 0;
   }

   /**
    * Return length of the value.
    * 
    * @param value the value
    * @param type the type
    * @return the length
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private long getLength(Value value, int type) throws RepositoryException
   {
      if (type == PropertyType.BINARY)
         return ((BaseValue)value).getLength();
      return value.getString().length();

   }

   /**
    * Gets the property data.
    * 
    * @param parent the parent
    * @param name the name
    * 
    * @return NodeData
    */
   private PropertyData getPropertyData(NodeData parent, QPathEntry name)
   {
      PropertyData result = null;
      try
      {
         ItemData itemData = itemDataConsumer.getItemData(parent, name);
         if (itemData != null && !itemData.isNode())
            result = (PropertyData)itemData;
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug(e.getLocalizedMessage());
      }

      return result;
   }

   /**
    * Load value.
    * 
    * @param vData the value data
    * @param type the type
    * @return Value
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private Value loadValue(ValueData vData, int type) throws RepositoryException
   {
      TransientValueData trData = null;
      if (vData instanceof TransientValueData)
         trData = (TransientValueData)vData;
      else
         trData = ((AbstractPersistedValueData)vData).createTransientCopy();
      return valueFactoryImpl.loadValue(trData, type);
   }

}
