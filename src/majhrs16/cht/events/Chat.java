package majhrs16.cht.events;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Listener {
	private final ChatTranslator plugin  = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API  = ChatTranslatorAPI.getInstance();

	public static final Pattern mentions = Pattern.compile("@([A-Za-z0-9_.]+)");

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (plugin.isDisabled() || event.isCancelled())
			return;

		event.setCancelled(Config.NativeChat.CANCEL.IF());

		String from_lang = API.getLang(event.getPlayer());
		Message console  = util.createChat(
				Bukkit.getConsoleSender(),
				event.getMessage().replace("\"", "\\\""),
				from_lang,
				API.getLang(Bukkit.getConsoleSender()),
				"console")
			.setSender(event.getPlayer())
			.setCancelledThis(true); // Evitar duplicacion para el remitente.

		List<Player> players = new ArrayList<>();
		Matcher matcher      = Chat.mentions.matcher(event.getMessage());
		while (matcher.find()) {
			String nick_mention = matcher.group(1);

			Player to_player = Bukkit.getPlayer(nick_mention);

			if (to_player == null || players.contains(to_player))
				continue;

			players.add(to_player);
		}

		Message model = util.createChat(
			event.getPlayer(),
			event.getMessage(),
			from_lang,
			from_lang,
			players.isEmpty() ? null : "mention"); // null = chat normal por defecto.

		API.broadcast(model, players.isEmpty() ? util.getOnlinePlayers() : players.toArray(new Player[0]), froms -> {
			froms.add(console);
			API.broadcast(froms);
		});

		if (Config.NativeChat.CLEAR.IF())
			event.getRecipients().clear();
	}
}
