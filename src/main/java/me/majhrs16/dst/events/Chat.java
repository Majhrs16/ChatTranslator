package me.majhrs16.dst.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;

import me.majhrs16.lib.network.translator.TranslatorBase;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Chat extends ListenerAdapter {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();

		if (message.getAuthor().equals(DiscordTranslator.getJDA().getSelfUser()))
			return;

		FileConfiguration config = plugin.config.get();

		if (event.getMessage().getMessageReference() != null) {
			handleRefMessage(event);

		} else if (message.getContentRaw().startsWith("!cht")) {
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
		if (!Config.TranslateOthers.DISCORD_REPLY.IF())
			return;

		MessageReference ref = event.getMessage().getMessageReference();

		if (ref == null)
			return;

		Message message = ref.getMessage();

		if (message == null)
			return;


		me.majhrs16.cht.events.custom.Message from = new me.majhrs16.cht.events.custom.Message();
		from.setSenderName(message.getAuthor().getName());
		from.getMessages().setFormats("%player_name%: %ct_messages%");
		from.getMessages().setTexts(event.getMessage().getContentDisplay());

		UUID from_uuid = AccountManager.getMinecraft(message.getAuthor().getId());
		UUID to_uuid   = AccountManager.getMinecraft(event.getMessage().getAuthor().getId());

		if (from_uuid == null || to_uuid == null)
			return;

		OfflinePlayer from_player = AccountManager.getOfflinePlayer(from_uuid);
		OfflinePlayer to_player   = AccountManager.getOfflinePlayer(to_uuid);

		if (from_player == null || to_player == null)
			return;

		from.setLangSource(API.getLang(from_player));
		from.setLangTarget(API.getLang(to_player));

		message.reply(API.formatMessage(from).getMessages().getFormat(0)).queue();
		event.getMessage().delete().queue();
	}

	private void handleChatMessage(MessageReceivedEvent event) {
		Message message = event.getMessage();

		UUID authorUuid      = AccountManager.getMinecraft(message.getAuthor().getId());
		OfflinePlayer player = authorUuid == null ? null : AccountManager.getOfflinePlayer(authorUuid);
		TranslatorBase.LanguagesBase from_lang = player == null ? util.convertStringToLang("auto") : API.getLang(player);

		me.majhrs16.cht.events.custom.Message model = util.createChat(
				player == null ? null : player.getPlayer(),
				new String[] { message.getContentDisplay() },
				from_lang,
				from_lang,
				null
		);
		if (player == null)
			model.setSenderName(message.getAuthor().getName());

		else
			model.setSenderName(player.getName());

		model.setCancelledThis(player == null || !player.isOnline());

		me.majhrs16.cht.events.custom.Message console = util.createChat(
				Bukkit.getConsoleSender(),
				new String[] { message.getContentDisplay() },
				from_lang,
				API.getLang(Bukkit.getConsoleSender()),
				"console"
		);

		console.setSender(model.getSender());
		if (player == null)
			console.setSenderName(message.getAuthor().getName());

		else
			console.setSenderName(player.getName());
		console.setCancelledThis(true);

		if (player != null && player.isOnline())
			message.delete().queue();

		API.broadcast(model, util.getOnlinePlayers(), froms -> {
			froms.add(console);
			API.broadcast(froms);
		});
	}

	private void handleConsoleCommand(MessageReceivedEvent event) {
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw()));
	}

	private void handlePrivateMessage(MessageReceivedEvent event) {
		Message message = event.getMessage();

		String key = message.getContentRaw();

		if (AccountManager.confirmLink(key, event.getAuthor().getId())) {
			message.addReaction(Emoji.fromUnicode("✅")).queue();

			Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
				UUID authorUuid  = AccountManager.getMinecraft(message.getAuthor().getId());

				if (authorUuid == null) // En caso de estar saturado el server.
					return;

				Player player    = (Player) AccountManager.getOfflinePlayer(authorUuid);

				me.majhrs16.cht.events.custom.Message from = new me.majhrs16.cht.events.custom.Message();
				from.setSender(player);
				from.getMessages().setTexts("&aSu cuenta ha sido vinculada exitosamente&f!");
				from.setLangTarget(API.getLang(player));
				API.sendMessage(from);
			}, 5L);

		} else {
			message.addReaction(Emoji.fromUnicode("❌")).queue();
		}
	}

	private void handleChtCommand(MessageReceivedEvent event) {
		Message message = event.getMessage();

		String[] args = message.getContentRaw().split(" ");
		Member member = event.getMember();

		me.majhrs16.cht.events.custom.Message DC = new me.majhrs16.cht.events.custom.Message();
		if (member != null) {
			UUID memberUuid = AccountManager.getMinecraft(member.getId());
			if (memberUuid != null)
				DC.setLangTarget(API.getLang(AccountManager.getOfflinePlayer(memberUuid)));
		}
		DC.setForceColor(false);

		if (args.length > 1) {
			if (args[1].equals("lang")) { // !cht lang CODE
				String lang = args[2];

				if (API.getTranslator().isSupport(lang)) {
					UUID uuid = AccountManager.getMinecraft(message.getAuthor().getId());

					if (uuid == null) {
						DC.getMessages().setTexts(
								"Para usar este comando, antes debe vincular su cuenta de Discord con su Minecraft.",
								"    Por favor, use el comando `/cht link` en el server."
						);

					} else {
						plugin.storage.set(uuid, null, lang);

						TranslatorBase.LanguagesBase language = util.convertStringToLang(lang);

						DC.getMessages().setTexts(
							"Su idioma ha sido establecido a `" + language.getValue() + "`."
						);

						DC.setLangTarget(language);
					}

				} else {
					DC.getMessages().setTexts("El idioma '" + lang + "' no esta soportado!.");
				}

			} else {
				DC.getMessages().setTexts("Sintaxis invalida. Por favor use la sintaxis:\n    `!cht lang <codigo>`.");
			}
		}

		message.reply(String.join("\n", API.formatMessage(DC).getMessages().getFormats())).queue();
	}
}