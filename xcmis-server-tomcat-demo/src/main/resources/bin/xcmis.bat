@echo off

rem Computes the absolute path of eXo
setlocal ENABLEDELAYEDEXPANSION
for %%i in ( !%~f0! )         do set BIN_DIR=%%~dpi
for %%i in ( !%BIN_DIR%\..! ) do set TOMCAT_HOME=%%~dpni

rem Sets some variables

set XCMIS_OPTS="-Xshare:auto -Xms256m -Xmx512m"

set JAVA_OPTS=%XCMIS_OPTS%

rem Launches the server
cd %BIN_DIR%
call catalina.bat %*