package majhrs16.ct.commands;

import java.util.ArrayList;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.translator.API;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CT implements CommandExecutor {
  private ChatTranslator plugin;
  
  private API API;
  
  public CT(ChatTranslator plugin) {
    this.plugin = plugin;
    this.API = new API(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    String lang = this.API.getLang(sender);
    if (args.length > 0) {
      String msgFormat;
      byte b;
      int j;
      String arrayOfString[], str1;
      switch ((str1 = args[0].toLowerCase()).hashCode()) {
        case -934641255:
          if (!str1.equals("reload"))
            break; 
          if (!sender.hasPermission("ChatTranslator.admin")) {
            this.API.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          updateConfig(sender, lang);
          return true;
        case 106437299:
          if (!str1.equals("parse"))
            break; 
          if (!sender.hasPermission("ChatTranslator.admin")) {
            this.API.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          msgFormat = "";
          for (j = (arrayOfString = args).length, b = 0; b < j; ) {
            String slice = arrayOfString[b];
            msgFormat = String.valueOf(msgFormat) + slice;
            b++;
          } 
          this.API.sendMessage(null, sender, msgFormat.replaceFirst("parse", ""), "&eDato de ejemplo", "es");
          return true;
        case 351608024:
          if (!str1.equals("version"))
            break; 
          showVersion(sender, lang);
          return true;
      } 
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7 Ese comando &cno existe&f!", lang);
      return false;
    } 
    ArrayList<String> msg = new ArrayList<>();
    msg.add(this.plugin.name);
    msg.add("&e  /lang <lang>\n&7Especifique con su codigo de idioma&f, &apara traducir el chat a su gusto&f.\n&f  (&7Independientemente de su lenguaje en el Minecraft&f).");
    msg.add("");
    msg.add("&e  /ct");
    msg.add("&e    version\n&aVisualizar version&f.");
    msg.add("&e    reload\n&aRecargar config&f.");
    msg.add("&e    parse &f<&eformatMsg&f>\n&aProcesa en tiempo real formatMsg&f(&7Sirve para testear &f;&aD&f)&f.\n&e  EN DESARROLLO&f.");
    for (int i = 0; i < msg.size(); i++) {
      if (msg.get(i) == "")
        sender.sendMessage(""); 
      String[] l = ((String)msg.get(i)).split("\n", 2);
      String title = ChatColor.translateAlternateColorCodes("&".charAt(0), l[0]);
      String description = "";
      if (l.length > 1)
        description = this.API.formatMsg(null, sender, "", l[1], "es", this.API.getLang(sender)); 
      if (sender instanceof Player) {
        Player p = (Player)sender;
        TextComponent message = new TextComponent(title);
        if (l.length > 1) {
          ComponentBuilder hoverText = new ComponentBuilder(description);
          message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create()));
        } 
        p.spigot().sendMessage((BaseComponent)message);
      } else {
        Bukkit.getConsoleSender().sendMessage(title);
        if (l.length > 1)
          Bukkit.getConsoleSender().sendMessage("\t" + description); 
      } 
    } 
    return true;
  }
  
  public void showVersion(CommandSender sender, String lang) {
    this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &7 Version&f: &a" + this.plugin.version, lang);
  }
  
  public void updateConfig(CommandSender sender, String lang) {
    try {
      this.plugin.reloadConfig();
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7 Recargado config.yml&f.", lang);
      this.plugin.reloadPlayers();
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7 Recargado players.yml&f.", lang);
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7 Config recargada &aexitosamente&f.", lang);
    } catch (Exception e) {
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&f [&4ERROR&f] &cNO se pudo recargar la config&f. &ePor favor, vea su consola &f/ &eterminal&f.", lang);
      e.printStackTrace();
    } 
  }
}
