package me.majhrs16.cht;

import me.majhrs16.cht.commands.utils.TranslateYaml; // Experimental ...

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.updater.CommandsUpdater;
import me.majhrs16.cht.commands.dst.DiscordLinker;
import me.majhrs16.cht.util.updater.UpdateChecker;
import me.majhrs16.cht.util.updater.ConfigUpdater;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.util.cache.Dependencies;
import me.majhrs16.cht.commands.CommandHandler;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.LoggerListener;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.storage.Storage;
import me.majhrs16.cht.events.Metrics;
import me.majhrs16.cht.commands.cht.*;
import me.majhrs16.cht.events.*;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;

import me.majhrs16.lib.minecraft.events.CommandListener;
import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.minecraft.plugin.PluginBase;
import me.majhrs16.lib.storages.YAML;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.dst.utils.DiscordChat;

import me.majhrs16.cot.CoreTranslator;

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
	private InternetCheckerAsync internetCheckerAsync;

	private boolean is_disabled = true;
	private final LoggerListener loggerListener = new LoggerListener();

	public void onLoad() {
		instance       = this;
		metrics        = new Metrics(instance, 20251);
		logger.registerLogger(loggerListener);

		API = ChatTranslatorAPI.getInstance();

		super.onLoad();
	}

	public void onEnable() {
		super.onEnable();

		Message from = new Message();

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

		Message from = new Message();

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

		API.sendMessage(from.format("plugin.disable"));

		API.sendMessage(from.format("plugin.separator.vertical"));
		API.sendMessage(from.format("plugin.separator.horizontal"));

		storage.unregister();
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
				Texts.reload();
				new ConfigUpdater();
				new CommandsUpdater();
			storage.register();

		} catch (StorageRegisterFailedException e) {
			throw new ParseYamlException(e);
		}

		commandHandler = new CommandHandler(commandManager, commands);
	}

	public void registerCommands() {
//      ChT
		commandManager.addExecutor("resetterConfig", new ResetterConfig());
		commandManager.addExecutor("showVersion", new ShowVersion());
		commandManager.addExecutor("privateChat", new PrivateChat());
		commandManager.addExecutor("setterlang", new SetterLang());
		commandManager.addExecutor("reloader", new Reloader());
		commandManager.addExecutor("toggler", new Toggler());

//      DST
		commandManager.addExecutor("discordLinker", new DiscordLinker());

//		UTILS
		commandManager.addExecutor("translateYaml", new TranslateYaml());
	}

	protected void unregisterCommands() {
//      ChT
		commandManager.removeExecutor("resetterConfig");
		commandManager.removeExecutor("showVersion");
		commandManager.removeExecutor("privateChat");
		commandManager.removeExecutor("setterlang");
		commandManager.removeExecutor("reloader");
		commandManager.removeExecutor("toggler");

//      DST
		commandManager.removeExecutor("discordLinker");

//		UTILS
		commandManager.removeExecutor("translateYaml");
	}

	public void registerEvents() {
		metrics.addCustomChart(new Metrics.DrilldownPie("used_extensions", new UsedExtensionsMetric()));

		eventManager.addExecutor("commandListener", new CommandListener(commandHandler, commands));
		eventManager.addExecutor("messageListener", new MessageListener());
		eventManager.addExecutor("accessPlayer", new AccessPlayer());
		eventManager.addExecutor("deathPlayer", new DeathPlayer());
		eventManager.addExecutor("signs", new Signs());
		eventManager.addExecutor("chat", new Chat());

		internetCheckerAsync = new InternetCheckerAsync();
		internetCheckerAsync.start();

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

		internetCheckerAsync.stop();
		chatLimiter.stop();
		metrics.shutdown();

		if (Dependencies.PAPI.exist())
			coreTranslator.unregister();

		unregisterDiscordBot();
	}

	public void registerDiscordBot() {
		if (!Config.TranslateOthers.DISCORD.IF())
			return;

		Message from = new Message();

		try {
			discordTranslator.connect(
				config.get().getString("discord.bot-token")
			);

			discordTranslator.registerEvents();
			discordTranslator.registerCommands();

			if (isDisabled()) {
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

		} catch (InvalidTokenException e) {
			discordTranslator.disconnect();

			from.format("discord-translator.load.error.token",
				format -> format.replace("%reason%", e.toString())
			);

		} catch (IllegalStateException e) {
			discordTranslator.disconnect();

			from.format("discord-translator.load.error.intents",
				format -> format.replace("%reason%", e.toString())
			);

		} catch (Exception e) {
			discordTranslator.disconnect();

			from.format("discord-translator.load.error",
				format -> format.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(from);
	}

	public void unregisterDiscordBot() {
		if (!Config.TranslateOthers.DISCORD.IF())
			return;

		Message from = new Message();

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
			}

			discordTranslator.unregisterEvents();
			discordTranslator.unregisterCommands();

			discordTranslator.disconnect();

			from.format("discord-translator.unload.done.console");

		} catch (Exception e) {
			from.format("discord-translator.unload.error",
				format -> format.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(from);
	}

	public static ChatTranslator getInstance() {
		return instance;
	}
}