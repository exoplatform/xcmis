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
package org.xcmis.spi.tck;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.junit.runners.Suite.SuiteClasses;

//New manner;
//@RunWith(value = Suite.class)
//@SuiteClasses(value = {ACLTest.class, DiscoveryTest.class, MultifilingTest.class, NavigationTest.class,
//   ObjectTest.class, RepositoryTest.class, VersioningTest.class, RelationshipTest.class, PolicyTest.class})
public class AllTests
{
   //Old manner:
   public static junit.framework.Test suite() { 
      TestSuite suite = new TestSuite("All tests");
      suite.addTest(new JUnit4TestAdapter(ACLTest.class));
      suite.addTest(new JUnit4TestAdapter(DiscoveryTest.class));
      suite.addTest(new JUnit4TestAdapter(MultifilingTest.class));
      suite.addTest(new JUnit4TestAdapter(NavigationTest.class));
      suite.addTest(new JUnit4TestAdapter(ObjectTest.class));
      suite.addTest(new JUnit4TestAdapter(RepositoryTest.class));
      suite.addTest(new JUnit4TestAdapter(VersioningTest.class));
      suite.addTest(new JUnit4TestAdapter(RelationshipTest.class));
      suite.addTest(new JUnit4TestAdapter(PolicyTest.class));
      return suite;
  }
   }
