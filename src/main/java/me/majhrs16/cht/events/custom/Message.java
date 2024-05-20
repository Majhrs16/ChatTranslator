package me.majhrs16.cht.events.custom;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;
import me.majhrs16.lib.logger.Logger;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import java.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.UUID;

public class Message extends Event implements Cancellable {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final UUID uuid             = UUID.randomUUID();
	private final Logger logger         = plugin.logger;

	private static final HandlerList HANDLERS = new HandlerList();

	private Message to;
	private String name;
	private CommandSender sender;
	private SenderType sender_type;
	private TranslatorBase.LanguagesBase lang_source;
	private TranslatorBase.LanguagesBase lang_target;

	private byte is_color          = 1; // -1 = Disable color, 0 color by permission, 1 force color;
	private boolean is_format_papi = true;
	private boolean _is_processed  = false;
	private boolean is_cancelled   = false;
	private String last_format     = "UNKNOWN";
	private Formats messages       = new Formats();
	private Formats tool_tips      = new Formats();
	private String[] sounds        = new String[0];

	private enum SenderType {
		UNKNOWN,
		CONSOLE,
		PLAYER
	}

	public Message(TranslatorBase.LanguagesBase langSource, TranslatorBase.LanguagesBase langTarget) {
		CommandSender term = Bukkit.getConsoleSender();
		setSender(term);
		if (term == null) { // 1.5.2 bug!
			setSenderName("CONSOLE");
			setSenderType(SenderType.CONSOLE);
		}

		getMessages().setFormats("%ct_messages%");

		lang_source = langSource;
		lang_target = langTarget;
	}

	public Message() {
		this(util.getNativeLang(), ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender()));
	}

	@NotNull public static HandlerList getHandlerList() { return HANDLERS; }
	@NotNull public HandlerList getHandlers()           { return HANDLERS; }

	public boolean isCancelled() {
		return is_cancelled;
	}

	public Message setCancelledThis(boolean isCancelled) {
		this.is_cancelled  = isCancelled;
		return this;
	}

	public void setCancelled(boolean isCancelled) {
		try {
			getTo().setCancelledThis(isCancelled); // Soporte con CE.

		} catch (NullPointerException e) {
			logger.error(e.toString());
		}

		setCancelledThis(isCancelled);
	}

	public Message setTo(Message to) {
		this.to = to == null ? new Message() : to;
		return this;
	}

	public Message setSender(CommandSender sender) {
		this.sender = sender;
		setSenderName(sender == null ? "UNKNOWN" : sender.getName());

		if (sender == null)
			setSenderType(SenderType.UNKNOWN);

		else
			setSenderType(sender instanceof Player ? SenderType.PLAYER : SenderType.CONSOLE);

		return this;
	}

	public Message setSender(String sender) {
		// Support for CoT + CE!.
		setSender(BukkitUtils.getSenderByName(sender));

		return this;
	}

	private Message setSenderType(SenderType type) {
		this.sender_type = type;
		return this;
	}

	public Message setSenderName(String name) {
		this.name = name;
		return this;
	}

	private Message setMessages(Formats formats) {
		this.messages = formats;
		return this;
	}
	private Message setToolTips(Formats formats) {
		this.tool_tips = formats;
		return this;
	}

	public Message setSound(int index, String sounds) {
		this.sounds[index] = sounds;
		return this;
	}

	public Message setSounds(String... sounds) {
		this.sounds = sounds;
		return this;
	}

	public Message setLangSource(TranslatorBase.LanguagesBase lang) {
		this.lang_source = lang;
		return this;
	}

	public Message setLangTarget(TranslatorBase.LanguagesBase lang) {
		this.lang_target = lang;
		return this;
	}

	public Message setLangSource(String lang) {
		// Support for CoT + CE!
		this.lang_source = util.convertStringToLang(lang);
		return this;
	}

	public Message setLangTarget(String lang) {
		// Support for CoT + CE!
		this.lang_target = util.convertStringToLang(lang);
		return this;
	}

	public Message setFormatPAPI(boolean formatPAPI) {
		this.is_format_papi = formatPAPI;
		return this;
	}

	public Message setColor(byte color) {
		this.is_color = color;
		return this;
	}

	public Message setColor(@Range(from = -1, to = 1) int color) {
		this.is_color = (byte) color;
		return this;
	}

	public Message format(String path, UnaryOperator<String> preFormats, UnaryOperator<String> preTexts) {
		util.applyMessagesFormat(this, path, (formats, texts) -> {
			if (preFormats != null) formats.replaceAll(preFormats);
			if (preTexts != null) texts.replaceAll(preTexts);
		});

		util.applyToolTipsFormat(this, path, (formats, texts) -> {
			if (preFormats != null) formats.replaceAll(preFormats);
			if (preTexts != null) texts.replaceAll(preTexts);
		});

		util.applySoundsFormat(this, path);

		last_format = path;

		return this;
	}

	public Message format(String path, UnaryOperator<String> preFormats) {
		return format(path, preFormats, null);
	}

	public Message format(String path) {
		return format(path, null, null);
	}


	public Message getTo()		      { return to; }

	public CommandSender getSender()  { return sender; }
	public String getSenderType()	  { return sender_type.toString(); }
	public String getSenderName()	  { return name; }

	public String getSound(int index) { return sounds[index]; }

	public Formats getMessages()      { return messages; }
	public Formats getToolTips()      { return tool_tips; }
	public String[] getSounds()       { return sounds; }

	public TranslatorBase.LanguagesBase getLangSource() { return lang_source; }
	public TranslatorBase.LanguagesBase getLangTarget() { return lang_target; }

	public boolean getFormatPAPI()    { return is_format_papi; }
	public byte isColor()     { return is_color; }
	public String getLastFormatPath() { return last_format; }

	public void silent() {}

	public Message clone() {
		Message from = new Message(util.getNativeLang(), plugin.storage.getDefaultLang());
//			BUGAZO!! Hay que clonarlo manualmente o sino no copia completamente. O_o??
			Message to = new Message(util.getNativeLang(), plugin.storage.getDefaultLang());
				if (getTo() != null) {
					to.setSender(getTo().getSender());
					to.setSenderName(getTo().getSenderName());
					to.getMessages().setFormats(getTo().getMessages().getFormats());
					to.getMessages().setTexts(getTo().getMessages().getTexts());
					to.getToolTips().setFormats(getTo().getToolTips().getFormats());
					to.getToolTips().setTexts(getTo().getToolTips().getTexts());
					to.setSounds(getTo().getSounds());
					to.setCancelledThis(getTo().isCancelled());
					to.setLangSource(getTo().getLangSource());
					to.setLangTarget(getTo().getLangTarget());
					to.setColor(getTo().isColor());
					to.setFormatPAPI(getTo().getFormatPAPI());
					to.last_format = getTo().getLastFormatPath();
				}
			from.setTo(to);

			from.setSender(getSender());
			from.setSenderName(getSenderName());
			from.getMessages().setFormats(getMessages().getFormats());
			from.getMessages().setTexts(getMessages().getTexts());
			from.getToolTips().setFormats(getToolTips().getFormats());
			from.getToolTips().setTexts(getToolTips().getTexts());
			from.setSounds(getSounds());
			from.setCancelledThis(isCancelled());
			from.setLangSource(getLangSource());
			from.setLangTarget(getLangTarget());
			from.setColor(isColor());
			from.setFormatPAPI(getFormatPAPI());
			from.last_format = getLastFormatPath();

		return from;
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
			from.put("isCancelled", isCancelled());
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
				to.put("isCancelled", getTo().isCancelled());
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
		JSONArray formats;

		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

			JSONObject fromJson = (JSONObject) json.get("from");
				Message from = new Message();
					from.setSender(BukkitUtils.getSenderByName((String) fromJson.get("senderName")));

					messages =  (JSONObject) fromJson.get("messages");
						formats = (JSONArray) messages.get("formats");
					from.getMessages().setFormats((String[]) formats.toArray());

					tool_tips =  (JSONObject) fromJson.get("toolTips");
						formats = (JSONArray) tool_tips.get("formats");
					from.getToolTips().setFormats((String[]) formats.toArray());

					from.setSounds((String[]) fromJson.get("sounds"));
					from.setCancelledThis((boolean) fromJson.get("isCancelled"));
					from.setLangSource((String) fromJson.get("langSource"));
					from.setLangTarget((String) fromJson.get("langTarget"));
					from.setColor((byte) fromJson.get("isColor"));
					from.setFormatPAPI((boolean) fromJson.get("isPAPI"));

			JSONObject toJson = (JSONObject) json.get("to");
				Message to = new Message();
					if (toJson != null) {
						to.setSender(BukkitUtils.getSenderByName((String) toJson.get("senderName")));

						messages =  (JSONObject) fromJson.get("messages");
							formats = (JSONArray) messages.get("formats");
						from.getMessages().setFormats((String[]) formats.toArray());

						tool_tips =  (JSONObject) fromJson.get("toolTips");
							formats = (JSONArray) tool_tips.get("formats");
						from.getToolTips().setFormats((String[]) formats.toArray());

						to.setSounds((String[]) toJson.get("sounds"));
						to.setCancelledThis((boolean) toJson.get("isCancelled"));
						to.setLangSource((String) toJson.get("langSource"));
						to.setLangTarget((String) toJson.get("langTarget"));
						to.setColor((byte) toJson.get("isColor"));
						to.setFormatPAPI((boolean) toJson.get("isPAPI"));
					}
				from.setTo(to);

			return from;

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

//	INTERNAL USE, DO NOT USE;
	public boolean _isProcessed() {
		return _is_processed;
	}

	public void _setProcessed(boolean processed) {
		_is_processed = processed;
	}
}