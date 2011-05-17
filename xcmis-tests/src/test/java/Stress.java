/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import junit.framework.TestCase;

import org.ow2.bonita.DocumentAlreadyExistsException;
import org.ow2.bonita.DocumentNotFoundException;
import org.ow2.bonita.DocumentationCreationException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.CMISDocumentManager;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;

/**
 * @author Baptiste Mesta
 * 
 */
public class Stress extends TestCase {

 
  private static DocumentationManager manager;
  
  private Integer filesNum;

  public static void setUpClass() {}

  public void setUp() {
     
     String cmisRepositoryUrl = System.getProperty("cmisRepositoryUrl");
     String cmisRepositoryId = System.getProperty("cmisRepositoryId");
     String cmisUsername = System.getProperty("cmisUsername");
     String cmisPassword = System.getProperty("cmisPassword");

     if (cmisRepositoryUrl == null
           || "${cmisRepositoryUrl}".intern().equals(cmisRepositoryUrl)) {
        throw new RuntimeException(
              "setUp: you should set the system property 'cmisRepositoryUrl'");
     }
     if (cmisRepositoryId == null
           || "${cmisRepositoryId}".intern().equals(cmisRepositoryId)) {
        throw new RuntimeException(
              "setUp: you should set the system property 'cmisRepositoryId'");
     }
     if (cmisUsername == null
           || "${cmisUsername}".intern().equals(cmisUsername)) {
        throw new RuntimeException(
              "setUp: you should set the system property 'cmisUsername'");
     }
     if (cmisPassword == null
           || "${cmisPassword}".intern().equals(cmisPassword)) {
        throw new RuntimeException(
              "setUp: you should set the system property 'cmisPassword'");
     }

     System.out.println("\t cmisRepositoryUrl = " + cmisRepositoryUrl
           + ",\n\t repositoryId = " + cmisRepositoryId + ", userId = "
           + cmisUsername + ", password = " + cmisPassword);

     manager = new CMISDocumentManager("ATOM", cmisRepositoryUrl,
           cmisRepositoryId, true, cmisUsername, cmisPassword);
     Folder rootFolder = manager.getRootFolder();
     try {
        manager.clear();
     } catch (DocumentNotFoundException e) {
        e.printStackTrace();
     }
     
     
    filesNum = Integer.parseInt(System.getProperty("filesNum"));
    
    System.out.println("setUp property filesNum = " + filesNum);
     
  }

  
  public void testStress() throws DocumentAlreadyExistsException, DocumentationCreationException, InterruptedException {
    System.out.println("start");
    int nbFiles = 0;
    byte[] bytes = "fskqjsghnisrb,ùazel,f sdlkg,dlkgj aù dpfl;zaùfdskgdklmdslkngf dslkgn sdù akdgmqslgsd mskdgfd".getBytes();
    long max0 = 10;
    long max1 = 10;
    long max2 = 10;
    
    if (filesNum != null && filesNum > 1000) {
      max0 = filesNum / 100;
    }
    System.out.println("Stress.testStress, files num = " + max0 * max1 * max2);
    
    for (int i = 1; i <= max0; i++) {
      System.out.println("-==== p " + i + "/" + max0 + " ====-");
      ProcessDefinitionUUID pdef = new ProcessDefinitionUUID("procName",String.valueOf(i));
      for (int j = 1; j <= max1; j++) {
        ProcessInstanceUUID idef = new ProcessInstanceUUID(pdef, j);
        for (int k = 1; k <= max2; k++) {
          manager.createDocument("myDocument"+i+j+k, pdef, idef, "theFile"+i+j+k+".txt", "plain/text", bytes);
          nbFiles ++ ;
        }
        System.out.println("-==== " + nbFiles + " files ====-");
      }
    }
  }


}
