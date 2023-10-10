package majhrs16.cht.events.custom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Message extends Event implements Cancellable {
	private static final ChatTranslator plugin = ChatTranslator.getInstance();
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
	private Boolean color       = true;
	private Boolean format_papi = true;

	public Message() {}

	public static HandlerList getHandlerList() { return HANDLERS; }
	public HandlerList getHandlers()           { return HANDLERS; }

	public boolean isCancelled() { return is_cancelled; }

	public void setCancelledThis(boolean isCancelled) { this.is_cancelled  = isCancelled; }
	public void setCancelled(boolean isCancelled) { // Soporte con CE.
		try {
			getTo().setCancelledThis(isCancelled);

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

	public void setTo(Message to)                                 { this.to             = to == null ? new Message() : to; }
	public void setSender(CommandSender sender)                   { this.sender         = sender; }

	public void setMessageFormat(int index, String messageFormat) { this.messages_formats[index] = getFormat("messages", messageFormat); }
	public void setMessage(int index, String messages)            { this.messages[index]         = messages; }
	public void setToolTip(int index, String toolTips)            { this.tool_tips[index]        = getFormat("toolTips", toolTips); }
	public void setSound(int index, String sounds)                { this.sounds[index]           = getFormat("sounds", sounds); }

	public void setMessagesFormats(String messageFormat)          { this.messages_formats = getChat("messages", messageFormat); }
	public void setMessages(String messages)                      { this.messages         = messages == null ? null : messages.split("\n"); }
	public void setToolTips(String toolTips)                      { this.tool_tips        = getChat("toolTips", toolTips); }
	public void setSounds(String sounds)                          { this.sounds           = getChat("sounds", sounds); }

	public void setMessagesFormats(String... messageFormat)       { this.messages_formats = getChat("messages", messageFormat); }
	public void setMessages(String... messages)                   { this.messages       = messages; }
	public void setToolTips(String... toolTips)                   { this.tool_tips      = getChat("toolTips", toolTips); }
	public void setSounds(String... sounds)                       { this.sounds         = getChat("sounds", sounds); }

	public void setLangSource(String lang)                        { this.lang_source    = lang; }
	public void setLangTarget(String lang)                        { this.lang_target    = lang; }

	public void setFormatPAPI(Boolean formatPAPI)                 { this.format_papi    = formatPAPI; }
	public void setColor(Boolean color)                           { this.color          = color; }

	public Message getTo()           { return to; }

	public CommandSender getSender() { return sender; }
	public String getSenderName()    { return sender.getName(); }

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

	public Message valueOf(String json) {
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

			Player player = null;
			if (player_name == null || (player = Bukkit.getServer().getPlayer(player_name)) == null) {
				return null;
			}

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