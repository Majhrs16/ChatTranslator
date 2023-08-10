package majhrs16.cht.events.sign;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;

// import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;

public class SignBreak implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
//	private API API = new API();

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getUniqueId().toString(), (int) block.getX(), (int) block.getY(), (int) block.getZ());

		if (block.getType().equals(Material.AIR) && plugin.getSigns().contains(path)) {
			plugin.getSigns().set(path, null);
			plugin.saveSigns();
		}
	}
}
