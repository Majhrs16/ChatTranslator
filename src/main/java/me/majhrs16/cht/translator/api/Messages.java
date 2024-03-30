package me.majhrs16.cht.translator.api;

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.JsonFormatter;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.util.util;

import me.majhrs16.lib.logger.Logger;
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
				|| original.isCancelled()
				|| original.getLangSource() == null
				|| original.getLangTarget() == null
				|| original.getLangSource().getCode().equals("DISABLED")
				|| original.getLangTarget().getCode().equals("DISABLED")
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
				logger.error(e.toString());
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

	default void sendMessage(Message original) {
//			Envia los mensajes especificados en el from(Objeto Message actual) y to.

		if (original == null || original.isEmpty())
			return;

		try {
			logger.debug("PRE:  %s", JsonFormatter.format(original.toJson()));

			Message formatted = ChatTranslatorAPI.getInstance().formatMessage(original);

			logger.debug("POST: %s", JsonFormatter.format(formatted.toJson()));

			showMessage(original, formatted);
			showMessage(original.getTo(), formatted.getTo());

			logger.debug("SEP:  ------------------------------------------------");

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	default void sendMessageAsync(Message original) {
		new Thread(() -> sendMessage(original)).start();
	}

	default void broadcast(List<Message> messages, Consumer<Message> broadcastAction) {
		for (Message from : messages) {
			if (broadcastAction != null)
				broadcastAction.accept(from);
		}
	}

	default void broadcast(List<Message> messages) {
		broadcast(messages, ChatLimiter::add);
	}

	default void broadcast(Message model, Player[] players, Consumer<List<Message>> broadcastAction) {
		if (model == null || model.isEmpty())
			return;

		if (model.getTo() == null || model.getTo().isEmpty())
			model.setTo(model.clone());

		Message to_model    = model.getTo();
		List<Message> froms = new ArrayList<>();
//		String original_lang_target = to_model.getLangTarget(); // Limitacion necesaria por ahora...

		for (Player to_player : players) {
			to_model.setSender(to_player);

			if (to_player.equals(model.getSender())) {
				if (players.length > 1)
					continue;

				to_model.setCancelledThis(true);

			} else {
				to_model.setCancelledThis(false);
			}

//			if (original_lang_target == null) // Limitacion necesaria por ahora...
			to_model.setLangTarget(ChatTranslatorAPI.getInstance().getLang(to_player));

			froms.add(model.clone());
			model.setCancelledThis(true);
		}

		if (broadcastAction != null)
			broadcastAction.accept(froms);
	}
}