package me.majhrs16.cht.translator.api;

import me.majhrs16.lib.network.translator.GoogleTranslator;
import me.majhrs16.lib.network.translator.LibreTranslator;
import me.majhrs16.lib.network.translator.TranslatorBase;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.LocaleUtil;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.UUID;

public interface Lang {
	default void setLang(Object sender, TranslatorBase.LanguagesBase lang) throws NullPointerException {
//		Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(player, GoogleTranslator.Languages.ES);         -> null,
//			setLang(console,  GoogleTranslator.Languages.ES);       -> null,
//			setLang(offlinePlayer,  GoogleTranslator.Languages.FR); -> null,

//		    setLang(OTRA COSA, GoogleTranslator.Languages.ES);      -> NullPointerException.

		UUID uuid = util.getUUID(sender);

		if (uuid == null)
			throw new NullPointerException();

		ChatTranslator.getInstance().storage.set(uuid, null, lang.getCode());
	}

	default TranslatorBase.LanguagesBase getLang(Object sender) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		ChatTranslator plugin     = ChatTranslator.getInstance();
		FileConfiguration config  = plugin.config.get();
		String defaultLang        = config.getString("default-lang");
		String lang               = null;

		UUID uuid = util.getUUID(sender);

		if (uuid != null) {
			String[] values = plugin.storage.get(uuid);

			if (values != null) {
				lang = values[2];

				if (lang.startsWith("off-"))
					lang = "disabled";
			}
		}

		if (lang == null || lang.equals("auto")) {
			if (sender instanceof Player) {
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
				plugin.logger.error(ChatTranslatorAPI.getInstance().convertColor(
						"&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!.")[0]);

				lang = null;
			}
		}

		return util.convertStringToLang(lang);
	}

	default TranslatorBase getTranslator() {
		TranslatorBase translator;

		String engine = "google"; // ChatTranslator.getInstance().config.get().getString("translator-api");

		switch (engine.toLowerCase()) {
			case "google":
				translator = new GoogleTranslator();
				break;

			case "libre":
				translator = new LibreTranslator();
				break;

			default:
				throw new IllegalArgumentException("No such translator engine '" + engine + "'");
		}

		return translator;
	}
}