package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordLinker implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (Config.TranslateOthers.DISCORD.IF()) {
			if (DC.getSender() instanceof Player) {
				int code = AccountManager.preLink(((Player) DC.getSender()).getUniqueId(), () -> {
					DC.getMessages().setTexts("&cTiempo de espera agotado para su vinculacion de cuenta&f.");
					API.sendMessage(DC);
				});

				DC.getMessages().setTexts(
					"&eSu codigo de verificacion es&f: '&b" + code + "&f', &6Y expira en 1 minuto&f.",
					"    &6Por favor escribalo por privado al bot del servidor de &9Discord&f: (&b`" + DiscordTranslator.getJDA().getSelfUser().getName() + "`&f)"
				);

			} else {
				DC.getMessages().setTexts("&cSolo se puede ejecutar este comando desde un jugador.");
			}

		} else {
			DC.getMessages().setTexts("&cDebe activar `&bconfig." + Config.TranslateOthers.DISCORD.getPath() + "` &cpara usar este comando&f.");
		}

		API.sendMessage(DC);
		return true;
	}
}
