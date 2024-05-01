package me.majhrs16.dst.events;

import me.majhrs16.cht.util.cache.Config;
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

import java.util.Objects;
import java.util.UUID;

public class Commands extends ListenerAdapter {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.DISCORD.IF())
			return;

		if (event.getName().equals("Translate")) {
			event.deferReply(true).queue();

			Message message = event.getTarget();
			Member member = event.getMember();

			me.majhrs16.cht.events.custom.Message from = new me.majhrs16.cht.events.custom.Message();
			from.setColor(-1);

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

					from.getMessages().setTexts(message.getContentDisplay());
					from.setLangSource(from_lang);
					from.setLangTarget(to_lang);

					event.getHook().sendMessage(String.join("\n", API.formatMessage(from).getMessages().getFormats())).queue();
					return;
				}
			}

			from.format("discord-translator.unlinked");
			event.getHook().sendMessage(String.join("\n", API.formatMessage(from).getMessages().getFormats())).queue();
		}
	}
}