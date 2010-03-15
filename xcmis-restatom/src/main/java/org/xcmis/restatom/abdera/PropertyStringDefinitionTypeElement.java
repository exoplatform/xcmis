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
import org.apache.abdera.model.ExtensibleElement;
import org.xcmis.core.CmisChoiceString;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisPropertyStringDefinitionTypeElementWrapper.java 2279
 *          2009-07-23 11:47:50Z sunman $ Jul 15, 2009
 */
public class PropertyStringDefinitionTypeElement extends
   PropertyDefinitionTypeElement<CmisPropertyStringDefinitionType>
{

   /**
    * Instantiates a new property string definition type element.
    * 
    * @param internal the internal
    */
   public PropertyStringDefinitionTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new property string definition type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertyStringDefinitionTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param propdef the propdef
    */
   @Override
   public void build(CmisPropertyStringDefinitionType propdef)
   {
      if (propdef != null)
      {
         
         super.build(propdef);
         
         if (propdef.getDefaultValue() != null && propdef.getDefaultValue().getValue().size() > 0)
         {
            ExtensibleElement defaultValue = addExtension(AtomCMIS.DEFAULT_VALUE);
            for (String value : propdef.getDefaultValue().getValue())
            {
               if (value != null)
                  defaultValue.addSimpleExtension(AtomCMIS.VALUE, value);
            }
         }

         if (propdef.getMaxLength() != null)
            addSimpleExtension(AtomCMIS.MAX_LENGTH, propdef.getMaxLength().toString());
         
         List<CmisChoiceString> choice = propdef.getChoice();
         if (choice != null && choice.size() > 0)
         {
            for (CmisChoiceString ch : choice)
            {
               ChoiceStringElement elChoice = addExtension(AtomCMIS.CHOICE_STRING);
               elChoice.build(ch);
            }
         }
      }
   }

}
