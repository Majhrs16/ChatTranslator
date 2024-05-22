package me.majhrs16.cht.events;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.lang.reflect.InvocationTargetException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import me.majhrs16.lib.minecraft.BukkitUtils;

// ONLY FUNCTIONAL FOR BUKKIT 1.16
public class Advancements implements Listener {
	private final ChatTranslator plugin  = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API  = ChatTranslatorAPI.getInstance();
	private static final Map<Advancement, String[]> CACHE = new ConcurrentHashMap<>();

	@EventHandler
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		if (plugin.isDisabled()) return;

		Advancement advancement = event.getAdvancement();

		String[] data = CACHE.computeIfAbsent(advancement, k -> {
			Object advancementDisplay = getAdvancementDisplay(advancement);

			if (advancementDisplay == null) return new String[] { null, null };

			String title       = getAdvancementString(advancementDisplay, "a");
			String description = getAdvancementString(advancementDisplay, "b");

			plugin.logger.warn("Title: %s, Description: %s", title, description);

			return new String[] { title, description };
		});

		if (data[0] == null || data[1] == null) return;

		CACHE.put(advancement, data);

		Message model = util.createChat(
			event.getPlayer(),
			new String[] { data[0] },
			util.convertStringToLang("en"),
			API.getLang(event.getPlayer()),
			"advancement");

		model.getToolTips().setTexts(data[1]);

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::broadcast);
	}

	private Object getAdvancementDisplay(Advancement advancement) {
		try {
			Object handle = advancement.getClass().getMethod("getHandle").invoke(advancement);
			return handle.getClass().getDeclaredMethod("c").invoke(handle);

		} catch (NoSuchMethodException
				 | IllegalAccessException
				 | InvocationTargetException e) {
			plugin.logger.error("Error while getting advancement: %s", e);
			return null;
		}
	}

	private String getAdvancementString(Object advancementDisplay, String methodName) {
		try {
			Object IChatBaseComponent = advancementDisplay.getClass().getMethod(methodName).invoke(advancementDisplay);
			return (String) IChatBaseComponent.getClass().getMethod("getString").invoke(IChatBaseComponent);

		} catch (Exception e) {
			plugin.logger.error("Error while getting advancement string: %s", e);
			return null;
		}
	}
}