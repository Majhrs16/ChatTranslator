package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.api.Core;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.UUID;

public class util {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static final Pattern version       = Pattern.compile("\\d+\\.(\\d+)(\\.(\\d+))?");

	public static UUID getUUID(Object sender) {
		UUID uuid = null;

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			try {
				uuid = ((OfflinePlayer) sender).getPlayer().getUniqueId();

			} catch (NullPointerException e) {
				uuid = ((OfflinePlayer) sender).getUniqueId();
			}

		} else if (sender instanceof CommandSender) {
			uuid = UUID.fromString(plugin.config.get().getString("server-uuid"));
		}

		return uuid;
	}

	public static Player[] getOnlinePlayers() {
		try {
			Object players = Bukkit.getServer().getClass().getMethod("getOnlinePlayers").invoke(Bukkit.getServer());

			if (players instanceof Player[]) {
				return (Player[]) players;

			} else if (players instanceof Collection<?>) {
				return ((Collection<?>) players).toArray(new Player[0]);

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static double getMinecraftVersion() {
		Matcher matcher = version.matcher(Bukkit.getVersion());

		if (matcher.find()) {
			String version = matcher.group(1) + "." + (matcher.group(3) == null ? 0 : matcher.group(3));
			return Double.parseDouble(version);
		}

		return 0.0;
	}

	public static boolean IF(FileConfiguration cfg, String path) {
			// Comprobador rapido si existe una configuracion y si es true.

		return cfg.contains(path) && cfg.getBoolean(path);
	}

	public static void assertLang(String lang, String text) {
			// Si no esta soportado lang lanza una excepcion IllegalArgumentException junto con text. 

		if (!Core.GT.isSupport(lang)) {
			throw new IllegalArgumentException(text);
		}
	}

	public static void assertLang(String lang) {
		assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}

	public static Message _getDataConfigDefault() {
		Message from = new Message();
			from.setTo(null); // Necesario para evitar crashes.
			from.setSender(Bukkit.getConsoleSender());
			from.setMessagesFormats("%ct_messages%");
			from.setLangSource(plugin.messages.get().getString("native-lang"));
			from.setLangTarget(plugin.storage.getDefaultLang());
			from.setColor(true);
			from.setFormatPAPI(false);

		return from;
	}

	public static Message getDataConfigDefault() {
		Message from = _getDataConfigDefault();
			from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));

		return from;
	}

	public static Message createGroupFormat(CommandSender sender, String messages, String langSource, String langTarget, String path) {
		FileConfiguration config = plugin.config.get();

		Message from = new Message();
			from.setSender(sender);
			from.setMessagesFormats(config.contains("formats." + path + ".messages") ? path : null);
			from.setMessages(messages);
			from.setToolTips(config.contains("formats." + path + ".toolTips")        ? path : null);
			from.setSounds(config.contains("formats." + path + ".sounds")            ? path : null);

			from.setLangSource(langSource);
			from.setLangTarget(langTarget);

			from.setColor(Config.CHAT_COLOR.IF());
			from.setFormatPAPI(Config.FORMAT_PAPI.IF());
		return from;
	}

	public static Message createChat(CommandSender sender, String messages, String langSource, String langTarget, String path) {
		path = path == null ?  "" : "_" + path;

		Message to   = createGroupFormat(sender, messages, langSource, langTarget, "to" + path);
		Message from = createGroupFormat(sender, messages, langSource, langTarget, "from" + path);
			from.setTo(to);

		return from;
	}
}