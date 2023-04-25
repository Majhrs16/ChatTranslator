package majhrs16.ct.events;

import majhrs16.ct.api;
import majhrs16.ct.main;
import majhrs16.ct.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chat implements Listener {
	private main plugin;
	private api api;
	private util util;

	public chat(main plugin) {
		this.plugin = plugin;
		this.api    = new api(plugin);
		this.util   = new util(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
	public void onMessage(AsyncPlayerChatEvent event) {
    	if (event.isAsynchronous()) {
    		event.setCancelled(true);
    		majhrs16.ct.util.chat.add(new AsyncPlayerChatEvent(false, event.getPlayer(), event.getMessage(), event.getRecipients()));
    	}
    }

    public void processMsg(AsyncPlayerChatEvent event) {
    	FileConfiguration config = plugin.getConfig();
		String msgFormat         = config.getString("message-format");
		CommandSender player     = event.getPlayer();
		String msg               = event.getMessage();

		if (util.IF("debug")) {
			System.out.println("Debug: PlayerFrom: '" + player.getName() + "'");
			System.out.println("Debug: msgFormat: '" + msgFormat + "'");
			System.out.println("Debug: msg: '" + msg + "'");
		}
		
		Bukkit.getPluginManager().callEvent(event);

		api.broadcast(player, msgFormat, msg);
    }
}