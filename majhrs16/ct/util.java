package majhrs16.ct;

import java.util.ArrayList;
import majhrs16.ct.translator.GoogleTranslator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class util {
  public static ArrayList<AsyncPlayerChatEvent> chat = new ArrayList<>();
  
  public static Boolean checkPAPI() {
    Boolean havePAPI = null;
    try {
      Class.forName("me.clip.placeholderapi.PlaceholderAPI");
      havePAPI = Boolean.valueOf(true);
    } catch (ClassNotFoundException e) {
      havePAPI = Boolean.valueOf(false);
    } 
    return havePAPI;
  }
  
  public static boolean IF(FileConfiguration cfg, String path) {
    return (cfg.contains(path) && cfg.getString(path).equals("true"));
  }
  
  public static AsyncPlayerChatEvent popChat(int i) {
    AsyncPlayerChatEvent event;
    try {
      event = chat.get(i);
      chat.remove(i);
    } catch (IndexOutOfBoundsException e) {
      event = null;
    } 
    return event;
  }
  
  public static String assertLang(String lang, String text) {
    if (!(new GoogleTranslator()).isSupport(lang))
      throw new IllegalArgumentException(text); 
    return text;
  }
  
  public static String assertLang(String lang) {
    return assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
  }
}
