package majhrs16.ct.events.custom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.bukkit.event.Event;
import org.json.JSONArray;
import org.bukkit.Bukkit;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;

public class Message extends Event implements Cancellable {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private static final HandlerList HANDLERS = new HandlerList();

	private Message father;
	private CommandSender sender;
	private String message_format;
	private String tool_tips;
	private String sounds;
	private String messages;
	private boolean isCancelled;

	private String lang;

	private Boolean color = true;
	private Boolean format_papi = true;


	public Message() {
//		setCancelledThis(false);
	}

	public Message(
			Message father,
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

		setFather(father);
		setPlayer(sender);
		setMessageFormat(message_format);
		setMessages(messages);
		setToolTips(tool_tips);
		setSounds(sounds);

		setCancelledThis(isCancelled);

		setLang(lang);

		setColorPersonalized(color);
		setFormatMessage(format_papi);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setCancelledThis(boolean isCancelled) { this.isCancelled = isCancelled; }
	public void setCancelled(boolean isCancelled) {
		getFather().setCancelledThis(isCancelled); // Explosivo!!
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

	public void setFather(Message father)               { this.father = father == null ? new Message() : father; }
	public void setPlayer(CommandSender sender)         { this.sender = sender; }
	public void setMessageFormat(String message_format) { this.message_format = getChat(message_format, "messages") ; }
	public void setMessages(String messages)            { this.messages = messages; }
	public void setToolTips(String tool_tips)           { this.tool_tips = getChat(tool_tips, "toolTips"); }
	public void setSounds(String sounds)                { this.sounds = getChat(sounds, "sounds"); }
	public void setLang(String lang)                    { this.lang = lang; }
	public void setFormatMessage(Boolean format_papi)   { this.format_papi = format_papi; }
	public void setColorPersonalized(Boolean color)     { this.color = color; }


	public Message getFather()            { return father; }
	public CommandSender getPlayer()      { return sender; }
	public String getPlayerName()         { return sender.getName(); }
	public String getMessageFormat()      { return message_format; }
	public String getMessages()           { return messages; }
	public String getToolTips()           { return tool_tips; }
	public String getSounds()             { return sounds; }
	public String getLang()               { return lang; }
	public Boolean getFormatMessage()     { return format_papi; }
	public Boolean getColorPersonalized() { return color; }

	public Message clone() {
		Message DC = new Message();
			Message father = new Message(); // BUGAZO!!
				father.setPlayer(getFather().getPlayer());
				father.setMessageFormat(getFather().getMessageFormat());
				father.setMessages(getFather().getMessages());
				father.setToolTips(getFather().getToolTips());
				father.setSounds(getFather().getSounds());
				father.setCancelledThis(getFather().isCancelled());

				father.setLang(getFather().getLang());

				father.setColorPersonalized(getFather().getColorPersonalized());
				father.setFormatMessage(getFather().getFormatMessage());
			DC.setFather(father);

			DC.setPlayer(getPlayer());
			DC.setMessageFormat(getMessageFormat());
			DC.setMessages(getMessages());
			DC.setToolTips(getToolTips());
			DC.setSounds(getSounds());
			DC.setCancelledThis(isCancelled());

			DC.setLang(getLang());

			DC.setColorPersonalized(getColorPersonalized());
			DC.setFormatMessage(getFormatMessage());
		return DC;
	}

	public String toString() {
		JSONArray jsonArray = new JSONArray();
			jsonArray.put(getPlayerName());
			jsonArray.put(getMessageFormat());
			jsonArray.put(getMessages());
			jsonArray.put(getToolTips());
			jsonArray.put(getToolTips());
			jsonArray.put(isCancelled());
			jsonArray.put(getLang());
			jsonArray.put(getColorPersonalized());
			jsonArray.put(getFormatMessage());
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

			Player player = Bukkit.getServer().getPlayer(player_name);
			if (player == null) {
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