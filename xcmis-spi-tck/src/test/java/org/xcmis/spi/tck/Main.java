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

import java.util.Enumeration;

import junit.framework.TestFailure;

import junit.framework.TestResult;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

/**
 * xCMIS SPI TCK entry point.
 *
 * Creates an test suite with TCK classes, and run it with results checking. 
 * Used only for TCK launching from the command line.
 *
 * @version $Id$
 */
public class Main
{
   
   public static void main(String[] args){
      TestSuite suite = new TestSuite("All tests");
      TestResult res = new TestResult();
      suite.addTest(new JUnit4TestAdapter(RepositoryTest.class));
      suite.addTest(new JUnit4TestAdapter(NavigationTest.class));
      suite.addTest(new JUnit4TestAdapter(ObjectTest.class));
      suite.addTest(new JUnit4TestAdapter(RelationshipTest.class));
      suite.addTest(new JUnit4TestAdapter(MultifilingTest.class));
      suite.addTest(new JUnit4TestAdapter(DiscoveryTest.class));
      suite.addTest(new JUnit4TestAdapter(ACLTest.class));
      suite.addTest(new JUnit4TestAdapter(PolicyTest.class));
      suite.addTest(new JUnit4TestAdapter(VersioningTest.class));
      suite.run(res);
      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
      System.out.println("Test running resluts: errors: " + res.errorCount() + ", failures: " + res.failureCount());
      if (res.errorCount() > 0){
         Enumeration<TestFailure> en = res.errors();
         while (en.hasMoreElements()){
            TestFailure one = en.nextElement();
            System.out.println(one.exceptionMessage());
            System.out.println(one.trace());
         }
      }
      if (res.failureCount() > 0){
         Enumeration<TestFailure> en = res.failures();
         while (en.hasMoreElements()){
            TestFailure one = en.nextElement();
            System.out.println(one.exceptionMessage());
            System.out.println(one.trace());
         }
      }

   }
}
