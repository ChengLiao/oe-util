:ProcDelay delayMSec_
echo off
echo start Proccess Delay
  setlocal enableextensions
  for /f "tokens=1-4 delims=:. " %%h in ("%time%") do set start_=%%h%%i%%j%%k
    :_procwaitloop
    for /f "tokens=1-4 delims=:. " %%h in ("%time%") do set now_=%%h%%i%%j%%k
    set /a diff_=%now_%-%start_%
  if %diff_% LSS %1 goto _procwaitloop
  endlocal & goto :EOF
  
echo on
