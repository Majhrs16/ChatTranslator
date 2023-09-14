package majhrs16.cht.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class CommandWrapper implements CommandExecutor {
	private String key;
	public CommandWrapper(String key) {
		this.key = key;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return new CommandHandler().onCommand(sender, null, key, args);
	}
}