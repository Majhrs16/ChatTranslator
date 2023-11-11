package majhrs16.cht.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.dst.utils.AccountManager;
import majhrs16.cht.util.cache.Config;
import majhrs16.dst.utils.DiscordChat;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import net.dv8tion.jda.api.entities.User;

import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public class MessageListener implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMessage(Message event) {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
			toMinecraft(event);

			String path              = "to_discord";
			FileConfiguration config = plugin.config.get();
			event.getTo().setMessagesFormats(config.contains("formats." + path + ".messages") ? path : null);
			event.getTo().setToolTips(config.contains("formats." + path + ".toolTips")        ? path : null);
			event.getTo().setSounds(config.contains("formats." + path + ".sounds")            ? path : null);

			toDiscord(event);

			event.setCancelled(true);
		}, 1L);
	}

	public void toMinecraft(Message event) {
		if (event.isEmpty())
			return;

		Message to_model     = util.createChat(event.getSender(), event.getMessages(), event.getLangSource(), event.getLangSource(), "mention");
		Matcher matcher      = Chat.mentions.matcher(event.getMessages());
		List<Player> players = new ArrayList<>();

		while (matcher.find()) {
			String nick_mention = matcher.group(1);

			Player to_player = Bukkit.getPlayer(nick_mention);

			if (to_player == null || event.getSender() == to_player || players.contains(to_player))
				continue;

			else if (event.getTo().getSender().equals(to_player))
				return;

			players.add(to_player);
		}

		API.broadcast(to_model, players.toArray(new Player[0]), froms -> API.broadcast(froms, API::sendMessage));
		API.sendMessage(event);
	}

	public void toDiscord(Message event) {
		if (event.isEmpty()
				|| event.isCancelled()
				|| !Config.TranslateOthers.DISCORD.IF()
				|| event.getLangSource() == null
				|| event.getLangTarget() == null
				|| event.getLangSource().equals("disabled")
				|| event.getLangTarget().equals("disabled")
				|| event.getMessagesFormats() == null
				|| event.getMessages() == null
				|| event.getTo().getMessagesFormats() == null
				|| event.getTo().getMessagesFormats().isEmpty())
			return;

		Matcher matcher = Chat.mentions.matcher(event.getMessages());

		while (matcher.find()) {
			String nick_mention = matcher.group(1);

			@SuppressWarnings("deprecation")
			OfflinePlayer to_player = Bukkit.getOfflinePlayer(nick_mention);

			if (!to_player.hasPlayedBefore())
				continue;

			User user = AccountManager.getDiscord(util.getUUID(to_player));

			if (user == null)
				continue;

			String user_mention = user.getAsMention();
			event.getTo().setMessages(event.getTo().getMessages().replace(matcher.group(), user_mention));
		}

		Message from = API.formatMessage(event);

		DiscordChat.broadcast("discord.channels.chat", channel -> {
			channel.sendMessage(util.stripColor(from.getTo().getMessagesFormats())).queue();

			if (from.getTo().getToolTips() != null)
				channel.sendMessage(util.stripColor(from.getTo().getToolTips())).queue();
		});
	}
}