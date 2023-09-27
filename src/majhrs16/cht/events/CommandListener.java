package majhrs16.cht.events;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import majhrs16.cht.commands.CommandHandler;
import majhrs16.cht.util.util;

public class CommandListener implements Listener {
	public boolean preProcessCommand(CommandSender sender, String command_line) {
		String[] command_parts;

		if (command_line.startsWith("/"))
			command_parts = command_line.substring(1).split(" ");

		else
			command_parts = command_line.split(" ");

		String command_base = command_parts[0];

		String[] args = new String[command_parts.length - 1];
		System.arraycopy(command_parts, 1, args, 0, command_parts.length - 1);

		return new CommandHandler().onCommand(sender, null, command_base, args);
	}

	@EventHandler
	public void onCommandPlayer(PlayerCommandPreprocessEvent event) { event.setCancelled(preProcessCommand(event.getPlayer(), event.getMessage())); }

	@EventHandler
	public void onCommandServer(ServerCommandEvent event) {
//		@SuppressWarnings("unused")
		boolean status = preProcessCommand(event.getSender(), event.getCommand());

		if (util.getMinecraftVersion() >= 8.0)
			event.setCancelled(status)
		;
	}
}