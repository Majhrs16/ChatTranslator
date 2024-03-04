package majhrs16.cot;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.MessageListener;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.ChatLimiter;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.UUID;

public class CoreTranslator extends PlaceholderExpansion {
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public static final String version = "b1.6";

	public boolean persist()	  { return true; }
	public boolean canRegister()  { return true; }

	@NotNull public String getAuthor()	   { return "Majhrs16"; }
	@NotNull public String getVersion()	   { return version; }
	@NotNull public String getIdentifier() { return "cot"; }

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (!identifier.contains("{") && identifier.contains("}")) // bug CE, agrega un innecesario: }
			identifier = identifier.replace("}", "");

		identifier = API.parseSubVariables(player, identifier)[0];

		String result = null;

		for (Commands command : Commands.values()) {
			Matcher matcher = command.getPattern().matcher(identifier);
			if (matcher.find()) {
				switch (command) {
					case SEND_DISCORD:
						result = sendDiscord(player, matcher.group(1), matcher.group(2));
						break;

					case TRANSLATE:
						result = API.getTranslator().translate(matcher.group(1), matcher.group(2), matcher.group(3));
						break;

					case BROADCAST:
						result = broadcast(player, matcher.group(1));
						break;

					case SEND:
						result = send(player, matcher.group(1));
						break;

					case LANG:
						CommandSender targetPlayer = util.getSenderByName(matcher.group(1));
						if (targetPlayer == null)
							result = getMessageFormatted(null, "&4Error&f: &cJugador no encontrado&f.");

						else
							result = API.getLang(targetPlayer);
						break;

					case VAR:
						result = property(
							matcher.group(1), // UUID
							matcher.group(3)  // Path
						);
						break;

					case NEW:
						Message from = new Message();
						ChatLimiter.add(from);
						result = from.getUUID().toString();
						break;
				}
			}
		}
	
		return result;
	}

	public String property(String uuid, String path) {
		Message from = ChatLimiter.get(UUID.fromString(uuid));

		if (from == null)
			return null;

		Object result = from.property(path);

		if (result instanceof String[])
			result = Arrays.asList((String[]) result); // List.asArray....

		return result.toString();
	}

	public String sendDiscord(Player player, String uuid, String channels) {
		Message from = ChatLimiter.get(UUID.fromString(uuid));

		if (from == null)
			return "null";

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (from.getTo().getSender() == null)
			return getMessageFormatted(player, "&cJugador to no encontrado&f.");

		new MessageListener().toDiscord(from, Arrays.asList(channels.split("\\s*,\\s*")));
		from.setCancelled(true);

		return "ok";
	}

	public String send(Player player, String uuid) {
		Message from = ChatLimiter.get(UUID.fromString(uuid));

		if (from == null)
			return "null";

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (from.getTo().getSender() == null)
			return getMessageFormatted(player, "&cJugador to no encontrado&f.");

		API.sendMessage(from);
		return "ok";
	}

	public String broadcast(Player player, String uuid) {
		Message from = ChatLimiter.get(UUID.fromString(uuid));

		if (from == null)
			return "null";

		if (from.getSender() == null)
			return getMessageFormatted(player, "&4Error&f: &cJugador no encontrado&f.");

		MessageListener listener = new MessageListener();
		API.broadcast(from, util.getOnlinePlayers(), froms -> API.broadcast(froms, _from -> {
			listener.toMinecraft(_from);
			_from.setCancelled(true);
		}));

		return "ok";
	}

	////////////////
	// UTILS!

	public String getMessageFormatted(Player player, String lang_source, String lang_target, String text) {
		Message from = new Message();
			from.setSender(player);
			from.setLangSource(lang_source);
			from.setLangTarget(lang_target);
			from.getMessages().setTexts(text);

		return API.formatMessage(from).getMessages().getTexts()[0];
	}

	public String getMessageFormatted(Player player, String text) {
		return getMessageFormatted(player, "es", API.getLang(player), text);
	}
}