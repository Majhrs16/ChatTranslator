package majhrs16.ct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.json.JSONArray;

import me.clip.placeholderapi.PlaceholderAPI;

public class api {
	private main plugin;
	public GoogleTranslator GT;

	public api(main plugin) {
		this.plugin = plugin;
		this.GT     = new GoogleTranslator();
	}

	public void broadcast(_Sender player, String msgFormat, String msg, String sourceLang) {
		checkSupportLang(sourceLang);
		if (msgFormat == null) { msgFormat = "%msg%"; }
		if (msg == null) { msg = ""; }

		@SuppressWarnings("unchecked")
		List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();

		msgFormat = msgFormat.replace("%player%", player.getName());
		msgFormat = msgFormat.replace("%lang%", getLang(player));

		sendMessage(new _Sender(Bukkit.getConsoleSender()), msgFormat, msg, sourceLang);
		for(int i = 0; i < players.size(); i++) {
            sendMessage(new _Sender(players.get(i)), msgFormat, msg, sourceLang);
        }
	}

	public void broadcast(_Sender player, String msgFormat, String msg) {
		broadcast(player, msgFormat, msg, getLang(player));
	}

	public void checkSupportLang(String lang, String text) {
		if (GT.getCode(lang) == null) {
			throw new IllegalArgumentException(text);
		}
	}
	
	public void checkSupportLang(String lang) {
		checkSupportLang(lang, "This lang '" + lang + "' no supported.");
	}

	public void sendMessage(_Sender player, String msgFormat, String msg, String sourceLang, String targetLang) {
		checkSupportLang(sourceLang, "This sourceLang '" + sourceLang + "' no supported.");
		checkSupportLang(targetLang, "This targetLang '" + targetLang + "' no supported.");
		if (msgFormat == null) { msgFormat = "%msg%"; }
		if (msg == null) { msg = ""; }

		if (IF(plugin.getConfig(), "debug")) {
			System.out.println("Debug: Player: '" + player.getName() + "', source: " + sourceLang + ", target: " + targetLang); ///////////////////////////////////////////////////////////////////////////////////
			System.out.println("Debug: msgFormat: '" + msgFormat + "'");
			System.out.println("Debug: msg: '" + msg + "'");
		}

		player.sendMessage(formatMsg(player, msgFormat, msg, sourceLang, targetLang));
	}
	
	public void sendMessage(_Sender player, String msgFormat, String msg, String sourceLang) {
		sendMessage(player, msgFormat, msg, sourceLang, getLang(player));
	}

	public String formatMsg(_Sender player, String msgFormat, String msg, String sourceLang, String targetLang) {
		FileConfiguration config = plugin.getConfig();

		msgFormat = msgFormat.replace("%player%", player.getName());
		msgFormat = msgFormat.replace("%lang%", getLang(player));

		if (IF(config, "auto-format-messages")) {
			msgFormat = PlaceholderAPI.setPlaceholders(player.getPlayer(), msgFormat);
		}

//		System.out.println("Debug: msgFormat: '" + msgFormat + "'");
//		System.out.println("Debug: msg: '" + msg + "'");
		if (IF(config, "auto-translate-chat")) {
			boolean i = false;
			if (msg.startsWith(plugin.name)) {
				msg = msg.substring(plugin.name.length(), msg.length());
				i = true;
			}

//			System.out.println("Debug: msgFormat: '" + msgFormat + "'");
//			System.out.println("Debug: msg: '" + msg + "'");
    		msg     = GT.translateText(msg, sourceLang, targetLang); //// por alguna razon esto corta el msgFormat ocasionando un msg vacio O_o? 
//			System.out.println("Debug: msgFormat: '" + msgFormat + "'");
//			System.out.println("Debug: msg: '" + msg + "'");

    		if (i) {
				msg = plugin.name + " " + msg;
			}
		}
//		System.out.println("Debug: msgFormat: '" + msgFormat + "'");
//		System.out.println("Debug: msg: '" + msg + "'");

		msgFormat = ChatColor.translateAlternateColorCodes("&".charAt(0), msgFormat);
		
		if (IF(config, "chat-color-personalized")) {
    		msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg);
		}

		return msgFormat.replace("%msg%", msg);
	}

	public String getLang(_Sender player) {
        String lang               = null;
        FileConfiguration config  = plugin.getConfig();
        FileConfiguration players = config; //plugin.getPlayers();
        String defaultLang        = config.getString("default-lang");
        String path               = "players.";

        if (players.contains(path + player.getUniqueId())) {
    		lang     = players.getString(path + player.getUniqueId());

    		if (lang.equals("auto")) {
    			lang = PlaceholderAPI.setPlaceholders(player.getPlayer(), "%player_locale_short%");
    		}

		} else {
    		if (player.isPlayer()) {
    			lang = PlaceholderAPI.setPlaceholders(player.getPlayer(), "%player_locale_short%");

    		} else if (player.isConsole()) {
    			lang = defaultLang;
    		}
    	}

		if (!GT.isSupport(lang)) {
			if (GT.isSupport(defaultLang)) {
    			sendMessage(player, null, plugin.name + " &7El idioma &f'&b" + lang + "&f' &cno esta soportado&f.", "es");
    			lang = defaultLang;

			} else {
				sendMessage(player, null, plugin.name + "&7El idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.", "es");
				return null;
			}
		}
		
		return lang;
	}

	public boolean IF(FileConfiguration cfg, String path) {
		return cfg.contains(path) && cfg.getString(path).equals("true");
	}

	public class _Sender {
		private CommandSender sender;
		public _Sender(CommandSender sender) {
			this.sender = sender;
		}

		public boolean isConsole() {
			return !isPlayer();
		}

		public boolean isPlayer() {
			return sender instanceof Player;
		}

		public void sendMessage(String s) {
			sender.sendMessage(s);
		}

		public Location getLocation() {
			if (isPlayer()) {
				return getPlayer().getLocation();
			}

			return null;
		}

		public GameMode getGameMode() {
			if (isPlayer()) {
				return getPlayer().getGameMode();
			}

			return null;
		}

		public String getName() {
			return sender.getName();
		}

		public boolean hasPermission(String s) {
			return sender.hasPermission(s);
		}

		public UUID getUniqueId() {
			if (isPlayer()) {
				return getPlayer().getUniqueId();
			}

			return null;
		}

		public Player getPlayer() {
			if (isPlayer()) {
				return (Player) sender;

			} else if (isConsole()) {
				return null;
			}

			return null;
		}

		public CommandSender getConsole() {
			if (isConsole()) {
				return sender;

			} else if (isPlayer()) {
				return null;
			}

			return null;
		}
	}
	
	public class GoogleTranslator {
	    private final Map<String,String> LANGUAGE_MAP = new HashMap<String,String>();
	    
	    public GoogleTranslator() {
	        LANGUAGE_MAP.put("auto", "Automatic");
	        LANGUAGE_MAP.put("af", "Afrikaans");
	        LANGUAGE_MAP.put("sq", "Albanian");
	        LANGUAGE_MAP.put("am", "Amharic");
	        LANGUAGE_MAP.put("ar", "Arabic");
	        LANGUAGE_MAP.put("hy", "Armenian");
	        LANGUAGE_MAP.put("az", "Azerbaijani");
	        LANGUAGE_MAP.put("eu", "Basque");
	        LANGUAGE_MAP.put("be", "Belarusian");
	        LANGUAGE_MAP.put("bn", "Bengali");
	        LANGUAGE_MAP.put("bs", "Bosnian");
	        LANGUAGE_MAP.put("bg", "Bulgarian");
	        LANGUAGE_MAP.put("ca", "Catalan");
	        LANGUAGE_MAP.put("ceb", "Cebuano");
	        LANGUAGE_MAP.put("ny", "Chichewa");
	        LANGUAGE_MAP.put("zh_cn", "Chinese Simplified");
	        LANGUAGE_MAP.put("zh_tw", "Chinese Traditional");
	        LANGUAGE_MAP.put("co", "Corsican");
	        LANGUAGE_MAP.put("hr", "Croatian");
	        LANGUAGE_MAP.put("cs", "Czech");
	        LANGUAGE_MAP.put("da", "Danish");
	        LANGUAGE_MAP.put("nl", "Dutch");
	        LANGUAGE_MAP.put("en", "English");
	        LANGUAGE_MAP.put("eo", "Esperanto");
	        LANGUAGE_MAP.put("et", "Estonian");
	        LANGUAGE_MAP.put("tl", "Filipino");
	        LANGUAGE_MAP.put("fi", "Finnish");
	        LANGUAGE_MAP.put("fr", "French");
	        LANGUAGE_MAP.put("fy", "Frisian");
	        LANGUAGE_MAP.put("gl", "Galician");
	        LANGUAGE_MAP.put("ka", "Georgian");
	        LANGUAGE_MAP.put("de", "German");
	        LANGUAGE_MAP.put("el", "Greek");
	        LANGUAGE_MAP.put("gu", "Gujarati");
	        LANGUAGE_MAP.put("ht", "Haitian Creole");
	        LANGUAGE_MAP.put("ha", "Hausa");
	        LANGUAGE_MAP.put("haw", "Hawaiian");
	        LANGUAGE_MAP.put("iw", "Hebrew");
	        LANGUAGE_MAP.put("hi", "Hindi");
	        LANGUAGE_MAP.put("hmn", "Hmong");
	        LANGUAGE_MAP.put("hu", "Hungarian");
	        LANGUAGE_MAP.put("is", "Icelandic");
	        LANGUAGE_MAP.put("ig", "Igbo");
	        LANGUAGE_MAP.put("id", "Indonesian");
	        LANGUAGE_MAP.put("ga", "Irish");
	        LANGUAGE_MAP.put("it", "Italian");
	        LANGUAGE_MAP.put("ja", "Japanese");
	        LANGUAGE_MAP.put("jw", "Javanese");
	        LANGUAGE_MAP.put("kn", "Kannada");
	        LANGUAGE_MAP.put("kk", "Kazakh");
	        LANGUAGE_MAP.put("km", "Khmer");
	        LANGUAGE_MAP.put("ko", "Korean");
	        LANGUAGE_MAP.put("ku", "Kurdish (Kurmanji)");
	        LANGUAGE_MAP.put("ky", "Kyrgyz");
	        LANGUAGE_MAP.put("lo", "Lao");
	        LANGUAGE_MAP.put("la", "Latin");
	        LANGUAGE_MAP.put("lv", "Latvian");
	        LANGUAGE_MAP.put("lt", "Lithuanian");
	        LANGUAGE_MAP.put("lb", "Luxembourgish");
	        LANGUAGE_MAP.put("mk", "Macedonian");
	        LANGUAGE_MAP.put("mg", "Malagasy");
	        LANGUAGE_MAP.put("ms", "Malay");
	        LANGUAGE_MAP.put("ml", "Malayalam");
	        LANGUAGE_MAP.put("mt", "Maltese");
	        LANGUAGE_MAP.put("mi", "Maori");
	        LANGUAGE_MAP.put("mr", "Marathi");
	        LANGUAGE_MAP.put("mn", "Mongolian");
	        LANGUAGE_MAP.put("my", "Myanmar (Burmese)");
	        LANGUAGE_MAP.put("ne", "Nepali");
	        LANGUAGE_MAP.put("no", "Norwegian");
	        LANGUAGE_MAP.put("ps", "Pashto");
	        LANGUAGE_MAP.put("fa", "Persian");
	        LANGUAGE_MAP.put("pl", "Polish");
	        LANGUAGE_MAP.put("pt", "Portuguese");
	        LANGUAGE_MAP.put("ma", "Punjabi");
	        LANGUAGE_MAP.put("ro", "Romanian");
	        LANGUAGE_MAP.put("ru", "Russian");
	        LANGUAGE_MAP.put("sm", "Samoan");
	        LANGUAGE_MAP.put("gd", "Scots Gaelic");
	        LANGUAGE_MAP.put("sr", "Serbian");
	        LANGUAGE_MAP.put("st", "Sesotho");
	        LANGUAGE_MAP.put("sn", "Shona");
	        LANGUAGE_MAP.put("sd", "Sindhi");
	        LANGUAGE_MAP.put("si", "Sinhala");
	        LANGUAGE_MAP.put("sk", "Slovak");
	        LANGUAGE_MAP.put("sl", "Slovenian");
	        LANGUAGE_MAP.put("so", "Somali");
	        LANGUAGE_MAP.put("es", "Spanish");
	        LANGUAGE_MAP.put("su", "Sundanese");
	        LANGUAGE_MAP.put("sw", "Swahili");
	        LANGUAGE_MAP.put("sv", "Swedish");
	        LANGUAGE_MAP.put("tg", "Tajik");
	        LANGUAGE_MAP.put("ta", "Tamil");
	        LANGUAGE_MAP.put("te", "Telugu");
	        LANGUAGE_MAP.put("th", "Thai");
	        LANGUAGE_MAP.put("tr", "Turkish");
	        LANGUAGE_MAP.put("uk", "Ukrainian");
	        LANGUAGE_MAP.put("ur", "Urdu");
	        LANGUAGE_MAP.put("uz", "Uzbek");
	        LANGUAGE_MAP.put("vi", "Vietnamese");
	        LANGUAGE_MAP.put("cy", "Welsh");
	        LANGUAGE_MAP.put("xh", "Xhosa");
	        LANGUAGE_MAP.put("yi", "Yiddish");
	        LANGUAGE_MAP.put("yo", "Yoruba");
	        LANGUAGE_MAP.put("zu", "Zulu");
	    }

	    private boolean isSupport(String language) {
	        return LANGUAGE_MAP.get(language) != null;
	    }

	    public String getCode(String desiredLang) {
	        if(isSupport(desiredLang)) {
	            return desiredLang;
	        }

	        for(Map.Entry<String, String> enter: LANGUAGE_MAP.entrySet()) {
	            if(enter.getValue().equalsIgnoreCase(desiredLang)){
	                return enter.getKey();
	            }
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
				e.printStackTrace();
	        	return text;
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
