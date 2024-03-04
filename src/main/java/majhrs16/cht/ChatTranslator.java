package majhrs16.cht;

import majhrs16.cht.exceptions.StorageRegisterFailedException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.updater.CommandsUpdater;
import majhrs16.cht.util.updater.UpdateChecker;
import majhrs16.cht.util.updater.ConfigUpdater;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Dependencies;
import majhrs16.cht.commands.CommandHandler;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.LoggerListener;
import majhrs16.cht.events.ChatLimiter;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.storage.Storage;
import majhrs16.cht.events.Metrics;
import majhrs16.cot.CoreTranslator;
import majhrs16.cht.commands.cht.*;
import majhrs16.cht.util.util;
import majhrs16.cht.events.*;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;

import majhrs16.lib.minecraft.events.CommandListener;
import majhrs16.lib.exceptions.ParseYamlException;
import majhrs16.lib.minecraft.plugin.PluginBase;
import majhrs16.lib.storages.YAML;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import majhrs16.dst.DiscordTranslator;
import majhrs16.dst.utils.DiscordChat;

import org.bukkit.Bukkit;

public class ChatTranslator extends PluginBase {
	public YAML signs;
	public YAML formats;
	public Storage storage;
	private Metrics metrics;
	private ChatTranslatorAPI API;
	private ChatLimiter chatLimiter;
	private CoreTranslator coreTranslator;
	private CommandHandler commandHandler;
	private static ChatTranslator instance;
	private DiscordTranslator discordTranslator;

	private boolean is_disabled = true;
	private final LoggerListener loggerListener = new LoggerListener();

	public void onEnable() {
		instance       = this;
		metrics        = new Metrics(instance, 20251);
		logger.registerLogger(loggerListener);

		API = ChatTranslatorAPI.getInstance();

		super.onEnable();

		Message from = util.getDataConfigDefault();

		API.sendMessage(from.format("plugin.separator.horizontal"));
		API.sendMessage(from.format("plugin.separator.vertical"));

		if (Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
			API.sendMessage(from.format("plugin.available-UTF-8.true"));
			API.sendMessage(from.format("plugin.title.UTF-8"));
			API.sendMessage(from.format("plugin.separator.vertical"));

		} else {
			API.sendMessage(from.format("plugin.available-UTF-8.false"));
			API.sendMessage(from.format("plugin.title.text"));
		}

		API.sendMessage(from.format("plugin.enable"));

		if (Config.CHECK_UPDATES.IF())
			new UpdateChecker(Bukkit.getConsoleSender());

		API.sendMessage(from.format("plugin.separator.vertical"));
		API.sendMessage(from.format("plugin.separator.horizontal"));

		setDisabled(false);
	}

	public void onDisable() {
		if (isDisabled()) // En caso de crashes,
			return;       // evitar des-cargar algo que ni esta registrado.

		setDisabled(true);

		unregisterEvents();
		unregisterCommands();

		ChatLimiter.clear();

		Message from = util.getDataConfigDefault();

		API.sendMessage(from.format("plugin.separator.horizontal"));
		API.sendMessage(from.format("plugin.separator.vertical"));

		API.sendMessage(from.format("plugin.title.UTF-8"));

		API.sendMessage(from.format("plugin.disable"));
		storage.unregister();

		API.sendMessage(from.format("plugin.separator.vertical"));
		API.sendMessage(from.format("plugin.separator.horizontal"));
	}

	public boolean isDisabled() {
		return is_disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.is_disabled = isDisabled;
	}

	protected void registerStorage() throws ParseYamlException {
		super.registerStorage();

		String folder = getDataFolder().getPath();
		formats = new YAML(folder, "formats.yml");
		signs   = new YAML(folder, "signs.yml");

		formats.register();
		signs.register();

		try {
			storage = new Storage();
				new ConfigUpdater();
				new CommandsUpdater();

				Texts.reload();
			storage.register();

		} catch (StorageRegisterFailedException e) {
			throw new ParseYamlException(e);
		}

		commandHandler = new CommandHandler(commandManager, commands);
	}

	public void registerCommands() {
		commandManager.addExecutor("resetterConfig", new ResetterConfig());
		commandManager.addExecutor("discordLinker", new DiscordLinker());
		commandManager.addExecutor("showVersion", new ShowVersion());
		commandManager.addExecutor("privateChat", new PrivateChat());
		commandManager.addExecutor("setterlang", new SetterLang());
		commandManager.addExecutor("reloader", new Reloader());
		commandManager.addExecutor("toggler", new Toggler());
	}

	protected void unregisterCommands() {
		commandManager.removeExecutor("resetterConfig", new ResetterConfig());
		commandManager.removeExecutor("discordLinker", new DiscordLinker());
		commandManager.removeExecutor("showVersion", new ShowVersion());
		commandManager.removeExecutor("privateChat", new PrivateChat());
		commandManager.removeExecutor("setterlang", new SetterLang());
		commandManager.removeExecutor("reloader", new Reloader());
		commandManager.removeExecutor("toggler", new Toggler());
	}

	public void registerEvents() {
		eventManager.addExecutor("commandListener", new CommandListener(commandHandler, commands));
		eventManager.addExecutor("messageListener", new MessageListener());
		eventManager.addExecutor("accessPlayer", new AccessPlayer());
		eventManager.addExecutor("deathPlayer", new DeathPlayer());
		eventManager.addExecutor("signs", new Signs());
		eventManager.addExecutor("chat", new Chat());

		chatLimiter = new ChatLimiter();
		chatLimiter.start();

		if (Dependencies.PAPI.exist()) {
			coreTranslator = new CoreTranslator();
			coreTranslator.register();
		}

		discordTranslator = new DiscordTranslator();
		registerDiscordBot();
	}

	public void unregisterEvents() {
		eventManager.removeExecutors("messageListener");
		eventManager.removeExecutors("commandListener");
		eventManager.removeExecutors("accessPlayer");
		eventManager.removeExecutors("deathPlayer");
		eventManager.removeExecutors("signs");
		eventManager.removeExecutors("chat");
		chatLimiter.stop();
		metrics.shutdown();

		if (Dependencies.PAPI.exist())
			coreTranslator.unregister();

		unregisterDiscordBot();
	}

	public void registerDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {
			Message from = util.getDataConfigDefault();

			try {
				discordTranslator.connect(
					config.get().getString("discord.bot-token")
				);

				if (isDisabled()) {
					discordTranslator.registerCommands();
					discordTranslator.registerEvents();

					from.format("discord-translator.load.done.discord");

					Message _from = API.formatMessage(from);

					DiscordChat.broadcastEmbed(
						DiscordChat.getChannels("discord.channels.server-status"),
						_from.getMessages().getFormats(),
						_from.getToolTips().getFormats(),
						Integer.parseInt(Texts.get("discord-translator.load.done.discord.color")[0].substring(1), 16)
					);
				}

				from.format("discord-translator.load.done.console");

			} catch (InvalidTokenException | InterruptedException e) {
				discordTranslator.disconnect();

				from.format("discord-translator.load.error",
					format -> format.replace("%reason%", e.toString())
				);
			}

			API.sendMessage(from);
		}
	}

	public void unregisterDiscordBot() {
		if (Config.TranslateOthers.DISCORD.IF()) {
			Message from = util.getDataConfigDefault();

			try {
				if (isDisabled()) {
					from.format("discord-translator.unload.done.discord");

					Message _from = API.formatMessage(from);

					DiscordChat.broadcastEmbed(
						DiscordChat.getChannels("discord.channels.server-status"),
						_from.getMessages().getFormats(),
						_from.getToolTips().getFormats(),
						Integer.parseInt(Texts.get("discord-translator.unload.done.discord.color")[0].substring(1), 16)
					);

					discordTranslator.unregisterEvents();
					discordTranslator.unregisterCommands();
				}

				discordTranslator.disconnect();

				from.format("discord-translator.unload.done.console");

			} catch (Exception e) {
				from.format("discord-translator.unload.error",
					format -> format.replace("%reason%", e.toString())
				);
			}

			API.sendMessage(from);
		}
	}

	public static ChatTranslator getInstance() {
		return instance;
	}
}