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
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.model.impl.BooleanProperty;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyBooleanElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PropertyBooleanElement extends PropertyElement<BooleanProperty>
{

   /**
    * Instantiates a new property boolean element.
    * 
    * @param internal the internal
    */
   public PropertyBooleanElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new property boolean element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertyBooleanElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void build(BooleanProperty value)
   {
      if (value != null)
      {
         super.build(value);
         List<Boolean> listBoolean = value.getValues();
         if (listBoolean != null && listBoolean.size() > 0)
         {
            for (Boolean v : listBoolean)
               addSimpleExtension(AtomCMIS.VALUE, v.toString());
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public BooleanProperty getProperty()
   {
      BooleanProperty b = new BooleanProperty();
      processPropertyElement(b);
      if (getElements() != null && getElements().size() > 0)
      {
         for (Element el : getElements())
            b.getValues().add(Boolean.valueOf(el.getText()));
      }
      return b;
   }

}
