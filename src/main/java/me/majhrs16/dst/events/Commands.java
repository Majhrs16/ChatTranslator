package me.majhrs16.dst.events;

import me.majhrs16.cht.util.util;
import me.majhrs16.lib.network.translator.TranslatorBase;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.dst.utils.AccountManager;

import java.util.Objects;
import java.util.UUID;

public class Commands extends ListenerAdapter {
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		if (event.getName().equals("Traducir")) {
			event.deferReply(true).queue();

			Message message = event.getTarget();
			Member member = event.getMember();

			me.majhrs16.cht.events.custom.Message DC = new me.majhrs16.cht.events.custom.Message();
			DC.setForceColor(false);

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

					DC.getMessages().setTexts(message.getContentDisplay());
					DC.setLangSource(from_lang);
					DC.setLangTarget(to_lang);

					event.getHook().sendMessage(String.join("\n", API.formatMessage(DC).getMessages().getFormats())).queue();
					return;
				}
			}

			DC.getMessages().setTexts(
				"Debe vincular su cuenta de Discord con su Minecraft.",
				"    Por favor, use el comando `/cht link`"
			);
			event.getHook().sendMessage(String.join("\n", API.formatMessage(DC).getMessages().getFormats())).queue();
		}
	}
}