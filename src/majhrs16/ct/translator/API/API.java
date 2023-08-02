package majhrs16.ct.translator.API;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.ChatTranslator;
import majhrs16.lib.utils.Str;
import majhrs16.ct.util.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class API {
	private ChatTranslator plugin = ChatTranslator.plugin;
	public GoogleTranslator GT    = new GoogleTranslator();

	private static Pattern variables    = Pattern.compile("[\\%\\$][a-z0-9_]+[\\%\\$]", Pattern.CASE_INSENSITIVE);
	private static Pattern subVariables = Pattern.compile("\\{([a-z0-9_]+)\\}", Pattern.CASE_INSENSITIVE);
	private static Pattern colorsHex    = Pattern.compile("#[a-f0-9]{6}", Pattern.CASE_INSENSITIVE);

	public String parseSubVarables(Player player, String input) {
//			Procesa sub variables de PAPI.

		Matcher SubVar;
		while ((SubVar = subVariables.matcher(input)).find())
			input = input.replace(SubVar.group(0), PlaceholderAPI.setPlaceholders(player, "%" + SubVar.group(1) + "%"));
		return  PlaceholderAPI.setPlaceholders(player, input);
	}

	public String convertVariablesToLowercase(String input) {
		String output = input;

		Matcher matcher;
		while ((matcher = variables.matcher(input)).find()) {
			input = input.replace(matcher.group(0), "");
			output = output.replace(matcher.group(0), matcher.group(0).toLowerCase());
		}

		return output;
	}

	public String getColor(String text) {
//			Convierte el color RGB y tradicional a un formato visible.

		String version = Bukkit.getVersion();
		if (version.contains("1.16") && 
				version.contains("1.17") && 
				version.contains("1.18") && 
				version.contains("1.19") && 
				version.contains("1.20")) {
			Matcher matcher;
			while ((matcher = colorsHex.matcher(text)).find())
				text = text.replace(matcher.group(0), "" + ChatColor.valueOf(matcher.group(0)));
		}

		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public Message formatMessage(Message DC) {
//			Este formateador basicamente remplaza (sub)vriables PAPI y locales, colorea el chat y/o el formato de este, tambien para los tooltips, y ya por ultimo traduce el mensaje. 

		FileConfiguration config = plugin.getConfig();

		Message to               = DC.clone();
		CommandSender to_player  = to.getPlayer();
		String to_message_format = to.getMessageFormat();
		String to_messages       = to.getMessages();
		String to_tool_tips      = to.getToolTips();
		Boolean to_isCancelled   = to.isCancelled();
		String lang_target       = to.getLang();

		Message from               = to.getFather();
		CommandSender from_player  = from.getPlayer();
		String from_message_format = from.getMessageFormat();
		String from_messages       = from.getMessages();
		String from_tool_tips      = from.getToolTips();
		Boolean from_isCancelled   = from.isCancelled();
		String lang_source         = from.getLang();

		Boolean color = to.getColorPersonalized();
		Boolean papi  = to.getFormatMessage();

		String from_messages_original = from_messages;

		if (!from_isCancelled && from_message_format != null)
			from_message_format = convertVariablesToLowercase(from_message_format);

		if (!to_isCancelled && to_message_format != null)
			to_message_format   = convertVariablesToLowercase(to_message_format);

		if (!from_isCancelled && lang_source != null) {
			if (from_message_format != null) {
				from_message_format = from_message_format.replace("%ct_lang_source%", lang_source);
				from_message_format = from_message_format.replace("$ct_lang_target$", lang_target);
			}

			if (from_tool_tips != null) {
				from_tool_tips = from_tool_tips.replace("%ct_lang_source%", lang_source);
				from_tool_tips = from_tool_tips.replace("$ct_lang_target$", lang_target);
			}
		}

		if (!to_isCancelled && lang_target != null) {
			if (to_message_format != null) {
				to_message_format = to_message_format.replace("%ct_lang_source%", lang_source);
				to_message_format = to_message_format.replace("$ct_lang_target$", lang_target);
			}

			if (to_tool_tips != null) {
				to_tool_tips = to_tool_tips.replace("%ct_lang_source%", lang_source);
				to_tool_tips = to_tool_tips.replace("$ct_lang_target$", lang_target);
			}
		}

		if (!from_isCancelled && from_player != null) { 
			if (from_message_format != null) {
				from_message_format = from_message_format.replace("%player_name%", from_player.getName());
				from_message_format = from_message_format.replace("$player_name$", to_player.getName());
			}

			if (from_tool_tips != null) {
				from_tool_tips = from_tool_tips.replace("%player_name%", from_player.getName());
				from_tool_tips = from_tool_tips.replace("$player_name$", to_player.getName());
			}
		}

		if (!to_isCancelled && to_player != null) {
			if (to_message_format != null) {
				to_message_format = to_message_format.replace("%player_name%", from_player.getName());
				to_message_format = to_message_format.replace("$player_name$", to_player.getName());
			}

			if (to_tool_tips != null) {
				to_tool_tips = to_tool_tips.replace("%player_name%", from_player.getName());
				to_tool_tips = to_tool_tips.replace("$player_name$", to_player.getName());
			}
		}

		if (from_message_format != null)
			from_message_format = from_message_format.replace("$ct_messages$", "#ct_messages#"); // Fix bug %ct_messages% en vez de $ct_messages$

		if (to_message_format != null)
			to_message_format   = to_message_format.replace("$ct_messages$", "#ct_messages#"); // Fix bug %ct_messages% en vez de $ct_messages$

		if (util.checkPAPI() && papi) {
			Player _from_player, _to_player;

			if (from_player instanceof Player)
				_from_player = (Player) from_player;

			else
				_from_player = null;

			if (to_player instanceof Player)
				_to_player = (Player) to_player;

			else
				_to_player = null;

			if (!from_isCancelled) {
				if (from_message_format != null) {
					from_message_format = parseSubVarables(_from_player, from_message_format);
					from_message_format = parseSubVarables(_to_player, from_message_format.replace("$", "%"));
				}

				if (from_tool_tips != null) {
					from_tool_tips = parseSubVarables(_from_player, from_tool_tips);
					from_tool_tips = parseSubVarables(_to_player, from_tool_tips.replace("$", "%"));
				}
			}

			if (!to_isCancelled) {
				if (to_message_format != null) {
					to_message_format = parseSubVarables(_from_player, to_message_format);
					to_message_format = parseSubVarables(_to_player, to_message_format.replace("$", "%"));
				}

				if (to_tool_tips != null) {
					to_tool_tips = parseSubVarables(_from_player, to_tool_tips);
					to_tool_tips = parseSubVarables(_to_player, to_tool_tips.replace("$", "%"));
				}
			}
		}

		if (from_message_format != null)
			from_message_format = from_message_format.replace("#ct_messages#", "$ct_messages$"); // Fix bug %ct_messages% en vez de $ct_messages$

		if (to_message_format != null)
			to_message_format   = to_message_format.replace("#ct_messages#", "$ct_messages$"); // Fix bug %ct_messages% en vez de $ct_messages$

		if (lang_source != null && lang_target != null
				&& !lang_source.equals("off") && !lang_target.equals("off")
				&& !lang_source.equals(lang_target)) {
			if (!from_isCancelled) {
				if (from_messages != null && from_message_format != null && from_message_format.contains("$ct_messages$")) {
					from_messages = GT.translate(from_messages, lang_source, lang_target);
				}

				if (from_tool_tips != null) { // && from_tool_tips.contains("$ct_messages$")
					from_tool_tips = GT.translate(from_tool_tips, lang_source, lang_target);
				}
			}

			if (!to_isCancelled) {
				if (to_messages != null && to_message_format != null && to_message_format.contains("$ct_messages$")) {
					to_messages = GT.translate(to_messages, lang_source, lang_target);
				}

				if (to_tool_tips != null) {
					to_tool_tips = GT.translate(to_tool_tips, lang_source, lang_target);
				}
			}
		}

		if (!from_isCancelled && from_message_format != null)
			from_message_format = getColor(from_message_format);

		if (!from_isCancelled && from_tool_tips != null)
			from_tool_tips = getColor(from_tool_tips);

		if (!to_isCancelled && to_message_format != null)
			to_message_format = getColor(to_message_format);

		if (!to_isCancelled && to_tool_tips != null)
			to_tool_tips = getColor(to_tool_tips);

		if (color) {
			if (!from_isCancelled && from_messages != null && from_player.hasPermission("ChatTranslator.chat.from.color")) {
				from_messages_original = getColor(from_messages_original);
				from_messages          = getColor(from_messages);
			}

			if (!to_isCancelled && to_messages != null && from_player.hasPermission("ChatTranslator.chat.to.color"))
				to_messages   = getColor(to_messages);
		}

		if (!from_isCancelled) {
			if (from_message_format != null) {
				if (from_messages != null)
					from_message_format = from_message_format.replace("%ct_messages%", from_messages_original);

				if (to_messages != null)
					from_message_format = from_message_format.replace("$ct_messages$", to_messages);
			}

			if (from_tool_tips != null) {
				if (from_messages != null)
					from_tool_tips = from_tool_tips.replace("%ct_messages%", from_messages_original);

				if (to_messages != null)
					from_tool_tips = from_tool_tips.replace("$ct_messages$", to_messages);
			}
		}

		if (!to_isCancelled) {
			if (to_message_format != null) {
				if (from_messages != null)
					to_message_format = to_message_format.replace("%ct_messages%", from_messages_original);

				if (to_messages != null)
					to_message_format = to_message_format.replace("$ct_messages$", to_messages);
			}

			if (to_tool_tips != null) {
				if (from_messages != null)
					to_tool_tips = to_tool_tips.replace("%ct_messages%", from_messages_original);

				if (to_messages != null)
					to_tool_tips = to_tool_tips.replace("$ct_messages$", to_messages);
			}
		}

		if (!from_isCancelled && from_message_format != null) {
			int count = Str.count(from_message_format, "%ct_expand%");
			for(int i = count; i > 0; i--) {
				int padding = (70 - ChatColor.stripColor(from_message_format).replace("%ct_expand%", "").length()) / i;

				if (util.IF(config, "debug")) {
					System.out.println("Debug 03, i: " + i);
					System.out.println("Debug 03, padding: " + padding);
				}

				from_message_format = from_message_format.replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		if (!to_isCancelled && to_message_format != null) {
			int count = Str.count(to_message_format, "%ct_expand%");
			for(int i = count; i > 0; i--) {
				int padding = (70 - ChatColor.stripColor(to_message_format).replace("%ct_expand%", "").length()) / i;
	
				if (util.IF(config, "debug")) {
					System.out.println("Debug 03, i: " + i);
					System.out.println("Debug 03, padding: " + padding);
				}
	
				to_message_format = to_message_format.replaceFirst("%ct_expand%", Str.repeat(" ", padding));
			}
		}

		DC.getFather().setMessageFormat(from_message_format);
		DC.getFather().setMessages(from_messages);
		DC.getFather().setToolTips(from_tool_tips);

		DC.setMessageFormat(to_message_format);
		DC.setMessages(to_messages);
		DC.setToolTips(to_tool_tips);

		return DC;
	}

	public void processMessage(Message formatted) {
//			Envia el Message a su destinario. Pero es necesario formatearlo previamente con formatMessage.

		if (!formatted.isCancelled()
					&& formatted.getLang() != null
					&& !formatted.getLang().equals("disabled")
					&& formatted.getMessageFormat()  != null
					&& formatted.getMessages() != null
				) {

			 if (formatted.getPlayer() instanceof Player) {
				TextComponent message = new TextComponent(formatted.getMessageFormat());

				if (formatted.getToolTips() != null) {
					message.setHoverEvent(new HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(formatted.getToolTips()).create()
					));
				}

				Player player = ((Player) formatted.getPlayer());
				player.spigot().sendMessage(message);
			
				if (formatted.getSounds() != null) {
					for (String line : formatted.getSounds().split("\n")) {
						line = line.trim().toUpperCase();

						try {
							Sound sound = Sound.valueOf(line);

							player.playSound(player.getLocation(), sound, 1, 1); // agregar soporte para pitch y volume!!!

						} catch (IllegalArgumentException e) {
							Message msg = util.getDataConfigDefault();
								msg.setPlayer(Bukkit.getConsoleSender());
								msg.setLang(getLang(Bukkit.getConsoleSender()));
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

	public void sendMessage(Message event) {
//			Envia los mensajes especificados en father y el objeto Message actual.

		Message formatted = formatMessage(event.clone());

		if (util.IF(plugin.getConfig(), "debug")) {
			System.out.println("DEBUG: Format, Msgs, ToolTips, Lang");
			System.out.println(String.format("DEBUG from: '%s', '%s', '%s', %s",
					formatted.getFather().getMessageFormat(),
					formatted.getFather().getMessages(),
					formatted.getFather().getToolTips(),
					formatted.getFather().getLang()
			));
	
			System.out.println(String.format("DEBUG to: '%s', '%s', '%s'  %s",
					formatted.getMessageFormat(),
					formatted.getMessages(),
					formatted.getToolTips(),
					formatted.getLang()
			));
		}

		processMessage(formatted.getFather());

		if (util.IF(plugin.getConfig(), "debug")) {
			System.out.println("DEBUG: Format, Msgs, ToolTips, Lang");
			System.out.println(String.format("DEBUG from: '%s', '%s', '%s', %s",
					formatted.getFather().getMessageFormat(),
					formatted.getFather().getMessages(),
					formatted.getFather().getToolTips(),
					formatted.getFather().getLang()
			));

			System.out.println(String.format("DEBUG to: '%s', '%s', '%s'  %s",
					formatted.getMessageFormat(),
					formatted.getMessages(),
					formatted.getToolTips(),
					formatted.getLang()
			));
		}

		processMessage(formatted);
	}

	public void broadcast(List<Message> messages) {
		for (Message to : messages) {
			try {
				util.assertLang(to.getLang()); 
				majhrs16.ct.util.ChatLimiter.chat.add(to);

			} catch (IllegalArgumentException e) {
				String msg = String.format("&cIdioma &f'&b%s&f' no soportado&f.", to.getLang());

				Message alert = util.getDataConfigDefault();
					alert.getFather().setPlayer(Bukkit.getConsoleSender());
					alert.getFather().setMessages(String.format("&b%s&f: %s", to.getPlayerName(), msg));
					alert.getFather().setCancelled(false);

					alert.setPlayer(to.getPlayer());
					alert.setMessages(msg);
				sendMessage(alert);
			}
		}
	}

	public void broadcast(Message to_model) {
		List<Message> tos = new ArrayList<Message>();

		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(to_player == to_model.getFather().getPlayer())
				continue;

			Message to = to_model.clone();
				to.setPlayer(to_player);
				to.setLang(getLang(to_player));
			tos.add(to);
		}

		Message to = to_model.clone();
			to.setPlayer(Bukkit.getConsoleSender());
			to.setLang(getLang(Bukkit.getConsoleSender()));
		tos.add(to);

		broadcast(tos);
	}

	public void setLang(CommandSender sender, String lang) throws IllegalArgumentException {
//		setLang(player, "es");  -> null, Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//		setLang(console, "es"); -> null, Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//		setLang(player/consola, "XD"); -> IllegalArgumentException...

		UUID uuid;
		FileConfiguration config  = plugin.getConfig();
		Message DC = util.getDataConfigDefault();
			DC.setPlayer(Bukkit.getConsoleSender());
			DC.setLang(getLang(Bukkit.getConsoleSender()));

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} else {
			uuid = UUID.fromString(config.getString("server-uuid"));
		}

		lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f' &cno &7esta soportado&f!.");

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

	public String getLang(CommandSender sender) {
	//		Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
	//		Ejemplo: getLang(Alejo09Games) -> String = "en"

		UUID uuid;
		String lang               = null;
		FileConfiguration config  = plugin.getConfig();
		String defaultLang        = config.getString("default-lang");

		CommandSender console = Bukkit.getConsoleSender();
		Message DC            = util.getDataConfigDefault();
			DC.setPlayer(console);

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
					DC.setLang("es");
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
					DC.setLang("es");
						sendMessage(DC);

				} catch (NullPointerException e) {
					;
				}
				break;
		}

		if (lang == null || lang.equals("auto")) {
			if (sender instanceof Player && util.checkPAPI()) {
				lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

			} else {
				lang = defaultLang;
			}
		}

		if (!GT.isSupport(lang)) {
			if (GT.isSupport(defaultLang)) {
				DC.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
				DC.setLang(defaultLang);
					sendMessage(DC);

				lang = defaultLang;

			} else {
				console.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), "&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}
		
		return lang;
	}
}