package majhrs16.cot;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CoreTranslator extends PlaceholderExpansion {
	private final Pattern sendMessage          = Pattern.compile("sendMessage; *\\[(\\[.+\\]), *(\\[.+\\])?\\]", Pattern.CASE_INSENSITIVE);
	private final Pattern broadcast            = Pattern.compile("broadcast; *\\[(\\[.+\\]), *(\\[.+\\])\\]", Pattern.CASE_INSENSITIVE);
	private final Pattern sendMessageToDiscord = Pattern.compile("sendMessageToDiscord; (\\[.+\\])", Pattern.CASE_INSENSITIVE);
	private final Pattern translate            = Pattern.compile("translate; *(.+); *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private final Pattern parser               = Pattern.compile("papiParse; *(.+); *(.+)", Pattern.CASE_INSENSITIVE);
	private final Pattern lang                 = Pattern.compile("getLang_(.+)", Pattern.CASE_INSENSITIVE);

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean persist()      { return true; }
	public boolean canRegister()  { return true; }

	public String getAuthor()     { return "Majhrs16"; }
	public String getVersion()    { return "b1.4"; }
	public String getIdentifier() { return "cot"; }

//	"\\[['\"]?.+['\"]?, *['\"].+['\"], *['\"].+['\"], *['\"].+['\"], *['\"].+['\"], *[true|false], *['\"]?.+['\"]?, *[true|false], *[true|false]\\]";

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
			result = sendMessageToDiscord(player, matcher.group(1));

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

	public String sendMessageToDiscord(Player player, String from_json) {
		Message from = new Message().valueOf(from_json);

		if (from.getSender() == null)
			return getMessageFormatted(player, "&4Error&f: &cJugador no encontrado&f.");

		from = API.formatMessage(from);

		for (String channelID : plugin.config.get().getStringList("discord.channels")) {
			TextChannel channel = DiscordTranslator.getJda().getTextChannelById(channelID);

			if (channel == null)
				continue;

			channel.sendMessage(ChatColor.stripColor(from.getMessageFormat())).queue();

			if (from.getToolTips() != null)
				channel.sendMessage(ChatColor.stripColor(from.getToolTips())).queue();
		}

		return "ok";
	}

	private String broadcast(Player player, String from_json, String to_json) {
		Message from = new Message().valueOf(from_json);

		if (from.getSender() == null)
			return getMessageFormatted(player, "&4Error&f: &cJugador no encontrado&f.");

		from.setTo(new Message().valueOf(to_json));

		API.broadcast(from, froms -> {
			API.broadcast(froms, _from -> { // Controlar cada from.
				API.sendMessage(_from);
				_from.setCancelled(true); // Cancelar from y to alavez.
			});

			froms.clear(); // Evitar lanzar el broadcast por defecto.
		});

		return "ok";
	}

	public String getMessageFormatted(Player player, String lang_source, String lang_target, String text) {
		Message from = util._getDataConfigDefault();
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
		Message from = new Message().valueOf(from_json);
		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (to_json != null) {
			Message to = new Message().valueOf(to_json);

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