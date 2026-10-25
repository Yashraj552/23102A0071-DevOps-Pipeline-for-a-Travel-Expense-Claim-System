<#
Starts the packaged application on a test port, runs the Selenium UI tests against it
and stops the application again. Used by the Jenkins pipeline (UI Tests stage).
Exit code 0 means all UI tests passed, any other value means a failure.
#>
param(
    [int]$Port = 8083,
    [string]$Mvn = "mvn"
)

$ErrorActionPreference = "Stop"

$war = Get-ChildItem -Path "target" -Filter "*.war" | Select-Object -First 1
if (-not $war) {
    Write-Host "No WAR file found in target. Run mvn package first."
    exit 1
}

$java = "java"
if ($env:JAVA_HOME) { $java = Join-Path $env:JAVA_HOME "bin\java.exe" }

$log = Join-Path (Get-Location) "target\ui-test-app.log"
$appArgs = @("-jar", ('"' + $war.FullName + '"'), "--server.port=$Port", "--spring.datasource.url=jdbc:h2:mem:uitest;DB_CLOSE_DELAY=-1", "--app.upload-dir=target/ui-test-uploads")

Write-Host "Starting application on port $Port using $($war.Name)"
$app = Start-Process -FilePath $java -ArgumentList $appArgs -PassThru -WindowStyle Hidden -RedirectStandardOutput $log -RedirectStandardError "$log.err"

$exitCode = 1
try {
    $ready = $false
    for ($i = 0; $i -lt 60; $i++) {
        if ($app.HasExited) { break }
        try {
            $response = Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:$Port/actuator/health" -TimeoutSec 3
            if ($response.StatusCode -eq 200) { $ready = $true; break }
        } catch { }
        Start-Sleep -Seconds 2
    }

    if (-not $ready) {
        Write-Host "The application did not start. Last lines of its log:"
        if (Test-Path $log) { Get-Content $log -Tail 40 }
        if (Test-Path "$log.err") { Get-Content "$log.err" -Tail 40 }
        $exitCode = 1
    }
    else {
        Write-Host "Application is up. Running the Selenium UI tests."
        & $Mvn -B test -Pselenium "-DBASE_URL=http://localhost:$Port" "-DHEADLESS=true" "-DNO_SANDBOX=true"
        $exitCode = $LASTEXITCODE
    }
}
finally {
    if ($app -and -not $app.HasExited) {
        Write-Host "Stopping application (PID $($app.Id))"
        Stop-Process -Id $app.Id -Force
    }
}

exit $exitCode