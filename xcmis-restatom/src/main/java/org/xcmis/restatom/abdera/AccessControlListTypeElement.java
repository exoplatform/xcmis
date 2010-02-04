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
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.restatom.AtomCMIS;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class AccessControlListTypeElement extends ExtensibleElementWrapper
{
   
   /**
    * Instantiates a new access control list type element.
    * 
    * @param internal the internal
    */
   public AccessControlListTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new access control list type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public AccessControlListTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param value the value
    */
   public void build(CmisAccessControlListType value)
   {
      if (value != null)
      {
         declareNS(org.apache.abdera.util.Constants.ATOM_NS, "atom");
         declareNS(org.apache.abdera.util.Constants.APP_NS, "app");

         if (value.getPermission() != null && value.getPermission().size() > 0)
         {
            for (CmisAccessControlEntryType one : value.getPermission())
            {
               AccessControlEntryTypeElement el = addExtension(AtomCMIS.PERMISSION);
               el.build(one);
            }
         }
      }
   }

   /**
    * Gets the CmisAccessControlListType.
    * 
    * @return CmisAccessControlListType
    */
   public CmisAccessControlListType getACL()
   {
      CmisAccessControlListType acl = new CmisAccessControlListType();
      List<AccessControlEntryTypeElement> els = getExtensions(AtomCMIS.PERMISSION);
      if (els != null && els.size() > 0)
      {
         for (AccessControlEntryTypeElement el : els)
         {
            CmisAccessControlEntryType ace = el.getACE();
            acl.getPermission().add(ace);
         }
      }
      return acl;
   }
}
