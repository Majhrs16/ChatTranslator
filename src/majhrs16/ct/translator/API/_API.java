package majhrs16.ct.translator.API;

/*
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import majhrs16.ct.translator.GoogleTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;
import majhrs16.lib.utils.Str;
import me.clip.placeholderapi.PlaceholderAPI;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.regex.Pattern;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
*/

public class _API {
	/*
	private ChatTranslator plugin = ChatTranslator.plugin;
	public GoogleTranslator GT    = new GoogleTranslator();

	public String formatMsg(
			CommandSender from_player,
			CommandSender to_player,
			String message_format,
			String messages,
			String lang_source,
			String lang_target,
			Boolean color_personalized,
			Boolean format_message
		) {

		FileConfiguration config = plugin.getConfig();
		String messages_original = messages;

		if (lang_source != null)
			message_format = Pattern.compile("\\%ct_lang_source\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(lang_source);

		if (lang_target != null)
			message_format = Pattern.compile("\\$ct_lang_target\\$", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(lang_target);

		if (from_player != null)
			message_format = Pattern.compile("\\%player_name\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(from_player.getName()); // Por si no esta instalado PAPI, se formateara igualmente.

		if (to_player != null)
			message_format = Pattern.compile("\\$player_name\\$", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(to_player.getName()); // Por si no esta instalado PAPI, se formateara igualmente.

		if (util.checkPAPI() && format_message) {
			if (from_player instanceof Player)
				message_format = PlaceholderAPI.setPlaceholders((Player) from_player, message_format);

			if (to_player instanceof Player)
				message_format = PlaceholderAPI.setPlaceholders((Player) to_player, message_format.replace("$", "%"));
		}

		message_format = Pattern.compile("\\$ct_messages\\$", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll("\\#ct_messages\\#"); // Fix bug %ct_messages% on $ct_messages$

		if (message_format.contains("#ct_messages#")
				&& new String[]{lang_source, lang_target} != new String[]{null, null}
				&& !lang_source.equals("off")
				&& !lang_target.equals("off")) {
//			messages = hexToOctalColors(messages);
			messages = GT.translate(messages, lang_source, lang_target);
//			messages = octalToHexColors(messages);
		}

		message_format = ChatColor.translateAlternateColorCodes("&".charAt(0), message_format);

		if (color_personalized) {
			messages          = ChatColor.translateAlternateColorCodes("&".charAt(0), messages);
			messages_original = ChatColor.translateAlternateColorCodes("&".charAt(0), messages_original);
		}

		message_format = Pattern.compile("\\%ct_messages\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(messages_original);
		message_format = message_format.replace("#ct_messages#", messages);
		
		int count = util.stringCount(message_format, "%ct_expand%");

		for(int i = count; i > 0; i--) {
			int padding = (70 - Pattern.compile("\\%ct_expand\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll("").length()) / i;

			if (util.IF(config, "debug")) {
				System.out.println("Debug 03, i: " + i);
				System.out.println("Debug 03, padding: " + padding);
			}

			message_format = Pattern.compile("\\%ct_expand\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceFirst(Str.repeat(" ", padding));
		}

		if (util.IF(config, "debug")) {
			System.out.println("Debug 01, messages: " + messages);
			System.out.println("Debug 01, messages_original: " + messages_original);
			System.out.println("Debug 01, message_format: " + message_format);
		}
		
		return message_format;
	}

	public void processMsg(
			Message father,
    		CommandSender player,
    		String message_format,
    		String messages,
    		String tool_tips,
    		String sounds,
    		Boolean show,

    		String lang,

    		Boolean color_personalized,
    		Boolean format_message
		) {

		if (lang != null && lang.equals("disabled")) {
			return;
		}

		if (show) {
			if (message_format != null && messages != null) {
				if (tool_tips != null)
					tool_tips = formatMsg(
						father.getPlayer(),
						player,
						tool_tips,
						messages,
						father.getLang(),
						lang,
						color_personalized,
						format_message
					);

				messages = formatMsg(
					father.getPlayer(),
					player,
					message_format,
					messages,
					father.getLang(),
					lang,
					color_personalized,
					format_message
				);

				if (player instanceof Player) {
					TextComponent message = new TextComponent(messages);

				    if (tool_tips != null) {
						message.setHoverEvent(new HoverEvent(
							HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(tool_tips).create()
						));
				    }

					((Player) player).spigot().sendMessage(message);

				} else {
					CommandSender console = Bukkit.getConsoleSender();
					console.sendMessage(messages);
					if (tool_tips != null) {
						for (String line : tool_tips.split("\n")) {
							console.sendMessage(line);
						}
					}
				}
			}

			if (sounds != null) {
				if (player instanceof Player) {
					for (String line : sounds.split("\n")) {
						line = line.trim().toUpperCase();

						try {
							Sound sound    = Sound.valueOf(line);
							Player player2 = (Player) player;

							player2.playSound(player2.getLocation(), sound, 1, 1);

						} catch (IllegalArgumentException e) {
							Message msg = util.getDataConfigDefault();
								CommandSender console = Bukkit.getConsoleSender();
								msg.setPlayer(console);
								msg.setLang(getLang(console));
								msg.setMessages("&eSonido &f'&bformats&f.&bfrom&f.&bsounds&f.&b" + line + "&f' &cinvalido&f.");
							sendMessage(msg);
						}
					}
				}
			}
		}
	}

	public void sendMessage(Message event) {
		Message to               = event.clone();
		CommandSender to_player  = to.getPlayer();
		String to_message_format = to.getMessageFormat();
		String to_messages       = to.getMessages();
		String to_tool_tips      = to.getToolTips();
		String to_sounds         = to.getSounds();
		Boolean to_show          = to.isCancelled();
		String lang_target       = to.getLang();

		Message from = to.getFather() == null ? new Message() : to.getFather();
		CommandSender from_player  = from.getPlayer();
		String from_message_format = from.getMessageFormat();
		String from_messages       = from.getMessages();
		String from_tool_tips      = from.getToolTips();
		String from_sounds         = from.getSounds();
		Boolean from_show          = from.isCancelled();
		String lang_source         = from.getLang();

		Boolean color_personalized = to.getColorPersonalized();
		Boolean format_message     = to.getFormatMessage();

		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (event.isCancelled())
					return;

				processMsg(
					to,
					from_player,
					from_message_format,
					from_messages,
					from_tool_tips,
					from_sounds,
					from_show,

					lang_source,

					color_personalized,
					format_message
				);

				if (util.IF(plugin.getConfig(), "debug")) {
					System.out.println(              "DEBUG:      Format,	Msgs,	ToolTips	Lang");
					System.out.println(String.format("DEBUG from: '%s',		'%s',	'%s'		%s", from_message_format, from_messages, from_tool_tips, lang_source));
					System.out.println(String.format("DEBUG to:   '%s',		'%s',	'%s'		%s", to_message_format, to_messages, to_tool_tips, lang_target));
				}

				processMsg(
					from,
					to_player,
					to_message_format,
					to_messages,
					to_tool_tips,
					to_sounds,
					to_show,

					lang_target,

					color_personalized,
					format_message
				);
			}
		}, 1L);
	}
	
	public void broadcast(Message from, List<Message> tos) {
		util.assertLang(from.getLang());

		majhrs16.ct.util.ChatLimiter.chat.add(from);

		for (Message to : tos) {
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

	public void broadcast(Message from, Message to_model) {
		List<Message> tos = new ArrayList<Message>();

		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(to_player == from.getPlayer())
				continue;

			Message to = to_model.clone(); // LIMITACION EN EL FORMATEO DEL FROM AL TO...
				to.setPlayer(to_player);
				to.setLang(getLang(to_player));
			tos.add(to);
		}

		Message to = to_model.clone();
			to.setPlayer(Bukkit.getConsoleSender());
			to.setLang(getLang(Bukkit.getConsoleSender()));
		tos.add(to);

		broadcast(from, tos);
	}

	public void setLang(CommandSender sender, String lang) {
//			setLang(player, "es");  -> null, Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.
//			setLang(console, "es"); -> null, Dependiendo del tipo de almacen usado, se guardara en su respectivo lugar.

		UUID uuid;
		FileConfiguration config  = plugin.getConfig();

		Message DC            = util.getDataConfigDefault();
		CommandSender console = Bukkit.getConsoleSender();
		DC.setPlayer(console);

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} else {
			uuid = UUID.fromString(config.getString("server-uuid"));
		}

		try {
			lang = util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f' &cno &7esta soportado&f!.");

		} catch (IllegalArgumentException e) {
			lang = config.getString("default-lang");
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
					// TODO Auto-generated catch block
//					e.printStackTrace();
					DC.setMessages("&cError al escribir en SQLite&f.");
					DC.setLang("es");
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
					// TODO Auto-generated catch block
//					e.printStackTrace();
					DC.setMessages("&cError al escribir en MySQL&f.");
					DC.setLang("es");
						sendMessage(DC);
				}
				break;
		}
	}

	public String getLang(CommandSender sender) {
//			Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
//			Ejemplo: getLang(Alejo09Games) -> String = "en"

		UUID uuid;
		String lang               = null;
		FileConfiguration config  = plugin.getConfig();
		FileConfiguration players = plugin.getPlayers();
		String defaultLang        = config.getString("default-lang");

		Message DC            = util.getDataConfigDefault();
		CommandSender console = Bukkit.getConsoleSender();
		DC.setPlayer(console);

		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();

		} else {
			uuid = UUID.fromString(config.getString("server-uuid"));
		}
		
		switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
			case "yaml":
				if (players.contains(uuid.toString())) {
					lang = players.getString(uuid.toString());
				}
				break;
	
			case "sqlite":
				try {
					lang = plugin.getSQLite().get(uuid);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					DC.setMessages("&cError al leer en SQLite&f.");
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
					// TODO Auto-generated catch block
//					e.printStackTrace();
					DC.setMessages("&cError al leer en MySQL&f.");
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
			DC.setPlayer(console);

			if (GT.isSupport(defaultLang)) {
				DC.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
				DC.setLang(defaultLang);
					// util.processMsgFromDC(DC);

				lang = defaultLang;

			} else {
				console.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), "&4EL IDIOMA POR DEFECTO &f'&b" + defaultLang + "&f' &4NO ESTA SOPORTADO&f!."));

				lang = null;
			}
		}
		
		return lang;
	}
	*/
}