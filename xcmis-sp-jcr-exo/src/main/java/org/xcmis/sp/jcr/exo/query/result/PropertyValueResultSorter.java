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

import org.exoplatform.services.jcr.core.ExtendedPropertyType;
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
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.result.ScoredRow;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModelConstants;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class PropertyValueResultSorter extends AbstractItemDataResultSorter
{
   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(PropertyValueResultSorter.class);

   /** The value factory impl. */
   private final ValueFactoryImpl valueFactoryImpl;

   /** The ordering. */
   private final Ordering ordering;

   /**
    * The Constructor.
    * 
    * @param itemMgr the item mgr
    * @param itemCache the item cache
    * @param ordering the ordering
    * @param valueFactoryImpl the value factory impl
    */
   public PropertyValueResultSorter(final ItemDataConsumer itemMgr, final Map<String, ItemData> itemCache,
      Ordering ordering, ValueFactoryImpl valueFactoryImpl)
   {
      super(itemMgr, itemCache);
      this.ordering = ordering;
      this.valueFactoryImpl = valueFactoryImpl;
   }

   /**
    * The Constructor.
    * 
    * @param itemMgr the item mgr
    * @param ordering the ordering
    * @param valueFactoryImpl the value factory impl
    */
   public PropertyValueResultSorter(final ItemDataConsumer itemMgr, Ordering ordering, ValueFactoryImpl valueFactoryImpl)
   {
      super(itemMgr, new HashMap<String, ItemData>());
      this.ordering = ordering;
      this.valueFactoryImpl = valueFactoryImpl;
   }

   /**
    * {@inheritDoc}
    */
   public int compare(ScoredRow o1, ScoredRow o2)
   {
      if (LOG.isDebugEnabled())
      {
         // ResultDumper.dump(o1);
         // ResultDumper.dump(o2);
      }
      int result = 0;
      try
      {
         result = compare(o1, o2, ordering);
         if (result != 0)
         {
            result *= QueryObjectModelConstants.JCR_ORDER_DESCENDING.equals(ordering.getOrder()) ? -1 : 1;
            return result;
         }

      }
      catch (RepositoryException e)
      {
         return 0;
      }
      return result;
   }

   /**
    * Compare by given Ordering.
    * 
    * @param o1 ScoredRow
    * @param o2 ScoredRow
    * @param order Ordering
    * @return int compare result
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private int compare(ScoredRow o1, ScoredRow o2, Ordering order) throws RepositoryException
   {
      if (order.getOperand() instanceof PropertyValue)
      {
         PropertyValueImpl propertyValueOrdering = (PropertyValueImpl)order.getOperand();
         String uuid1 = o1.getNodeIdentifer(propertyValueOrdering.getSelectorName());
         String uuid2 = o2.getNodeIdentifer(propertyValueOrdering.getSelectorName());

         if (uuid1 == null && uuid2 == null)
            return 0;
         if (uuid1 == null)
            return -1;
         if (uuid2 == null)
            return 1;

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
         {
            return 0;
         }
         Value value1 = null;
         Value value2 = null;
         try
         {
            value1 = loadValue(propertyData1.getValues().get(0), propertyData1.getType());
            value2 = loadValue(propertyData2.getValues().get(0), propertyData2.getType());
         }
         catch (RepositoryException e)
         {
            return 0;
         }
         switch (propertyData1.getType())
         {
            case PropertyType.STRING :
            case PropertyType.REFERENCE :
               // case PropertyType.WEAKREFERENCE:
               // case PropertyType.URI:
            case PropertyType.PATH :
            case PropertyType.NAME :
            case ExtendedPropertyType.PERMISSION :
               if (LOG.isDebugEnabled())
                  LOG.debug(value1.getString() + "=" + value1.getString().compareTo(value2.getString()) + " = "
                     + value2.getString());
               return value1.getString().compareTo(value2.getString());
            case PropertyType.BINARY :
               return new Long(((BaseValue)value1).getLength()).compareTo(((BaseValue)value2).getLength());
            case PropertyType.BOOLEAN :
               return new Boolean(value1.getBoolean()).compareTo(value2.getBoolean());
            case PropertyType.LONG :
               return new Long(value1.getLong()).compareTo(value2.getLong());
            case PropertyType.DOUBLE :
               return new Double(value1.getDouble()).compareTo(value2.getDouble());
            case PropertyType.DATE :
               return value1.getDate().compareTo(value2.getDate());
               // case PropertyType.DECIMAL:
               // return value1.getDecimal().compareTo(value2.getDecimal());
         }
      }
      else
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Order by " + order.getOperand() + " doesn't suported");
      }
      return 0;
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
    * @param vData ValueData
    * @param type integer
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
