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

import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.result.AbstractResultSorterFactory;
import org.xcmis.search.result.ResultSorter;

import java.util.HashMap;

import javax.jcr.query.qom.Ordering;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class JcrResultSorterFactory extends AbstractResultSorterFactory
{

   /** The item data consumer. */
   private final ItemDataConsumer itemDataConsumer;

   /** The value factory. */
   private final ValueFactoryImpl valueFactory;

   /** The per query cache. */
   private HashMap<String, ItemData> perQueryCache;

   /** The location factory. */
   private LocationFactory locationFactory;

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param namespaceAccessor the namespace accessor
    */
   public JcrResultSorterFactory(final ItemDataConsumer itemDataConsumer, final NamespaceAccessor namespaceAccessor)
   {
      super();
      this.itemDataConsumer = itemDataConsumer;
      this.locationFactory = new LocationFactory(namespaceAccessor);
      this.valueFactory = new ValueFactoryImpl(locationFactory);
      this.perQueryCache = new HashMap<String, ItemData>();
   }

   /**
    * {@inheritDoc}
    */
   public ResultSorter getDefaultResultSorter(String[] selectorNames)
   {
      // TODO check array
      return new DocumentOrderResultSorter(itemDataConsumer, perQueryCache, selectorNames[0]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(LengthImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return new LengthResultSorter(itemDataConsumer, perQueryCache, valueFactory, (Ordering)context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      return new NodeLocalNameResultSorter(itemDataConsumer, perQueryCache, (Ordering)context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      return new NodeNameResultSorter(itemDataConsumer, perQueryCache, locationFactory, (Ordering)context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      return new PropertyValueResultSorter(itemDataConsumer, perQueryCache, (Ordering)context, valueFactory);
   }

}
