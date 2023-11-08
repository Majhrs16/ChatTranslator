package majhrs16.cht.commands.cht;

import majhrs16.cht.exceptions.StorageRegisterFailedException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;

import java.sql.SQLException;

public class Reloader {
	private Message DC;

	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@FunctionalInterface
	public interface RunnableWithException {
		void run() throws SQLException, ParseYamlException, StorageRegisterFailedException;
	}

	public Reloader(Message DC) {
		DC.setMessages("&7Recargando almacenamiento &f...");
			API.sendMessage(DC);

		this.DC = DC;
	}

	public void reloadAll() {
		try {
			reloadMessages();

			reloadConfig();

			reloadCommands();

			reloadSigns();

			reloadStorage();

		} catch (Exception e) {
			if (Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
				DC.setMessages(Texts.getString("plugin.title.text") + "&7[&4ERR110&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
					API.sendMessage(DC);
			}

			e.printStackTrace();
		}
	}

	private void reload(String text, RunnableWithException action) {
		try {
			action.run();
			DC.setMessages("&7[  &aOK  &7] " + text);

		} catch (SQLException e) {
			DC.setMessages("&7[ &cFAIL &7] " + text + "\n    " + e.toString());

		} catch (ParseYamlException e) {
			DC.setMessages("&7[ &cFAIL &7] " + text + "\n    " + e.getMessage());

		} catch (StorageRegisterFailedException e) {
			DC.setMessages("&7[ &cFAIL &7] " + text);
			e.printStackTrace();
		}

		API.sendMessage(DC);
	}

	public void reloadConfig() {
		reload("&bconfig&f.&byml", () -> {
			plugin.config.reload();
			plugin.unregisterDiscordBot();
			plugin.registerDiscordBot();
		});
	}

	public void reloadSigns() {
		reload("&bsigns&f.&byml", () -> plugin.signs.reload());
	}

	public void reloadCommands() {
		reload("&bcommands&f.&byml", () -> plugin.commands.reload());
	}

	public void reloadMessages() {
		reload("&bmessages&f.&byml", () -> {
			plugin.messages.reload();
			Texts.reload();
		});
	}

	public void reloadStorage() {
		switch (plugin.config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				DC.setMessages("&b" + plugin.config.get().getString("storage.database") + "&f.&byml");
				break;

			case "sqlite":
				DC.setMessages("&bSQLite");
				break;

			case "mysql":
				DC.setMessages("&bMySQL");
				break;

			default:
				DC.setMessages("&7[&4ERR100&7]"); // En el dado caso que se haya establecido un almacenamiento desconocido y haya pasado el arranque O_o...
				break;
		}

		reload(DC.getMessages(), new RunnableWithException() {
			@Override
			public void run() throws SQLException, ParseYamlException, StorageRegisterFailedException {
				plugin.storage.reload();
			}
		});
	}
}