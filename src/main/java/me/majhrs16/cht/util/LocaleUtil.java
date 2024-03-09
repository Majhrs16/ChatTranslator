package me.majhrs16.cht.util;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LocaleUtil {
	public static String getPlayerLocale(Player player) {
		String playerLocale = null;

		try {
			Method getHandle = player.getClass().getDeclaredMethod("getHandle");
			getHandle.setAccessible(true);

			Object entityPlayer = getHandle.invoke(player);

			if (entityPlayer != null) {
				if (util.getMinecraftVersion() < 7.2) { // Por debajo de la 1.7.
					Field localeField   = entityPlayer.getClass().getDeclaredField("locale");
					localeField.setAccessible(true);
					Object localeObject = localeField.get(entityPlayer);

					if (localeObject != null) {
						Field eField = localeObject.getClass().getDeclaredField("e");
						eField.setAccessible(true);
						playerLocale = (String) eField.get(localeObject);
					}

				} else if (util.getMinecraftVersion() <= 20.1) { // 1.20.1 o anterior.
					playerLocale = (String) entityPlayer.getClass().getField("locale").get(entityPlayer);

				} else if (util.getMinecraftVersion() == 20.2) { // 1.20.2
					playerLocale = (String) entityPlayer.getClass().getField("cM").get(entityPlayer);

				} else if (util.getMinecraftVersion() == 20.4) { // 1.20.4
					playerLocale = (String) entityPlayer.getClass().getField("cO").get(entityPlayer);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return playerLocale;
	}
}