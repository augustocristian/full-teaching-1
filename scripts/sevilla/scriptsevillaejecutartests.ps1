
Set-Location C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest
mvn install -DskipTests

Set-Location C:\Users\crist\Escritorio\full-teaching-tunon-tests\e2e-test\no-Elastest
$x = @( "800","1024", "1366", "1920")
$y = @( "600","768", "768", "1024")
$menmax = @("-Xmx90m", "-Xmx120m", "-Xmx240m", "-Xmx520m")
$memin = @("-Xms90m", "-Xms120m", "-Xms240m", "-Xms520m")
[flags()] Enum Cores {
    Core1 = 0x0001
    Core2 = 0x0002
    Core3 = 0x0004
    Core4 = 0x0008
    Core5 = 0x0010
    Core6 = 0x0020
    Core7 = 0x0040
    Core8 = 0x0080
}
$processafinity = @( [int][cores]'core1', [int][cores]'core1,core2', [int][cores]'core1,core2,core3')

try {
    [console]::TreatControlCAsInput = $true
    For ($m = 0; $m -le 3; $m++) {
        For ($p = 0; $p -le 2; $p++) {
            For ($i = 0; $i -le 3; $i++) {
            
                $namearchivo = "$($x[$i])x$($y[$i])&$($memin[$m])&Windows10&$($processafinity[$p])"
                C:\Users\crist\Escritorio\full-teaching-tunon-tests\scripts\sevilla\QRes.exe /x:$($x[$i]) /y:$($y[$i])

                Start-Sleep 5

                $app_name = "java"
                $app_arguments = "-cp C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-jar-with-dependencies.jar $($memin[$m]) $($menmax[$m]) -jar C:/Users/crist/Escritorio/full-teaching-tunon-tests/e2e-test/no-Elastest/target/e2e.no-elastest-0.0.2-SNAPSHOT-tests.jar $($namearchivo)"

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
    C:\Users\crist\Escritorio\full-teaching-tunon-tests\scripts\sevilla\QRes.exe /x:$($x[3]) /y:$($y[3])

    [console]::TreatControlCAsInput = $false
   
}
