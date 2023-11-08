package majhrs16.dst;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

import majhrs16.cht.ChatTranslator;
public class DiscordTranslator {
	private static JDA jda;

	private final ChatTranslator plugin  = ChatTranslator.getInstance();
	private final JDAListener listener   = new JDAListener();
	public final static String version   = "b3.2";

	private final ToDiscordLogger logger = new ToDiscordLogger();

	public boolean connect() {
		String bot_token = plugin.config.get().getString("discord.bot-token");

		if (bot_token == null || bot_token.isEmpty())
			return false;

		try {
			JDABuilder builder = JDABuilder.createDefault(bot_token);
			builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

			jda = builder.build();
			jda.addEventListener(listener);

			jda.awaitReady();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void registerEvents() {
		logger.start();
	}

	public void unregisterEvents() {
		logger.stop();
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

	public static JDA getJda() {
		return jda;
	}
}