package majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.scanner.ScannerException;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;

public class ConfigUpdater {
	public int config_version;

	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public ConfigUpdater() {
		String path, tmp;
		Boolean cancel_event, clear_recipients;
		FileConfiguration config = plugin.config.get();

		String _path = "config-version";
		if (!config.contains(_path))
			config.set(_path, -1);

		config_version = config.getInt(_path);
		int config_version_original = config_version;

		if (config_version_original == 0)
			config.set("server-uuid", UUID.randomUUID().toString()); // Para evitar crashes.

		Message DC = util.getDataConfigDefault();

		if (config_version_original == 0) {
			if (Dependencies.Chatty.exist()) {
				DC.setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				cancel_event     = false;
				clear_recipients = false;

			} else if (Dependencies.ChatManager.exist()) {
				DC.setMessages("&aDetectado ChatManager&f.");
					API.sendMessage(DC);
				cancel_event     = false;
				clear_recipients = false;

			} else if (Dependencies.DiscordSRV.exist()) {
				DC.setMessages("&aDetectado DiscordSRV&f.");
					API.sendMessage(DC);

				cancel_event     = false;
				clear_recipients = true;

			} else {
				cancel_event     = true;
				clear_recipients = false;
			}
			config.set(Config.NativeChat.CANCEL.getPath(), cancel_event);
			config.set(Config.NativeChat.CLEAR.getPath(), clear_recipients);

			if (Dependencies.ProtocolLib.exist()) {
				DC.setMessages("&aDetectado ProtocolLib&f.");
					API.sendMessage(DC);

				config.set(Config.TranslateOthers.SIGNS.getPath(), true);
			}

			config_version = 5;
		}

		path = "auto-update-config";
		if (config.contains(path) && !util.IF(config, path)) { // Solo por nostalgia lo dejare asi :,3
			config.set(_path, config_version);
			plugin.config.save();
			return;
		}
		
		if (Config.DEBUG.IF())
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

			if (Dependencies.Chatty.exist()) {
				DC.setMessages("&aDetectado Chatty&f.");
					API.sendMessage(DC);
				show_native_chat = true;

			} else if (Dependencies.ChatManager.exist()) {
				DC.setMessages("&aDetectado ChatManager&f.");
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
			ArrayList<String> formats_to_console_messages = new ArrayList<String>();
			ArrayList<String> formats_to_console_toolTips = new ArrayList<String>();
				formats_from_messages.add("&e%ct_messages%");
				formats_to_messages.add("&e$ct_messages$");

				formats_to_console_messages.add("&f<&b%player_name%&f> &a$ct_messages$");
				formats_to_console_toolTips.add("\\t&f[&6%ct_lang_source%&f] &a%ct_messages%");
			config.set("formats.from_entry.messages", new ArrayList<>(formats_from_messages));
			config.set("formats.from_exit.messages", new ArrayList<>(formats_from_messages));
			config.set("formats.to_entry.messages", new ArrayList<>(formats_to_messages));
			config.set("formats.to_exit.messages", new ArrayList<>(formats_to_messages));
			config.set("formats.to_console.messages", formats_to_console_messages);
			config.set("formats.to_console.toolTips", formats_to_console_toolTips);
			config.set("auto-translate-others.access", true);
			config.set("auto-translate-others.signs", true);

			try {
				upgradePlayers();
			} catch (ParseYamlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			config_version = 3;
		}

		if (config_version < 4) {
			config.set("check-updates", true);
			config_version = 4;
		}

		if (config_version < 5) {
			path = "chat-color-personalized";
			config.set("chat-custom-colors", config.getString(path));
			config.set(path, null);

			path = "formats.from_entry.messages";
			if (config.contains(path)) {
				List<String> from_entry = config.getStringList(path);
				for (int i = 0; i < from_entry.size(); i++)
					from_entry.set(i, from_entry.get(i).replace("$ct_messages$", "%ct_messages%"));
				config.set(path, from_entry);
			}

			path = "formats.from_exit.messages";
			if (config.contains(path)) {
				List<String> from_exit = config.getStringList(path);
				for (int i = 0; i < from_exit.size(); i++)
					from_exit.set(i, from_exit.get(i).replace("$ct_messages$", "%ct_messages%"));
				config.set(path, from_exit);
			}

			config_version = 5;
		}

		config.set(_path, config_version);
		plugin.config.save();

		if (config_version > config_version_original) {
			DC.setMessages(String.format(
				"&eSe ha actualizado la config de la version &b%s &ea la &b%s&f.",
				"" + config_version_original,
				"" + config_version
			));
			API.sendMessage(DC);
	    }
	}
	
	public void upgradePlayers() throws ParseYamlException {
		String filename = "players.yml";
		File file       = new File(plugin.getDataFolder(), filename);

		if (file.exists()) {
			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("config-version", 1);
				config.save(file);

			} catch (ScannerException e) {
				throw new ParseYamlException("[ERR021]");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}