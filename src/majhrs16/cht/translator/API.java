package majhrs16.cht.translator;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.bool.Dependencies;
import majhrs16.cht.bool.Permissions;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.bool.Config;
import majhrs16.lib.utils.Str;
import majhrs16.cht.util.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.function.Consumer;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class API {
	private static  ChatTranslator plugin = ChatTranslator.plugin;
	
	private static Pattern sub_variables = Pattern.compile("\\{([a-z0-9_]+)\\}", Pattern.CASE_INSENSITIVE);
	private static Pattern color_hex     = Pattern.compile("#[a-f0-9]{6}", Pattern.CASE_INSENSITIVE);
	private static Pattern variables     = Pattern.compile("[\\%\\$][A-Z0-9_]+[\\%\\$]"); // ARREGLR EL lowercase EXCESIVO!!

	public static GoogleTranslator GT = new GoogleTranslator();


	public static String parseSubVarables(Player player, String input) {
		Matcher SubVar;
		while ((SubVar = sub_variables.matcher(input)).find())
			input = input.replace(SubVar.group(0), PlaceholderAPI.setPlaceholders(player, "%" + SubVar.group(1) + "%"));
		return  PlaceholderAPI.setPlaceholders(player, input);
	}

/*	public static String parseSubVarables(Player player, String input) {
		Matcher SubVar = sub_variables.matcher(input);
		StringBuffer result = new StringBuffer();

		while (SubVar.find()) {
			String replacement = PlaceholderAPI.setPlaceholders(player, "%" + SubVar.group(1) + "%");
			SubVar.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}

		SubVar.appendTail(result);
	    return result.toString();
	}*/

	public static String convertVariablesToLowercase(String input) {
		Matcher matcher = variables.matcher(input);
		while (matcher.find())
			input = input.replace(matcher.group(0), matcher.group(0).toLowerCase());
		return input;
	}

	public static String getColor(String text) {
//			Convierte el color RGB y tradicional a un formato visible.

		String version = Bukkit.getVersion();
		if (version.contains("1.16") &&
				version.contains("1.17") &&
				version.contains("1.18") &&
				version.contains("1.19") &&
				version.contains("1.20")) {
			Matcher matcher;
			while ((matcher = color_hex.matcher(text)).find())
				text = text.replace(matcher.group(0), "" + ChatColor.of(matcher.group(0)));
		}

		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static Message formatMessage(Message DC) {
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

		if (from_message_format != null)
			from_message_format = convertVariablesToLowercase(from_message_format);
	
		if (to_message_format != null)
			to_message_format   = convertVariablesToLowercase(to_message_format);

		if (from_lang_source != null && to_lang_target != null) {
			if (from_message_format != null) {
				from_message_format = from_message_format.replace("%ct_lang_source%", from_lang_source);
				from_message_format = from_message_format.replace("$ct_lang_target$", to_lang_target);
			}

			if (from_tool_tips != null) {
				from_tool_tips = from_tool_tips.replace("%ct_lang_source%", from_lang_source);
				from_tool_tips = from_tool_tips.replace("$ct_lang_target$", to_lang_target);
			}

			if (to_message_format != null) {
				to_message_format = to_message_format.replace("%ct_lang_source%", from_lang_source);
				to_message_format = to_message_format.replace("$ct_lang_target$", to_lang_target);
			}

			if (to_tool_tips != null) {
				to_tool_tips = to_tool_tips.replace("%ct_lang_source%", from_lang_source);
				to_tool_tips = to_tool_tips.replace("$ct_lang_target$", to_lang_target);
			}
		}

		if (from_player != null && to_player != null) {
			if (from_message_format != null) {
				from_message_format = from_message_format.replace("%player_name%", from_player.getName());
				from_message_format = from_message_format.replace("$player_name$", to_player.getName());
			}

			if (from_tool_tips != null) {
				from_tool_tips = from_tool_tips.replace("%player_name%", from_player.getName());
				from_tool_tips = from_tool_tips.replace("$player_name$", to_player.getName());
			}

			if (to_message_format != null) {
				to_message_format = to_message_format.replace("%player_name%", from_player.getName());
				to_message_format = to_message_format.replace("$player_name$", to_player.getName());
			}

			if (to_tool_tips != null) {
				to_tool_tips = to_tool_tips.replace("%player_name%", from_player.getName());
				to_tool_tips = to_tool_tips.replace("$player_name$", to_player.getName());
			}
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

		if (color) {
			if (from_tool_tips != null)
				from_tool_tips = getColor(from_tool_tips);

			if (to_tool_tips != null)
				to_tool_tips = getColor(to_tool_tips);

			if (from_messages != null && from_player != null && Permissions.chattranslator.Color.FROM_COLOR.IF(from_player))
				from_messages = getColor(from_messages);

			if (to_messages != null && to_player != null && Permissions.chattranslator.Color.TO_COLOR.IF(to_player))
				to_messages = getColor(to_messages);
		}

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
				int padding = (70 - ChatColor.stripColor(from_message_format).replace("%ct_expand%", "").length()) / i;

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
				int padding = (70 - ChatColor.stripColor(to_message_format).replace("%ct_expand%", "").length()) / i;

				if (Config.DEBUG.IF()) {
					System.out.println("Debug 03, i: " + i);
					System.out.println("Debug 03, padding: " + padding);
				}

				to_message_format = to_message_format.replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		DC.setMessageFormat(from_message_format);
		DC.setMessages(from_messages);
		DC.setToolTips(from_tool_tips);

		DC.getTo().setMessageFormat(to_message_format);
		DC.getTo().setMessages(to_messages);
		DC.getTo().setToolTips(to_tool_tips);

		return DC;
	}

	@SuppressWarnings("deprecation")
	public static void processMessage(Message formatted) {
//			Envia el Message a su destinario. Pero es necesario formatearlo previamente con formatMessage.

		if (!new Message().equals(formatted)
					&& !formatted.isCancelled()
					&& formatted.getLangSource() != null
					&& formatted.getLangTarget() != null
					&& !formatted.getLangSource().equals("disabled")
					&& !formatted.getLangTarget().equals("disabled")
					&& formatted.getMessageFormat() != null
					&& formatted.getMessages() != null
				) {

			 if (formatted.getSender() instanceof Player) {
				TextComponent message = new TextComponent(formatted.getMessageFormat());

				if (formatted.getToolTips() != null) {
					message.setHoverEvent(new HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(formatted.getToolTips()).create()
					));
				}

				Player player = ((Player) formatted.getSender());
				player.spigot().sendMessage(message);
			
				if (formatted.getSounds() != null) {
					for (String line : formatted.getSounds().split("\n")) {
						line = line.trim().toUpperCase();

						try {
							Sound sound = Sound.valueOf(line);

							player.playSound(player.getLocation(), sound, 1, 1); // agregar soporte para pitch y volume!!!

						} catch (IllegalArgumentException e) {
							Message msg = util.getDataConfigDefault();
								msg.setSender(Bukkit.getConsoleSender());
								msg.setLangTarget(getLang(Bukkit.getConsoleSender()));
								msg.setMessages("&eSonido &f'&bformats&f.&bfrom&f.&bsounds&f.&b" + line + "&f' &cinvalido&f.");
							 sendMessage(msg);
						}
					}
				}
			
			} else {
				CommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(formatted.getMessageFormat());
				if (formatted.getToolTips() != null) {
					for (String line : formatted.getToolTips().split("\n")) {
						console.sendMessage(line);
					}
				}
			}
		 }
	}

	public static void sendMessage(Message event) {
//			Envia los mensajes especificados en father y el objeto Message actual.

		if (event == new Message())
			return;

		try {
			Message formatted = formatMessage(event);

			if (Config.DEBUG.IF()) {
				System.out.println("DEBUG: Format, Msgs, ToolTips, LangSource, LangTarget");
				System.out.println(String.format("DEBUG from: '%s', '%s', '%s', '%s' -> '%s'",
					formatted.getMessageFormat(),
					formatted.getMessages(),
					formatted.getToolTips(),
					formatted.getLangSource(),
					formatted.getLangTarget()
				));

				System.out.println(String.format("DEBUG to:   '%s', '%s', '%s', '%s' -> '%s'",
					formatted.getTo().getMessageFormat(),
					formatted.getTo().getMessages(),
					formatted.getTo().getToolTips(),
					formatted.getTo().getLangSource(),
					formatted.getTo().getLangTarget()
				));
			}

			processMessage(formatted);
			processMessage(formatted.getTo());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void broadcast(List<Message> messages, Consumer<Message> preBroadcastAction) {
		for (Message to : messages) {
			try {
				util.assertLang(to.getLangSource());
				util.assertLang(to.getLangTarget());

				if (preBroadcastAction != null)
					preBroadcastAction.accept(to);

			} catch (IllegalArgumentException e) {
				Message alert = util.getDataConfigDefault();
					alert.setMessages(String.format("&b%s&f: &cIdioma &f'&b%s&f' &cy&f/&co &f'&b%s&f' &cno soportado&f.",
						to.getSenderName(),
						to.getLangSource(),
						to.getLangTarget()
					));
				sendMessage(alert);
			}
		}
	}

	public static void broadcast(List<Message> messages) {
		broadcast(messages, to -> majhrs16.cht.util.ChatLimiter.chat.add(to));
	}

	 public static void broadcast(Message from, Consumer<List<Message>> preBroadcastAction) {
		if (from == null
				|| from.equals(new Message())
				|| from.getTo().equals(new Message()))
			return;

		List<Message> tos = new ArrayList<Message>();

		Message to_model = from.getTo();

		to_model.setSender(from.getSender());
		to_model.setLangTarget(from.getLangTarget()); // Por si no se establece en el to_model el lang_target.

		to_model.setCancelledThis(true);  // Evitar duplicacion.
		tos.add(from.clone());
		from.setCancelledThis(true);      // Evitar duplicacion.

		to_model.setCancelledThis(false); // Restaurar funcionalidad.
		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(from.getSender().equals(to_player))
				continue;

			to_model.setSender(to_player);
			to_model.setLangTarget(getLang(to_player));
			tos.add(from.clone());
		}

		if (preBroadcastAction != null)
			preBroadcastAction.accept(tos);

		broadcast(tos);
	}

	 public static void broadcast(Message from) {
		 broadcast(from, null);
	 }

	public static void setLang(Object sender, String lang) throws IllegalArgumentException {
//		Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(player, "es");        -> null,
//			setLang(console, "es");       -> null,
//			setLang(offlinePlayer, "fr"); -> null,

//		setLang(Player/offlinePlayer, "Ekisde"); -> IllegalArgumentException...

		UUID uuid;
		FileConfiguration config  = plugin.getConfig();
		Message DC = util.getDataConfigDefault();

		try {
			util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				API.sendMessage(DC);
			return;
		}

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} if (sender instanceof OfflinePlayer) {
			uuid = ((OfflinePlayer) sender).getUniqueId();

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
						sendMessage(DC);
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
						sendMessage(DC);
				}
				break;
		}
	}

	public static String getPlayerLocale(Player player) {
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

	private static String getLang(CommandSender sender, String to_lang) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		UUID uuid;
		String lang               = null;
		FileConfiguration config  = plugin.getConfig();
		String defaultLang        = config.getString("default-lang");
		Message DC                = new Message(); // Duplique el codigo del util.getDataConfigDefault ya que no veo otra forma.
			DC.setTo(null); // Necesario para evitar crashes.
			DC.setSender(Bukkit.getConsoleSender());
			DC.setMessageFormat("$ct_messages$");
			DC.setLangSource("es");
			DC.setLangTarget(to_lang);
			DC.setColor(true);
			DC.setFormatPAPI(false);

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

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
						sendMessage(DC);

				} catch (NullPointerException e) {
					;
				}
				break;

			case "mysql":
				try {
					lang = plugin.getMySQL().get(uuid);

				} catch (SQLException e) {
					DC.setMessages("&cError al leer en MySQL&f.\n\t" + e.toString());
						sendMessage(DC);

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

		if (!GT.isSupport(lang)) {
			if (GT.isSupport(defaultLang)) {
				DC.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
					sendMessage(DC);

				lang = defaultLang;

			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}

		return lang;
	}

	public static String getLang(CommandSender sender) {
		return getLang(
			sender,
			getLang(
				Bukkit.getConsoleSender(),
				plugin.getConfig().getString("default-lang")
		));
	}
}