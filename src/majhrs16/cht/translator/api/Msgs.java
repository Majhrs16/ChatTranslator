package majhrs16.cht.translator.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import majhrs16.cht.bool.Config;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.util;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public interface Msgs {
	@SuppressWarnings("deprecation")
	default public void processMessage(Message formatted) {
//			Envia el Message a su destinario. Pero es necesario formatearlo previamente con formatMessage.

		if (!new Message().equals(formatted)
					&& !formatted.isCancelled()
					&& formatted.getLangSource() != null
					&& formatted.getLangTarget() != null
					&& !formatted.getLangSource().equals("disabled")
					&& !formatted.getLangTarget().equals("disabled")
					&& formatted.getMessageFormat() != null
					&& formatted.getMessages() != null
				) {

			 if (formatted.getSender() instanceof Player) {
				Player player = ((Player) formatted.getSender());

				 if (util.getMinecraftVersion() >= 8.0) { // 1.8.0
					TextComponent message = new TextComponent(formatted.getMessageFormat());

					if (formatted.getToolTips() != null) {
						message.setHoverEvent(new HoverEvent(
							HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(formatted.getToolTips()).create()
						));
					}

					player.spigot().sendMessage(message);

				 } else {
					player.sendMessage(formatted.getMessageFormat());
					if (formatted.getToolTips() != null)
						player.sendMessage("        " + formatted.getToolTips());
				 }

				 if (formatted.getSounds() != null) {
					for (String line : formatted.getSounds().split("\n")) {
						String[] parts = line.replace(" ", "").toUpperCase().split(";");

						try {
							Sound sound  = Sound.valueOf(parts[0]);
							Float volume = Float.parseFloat(parts[1]);
							Float pitch  = Float.parseFloat(parts[2]);

							player.playSound(
								player.getLocation(),
								sound, pitch, volume
							);

						} catch (IllegalArgumentException e) {
							Message msg = util.getDataConfigDefault();
								msg.setSender(Bukkit.getConsoleSender());
								msg.setLangTarget(ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender()));
								msg.setMessages("&eSonido &f'&bformats&f.&bfrom&f.&bsounds&f.&b" + line + "&f' &cinvalido&f.");
							 sendMessage(msg);
						}
					}
				}
			
			} else {
				CommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(formatted.getMessageFormat());
				if (formatted.getToolTips() != null) {
					for (String line : formatted.getToolTips().split("\n")) {
						console.sendMessage(line);
					}
				}
			}
		 }
	}

	default public void sendMessage(Message event) {
//			Envia los mensajes especificados en father y el objeto Message actual.

		if (event == new Message())
			return;

		try {
			Message formatted = ChatTranslatorAPI.getInstance().formatMessage(event);

			if (Config.DEBUG.IF()) {
				System.out.println("DEBUG: Format, Msgs, ToolTips, LangSource, LangTarget");
				System.out.println(String.format("DEBUG from: '%s', '%s', '%s', '%s' -> '%s'",
					formatted.getMessageFormat(),
					formatted.getMessages(),
					formatted.getToolTips(),
					formatted.getLangSource(),
					formatted.getLangTarget()
				));

				System.out.println(String.format("DEBUG to:   '%s', '%s', '%s', '%s' -> '%s'",
					formatted.getTo().getMessageFormat(),
					formatted.getTo().getMessages(),
					formatted.getTo().getToolTips(),
					formatted.getTo().getLangSource(),
					formatted.getTo().getLangTarget()
				));
			}

			processMessage(formatted);
			processMessage(formatted.getTo());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	default public void broadcast(List<Message> messages, Consumer<Message> preBroadcastAction) {
		for (Message to : messages) {
			try {
				util.assertLang(to.getLangSource());
				util.assertLang(to.getLangTarget());

				if (preBroadcastAction != null)
					preBroadcastAction.accept(to);

			} catch (IllegalArgumentException e) {
				Message alert = util.getDataConfigDefault();
					alert.setMessages(String.format("&b%s&f: &cIdioma &f'&b%s&f' &cy&f/&co &f'&b%s&f' &cno soportado&f.",
						to.getSenderName(),
						to.getLangSource(),
						to.getLangTarget()
					));
				sendMessage(alert);
			}
		}
	}

	default public void broadcast(List<Message> messages) {
		broadcast(messages, to -> majhrs16.cht.util.ChatLimiter.chat.add(to));
	}

	default  public void broadcast(Message from, Consumer<List<Message>> preBroadcastAction) {
		if (from == null
				|| from.equals(new Message())
				|| from.getTo().equals(new Message()))
			return;

		List<Message> tos = new ArrayList<Message>();

		Message to_model = from.getTo();

		to_model.setSender(from.getSender());
		to_model.setLangTarget(from.getLangTarget()); // Por si no se establece en el to_model el lang_target.

		to_model.setCancelledThis(true);  // Evitar duplicacion.
		tos.add(from.clone());
		from.setCancelledThis(true);      // Evitar duplicacion.

		to_model.setCancelledThis(false); // Restaurar funcionalidad.
		for (Player to_player : Bukkit.getOnlinePlayers()) {
			if(from.getSender().equals(to_player))
				continue;

			to_model.setSender(to_player);
			to_model.setLangTarget(ChatTranslatorAPI.getInstance().getLang(to_player));
			tos.add(from.clone());
		}

		if (preBroadcastAction != null)
			preBroadcastAction.accept(tos);

		broadcast(tos);
	}

	default public void broadcast(Message to_model) {
		 broadcast(to_model, null);
	 }
}
