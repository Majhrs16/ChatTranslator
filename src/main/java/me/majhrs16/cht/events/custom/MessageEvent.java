package me.majhrs16.cht.events.custom;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class MessageEvent extends Event implements Cancellable {
	private Message message;
	private boolean is_cancelled = false;
	private boolean _is_processed = false;
	private static final HandlerList HANDLERS = new HandlerList();

	public MessageEvent(Message message) {
		this.message = message;
	}

	@SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
	@NotNull public static HandlerList getHandlerList() { return HANDLERS; }

	@SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
	@NotNull public HandlerList getHandlers()           { return HANDLERS; }

	public boolean isCancelled() {
		return is_cancelled;
	}

	public void setCancelled(boolean cancelled) {
		message = message.clone()
			.setTo(
				message.getTo().clone().setShow(!cancelled)
			).setShow(!cancelled)
			.build();

		is_cancelled = cancelled;
	}

	public Message getChat() {
		return message;
	}

///////////////////////////////////////////////////////////////
//	INTERNAL USE, DO NOT USE;
	public boolean _isProcessed() {
		return _is_processed;
	}

	public void _setProcessed(boolean processed) {
		_is_processed = processed;
	}
}