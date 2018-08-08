
@call "%~dp0setEnv.bat"
@echo off
REM echo "now will run..."
java -cp %CLASSPATH% com.teradata.util.DataTransfer %*
echo on