package majhrs16.dst.utils;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import net.dv8tion.jda.api.entities.User;

import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.Map;

public class AccountManager {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static final Map<String, String> linking = new HashMap<>();

	public static UUID getMinecraft(String discordID) {
		String[] result = plugin.storage.get(discordID);

		if (result == null)
			return null;

		return UUID.fromString(result[0]);
	}

	public static User getDiscord(UUID minecraftUUID) {
		String[] result = plugin.storage.get(minecraftUUID);

		if (result == null || result[1] == null)
			return null;

		return DiscordTranslator.getJDA().retrieveUserById(result[1]).complete();
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		if (util.getMinecraftVersion() >= 7.2)
			return Bukkit.getOfflinePlayer(uuid);

		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (player.getPlayer().getUniqueId().equals(uuid))
				return player;
		}

		return null;
	}

	public static boolean confirmLink(String key, String discordID) {
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

	public static int preLink(UUID uuid, Runnable timeout) {
		Integer key = getUniqueKey();

		linking.put(key.toString(), uuid.toString());

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (linking.containsKey(key.toString())) {
				linking.remove(key.toString());
				if (timeout != null)
					timeout.run();
			}
		}, 20 * 60); // 1min

		return key;
	}
}