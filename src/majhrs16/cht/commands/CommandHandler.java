package majhrs16.cht.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.util;

public class CommandHandler implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	private String command_base;

	public CommandHandler(String key) {
		command_base = key;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config = plugin.commands.get();

		Message DC = util.getDataConfigDefault();
			DC.setSender(sender);
			DC.setLangTarget(API.getLang(sender));

		String path = command_base;

		if (config.getString(path + ".type").equals("main")) {
			String description = String.join("\n", config.getStringList(path + ".toolTips"));
			DC.setToolTips(sender instanceof Player ? description : "	" + description.replace("\n", "\n\t"));

			DC.setMessages(config.getString(path + ".text"));
			API.sendMessage(DC);

			for (String key : config.getConfigurationSection(command_base).getKeys(false)) {
				if (!key.equals("type") && !key.equals("text") && !key.equals("suggest") && !key.equals("toolTip")) {
					path = command_base + "." + key;

					description = String.join("\n", config.getStringList(path + ".toolTips"));
					DC.setToolTips(sender instanceof Player ? description : "	" + description.replace("\n", "\n\t"));

					DC.setMessages(config.getString(path + ".text"));
						API.sendMessage(DC);
				}
			}
		}

		return false;
	}
}