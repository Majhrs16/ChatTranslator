package majhrs16.ct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.json.JSONArray;

import me.clip.placeholderapi.PlaceholderAPI;

public class api {
	private main plugin;
	private util util;
	public GoogleTranslator GT;

	public api(main plugin) {
		this.plugin = plugin;
		this.util   = new util(plugin);
		this.GT     = new GoogleTranslator();
	}

	public void broadcast(CommandSender sender, String msgFormat, String msg, String sourceLang) {
		checkSupportLang(sourceLang);
		if (msgFormat == null) { msgFormat = "%msg%"; }
		if (msg == null) { msg = ""; }

		@SuppressWarnings("unchecked")
		List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();

		msgFormat = msgFormat.replace("%sender%", sender.getName());
		msgFormat = msgFormat.replace("%lang%", getLang(sender));

		sendMessage(Bukkit.getConsoleSender(), msgFormat, msg, sourceLang);
		for(int i = 0; i < players.size(); i++) {
            sendMessage(sender, players.get(i), msgFormat, msg, sourceLang);
        }
	}

	public void broadcast(CommandSender sender, String msgFormat, String msg) {
		broadcast(sender, msgFormat, msg, getLang(sender));
	}

	public void checkSupportLang(String lang, String text) {
		if (GT.getCode(lang) == null) {
			throw new IllegalArgumentException(text);
		}
	}
	
	public void checkSupportLang(String lang) {
		checkSupportLang(lang, "This lang '" + lang + "' no supported.");
	}

	public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
		checkSupportLang(sourceLang, "This sourceLang '" + sourceLang + "' no supported.");
		checkSupportLang(targetLang, "This targetLang '" + targetLang + "' no supported.");
		if (msgFormat == null || msgFormat == "") { msgFormat = "%msg%"; }
		if (msg == null || msg == "") { msg = "&enull"; }

		if (util.IF("debug")) {
			Bukkit.getConsoleSender().sendMessage("Debug: PlayerFrom: '" + playerFrom.getName() + "', source: " + sourceLang);
			Bukkit.getConsoleSender().sendMessage("Debug: PlayerTo: '" + playerTo.getName() + "', target: " + targetLang);
			Bukkit.getConsoleSender().sendMessage("Debug: msgFormat: '" + msgFormat + "'");
			Bukkit.getConsoleSender().sendMessage("Debug: msg: '" + msg + "'");
		}

		playerTo.sendMessage(formatMsg(playerFrom, playerTo, msgFormat, msg, sourceLang, targetLang));
	}

	public void sendMessage(CommandSender playerFrom, String msgFormat, String msg, String sourceLang) {
		sendMessage(playerFrom, playerFrom, msgFormat, msg, sourceLang, getLang(playerFrom));
	}

	public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang) {
		sendMessage(playerFrom, playerTo, msgFormat, msg, sourceLang, getLang(playerTo));
	}

	public void sendMessage(CommandSender playerFrom, String msgFormat, String msg, String sourceLang, String targetLang) {
		sendMessage(playerFrom, playerFrom, msgFormat, msg, sourceLang, targetLang);
	}

	public String formatMsg(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
		msgFormat = msgFormat.replace("%player_name%", playerFrom.getName()); // Parece rebundante, pero es necesario.
		msgFormat = msgFormat.replace("%sourceLang%", getLang(playerFrom));

		if (playerFrom instanceof Player && util.checkPAPI() && util.IF("auto-format-messages")) {
			msgFormat = PlaceholderAPI.setPlaceholders((Player) playerFrom, msgFormat);
		}

		if (playerTo != null || playerFrom != playerTo)
			msgFormat = msgFormat.replace("$targetLang$", getLang(playerTo));

		if (playerTo instanceof Player && util.checkPAPI()) {
			if (playerTo != null || playerFrom != playerTo) {
				msgFormat = PlaceholderAPI.setPlaceholders((Player) playerTo, msgFormat.replace("$", "%"));
			}
		}

		if (util.IF("auto-translate-chat")) {
			boolean i = false;
			if (msg.startsWith(plugin.name)) {
				msg = msg.substring(plugin.name.length(), msg.length());
				i = true;
			}

    		msg     = GT.translateText(msg, sourceLang, targetLang);

    		if (i) {
				msg = plugin.name + " " + msg;
			}
		}

		msgFormat = ChatColor.translateAlternateColorCodes("&".charAt(0), msgFormat);
		
		if (util.IF("chat-color-personalized")) {
    		msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg);
		}

		return msgFormat.replace("%msg%", msg);
	}
	
	public String formatMsg(CommandSender playerFrom, String msgFormat, String msg, String sourceLang, String targetLang) {
		return formatMsg(playerFrom, null, msgFormat, msg, sourceLang, targetLang);
	}

	public String getLang(CommandSender sender) {
        String lang               = null;
        FileConfiguration config  = plugin.getConfig();
        FileConfiguration players = config; //plugin.getPlayers();
        String defaultLang        = config.getString("default-lang");
        String path               = "players.";
        String UUID               = util.getUUID(sender);

        if (players.contains(path + UUID)) {
    		lang     = players.getString(path + UUID);

    		if (sender instanceof Player && util.checkPAPI()) {
				if (lang.equals("auto")) {
					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");
				}

    		} else {
    			lang = defaultLang;
    		}

		} else {
    		if (sender instanceof Player) {
        		if (util.checkPAPI())
   					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

        		else
        			lang = defaultLang;

    		} else
    			lang = defaultLang;
    	}

		if (!GT.isSupport(lang)) {
			if (GT.isSupport(defaultLang)) {
    			sendMessage(sender, "", plugin.name + " &7El idioma &f'&b" + lang + "&f' &cno esta soportado&f.", "es");
    			lang = defaultLang;

			} else {
				sendMessage(sender, "", plugin.name + "&7El idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.", "es");
				return null;
			}
		}
		
		return lang;
	}

    public enum Language {
    	AUTO("Automatic"),
    	AF("Afrikaans"),
    	SQ("Albanian"),
    	AM("Amharic"),
    	AR("Arabic"),
    	HY("Armenian"),
    	AZ("Azerbaijani"),
    	EU("Basque"),
    	BE("Belarusian"),
    	BN("Bengali"),
    	BS("Bosnian"),
    	BG("Bulgarian"),
    	CA("Catalan"),
    	CEB("Cebuano"),
    	NY("Chichewa"),
    	ZH_CN("Chinese Simplified"),
    	ZH_TW("Chinese Traditional"),
    	CO("Corsican"),
    	HR("Croatian"),
    	CS("Czech"),
    	DA("Danish"),
    	NL("Dutch"),
    	EN("English"),
    	EO("Esperanto"),
    	ET("Estonian"),
    	TL("Filipino"),
    	FI("Finnish"),
    	FR("French"),
    	FY("Frisian"),
    	GL("Galician"),
    	KA("Georgian"),
    	DE("German"),
    	EL("Greek"),
    	GU("Gujarati"),
    	HT("Haitian Creole"),
    	HA("Hausa"),
    	HAW("Hawaiian"),
    	IW("Hebrew"),
    	HI("Hindi"),
    	HMN("Hmong"),
    	HU("Hungarian"),
    	IS("Icelandic"),
    	IG("Igbo"),
    	ID("Indonesian"),
    	GA("Irish"),
    	IT("Italian"),
    	JA("Japanese"),
    	JW("Javanese"),
    	KN("Kannada"),
    	KK("Kazakh"),
    	KM("Khmer"),
    	KO("Korean"),
    	KU("Kurdish (Kurmanji)"),
    	KY("Kyrgyz"),
    	LO("Lao"),
    	LA("Latin"),
    	LV("Latvian"),
    	LT("Lithuanian"),
    	LB("Luxembourgish"),
    	MK("Macedonian"),
    	MG("Malagasy"),
    	MS("Malay"),
    	ML("Malayalam"),
    	MT("Maltese"),
    	MI("Maori"),
    	MR("Marathi"),
    	MN("Mongolian"),
    	MY("Myanmar (Burmese)"),
    	NE("Nepali"),
    	NO("Norwegian"),
    	PS("Pashto"),
    	FA("Persian"),
    	PL("Polish"),
    	PT("Portuguese"),
    	MA("Punjabi"),
    	RO("Romanian"),
    	RU("Russian"),
    	SM("Samoan"),
    	GD("Scots Gaelic"),
    	SR("Serbian"),
    	ST("Sesotho"),
    	SN("Shona"),
    	SD("Sindhi"),
    	SI("Sinhala"),
    	SK("Slovak"),
    	SL("Slovenian"),
    	SO("Somali"),
    	ES("Spanish"),
    	SU("Sundanese"),
    	SW("Swahili"),
    	SV("Swedish"),
    	TG("Tajik"),
    	TA("Tamil"),
    	TE("Telugu"),
    	TH("Thai"),
    	TR("Turkish"),
    	UK("Ukrainian"),
    	UR("Urdu"),
    	UZ("Uzbek"),
    	VI("Vietnamese"),
    	CY("Welsh"),
    	XH("Xhosa"),
    	YI("Yiddish"),
    	YO("Yoruba"),
    	ZU("Zulu");

        private String value;

        private Language(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
	
	public class GoogleTranslator {
	    private boolean isSupport(String language) {
	        return Language.valueOf(language.toUpperCase()).getValue() != null;
	    }

	    public String getCode(String desiredLang) {
	        if(isSupport(desiredLang)) {
	            return desiredLang;
	        }
	        
	        return null;
	    }

	    public String translateText(String text, String sourceLang, String targetLang) {
	        if(!(isSupport(sourceLang) && isSupport(targetLang))){
	            return text;
	        }

	        try {
		        String str = peticionHttpGet(
		        	"https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + (text.replace("+", "%2B").replace(" ", "+").replace("&", "%26").replace("%", "%25"))
		        ); // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]
		        
		        String list1 = new JSONArray(str).get(0).toString(); // [["hola","hola",null,null,5]]
		        String list2 = new JSONArray(list1).get(0).toString(); // ["hola","hola",null,null,5]
		        String list3 = new JSONArray(list2).get(0).toString(); // "hola"
		        return list3.replace("%2B", "+").replace("%26", "&").replace("%25", "%");

	        } catch (Exception e) {
//				e.printStackTrace();
	        	return "[NO INTERNET] " + text;
	        }
	    } 

	    private String peticionHttpGet(String urlParaVisitar) throws Exception {
			// Esto es lo que vamos a devolver
			StringBuilder resultado = new StringBuilder();
			// Crear un objeto de tipo URL
			URL url = new URL(urlParaVisitar);
			// Abrir la conexión e indicar que será de tipo GET
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setRequestMethod("GET");
			// Búferes para leer
			BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
			String linea;
			// Mientras el BufferedReader se pueda leer, agregar contenido a resultado
			while ((linea = rd.readLine()) != null) {
				resultado.append(linea);
			}
			// Cerrar el BufferedReader
			rd.close();
			// Regresar resultado, pero como cadena, no como StringBuilder
			return resultado.toString();
	    }
	}
}