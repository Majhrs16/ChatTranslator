package me.majhrs16.cht.events.custom;

public class Formats {
    private String[] formats = new String[0];
    private String[] texts   = new String[0];

    public Formats setFormats(String... formats) {
        this.formats = formats;
        return this;
    }

    public Formats setTexts(String... texts) {
        this.texts = texts;
        return this;
    }

    public Formats setFormat(int index, String format) {
        this.formats[index] = format;
        return this;
    }

    public Formats setText(int index, String text) {
        this.texts[index] = text;
        return this;
    }

    public String getFormat(int index) {
        return formats[index];
    }

    public String getText(int index) {
        return texts[index];
    }

    public String[] getFormats() {
        return formats.clone();
    }

    public String[] getTexts() {
        return texts.clone();
    }

    public void silent() {}
}
