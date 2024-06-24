package me.majhrs16.dst.events;

import me.majhrs16.cht.events.InternetCheckerAsync;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.dst.DiscordTranslator;

import java.util.TimerTask;
import java.util.Timer;

public class ReloadInternetLoss {
	private Timer timer;
	private static int retries = 0;
	private static boolean isHaveInternet = true;
	private static final ChatTranslator plugin = ChatTranslator.getInstance();

	private static class ReloadInternetLossTask extends TimerTask {
		@Override
		public void run() {
			if (DiscordTranslator.isDisabled()) return;

			if (!isHaveInternet) {
				retries ++;
			}

			if (retries > 3) {
				try {
					plugin.unregisterDiscordBot();
					plugin.registerDiscordBot();

				} catch (Exception ignore) {}

				retries = 0;
			}

			isHaveInternet = InternetCheckerAsync.isInternetAvailable();
		}
	}

	public void start() {
		timer = new Timer("ChatTranslator.DST.ReloadInternetLoss", true);
		timer.scheduleAtFixedRate(new ReloadInternetLossTask(), 0, 1000);
	}

	public void stop() {
		if (timer != null) timer.cancel();
	}
}
