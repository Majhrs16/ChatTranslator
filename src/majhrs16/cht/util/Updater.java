package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.scanner.ScannerException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;

import majhrs16.cht.bool.Dependencies;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.bool.Config;

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

	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public void updateChecker(CommandSender to_sender) {
		if (!Config.CHECK_UPDATES.IF())
			return;

		Message DC = util.getDataConfigDefault();
			DC.setSender(to_sender);
			DC.setLangTarget(API.getLang(to_sender));

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out = 3000;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestVersion.length() <= 7) {
				latestVersion = "&b" + latestVersion.replace(".", "&f.&b");

				if (plugin.version.equals(latestVersion)) {
					DC.setMessages("&a	Estas usando la última versión del plugin <3");
						API.sendMessage(DC);

				} else {
					if (to_sender instanceof Player) {
						if (util.getMinecraftVersion() < 1.8)
							return;

						Player player = (Player) to_sender;

						DC.setMessages("&9link");
						DC.setToolTips("&7Descargar " + plugin.name + " &b" + latestVersion);

						TextComponent linkText = new TextComponent(API.formatMessage(DC).getMessages());
							ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/chattranslator.106604/");
								linkText.setClickEvent(clickEvent);

							@SuppressWarnings("deprecation")
							HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(API.formatMessage(DC).getToolTips()).create());
								linkText.setHoverEvent(hoverEvent);

						TextComponent message = new TextComponent("    ");
							DC.setMessages("&aPuedes descargarla en este");
							DC.setToolTips("&f!");

							message.addExtra(API.formatMessage(DC).getMessages() + " ");
							message.addExtra(linkText);
							message.addExtra(API.formatMessage(DC).getToolTips());

						TextComponent versionMessage = new TextComponent();
							DC.setMessages(String.format("&eHay una nueva versión disponible&f! &f(&B%s&f)", latestVersion));
							DC.setToolTips(null);

							versionMessage.setText(API.formatMessage(DC).getMessages());

						player.spigot().sendMessage(versionMessage);
						player.spigot().sendMessage(message);

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

	/////////////////////////////////////////////////////////
	public void updateConfig() {
		String path, tmp;
		Boolean cancel_event, clear_recipients;
		FileConfiguration config = plugin.getConfig();

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

			config_version = 4;
		}

		path = "auto-update-config";
		if (config.contains(path) && !util.IF(config, path)) { // Solo por nostalgia lo dejare asi :,3
			config.set(_path, config_version);
			plugin.saveConfig();
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

			upgradePlayers();

			config_version = 3;
		}

		if (config_version < 4) {
			config.set("check-updates", true);
			config_version = 4;
		}

		config.set(_path, config_version);
		plugin.saveConfig();

		if (config_version > config_version_original) {
			DC.setMessages(String.format(
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
				throw new IllegalArgumentException("[ERR021]");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}