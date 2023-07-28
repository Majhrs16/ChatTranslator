package majhrs16.ct.events;

import org.bukkit.event.EventPriority;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;

public class Msg implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (event.isCancelled())
					return;

				API.sendMessage(event);
				event.getFather().setCancelled(true);
				event.setCancelled(true);
			}
		}, 1L);
	}
}
