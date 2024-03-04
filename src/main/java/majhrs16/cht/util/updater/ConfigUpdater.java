package majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;

import majhrs16.lib.exceptions.ParseYamlException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class ConfigUpdater {
	public int version;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	private final Consumer[] applyConfigVersions = new Consumer[] {
		this::applyConfigVersion1,
		this::applyConfigVersion2,
		this::applyConfigVersion3,
		this::applyConfigVersion4,
		this::applyConfigVersion5,
		this::applyConfigVersion6,
		this::applyConfigVersion7,
		this::applyConfigVersion8, // v1.7.11
		this::applyConfigVersion9  // 1.8
	};

	@FunctionalInterface
	private interface Consumer {
		void accept(FileConfiguration cfg, Message msg);
	}

	public ConfigUpdater() {
		FileConfiguration config = plugin.config.get();

		String path = "config-version";
		if (!config.contains(path))
			config.set(path, -1);

		version = config.getInt(path);
		int version_original = version;

		if (version_original == 0)   // Inicializar el plugin por primera vez.
			config.set("server-uuid", UUID.randomUUID().toString()); // Para evitar crashes.

		Message DC = util.getDataConfigDefault();

		if (version_original == 0) { // Inicializar el plugin por primera vez.
			applyConfigVersion0(config, DC);
			version = applyConfigVersions.length; // "Actualizar" a la ultima version disponible en el codigo.
		}

		if (!Config.UPDATE_CONFIG.IF()) {
			config.set(path, version);
			plugin.config.save();
			return;
		}

		if (Config.DEBUG.IF())
			System.out.println("Debug, config.version: " + version_original);

		for (int i = Math.max(0, version); i < applyConfigVersions.length; i++) { // Actualizar gradualmente por el historial de versiones.
			applyConfigVersions[i].accept(config, DC);
			version = i + 1;
		}

		config.set(path, version);
		plugin.config.save();

		if (version > version_original) {
			DC.getMessages().setTexts(String.format(
				"&eSe ha actualizado la config de la version &b%s &ea la &b%s&f.",
				version_original, version
			));

			API.sendMessage(DC);
	    }
	}

	public void applyConfigVersion0(FileConfiguration config, Message DC) {
		boolean cancel_event, clear_recipients;

		if (Dependencies.Chatty.exist()) {
			DC.getMessages().setTexts("&aDetectado Chatty&f.");
			API.sendMessage(DC);

			cancel_event     = false;
			clear_recipients = false;

		} else if (Dependencies.ChatManager.exist()) {
			DC.getMessages().setTexts("&aDetectado ChatManager&f.");
			API.sendMessage(DC);

			cancel_event     = false;
			clear_recipients = false;

		} else {
			cancel_event     = true;
			clear_recipients = false;
		}

		config.set(Config.NativeChat.CANCEL.getPath(), cancel_event);
		config.set(Config.NativeChat.CLEAR.getPath(), clear_recipients);
	}

	public void applyConfigVersion1(FileConfiguration config, Message DC) {
		String path, tmp;

		ArrayList<String> formats_from_messages  = new ArrayList<>();
		ArrayList<String> formats_from_tool_tips = new ArrayList<>();
		ArrayList<String> formats_from_sounds    = new ArrayList<>();
		ArrayList<String> formats_to_messages    = new ArrayList<>();
		ArrayList<String> formats_to_toolTips    = new ArrayList<>();
		ArrayList<String> formats_to_sounds      = new ArrayList<>();

		String formatMsg = config.getString("message-format");
		assert formatMsg != null;
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
	}

	public void applyConfigVersion2(FileConfiguration config, Message DC) {
		boolean show_native_chat;

		if (Dependencies.Chatty.exist()) {
			DC.getMessages().setTexts("&aDetectado Chatty&f.");
			API.sendMessage(DC);

			show_native_chat = true;

		} else if (Dependencies.ChatManager.exist()) {
			DC.getMessages().setTexts("&aDetectado ChatManager&f.");
			API.sendMessage(DC);

			show_native_chat = true;

		} else
			show_native_chat = false;
		config.set("show-native-chat", show_native_chat);

		config.set("max-spam-per-tick", 150.0007);
	}

	public void applyConfigVersion3(FileConfiguration config, Message DC) {
		String path;

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

		ArrayList<String> formats_from_messages = new ArrayList<>();
		ArrayList<String> formats_to_messages   = new ArrayList<>();
		ArrayList<String> formats_to_console_messages = new ArrayList<>();
		ArrayList<String> formats_to_console_toolTips = new ArrayList<>();
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
	}

	public void applyConfigVersion4(FileConfiguration config, Message DC) {
		config.set("check-updates", true);
	}

	public void applyConfigVersion5(FileConfiguration config, Message DC) {
		String path;

		path = "chat-color-personalized";
		config.set("chat-custom-colors", util.IF(config, path));
		config.set(path, null);

		path = "formats.from_entry.messages";
		if (config.contains(path)) {
			List<String> from_entry = config.getStringList(path);
			from_entry.replaceAll(s -> s.replace("$ct_messages$", "%ct_messages%"));
			config.set(path, from_entry);
		}

		path = "formats.from_exit.messages";
		if (config.contains(path)) {
			List<String> from_exit = config.getStringList(path);
			from_exit.replaceAll(s -> s.replace("$ct_messages$", "%ct_messages%"));
			config.set(path, from_exit);
		}
	}
	public void applyConfigVersion6(FileConfiguration config, Message DC) {
		ArrayList<String> from_messages  = new ArrayList<String>();
		from_messages.add("%ct_expand% &7%ct_messages%");

		ArrayList<String> from_tool_tips = new ArrayList<String>();
		from_tool_tips.add("&f[&6%ct_lang_source%&f] &f<&b%player_name%&f>");

		ArrayList<String> from_sounds    = new ArrayList<String>();
		from_sounds.add("BLOCK_NOTE_BLOCK_BELL; 1; 1");
		from_sounds.add("NOTE_PLING; 1; 1");


		ArrayList<String> to_messages  = new ArrayList<String>();
		to_messages.add("{\"text\": \"&f<&b%player_name%&f> &7$ct_messages$\", \"clickEvent\": {\"action\": \"suggest_command\", \"value\": \"/tell %player_name% \"}}");

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
	}

	public void applyConfigVersion7(FileConfiguration config, Message DC) {
		ArrayList<String> to_discord_messages  = new ArrayList<>();
		to_discord_messages.add("&f<&b%player_name%&f> &f[&6%ct_lang_source%&f] &a$ct_messages$");
		ArrayList<String> to_discord_tool_tips = new ArrayList<>();

		List<String> discord_channels_chat = config.getStringList("discord.channels");

		config.set("formats.to_discord.messages", to_discord_messages);
		config.set("formats.to_discord.toolTips", to_discord_tool_tips);

		config.set("discord.channels", null);
		config.set("discord.channels.chat", discord_channels_chat);
		config.set("discord.channels.console", new ArrayList<>());
		config.set("discord.channels.player-access", new ArrayList<>());


		DC.getMessages().setTexts(Texts.getString("plugin.title.text") +
			" &eADVERTENCIA&f: &6Apartir de ahora&f, &cNO &6se maneja los colores del chat por 1 configuracion&f. &aSino ahora por permisos&f.",
			"\t&aPor favor mire la &9Wiki&f.");
		API.sendMessage(DC);

		config.set("chat-custom-colors", null);

		ArrayList<String> from_mention_messages  = new ArrayList<>();
		from_mention_messages.add("%ct_expand% &a%ct_messages%");
		ArrayList<String> from_mention_tool_tips = new ArrayList<>();
		from_mention_tool_tips.add("&f[&6%ct_lang_source%&f] &f<&7%player_name%&f>");

		ArrayList<String> to_mention_messages  = new ArrayList<>();
		to_mention_messages.add("{\"text\": \"&f<&6%player_name%&f> &e$ct_messages$\", \"clickEvent\": {\"action\": \"suggest_command\", \"value\": \"@%player_name% \"}}");
		ArrayList<String> to_mention_tool_tips = new ArrayList<>();
		to_mention_tool_tips.add("&eTe han mencionado&f! &aHaz click para responder&f!");
		to_mention_tool_tips.add("&f[&6%ct_lang_source%&f] &e%ct_messages%");
		ArrayList<String> to_mention_sounds = new ArrayList<>();
		to_mention_sounds.add("ENTITY_EXPERIENCE_ORB_PICKUP; 1; 1");
		to_mention_sounds.add("ORB_PICKUP; 1; 1");

		config.set("formats.from_mention.messages", from_mention_messages);
		config.set("formats.from_mention.toolTips", from_mention_tool_tips);

		config.set("formats.to_mention.messages", to_mention_messages);
		config.set("formats.to_mention.toolTips", to_mention_tool_tips);
		config.set("formats.to_mention.sounds", to_mention_sounds);
	}

	public void applyConfigVersion8(FileConfiguration config, Message DC) {
		String path;

		ArrayList<String> death_messages = new ArrayList<>();
			death_messages.add("&c$ct_messages$");

		config.set("formats.from_death.messages", new ArrayList<>(death_messages));

		config.set("formats.to_death.messages", new ArrayList<>(death_messages));
		config.set("formats.to_death_discord.messages", new ArrayList<>(death_messages));
		config.set("formats.to_death_console.messages", new ArrayList<>(death_messages));

		config.set("discord.channels.deaths", new ArrayList<>());


		ArrayList<String> entry_messages = new ArrayList<>();
			entry_messages.add("&a+ &e%player_name% $ct_messages$");

		ArrayList<String> exit_messages  = new ArrayList<>();
			exit_messages.add("&c- &e%player_name% $ct_messages$");

		config.set("formats.to_entry_discord.messages", new ArrayList<>(entry_messages));
		config.set("formats.to_entry_cosnole.messages", new ArrayList<>(entry_messages));

		config.set("formats.to_exit_discord.messages", new ArrayList<>(exit_messages));
		config.set("formats.to_exit_console.messages", new ArrayList<>(exit_messages));


		path = "auto-translate-others.signs";
		boolean signs_enabled = config.getBoolean(path);
		config.set(path, null);

		config.set(path + ".enable", signs_enabled);
		config.set(path + ".wrapText", false);
	}

	public void applyConfigVersion9(FileConfiguration config, Message DC) {
		// FALTA MUDAR LA CONFIG.yml VIEJA A FORMATS.yml!!
	}
}