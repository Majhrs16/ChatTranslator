package majhrs16.ct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import majhrs16.ct.commands.CT;
import majhrs16.ct.commands.Lang;
import majhrs16.ct.events.Chat;

public class Main extends JavaPlugin {
	public String rutaConfig;
	private FileConfiguration players           = null;
	private File playersFile                    = null;
	PluginDescriptionFile pdffile               = getDescription();
	public String version                       = pdffile.getVersion();
	public String name                          = ChatColor.YELLOW + "[" + ChatColor.GREEN + pdffile.getName() + ChatColor.YELLOW + "]";
	
	public void onEnable() {
		RegistryConfig();
		RegisterPlayers();

		FileConfiguration config = getConfig();
		String path = "server-uuid";
		if (!config.contains(path)) {
			config.set(path, "" + UUID.randomUUID());
			saveConfig();
		}

		RegistryCommands();
		RegistryEvents();
		chatManager();

		API API = new API(this);
		CommandSender   console = Bukkit.getConsoleSender();
		API.sendMessage(null, console, "", "&4<------------------------->", "es");
		API.sendMessage(null, console, "", name + "&aActivado&f. &7Version&f: &a%version%&f.".replace("%version%", version), "es");
		API.sendMessage(null, console, "", " ", "es");
		updateChecker();
		API.sendMessage(null, console, "", "&4<------------------------->", "es");

		if (!new Util(this).checkPAPI())
			API.sendMessage(null, console, "", "&cNo esta disponible PlaceHolderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas de &a" + pdffile.getName(), "es");
	}

	public void onDisable() {
		API API = new API(this);
		CommandSender   console = Bukkit.getConsoleSender();
		API.sendMessage(null, console, "", "&4<------------------------->", "es");
		API.sendMessage(null, console, "", name + "&cDesactivado&f.", "es");
		API.sendMessage(null, console, "", "&4<------------------------->", "es");
	}

	public void chatManager() {
		Chat Chat = new Chat(this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		        for (int i = 0; i < majhrs16.ct.Util.chat.size(); i += 30) {
		        	int end = Math.min(i + 30, majhrs16.ct.Util.chat.size());
			    	System.out.println("DEBUG: min: " + end);
		        	for (AsyncPlayerChatEvent event : majhrs16.ct.Util.chat.subList(i, end)) {
			    		Chat.processMsg(event);
		        	}

		        	majhrs16.ct.Util.chat.subList(i, end).clear();
		        }
		    }
		}, 0L, 1L);
	}

	public void RegistryCommands() {
		this.getCommand("ChatTranslator").setExecutor(new CT(this));
		this.getCommand("ct").setExecutor(new CT(this));
		this.getCommand("lang").setExecutor(new Lang(this));
	}
	
	public void RegistryEvents() {
		PluginManager pe = getServer().getPluginManager();
		pe.registerEvents(new Chat(this), this);
	}
	
	public void RegistryConfig() {
		File config = new File(this.getDataFolder(), "config.yml");
		rutaConfig  = config.getPath();
		
		if (!config.exists()) {
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
	}

	public void updateChecker() {
		API API = new API(this);
		CommandSender console = Bukkit.getConsoleSender();
		
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://API.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out         = 1250;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestversion  = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestversion.length() <= 7) {
				if (!version.equals(latestversion)) {
					API.sendMessage(null, console, "", name + "&e  Hay una nueva version disponible&f! &f(&a%latestversion%&f)".replace("%latestversion%", latestversion), "es");
					API.sendMessage(null, console, "", name + "&a    Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/", "es");

				} else
					API.sendMessage(null, console, "", name + "&a  Estas usando la ultima version del plugin <3", "es");
			}

		} catch (Exception ex) {
			API.sendMessage(null, console, "", name + "&c Error mientras se buscaban actualizaciones&f.", "es");
		}
	}

	/////////////////////////////////////////
	// Codigo para cada nuevo archivo.yml

	public FileConfiguration getPlayers() {
		if (players == null) {
			reloadPlayers();
		}

		return players;
	}
 
	public void reloadPlayers(){
		if (players == null) {
			playersFile = new File(getDataFolder(), "players.yml");
		}

		players = YamlConfiguration.loadConfiguration(playersFile);
		Reader defConfigStream;

		try {
			defConfigStream = new InputStreamReader(this.getResource("players.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				players.setDefaults(defConfig);
			}			

		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}
 
	public void savePlayers(){
		try {
			players.save(playersFile);

		} catch(IOException e){
			e.printStackTrace();
		}
	}
 
	public void RegisterPlayers(){
		playersFile = new File(this.getDataFolder(), "players.yml");
		if (!playersFile.exists()){
			this.getPlayers().options().copyDefaults(true);
			savePlayers();
		}
	}
}