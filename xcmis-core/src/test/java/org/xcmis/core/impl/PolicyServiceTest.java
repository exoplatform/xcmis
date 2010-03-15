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

package org.xcmis.core.impl;

import org.xcmis.core.CmisObjectType;
import org.xcmis.core.PolicyService;
import org.xcmis.core.impl.PolicyServiceImpl;
import org.xcmis.spi.object.Entry;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PolicyServiceTest.java 2 2010-02-04 17:21:49Z andrew00x $
 *
 */
public class PolicyServiceTest extends BaseTest
{

   private PolicyService policyService;

   private Entry policy;

   private Entry doc;

   public void setUp() throws Exception
   {
      super.setUp();
      policyService = new PolicyServiceImpl(repositoryService, propertyService);
      policy = createPolicy(testFolder, "policy1");
      doc = createDocument(testFolder, "doc1", null);
   }

   public void tearDown() throws Exception
   {
      doc.removePolicy(policy);
      super.tearDown();
   }

   public void testApplyPolicy() throws Exception
   {
      policyService.applyPolicy(repositoryId, policy.getObjectId(), doc.getObjectId());
      assertEquals(1, doc.getAppliedPolicies().size());
   }

   public void testGetPolicy() throws Exception
   {
      doc.applyPolicy(policy);
      doc.save();
      List<CmisObjectType> policies = policyService.getAppliedPolicies(repositoryId, doc.getObjectId(), null);
      assertEquals(1, policies.size());
   }

   public void testRemovePolicy() throws Exception
   {
      doc.applyPolicy(policy);
      doc.save();
      assertEquals(1, doc.getAppliedPolicies().size());
      policyService.removePolicy(repositoryId, policy.getObjectId(), doc.getObjectId());
      assertEquals(0, doc.getAppliedPolicies().size());
   }
}
