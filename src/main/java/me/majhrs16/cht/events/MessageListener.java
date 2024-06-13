package me.majhrs16.cht.events;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.MessageEvent;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.dst.utils.DiscordChat;

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
	public void onMessage(MessageEvent event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
			if (!event.isCancelled()) {
				Message chat = event.getChat();
				toMinecraft(chat);

				toDiscord(
					chat.clone().setTo(chat.getTo().clone()
						.setLangTarget(plugin.storage.getDefaultLang())
						.format("to_discord")
					).build(),
					DiscordChat.getChannels("discord.channels.chat")
				);
			}

			event.setCancelled(true);
		}, 1L);
	}

	public void toMinecraft(Message chat) {
		if (chat.isEmpty())
			return;

		API.sendMessageAsync(chat);
	}

	@SuppressWarnings("deprecation")
	public void toDiscord(Message chat, List<String> channels) {
		if (chat.getTo().isShow()
				|| chat.isEmpty()
				|| !Config.TranslateOthers.DISCORD.IF()
				|| chat.getLangSource() == null
				|| chat.getLangTarget() == null
				|| chat.getLangSource().getCode().equals("DISABLED")
				|| chat.getLangTarget().getCode().equals("DISABLED")
				|| chat.getMessages().getFormats().length == 0
				|| chat.getMessages().getTexts().length == 0
				|| chat.getTo().getMessages().getFormats().length == 0)
			return;

		Formats.Builder builder = new Formats.Builder()
			.setFormats(chat.getTo().getMessages().getFormats())
			.setTexts(chat.getTo().getMessages().getTexts());

		for (int i = 0; i < chat.getMessages().getTexts().length; i++) {
			String message = chat.getMessages().getText(i);
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
				builder.setText(i, message.replace(matcher.group(), user_mention));
			}
		}

		chat = chat.clone().setTo(chat.getTo().clone()
			.setMessages(builder)
		).build();

		Message to = API.formatMessage(chat).getTo();

		if (chat.getTo().getMessages().getFormats().length == 0)
			return;

		DiscordChat.broadcast(channels,
			String.join("\n", to.getMessages().getFormats())
				+ "\n\t" + String.join("\n\t", to.getToolTips().getFormats())
		);
	}
}