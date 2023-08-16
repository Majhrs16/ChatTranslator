package majhrs16.cht.events.custom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.bukkit.event.Event;
import org.json.JSONArray;
import org.bukkit.Bukkit;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public class Message extends Event implements Cancellable {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private static final HandlerList HANDLERS = new HandlerList();


	private Message to;
	private CommandSender sender;
	private String message_format;
	private String messages;
	private String tool_tips;
	private String sounds;
	private boolean isCancelled = false;
	private String lang;
	private Boolean color       = true;
	private Boolean format_papi = true;


	public Message() {}

	public Message(
			Message to,
			CommandSender sender,
			String message_format,
			String messages,
			String tool_tips,
			String sounds,
			Boolean isCancelled,
			String lang,
			Boolean color,
			Boolean format_papi
		) {

		setTo(to);
		setSender(sender);
		setMessageFormat(message_format);
		setMessages(messages);
		setToolTips(tool_tips);
		setSounds(sounds);
		setCancelledThis(isCancelled);
		setLang(lang);
		setColor(color);
		setFormatPAPI(format_papi);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public boolean isCancelled() { return isCancelled; }

	public void setCancelledThis(boolean isCancelled) { this.isCancelled = isCancelled; }
	public void setCancelled(boolean isCancelled) { // Soporte con CE.
		getTo().setCancelledThis(isCancelled); // Explosivo!!
		setCancelledThis(isCancelled);
	}

	private String getChat(String format, String chat) {
		if (format == null)
			return format;
		FileConfiguration config = plugin.getConfig();
		String path = "formats." + format + "."  + chat;
		if (util.IF(config, "debug"))
			System.out.println("DEBUG: " + config.contains(path));
		return config.contains(path) ? String.join("\n", config.getStringList(path)) : format;
	}

	public void setTo(Message to)                       { this.to             = to == null ? new Message() : to; }
	public void setSender(CommandSender sender)         { this.sender         = sender; }
	public void setMessageFormat(String message_format) { this.message_format = getChat(message_format, "messages") ; }
	public void setMessages(String messages)            { this.messages       = messages; }
	public void setToolTips(String tool_tips)           { this.tool_tips      = getChat(tool_tips, "toolTips"); }
	public void setSounds(String sounds)                { this.sounds         = getChat(sounds, "sounds"); }
	public void setLang(String lang)                    { this.lang           = lang; }
	public void setFormatPAPI(Boolean format_papi)      { this.format_papi    = format_papi; }
	public void setColor(Boolean color)                 { this.color          = color; }


	public Message getTo()                { return to; }
	public CommandSender getSender()      { return sender; }
	public String getSenderName()         { return sender.getName(); }
	public String getMessageFormat()      { return message_format; }
	public String getMessages()           { return messages; }
	public String getToolTips()           { return tool_tips; }
	public String getSounds()             { return sounds; }
	public String getLang()               { return lang; }
	public Boolean getFormatPAPI()        { return format_papi; }
	public Boolean getColor()             { return color; }

	public Message clone() {
		Message from = new Message();
			Message to = new Message(); // BUGAZO!! Hay que clonarlo manualmente o sino no copia todo. O_o??
				to.setSender(getTo().getSender());
				to.setMessageFormat(getTo().getMessageFormat());
				to.setMessages(getTo().getMessages());
				to.setToolTips(getTo().getToolTips());
				to.setSounds(getTo().getSounds());
				to.setCancelledThis(getTo().isCancelled());
				to.setLang(getTo().getLang());
				to.setColor(getTo().getColor());
				to.setFormatPAPI(getTo().getFormatPAPI());
			from.setTo(to);

			from.setSender(getSender());
			from.setMessageFormat(getMessageFormat());
			from.setMessages(getMessages());
			from.setToolTips(getToolTips());
			from.setSounds(getSounds());
			from.setCancelledThis(isCancelled());
			from.setLang(getLang());
			from.setColor(getColor());
			from.setFormatPAPI(getFormatPAPI());
		return from;
	}

	public String toString() {
		JSONArray jsonArray = new JSONArray();
			jsonArray.put(getSenderName());
			jsonArray.put(getMessageFormat());
			jsonArray.put(getMessages());
			jsonArray.put(getToolTips());
			jsonArray.put(getSounds());
			jsonArray.put(isCancelled());
			jsonArray.put(getLang());
			jsonArray.put(getColor());
			jsonArray.put(getFormatPAPI());
		return jsonArray.toString();
	}

	public Message valueOf(String data) {
		try {
			JSONArray jsonArray = new JSONArray(data);

			String player_name    = jsonArray.getString(0);
			String message_format = jsonArray.getString(1);
			String messages       = jsonArray.getString(2);
			String tool_tips      = jsonArray.getString(3);
			String sounds         = jsonArray.getString(4);
			boolean show          = jsonArray.getBoolean(5);
			String lang           = jsonArray.getString(6);
			boolean color         = jsonArray.getBoolean(7);
			boolean papi          = jsonArray.getBoolean(8);

			Player player = null;
			if (player_name != null && (player = Bukkit.getServer().getPlayer(player_name)) == null) { // Sino se hace este filtro se HYPER BUGEA.
				return null;
			}

			return new Message(
				null, 
				player,
				message_format,
				messages,
				tool_tips,
				sounds,
				show,
				lang,
				color,
				papi
			);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}