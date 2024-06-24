package me.majhrs16.cht.events;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;

import java.nio.file.FileSystems;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class LoggerListenerImpl implements me.majhrs16.lib.logger.LoggerListener<String> {
	private BufferedWriter debugFile;
	private BukkitTask task;
	private final ConcurrentLinkedQueue<String> debugQueue = new ConcurrentLinkedQueue<>();

	public void start() {
		if (!Config.DEBUG.IF())
			return;

		ChatTranslator plugin = ChatTranslator.getInstance();
		String logFilePath = plugin.getDataFolder() + FileSystems.getDefault().getSeparator() + "debug.log";

		try {
			debugFile = new BufferedWriter(new FileWriter(logFilePath, false));

		} catch (IOException e) {
			error(e.toString());
			return;
		}

		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			String log = debugQueue.poll();
			if (log == null) return;

			try {
				debugFile.write(log);
				debugFile.newLine();
				debugFile.flush();

			} catch (IOException e) {
				error(e.toString());
			}

		}, 0L, 5L);
	}

	public void stop() {
		if (!Config.DEBUG.IF())
			return;

		if (task != null)
			task.cancel();

		try {
			if (debugFile != null)
				debugFile.close();

		} catch (IOException e) {
			error(e.toString());
		}
	}

	private String format(String format, Object... args) {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		return stackTrace[6].getClassName() + "." + stackTrace[6].getMethodName() + ": " + String.format(format, args);
	}

	public String debug(String format, Object... args) {
		if (!Config.DEBUG.IF())
			return null;

		String result = format(format, args);
		debugQueue.add(result);
		return result;
	}

	public String info(String format, Object... args) {
		String result = format(format, args);
		Bukkit.getLogger().info(result);
		return result;
	}

	public String warn(String format, Object... args) {
		String result = format(format, args);
		Bukkit.getLogger().warning(result);
		return result;
	}

	public String error(String format, Object... args) {
		String result = format(format, args);
		Bukkit.getLogger().severe(result);
		return result;
	}

	public String reserved(String format, Object... args) {
		return null;
	}
}