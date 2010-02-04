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
import org.xcmis.atom.CmisUriTemplateType;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UriTemplateTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new uri template type element.
    * 
    * @param internal the internal
    */
   public UriTemplateTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new uri template type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public UriTemplateTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }
   
   /**
    * Gets the template.
    * 
    * @return the template
    */
   public String getTemplate()
   {
      return getText(AtomCMIS.TEMPLATE);
   }
   
   /**
    * Gets the type.
    * 
    * @return the type
    */
   public String getType()
   {
      return getText(AtomCMIS.TYPE);
   }
   
   /**
    * Gets the mediatype.
    * 
    * @return the mediatype
    */
   public String getMediatype()
   {
      return getText(AtomCMIS.MEDIATYPE);
   }

   /**
    * Gets the cmis template.
    * 
    * @return the cmis template
    */
   public CmisUriTemplateType getCmisTemplate()
   {
      CmisUriTemplateType template = new CmisUriTemplateType();
      template.setTemplate(getSimpleExtension(AtomCMIS.TEMPLATE));
      template.setType(getSimpleExtension(AtomCMIS.TYPE));
      template.setMediatype(getSimpleExtension(AtomCMIS.MEDIATYPE));
      return template;
   }

   /**
    * Builds the element.
    * 
    * @param template the template
    */
   public void build(CmisUriTemplateType template)
   {
      if (template != null)
      {
         addSimpleExtension(AtomCMIS.TEMPLATE, template.getTemplate());
         addSimpleExtension(AtomCMIS.TYPE, template.getType());
         addSimpleExtension(AtomCMIS.MEDIATYPE, template.getMediatype());
      }
   }
   
   /**
    * Gets the text.
    * 
    * @param elName the el name
    * 
    * @return the text
    */
   protected String getText(QName elName)
   {
      Element el = getExtension(elName);
      if (el != null)
         return el.getText();
      return null;
   }

}
