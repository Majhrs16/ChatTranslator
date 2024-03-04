package majhrs16.cht.commands.cht;

import majhrs16.lib.minecraft.commands.CommandExecutor;
import majhrs16.lib.exceptions.ParseYamlException;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.updater.ConfigUpdater;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;

public class ResetterConfig implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();

	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		DC.getMessages().setTexts("&aRestableciendo la config&f...");
		API.sendMessage(DC);

		try {
			plugin.config.reset();
			new ConfigUpdater();
			DC.getMessages().setTexts("&aSe ha restablecido la config exitosamente&f.");

		} catch (ParseYamlException e) {
			DC.getMessages().setTexts("&cNO se ha podido restablecer la config&f.");
		}

		DC.getMessages().setFormats("{0}");
		API.sendMessage(DC);
		return true;
	}
}
