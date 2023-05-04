package majhrs16.ct.events;

import java.util.HashMap;
import majhrs16.ct.api;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class chat implements Listener {
  private main plugin;
  
  private api api;
  
  private HashMap<Player, Boolean> map;
  
  public chat(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
    this.map = new HashMap<>();
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onMessage(final AsyncPlayerChatEvent event) throws Exception {
    FileConfiguration config = this.plugin.getConfig();
    String msgFormat = config.getString("message-format");
    this.api.getClass();
    api._Sender player = new api._Sender(this.api, (CommandSender)event.getPlayer());
    String msg = event.getMessage();
    if (this.map.get(player.getPlayer()) == null) {
      if (this.api.IF("debug")) {
        System.out.println("Debug: PlayerFrom: '" + player.getName() + "'");
        System.out.println("Debug: msgFormat: '" + msgFormat + "'");
        System.out.println("Debug: msg: '" + msg + "'");
      } 
      this.map.put(player.getPlayer(), Boolean.valueOf(true));
    } 
    if (((Boolean)this.map.get(player.getPlayer())).booleanValue()) {
      event.setCancelled(true);
      this.map.put(player.getPlayer(), Boolean.valueOf(false));
      Bukkit.getScheduler().runTask((Plugin)this.plugin, new Runnable() {
            public void run() {
              try {
                Bukkit.getPluginManager().callEvent((Event)new PlayerChatEvent(event.getPlayer(), event.getMessage(), event.getFormat(), event.getRecipients()));
              } catch (Exception e) {
                chat.this.api.getClass();
                api._Sender console = new api._Sender(chat.this.api, (CommandSender)Bukkit.getConsoleSender());
                chat.this.api.sendMessage(console, "", "&cClase PlayerChatEvent no disponible&f, &aUsando AsyncPlayerChatEvent&f(&ePosible error&f).", "es");
                Bukkit.getPluginManager().callEvent((Event)new AsyncPlayerChatEvent(false, event.getPlayer(), event.getMessage(), event.getRecipients()));
              } 
            }
          });
      this.map.put(player.getPlayer(), Boolean.valueOf(true));
      this.api.broadcast(player, msgFormat, msg);
    } 
  }
}
