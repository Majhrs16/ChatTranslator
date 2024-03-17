package me.majhrs16.cot;

import java.util.regex.Pattern;

public enum CommandsPAPI {
    SEND_DISCORD("^sendDiscord;\\s*(.+?);\\s*(.+?)$"),
    TRANSLATE("^translate;\\s*(.+?);\\s*(.+?);\\s*(.+?)$"),
    BROADCAST("^broadcast;\\s*(.+?)$"),
    VAR("^var;\\s*(.+?)(;\\s*(.+?))?$"),
    SEND("^send;\\s*(.+?)$"),
    LANG("^lang;\\s*(.+?)$"),
    NEW("new");

    private final Pattern pattern;

    CommandsPAPI(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
