package me.majhrs16.cht.storage;

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.storages.YAML;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class Storage {
	public YAML yaml;
	public MySQL mysql;
	public SQLite sqlite;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public String getType() {
		String type = plugin.config.get().getString("storage.type");

		if (type == null)
			type = "sqlite";

		return type.toLowerCase();
	}

	public TranslatorBase.LanguagesBase getDefaultLang() {
		return util.convertStringToLang(plugin.config.get().getString("default-lang"));
	}

	public void register() throws StorageRegisterFailedException {
		String database = plugin.config.get().getString("storage.database");

		yaml   = new YAML(database + ".yml", "storage.yml");
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
					sqlite.set(null, 0, database, null, null);

					sqlite.connect();
					sqlite.createTable();
					break;

				case "mysql":
				case "mariadb":
					FileConfiguration config = plugin.config.get();

					mysql.set(
						config.getString("storage.host"),
						config.getInt("storage.port"),
						database,
						config.getString("storage.user"),
						config.getString("storage.password")
					);

					mysql.connect();
					mysql.createTable();
					break;

				default:
					from = new Message();
					from.format("storage.errors.invalid-type");
					throw new RuntimeException(String.join("\n", from.getMessages().getFormats()));
			}

		} catch (ParseYamlException  | SQLException | RuntimeException e) {
			from = new Message();
			from.format("storage.error.open", formats -> formats
				.replace("%type%", storage_type)
				.replace("%reason%", e.toString())
			);

			API.sendMessage(from);

			throw new StorageRegisterFailedException(String.join("\n", from.getMessages().getFormats()));
		}

		API.sendMessage(new Message().format(
			"storage.done.open",
			formats -> formats
				.replace("%type%", storage_type)
		));
	}

	public void unregister() {
		Message from        = new Message();
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
				case "mariadb":
					mysql.disconnect();
					break;
			}

			from.format("storage.done.close", format -> format
				.replace("%type%", storage_type)
			);

		} catch (Exception e) {
			from.format("storage.error.close", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type)
			);
		}

		API.sendMessage(from);
	}

	public void set(UUID uuid, String discordID, String lang) {
		Message from        = new Message();
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

		Message from = new Message(util.getNativeLang(), plugin.storage.getDefaultLang());
		from.setTo(from.clone()); // Yes, simply void Message xD.
		String storage_type = getType();

		try {
			switch (storage_type) {
				case "yaml":
					String path               = uuid.toString();
					FileConfiguration storage = yaml.get();
					if (storage.contains(path + ".lang")) {
						result = new String[] {
							uuid.toString(),
							storage.getString(uuid + ".discordID"),
							storage.getString(uuid + ".lang")
						};
					}
					break;

				case "sqlite":
					result = sqlite.get(uuid);
					break;

				case "mysql":
				case "mariadb":
					result = mysql.get(uuid);
					break;
			}

			from.format("storage.done.read", format -> format.replace("%type%", storage_type));

		} catch (SQLException e) {
			from.format("storage.error.read", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type));

		} catch (NullPointerException ignored) {}

		if (from.isEmpty(from.getTo()))
			return result;

		API.showMessage(from, API.formatMessage(from));

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
						if (storage.contains(path) && Objects.equals(storage.getString(path), discordID)) {
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
				case "mariadb":
					result = mysql.get(discordID);
					break;
			}

			from.format("storage.done.read", format -> format
				.replace("%type%", storage_type));

		} catch (SQLException e) {
			from.format("storage.error.read", format -> format
				.replace("%reason%", e.toString())
				.replace("%type%", storage_type));

		} catch (NullPointerException ignored) {}

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