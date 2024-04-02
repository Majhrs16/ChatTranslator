package me.majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.majhrs16.lib.exceptions.ParseYamlException;

import org.yaml.snakeyaml.scanner.ScannerException;

import me.majhrs16.cht.ChatTranslator;

import java.io.IOException;
import java.io.File;

public class StorageYamlUpdater {
	private final ChatTranslator plugin = ChatTranslator.getInstance();

	public void initYaml() throws ParseYamlException {
		String filename = "players.yml";
		File file       = new File(plugin.getDataFolder(), filename);

		if (file.exists()) {
			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("config-version", 1);
				config.save(file);

			} catch (ScannerException e) {
				throw new ParseYamlException("[ERR021]");

			} catch (IOException e) {
				ChatTranslator.getInstance().logger.error(e.toString());
			}
		}
	}
}