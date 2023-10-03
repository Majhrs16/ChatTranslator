package majhrs16.cht.util.updater;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

public class UpdateChecker {
	private static ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static final int timed_out   = 1500;

	public UpdateChecker(CommandSender to_sender) {
		Message DC = util.getDataConfigDefault();
			DC.setSender(to_sender);
			DC.setLangTarget(API.getLang(to_sender));

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			conn.setConnectTimeout(timed_out);
			conn.setReadTimeout(timed_out);
			String latestVersion = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
			if (latestVersion.length() <= 8) {
				if (to_sender instanceof Player) {
					if (ChatColor.stripColor(Texts.getString("versions.plugin")).equals(latestVersion))
						return;

					@SuppressWarnings("unused")
					Player player = (Player) to_sender;

					if (util.getMinecraftVersion() >= 7.10) { // 1.7.10
						DC.setMessages(Texts.get("plugin.updates.new.player.messages"));
						DC.setMessageFormat(String.format(Texts.getString("plugin.updates.new.player.message_format").replace("%latestVersion%", latestVersion), (Object[]) API.formatMessage(DC).getMessages().split("\n")));

/*
						DC.setMessages("&9link");
						DC.setToolTips("&7Descargar " + Texts.get("plugin.name") + " &b" + latestVersion);

						net.md_5.bungee.api.chat.TextComponent linkText = new net.md_5.bungee.api.chat.TextComponent(API.formatMessage(DC).getMessages());
							linkText.setClickEvent(
								new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, Texts.get("plugin.url")[0])
							);

							net.md_5.bungee.api.chat.HoverEvent hoverEvent;
							if (util.getMinecraftVersion() < 16.0) { // 1.16.0
								hoverEvent = new net.md_5.bungee.api.chat.HoverEvent(
									net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
									new net.md_5.bungee.api.chat.ComponentBuilder(API.formatMessage(DC).getToolTips()).create()
								);

							} else {
								hoverEvent = new net.md_5.bungee.api.chat.HoverEvent(
									net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
									new net.md_5.bungee.api.chat.hover.content.Text(API.formatMessage(DC).getToolTips())
								);
							}

							linkText.setHoverEvent(hoverEvent);

						net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent("    ");
							DC.setMessages("&aPuedes descargarla en este");
							DC.setToolTips("&f!");

							message.addExtra(API.formatMessage(DC).getMessages() + " ");
							message.addExtra(linkText);
							message.addExtra(API.formatMessage(DC).getToolTips());

						DC.setMessages(String.format("&eHay una nueva versión disponible&f! &f(&B%s&f)", latestVersion));
						DC.setToolTips();
						player.spigot().sendMessage(new net.md_5.bungee.api.chat.TextComponent(API.formatMessage(DC).getMessages()));

						player.spigot().sendMessage(message);
// */
					}

				} else {
					if (ChatColor.stripColor(Texts.get("versions.plugin")[0]).equals(latestVersion)) {
						DC.setMessages(Texts.get("plugin.updates.latest"));

					} else {
						DC.setMessages(Texts.getString("plugin.updates.new.console").replace("%verion%", latestVersion));
					}
				}

			} else {
				DC.setMessages(Texts.get("plugin.updates.error"));
			}

		} catch (IOException ex) {
			DC.setMessages(Texts.get("plugin.updates.error"));
		}

		API.sendMessage(DC);
	}
}