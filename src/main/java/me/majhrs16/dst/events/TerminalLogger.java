package me.majhrs16.dst.events;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.InternetCheckerAsync;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.*;

import me.majhrs16.dst.utils.TerminalAnsiReader;
import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.dst.utils.DiscordChat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.File;

public class TerminalLogger {
	private Timer timer;
	private static String LOG_FILE_PATH;

	private long lastFileLine = 0;
	private long lastFileByte = 0;
	private final int MAX_LENGTH_MESSAGE  = 1900;
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	public static final Map<String, String> ANSI_TO_DISCORD_MAP = createAnsiMap();

	public TerminalLogger() {
		Message from = new Message();

		try {
			if (util.getMinecraftVersion() <= 8.0)
				throw new RuntimeException("Incompatibly Minecraft version.");

			TerminalAnsiReader.injectReader();

			LOG_FILE_PATH = "logs/DST.log";

			from.format("discord-translator.log4j.done", format -> format
				.replace("%logger%", LOG_FILE_PATH)
			);

		} catch (Throwable e) {
			LOG_FILE_PATH = "logs/latest.log";

			from.format("discord-translator.log4j.error", format -> format
				.replace("%logger%", LOG_FILE_PATH)
				.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(from);
	}

	public void start() {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new LogReaderTask(), 0, 6000);
	}

	public void stop() {
		if (timer != null) timer.cancel();
	}

	private class LogReaderTask extends TimerTask {
		@Override
		public void run() {
			if (DiscordTranslator.isDisabled())
				return;

			try {
				File logFile = new File(LOG_FILE_PATH);

//				Restablecer contadores en caso de una posible rotacion de log.
				if (lastFileByte > logFile.length()) {
					lastFileByte = 0;
					lastFileLine = 0;
				}

				try (Stream<String> linesStream = Files.lines(Paths.get(LOG_FILE_PATH), StandardCharsets.UTF_8)) {
					AtomicReference<StringBuilder> block = new AtomicReference<>(new StringBuilder());

					linesStream.skip(lastFileLine).forEachOrdered(line -> {
						if ((block.get().length() + line.length()) > MAX_LENGTH_MESSAGE) {
							if (InternetCheckerAsync.isInternetAvailable()) {
								if (block.get().length() > 0) {
									sendToDiscord(block.get().toString());
									block.set(new StringBuilder());
								}

								while (line.length() > MAX_LENGTH_MESSAGE) {
									line = line.substring(0, MAX_LENGTH_MESSAGE);
									sendToDiscord(line);
								}

							} else {
								API.sendMessage(new Message().format("discord-translator."));
								return;
							}
						}

						block.get().append(line).append("\n");
						lastFileByte += line.getBytes().length;
						lastFileLine ++;
					});

					if (block.get().length() > 0) {
						sendToDiscord(block.get().toString());
					}
				}

			} catch (Exception e) {
				plugin.logger.error(e.toString());
			}
		}
	}

	@Deprecated
	public String removeAnsiCodes(String input) {
		return input.replaceAll("\u001B\\[[;\\d]*m", "");
	}

	public String replaceAnsiCodes(String input) {
		for (Map.Entry<String, String> entry : ANSI_TO_DISCORD_MAP.entrySet())
			input = input.replace(entry.getKey(), entry.getValue());
		return input;
	}

	private static Map<String, String> createAnsiMap() {
//		Codigos de color ANSI originales:
//			[30m0[34m1[32m2[36m3[31m4[35m5[33m6[37m7[90m8[94m9[92ma[96mb[91mc[95md[93me[97mf[mr

//		Codigos de color ANSI re-mapeados:
		Map<String, String> map = new HashMap<>();
			map.put("\u001B[94m", "\u001B[34m"); // 9 > 1
			map.put("\u001B[92m", "\u001B[32m"); // A > 2
			map.put("\u001B[96m", "\u001B[36m"); // B > 3
			map.put("\u001B[91m", "\u001B[31m"); // C > 4
			map.put("\u001B[95m", "\u001B[35m"); // D > 5
			map.put("\u001B[93m", "\u001B[33m"); // E > 6
			map.put("\u001B[97m", "\u001B[37m"); // F > 7
			map.put("\u001B[m",   "\u001B[0m");  // RESET
		return map;
	}

	private void sendToDiscord(String message) {
		List<String> channels = DiscordChat.getChannels("discord.channels.console");

		if (DiscordChat.broadcast(channels, "```ansi\n" + replaceAnsiCodes(message).replaceAll("\n+", "\n") + "```") < channels.size())
			API.sendMessage(new Message().format("discord-translator.terminalLogger.sendDiscord"));
	}
}
