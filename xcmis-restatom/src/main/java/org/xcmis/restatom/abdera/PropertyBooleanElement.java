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
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PropertyBooleanElement extends PropertyElement<CmisPropertyBoolean>
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
   public void build(CmisProperty value)
   {
      if (value != null)
      {
         super.build(value);

         List<Boolean> listBoolean = ((CmisPropertyBoolean)value).getValue();
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
   public CmisPropertyBoolean getProperty()
   {
      CmisPropertyBoolean b = new CmisPropertyBoolean();
      processPropertyElement(b);
      if (getElements() != null && getElements().size() > 0)
      {
         for (Element el : getElements())
            b.getValue().add(Boolean.valueOf(el.getText()));
      }
      return b;
   }

}
