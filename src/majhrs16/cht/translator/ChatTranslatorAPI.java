package majhrs16.cht.translator;

import majhrs16.cht.translator.api.*;

public class ChatTranslatorAPI implements Core, Lang, Msgs {
	private static final ChatTranslatorAPI API;

	static {
		API = new ChatTranslatorAPI();
	}

	public static ChatTranslatorAPI getInstance() {
		return API;
	}
}