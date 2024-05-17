package me.majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class FormatsUpdater {
	public int version;

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private final Pattern texts_words = Pattern.compile("(?<!(&[a-z0-9]|[.,%${]))(\\b\\w+\\b)(?!(&[a-z0-9]|[}$%,.]))");
	private final Consumer[] applyConfigVersions = new Consumer[] {
			this::applyConfigVersion8  // v2.0
	};

	@FunctionalInterface
	private interface Consumer {
		void accept(FileConfiguration cfg, Message msg);
	}

	public FormatsUpdater() {
		FileConfiguration formats = plugin.formats.get();

		String path = "formats-version";
		if (!formats.contains(path))
			formats.set(path, -1);

		version = formats.getInt(path);
		int version_original = version;

		Message from = new Message();

		if (!Config.UPDATE_CONFIG.IF()) {
			formats.set(path, version);
			plugin.formats.save();
			return;
		}

		plugin.logger.debug("version.original: " + version_original);

		// Actualizar gradualmente por el historial de versiones.
		for (int i = Math.max(0, version); i < applyConfigVersions.length; i++) {
			applyConfigVersions[i].accept(formats, from);
			version = i + 1;
		}

		formats.set(path, version);
		plugin.formats.save();

		if (version > version_original) {
			API.sendMessage(from.format("formatsUpdater.done", null, s -> s
				.replace("%original%", "" + version_original)
				.replace("%new%", "" + version)
			));
		}
	}

	private List<String>[] upgradeGroupFormat(ConfigurationSection section, String path) {
		List<String> texts   = new ArrayList<>();
		List<String> formats = new ArrayList<>();
		int i = 0;

		for (String format : section.getStringList(path)) {
			Matcher matcher = texts_words.matcher(format);
			if (matcher.find()) {
				String text = matcher.group(1);

				if (text != null) {
					texts.add(text);
					formats.add(format.replace(text, String.format("{%s}", i)));
				}

			} else {
				formats.add(format);
			}

			i++;
		}

		return new List[] {texts, formats};
	}

	private void applyConfigVersion8(FileConfiguration new_formats, Message from) {
		FileConfiguration config = plugin.config.get();
		ConfigurationSection old_formats = config.getConfigurationSection("formats");

		if (old_formats == null)
			return;

		for (String key : old_formats.getKeys(false)) {
			List<String>[] messages = upgradeGroupFormat(old_formats, key + ".messages");
			List<String>[] tool_tips = upgradeGroupFormat(old_formats, key + ".toolTips");

			new_formats.set(key + ".messages.texts", messages[0]);
			new_formats.set(key + ".toolTips.texts", tool_tips[0]);
			new_formats.set(key + ".messages.formats", messages[1]);
			new_formats.set(key + ".toolTips.formats", tool_tips[1]);

			for (String sound : old_formats.getStringList(key + ".sounds")) {
				String[] parts = sound.replace(" ", "").split(":");

				if (parts.length != 3)
					continue;

				new_formats.set(key + ".sounds." + parts[0] + ".volume", parts[1]);
				new_formats.set(key + ".sounds." + parts[0] + ".pitch", parts[2]);
			}
		}

		config.set("formats", null);

		plugin.config.save();

		API.sendMessage(from.format(
			"configUpdater.version9.unsupportedMessagesConfig"
		));
	}
}