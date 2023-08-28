package majhrs16.cht;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.commands.cht.MainCommand;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.storage.data.SQLite;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.events.SignHandler;
import majhrs16.cht.storage.data.MySQL;
import majhrs16.cht.bool.Dependencies;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cot.CoreTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.util.Updater;
import majhrs16.cht.events.Chat;
import majhrs16.cht.events.Msg;
import majhrs16.cht.util.util;

import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class ChatTranslator extends JavaPlugin {
	private YAML signs;
	private MySQL mysql;
	private YAML config;
	private YAML players;
	private SQLite sqlite;
	private static ChatTranslator plugin;

	private boolean is_disabled           = true;
	private PluginDescriptionFile pdffile = getDescription();
	private ChatTranslatorAPI API         = ChatTranslatorAPI.getInstance();

	public final String name       = "&aChat&9Translator";
	public final String version    = "&bv" + pdffile.getVersion().replace(".", "&f.&b");
	public final String sep        = "&c<&4-------------------------&c>";
	public final String title      = "&6<&e[ %name% &e]&6> ".replace("%name%", name);
	public final String title_UTF8 = "\n"
		+ "&a╔═╦╗   ╔╗ &9╔══╗        ╔╗  ╔╗\n"
		+ "&a║╔╣╚╦═╦╣╠╗&9╚╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\n"
		+ "&a║╚╣║╠╝╠╗╔╣&9 ║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\n"
		+ "&a╚═╩╩╩═╝╚═╝&9 ╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝";

	public void onEnable() {
		plugin  = this;

		if (!isDisabled())
			return;

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

		Message from = util.getDataConfigDefault();

		from.setMessages(sep);
			API.sendMessage(from);

		if (Dependencies.ProtocolLib.exist())
			if (Bukkit.getVersion().contains("1.20")) {
				from.setMessages("&eAdvertencia&f: &cProtocolLib no esta soportado en la 1.20.x&f(&7Hasta la fecha&f: &b12/08/2023&f), &eNo se asegura que funcione&f...");
					API.sendMessage(from);
			}

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
		if (isDisabled())
			return;

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
					getMySQL().disconnect();
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
					getMySQL().disconnect();
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

		if (isDisabled())
			plugin.onDisable();

		else
			plugin.onEnable();
	}

	public void registerCommands() {
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

		String storageType = getConfig().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				players.register();
				break;

			case "sqlite":
				sqlite.set(null, 0, getConfig().getString("storage.database"), null, null);
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
					getConfig().getString("storage.host"),
					getConfig().getInt("storage.port"),
					getConfig().getString("storage.database"),
					getConfig().getString("storage.user"),
					getConfig().getString("storage.password")
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
		setDisabled(true);

		String storageType = getConfig().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				players.reload();
				break;

			case "sqlite":
				sqlite.disconnect();
				registerStorage();
				break;

			case "mysql":
				mysql.disconnect();
				registerStorage();
				break;

			default:
				Message from = util.getDataConfigDefault();
					from.setMessages(title + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
				API.sendMessage(from);
		}

		setDisabled(false);
	}

	public void savePlayers() {
		if (getConfig().getString("storage.type").toLowerCase().equals("yaml")) {
			players.save();
		}
	}
}