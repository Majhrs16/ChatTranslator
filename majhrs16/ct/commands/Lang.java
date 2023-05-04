package majhrs16.ct.commands;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.translator.API;
import majhrs16.ct.util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Lang implements CommandExecutor {
  private ChatTranslator plugin;
  
  private API API;
  
  public Lang(ChatTranslator plugin) {
    this.plugin = plugin;
    this.API = new API(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    FileConfiguration players = this.plugin.getPlayers();
    String path = "";
    try {
      String lang;
      Player player2;
      switch (args.length) {
        case 1:
          lang = util.assertLang(args[0], "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");
          if (sender instanceof Player) {
            players.set(String.valueOf(path) + ((Player)sender).getUniqueId(), lang);
            this.plugin.savePlayers();
          } else {
            path = "default-lang";
            config.set(path, lang);
            this.plugin.saveConfig();
          } 
          this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &7Su idioma ha sido &aestablecido &7a &b" + lang + "&f.", "es");
          return true;
        case 2:
          try {
            player2 = Bukkit.getServer().getPlayer(args[0]);
          } catch (NullPointerException e) {
            this.API.sendMessage(null, sender, "", "&7El jugador &f'&b" + args[0] + "&f' &cno &7esta &cdisponible&f.", "es");
            player2 = null;
            return false;
          } 
          lang = util.assertLang(args[1], "&7El idioma &f'&b%lang%&f' &cno &7esta &csoportado&f!.");
          if (!sender.hasPermission("ChatTranslator.admin")) {
            this.API.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          players.set(String.valueOf(path) + player2.getUniqueId(), lang);
          this.plugin.savePlayers();
          this.API.broadcast(sender, 
              String.valueOf(this.plugin.name) + " &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player%", sender.getName()).replace("%player2%", player2.getName()).replace("%lang%", lang), "", 
              "es");
          return true;
      } 
      util.assertLang(config.getString("default-lang"), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
      this.API.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f<&6codigo&f>&f.", "es");
      return false;
    } catch (IllegalArgumentException e) {
      sender.sendMessage(this.API.formatMsg(null, sender, "", String.valueOf(this.plugin.name) + " " + e.getMessage(), "es", this.API.getLang(sender)));
      return false;
    } 
  }
}
