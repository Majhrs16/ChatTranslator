package majhrs16.dst;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.dst.utils.AccountManager;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.event.Listener;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

public class JDAListener extends ListenerAdapter implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		if (event.getName().equals("Traducir")) {
			event.deferReply(true).queue();

			Message message = event.getTarget();
			Member member = event.getMember();

			majhrs16.cht.events.custom.Message DC = util._getDataConfigDefault();
				DC.setColor(false);

			if (member != null) {
				UUID authorUuid = AccountManager.getMinecraft(message.getAuthor().getId());
				UUID memberUuid = AccountManager.getMinecraft(member.getId());

				if (memberUuid != null) {
					String from = authorUuid == null ? "auto" : API.getLang(AccountManager.getOfflinePlayer(authorUuid)); // API.getLang(authorPlayer);
					String to = API.getLang(AccountManager.getOfflinePlayer(memberUuid));

					if (from == to)
						from = "auto";

					DC.setMessages(message.getContentDisplay());
					DC.setLangSource(from);
					DC.setLangTarget(to);

					event.getHook().sendMessage(API.formatMessage(DC).getMessages()).queue();
					return;
				}
			}

			DC.setMessages("Debe vincular su cuenta de Discord con su Minecraft.\n    Por favor, use el comando `/cht link`");
			event.getHook().sendMessage(API.formatMessage(DC).getMessages()).queue();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;

		Message message = event.getMessage();

		if (message.getContentRaw().startsWith("!cht")) {
			String[] args = message.getContentRaw().split(" ");
			Member member = event.getMember();

			majhrs16.cht.events.custom.Message DC = util._getDataConfigDefault();
				if (member != null) {
					UUID memberUuid = AccountManager.getMinecraft(member.getId());
					if (memberUuid != null)
						DC.setLangTarget(API.getLang(AccountManager.getOfflinePlayer(memberUuid)));
				}
				DC.setColor(false);

			if (args.length > 1) {
				switch (args[1]) {
					case "lang": // !cht lang CODE
						String lang = args[2];

						try {
							util.assertLang(lang, "&7El idioma &f'&b" + lang + "&f'&c no &7esta soportado&f!.");

							UUID uuid = AccountManager.getMinecraft(message.getAuthor().getId());

							if (uuid == null) {
								DC.setMessages("Para usar este comando, antes debe vincular su cuenta de Discord con su Minecraft.\n    Por favor, use el comando `/cht link` en el server.");

							} else {
								plugin.storage.set(uuid, null, lang);

								DC.setMessages("Su idioma ha sido establecido a `" + GoogleTranslator.Languages.valueOf(lang.toUpperCase()).getValue() + "`.");
								DC.setLangTarget(lang);
							}

						} catch (IllegalArgumentException e) {
							DC.setMessages(e.getMessage());
						}

						break;

					default:
						DC.setMessages("Sintaxis invalida. Por favor use la sintaxis:\n    `!cht lang <codigo>`.");
				}
			}

			message.reply(API.formatMessage(DC).getMessages()).queue();

		} else if (event.isFromType(ChannelType.PRIVATE)) {
			String key = message.getContentRaw();

			if (AccountManager.login(key, event.getAuthor().getId())) {
				message.addReaction(Emoji.fromUnicode("✅")).queue();

				Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
					UUID authorUuid  = AccountManager.getMinecraft(message.getAuthor().getId());
					
					if (authorUuid == null) // En caso de estar saturado el server.
						return;
					
					Player player    = (Player) AccountManager.getOfflinePlayer(authorUuid);

					majhrs16.cht.events.custom.Message from = util.getDataConfigDefault();
					from.setSender(player);
					from.setMessages("&aSu cuenta ha sido vinculada exitosamente&f!");
					from.setLangTarget(API.getLang(player));

					API.sendMessage(from);
				}, 5L);

			} else {
				message.addReaction(Emoji.fromUnicode("❌")).queue();
			}

		} else if (plugin.config.get().getStringList("discord.channels.console").contains(event.getChannel().getId())) {
			Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw()));

		} else if (plugin.config.get().getStringList("discord.channels.chat").contains(event.getChannel().getId())) {
			Player[] players = util.getOnlinePlayers();

			if (players.length < 1)
				return;

			UUID authorUuid      = AccountManager.getMinecraft(message.getAuthor().getId());
			OfflinePlayer player = authorUuid == null ? null : AccountManager.getOfflinePlayer(authorUuid);
			String from_lang     = player == null ? "auto" : API.getLang(player);

			majhrs16.cht.events.custom.Message to_model = util.createChat(
					player == null ? null : player.getPlayer(),
					message.getContentDisplay(),
					from_lang,
					from_lang,
					null
				);
				to_model.setMessagesFormats(to_model.getMessageFormat().replace("%player_name%", player == null ? event.getAuthor().getName() : player.getName() ));
				to_model.getTo().setMessagesFormats(to_model.getTo().getMessageFormat().replace("%player_name%", player == null ? event.getAuthor().getName() : player.getName() ));
				to_model.setCancelledThis(player == null);

			majhrs16.cht.events.custom.Message from_console = util.createChat(
					Bukkit.getConsoleSender(),
					message.getContentDisplay(),
					from_lang,
					API.getLang(Bukkit.getConsoleSender()),
					"console"
				);

//				from_console.setMessagesFormats(from_console.getMessageFormat().replace("%player_name%", player == null ? event.getAuthor().getName() : player.getName() ));
				from_console.getTo().setMessagesFormats(from_console.getTo().getMessageFormat().replace("%player_name%", player == null ? event.getAuthor().getName() : player.getName() ));

				from_console.setSender(to_model.getSender());
				from_console.setCancelledThis(true);

			if (player != null)
				message.delete().queue();

			API.broadcast(to_model, froms -> {
				froms.add(from_console);

				/*
				API.broadcast(froms, from -> {
					if (from == new majhrs16.cht.events.custom.Message())
						return;

					API.sendMessage(from);
					from.setCancelled(true);
				});

				froms.clear();
				*/
			});
		}
	}

	/*
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		String ID = event.getComponentId();

		if (ID.startsWith("translate")) {
			event.deferReply(true).queue();

			String to_lang;
			UUID userUuid = AccountManager.getMinecraft(event.getUser().getId());

			if (userUuid == null)
				to_lang = plugin.storage.getDefaultLang();

			else
				to_lang = API.getLang(Bukkit.getOfflinePlayer(userUuid));

			String from_lang = ID.split("-")[1];

			majhrs16.cht.events.custom.Message DC = util.getDataConfigDefault();
				DC.setMessages(event.getMessage().getContentDisplay());
				DC.setLangSource(from_lang);
				DC.setLangTarget(to_lang);
				DC.setColor(false);

			event.getHook().sendMessage(API.formatMessage(DC).getMessages()).queue();
		}
	}
	*/
}