package majhrs16.cot;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.util.util;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public class CoreTranslator extends PlaceholderExpansion {
	private API API = new API();

	private Pattern sendMessage = Pattern.compile("sendMessage_\\[(\\[.+\\]),? *(\\[.+\\])?\\]", Pattern.CASE_INSENSITIVE);
	private Pattern broadcast   = Pattern.compile("broadcast_\\[(\\[.+\\]), *(\\[.+\\]), *(\\[.+\\])\\]", Pattern.CASE_INSENSITIVE);
	private Pattern translate   = Pattern.compile("translate; *(.+); *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private Pattern parser      = Pattern.compile("papiParse; *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private Pattern lang        = Pattern.compile("getLang_(.+)", Pattern.CASE_INSENSITIVE);

	public String getAuthor()     { return "Majhrs16"; }
	public String getVersion()    { return "b1.3.2"; }
	public String getIdentifier() { return "ct"; }

//	"\\[['\"]?.+['\"]?, *['\"].+['\"], *['\"].+['\"], *['\"].+['\"], *['\"].+['\"], *[true|false], *['\"]?.+['\"]?, *[true|false], *[true|false]\\]";

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (!identifier.contains("{") && identifier.contains("}")) // bug fix CE, add innecesary }.
			identifier = identifier.replace("}", "");

		Matcher Parser = parser.matcher(identifier);

		identifier = API.parseSubVarables(player, identifier);

		Matcher Translate   = translate.matcher(identifier);
		Matcher Broadcast   = broadcast.matcher(identifier);
		Matcher SendMessage = sendMessage.matcher(identifier);
		Matcher Lang        = lang.matcher(identifier);
		String result = null;

		if (Broadcast.find()) { // %ct_broadcast_[["Maj", "from", "Hola mundo", "from", "from", false, "es", true, true], ["", "to", "Que tal?", "to", "to", false, "", true, true], ["", "console", "Que tal?", "console", "console", false, "", true, true]]%
			result = broadcast(player, Broadcast.group(1), Broadcast.group(2), Broadcast.group(3));

		} else if (SendMessage.find()) { // %ct_sendMessage_[["Maj", "from", "Hola mundo", "from", "from", false, "es", true, true], ["Maj", "to", "Que tal?", "to", "to", false, "es", true, true]]%
			result = sendMessage(player, SendMessage.group(1), SendMessage.group(2));

		} else if (Lang.find()) { // %ct_getLang_{player_name}%
			player = Bukkit.getServer().getPlayer(Lang.group(1));
			if (player == null)
				result = getMessageFormatted(player, "&cJugador no encontrado&f.");

			else
				result = API.getLang(player);

		} else if (Translate.find()) { // %ct_translate; en; es; This server is a multi_language%
			result = getMessageFormatted(player, Translate.group(1), Translate.group(2), Translate.group(3));

		} else if (Parser.find()) { // %ct_parse; player_name; player_world%
			result = parseOtherPlayer(Parser.group(1), Parser.group(2));

		} else if (identifier.equals("test")) {
			result = "Debug, player = " + player.getName();
		}

		return result;
	}

	private String broadcast(Player player, String from_json, String to_json, String term_json) {
		List<Message> tos = new ArrayList<Message>();
		Message from = new Message().valueOf(from_json);

		if (from == null)
			return getMessageFormatted(player, "&cJugador no encontrado&f.");

		Message console = new Message().valueOf(term_json);
			console.setSender(Bukkit.getConsoleSender());
			console.setLang(API.getLang(Bukkit.getConsoleSender()));
		from.setTo(console);
		tos.add(from.clone());

		Message to_model = new Message().valueOf(to_json);
		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(to_player == from.getSender())
				continue;

			from.setTo(to_model.clone());
				from.getTo().setSender(to_player);
				from.getTo().setLang(API.getLang(to_player));
			tos.add(from.clone());
		}

		for (Message to : tos) {
			try {
				util.assertLang(to.getLang());
				API.sendMessage(to);
				to.setCancelled(true);

			} catch (IllegalArgumentException e) {
				String msg = String.format("&cIdioma &f'&b%s&f' no soportado&f.", to.getLang());

				Message alert = util.getDataConfigDefault();
					alert.setSender(Bukkit.getConsoleSender());
					alert.setMessages(String.format("&b%s&f: %s", to.getSenderName(), msg));
					alert.setCancelled(false);

					alert.getTo().setSender(to.getSender());
					alert.getTo().setMessages(msg);
				API.sendMessage(alert);
			}
		}

		return "ok";
	}
	public String getMessageFormatted(Player player, String lang_source, String lang_target, String text) {
		Message from = util.getDataConfigDefault();
			from.setLang(lang_source);

			from.getTo().setSender(player);
			from.getTo().setLang(lang_target);
			from.getTo().setMessages(text);
		return API.formatMessage(from).getMessageFormat();
	}

	public String getMessageFormatted(Player player, String text) {
		return getMessageFormatted(player, "es", API.getLang(player), text);
	}

	public Player getSender(String player_name) {
		Player player;
		try {
			player = Bukkit.getServer().getPlayer(player_name);

		} catch (NullPointerException e) {
			player = null;
		}

		return player;
	}

	public String sendMessage(Player player, String from_json, String to_json) {
		Message from = new Message().valueOf(from_json);
		if (from == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (to_json != null) {
			Message to = new Message().valueOf(to_json);

			if (to == null)
				return getMessageFormatted(player, "&cJugador to no encontrado&f.");

			from.setTo(to);
		}

		API.sendMessage(from);
		return "ok";
	}

	public String parseOtherPlayer(String player_name, String placeholder) {
		Player player = getSender(player_name);
		if (player == null)
			return null;

		return PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
	}
}