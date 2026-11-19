$ErrorActionPreference = 'Stop'
Set-Location (Join-Path $PSScriptRoot '..')
try {
    Write-Host 'EcoLar Java - use os dados da conexao que funciona no SQL Developer.'
    Write-Host 'A senha fica somente no processo desta execucao; nao sera gravada em arquivo.'
    $url = Read-Host 'URL JDBC (servico: jdbc:oracle:thin:@//HOST:PORTA/SERVICO; SID: jdbc:oracle:thin:@HOST:PORTA:SID)'
    if ($url -notmatch '^jdbc:oracle:thin:@') { throw 'URL deve iniciar com jdbc:oracle:thin:@' }
    $usuario = Read-Host 'Usuario Oracle'
    $segredo = Read-Host 'Senha Oracle (nao aparece na tela)' -AsSecureString
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segredo)
    try { $env:ORACLE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
    $env:ORACLE_URL = $url
    $env:ORACLE_USER = $usuario
    $ia = Read-Host 'URL Flask [Enter = http://127.0.0.1:5000]'
    $env:IA_BASE_URL = if ([string]::IsNullOrWhiteSpace($ia)) {'http://127.0.0.1:5000'} else {$ia}
    $env:CORS_ORIGINS = 'http://localhost:5173,http://127.0.0.1:5173'
    if (-not (Test-Path 'bin\ecolar-api.jar')) { throw 'JAR ausente. Execute COMPILAR_WINDOWS.bat.' }
    Write-Host 'Abra http://localhost:8080 para testar. Para encerrar: Ctrl+C.'
    & java '-Duser.timezone=America/Sao_Paulo' -jar 'bin\ecolar-api.jar'
    if ($LASTEXITCODE -ne 0) { throw 'Java encerrou com erro. Confira JDK 21 e a mensagem acima.' }
} catch {
    Write-Host ('Falha: ' + $_.Exception.Message) -ForegroundColor Red
    exit 1
} finally {
    Remove-Item Env:ORACLE_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:ORACLE_USER -ErrorAction SilentlyContinue
    Remove-Item Env:ORACLE_URL -ErrorAction SilentlyContinue
}
