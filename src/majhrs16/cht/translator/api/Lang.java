package majhrs16.cht.translator.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.UUID;

public interface Lang {
	class LocaleUtil {
		public static String getPlayerLocale(Player player) {
			String playerLocale = null;

			try {
				Method getHandle = player.getClass().getDeclaredMethod("getHandle");
				getHandle.setAccessible(true);

				Object entityPlayer = getHandle.invoke(player);

				if (entityPlayer != null) {
					if (util.getMinecraftVersion() < 7.2) { // Por debajo de la 1.7.
						Field localeField   = entityPlayer.getClass().getDeclaredField("locale");
						localeField.setAccessible(true);
						Object localeObject = localeField.get(entityPlayer);

						if (localeObject != null) {
							Field eField = localeObject.getClass().getDeclaredField("e");
							eField.setAccessible(true);
							playerLocale = (String) eField.get(localeObject);
						}

					} else if (util.getMinecraftVersion() <= 20.1) { // Version 1.20.1 o anterior.
						playerLocale = (String) entityPlayer.getClass().getField("locale").get(entityPlayer);

					} else if (util.getMinecraftVersion() >= 20.2) {  // Versión 1.20.2 o superior
						playerLocale = (String) entityPlayer.getClass().getField("cM").get(entityPlayer);

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return playerLocale;
		}
	}

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
				from.setMessages(e.getMessage()); ChatTranslatorAPI.getInstance().sendMessage(from);
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

		if (!Core.GT.isSupport(lang)) {
			if (Core.GT.isSupport(defaultLang)) {
				Message from = new Message();
					from.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f."); ChatTranslatorAPI.getInstance().sendMessage(from);

				lang = defaultLang;

			} else {
				Bukkit.getConsoleSender().sendMessage(ChatTranslatorAPI.getInstance().getColor("&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}

		return lang;
	}
}