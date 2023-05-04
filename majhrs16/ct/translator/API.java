package majhrs16.ct.translator;

import java.util.UUID;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class API {
  private ChatTranslator plugin;
  
  public GoogleTranslator GT;
  
  public API(ChatTranslator plugin) {
    this.plugin = plugin;
    this.GT = new GoogleTranslator();
  }
  
  public void broadcast(CommandSender sender, String msgFormat, String msg, String sourceLang) {
    util.assertLang(sourceLang);
    if (msgFormat == "" || msgFormat == null)
      msgFormat = "%msg%"; 
    if (msg == "" || msg == null)
      msg = "[no data]"; 
    msgFormat = msgFormat.replace("%player_name%", sender.getName());
    msgFormat = msgFormat.replace("%lang%", getLang(sender));
    sendMessage(sender, (CommandSender)Bukkit.getConsoleSender(), msgFormat, msg, sourceLang);
    for (Player player : Bukkit.getOnlinePlayers())
      sendMessage(sender, (CommandSender)player, msgFormat, msg, sourceLang); 
  }
  
  public void broadcast(CommandSender sender, String msgFormat, String msg) {
    broadcast(sender, msgFormat, msg, getLang(sender));
  }
  
  public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
    util.assertLang(sourceLang, "El sourceLang '" + sourceLang + "' no esta soportado.");
    util.assertLang(targetLang, "El targetLang '" + targetLang + "' no esta soportado.");
    if (msgFormat == null || msgFormat == "")
      msgFormat = "%msg%"; 
    if (msg == null || msg == "")
      msg = "&enull"; 
    if (util.IF(this.plugin.getConfig(), "debug")) {
      if (playerFrom != null)
        Bukkit.getConsoleSender().sendMessage("Debug: PlayerFrom: '" + playerFrom.getName() + "', source: " + sourceLang); 
      if (playerTo != null)
        Bukkit.getConsoleSender().sendMessage("Debug: PlayerTo: '" + playerTo.getName() + "', target: " + targetLang); 
      Bukkit.getConsoleSender().sendMessage("Debug: msgFormat: '" + msgFormat + "'");
      Bukkit.getConsoleSender().sendMessage("Debug: msg: '" + msg + "'");
    } 
    playerTo.sendMessage(formatMsg(playerFrom, playerTo, msgFormat, msg, sourceLang, targetLang));
  }
  
  public void sendMessage(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang) {
    sendMessage(playerFrom, playerTo, msgFormat, msg, sourceLang, getLang(playerTo));
  }
  
  public String formatMsg(CommandSender playerFrom, CommandSender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
    if (msgFormat == "" || msgFormat == null)
      msgFormat = "%msg%"; 
    if (msg == "" || msg == null)
      msg = "[no data]"; 
    FileConfiguration config = this.plugin.getConfig();
    if (playerFrom != null && playerFrom instanceof Player) {
      msgFormat = msgFormat.replace("%player_name%", playerFrom.getName());
      msgFormat = msgFormat.replace("%sourceLang%", sourceLang);
      if (util.checkPAPI().booleanValue() && util.IF(config, "auto-format-messages"))
        msgFormat = PlaceholderAPI.setPlaceholders((Player)playerFrom, msgFormat); 
    } 
    if (playerTo != null && playerTo instanceof Player) {
      msgFormat = msgFormat.replace("$targetLang$", targetLang);
      if (util.checkPAPI().booleanValue() && util.IF(config, "auto-format-messages"))
        msgFormat = PlaceholderAPI.setPlaceholders((Player)playerTo, msgFormat.replace("$", "%")); 
    } 
    if (util.IF(config, "auto-translate-chat") && 
      !sourceLang.equals(targetLang) && 
      playerFrom != playerTo) {
      boolean isStartWithPluginName = false;
      if (msg.startsWith(this.plugin.name)) {
        msg = msg.substring(this.plugin.name.length(), msg.length());
        isStartWithPluginName = true;
      } 
      msg = this.GT.translate(msg, sourceLang, targetLang);
      if (isStartWithPluginName)
        msg = String.valueOf(this.plugin.name) + " " + msg; 
    } 
    msgFormat = ChatColor.translateAlternateColorCodes("&".charAt(0), msgFormat);
    if (util.IF(config, "message-color-personalized"))
      msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg); 
    return msgFormat.replace("%msg%", msg);
  }
  
  public String getLang(CommandSender sender) {
    String lang = null;
    FileConfiguration config = this.plugin.getConfig();
    FileConfiguration players = this.plugin.getPlayers();
    String defaultLang = config.getString("default-lang");
    String path = "";
    if (sender instanceof Player) {
      UUID uUID = ((Player)sender).getUniqueId();
      if (players.contains((String)uUID)) {
        lang = players.getString((String)uUID);
        if (util.checkPAPI().booleanValue() && lang.equals("auto"))
          lang = PlaceholderAPI.setPlaceholders((Player)sender, "%player_locale_short%"); 
      } else if (util.checkPAPI().booleanValue()) {
        lang = PlaceholderAPI.setPlaceholders((Player)sender, "%player_locale_short%");
      } else {
        lang = defaultLang;
      } 
    } else {
      lang = defaultLang;
    } 
    if (!this.GT.isSupport(lang))
      if (this.GT.isSupport(defaultLang)) {
        sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &7El idioma &f'&b" + lang + "&f' &cno esta soportado&f.", "es", lang);
        lang = defaultLang;
      } else {
        sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &7El idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.", "es", lang);
        return null;
      }  
    return lang;
  }
}
