package majhrs16.cht.events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import majhrs16.cht.translator.ChatTranslatorAPI;

import majhrs16.cht.events.custom.Message;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class MessageListener implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				toMinecraft(event);
				toDiscord(event);
				event.setCancelled(true);
			}}, 1L);
	}

	public void toMinecraft(Message event) {
		if (event.equals(new Message()))
			return;

		API.sendMessage(event);
	}

	public void toDiscord(Message event) {
		if (event.equals(new Message()) || event.isCancelled() || !Config.TranslateOthers.DISCORD.IF())
			return;

//		Agregar control del MF.

		Message from = API.formatMessage(event);

		for (String channelID : plugin.config.get().getStringList("discord.channels")) {
			TextChannel channel = DiscordTranslator.getJda().getTextChannelById(channelID);

			if (channel == null)
				continue;

			channel.sendMessage(ChatColor.stripColor(from.getTo().getMessageFormat()))
				/* .addActionRow(Button.primary("translate-" + from.getLangSource(), "Traducir")) */
				.queue();

			if (from.getTo().getToolTips() != null)
				channel.sendMessage(ChatColor.stripColor(from.getTo().getToolTips())).queue();;
		}
	}
}