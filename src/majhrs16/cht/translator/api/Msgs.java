package majhrs16.cht.translator.api;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cht.util.util;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.function.Consumer;
import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public interface Msgs {

	@SuppressWarnings("unchecked")
	default void processOlderMessage(Message formatted, String version) throws ClassNotFoundException {
		Class<?> craftPlayerClass        = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
		Class<?> iChatBaseComponent      = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
		Class<?> chatSerializerClass     = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
		Class<?> entityPlayerClass       = Class.forName("net.minecraft.server." + version + ".EntityPlayer");

		for (String format : formatted.getMessagesFormats().split("\n")) {
			try {
				JSONObject json;

				if (format.startsWith("{") && format.endsWith("}")) {
					json = (JSONObject) new JSONParser().parse(format);

				} else {
					json = new JSONObject();
					json.put("text", format);
				}

				if (formatted.getToolTips() != null) {
					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");
						hoverEvent.put("value", formatted.getToolTips());
					json.put("hoverEvent", hoverEvent);
				}

				if (Config.DEBUG.IF())
					System.out.println(json.toString());

				Object chatComponentText = chatSerializerClass.getMethod("a", String.class).invoke(null, json.toString());

				Method sendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponent);
				sendMessageMethod.invoke(craftPlayerClass.getMethod("getHandle").invoke(craftPlayerClass.cast(formatted.getSender())), chatComponentText);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	default public void processMessage(Message original, Message formatted) {
		if (!new Message().equals(formatted)
			&& !formatted.isCancelled()
			&& formatted.getLangSource() != null
			&& formatted.getLangTarget() != null
			&& !formatted.getLangSource().equals("disabled")
			&& !formatted.getLangTarget().equals("disabled")
			&& formatted.getMessagesFormats() != null
			&& formatted.getMessages() != null) {

			if (formatted.getSender() instanceof Player) {
				Player player = (Player) formatted.getSender();
				double version = util.getMinecraftVersion();

				if (!Permissions.ChatTranslator.Chat.MESSAGES.IF(original))
					return;

				try {
					if (version > 7.9) {
						Class<?> componentSerializerClass = Class.forName("net.md_5.bungee.chat.ComponentSerializer");
						Class<?> baseComponentClass       = Class.forName("net.md_5.bungee.api.chat.BaseComponent");

						for (String format : formatted.getMessagesFormats().split("\n")) {
							JSONObject json;

							if (format.startsWith("{") && format.endsWith("}")) {
								json = (JSONObject) new JSONParser().parse(format);

							} else {
								json = new JSONObject();

								if (version >= 16.0) {
									JSONArray extras = new JSONArray();
										Matcher matcher = Core.color_hex.matcher(format);

										boolean start = true;
										while (matcher.find()) {
											if (start && format.indexOf("#") > 0)  {
												JSONObject extra = new JSONObject();
													extra.put("text", format.substring(0, format.indexOf("#")));
												extras.add(extra);
											}

											JSONObject extra = new JSONObject();
												int index = format.indexOf("#", matcher.end());
												if (index <= 0 || index < matcher.end())
													extra.put("text", format.substring(matcher.end()));

												else
													extra.put("text", format.substring(matcher.end(), index));
												extra.put("color", matcher.group());
											extras.add(extra);

											start = false;
										}

									if (extras.isEmpty()) {
										json.put("text", format);

									} else {
										json.put("text", "");
										json.put("extra", extras);
									}

								} else {
									json.put("text", format);
								}
							}

							if (formatted.getToolTips() != null && Permissions.ChatTranslator.Chat.TOOL_TIPS.IF(original)) {
								if (version >= 16.0) {
									JSONArray extras = new JSONArray();
									Matcher matcher = Core.color_hex.matcher(formatted.getToolTips());

									boolean start = true;
									while (matcher.find()) {
										if (start && formatted.getToolTips().indexOf("#") > 0)  {
											JSONObject extra = new JSONObject();
												extra.put("text", formatted.getToolTips().substring(0, formatted.getToolTips().indexOf("#")));
											extras.add(extra);
										}

										JSONObject extra = new JSONObject();
											int index = formatted.getToolTips().indexOf("#", matcher.end());
											if (index <= 0 || index < matcher.end())
												extra.put("text", formatted.getToolTips().substring(matcher.end()));

											else
												extra.put("text", formatted.getToolTips().substring(matcher.end(), index));
											extra.put("color", matcher.group());
										extras.add(extra);

										start = false;
									}

									if (extras.isEmpty()) {
										JSONObject hoverEvent = new JSONObject();
											hoverEvent.put("action", "show_text");
											hoverEvent.put("value", formatted.getToolTips());
										json.put("hoverEvent", hoverEvent);

									} else {
										JSONObject hoverEvent = new JSONObject();
											hoverEvent.put("action", "show_text");
											JSONObject tool_tips_json = new JSONObject();
												tool_tips_json.put("text", "");
												tool_tips_json.put("extra", extras);
											hoverEvent.put("value", tool_tips_json);
										json.put("hoverEvent", hoverEvent);
									}

								} else {
									JSONObject hoverEvent = new JSONObject();
										hoverEvent.put("action", "show_text");
										hoverEvent.put("value", formatted.getToolTips());
									json.put("hoverEvent", hoverEvent);
								}
							}

							if (Config.DEBUG.IF())
								System.out.println(json.toString());

							Object message = componentSerializerClass.getDeclaredMethod("parse", String.class).invoke(null, json.toString());

							Method sendMessageMethod = player.spigot().getClass().getMethod("sendMessage", Array.newInstance(baseComponentClass, 0).getClass());
							sendMessageMethod.setAccessible(true);

//							player.sendMessage(format); // ES LA UNICA FORMA DE MOSTRAR COLORES #RRGGBB, PERO... Solo puedo usar los msg JSON, lo cual es incompatible >: /
							sendMessageMethod.invoke(player.spigot(), message);
						}

					} else if (version >= 7.5) { // 1.7.5 - 1.7.8 / 1.7.9
						try {
							processOlderMessage(formatted, "v1_7_R2");

						} catch (ClassNotFoundException e) {
							processOlderMessage(formatted, "v1_7_R3");
						}

					} else if (version == 7.2) { // 1.7.2
						processOlderMessage(formatted, "v1_7_R1");

					} else {
						player.sendMessage(formatted.getMessagesFormats());
						if (formatted.getToolTips() != null)
							player.sendMessage("\t" + formatted.getToolTips());
					}

					if (formatted.getSounds() != null && Permissions.ChatTranslator.Chat.SOUNDS.IF(original)) {
						for (String s : formatted.getSounds().split("\n")) {
							String[] parts = s.replace(" ", "").toUpperCase().split(";");

							try {
								Sound sound = Sound.valueOf(parts[0]);
								float volume = Float.parseFloat(parts[1]);
								float pitch = Float.parseFloat(parts[2]);
								player.playSound(player.getLocation(), sound, pitch, volume);

							} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
								Message from = util.getDataConfigDefault();
									from.setMessages("&eSonido &f'&bformats&f.&b" + original.getSound(0) + "&f.&bsounds&f.&b" + parts[0] + "&f' &cinvalido&f.");
								sendMessage(from);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				CommandSender console = Bukkit.getConsoleSender();
				for (String line : formatted.getMessagesFormats().split("\n")) {
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
				System.out.printf("DEBUG from: '%s', '%s', '%s', '%s' -> '%s'\n",
					formatted.getMessagesFormats(),
					formatted.getMessages(),
					formatted.getToolTips(),
					formatted.getLangSource(),
					formatted.getLangTarget()
				);

				System.out.printf("DEBUG to:   '%s', '%s', '%s', '%s' -> '%s'\n",
					formatted.getTo().getMessagesFormats(),
					formatted.getTo().getMessages(),
					formatted.getTo().getToolTips(),
					formatted.getTo().getLangSource(),
					formatted.getTo().getLangTarget()
				);
			}

			processMessage(event, formatted);
			processMessage(event.getTo(), formatted.getTo());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	default public void broadcast(List<Message> messages, Consumer<Message> broadcastAction) {
		for (Message from : messages) {
			try {
				util.assertLang(from.getLangSource());
				util.assertLang(from.getLangTarget());

				if (broadcastAction != null)
					broadcastAction.accept(from);

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
		broadcast(messages, ChatLimiter::add);
	}

	default public void broadcast(Message from, Player[] players, Consumer<List<Message>> broadcastAction) {
		if (from == null || from.equals(new Message()))
			return;

		if (from.getTo() == null || from.getTo().equals(new Message()))
			from.setTo(from.clone());

		Message to_model    = from.getTo();
		List<Message> froms = new ArrayList<>();

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

		if (broadcastAction != null)
			broadcastAction.accept(froms);
	}
}