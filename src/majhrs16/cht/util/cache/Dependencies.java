package majhrs16.cht.util.cache;

public enum Dependencies {
	ProtocolLib	("com.comphenix.protocol.ProtocolLib"),
	ChatManager	("me.h1dd3nxn1nja.chatmanager.Main"),
	DiscordSRV	("github.scarsz.discordsrv.DiscordSRV"),
	Chatty		("ru.mrbrikster.chatty.api.ChattyApi"),
	PAPI		("me.clip.placeholderapi.PlaceholderAPI");

	private boolean exist;

	Dependencies(String string) {
		exist = exist(string);
	}

	public boolean exist() {
		return exist;
	}

	public static Boolean exist(String dependency) {
		Boolean haveDependency = null;

		try {
			Class.forName(dependency);
			haveDependency = true;

		} catch (ClassNotFoundException e) {
			haveDependency = false;
		}

		return haveDependency;
	}
}