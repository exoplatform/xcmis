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
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class AccessControlEntryTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new access control entry type element.
    * 
    * @param internal the internal
    */
   public AccessControlEntryTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new access control entry type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public AccessControlEntryTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param value the value
    */
   public void build(CmisAccessControlEntryType value)
   {
      if (value != null)
      {
         AccessControlPrincipalTypeElement accessControlPrincipalTypeElement = addExtension(AtomCMIS.PRINCIPAL);
         accessControlPrincipalTypeElement.build(value.getPrincipal());

         if (value.getPermission() != null && value.getPermission().size() > 0)
         {
            for (String one : value.getPermission())
               addSimpleExtension(AtomCMIS.PERMISSION, one);
         }
         addSimpleExtension(AtomCMIS.DIRECT, value.isDirect() ? "true" : "false");
      }
   }

   /**
    * Gets the CmisAccessControlEntryType.
    * 
    * @return CmisAccessControlEntryType 
    */
   public CmisAccessControlEntryType getACE()
   {
      CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
      AccessControlPrincipalTypeElement principal = getExtension(AtomCMIS.PRINCIPAL);
      if (principal != null)
         ace.setPrincipal(principal.getPrincipal());
      List<Element> els = getExtensions(AtomCMIS.PERMISSION);
      if (els != null && els.size() > 0)
      {
         for (Element el : els)
            ace.getPermission().add(el.getText());
      }
      Element direct = getExtension(AtomCMIS.DIRECT);
      ace.setDirect(direct == null ? true : Boolean.parseBoolean(direct.getText()));
      return ace;
   }

}
