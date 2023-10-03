package majhrs16.cht.storage;

import org.bukkit.configuration.file.FileConfiguration;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.exceptions.StorageRegisterFailedException;
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

	private void __init__() {
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

	public void register() throws StorageRegisterFailedException {
		__init__();

		Message from       = null;
		String storageType = getType();

		try {
			switch (storageType) {
				case "yaml":
					yaml.register();
					break;

				case "sqlite":
					sqlite.set(null, 0, plugin.config.get().getString("storage.database"), null, null);

					sqlite.connect();
					sqlite.createTable();
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

					mysql.connect();
					mysql.createTable();
					break;

				default:
					from = util.getDataConfigDefault();
					from.setMessages(Texts.getString("storage.errors.invalid-type"));
			}
		
		} catch (ParseYamlException  | SQLException e) {
			throw new StorageRegisterFailedException(Texts.getString("storage.open.error").replace("%type%", storageType).replace("%reason%", e.toString()));
		}

		if (from == null) {
			from = util.getDataConfigDefault();
			from.setMessages(Texts.getString("storage.open.ok").replace("%type%", storageType));
		}

		API.sendMessage(from);
	}

	public void unregister() {
		Message from = util.getDataConfigDefault();

		try {
			switch (getType()) {
				case "yaml":
					yaml.save();
					break;

				case "sqlite":
					sqlite.disconnect();
					break;

				case "mysql":
					mysql.disconnect();
					break;
			}

			from.setMessages(Texts.getString("storage.close.ok").replace("%type%", getType()));

		} catch (Exception e) {
			from.setMessages(Texts.getString("storage.close.error").replace("%type%", getType()).replace("%reason%", e.toString()));
		}

		API.sendMessage(from);
	}

	public void set(UUID uuid, @Nullable String discordID, String lang) {
		Message DC = util.getDataConfigDefault();

		try {
			switch (getType()) {
				case "yaml":
					if (discordID != null)
						yaml.get().set(uuid.toString() + ".discordID", discordID);

					yaml.get().set(uuid.toString() + ".lang", lang);
					yaml.save();
					break;

				case "sqlite":
					if (sqlite.get(uuid) == null) {
						sqlite.insert(uuid, discordID, lang);

					} else {
						sqlite.update(uuid, discordID, lang);
					}

					break;
	
				case "mysql":
					if (mysql.get(uuid) == null) {
						mysql.insert(uuid, discordID, lang);

					} else {
						mysql.update(uuid, discordID, lang);
					}
					break;
			}

			DC.setMessages(Texts.getString("storage.done.write").replace("%type%", getType()));

		} catch (SQLException e) {
			DC.setMessages(Texts.getString("storage.errors.write").replace("%type%", getType()).replace("%reason%", e.toString()));
		}

		API.sendMessage(DC);
	}

	public String[] get(UUID uuid) {
		String[] result = null;

		Message DC = util._getDataConfigDefault();

		try {
			switch (getType()) {
				case "yaml":
					String path               = uuid.toString();
					FileConfiguration storage = yaml.get();
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

			DC.setMessages(Texts.getString("storage.done.read").replace("%type%", getType()));

		} catch (SQLException e) {
			DC.setMessages(Texts.getString("storage.errors.read").replace("%type%", getType()).replace("%reason%", e.toString()));

		} catch (NullPointerException e) {
			// Manejar la excepción de NullPointerException si es necesario.
		}

		API.sendMessage(DC);

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

			DC.setMessages(Texts.getString("storage.done.read").replace("%type%", getType()));

		} catch (SQLException e) {
			DC.setMessages(Texts.getString("storage.errors.read").replace("%type%", getType()).replace("%reason%", e.toString()));

		} catch (NullPointerException e) {
			// Manejar la excepción de NullPointerException si es necesario.
		}

		return result;
	}

	public void reload() throws StorageRegisterFailedException, SQLException, ParseYamlException {
		plugin.setDisabled(true);

		unregister();
		register();

		plugin.setDisabled(false);
	}
}