package me.majhrs16.cot;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ExpressionParser;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.MessageEvent;
import me.majhrs16.cht.events.MessageListener;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.majhrs16.lib.minecraft.BukkitUtils;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Arrays;
import java.util.UUID;
import java.util.Map;

public class CoreTranslator extends PlaceholderExpansion {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private final Map<Player, Map<String, String>> map_variables = new HashMap<>();

	public static final String version = "${cot_version}";

	@NotNull public String getAuthor()     { return "Majhrs16"; }
	@NotNull public String getVersion()    { return version; }
	@NotNull public String getIdentifier() { return "cot"; }
	public boolean canRegister()           { return true; }
	public boolean persist()               { return true; }

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		MessageEvent event;
		Message.Builder builder = new Message.Builder();

		identifier = API.parseSubVariables(player, identifier)[0];

		String result = null;

		for (CommandsPAPI command : CommandsPAPI.values()) {
			Matcher matcher = command.getPattern().matcher(identifier);
			if (matcher.find()) {
				switch (command) {
					case SEND_DISCORD:
						result = sendDiscord(player, matcher.group(1), matcher.group(2));
						break;

					case EXPRESSION:
						result = expressionExecutor(
							player,
							matcher.group(1), // UUID
							matcher.group(2)  // Expression
						);
						break;

					case TRANSLATE:
						builder.setSender(player)
							.setLangSource(util.convertStringToLang(matcher.group(1)))
							.setLangTarget(util.convertStringToLang(matcher.group(2)))
							.setMessages(new Formats.Builder().setTexts(matcher.group(3)));
						result = API.formatMessage(builder.build()).getMessages().getFormat(0);
						break;

					case BROADCAST:
						result = broadcast(player, matcher.group(1));
						break;

					case CLONE:
						event = ChatLimiter.get(UUID.fromString(matcher.group(1)));
						if (event == null)
							return getMessageFormatted(player, "&cUUID &b" + matcher.group(1) + "&c no encontrado en el historial reciente&f.");;

						event._setProcessed(true); // Evitar el procesado por ChatLimiter.

						setVariable(
							player,
							matcher.group(2),                  // Key
							result = event.getChat().getUUID().toString() // Value / UUID
						);

						ChatLimiter.add(event.getChat());
						break;

					case SEND:
						result = send(player, matcher.group(1));
						break;

					case LANG:
						CommandSender targetPlayer = BukkitUtils.getSenderByName(matcher.group(1));
						if (targetPlayer == null) {
							result = getMessageFormatted(null, "&4Error&f: &cJugador no encontrado&f.");

						} else {
							result = API.getLang(targetPlayer).getCode();
						}

						break;

					case NEW:
						event = new MessageEvent(new Message.Builder().build());
						event._setProcessed(true); // Evitar el procesado por ChatLimiter.
						ChatLimiter.add(event);
						result = event.getChat().getUUID().toString();
						break;

					case SET:
						setVariable(
							player,
							matcher.group(1),         // Key
							result = matcher.group(2) // Value
						);

						break;

					case GET:
						result = map_variables.getOrDefault(player, new HashMap<>()).get(matcher.group(1));
						break;
				}
			}
		}

		return result;
	}

	public void setVariable(Player player, String key, String value) {
		Map<String, String> variables = map_variables.computeIfAbsent(player, k -> new HashMap<>());

		if (value == null || value.isEmpty() || value.equals("null")) {
			variables.remove(key);

		} else {
			variables.put(key, value);
		}

		if (variables.isEmpty()) {
			map_variables.remove(player);

		} else {
			map_variables.put(player, variables);
		}
	}

	public String expressionExecutor(Player player, String uuid, String expression) {
		Object result;
		MessageEvent event = ChatLimiter.get(UUID.fromString(uuid));

		if (event == null)
			return getMessageFormatted(player, "&cUUID &b" + uuid + "&c no encontrado en el historial reciente&f.");;

		Message.Builder fromBuilder = event.getChat().clone();
		Message.Builder toBuilder = event.getChat().getTo().clone();

		try {
			ExpressionParser parser = new SpelExpressionParser();

			StandardEvaluationContext context = new StandardEvaluationContext();
				context.setVariable("from", fromBuilder);
				context.setVariable("to", toBuilder);

				context.registerFunction("stripColor",
					util.class.getMethod("stripColor", String[].class));

				context.registerFunction("getMinecraftVersion",
					util.class.getMethod("getMinecraftVersion"));

				context.registerFunction("convertStringToLang",
					util.class.getMethod("convertStringToLang", String.class));

				context.registerFunction("getSenderByName",
					BukkitUtils.class.getMethod("getSenderByName", String.class));

			result = parser.parseExpression(expression).getValue(context);

		} catch (Exception e) {
			plugin.logger.error(e.toString());
			return null;
		}

		Message from = fromBuilder.setTo(toBuilder).build();

		try {
			Method method = MessageEvent.class.getDeclaredMethod("setChat", Message.class);
			method.setAccessible(true);
			method.invoke(event, from);

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			plugin.logger.error(e.toString());
			return null;
		}

		if (result instanceof String[])
			result = Arrays.asList((String[]) result);

		return result == null ? "" : result.toString();
	}

	public String sendDiscord(Player player, String uuid, String channels) {
		MessageEvent event = ChatLimiter.get(UUID.fromString(uuid));

		if (event == null)
			return getMessageFormatted(player, "&cUUID &b" + uuid + "&c no encontrado en el historial reciente&f.");;

		Message from = event.getChat();

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (from.getTo().getSender() == null)
			return getMessageFormatted(player, "&cJugador to no encontrado&f.");

		new MessageListener().toDiscord(from, channels.split("\\s*,\\s*"));

		return "ok";
	}

	public String send(Player player, String uuid) {
		MessageEvent event = ChatLimiter.get(UUID.fromString(uuid));

		if (event == null)
			return getMessageFormatted(player, "&cUUID &b" + uuid + "&c no encontrado en el historial reciente&f.");;

		Message from = event.getChat();

		if (from.getSender() == null)
			return getMessageFormatted(player, "&cJugador from no encontrado&f.");

		if (from.getTo().getSender() == null)
			return getMessageFormatted(player, "&cJugador to no encontrado&f.");

		API.sendMessage(from);
		return "ok";
	}

	public String broadcast(Player player, String uuid) {
		MessageEvent event = ChatLimiter.get(UUID.fromString(uuid));

		if (event == null)
			return getMessageFormatted(player, "&cUUID &b" + uuid + "&c no encontrado en el historial reciente&f.");;

		Message builder = event.getChat();

		if (builder.getSender() == null)
			return getMessageFormatted(player, "&4Error&f: &cJugador no encontrado&f.");

		MessageListener listener = new MessageListener();
		API.broadcast(builder.clone(), BukkitUtils.getOnlinePlayers(), _from -> {
			listener.toMinecraft(_from);
			event.setCancelled(true);
		});

		return "ok";
	}

///////////////////////////////////////////////////////////////
// UTILS!

	public String getMessageFormatted(Player player, String text) {
		return API.formatMessage(new Message.Builder()
			.setSender(player)
			.setLangSource(util.convertStringToLang("es"))
			.setLangTarget(API.getLang(player))
			.setMessages(new Formats.Builder()
				.setTexts(text)
			).setColor(1)
			.build()

		).getMessages().getFormat(0);
	}
}