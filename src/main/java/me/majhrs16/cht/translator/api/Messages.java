package me.majhrs16.cht.translator.api;

import me.majhrs16.lib.network.translator.TranslatorBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.util.JsonFormatter;
import me.majhrs16.cht.util.TimerLapser;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.lib.logger.Logger;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public interface Messages {
	Logger logger = ChatTranslator.getInstance().logger;

	default void showMessage(Message original, Message formatted) {
		if (original == null
				|| !original.isShow()
				|| original.getLangSource().getCode().equals("DISABLED")
				|| original.getLangTarget().getCode().equals("DISABLED")
				|| formatted.getMessages().getFormats().length == 0
				|| formatted.getMessages().getFormat(0).isEmpty()
				|| formatted.getMessages().getFormat(0).equals("[!] ")
				|| formatted.getMessages().getTexts().length == 0) {
			return;
		}

		if (formatted.getSender() instanceof Player) {
			if (!Permissions.ChatTranslator.Chat.MESSAGES.IF(original))
				return;

			double version = util.getMinecraftVersion();

			try {
				if (version > 7.9) { // 1.7.10+
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

		TimerLapser timer = new TimerLapser();

		if (original == null || original.isEmpty()) {
			timer.stop();
			logger.debug(timer.getResults());
			return;
		}

		try {
			logger.debug("PRE:  %s", JsonFormatter.format(original.toJson()));

			timer.start();
				Message formatted = ChatTranslatorAPI.getInstance().formatMessage(original);
			timer.stop();
			logger.debug(timer.getResults());

			logger.debug("POST: %s", JsonFormatter.format(formatted.toJson()));

			timer.analyzeCode(() -> showMessage(original, formatted));
			logger.debug(timer.getResults());

			timer.analyzeCode(() -> showMessage(original.getTo(), formatted.getTo()));
			logger.debug(timer.getResults());

			logger.debug("SEP:  ------------------------------------------------");

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	default void sendMessageAsync(Message original) {
		Message backup = original.clone().build();

		Thread thread = new Thread(() -> sendMessage(backup));
		thread.setName("ChatTranslator.API.sendMessageAsync: " + original.getUUID());
		thread.start();
	}

	default void broadcast(Message.Builder model, Player[] players, Consumer<Message> broadcastAction) {
		TimerLapser timer = new TimerLapser();

		if (model == null
				|| model.build().isEmpty()
				|| players == null
				|| players.length == 0) {

			timer.stop();
			logger.debug(timer.getResults());
			return;
		}

		if (model.build().getTo() == null || model.build().getTo().isEmpty())
			throw new IllegalArgumentException("The models from and to must be not empty.");

		List<Message> froms = new ArrayList<>();
		Message.Builder to_model = model.build().getTo().clone();
		TranslatorBase.LanguagesBase to_lang_target_original = to_model.build().getLangTarget();

		timer.analyzeCode(() -> {
			for (Player to_player : players) {
				to_model.setSender(to_player);

				if (to_player.equals(model.build().getSender()) && players.length > 1)
					continue;

				to_model.setShow(!to_player.equals(model.build().getSender()));

				if (to_lang_target_original == null)
					to_model.setLangTarget(ChatTranslatorAPI.getInstance().getLang(to_player));

				model.setTo(to_model);
				froms.add(model.build().clone().build());
				model.setShow(false);
		}});
		logger.debug(timer.getResults());

		timer.analyzeCode(() -> {
			Consumer<Message> action = broadcastAction == null ? ChatLimiter::add : broadcastAction;
			for (Message from : froms) action.accept(from);
		});
		logger.debug(timer.getResults());
	}

	default void broadcast(Message.Builder model, Player[] players) {
		broadcast(model, players, null);
	}
}