====== xCMIS Deployment procedure to an application server ====== 

For assembly "xCMIS" or "xCMIS Demo" with Tomcat server application:

  Make sure you have correct: 
   * Maven version 2.2.0 (or higher). 
   * Apache Tomcat 6.0.32+ application server distributive. 

# Build xCMIS Demo application and assembly it with Tomcat server application.
   * Run "mvn clean install" command within root project folder.
   * Run "mvn clean install -Dtomcat.distrib=/PATH/TO/TOMCAT/DISTRIBUTIVE" command within "xcmis-server-tomcat-demo" folder.
   * The result of that you'll have the Tomcat with xCMIS Demo web application archive bundle "xcmis-tomcat", placed at "xcmis-server-tomcat-demo/target".
   Run Tomcat
   * Go to xcmis-server-tomcat-demo/target/xcmis-tomcat/bin
   * On the Windows platform
     Open a DOS prompt command and type the command
     xcmis.bat run
   * On Unix/linux/cygwin/MacOSX
     Open a terminal and type the command:
     ./xcmis.sh run
     You may need to change the permission of all *.sh files in the tomcat/bin dir by using: chmod +x *.sh 
   Available services URLs:
   * Home page at the URL "http://localhost:8080/xcmis"
   * Demo CMIS Client http://localhost:8080/xcmis/xcmis-demo-gadget/GadgetWrapper.html
   * REST Atom services will be available at the URL "http://localhost:8080/xcmis/rest/cmisatom"
   * SOAP services should be available at the URL "http://localhost:8080/xcmis/cmisws"


# Build xCMIS server WAR.
   * Run "mvn clean install" command within root project folder.
   * The result of that you'll have the xCMIS web application archive "xcmis.war" placed at "xcmis-server-war/target".
   Assembly xCMIS with Tomcat server application
   * Run "mvn clean install -Dtomcat.distrib=/PATH/TO/TOMCAT/DISTRIBUTIVE" command within "xcmis-server-tomcat" folder.
   * The result of that you'll have the Tomcat with xCMIS web application archive bundle "xcmis-tomcat", placed at "xcmis-server-tomcat/target".
   Run Tomcat
   * Go to xcmis-server-tomcat/target/xcmis-tomcat/bin
   * On the Windows platform
     Open a DOS prompt command and type the command
     xcmis.bat run
   * On Unix/linux/cygwin/MacOSX
     Open a terminal and type the command:
     ./xcmis.sh run
     You may need to change the permission of all *.sh files in the tomcat/bin dir by using: chmod +x *.sh 
   Available services URLs:
   * Home page at the URL "http://localhost:8080/xcmis"
   * REST Atom services will be available at the URL "http://localhost:8080/xcmis/rest/cmisatom"
   * SOAP services should be available at the URL "http://localhost:8080/xcmis/cmisws"

  Default service credential: username is "root" and password is "exo".

