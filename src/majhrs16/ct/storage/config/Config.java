package majhrs16.ct.storage.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.storage.Configuration;
import majhrs16.ct.util.Updater;

public class Config implements Configuration {
	private ChatTranslator plugin    = ChatTranslator.plugin;
	private FileConfiguration config = null;
	private File file                = null;

	public FileConfiguration get() {
	    if (config == null) {
	        reload();
	    }

	    return config;
	}

	public void reload() {
	    if (file == null) {
	        file = new File(plugin.getDataFolder(), "config.yml");
	    }

	    if (file.exists()) {
	        config = YamlConfiguration.loadConfiguration(file);

	    } else {
	        config = new YamlConfiguration(); // Crea una nueva configuración vacía
//	        config.options().copyDefaults(true); // Copia las configuraciones por defecto
	        new Updater().applyDefaultConfig();
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

	public void save() {
		try {
			config.save(file);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void register() {
	    file = new File(plugin.getDataFolder(), "config.yml");
	    if (!file.exists()) {
	        reload();
	        save();
	    }
	}
}
