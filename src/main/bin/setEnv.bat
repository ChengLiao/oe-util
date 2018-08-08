
@echo off
cd ..

REM set JAVA_HOME="D:\Java\jdk1.6.0_11"
REM set JAVA_HOME=%JAVA_HOME%
set path="%JAVA_HOME%"\bin;%PATH%

REM echo Using JAVA_HOME=%JAVA_HOME%
set BIN_HOME=%~dp0
CD /d %BIN_HOME%
CD..
set MAIN_HOME=%cd%
REM echo MAIN_HOME=%MAIN_HOME%
set ETC_HOME="%MAIN_HOME%"\etc
set CLASSPATH=%ETC_HOME%;"%JAVA_HOME%"\lib\tools.jar

for %%i in ("%MAIN_HOME%\lib\*.jar") do call "%MAIN_HOME%\bin\cpappend.bat" %%i
for %%i in ("%MAIN_HOME%\lib\*.zip") do call "%MAIN_HOME%\bin\cpappend.bat" %%i

set CLASSPATH=%ETC_HOME%;%CLASSPATH%;
REM echo CLASSPATH=%CLASSPATH%
@echo on