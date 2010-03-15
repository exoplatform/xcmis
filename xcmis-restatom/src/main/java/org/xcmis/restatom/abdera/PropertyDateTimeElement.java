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
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.object.impl.DateTimeProperty;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyDateTimeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PropertyDateTimeElement extends PropertyElement<DateTimeProperty>
{

   /**
    * Instantiates a new property date time element.
    * 
    * @param internal the internal
    */
   public PropertyDateTimeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new property date time element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertyDateTimeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void build(DateTimeProperty value)
   {
      if (value != null)
      {
         super.build(value);
         List<Calendar> listCalendar = value.getValues();
         if (listCalendar != null && listCalendar.size() > 0)
         {
            for (Calendar v : listCalendar)
               addSimpleExtension(AtomCMIS.VALUE, AtomUtils.getAtomDate(v));
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public DateTimeProperty getProperty()
   {
      DateTimeProperty d = new DateTimeProperty();
      processPropertyElement(d);
      if (getElements() != null && getElements().size() > 0)
      {
         for (Element el : getElements())
            d.getValues().add(AtomUtils.parseCalendar(el.getText()));
      }
      return d;
   }

}
