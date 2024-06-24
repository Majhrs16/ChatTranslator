package me.majhrs16.cht.translator.api;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.logger.Logger;
import me.majhrs16.lib.utils.Str;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.InternetCheckerAsync;
import me.majhrs16.cht.util.cache.Dependencies;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public interface Core {
	Pattern SUB_VARIABLES = Pattern.compile("\\{([a-zA-Z0-9_]+)}");
	Pattern VARIABLES     = Pattern.compile("[%$][A-Z0-9_]+[%$]");
	Pattern COLOR_HEX     = Pattern.compile("#[a-fA-F0-9]{6}");
	Pattern FORMAT_INDEX  = Pattern.compile("[{%](\\d+?)[}%]");
	Pattern LITERAL       = Pattern.compile("`(.+?)`");

	Logger logger         = ChatTranslator.getInstance().logger;

	default String[] translateMessages(
			String[] texts, String[] formats,
			String sourceLang, String targetLang,
			TranslatorBase translator) {

		if (formats.length == 0
				|| sourceLang.equals("OFF")
				|| targetLang.equals("OFF")
				|| sourceLang.equals(targetLang))
			return texts;

		String[] newTexts = new String[texts.length];

		IntStream.range(0, texts.length)
			.parallel()
			.forEach(i -> {
				String text = texts[i];
				newTexts[i] = text.isEmpty() ? text : translator.translate(text, sourceLang, targetLang);
			});

		return newTexts;
	}

	default String[] processExpand(String... formats) {
		return Arrays.stream(formats).map(format -> {
			StringBuilder builder = new StringBuilder(format);
			int count = Str.count(format, "%ct_expand%");
			for (int i = count; i > 0; i--) {
				int padding = Math.max(1, (70 - util.stripColor(builder.toString().replace("%ct_expand%", ""))[0].length()) / i);
				logger.debug("padding = %s, i = %s", padding, i);
				int index = builder.indexOf("%ct_expand%");
				if (index != -1) {
					builder.replace(index, index + 11, Str.repeat(" ", padding));
				}
			}

			return builder.toString();

		}).toArray(String[]::new);
	}

	default String[] processEscapes(String[] messages, ArrayList<String> escapesList) {
		AtomicInteger escapeCounter = new AtomicInteger(10);

		return Arrays.stream(messages).map(message -> {
			StringBuilder builder = new StringBuilder(message);
			Matcher matcher = LITERAL.matcher(builder);
			while (matcher.find()) {
				String group = matcher.group(1);
				if (!escapesList.contains(group)) {
					escapesList.add(group);
					String replacement = "[" + Str.rjust(Integer.toHexString(escapeCounter.getAndIncrement()), 2, "0") + "]";
					builder.replace(matcher.start(), matcher.end(), replacement);
					matcher.reset(builder);
				}
			}

			return builder.toString();

		}).toArray(String[]::new);
	}

	default String[] revertEscapes(String[] messages, ArrayList<String> escapes) {
		AtomicInteger counter = new AtomicInteger(10);

		return Arrays.stream(messages).map(message -> {
			StringBuilder sb = new StringBuilder(message);
			for (String escape : escapes) {
				String pattern = "\\[" + Str.rjust(Integer.toHexString(counter.getAndIncrement()), 2, "0") + "]";
				int index;
				while ((index = sb.indexOf(pattern)) != -1) {
					sb.replace(index, index + pattern.length(), escape);
				}
			}

			return sb.toString();

		}).toArray(String[]::new);
	}

	default String[] replaceArray(String[] array, String target, String replacement, int max) {
		if (max == 0 || array.length == 0) return array;
		return Arrays.stream(array)
			.limit(max)
			.map(format -> format.replaceAll(target, replacement))
			.toArray(String[]::new);
	}

	default String[] replaceArray(String[] array, String target, String replacement) {
		return replaceArray(array, target, replacement, array.length);
	}

	default String[] replaceArray(String[] array, String target, String[] replacements) {
 		return replaceArray(array, target, String.join("\n", replacements));
	}

	default String[] parseSubVariables(Player player, String... formats) {
		return Arrays.stream(formats).map(format -> {
			StringBuilder builder = new StringBuilder(format);
			Matcher matcher = SUB_VARIABLES.matcher(builder);
			while (matcher.find()) {
				String placeholder = "%" + matcher.group(1) + "%";
				String replacement = PlaceholderAPI.setPlaceholders(player, placeholder);
				builder.replace(
					matcher.start(),
					matcher.end(),
					replacement
				);
				matcher.reset(builder);
			}
			return PlaceholderAPI.setPlaceholders(player, builder.toString());

		}).toArray(String[]::new);
	}

	default String[] convertVariablesToLowercase(String... formats) {
		return Arrays.stream(formats).map(format -> {
			StringBuilder sb = new StringBuilder(format);
			Matcher matcher = VARIABLES.matcher(sb);
			while (matcher.find()) {
				sb.replace(
					matcher.start(),
					matcher.end(),
					matcher.group().toLowerCase()
				);
			}
			return sb.toString();

		}).toArray(String[]::new);
	}

	default String[] convertColor(String... formats) {
		return Arrays.stream(formats)
			.map(format -> ChatColor.translateAlternateColorCodes('&', format))
			.toArray(String[]::new);
	}

	default String[] processFormatIndex(String[] formats, String[] texts) {
		return Arrays.stream(formats).map(format -> {
			StringBuilder builder = new StringBuilder(format);
			Matcher matcher = FORMAT_INDEX.matcher(builder);
			while (matcher.find()) {
				try {
					int index = Integer.parseInt(matcher.group(1));
					String replacement = texts[index];
					builder.replace(matcher.start(), matcher.end(), replacement);
					matcher.reset(builder); // Espero esto no traiga problemas...

				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					logger.warn(e.toString());
				}
			}

			return builder.toString();

		}).toArray(String[]::new);
	}

	default String[] preFormatIndexes(String... formats) {
		return replaceArray(formats, FORMAT_INDEX.pattern(), "%$1%");
	}

	default String[] escapePapiVariables(String... formats) {
		return replaceArray(formats, "[%$].+[%$]", "`$0`");
	}

	default void replaceFormats(String oldStr, String newStr, String[]... formatsArray) {
		if (newStr == null)
			return;

		Arrays.stream(formatsArray).forEach(formats -> {
			String[] newFormats = replaceArray(formats, oldStr, newStr);
			System.arraycopy(
				newFormats, 0,
				formats, 0,
				formats.length
			);
		});
	}

	default void replaceFormats(String oldStr, String[] newStr, String[]... formatsArray) {
		if (oldStr == null || newStr == null)
			return;

		Arrays.stream(formatsArray).forEach(formats -> {
			String[] newFormats = replaceArray(formats, oldStr, newStr);
			System.arraycopy(
				newFormats, 0,
				formats, 0,
				formats.length
			);
		});
	}

	default Message formatMessage(Message original) {
//		Se clona para evitar sobre-escrituras en el evento.
		Message from              = original.clone().build();
		CommandSender from_player = from.getSender();

		String[] from_tool_tips_formats = from.getToolTips().getFormats();
		String[] from_messages_formats  = from.getMessages().getFormats();
		String[] from_tool_tips_texts   = from.getToolTips().getTexts();
		String[] from_messages_texts    = from.getMessages().getTexts();
		String[] from_sounds            = from.getSounds();

		TranslatorBase.LanguagesBase from_lang_source = from.getLangSource();
		TranslatorBase.LanguagesBase from_lang_target = from.getLangTarget();


		Message to              = from.getTo();
		CommandSender to_player = to.getSender();

		String[] to_tool_tips_formats = to.getToolTips().getFormats();
		String[] to_messages_formats  = to.getMessages().getFormats();
		String[] to_tool_tips_texts   = to.getToolTips().getTexts();
		String[] to_messages_texts    = to.getMessages().getTexts();
		String[] to_sounds            = to.getSounds();

		TranslatorBase.LanguagesBase to_lang_source = to.getLangSource();
		TranslatorBase.LanguagesBase to_lang_target = to.getLangTarget();


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
			"\\%ct_lang_source\\%", from_lang_source.getCode(),
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_lang_source\\$", to_lang_source.getCode(),
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\%ct_lang_target\\%", from_lang_target.getCode(),
			from_tool_tips_formats,
			from_messages_formats,
			to_tool_tips_formats,
			to_messages_formats
		);

		replaceFormats(
			"\\$ct_lang_target\\$", to_lang_target.getCode(),
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
			from_tool_tips_texts = translateMessages(
				from_tool_tips_texts,
				from_tool_tips_formats,
				from_lang_source.getCode(),
				from_lang_target.getCode(),
				translator
			);

			from_messages_texts  = translateMessages(
				from_messages_texts,
				from_messages_formats,
				from_lang_source.getCode(),
				from_lang_target.getCode(),
				translator
			);

			to_tool_tips_texts   = translateMessages(
				to_tool_tips_texts,
				to_tool_tips_formats,
				to_lang_source.getCode(),
				to_lang_target.getCode(),
				translator
			);

			to_messages_texts    = translateMessages(
				to_messages_texts,
				to_messages_formats,
				to_lang_source.getCode(),
				to_lang_target.getCode(),
				translator
			);

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

		return new Message.Builder(null, null)
			.setSender(from_player)
			.setMessages(new Formats.Builder()
				.setFormats(from_messages_formats)
				.setTexts(from_messages_texts)

			).setToolTips(new Formats.Builder()
				.setFormats(from_tool_tips_formats)
				.setTexts(from_tool_tips_texts)

			).setColor(is_color)
			.setSounds(from_sounds)
			.setLangSource(from_lang_source)
			.setLangTarget(from_lang_target)

			.setTo(new Message.Builder(null, null)
				.setSender(to_player)
				.setMessages(new Formats.Builder()
					.setFormats(to_messages_formats)
					.setTexts(to_messages_texts)

				).setToolTips(new Formats.Builder()
					.setFormats(to_tool_tips_formats)
					.setTexts(to_tool_tips_texts)

				).setColor(is_color)
				.setSounds(to_sounds)
				.setLangSource(to_lang_source)
				.setLangTarget(to_lang_target)
			).build();
	}
}
