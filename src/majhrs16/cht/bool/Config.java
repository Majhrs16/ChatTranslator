package majhrs16.cht.bool;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public enum Config {
    UPDATE_CONFIG	("auto-update-config"),
    CHECK_UPDATES	("check-updates"),
    FORMAT_PAPI		("use-PAPI-format"),
    CHAT_COLOR		("chat-color-personalized"),
    DEBUG			("debug");

    private final String path;

    Config(String string) {
        path = string;
    }

    public boolean IF() {
        return util.IF(ChatTranslator.getInstance().getConfig(), getPath());
    }

    public String getPath() {
    	return path;
    }

    public enum NativeChat {
        CANCEL	("show-native-chat.cancel-event"),
        CLEAR	("show-native-chat.clear-recipients");

        private final String path;

        NativeChat(String string) {
            path = string;
        }

        public boolean IF() {
            return util.IF(ChatTranslator.getInstance().getConfig(), getPath());
        }

        public String getPath() {
        	return path;
        }
    }

    public enum TranslateOthers {
        ACCESS	("auto-translate-others.access"),
    	SIGNS	("auto-translate-others.signs");

        private final String path;

        TranslateOthers(String string) {
            path = string;
        }

        public boolean IF() {
            return util.IF(ChatTranslator.getInstance().getConfig(), getPath());
        }

        public String getPath() {
        	return path;
        }
    }
}