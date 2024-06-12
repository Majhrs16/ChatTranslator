package me.majhrs16.cht.events.custom;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import org.jetbrains.annotations.Range;

import java.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("unused")
public class Message {
	private Message to;
	private String[] sounds;
	private Formats messages;
	private Formats tool_tips;
	private String sender_name;
	private CommandSender sender;
	private SenderType sender_type;
	private TranslatorBase.LanguagesBase lang_source;
	private TranslatorBase.LanguagesBase lang_target;

	private byte is_color          = 1; // -1 = Disable color, 0 color by permission, 1 force color;
	private boolean showing        = true;
	private boolean is_format_papi = true;
	private String last_format     = "UNKNOWN";
	private final UUID uuid        = UUID.randomUUID();

	public enum SenderType {
		UNKNOWN,
		CONSOLE,
		PLAYER
	}

	public static class Builder {
		private final Message from;
		private Message.Builder to;
		private Formats.Builder messages;
		private Formats.Builder tool_tips;

		public Builder(TranslatorBase.LanguagesBase langSource, TranslatorBase.LanguagesBase langTarget) {
			CommandSender term = Bukkit.getConsoleSender();
			setSender(term);
			if (term == null) { // 1.5.2 bug!
				setSenderName("CONSOLE");
				setSenderType(SenderType.CONSOLE);
			}

			from = new Message();
			to   = new Message.Builder();

			setLangSource(langSource);
			setLangTarget(langTarget);
		}

		public Builder() {
			this(
				util.getNativeLang(),
				ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender())
			);
		}

		public Builder setTo(Message.Builder to) {
			this.to = to == null ? new Message.Builder() : to;
			return this;
		}

		public Builder setSender(CommandSender sender) {
			from.sender = sender;
			setSenderName(sender == null ? "UNKNOWN" : sender.getName());

			if (sender == null)
				setSenderType(SenderType.UNKNOWN);

			else
				setSenderType(sender instanceof Player ? SenderType.PLAYER : SenderType.CONSOLE);

			return this;
		}

		private Builder setSenderType(SenderType type) {
			from.sender_type = type;
			return this;
		}

		public Builder setSenderName(String name) {
			from.sender_name = name;
			return this;
		}

		public Builder setMessages(Formats.Builder formats) {
			messages = formats;
			return this;
		}
		public Builder setToolTips(Formats.Builder formats) {
			tool_tips = formats;
			return this;
		}

		public Builder setSound(int index, String sounds) {
			from.sounds[index] = sounds;
			return this;
		}

		public Builder setSounds(String... sounds) {
			from.sounds = sounds;
			return this;
		}

		public Builder setLangSource(TranslatorBase.LanguagesBase lang) {
			from.lang_source = lang;
			return this;
		}

		public Builder setLangTarget(TranslatorBase.LanguagesBase lang) {
			from.lang_target = lang;
			return this;
		}

		public Builder setFormatPAPI(boolean formatPAPI) {
			from.is_format_papi = formatPAPI;
			return this;
		}

		public Builder setColor(byte color) {
			from.is_color = color;
			return this;
		}

		public Builder setColor(@Range(from = -1, to = 1) int color) {
			from.is_color = (byte) color;
			return this;
		}

		public Builder format(String path, UnaryOperator<String> preFormats, UnaryOperator<String> preTexts) {
			util.applyMessagesFormat(this, path, (formats, texts) -> {
				if (preFormats != null) formats.replaceAll(preFormats);
				if (preTexts != null) texts.replaceAll(preTexts);
			});

			util.applyToolTipsFormat(this, path, (formats, texts) -> {
				if (preFormats != null) formats.replaceAll(preFormats);
				if (preTexts != null) texts.replaceAll(preTexts);
			});

			util.applySoundsFormat(this, path);

			from.last_format = path;

			return this;
		}

		public Builder format(String path, UnaryOperator<String> preFormats) {
			return format(path, preFormats, null);
		}

		public Builder format(String path) {
			return format(path, null, null);
		}

		private Builder setLastFormatPath(String path) {
			from.last_format = path;
			return this;
		}

		public Builder setShow(boolean show) {
			from.showing = show;
			return this;
		}

		public Message build() {
			from.to        = to.build();
			from.messages  = messages.build();
			from.tool_tips = tool_tips.build();
			return from;
		}
	}

	private Message() {}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public Message getTo() {
		return to;
	}

	public CommandSender getSender() {
		return sender;
	}

	public SenderType getSenderType() {
		return sender_type;
	}

	public String getSenderName() {
		return sender_name;
	}

	public String getSound(int index) {
		return sounds[index];
	}

	public Formats getMessages() {
		return messages;
	}

	public Formats getToolTips() {
		return tool_tips;
	}

	public String[] getSounds() {
		return sounds.clone();
	}

	public TranslatorBase.LanguagesBase getLangSource() {
		return lang_source;
	}
	public TranslatorBase.LanguagesBase getLangTarget() {
		return lang_target;
	}

	public boolean getFormatPAPI() {
		return is_format_papi;
	}

	public byte isColor() {
		return is_color;
	}

	public boolean isShow() {
		return showing;
	}

	public String getLastFormatPath() {
		return last_format;
	}

	public void silent() {}

	@SuppressWarnings("unused")
	private void cloneFromTo(Message from, Message.Builder to) {
		to.setSender(from.getSender())
			.setSenderName(from.getSenderName())
			.setSenderType(from.getSenderType())
			.setMessages(new Formats.Builder()
				.setFormats(from.getMessages().getFormats())
				.setTexts(from.getMessages().getTexts())

			).setToolTips(new Formats.Builder()
				.setFormats(from.getToolTips().getFormats())
				.setTexts(from.getToolTips().getTexts())

			).setSounds(from.getSounds())
			.setLangSource(from.getLangSource())
			.setLangTarget(from.getLangTarget())
			.setColor(from.isColor())
			.setFormatPAPI(from.getFormatPAPI())
			.setLastFormatPath(from.getLastFormatPath());
	}

	@Override
	public Message.Builder clone() {
		Message.Builder builder = new Message.Builder();

//		BUGAZO!! Hay que clonarlo manualmente o sino no copia completamente. O_o??
		Message.Builder to = new Message.Builder();
		if (getTo() != null) cloneFromTo(getTo(), to);
		builder.setTo(to);

		cloneFromTo(this, builder);

		return builder;
	}

	@SuppressWarnings("unchecked")
	public String toJson() {
		JSONObject from = new JSONObject();
		from.put("senderName", getSenderName());

		JSONObject messages = new JSONObject();
		messages.put("formats", Arrays.asList(getMessages().getFormats()));
		messages.put("texts", Arrays.asList(getMessages().getTexts()));
		from.put("messages", messages);

		JSONObject tool_tips = new JSONObject();
		tool_tips.put("formats", Arrays.asList(getToolTips().getFormats()));
		tool_tips.put("texts", Arrays.asList(getToolTips().getTexts()));
		from.put("toolTips", tool_tips);

		from.put("sounds", Arrays.asList(getSounds()));
		from.put("langSource", getLangSource());
		from.put("langTarget", getLangTarget());
		from.put("isColor", isColor());
		from.put("isPAPI", getFormatPAPI());

		JSONObject to = new JSONObject();
		if (getTo() != null) {
			to.put("senderName", getTo().getSenderName());

			JSONObject messages2 = new JSONObject();
			messages2.put("formats", Arrays.asList(getTo().getMessages().getFormats()));
			messages2.put("texts", Arrays.asList(getTo().getMessages().getTexts()));
			to.put("messages", messages2);

			JSONObject tool_tips2 = new JSONObject();
			tool_tips2.put("formats", Arrays.asList(getTo().getToolTips().getFormats()));
			tool_tips2.put("texts", Arrays.asList(getTo().getToolTips().getTexts()));
			to.put("toolTips", tool_tips2);

			to.put("sounds", Arrays.asList(getTo().getSounds()));
			to.put("langSource", getTo().getLangSource());
			to.put("langTarget", getTo().getLangTarget());
			to.put("isColor", getTo().isColor());
			to.put("isPAPI", getTo().getFormatPAPI());
		}

		JSONObject json = new JSONObject();
		json.put("from", from);
		json.put("to", to);
		return json.toString();
	}

	public static Message fromJson(String jsonString) {
		JSONObject messages, tool_tips;
		Formats.Builder formatsBuilder;
		JSONArray formats;

		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

			JSONObject fromJson = (JSONObject) json.get("from");
			Message.Builder from = new Message.Builder();
			from.setSender(BukkitUtils.getSenderByName((String) fromJson.get("senderName")));

			messages =  (JSONObject) fromJson.get("messages");
			formats = (JSONArray) messages.get("formats");
			formatsBuilder = new Formats.Builder();
			formatsBuilder.setFormats(new String[formats.size()]);
			for (int i = 0; i < formats.size(); i++)
				formatsBuilder.setFormat(i, formats.get(i).toString());
			from.setMessages(formatsBuilder);

			tool_tips =  (JSONObject) fromJson.get("toolTips");
			formats = (JSONArray) tool_tips.get("formats");
			formatsBuilder = new Formats.Builder();
			formatsBuilder.setFormats(new String[formats.size()]);
			for (int i = 0; i < formats.size(); i++)
				formatsBuilder.setFormat(i, formats.get(i).toString());
			from.setToolTips(formatsBuilder);

			from.setSounds((String[]) fromJson.get("sounds"))
				.setLangSource(util.convertStringToLang((String) fromJson.get("langSource")))
				.setLangTarget(util.convertStringToLang((String) fromJson.get("langTarget")))
				.setColor((byte) fromJson.get("isColor"))
				.setFormatPAPI((boolean) fromJson.get("isPAPI"));

			JSONObject toJson = (JSONObject) json.get("to");
			Message.Builder to = new Message.Builder();
			if (toJson != null) {
				to.setSender(BukkitUtils.getSenderByName((String) toJson.get("senderName")));

				messages =  (JSONObject) toJson.get("messages");
				formats = (JSONArray) messages.get("formats");
				formatsBuilder = new Formats.Builder();
				formatsBuilder.setFormats(new String[formats.size()]);
				for (int i = 0; i < formats.size(); i++)
					formatsBuilder.setFormat(i, formats.get(i).toString());
				to.setToolTips(formatsBuilder);

				tool_tips =  (JSONObject) toJson.get("toolTips");
				formats = (JSONArray) tool_tips.get("formats");
				formatsBuilder = new Formats.Builder();
				formatsBuilder.setFormats(new String[formats.size()]);
				for (int i = 0; i < formats.size(); i++)
					formatsBuilder.setFormat(i, formats.get(i).toString());
				to.setToolTips(formatsBuilder);

				to.setSounds((String[]) toJson.get("sounds"))
					.setLangSource(util.convertStringToLang((String) toJson.get("langSource")))
					.setLangTarget(util.convertStringToLang((String) toJson.get("langTarget")))
					.setColor((byte) toJson.get("isColor"))
					.setFormatPAPI((boolean) toJson.get("isPAPI"));
			}
			from.setTo(to);

			return from.build();

		} catch (ParseException e) {
			ChatTranslator.getInstance().logger.error(e.toString());
			return null;
		}
	}

	public boolean isEmpty(Message compareWith) {
		return getMessages().getFormats().length     == 0
				&& getMessages().getTexts().length   == 0
				&& getToolTips().getFormats().length == 0
				&& getToolTips().getTexts().length   == 0
				|| this.equals(compareWith);
	}

	public boolean isEmpty() {
		return isEmpty(new Message());
	}

	public UUID getUUID() {
		return uuid;
	}
}
