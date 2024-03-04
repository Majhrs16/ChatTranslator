package majhrs16.cht.translator;

import majhrs16.cht.translator.api.*;

public class ChatTranslatorAPI implements Core, Lang, Messages {
	private static ChatTranslatorAPI instance;

	public static ChatTranslatorAPI getInstance() {
		if (instance == null)
			instance = new ChatTranslatorAPI();

		return instance;
	}
}
