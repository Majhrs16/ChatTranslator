package majhrs16.ct.events;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;

public class Msg implements Listener {
	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		new API().sendMessage(event);
	}
}
