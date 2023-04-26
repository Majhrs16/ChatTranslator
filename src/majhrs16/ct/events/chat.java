package majhrs16.ct.events;

import majhrs16.ct.API;
import majhrs16.ct.Main;
import majhrs16.ct.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
	private Main plugin;
	private API API;
	private Util Util;

	public Chat(Main plugin) {
		this.plugin = plugin;
		this.API    = new API(plugin);
		this.Util   = new Util(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
	public void onMessage(AsyncPlayerChatEvent event) {
    	if (event.isAsynchronous()) {
    		event.setCancelled(true);
    		majhrs16.ct.Util.chat.add(new AsyncPlayerChatEvent(false, event.getPlayer(), event.getMessage(), event.getRecipients()));
    	}
    }

    public void processMsg(AsyncPlayerChatEvent event) {
    	FileConfiguration config = plugin.getConfig();
		String msgFormat         = config.getString("message-format");
		CommandSender player     = event.getPlayer();
		String msg               = event.getMessage();

		if (Util.IF("debug")) {
			System.out.println("Debug: PlayerFrom: '" + player.getName() + "'");
			System.out.println("Debug: msgFormat: '" + msgFormat + "'");
			System.out.println("Debug: msg: '" + msg + "'");
		}
		
		Bukkit.getPluginManager().callEvent(event);

		API.broadcast(player, msgFormat, msg);
    }
}