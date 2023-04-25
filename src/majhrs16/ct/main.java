package majhrs16.ct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import majhrs16.ct.commands.mainCommand;
import majhrs16.ct.commands.setLang;
import majhrs16.ct.events.chat;

public class main extends JavaPlugin {
	public String rutaConfig;
	private FileConfiguration players           = null;
	private File playersFile                    = null;
	PluginDescriptionFile pdffile               = getDescription();
	public String version                       = pdffile.getVersion();
	public String name                          = ChatColor.YELLOW + "[" + ChatColor.GREEN + pdffile.getName() + ChatColor.YELLOW + "]";
	
	public void onEnable() {
		api api = new api(this);
		CommandSender   console = Bukkit.getConsoleSender();
		api.sendMessage(console, "", "&4<------------------------->", "es");
		api.sendMessage(console, "", name + "&aActivado&f. &f(&a%version%&f)".replace("%version%", version), "es");
		api.sendMessage(console, "", " ", "es");
		updateChecker();
		api.sendMessage(console, "", "&4<------------------------->", "es");

		RegistryConfig();
//		RegisterPlayers();
		RegistryCommands();
		RegistryEvents();
		
		chatManager();

		if (!new util(this).checkPAPI())
			api.sendMessage(console, "", "&cNo esta disponible PlaceHolderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas de &a" + pdffile.getName(), "es");
	}

	public void onDisable() {
		api api = new api(this);
		CommandSender   console = Bukkit.getConsoleSender();
		api.sendMessage(console, "", "&4<------------------------->", "es");
		api.sendMessage(console, "", name + "&cDesactivado&f.", "es");
		api.sendMessage(console, "", "&4<------------------------->", "es");
	}

	public void chatManager() {
		chat chat = new chat(this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		        for (int i = 0; i < majhrs16.ct.util.chat.size(); i += 10) {
		        	int end = Math.min(i + 10, majhrs16.ct.util.chat.size());
			    	System.out.println("DEBUG: min: " + end);
		        	for (AsyncPlayerChatEvent event : majhrs16.ct.util.chat.subList(i, end)) {
			    		chat.processMsg(event);
		        	}

		        	majhrs16.ct.util.chat.subList(i, end).clear();
		        }
		    }
		}, 0L, 1L);
	}

	public void RegistryCommands() {
		this.getCommand("ChatTranslator").setExecutor(new mainCommand(this));
		this.getCommand("ct").setExecutor(new mainCommand(this));
		this.getCommand("lang").setExecutor(new setLang(this));
	}
	
	public void RegistryEvents() {
		PluginManager pe = getServer().getPluginManager();
		pe.registerEvents(new chat(this), this);
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
		api api = new api(this);
		CommandSender console = Bukkit.getConsoleSender();
		
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=106604").openConnection();
			int timed_out         = 1250;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			String latestversion  = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestversion.length() <= 7) {
				if (!version.equals(latestversion)) {
					api.sendMessage(console, "", name + "&e Hay una nueva version disponible&f! &f(&a%latestversion%&f)".replace("%latestversion%", latestversion), "es");
					api.sendMessage(console, "", name + "&a   Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/", "es");
				}
			}

		} catch (Exception ex) {
			api.sendMessage(console, "", name + "&c Error mientras se buscaban actualizaciones&f.", "es");
		}
	}

	/////////////////////////////////////////
	// Codigo para cada nuevo archivo.yml
	// Code for mew file.yml

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