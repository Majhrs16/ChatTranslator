package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.CacheSpam;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatLimiter {
	private ChatTranslator plugin         = ChatTranslator.getInstance();
	public static ArrayList<Message> chat = new ArrayList<Message>();
	private BukkitTask task;

	public void start() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			int max_messages_per_tick     = 7;
			Map<Player, CacheSpam> counts = new HashMap<>();
			FileConfiguration config      = plugin.config.get();
			CacheSpam spam                = new CacheSpam(0.0F,
				config.contains("max-spam-per-tick")
					? Float.valueOf(config.getString("max-spam-per-tick"))
					: 0.0F
			);

			public void run() {
				if (plugin.isDisabled())
					return;

				if (spam.getCountInt() > spam.getMaxInt() && spam.getMax() > 0.0F) {
					counts.clear();

					config = plugin.config.get();

					if (Config.DEBUG.IF())
						Bukkit.getConsoleSender().sendMessage("Debug 11: " + spam.getCount() + ", " + spam.getMax());

					spam = new CacheSpam(0.0F,
						config.contains("max-spam-per-tick")
						? Float.valueOf(config.getString("max-spam-per-tick"))
						: 0.0F
					);
				}
				
				if (chat.size() > 0) {
					int end = Math.min(max_messages_per_tick, chat.size());

					for (Message event : chat.subList(0, end)) {
						if (event.getSender() instanceof Player) {
							Player player = (Player) event.getSender();

							if (spam.getMax() > 0.0F) {
								if (counts.containsKey(player)) {
									CacheSpam count = counts.get(player);
										count.setCount(count.getCount() + 0.0001F);
									counts.put(player, count);

									if (Config.DEBUG.IF())
										Bukkit.getConsoleSender().sendMessage("Debug 20: " + count.getCount() + ", " + spam.getCount() + ", " + spam.getMax());

									if (count.getCountFloat() >= spam.getMaxFloat()) {
										player.kickPlayer("Spam");
										counts.remove(player);
										continue;
									}

								} else {
									counts.put(player, new CacheSpam(0.0F, null));
								}
							}
						}

						Bukkit.getPluginManager().callEvent(event);
					}

					if (Config.DEBUG.IF())
						Bukkit.getConsoleSender().sendMessage("Debug 30: " + chat.size());

					chat.subList(0, end).clear();
				}

				if (spam.getMax() > 0.0F) {
					for (Map.Entry<Player, CacheSpam> entry : counts.entrySet()) {
						Player player = entry.getKey();
						CacheSpam count = entry.getValue();
							count.setCount(count.getCount() + 1);
						counts.put(player, count);
					}

					spam.setCount(spam.getCount() + 1);
				}
			}
		}, 0L, 1L);
	}

	public void stop() {
		task.cancel();
	}
}