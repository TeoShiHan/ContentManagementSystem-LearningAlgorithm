@echo off
title LeetCode Learning System Launcher
echo =============================================
echo  LeetCode Learning System - Starting up...
echo =============================================
echo.

REM Kill any existing java/node processes to ensure a clean start
echo [0/3] Stopping any running backend/frontend processes...
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1
timeout /t 2 /nobreak > nul

REM Get the directory where this script lives
set "ROOT=%~dp0"
set "BACKEND=%ROOT%backend"
set "FRONTEND=%ROOT%frontend"

REM Start the Spring Boot backend in a new window
echo [1/3] Starting Backend (Spring Boot on port 8080)...
start "LCS Backend" cmd /k "cd /d "%BACKEND%" && mvn spring-boot:run"

REM Poll until backend is fully booted (port 8080 responds)
echo [2/3] Waiting for backend to be ready...
:WAIT_BACKEND
timeout /t 3 /nobreak > nul
powershell -Command "try { $r = Invoke-WebRequest -Uri http://localhost:8080/actuator/health -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; exit 0 } catch { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo       Backend not ready yet, retrying...
    goto WAIT_BACKEND
)
echo       Backend is UP!

REM Start the Angular frontend in a new window
echo [3/3] Starting Frontend (Angular on port 4200)...
start "LCS Frontend" cmd /k "cd /d "%FRONTEND%" && npm start"

REM Poll until frontend is ready
echo       Waiting for frontend to compile...
:WAIT_FRONTEND
timeout /t 3 /nobreak > nul
powershell -Command "try { Invoke-WebRequest -Uri http://localhost:4200 -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop | Out-Null; exit 0 } catch { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo       Frontend not ready yet, retrying...
    goto WAIT_FRONTEND
)
echo       Frontend is UP!

echo.
echo Opening browser at http://localhost:4200 ...
start "" "http://localhost:4200"

echo.
echo =============================================
echo  Both services are READY!
echo  Backend :  http://localhost:8080
echo  Frontend:  http://localhost:4200
echo  Close the two command windows to stop.
echo =============================================
