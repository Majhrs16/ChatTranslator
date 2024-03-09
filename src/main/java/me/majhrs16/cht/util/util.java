package me.majhrs16.cht.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.translator.api.Core;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class util {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static final Pattern version       = Pattern.compile("\\d+\\.(\\d+)(\\.(\\d+))?");

	public static CommandSender getSenderByName(String playerName) {
		if (playerName == null)
			return Bukkit.getConsoleSender();

		return Bukkit.getServer().getPlayer(playerName);
	}

	public static String[] stripColor(String... array) {
		String[] newArray = array.clone();

		for (int i = 0; i < newArray.length; i++)
			newArray[i] = ChatColor.stripColor(newArray[i].replaceAll(Core.color_hex.pattern(), ""));

		return newArray;
	}

	public static String getNativeLang() {
		return plugin.formats.get().getString("native-lang");
	}

	public static UUID getUUID(Object sender) {
		UUID uuid = null;

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			OfflinePlayer offlinePlayer = (OfflinePlayer) sender;

			if (offlinePlayer.getPlayer() != null)
				uuid = ((OfflinePlayer) sender).getPlayer().getUniqueId();

			else
				try {
					uuid = ((OfflinePlayer) sender).getUniqueId();

				} catch (NoSuchMethodError e) {
					uuid = null;
				}

		} else if (sender instanceof CommandSender) {
			String server_uuid = plugin.config.get().getString("server-uuid");

			if (server_uuid != null)
				uuid = UUID.fromString(server_uuid);
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
			plugin.logger.error(e.toString());
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
		return cfg.contains(path) && cfg.getBoolean(path);
	}

	public static void assertLang(String lang, String text) {
		if (!API.getTranslator().isSupport(lang)) {
			throw new IllegalArgumentException(text);
		}
	}

	public static void assertLang(String lang) {
		assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}

	public static Message getDataConfigDefault() {
		return new Message().setLangTarget(API.getLang(Bukkit.getConsoleSender()));
	}

	public static Message createGroupFormat(CommandSender sender, String[] messages, String langSource, String langTarget, String path) {
		Message DC = new Message();
			DC.setSender(sender);
			DC.getMessages().setTexts(messages);
			DC.getToolTips().setTexts(messages);
			DC.setLangSource(langSource);
			DC.setLangTarget(langTarget);
			DC.setForceColor(false);
			DC.setFormatPAPI(Config.FORMAT_PAPI.IF());
			DC.format(path);

		return DC;
	}

	public static Message createChat(
			CommandSender sender,
			String[] messages,
			String langSource,
			String langTarget,
			String path) {

		path = path == null ?  "" : "_" + path;

		Message to   = createGroupFormat(sender, messages, langSource, langTarget, "to" + path);
		Message from = createGroupFormat(sender, messages, langSource, langTarget, "from" + path);
			from.setTo(to);

		return from;
	}

	public static void applySoundsFormat(Message original, String path) {
		FileConfiguration formats = plugin.formats.get();
		String[] sounds           = new String[] {};

		if (formats.contains(path + ".sounds")) {
			List<String> temp_sounds = new ArrayList<>();

			ConfigurationSection section = formats.getConfigurationSection(path + ".sounds");

			if (section == null)
				return;

			for (String key : section.getKeys(false)) {
				temp_sounds.add(String.format("%s; %s; %s",
					key,
					formats.getInt(path + ".sounds.volume"),
					formats.getInt(path + ".sounds.pitch")
				));
			}

			sounds = temp_sounds.toArray(new String[0]);
		}

		if (sounds.length > 0)
			original.setSounds(sounds);
	}

	/*
	@Deprecated
	private static void applyTextFormat(Message original, String[] formats, String[] texts, Consumer<String[]> formatSetter) {
		Message DC = original.clone();

		DC.getMessages().setFormats(formats);
		DC.getMessages().setTexts(texts);

		Message translated = API.formatMessage(DC);

		try {
			for (int i = 0; i < formats.length; i++) {
				String input = formats[i];

				Matcher matcher = FORMAT_INDEX.matcher(input);
				while (matcher.find()) {
					input = input.replace(
						matcher.group(0),
						translated.getMessages().getFormat(Integer.parseInt(matcher.group(1)))
					);
				}

				formats[i] = input;
			}

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		if (formats.length > 0)
			formatSetter.accept(formats);

		if (original.getMessages().getTexts().length == 0)
			original.getMessages().setTexts(texts);

		// En caso de no haber textos originales, esto es necesario para mostrarse por API.Messages.processMessage.
		if (!original.getMessages().getFormat(0).equals("%ct_messages%")
				&& original.getMessages().getTexts().length == 0)
			original.getMessages().setTexts("\t");
	}*/

	public static void applyToolTipsFormat(Message original, String path, BiConsumer<List<String>, List<String>> preAction) {
		String[] formats = Texts.get(path + ".toolTips.formats");
		String[] texts   = Texts.get(path + ".toolTips.texts");

		// En caso contrario: Textos compartidos.
		if (texts.length == 0)
			texts = Texts.get(path + ".texts");

		if (preAction != null) {
			List<String> formatList = Arrays.asList(formats);
			List<String> textstList = Arrays.asList(texts);

			preAction.accept(formatList, textstList);

			formats = formatList.toArray(new String[0]);
			texts   = textstList.toArray(new String[0]);
		}

		if (formats.length > 0)
			original.getToolTips().setFormats(formats);

		if (texts.length > 0)
			original.getToolTips().setTexts(texts);
	}

	public static void applyMessagesFormat(Message original, String path, BiConsumer<List<String>, List<String>> preAction) {
		String[] formats = Texts.get(path + ".messages.formats");
		String[] texts = Texts.get(path + ".messages.texts");

		// En caso contrario: Variable literal.
		if (formats.length == 0)
			formats = Texts.get(path);

		// En caso contrario: Textos compartidos.
		if (texts.length == 0)
			texts = Texts.get(path + ".texts");

		if (preAction != null) {
			List<String> formatList = Arrays.asList(formats);
			List<String> textstList = Arrays.asList(texts);

			preAction.accept(formatList, textstList);

			formats = formatList.toArray(new String[0]);
			texts   = textstList.toArray(new String[0]);
		}

		if (formats.length > 0)
			original.getMessages().setFormats(formats);

		if (texts.length > 0)
			original.getMessages().setTexts(texts);
	}

	@Deprecated
	public static String convertColorHex(String text) {
		Matcher matcher;
		while ((matcher = API.color_hex.matcher(text)).find()) {
			text = text.replace(matcher.group(0), net.md_5.bungee.api.ChatColor.of(matcher.group(0)).toString());
		}

		return text;
	}
}