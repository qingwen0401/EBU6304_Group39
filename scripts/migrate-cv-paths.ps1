param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..").Path
)

$ErrorActionPreference = "Stop"

function Backup-JsonFile {
    param([string]$FilePath)
    $timestamp = Get-Date -Format "yyyyMMddHHmmss"
    $backupPath = "$FilePath.bak.$timestamp"
    Copy-Item -Path $FilePath -Destination $backupPath -Force
    return $backupPath
}

function Get-NewCvPath {
    param([string]$OldPath)

    if ([string]::IsNullOrWhiteSpace($OldPath)) {
        return $OldPath
    }

    $normalized = $OldPath.Replace('\', '/').Trim()
    if ($normalized -match '^cv/(.+)$') {
        return "data/uploads/cv/$($Matches[1])"
    }
    return $OldPath
}

$dataDir = Join-Path $ProjectRoot "data"
$legacyCvDir = Join-Path $dataDir "cv"
$newCvDir = Join-Path $dataDir "uploads\cv"

if (-not (Test-Path $dataDir)) {
    throw "Data directory not found: $dataDir"
}

New-Item -ItemType Directory -Path $newCvDir -Force | Out-Null

$movedCount = 0
if (Test-Path $legacyCvDir) {
    $legacyFiles = Get-ChildItem -Path $legacyCvDir -File -ErrorAction SilentlyContinue
    foreach ($file in $legacyFiles) {
        $destFile = Join-Path $newCvDir $file.Name
        Move-Item -Path $file.FullName -Destination $destFile -Force
        $movedCount++
    }
}

$jsonFiles = Get-ChildItem -Path $dataDir -Filter "*.json" -File
$updatedFiles = @()
$updatedPathCount = 0

foreach ($jsonFile in $jsonFiles) {
    $raw = Get-Content -Path $jsonFile.FullName -Raw -Encoding UTF8
    if ([string]::IsNullOrWhiteSpace($raw)) {
        continue
    }

    $rawTrimmed = $raw.TrimStart()
    $sourceIsArray = $rawTrimmed.StartsWith("[")
    $obj = $raw | ConvertFrom-Json
    $changed = $false

    if ($obj -is [System.Array]) {
        foreach ($item in $obj) {
            if ($null -ne $item -and $item.PSObject.Properties.Name -contains "cvPath") {
                $old = [string]$item.cvPath
                $new = Get-NewCvPath -OldPath $old
                if ($new -ne $old) {
                    $item.cvPath = $new
                    $updatedPathCount++
                    $changed = $true
                }
            }
        }
    } else {
        if ($obj.PSObject.Properties.Name -contains "cvPath") {
            $old = [string]$obj.cvPath
            $new = Get-NewCvPath -OldPath $old
            if ($new -ne $old) {
                $obj.cvPath = $new
                $updatedPathCount++
                $changed = $true
            }
        }
    }

    if ($changed) {
        $backup = Backup-JsonFile -FilePath $jsonFile.FullName
        $outputObject = $obj
        if ($sourceIsArray -and -not ($obj -is [System.Array])) {
            $outputObject = @($obj)
        }
        $jsonOut = $outputObject | ConvertTo-Json -Depth 20
        Set-Content -Path $jsonFile.FullName -Value $jsonOut -Encoding UTF8
        $updatedFiles += [PSCustomObject]@{
            File = $jsonFile.FullName
            Backup = $backup
        }
    }
}

Write-Host "Migration completed."
Write-Host "ProjectRoot: $ProjectRoot"
Write-Host "Moved CV files: $movedCount"
Write-Host "Updated cvPath count: $updatedPathCount"
Write-Host "Updated JSON files: $($updatedFiles.Count)"
if ($updatedFiles.Count -gt 0) {
    Write-Host "Backups:"
    $updatedFiles | ForEach-Object { Write-Host " - $($_.Backup)" }
}
