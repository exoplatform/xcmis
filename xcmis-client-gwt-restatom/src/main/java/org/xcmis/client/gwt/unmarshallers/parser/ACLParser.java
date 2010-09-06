/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.unmarshallers.parser;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.acl.AccessControlEntry;
import org.xcmis.client.gwt.model.acl.AccessControlList;
import org.xcmis.client.gwt.model.acl.AccessControlPrincipal;

import com.google.gwt.xml.client.Node;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class ACLParser
{
   
   /**
    * Constructor.
    */
   protected ACLParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse xml to get {@link AccessControlList}.
    * 
    * @param aclNode ACL node
    * @param acl ACL
    */
   public static void parse(Node aclNode, AccessControlList acl)
   {
      for (int i = 0; i < aclNode.getChildNodes().getLength(); i++)
      {
         Node node = aclNode.getChildNodes().item(i);
         if (node.getNodeName().equals(CMIS.CMIS_PERMISSION))
         {
            acl.getPermission().add(getACE(node));
         }
      }
   }

   /**
    * Get ACE from xml element.
    * 
    * @param aceNode ACE node
    * @return {@link CmisAccessControlEntryType}
    */
   public static AccessControlEntry getACE(Node aceNode)
   {
      AccessControlEntry accessControlEntry = new AccessControlEntry();
      for (int i = 0; i < aceNode.getChildNodes().getLength(); i++)
      {
         Node node = aceNode.getChildNodes().item(i);
         if (node.getNodeName().equals(CMIS.CMIS_PERMISSION))
         {
            String value = node.getFirstChild().getNodeValue();
            accessControlEntry.getPermissions().add(value);
         }
         else if (node.getNodeName().equals(CMIS.CMIS_DIRECT))
         {
            String value = node.getFirstChild().getNodeValue();
            accessControlEntry.setDirect(Boolean.parseBoolean(value));
         }
         else if (node.getNodeName().equals(CMIS.CMIS_PRINCIPAL))
         {
            Node childNode = node.getFirstChild();
            if (childNode.getNodeName().equals(CMIS.CMIS_PRINCIPAL_ID))
            {
               String value = childNode.getFirstChild().getNodeValue();
               AccessControlPrincipal principal = new AccessControlPrincipal();
               principal.setPrincipalId(value);
               accessControlEntry.setPrincipal(principal);
            }
         }
      }
      return accessControlEntry;
   }
}
