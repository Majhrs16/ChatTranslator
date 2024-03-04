package majhrs16.cht.translator.api;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.lib.network.translator.TranslatorBase;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.LocaleUtil;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

public interface Lang {
	default void setLang(Object sender, String lang) throws IllegalArgumentException {
//		Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(player, "es");        -> null,
//			setLang(console, "es");       -> null,
//			setLang(offlinePlayer, "fr"); -> null,

//		setLang(Player/offlinePlayer, "Ekisde"); -> IllegalArgumentException...

		ChatTranslator plugin     = ChatTranslator.getInstance();

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			Message from = util.getDataConfigDefault();
				from.getMessages().setTexts(e.getMessage());
				ChatTranslatorAPI.getInstance().sendMessage(from);
			return;
		}

		UUID uuid = util.getUUID(sender);

		plugin.storage.set(uuid, null, lang);
	}

	default String getLang(Object sender) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		ChatTranslator plugin     = ChatTranslator.getInstance();
		FileConfiguration config  = plugin.config.get();
		String defaultLang        = config.getString("default-lang");
		String lang               = null;

		UUID uuid = util.getUUID(sender);

		if (uuid != null) {
			String[] values = plugin.storage.get(uuid);

			if (values != null)
				lang = values[2];
		}

		if (lang == null || lang.equals("auto")) {
			if (sender instanceof Player) { // && util.checkPAPI()
				String locale = LocaleUtil.getPlayerLocale((Player) sender);

				if (locale == null) {
					lang = defaultLang;

				} else {
					lang = locale.split("_")[0];
				}

			} else {
				lang = defaultLang;
			}
		}

		TranslatorBase translator = getTranslator();
		if (!translator.isSupport(lang)) {
			if (!translator.isSupport(defaultLang)) {
				Message from = new Message();
					from.getMessages().setTexts("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
					ChatTranslatorAPI.getInstance().sendMessage(from);

				lang = defaultLang;

			} else {
				Bukkit.getConsoleSender().sendMessage(ChatTranslatorAPI.getInstance().convertColor(
					"&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}

		return lang;
	}

	default TranslatorBase getTranslator() {
		TranslatorBase translator;

//		String engine = ChatTranslator.getInstance().config.get().getString("translator-api");

//		switch (engine.toLowerCase()) {
//			case "google":
				translator = new GoogleTranslator();
//				break;

//			case "libre":
//				translator = new LibreTranslator();
//				break;

//			default:
//				throw new IllegalArgumentException("No such translator engine '" + engine + "'");
//		}

		return translator;
	}
}