package majhrs16.lib.storages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.scanner.ScannerException;

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
			try {
				FileConfiguration temp = YamlConfiguration.loadConfiguration(file);
				if (temp.getString("config-version") == null)
					throw new IllegalArgumentException("[ERR020]");
				config = temp;

			} catch (ScannerException | IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			config = new YamlConfiguration();

			try (Reader defConfigStream = new InputStreamReader(plugin.getResource(filename), StandardCharsets.UTF_8)) {
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

					config.setDefaults(defConfig);
					config.options().copyDefaults(true);
				}

			} catch (IOException e) {
				e.printStackTrace();
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