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

		DC.setMessageFormat("$ct_messages$ &b" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "&f.");
		DC.setMessages("&7Su idioma ha sido &aestablecido&7 a");
		DC.setLang(lang);
			API.sendMessage(DC);
	}

	public void setLangAnother(Message DC, String player, String lang) {
		Player to_player;

		try {
			to_player = Bukkit.getServer().getPlayer(player);

		} catch (NullPointerException e) {
			to_player = null;
		}

		if (to_player == null) {
			DC.setMessages("&7El jugador &f'&b" + player + "&f'&c no &7esta&c disponible&f.");
				API.sendMessage(DC);
			return;
		}

		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		API.setLang(to_player, lang);
		plugin.savePlayers();

		String msg = String.format(
			"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b%s&f.",
			DC.getPlayerName(),
			to_player.getName(),
			GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue()
		);

		Message from = util.getDataConfigDefault();
			from.setPlayer(Bukkit.getConsoleSender());
			from.setLang("es");
			from.setCancelledThis(true);
			from.setMessages(msg);

		Message to_model = util.getDataConfigDefault();
			to_model.setFather(from);
			to_model.setMessages(msg);

		Message console = util.getDataConfigDefault();
			console.setFather(from);
			console.setPlayer(Bukkit.getConsoleSender());
			console.setLang(API.getLang(Bukkit.getConsoleSender()));
			console.setMessages(msg);

		API.broadcast(to_model, console);
	}
}