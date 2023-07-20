package majhrs16.ct.storage.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.storage.Configuration;

public class YAML implements Configuration {
	private ChatTranslator plugin     = ChatTranslator.plugin;
	public FileConfiguration config   = null;
	public File file                  = null;

	public FileConfiguration get() {
		if (config == null) {
			reload();
		}

		return config;
	}

	public void reload() {
		if (config == null) {
			file = new File(plugin.getDataFolder(), "players.yml");
		}

		config = YamlConfiguration.loadConfiguration(file);
		Reader defConfigStream;

		try {
			defConfigStream = new InputStreamReader(plugin.getResource("players.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				config.setDefaults(defConfig);
			}

		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.save(file);

		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public void register() {
		file = new File(plugin.getDataFolder(), "players.yml");
		if (!file.exists()) {
			get().options().copyDefaults(true);
			save();
		}
	}
}
