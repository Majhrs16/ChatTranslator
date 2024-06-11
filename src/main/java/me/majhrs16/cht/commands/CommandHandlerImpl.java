package me.majhrs16.cht.commands;

import me.majhrs16.lib.minecraft.commands.CommandManager;
import me.majhrs16.lib.storages.YAML;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
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

		Message from = new Message();
			from.setSender(sender);
			from.setLangTarget(API.getLang(sender));
			from.getMessages().setFormats(text);

		if (description.length > 0) {
			from.getToolTips().setFormats("%ct_toolTips%", "");
			from.getToolTips().setTexts(description);
		}

		if (sender instanceof Player && util.getMinecraftVersion() >= 7.2) {
			if (description.length > 0)
				from.getToolTips().setFormats("%ct_toolTips%");

			JSONObject jsonMessage = new JSONObject();

			Message tmp = from.clone();
				tmp.getMessages().setFormats(text, "/" + suggest);
			tmp = API.formatMessage(tmp);

			jsonMessage.put("text", tmp.getMessages().getFormat(0));

			if (suggest != null) {
				JSONObject clickEvent = new JSONObject();
					clickEvent.put("action", "suggest_command");
					clickEvent.put("value", tmp.getMessages().getFormat(1));
				jsonMessage.put("clickEvent", clickEvent);
			}

			from.getMessages().setFormats(jsonMessage.toString());
			from.getMessages().setTexts();
		}

		API.sendMessage(from);
	}
}