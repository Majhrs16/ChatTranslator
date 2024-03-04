package majhrs16.cht.events.custom;

public class Formats {
    private String[] formats = new String[0];
    private String[] texts   = new String[0];

    public void setFormats(String... formats) {
        this.formats = formats;
    }

    public void setTexts(String... texts) {
        this.texts = texts;
    }

    public void setFormat(int index, String format) {
        this.formats[index] = format;
    }

    public void setText(int index, String text) {
        this.texts[index] = text;
    }

    public String getFormat(int index) {
        return formats[index];
    }

    public String getText(int index) {
        return texts[index];
    }

    public String[] getFormats() {
        return formats;
    }

    public String[] getTexts() {
        return texts;
    }
}
