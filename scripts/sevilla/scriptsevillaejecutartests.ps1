
Set-Location C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest
mvn install -DskipTests

Set-Location C:\Users\crist\Escritorio\full-teaching-tunon-tests\e2e-test\no-Elastest
$x = @("800", "1920")
$y = @("600", "1080")
$menmax = @("-Xms256m", "-Xms512m")
$memin = @("-Xms256m", "-Xmx512m")
$processafinity = @('0001', '0111')

try {
    [console]::TreatControlCAsInput = $true
    For ($p = 0; $p -le 1; $p++) {
        For ($i = 0; $i -le 1; $i++) {
            For ($j = 0; $j -le 1; $j++) {
                $namearchivo="$($x[$i])x$($y[$j])+$($memin[$i])+Windows10+$($processafinity[$p])"
                C:\Users\crist\Escritorio\full-teaching-tunon-tests\scripts\sevilla\QRes.exe /x:$($x[$i]) /y:$($y[$i])

                Start-Sleep 5

                $app_name = "java"
                $app_arguments = "-cp C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-jar-with-dependencies.jar $($memin[$j]) $($menmax[$j]) -jar C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-tests.jar $($namearchivo)"

                $app = Start-Process $app_name $app_arguments -PassThru 
                Write-Output "sin afinidad"
                $app.ProcessorAffinity = [System.IntPtr][Int]$processafinity[$p]
                Write-Output "metido afinidad "
                Wait-Process -Id $app.Id

            }
        }
    }
}
finally {
    C:\Users\crist\Escritorio\full-teaching-tunon-tests\scripts\sevilla\QRes.exe /x:$($x[1]) /y:$($y[1])

    [console]::TreatControlCAsInput = $false
   
}
