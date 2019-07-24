
cd C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest
mvn install -DskipTests
java -cp e2e.no-elastest-0.0.2-SNAPSHOT-jar-with-dependencies.jar -Xms256m -Xmx512m -jar e2e.no-elastest-0.0.2-SNAPSHOT-tests.jar

java -Xms256m -Xmx512m -jar  C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT.jar "1GBRAM"



