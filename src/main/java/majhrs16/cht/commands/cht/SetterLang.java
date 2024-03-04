package majhrs16.cht.commands.cht;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.lib.minecraft.commands.CommandExecutor;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class SetterLang implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		try {
			switch (args.length) {
				case 0: // /cht lang
					DC.getMessages().setTexts("&aSu idioma establecido es&f: &b" + GoogleTranslator.Languages.valueOf(API.getLang(DC.getSender()).toUpperCase()).getValue() + "&f.");
					API.sendMessage(DC);
					break;

				case 1: // /cht lang es
					setLang(DC, args[0]);
					break;

				case 2:  // /cht lang Majhrs16 es
					if (!Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
						DC.getMessages().setTexts("&cUsted no tiene permisos para ejecutar este comando&f.");
						API.sendMessage(DC);
						break;
					}

					setLangAnother(DC, args[0], args[1]);
					break;

				default:
					DC.getMessages().setTexts(
						"&cSintaxis invalida&f. &aPor favor use la sintaxis&f:",
						"    &e/cht lang &f[&6player&f] &f<&6codigo&f>&f."
					);
					API.sendMessage(DC);
			}

		} catch (IllegalArgumentException e) {
			DC.getMessages().setTexts(e.getMessage());
			API.sendMessage(DC);
		}

		return true;
	}

	public void setLang(Message DC, String lang) {
		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.getMessages().setTexts(e.getMessage());
				API.sendMessage(DC);
			return;
		}

		API.setLang(DC.getSender(), lang);

		DC.getMessages().setTexts("&7Su idioma ha sido &aestablecido&7 a &b`" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "`&f.");
		DC.setLangTarget(lang);

		API.sendMessage(DC);
	}

	@SuppressWarnings("deprecation")
	public void setLangAnother(Message DC, String player, String lang) {
		OfflinePlayer to_player;
		try {
			to_player = Bukkit.getOfflinePlayer(player);

			if (!to_player.isOnline() && !to_player.hasPlayedBefore()) {
				throw new NullPointerException();
			}

		} catch (NullPointerException e) {
			DC.getMessages().setTexts("&7El jugador &f'&b" + player + "&f' &cno &cexiste&f.");
				API.sendMessage(DC);
			return;
		}

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.getMessages().setTexts(e.getMessage());
				API.sendMessage(DC);
			return;
		}

		API.setLang(to_player, lang);

		Message model = util.getDataConfigDefault();
			model.getMessages().setTexts(String.format(
				"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b`%s`&f.",
				DC.getSenderName(),
				to_player.getName(),
				GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue()
			));

		Message to_model = model.clone();
			to_model.getMessages().setFormats("$ct_messages$");
		model.setTo(to_model);

		API.broadcast(model, util.getOnlinePlayers(), API::broadcast);
	}
}