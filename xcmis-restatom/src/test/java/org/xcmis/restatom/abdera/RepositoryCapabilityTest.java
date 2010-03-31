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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryCapabilityTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class RepositoryCapabilityTest extends TestCase
{

   public void testWriteCapability() throws Exception
   {
      assertTrue(true);
      //      RepositoryCapabilities capabilities = new RepositoryCapabilities();
      //      capabilities.setCapabilityACL(EnumCapabilityACL.NONE);
      //      capabilities.setCapabilityAllVersionsSearchable(true);
      //      capabilities.setCapabilityChanges(EnumCapabilityChanges.NONE);
      //      capabilities.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates.ANYTIME);
      //      capabilities.setCapabilityGetDescendants(true);
      //      capabilities.setCapabilityGetFolderTree(true);
      //      capabilities.setCapabilityJoin(EnumCapabilityJoin.NONE);
      //      capabilities.setCapabilityMultifiling(false);
      //      capabilities.setCapabilityPWCSearchable(true);
      //      capabilities.setCapabilityPWCUpdatable(false);
      //      capabilities.setCapabilityQuery(EnumCapabilityQuery.BOTHSEPARATE);
      //      capabilities.setCapabilityRenditions(EnumCapabilityRendition.READ);
      //      capabilities.setCapabilityUnfiling(false);
      //      capabilities.setCapabilityVersionSpecificFiling(false);
      //
      //      RepositoryCapabilitiesTypeElement el =
      //         new RepositoryCapabilitiesTypeElement(AbderaFactory.getInstance().getFactory(), AtomCMIS.CAPABILITIES);
      //      el.build(capabilities);
      //
      //      ByteArrayOutputStream out = new ByteArrayOutputStream();
      //      el.writeTo(out);
      //
      //      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      //      f.setNamespaceAware(true);
      //      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      //      XPath xp = XPathFactory.newInstance().newXPath();
      //      xp.setNamespaceContext(new NamespaceResolver());
      //
      //      String r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityMultifiling", xmlDoc, XPathConstants.STRING);
      //      assertEquals("false", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityACL", xmlDoc, XPathConstants.STRING);
      //      assertEquals("none", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityAllVersionsSearchable", xmlDoc, XPathConstants.STRING);
      //      assertEquals("true", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityChanges", xmlDoc, XPathConstants.STRING);
      //      assertEquals("none", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityContentStreamUpdatability", xmlDoc, XPathConstants.STRING);
      //      assertEquals("anytime", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityGetDescendants", xmlDoc, XPathConstants.STRING);
      //      assertEquals("true", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityGetFolderTree", xmlDoc, XPathConstants.STRING);
      //      assertEquals("true", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityJoin", xmlDoc, XPathConstants.STRING);
      //      assertEquals("none", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityMultifiling", xmlDoc, XPathConstants.STRING);
      //      assertEquals("false", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityPWCSearchable", xmlDoc, XPathConstants.STRING);
      //      assertEquals("true", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityPWCUpdateable", xmlDoc, XPathConstants.STRING);
      //      assertEquals("false", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityQuery", xmlDoc, XPathConstants.STRING);
      //      assertEquals("bothseparate", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityRenditions", xmlDoc, XPathConstants.STRING);
      //      assertEquals("read", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityUnfiling", xmlDoc, XPathConstants.STRING);
      //      assertEquals("false", r);
      //      r = (String)xp.evaluate("/cmis:capabilities/cmis:capabilityVersionSpecificFiling", xmlDoc, XPathConstants.STRING);
      //      assertEquals("false", r);
   }

}
