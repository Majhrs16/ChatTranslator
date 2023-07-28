package majhrs16.ct.commands;

import org.bukkit.command.CommandSender;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;

public class HelperCommand {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API	              = new API();

	public String[] help = new String[] {
		plugin.title + "\n"
			+ "&aTraduce tu chat de Minecraft a cualquier idioma&f!!",
		"&e  /cht",
		"",
		"&e  lang &f[&6Jugador&f] &f<&6codigo&f>\n"
			+ "&7Especifique con su codigo de idioma&f, &apara traducir el chat a su gusto&f.\n"
			+ "&f  (&7Independientemente de su lenguaje en el Minecraft&f)\n"
			+ "\n"
			+ "&aTrucos&f:\n"
			+ "&7  Puede poner &bauto &7como codigo para volver a la\n"
			+ "&7    deteccion automatica del idioma de su Minecraft&f."
			+ "\n"
			+ "&7  Puede poner &boff &7como codigo para &cdeshabilitar &7la\n"
			+ "&7    traduccion automatica para el jugador especificado&f.",
		"",
		"&e  version\n"
			+ "&aVisualizar version&f.",
		"",
		"&e  reload\n"
			+ "&aRecargar config&f.",
		"",
		"&e  toggle &f[&6Jugador&f]\n"
			+ "&aActiva &7o &cdesactiva &7el chat para el jugador o por defecto en global&f.\n"
			+ "&e  Advertencia&f: &eEste comando limpia los mensajes pendientes del chat&f.",
		"",
		"&e  reset\n" 
			+ "&4Restablece la config&f,&e Pero no los datos de lenguajes&f.",
		"",
	};

	public void showToolTip(CommandSender sender) {
		for (int i = 0; i < help.length; i++) {
			if (help[i] == "") {
				sender.sendMessage("");
				continue;
			}

			String description;
			String[] msg       = help[i].split("\n", 2);
			String title       = msg[0];

			if (msg.length > 1)
				description = msg[1];

			else
				description = null;

			Message DC = util.getDataConfigDefault();
				DC.setPlayer(sender);
				DC.setMessages(title);
				DC.setToolTips(description);
				DC.setLang(API.getLang(sender));
			API.sendMessage(DC);
		}
	}
}