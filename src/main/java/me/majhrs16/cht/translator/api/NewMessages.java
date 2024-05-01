package me.majhrs16.cht.translator.api;

import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.JsonFormatter;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import me.majhrs16.lib.logger.Logger;

import org.bukkit.entity.Player;

import java.util.regex.Matcher;

class NewMessages {
	private static final double version = util.getMinecraftVersion();
	private static final Logger logger = ChatTranslator.getInstance().logger;

	@SuppressWarnings("unchecked")
	static void J7_J20(Message original, Message formatted) throws ParseException {
		Player player = (Player) formatted.getSender();

		for (String format : formatted.getMessages().getFormats()) {
			JSONObject json;

			if (format.startsWith("{") && format.endsWith("}")) {
				json = (JSONObject) new JSONParser().parse(format);

			} else if (format.startsWith("[!] {") && format.endsWith("}")) {
				json = (JSONObject) new JSONParser().parse(format.substring(4));

			} else {
				json = new JSONObject();

				if (version >= 16.0) {
					JSONArray extras = new JSONArray();
					Matcher matcher = Core.COLOR_HEX.matcher(format);

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

			if (formatted.getToolTips().getFormats().length > 0 && Permissions.ChatTranslator.Chat.TOOL_TIPS.IF(original)) {
				if (version >= 16.0) {
					JSONArray extras = new JSONArray();

					for (String tool_tip : formatted.getToolTips().getFormats()) {
						Matcher matcher = Core.COLOR_HEX.matcher(tool_tip);
						boolean start = true;
						while (matcher.find()) {
							if (start && tool_tip.indexOf("#") > 0)  {
								JSONObject extra = new JSONObject();
								extra.put("text", tool_tip.substring(0, tool_tip.indexOf("#")));
								extras.add(extra);
							}

							JSONObject extra = new JSONObject();
							int index = tool_tip.indexOf("#", matcher.end());
							if (index <= 0 || index < matcher.end())
								extra.put("text", tool_tip.substring(matcher.end()));

							else
								extra.put("text", tool_tip.substring(matcher.end(), index));

							extra.put("color", matcher.group());
							extras.add(extra);

							start = false;
						}

						if (!start) {
							JSONObject extra = new JSONObject();
								extra.put("text", "\n");
							extras.add(extra);
						}
					}

					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");

					if (extras.isEmpty()) {
						hoverEvent.put("value", String.join("\n",formatted.getToolTips().getFormats()));

					} else {
						JSONObject tool_tips_json = new JSONObject();
							tool_tips_json.put("text", "");
							tool_tips_json.put("extra", extras);
						hoverEvent.put("value", tool_tips_json);
					}

					json.put("hoverEvent", hoverEvent);

				} else {
					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");
						hoverEvent.put("value", String.join("\n",formatted.getToolTips().getFormats()));
					json.put("hoverEvent", hoverEvent);
				}
			}

			logger.debug("Json: ", JsonFormatter.format(json.toString()));

			BaseComponent[] message = ComponentSerializer.parse(json.toString());
			player.spigot().sendMessage(message);
		}
	}
}