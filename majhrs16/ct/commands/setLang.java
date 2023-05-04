package majhrs16.ct.commands;

import majhrs16.ct.GT;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class setLang implements CommandExecutor {
  private main plugin;
  
  public setLang(main plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    GT g = new GT();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (args.length > 0) {
        String PERMISSION = "ChatTranslator.lang.others";
        if (args.length == 2) {
          if (!sender.hasPermission(PERMISSION)) {
            String str2 = g.getCode(args[1]);
            if (!g.isSupport(str2))
              str2 = config.getString("default-lang"); 
            player.sendMessage(String.valueOf(this.plugin.name) + " " + ChatColor.RED + g.translateText("Usted no tiene permisos para ejecutar este comando.", "es", str2));
            return false;
          } 
          Player player2 = Bukkit.getServer().getPlayer(args[0]);
          String pathPlayer = "players." + player2.getUniqueId();
          String str1 = g.getCode(args[1]);
          if (g.isSupport(str1)) {
            config.set(pathPlayer, str1);
            this.plugin.saveConfig();
            player.sendMessage(String.valueOf(this.plugin.name) + " " + ChatColor.GREEN + g.translateText(String.valueOf(player.getName()) + "ha cambiado el idioma de " + player2.getName() + " a " + str1, "es", str1));
            return true;
          } 
        } else {
          String pathPlayer = "players." + player.getUniqueId();
          String str1 = g.getCode(args[0]);
          if (g.isSupport(str1)) {
            config.set(pathPlayer, str1);
            this.plugin.saveConfig();
            player.sendMessage(String.valueOf(this.plugin.name) + " " + ChatColor.GREEN + g.translateText("Su idioma ha sido establecido a " + str1, "es", str1));
            return true;
          } 
        } 
      } 
      String str = config.getString("default-lang");
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " " + g.translateText("Por favor use un codigo de lenguaje valido.", "es", str));
      return false;
    } 
    String lang = config.getString("default-lang");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + " " + ChatColor.RED + g.translateText(" No comandos desde consola.", "es", lang));
    return false;
  }
}
