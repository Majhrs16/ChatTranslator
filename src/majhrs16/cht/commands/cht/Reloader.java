package majhrs16.cht.commands.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.bool.Permissions;
import majhrs16.cht.ChatTranslator;

public class Reloader {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public void reloadConfig(Message DC) {
		try {
			DC.setMessages("&7Recargando almacenamiento");
				API.sendMessage(DC);

			DC.setMessages("&bconfig&f.&byml");

			try {
				plugin.config.reload();
				DC.setMessages("&7[  &aOK  &7] " + DC.getMessages());

			} catch (IllegalArgumentException e) {
				DC.setMessages("&7[ &cFAIL &7] " + DC.getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

			DC.setMessages("&bsigns&f.&byml");

			try {
				plugin.signs.reload();
				DC.setMessages("&7[  &aOK  &7] " + DC.getMessages());

			} catch (IllegalArgumentException e) {
				DC.setMessages("&7[ &cFAIL &7] " + DC.getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

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

			try {
				plugin.players.reloads();
				DC.setMessages("&7[  &aOK  &7] " + DC.getMessages());

			} catch (IllegalArgumentException e) {
				DC.setMessages("&7[ &cFAIL &7] " + DC.getMessages() + "\n    " + e.toString());
			}

			API.sendMessage(DC);

		} catch (Exception e) {
			if (Permissions.chattranslator.ADMIN.IF(DC.getSender())) {
				DC.setMessages(plugin.title + "&7[&4ERR110&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
					API.sendMessage(DC);
			}

			e.printStackTrace();
		}
	}
}