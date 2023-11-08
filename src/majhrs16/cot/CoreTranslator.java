package majhrs16.cot;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.MessageListener;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.util;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CoreTranslator extends PlaceholderExpansion {
	private final Pattern sendMessageToDiscord = Pattern.compile("sendMessageToDiscord; *\\[(\\[.+]), *(\\[.+])]", Pattern.CASE_INSENSITIVE);
	private final Pattern sendMessage          = Pattern.compile("sendMessage; *\\[(\\[.+]), *(\\[.+])?]", Pattern.CASE_INSENSITIVE);
	private final Pattern broadcast            = Pattern.compile("broadcast; *\\[(\\[.+]), *(\\[.+])]", Pattern.CASE_INSENSITIVE);
	private final Pattern translate            = Pattern.compile("translate; *(.+); *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private final Pattern parser               = Pattern.compile("papiParse; *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private final Pattern lang                 = Pattern.compile("getLang_(.+)", Pattern.CASE_INSENSITIVE);

	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean persist()      { return true; }
	public boolean canRegister()  { return true; }

	@NotNull public String getAuthor()     { return "Majhrs16"; }
	@NotNull public String getVersion()    { return "v1.4"; }
	@NotNull public String getIdentifier() { return "cot"; }

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (!identifier.contains("{") && identifier.contains("}")) // bug fix CE, add innecesary }.
			identifier = identifier.replace("}", "");

		Matcher matcher = parser.matcher(identifier);

		identifier = API.parseSubVarables(player, identifier);

		String result = null;

		if (matcher.find()) { // %ct_parse; player_name; player_world%
			result = parseOtherPlayer(matcher.group(1), matcher.group(2));

		} else if ((matcher = broadcast.matcher(identifier)).find()) { // %ct_broadcast; [["Maj", "from", "Hola mundo", "from", "from", false, "es", true, true], ["", "to", "Que tal?", "to", "to", false, "", true, true], ["", "console", "Que tal?", "console", "console", false, "", true, true]]% // falta actualizar
			result = broadcast(player, matcher.group(1), matcher.group(2));

		} else if ((matcher = sendMessageToDiscord.matcher(identifier)).find()) { // %ct_sendMessageToDiscord; ["Maj", "from", "Hola mundo", "from", "from", false, "es", "es", true, true]%
			result = sendMessageToDiscord(player, matcher.group(1), matcher.group(2));

		} else if ((matcher = sendMessage.matcher(identifier)).find()) { // %ct_sendMessage; [["Maj", "from", "Hola mundo", "from", "from", false, "es", "es", true, true], ["Maj", "to", "Que tal?", "to", "to", false, "es", "en", true, true]]%
			result = sendMessage(player, matcher.group(1), matcher.group(2));

		} else if ((matcher = lang.matcher(identifier)).find()) { // %ct_getLang_{player_name}%
			player = Bukkit.getServer().getPlayer(matcher.group(1));
			if (player == null)
				result = getMessageFormatted(null, "&4Error&f: &cJugador no encontrado&f.");

			else
				result = API.getLang(player);

		} else if ((matcher = translate.matcher(identifier)).find()) { // %ct_translate; en; es; This server is a multi_language%
			result = getMessageFormatted(player, matcher.group(1), matcher.group(2), matcher.group(3));

		} else if (identifier.equals("test")) {
			result = "Debug, player = " + player.getName();
		}

		return result;
	}

	public String sendMessageToDiscord(Player player, String from_json, String to_json) {
		Message from = Message.valueOf(from_json);
		assert from != null;

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (to_json != null) {
			Message to = Message.valueOf(to_json);
			assert to != null;

			if (to.getSender() == null)
				return getMessageFormatted(player, "&cJugador to no encontrado&f.");

			from.setTo(to);
		}

		new MessageListener().toDiscord(from);
		from.setCancelled(true);

		return "ok";
	}

	private String broadcast(Player player, String from_json, String to_json) {
		Message from = Message.valueOf(from_json);
		assert from != null;

		if (from.getSender() == null)
			return getMessageFormatted(player, "&4Error&f: &cJugador no encontrado&f.");

		from.setTo(Message.valueOf(to_json));

		MessageListener listener = new MessageListener();
		API.broadcast(from, util.getOnlinePlayers(), froms -> API.broadcast(froms, _from -> {
			listener.toMinecraft(_from);
			_from.setCancelled(true);
		}));

		return "ok";
	}

	public String getMessageFormatted(Player player, String lang_source, String lang_target, String text) {
		Message from = new Message();
			from.setSender(player);
			from.setLangSource(lang_source);
			from.setLangTarget(lang_target);
			from.setMessages(text);
		return API.formatMessage(from).getMessages();
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
		Message from = Message.valueOf(from_json);
		assert from != null;

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (to_json != null) {
			Message to = Message.valueOf(to_json);

			assert to != null;
			if (to.getSender() == null)
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