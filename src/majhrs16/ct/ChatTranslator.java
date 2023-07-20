package majhrs16.ct;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.storage.config.Config;
import majhrs16.ct.storage.Configuration;
import majhrs16.ct.storage.data.SQLite;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.storage.data.MySQL;
import majhrs16.ct.storage.data.YAML;
import majhrs16.ct.util.Updater;
import majhrs16.ct.util.ChatLimiter;
import majhrs16.ct.commands.CT;
import majhrs16.ct.events.Chat;
import majhrs16.ct.events.Msg;
import majhrs16.ct.util.util;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.net.URL;

public class ChatTranslator extends JavaPlugin {
	private MySQL mysql;
	private SQLite sqlite;
	public Boolean enabled;
	private Configuration config;
	private Configuration players;
	public static ChatTranslator plugin;
	PluginDescriptionFile pdffile     = getDescription();
	public String version             = pdffile.getVersion();
	public String name                = "&aChat&9Translator";
	public String title               = "&6<&e[ %name% &e]&6> ".replace("%name%", name);
	public String title_UTF8          = "\r\n"
		+ "&a╔═╦╗   ╔╗ &9╔══╗        ╔╗  ╔╗\r\n"
		+ "&a║╔╣╚╦═╦╣╠╗&9╚╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\r\n"
		+ "&a║╚╣║╠╝╠╗╔╣&9 ║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\r\n"
		+ "&a╚═╩╩╩═╝╚═╝&9 ╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝";

	public void onEnable() {
		plugin = this;
		config  = new Config();
		players = new YAML();
		sqlite  = new SQLite();
		mysql   = new MySQL();

		config.register();
		new Updater().update();
		if (registerStorage()) {
			onDisable();
			return;
		}
		registerCommands();
		registerEvents();
		new ChatLimiter();

		API API = new API();
		CommandSender console = Bukkit.getConsoleSender();

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		DC.setMessages("	");
			util.processMsgFromDC(DC);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			DC.setMessages("&eAdvertencia&f, &cPodria mostrarse feo el titulo si no ha configurado su consola&f(&eAdemas del Java&f) &cen UTF&f-&c8&f.");
				util.processMsgFromDC(DC);

			DC.setMessageFormat("%ct_messages%");
			DC.setMessages(title_UTF8);
				util.processMsgFromDC(DC);
			DC.setMessageFormat("$ct_messages$");

			DC.setMessages("	");
				util.processMsgFromDC(DC);

		} else {
			DC.setMessages(title);
				util.processMsgFromDC(DC);
		}

		DC.setMessages(String.format("&a	Activado&f, &7Version&f: &b%s&f.", version));
			util.processMsgFromDC(DC);

		if (!util.checkPAPI()) {
			DC.setMessages("	");
				util.processMsgFromDC(DC);

			DC.setMessages("&c	No esta disponible PlaceholderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas&f.");
				util.processMsgFromDC(DC);
		}

		updateChecker();

		DC.setMessages("	");
			util.processMsgFromDC(DC);

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		enabled = true;
	}

	public void onDisable() {
		CommandSender console = Bukkit.getConsoleSender();
		API API               = new API();

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		switch (config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				players.register();
				break;
	
			case "sqlite":
				try {
					getMySQL().disconnect();
					DC.setMessages(title + "&aDesconectado de SQLite&f.");
						util.processMsgFromDC(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al desconectar a SQLite&f.");
						util.processMsgFromDC(DC);
					return;
				}
				break;
	
			case "mysql":
				try {
					getMySQL().disconnect();
					DC.setMessages(title + "&aDesconectado de MySQL&f.");
						util.processMsgFromDC(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al desconectar a MySQL&f.");
						util.processMsgFromDC(DC);
					return;
				}
				break;
		}

		DC.setMessages(title + "&cDesactivado&f.");
			util.processMsgFromDC(DC);

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		enabled = false;

		majhrs16.ct.util.ChatLimiter.chat.clear();
	}

	public void registerCommands() {
		this.getCommand("ChatTranslator").setExecutor(new CT());
		this.getCommand("ct").setExecutor(new CT());
//		this.getCommand("lang").setExecutor(new Lang(this));
	}

	public void registerEvents() {
		PluginManager pe = getServer().getPluginManager();
		pe.registerEvents(new Chat(this), this);
		pe.registerEvents(new Msg(), this);
	}
	
	public boolean registerStorage() {
		API API = new API();
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
						util.processMsgFromDC(DC);

				} catch (SQLException e) {
					DC.setMessages(title + "&4Error al conectar a SQLite&f.");
						util.processMsgFromDC(DC);
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
						util.processMsgFromDC(DC);

				} catch (SQLException e) {
					e.printStackTrace();
					DC.setMessages(title + "&4Error al conectar a MySQL&f.");
						util.processMsgFromDC(DC);
					return true;
				}
				break;
			
			default:
				DC.setMessages(title + "&4Error&f, &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					util.processMsgFromDC(DC);
		}
		
		return false;
	}

	public void updateChecker() {
		CommandSender console = Bukkit.getConsoleSender();

		API API           = new API();
		Message DC = util.getDataConfigDefault();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

			DC.setMessages("	");
				util.processMsgFromDC(DC);

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://API.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out         = 3000;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestversion  = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestversion.length() <= 7) {
				if (version.equals(latestversion)) {
					DC.setMessages("&a	Estas usando la ultima version del plugin <3");
						util.processMsgFromDC(DC);

				} else {
					DC.setMessages(String.format("&e	Hay una nueva version disponible&f! &f(&b%s&f)", latestversion));
						util.processMsgFromDC(DC);

					DC.setMessages("&a		Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/");
						util.processMsgFromDC(DC);
				}

			}

		} catch (Exception ex) {
			DC.setMessages("&c    Error mientras se buscaban actualizaciones&f.");
				util.processMsgFromDC(DC);
		}
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public SQLite getSQLite() {
		return sqlite;
	}

	public FileConfiguration getConfig() { return config.get(); }
	public void reloadConfig() { config.reload(); }
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
