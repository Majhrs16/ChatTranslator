package majhrs16.cht.events.sign;

import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;

import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;

public class SignPlace implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getUniqueId().toString(), block.getX(), block.getY(), block.getZ());

		plugin.getSigns().set(path, API.getLang(player));
		plugin.saveSigns();
	}
}
