package majhrs16.ct.commands;

import majhrs16.ct.api;
import majhrs16.ct.main;
import majhrs16.ct.util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class setLang implements CommandExecutor {
  private main plugin;
  
  private api api;
  
  private util util;
  
  public setLang(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
    this.util = new util(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    FileConfiguration players = config;
    try {
      String lang, path;
      Player player2;
      switch (args.length) {
        case 1:
          if (!(sender instanceof Player)) {
            String str = this.api.GT.getCode(config.getString("default-lang"));
            this.api.checkSupportLang(str, "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
            this.api.sendMessage(sender, "", String.valueOf(this.plugin.name) + " &cNo se puede ejecutar este comando desde consola&f.", "es");
            return false;
          } 
          path = "players." + this.util.getUUID(sender);
          lang = this.api.GT.getCode(args[0]);
          this.api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");
          players.set(path, lang);
          this.plugin.saveConfig();
          this.api.sendMessage(sender, "", String.valueOf(this.plugin.name) + " &7Su idioma ha sido &aestablecido &7a &b" + lang + "&f.", "es");
          return true;
        case 2:
          try {
            player2 = Bukkit.getServer().getPlayer(args[0]);
          } catch (NullPointerException e) {
            this.api.sendMessage(sender, "", "&7El jugador &f'&b" + args[0] + "&f' &cno existe&f.", "es");
            player2 = null;
            return false;
          } 
          path = "players." + this.util.getUUID((CommandSender)player2);
          lang = args[1];
          this.api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");
          lang = this.api.GT.getCode(lang);
          if (!sender.hasPermission("ChatTranslator.admin")) {
            this.api.sendMessage(sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          players.set(path, lang);
          this.plugin.saveConfig();
          this.api.broadcast(sender, 
              String.valueOf(this.plugin.name) + " &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player2%", player2.getName()).replace("%lang%", lang), "", 
              "es");
          return true;
      } 
      this.api.checkSupportLang(this.api.GT.getCode(config.getString("default-lang")), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
      this.api.sendMessage(sender, "", String.valueOf(this.plugin.name) + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f[&6codigo&f]&f.", "es");
      return false;
    } catch (IllegalArgumentException e) {
      sender.sendMessage(this.api.formatMsg(sender, "", String.valueOf(this.plugin.name) + " " + e.getMessage(), "es", this.api.getLang(sender)));
      return false;
    } 
  }
}
