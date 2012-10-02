@echo off
@setlocal enabledelayedexpansion

IF "%SDK_DIR%" == "" ((
	set SDK_DIR=d:\Programming\android-sdk-windows
	echo SDK_DIR not found, defaulting to !SDK_DIR!
))

cd "%SDK_DIR%\platform-tools"

FOR /F "tokens=1,2* delims==" %%A IN (%~dp0log_trace.properties) DO (
	echo Setting %%A as level %%B...
	adb shell setprop %%A %%B
)

pause