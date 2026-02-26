param(
    [string]$AppBaseUrl = "http://localhost:8080",
    [string]$KeycloakBaseUrl = $(if ([string]::IsNullOrWhiteSpace($env:KEYCLOAK_AUTH_SERVER_URL)) { "http://localhost:9090" } else { $env:KEYCLOAK_AUTH_SERVER_URL }),
    [string]$Realm = "intellian-app",
    [string]$ClientId = "intellian-app-spring-client",
    [string]$ClientSecret = $env:KEYCLOAK_CLIENT_SECRET,
    [string]$UserUsername,
    [string]$UserPassword,
    [string]$AdminUsername,
    [string]$AdminPassword
)

function Require-Value {
    param(
        [string]$Value,
        [string]$Name
    )
    if ([string]::IsNullOrWhiteSpace($Value)) {
        throw "Missing required value: $Name"
    }
}

function Get-AccessToken {
    param(
        [string]$Username,
        [string]$Password
    )

    $tokenUri = "$KeycloakBaseUrl/realms/$Realm/protocol/openid-connect/token"
    $body = @{
        grant_type    = "password"
        client_id     = $ClientId
        client_secret = $ClientSecret
        username      = $Username
        password      = $Password
    }

    $response = Invoke-RestMethod -Method Post -Uri $tokenUri -ContentType "application/x-www-form-urlencoded" -Body $body
    return $response.access_token
}

Require-Value -Value $KeycloakBaseUrl -Name "KeycloakBaseUrl"
Require-Value -Value $ClientSecret -Name "KEYCLOAK_CLIENT_SECRET"
Require-Value -Value $UserUsername -Name "UserUsername"
Require-Value -Value $UserPassword -Name "UserPassword"
Require-Value -Value $AdminUsername -Name "AdminUsername"
Require-Value -Value $AdminPassword -Name "AdminPassword"

$userToken = Get-AccessToken -Username $UserUsername -Password $UserPassword
$adminToken = Get-AccessToken -Username $AdminUsername -Password $AdminPassword

if ([string]::IsNullOrWhiteSpace($userToken) -or [string]::IsNullOrWhiteSpace($adminToken)) {
    throw "Failed to issue access token(s) from Keycloak."
}

Write-Host "[1/3] ROLE_USER token -> /api/v1/orders"
$userOrders = Invoke-WebRequest -Method Get -Uri "$AppBaseUrl/api/v1/orders" -Headers @{ Authorization = "Bearer $userToken" } -ErrorAction SilentlyContinue
if ($userOrders.StatusCode -eq 401 -or $userOrders.StatusCode -eq 403) {
    throw "ROLE_USER request to /orders failed with status $($userOrders.StatusCode)"
}

Write-Host "[2/3] ROLE_USER token -> /api/v1/admin/members (expect 403)"
try {
    Invoke-WebRequest -Method Get -Uri "$AppBaseUrl/api/v1/admin/members" -Headers @{ Authorization = "Bearer $userToken" } -ErrorAction Stop | Out-Null
    throw "Expected 403 but ROLE_USER request succeeded."
}
catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 403) {
        throw "Expected 403 for ROLE_USER admin request, got $($_.Exception.Response.StatusCode.value__)"
    }
}

Write-Host "[3/3] ROLE_ADMIN token -> /api/v1/admin/members (expect 200)"
$adminMembers = Invoke-WebRequest -Method Get -Uri "$AppBaseUrl/api/v1/admin/members" -Headers @{ Authorization = "Bearer $adminToken" } -ErrorAction Stop
if ($adminMembers.StatusCode -ne 200) {
    throw "Expected 200 for ROLE_ADMIN admin request, got $($adminMembers.StatusCode)"
}

Write-Host "Keycloak E2E verification passed."
