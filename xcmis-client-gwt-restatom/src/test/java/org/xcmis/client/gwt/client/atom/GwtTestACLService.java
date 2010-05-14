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

package org.xcmis.client.gwt.client.atom;

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.acl.AccessControlEntry;
import org.xcmis.client.gwt.client.model.acl.AccessControlList;
import org.xcmis.client.gwt.client.unmarshallers.parser.ACLParser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestACLService extends GWTTestCase
{

   private String aclResponse =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
         + "<cmis:acl xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:cmism=\"http://docs.oasis-open.org/ns/cmis/messaging/200908/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:app=\"http://www.w3.org/2007/app\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\">"
         + "<cmis:permission>" + "<cmis:principal>" + "<cmis:principalId>root</cmis:principalId>" + "</cmis:principal>"
         + "<cmis:permission>cmis:all</cmis:permission>" + "<cmis:direct>true</cmis:direct>" + "</cmis:permission>"
         + "<cmis:permission>" + "<cmis:principal>" + "<cmis:principalId>Makis</cmis:principalId>"
         + "</cmis:principal>" + "<cmis:permission>cmis:read</cmis:permission>"
         + "<cmis:permission>cmis:write</cmis:permission>" + "<cmis:direct>true</cmis:direct>" + "</cmis:permission>"
         + "</cmis:acl>";

   public void testGetACL()
   {
      Document doc = XMLParser.parse(aclResponse);
      AccessControlList accessControlList = new AccessControlList();
      Node ACLNode = doc.getElementsByTagName(CMIS.ACL).item(0);
      
      ACLParser.parse(ACLNode, accessControlList);
      
      assertEquals(2, accessControlList.getPermission().size());
      AccessControlEntry accessControlEntry = accessControlList.getPermission().get(0);
      assertEquals(1, accessControlEntry.getPermissions().size());
      assertEquals("cmis:all", accessControlEntry.getPermissions().iterator().next());
      assertEquals("root", accessControlEntry.getPrincipal().getPrincipalId());
      assertTrue(accessControlEntry.isDirect());
      
      accessControlEntry = accessControlList.getPermission().get(1);
      assertEquals(2, accessControlEntry.getPermissions().size());
      assertEquals("Makis", accessControlEntry.getPrincipal().getPrincipalId());
      assertTrue(accessControlEntry.isDirect());
   }

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.xcmis.client.gwt.CmisClientFrameworkJUnit";
   }

}
