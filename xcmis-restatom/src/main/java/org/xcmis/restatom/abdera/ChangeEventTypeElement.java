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
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;

import javax.xml.datatype.XMLGregorianCalendar;
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
    * @param changeEventType the change event type
    */
   public void build(CmisChangeEventType changeEventType)
   {
      if (changeEventType != null)
      {
         if (changeEventType.getChangeType() != null)
            addSimpleExtension(AtomCMIS.CHANGE_TYPE, changeEventType.getChangeType().value());
         if (changeEventType.getChangeTime() != null)
         {
            XMLGregorianCalendar v = changeEventType.getChangeTime();
            addSimpleExtension(AtomCMIS.CHANGE_TIME, AtomUtils.getAtomDate(v.toGregorianCalendar()));
         }
      }
   }

}
