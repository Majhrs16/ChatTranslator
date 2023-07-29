package majhrs16.ct.commands.cht;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;

public class SetterLang {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void setLang(Message DC, String lang) {
		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		API.setLang(DC.getPlayer(), lang);
		plugin.savePlayers();

		DC.setMessages("&7Su idioma ha sido &aestablecido&7 a &b" + lang + "&f.");
		DC.setLang(lang);
			API.sendMessage(DC);
	}
	
	public void setLangAnother(Message DC, String player, String lang) {
		FileConfiguration config  = plugin.getConfig();
		String path               = "";

		Player player2;
		try {
			player2 = Bukkit.getServer().getPlayer(player);

		} catch (NullPointerException e) {
			player2 = null;
		}

		if (player2 == null) {
			DC.setMessages("&7El jugador &f'&b" + player + "&f'&c no &7esta&c disponible&f.");
				API.sendMessage(DC);
			return;
		}

		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		API.setLang(player2, lang);
		plugin.savePlayers();

		DC.setMessages(String.format(
			"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b%s&f.",
			DC.getPlayerName(),
			player2.getName(),
			lang
		));

		DC.setCancelled(true);

		path = "formats.to";
		Message to_model = new Message(
			DC,
			null,
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
			DC.getMessages(),
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
			config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds"))   : null,
			false,

			null,

			util.IF(config, "chat-color-personalized"),
			util.IF(config, "use-PAPI-format")
		);

		API.broadcast(to_model);
	}
}
