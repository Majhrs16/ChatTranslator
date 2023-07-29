package majhrs16.ct;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import majhrs16.ct.commands.cht.MainCommand;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.storage.data.SQLite;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.storage.data.MySQL;
import majhrs16.ct.util.ChatLimiter;
import majhrs16.lib.storages.YAML;
import majhrs16.ct.util.Updater;
import majhrs16.ct.events.Chat;
import majhrs16.ct.events.Msg;
import majhrs16.ct.util.util;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class ChatTranslator extends JavaPlugin {
	private API API;
	private MySQL mysql;
	private YAML config;
	private YAML players;
	private SQLite sqlite;
	public Boolean enabled;
	public static ChatTranslator plugin;
	PluginDescriptionFile pdffile     = getDescription();
	public String version             = "v" + pdffile.getVersion();
	public String name                = "&aChat&9Translator";
	public String title               = "&6<&e[ %name% &e]&6> ".replace("%name%", name);
	public String title_UTF8          = ""
		+ "&a╔═╦╗   ╔╗ &9╔══╗        ╔╗  ╔╗\r\n"
		+ "&a║╔╣╚╦═╦╣╠╗&9╚╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\r\n"
		+ "&a║╚╣║╠╝╠╗╔╣&9 ║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\r\n"
		+ "&a╚═╩╩╩═╝╚═╝&9 ╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝";

	public void onEnable() {
		plugin  = this;
		API     = new API();

		config  = new YAML(plugin, "config.yml");
		players = new YAML(plugin, "players.yml");
		sqlite  = new SQLite();
		mysql   = new MySQL();

		config.register();
		new Updater().updateConfig();
		if (registerStorage()) {
			onDisable();
			return;
		}
		registerCommands();
		registerEvents();
		new ChatLimiter();

		CommandSender console = Bukkit.getConsoleSender();
		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

		DC.setMessages("&4<------------------------->");
			API.sendMessage(DC);

		DC.setMessages("	");
			API.sendMessage(DC);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			DC.setMessages("&eAdvertencia&f, &cPodria mostrarse feo el titulo si no ha configurado su consola&f(&eAdemas del Java&f) &cen UTF&f-&c8&f.");
				API.sendMessage(DC);

			DC.setMessages(title_UTF8);
			DC.setMessageFormat("\r\n$ct_messages$");
				API.sendMessage(DC);

			DC.setMessageFormat("$ct_messages$");

			DC.setMessages("	");
				API.sendMessage(DC);

		} else {
			DC.setMessages(title);
				API.sendMessage(DC);
		}

		DC.setMessages(String.format("&a	Activado&f, &7Version&f: &b%s&f.", version));
			API.sendMessage(DC);

		if (!util.checkPAPI()) {
			DC.setMessages("	");
				API.sendMessage(DC);

			DC.setMessages("&c	No esta disponible PlaceholderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas&f.");
				API.sendMessage(DC);
		}

		new Updater().updateChecker();

		DC.setMessages("	");
			API.sendMessage(DC);

		DC.setMessages("&4<------------------------->");
			API.sendMessage(DC);

		enabled = true;
	}

	public void onDisable() {
		majhrs16.ct.util.ChatLimiter.chat.clear();

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(Bukkit.getConsoleSender());
			DC.setLang(API.getLang(Bukkit.getConsoleSender()));

		DC.setMessages("&4<------------------------->");
			API.sendMessage(DC);

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				players.register();
				break;
	
			case "sqlite":
				try {
					getMySQL().disconnect();
					DC.setMessages(title + "&aDesconectado de SQLite&f.");
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al desconectar a SQLite&f.");
						API.sendMessage(DC);
					return;
				}
				break;
	
			case "mysql":
				try {
					getMySQL().disconnect();
					DC.setMessages(title + "&aDesconectado de MySQL&f.");
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al desconectar a MySQL&f.");
						API.sendMessage(DC);
					return;
				}
				break;
		}

		DC.setMessages(title + "&cDesactivado&f.");
			API.sendMessage(DC);

		DC.setMessages("&4<------------------------->");
			API.sendMessage(DC);

		enabled = false;
	}

	public void registerCommands() {
		MainCommand main_command = new MainCommand();
		this.getCommand("chattranslator").setExecutor(main_command);
		this.getCommand("cht").setExecutor(main_command);
	}

	public void registerEvents() {
		PluginManager pe = getServer().getPluginManager();
		pe.registerEvents(new Chat(this), this);
		pe.registerEvents(new Msg(), this);
	}
	
	public boolean registerStorage() {
		CommandSender console = Bukkit.getConsoleSender();

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

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
					
					DC.setMessages(title + "&aConectado a SQLite&f.");
					DC.setLang(API.getLang(console));
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al conectar a SQLite&f.");
						API.sendMessage(DC);
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

					DC.setMessages(title + "&aConectado a MySQL&f.");
					DC.setLang(API.getLang(console));
						API.sendMessage(DC);

				} catch (SQLException e) {
					e.printStackTrace();
					DC.setMessages(title + "&4Error al conectar a MySQL&f.");
						API.sendMessage(DC);
					return true;
				}
				break;
			
			default:
				DC.setMessages(title + "&4Error&f, &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					API.sendMessage(DC);
		}
		
		return false;
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public SQLite getSQLite() {
		return sqlite;
	}

	public FileConfiguration getConfig() { return config.get(); }
	public void reloadConfig() { config.reload(); }
	public void resetConfig() { config.reset(); }
	public void saveConfig() { config.save(); }

	public FileConfiguration getPlayers() { return players.get(); }

	public void reloadPlayers() {
		enabled = false;

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				players.reload();
				break;

			case "sqlite":
				sqlite.set(null, 0, config.get().getString("storage.database"), null, null);
				try {
					sqlite.disconnect();
					registerStorage();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
					mysql.disconnect();
					registerStorage();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
		}

		enabled = true;
	}

	public void savePlayers() {
		if (config.get().getString("storage.type").toLowerCase().equals("yaml")) {
			players.save();
		}
	}
}
