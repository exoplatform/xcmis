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
import org.xcmis.spi.Choice;
import org.xcmis.spi.impl.ChoiceImpl;

import java.math.BigInteger;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisChoiceIntegerTypeElementWrapper.java 2279 2009-07-23
 *          11:47:50Z sunman $ Jul 16, 2009
 */
public class ChoiceIntegerElement extends ChoiceElement<Choice<BigInteger>>
{

   /**
    * Instantiates a new choice integer element.
    * 
    * @param internal the internal
    */
   public ChoiceIntegerElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new choice integer element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ChoiceIntegerElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   public void build(Choice<BigInteger> choice)
   {
      if (choice != null)
      {
         super.build(choice);
         // VALUES
         if (choice.getValues() != null && choice.getValues().length > 0)
         {
            for (BigInteger v : choice.getValues())
            {
               if (v != null)
                  addSimpleExtension(AtomCMIS.VALUE, v.toString());
            }
         }
         // CHOICE
         if (choice.getChoices() != null && choice.getChoices().size() > 0)
         {
            for (Choice<BigInteger> ch : choice.getChoices())
            {
               ExtensibleElementWrapper el = addExtension(AtomCMIS.CHOICE);
               new ChoiceIntegerElement(el).build(ch);
            }
         }
      }
   }

   public Choice<BigInteger> getChoice()
   {
      ChoiceImpl<BigInteger> result = new ChoiceImpl<BigInteger>();
      // VALUES
      List<Element> values = getExtensions(AtomCMIS.VALUE);
      if (values != null && values.size() > 0)
      {
         BigInteger[] array = new BigInteger[values.size()];
         int i = 0;
         for (Element element : values)
         {
            array[i] = new BigInteger(element.getText());
            i++;
         }
         result.setValues(array);
      }
      // CHOICE
      List<ExtensibleElementWrapper> choices = getExtensions(AtomCMIS.CHOICE);
      if (choices != null && choices.size() > 0)
         for (ExtensibleElementWrapper choiceIntegerElement : choices)
         {
            result.getChoices().add(new ChoiceIntegerElement(choiceIntegerElement).getChoice());
         }
      return result;
   }

}
