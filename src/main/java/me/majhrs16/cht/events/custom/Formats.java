package me.majhrs16.cht.events.custom;

public class Formats {
	private String[] formats = new String[] { "%ct_messages%" };
	private String[] texts   = new String[0];

	public static class Builder {
		Formats format;

		public Builder() {
			format = new Formats();
		}

		public Builder setFormats(String... formats) {
			format.formats = formats;
			return this;
		}

		public Builder setTexts(String... texts) {
			format.texts = texts;
			return this;
		}

		public Builder setFormat(int index, String format) {
			this.format.formats[index] = format;
			return this;
		}

		public Builder setText(int index, String text) {
			format.texts[index] = text;
			return this;
		}

		public Formats build() {
			return format;
		}
	}

	private Formats() {}

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

	@Override
	public Formats.Builder clone() {
		return new Formats.Builder()
			.setFormats(formats)
			.setTexts(texts);
	}

	@SuppressWarnings("unused")
	public void silent() {}
}