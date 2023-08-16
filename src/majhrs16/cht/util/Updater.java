package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.scanner.ScannerException;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.io.File;
import java.net.URL;

public class Updater {
	public int config_version;
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void updateChecker() {
		CommandSender console = Bukkit.getConsoleSender();
		Message DC = util.getDataConfigDefault();
		DC.setSender(console);
		DC.setLang(API.getLang(console));

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out = 3000;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestVersion.length() <= 7) {
				if (plugin.version.equals(latestVersion)) {
					DC.getTo().setMessages("&a	Estas usando la última versión del plugin <3");
				} else {
					DC.getTo().setMessages(String.format("&e	Hay una nueva versión disponible&f! &f(&b%s&f)", latestVersion));
						API.sendMessage(DC);

					DC.getTo().setMessages("&a		Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/");
				}
			} else {
				DC.getTo().setMessages("&c	Error mientras se buscaban actualizaciones&f.");
			}

		} catch (IOException ex) {
			DC.getTo().setMessages("&c	Error mientras se buscaban actualizaciones&f.");
		}

		API.sendMessage(DC);
	}

	/////////////////////////////////////////////////////////
	public void updateConfig() {
		String path, tmp;
		Boolean cancel_event, clear_recipients;
		FileConfiguration config = plugin.getConfig();

		Message DC = util.getDataConfigDefault();
			DC.getTo().setSender(Bukkit.getConsoleSender());
			DC.getTo().setLang(API.getLang(Bukkit.getConsoleSender()));

		String _path = "config-version";
		if (!config.contains(_path))
			config.set(_path, -1);

		config_version = config.getInt(_path);
		int config_version_original = config_version;

		if (config_version_original == 0) {
			config.set("server-uuid", UUID.randomUUID().toString());

			if (util.checKDependency("ru.mrbrikster.chatty.api.ChattyApi")) {
				DC.getTo().setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				cancel_event     = false;
				clear_recipients = false;

			} else if (util.checKDependency("me.h1dd3nxn1nja.chatmanager.Main")) {
				DC.getTo().setMessages("&aDetectado ChatManager&f.");
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
			config_version = 3;
		}

		path = "auto-update-config";
		if (config.contains(path) && !util.IF(config, path)) {
			config.set(_path, config_version);
			plugin.saveConfig();
			return;
		}
		
		if (util.IF(config, "debug"))
			System.out.println("Debug, config_version: " + config_version);

		if (config_version < 1) {
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

				formats_from_messages.add(formatMsg.replace("%msg%", "%ct_messages%"));
				formats_to_messages.add(formatMsg.replace("%msg%", "$ct_messages$"));
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

			config_version = 1;

		} if (config_version < 2) {
			Boolean show_native_chat;

			if (util.checKDependency("ru.mrbrikster.chatty.api.ChattyApi")) {
				DC.getTo().setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				show_native_chat = true;

			} else if (util.checKDependency("me.h1dd3nxn1nja.chatmanager.Main")) {
				DC.getTo().setMessages("&aDetectado ChatManager&f.");
					API.sendMessage(DC);
				show_native_chat = true;
			
			} else
				show_native_chat = false;
			config.set("show-native-chat", show_native_chat);

			config.set("max-spam-per-tick", 150.0007);

			config_version = 2;
		}
		
		if (config_version < 3) {
			path = "show-native-chat";
			Boolean state = util.IF(config, path);
			config.set(path, null);
			config.set(path + ".cancel-event", state);
			config.set(path + ".clear-recipients", state);

			config.set("storage.type", "yaml");
			config.set("storage.host", "localhost");
			config.set("storage.port", 3306);
			config.set("storage.database", "ChatTranslator");
			config.set("storage.user", "root");
			config.set("storage.password", "password");

			ArrayList<String> formats_from_messages = new ArrayList<String>();
			ArrayList<String> formats_to_messages   = new ArrayList<String>();

			formats_from_messages.add("&e%ct_messages%");
			formats_to_messages.add("&e$ct_messages$");

			config.set("formats.from_entry.messages", new ArrayList<>(formats_from_messages));
			config.set("formats.to_entry.messages", new ArrayList<>(formats_to_messages));

			config.set("formats.from_exit.messages", new ArrayList<>(formats_from_messages));
			config.set("formats.to_exit.messages", new ArrayList<>(formats_to_messages));


			ArrayList<String> formats_console_messages = new ArrayList<String>();
			ArrayList<String> formats_console_toolTips = new ArrayList<String>();

			formats_console_messages.add("&f<&b%player_name%&f> &a$ct_messages$");
			formats_console_toolTips.add("\\t&f[&6%ct_lang_source%&f] &a%ct_messages%");

			config.set("formats.console.messages", formats_console_messages);
			config.set("formats.console.toolTips", formats_console_toolTips);


			config.set("auto-translate-others", false);

			upgradePlayers();

			config_version = 3;
		}

		config.set(_path, config_version);
		plugin.saveConfig();

		if (config_version > config_version_original) {
			DC.getTo().setMessages(String.format(
				"&eSe ha actualizado la config de la version &b%s &ea la &b%s&f.",
				"" + config_version_original,
				"" + config_version
			));
			API.sendMessage(DC);
	    }
	}
	
	public void upgradePlayers() {
		String filename = "players.yml";
		File file       = new File(plugin.getDataFolder(), filename);

		if (file.exists()) {
			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("config-version", 1);
				config.save(file);

			} catch (ScannerException e) {
				throw new IllegalArgumentException("[ERR020]");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}