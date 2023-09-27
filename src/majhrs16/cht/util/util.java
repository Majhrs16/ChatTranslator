package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.api.Core;
import majhrs16.cht.ChatTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class util {
	private static ChatTranslator plugin = ChatTranslator.getInstance();
	private static ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static Pattern version       = Pattern.compile("\\d+\\.(\\d+)(\\.(\\d+))?");

	public static UUID getUUID(Object sender) {
		UUID uuid;

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			try {
				uuid = ((OfflinePlayer) sender).getUniqueId();

			} catch (NoSuchMethodError e) {
				uuid = ((OfflinePlayer) sender).getPlayer().getUniqueId();
			}

		} else if (sender instanceof CommandSender) {
			uuid = UUID.fromString(plugin.config.get().getString("server-uuid"));

		} else {
			uuid = null;
		}

		return uuid;
	}

	public static Player[] getOnlinePlayers() {
		List<Player> playerList = new ArrayList<Player>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			playerList.add(player);
		}

		return playerList.toArray(new Player[0]);
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
			from.setMessageFormat("%ct_messages%");
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

		return  new Message(
			null,
			sender,
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
			messages,
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
			config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds"))   : null,
			false,

			langSource,
			langTarget,

			IF(config, "chat-color-personalized"),
			IF(config, "use-PAPI-format")
		);
	}

	public static Message createChat(CommandSender sender, String messages, String langSource, String langTarget, String path) {
		if (path == null)
			path = "";

		else
			path = "_" + path;

		String _path = "formats.to" + path;
		Message to = createGroupFormat(sender, messages, langSource, langTarget, _path);

		_path = "formats.from" + path;
		Message from = createGroupFormat(sender, messages, langSource, langTarget, _path);
			from.setTo(to);

		return from;
	}
}