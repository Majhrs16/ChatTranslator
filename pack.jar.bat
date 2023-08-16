jar cf ChatTranslator.jar -C bin . plugin.yml config.yml players.yml signs.yml LICENSE || Goto Error
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%