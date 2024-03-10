package me.majhrs16.cht.translator.api;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.util;

import org.bukkit.entity.Player;
import org.bukkit.Sound;

public class Sounds {
	public static void playSounds(Message formatted) {
		Player player = (Player) formatted.getSender();

		if (formatted.getSounds() != null && Permissions.ChatTranslator.Chat.SOUNDS.IF(formatted)) {
			for (int i = 0; i < formatted.getSounds().length; i++) {
				String[] parts = formatted.getSound(i).replace(" ", "").toUpperCase().split(";");

				try {
					Sound sound = Sound.valueOf(parts[0]);
					float volume = Float.parseFloat(parts[1]);
					float pitch = Float.parseFloat(parts[2]);

					player.playSound(player.getLocation(), sound, pitch, volume);

				} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
					Message from = new Message();
					from.getMessages().setTexts("&eSonido &f'&bformats&f.&b" + formatted.getLastFormatPath() + "&f.&bsounds&f.&b" + parts[0] + "&f' &cinvalido&f.");
					ChatTranslatorAPI.getInstance().sendMessage(from);
				}
			}
		}
	}
}
