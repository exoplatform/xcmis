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
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ListOfIdsTypeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ListOfIdsTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new list of ids type element.
    * 
    * @param internal the internal
    */
   public ListOfIdsTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new list of ids type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ListOfIdsTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param value the value
    */
   public void build(CmisListOfIdsType value)
   {
      if (value != null)
      {
         List<String> list = value.getId();
         if (list != null && list.size() > 0)
         {
            for (String string : list)
               addSimpleExtension(AtomCMIS.ID, string);
         }
      }
   }
}
