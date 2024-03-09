package majhrs16.cht.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.util.cache.SpamTracker;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;

import majhrs16.lib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class ChatLimiter {
	private BukkitTask task;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final ArrayList<Message> chat = new ArrayList<>();
	private final Logger logger = plugin.logger;

	public void start() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			final Map<Player, SpamTracker> counts = new HashMap<>();
			SpamTracker spam = getGlobalSpamTracker();

			private SpamTracker getGlobalSpamTracker() {
				FileConfiguration config  = plugin.config.get();
				return new SpamTracker(
					0, // Contador de mensajes.
					config.getInt("spam.max-messages"),
					0,  // Contador de ticks.
					config.getInt("spam.max-ticks")
				);
			}

			public void run() {
				if (plugin.isDisabled()
						|| spam.getTotalCount() == 0
						|| spam.getTotalMax() == 0)
					return;

				if (spam.getActualMax() >= spam.getTotalMax()) {
					counts.clear();

					logger.debug("spam global: actualCount: %s, totalCount: %s, actualMax: %s, totalMax: %s",
						spam.getActualCount(), spam.getTotalCount(), spam.getActualMax(), spam.getTotalMax());

					spam = getGlobalSpamTracker();
				}

				logger.debug("chat.size: %s", chat.size());

				for (int i = 0; !chat.isEmpty() && i < Math.min(spam.getTotalCount(), chat.size()); i++) {
					Message event = chat.get(0);

					if (event.getSender() instanceof Player) {
						Player player = (Player) event.getSender();

						if (!counts.containsKey(player))
							counts.put(player, new SpamTracker(0, 0, 0, 0));

						SpamTracker count = counts.get(player);
						count.setActualCount(count.getActualCount() + 1);
						counts.put(player, count);

						logger.debug("count '%s': actualCount: %s, totalCount: %s, actualMax: %s, totalMax: %s",
							player.getName(),
							count.getActualCount(), count.getTotalCount(), count.getActualMax(), count.getTotalMax());

						if (count.getActualCount() >= spam.getTotalCount() * 1.5) {
							player.kickPlayer("SPAM!");
							counts.remove(player);
						}

						if (count.getActualCount() >= spam.getTotalCount())
							continue;
					}

					Bukkit.getPluginManager().callEvent(event);

					try { remove(event); }
					catch (Exception ignored) {}
				}

				// Aumentamos el contador de los ticks procesados.
				spam.setActualMax(spam.getActualMax() + 5);
			}

		}, 0L, 5L);
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