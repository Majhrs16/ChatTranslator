package majhrs16.cht;

import majhrs16.cht.exceptions.StorageRegisterFailedException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.updater.CommandsUpdater;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.updater.UpdateChecker;
import majhrs16.cht.util.updater.ConfigUpdater;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.events.MessageListener;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.AccessPlayer;
import majhrs16.cht.events.SignHandler;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.events.OnCommand;
import majhrs16.cht.util.ChatLimiter;
import majhrs16.cht.storage.Storage;
import majhrs16.cot.CoreTranslator;
import majhrs16.lib.storages.YAML;
import majhrs16.cht.events.Chat;
import majhrs16.dst.utils.Utils;
import majhrs16.cht.util.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;

public class ChatTranslator extends JavaPlugin {
	public YAML signs;
	public YAML config;
//	public YAML formats;
	public YAML messages;
	public YAML commands;
	public Storage storage;

	private ChatTranslatorAPI API;
	private static ChatTranslator plugin;

	private boolean is_disabled = true;

	private static class Events {

		public static boolean is_installed = false;

		public static DiscordTranslator discordTranslator = new DiscordTranslator();
		public static MessageListener messageListener     = new MessageListener();
		public static AccessPlayer accessPlayer           = new AccessPlayer();
		public static SignHandler signHandler             = new SignHandler();
		public static ChatLimiter chatLimiter             = new ChatLimiter();
		public static OnCommand onCommand                 = new OnCommand();
		public static Chat chat	                          = new Chat();
	}

	public void onEnable() {
		plugin = this;

		if (util.getMinecraftVersion() < 5.2)
			Bukkit.getLogger().severe("ChatTranslator don't tested for your version.");

		API = ChatTranslatorAPI.getInstance();

		signs    = new YAML(plugin, "signs.yml");
		config   = new YAML(plugin, "config.yml");
//		formats  = new YAML(plugin, "formats.yml");
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
            new CommandsUpdater();
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
		if (Events.is_installed)
			return;

		PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(Events.messageListener, this);
//			if (util.getMinecraftVersion() > 13.0) // 1.13.0
//				pm.registerEvents(Events.tabCompleter, this);
			pm.registerEvents(Events.accessPlayer, this);
			pm.registerEvents(Events.signHandler, this);
			pm.registerEvents(Events.onCommand, this);
			pm.registerEvents(Events.chat, this);
			Events.chatLimiter.start();

		if (Dependencies.PAPI.exist())
			new CoreTranslator().register(); // Expansion de ChT para PAPI: CoT.

		registerDiscordBot();

		Events.is_installed = true;
	}

	public void unregisterEvents() {
		if (!Events.is_installed)
			return;

		Events.is_installed = false;

		HandlerList.unregisterAll(Events.messageListener);
		HandlerList.unregisterAll(Events.accessPlayer);
		HandlerList.unregisterAll(Events.signHandler);
		HandlerList.unregisterAll(Events.onCommand);
		HandlerList.unregisterAll(Events.chat);
		Events.chatLimiter.stop();

		unregisterDiscordBot();
	}

	public void registerDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {
			if (Events.discordTranslator.connect()) {
				Events.discordTranslator.registerCommands();
				Events.discordTranslator.registerEvents();

				if (!Events.is_installed)
					Utils.broadcast(
						"discord.channels.server-status",
						channel -> Utils.sendMessageEmbed(channel, "Servidor encendido!, :D", null, 0x00FF00)
					);

			} else {
				Message from = util.getDataConfigDefault();

				from.setMessages(Texts.getString("plugin.title.text") + "&cNo se pudo iniciar el bot de Discord.\n    Por favor verifique &bconfig&f.&bbot-token&f.");
					API.sendMessage(from);
			}
		}
	}

	public void unregisterDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {
			if (!Events.is_installed)
				Utils.broadcast(
						"discord.channels.server-status",
						channel -> Utils.sendMessageEmbed(channel, "Servidor apagado, :(", null, 0xFF0000)
				);

			Events.discordTranslator.unregisterEvents();
			Events.discordTranslator.unregisterCommands();
			Events.discordTranslator.disconnect();
		}
	}
}