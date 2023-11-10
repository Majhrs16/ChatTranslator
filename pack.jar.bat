jar cf ChatTranslator.jar -C bin . -C resources . || Goto Error
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%
