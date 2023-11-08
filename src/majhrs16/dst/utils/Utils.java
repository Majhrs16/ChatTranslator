package majhrs16.dst.utils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.ChatTranslator;

import java.util.function.Consumer;

public class Utils {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();

	public static class Integer {
		private int integer;

		public Integer(int start) {
			integer = start;
		}

		public int get() {
			return integer;
		}

		public int getAndIncrement() {
			integer++;
			return integer;
		}
	}
	public static void sendMessageEmbed(TextChannel channel, String title, String description, int color) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setColor(color);
			embedBuilder.setTitle(title);
			if (description != null)
				embedBuilder.setDescription(description);
		channel.sendMessageEmbeds(embedBuilder.build()).queue();
	}

	public static void broadcast(String path, Consumer<TextChannel> action) {
		for (String channelID : plugin.config.get().getStringList(path)) {
			TextChannel channel = DiscordTranslator.getJda().getTextChannelById(channelID);

			if (channel == null)
				continue;

			action.accept(channel);
		}
	}
}
