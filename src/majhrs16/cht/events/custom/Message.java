package majhrs16.cht.events.custom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.bool.Config;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;

public class Message extends Event implements Cancellable {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private static final HandlerList HANDLERS = new HandlerList();


	private Message to;
	private CommandSender sender;
	private String message_format;
	private String messages;
	private String tool_tips;
	private String sounds;
	private boolean is_cancelled = false;
	private String lang_source;
	private String lang_target;
	private Boolean color       = true;
	private Boolean format_papi = true;


	public Message() {}

	public Message(
			Message to,
			CommandSender sender,
			String messageFormat,
			String messages,
			String toolTips,
			String sounds,
			Boolean isCancelled,
			String langSource,
			String langTarget,
			Boolean color,
			Boolean formatPAPI
		) {

		setTo(to);
		setSender(sender);
		setMessageFormat(messageFormat);
		setMessages(messages);
		setToolTips(toolTips);
		setSounds(sounds);
		setCancelledThis(isCancelled);
		setLangSource(langSource);
		setLangTarget(langTarget);
		setColor(color);
		setFormatPAPI(formatPAPI);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public boolean isCancelled() { return is_cancelled; }

	public void setCancelledThis(boolean isCancelled) { this.is_cancelled = isCancelled; }
	public void setCancelled(boolean isCancelled) { // Soporte con CE.
		getTo().setCancelledThis(isCancelled); // Explosivo!!
		setCancelledThis(isCancelled);
	}

	private String getChat(String format, String chat) {
		if (format == null)
			return format;
		FileConfiguration config = plugin.getConfig();
		String path = "formats." + format + "."  + chat;
		if (Config.DEBUG.IF())
			System.out.println("DEBUG: " + config.contains(path));
		return config.contains(path) ? String.join("\n", config.getStringList(path)) : format;
	}

	public void setTo(Message to)                       { this.to             = to == null ? new Message() : to; }
	public void setSender(CommandSender sender)         { this.sender         = sender; }
	public void setMessageFormat(String messageFormat)  { this.message_format = getChat(messageFormat, "messages") ; }
	public void setMessages(String messages)            { this.messages       = messages; }
	public void setToolTips(String toolTips)            { this.tool_tips      = getChat(toolTips, "toolTips"); }
	public void setSounds(String sounds)                { this.sounds         = getChat(sounds, "sounds"); }
	public void setLangSource(String lang)              { this.lang_source    = lang; }
	public void setLangTarget(String lang)              { this.lang_target    = lang; }
	public void setFormatPAPI(Boolean formatPAPI)       { this.format_papi    = formatPAPI; }
	public void setColor(Boolean color)                 { this.color          = color; }


	public Message getTo()           { return to; }
	public CommandSender getSender() { return sender; }
	public String getSenderName()    { return sender.getName(); }
	public String getMessageFormat() { return message_format; }
	public String getMessages()      { return messages; }
	public String getToolTips()      { return tool_tips; }
	public String getSounds()        { return sounds; }
	public String getLangSource()    { return lang_source; }
	public String getLangTarget()    { return lang_target; }
	public Boolean getFormatPAPI()   { return format_papi; }
	public Boolean getColor()        { return color; }

	public Message clone() {
		Message from = new Message();
			Message to = new Message(); // BUGAZO!! Hay que clonarlo manualmente o sino no copia todo. O_o??
				to.setSender(getTo().getSender());
				to.setMessageFormat(getTo().getMessageFormat());
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
			from.setMessageFormat(getMessageFormat());
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

	public String toString() {
		JsonArray jsonArray = new JsonArray();
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

	private String getJsonString(JsonElement element) {
		return !element.isJsonNull() ? element.getAsString() : null;
	}

	public Message valueOf(String data) {
		try {
			JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();

			String player_name = getJsonString(jsonArray.get(0));
			String message_format = jsonArray.get(1).getAsString();
			String messages = getJsonString(jsonArray.get(2)); // Explosivo?
			String tool_tips = getJsonString(jsonArray.get(3));
			String sounds = getJsonString(jsonArray.get(4));
			boolean show = jsonArray.get(5).getAsBoolean();
			String lang_source = jsonArray.get(6).getAsString();
			String lang_target = getJsonString(jsonArray.get(7));
			boolean color = jsonArray.get(8).getAsBoolean();
			boolean papi = jsonArray.get(9).getAsBoolean();

			Player player = null;
			if (player_name != null && (player = Bukkit.getServer().getPlayer(player_name)) == null) {
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
				lang_source,
				lang_target,
				color,
				papi
			);

		} catch (UnsupportedOperationException e) {
			Bukkit.getLogger().warning(e.toString());
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}