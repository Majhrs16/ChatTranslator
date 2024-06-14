package me.majhrs16.dst.utils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import me.majhrs16.dst.DiscordTranslator;

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.function.Consumer;
import java.util.List;

public class DiscordChat {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();

	public static String[] getChannels(String path) {
		return plugin.config.get().getStringList(path).toArray(new String[0]);
	}

	public static int broadcast(String[] channels, Consumer<TextChannel> action) {
		int oks = 0;

		if (DiscordTranslator.getJDA() == null)
			return oks;

		for (String channelID : channels) {
			TextChannel channel = DiscordTranslator.getJDA().getTextChannelById(channelID);

			if (channel == null)
				continue;

			if (action != null) {
				try {
					action.accept(channel);
					oks++;

				} catch (Throwable e) {
					plugin.logger.error(e.toString());
				}
			}
		}

		return oks;
	}

	public static int broadcast(String[] channels, String... messages) {
		return broadcast(channels, channel -> {
			for (String message : messages) {
				if (message == null)
					continue;

				channel.sendMessage(String.join("\n", util.stripColor(message))).queue();
			}
		});
	}

	public static int broadcastEmbed(String[] channels, String[] title, String[] description, int color) {
		return broadcast(channels, channel -> {
			EmbedBuilder embedBuilder = new EmbedBuilder();
				embedBuilder.setColor(color);
				if (title.length > 0)
					embedBuilder.setTitle(String.join("\n", util.stripColor(title)));
				if (description.length > 0)
					embedBuilder.setDescription(String.join("\n", util.stripColor(description)));

			if (title.length > 0 || description.length > 0)
				channel.sendMessageEmbeds(embedBuilder.build()).queue();
		});
	}
}