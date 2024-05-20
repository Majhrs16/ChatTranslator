<img align="left" src="https://github.com/Majhrs16/ChatTranslator/blob/main/icon.png" width="25%" height="25%" alt="ChatTranslator Icon"></img>
<h1>ChatTranslator</h1>

# Spigot Plugin Translator and Powerful Chat Formatter for Minecraft 1.5.2 - 1.20.6
With ChatTranslator, you can communicate with players from all over the world effortlessly.

## Features:
1. **Custom Colors**: Use `#RRGGBB` colors! Only for 1.16.5+ and non-JSON messages.
2. **Advanced Messages**: Further customize message formats through JSON! (1.7.2+)
3. **Sign Translation**: Simply `Shift` + `Left Click` on a sign to translate it instantly.
4. **Configurable Anti-Spam Protection**: Prevent chat spam with adjustable message limits per tick.
5. **Custom Formats and Commands**: Fully customize the plugin’s formats and commands to your liking.
6. **Language Selection**: Change your language using the default command `/cht lang [Player] <Language Code>`.
7. **Plugin Self-Translation**: ChatTranslator even translates the plugin's internal formats and console communications.
8. **Automatic Language Detection**: ChatTranslator automatically detects the language in which you have your Minecraft set!
9. **Connection Loss Indicator**: If you lose your internet connection, the formats will carry the prefix `[!]` to indicate it.
10. **Free Google Translation API**: Use the [Google Translator](https://translate.google.com/) API at no additional cost for easy access translations.
11. **Bungeecord Support**: Compatible with Bungeecord and its derivatives with storage options: For local: `YAML`, `SQLite` and, for Bungeecord: `MySQL` / `MariaDB`.
12. **Minecraft to Discord Synchronization**: Connect your Minecraft account to Discord with the command `/cht link` and translate and sync messages or permissions between platforms.

## Optional Integrations:
13. **[ConditionalEvents](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/)**: Dynamically control chats based on specific conditions, such as: Formatting groups by rank using [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/).
14. **[PlaceholderAPI (PAPI)](https://www.spigotmc.org/resources/placeholderapi.6245/)**: Enjoy all PAPI variables plus the ChatTranslator expansion already integrated: CoreTranslator.

## Installation

To install ChatTranslator, follow these steps:

1. Download the latest version of ChatTranslator from [Spigot](https://www.spigotmc.org/resources/chattranslator.106604/) or [GitHub](https://github.com/CreativeMD/ChatTranslator/releases/latest).
2. Place the ChatTranslator.jar file in your server’s plugins folder.
3. Restart your server.

## Usage

Once installed, you can customize ChatTranslator’s settings and/or formats by modifying the `config.yml` and `formats.yml` files in the plugin’s folder.

ChatTranslator activates automatically on your server upon installation. Once you start chatting, messages will be translated automatically (by default, depending on the language of your Minecraft).

If you wish to return to automatic detection after setting a static language, you can do so using the default command: `/cht lang [player] auto`.

## Building (For Developers)

### Without IDE.

1. Ensure you have preferably Java Development Kit (JDK) 8 installed on your system.
2. Clone the ChatTranslator repository from GitHub to your local machine. You can do this by downloading the source code as a ZIP file and extracting it or using git:

   `git clone https://github.com/Majhrs16/ChatTranslator/`

3. Navigate to the project directory:

4. Download the required dependencies and place them in the `Libs` folder within the project:

   - [Java Discord API (JDA)](https://ci.dv8tion.net/job/JDA5/lastSuccessfulBuild/artifact/build/libs/JDA-5.0.0-beta.24-withDependencies-min.jar)

5. Finally, run `gradlew` with the `shadowJar` parameter:

This will compile all source files and generate a usable final file: `./build/libs/ChatTranslator-v2.0.jar`.

And that's it! `:D`

### With IntelliJ IDEA.

Follow these steps to import ChatTranslator into IntelliJ IDEA:

1. Clone the ChatTranslator repository from GitHub to your local machine. You can do this by downloading the source code as a ZIP file and extracting it or using git:

   - `git clone https://github.com/Majhrs16/ChatTranslator/`

2. Download the required dependencies and place them in the `Libs` folder within the project:

   - [Java Discord API (JDA)](https://ci.dv8tion.net/job/JDA5/lastSuccessfulBuild/artifact/build/libs/JDA-5.0.0-beta.13_c75c04e-withDependencies-min.jar)

3. Open IntelliJ IDEA and select "Open Project" from the main menu.

4. Navigate to the folder where you cloned or unzipped the project and select it.

5. IntelliJ IDEA will automatically detect that you are working on a Java project with Gradle.

6. Ensure you have preferably JDK 8 installed.

7. Once imported and configured, you can start working on the project.

## [Usage Statistics](https://bstats.org/plugin/bukkit/ChT/20251):

<img align="center" src="https://bstats.org/signatures/bukkit/ChT.svg" alt="ChatTranslator Usage Statistics">

## Supported Languages List:

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
KM    =  ខ្មែរ,
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
MY    =  မြန်မာ,
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

## Troubleshooting

If you encounter issues with ChatTranslator, check the plugin documentation and support resources at the following links:

- [ChatTranslator wiki](https://github.com/Majhrs16/ChatTranslator/wiki)
- [Discord](https://discord.gg/kZxHnSVPTg)

## Contribution

If you would like to contribute to the development of ChatTranslator, you can submit pull requests or issue reports to the [GitHub repository](https://github.com/Majhrs16/ChatTranslator) or through [Discord](https://discord.gg/kZxHnSVPTg). Your contribution is greatly appreciated.

## License

ChatTranslator is licensed under the GPL v3.0 License. See the [LICENSE](resources/LICENSE) file for more details.

## Support

For support or help, join our [Discord server](https://discord.gg/kZxHnSVPTg).

Thank you for using ChatTranslator! If you have any questions or need assistance, feel free to ask.
