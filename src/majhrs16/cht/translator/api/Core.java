package majhrs16.cht.translator.api;

import majhrs16.cht.ChatTranslator;
import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.lib.utils.Str;
import majhrs16.cht.util.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public interface Core {
	public GoogleTranslator GT = new GoogleTranslator();

	Pattern sub_variables = Pattern.compile("\\{([a-z0-9_]+)\\}", Pattern.CASE_INSENSITIVE);
	Pattern no_translate  = Pattern.compile("`(.+)`", Pattern.CASE_INSENSITIVE);
	Pattern variables     = Pattern.compile("[%$][A-Z0-9_]+[%$]"); // CORREGIR el mal procesado de PAPI con la modifiacion de ct_messages,
	Pattern color_hex     = Pattern.compile("#[a-fA-Z0-9]{6}");

	default public String parseSubVarables(Player player, String input) {
		if (input == null)
			return input;

		Matcher matcher = sub_variables.matcher(input);
		while (matcher.find())
			input = input.replace(matcher.group(0), PlaceholderAPI.setPlaceholders(player, "%" + matcher.group(1) + "%"));
		return  PlaceholderAPI.setPlaceholders(player, input);
	}

	default public String convertVariablesToLowercase(String input) {
		if (input == null)
			return input;

		Matcher matcher;
		while ((matcher = variables.matcher(input)).find())
			input = input.replace(matcher.group(0), matcher.group(0).toLowerCase());
		return input;
	}

	@Deprecated
	default public String getColorJ16(String text) {
		try {
			Class<?> chatColorClass = Class.forName("net.md_5.bungee.api.ChatColor");

			Matcher matcher;
			while ((matcher = color_hex.matcher(text)).find()) {
				try {
					Object chatColorObj = chatColorClass.getMethod("of", String.class).invoke(null, matcher.group(0));
					String replacement  = (String) chatColorClass.getMethod("toString").invoke(chatColorObj);

					text = text.replace(matcher.group(0), replacement);

				} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return text;
	}

	default public String getColor(String text) {
//			Convierte tradicional a un formato visible.

        if (text == null)
            return text;

/*		if (util.getMinecraftVersion() >= 16.0) { // 1.16.0
			text = getColorJ16(text);
		} */


		return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
	}

	default String getFormat(String chat, String format) {
		FileConfiguration config = ChatTranslator.getInstance().config.get();
		String path = "formats." + format + "."  + chat;
		if (Config.DEBUG.IF())
			System.out.println("DEBUG exists '" + path + "' ?: " + config.contains(path));
		return config.contains(path) ? String.join("\n", config.getStringList(path)) : format;
	}

	default public Message formatMessage(Message original) {
//			Este formateador basicamente remplaza (sub)vriables PAPI y locales, colorea el chat y/o el formato de este, tambien para los tooltips, y ya por ultimo traduce el mensaje. 

		Message from               = original.clone(); // se clona para evitar sobreescrituras en el evento.
		CommandSender from_player  = from.getSender();

		String from_message_format = getFormat("messages", from.getMessagesFormats());
		String from_messages       = from.getMessages();
		String from_tool_tips      = getFormat("toolTips", from.getToolTips());
		String from_sounds         = getFormat("sounds", from.getSounds());

		String from_lang_source    = from.getLangSource();
		String from_lang_target    = from.getLangTarget();


		Message to               = from.getTo();
		CommandSender to_player  = to.getSender();

		String to_message_format = getFormat("messages", to.getMessagesFormats());
		String to_messages       = to.getMessages();
		String to_tool_tips      = getFormat("toolTips", to.getToolTips());
		String to_sounds         = getFormat("sounds", to.getSounds());

		String to_lang_source    = to.getLangSource();
		String to_lang_target    = to.getLangTarget();


		Boolean color = to.getColor();
		Boolean papi  = to.getFormatPAPI();


		ArrayList<String> from_messages_escapes = new ArrayList<String>();
		ArrayList<String> to_messages_escapes   = new ArrayList<String>();

		ArrayList<String> from_tool_tips_escapes = new ArrayList<String>();
		ArrayList<String> to_tool_tips_escapes   = new ArrayList<String>();

		if (from_message_format != null)
			from_message_format = convertVariablesToLowercase(from_message_format);
	
		if (to_message_format != null)
			to_message_format   = convertVariablesToLowercase(to_message_format);

		if (from_tool_tips != null)
			from_tool_tips = convertVariablesToLowercase(from_tool_tips);
	
		if (to_tool_tips != null)
			to_tool_tips = convertVariablesToLowercase(to_tool_tips);

		if (from_message_format != null) {
			if (from_lang_source != null)
				from_message_format = from_message_format.replace("%ct_lang_source%", from_lang_source);

			if (to_lang_target != null)
				from_message_format = from_message_format.replace("$ct_lang_target$", to_lang_target);
		}

		if (from_tool_tips != null) {
			if (from_lang_source != null)
				from_tool_tips = from_tool_tips.replace("%ct_lang_source%", from_lang_source);

			if (to_lang_target != null)
				from_tool_tips = from_tool_tips.replace("$ct_lang_target$", to_lang_target);
		}

		if (to_message_format != null) {
			if (from_lang_source != null)
				to_message_format = to_message_format.replace("%ct_lang_source%", from_lang_source);

			if (to_lang_target != null)
				to_message_format = to_message_format.replace("$ct_lang_target$", to_lang_target);
		}

		if (to_tool_tips != null) {
			if (from_lang_source != null)
				to_tool_tips = to_tool_tips.replace("%ct_lang_source%", from_lang_source);

			if (to_lang_target != null)
				to_tool_tips = to_tool_tips.replace("$ct_lang_target$", to_lang_target);
		}

		if (from_message_format != null) {
			if (from.getSenderName() != null)
				from_message_format = from_message_format.replace("%player_name%", from.getSenderName());

			if (to.getSenderName() != null)
				from_message_format = from_message_format.replace("$player_name$", to.getSenderName());
		}

		if (to_message_format != null) {
			if (from.getSenderName() != null)
				to_message_format = to_message_format.replace("%player_name%",  from.getSenderName());

			if (to.getSenderName() != null)
				to_message_format = to_message_format.replace("$player_name$", to.getSenderName());
		}

		if (from_tool_tips != null) {
			if (from.getSenderName() != null)
				from_tool_tips = from_tool_tips.replace("%player_name%", "`" + from.getSenderName() + "`");

			if (to.getSenderName() != null)
				from_tool_tips = from_tool_tips.replace("$player_name$", "`" + to.getSenderName() + "`");
		}

		if (to_tool_tips != null) {
			if (from.getSenderName() != null)
				to_tool_tips = to_tool_tips.replace("%player_name%", "`" + from.getSenderName() + "`");

			if (to.getSenderName() != null)
				to_tool_tips = to_tool_tips.replace("$player_name$", "`" + to.getSenderName() + "`");
		}

		if (from_message_format != null) {
			from_message_format = from_message_format.replace("%ct_messages%", "x00");
			from_message_format = from_message_format.replace("$ct_messages$", "x01");
		}
		if (to_message_format != null) {
			to_message_format = to_message_format.replace("%ct_messages%", "x00");
			to_message_format = to_message_format.replace("$ct_messages$", "x01");
		}

		if (from_tool_tips != null) {
			from_tool_tips = from_tool_tips.replace("%ct_messages%", "x00");
			from_tool_tips = from_tool_tips.replace("$ct_messages$", "x01");
		}
		if (to_tool_tips != null) {
			to_tool_tips = to_tool_tips.replace("%ct_messages%", "x00");
			to_tool_tips = to_tool_tips.replace("$ct_messages$", "x01");
		}

		if (Dependencies.PAPI.exist() && papi) {
			Player _from_player, _to_player;

			if (from_player instanceof Player)
				_from_player = (Player) from_player;

			else
				_from_player = null;

			if (to_player instanceof Player)
				_to_player = (Player) to_player;

			else
				_to_player = null;

			if (from_message_format != null) {
				from_message_format = parseSubVarables(_from_player, from_message_format);
				from_message_format = parseSubVarables(_to_player, from_message_format.replace("$", "%"));
			}

			if (from_tool_tips != null) {
				from_tool_tips = parseSubVarables(_from_player, from_tool_tips);
				from_tool_tips = parseSubVarables(_to_player, from_tool_tips.replace("$", "%"));
			}

			if (to_message_format != null) {
				to_message_format = parseSubVarables(_from_player, to_message_format);
				to_message_format = parseSubVarables(_to_player, to_message_format.replace("$", "%"));
			}

			if (to_tool_tips != null) {
				to_tool_tips = parseSubVarables(_from_player, to_tool_tips);
				to_tool_tips = parseSubVarables(_to_player, to_tool_tips.replace("$", "%"));
			}
		}

		if (from_messages != null) {
			Matcher matcher = no_translate.matcher(from_messages);
			int i = 10;
			while (matcher.find()) {
				String escape = matcher.group(0);
				if (!from_messages_escapes.contains(escape)) {
					from_messages_escapes.add(escape);
					from_messages = from_messages.replace(escape, "x" + Str.rjust(Integer.toHexString(i), 2, "0"));
					i++;
				}
			}
		}

		if (to_messages != null) {
			Matcher matcher = no_translate.matcher(to_messages);
			int i = 10;
			while (matcher.find()) {
				String escape = matcher.group(0);
				if (!to_messages_escapes.contains(escape)) {
					to_messages_escapes.add(escape);
					to_messages = to_messages.replace(escape, "x" + Str.rjust(Integer.toHexString(i), 2, "0"));
					i++;
				}
			}
		}

		if (from_tool_tips != null) {
			Matcher matcher = no_translate.matcher(from_tool_tips);
			int i = 10;
			while (matcher.find()) {
				String escape = matcher.group(0);
				if (!from_tool_tips_escapes.contains(escape)) {
					from_tool_tips_escapes.add(escape);
					from_tool_tips = from_tool_tips.replace(escape, "x" + Str.rjust(Integer.toHexString(i), 2, "0"));
					i++;
				}
			}
		}

		if (to_tool_tips != null) {
			Matcher matcher = no_translate.matcher(to_tool_tips);
			int i = 10;
			while (matcher.find()) {
				String escape = matcher.group(0);
				if (!to_tool_tips_escapes.contains(escape)) {
					to_tool_tips_escapes.add(escape);
					to_tool_tips = to_tool_tips.replace(escape, "x" + Str.rjust(Integer.toHexString(i), 2, "0"));
					i++;
				}
			}
		}

		if (from_lang_source != null
				&& from_lang_target != null
				&& !from_lang_source.equals("off")
				&& !from_lang_target.equals("off")
				&& !from_lang_source.equals(from_lang_target)) {

			if (from_messages != null && from_message_format != null && from_message_format.contains("x00"))
				from_messages = GT.translate(from_messages, from_lang_source, from_lang_target);

			if (from_tool_tips != null)
				from_tool_tips = GT.translate(from_tool_tips, from_lang_source, from_lang_target);
		}

		if (to_lang_source != null
				&& to_lang_target != null
				&& !to_lang_source.equals("off")
				&& !to_lang_target.equals("off")
				&& !to_lang_source.equals(to_lang_target)) {

			if (to_messages != null && to_message_format != null && to_message_format.contains("x01"))
				to_messages = GT.translate(to_messages, to_lang_source, to_lang_target);

			if (to_tool_tips != null) // No hace falta pensar mas. Si from y to son de distinto idioma, mejor traducirlos...
				to_tool_tips = GT.translate(to_tool_tips, to_lang_source, to_lang_target);
		}

		if (from_messages != null) {
			int i = 10;
			for (String escape : from_messages_escapes) {
				from_messages = from_messages.replace("x" + Str.rjust(Integer.toHexString(i), 2, "0"), escape.substring(1, escape.length() - 1));
				i++;
			}
		}

		if (to_messages != null) {
			int i = 10;
			for (String escape : to_messages_escapes) {
				to_messages = to_messages.replace("x" + Str.rjust(Integer.toHexString(i), 2, "0"), escape.substring(1, escape.length() - 1));
				i++;
			}
		}

		if (from_tool_tips != null) {
			int i = 10;
			for (String escape : from_tool_tips_escapes) {
				from_tool_tips = from_tool_tips.replace("x" + Str.rjust(Integer.toHexString(i), 2, "0"), escape.substring(1, escape.length() - 1));
				i++;
			}
		}

		if (to_tool_tips != null) {
			int i = 10;
			for (String escape : to_tool_tips_escapes) {
				to_tool_tips = to_tool_tips.replace("x" + Str.rjust(Integer.toHexString(i), 2, "0"), escape.substring(1, escape.length() - 1));
				i++;
			}
		}

		from_messages_escapes = null;
		to_messages_escapes   = null;

		if (from_message_format != null) {
			from_message_format = from_message_format.replace("x00", "%ct_messages%");
			from_message_format = from_message_format.replace("x01", "$ct_messages$");
		}
		if (to_message_format != null) {
			to_message_format = to_message_format.replace("x00", "%ct_messages%");
			to_message_format = to_message_format.replace("x01", "$ct_messages$");
		}

		if (from_tool_tips != null) {
			from_tool_tips = from_tool_tips.replace("x00", "%ct_messages%");
			from_tool_tips = from_tool_tips.replace("x01", "$ct_messages$");
		}
		if (to_tool_tips != null) {
			to_tool_tips = to_tool_tips.replace("x00", "%ct_messages%");
			to_tool_tips = to_tool_tips.replace("x01", "$ct_messages$");
		}

		if (from_messages != null && color)
			if (from_player != null && Permissions.ChatTranslator.Chat.COLOR.IF(original))
				from_messages = getColor(from_messages);

		if (to_messages != null && color)
			if (to_player != null && Permissions.ChatTranslator.Chat.COLOR.IF(original))
				to_messages = getColor(to_messages);

		if (from_message_format != null)
			from_message_format = getColor(from_message_format);

		if (to_message_format != null)
			to_message_format = getColor(to_message_format);

		if (from_tool_tips != null)
			from_tool_tips = getColor(from_tool_tips);

		if (to_tool_tips != null)
			to_tool_tips = getColor(to_tool_tips);

		if (from_message_format != null) {
			if (from_messages != null)
				from_message_format = from_message_format.replace("%ct_messages%", from_messages);

			if (to_messages != null)
				from_message_format = from_message_format.replace("$ct_messages$", to_messages);
		}

		if (to_message_format != null) {
			if (from_messages != null)
				to_message_format = to_message_format.replace("%ct_messages%", from_messages);

			if (to_messages != null)
				to_message_format = to_message_format.replace("$ct_messages$", to_messages);
		}

		if (from_tool_tips != null) {
			if (from_messages != null)
				from_tool_tips = from_tool_tips.replace("%ct_messages%", from_messages);

			if (to_messages != null)
				from_tool_tips = from_tool_tips.replace("$ct_messages$", to_messages);
		}

		if (to_tool_tips != null) {
			if (from_messages != null)
				to_tool_tips = to_tool_tips.replace("%ct_messages%", from_messages);

			if (to_messages != null)
				to_tool_tips = to_tool_tips.replace("$ct_messages$", to_messages);
		}

		if (from_message_format != null) {
			int count = Str.count(from_message_format, "%ct_expand%");
			for(int i = count; i > 0; i--) {
				int padding = (70 - util.stripColor(from_message_format).replace("%ct_expand%", "").length()) / i;

				if (Config.DEBUG.IF()) {
					System.out.println("Debug 03, i: " + i);
					System.out.println("Debug 03, padding: " + padding);
				}

				from_message_format = from_message_format.replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		if (to_message_format != null) {
			int count = Str.count(to_message_format, "%ct_expand%");
			for(int i = count; i > 0; i--) {
				int padding = (70 - util.stripColor(to_message_format).replace("%ct_expand%", "").length()) / i;

				if (Config.DEBUG.IF()) {
					System.out.println("Debug 03, i: " + i);
					System.out.println("Debug 03, padding: " + padding);
				}

				to_message_format = to_message_format.replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		if (from_message_format != null)
			from_message_format = from_message_format.replace("\\t", "\t"); 
	
		if (to_message_format != null)
			to_message_format = to_message_format.replace("\\t", "\t"); 

		if (from_tool_tips != null)
			from_tool_tips = from_tool_tips.replace("\\t", "\t");
	
		if (to_tool_tips != null)
			to_tool_tips = to_tool_tips.replace("\\t", "\t");

		from.setMessagesFormats(from_message_format);
		from.setMessages(from_messages);
		from.setToolTips(from_tool_tips);
		from.setSounds(from_sounds);

		to.setMessagesFormats(to_message_format);
		to.setMessages(to_messages);
		to.setToolTips(to_tool_tips);
		to.setSounds(to_sounds);

		return from;
	}
}
