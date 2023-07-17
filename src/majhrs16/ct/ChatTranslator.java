package majhrs16.ct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import majhrs16.ct.commands.CT;
import majhrs16.ct.events.Chat;
import majhrs16.ct.events.Msg;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.util.util;
import majhrs16.ct.util.ChatLimiter;
import majhrs16.ct.util.UpdateConfig;

public class ChatTranslator extends JavaPlugin {
	public Boolean enabled;
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

		registerConfig();
		new UpdateConfig().update();
		registerPlayers();

		registerCommands();
		registerEvents();
		new ChatLimiter();

		API API = new API();
		CommandSender console = Bukkit.getConsoleSender();

		Message DC = util.getDataConfigConsole();
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

		DC.setMessages(String.format("&a	Activado&f. &7Version&f: &b%s&f.", version));
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

		Message DC = util.getDataConfigConsole();
			DC.setPlayer(console);
			DC.setLang(API.getLang(console));

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		DC.setMessages("&c Desactivado&f.");
			util.processMsgFromDC(DC);

		DC.setMessages("&4<------------------------->");
			util.processMsgFromDC(DC);

		enabled = false;

		majhrs16.ct.util.ChatLimiter.chat.clear();
	}

	public void registerCommands() {
		this.getCommand("ChatTranslator").setExecutor(new CT(this));
		this.getCommand("ct").setExecutor(new CT(this));
//		this.getCommand("lang").setExecutor(new Lang(this));
	}

	public void registerEvents() {
		PluginManager pe = getServer().getPluginManager();
		pe.registerEvents(new Chat(this), this);
		pe.registerEvents(new Msg(), this);
	}

	public void updateChecker() {
		CommandSender console = Bukkit.getConsoleSender();

		API API           = new API();
		Message DC = util.getDataConfigConsole();
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
	
	/////////////////////////////////////////
	// Codigo para cada nuevo archivo.yml

	private FileConfiguration config = null;
	private File configFile = null;

	public FileConfiguration getConfig() {
	    if (config == null) {
	        reloadConfig();
	    }

	    return config;
	}

	public void reloadConfig() {
	    if (configFile == null) {
	        configFile = new File(plugin.getDataFolder(), "config.yml");
	    }

	    if (configFile.exists()) {
	        config = YamlConfiguration.loadConfiguration(configFile);
	    } else {
	        config = new YamlConfiguration(); // Crea una nueva configuración vacía
//	        config.options().copyDefaults(true); // Copia las configuraciones por defecto
	        new UpdateConfig().applyDefaultConfig();
	    }
	    
	    Reader defConfigStream;
	    try {
	        defConfigStream = new InputStreamReader(plugin.getResource("config.yml"), "UTF8");
	        if (defConfigStream != null) {
	            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	            config.setDefaults(defConfig);
	        }
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	}

	public void saveConfig() {
		try {
			config.save(configFile);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void registerConfig() {
	    configFile = new File(plugin.getDataFolder(), "config.yml");
	    if (!configFile.exists()) {
	        reloadConfig();
	        saveConfig();
	    }
	}
	
	/////////////////////////////////////////
	// Codigo para cada nuevo archivo.yml

	private FileConfiguration players = null;
	private File playersFile          = null;

	public FileConfiguration getPlayers() {
		if (players == null) {
			reloadPlayers();
		}

		return players;
	}

	public void reloadPlayers() {
		if (players == null) {
			playersFile = new File(plugin.getDataFolder(), "players.yml");
		}

		players = YamlConfiguration.loadConfiguration(playersFile);
		Reader defConfigStream;

		try {
			defConfigStream = new InputStreamReader(plugin.getResource("players.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				players.setDefaults(defConfig);
			}

		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void savePlayers() {
		try {
			players.save(playersFile);

		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerPlayers() {
		playersFile = new File(plugin.getDataFolder(), "players.yml");
		if (!playersFile.exists()) {
			this.getPlayers().options().copyDefaults(true);
			savePlayers();
		}
	}
}
