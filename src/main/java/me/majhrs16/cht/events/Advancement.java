package me.majhrs16.cht.events;

// import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Advancement implements Listener {
	@EventHandler()
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
//		org.bukkit.advancement.Advancement logro = event.getAdvancement();
// Desde la 1.7.2 es posible modificar el IChatBaseComponent para cambiar el title y description del logro...
	}
}