package me.majhrs16.cht;

import me.majhrs16.cht.commands.utils.TranslateYaml; // Experimental ...

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.updater.CommandsUpdater;
import me.majhrs16.cht.commands.CommandHandlerImpl;
import me.majhrs16.cht.util.updater.FormatsUpdater;
import me.majhrs16.cht.commands.dst.DiscordLinker;
import me.majhrs16.cht.util.updater.UpdateChecker;
import me.majhrs16.cht.util.updater.ConfigUpdater;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.util.cache.Dependencies;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.storage.Storage;
import me.majhrs16.cht.events.Metrics;
import me.majhrs16.cht.commands.cht.*;
import me.majhrs16.cht.events.*;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import me.majhrs16.lib.minecraft.events.CommandListener;
import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.minecraft.plugin.PluginBase;
import me.majhrs16.lib.storages.YAML;

import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.dst.utils.DiscordChat;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import me.majhrs16.cot.CoreTranslator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public class ChatTranslator extends PluginBase {
	public YAML signs;
	public YAML formats;
	public Storage storage;
	private Metrics metrics;
	private ChatTranslatorAPI API;
	private ChatLimiter chatLimiter;
	private CoreTranslator coreTranslator;
	private static ChatTranslator instance;
	private CommandHandlerImpl commandHandler;
	private DiscordTranslator discordTranslator;
	private InternetCheckerAsync internetCheckerAsync;

	private boolean is_disabled = true;
	private final LoggerListenerImpl loggerListener = new LoggerListenerImpl();

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	public void onLoad() {
		instance       = this;
		metrics        = new Metrics(instance, 20251);
		logger.registerLogger(loggerListener);

		API = ChatTranslatorAPI.getInstance();

		super.onLoad();
	}

	public void onEnable() {
		super.onEnable();

		Message.Builder builder = new Message.Builder();

		API.sendMessage(builder.format("plugin.separator.horizontal").build());
		API.sendMessage(builder.format("plugin.separator.vertical").build());

		if (Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
			API.sendMessage(builder.format("plugin.available-UTF-8.true").build());
			API.sendMessage(builder.format("plugin.title.UTF-8").build());
			API.sendMessage(builder.format("plugin.separator.vertical").build());

		} else {
			API.sendMessage(builder.format("plugin.available-UTF-8.false").build());
			API.sendMessage(builder.format("plugin.title.text").build());
		}

		API.sendMessage(builder.format("plugin.enable").build());

		if (Config.CHECK_UPDATES.IF())
			new UpdateChecker(Bukkit.getConsoleSender());

		API.sendMessage(builder.format("plugin.separator.vertical").build());
		API.sendMessage(builder.format("plugin.separator.horizontal").build());

		setDisabled(false);
	}

	public void onDisable() {
		if (isDisabled()) // En caso de crashes,
			return;       // evitar des-cargar algo que ni está registrado.

		setDisabled(true);

		unregisterEvents();
		unregisterCommands();

		ChatLimiter.clear();

		Message.Builder builder = new Message.Builder();

		API.sendMessage(builder.format("plugin.separator.horizontal").build());
		API.sendMessage(builder.format("plugin.separator.vertical").build());

		if (Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
			API.sendMessage(builder.format("plugin.available-UTF-8.true").build());
			API.sendMessage(builder.format("plugin.title.UTF-8").build());
			API.sendMessage(builder.format("plugin.separator.vertical").build());

		} else {
			API.sendMessage(builder.format("plugin.available-UTF-8.false").build());
			API.sendMessage(builder.format("plugin.title.text").build());
		}

		API.sendMessage(builder.format("plugin.disable").build());

		API.sendMessage(builder.format("plugin.separator.vertical").build());
		API.sendMessage(builder.format("plugin.separator.horizontal").build());

		storage.unregister();
	}

	public boolean isDisabled() {
		return is_disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.is_disabled = isDisabled;
	}

	private YAML registerInternalYAML(YAML yaml, String filenameInternal) throws ParseYamlException {
		try {
			yaml.register();

		} catch (ParseYamlException e) {
			yaml = new YAML(filenameInternal);
			yaml.register();
		}

		return yaml;
	}

	protected void registerStorage() throws ParseYamlException {
		String folder = getDataFolder().getPath();
		config   = new YAML(folder, "config.yml");
		commands = new YAML(folder, "commands.yml");
		formats  = new YAML(folder, "formats.yml");
		signs    = new YAML(folder, "signs.yml");

		List<String> rescueFiles = new ArrayList<>();
		if ((config = registerInternalYAML(config, "config.yml")).isReadonly())
			rescueFiles.add("config.yml");

		if ((commands = registerInternalYAML(commands, "commands.yml")).isReadonly())
			rescueFiles.add("commands.yml");

		if ((formats = registerInternalYAML(formats, "formats.yml")).isReadonly())
			rescueFiles.add("formats.yml");

		if ((signs = registerInternalYAML(signs, "signs.yml")).isReadonly())
			rescueFiles.add("signs.yml");

		try {
			storage = new Storage();
				Texts.reload();
				new ConfigUpdater();
				new FormatsUpdater();
				new CommandsUpdater();
			storage.register();

		} catch (StorageRegisterFailedException e) {
			throw new ParseYamlException(e);
		}

		if (!rescueFiles.isEmpty()) {
			API.sendMessage(new Message.Builder().format("plugin.rescue-mode", format -> format
				.replace("%files%", String.join(", ", rescueFiles))
			).build());
		}

		commandHandler = new CommandHandlerImpl(commandManager, commands);
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

//		UTILS EXPERIMENTAL!
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

/*
		double version = util.getMinecraftVersion();
		if (version > 7.2 && version < 12.0)
			eventManager.addExecutor("achievements", new Achievements());

		else if (version > 12.2)
			eventManager.addExecutor("advancements", new Advancements());
*/

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
		eventManager.removeExecutors("advancements");
		eventManager.removeExecutors("achievements");
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

		Message.Builder builder = new Message.Builder();

		try {
			discordTranslator.connect(
				config.get().getString("discord.bot-token")
			);

			discordTranslator.registerEvents();
			discordTranslator.registerCommands();

			if (isDisabled()) {
				builder.format("discord-translator.load.done.discord");

				Message _from = API.formatMessage(builder.build());

				DiscordChat.broadcastEmbed(
					DiscordChat.getChannels("discord.channels.server-status"),
					_from.getMessages().getFormats(),
					_from.getToolTips().getFormats(),
					Integer.parseInt(Texts.get("discord-translator.load.done.discord.color")[0].substring(1), 16)
				);
			}

			builder.format("discord-translator.load.done.console");

		} catch (InvalidTokenException e) {
			try { discordTranslator.disconnect(); } catch (InterruptedException ignored) {}

			builder.format("discord-translator.load.error.token",
				format -> format.replace("%reason%", e.toString())
			);

		} catch (IllegalStateException e) {
			try { discordTranslator.disconnect(); } catch (InterruptedException ignored) {}

			builder.format("discord-translator.load.error.intents",
				format -> format.replace("%reason%", e.toString())
			);

		} catch (Throwable e) {
			try { discordTranslator.disconnect(); } catch (InterruptedException ignored) {}

			builder.format("discord-translator.load.error",
				format -> format.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(builder.build());
	}

	public void unregisterDiscordBot() {
		if (!Config.TranslateOthers.DISCORD.IF())
			return;

		Message.Builder builder = new Message.Builder();

		try {
			if (isDisabled()) {
				builder.format("discord-translator.unload.done.discord");

				Message _from = API.formatMessage(builder.build());

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

			builder.format("discord-translator.unload.done.console");

		} catch (Throwable e) {
			builder.format("discord-translator.unload.error",
				format -> format.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(builder.build());
	}

	@SuppressFBWarnings("MS_EXPOSE_REP")
	public static ChatTranslator getInstance() {
		return instance;
	}
}