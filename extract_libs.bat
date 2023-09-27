cd bin
jar xf ..\libs\Majhrs16.lib.jar || Goto Error
jar xf ..\libs\JDA*.jar || Goto Error
rmdir /S /Q META-INF || Goto Error
cd ..
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%