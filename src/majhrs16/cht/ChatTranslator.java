package majhrs16.cht;

import majhrs16.lib.storages.YAML.ParseYamlException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.commands.CommandWrapper;
import majhrs16.cht.events.CommandListener;
import majhrs16.cht.events.MessageListener;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.events.SignHandler;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cht.storage.Players;
import majhrs16.cot.CoreTranslator;
import majhrs16.cht.storage.SQLite;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.storage.MySQL;
import majhrs16.cht.util.Updater;
import majhrs16.cht.events.Chat;
import majhrs16.cht.util.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class ChatTranslator extends JavaPlugin {
	public YAML signs;
	public YAML config;
	public MySQL mysql;
	public SQLite sqlite;
	public YAML messages;
	public YAML commands;
	public Players players;

	private static ChatTranslator plugin;

	private boolean is_disabled = true;
	private ChatTranslatorAPI API;

	private static class Events {
		public static boolean installed = false;

		public static CommandListener commandHandler  = new CommandListener();
		public static MessageListener nessageListener = new MessageListener();
//		public static TabCompleter tabCompleter       = new TabCompleter();
		public static AccessPlayer accessPlayer       = new AccessPlayer();
		public static SignHandler signHandler         = new SignHandler();
		public static ChatLimiter chatLimiter         = new ChatLimiter();
		public static Chat chat                       = new Chat();
	}

	public void onEnable() {
		plugin  = this;

		if (util.getMinecraftVersion() < 5.2) {
			Bukkit.getLogger().warning("[ERRFFF] ChatTranslator no compatible.");
			return;
		}

		API = ChatTranslatorAPI.getInstance();

		signs    = new YAML(plugin, "signs.yml");
		config   = new YAML(plugin, "config.yml");
		messages = new YAML(plugin, "messages.yml");
		commands = new YAML(plugin, "commands.yml");
		players  = new Players(plugin, "players.yml");
		sqlite   = new SQLite();
		mysql    = new MySQL();

		try {
			messages.register();
			commands.register();
			config.register();
			signs.register();

		} catch (ParseYamlException e) {
			Bukkit.getLogger().warning("[ERRFFF] FATAL");
			return;
		}

		Updater updater = new Updater();
		updater.updateConfig();
		Texts.reload();

		try {
			registerPlayers();

		} catch (SQLException | ParseYamlException e) {
			Message from = util.getDataConfigDefault();
			from.setMessages(e.getMessage());
				API.sendMessage(from);

			onDisable();
			return;
		}

		Message from = util.getDataConfigDefault();

//		registerCommands();
		registerEvents();

		from.setMessages(Texts.SEPARATOR);
			API.sendMessage(from);

		from.setMessages("	"); API.sendMessage(from);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			from.setMessages(Texts.PLUGIN.IS_UTF_8.YES);
				API.sendMessage(from);

			from.setMessages(Texts.PLUGIN.TITLE.UTF_8);
				API.sendMessage(from);

			from.setMessages("	"); API.sendMessage(from);

		} else {
			from.setMessages(Texts.PLUGIN.IS_UTF_8.NO);
				API.sendMessage(from);

			from.setMessages(Texts.PLUGIN.TITLE.TEXT);
				API.sendMessage(from);
		}

		from.setMessages(Texts.PLUGIN.ON);
			API.sendMessage(from);

		if (Config.CHECK_UPDATES.IF())
			updater.checkUpdate(Bukkit.getConsoleSender());

		from.setMessages("	"); API.sendMessage(from);

		from.setMessages(Texts.SEPARATOR);
			API.sendMessage(from);

		setDisabled(false);
	}

	public void onDisable() {
		if (isDisabled())
			return;

		majhrs16.cht.util.ChatLimiter.chat.clear();

		Message from = util.getDataConfigDefault();

		from.setMessages(Texts.SEPARATOR);
			API.sendMessage(from);

		from.setMessages(Texts.PLUGIN.TITLE.TEXT);
			API.sendMessage(from);

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				try {
					players.save();
					from.setMessages(Texts.STORAGE.CLOSE.YAML.OK);
						API.sendMessage(from);

				} catch (IllegalArgumentException e) {
					from.setMessages(Texts.STORAGE.CLOSE.YAML.ERROR);
						API.sendMessage(from);
					return;
				}
				break;

			case "sqlite":
				try {
					sqlite.disconnect();
					from.setMessages(Texts.STORAGE.CLOSE.SQLITE.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					from.setMessages(Texts.STORAGE.CLOSE.SQLITE.ERROR);
						API.sendMessage(from);
					return;
				}
				break;

			case "mysql":
				try {
					mysql.disconnect();
					from.setMessages(Texts.STORAGE.CLOSE.MYSQL.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					from.setMessages(Texts.STORAGE.CLOSE.MYSQL.ERROR);
						API.sendMessage(from);
					return;
				}
				break;
		}

		from.setMessages(Texts.PLUGIN.OFF);
			API.sendMessage(from);

//		from.setMessages(" "); API.sendMessage(from);

		from.setMessages(Texts.SEPARATOR);
			API.sendMessage(from);

		setDisabled(true);
	}

	public static ChatTranslator getInstance() {
		return plugin;
	}

	public boolean isDisabled() {
		return is_disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.is_disabled = isDisabled;
	}

	public void registerCommands() { // Sino se registran los comandos en el plugin,yml, no te dejara. Asi de simple.
		for (String key : commands.get().getKeys(false)) {
			if (!key.equals("config-version")) {
				PluginCommand pc = getCommand(key);

				if (pc == null) {
					Message from = util.getDataConfigDefault();

					from.setMessages("&cNo fue posible registrar el comando raiz&f: &b`" + key + "`");
						API.sendMessage(from);

				} else {
					CommandWrapper ch = new CommandWrapper(key);
					pc.setExecutor(ch);
				}
			}
		}

		/*
		MainCommand main_command = new MainCommand(); 
		getCommand("chattranslator").setExecutor(main_command);
		getCommand("cht").setExecutor(main_command);
		*/
	}

	public void registerEvents() {
		if (Events.installed)
			return;

		PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(Events.nessageListener, this);
			pm.registerEvents(Events.commandHandler, this);
//			if (util.getMinecraftVersion() > 13.0) // 1.13.0 pm.registerEvents(Events.tabCompleter, this);
			pm.registerEvents(Events.accessPlayer, this);
			pm.registerEvents(Events.signHandler, this);
			pm.registerEvents(Events.chat, this);
			Events.chatLimiter.start();

		if (Dependencies.PAPI.exist())
			new CoreTranslator().register(); // Expansion de ChT para PAPI: CoT.

		Events.installed = true;
	}

	public void registerPlayers() throws SQLException, ParseYamlException {
		Message from = util.getDataConfigDefault();
		String storageType = config.get().getString("storage.type").toLowerCase();

		switch (storageType) {
			case "yaml":
				try {
					players.register();
					from.setMessages(Texts.STORAGE.OPEN.YAML.OK);
						API.sendMessage(from);

				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(Texts.STORAGE.OPEN.YAML.ERROR + "\n\t" + e.toString());
				}
				break;

			case "sqlite":
				sqlite.set(null, 0, config.get().getString("storage.database"), null, null);
				try {
					sqlite.connect();
					sqlite.createTable();

					from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));
					from.setMessages(Texts.STORAGE.OPEN.SQLITE.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					throw new IllegalArgumentException(Texts.STORAGE.OPEN.SQLITE.ERROR + "\n\t" + e.toString());
				}
				break;

			case "mysql":
				mysql.set(
					config.get().getString("storage.host"),
					config.get().getInt("storage.port"),
					config.get().getString("storage.database"),
					config.get().getString("storage.user"),
					config.get().getString("storage.password")
				);
				try {
					mysql.connect();
					mysql.createTable();

					from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));
					from.setMessages(Texts.STORAGE.OPEN.MYSQL.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					throw new IllegalArgumentException(Texts.STORAGE.OPEN.MYSQL.ERROR + "\n\t" + e.toString());
				}
				break;

			default:
				from.setMessages(Texts.PLUGIN.TITLE.TEXT + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					API.sendMessage(from);
		}
	}
}