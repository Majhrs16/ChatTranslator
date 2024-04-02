package me.majhrs16.cht.commands.utils;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.storages.YAML;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class TranslateYaml implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	private final Pattern variables     = Pattern.compile("[%$][A-Za-z0-9_]+[%$]");

	public boolean apply(CommandSender sender, String path, String[] args) {
		String from = args[0];
		String to = args[1];

		YAML[] yamls = new YAML[args.length];

		for (int i = 2; i < args.length; i++) {
			yamls[i] = new YAML(plugin.getDataFolder().getPath(), args[i]);

			try {
				yamls[i].register();

			} catch (ParseYamlException e) {
				plugin.logger.error(e.toString());
			}
		}

		for (YAML yaml : yamls) {
			if (yaml == null) {
				plugin.logger.warn("Found null YAML!");
				continue;
			}

			plugin.logger.info("translating any file.yml...!");

			FileConfiguration config = yaml.get();
			for (String configPath : config.getKeys(true)) {
				if (config.isList(configPath)) {
					List<String> texts = config.getStringList(configPath);
					texts.replaceAll(text -> translate(from, to, text));
					config.set(configPath, texts);

				} else if (config.isString(configPath)) {
					String text = config.getString(configPath);

					if (text == null)
						continue;

					text = translate(from, to, text);
					config.set(configPath, text);
				}
			}

			plugin.logger.info("Translated any file.yml!");
		}

		for (YAML yaml : yamls) {
			if (yaml == null)
				continue;

			yaml.save();
		}

		return true;
	}

	private String translate(String from, String to, String text) {
		plugin.logger.debug("Original: %s", text);

		if (text.equals(text.toUpperCase())
				|| text.equals("null")
				|| text.isEmpty())
			return text;

		Matcher matcher = variables.matcher(text);

		ArrayList<String> escapes = new ArrayList<>();

		int i = 0;
		while (matcher.find()) {
			text = text.replace(matcher.group(0), "[" + i + "]");
			plugin.logger.warn(matcher.group(0));
			escapes.add(matcher.group(0));
			i++;
		}

		text = API.getTranslator().translate(text, from, to);

		i = 0;
		for (String escape : escapes) {
			text = text.replace("[" + i + "]", escape);
			i++;
		}

		plugin.logger.debug("Translated: %s", text);

		return text;
	}
}