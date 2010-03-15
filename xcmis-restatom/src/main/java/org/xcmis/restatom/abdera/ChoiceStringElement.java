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
import org.xcmis.core.CmisChoiceString;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisChoiceStringTypeElementWrapper.java 2279 2009-07-23
 *          11:47:50Z sunman $ Jul 16, 2009
 */
public class ChoiceStringElement extends ChoiceElement<CmisChoiceString>
{

   /**
    * Instantiates a new choice string element.
    * 
    * @param internal the internal
    */
   public ChoiceStringElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new choice string element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ChoiceStringElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   public void build(CmisChoiceString choice)
   {
      if (choice != null)
      {
         super.build(choice);
         if (choice.getValue() != null && choice.getValue().size() > 0)
         {
            for (String v : choice.getValue())
            {
               if (v != null)
                  addSimpleExtension(AtomCMIS.VALUE, v);
            }
         }
         if (choice.getChoice() != null && choice.getChoice().size() > 0)
         {
            for (CmisChoiceString ch : choice.getChoice())
            {
               ChoiceStringElement el = addExtension(AtomCMIS.CHOICE);
               el.build(ch);
            }
         }
      }
   }

}
