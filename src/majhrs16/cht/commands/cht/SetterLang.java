package majhrs16.cht.commands.cht;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
// import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class SetterLang {
//	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public void setLang(Message DC, String lang) {
		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				API.sendMessage(DC);
			return;
		}

		API.setLang(DC.getSender(), lang);

		DC.setMessages("&7Su idioma ha sido &aestablecido&7 a &b`" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "`&f.");
		DC.setLangTarget(lang);
			API.sendMessage(DC);
	}

	@SuppressWarnings("deprecation")
	public void setLangAnother(Message DC, String player, String lang) {
		OfflinePlayer to_player;
		try {
			to_player = Bukkit.getOfflinePlayer(player);

			if (to_player == null || !to_player.hasPlayedBefore()) {
				throw new NullPointerException();
			}

		} catch (NullPointerException e) {
			DC.setMessages("&7El jugador &f'&b" + player + "&f' &cno &cexiste&f.");
				API.sendMessage(DC);
			return;
		}

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				API.sendMessage(DC);
			return;
		}

		API.setLang(to_player, lang);

		String msg = String.format(
			"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b`%s`&f.",
			DC.getSenderName(),
			to_player.getName(),
			GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue()
		);

		Message from = util.getDataConfigDefault();
			from.setMessages(msg);

		Message to_model = from.clone();
			to_model.setMessageFormat("$ct_messages$");
		from.setTo(to_model);

		API.broadcast(from);
	}
}