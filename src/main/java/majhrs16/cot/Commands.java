package majhrs16.cot;

import java.util.regex.Pattern;

public enum Commands {
    SEND_DISCORD("^sendDiscord;\\s*(.+?);\\s*(.+?)$"),
    BROADCAST("^broadcast;\\s*(.+?)$"),
    VAR("^var;\\s*(.+?)(;\\s*(.+?))?$"),
    SEND("^send;\\s*(.+?)$"),
    LANG("^lang;\\s*(.+?)$"),
    NEW("new");

    private final Pattern pattern;

    Commands(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public boolean find(String input) {
        return pattern.matcher(input).find();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
