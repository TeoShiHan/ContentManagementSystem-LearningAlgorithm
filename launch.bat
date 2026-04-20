@echo off
title LeetCode Learning System Launcher
echo =============================================
echo  LeetCode Learning System - Starting up...
echo =============================================
echo.

REM Get the directory where this script lives
set "ROOT=%~dp0"
set "BACKEND=%ROOT%backend"
set "FRONTEND=%ROOT%frontend"

REM Start the Spring Boot backend in a new window
echo [1/2] Starting Backend (Spring Boot on port 8080)...
start "LCS Backend" cmd /k "cd /d "%BACKEND%" && mvn spring-boot:run"

REM Give the backend a few seconds to begin initializing
echo       Waiting 8 seconds for backend to initialize...
timeout /t 8 /nobreak > nul

REM Start the Angular frontend in a new window
echo [2/2] Starting Frontend (Angular on port 4200)...
start "LCS Frontend" cmd /k "cd /d "%FRONTEND%" && npm start"

REM Wait for frontend to spin up, then open browser
echo       Waiting 12 seconds for frontend to compile...
timeout /t 12 /nobreak > nul

echo.
echo Opening browser at http://localhost:4200 ...
start "" "http://localhost:4200"

echo.
echo =============================================
echo  Both services are starting!
echo  Backend :  http://localhost:8080
echo  Frontend:  http://localhost:4200
echo  Close the two command windows to stop.
echo =============================================
