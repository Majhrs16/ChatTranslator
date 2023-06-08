package majhrs16.ct.events.custom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;

public class Message extends Event implements Cancellable {
	private ChatTranslator plugin = ChatTranslator.plugin;

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    
    private Pattern chat = Pattern.compile(_getRegex(), Pattern.CASE_INSENSITIVE);
    

    private Message father;
	private CommandSender sender;
	private String message_format;
	private String tool_tips;
	private String sounds;
	private String messages;
	private Boolean show = true;

	private String lang;

	private Boolean color = true;
	private Boolean format_papi = true;


	public Message() {
    	setCancelled(false);
    }

    public Message(
    		Message father,
    		CommandSender sender,
    		String message_format,
    		String messages,
    		String tool_tips,
    		String sounds,
    		Boolean show,

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
        setShow(show);
        
        setLang(lang);
        
        setColorPersonalized(color);
        setFormatMessage(format_papi);
        
        setCancelled(false);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
    	return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
    	this.isCancelled = isCancelled;
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

    public void setFather(Message father)               { this.father = father; }
	public void setPlayer(CommandSender sender)         { this.sender = sender; }
	public void setMessageFormat(String message_format) { this.message_format = getChat(message_format, "messages") ; }
	public void setMessages(String messages)            { this.messages = messages; }
	public void setToolTips(String tool_tips)           { this.tool_tips = getChat(tool_tips, "toolTips"); }
	public void setSounds(String sounds)                { this.sounds = getChat(sounds, "sounds"); }
	public void setShow(Boolean show)                   { this.show = show; }
	public void setLang(String lang)                    { this.lang = lang; }
	public void setFormatMessage(Boolean format_papi)   { this.format_papi = format_papi; }
	public void setColorPersonalized(Boolean color)     { this.color = color; }


    public Message getFather()            { return this.father; }
	public CommandSender getPlayer()      { return sender; }
	public String getPlayerName()         { return sender.getName(); }
	public String getMessageFormat()      { return message_format; }
	public String getMessages()           { return messages; }
	public String getToolTips()           { return tool_tips; }
	public String getSounds()             { return sounds; }
	public Boolean getShow()              { return show; }
	public String getLang()               { return lang; }
	public Boolean getFormatMessage()     { return format_papi; }
	public Boolean getColorPersonalized() { return color; }
	
	public Message clone() {
		Message DC = new Message();
			DC.setFather(father);
			DC.setPlayer(sender);
			DC.setMessageFormat(message_format);
			DC.setMessages(messages);
			DC.setToolTips(tool_tips);
			DC.setSounds(sounds);
			DC.setShow(show);

			DC.setLang(lang);

			DC.setColorPersonalized(color);
			DC.setFormatMessage(format_papi);
		return DC;
	}

	public static String _getRegex() {
		return "\\[\\'(.+)\\'\\, ?\\'(.+)\\'\\, ?\\'(.+)\\'\\, ?\\'(.+)\\'\\, ?\\'(.+)\\'\\, ?(true|false), ?\\'(.+)\\'\\, ?(true|false), ?(true|false)\\]";
	}
	
	public String toString() {
		return String.format("['%s', '%s', '%s', '%s', '%s', %s, '%s', %s, %s]",
			sender.getName(),
			message_format,
			messages,
			tool_tips,
			sounds,
			show,
			lang,
			color,
			format_papi
		);
	}
	
	public Message valueOf(String data) {
		Matcher Chat = chat.matcher(data);
		
		if (!Chat.find())
			return null;

		String player_name    = Chat.group(1);
		String message_format = Chat.group(2);
		String messages       = Chat.group(3);
		String tool_tips      = Chat.group(4);
		String sounds         = Chat.group(5);
		Boolean show          = Boolean.valueOf(Chat.group(6));
		String lang           = Chat.group(7);
		Boolean color         = Boolean.valueOf(Chat.group(8));
		Boolean papi          = Boolean.valueOf(Chat.group(9));
		
		Player player;
		try {
			player = Bukkit.getServer().getPlayer(player_name);

		} catch (NullPointerException e) {
			player = null;
		}

		if (player == null)
			return null;

		return new Message(
			new Message(),
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
	}
}