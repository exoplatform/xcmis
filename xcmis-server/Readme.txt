====== Deployment procedure to an application server ======

For standalone CMIS:

1. Make sure you have correct: 
  1.1. pom.xml. There should be the correct application server folder name or relative path (<xcmis.deploy.tomcat.dependency> property).
  1.2. Maven version 2.0.9 (or higher).
2. Run "mvn clean install antrun:run" command within "assembly" folder.
3. If the command has executed successfully, go to xcmis-tomcat bin directory placed at "./deploy/target/xcmis-tomcat/bin" and run "xcmis run" command.
4. REST Atom services will be available at the URL "http://localhost:8080/xcmis/rest/cmisatom"
5. SOAP services should be available at the URL "http://localhost:8080/xcmis/cmisws".

