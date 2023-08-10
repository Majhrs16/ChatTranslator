package majhrs16.cht.events;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import org.bukkit.Material;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class SignHandler implements Listener {
	private ChatTranslator _plugin = ChatTranslator.plugin;
	private API API                = new API();

	@SuppressWarnings("deprecation")
	public void SignUpdater() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(_plugin, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_SIGN) {
			public void onPacketSending(PacketEvent event) {
				if (!util.IF(_plugin.getConfig(), "auto-translate-others"))
					return;

				Player to_player       = event.getPlayer();
				String uuid            = to_player.getUniqueId().toString();
				Player from_player     = Bukkit.getPlayer(uuid);
				String to_lang         = API.getLang(to_player);

				if (event.getPacketType() == PacketType.Play.Server.UPDATE_SIGN) {
					BlockPosition blockPosition = event.getPacket().getBlockPositionModifier().read(0);
					Block block                 = to_player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
					String path                 = String.format("%s_%s_%s_%s", uuid, (int) block.getX(), (int) block.getY(), (int) block.getZ());
					String from_lang            = _plugin.getSigns().getString(path);
					Sign signBlock              = (Sign) block.getState();
					String[] lines              = signBlock.getLines();

					if (!lines.equals(new String[] {"", "", "", ""})) {
						String msg = String.join("\n", lines);

						Message from = new Message(null, from_player, "%ct_messages%", msg, null, null, false, from_lang, true, false);
						Message to   = new Message(from, to_player,   "$ct_messages$", msg, null, null, false, to_lang,   true, false);

						lines = util.wrapText(API.formatMessage(to).getMessageFormat(), 20).split("\n", 4);

						WrappedChatComponent[] wrappedLines = Arrays.stream(lines).map(WrappedChatComponent::fromText).toArray(WrappedChatComponent[]::new);
						event.getPacket().getChatComponentArrays().write(0, wrappedLines);
					}
				}
			}
		});
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (!util.IF(_plugin.getConfig(), "auto-translate-others"))
			return;

		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getUniqueId().toString(), (int) block.getX(), (int) block.getY(), (int) block.getZ());

		if (block.getType().equals(Material.AIR) && _plugin.getSigns().contains(path)) {
			_plugin.getSigns().set(path, null);
			_plugin.saveSigns();
		}
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (!util.IF(_plugin.getConfig(), "auto-translate-others"))
			return;

		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getUniqueId().toString(), block.getX(), block.getY(), block.getZ());

		_plugin.getSigns().set(path, API.getLang(player));
		_plugin.saveSigns();
	}
}