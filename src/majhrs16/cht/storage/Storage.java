package majhrs16.cht.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.util.util;

import javax.annotation.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class Storage {
	public YAML yaml;
	public MySQL mysql;
	public SQLite sqlite;

	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public Storage() {
		yaml   = new YAML(plugin, plugin.config.get().getString("storage.database") + ".yml", "storage.yml");
		sqlite = new SQLite();
		mysql  = new MySQL();
	}

	public String getType() {
		return plugin.config.get().getString("storage.type").toLowerCase();
	}

	public String getDefaultLang() {
		return plugin.config.get().getString("default-lang");
	}

	public void register() throws SQLException, ParseYamlException {
		String storageType = getType();

		switch (storageType) {
			case "yaml":
				try {
					yaml.register();

					Message from = util.getDataConfigDefault();
					from.setMessages(Texts.STORAGE.OPEN.YAML.OK);
						API.sendMessage(from);

				} catch (ParseYamlException e) {
					throw new ParseYamlException(Texts.STORAGE.OPEN.YAML.ERROR + "\n\t" + e.toString());
				}
				break;

			case "sqlite":
				sqlite.set(null, 0, plugin.config.get().getString("storage.database"), null, null);

				try {
					sqlite.connect();
					sqlite.createTable();

					Message from = util.getDataConfigDefault();
					from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));
					from.setMessages(Texts.STORAGE.OPEN.SQLITE.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					throw new IllegalArgumentException(Texts.STORAGE.OPEN.SQLITE.ERROR + "\n\t" + e.toString());
				}
				break;

			case "mysql":
				FileConfiguration config = plugin.config.get();

				mysql.set(
					config.getString("storage.host"),
					config.getInt("storage.port"),
					config.getString("storage.database"),
					config.getString("storage.user"),
					config.getString("storage.password")
				);

				try {
					mysql.connect();
					mysql.createTable();

					Message from = util.getDataConfigDefault();
					from.setLangTarget(API.getLang(Bukkit.getConsoleSender()));
					from.setMessages(Texts.STORAGE.OPEN.MYSQL.OK);
						API.sendMessage(from);

				} catch (SQLException e) {
					throw new IllegalArgumentException(Texts.STORAGE.OPEN.MYSQL.ERROR + "\n\t" + e.toString());
				}
				break;

			default:
				Message from = util.getDataConfigDefault();
				from.setMessages(Texts.PLUGIN.TITLE.TEXT + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
					API.sendMessage(from);
		}
	}

	public void unregister() {
		Message from = util.getDataConfigDefault();

		switch (getType()) {
			case "yaml":
				try {
					yaml.save();
					from.setMessages(Texts.STORAGE.CLOSE.YAML.OK);
						API.sendMessage(from);
	
				} catch (IllegalArgumentException e) {
					from.setMessages(Texts.STORAGE.CLOSE.YAML.ERROR);
						API.sendMessage(from);
					return;
				}
				break;
	
			case "sqlite":
				try {
					sqlite.disconnect();
					from.setMessages(Texts.STORAGE.CLOSE.SQLITE.OK);
						API.sendMessage(from);
	
				} catch (SQLException e) {
					from.setMessages(Texts.STORAGE.CLOSE.SQLITE.ERROR);
						API.sendMessage(from);
					return;
				}
				break;
	
			case "mysql":
				try {
					mysql.disconnect();
					from.setMessages(Texts.STORAGE.CLOSE.MYSQL.OK);
						API.sendMessage(from);
	
				} catch (SQLException e) {
					from.setMessages(Texts.STORAGE.CLOSE.MYSQL.ERROR);
						API.sendMessage(from);
					return;
				}
				break;
		}
	}

	public void set(UUID uuid, @Nullable String discordID, String lang) {
		Message DC = util.getDataConfigDefault();

		switch (getType()) {
			case "yaml":
				if (discordID != null)
					yaml.get().set(uuid.toString() + ".discordID", discordID);

				yaml.get().set(uuid.toString() + ".lang", lang);
				yaml.save();
				break;

			case "sqlite":
				try {
					if (sqlite.get(uuid) == null) {
						sqlite.insert(uuid, discordID, lang);

					} else {
						sqlite.update(uuid, discordID, lang);
					}

				} catch (SQLException e) {
					DC.setMessages("&cError al escribir en SQLite&f.\n\t" + e.toString());
						API.sendMessage(DC);
				}

				break;

			case "mysql":
				try {
					if (mysql.get(uuid) == null) {
						mysql.insert(uuid, discordID, lang);
	
					} else {
						mysql.update(uuid, discordID, lang);
					}
	
				} catch (SQLException e) {
					DC.setMessages("&cError al escribir en MySQL&f.\n\t" + e.toString());
						API.sendMessage(DC);
				}

				break;
		}
	}

	public String[] get(UUID uuid) {
		String[] result = null;

		Message DC = util._getDataConfigDefault();

		try {
			switch (getType()) {
				case "yaml":
					FileConfiguration storage = yaml.get();
					String path = uuid.toString();
					if (storage.contains(path + ".lang")) {
						result = new String[]{
								uuid.toString(),
								storage.getString(uuid.toString() + ".discordID"),
								storage.getString(uuid.toString() + ".lang")
						};
					}
					break;

				case "sqlite":
					result = sqlite.get(uuid);
					break;

				case "mysql":
					result = mysql.get(uuid);
					break;
			}

		} catch (SQLException e) {
			DC.setMessages("&cError al leer en &b" + getType() + "&f.\n\t" + e.toString());
				API.sendMessage(DC);

		} catch (NullPointerException e) {
			// Manejar la excepción de NullPointerException si es necesario.
		}

		return result;
	}

	public String[] get(String discordID) {
		String[] result = null;

		Message DC = util._getDataConfigDefault();

		try {
			switch (getType()) {
				case "yaml":
					FileConfiguration storage = yaml.get();

					for (String uuid : storage.getKeys(false)) {
						String path = uuid + ".discordID";
						if (storage.contains(path) && storage.getString(path).equals(discordID)) {
							path = uuid + ".lang";
							if (storage.contains(path)) {
								result = new String[] {uuid, discordID, storage.getString(path)};
								break;
							}
						}
					}

					break;

				case "sqlite":
					result = sqlite.get(discordID);
					break;

				case "mysql":
					result = mysql.get(discordID);
					break;
			}

		} catch (SQLException e) {
			DC.setMessages("&cError al leer en &b" + getType() + "&f.\n\t" + e.toString());
				API.sendMessage(DC);

		} catch (NullPointerException e) {
			// Manejar la excepción de NullPointerException si es necesario.
		}

		return result;
	}

	public void reload() throws SQLException, ParseYamlException {
		plugin.setDisabled(true);

		String storageType = plugin.config.get().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				yaml.reload();
				break;

			case "sqlite":
				sqlite.disconnect();
				register();
				break;

			case "mysql":
				mysql.disconnect();
				register();
				break;

			default:
				Message from = util.getDataConfigDefault();
					from.setMessages(Texts.PLUGIN.TITLE.TEXT + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
				API.sendMessage(from);
		}

		plugin.setDisabled(false);
	}
}