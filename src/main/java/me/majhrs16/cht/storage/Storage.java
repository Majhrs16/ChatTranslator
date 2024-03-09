package me.majhrs16.cht.storage;

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.configuration.file.FileConfiguration;

import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.storages.YAML;

import java.sql.SQLException;
import java.util.UUID;

public class Storage {
	public YAML yaml;
	public MySQL mysql;
	public SQLite sqlite;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public String getType() {
		return plugin.config.get().getString("storage.type").toLowerCase();
	}

	public String getDefaultLang() {
		return plugin.config.get().getString("default-lang");
	}

	public void register() throws StorageRegisterFailedException {
		yaml   = new YAML(plugin.config.get().getString("storage.database") + ".yml", "storage.yml");
		sqlite = new SQLite();
		mysql  = new MySQL();

		Message from;
		String storage_type = getType();

		try {
			switch (storage_type) {
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
					from.format("storage.errors.invalid-type");
					throw new RuntimeException(String.join("\n", from.getMessages().getFormats()));
			}

		} catch (ParseYamlException  | SQLException | RuntimeException e) {
			from = util.getDataConfigDefault();
			from.format("storage.error.open", formats -> formats
				.replace("%type%", storage_type)
				.replace("%reason%", e.toString())
			);

			API.sendMessage(from);

			throw new StorageRegisterFailedException(String.join("\n", from.getMessages().getFormats()));
		}

		from = util.getDataConfigDefault();
		from.format("storage.done.open", formats -> formats.replace("%type%", storage_type));
		API.sendMessage(from);
	}

	public void unregister() {
		Message from        = util.getDataConfigDefault();
		String storage_type = getType();

		try {
			switch (storage_type) {
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

			from.format("storage.done.close",  format -> format.replace("%type%", storage_type));

		} catch (Exception e) {
			from.format("storage.error.close", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type)
			);
		}

		API.sendMessage(from);
	}

	public void set(UUID uuid, String discordID, String lang) {
		Message from        = util.getDataConfigDefault();
		String storage_type = getType();

		try {
			switch (storage_type) {
				case "yaml":
					if (discordID != null)
						yaml.get().set(uuid.toString() + ".discordID", discordID);

					yaml.get().set(uuid.toString() + ".lang", lang);
					yaml.save();
					break;

				case "sqlite":
					if (sqlite.get(uuid) == null)
						sqlite.insert(uuid, discordID, lang);

					else
						sqlite.update(uuid, discordID, lang);

					break;
	
				case "mysql":
					if (mysql.get(uuid) == null)
						mysql.insert(uuid, discordID, lang);

					else
						mysql.update(uuid, discordID, lang);

					break;
			}

			from.format("storage.done.write", format -> format.replace("%type%", storage_type));

		} catch (SQLException e) {
			from.format("storage.error.write", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type)
			);
		}

		API.sendMessage(from);
	}

	public String[] get(UUID uuid) {
		String[] result = null;

		Message from        = new Message();
		String storage_type = getType();

		try {
			switch (storage_type) {
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

			from.format("storage.done.read", format -> format.replace("%type%", storage_type));

		} catch (SQLException e) {
			from.format("storage.error.read", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type));

		} catch (NullPointerException ignored) {
			;
		}

		API.sendMessage(from);

		return result;
	}

	public String[] get(String discordID) {
		String[] result     = null;
		Message from        = new Message();
		String storage_type = getType();

		try {
			switch (storage_type) {
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

			from.format("storage.done.read", format -> format
				.replace("%type%", storage_type));

		} catch (SQLException e) {
			from.format("storage.error.read", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type));

		} catch (NullPointerException ignored) {
			;
		}

		API.sendMessage(from);

		return result;
	}

	public void reload() throws StorageRegisterFailedException {
		plugin.setDisabled(true);

		unregister();
		register();

		plugin.setDisabled(false);
	}
}