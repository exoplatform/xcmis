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
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlPrincipalTypeElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class AccessControlPrincipalTypeElement extends ExtensibleElementWrapper
{
   
   /**
    * Instantiates a new access control principal type element.
    * 
    * @param internal the internal
    */
   public AccessControlPrincipalTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new access control principal type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public AccessControlPrincipalTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    * 
    * @param value the value
    */
   public void build(CmisAccessControlPrincipalType value)
   {
      if (value != null)
         addSimpleExtension(AtomCMIS.PRINCIPAL_ID, value.getPrincipalId());
   }

   /**
    * Gets the principal.
    * 
    * @return the principal
    */
   public CmisAccessControlPrincipalType getPrincipal()
   {
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      Element principalId = getExtension(AtomCMIS.PRINCIPAL_ID);
      if (principalId != null)
         principal.setPrincipalId(principalId.getText());
      return principal;
   }

}
