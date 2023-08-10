package majhrs16.cht.commands.cht;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class SetterLang {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void setLang(Message DC, String lang) {
		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		API.setLang(DC.getPlayer(), lang);
		plugin.savePlayers();

		DC.setMessages("&7Su idioma ha sido &aestablecido&7 a &b" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "&f.");
		DC.setLang(lang);
			API.sendMessage(DC);
	}
	
	public void setLangAnother(Message DC, String player, String lang) {
		String path = "";

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
			GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue()
		));

		DC.setCancelled(true);

		path = "formats.to";
		path = "formats.";
		Message console  = util.createMessage(DC, Bukkit.getConsoleSender(), DC.getMessages(), false, API.getLang(Bukkit.getConsoleSender()), path + "console");
		Message to_model = util.createMessage(DC, null,                      DC.getMessages(), false, null, path + "to");

		API.broadcast(to_model, console);
	}
}
