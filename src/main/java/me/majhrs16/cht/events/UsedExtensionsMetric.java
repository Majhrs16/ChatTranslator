package me.majhrs16.cht.events;

import me.majhrs16.cht.util.cache.Dependencies;
import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cot.CoreTranslator;

import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

public class UsedExtensionsMetric implements Callable<Map<String, Map<String, Integer>>> {

	private static class PluginInfo {
		String name;
		String version;

		PluginInfo(String name, String version) {
			this.name = name;
			this.version = version;
		}

		PluginInfo(String name) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(name);

			if (plugin == null)
				return;

			this.version = plugin.getDescription().getVersion();
			this.name = name;
		}
	}

	@Override
	public Map<String, Map<String, Integer>> call() throws Exception {
		Map<String, Map<String, Integer>> map = new HashMap<>();

		addPlugins(map,
			new PluginInfo("ConditionalEvents"),
			new PluginInfo("PlaceholderAPI"),
			new PluginInfo("LuckPerms")
		);

		if (Dependencies.PAPI.exist())
			addPlugins(map, new PluginInfo("CoreTranslator", CoreTranslator.version));

		if (Config.TranslateOthers.DISCORD.IF())
			addPlugins(map, new PluginInfo("DiscordTranslator", DiscordTranslator.version));

		return map;
	}

	private void addPlugins(Map<String, Map<String, Integer>> map, PluginInfo... plugins) {
		for (PluginInfo plugin : plugins) {
			if (plugin.name == null || plugin.version == null)
				continue;

			Map<String, Integer> entry = new HashMap<>();
			entry.put(plugin.version, 1);
			map.put(plugin.name, entry);
		}
	}
}