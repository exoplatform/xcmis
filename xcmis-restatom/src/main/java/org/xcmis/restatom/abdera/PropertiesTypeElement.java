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
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.impl.property.PropertyFilter;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertiesTypeElement.java 47 2010-02-08 22:59:45Z andrew00x $
 */
public class PropertiesTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new properties type element.
    * 
    * @param internal the internal
    */
   public PropertiesTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new properties type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertiesTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Gets the properties.
    * 
    * @return the properties
    */
   public CmisPropertiesType getProperties()
   {
      List<Element> elements = getElements();
      CmisPropertiesType properties = new CmisPropertiesType();
      if (elements != null)
      {
         for (Element element : elements)
            properties.getProperty().add(((PropertyElement<?>)element).getProperty());
      }
      return properties;
   }

   /**
    * Builds the element.
    * 
    * @param properties the properties
    * @param filter the filter
    */
   public void build(CmisPropertiesType properties, PropertyFilter filter)
   {
      if (properties != null)
      {
         if (properties.getProperty() != null && properties.getProperty().size() > 0)
         {
            for (CmisProperty cmisProperty : properties.getProperty())
            {
               if (filter == null || filter.accept(cmisProperty.getQueryName()))
               {
                  PropertyElement<CmisProperty> el =
                     addExtension(CMISExtensionFactory.getElementName(cmisProperty.getClass()));
                  el.build(cmisProperty);
               }
            }
         }
      }
   }

   /**
    * Builds the element.
    * 
    * @param properties the properties
    */
   public void build(CmisPropertiesType properties)
   {
      if (properties != null)
         build(properties, null);
   }

}
