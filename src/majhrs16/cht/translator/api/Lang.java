package majhrs16.cht.translator.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.UUID;

public interface Lang {
	default public void setLang(Object sender, String lang) throws IllegalArgumentException {
//		Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(player, "es");        -> null,
//			setLang(console, "es");       -> null,
//			setLang(offlinePlayer, "fr"); -> null,

//		setLang(Player/offlinePlayer, "Ekisde"); -> IllegalArgumentException...

		UUID uuid;
		ChatTranslator plugin     = ChatTranslator.getInstance();
		FileConfiguration config  = plugin.getConfig();
		Message DC = util.getDataConfigDefault();

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				ChatTranslatorAPI.getInstance().sendMessage(DC);
			return;
		}

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			try {
				uuid = ((OfflinePlayer) sender).getUniqueId();

			} catch (NoSuchMethodError e) {
				uuid = ((OfflinePlayer) sender).getPlayer().getUniqueId();
			}

		} else {
			uuid = UUID.fromString(config.getString("server-uuid"));
		}

		switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
			case "yaml":
				plugin.getPlayers().set(uuid.toString(), lang);;
				break;

			case "sqlite":
				try {
					if (plugin.getSQLite().get(uuid) == null) {
						plugin.getSQLite().insert(uuid, lang);

					} else {
						plugin.getSQLite().update(uuid, lang);
					}

				} catch (SQLException e) {
					DC.setMessages("&cError al escribir en SQLite&f.\n\t" + e.toString());
						ChatTranslatorAPI.getInstance().sendMessage(DC);
				}
				break;

			case "mysql":
				try {
					if (plugin.getMySQL().get(uuid) == null) {
						plugin.getMySQL().insert(uuid, lang);

					} else {
						plugin.getMySQL().update(uuid, lang);
					}

				} catch (SQLException e) {
					DC.setMessages("&cError al escribir en MySQL&f.\n\t" + e.toString());
						ChatTranslatorAPI.getInstance().sendMessage(DC);
				}
				break;
		}
	}

	default public String getPlayerLocale(Player player) {
		try {
			Class<?> craftPlayerClass = player.getClass();
			Method getHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
			getHandleMethod.setAccessible(true);
			Object entityPlayer = getHandleMethod.invoke(player);

			Field localeField = entityPlayer.getClass().getField("locale");
			localeField.setAccessible(true);
			return (String) localeField.get(entityPlayer);

		} catch (Exception e) {
			e.printStackTrace();
			return "en_US"; // Valor predeterminado en caso de error
		}
	}

	default String getLang(Object sender, String to_lang) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		UUID uuid;
		ChatTranslator plugin     = ChatTranslator.getInstance();
		String lang               = null;
		FileConfiguration config  = plugin.getConfig();
		String defaultLang        = config.getString("default-lang");
		Message DC                = new Message(); // Duplique el codigo del util.getDataConfigDefault ya que no veo otra forma.
			DC.setTo(null); // Necesario para evitar crashes.
			DC.setSender(Bukkit.getConsoleSender());
			DC.setMessageFormat("%ct_messages%");
			DC.setLangSource("es");
			DC.setLangTarget(to_lang);
			DC.setColor(true);
			DC.setFormatPAPI(false);

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			try {
				uuid = ((OfflinePlayer) sender).getUniqueId();

			} catch (NoSuchMethodError e) {
				uuid = ((OfflinePlayer) sender).getPlayer().getUniqueId();
			}

		} else {
			uuid = UUID.fromString(config.getString("server-uuid"));
		}

		switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
			case "yaml":
				FileConfiguration players = plugin.getPlayers();
				if (players.contains(uuid.toString())) {
					lang = players.getString(uuid.toString());
				}
				break;

			case "sqlite":
				try {
					lang = plugin.getSQLite().get(uuid);

				} catch (SQLException e) {
					DC.setMessages("&cError al leer en SQLite&f.\n\t" + e.toString());
						ChatTranslatorAPI.getInstance().sendMessage(DC);

				} catch (NullPointerException e) {
					;
				}
				break;

			case "mysql":
				try {
					lang = plugin.getMySQL().get(uuid);

				} catch (SQLException e) {
					DC.setMessages("&cError al leer en MySQL&f.\n\t" + e.toString());
						ChatTranslatorAPI.getInstance().sendMessage(DC);

				} catch (NullPointerException e) {
					;
				}
				break;
		}

		if (lang == null || lang.equals("auto")) {
			if (sender instanceof Player) { //  && util.checkPAPI()
				lang = getPlayerLocale((Player) sender).split("_")[0];
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

	default public String getLang(CommandSender sender) {
		return getLang(
			sender,
			getLang(
				Bukkit.getConsoleSender(),
				ChatTranslator.getInstance().getConfig().getString("default-lang")
		));
	}
}
