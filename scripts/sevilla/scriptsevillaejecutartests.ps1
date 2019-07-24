
Set-Location C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest
mvn install -DskipTests

Set-Location C:\Users\crist\Escritorio\full-teaching-tunon-tests\e2e-test\no-Elastest
$x = @("800","1920")
$y = @("600","1080")
$menmax = @("-Xms256m","-Xms512m")
$memin = @("-Xms256m","-Xmx512m")
For($i=0; $i -le 1; $i++){
For ($j=0; $j -le 1; $j++){
    C:\Users\crist\Escritorio\full-teaching-tunon-tests\scripts\sevilla\QRes.exe /x:$($x[$i]) /y:$($y[$i])

Start-Sleep 5

java -cp C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-jar-with-dependencies.jar $($memin[$j]) $($menmax[$j]) -jar C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-tests.jar

}
}
