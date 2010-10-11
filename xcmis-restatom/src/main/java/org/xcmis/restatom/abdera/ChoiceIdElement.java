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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ChoiceIdElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ChoiceIdElement extends ChoiceElement<Choice<String>>
{

   /**
    * Instantiates a new choice id element.
    * 
    * @param internal the internal
    */
   public ChoiceIdElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new choice id element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ChoiceIdElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void build(Choice<String> choice)
   {
      if (choice != null)
      {
         super.build(choice);
         // VALUES
         if (choice.getValues() != null && choice.getValues().length > 0)
         {
            for (String v : choice.getValues())
            {
               if (v != null)
               {
                  addSimpleExtension(AtomCMIS.VALUE, v);
               }
            }
         }
         // CHOICE
         if (choice.getChoices() != null && choice.getChoices().size() > 0)
         {
            for (Choice<String> ch : choice.getChoices())
            {
               ExtensibleElementWrapper el = addExtension(AtomCMIS.CHOICE);
               new ChoiceIdElement(el).build(ch);
            }
         }
      }
   }

   @Override
   public Choice<String> getChoice()
   {
      Choice<String> result = new Choice<String>();
      // VALUES
      List<Element> values = getExtensions(AtomCMIS.VALUE);
      if (values != null && values.size() > 0)
      {
         String[] array = new String[values.size()];
         int i = 0;
         for (Element element : values)
         {
            array[i] = element.getText();
            i++;
         }
         result.setValues(array);
      }
      // CHOICE
      List<ExtensibleElementWrapper> choices = getExtensions(AtomCMIS.CHOICE);
      if (choices != null && choices.size() > 0)
      {
         for (ExtensibleElementWrapper choiceIdElement : choices)
         {
            result.getChoices().add(new ChoiceIdElement(choiceIdElement).getChoice());
         }
      }
      return result;
   }

}
