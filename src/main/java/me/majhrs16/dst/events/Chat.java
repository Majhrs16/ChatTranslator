package me.majhrs16.dst.events;

import me.majhrs16.cht.events.custom.Formats;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat extends ListenerAdapter {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (DiscordTranslator.isDisabled())
			return;

		Message message = event.getMessage();

		FileConfiguration config = plugin.config.get();

		if (event.getMessage().getMessageReference() != null
				&& config.getStringList("discord.channels.replies").contains(event.getChannel().getId())) {
			handleRefMessage(event);

		} else if (message.getContentRaw().startsWith("!dst")) {
			handleChtCommand(event);

		} else if (event.isFromType(ChannelType.PRIVATE)) {
			handlePrivateMessage(event);

		} else if (config.getStringList("discord.channels.console").contains(event.getChannel().getId())) {
			handleConsoleCommand(event);

		} else if (config.getStringList("discord.channels.chat").contains(event.getChannel().getId())) {
			handleChatMessage(event);
		}
	}

	private void handleRefMessage(MessageReceivedEvent event) {
		MessageReference ref = event.getMessage().getMessageReference();

		if (ref == null)
			return;

		Message message = ref.getMessage();

		if (message == null)
			return;

		me.majhrs16.cht.events.custom.Message.Builder builder = new me.majhrs16.cht.events.custom.Message.Builder();
		builder.setSenderName(event.getMessage().getAuthor().getName());
		builder.setMessages(new Formats.Builder()
			.setFormats("%player_name%: %ct_messages%")
			.setTexts(event.getMessage().getContentDisplay())
		);

		UUID from_uuid = AccountManager.getMinecraft(event.getMessage().getAuthor().getId());
		UUID to_uuid   = AccountManager.getMinecraft(message.getAuthor().getId());

		if (to_uuid == null) {
			List<User> users = DiscordTranslator.getJDA().getUsersByName(
				message.getContentRaw().split(":")[0],
				true
			);

			if (users.isEmpty())
				return;

			to_uuid = AccountManager.getMinecraft(users.get(0).getId());
		}

		if (from_uuid == null || to_uuid == null)
			return;

		OfflinePlayer from_player = AccountManager.getOfflinePlayer(from_uuid);
		OfflinePlayer to_player   = AccountManager.getOfflinePlayer(to_uuid);

		if (from_player == null || to_player == null)
			return;

		TranslatorBase.LanguagesBase from_lang = API.getLang(from_player);
		TranslatorBase.LanguagesBase to_lang   = API.getLang(to_player);

		builder.setLangSource(from_lang);
		builder.setLangTarget(to_lang);

		message.reply(API.formatMessage(builder.build()).getMessages().getFormat(0)).queue();
		event.getMessage().delete().queue();
	}

	private void handleChatMessage(MessageReceivedEvent event) {
		if (event.getMessage().getAuthor().equals(DiscordTranslator.getJDA().getSelfUser()))
			return;

		Message message = event.getMessage();

		List<String> texts = new ArrayList<>();
		List<String> tool_tips = new ArrayList<>();

		if (message.getContentRaw().isEmpty()) {
			for (MessageEmbed embed : message.getEmbeds()) {
				if (embed.getTitle() != null)
					texts.add(embed.getTitle());

				if (embed.getDescription() != null)
					tool_tips.add(embed.getDescription());
			}

		} else {
			texts.add(message.getContentDisplay());
		}

		String[] message_texts  = texts.toArray(new String[0]);
		String[] tool_tip_texts = tool_tips.toArray(new String[0]);

		UUID authorUuid      = AccountManager.getMinecraft(message.getAuthor().getId());
		OfflinePlayer player = authorUuid == null ? null : AccountManager.getOfflinePlayer(authorUuid);
		TranslatorBase.LanguagesBase from_lang = player == null ? util.convertStringToLang("auto") : API.getLang(player);

		me.majhrs16.cht.events.custom.Message.Builder model = util.createChat(
			player == null ? null : player.getPlayer(),
			message_texts,
			from_lang,
			from_lang,
			null
		);
		if (player == null)
			model.setSenderName(message.getAuthor().getName());

		else
			model.setSenderName(player.getName());

		model.setToolTips(model.build().getToolTips().clone()
				.setTexts(tool_tip_texts)

			).setTo(model.build().getTo().clone()
				.setToolTips(model.build().getTo().getToolTips().clone()
					.setTexts(tool_tip_texts)
				)

			).setShow(player != null && player.isOnline());

		me.majhrs16.cht.events.custom.Message.Builder console = util.createChat(
			Bukkit.getConsoleSender(),
			message_texts,
			from_lang,
			API.getLang(Bukkit.getConsoleSender()),
			"console"
		);

		console.setSender(model.build().getSender());
		if (player == null)
			console.setSenderName(message.getAuthor().getName());

		else
			console.setSenderName(player.getName());
		console.setShow(false);

		if (player != null && player.isOnline())
			message.delete().queue();

		API.sendMessageAsync(console.build());
		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::sendMessageAsync);
	}

	private void handleConsoleCommand(MessageReceivedEvent event) {
		if (event.getMessage().getAuthor().isBot())
			return;

		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(
			Bukkit.getConsoleSender(),
			event.getMessage().getContentRaw()
		));
	}

	private void handlePrivateMessage(MessageReceivedEvent event) {
		if (event.getMessage().getAuthor().equals(DiscordTranslator.getJDA().getSelfUser()))
			return;

		Message message = event.getMessage();

		String key = message.getContentRaw();

		if (AccountManager.confirmLink(key, event.getAuthor().getId())) {
			message.addReaction(Emoji.fromUnicode("✅")).queue();

			Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
				UUID authorUuid  = AccountManager.getMinecraft(message.getAuthor().getId());

				if (authorUuid == null) // En caso de estar saturado el server.
					return;

				Player player    = (Player) AccountManager.getOfflinePlayer(authorUuid);

				API.sendMessage(new me.majhrs16.cht.events.custom.Message.Builder()
					.setSender(player)
					.setLangTarget(API.getLang(player))
					.format("commands.dst.discordLink")
					.build()
				);
			}, 5L);

		} else {
			message.addReaction(Emoji.fromUnicode("❌")).queue();
		}
	}

	private void handleChtCommand(MessageReceivedEvent event) {
		if (event.getMessage().getAuthor().equals(DiscordTranslator.getJDA().getSelfUser()))
			return;

		Message message = event.getMessage();

		String[] args = message.getContentRaw().split(" ");
		Member member = event.getMember();

		me.majhrs16.cht.events.custom.Message.Builder builder = new me.majhrs16.cht.events.custom.Message.Builder();
		if (member != null) {
			UUID memberUuid = AccountManager.getMinecraft(member.getId());
			if (memberUuid != null)
				builder.setLangTarget(API.getLang(AccountManager.getOfflinePlayer(memberUuid)));
		}
		builder.setColor(-1);

		if (args.length > 1) {
			if (args[1].equals("lang")) { // !cht lang CODE
				String lang = args[2];

				if (API.getTranslator().isSupport(lang)) {
					UUID uuid = AccountManager.getMinecraft(message.getAuthor().getId());

					if (uuid == null) {
						builder.format("commands.dst.unlinked");

					} else {
						plugin.storage.set(uuid, null, lang);

						TranslatorBase.LanguagesBase language = util.convertStringToLang(lang);

						builder.setLangTarget(language);
						builder.format("commands.dst.setLang.done", s -> s
							.replace("%lang%", language.getValue())
						);
					}

				} else {
					builder.format("commands.dst.setLang.error.unsupported", s -> s
						.replace("%lang%", lang)
					);
				}

			} else {
				builder.format("commands.dst.setLang.error.syntax");
			}
		}

		message.reply(String.join("\n",
			API.formatMessage(builder.build()).getMessages().getFormats()
		)).queue();
	}
}