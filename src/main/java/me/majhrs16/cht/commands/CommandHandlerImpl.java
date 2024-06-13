package me.majhrs16.cht.commands;

import me.majhrs16.lib.minecraft.commands.CommandManager;
import me.majhrs16.lib.storages.YAML;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;
import me.majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.json.simple.JSONObject;

public class CommandHandlerImpl extends me.majhrs16.lib.minecraft.commands.CommandHandler {
	public CommandHandlerImpl(CommandManager manager, YAML commands) {
		super(manager, commands);
	}

	@SuppressWarnings("unchecked")
	protected void showCommandHelp(CommandSender sender, String text, String[] description, String suggest) {
		ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

		Message.Builder from = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		Formats.Builder tool_tips = new Formats.Builder()
			.setFormats("%ct_toolTips%");

		Formats.Builder messages = new Formats.Builder()
			.setFormats(text);

		if (description.length > 0)
			tool_tips.setTexts(description);

		if (sender instanceof Player && util.getMinecraftVersion() >= 7.2) {
			JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("text", text);

			if (suggest != null) {
				JSONObject clickEvent = new JSONObject();
					clickEvent.put("action", "suggest_command");
					clickEvent.put("value", "/" + suggest);
				jsonMessage.put("clickEvent", clickEvent);
			}

			messages.setFormats(jsonMessage.toString()).setTexts();
		}

		API.sendMessage(from
			.setMessages(messages)
			.setToolTips(tool_tips)
			.build()
		);
	}
}