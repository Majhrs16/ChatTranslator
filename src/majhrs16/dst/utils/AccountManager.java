package majhrs16.dst.utils;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.util;
import majhrs16.cht.ChatTranslator;

import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.Map;

public class AccountManager {
	private static ChatTranslator plugin = ChatTranslator.getInstance();
	private static ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static Map<String, String> linking = new HashMap<String, String>();

	public static UUID getMinecraft(String discordID) {
		String[] result = plugin.storage.get(discordID);

		if (result == null)
			return null;

		return UUID.fromString(result[0]);
	}

	public static String getDiscord(UUID minecraftUUID) {
		String[] result = plugin.storage.get(minecraftUUID);

		if (result == null)
			return null;

		return result[1];
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		if (util.getMinecraftVersion() >= 7.2) {
			return Bukkit.getOfflinePlayer(uuid);

		} else {
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				if (player.getPlayer().getUniqueId().equals(uuid))
					return player;
			}
		}

		return null;
	}

	public static boolean login(String key, String discordID) {
		boolean status = linking.containsKey(key);

		if (status) {
			UUID uuid = UUID.fromString(linking.get(key));
			linking.remove(key);

			plugin.storage.set(
				uuid,
				discordID,
				API.getLang(getOfflinePlayer(uuid))
			);
		}

		return status;
	}

	private static int getUniqueKey() {
		Integer key = null;

		while (key == null || linking.containsKey(key.toString()))
			key = new Random().nextInt(100_000); // 99_999
		
		return key;
	}

	public static int register(UUID uuid, Runnable timeout) {
		Integer key = getUniqueKey();

		linking.put(key.toString(), uuid.toString());

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (linking.containsKey(key.toString())) {
					linking.remove(key.toString());
					if (timeout != null)
						timeout.run();
				}
			}
		}, 20 * 60); // 1min

		return key;
	}
}