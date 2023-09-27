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
	public class LocaleUtil {
		public static String getPlayerLocale(Player player) {
			try {
				Method getHandle = player.getClass().getDeclaredMethod("getHandle");
				getHandle.setAccessible(true);

				Object entityPlayer = getHandle.invoke(player);
				Field locale = entityPlayer.getClass().getField("locale");

				return (String) locale.get(entityPlayer);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	default public void setLang(Object sender, String lang) throws IllegalArgumentException {
//		Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(player, "es");        -> null,
//			setLang(console, "es");       -> null,
//			setLang(offlinePlayer, "fr"); -> null,

//		setLang(Player/offlinePlayer, "Ekisde"); -> IllegalArgumentException...

		ChatTranslator plugin     = ChatTranslator.getInstance();
		Message DC = util.getDataConfigDefault();

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				ChatTranslatorAPI.getInstance().sendMessage(DC);
			return;
		}

		UUID uuid = util.getUUID(sender);

		plugin.storage.set(uuid, null, lang);
	}

	default String getLang(Object sender) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		ChatTranslator plugin     = ChatTranslator.getInstance();
		String lang               = null;
		FileConfiguration config  = plugin.config.get();
		String defaultLang        = config.getString("default-lang");

		Message DC = util._getDataConfigDefault();

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

//				lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

			} else {
				lang = defaultLang;
			}
		}

		if (!Core.GT.isSupport(lang)) {
			if (Core.GT.isSupport(defaultLang)) {
				DC.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
				ChatTranslatorAPI.getInstance().sendMessage(DC);

				lang = defaultLang;

			} else {
				Bukkit.getConsoleSender().sendMessage(ChatTranslatorAPI.getInstance().getColor("&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}

		return lang;
	}
}