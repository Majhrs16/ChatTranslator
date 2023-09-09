package majhrs16.cht.commands.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.bool.Permissions;
import majhrs16.cht.ChatTranslator;

import java.sql.SQLException;

public class Reloader {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public void reloadAll(Message DC) {
		try {
			DC.setMessages("&7Recargando almacenamiento");
				API.sendMessage(DC);

			reloadConfig(DC);

			reloadCommands(DC);

			reloadSigns(DC);

			reloadPlayers(DC);

		} catch (Exception e) {
			if (Permissions.chattranslator.ADMIN.IF(DC.getSender())) {
				DC.setMessages(plugin.title + "&7[&4ERR110&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
					API.sendMessage(DC);
			}

			e.printStackTrace();
		}
	}

	private void reload(Message DC, String text, Runnable action) {
		try {
			action.run();
			DC.setMessages("&7[  &aOK  &7] " + text);

		} catch (IllegalArgumentException e) {
			DC.setMessages("&7[ &cFAIL &7] " + text + "\n    " + e.toString());
		}

		API.sendMessage(DC);
	}

	public void reloadConfig(Message DC) {
		reload(DC, "&bconfig&f.&byml", () -> plugin.config.reload());
	}

	public void reloadSigns(Message DC) {
		reload(DC, "&bsigns&f.&byml", () -> plugin.signs.reload());
	}

	public void reloadCommands(Message DC) {
		reload(DC, "&bcommands&f.&byml", () -> plugin.commands.reload());
	}

	public void reloadPlayers(Message DC) {
		switch (plugin.config.get().getString("storage.type").toLowerCase()) {
			case "yaml":
				DC.setMessages("&bplayers&f.&byml&f");
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
	
		reload(DC, DC.getMessages(), () -> {
			try {
				plugin.players.reloads();

			} catch (SQLException e) {
				throw new IllegalArgumentException(e);
			}
		});
	}
}