package majhrs16.cht.commands.cht;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;

public class Reloader {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void reloadConfig(Message DC) {
		try {
			DC.getTo().setMessages("&7Recargando almacenamiento");
				API.sendMessage(DC);

			DC.getTo().setMessages("&bconfig&f.&byml");

			try {
				plugin.reloadConfig();
				DC.getTo().setMessages("&7[  &aOK  &7] " + DC.getTo().getMessages());

			} catch (IllegalArgumentException e) {
				DC.getTo().setMessages("&7[ &cFAIL &7] " + DC.getTo().getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

			switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
				case "yaml":
					DC.getTo().setMessages("&bplayers&f.&byml&f");
					break;

				case "sqlite":
					DC.getTo().setMessages("&bSQLite");
					break;

				case "mysql":
					DC.getTo().setMessages("&bMySQL");
					break;

				default:
					DC.getTo().setMessages("&7[&4ERR001&7]"); // En el dado caso que se haya establecido un almacenamiento desconocido y haya pasado el arranque O_o...
					break;
			}

			try {
				plugin.reloadPlayers();
				DC.getTo().setMessages("&7[  &aOK  &7] " + DC.getTo().getMessages());

			} catch (Exception e) {
				DC.getTo().setMessages("&7[ &cFAIL &7] " + DC.getTo().getMessages() + "\n    " + e.toString());
				e.printStackTrace();
			}

			API.sendMessage(DC);

		} catch (Exception e) {
			DC.getTo().setMessages(plugin.title + "&7[&4ERR000&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
				API.sendMessage(DC);

			e.printStackTrace();
		}
	}
}