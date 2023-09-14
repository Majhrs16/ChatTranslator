package majhrs16.cht.translator.api;

import majhrs16.lib.network.translator.GoogleTranslator;
import me.clip.placeholderapi.PlaceholderAPI;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.lib.utils.Str;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public interface Core {
	public GoogleTranslator GT = new GoogleTranslator();

	Pattern sub_variables = Pattern.compile("\\{([a-z0-9_]+)\\}", Pattern.CASE_INSENSITIVE);
	Pattern color_hex     = Pattern.compile("#[a-f0-9]{6}", Pattern.CASE_INSENSITIVE);
	Pattern no_translate  = Pattern.compile("`(.+)`", Pattern.CASE_INSENSITIVE);
	Pattern variables     = Pattern.compile("[\\%\\$][A-Z0-9_]+[\\%\\$]"); // ARREGLR EL lowercase EXCESIVO!!

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

	default public String getColor(String text) {
//			Convierte el color RGB y tradicional a un formato visible.

		if (text == null)
			return text;

		if (util.getMinecraftVersion() >= 16.0) { // 1.16.0
			/*
			Matcher matcher;
			while ((matcher = color_hex.matcher(text)).find())
				text = text.replace(matcher.group(0), "" + net.md_5.bungee.api.ChatColor.of(matcher.group(0)));
			*/
		}

		return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
	}

	default public Message formatMessage(Message DC) {
//			Este formateador basicamente remplaza (sub)vriables PAPI y locales, colorea el chat y/o el formato de este, tambien para los tooltips, y ya por ultimo traduce el mensaje. 

		DC = DC.clone(); // se clona para evitar sobreescrituras en el evento.

		Message from               = DC;
		CommandSender from_player  = from.getSender();
		String from_message_format = from.getMessageFormat();
		String from_messages       = from.getMessages();
		String from_tool_tips      = from.getToolTips();
		String from_lang_source    = from.getLangSource();
		String from_lang_target    = from.getLangTarget();

		Message to               = from.getTo();
		CommandSender to_player  = to.getSender();
		String to_message_format = to.getMessageFormat();
		String to_messages       = to.getMessages();
		String to_tool_tips      = to.getToolTips();
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
			if (from_player != null)
				from_message_format = from_message_format.replace("%player_name%", from_player.getName());

			if (to_player != null)
				from_message_format = from_message_format.replace("$player_name$", to_player.getName());
		}

		if (to_message_format != null) {
			if (from_player != null)
				to_message_format = to_message_format.replace("%player_name%",  from_player.getName());

			if (to_player != null)
				to_message_format = to_message_format.replace("$player_name$", to_player.getName());
		}

		if (from_tool_tips != null) {
			if (from_player != null)
				from_tool_tips = from_tool_tips.replace("%player_name%", "`" + from_player.getName() + "`");

			if (to_player != null)
				from_tool_tips = from_tool_tips.replace("$player_name$", "`" + to_player.getName() + "`");
		}

		if (to_tool_tips != null) {
			if (from_player != null)
				to_tool_tips = to_tool_tips.replace("%player_name%", "`" + from_player.getName() + "`");

			if (to_player != null)
				to_tool_tips = to_tool_tips.replace("$player_name$", "`" + to_player.getName() + "`");
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

		if (from_message_format != null)
			from_message_format = getColor(from_message_format);
		if (to_message_format != null)
			to_message_format = getColor(to_message_format);

		if (from_tool_tips != null && (color || (from_player != null && Permissions.chattranslator.Color.FROM_COLOR.IF(from_player))))
			from_tool_tips = getColor(from_tool_tips);

		if (to_tool_tips != null && (color || (to_player != null && Permissions.chattranslator.Color.TO_COLOR.IF(to_player))))
			to_tool_tips = getColor(to_tool_tips);

		if (from_messages != null && (color || (from_player != null && Permissions.chattranslator.Color.FROM_COLOR.IF(from_player))))
			from_messages = getColor(from_messages);

		if (to_messages != null && (color || (to_player != null && Permissions.chattranslator.Color.TO_COLOR.IF(to_player))))
			to_messages = getColor(to_messages);

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
				int padding = (70 - org.bukkit.ChatColor.stripColor(from_message_format).replace("%ct_expand%", "").length()) / i;

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
				int padding = (70 - org.bukkit.ChatColor.stripColor(to_message_format).replace("%ct_expand%", "").length()) / i;

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

		DC.setMessageFormat(from_message_format);
		DC.setMessages(from_messages);
		DC.setToolTips(from_tool_tips);

		DC.getTo().setMessageFormat(to_message_format);
		DC.getTo().setMessages(to_messages);
		DC.getTo().setToolTips(to_tool_tips);

		return DC;
	}
}