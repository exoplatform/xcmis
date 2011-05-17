Tests run 36:
mvn clean install -Ptesting -DcmisUsername=root -DcmisPassword=exo -DcmisRepositoryId=cmis1

Stress test
mvn clean install -Ptesting -DcmisUsername=root -DcmisPassword=exo -DcmisRepositoryId=cmis1 -Dtest=Stress -DfilesNum=1000