package majhrs16.cht.events;

import majhrs16.lib.logger.Logger;
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
import java.util.UUID;

public class ChatLimiter {
	private BukkitTask task;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final ArrayList<Message> chat = new ArrayList<>();
	private final Logger logger = plugin.logger;

	public void start() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			final int max_messages_per_tick     = 7;
			final Map<Player, CacheSpam> counts = new HashMap<>();
			FileConfiguration config      = plugin.config.get();
			CacheSpam spam                = new CacheSpam(0.0D,
				config.contains("max-spam-per-tick")
					? config.getDouble("max-spam-per-tick")
					: 0.0D
			);

			public void run() {
				if (plugin.isDisabled())
					return;

				if (spam.getCountInt() > spam.getMaxInt() && spam.getMax() > 0.0F) {
					counts.clear();

					config = plugin.config.get();

					logger.debug("spam.count: %s, spam.max: %s", spam.getCount(), spam.getMax());

					spam = new CacheSpam(0.0D,
						config.contains("max-spam-per-tick")
							? config.getDouble("max-spam-per-tick")
							: 0.0D
					);
				}
				
				if (!chat.isEmpty()) {
					int end = Math.min(max_messages_per_tick, chat.size());

					for (Message event : chat.subList(0, end)) {
						if (event.getSender() instanceof Player) {
							Player player = (Player) event.getSender();

							if (spam.getMax() > 0.0F) {
								if (counts.containsKey(player)) {
									CacheSpam count = counts.get(player);
										count.setCount(count.getCount() + 0.0001F);
									counts.put(player, count);

									logger.debug("count: %s, spam.count: %s, spam.max: %s", count.getCount(), spam.getCount(), spam.getMax());

									if (count.getCountDouble() >= spam.getMaxDouble()) {
										player.kickPlayer("Spam!");
										counts.remove(player);
										continue;
									}

								} else {
									counts.put(player, new CacheSpam(0.0D, null));
								}
							}
						}

						Bukkit.getPluginManager().callEvent(event);
					}

					logger.debug("chat.size: %s", chat.size());

					Bukkit.getScheduler().runTaskLater(plugin, ChatLimiter::clear, 1L);
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

	public static void add(Message from) {
		chat.add(from);
	}

	public static void clear() {
		chat.clear();
	}

	public static Message get(UUID uuid) {
		for (Message from : chat) {
			if (from.getUUID().equals(uuid))
				return from;
		}

		return null;
	}

	public static void remove(Message from) {
		chat.remove(from);
	}
}