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
	private final Pattern phrase_texts = Pattern.compile("\\b[\\w\\s]+\\b");
	private final Consumer[] applyFormatsVersions = new Consumer[] {
			this::applyFormatsVersion1  // v2.0
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

//		Inicializar el plugin por primera vez.
		if (version_original == 0) {
//			"Actualizar" a la ultima version disponible en el codigo.
			version = applyFormatsVersions.length;
		}

		if (!Config.UPDATE_CONFIG.IF()) {
			formats.set(path, version);
			plugin.formats.save();
			return;
		}

		plugin.logger.debug("version.original: " + version_original);

		// Actualizar gradualmente por el historial de versiones.
		for (int i = Math.max(0, version); i < applyFormatsVersions.length; i++) {
			applyFormatsVersions[i].accept(formats, from);
			version = i + 1;
		}

		formats.set(path, version);
		plugin.formats.save();

		if (version > version_original) {
			API.sendMessage(from.format("updaters.formats.done", null, s -> s
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
			String preformat = format;

//			Eliminar secuencias de escape
			preformat = preformat.replace("\\t", "");

//			Eliminar secuencias &x
			preformat = preformat.replaceAll("&[a-f0-9]", "");

//			Eliminar variables %var%
			preformat = preformat.replaceAll("%[^%]*%", "");

//			Eliminar variables $var$
			preformat = preformat.replaceAll("\\$[^$]*\\$", "");

//			Eliminar cualquier cosa entre llaves
			preformat = preformat.replaceAll("\\{[^}]*}", "");

			Matcher matcher = phrase_texts.matcher(preformat);
			while (matcher.find()) {
				String text = matcher.group(0);

				texts.add(text);

//				Reemplazar texto en el formato con índice
				format = format.replace(text, String.format("{%s}", i));
				i++;
			}

			formats.add(format);
		}

		return new List[] {formats, texts};
	}

	private void fixAccess(ConfigurationSection old_formats, String key, String replacement) {
		List<String> messages = old_formats.getStringList(key);
		messages.replaceAll(s -> s.replaceAll("[%$]ct_messages[$%]", replacement));
		old_formats.set(key, messages);
	}

	private void fixGroupFormatAccess(ConfigurationSection old_formats, String key, String replacement) {
		fixAccess(old_formats, key + ".messages", replacement);
		fixAccess(old_formats, key + ".toolTips", replacement);
	}

	void applyFormatsVersion1(FileConfiguration new_formats, Message from) {
		FileConfiguration config = plugin.config.get();
		ConfigurationSection old_formats = config.getConfigurationSection("formats");

		if (old_formats == null)
			return;

		String exit = "ha abandonado el juego";
		String entry = "se ha unido al juego";

		fixGroupFormatAccess(old_formats, "to_exit", exit);
		fixGroupFormatAccess(old_formats, "to_entry", entry);
		fixGroupFormatAccess(old_formats, "from_exit", exit);
		fixGroupFormatAccess(old_formats, "from_entry", entry);

//		NO FUNCIONAN????
		fixGroupFormatAccess(old_formats, "to_exit_console", exit);
		fixGroupFormatAccess(old_formats, "to_exit_discord", exit);
		fixGroupFormatAccess(old_formats, "to_entry_discord", entry);

		for (String key : old_formats.getKeys(false)) {
			List<String>[] messages  = upgradeGroupFormat(old_formats, key + ".messages");
			List<String>[] tool_tips = upgradeGroupFormat(old_formats, key + ".toolTips");

			new_formats.set(key + ".messages.texts", messages[1]);
			new_formats.set(key + ".toolTips.texts", tool_tips[1]);
			new_formats.set(key + ".messages.formats", messages[0]);
			new_formats.set(key + ".toolTips.formats", tool_tips[0]);

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
