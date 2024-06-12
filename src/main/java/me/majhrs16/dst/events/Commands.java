package me.majhrs16.dst.events;

import me.majhrs16.cht.events.custom.Formats;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;

import me.majhrs16.lib.network.translator.TranslatorBase;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.dst.utils.AccountManager;
import org.jetbrains.annotations.NotNull;

import me.majhrs16.dst.DiscordTranslator;

import java.util.Objects;
import java.util.UUID;

public class Commands extends ListenerAdapter {
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
		if (DiscordTranslator.isDisabled())
			return;

		if (event.getName().equals("Translate")) {
			event.deferReply(true).queue();

			Message message = event.getTarget();
			Member member = event.getMember();

			me.majhrs16.cht.events.custom.Message.Builder builder = new me.majhrs16.cht.events.custom.Message.Builder();
			builder.setColor(-1);

			if (member != null) {
				UUID authorUuid = AccountManager.getMinecraft(message.getAuthor().getId());
				UUID memberUuid = AccountManager.getMinecraft(member.getId());

				if (memberUuid != null) {
					TranslatorBase.LanguagesBase from_lang = authorUuid == null
						? util.convertStringToLang("auto")
						: API.getLang(AccountManager.getOfflinePlayer(authorUuid));
					TranslatorBase.LanguagesBase to_lang = API.getLang(AccountManager.getOfflinePlayer(memberUuid));

					if (Objects.equals(from_lang, to_lang))
						from_lang = util.convertStringToLang("auto");

					builder.setMessages(new Formats.Builder().setTexts(message.getContentDisplay()));
					builder.setLangSource(from_lang);
					builder.setLangTarget(to_lang);

					event.getHook().sendMessage(String.join("\n",
						API.formatMessage(builder.build()).getMessages().getFormats()
					)).queue();
					return;
				}
			}

			builder.format("discord-translator.unlinked");
			event.getHook().sendMessage(String.join("\n",
				API.formatMessage(builder.build()).getMessages().getFormats()
			)).queue();
		}
	}
}