package majhrs16.cht.storage;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.util.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Players extends YAML {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public Players(JavaPlugin plugin, String filename) {
		super(plugin, filename);
	}

	public void reloads() throws SQLException, ParseYamlException {
		plugin.setDisabled(true);

		String storageType = plugin.config.get().getString("storage.type").toLowerCase();
		switch (storageType) {
			case "yaml":
				super.reload();
				break;

			case "sqlite":
				plugin.sqlite.disconnect();
				plugin.registerPlayers();
				break;

			case "mysql":
				plugin.mysql.disconnect();
				plugin.registerPlayers();
				break;

			default:
				Message from = util.getDataConfigDefault();
					from.setMessages(Texts.PLUGIN.TITLE.TEXT + "&f[&4ERR100&f], &eTipo de almacenamiento invalido: &f'&b" + storageType + "&f'");
				API.sendMessage(from);
		}

		plugin.setDisabled(false);
	}
}