package majhrs16.cht.translator.api;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.util;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.function.Consumer;
import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface Msgs {
	@SuppressWarnings("unchecked")
	default void processOlderTooltipMessage(Message formatted, String version) throws ClassNotFoundException {
		Object chatComponentText;
		
		Class<?> craftPlayerClass        = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
		Class<?> iChatBaseComponent      = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
		Class<?> chatSerializerClass     = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
		Class<?> entityPlayerClass       = Class.forName("net.minecraft.server." + version + ".EntityPlayer");

		for (String format : formatted.getMessageFormat().split("\n")) {
			try {
				JSONObject json = (JSONObject) new JSONParser().parse(format.startsWith("{") && format.endsWith("}") ? format : "{\"text\": \"" + format + "\"}");

				if (formatted.getToolTips() != null) {
					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");
						hoverEvent.put("value", formatted.getToolTips());
					json.put("hoverEvent", hoverEvent);
				}

				chatComponentText = chatSerializerClass.getMethod("a", String.class).invoke(null, json.toString());

				Method sendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponent);
				sendMessageMethod.invoke(craftPlayerClass.getMethod("getHandle").invoke(craftPlayerClass.cast(formatted.getSender())), chatComponentText);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	default public void processMessage(Message formatted) {
		if (!new Message().equals(formatted)
			&& !formatted.isCancelled()
			&& formatted.getLangSource() != null
			&& formatted.getLangTarget() != null
			&& !formatted.getLangSource().equals("disabled")
			&& !formatted.getLangTarget().equals("disabled")
			&& formatted.getMessageFormat() != null
			&& formatted.getMessages() != null) {

			if (formatted.getSender() instanceof Player) {
				Player player = (Player) formatted.getSender();
				Double version = util.getMinecraftVersion();

				try {
					if (version > 7.9) { // LIMITACION DE util.getMinecraftVersion, RETORNA MAL EL #, EJEMPLO REAL: 7.2 >= 7.0 = true (version = 7.10) // LIMITACION LOCAL, NO ES POSIBLE DIVIDIR LOS MF, ES NECESARIO HACER UN FOREACH EN LOS MFs.
						Object message;

						Class<?> hoverEventActionClass    = Class.forName("net.md_5.bungee.api.chat.HoverEvent$Action");
						Class<?> componentBuilderClass    = Class.forName("net.md_5.bungee.api.chat.ComponentBuilder");
						Class<?> baseComponentArrayClass  = Class.forName("[Lnet.md_5.bungee.api.chat.BaseComponent;");
						Class<?> componentSerializerClass = Class.forName("net.md_5.bungee.chat.ComponentSerializer");
						Class<?> baseComponentClass       = Class.forName("net.md_5.bungee.api.chat.BaseComponent");
						Class<?> textComponentClass       = Class.forName("net.md_5.bungee.api.chat.TextComponent");
						Class<?> hoverEventClass          = Class.forName("net.md_5.bungee.api.chat.HoverEvent");

						for (String format : formatted.getMessageFormat().split("\n")) {
							if (format.startsWith("{") && format.endsWith("}")) {
								Method parseMethod = componentSerializerClass.getDeclaredMethod("parse", String.class);
								message = parseMethod.invoke(null, format);

							} else {
								Object componentBuilder = componentBuilderClass.getConstructor(String.class).newInstance(format);
								Method createMethod = componentBuilderClass.getMethod("create");
								message = (Object[]) createMethod.invoke(componentBuilder);
							}

							if (formatted.getToolTips() != null) {
								if (version < 16.0) {
									@SuppressWarnings({ "unchecked", "rawtypes" })
									Object hoverEvent = hoverEventClass
										.getDeclaredConstructor(hoverEventActionClass, baseComponentArrayClass)
										.newInstance(
											Enum.valueOf((Class<Enum>) hoverEventActionClass, "SHOW_TEXT"),
											(Object[]) componentBuilderClass.getMethod("create")
												.invoke(componentBuilderClass.getConstructor(String.class)
												.newInstance(formatted.getToolTips()))
										);

									textComponentClass.getMethod("setHoverEvent", hoverEventClass).invoke(((Object[]) message)[0], hoverEvent);

								} else {
									// Crea un componente Text para las tooltips en versiones 1.16+
									Object tooltipComponent = textComponentClass.getConstructor(String.class).newInstance(formatted.getToolTips());

									// Crea un evento HoverEvent para mostrar el componente Text
									@SuppressWarnings({ "unchecked", "rawtypes" })
									Object hoverEvent = hoverEventClass.getConstructor(hoverEventActionClass, baseComponentClass)
										.newInstance(Enum.valueOf((Class<Enum>) hoverEventActionClass, "SHOW_TEXT"), tooltipComponent);

									// Agregar el HoverEvent al mensaje principal
									textComponentClass.getMethod("setHoverEvent", hoverEventClass).invoke(message, hoverEvent);
								}
							}

							Method sendMessageMethod = player.spigot().getClass().getMethod("sendMessage", Array.newInstance(baseComponentClass, 0).getClass());
							sendMessageMethod.setAccessible(true);
							sendMessageMethod.invoke(player.spigot(), message);
						}

					} else if (version >= 7.5 && version <= 7.9) { // 1.7.5 - 1.7.8 / 1.7.9
						try {
							processOlderTooltipMessage(formatted, "v1_7_R2");

						} catch (ClassNotFoundException e) {
							processOlderTooltipMessage(formatted, "v1_7_R3");
						}

					} else if (version == 7.2) { // 1.7.2
						processOlderTooltipMessage(formatted, "v1_7_R1");

					} else {
						player.sendMessage(formatted.getMessageFormat());
						if (formatted.getToolTips() != null)
							player.sendMessage("	" + formatted.getToolTips());
					}

					if (formatted.getSounds() != null) {
						for (String line : formatted.getSounds().split("\n")) {
							String[] parts = line.replace(" ", "").toUpperCase().split(";");
							try {
								Sound sound = Sound.valueOf(parts[0]);
								Float volume = Float.parseFloat(parts[1]);
								Float pitch = Float.parseFloat(parts[2]);
								player.playSound(player.getLocation(), sound, pitch, volume);

							} catch (IllegalArgumentException e) {
								Message from = util.getDataConfigDefault();

								from.setMessages("&eSonido &f'&bformats&f.&bfrom&f.&bsounds&f.&b" + parts[0] + "&f' &cinvalido&f.");
									sendMessage(from);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				CommandSender console = Bukkit.getConsoleSender();
				for (String line : formatted.getMessageFormat().split("\n")) {
					console.sendMessage(line);
				}

				if (formatted.getToolTips() != null) {
					for (String line : formatted.getToolTips().split("\n")) {
						console.sendMessage(line);
					}
				}
			}
		}
	}

	default public void sendMessage(Message event) {
//			Envia los mensajes especificados en el from(Objeto Message actual) y to.

		if (event == new Message())
			return;

		try {
			Message formatted = ChatTranslatorAPI.getInstance().formatMessage(event);

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

	default public void broadcast(List<Message> messages, Consumer<Message> preBroadcastAction) {
		for (Message from : messages) {
			try {
				util.assertLang(from.getLangSource());
				util.assertLang(from.getLangTarget());

				if (preBroadcastAction != null)
					preBroadcastAction.accept(from);

			} catch (IllegalArgumentException e) {
				Message alert = util.getDataConfigDefault();
					alert.setMessages(String.format("&b%s&f: &cIdioma &f'&b%s&f' &cy&f/&co &f'&b%s&f' &cno soportado&f.",
						from.getSenderName(),
						from.getLangSource(),
						from.getLangTarget()
					));
				sendMessage(alert);
			}
		}
	}

	default public void broadcast(List<Message> messages) {
		broadcast(messages, from -> majhrs16.cht.util.ChatLimiter.chat.add(from));
	}

	default  public void broadcast(Message from, Consumer<List<Message>> preBroadcastAction) {
		if (from == null || from.equals(new Message()))
			return;

		if (from.getTo() == null || from.getTo().equals(new Message()))
			from.setTo(from.clone());

		Message to_model    = from.getTo();
		List<Message> froms = new ArrayList<Message>();
		Player[] players    = util.getOnlinePlayers();

		for (Player to_player : players) {
			if (from.getSender() == to_player) {
				if (players.length > 1)
					continue;

				to_model.setSender(from.getSender());
				to_model.setLangTarget(from.getLangTarget());
				to_model.setCancelledThis(true);

			} else {
				to_model.setSender(to_player);
				to_model.setLangTarget(ChatTranslatorAPI.getInstance().getLang(to_player));
				to_model.setCancelledThis(false);
			}

			froms.add(from.clone());
			from.setCancelledThis(true);
		}

		if (preBroadcastAction != null)
			preBroadcastAction.accept(froms);

		broadcast(froms);
	}

	default public void broadcast(Message to_model) {
		 broadcast(to_model, null);
	 }
}