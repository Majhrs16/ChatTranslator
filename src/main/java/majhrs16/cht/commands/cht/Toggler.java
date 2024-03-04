package majhrs16.cht.commands.cht;

import majhrs16.lib.minecraft.commands.CommandExecutor;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.ChatLimiter;
import majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class Toggler implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			DC.getMessages().setTexts("&cUsted no tiene permisos para ejecutar este comando&f.");
			API.sendMessage(DC);
			return true; // Para evitar mostrar el unknown command.
		}

		switch (args.length) {
			case 0:
				TogglePlugin(DC);
				break;

			case 1:
				ToggleOffPlayer(DC, args[0]);
				break;
		}

		API.sendMessage(DC);
		return true;
	}
	
	public void ToggleOffPlayer(Message sender, String player) {
		Player player2;
		try {
			player2 = Bukkit.getServer().getPlayer(player);

		} catch (NullPointerException e) {
			player2 = null;
		}

		if (player2 == null) {
			sender.getMessages().setTexts("&7El jugador &f'&b" + player + "&f' &cno &7esta &cdisponible&f.");
				API.sendMessage(sender);
			return;
		}

		API.setLang(player2, "disabled");

		sender.getMessages().setTexts(String.format("&cSe ha desactivado el chat para &f'&b%s&f'&f.", player2.getName()));
	}
	
	public void TogglePlugin(Message sender) {
		ChatLimiter.clear();
		plugin.setDisabled(!plugin.isDisabled());
		sender.getMessages().setFormats("" + !plugin.isDisabled());
	}
}