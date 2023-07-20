package majhrs16.ct.storage;

import org.bukkit.configuration.file.FileConfiguration;

public interface Configuration {
	public FileConfiguration get();
	public void reload();
	public void save();
	public void register();
}
