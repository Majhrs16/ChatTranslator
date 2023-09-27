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

	public UpdateChecker(CommandSender to_sender) {
		Message DC = util.getDataConfigDefault();
			DC.setSender(to_sender);
			DC.setLangTarget(API.getLang(to_sender));

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out = 3000;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestVersion.length() <= 8) {
				if (ChatColor.stripColor(Texts.VERSIONS.PLUGIN).equals(latestVersion)) {
					DC.setMessages("&a	Estas usando la última versión del plugin <3");
						API.sendMessage(DC);

				} else {
					if (to_sender instanceof Player) {
						if (util.getMinecraftVersion() >= 7.10) { // 1.7.10
//							/*
							Player player = (Player) to_sender;

							DC.setMessages("&9link");
							DC.setToolTips("&7Descargar " + Texts.PLUGIN.NAME + " &b" + latestVersion);

							net.md_5.bungee.api.chat.TextComponent linkText = new net.md_5.bungee.api.chat.TextComponent(API.formatMessage(DC).getMessages());
								net.md_5.bungee.api.chat.ClickEvent clickEvent = new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/chattranslator.106604/");
									linkText.setClickEvent(clickEvent);

								@SuppressWarnings("deprecation")
								net.md_5.bungee.api.chat.HoverEvent hoverEvent = new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.ComponentBuilder(API.formatMessage(DC).getToolTips()).create());
									linkText.setHoverEvent(hoverEvent);

							net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent("    ");
								DC.setMessages("&aPuedes descargarla en este");
								DC.setToolTips("&f!");

								message.addExtra(API.formatMessage(DC).getMessages() + " ");
								message.addExtra(linkText);
								message.addExtra(API.formatMessage(DC).getToolTips());

							net.md_5.bungee.api.chat.TextComponent versionMessage = new net.md_5.bungee.api.chat.TextComponent();
								DC.setMessages(String.format("&eHay una nueva versión disponible&f! &f(&B%s&f)", latestVersion));
								DC.setToolTips();

								versionMessage.setText(API.formatMessage(DC).getMessages());

							player.spigot().sendMessage(versionMessage);
							player.spigot().sendMessage(message);
//							*/

						} else {
							DC.setMessages(String.format("&eHay una nueva versión disponible&f! &f(&B%s&f)", latestVersion));
							DC.setToolTips("&aPor favor vea su consola &f/ &aterminal&f.");
								API.sendMessage(DC);
						}

					} else {
						DC.setMessages(String.format("&e	Hay una nueva versión disponible&f! &f(&b%s&f)", latestVersion));
							API.sendMessage(DC);
	
						DC.setMessages("&a		Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/");
							API.sendMessage(DC);
					}
				}

			} else {
				DC.setMessages("&c	Error mientras se buscaban actualizaciones&f.");
					API.sendMessage(DC);
			}

		} catch (IOException ex) {
			DC.setMessages("&c	Error mientras se buscaban actualizaciones&f.");
				API.sendMessage(DC);
		}
	}
}