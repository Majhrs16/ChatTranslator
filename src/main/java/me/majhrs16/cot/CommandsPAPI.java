package me.majhrs16.cot;

import java.util.regex.Pattern;

public enum CommandsPAPI {
    SEND_DISCORD("^sendDiscord;\\s*(.+?);\\s*(.+?)$"),
    TRANSLATE("^translate;\\s*(.+?);\\s*(.+?);\\s*(.+?)$"),
    EXPRESSION("^var;\\s*(.+?);\\s*(.+?)$"),
    CLONE("^clone;\\s*(.+?);\\s*(.+?)$"),
    BROADCAST("^broadcast;\\s*(.+?)$"),
    SET("^set;\\s*(.+?);\\s*(.+?)$"),
    SEND("^send;\\s*(.+?)$"),
    LANG("^lang;\\s*(.+?)$"),
    GET("^get;\\s*(.+?)$"),
    NEW("^new$");

    private final Pattern pattern;

    CommandsPAPI(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public Pattern getPattern() {
        return pattern;
    }
}