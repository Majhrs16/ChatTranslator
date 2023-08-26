package majhrs16.cht.commands.cht;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.translator.API;
import majhrs16.cht.util.util;

public class HelperCommand {
	private ChatTranslator plugin = ChatTranslator.plugin;

	public String[] help = new String[] {
		plugin.title + "\n"
			+ "&aTraduce tu chat de Minecraft a cualquier idioma&f!!",
		"&e  /cht\n"
			+ "&aMuestra este mismo mensaje de ayuda&f.",
		"",
		"&e  lang &f[&6player&f] &f<&6lang&f>\n"
			+ "&7Especifique con su &bcodigo de idioma&f, &apara traducir el chat a su gusto&f,\n"
			+ "&f  (&7Independientemente de su lenguaje en el Minecraft&f)\n"
			+ "\n"
			+ "&aTrucos&f:\n"
			+ "&7  Puede poner &bauto &7como codigo para volver a la\n"
			+ "&7    deteccion automatica del idioma de su Minecraft," // En ingame al traducir el ultimo &f, se bugea como x 26
			+ "\n"
			+ "&7  Puede poner &boff &7como codigo para &cdeshabilitar &7la traduccion\n"
			+ "&7    automatica para el jugador especificado&f.",
		"",
		"&e  version\n"
			+ "&aVisualizar version&f.",
		"",
		"&e  reload\n"
			+ "&aRecargar config&f.",
		"",
		"&e  toggle &f[&6player&f]\n"
			+ "&aActiva &7o &cdesactiva &7el chat para el jugador o por defecto en global&f,\n"
			+ "&e  Advertencia&f: &eEste comando limpia los mensajes pendientes del chat&f.",
		"",
		"&e  reset\n" 
			+ "&4Restablece la config&f,&e Pero no los datos de lenguajes&f.",
		"",
	};

	public void show(CommandSender sender) {
		for (int i = 0; i < help.length; i++) {
			if (help[i] == "") {
				sender.sendMessage("");
				continue;
			}

			String description;
			String[] msg = help[i].split("\n", 2);
			String title = msg[0];

			if (msg.length > 1)
				description = msg[1];

			else
				description = null;

			Message DC = util.getDataConfigDefault();
				DC.setSender(sender);
				DC.setMessageFormat(title);
				DC.setMessages("");
				if (description != null)
					DC.setToolTips(sender instanceof Player ? description : "	" + description.replace("\n", "\n\t"));
//					Limitacino de la API, siosi necesitaremos agregar config.formats.<grupo>.toolTipsFormat, ademas de agregar %ct_tooltips$...
//					O quizas no...
				DC.setLangTarget(API.getLang(sender));
			API.sendMessage(DC);
		}
	}
}