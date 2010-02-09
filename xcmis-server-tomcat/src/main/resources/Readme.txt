====== xCMIS Running procedure on Tomcat application server ======

1. Run Tomcat
   * On the Windows platform
     Open a DOS prompt command and type the command
     xcmis.bat run
   * On Unix/linux/cygwin/MacOSX
     Open a terminal and type the command:
     ./xcmis run
     You may need to change the permission of all *.sh files in the tomcat/bin dir by using: chmod +x *.sh 
2. Available services URLs:
   * Home page at the URL "http://localhost:8080/xcmis"
   * REST Atom services will be available at the URL "http://localhost:8080/xcmis/rest/cmisatom"
   * SOAP services should be available at the URL "http://localhost:8080/xcmis/cmisws"
3. Default service credential: username is "root" and password is "exo".
