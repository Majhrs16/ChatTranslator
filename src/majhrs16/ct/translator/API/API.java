package majhrs16.ct.translator.API;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.GoogleTranslator;
import majhrs16.ct.util.util;

import me.clip.placeholderapi.PlaceholderAPI;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class API implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	public GoogleTranslator GT    = new GoogleTranslator();

	public void broadcast(Message from, List<Message> tos) {
		util.assertLang(from.getLang());

		majhrs16.ct.util.ChatLimiter.chat.add(from);

		for (Message to : tos) {
			try {
				util.assertLang(to.getLang());
	
				majhrs16.ct.util.ChatLimiter.chat.add(to);

			} catch (IllegalArgumentException e) {
				String msg = String.format("&cIdioma &f'&b%s&f' no soportado&f.", to.getLang());

				Message alert = util.getDataConfigConsole();
					alert.getFather().setPlayer(Bukkit.getConsoleSender());
					alert.getFather().setMessages(String.format("&b%s&f: %s", to.getPlayerName(), msg));
					alert.getFather().setShow(true);

					alert.setPlayer(to.getPlayer());
					alert.setMessages(msg);
				sendMessage(alert);
			}
		}
	}

	public void broadcast(Message from, Message to_model) {
		Message to;
		List<Message> tos = new ArrayList<Message>();

		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(to_player == from.getPlayer())
				continue;

			to = to_model.clone();
				to.setFather(from);
				to.setPlayer(to_player);
				to.setLang(getLang(to_player));
			tos.add(to);
		}
		
		to = to_model.clone();
			to.setFather(from);
			to.setPlayer(Bukkit.getConsoleSender());
			to.setLang(getLang(Bukkit.getConsoleSender()));
		tos.add(to);
		
		broadcast(from, tos);
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

		if (player instanceof Player) {
			FileConfiguration players = plugin.getPlayers();
			String path = "" + ((Player) player).getUniqueId();
			if (players.contains(path) && players.getString(path).equals("disabled")) {
				return;
			}
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
							Message msg = util.getDataConfigConsole();
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
		Boolean to_show          = to.getShow();

		String lang_target       = to.getLang();

    	Message from = to.getFather() == null ? new Message() : to.getFather();
		CommandSender from_player  = from.getPlayer();
		String from_message_format = from.getMessageFormat();
		String from_messages       = from.getMessages();
		String from_tool_tips      = from.getToolTips();
		String from_sounds         = from.getSounds();
		Boolean from_show          = from.getShow();
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

	public String hexToOctalColors(String text) {
		for (int i = 0; i < 16; i++) {
			text = text.replace("&" + Integer.toHexString(i), "&" + Integer.toOctalString(i));
		}

		return text;
	}

	public String octalToHexColors(String text) {
		for (int i = 0; i < 16; i++) {
			text = text.replace("&" + Integer.toOctalString(i), "&" + Integer.toHexString(i));
		}

		return text;
	}

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
			message_format = Pattern.compile("\\%player_name\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(from_player.getName()); // Parece rebundante, pero es necesario.

		if (to_player != null)
			message_format = Pattern.compile("\\$player_name\\$", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(to_player.getName()); // Parece rebundante, pero es necesario.
		
		if (util.IF(config, "debug")) {
			System.out.println("Debug 01, messages: " + messages);
			System.out.println("Debug 01, messages_original: " + messages_original);
			System.out.println("Debug 01, message_format: " + message_format);
		}

		if (message_format.contains("$ct_messages$")
				// && !(lang_source.equals("off") && lang_target.equals("off"))
				) {
			messages = hexToOctalColors(messages);
			messages = GT.translate(messages, lang_source, lang_target);
			messages = octalToHexColors(messages);
		}

		message_format = Pattern.compile("\\$ct_messages\\$", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll("\\#ct_messages\\#"); // Fix bug %ct_messages% on $ct_messages$

		int count = util.stringCount(message_format, "%ct_expand%");
		message_format = Pattern.compile("\\%ct_messages\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll(messages_original);
		message_format = message_format.replace("#ct_messages#", messages);

		if (util.checkPAPI() && format_message) {
			if (from_player instanceof Player)
				message_format = PlaceholderAPI.setPlaceholders((Player) from_player, message_format);

			if (to_player instanceof Player)
				message_format = PlaceholderAPI.setPlaceholders((Player) to_player, message_format.replace("$", "%"));
		}

		if (util.IF(config, "debug")) {
			System.out.println("Debug 02, messages: " + messages);
			System.out.println("Debug 02, messages_original: " + messages_original);
			System.out.println("Debug 02, message_format: " + message_format);
		}
		
		for(int i = count; i > 0; i--) {
			int padding = (70 - Pattern.compile("\\%ct_expand\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceAll("").length()) / i;

			if (util.IF(config, "debug")) {
				System.out.println("Debug 03, i: " + i);
				System.out.println("Debug 03, padding: " + padding);
			}

			String spaces = String.format("%" + padding + "s", "");
			message_format = Pattern.compile("\\%ct_expand\\%", Pattern.CASE_INSENSITIVE).matcher(message_format).replaceFirst(spaces);
		}

		message_format = ChatColor.translateAlternateColorCodes("&".charAt(0), message_format);

		if (color_personalized)
			messages = ChatColor.translateAlternateColorCodes("&".charAt(0), messages);

		if (util.IF(config, "debug"))
			System.out.println("Debug 04, message_format: " + message_format);

		return message_format;
	}

	public String getLang(CommandSender sender) {
//			Ejemplo: getLang(Bukkit.getConsoleSender()) -> String = "es"
//			Ejemplo: getLang(Alejo09Games) -> String = "en"

		String lang               = null;
		FileConfiguration config  = plugin.getConfig();
		FileConfiguration players = plugin.getPlayers();
		String defaultLang        = config.getString("default-lang");
		String path               = "";

		if (sender instanceof Player) {
			path += ((Player) sender).getUniqueId();
			if (players.contains(path)) {
				lang = players.getString(path);

				if (util.checkPAPI() && lang.equals("auto"))
					lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

			} else if (util.checkPAPI()) {
				lang = PlaceholderAPI.setPlaceholders((Player) sender, "%player_locale_short%");

			} else {
	   			lang = defaultLang;
			}

		} else {
			path += config.getString("server-uuid");
			if (players.contains(path)) {
				lang = players.getString(path);

			} else {
				lang = defaultLang;
			}
		}

		if (!GT.isSupport(lang)) {
			Message msg = util.getDataConfigConsole();
			
			CommandSender console = Bukkit.getConsoleSender();
			msg.setPlayer(console);

			if (GT.isSupport(defaultLang)) {
				msg.setMessages("&eEl idioma &f'&b" + lang + "&f' &cno &eesta soportado&f.");
				msg.setLang(defaultLang);
					sendMessage(msg);

				lang = defaultLang;

			} else {
				msg.setMessages("&cEl idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.");
				msg.setLang("es");
					sendMessage(msg);

				lang = null;
			}
		}

		return lang;
	}
}
