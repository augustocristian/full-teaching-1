ls
$PATHORIGEN=Get-Location
mkdir C:/sevillarepos
Set-Location C:/sevillarepos
git clone https://github.com/OpenVidu/full-teaching.git
Set-Location full-teaching
Start-Process docker-compose up
Set-Location $PATHORIGEN
ls
cd C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest
mvn install -DskipTests
java -jar C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target


