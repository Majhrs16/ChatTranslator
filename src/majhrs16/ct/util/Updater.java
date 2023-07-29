package majhrs16.ct.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.UUID;
import java.net.URL;

public class Updater {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private int config_version;

	public void updateChecker() {
		CommandSender console = Bukkit.getConsoleSender();

		API API           = new API();
		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

			DC.setMessages("	");
				API.sendMessage(DC);

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://API.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out         = 3000;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestversion  = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestversion.length() <= 7) {
				if (plugin.version.equals(latestversion)) {
					DC.setMessages("&a	Estas usando la ultima version del plugin <3");
						API.sendMessage(DC);

				} else {
					DC.setMessages(String.format("&e	Hay una nueva version disponible&f! &f(&b%s&f)", latestversion));
						API.sendMessage(DC);

					DC.setMessages("&a		Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/");
						API.sendMessage(DC);
				}
			}

		} catch (Exception ex) {
			DC.setMessages("&c    Error mientras se buscaban actualizaciones&f.");
				API.sendMessage(DC);
		}
	}

	/////////////////////////////////////////////////////////
	public void updateConfig() {
		String path, tmp;
		Boolean cancel_event, clear_recipients;
		FileConfiguration config = plugin.getConfig();

		API API = new API();
		Message DC = util.getDataConfigDefault();

		String _path = "config-version";
		if (!config.contains(_path))
			config.set(_path, -1);

		config_version = config.getInt(_path);
		int config_version_original = config_version;

		if (config_version_original == 0) {
			config.set("server-uuid", UUID.randomUUID().toString());

			DC.setPlayer(Bukkit.getConsoleSender());
			DC.setLang(API.getLang(Bukkit.getConsoleSender()));
			if (util.checKDependency("ru.mrbrikster.chatty.api.ChattyApi")) {
				DC.setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				cancel_event     = false;
				clear_recipients = false;

			} else if (util.checKDependency("me.h1dd3nxn1nja.chatmanager.Main")) {
				DC.setMessages("&aDetectado ChatManager&f.");
					API.sendMessage(DC);
				cancel_event     = false;
				clear_recipients = false;

			} else {
				cancel_event     = true;
				clear_recipients = false;
			}
			path = "show-native-chat";
			config.set(path + ".cancel-event", cancel_event);
			config.set(path + ".clear-recipients", clear_recipients);
			config_version = 5;
		}

		path = "auto-update-config";
		if (config.contains(path) && !util.IF(config, path)) {
			config.set(_path, config_version);
			plugin.saveConfig();
			return;
		}
		
		if (util.IF(config, "debug"))
			System.out.println("Debug, config_version: " + config_version);

		if (config_version < 1)
			// Version perdida donde se usaba formatMsg en vez de format-message...
			config_version = 1;

		if (config_version < 2) {
			ArrayList<String> formats_from_messages  = new ArrayList<String>();
			ArrayList<String> formats_from_tool_tips = new ArrayList<String>();
			ArrayList<String> formats_from_sounds    = new ArrayList<String>();
			ArrayList<String> formats_to_messages    = new ArrayList<String>();
			ArrayList<String> formats_to_toolTips    = new ArrayList<String>();
			ArrayList<String> formats_to_sounds      = new ArrayList<String>();

			String formatMsg = config.getString("message-format");
				formatMsg = formatMsg.replace("%player%", "%player_name%");
				formatMsg = formatMsg.replace("%targetLang%", "$targetLang$");

				formatMsg = formatMsg.replace("%sourceLang%", "%ct_lang_source%");
				formatMsg = formatMsg.replace("$targetLang$", "$ct_lang_target$");

				formatMsg.replace("%msg%", "$ct_messages$");

				formats_from_messages.add(formatMsg);
				formats_to_messages.add(formatMsg);
			config.set("formats.from.messages", formats_from_messages);
				config.set("formats.from.toolTips", formats_from_tool_tips);
				config.set("formats.from.sounds", formats_from_sounds);
	
				config.set("formats.to.messages", formats_to_messages);
				config.set("formats.to.toolTips", formats_to_toolTips);
				config.set("formats.to.sounds", formats_to_sounds);
			config.set("message-format", null);

			path = "message-color-personalized";
			tmp = config.getString(path);
				config.set(path, null);
			config.set("chat-color-personalized", tmp);

			path = "auto-format-messages";
			tmp = config.getString(path);
				config.set(path, null);
			config.set("use-PAPI-format", tmp);

			path = "auto-translate-chat";
			if (!util.IF(config, path)) {
				String from_messages = formats_from_messages.get(0);
				formats_from_messages.remove(0);
				from_messages = from_messages.replace("$ct_messages$", "%ct_messages%");
				formats_from_messages.add(from_messages);

				String to_messages = formats_to_messages.get(0);
				formats_to_messages.remove(0);
				to_messages = to_messages.replace("$ct_messages$", "%ct_messages%");
				formats_to_messages.add(to_messages);
			}
			config.set(path, null);

			config.set("auto-update-config", true);

			config_version = 2;

		} if (config_version < 3) {
			Boolean show_native_chat;

			DC.setPlayer(Bukkit.getConsoleSender());
			DC.setLang(API.getLang(Bukkit.getConsoleSender()));
			if (util.checKDependency("ru.mrbrikster.chatty.api.ChattyApi")) {
				DC.setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				show_native_chat = true;

			} else if (util.checKDependency("me.h1dd3nxn1nja.chatmanager.Main")) {
				DC.setMessages("&aDetectado ChatManager&f.");
					API.sendMessage(DC);
				show_native_chat = true;
			
			} else
				show_native_chat = false;
			config.set("show-native-chat", show_native_chat);

			config.set("max-spam-per-tick", 150.0007);

			config_version = 3;
		}
		
		if (config_version < 4) {
			path = "show-native-chat";
			Boolean state = util.IF(config, path);
			config.set(path, null);
			config.set(path + ".cancel-event", state);
			config.set(path + ".clear-recipients", state);

			config_version = 4;
		}

		if (config_version < 5) {
			config.set("storage.type", "local");
			config.set("storage.host", "localhost");
			config.set("storage.port", 3306);
			config.set("storage.database", "ChatTranslator");
			config.set("storage.user", "root");
			config.set("storage.password", "password");
			config_version = 5;
		}

		config.set(_path, config_version);
		plugin.saveConfig();

		if (config_version > config_version_original) {
			DC.setPlayer(Bukkit.getConsoleSender());
			DC.setLang(API.getLang(Bukkit.getConsoleSender()));
			DC.setMessages(String.format(
				"&eSe ha actualizado la config de la version &b%s &ea la &b%s&f.",
				"" + config_version_original,
				"" + config_version));
			API.sendMessage(DC);
	    }
	}
}
