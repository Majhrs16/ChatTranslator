package majhrs16.cht.commands.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class Toggler {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public void ToggleOffPlayer(Message sender, String player) {
		Player player2;
		try {
			player2 = Bukkit.getServer().getPlayer(player);

		} catch (NullPointerException e) {
			player2 = null;
		}

		if (player2 == null) {
			sender.getTo().setMessages("&7El jugador &f'&b" + player + "&f' &cno &7esta &cdisponible&f.");
				API.sendMessage(sender);
			return;
		}

		API.setLang(player2, "disabled");
		plugin.savePlayers();

		sender.getTo().setMessages(String.format("&cSe ha desactivado el chat para &f'&b%s&f'&f.", player2.getName()));
			API.sendMessage(sender);
	}
	
	public void TogglePlugin(CommandSender sender) {
		plugin.setDisabled(!plugin.isDisabled());
		sender.sendMessage("" + plugin.isDisabled());
	}
}
