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
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.AfterClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 


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
      generateXDOC();
   }
   
   
   public static void generateXDOC(){
         
      try {
         DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
        Document dataDoc = docbuilder.newDocument();
        
        Element root = dataDoc.createElement("document");
        root.setAttribute("xmlns", "http://maven.apache.org/XDOC/2.0");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd");
        
        Element props = dataDoc.createElement("properties");
        Element title = dataDoc.createElement("title");
        title.setTextContent("TCK Results");
        Element author = dataDoc.createElement("author");
        author.setTextContent("xCMIS");
        props.appendChild(title);
        props.appendChild(author);
        //Element head = dataDoc.createElement("head");
        root.appendChild(props);
        //root.appendChild(head);
        
        Element body = dataDoc.createElement("body");
        Element table = dataDoc.createElement("table");
        Element tr1 = dataDoc.createElement("tr");
        Element td1 = dataDoc.createElement("td");
        Element font = dataDoc.createElement("font");
        font.setAttribute("color", "#DE2222");
        Element b = dataDoc.createElement("b");
        b.setTextContent("Test FQN");
        font.appendChild(b);
        td1.appendChild(font);
        Element td2 = dataDoc.createElement("td");
        Element font2 = dataDoc.createElement("font");
        font2.setAttribute("color", "#DE2222");
        Element b2 = dataDoc.createElement("b");
        b2.setTextContent("Test Result");
        font2.appendChild(b2);
        td2.appendChild(font2);
        tr1.appendChild(td1);
        tr1.appendChild(td2);
        table.appendChild(tr1);
        

        for (String one : passedTests){
           Element tr = dataDoc.createElement("tr");
           Element td = dataDoc.createElement("td");
           td.setTextContent(one);
           Element tdd = dataDoc.createElement("td");
           tdd.setTextContent("PASSED");
           tr.appendChild(td);
           tr.appendChild(tdd);
           table.appendChild(tr);
        }
        
        for (String one : failedTests){
           Element tr = dataDoc.createElement("tr");
           Element td = dataDoc.createElement("td");
           td.setTextContent(one);
           Element tdd = dataDoc.createElement("td");
           tdd.setTextContent("FAILED");
           tr.appendChild(td);
           tr.appendChild(tdd);
           table.appendChild(tr);
        }


        for (String one : skippedTests){
           Element tr = dataDoc.createElement("tr");
           Element td = dataDoc.createElement("td");
           td.setTextContent(one);
           Element tdd = dataDoc.createElement("td");
           tdd.setTextContent("SKIPPED");
           tr.appendChild(td);
           tr.appendChild(tdd);
           table.appendChild(tr);
        }

        body.appendChild(table);
        root.appendChild(body);
        dataDoc.appendChild(root);
        TransformerFactory tFactory =
           TransformerFactory.newInstance();
         Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(dataDoc);
        
        URL url = AllTests.class.getResource("/xdoc/tck-result.xml");
        File path  = new File (url.toURI()); 
        FileOutputStream str = new FileOutputStream(path, false);
        StreamResult result = new StreamResult(str);
        transformer.transform(source, result);

       } catch (Exception e) {
         System.out.println("Problem creating document: "); e.printStackTrace();
       }

     
      }
}
