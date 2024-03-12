package me.majhrs16.cht.util.cache;

public enum Dependencies {
	ChatManager	("me.h1dd3nxn1nja.chatmanager.Main"),
	Chatty		("ru.mrbrikster.chatty.api.ChattyApi"),
	PAPI		("me.clip.placeholderapi.PlaceholderAPI"),
	LP          ("me.lucko.luckperms.bukkit.loader.BukkitLoaderPlugin"),
	CE          ("ce.ajneb97.ConditionalEvents");

	private final boolean exist;

	Dependencies(String string) {
		exist = exist(string);
	}

	public boolean exist() {
		return exist;
	}

	public static Boolean exist(String dependency) {
		boolean haveDependency;

		try {
			Class.forName(dependency);
			haveDependency = true;

		} catch (ClassNotFoundException e) {
			haveDependency = false;
		}

		return haveDependency;
	}
}