package majhrs16.ct;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import majhrs16.ct.core.GoogleTranslator;

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
		util.checkSupportLang(sourceLang);

		if (msgFormat == null)
			msgFormat = "%msg%";

		if (msg == null)
			msg = "";

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
		util.checkSupportLang(sourceLang, "El sourceLang '" + sourceLang + "' no esta soportado.");
		util.checkSupportLang(targetLang, "El targetLang '" + targetLang + "' no esta soportado.");

		if (msgFormat == null || msgFormat == "")
			msgFormat = "%msg%";

		if (msg == null || msg == "")
			msg = "&enull";

		if (util.IF("debug")) {
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
		if (playerFrom instanceof Player && util.checkPAPI() && util.IF("auto-format-messages") && playerFrom != null) {
			msgFormat = msgFormat.replace("%player_name%", playerFrom.getName()); // Parece rebundante, pero es necesario.
			msgFormat = msgFormat.replace("%sourceLang%", getLang(playerFrom));
			msgFormat = PlaceholderAPI.setPlaceholders((Player) playerFrom, msgFormat);
		}

		if (playerTo instanceof Player && util.checkPAPI() && playerTo != null) {
			msgFormat = msgFormat.replace("$targetLang$", getLang(playerTo));
			msgFormat = PlaceholderAPI.setPlaceholders((Player) playerTo, msgFormat.replace("$", "%"));
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
	
	public String formatMsg(CommandSender sender, String msgFormat, String msg, String sourceLang, String targetLang) {
		return formatMsg(sender, sender, msgFormat, msg, sourceLang, targetLang);
	}

	public String getLang(CommandSender sender) {
        String lang               = null;
        FileConfiguration config  = plugin.getConfig();
        FileConfiguration players = plugin.getPlayers();
        String defaultLang        = config.getString("default-lang");
        String path               = "";

        /*
        if (sender instanceof Player && players.contains(path + ((Player) sender).getUniqueId())) {
    		lang     = players.getString(path + ((Player) sender).getUniqueId());

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
        */

        if (sender instanceof Player) {
        	if (players.contains(path + ((Player) sender).getUniqueId())) {
	    		lang     = players.getString(path + ((Player) sender).getUniqueId());
	
	    		if (util.checkPAPI() && lang.equals("auto")) {
					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");
		
	    		} else {
	    			lang = defaultLang;
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