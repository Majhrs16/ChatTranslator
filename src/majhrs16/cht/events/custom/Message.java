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
	private ChatTranslator plugin = ChatTranslator.getInstance();
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

	public void setCancelledThis(boolean isCancelled) { this.is_cancelled  = isCancelled; }
	public void setCancelled(boolean isCancelled) { // Soporte con CE.
		getTo().setCancelledThis(isCancelled); // Explosivo!!
		setCancelledThis(isCancelled);
	}

	private String getChat(String[] formats, String chat) {
		if (formats == null || formats.length < 1)
			return null;

		List<String> result = new ArrayList<String>();

		for (String format : formats) {
			if (format == null)
				continue;

			FileConfiguration config = plugin.config.get();
			String path = "formats." + format + "."  + chat;
			if (Config.DEBUG.IF())
				System.out.println("DEBUG exists '" + path + "' ?: '" + config.contains(path) + "'");
			result.add(config.contains(path) ? String.join("\n", config.getStringList(path)) : format);
		}

		return result.size() > 0 ? String.join("\n", result) : null;
	}

	public void setTo(Message to)                       { this.to          = to == null ? new Message() : to; }
	public void setSender(CommandSender sender)         { this.sender      = sender; }

	public void setMessageFormat(String... messageFormat) {
		this.message_format = getChat(messageFormat, "messages");
	}

	public void setMessages(String... messages) {
		if (messages == null)
			this.messages   = null;

		else
			this.messages   = String.join("\n", messages);
	}

	public void setToolTips(String... toolTips) {
		this.tool_tips      = getChat(toolTips, "toolTips");
	}

	public void setSounds(String... sounds)             { this.sounds      = getChat(sounds, "sounds"); }
	public void setLangSource(String lang)              { this.lang_source = lang; }
	public void setLangTarget(String lang)              { this.lang_target = lang; }
	public void setFormatPAPI(Boolean formatPAPI)       { this.format_papi = formatPAPI; }
	public void setColor(Boolean color)                 { this.color       = color; }


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


	private String getJsonString(Object obj) {
		return (String) obj;
	}

	public Message valueOf(String data) {
		try {
			JSONArray jsonArray = (JSONArray) new JSONParser().parse(data);

			String player_name = getJsonString(jsonArray.get(0));
			String message_format = (String) jsonArray.get(1);
			String messages = getJsonString(jsonArray.get(2)); // Explosivo?
			String tool_tips = getJsonString(jsonArray.get(3));
			String sounds = getJsonString(jsonArray.get(4));
			boolean show = (boolean) jsonArray.get(5);
			String lang_source = (String) jsonArray.get(6);
			String lang_target = getJsonString(jsonArray.get(7));
			boolean color = (boolean) jsonArray.get(8);
			boolean papi = (boolean) jsonArray.get(9);

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