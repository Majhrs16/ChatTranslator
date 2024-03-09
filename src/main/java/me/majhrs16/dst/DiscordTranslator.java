package me.majhrs16.dst;

import me.majhrs16.dst.events.JDAListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

import me.majhrs16.cht.ChatTranslator;

public class DiscordTranslator {
	private static JDA jda;

	private final ChatTranslator plugin  = ChatTranslator.getInstance();
	private final JDAListener listener   = new JDAListener();
	public final static String version   = "b3.6";

	private final TerminalLogger terminalLogger = new TerminalLogger();

	public JDA connect(String bot_token) throws InvalidTokenException, InterruptedException {
		if (bot_token == null || bot_token.isEmpty())
			throw new InvalidTokenException();

		JDABuilder builder = JDABuilder.createDefault(bot_token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

		jda = builder.build();
		jda.addEventListener(listener);

		jda.awaitReady();
		return jda;
	}

	public void registerEvents() {
		terminalLogger.start();
	}

	public void unregisterEvents() {
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
		;
	}

	public void disconnect() {
		if (jda != null)
			jda.shutdown();
	}

	public static JDA getJDA() {
		return jda;
	}
}