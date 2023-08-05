package majhrs16.ct.commands.cht;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;

public class Reloader {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void reloadConfig(Message DC) {
		try {
			DC.setMessages("&7Recargando almacenamiento");
				API.sendMessage(DC);

			DC.setMessages("&bconfig&f.&byml");

			try {
				plugin.reloadConfig();
				DC.setMessages("&7[  &aOK  &7] " + DC.getMessages());

			} catch (Exception e) {
				DC.setMessages("&7[ &cFAIL &7] " + DC.getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

			switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
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
					DC.setMessages("&7[&4ERR001&7]"); // En el dado caso que se haya establecido un almacenamiento desconocido y haya pasado el arranque O_o...
					break;
			}

			try {
				plugin.reloadPlayers();
				DC.setMessages("&7[  &aOK  &7] " + DC.getMessages());

			} catch (Exception e) {
				DC.setMessages("&7[ &cFAIL &7] " + DC.getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

		} catch (Exception e) {
			DC.setMessages(plugin.title + "&7[&4ERR000&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
				API.sendMessage(DC);

			e.printStackTrace();
		}
	}
}