package me.majhrs16.cot;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ExpressionParser;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.MessageListener;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.UUID;

public class CoreTranslator extends PlaceholderExpansion {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public static final String version = "${cot_version}";

	@NotNull public String getAuthor()     { return "Majhrs16"; }
	@NotNull public String getVersion()    { return version; }
	@NotNull public String getIdentifier() { return "cot"; }
	public boolean canRegister()           { return true; }
	public boolean persist()               { return true; }

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		identifier = API.parseSubVariables(player, identifier)[0];

		String result = null;

		for (CommandsPAPI command : CommandsPAPI.values()) {
			Matcher matcher = command.getPattern().matcher(identifier);
			if (matcher.find()) {
				switch (command) {
					case SEND_DISCORD:
						result = sendDiscord(player, matcher.group(1), matcher.group(2));
						break;

					case TRANSLATE:
						Message from = new Message();
							from.setSender(player);
							from.setLangSource(matcher.group(1));
							from.setLangTarget(matcher.group(2));
							from.getMessages().setTexts(matcher.group(3));
						result = API.formatMessage(from).getMessages().getFormat(0);
						break;

					case BROADCAST:
						result = broadcast(player, matcher.group(1));
						break;

					case SEND:
						result = send(player, matcher.group(1));
						break;

					case LANG:
						CommandSender targetPlayer = BukkitUtils.getSenderByName(matcher.group(1));
						if (targetPlayer == null)
							result = getMessageFormatted(null, "&4Error&f: &cJugador no encontrado&f.");

						else
							result = API.getLang(targetPlayer).getCode();
						break;

					case VAR:
						result = expressionExecutor(
							matcher.group(1), // UUID
							matcher.group(2)  // Path
						);
						break;

					case NEW:
						Message from_new = new Message();
						ChatLimiter.add(from_new);
						result = from_new.getUUID().toString();
						break;
				}
			}
		}

		return result;
	}

	public String expressionExecutor(String uuid, String expression) {
		Object result;
		Message from = ChatLimiter.get(UUID.fromString(uuid));

		if (from == null)
			return null;

		try {
			ExpressionParser parser = new SpelExpressionParser();

			StandardEvaluationContext context = new StandardEvaluationContext();
				context.setVariable("from", from);
				context.setVariable("to", from.getTo());

			result = parser.parseExpression(expression).getValue(context);

		} catch (Exception e) {
			plugin.logger.error(e.toString());
			return null;
		}

		if (result instanceof String[])
			result = Arrays.asList((String[]) result); // List.asArray....

		return result == null ? "" : result.toString();
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
		API.broadcast(from, BukkitUtils.getOnlinePlayers(), froms -> API.broadcast(froms, _from -> {
			listener.toMinecraft(_from);
			_from.setCancelled(true);
		}));

		return "ok";
	}

	////////////////
	// UTILS!

	public String getMessageFormatted(
			Player player,
			TranslatorBase.LanguagesBase lang_source,
			TranslatorBase.LanguagesBase lang_target,
			String text) {

		Message from = new Message();
			from.setSender(player);
			from.setLangSource(lang_source);
			from.setLangTarget(lang_target);
			from.getMessages().setTexts(text);
			from.setColor(1);

		return API.formatMessage(from).getMessages().getFormat(0);
	}

	public String getMessageFormatted(Player player, String text) {
		return getMessageFormatted(player, util.convertStringToLang("es"), API.getLang(player), text);
	}
}
