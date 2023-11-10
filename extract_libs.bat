cd bin
jar xf ..\Libs\Majhrs16.lib.jar || Goto Error
jar xf ..\Libs\JDA*.jar || Goto Error
rmdir /S /Q META-INF || Goto Error
cd ..
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%
