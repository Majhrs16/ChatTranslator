package me.majhrs16.dst;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

import me.majhrs16.dst.events.Chat;

public class DiscordTranslator {
	private static JDA jda;

	public final static String version  = "${dst_version}";

	private static class Events {
		private static final me.majhrs16.dst.events.Commands commands = new me.majhrs16.dst.events.Commands();
		private static final TerminalLogger terminalLogger = new TerminalLogger();
		private static final DiscordSync discordSync = new DiscordSync();
		private static final Chat chat = new Chat();
	}

	public JDA connect(String bot_token) throws InvalidTokenException, InterruptedException {
		if (bot_token == null || bot_token.isEmpty())
			throw new InvalidTokenException();

		JDABuilder builder = JDABuilder.createDefault(bot_token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
		builder.enableIntents(GatewayIntent.GUILD_PRESENCES);

		jda = builder.build();
		return jda.awaitReady();
	}

	public void registerEvents() {
		if (jda == null) return;

		jda.addEventListener(Events.commands);
		jda.addEventListener(Events.chat);
		Events.terminalLogger.start();
		Events.discordSync.start();
	}

	public void unregisterEvents() {
		if (jda == null) return;

		jda.removeEventListener(Events.commands);
		jda.removeEventListener(Events.chat);
		Events.terminalLogger.stop();
		Events.discordSync.stop();
	}

	public void registerCommands() {
		if (jda == null) return;

		for (Guild guild : jda.getGuilds())
			guild.updateCommands().addCommands(Commands.message("Translate")).queue();
	}

	public void unregisterCommands() {
	}

	public void disconnect() {
		if (jda == null) return;

		jda.shutdown();
	}

	public static JDA getJDA() {
		return jda;
	}
}