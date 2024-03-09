package me.majhrs16.cht.util.cache;

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

public enum Config {
    UPDATE_CONFIG	("auto-update-config"),
    CHECK_UPDATES	("check-updates"),
    FORMAT_PAPI		("use-PAPI-format"),
    DEBUG			("debug");

    private final String path;

    Config(String string) {
        path = string;
    }

    public boolean IF() {
        return util.IF(ChatTranslator.getInstance().config.get(), getPath());
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
            return util.IF(ChatTranslator.getInstance().config.get(), getPath());
        }

        public String getPath() {
        	return path;
        }
    }

    public enum TranslateOthers {
    	DISCORD	("auto-translate-others.discord"),
        ACCESS	("auto-translate-others.access");

        private final String path;

        TranslateOthers(String string) {
            path = string;
        }

        public boolean IF() {
            return util.IF(ChatTranslator.getInstance().config.get(), getPath());
        }

        public String getPath() {
        	return path;
        }

        public enum Signs {
            ENABLE  ("auto-translate-others.signs.enable"),
            WRAP  ("auto-translate-others.signs.enable");

            private final String path;

            Signs(String string) {
                path = string;
            }

            public boolean IF() {
                return util.IF(ChatTranslator.getInstance().config.get(), getPath());
            }

            public String getPath() {
                return path;
            }

        }
    }
}