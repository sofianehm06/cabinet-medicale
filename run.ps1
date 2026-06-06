# =====================================================================
#  Compile et lance l'application SANS Maven (JDK 17+ requis).
#  Pratique si vous n'utilisez pas NetBeans. Double-cliquez run.bat
#  ou executez :  powershell -ExecutionPolicy Bypass -File run.ps1
# =====================================================================
$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$lib  = Join-Path $root 'lib'
$out  = Join-Path $root 'build\classes'
New-Item -ItemType Directory -Force -Path $lib, $out | Out-Null

$sqlite = Join-Path $lib 'sqlite-jdbc-3.45.3.0.jar'
$slf4j  = Join-Path $lib 'slf4j-api-1.7.36.jar'

if (-not (Test-Path $sqlite)) {
    Write-Host 'Telechargement du pilote SQLite (une seule fois)...' -ForegroundColor Cyan
    Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar' -OutFile $sqlite
}
if (-not (Test-Path $slf4j)) {
    Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar' -OutFile $slf4j
}

# Liste des sources (chemins en slash avant pour les fichiers @argfile de javac)
$sources = Get-ChildItem -Path (Join-Path $root 'src\main\java') -Recurse -Filter *.java |
    ForEach-Object { '"' + ($_.FullName -replace '\\','/') + '"' }
$argfile = Join-Path $env:TEMP 'cabinet-sources.txt'
$sources | Set-Content $argfile -Encoding ascii

Write-Host 'Compilation...' -ForegroundColor Cyan
& javac -encoding UTF-8 -cp "$lib\*" -d $out "@$argfile"
if ($LASTEXITCODE -ne 0) { throw 'Echec de la compilation.' }

Write-Host 'Lancement de l''application (identifiants : admin / admin123)...' -ForegroundColor Green
& java -cp "$out;$lib\*" com.cabinet.Main