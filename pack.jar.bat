jar cvf ChatTranslator.jar -C bin . -C src . plugin.yml config.yml players.yml signs.yml LICENSE || Goto Error
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%