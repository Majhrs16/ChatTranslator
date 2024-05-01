package me.majhrs16.cht.events;

import me.majhrs16.lib.network.utils.InternetAccess;

import java.util.TimerTask;
import java.util.Timer;

public class InternetCheckerAsync {
	private Timer timer;
	private static boolean is_internet_available = true;

	private static class CheckerTask extends TimerTask {
		public void run() {
			try {
				is_internet_available = InternetAccess.isInternetAvailable();

			} catch (Exception e) {
				is_internet_available = false;
			}
		}
	}

	public void start() {
		stop();

		timer = new Timer(true);
		timer.scheduleAtFixedRate(new CheckerTask(), 0, 5000);
	}

	public void stop() {
		if (timer == null) return;
		timer.cancel();
	}

	public static boolean isInternetAvailable() {
		return is_internet_available;
	}
}