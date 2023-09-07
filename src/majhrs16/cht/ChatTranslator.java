package majhrs16.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
// import majhrs16.cht.commands.CommandHandler;
import majhrs16.cht.commands.cht.MainCommand;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.events.SignHandler;
import majhrs16.cht.bool.Dependencies;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cot.CoreTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.util.Updater;
import majhrs16.cht.storage.Players;
import majhrs16.cht.storage.SQL;
import majhrs16.cht.events.Chat;
import majhrs16.cht.events.Msg;
import majhrs16.cht.util.util;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class ChatTranslator extends JavaPlugin {
	public SQL mysql;
	public SQL sqlite;
	public YAML signs;
	public YAML config;
	public YAML messages;
	public YAML commands;
	public Players players;

	private static ChatTranslator plugin;

	private boolean is_disabled           = true;
	private PluginDescriptionFile pdffile = getDescription();
	private ChatTranslatorAPI API         = ChatTranslatorAPI.getInstance();

	public final String name       = "&aChat&9Translator";
	public final String version    = "&bb" + pdffile.getVersion().replace(".", "&f.&b");
	public final String sep        = "&c<&4-------------------------&c>";
	public final String title      = "&6<&e[ %name% &e]&6> ".replace("%name%", name);
	public final String title_UTF8 = "\n"
		+ "&a╔═╦╗   ╔╗ &9╔══╗        ╔╗  ╔╗\n"
		+ "&a║╔╣╚╦═╦╣╠╗&9╚╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\n"
		+ "&a║╚╣║╠╝╠╗╔╣&9 ║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\n"
		+ "&a╚═╩╩╩═╝╚═╝&9 ╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝";

	public void onEnable() {
		plugin  = this;

		signs    = new YAML(plugin, "signs.yml");
		config   = new YAML(plugin, "config.yml");
		players  = new Players(plugin, "players.yml");
		messages = new YAML(plugin, "messages.yml");
		commands = new YAML(plugin, "commands.yml");
		sqlite   = new SQL("org.sqlite.JDBC", "sqlite");
		mysql    = new SQL("com.mysql.jdbc.Driver", "mysql");

		commands.register();
		config.register();
		signs.register();
		new Updater().updateConfig();
		if (registerStorage()) {
			onDisable();
			return;
		}
		registerCommands();
		registerEvents();
		new ChatLimiter();

		Message from = util.getDataConfigDefault();

		from.setMessages(sep);
			API.sendMessage(from);

/*
		if (Dependencies.ProtocolLib.exist())
			if (Bukkit.getVersion().contains("1.20")) {
				from.setMessages("&eAdvertencia&f: &cProtocolLib no esta soportado en la 1.20.x&f(&7Hasta la fecha&f: &b12/08/2023&f), &eNo se asegura que funcione&f...");
					API.sendMessage(from);
			}
*/

//		from.setMessages(" "); API.sendMessage(from);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			from.setMessages("&eAdvertencia&f, &cPodria mostrarse feo el titulo si no ha configurado su consola&f(&eAdemas del Java&f)&c en &BUTF&f-&b8&f.");
				API.sendMessage(from);

			Bukkit.getConsoleSender().sendMessage(API.getColor(title_UTF8));

//			from.setMessages(" "); API.sendMessage(from);

		} else {
			from.setMessages("&eAdvertencia&f, &eEs muy recomendable configurar su consola&f(&eAdemas del Java&f)&e en &bUTF&f-&b8&f.");
				API.sendMessage(from);

			from.setMessages(title);
				API.sendMessage(from);
		}

		from.setMessages(String.format("&a	Activado&f, &7Version&f: &b%s&f.", version));
			API.sendMessage(from);

/*
		if (!Dependencies.PAPI.exist()) {
//			from.setMessages(" "); API.sendMessage(from);

			from.setMessages("&c	No esta disponible PlaceholderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas&f.");
				API.sendMessage(from);
		}
*/

		new Updater().updateChecker(Bukkit.getConsoleSender());

//		from.setMessages(" "); API.sendMessage(from);

		from.setMessages(sep);
			API.sendMessage(from);

		setDisabled(false);
	}

	public void onDisable() {
		majhrs16.cht.util.ChatLimiter.chat.clear();

		Message from = util.getDataConfigDefault();

		from.setMessages(sep);
			API.sendMessage(from);

		from.setMessages(title);
			API.sendMessage(from);

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				players.register();
				break;
	
			case "sqlite":
				try {
					sqlite.disconnect();
					from.setMessages("\t&aDesconectado de SQLite&f.");
						API.sendMessage(from);

				} catch (SQLException e) {
					from.setMessages("\t&4Error al desconectar a SQLite&f.");
						API.sendMessage(from);
					return;
				}
				break;
	
			case "mysql":
				try {
					mysql.disconnect();
					from.setMessages("\t&aDesconectado de MySQL&f.");
						API.sendMessage(from);

				} catch (SQLException e) {
					from.setMessages("\t&4Error al desconectar a MySQL&f.");
						API.sendMessage(from);
					return;
				}
				break;
		}

		from.setMessages("\t&cDesactivado&f.");
			API.sendMessage(from);

//		from.setMessages(" "); API.sendMessage(from);

		from.setMessages(sep);
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

	public void registerCommands() {
		/*for (String key : commands.get().getKeys(false)) {
			if (!key.equals("config-version")) {
				Bukkit.getLogger().warning(key);
				getCommand(key).setExecutor(new CommandHandler(key));
			}
		}*/

		MainCommand main_command = new MainCommand(); 
		getCommand("chattranslator").setExecutor(main_command);
		getCommand("cht").setExecutor(main_command);
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new AccessPlayer(), this);
		pm.registerEvents(new Chat(), this);
		pm.registerEvents(new Msg(), this);

//		if (Dependencies.ProtocolLib.exist()) {
			pm.registerEvents(new SignHandler(), this);
//		}

		if (Dependencies.PAPI.exist())
			new CoreTranslator().register(); // Expansion de ChT para PAPI: CoT.
	}

	public boolean registerStorage() {
		Message from = util.getDataConfigDefault();

		String storageType = config.get().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				players.register();
				break;

			case "sqlite":
				sqlite.set(null, 0, config.get().getString("storage.database"), null, null);
				try {
					sqlite.connect();
					sqlite.createTable();

					from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));
					from.setMessages(title + "&aConectado a SQLite&f.");
						API.sendMessage(from);

				} catch (SQLException e) {
					from.setMessages(title + "&4Error al conectar a SQLite&f.");
						API.sendMessage(from);
					return true;
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
					from.setMessages(title + "&aConectado a MySQL&f.");
						API.sendMessage(from);

				} catch (SQLException e) {
					e.printStackTrace();
					from.setMessages(title + "&4Error al conectar a MySQL&f.");
						API.sendMessage(from);
					return true;
				}
				break;

			default:
				from.setMessages(title + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					API.sendMessage(from);
		}
		
		return false;
	}
}