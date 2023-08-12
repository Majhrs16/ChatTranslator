@Echo OFF

Cls && Echo Compilando ...
	call linking.bat || goto Error
Cls && Echo Compilando ... OK

Cls && Echo Extrayendo dependencias criticas ...
	call extract_libs.bat || goto Error
Cls && Echo Extrayendo dependencias criticas ... OK

Cls && Echo Empaquetando jar final ... 
	call pack.jar.bat || goto Error
Cls && Echo Empaquetando jar final ... OK

Cls && Echo ChatTranslator OK.

Goto Exit

:Error
Echo HA OCURRIDO UN ERROR:
Echo 	%ERRORLEVEL%

:Exit
Pause