package majhrs16.ct.commands.cht;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;

public class Reloader {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public void reloadConfig(Message DC) {
		try {
			plugin.reloadConfig();
			DC.setMessages("&7Recargado &bconfig&f.&byml&f.");
				API.sendMessage(DC);

			plugin.reloadPlayers();
				switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
					case "yaml":
						DC.setMessages("&7Recargado &bplayers&f.&byml&f.");
						break;
	
					case "sqlite":
						DC.setMessages("&7Recargado almacenamiento &bSQLite&f.");
						break;
	
					case "mysql":
						DC.setMessages("&7Recargado almacenamiento &bMySQL&f.");
						break;
				}
			API.sendMessage(DC);

			DC.setMessages("&7Config recargada &aexitosamente&f.");
				API.sendMessage(DC);

		} catch (Exception e) {
			DC.setMessages(plugin.title + "&f [&4ERROR&f] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
				API.sendMessage(DC);

			e.printStackTrace();
		}
	}
}
