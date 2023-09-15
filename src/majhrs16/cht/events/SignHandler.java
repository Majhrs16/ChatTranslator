package majhrs16.cht.events;

/*
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType;
*/

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.lib.utils.Str;
import majhrs16.cht.util.util;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignHandler implements Listener {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler
	public void updateSign(PlayerInteractEvent event) {
		if (plugin.isDisabled() && !Config.TranslateOthers.SIGNS.IF())
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (!(block.getState() instanceof Sign))
				return;

			if (event.getPlayer().isSneaking())
				event.setCancelled(true);

			Sign sign = (Sign) block.getState();

			String path   = String.format("%s_%s_%s_%s", block.getWorld().getName(), (int) block.getX(), (int) block.getY(), (int) block.getZ());

			FileConfiguration signs = plugin.signs.get();
			Message from = util.getDataConfigDefault();
				from.setSender(null);
				from.setLangSource(signs.getString(path + ".lang"));
				from.setLangTarget(API.getLang(event.getPlayer()));
;
			from.setMessages(String.join(" ", signs.getStringList(path + ".text")));
			from = API.formatMessage(from);

			int i = 0;
			String[] lines = Str.wrapText(from.getMessages(), 15).split("\n");
			for (; i < Math.min(lines.length, 4); i++) {
				String line = lines[i];

				System.out.println("Line sign: " + i + " text: '" + line + "'");

				if (line.isEmpty()) {
					sign.setLine(i, "");
					continue;
				}

				sign.setLine(i, line);
			}

			for (; i < 4; i++)
				sign.setLine(i, "");

			sign.update();
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (plugin.isDisabled() && !Config.TranslateOthers.SIGNS.IF())
			return;

		FileConfiguration signs = plugin.signs.get();
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getWorld().getName(), (int) block.getX(), (int) block.getY(), (int) block.getZ());

		if (signs.contains(path)) {
			signs.set(path + ".text", null);
			signs.set(path + ".lang", null);
			signs.set(path, null);
			plugin.signs.save();

			try {
				plugin.signs.reload();

			} catch (ParseYamlException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (plugin.isDisabled() && !Config.TranslateOthers.SIGNS.IF())
			return;

		FileConfiguration signs = plugin.signs.get();
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getWorld().getName(), block.getX(), block.getY(), block.getZ());

		if (event.getLines().equals(new String[] {"", "", "", ""}) || event.getLines().length == 0)
			return;

		signs.set(path + ".text", event.getLines());
		signs.set(path + ".lang", API.getLang(player));
		plugin.signs.save();
		try {
			plugin.signs.reload();

		} catch (ParseYamlException e) {
			e.printStackTrace();
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//	private List<Player> showed = new ArrayList<Player>();

//	private final int VIEW_DISTANCE = (int) Math.pow(7, 2); // 7 bloques al cuadrado

	/*
	public SignHandler() {
		new BukkitRunnable() {
			public void run() {
				signUpdater();
			}

		}.runTaskTimer(_plugin, 0L, 20L);
	}

	public void signUpdater() {
		FileConfiguration signs = _plugin.getSigns();

		for (String path : signs.getKeys(false)) {
			String[] locationParts = path.split("_");
			if (locationParts.length != 4)
				continue;

			World world = _plugin.getServer().getWorld(locationParts[0]);
			if (world == null)
				return;

			int x = Integer.parseInt(locationParts[1]);
			int y = Integer.parseInt(locationParts[2]);
			int z = Integer.parseInt(locationParts[3]);

			Location signLocation = new Location(world, x, y, z);
			Block block = world.getBlockAt(signLocation);

			if (!(block.getState() instanceof Sign))
				continue;

			Sign signState = (Sign) block.getState();
			String[] lines = signState.getLines();

			String from_lang = signs.getString(path + ".lang");
//			BlockPosition blockPosition = new BlockPosition(x, y, z);

//			int blockId = block.getState().getType().getId();

			for (Player to_player : world.getPlayers()) {
				if (to_player.getLocation().distanceSquared(signLocation) <= VIEW_DISTANCE) {
					if (!showed.contains(to_player)) {
						showed.add(to_player);
//						updateSign( blockPosition, blockId, lines, from_lang, to_player);
 * 						updateSign(lines, from_lang, to_player);
					}

				} else {
					showed.remove(to_player);
				}
			}
		}
	}
	*/

//	public void updateSign(/* BlockPosition blockPosition, int blockId, */ String[] lines, String fromLang, Player toPlayer) {
		/*
		Message from = util.getDataConfigDefault();
			from.setSender(toPlayer);
			from.setLangTarget(API.getLang(toPlayer));

		from.setMessages(_plugin.sep); API.sendMessage(from);
		from.setMessages("&f[ &eATENCION! &f]"); API.sendMessage(from);
		from.setMessages("&f[ &6Traduccion del cartel mas cercano &f]"); API.sendMessage(from);

		from.setLangSource(fromLang);
		for (String line : lines) {
			if (line.isEmpty())
				continue;

			from.setMessages(line); API.sendMessage(from);
		}

		from.setLangSource("es");
		from.setMessages(_plugin.sep); API.sendMessage(from);
		*/

//		Me parece muy triste una idea con tanto potencial perdida en el olvido por limitaciones de ProtocolLib o probablemente de mi conocimiento.

		/*
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);
		packet.getBlockPositionModifier().write(0, blockPosition);
//		packet.getIntegers().write(0, blockId);

		NbtCompound signNbt = NbtFactory.ofCompound("");
        signNbt.put("id", "Sign");
        for (int i = 0; i < lines.length; i++)
            signNbt.put("Text" + (i + 1), "X");
        packet.getNbtModifier().write(0, signNbt);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(toPlayer, packet);

		} catch (Exception e) {
			e.printStackTrace();
		}*/

		/*
		@SuppressWarnings("deprecation")
		PacketContainer signUpdatePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_SIGN);
			WrappedChatComponent[] wrappedLines = new WrappedChatComponent[4];
			for (int i = 0; i < 4; i++) {
				String formattedLine = "";
				if (!lines[i].isEmpty()) {
					formattedLine = API.formatMessage(
						new Message(null, null, "%ct_messages%", lines[i], null, null, false, fromLang, API.getLang(to_player), true, false)
					).getMessages();
				}
	
				wrappedLines[i] = WrappedChatComponent.fromText(formattedLine);
			}
	
			signUpdatePacket.getBlockPositionModifier().write(0, blockPosition);
			signUpdatePacket.getChatComponentArrays().write(0, wrappedLines);

		PacketContainer fakeOpenEditorPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Client.UPDATE_SIGN);
			fakeOpenEditorPacket.getBlockPositionModifier().write(0, blockPosition);
//			fakeOpenEditorPacket.getBooleans().write(0, false); // isFrontText

		PacketContainer blockUpdatePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
			blockUpdatePacket.getBlockPositionModifier().write(0, blockPosition);
			blockUpdatePacket.getBlockData().write(0, WrappedBlockData.createData(blockData));

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(to_player, blockUpdatePacket);
			ProtocolLibrary.getProtocolManager().receiveClientPacket(to_player, fakeOpenEditorPacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(to_player, signUpdatePacket);

		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
}