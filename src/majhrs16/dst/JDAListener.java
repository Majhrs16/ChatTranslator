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

			majhrs16.cht.events.custom.Message DC = new majhrs16.cht.events.custom.Message();
				DC.setColor(false);

			if (member != null) {
				UUID authorUuid = AccountManager.getMinecraft(message.getAuthor().getId());
				UUID memberUuid = AccountManager.getMinecraft(member.getId());

				if (memberUuid != null) {
					String from = authorUuid == null ? "auto" : API.getLang(AccountManager.getOfflinePlayer(authorUuid));
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

			majhrs16.cht.events.custom.Message DC = new majhrs16.cht.events.custom.Message();
				if (member != null) {
					UUID memberUuid = AccountManager.getMinecraft(member.getId());
					if (memberUuid != null)
						DC.setLangTarget(API.getLang(AccountManager.getOfflinePlayer(memberUuid)));
				}
				DC.setColor(false);

			if (args.length > 1) {
				if (args[1].equals("lang")) { // !cht lang CODE
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
				} else {
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

					API.sendMessage(new majhrs16.cht.events.custom.Message()
						.setSender(player)
						.setMessages("&aSu cuenta ha sido vinculada exitosamente&f!")
						.setLangTarget(API.getLang(player)
					));
				}, 5L);

			} else {
				message.addReaction(Emoji.fromUnicode("❌")).queue();
			}

		} else if (plugin.config.get().getStringList("discord.channels.console").contains(event.getChannel().getId())) {
			Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw()));

		} else if (plugin.config.get().getStringList("discord.channels.chat").contains(event.getChannel().getId())) {
			UUID authorUuid      = AccountManager.getMinecraft(message.getAuthor().getId());
			OfflinePlayer player = authorUuid == null ? null : AccountManager.getOfflinePlayer(authorUuid);
			String from_lang     = player == null ? "auto" : API.getLang(player);

			majhrs16.cht.events.custom.Message model = util.createChat(
					player == null ? null : player.getPlayer(),
					message.getContentDisplay(),
					from_lang,
					from_lang,
					null
				);
				if (player == null)
					model.setSenderName(message.getAuthor().getName());

				else
					model.setSenderName(player.getName());

				model.setCancelledThis(player == null || !player.isOnline());

			majhrs16.cht.events.custom.Message console = util.createChat(
					Bukkit.getConsoleSender(),
					message.getContentDisplay(),
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