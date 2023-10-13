<p align="center">
  <img src="https://github.com/Majhrs16/ChatTranslator/blob/main/icon.png" width="50%" height="50%">
</p>


# ChatTranslator - Plugin Potente de Traducción y Formato del Chat Para Minecraft 1.5.2 - 1.20.1
Con ChatTranslator podrás comunicarte con jugadores de todo el mundo sin problemas.

## Características destacadas:
1. **Colores Personalizados**: Usa colores `#RRGGBB`! (1.16.5+)
2. **Mensajes Avanzados**: Personaliza aun mas los formatos de mensajes a través del JSON! (1.7.2+)
3. **Selección de Idioma**: Cambia tu idioma usando el comando por defecto `/cht lang [Jugador] <Código de Idioma>`.
4. **Detección Automática del Idioma**: ChatTranslator detecta automáticamente el idioma en el que tienes tu Minecraft!
5. **Configuración Modular**: Personaliza fácilmente las configuraciónes `config.formats`, `messages.yml` y `commands.yml`.
6. **Traducción de Carteles**: Simplemente haz `Click Derecho` o `Shift + Click derecho` en un letrero para traducirlo al instante.
7. **Protección Antispam Configurable**: Evita el spam en el chat con límites de mensajes por ticks que puedes ajustar a tu medida.
8. **Mensajes y Comandos Personalizados**: Personaliza completamente los mensajes y comandos del plugin según tus preferencias.
9. **Traducción del Propio Plugin**: ChatTranslator traduce incluso los mensajes internos del plugin y las comunicaciones a la consola.
10. **API de Traducción de Google Gratuita**: Utiliza la API del [Traductor de Google](https://translate.google.com/) sin costo adicional para lograr traducciones de facil acceso.
11. **Indicador de Pérdida de Conexión**: Si pierdes la conexión a Internet, las variables locales `ct_messages` llevarán el prefijo `[!]` para indicarlo.
12. **Soporte Bungeecord**: Compatible con Bungeecord y derivados gracias a las opciones de almacenamiento: `YAML`, `SQLite` y `MySQL` / `MariaDB`.
13. **Traducción entre Minecraft y Discord**: Conecta tu cuenta de Minecraft a Discord con el comando `/cht link` y traduce mensajes entre plataformas.

## Características acoplables:
14. *Compatibilidad con Otros Plugins de Chat**: Funciona con otros plugins populares de chat como [Chatty](https://www.spigotmc.org/resources/chatty-lightweight-universal-bukkit-chat-system-solution-1-7-10-1-19.59411/), y [ChatManager](https://www.spigotmc.org/resources/chat-manager-1-8-1-19-30-features-and-40-commands.52245/) a través del [ejemplo de configuración #3](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#soporte-parcial-para-otros-plugins-de-chat).
15. **[ConditionalEvents](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/)**: Controla los chats en base a condiciones específicas, como por ejemplo: Grupos de formato por rango usando [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/).
16. **[PlaceholderAPI (PAPI)](https://www.spigotmc.org/resources/placeholderapi.6245/)**: Disfruta de todas las variables de PAPI + la expansión de ChatTranslator ya integrada.
 
## Instalación

Para instalar ChatTranslator, sigue los siguientes pasos:

1. Descarga la ultima version de ChatTranslator desde [GitHub](https://github.com/CreativeMD/ChatTranslator/releases).
2. Copia el archivo .jar de ChatTranslator en la carpeta de plugins de tu servidor.
3. Reinicia tu servidor.

Una vez instalado, puedes personalizar la configuración de ChatTranslator modificando el archivo `config.yml` en la carpeta del plugin.

## Uso

ChatTranslator se activa automáticamente en tu servidor cuando lo instalas. Y una vez empiezas a chatear, se traduciran entre si automaticamente(Por defecto, dependiendo del idioma de su Minecraft).

Si deseas volver a la deteccion automatica tras establecer un idioma estatico, puedes hacerlo usando el comando por defecto: `/cht lang [jugador] auto`.

## Compilación(Para desarrolladores)

### Sin IDE.

1. Asegúrate de tener instalado preferiblemente Java Development Kit (JDK) 8 en tu sistema.
2. Clona el repositorio ChatTranslator desde GitHub a tu máquina local. Puedes hacerlo descargando el código fuente como un archivo ZIP y extrayéndolo o usando git:

   ```
   git clone https://github.com/Majhrs16/ChatTranslator/
   ```

3. Navega al directorio del proyecto:

   ```
   cd ChatTranslator
   ```

4. Descarga las dependencias requeridas y colócalas en la carpeta `libs` dentro del proyecto:

   - [Java Discord API(JDA)](https://ci.dv8tion.net/job/JDA5/lastSuccessfulBuild/artifact/build/libs/JDA-5.0.0-beta.13_c75c04e-withDependencies-min.jar)
   - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/download?version=514383)
   - [Spigot 1.16.5](https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar)

5. Y ya por ultimo, ejecuta el compile.bat:

   Esto compilará todos los archivos fuente y generará un ChatTranslator.jar.

Y eso es todo! :D

### Con IntelliJ IDEA.

Siga estos pasos para importar ChatTranslator en IntelliJ IDEA:

1. Clona el repositorio ChatTranslator desde GitHub a tu máquina local. Puedes hacerlo descargando el código fuente como un archivo ZIP y extrayéndolo o usando git:

   ```
   git clone https://github.com/Majhrs16/ChatTranslator/
   ```

2. Descarga las dependencias requeridas y colócalas en la carpeta `libs` dentro del proyecto:

   - [Java Discord API(JDA)](https://ci.dv8tion.net/job/JDA5/lastSuccessfulBuild/artifact/build/libs/JDA-5.0.0-beta.13_c75c04e-withDependencies-min.jar)
   - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/download?version=514383)
   - [Spigot 1.16.5](https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar)

3. Abre IntelliJ IDEA y selecciona "Abrir proyecto" desde el menú principal.

4. Navega hasta la carpeta donde clonaste o descomprimiste el proyecto y selecciónala.

5. IntelliJ IDEA detectará automáticamente que estás trabajando en un proyecto de Java.

6. Asegurate de establecer la version del JDK en 1.8.

7. Configura las rutas de exportación. Dado que estoy exportando las bibliotecas JDA5 y Majhrs16.lib, asegúrate de que estén configuradas para exportarse al archivo JAR final.

8. Recuerda marcar la carpeta `resources` como `Resources root`

8. Una vez importado y configurado, puedes comenzar a trabajar en el proyecto.

## Lista de idiomas soportados:

```
AF    = Afrikaans,
SQ    = shqiptare,
AM    = አማርኛ,
AR    = عرب,
HY    = հայերեն,
AS    = অসমীয়া,
AY    = Aymara,
AZ    = Azərbaycan,
BM    = Bamanankan,
EU    = euskara,
BE    = беларускі,
BN    = বাংলা,
BHO   = भोसपुरी के बा,
BS    = bosanski,
BG    = български,
CA    = Català,
CEB   = Cebuano,
ZH_CN = 简体中文,
ZH    = 简体中文,
ZH_TW = 繁體中文,
CO    = Corsu,
HR    = Hrvatski,
CS    = čeština,
DA    = dansk,
DV    = ދިވެހި,
DOI   = डोगरी,
NL    = Nederlands,
EN    = English,
EO    = Esperanto,
ET    = eesti keel,
EE    = Eʋegbe,
FIL   = Filipino Tagalog,
FI    = Tarkoitukset,
FR    = Français,
FY    = Frysk,
GL    = galego,
KA    = ქართული,
DE    = Deutsch,
EL    = Ελληνικά,
GN    = Guarani,
GU    = ગુજરાતી,
HT    = Kreyòl ayisyen,
HA    = hausa,
HAW   = ʻŌlelo Hawaiʻi,
HE    = עִברִית,
IW    = עִברִית,
HI    = हिंदी,
HMN   = Hmoob,
HU    = Magyar,
IS    = íslenskur,
IG    = igbo,
ILO   = Ilocano,
ID    = bahasa Indonesia,
GA    = Gaeilge,
IT    = Italiano,
JA    = 日本語,
JV    = basa jawa,
JW    = basa jawa,
KN    = ಕ್ಯಾನರ್ಸ್,
KK    = қазақ,
KM    = ខ្មែរ,
RW    = Kiñarwanda,
GOM   = कोंकणी,
KO    = 한국인,
KRI   = Krio,
KU    = Kurdî,
CKB   = کوردی سۆرانی,
KY    = Кыргызча,
LO    = ຊາວລາວ,
LA    = Latinus,
LV    = latviski,
LN    = Lingala,
LT    = lietuvių,
LG    = Oluganda,
LB    = lëtzebuergesch,
MK    = македонски,
MAI   = मैथिली,
MG    = Malagasy,
MS    = Melayu,
ML    = മലബാർ,
MT    = Malti,
MI    = Maori,
MR    = मराठी,
LUS   = Mizo tawng,
MN    = Монгол,
MY    = မြန်မာ,
NE    = नेपाली,
NO    = norsk,
NY    = Nyanja Chichewa,
OR    = ଘୃଣା oriya,
OM    = Afaan Oromoo,
PS    = پښتو,
FA    = فارسی,
PL    = Polski,
PT    = Português,
PA    = ਪੰਜਾਬੀ,
QU    = Runasimi,
RO    = Română,
RU    = Русский,
SM    = samoa,
SA    = संस्कृत,
GD    = Gàidhlig na h-Alba,
NSO   = Sepedi,
SR    = Српски,
ST    = sesotho,
SN    = Shona,
SD    = سنڌي,
SI    = සිංහල,
SK    = slovenský,
SL    = Slovenščina,
SO    = Soomaali,
ES    = Español,
SU    = basa sunda,
SW    = kiswahili,
SV    = svenska,
TL    = Tagalog Filipino,
TG    = тоҷикӣ,
TA    = தமிழ்,
TT    = Татар,
TE    = తెలుగు,
TH    = แบบไทย,
TI    = tigriña ዝብል ቃል ንረክብ,
TS    = Tsonga,
TR    = Türkçe,
TK    = Türkmenler,
AK    = Twi Akan .,
UK    = українська,
UR    = اردو,
UG    = Uigur,
UZ    = o'zbek,
VI    = Tiếng Việt,
CY    = Cymraeg,
XH    = isiXhosa,
YI    = יידיש,
YO    = Yoruba,
ZU    = Zulu.
```

## Solución de problemas

Si tienes problemas con ChatTranslator, revisa la documentación del plugin y los recursos de soporte en los siguientes enlaces:

- [ChatTranslator wiki](https://github.com/Majhrs16/ChatTranslator/wiki)
- [Discord](https://discord.gg/kZxHnSVPTg)

## Contribución

Si deseas contribuir al desarrollo de ChatTranslator, puedes enviar solicitudes de extracción o informes de problemas al [repositorio de GitHub](https://github.com/Majhrs16/ChatTranslator) o atravez del [Discord](https://discord.gg/kZxHnSVPTg). Tu contribución es muy apreciada.

## Licencia

ChatTranslator está licenciado bajo la Licencia GPL v3.0. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## Soporte

Para soporte o ayuda, únete a nuestro servidor de [Discord](https://discord.gg/kZxHnSVPTg).

¡Gracias por usar ChatTranslator! Si tienes alguna pregunta o necesitas ayuda, no dudes en preguntar.
