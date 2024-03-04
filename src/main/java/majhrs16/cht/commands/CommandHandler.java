package majhrs16.cht.commands;

import majhrs16.lib.minecraft.commands.CommandManager;
import majhrs16.lib.storages.YAML;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.json.simple.JSONObject;

public class CommandHandler extends majhrs16.lib.minecraft.commands.CommandHandler {
	public CommandHandler(CommandManager manager, YAML commands) {
		super(manager, commands);
	}

	@SuppressWarnings("unchecked")
	protected void showCommandHelp(CommandSender sender, String text, String[] description, String suggest) {
		ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

		Message from = util.getDataConfigDefault();
			from.setSender(sender);
			from.setLangTarget(API.getLang(sender));
			from.getMessages().setFormats(text);

		if (description.length > 0) {
			from.getToolTips().setFormats("%ct_messages%");
			from.getToolTips().setTexts(description);
		}

		if (sender instanceof Player && util.getMinecraftVersion() >= 7.2) {
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