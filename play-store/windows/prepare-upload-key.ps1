# Sync 30 Numerique - Google Play upload JKS for Mes Notes & Dossiers
# Run interactively on the owner's Windows PC. Does NOT affect the personal APK.
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$repo = "Chasmet/Bloc-note"
$alias = "sync30_mesnotes_upload"
$root = Join-Path $env:USERPROFILE "Documents\Sync30-Securite\MesNotesDossiers"
$jks = Join-Path $root "mesnotesdossiers-upload.jks"
function To-Plain([System.Security.SecureString]$s) {
  $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s)
  try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
  finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
}
$keytool = Get-Command keytool -ErrorAction Stop
$gh = Get-Command gh -ErrorAction Stop
& $gh.Source auth status -h github.com
if ($LASTEXITCODE -ne 0) { throw "Connecter d'abord GitHub CLI : gh auth login" }
New-Item -Path $root -ItemType Directory -Force | Out-Null
Write-Host "Chemin JKS (prive) : $jks"
$pw1 = Read-Host "Mot de passe fort (16 caracteres minimum)" -AsSecureString
$pw2 = Read-Host "Confirmer le mot de passe" -AsSecureString
$plain = $null
try {
  $plain = To-Plain $pw1
  $check = To-Plain $pw2
  if ($plain.Length -lt 16 -or $plain -cne $check) { throw "Mot de passe invalide ou confirmation differente" }
  $check = $null
  $env:SYNC30_JKS_PASSWORD = $plain
  if (-not (Test-Path -LiteralPath $jks)) {
    & $keytool.Source -genkeypair -alias $alias -keyalg RSA -keysize 4096 -sigalg SHA256withRSA -validity 10000 -storetype JKS -keystore $jks -storepass:env SYNC30_JKS_PASSWORD -keypass:env SYNC30_JKS_PASSWORD -dname "CN=Sync 30 Numerique, O=Sync 30 Numerique, C=FR" -noprompt
    if ($LASTEXITCODE -ne 0) { throw "La generation JKS a echoue" }
  } else { Write-Host "JKS existant conserve : pas de rotation de cle." }
  & $keytool.Source -list -keystore $jks -alias $alias -storepass:env SYNC30_JKS_PASSWORD -v | Select-String "SHA256:"
  if ($LASTEXITCODE -ne 0) { throw "Verification JKS echouee" }
  $backup = Read-Host "Dossier de sauvegarde sur support distinct (ENTREE pour annuler)"
  if ([string]::IsNullOrWhiteSpace($backup) -or -not (Test-Path $backup -PathType Container)) { throw "Sauvegarde sur support distinct obligatoire" }
  $dest = Join-Path $backup "mesnotesdossiers-upload.jks"
  if (Test-Path $dest) { throw "Destination de sauvegarde existante : ne pas ecraser" }
  Copy-Item -LiteralPath $jks -Destination $dest -ErrorAction Stop
  if ((Get-FileHash $jks -Algorithm SHA256).Hash -ne (Get-FileHash $dest -Algorithm SHA256).Hash) { throw "Echec de verification sauvegarde" }
  function Set-SecureGitHubSecret([string]$name,[string]$value) {
    $s = New-Object System.Diagnostics.ProcessStartInfo
    $s.FileName = $gh.Source
    $s.Arguments = "secret set $name -R $repo"
    $s.UseShellExecute = $false
    $s.RedirectStandardInput = $true
    $s.CreateNoWindow = $true
    $p = [System.Diagnostics.Process]::Start($s)
    try {
      $p.StandardInput.Write($value)
      $p.StandardInput.Close()
      $p.WaitForExit()
      if ($p.ExitCode -ne 0) { throw "Echec du secret GitHub $name" }
    } finally { $p.Dispose() }
  }
  Set-SecureGitHubSecret "BLOCNOTE_UPLOAD_JKS_BASE64" ([Convert]::ToBase64String([IO.File]::ReadAllBytes($jks)))
  Set-SecureGitHubSecret "BLOCNOTE_UPLOAD_STORE_PASSWORD" $plain
  Set-SecureGitHubSecret "BLOCNOTE_UPLOAD_ALIAS" $alias
  Set-SecureGitHubSecret "BLOCNOTE_UPLOAD_KEY_PASSWORD" $plain
  Write-Host "Fait : signature preparee sur PC et secrets GitHub transmis sans affichage." -ForegroundColor Green
  Write-Host "Reste a configurer le verificateur de paiement HTTPS et lancer le workflow AAB."
} finally {
  Remove-Item Env:SYNC30_JKS_PASSWORD -ErrorAction SilentlyContinue
  $plain = $null
  $pw1.Dispose()
  $pw2.Dispose()
}
