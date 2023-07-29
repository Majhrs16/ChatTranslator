package majhrs16.lib.storages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YAML {
	private JavaPlugin plugin;
	public final String filename;
	private FileConfiguration config   = null;
	private File file                  = null;

	public YAML(JavaPlugin plugin, String filename) {
		this.plugin   = plugin;
		this.filename = filename;
	}

	public FileConfiguration get() {
		if (config == null) {
			reload();
		}

		return config;
	}

	public void reload() {
		if (file == null) {
			file = new File(plugin.getDataFolder(), filename);
		}
		
		if (file.exists()) {
			config = YamlConfiguration.loadConfiguration(file);

		} else {
			config = new YamlConfiguration();

			try (Reader defConfigStream = new InputStreamReader(plugin.getResource(filename), "UTF8")) {
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					config.setDefaults(defConfig);
					config.options().copyDefaults(true);
				}

			} catch(UnsupportedEncodingException e){
				e.printStackTrace();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
		file = new File(plugin.getDataFolder(), filename);
		if (!file.exists()) {
			reload();
			save();
		}
	}

	public void reset() {
		file = new File(plugin.getDataFolder(), filename);
		if (file.exists()) {
			file.delete();
			register();
		}
	}
}