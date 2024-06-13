package me.majhrs16.cht.util.updater;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.InternetCheckerAsync;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.custom.Formats;

import java.nio.charset.StandardCharsets;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

public class UpdateChecker {
	private static final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private static final int timed_out   = 1500;

	public UpdateChecker(CommandSender to_sender) {
		Message.Builder builder = new Message.Builder()
			.setSender(to_sender)
			.setLangTarget(API.getLang(to_sender));

		try {
			if (!InternetCheckerAsync.isInternetAvailable())
				throw new RuntimeException("NO INTERNET");

			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			conn.setConnectTimeout(timed_out);
			conn.setReadTimeout(timed_out);

			String latestVersion;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				latestVersion = reader.readLine();
			}

			if (latestVersion != null && latestVersion.length() <= 8) {
				if (to_sender instanceof Player) {
					if (ChatColor.stripColor(Texts.get("versions.plugin")[0]).equals(latestVersion)) {
						builder.format("plugin.updates.latest.player");

					} else {
						////////////////////////////////////////////////////
						// Toda esta update v1.8 nacio apartir de aqui:
						// builder.setMessages(Texts.get("plugin.updates.new.player.messages"));
						// builder.setMessagesFormats(String.format(Texts.getString("plugin.updates.new.player.message_format").replace("$s", "%s").replace("%latestVersion%", latestVersion), (Object[]) API.formatMessage(builder).getMessages().split("\n")));

						builder.format("plugin.updates.new.player");
					}

				} else {
					if (ChatColor.stripColor(Texts.get("versions.plugin")[0]).equals(latestVersion)) {
						builder.format("plugin.updates.latest.console");

					} else {
						builder.format("plugin.updates.new.console");
					}
				}

				builder.setMessages(new Formats.Builder()
					.setFormats(API.replaceArray(
						builder.build().getMessages().getFormats(),
						"%latestVersion%",
						latestVersion

					)).setTexts(builder.build().getMessages().getTexts())
				);

			} else {
				throw new RuntimeException();
			}

		} catch (IOException | RuntimeException e) {
			if (to_sender instanceof Player) {
				builder.format("plugin.updates.error.player");

			} else {
				builder.format("plugin.updates.error.console");
			}
		}

		API.sendMessage(builder.build());
	}
}