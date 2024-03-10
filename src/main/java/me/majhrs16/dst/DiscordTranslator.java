package me.majhrs16.dst;


import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.dst.events.Chat;

public class DiscordTranslator {
	private static JDA jda;

	public final static String version  = "b3.6";
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private final TerminalLogger terminalLogger = new TerminalLogger();

	private static class Events {
		private static final Chat chat = new Chat();
		private static final me.majhrs16.dst.events.Commands commands = new me.majhrs16.dst.events.Commands();
	}

	public JDA connect(String bot_token) throws InvalidTokenException, InterruptedException {
		if (bot_token == null || bot_token.isEmpty())
			throw new InvalidTokenException();

		JDABuilder builder = JDABuilder.createDefault(bot_token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

		jda = builder.build();
		return jda.awaitReady();
	}

	public void registerEvents() {
		jda.addEventListener(Events.commands);
		jda.addEventListener(Events.chat);
		terminalLogger.start();
	}

	public void unregisterEvents() {
		jda.removeEventListener(Events.commands);
		jda.removeEventListener(Events.chat);
		terminalLogger.stop();
	}

	public void registerCommands() {
		if (jda != null) {
			for (Guild guild : jda.getGuilds()) {
				guild.updateCommands().addCommands( Commands.message("Traducir") ).queue();
			}
		}
	}

	public void unregisterCommands() {
	}

	public void disconnect() {
		if (jda != null)
			jda.shutdown();
	}

	public static JDA getJDA() {
		return jda;
	}
}