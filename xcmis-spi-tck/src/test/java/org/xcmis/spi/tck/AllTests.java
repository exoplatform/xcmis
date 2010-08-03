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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 


@RunWith(value = Suite.class)
@SuiteClasses(value = {ACLTest.class, DiscoveryTest.class, MultifilingTest.class, NavigationTest.class,
   ObjectTest.class, RepositoryTest.class, VersioningTest.class, RelationshipTest.class, PolicyTest.class})
public class AllTests
{

//   protected static List<String> passedTests = new ArrayList<String>();
//
//   protected static List<String> failedTests = new ArrayList<String>();
//
//   protected static List<String> skippedTests = new ArrayList<String>();
//   
//   protected static Map<String, String> results = new TreeMap<String,String>();
//
//   @AfterClass
//   public static void doReport()
//   {
//      for (Map.Entry<String, String> e : results.entrySet()){
//         if (e.getValue() == null)
//            passedTests.add(e.getKey());
//         else if (e.getValue().equalsIgnoreCase("Not supported by storage;"))
//            skippedTests.add(e.getKey());
//         else
//            failedTests.add(e.getKey());
//      }      
//      
//      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//      System.out.println("          TCK RESULTS : ");
//      System.out.println("          Passed tests:  " + passedTests.size());
//      System.out.println("          Failed tests:   " + failedTests.size());
//      System.out.println("          Skipped tests:  " + skippedTests.size());
//      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//      generateXDOC();
//   }
   
   
//   public static void generateXDOC(){
//         
//      try {
//         DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
//         DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
//        Document dataDoc = docbuilder.newDocument();
//        
//        Element root = dataDoc.createElement("document");
//        root.setAttribute("xmlns", "http://maven.apache.org/XDOC/2.0");
//        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//        root.setAttribute("xsi:schemaLocation", "http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd");
//        
//        Element props = dataDoc.createElement("properties");
//        Element title = dataDoc.createElement("title");
//        title.setTextContent("TCK Results");
//        Element author = dataDoc.createElement("author");
//        author.setTextContent("xCMIS");
//        props.appendChild(title);
//        props.appendChild(author);
//        //Element head = dataDoc.createElement("head");
//        root.appendChild(props);
//        //root.appendChild(head);
//        
//        Element body = dataDoc.createElement("body");
//        Element table = dataDoc.createElement("table");
//        Element tr1 = dataDoc.createElement("tr");
//        Element td1 = dataDoc.createElement("td");
//        Element font = dataDoc.createElement("font");
//        font.setAttribute("color", "#1209b8");
//        Element b = dataDoc.createElement("b");
//        b.setTextContent("Test FQN");
//        font.appendChild(b);
//        td1.appendChild(font);
//        Element td2 = dataDoc.createElement("td");
//        Element font2 = dataDoc.createElement("font");
//        font2.setAttribute("color", "#1209b8");
//        Element b2 = dataDoc.createElement("b");
//        b2.setTextContent("Test Result");
//        font2.appendChild(b2);
//        td2.appendChild(font2);
//        
//        Element td3 = dataDoc.createElement("td");
//        Element font3 = dataDoc.createElement("font");
//        font3.setAttribute("color", "#1209b8");
//        Element b3 = dataDoc.createElement("b");
//        b3.setTextContent("Reason");
//        font3.appendChild(b3);
//        td3.appendChild(font3);
//        
//        tr1.appendChild(td1);
//        tr1.appendChild(td2);
//        tr1.appendChild(td3);
//        table.appendChild(tr1);
//        
//        
//        
//        for (Map.Entry<String, String> e : results.entrySet()){
//           if (e.getValue() == null)
//           {
//            Element tr = dataDoc.createElement("tr");
//            Element td = dataDoc.createElement("td");
//            td.setTextContent(e.getKey());
//            Element tdd = dataDoc.createElement("td");
//            Element tdd2 = dataDoc.createElement("td");
//            tdd2.setTextContent(" - ");
//            Element font5 = dataDoc.createElement("font");
//            font5.setAttribute("color", "#09b822");
//            font5.setTextContent("PASSED");
//            tdd.appendChild(font5);
//            
//            tr.appendChild(td);
//            tr.appendChild(tdd);
//            tr.appendChild(tdd2);
//            table.appendChild(tr);
//           }
//              
//           else if (e.getValue().equalsIgnoreCase("Not supported by storage;"))
//           {
//              Element tr = dataDoc.createElement("tr");
//              Element td = dataDoc.createElement("td");
//              td.setTextContent(e.getKey());
//              Element tdd = dataDoc.createElement("td");
//              Element tdd2 = dataDoc.createElement("td");
//              tdd2.setTextContent("Not supported by storage;");
//              Element font5 = dataDoc.createElement("font");
//              font5.setAttribute("color", "#837d7d");
//              font5.setTextContent("SKIPPED");
//              tdd.appendChild(font5);
//              
//              tr.appendChild(td);
//              tr.appendChild(tdd);
//              tr.appendChild(tdd2);
//              table.appendChild(tr);
//           }
//              
//           else
//           {
//              Element tr = dataDoc.createElement("tr");
//              Element td = dataDoc.createElement("td");
//              td.setTextContent(e.getKey());
//              Element tdd = dataDoc.createElement("td");
//              Element tdd2 = dataDoc.createElement("td");
//              tdd2.setTextContent(e.getValue());
//              Element font5 = dataDoc.createElement("font");
//              font5.setAttribute("color", "#c40000");
//              font5.setTextContent("FAILED");
//              tdd.appendChild(font5);
//              
//              tr.appendChild(td);
//              tr.appendChild(tdd);
//              tr.appendChild(tdd2);
//              table.appendChild(tr);
//           }
//        }      

//        for (String one : passedTests){
//           Element tr = dataDoc.createElement("tr");
//           Element td = dataDoc.createElement("td");
//           td.setTextContent(one);
//           Element tdd = dataDoc.createElement("td");
//           Element font5 = dataDoc.createElement("font");
//           font5.setAttribute("color", "#09b822");
//           font5.setTextContent("PASSED");
//           tdd.appendChild(font5);
//           
//           tr.appendChild(td);
//           tr.appendChild(tdd);
//           
//           table.appendChild(tr);
//        }
//        
//        for (String one : failedTests){
//           Element tr = dataDoc.createElement("tr");
//           Element td = dataDoc.createElement("td");
//           td.setTextContent(one);
//           Element tdd = dataDoc.createElement("td");
//           Element font3 = dataDoc.createElement("font");
//           font3.setAttribute("color", "#c40000");
//           font3.setTextContent("FAILED");
//           tdd.appendChild(font3);
//           tr.appendChild(td);
//           tr.appendChild(tdd);
//           table.appendChild(tr);
//        }
//
//
//        for (String one : skippedTests){
//           Element tr = dataDoc.createElement("tr");
//           Element td = dataDoc.createElement("td");
//           td.setTextContent(one);
//           Element tdd = dataDoc.createElement("td");
//           Element font3 = dataDoc.createElement("font");
//           font3.setAttribute("color", "#837d7d");
//           font3.setTextContent("SKIPPED");
//           tdd.appendChild(font3);
//           tr.appendChild(td);
//           tr.appendChild(tdd);
//           table.appendChild(tr);
//        }

//        body.appendChild(table);
//        root.appendChild(body);
//        dataDoc.appendChild(root);
//        TransformerFactory tFactory =
//           TransformerFactory.newInstance();
//         Transformer transformer = tFactory.newTransformer();
//
//        DOMSource source = new DOMSource(dataDoc);
//        
//        URL url = AllTests.class.getResource("/xdoc/tck-result.xml");
//        File path  = new File (url.toURI()); 
//        FileOutputStream str = new FileOutputStream(path, false);
//        StreamResult result = new StreamResult(str);
//        transformer.transform(source, result);
//
//       } catch (Exception e) {
//         System.out.println("Problem creating document: "); e.printStackTrace();
//       }
//
//     
//      }
}
