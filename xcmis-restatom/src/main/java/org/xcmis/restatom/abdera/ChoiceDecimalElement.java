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
import org.xcmis.spi.model.Choice;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: ChoiceDecimalTypeElement.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $ Jul 16, 2009
 */
public class ChoiceDecimalElement extends ChoiceElement<Choice<BigDecimal>>
{

   /**
    * Instantiates a new choice decimal element.
    *
    * @param internal the internal
    */
   public ChoiceDecimalElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new choice decimal element.
    *
    * @param factory the factory
    * @param qname the qname
    */
   public ChoiceDecimalElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void build(Choice<BigDecimal> choice)
   {
      if (choice != null)
      {
         super.build(choice);
         // VALUES
         if (choice.getValues() != null && choice.getValues().length > 0)
         {
            for (BigDecimal v : choice.getValues())
            {
               if (v != null)
               {
                  addSimpleExtension(AtomCMIS.VALUE, v.toString());
               }
            }
         }
         // CHOICE
         if (choice.getChoices() != null && choice.getChoices().size() > 0)
         {
            for (Choice<BigDecimal> ch : choice.getChoices())
            {
               ExtensibleElementWrapper el = addExtension(AtomCMIS.CHOICE);
               new ChoiceDecimalElement(el).build(ch);
            }
         }
      }
   }

   @Override
   public Choice<BigDecimal> getChoice()
   {
      Choice<BigDecimal> result = new Choice<BigDecimal>();
      // VALUES
      List<Element> values = getExtensions(AtomCMIS.VALUE);
      if (values != null && values.size() > 0)
      {
         BigDecimal[] array = new BigDecimal[values.size()];
         int i = 0;
         for (Element element : values)
         {
            array[i] = new BigDecimal(element.getText());
            i++;
         }
         result.setValues(array);
      }
      // CHOICE
      List<ExtensibleElementWrapper> choices = getExtensions(AtomCMIS.CHOICE);
      if (choices != null && choices.size() > 0)
      {
         for (ExtensibleElementWrapper choiceDecimalElement : choices)
         {
            result.getChoices().add(new ChoiceDecimalElement(choiceDecimalElement).getChoice());
         }
      }
      return result;
   }

}
