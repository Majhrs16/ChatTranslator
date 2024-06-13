package me.majhrs16.cht.util;

import me.majhrs16.lib.network.translator.GoogleTranslator;
import me.majhrs16.lib.network.translator.LibreTranslator;
import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;
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

	public static String[] stripColor(String... array) {
		String[] newArray = array.clone();

		for (int i = 0; i < newArray.length; i++)
			newArray[i] = BukkitUtils.stripColor(newArray[i]);

		return newArray;
	}

	public static TranslatorBase.LanguagesBase convertStringToLang(String lang) {
		lang = lang.toUpperCase();

		TranslatorBase translator = API.getTranslator();
		if (translator instanceof GoogleTranslator)
			return GoogleTranslator.Languages.valueOf(lang);

		else if (translator instanceof LibreTranslator)
			return GoogleTranslator.Languages.valueOf(lang);

		else
			throw new IllegalArgumentException("Translator engine invalid!");
	}

	public static TranslatorBase.LanguagesBase getNativeLang() {
		String lang = plugin.formats.get().getString("native-lang");
		return convertStringToLang(lang);
	}

	public static UUID getUUID(Object sender) {
		return BukkitUtils.getUUID(plugin.config, sender);
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

	public static Message.Builder createGroupFormat(
			CommandSender sender,
			String[] messages,
			TranslatorBase.LanguagesBase langSource,
			TranslatorBase.LanguagesBase langTarget,
			String path) {

		return new Message.Builder()
			.setSender(sender)
			.setMessages(new Formats.Builder().setTexts(messages))
			.setToolTips(new Formats.Builder().setTexts(messages))
			.setLangSource(langSource)
			.setLangTarget(langTarget)
			.setColor(0)
			.setFormatPAPI(Config.FORMAT_PAPI.IF())
			.format(path);
	}

	public static Message.Builder createChat(
			CommandSender sender,
			String[] messages,
			TranslatorBase.LanguagesBase langSource,
			TranslatorBase.LanguagesBase langTarget,
			String path) {

		path = path == null ?  "" : "_" + path;

		return createGroupFormat(sender, messages, langSource, langTarget, "from" + path)
			.setTo(createGroupFormat(sender, messages, langSource, langTarget, "to" + path));
	}

	public static void applySoundsFormat(Message.Builder original, String path) {
		FileConfiguration formats = plugin.formats.get();
		String[] sounds           = new String[0];

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

		original.setSounds(sounds);
	}

	public static void applyToolTipsFormat(Message.Builder original, String path, BiConsumer<List<String>, List<String>> preAction) {
		String[] formats = Texts.get(path + ".toolTips.formats");
		String[] texts   = Texts.get(path + ".toolTips.texts");

//		En caso contrario: Textos compartidos.
		if (texts.length == 0)
			texts = Texts.get(path + ".texts");

		if (preAction != null) {
			List<String> formatList = Arrays.asList(formats);
			List<String> textstList = Arrays.asList(texts);

			preAction.accept(formatList, textstList);

			formats = formatList.toArray(new String[0]);
			texts   = textstList.toArray(new String[0]);
		}

		original.setToolTips(new Formats.Builder()
			.setFormats(formats)
			.setTexts(texts.length > 0 ? texts : original.build().getToolTips().getTexts())
		);
	}

	public static void applyMessagesFormat(Message.Builder original, String path, BiConsumer<List<String>, List<String>> preAction) {
		String[] formats = Texts.get(path + ".messages.formats");
		String[] texts   = Texts.get(path + ".messages.texts");
		String[] source  = Texts.get(path + ".sourceLang");
		String[] target  = Texts.get(path + ".targetLang");

//		En caso contrario: Variable literal.
		if (formats.length == 0)
			formats = Texts.get(path);

//		En caso contrario: Textos compartidos.
		if (texts.length == 0)
			texts = Texts.get(path + ".texts");

		if (preAction != null) {
			List<String> formatList = Arrays.asList(formats);
			List<String> textstList = Arrays.asList(texts);

			preAction.accept(formatList, textstList);

			formats = formatList.toArray(new String[0]);
			texts   = textstList.toArray(new String[0]);
		}

//		En caso de no existir el grupo de formato, usar los datos en memoria,
		original.setMessages(new Formats.Builder()
			.setFormats(formats)
			.setTexts(texts.length > 0 ? texts : original.build().getMessages().getTexts())
		);

		if (source.length > 0)
			original.setLangSource(convertStringToLang(source[0]));

		if (target.length > 0)
			original.setLangTarget(convertStringToLang(target[0]));
	}
}