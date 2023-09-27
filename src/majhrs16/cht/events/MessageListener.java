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
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler (priority = EventPriority.LOWEST)
	private void toMinecraft(Message event) {
		Message chat = event.clone();

		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (chat == new Message())
					return;

				ChatTranslatorAPI.getInstance().sendMessage(chat);
				event.setCancelled(true);
		}}, 1L);
	}

	@EventHandler (priority = EventPriority.LOWEST)
	private void toDiscord(Message event) {
		Message chat = event.clone();

		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				if (event == new Message() || chat.isCancelled() || !Config.TranslateOthers.DISCORD.IF())
					return;

				Message from = API.formatMessage(chat);

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

				event.setCancelled(true);
		}}, 1L);
	}
}