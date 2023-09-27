jar cf ChatTranslator.jar -C bin . *.yml LICENSE || Goto Error
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%