package majhrs16.cht;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import majhrs16.cht.commands.cht.MainCommand;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.storage.data.SQLite;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.storage.data.MySQL;
import majhrs16.cht.events.SignHandler;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cot.CoreTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.util.Updater;
import majhrs16.cht.events.Chat;
import majhrs16.cht.events.Msg;
import majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class ChatTranslator extends JavaPlugin {
	private API API;
	private YAML signs;
	private MySQL mysql;
	private YAML config;
	private YAML players;
	private SQLite sqlite;
	public Boolean enabled;
	public static ChatTranslator plugin;
	PluginDescriptionFile pdffile     = getDescription();
	public String name                = "&aChat&9Translator";
	public String version             = "b" + pdffile.getVersion();
	public String sep                 = "&4<------------------------->";
	public String title               = "&6<&e[ %name% &e]&6> ".replace("%name%", name);
	public String title_UTF8          = "\n"
		+ "&a╔═╦╗   ╔╗ &9╔══╗        ╔╗  ╔╗\n"
		+ "&a║╔╣╚╦═╦╣╠╗&9╚╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\n"
		+ "&a║╚╣║╠╝╠╗╔╣&9 ║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\n"
		+ "&a╚═╩╩╩═╝╚═╝&9 ╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝";

	public void onEnable() {
		plugin  = this;
		API     = new API();

		signs   = new YAML(plugin, "signs.yml");
		config  = new YAML(plugin, "config.yml");
		players = new YAML(plugin, "players.yml");
		sqlite  = new SQLite();
		mysql   = new MySQL();

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

		CommandSender console = Bukkit.getConsoleSender();
		Message DC = util.getDataConfigDefault();
			DC.getTo().setSender(console);
			DC.getTo().setLang(API.getLang(console));

		DC.getTo().setMessages(sep);
			API.sendMessage(DC);

//		DC.getTo().setMessages(" "); API.sendMessage(DC);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			DC.getTo().setMessages("&eAdvertencia&f, &cPodria mostrarse feo el titulo si no ha configurado su consola&f(&eAdemas del Java&f)&c en UTF&f-&c8&f.");
				API.sendMessage(DC);

			console.sendMessage(API.getColor(title_UTF8));

//			DC.getTo().setMessages(" "); API.sendMessage(DC);

		} else {
			DC.getTo().setMessages("&eAdvertencia&f, &eEs muy recomendable configurar su consola&f(&eAdemas del Java&f)&c en UTF&f-&c8&f.");
				API.sendMessage(DC);

			DC.getTo().setMessages(title);
				API.sendMessage(DC);
		}

		DC.getTo().setMessages(String.format("&a	Activado&f, &7Version&f: &b%s&f.", version));
			API.sendMessage(DC);

		if (!util.checkPAPI()) {
//			DC.getTo().setMessages(" "); API.sendMessage(DC);

			DC.getTo().setMessages("&c	No esta disponible PlaceholderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas&f.");
				API.sendMessage(DC);
		}

		new Updater().updateChecker();

//		DC.getTo().setMessages(" "); API.sendMessage(DC);

		DC.getTo().setMessages(sep);
			API.sendMessage(DC);

		enabled = true;
	}

	public void onDisable() {
		majhrs16.cht.util.ChatLimiter.chat.clear();

		Message DC = util.getDataConfigDefault();
			DC.getTo().setSender(Bukkit.getConsoleSender());
			DC.getTo().setLang(API.getLang(Bukkit.getConsoleSender()));

		DC.getTo().setMessages(sep);
			API.sendMessage(DC);

//		DC.getTo().setMessages(" "); API.sendMessage(DC);

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				players.register();
				break;
	
			case "sqlite":
				try {
					getMySQL().disconnect();
					DC.getTo().setMessages(title + "&aDesconectado de SQLite&f.");
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.getTo().setMessages(title + "&4Error al desconectar a SQLite&f.");
						API.sendMessage(DC);
					return;
				}
				break;
	
			case "mysql":
				try {
					getMySQL().disconnect();
					DC.getTo().setMessages(title + "&aDesconectado de MySQL&f.");
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.getTo().setMessages(title + "&4Error al desconectar a MySQL&f.");
						API.sendMessage(DC);
					return;
				}
				break;
		}

		DC.getTo().setMessages(title + "&cDesactivado&f.");
			API.sendMessage(DC);

//		DC.getTo().setMessages(" "); API.sendMessage(DC);

		DC.getTo().setMessages(sep);
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
		pe.registerEvents(new AccessPlayer(), this);
		pe.registerEvents(new Chat(), this);
		pe.registerEvents(new Msg(), this);

		if (util.checkPAPI())
			new CoreTranslator().register(); // Expansion de ChT para PAPI: CoT.

		if (util.checkPL()) {
			if (Bukkit.getVersion().contains("1.20")) {
				CommandSender console = Bukkit.getConsoleSender();
				Message DC = util.getDataConfigDefault();
					DC.getTo().setSender(console);
					DC.getTo().setLang(API.getLang(console));

				DC.getTo().setMessages("&eAdvertencia&f: &cProtocolLib no esta soportado en la 1.20.x&f(&7Hasta la fecha&f: &b12/08/2023&f), &eNo se asegura que funcione&f...");
					API.sendMessage(DC);
			}

			SignHandler sh = new SignHandler();
			pe.registerEvents(sh, this);
			sh.SignUpdater();
		}
	}

	public boolean registerStorage() {
		CommandSender console = Bukkit.getConsoleSender();

		Message DC = util.getDataConfigDefault();
			DC.getTo().setSender(console);
			DC.getTo().setLang(API.getLang(console));

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

					DC.getTo().setMessages(title + "&aConectado a SQLite&f.");
					DC.getTo().setLang(API.getLang(console));
						API.sendMessage(DC);

				} catch (SQLException e) {
					DC.getTo().setMessages(title + "&4Error al conectar a SQLite&f.");
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

					DC.getTo().setMessages(title + "&aConectado a MySQL&f.");
					DC.getTo().setLang(API.getLang(console));
						API.sendMessage(DC);

				} catch (SQLException e) {
					e.printStackTrace();
					DC.getTo().setMessages(title + "&4Error al conectar a MySQL&f.");
						API.sendMessage(DC);
					return true;
				}
				break;

			default:
				DC.getTo().setMessages(title + "&f[&4ERR000&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					API.sendMessage(DC);
		}
		
		return false;
	}

	public MySQL getMySQL() { return mysql; }
	public SQLite getSQLite() { return sqlite; }

	public FileConfiguration getConfig() { return config.get(); }
	public void reloadConfig() { config.reload(); }
	public void resetConfig() { config.reset(); }
	public void saveConfig() { config.save(); }

	public FileConfiguration getSigns() { return signs.get(); }
	public void reloadSigns() { signs.reload(); }
	public void resetSigns() { signs.reset(); }
	public void saveSigns() { signs.save(); }

	public FileConfiguration getPlayers() { return players.get(); }
	public void reloadPlayers() throws SQLException {
		enabled = false;

		String storageType = config.get().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				players.reload();
				break;

			case "sqlite":
				sqlite.set(null, 0, config.get().getString("storage.database"), null, null);
				sqlite.disconnect();
				registerStorage();
				break;

			case "mysql":
				mysql.set(
					config.get().getString("storage.host"),
					config.get().getInt("storage.port"),
					config.get().getString("storage.database"),
					config.get().getString("storage.user"),
					config.get().getString("storage.password")
				);
				mysql.disconnect();
				registerStorage();
				break;

			default:
				CommandSender console = Bukkit.getConsoleSender();

				Message DC = util.getDataConfigDefault();
					DC.getTo().setSender(console);
					DC.getTo().setMessages(title + "&f[&4ERR000&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					DC.getTo().setLang(API.getLang(console));
				API.sendMessage(DC);
		}

		enabled = true;
	}

	public void savePlayers() {
		if (config.get().getString("storage.type").toLowerCase().equals("yaml")) {
			players.save();
		}
	}
}