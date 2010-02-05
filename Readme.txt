====== xCMIS Deployment procedure to an application server ======

For assembly xCMIS with Tomcat server application:

1. Make sure you have correct: 
  1.1. The xcmis root pom.xml. There should be the correct application server folder name or relative path (<xcmis.deploy.tomcat.dependency> property)
       pointed to the Tomcat 6.0.16.
  1.2. Maven version 2.0.9 (or higher).
2. Run "mvn clean install" command within "xcmis-server-tomcat" folder.
3. If the command has executed successfully, go to xcmis-tomcat bin directory placed at "./xcmis-server-tomcat/target/xcmis-tomcat/bin" and run "xcmis run" command.
4. Available services URLs:
  4.1 Home page at the URL "http://localhost:8080/xcmis"
  4.2 REST Atom services will be available at the URL "http://localhost:8080/xcmis/rest/cmisatom"
  4.3 SOAP services should be available at the URL "http://localhost:8080/xcmis/cmisws"
5. Default service credential: username is "root" and password is "exo".
