package majhrs16.ct;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class API {
	private Main plugin;
	private Util Util;
	public GoogleTranslator GT;

	public API(Main plugin) {
		this.plugin = plugin;
		this.Util   = new Util(plugin);
		this.GT     = new GoogleTranslator();
	}

	public void broadcast(CommandSender sender, String msgFormat, String msg, String sourceLang) {
//			ejemplo: broadcast(player, "<%player_name%> %msg%", "Hola mundo!", "es", "en")
//				Enviara a todos los jugadores presentes un mensaje diciendo "<Majhrs16> Hola mundo!" para cada player en su idioma.
//			ejemplo: broadcast(Bukkit.getConsoleSender(), "", "Hola! Soy el server.", "es", "en")
//				Enviara a todos los jugadores presentes un mensaje diciendo "Hola! Soy el server." para cada player en su idioma. En este caso no se podra usar PAPI con %ejemplo%.

		Util.checkSupportLang(sourceLang);

		if (msgFormat == "" || msgFormat == null)
			msgFormat = "%msg%";

		if (msg == "" || msg == null)
			msg       = "[no data]";

		msgFormat = msgFormat.replace("%player_name%", sender.getName());
		msgFormat = msgFormat.replace("%lang%", getLang(sender));

		sendMessage(sender, Bukkit.getConsoleSender(), msgFormat, msg, sourceLang);
		for(Player player2 : Bukkit.getOnlinePlayers()) {
            sendMessage(sender, player2, msgFormat, msg, sourceLang);
        }
	}

	public void broadcast(CommandSender sender, String msgFormat, String msg) {
		broadcast(sender, msgFormat, msg, getLang(sender));
	}

	public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
//			ejemplo: sendMessage(Bukkit.getConsoleSender(), Majhrs16, "%msg% <%player_name%>", "Hello world!", "en", "es")
//				Enviara al playerTo un mensaje traducido diciendo "Hola mundo! <Majhrs16>", En esta funcion se usa playerFrom para obtener las variables del enviador y pre ponerlas para todo playerTo. 

		Util.checkSupportLang(sourceLang, "El sourceLang '" + sourceLang + "' no esta soportado.");
		Util.checkSupportLang(targetLang, "El targetLang '" + targetLang + "' no esta soportado.");

		if (msgFormat == null || msgFormat == "")
			msgFormat = "%msg%";

		if (msg == null || msg == "")
			msg = "&enull";

		if (Util.IF("debug")) {
			if (playerFrom == null)
				 Bukkit.getConsoleSender().sendMessage("Debug: PlayerFrom: '" + null + "', source: " + sourceLang);
			else Bukkit.getConsoleSender().sendMessage("Debug: PlayerFrom: '" + playerFrom.getName() + "', source: " + sourceLang);
			Bukkit.getConsoleSender().sendMessage("Debug: PlayerTo: '" + playerTo.getName() + "', target: " + targetLang);
			Bukkit.getConsoleSender().sendMessage("Debug: msgFormat: '" + msgFormat + "'");
			Bukkit.getConsoleSender().sendMessage("Debug: msg: '" + msg + "'");
		}

		playerTo.sendMessage(formatMsg(playerFrom, playerTo, msgFormat, msg, sourceLang, targetLang));
	}

	public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang) {
		sendMessage(playerFrom, playerTo, msgFormat, msg, sourceLang, getLang(playerTo));
	}

	public String formatMsg(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
//			formatMsg(Alejo09Games, Majhrs16, "", "&eHerobrine joined the game", "en", "en")
//				Resultado: String = "&eHerobrine joined the game" (Por favor tenga en cuenta que no puedo poner color en este archivo de texto plano, asi que imagine el ejemplo de color amarillo)

		if (msgFormat == "" || msgFormat == null)
			msgFormat = "%msg%";

		if (msg == "" || msg == null)
			msg       = "[no data]";

		if (Util.checkPAPI() && Util.IF("auto-format-messages")) {
			if (playerFrom != null && playerFrom instanceof Player) {
				msgFormat = msgFormat.replace("%player_name%", playerFrom.getName()); // Parece rebundante, pero es necesario.
				msgFormat = msgFormat.replace("%sourceLang%", getLang(playerFrom));
				msgFormat = PlaceholderAPI.setPlaceholders((Player) playerFrom, msgFormat);
			}

			if (playerTo != null && playerTo instanceof Player) {
				msgFormat = msgFormat.replace("$targetLang$", getLang(playerTo));
				msgFormat = PlaceholderAPI.setPlaceholders((Player) playerTo, msgFormat.replace("$", "%"));
			}
		}

		if (Util.IF("auto-translate-chat")) {
			boolean i = false;
			if (msg.startsWith(plugin.name)) {
				msg = msg.substring(plugin.name.length(), msg.length());
				i = true;
			}

    		msg     = GT.translateText(msg, sourceLang, targetLang);

    		if (i)
				msg = plugin.name + " " + msg;
		}

		msgFormat = ChatColor.translateAlternateColorCodes("&".charAt(0), msgFormat);

		if (Util.IF("message-color-personalized"))
    		msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg);

		return msgFormat.replace("%msg%", msg);
	}
	
	public String formatMsg(CommandSender sender, String msgFormat, String msg, String sourceLang, String targetLang) {
		return formatMsg(sender, sender, msgFormat, msg, sourceLang, targetLang);
	}

	public String getLang(CommandSender sender) {
//			Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
//			Ejemplo: getLang(Alejo09Games) -> String = "en"

        String lang               = null;
        FileConfiguration config  = plugin.getConfig();
        FileConfiguration players = plugin.getPlayers();
        String defaultLang        = config.getString("default-lang");
        String path               = "";

        /*
        if (sender instanceof Player && players.contains(path + ((Player) sender).getUniqueId())) {
    		lang     = players.getString(path + ((Player) sender).getUniqueId());

    		if (sender instanceof Player && Util.checkPAPI()) {
				if (lang.equals("auto")) {
					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");
				}

    		} else {
    			lang = defaultLang;
    		}

		} else {
    		if (sender instanceof Player) {
        		if (Util.checkPAPI())
   					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

        		else
        			lang = defaultLang;

    		} else
    			lang = defaultLang;
    	}
        */

        if (sender instanceof Player) {
        	if (players.contains(path + ((Player) sender).getUniqueId())) {
	    		lang     = players.getString(path + ((Player) sender).getUniqueId());
	
	    		if (Util.checkPAPI() && lang.equals("auto")) {
					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");
		   		}
        	}
 
		} else {
   			lang = defaultLang;
    	}

		if (!GT.isSupport(lang)) {
			if (GT.isSupport(defaultLang)) {
    			sendMessage(null, sender, "", plugin.name + " &7El idioma &f'&b" + lang + "&f' &cno esta soportado&f.", "es");
    			lang = defaultLang;

			} else {
				sendMessage(null, sender, "", plugin.name + "&7El idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.", "es");
				return null;
			}
		}
		
		return lang;
	}

}