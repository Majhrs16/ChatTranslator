package majhrs16.cht.events.sign;

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

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;

public class SignUpdater {
	private ChatTranslator _plugin = ChatTranslator.plugin;
	private API API                = new API();

	@SuppressWarnings("deprecation")
	public SignUpdater() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(_plugin, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_SIGN) {
			public void onPacketSending(PacketEvent event) {
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

						System.out.println("DEBUG 00: " + msg);

						Message from = new Message(null, from_player, "%ct_messages%", msg, null, null, false, from_lang, true, false);
						Message to   = new Message(from, to_player,   "$ct_messages$", msg, null, null, false, to_lang,   true, false);

						lines = wrapText(API.formatMessage(to).getMessageFormat(), 14).split("\n", 4);

						System.out.println("DEBUG 01: " + String.join("\n", lines));

						WrappedChatComponent[] wrappedLines = Arrays.stream(lines).map(WrappedChatComponent::fromText).toArray(WrappedChatComponent[]::new);
						event.getPacket().getChatComponentArrays().write(0, wrappedLines);
					}
				}
			}
		});
	}
	
	private String wrapText(String text, int maxLength) {
		if (text.length() <= maxLength)
			return text;

		ArrayList<String> segments = new ArrayList<>();
		int currentIndex = 0;
		while (currentIndex < text.length()) {
			int endIndex = Math.min(currentIndex + maxLength, text.length());
			segments.add(text.substring(currentIndex, endIndex));
			currentIndex = endIndex;
		}

		return String.join("\n", segments);
	}
}