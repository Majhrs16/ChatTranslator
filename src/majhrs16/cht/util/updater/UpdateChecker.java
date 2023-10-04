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
					if (ChatColor.stripColor(Texts.getString("versions.plugin")).equals(latestVersion)) {
						DC.setMessages(Texts.getString("plugin.updates.latest.player.text").replace("%latestVersion%", latestVersion));

					} else {
						DC.setMessages(Texts.get("plugin.updates.new.player.messages"));
						DC.setMessageFormat(String.format(Texts.getString("plugin.updates.new.player.message_format").replace("$s", "%s").replace("%latestVersion%", latestVersion), (Object[]) API.formatMessage(DC).getMessages().split("\n")));
					}

				} else {
					if (ChatColor.stripColor(Texts.get("versions.plugin")[0]).equals(latestVersion)) {
						DC.setMessages(Texts.getString("plugin.updates.latest.console.text").replace("%latestVersion%", latestVersion));

					} else {
						DC.setMessages(Texts.getString("plugin.updates.new.console.text").replace("%latestVersion%", latestVersion));
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