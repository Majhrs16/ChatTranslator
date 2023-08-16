cd bin
jar xf ..\libs\org.json*.jar || Goto Error
jar xf ..\libs\mysql*.jar || Goto Error
jar xf ..\libs\Majhrs16.lib.jar || Goto Error
rmdir /S /Q com\mysql\cj || Goto Error
rmdir /S /Q META-INF || Goto Error
del INFO_BIN
del INFO_SRC
del LICENSE || Goto Error
del README
del VERSION
cd ..
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%