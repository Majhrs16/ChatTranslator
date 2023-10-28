package majhrs16.cht.events.custom;

import majhrs16.cht.translator.ChatTranslatorAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Message extends Event implements Cancellable {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private static final HandlerList HANDLERS = new HandlerList();

	private Message to;
	private CommandSender sender;
	private String[] messages_formats;
	private String[] messages;
	private String[] tool_tips;
	private String[] sounds;
	private boolean is_cancelled = false;
	private String lang_source;
	private String lang_target;
	private Boolean color        = true;
	private Boolean format_papi  = true;

	public Message() {}

	public static HandlerList getHandlerList() { return HANDLERS; }
	public HandlerList getHandlers()           { return HANDLERS; }

	public boolean isCancelled() { return is_cancelled; }

	public Message setCancelledThis(boolean isCancelled) {
		this.is_cancelled  = isCancelled;
		return this;
	}

	public void setCancelled(boolean isCancelled) {
		try {
			getTo().setCancelledThis(isCancelled); // Soporte con CE.

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		setCancelledThis(isCancelled);
	}

	private String getFormat(String chat, String format) {
		FileConfiguration config = plugin.config.get();
		String path = "formats." + format + "."  + chat;
		if (Config.DEBUG.IF())
			System.out.println("DEBUG exists '" + path + "' ?: " + config.contains(path));
		return config.contains(path) ? String.join("\n", config.getStringList(path)) : format;
	}

	private String[] getChat(String chat, String... formats) {
//		if (formats == null || formats.length < 1)
//			return null;

		List<String> result = new ArrayList<String>();

		for (String format : formats) {
			if (format == null
					|| format.isEmpty()
					|| format.equals("null") // El bug parece venir de afuera.
					)
				continue;

			result.add(getFormat(chat, format));
		}

		return result.toArray(new String[0]);
	}

	public Message setTo(Message to) {
		this.to             = to == null ? new Message() : to;
		return this;
	}

	public Message setSender(CommandSender sender) {
		this.sender         = sender;
		return this;
	}

	public Message setMessageFormat(int index, String messageFormat) {
		this.messages_formats[index] = getFormat("messages", messageFormat);
		return this;
	}

	public Message setMessage(int index, String messages) {
		this.messages[index]         = messages;
		return this;
	}

	public Message setToolTip(int index, String toolTips) {
		this.tool_tips[index]        = getFormat("toolTips", toolTips);
		return this;
	}
	
	public Message setSound(int index, String sounds) {
		this.sounds[index]           = getFormat("sounds", sounds);
		return this;
	}

	public Message setMessagesFormats(String messageFormat) {
		this.messages_formats = getChat("messages", messageFormat);
		return this;
	}

	public Message setMessages(String messages) {
		this.messages         = messages == null ? null : messages.split("\n");
		return this;
	}

	public Message setToolTips(String toolTips) {
		this.tool_tips        = getChat("toolTips", toolTips);
		return this;
	}

	public Message setSounds(String sounds) {
		this.sounds           = getChat("sounds", sounds);
		return this;
	}

	public Message setMessagesFormats(String... messageFormat) {
		this.messages_formats = getChat("messages", messageFormat);
		return this;
	}

	public Message setMessages(String... messages) {
		this.messages       = messages;
		return this;
	}

	public Message setToolTips(String... toolTips) {
		this.tool_tips      = getChat("toolTips", toolTips);
		return this;
	}

	public Message setSounds(String... sounds) {
		this.sounds         = getChat("sounds", sounds);
		return this;
	}

	public Message setLangSource(String lang) {
		this.lang_source    = lang;
		return this;
	}

	public Message setLangTarget(String lang) {
		this.lang_target    = lang;
		return this;
	}

	public Message setFormatPAPI(Boolean formatPAPI) {
		this.format_papi    = formatPAPI;
		return this;
	}

	public Message setColor(Boolean color) {
		this.color          = color;
		return this;
	}


	public Message getTo()           { return to; }

	public CommandSender getSender() { return sender; }
	public String getSenderName()    { return sender == null ? null : sender.getName(); }

	public String getMessageFormat(int index) {
		String out = messages_formats[index];
		return out.isEmpty() ? null : out;
	}

	public String getMessage(int index) {
		String out = messages[index];
		return out.isEmpty() ? null : out;
	}
	public String getToolTip(int index) {
		String out = tool_tips[index];
		return out.isEmpty() ? null : out;
	}
	public String getSound(int index)   {
		String out = sounds[index];
		return out.isEmpty() ? null : out;
	}

	private String getFormatAsString(String[] format) {
		String out = null;
		if (format != null)
			out = String.join("\n", format);
		return out == null || out.isEmpty() || out == "null" ? null : out;
	}

	public String getMessageFormat() { return getFormatAsString(messages_formats); }
	public String getMessages()      { return getFormatAsString(messages); }
	public String getToolTips()      { return getFormatAsString(tool_tips); }
	public String getSounds()        { return getFormatAsString(sounds); }

	public String getLangSource()    { return lang_source; }
	public String getLangTarget()    { return lang_target; }

	public Boolean getFormatPAPI()   { return format_papi; }
	public Boolean getColor()        { return color; }

	public Message clone() {
		Message from = new Message();
			Message to = new Message(); // BUGAZO!! Hay que clonarlo manualmente o sino no copia todo. O_o??
				if (getTo() != null) {
					to.setSender(getTo().getSender());
					to.setMessagesFormats(getTo().getMessageFormat());
					to.setMessages(getTo().getMessages());
					to.setToolTips(getTo().getToolTips());
					to.setSounds(getTo().getSounds());
					to.setCancelledThis(getTo().isCancelled());
					to.setLangSource(getTo().getLangSource());
					to.setLangTarget(getTo().getLangTarget());
					to.setColor(getTo().getColor());
					to.setFormatPAPI(getTo().getFormatPAPI());
				}
			from.setTo(to);

			from.setSender(getSender());
			from.setMessagesFormats(getMessageFormat());
			from.setMessages(getMessages());
			from.setToolTips(getToolTips());
			from.setSounds(getSounds());
			from.setCancelledThis(isCancelled());
			from.setLangSource(getLangSource());
			from.setLangTarget(getLangTarget());
			from.setColor(getColor());
			from.setFormatPAPI(getFormatPAPI());
		return from;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		JSONArray jsonArray = new JSONArray();
			jsonArray.add(getSenderName());
			jsonArray.add(getMessageFormat());
			jsonArray.add(getMessages());
			jsonArray.add(getToolTips());
			jsonArray.add(getSounds());
			jsonArray.add(isCancelled());
			jsonArray.add(getLangSource());
			jsonArray.add(getLangTarget());
			jsonArray.add(getColor());
			jsonArray.add(getFormatPAPI());
		return jsonArray.toString();
	}

	public static Message valueOf(String json) {
		try {
			JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);

			String player_name    = (String) jsonArray.get(0);
			String message_format = (String) jsonArray.get(1);
			String messages       = (String) jsonArray.get(2);
			String tool_tips      = (String) jsonArray.get(3);
			String sounds         = (String) jsonArray.get(4);
			boolean isCancelled   = (boolean) jsonArray.get(5);
			String lang_source    = (String) jsonArray.get(6);
			String lang_target    = (String) jsonArray.get(7);
			boolean color         = (boolean) jsonArray.get(8);
			boolean papi          = (boolean) jsonArray.get(9);

			CommandSender player;
			if (player_name == null)
				player = Bukkit.getConsoleSender();
	
			else
				player = Bukkit.getServer().getPlayer(player_name);

			if (lang_source == null)
				lang_source = ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender());

			if (lang_target == null)
				lang_target = ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender());

			Message from = new Message();
				from.setSender(player);
				from.setMessagesFormats(message_format);
				from.setMessages(messages);
				from.setToolTips(tool_tips);
				from.setSounds(sounds);

				from.setCancelledThis(isCancelled);

				from.setLangSource(lang_source);
				from.setLangTarget(lang_target);

				from.setColor(color);
				from.setFormatPAPI(papi);
			return from;

		} catch (UnsupportedOperationException e) {
			Bukkit.getLogger().warning(e.toString());
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}