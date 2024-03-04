package majhrs16.cht.commands.cht;

import majhrs16.lib.minecraft.commands.CommandExecutor;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;

public class ShowVersion implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		API.sendMessage(DC.format("commands.main.version"));
		return true;
	}
}
