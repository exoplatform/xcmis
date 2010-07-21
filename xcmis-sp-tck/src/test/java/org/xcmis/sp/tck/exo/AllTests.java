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
package org.xcmis.sp.tck.exo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.AfterClass;

@RunWith(value = Suite.class)
@SuiteClasses(value = {ACLTest.class, DiscoveryTest.class, MultifilingTest.class, NavigationTest.class,
   ObjectTest.class, RepositoryTest.class, VersioningTest.class, RelationshipTest.class, PolicyTest.class})
public class AllTests
{

   protected static List<String> passedTests = new ArrayList<String>();

   protected static List<String> failedTests = new ArrayList<String>();

   protected static List<String> skippedTests = new ArrayList<String>();

   @AfterClass
   public static void doReport()
   {
      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
      System.out.println("          TCK RESULTS : ");
      System.out.println("          Passed tests:  " + passedTests.size());
      System.out.println("          Failed tests:   " + failedTests.size());
      System.out.println("          Skipped tests:  " + skippedTests.size());
      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
   }
}
