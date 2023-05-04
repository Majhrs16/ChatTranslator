package majhrs16.ct.commands;

import majhrs16.ct.GT;
import majhrs16.ct.api;
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
  
  private api api;
  
  public setLang(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    this.api.getClass();
    api._Sender player = new api._Sender(this.api, sender);
    GT g = new GT();
    if (args.length > 0) {
      if (args.length == 2) {
        Player player2;
        try {
          player2 = Bukkit.getServer().getPlayer(args[0]);
        } catch (NullPointerException e) {
          this.api.sendMessage(player, ChatColor.RED + " Ese jugador no existe" + ChatColor.WHITE + ".", "es");
          player2 = null;
          return false;
        } 
        String path = "players." + player2.getUniqueId();
        String str1 = args[1];
        if (!g.isSupport(str1)) {
          showCheckLang(player, "&7El idioma &f'&b%lang%&f' &7no esta soportado&f!.");
          return false;
        } 
        str1 = g.getCode(str1);
        if (!sender.hasPermission("ChatTranslator.lang.others")) {
          if (!g.isSupport(str1)) {
            showCheckLang(player, "&cUsted no tiene permisos para ejecutar este comando&f.");
            return false;
          } 
          return false;
        } 
        config.set(path, str1);
        this.plugin.saveConfig();
        this.api.broadcast(player, 
            String.valueOf(this.plugin.name) + " &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player%", player.getName()).replace("%player2%", player2.getName()).replace("%lang%", str1), 
            "es");
        return true;
      } 
      if (args.length == 1) {
        if (player.isConsole()) {
          String str2 = config.getString("default-lang");
          if (!g.isSupport(str2)) {
            showCheckLang(player, "&7El idioma por defecto &f'&b%lang%&f' &7no esta soportado&f!.");
            return false;
          } 
          str2 = g.getCode(str2);
          this.api.sendMessage(player, String.valueOf(this.plugin.name) + " &cNo se puede ejecutar este comando desde consola&f.", "es");
          return false;
        } 
        String path = "players." + player.getUniqueId();
        String str1 = g.getCode(args[0]);
        if (!g.isSupport(str1)) {
          showCheckLang(player, "&7El idioma por defecto &f'&b%lang%&f' &7no esta soportado&f!.");
          return false;
        } 
        config.set(path, str1);
        this.plugin.saveConfig();
        this.api.sendMessage(player, String.valueOf(this.plugin.name) + " Su idioma ha sido establecido a " + str1, "es");
        return true;
      } 
      String str = config.getString("default-lang");
      if (!g.isSupport(str)) {
        showCheckLang(player, "&7El idioma por defecto &f'&b%lang%&f' &7no esta soportado&f!.");
        return false;
      } 
      str = g.getCode(str);
      this.api.sendMessage(player, String.valueOf(this.plugin.name) + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f[&6codigo&f]&f.", "es");
      return false;
    } 
    String lang = config.getString("default-lang");
    if (!g.isSupport(lang)) {
      showCheckLang(player, "&7El idioma por defecto &f'&b%lang%&f' &7no esta soportado&f!.");
      return false;
    } 
    lang = g.getCode(lang);
    this.api.sendMessage(player, String.valueOf(this.plugin.name) + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f[&6codigo&f]&f.", "es");
    return false;
  }
  
  public void showCheckLang(api._Sender player, String msg) {
    FileConfiguration config = this.plugin.getConfig();
    if (player.isPlayer()) {
      this.api.getLang(player);
    } else if (player.isConsole()) {
      String lang = config.getString("default-lang");
      player.sendMessage(String.valueOf(this.plugin.name) + " " + ChatColor.translateAlternateColorCodes("&".charAt(0), msg.replace("%lang%", lang)));
    } 
  }
}
