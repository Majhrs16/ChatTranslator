package majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.scanner.ScannerException;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.Dependencies;
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
	public int version;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public ConfigUpdater() {
		String path, tmp;
		Boolean cancel_event, clear_recipients;
		FileConfiguration config = plugin.config.get();

		String _path = "config-version";
		if (!config.contains(_path))
			config.set(_path, -1);

		version = config.getInt(_path);
		int version_original = version;

		if (version_original == 0)
			config.set("server-uuid", UUID.randomUUID().toString()); // Para evitar crashes.

		Message DC = util.getDataConfigDefault();

		if (version_original == 0) {
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

			} else {
				cancel_event     = true;
				clear_recipients = false;
			}

			config.set(Config.NativeChat.CANCEL.getPath(), cancel_event);
			config.set(Config.NativeChat.CLEAR.getPath(), clear_recipients);

			version = 7;
		}

		path = "auto-update-config";
		if (config.contains(path) && !util.IF(config, path)) { // Solo por nostalgia lo dejare asi :,3
			config.set(_path, version);
			plugin.config.save();
			return;
		}
		
		if (Config.DEBUG.IF())
			System.out.println("Debug, version: " + version);

		if (version < 1) {
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

			version = 1;

		} if (version < 2) {
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

			version = 2;
		}
		
		if (version < 3) {
			path = "show-native-chat";
			Boolean state = util.IF(config, path);
			config.set(path, null);
			config.set(path + ".cancel-event", state);
			config.set(path + ".clear-recipients", state);

			config.set("storage.type", "yaml");
			config.set("storage.host", "localhost");
			config.set("storage.port", 3306);
			config.set("storage.database", "players");
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
				new StorageYamlUpdater().initYaml();

			} catch (ParseYamlException e) {
				e.printStackTrace();
			}

			version = 3;
		}

		if (version < 4) {
			config.set("check-updates", true);
			version = 4;
		}

		if (version < 5) {
			path = "chat-color-personalized";
			config.set("chat-custom-colors", util.IF(config, path));
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

			version = 5;
		}

		if (version < 6) {
			ArrayList<String> from_messages  = new ArrayList<String>();
				from_messages.add("%ct_expand% &7%ct_messages%");

			ArrayList<String> from_tool_tips = new ArrayList<String>();
				from_tool_tips.add("&f[&6%ct_lang_source%&f] &f<&b%player_name%&f>");

			ArrayList<String> from_sounds    = new ArrayList<String>();
				from_sounds.add("BLOCK_NOTE_BLOCK_BELL; 1; 1");
				from_sounds.add("NOTE_PLING; 1; 1");


			ArrayList<String> to_messages  = new ArrayList<String>();
				to_messages.add("{\"text\": \"&f<&b%player_name%&f> &7$ct_messages$\", \"clickEvent\": {\"action\": \"suggest_command\", \"value\": \"/tell %player_name% ...\"}}");

			ArrayList<String> to_tool_tips = new ArrayList<String>();
				to_tool_tips.add("&7Te han hablado al privado&f! &aHaz click para responder&f!");
				to_tool_tips.add("&f[&6%ct_lang_source%&f] &a%ct_messages%");

			ArrayList<String> to_sounds    = new ArrayList<String>();
				to_sounds.add("BLOCK_NOTE_BLOCK_BELL; 1; 1");
				to_sounds.add("NOTE_PLING; 1; 1");

			ArrayList<String> discord_channels = new ArrayList<String>();


			config.set("formats.from_private.messages", from_messages);
			config.set("formats.from_private.toolTips", from_tool_tips);
			config.set("formats.from_private.sounds", from_sounds);

			config.set("formats.to_private.messages", to_messages);
			config.set("formats.to_private.toolTips", to_tool_tips);
			config.set("formats.to_private.sounds", to_sounds);

			config.set("auto-translate-others.force-permission-per-world", false); // tremendo fail XD
			config.set("auto-translate-others.discord", false);

			config.set("discord.bot-token", "''");
			config.set("discord.channels", discord_channels);
            version = 6;
		}

		if (version < 7) {
			ArrayList<String> void_list = new ArrayList<>();

			ArrayList<String> to_messages  = new ArrayList<>();
			ArrayList<String> to_tool_tips = new ArrayList<>();
				to_messages.add("&f<&b%player_name%&f> &f[&6%ct_lang_source%&f] &a$ct_messages$");

			List<String> discord_channels_chat = config.getStringList("discord.channels");

			config.set("formats.to_discord.messages", to_messages);
			config.set("formats.to_discord.toolTips", to_tool_tips);

			config.set("discord.channels", null);
			config.set("discord.channels.chat", discord_channels_chat);
			config.set("discord.channels.console", new ArrayList<>(void_list));
			config.set("discord.channels.player-access", new ArrayList<>(void_list));

			version = 7;
		}

		config.set(_path, version);
		plugin.config.save();

		if (version > version_original) {
			DC.setMessages(String.format(
				"&eSe ha actualizado la config de la version &b%s &ea la &b%s&f.",
				"" + version_original,
				"" + version
			));
			API.sendMessage(DC);
	    }
	}
}