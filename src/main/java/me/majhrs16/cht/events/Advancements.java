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
import java.lang.reflect.Field;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import me.majhrs16.lib.minecraft.BukkitUtils;

public class Advancements implements Listener {
	private final ChatTranslator plugin  = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API  = ChatTranslatorAPI.getInstance();
	private static final Map<Advancement, String[]> CACHE = new ConcurrentHashMap<>();
	@EventHandler
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		Advancement advancement = event.getAdvancement();

		String[] data = CACHE.computeIfAbsent(advancement, k -> {
			Object advancementDisplay;
			try {
				Object handle = advancement.getClass().getMethod("getHandle").invoke(advancement);
				advancementDisplay = handle.getClass().getMethod("c").invoke(handle);

			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				plugin.logger.error("Error while getting advancement: %s", e);
				return null;
			}

			String title       = getAdvancementString(advancementDisplay, "a");
			String description = getAdvancementString(advancementDisplay, "b");

			return new String[] { title, description };
		});

		if (data == null || data[0] == null || data[1] == null) return;

		Message model = util.createChat(
			event.getPlayer(),
			new String[] { data[0] },
			util.convertStringToLang("en"),
			API.getLang(event.getPlayer()),
			"advancement");

		model.getToolTips().setTexts(data[1]);

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::broadcast);
	}

	private String getAdvancementString(Object advancementDisplay, String methodName) {
		try {
			Field advancementMessageField = advancementDisplay.getClass().getDeclaredField(methodName);
			advancementMessageField.setAccessible(true);
			Object advancementMessage = advancementMessageField.get(advancementDisplay);
			return (String) advancementMessage.getClass().getMethod("getString").invoke(advancementMessage);

		} catch (Exception e) {
			plugin.logger.error("Error while getting advancement string: %s", e);
			return null;
		}
	}
}