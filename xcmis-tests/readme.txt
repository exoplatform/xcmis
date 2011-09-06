=========================================
Tests run (36 tests):
mvn clean install -Ptesting -DcmisUsername=root -DcmisPassword=exo -DcmisRepositoryId=cmis1

=========================================
Stress test run:
mvn clean install -Ptesting -DcmisUsername=root -DcmisPassword=exo -DcmisRepositoryId=cmis1 -Dtest=Stress -DfilesNum=1000


=========================================
Eclipse JUnit run:

Program arguments:
-Ptesting

VM arguments:
-DcmisUsername=root
-DcmisPassword=exo
-DcmisRepositoryId=driveA
-DcmisRepositoryUrl=http://localhost:8080/xcmis/rest/cmisatom

=========================================
