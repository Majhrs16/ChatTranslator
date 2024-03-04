package majhrs16.cht.translator.api;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.JsonFormatter;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.events.ChatLimiter;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.util;

import majhrs16.lib.logger.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public interface Messages {
	Logger logger = ChatTranslator.getInstance().logger;

	default void showMessage(Message original, Message formatted) {
		if (original == null
				|| original.isEmpty()
				|| original.isCancelled()
				|| original.getLangSource() == null
				|| original.getLangTarget() == null
				|| original.getLangSource().equals("disabled")
				|| original.getLangTarget().equals("disabled")
				|| original.getMessages().getFormats().length == 0
				|| original.getMessages().getFormat(0).isEmpty()
				|| original.getMessages().getTexts().length == 0) {
			return;
		}

		if (formatted.getSender() instanceof Player) {
			if (!Permissions.ChatTranslator.Chat.MESSAGES.IF(original))
				return;

			double version = util.getMinecraftVersion();

			try {
				if (version > 7.9) {
					NewMessages.J7_J20(original, formatted);

				} else if (version >= 7.5) { // 1.7.5 - 1.7.8 / 1.7.9
					try {
						OlderMessages.J75(formatted);

					} catch (ClassNotFoundException e) {
						OlderMessages.J78_J79(formatted);
					}

				} else if (version == 7.2) { // 1.7.2
					OlderMessages.J72(formatted);

				} else { // <= 1.6.2
					OlderMessages.J52(formatted);
				}

				Sounds.playSounds(formatted);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			CommandSender term = Bukkit.getConsoleSender();

			for (String lines : formatted.getMessages().getFormats()) {
				for (String line : lines.split("\n")) {
					if (term == null) // 1.5.2 bug
						Bukkit.getLogger().warning(util.stripColor(line)[0]);

					else
						term.sendMessage(line);
				}
			}

			for (String lines : formatted.getToolTips().getFormats()) {
				for (String line : lines.split("\n")) {
					if (term == null) // 1.5.2 bug
						Bukkit.getLogger().warning(util.stripColor(line)[0]);

					else
						term.sendMessage(line);
				}
			}
		}
	}

	default void sendMessage(Message event) {
//			Envia los mensajes especificados en el from(Objeto Message actual) y to.

		if (event == null || event.isEmpty())
			return;

		try {
			logger.debug("API.Messages.sendMessage.PRE: %s", JsonFormatter.format(event.toString()));

			Message formatted = ChatTranslatorAPI.getInstance().formatMessage(event);

			logger.debug("API.Messages.sendMessage.POST: %s", JsonFormatter.format(formatted.toString()));

			showMessage(event, formatted);
			showMessage(event.getTo(), formatted.getTo());

			logger.debug("API.Messages.sendMessage.SEP:  ------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	default void broadcast(List<Message> messages, Consumer<Message> broadcastAction) {
		for (Message from : messages) {
			try {
				util.assertLang(from.getLangSource());
				util.assertLang(from.getLangTarget());

				if (broadcastAction != null)
					broadcastAction.accept(from);

			} catch (IllegalArgumentException e) {
				Message alert = util.getDataConfigDefault();
					alert.getMessages().setTexts(String.format("&b%s&f: &cIdioma &f'&b%s&f' &cy&f/&co &f'&b%s&f' &cno soportado&f.",
						from.getSenderName(),
						from.getLangSource(),
						from.getLangTarget()
					));

				sendMessage(alert);
			}
		}
	}

	default void broadcast(List<Message> messages) {
		broadcast(messages, ChatLimiter::add);
	}

	default void broadcast(Message from_model, Player[] players, Consumer<List<Message>> broadcastAction) {
		if (from_model == null || from_model.isEmpty())
			return;

		if (from_model.getTo() == null || from_model.getTo().isEmpty()) {
			from_model.setTo(from_model.clone());
			from_model.getTo().setSender(null);
			from_model.getTo().setLangTarget(null);
		}

		Message to_model    = from_model.getTo();
		List<Message> froms = new ArrayList<>();
//		String original_lang_target = to_model.getLangTarget(); // Limitacion necesaria por ahora...

		for (Player to_player : players) {
			to_model.setSender(to_player);

			if (to_player.equals(from_model.getSender())) {
				if (players.length > 1)
					continue;

				to_model.setCancelledThis(true);

			} else {
				to_model.setCancelledThis(false);
			}

//			if (original_lang_target == null) // Limitacion necesaria por ahora...
			to_model.setLangTarget(ChatTranslatorAPI.getInstance().getLang(to_player));

			froms.add(from_model.clone());
			from_model.setCancelledThis(true);
		}

		if (broadcastAction != null)
			broadcastAction.accept(froms);
	}
}