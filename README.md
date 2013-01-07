The **xCMIS** project, initially contributed to the Open Source community by [eXo](http://exoplatform.org), is an implementation of the full stack of Java-based CMIS services. xCMIS also include the client side frameworks for integrating content from different enterprise repositories according to the <a href="http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=cmis">CMIS standard</a>.

OASIS's [Content Management Interoperability Services](http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=cmis) **CMIS** specification aims to standardize a Web services interface specification that will enable greater interoperability of Enterprise Content Management (ECM) systems.

xCMIS provides an out-of-the-box ability to expose an eXo JCR repository, and can be used in conjunction with CMIS gadgets using GWT based client side framework (coming soon to Open Source). Both can be easily integrated to the <a href="http://www.gatein.org"> !GateIn portal </a>. To see full-featured CMIS gadgets in action, check out the demo on the <a href="http://xcmis.org">xcmis.org</a> site.

xCMIS supports all the features specified in the CMIS core definition as well as both REST !AtomPub and Web Services (SOAP/WSDL) protocol bindings. In addition to architecture supposed to provide an ability to a plug any third party content repository thanks to Storage Provider Interface layer. 

So, the idea of the project is to make as simple as possible joining Enterprise Content repositories making all of them CMIS-able and expose them to language-independent CMIS clients using the most convenient protocol. Read more about CMIS in real-life <a href=http://gazarenkov.blogspot.com/2010/04/cmis-targets-in-real-life.html> here </a>.  

xCMIS project:

  * Is Packaged as J2EE Web archive ( [WAR](http://code.google.com/p/xcmis/downloads/detail?name=xcmis-server-war-1.2.1.zip) ) as well as prepared "[download and go](http://code.google.com/p/xcmis/downloads/detail?name=xcmis-server-tomcat-1.2.1.zip)" Tomcat bundle
  * Has a live demo with full-featured CMIS gadget on !GateIn portal is accessible on the [xcmis.org](http://xcmis.org/CmisExpert1/org.exoplatform.cmis.CmisExpertApplication/CmisExpertApplication.html) site as well as with prepared ["download and go"](http://code.google.com/p/xcmis/downloads/detail?name=xcmis-server-tomcat-demo-1.2.1.zip) Tomcat bundle (the client is accessible as remote gadget) 
  * Tested with third-party CMIS clients such as: IBM CMIS Firefox Connector and CMIS Spaces Flex+AIR client. Either local repository ( as described [here](http://code.google.com/p/xcmis/wiki/xCMISthirdPartyClients)) or http://xcmis.org/xcmis1/rest/cmisatom can be used as a CMIS repository's endpoint URL for these or other types of clients.


----
# Changelog

## 08/26/2011 xCMIS 1.2.2 released

 * Differentiated the cmis:name from cmis:contentStreamFileName of documents.
 * Added xcmis-tests module, for remote testing by URL based on Chemistry client.
 * Added source zip generation.

## 05/12/2011 xCMIS 1.2.1 released

 * Fixed the time zone problem in date and search with time zone date field.

## 04/18/2011 xCMIS 1.2.0-GA released

 * Remove eXo uses from the code and configuration.
 * Uses everREST project for testing.
 * Fixed the getObjectByPath method failed when get a multi-filing document.
 * Fixed getRepositories response does not contains root folder id for each repository.
 * Fixed - Connection to storage is not closed after using in method org.xcmis.wssoap.impl.!RepositoryServicePortImpl.getRepositoryInfo
 * Remove component !CmisRestApplication from configuration in xcmis-restatom jar
 * Differentiate cmis:name from cmis:contentStreamFileName of documents.
 * Upgrade to pdfbox 1.4

## 09/08/2010 xCMIS 1.1.0-GA released

## 06/12/2010 xCMIS 1.0 released

 * Based on the latest specification CMIS v1.0 OASIS Standard.
 * Fully TCK compatible (100% PASSED).
 * GWT CMIS client framework decoupled from CMISExpert and joined to xCMIS codebase
 * Refactored Storage interface and implementations to use atomic operations.
 * Finalized the Connection class architecture.
 * Improved Rendition mechanizm.
 * Modified !StorageProviders configuration.
 * Re-designed the xcmis.org site.
 * Documentation fixed and improved.


## 04/28/2010 xCMIS 1.0 beta2 released

We are waiting for the official approval of CMIS 1.0 specs, so it is the latest, pre-final release of xCMIS. The main new features comparing to xCMIS 1.0 beta1 are:

 * It implemented according to Content Management Interoperability Services Version 1.0 Committee Draft 07 for REST !AtomPub and Web Services (SOAP/WSDL) protocol bindings
 * Storage Provider Interface (SPI) for CMIS repository backend is finalized. It allows potentially any content storage be CMIS-able
 * <a href=http://code.google.com/p/xcmis/wiki/xCMISClientGwtRestAtom> GWT CMIS client framework</a> decoupled from CMISExpert and joined to xCMIS codebase
 * The <a href=http://code.google.com/p/xcmis/wiki/xCMISConfigure> configuration</a> is completely reworked, making possible to set different types of storages, indexes, rendition providers
 * <a href=http://code.google.com/p/xcmis/wiki/xCMISSearch> Search engine </a> is decouped it from JCR storage (naturally as SPI is independent). Added support of multifiling and unfiling
 * xCMIS is no more tightly coupled with eXo (IoC) Container, deploying mechanism is pluggable
 * Added In-memory storage for both content and index, so now we have full featured, fast mechanism to test the binding internals
 * xCMIS can be bundled with stable !GateIn 3.0 portal. Thanks to <a href=http://code.google.com/p/xcmis/wiki/xCMISGateInPortalExtension>!GateIn extension</a> bundle, added to the project's binaries it is possible to integrate xCMIS to the working portal. 
 * xCMIS is tested under !RestAtom TCK
 * You can access xCMIS along with other CMIS repositories on online <a href=http://gazarenkov.blogspot.com/2010/04/xcmis-in-aiim-cmis-demo-2010.html> AIIM CMIS Demo </a>


## 02/11/2010 xCMIS 1.0 beta1 released

 * Includes CMIS server with all the services implementation according to Content Management Interoperability Services Version 1.0 Committee Draft 06 for REST !AtomPub and Web Services (SOAP/WSDL) protocol bindings
 * Packaged as J2EE Web archive ( WAR ) as well as prepared "download and go" Tomcat bundle
 * Live demo with full-featured CMIS gadget on !GateIn portal is accessible on the xcmis.org site as well as with prepared "download and go" Tomcat bundle (the client is accessible as remote gadget)
 * Supplied with eXo JCR (Java Content Repository) and Inmemory (mostly for testing purpose) Storage Providers
 * Tested with third-party CMIS clients such as: IBM CMIS Firefox Connector and CMIS Spaces Flex+AIR client. Either local repository ( as described here) or http://xcmis.org/rest/cmisatom can be used as a CMIS repository's endpoint URL for these or other types of clients.

# xCMIS Deployment procedure to an application server

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

