package majhrs16.cht.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.dst.utils.Utils;

public class MessageListener implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
			toMinecraft(event);
			toDiscord(event);

			event.setCancelled(true);
		}, 1L);
	}

	public void toMinecraft(Message event) {
		if (event.equals(new Message()))
			return;

		API.sendMessage(event);
	}

	public void toDiscord(Message event) {
		if (event.equals(new Message()) || event.isCancelled() || !Config.TranslateOthers.DISCORD.IF())
			return;

		String path;
		FileConfiguration config = plugin.config.get();

		path = "to_discord";
		event.getTo().setMessagesFormats(config.contains("formats." + path + ".messages") ? path : null);
		event.getTo().setToolTips(config.contains("formats." + path + ".toolTips")        ? path : null);
		event.getTo().setSounds(config.contains("formats." + path + ".sounds")            ? path : null);

		if (event.getTo().getMessageFormat() == null)
			return;

		Message from = API.formatMessage(event);

		Utils.broadcast("discord.channels.chat", channel -> {
			channel.sendMessage(ChatColor.stripColor(from.getTo().getMessageFormat()))
					/* .addActionRow(Button.primary("translate-" + from.getLangSource(), "Traducir")) */
					.queue();

			if (from.getTo().getToolTips() != null)
				channel.sendMessage(ChatColor.stripColor(from.getTo().getToolTips())).queue();
		});
	}
}