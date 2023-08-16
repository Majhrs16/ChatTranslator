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

		API.setLang(DC.getTo().getSender(), lang);
		plugin.savePlayers();

		DC.getTo().setMessageFormat("$ct_messages$ &b" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "&f.");
		DC.getTo().setMessages("&7Su idioma ha sido &aestablecido&7 a");
		DC.getTo().setLang(lang);
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
			DC.getTo().setMessages("&7El jugador &f'&b" + player + "&f'&c no &7esta&c disponible&f.");
				API.sendMessage(DC);
			return;
		}

		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		API.setLang(to_player, lang);
		plugin.savePlayers();

		String msg = String.format(
			"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b%s&f.",
			DC.getTo().getSenderName(),
			to_player.getName(),
			GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue()
		);

		Message from = util.getDataConfigDefault();
			from.setMessages(msg);

		Message to_model = util.getDataConfigDefault();
			to_model.setMessages(msg);
		from.setTo(to_model);

		Message console = util.getDataConfigDefault();
			console.setSender(Bukkit.getConsoleSender());
			console.setLang(API.getLang(Bukkit.getConsoleSender()));
			console.setMessages(msg);
		to_model.setTo(console);

		API.broadcast(from);
	}
}