package majhrs16.cht.events;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

public class Msg implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (event.isCancelled())
					return;

				API.sendMessage(event);
				event.setCancelled(true);
			}
		}, 1L);
	}
}