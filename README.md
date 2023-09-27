# ChatTranslator

ChatTranslator es un plugin para Minecraft 1.5.2 - 1.20.1 que permite la traducción en tiempo real de mensajes entre jugadores que hablan diferentes idiomas. Con ChatTranslator, puedes comunicarte con jugadores de todo el mundo sin tener que preocuparte por las barreras del idioma.

## Características

- Utilización de la API de [Google Translate](https://translate.google.com/) para llevar a cabo traducciones al idioma establecido, abarcando el chat y los mensajes de la consola, entre otros.
- Configuración altamente personalizable para adecuar el formato del chat a las preferencias individuales, a través de `config.formats`.
- En situaciones en las que la conexión a internet no esté disponible, se añade el prefijo `[!]` a la variable local `ct_messages`.
- Los jugadores y la consola pueden especificar su idioma utilizando el comando `/cht lang [jugador] <código de idioma>`.
- Capacidad para la traducción de carteles, y aún más características planificadas para futuras actualizaciones.
- Compatibilidad con BungeeCord y la integración de bases de datos para un rendimiento óptimo.
- Detección automática del idioma del jugador para facilitar la comunicación.
- Funciones que se pueden activar o desactivar según las necesidades.
- Opcionalmente, se puede aprovechar el potencial de las siguientes dependencias:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).
  - [ConditionalEvents](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/), brindando una manera sencilla de condicionar los chats.
- Soporte parcial para otros plugins de chat, tales como:
  - [Chatty](https://www.spigotmc.org/resources/chatty-lightweight-universal-bukkit-chat-system-solution-1-7-10-1-19.59411/), y [ChatManager](https://www.spigotmc.org/resources/chat-manager-1-8-1-19-30-features-and-40-commands.52245/) a través del [ejemplo de configuración #3](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#soporte-parcial-para-otros-plugins-de-chat).
  - Soporte parcial para [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/) a través del [ejemplo de configuración #4](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#soporte-parcial-para-discordsrv).
  - Mediante el evento `majhrs16.cht.events.custom.Message`.

## Instalación

Para instalar ChatTranslator, sigue los siguientes pasos:

1. Descarga la ultima version de ChatTranslator desde [Spigot](https://www.spigotmc.org/resources/chattranslator.106604/) o [GitHub](https://github.com/CreativeMD/ChatTranslator/releases).
2. Copia el archivo .jar de ChatTranslator en la carpeta de plugins de tu servidor.
3. Reinicia tu servidor.

Una vez instalado, puedes personalizar la configuración de ChatTranslator modificando el archivo `config.yml` en la carpeta del plugin.

## Uso

ChatTranslator se activa automáticamente en tu servidor cuando lo instalas. Y una vez empiezas a chatear, se traduciran entre si automaticamente(Por defecto, dependiendo del idioma de su Minecraft).

Si deseas cambiar tu idioma al automatico, puedes hacerlo usando el comando `/cht lang [jugador] auto`.

## Compilación(Para desarrolladores)

Para compilar el código fuente de ChatTranslator, sigue estos pasos:

1. Asegúrate de tener instalado preferiblemente Java Development Kit (JDK) 8 en tu sistema.
2. Clona este repositorio en tu máquina local utilizando el siguiente comando:

   ```
   git clone https://github.com/Majhrs16/ChatTranslator.git
   ```

3. Navega al directorio del proyecto:

   ```
   cd ChatTranslator
   ```

4. Descarga las dependencias requeridas y colócalas en la carpeta "libs" dentro del proyecto:

   - [Java Discord API(JDA)](https://ci.dv8tion.net/job/JDA5/lastSuccessfulBuild/artifact/build/libs/JDA-5.0.0-beta.13_c75c04e-withDependencies-min.jar)
   - [PlaceholderAPI.jar](https://www.spigotmc.org/resources/placeholderapi.6245/download?version=514383)
   - [Spigot 1.16.5.jar](https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar)

5. Y ya por ultimo, ejecuta el compile.bat:

   Esto compilará todos los archivos fuente y generará un ChatTranslator.jar.

Y eso es todo! :D

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
