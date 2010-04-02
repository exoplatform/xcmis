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
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.model.ChangeInfo;
import org.xcmis.spi.model.ChangeType;
import org.xcmis.spi.model.impl.ChangeInfoImpl;

import java.util.Calendar;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ChangeEventTypeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ChangeEventTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new change event type element.
    * 
    * @param internal the internal
    */
   public ChangeEventTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new change event type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ChangeEventTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param changeInfo the change event type
    */
   public void build(ChangeInfo changeInfo)
   {
      if (changeInfo != null)
      {
         if (changeInfo.getChangeType() != null)
            addSimpleExtension(AtomCMIS.CHANGE_TYPE, changeInfo.getChangeType().value());
         if (changeInfo.getChangeTime() != null)
         {
            Calendar v = changeInfo.getChangeTime();
            addSimpleExtension(AtomCMIS.CHANGE_TIME, AtomUtils.getAtomDate(v));
         }
      }
   }

   public ChangeInfo getChangeInfo()
   {
      ChangeInfoImpl changeInfo = new ChangeInfoImpl();
      String changeTypeString = getSimpleExtension(AtomCMIS.CHANGE_TYPE);
      if (changeTypeString != null)
         changeInfo.setChangeType(ChangeType.fromValue(changeTypeString));
      String changeTimeString = getSimpleExtension(AtomCMIS.CHANGE_TIME);
      if (changeTimeString != null)
         changeInfo.setChangeTime(AtomUtils.parseCalendar(changeTimeString));
      return changeInfo;
   }
}
