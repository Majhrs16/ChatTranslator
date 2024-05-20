package me.majhrs16.cht.util.cache;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;

public class SpamTracker<T> {
	private int count;
	private final ConcurrentLinkedQueue<T> chat;

	public SpamTracker() {
		this.chat = new ConcurrentLinkedQueue<>();
		this.count = 0;
	}

/////////////
// SETTERS

	public void setCount(int count) {
		this.count = count;
	}

/////////////
// GETTERS

	public int getCount() {
		return count;
	}

	public ConcurrentLinkedQueue<T> getChat() {
		return chat;
	}

	public List<T> getChat(Predicate<T> filter) {
		return chat.stream().parallel().filter(filter).collect(Collectors.toList());
	}
}