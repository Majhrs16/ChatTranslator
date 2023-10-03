package majhrs16.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.updater.UpdateChecker;
import majhrs16.cht.util.updater.ConfigUpdater;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.events.CommandListener;
import majhrs16.cht.events.MessageListener;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.exceptions.StorageRegisterFailedException;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.events.SignHandler;
// import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cht.storage.Storage;
import majhrs16.cot.CoreTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.events.Chat;
import majhrs16.cht.util.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;

public class ChatTranslator extends JavaPlugin {
	public YAML signs;
	public YAML config;
	public YAML messages;
	public YAML commands;
	public Storage storage;
	
	private static ChatTranslator plugin;

	private boolean is_disabled = true;
	private ChatTranslatorAPI API;

	private static class Events {
//		public static DiscordTranslator discordTranslator;

		public static boolean installed = false;

		public static CommandListener commandHandler	  = new CommandListener();
		public static MessageListener messageListener	 = new MessageListener();
//		public static TabCompleter tabCompleter		   = new TabCompleter();
		public static AccessPlayer accessPlayer		   = new AccessPlayer();
		public static SignHandler signHandler			 = new SignHandler();
		public static ChatLimiter chatLimiter			 = new ChatLimiter();
		public static Chat chat						   = new Chat();

		static {
			if (Config.TranslateOthers.DISCORD.IF()) {
//				discordTranslator = new DiscordTranslator();
			}
		}
	}

	public void onEnable() {
		plugin = this;

		if (util.getMinecraftVersion() < 5.2)
			Bukkit.getLogger().severe("ChatTranslator don't tested for your version.");

		API = ChatTranslatorAPI.getInstance();

		signs    = new YAML(plugin, "signs.yml");
		config   = new YAML(plugin, "config.yml");
		messages = new YAML(plugin, "messages.yml");
		commands = new YAML(plugin, "commands.yml");
		storage  = new Storage();

		try {
			messages.register();
			commands.register();
			config.register();
			signs.register();

		} catch (ParseYamlException e) {
			Bukkit.getLogger().severe("[ERRFFF] FATAL");
			return;
		}

		Texts.reload();

		try {
			new ConfigUpdater();
			storage.register();

		} catch (StorageRegisterFailedException e) {
			Message from = util.getDataConfigDefault();
			from.setMessages(e.getMessage());
				API.sendMessage(from);

			onDisable();
			return;
		}

//		registerCommands();
		registerEvents();

		Message from = util.getDataConfigDefault();

		from.setMessages(Texts.get("separator.horizontal"));
			API.sendMessage(from);

		from.setMessages(Texts.get("separator.vertical"));
			API.sendMessage(from);

		if (Charset.defaultCharset().name().equals("UTF-8")) {
			from.setMessages(Texts.get("plugin.is-UTF-8.yes"));
				API.sendMessage(from);

			from.setMessages(Texts.get("plugin.title.UTF-8"));
				API.sendMessage(from);

			from.setMessages(Texts.get("separator.vertical"));
				API.sendMessage(from);

		} else {
			from.setMessages(Texts.get("plugin.is-UTF-8.no"));
				API.sendMessage(from);

			from.setMessages(Texts.get("plugin.title.text"));
				API.sendMessage(from);
		}

		from.setMessages(Texts.get("plugin.enable"));
			API.sendMessage(from);

		if (Config.CHECK_UPDATES.IF())
			new UpdateChecker(Bukkit.getConsoleSender());

		from.setMessages(Texts.get("separator.vertical"));
			API.sendMessage(from);

		from.setMessages(Texts.get("separator.horizontal"));
			API.sendMessage(from);

		setDisabled(false);
	}

	public void onDisable() {
		if (isDisabled())
			return;

		unregisterEvents();

		majhrs16.cht.util.ChatLimiter.chat.clear();

		Message from = util.getDataConfigDefault();

		from.setMessages(Texts.get("separator.horizontal"));
			API.sendMessage(from);

		from.setMessages(Texts.get("separator.vertical"));
			API.sendMessage(from);

		from.setMessages(Texts.get("plugin.title.text"));
			API.sendMessage(from);

		storage.unregister();

		from.setMessages(Texts.get("plugin.disable"));
			API.sendMessage(from);

		from.setMessages(Texts.get("separator.vertical"));
			API.sendMessage(from);

		from.setMessages(Texts.get("separator.horizontal"));
			API.sendMessage(from);

		setDisabled(true);
	}


	public static ChatTranslator getInstance() {
		return plugin;
	}

	public boolean isDisabled() {
		return is_disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.is_disabled = isDisabled;
	}

	/*
	public void registerCommands() { // Sino se registran los comandos en el plugin,yml, no te dejara. Asi de simple.
		for (String key : commands.get().getKeys(false)) {
			if (!key.equals("config-version")) {
				PluginCommand pc = getCommand(key);

				if (pc == null) {
					Message from = util.getDataConfigDefault();

					from.setMessages("&cNo fue posible registrar el comando raiz&f: &b`" + key + "`");
						API.sendMessage(from);

				} else {
					CommandWrapper ch = new CommandWrapper(key);
					pc.setExecutor(ch);
				}
			}
		}

//		MainCommand main_command = new MainCommand(); 
//		getCommand("chattranslator").setExecutor(main_command);
//		getCommand("cht").setExecutor(main_command);
	}*/

	public void registerEvents() {
		if (Events.installed)
			return;

		PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(Events.messageListener, this);
			pm.registerEvents(Events.commandHandler, this);
//			if (util.getMinecraftVersion() > 13.0) // 1.13.0
//				pm.registerEvents(Events.tabCompleter, this);
			pm.registerEvents(Events.accessPlayer, this);
			pm.registerEvents(Events.signHandler, this);
			pm.registerEvents(Events.chat, this);
			Events.chatLimiter.start();

		registerDiscordBot();

		if (Dependencies.PAPI.exist())
			new CoreTranslator().register(); // Expansion de ChT para PAPI: CoT.

		Events.installed = true;
	}

	public void registerDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {/*
			if (!Events.discordTranslator.connect()) {
				Message from = util.getDataConfigDefault();

				from.setMessages(Texts.PLUGIN.TITLE.TEXT + "&cNo se pudo iniciar el bot de Discord.\n    Por favor verifique &bconfig&f.&bbot-token&f.");
					API.sendMessage(from);
			}*/
		}
	}

	public void unregisterEvents() {
		if (!Events.installed) 
			return;

		HandlerList.unregisterAll(Events.messageListener);
		HandlerList.unregisterAll(Events.commandHandler); ////////////////////////////////////////////////////////////
		HandlerList.unregisterAll(Events.accessPlayer);
		HandlerList.unregisterAll(Events.signHandler);
		HandlerList.unregisterAll(Events.chat);
		Events.chatLimiter.stop();

		unregisterDiscordBot();

//		Events.installed = false;
	}

	public void unregisterDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {/*
			Events.discordTranslator.unregisterCommands();
			Events.discordTranslator.disconnect();
			*/
		}
	}
}