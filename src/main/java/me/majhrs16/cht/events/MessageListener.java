package me.majhrs16.cht.events;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.dst.utils.DiscordChat;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.List;

public class MessageListener implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
			toMinecraft(event);

//			event.getTo().setLangTarget(plugin.storage.getDefaultLang());
//			No me parece adecuado, teniendo en cuenta que los tooltips se ven feos en Discord.

			event.getTo().format("to_discord");
			toDiscord(event, DiscordChat.getChannels("discord.channels.chat"));

			event.setCancelled(true);
		}, 1L);
	}

	public void toMinecraft(Message event) {
		if (event.isEmpty())
			return;

		API.sendMessage(event);
	}

	@SuppressWarnings("deprecation")
	public void toDiscord(Message event, List<String> channels) {
		if (event.isEmpty()
				|| event.isCancelled()
				|| !Config.TranslateOthers.DISCORD.IF()
				|| event.getLangSource() == null
				|| event.getLangTarget() == null
				|| event.getLangSource().equals("disabled")
				|| event.getLangTarget().equals("disabled")
				|| event.getMessages().getFormats().length == 0
				|| event.getMessages().getTexts().length == 0
				|| event.getTo().getMessages().getFormats().length == 0)
			return;

		for (int i = 0; i < event.getMessages().getTexts().length; i++) {
			String message = event.getMessages().getText(i);
			Matcher matcher = Chat.mentions.matcher(message);

			while (matcher.find()) {
				User user = null;
				String nick_mention = matcher.group(1);

				OfflinePlayer to_player = Bukkit.getOfflinePlayer(nick_mention);

				if (to_player.hasPlayedBefore())
					user = AccountManager.getDiscord(util.getUUID(to_player));

				else {
					boolean ok = false;
					for (Guild guild : DiscordTranslator.getJDA().getGuilds()) {
						for (Member member : guild.retrieveMembersByPrefix(nick_mention, 100).get()) {
							String tag = member.getUser().getAsTag();
							if (nick_mention.equalsIgnoreCase(tag.substring(0, tag.lastIndexOf("#")))) {
								user = member.getUser();
								ok = true;
							}

							if (ok) break;
						}

						if (ok) break;
					}
				}

				if (user == null)
					continue;

				String user_mention = user.getAsMention();
				event.getTo().getMessages().setText(i, message.replace(matcher.group(), user_mention));
			}
		}

		Message from = API.formatMessage(event);

		if (event.getTo().getMessages().getFormats().length == 0)
			return;

		DiscordChat.broadcast(channels,
			String.join("\n", from.getTo().getMessages().getFormats())
				+ "\n\t" + String.join("\n\t", from.getTo().getToolTips().getFormats())
		);
	}
}