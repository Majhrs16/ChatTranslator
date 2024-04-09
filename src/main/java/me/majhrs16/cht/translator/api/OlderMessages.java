package me.majhrs16.cht.translator.api;

import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.JsonFormatter;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.lib.logger.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

class OlderMessages {
	private static final Logger logger = ChatTranslator.getInstance().logger;

	@SuppressWarnings("unchecked")
	private static void processJ7(Message formatted, String version) throws ClassNotFoundException {
		Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
		Class<?> iChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
		Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
		Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");

		for (String format : formatted.getMessages().getFormats()) {
			try {
				JSONObject json;

				if (format.startsWith("{") && format.endsWith("}")) {
					json = (JSONObject) new JSONParser().parse(format);

				} else {
					json = new JSONObject();
					json.put("text", format);
				}

				if (formatted.getToolTips().getFormats().length > 0) {
					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");
						hoverEvent.put("value", String.join("\n", formatted.getToolTips().getFormats()));
					json.put("hoverEvent", hoverEvent);
				}

				logger.debug(JsonFormatter.format(json.toString()));

				Object chatComponentText = chatSerializerClass.getMethod("a", String.class).invoke(null, json.toString());

				Method sendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponent);
				sendMessageMethod.invoke(craftPlayerClass.getMethod("getHandle").invoke(craftPlayerClass.cast(formatted.getSender())), chatComponentText);

			} catch (Exception e) {
				logger.error(e.toString());
			}
		}
	}

	static void J52(Message formatted) {
		formatted.getSender().sendMessage(formatted.getMessages().getFormats());
		if (formatted.getToolTips().getFormats().length > 0) {
			String tool_tips;
				if (formatted.getSender() instanceof Player)
					 tool_tips = "    " + String.join("\n    ", formatted.getToolTips().getFormats());
				else tool_tips = "\t"   + String.join("\n\t",   formatted.getToolTips().getFormats());
			formatted.getSender().sendMessage(tool_tips);
		}
	}

	static void J72(Message formatted) throws ClassNotFoundException {
		processJ7(formatted, "v1_7_R1");
	}

	static void J75(Message formatted) throws ClassNotFoundException {
		processJ7(formatted, "v1_7_R2");
	}

	static void J78_J79(Message formatted) throws ClassNotFoundException {
		processJ7(formatted, "v1_7_R3");
	}
}
