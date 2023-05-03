package majhrs16.ct.events;

import java.util.HashMap;

import majhrs16.ct.api;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chat implements Listener {
	private main plugin;
	private api api;
	private HashMap<Player, Boolean> map;

	public chat(main plugin) {
		this.plugin = plugin;
		this.api    = new api(plugin);
		
		map = new HashMap<Player,Boolean>();
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
	public void onMessage(AsyncPlayerChatEvent event) throws Exception {
		FileConfiguration config = plugin.getConfig();
		String msgFormat         = config.getString("message-format");
		api._Sender player = api.new _Sender(event.getPlayer());

		if (map.get(player.getPlayer()) == null) {
			map.put(player.getPlayer(), true);
		}

		if (map.get(player.getPlayer())) {
			event.setCancelled(true);

			map.put(player.getPlayer(), false);
			Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, event.getPlayer(), event.getMessage(), event.getRecipients()));
			map.put(player.getPlayer(), true);

			api.broadcast(player, msgFormat, event.getMessage());
		} 
	}
}
