package majhrs16.dst;

import majhrs16.cht.util.cache.Config;
import majhrs16.dst.utils.Utils;

import majhrs16.cht.ChatTranslator;
import org.bukkit.Bukkit;

import java.io.RandomAccessFile;
import java.io.File;

import java.util.TimerTask;
import java.util.Timer;

public class ToDiscordLogger {
	private Timer timer;
	public final int MAX_LENGTH_MESSAGE = 1990;
	private final ChatTranslator plugin = ChatTranslator.getInstance();

	public void start() {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new LogReaderTask(), 0, 6000);
	}

	public void stop() {
		if (timer != null)
			timer.cancel();
	}

	private class LogReaderTask extends TimerTask {
		private long lastFileSize = 0;
		private static final String LOG_FILE_PATH = "logs/latest.log";

		@Override
		public void run() {
			if (!Config.TranslateOthers.DISCORD.IF() || DiscordTranslator.getJda() == null)
				return;

			try {
				File logFile = new File(LOG_FILE_PATH);
				long fileSize = logFile.length();

				if (fileSize > lastFileSize) {
					// Read new content from the log file
					RandomAccessFile randomAccessFile = new RandomAccessFile(logFile, "r");
					randomAccessFile.seek(lastFileSize);
					byte[] buffer = new byte[MAX_LENGTH_MESSAGE];
					int bytesRead = randomAccessFile.read(buffer);

					while (bytesRead != -1) {
						String newLogContent = new String(buffer, 0, bytesRead);
						// Send the new log content to Discord channels
						try {
							sendToDiscord(newLogContent);

						} catch (Exception e) {
							Bukkit.getLogger().warning("[!] " + e.toString());
							Thread.sleep(5000);
							continue;
						}

						bytesRead = randomAccessFile.read(buffer);
					}

					lastFileSize = fileSize;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String removeColorCodes(String input) {
		String regex = "\u001B\\[[;\\d]*m";
		return input.replaceAll(regex, "");
	}

	private int sendToDiscord(String message) throws Exception {
		Utils.Integer ok = new Utils.Integer(0);

		// Iterate through configured channels and send the message to each one
		Utils.broadcast(
			"discord.channels.console",
			channel -> {
				channel.sendMessage(removeColorCodes("```" + message + "```")).queue();
				ok.getAndIncrement();
			}
		);

		return ok.get();
	}
}