package me.majhrs16.cht.translator.api;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.logger.Logger;
import me.majhrs16.lib.utils.Str;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.InternetCheckerAsync;
import me.majhrs16.cht.util.cache.Dependencies;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public interface Core {
	Pattern SUB_VARIABLES = Pattern.compile("\\{([a-zA-Z0-9_]+)}");
	Pattern VARIABLES     = Pattern.compile("[%$][A-Z0-9_]+[%$]");
	Pattern COLOR_HEX     = Pattern.compile("#[a-fA-F0-9]{6}");
	Pattern FORMAT_INDEX  = Pattern.compile("[{%](\\d+?)[}%]");
	Pattern LITERAL       = Pattern.compile("`(.+?)`");

	Logger logger         = ChatTranslator.getInstance().logger;

	default String[] translateMessages(
			String[] messages, String[] formats,
			String sourceLang, String targetLang,
			TranslatorBase translator) {
		String[] newArray = messages.clone();

		if (formats.length > 0
				&& !sourceLang.equals("OFF")
				&& !targetLang.equals("OFF")
				&& !sourceLang.equals(targetLang)) {

			for (int i = 0; i < newArray.length; i++) {
				if (newArray[i].isEmpty())
					continue;

				newArray[i] = translator.translate(newArray[i], sourceLang, targetLang);
			}
		}

		return newArray;
	}

	default String[] processExpand(String... messageFormats) {
		String[] newArray = messageFormats.clone();

		for (int i = 0; i < newArray.length; i++) {
			int count = Str.count(newArray[i], "%ct_expand%");
			for (int i2 = count; i2 > 0; i2--) {
				int padding = (70 - util.stripColor(newArray[i].replace("%ct_expand%", ""))[0].length()) / i2;

				logger.debug("padding: %s", padding);
				logger.debug("i2:      %s", i2);

				newArray[i] = newArray[i].replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		return newArray;
	}

	default String[] processEscapes(String[] messages, ArrayList<String> escapesList) {
		String[] newArray = messages.clone();

		int escapeCounter = 10;
		for (int i = 0; i < newArray.length; i++) {
			Matcher matcher = LITERAL.matcher(newArray[i]);
			while (matcher.find()) {
				if (!escapesList.contains(matcher.group(1))) {
					escapesList.add(matcher.group(1));
					newArray[i] = newArray[i].replace(matcher.group(0),
						"[" + Str.rjust(Integer.toHexString(escapeCounter), 2, "0") + "]");
					escapeCounter++;
				}
			}
		}

		return newArray;
	}

	default String[] revertEscapes(String[] messages, ArrayList<String> escapes) {
		String[] newArray = messages.clone();

		int i = 10;
		for (String escape : escapes) {
			newArray = replaceArray(newArray, "\\[" + Str.rjust(Integer.toHexString(i), 2, "0") + "]", escape);
			i++;
		}

		return newArray;
	}

	default String[] replaceArray(String[] array, String target, String replacement, int max) {
		String[] newArray = array.clone();

		for (int i = 0; i < max; i++)
			newArray[i] = newArray[i].replaceAll(target, replacement);

		return newArray;
	}

	default String[] replaceArray(String[] array, String target, String replacement) {
		return replaceArray(array, target, replacement, array.length);
	}

	default String[] replaceArray(String[] array, String target, String[] replacements) {
 		return replaceArray(array, target, String.join("\n", replacements));
	}

	default String[] parseSubVariables(Player player, String... formats) {
		String[] newArray = formats.clone();

		for (int i = 0; i < newArray.length; i++) {
			String input = newArray[i];

			Matcher matcher = SUB_VARIABLES.matcher(input);
			while (matcher.find())
				input = input.replace(matcher.group(0), PlaceholderAPI.setPlaceholders(player, "%" + matcher.group(1) + "%"));

			newArray[i] = PlaceholderAPI.setPlaceholders(player, input);
		}

		return newArray;
	}

	default String[] convertVariablesToLowercase(String... formats) {
		String[] newArray = formats.clone();

		for (int i = 0; i < newArray.length; i++) {
			Matcher matcher;
			while ((matcher = VARIABLES.matcher(newArray[i])).find())
				newArray[i] = newArray[i].replace(matcher.group(0), matcher.group(0).toLowerCase());
		}

		return newArray;
	}

	default String[] convertColor(String... inputs) {
		String[] newArray = inputs.clone();

		for (int i = 0; i < newArray.length; i++)
			newArray[i] = ChatColor.translateAlternateColorCodes('&', newArray[i]);

		return newArray;
	}

	default String[] processFormatIndex(String[] formats, String[] texts) {
		String[] newArray = formats.clone();

		for (int i = 0; i < newArray.length; i++) {
			String input = newArray[i];


			Matcher matcher = FORMAT_INDEX.matcher(input);
			while (matcher.find()) {
				try {
					input = input.replace(
						matcher.group(0),
						texts[Integer.parseInt(matcher.group(1))]
					);

				} catch (IndexOutOfBoundsException e) {
					logger.debug("IndexOutOfBoundsException: %s", e.toString());
				}
			}

			newArray[i] = input;
		}

		return newArray;
	}

	default String[] preFormatIndexes(String... formats) {
		return replaceArray(formats, FORMAT_INDEX.pattern(), "%$1%");
	}

	default String[] escapePapiVariables(String... formats) {
		return replaceArray(formats, "[%$].+[%$]", "`$0`");
	}

	default void replaceFormats(String oldStr, String newStr, String[]... formats) {
		if (newStr == null)
			return;

		for (String[] format : formats) {
			String[] newFormat = replaceArray(format, oldStr, newStr);
			System.arraycopy(
				newFormat, 0,
				format, 0,
				format.length
			);
		}
	}

	default void replaceFormats(String oldStr, String[] newStr, String[]... formats) {
		if (newStr == null)
			return;

		for (String[] format : formats) {
			String[] newFormat = replaceArray(format, oldStr, newStr);
			System.arraycopy(
				newFormat, 0,
				format, 0,
				format.length
			);
		}
	}

	default Message formatMessage(Message original) {
//		Se clona para evitar sobre-escrituras en el evento.
		Message from              = original.clone();
		CommandSender from_player = from.getSender();

		String[] from_tool_tips_formats = from.getToolTips().getFormats();
		String[] from_messages_formats  = from.getMessages().getFormats();
		String[] from_tool_tips_texts   = from.getToolTips().getTexts();
		String[] from_messages_texts    = from.getMessages().getTexts();
		String[] from_sounds            = from.getSounds();

		String from_lang_source = from.getLangSource().getCode();
		String from_lang_target = from.getLangTarget().getCode();


		Message to              = from.getTo();
		CommandSender to_player = to.getSender();

		String[] to_tool_tips_formats = to.getToolTips().getFormats();
		String[] to_messages_formats  = to.getMessages().getFormats();
		String[] to_tool_tips_texts   = to.getToolTips().getTexts();
		String[] to_messages_texts    = to.getMessages().getTexts();
		String[] to_sounds            = to.getSounds();

		String to_lang_source = to.getLangSource().getCode();
		String to_lang_target = to.getLangTarget().getCode();


		byte is_color          = from.isColor();
		boolean is_papi        = from.getFormatPAPI();


		ArrayList<String> from_tool_tips_escapes = new ArrayList<>();
		ArrayList<String> from_messages_escapes  = new ArrayList<>();
		ArrayList<String> to_tool_tips_escapes   = new ArrayList<>();
		ArrayList<String> to_messages_escapes    = new ArrayList<>();

		from_tool_tips_formats = convertVariablesToLowercase(from_tool_tips_formats);
		from_messages_formats  = convertVariablesToLowercase(from_messages_formats);
		to_tool_tips_formats   = convertVariablesToLowercase(to_tool_tips_formats);
		to_messages_formats    = convertVariablesToLowercase(to_messages_formats);

		from_tool_tips_texts = escapePapiVariables(from_tool_tips_texts);
		from_messages_texts  = escapePapiVariables(from_messages_texts);
		to_tool_tips_texts   = escapePapiVariables(to_tool_tips_texts);
		to_messages_texts    = escapePapiVariables(to_messages_texts);

		from_tool_tips_texts = processEscapes(from_tool_tips_texts, from_tool_tips_escapes);
		from_messages_texts  = processEscapes(from_messages_texts, from_messages_escapes);
		to_tool_tips_texts   = processEscapes(to_tool_tips_texts, to_tool_tips_escapes);
		to_messages_texts    = processEscapes(to_messages_texts, to_messages_escapes);

		replaceFormats(
			"\\%ct_lang_source\\%", from_lang_source,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_lang_source\\$", to_lang_source,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\%ct_lang_target\\%", from_lang_target,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_lang_target\\$", to_lang_target,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\%player_name\\%", from.getSenderName(),
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$player_name\\$", to.getSenderName(),
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		if (InternetCheckerAsync.isInternetAvailable()) {
			TranslatorBase translator = ChatTranslatorAPI.getInstance().getTranslator();
			from_tool_tips_texts = translateMessages(from_tool_tips_texts, from_tool_tips_formats, from_lang_source, from_lang_target, translator);
			from_messages_texts  = translateMessages(from_messages_texts, from_messages_formats, from_lang_source, from_lang_target, translator);
			to_tool_tips_texts   = translateMessages(to_tool_tips_texts, to_tool_tips_formats, to_lang_source, to_lang_target, translator);
			to_messages_texts    = translateMessages(to_messages_texts, to_messages_formats, to_lang_source, to_lang_target, translator);

		} else {
			from_tool_tips_formats = replaceArray(from_tool_tips_formats, "(.+)", "[!] $1");
			from_messages_formats  = replaceArray(from_messages_formats, "(.+)", "[!] $1");
			to_tool_tips_formats   = replaceArray(to_tool_tips_formats, "(.+)", "[!] $1");
			to_messages_formats    = replaceArray(to_messages_formats, "(.+)", "[!] $1");
		}

		if (from_player != null) {
			if (is_color == 1) {
				from_tool_tips_texts = convertColor(from_tool_tips_texts);
				from_messages_texts  = convertColor(from_messages_texts);

			} else if (is_color == 0 && Permissions.ChatTranslator.Chat.COLOR.IF(original)) {
				from_tool_tips_texts = convertColor(from_tool_tips_texts);
				from_messages_texts  = convertColor(from_messages_texts);

			} else if (is_color == -1) {
				from_tool_tips_texts = util.stripColor(from_tool_tips_texts);
				from_messages_texts  = util.stripColor(from_messages_texts);
			}
		}

		if (to_player != null) {
			if (is_color == 1) {
				to_tool_tips_texts = convertColor(to_tool_tips_texts);
				to_messages_texts  = convertColor(to_messages_texts);

			} else if (is_color == 0 && Permissions.ChatTranslator.Chat.COLOR.IF(original)) {
				to_tool_tips_texts = convertColor(to_tool_tips_texts);
				to_messages_texts  = convertColor(to_messages_texts);

			} else if (is_color == -1) {
				to_tool_tips_texts = util.stripColor(from_tool_tips_texts);
				to_messages_texts  = util.stripColor(from_messages_texts);
			}
		}

		replaceFormats(
			"\\%ct_messages\\%", from_messages_texts,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_messages\\$", to_messages_texts,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\%ct_toolTips\\%", from_tool_tips_texts,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_toolTips\\$", to_tool_tips_texts,
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		if (is_papi && Dependencies.PAPI.exist()) {
			Player _from_player    = (from_player instanceof Player) ? (Player) from_player : null;
			Player _to_player      = (to_player   instanceof Player) ? (Player) to_player   : null;

			from_messages_formats  = parseSubVariables(_from_player, from_messages_formats);
			from_messages_formats  = parseSubVariables(_to_player, replaceArray(from_messages_formats, "\\$", "%"));

			to_messages_formats    = parseSubVariables(_from_player, to_messages_formats);
			to_messages_formats    = parseSubVariables(_to_player, replaceArray(to_messages_formats, "\\$", "%"));

			from_tool_tips_formats = parseSubVariables(_from_player, from_tool_tips_formats);
			from_tool_tips_formats = parseSubVariables(_to_player, replaceArray(from_tool_tips_formats, "\\$", "%"));

			to_tool_tips_formats   = parseSubVariables(_from_player, to_tool_tips_formats);
			to_tool_tips_formats   = parseSubVariables(_to_player, replaceArray(to_tool_tips_formats, "\\$", "%"));

		} else {
			from_tool_tips_formats = preFormatIndexes(from_tool_tips_formats);
			from_messages_formats  = preFormatIndexes(from_messages_formats);
			to_tool_tips_formats   = preFormatIndexes(to_tool_tips_formats);
			to_messages_formats    = preFormatIndexes(to_messages_formats);
		}

		from_tool_tips_formats = processFormatIndex(from_tool_tips_formats, from_tool_tips_texts);
		from_messages_formats  = processFormatIndex(from_messages_formats, from_messages_texts);
		to_tool_tips_formats   = processFormatIndex(to_tool_tips_formats, to_tool_tips_texts);
		to_messages_formats    = processFormatIndex(to_messages_formats, to_messages_texts);

		from_tool_tips_formats  = convertColor(from_tool_tips_formats);
		from_messages_formats   = convertColor(from_messages_formats);
		to_tool_tips_formats    = convertColor(to_tool_tips_formats);
		to_messages_formats     = convertColor(to_messages_formats);

		replaceFormats(
			"\\\\t", "\t",
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		// En caso de no haber textos originales, esto es necesario para mostrarse por API.Messages.showMessage.
		if (from_messages_formats.length > 0
				&& !(from_messages_formats[0].endsWith("%ct_messages%") || from_messages_formats[0].endsWith("%0%"))
				&& from_messages_texts.length == 0) {

			from_messages_texts = new String[] { "\t" };
		}

		from_tool_tips_formats = revertEscapes(from_tool_tips_formats, from_tool_tips_escapes);
		from_messages_formats  = revertEscapes(from_messages_formats, from_messages_escapes);
		to_tool_tips_formats   = revertEscapes(to_tool_tips_formats, to_tool_tips_escapes);
		to_messages_formats    = revertEscapes(to_messages_formats, to_messages_escapes);

		from_messages_formats = processExpand(from_messages_formats);
		to_messages_formats   = processExpand(to_messages_formats);

		from.getToolTips().setFormats(from_tool_tips_formats);
		from.getMessages().setFormats(from_messages_formats);
		from.getToolTips().setTexts(from_tool_tips_texts);
		from.getMessages().setTexts(from_messages_texts);
		from.setSounds(from_sounds);

		to.getToolTips().setFormats(to_tool_tips_formats);
		to.getMessages().setFormats(to_messages_formats);
		to.getToolTips().setTexts(to_tool_tips_texts);
		to.getMessages().setTexts(to_messages_texts);
		to.setSounds(to_sounds);

		return from;
	}
}