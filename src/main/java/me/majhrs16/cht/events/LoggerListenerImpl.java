package me.majhrs16.cht.events;

import me.majhrs16.cht.util.cache.Config;

import org.bukkit.Bukkit;

public class LoggerListenerImpl implements me.majhrs16.lib.logger.LoggerListener<String> {
	private String format(String format, Object... args) {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		return stackTrace[6].getClassName() + "." + stackTrace[6].getMethodName() + ": " + String.format(format, args);
	}

	public String debug(String format, Object... args) {
		if (!Config.DEBUG.IF())
			return null;

		String result = format(format, args);
		Bukkit.getLogger().warning(result);
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