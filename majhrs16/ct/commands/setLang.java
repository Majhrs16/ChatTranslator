package majhrs16.ct.commands;

import majhrs16.ct.api;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class setLang implements CommandExecutor {
  private main plugin;
  
  private api api;
  
  public setLang(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    FileConfiguration players = config;
    this.api.getClass();
    api._Sender player = new api._Sender(this.api, sender);
    try {
      String lang, path;
      Player player2;
      switch (args.length) {
        case 1:
          if (player.isConsole()) {
            String str = this.api.GT.getCode(config.getString("default-lang"));
            this.api.checkSupportLang(str, "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
            this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + " &cNo se puede ejecutar este comando desde consola&f.", "es");
            return false;
          } 
          path = "players." + player.getUniqueId();
          lang = this.api.GT.getCode(args[0]);
          this.api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");
          players.set(path, lang);
          this.plugin.saveConfig();
          this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + " &7Su idioma ha sido &cestablecido &7a " + lang + "&f.", "es");
          return true;
        case 2:
          try {
            player2 = Bukkit.getServer().getPlayer(args[0]);
          } catch (NullPointerException e) {
            this.api.sendMessage(player, "", "&7El jugador &f'&b" + args[0] + "&f' &cno existe&f.", "es");
            player2 = null;
            return false;
          } 
          path = "players." + player2.getUniqueId();
          lang = args[1];
          this.api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");
          lang = this.api.GT.getCode(lang);
          if (!sender.hasPermission("ChatTranslator.lang.others")) {
            this.api.sendMessage(player, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          players.set(path, lang);
          this.plugin.saveConfig();
          this.api.broadcast(player, 
              String.valueOf(this.plugin.name) + " &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player2%", player2.getName()).replace("%lang%", lang), "", 
              "es");
          return true;
      } 
      this.api.checkSupportLang(this.api.GT.getCode(config.getString("default-lang")), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
      this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f[&6codigo&f]&f.", "es");
      return false;
    } catch (IllegalArgumentException e) {
      player.sendMessage(this.api.formatMsg(player, "", String.valueOf(this.plugin.name) + " " + e.getMessage(), "es", this.api.getLang(player)));
      return false;
    } 
  }
}
