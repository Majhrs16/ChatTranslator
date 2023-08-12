javac -encoding UTF-8 -sourcepath src -d bin -cp "libs/*" src\majhrs16\cht\ChatTranslator.java || Goto Error
Exit /B 0

:Error
	Exit /B %ERRORLEVEL%