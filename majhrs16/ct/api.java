package majhrs16.ct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.JSONArray;

public class api {
  private main plugin;
  
  public GoogleTranslator GT;
  
  public api(main plugin) {
    this.plugin = plugin;
    this.GT = new GoogleTranslator();
  }
  
  public void broadcast(_Sender player, String msgFormat, String msg, String sourceLang) {
    checkSupportLang(sourceLang);
    if (msgFormat == null)
      msgFormat = "%msg%"; 
    if (msg == null)
      msg = ""; 
    List<Player> players = (List<Player>)Bukkit.getOnlinePlayers();
    msgFormat = msgFormat.replace("%player%", player.getName());
    msgFormat = msgFormat.replace("%lang%", getLang(player));
    sendMessage(new _Sender((CommandSender)Bukkit.getConsoleSender()), msgFormat, msg, sourceLang);
    for (int i = 0; i < players.size(); i++)
      sendMessage(player, new _Sender((CommandSender)players.get(i)), msgFormat, msg, sourceLang); 
  }
  
  public void broadcast(_Sender player, String msgFormat, String msg) {
    broadcast(player, msgFormat, msg, getLang(player));
  }
  
  public void checkSupportLang(String lang, String text) {
    if (this.GT.getCode(lang) == null)
      throw new IllegalArgumentException(text); 
  }
  
  public void checkSupportLang(String lang) {
    checkSupportLang(lang, "This lang '" + lang + "' no supported.");
  }
  
  public void sendMessage(_Sender playerFrom, _Sender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
    checkSupportLang(sourceLang, "This sourceLang '" + sourceLang + "' no supported.");
    checkSupportLang(targetLang, "This targetLang '" + targetLang + "' no supported.");
    if (msgFormat == null || msgFormat == "")
      msgFormat = "%msg%"; 
    if (msg == null || msg == "")
      msg = "&enull"; 
    if (IF("debug")) {
      System.out.println("Debug: PlayerFrom: '" + playerFrom.getName() + "', source: " + sourceLang);
      System.out.println("Debug: PlayerTo: '" + playerTo.getName() + "', target: " + targetLang);
      System.out.println("Debug: msgFormat: '" + msgFormat + "'");
      System.out.println("Debug: msg: '" + msg + "'");
    } 
    playerTo.sendMessage(formatMsg(playerFrom, playerTo, msgFormat, msg, sourceLang, targetLang));
  }
  
  public void sendMessage(_Sender playerFrom, String msgFormat, String msg, String sourceLang) {
    sendMessage(playerFrom, playerFrom, msgFormat, msg, sourceLang, getLang(playerFrom));
  }
  
  public void sendMessage(_Sender playerFrom, _Sender playerTo, String msgFormat, String msg, String sourceLang) {
    sendMessage(playerFrom, playerTo, msgFormat, msg, sourceLang, getLang(playerTo));
  }
  
  public void sendMessage(_Sender playerFrom, String msgFormat, String msg, String sourceLang, String targetLang) {
    sendMessage(playerFrom, playerFrom, msgFormat, msg, sourceLang, targetLang);
  }
  
  public String formatMsg(_Sender playerFrom, _Sender playerTo, String msgFormat, String msg, String sourceLang, String targetLang) {
    msgFormat = msgFormat.replace("%player%", playerFrom.getName());
    msgFormat = msgFormat.replace("%sourceLang%", getLang(playerFrom));
    if (this.plugin.checkPAPI().booleanValue() && 
      IF("auto-format-messages"))
      msgFormat = PlaceholderAPI.setPlaceholders(playerFrom.getPlayer(), msgFormat); 
    if (playerTo != null || playerFrom != playerTo)
      msgFormat = msgFormat.replace("$targetLang$", getLang(playerTo)); 
    if (this.plugin.checkPAPI().booleanValue() && (
      playerTo != null || playerFrom != playerTo))
      msgFormat = PlaceholderAPI.setPlaceholders(playerTo.getPlayer(), msgFormat.replace("$", "%")); 
    if (IF("auto-translate-chat")) {
      boolean i = false;
      if (msg.startsWith(this.plugin.name)) {
        msg = msg.substring(this.plugin.name.length(), msg.length());
        i = true;
      } 
      msg = this.GT.translateText(msg, sourceLang, targetLang);
      if (i)
        msg = String.valueOf(this.plugin.name) + " " + msg; 
    } 
    msgFormat = ChatColor.translateAlternateColorCodes("&".charAt(0), msgFormat);
    if (IF("chat-color-personalized"))
      msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg); 
    return msgFormat.replace("%msg%", msg);
  }
  
  public String formatMsg(_Sender playerFrom, String msgFormat, String msg, String sourceLang, String targetLang) {
    return formatMsg(playerFrom, null, msgFormat, msg, sourceLang, targetLang);
  }
  
  public String getLang(_Sender player) {
    String lang = null;
    FileConfiguration config = this.plugin.getConfig();
    FileConfiguration players = config;
    String defaultLang = config.getString("default-lang");
    String path = "players.";
    if (players.contains(String.valueOf(path) + player.getUniqueId())) {
      lang = players.getString(String.valueOf(path) + player.getUniqueId());
      if (this.plugin.checkPAPI().booleanValue()) {
        if (lang.equals("auto"))
          lang = PlaceholderAPI.setPlaceholders(player.getPlayer(), "%player_locale_short%"); 
      } else {
        lang = defaultLang;
      } 
    } else if (player.isPlayer()) {
      if (this.plugin.checkPAPI().booleanValue()) {
        lang = PlaceholderAPI.setPlaceholders(player.getPlayer(), "%player_locale_short%");
      } else {
        lang = defaultLang;
      } 
    } else if (player.isConsole()) {
      lang = defaultLang;
    } 
    if (!this.GT.isSupport(lang))
      if (this.GT.isSupport(defaultLang)) {
        sendMessage(player, "", String.valueOf(this.plugin.name) + " &7El idioma &f'&b" + lang + "&f' &cno esta soportado&f.", "es");
        lang = defaultLang;
      } else {
        sendMessage(player, "", String.valueOf(this.plugin.name) + "&7El idioma por defecto &f'&b" + defaultLang + "&f' &cno esta soportado&f!.", "es");
        return null;
      }  
    return lang;
  }
  
  public boolean IF(FileConfiguration cfg, String path) {
    return (cfg.contains(path) && cfg.getString(path).equals("true"));
  }
  
  public boolean IF(String path) {
    return IF(this.plugin.getConfig(), path);
  }
  
  public class _Sender {
    private CommandSender sender;
    
    public _Sender(CommandSender sender) {
      this.sender = sender;
    }
    
    public boolean isConsole() {
      return !isPlayer();
    }
    
    public boolean isPlayer() {
      return this.sender instanceof Player;
    }
    
    public void sendMessage(String s) {
      this.sender.sendMessage(s);
    }
    
    public Location getLocation() {
      if (isPlayer())
        return getPlayer().getLocation(); 
      return null;
    }
    
    public GameMode getGameMode() {
      if (isPlayer())
        return getPlayer().getGameMode(); 
      return null;
    }
    
    public String getName() {
      return this.sender.getName();
    }
    
    public boolean hasPermission(String s) {
      return this.sender.hasPermission(s);
    }
    
    public UUID getUniqueId() {
      if (isPlayer())
        return getPlayer().getUniqueId(); 
      return null;
    }
    
    public Player getPlayer() {
      if (isPlayer())
        return (Player)this.sender; 
      if (isConsole())
        return null; 
      return null;
    }
    
    public CommandSender getConsole() {
      if (isConsole())
        return this.sender; 
      if (isPlayer())
        return null; 
      return null;
    }
  }
  
  public class GoogleTranslator {
    private final Map<String, String> LANGUAGE_MAP = new HashMap<>();
    
    public GoogleTranslator() {
      this.LANGUAGE_MAP.put("auto", "Automatic");
      this.LANGUAGE_MAP.put("af", "Afrikaans");
      this.LANGUAGE_MAP.put("sq", "Albanian");
      this.LANGUAGE_MAP.put("am", "Amharic");
      this.LANGUAGE_MAP.put("ar", "Arabic");
      this.LANGUAGE_MAP.put("hy", "Armenian");
      this.LANGUAGE_MAP.put("az", "Azerbaijani");
      this.LANGUAGE_MAP.put("eu", "Basque");
      this.LANGUAGE_MAP.put("be", "Belarusian");
      this.LANGUAGE_MAP.put("bn", "Bengali");
      this.LANGUAGE_MAP.put("bs", "Bosnian");
      this.LANGUAGE_MAP.put("bg", "Bulgarian");
      this.LANGUAGE_MAP.put("ca", "Catalan");
      this.LANGUAGE_MAP.put("ceb", "Cebuano");
      this.LANGUAGE_MAP.put("ny", "Chichewa");
      this.LANGUAGE_MAP.put("zh_cn", "Chinese Simplified");
      this.LANGUAGE_MAP.put("zh_tw", "Chinese Traditional");
      this.LANGUAGE_MAP.put("co", "Corsican");
      this.LANGUAGE_MAP.put("hr", "Croatian");
      this.LANGUAGE_MAP.put("cs", "Czech");
      this.LANGUAGE_MAP.put("da", "Danish");
      this.LANGUAGE_MAP.put("nl", "Dutch");
      this.LANGUAGE_MAP.put("en", "English");
      this.LANGUAGE_MAP.put("eo", "Esperanto");
      this.LANGUAGE_MAP.put("et", "Estonian");
      this.LANGUAGE_MAP.put("tl", "Filipino");
      this.LANGUAGE_MAP.put("fi", "Finnish");
      this.LANGUAGE_MAP.put("fr", "French");
      this.LANGUAGE_MAP.put("fy", "Frisian");
      this.LANGUAGE_MAP.put("gl", "Galician");
      this.LANGUAGE_MAP.put("ka", "Georgian");
      this.LANGUAGE_MAP.put("de", "German");
      this.LANGUAGE_MAP.put("el", "Greek");
      this.LANGUAGE_MAP.put("gu", "Gujarati");
      this.LANGUAGE_MAP.put("ht", "Haitian Creole");
      this.LANGUAGE_MAP.put("ha", "Hausa");
      this.LANGUAGE_MAP.put("haw", "Hawaiian");
      this.LANGUAGE_MAP.put("iw", "Hebrew");
      this.LANGUAGE_MAP.put("hi", "Hindi");
      this.LANGUAGE_MAP.put("hmn", "Hmong");
      this.LANGUAGE_MAP.put("hu", "Hungarian");
      this.LANGUAGE_MAP.put("is", "Icelandic");
      this.LANGUAGE_MAP.put("ig", "Igbo");
      this.LANGUAGE_MAP.put("id", "Indonesian");
      this.LANGUAGE_MAP.put("ga", "Irish");
      this.LANGUAGE_MAP.put("it", "Italian");
      this.LANGUAGE_MAP.put("ja", "Japanese");
      this.LANGUAGE_MAP.put("jw", "Javanese");
      this.LANGUAGE_MAP.put("kn", "Kannada");
      this.LANGUAGE_MAP.put("kk", "Kazakh");
      this.LANGUAGE_MAP.put("km", "Khmer");
      this.LANGUAGE_MAP.put("ko", "Korean");
      this.LANGUAGE_MAP.put("ku", "Kurdish (Kurmanji)");
      this.LANGUAGE_MAP.put("ky", "Kyrgyz");
      this.LANGUAGE_MAP.put("lo", "Lao");
      this.LANGUAGE_MAP.put("la", "Latin");
      this.LANGUAGE_MAP.put("lv", "Latvian");
      this.LANGUAGE_MAP.put("lt", "Lithuanian");
      this.LANGUAGE_MAP.put("lb", "Luxembourgish");
      this.LANGUAGE_MAP.put("mk", "Macedonian");
      this.LANGUAGE_MAP.put("mg", "Malagasy");
      this.LANGUAGE_MAP.put("ms", "Malay");
      this.LANGUAGE_MAP.put("ml", "Malayalam");
      this.LANGUAGE_MAP.put("mt", "Maltese");
      this.LANGUAGE_MAP.put("mi", "Maori");
      this.LANGUAGE_MAP.put("mr", "Marathi");
      this.LANGUAGE_MAP.put("mn", "Mongolian");
      this.LANGUAGE_MAP.put("my", "Myanmar (Burmese)");
      this.LANGUAGE_MAP.put("ne", "Nepali");
      this.LANGUAGE_MAP.put("no", "Norwegian");
      this.LANGUAGE_MAP.put("ps", "Pashto");
      this.LANGUAGE_MAP.put("fa", "Persian");
      this.LANGUAGE_MAP.put("pl", "Polish");
      this.LANGUAGE_MAP.put("pt", "Portuguese");
      this.LANGUAGE_MAP.put("ma", "Punjabi");
      this.LANGUAGE_MAP.put("ro", "Romanian");
      this.LANGUAGE_MAP.put("ru", "Russian");
      this.LANGUAGE_MAP.put("sm", "Samoan");
      this.LANGUAGE_MAP.put("gd", "Scots Gaelic");
      this.LANGUAGE_MAP.put("sr", "Serbian");
      this.LANGUAGE_MAP.put("st", "Sesotho");
      this.LANGUAGE_MAP.put("sn", "Shona");
      this.LANGUAGE_MAP.put("sd", "Sindhi");
      this.LANGUAGE_MAP.put("si", "Sinhala");
      this.LANGUAGE_MAP.put("sk", "Slovak");
      this.LANGUAGE_MAP.put("sl", "Slovenian");
      this.LANGUAGE_MAP.put("so", "Somali");
      this.LANGUAGE_MAP.put("es", "Spanish");
      this.LANGUAGE_MAP.put("su", "Sundanese");
      this.LANGUAGE_MAP.put("sw", "Swahili");
      this.LANGUAGE_MAP.put("sv", "Swedish");
      this.LANGUAGE_MAP.put("tg", "Tajik");
      this.LANGUAGE_MAP.put("ta", "Tamil");
      this.LANGUAGE_MAP.put("te", "Telugu");
      this.LANGUAGE_MAP.put("th", "Thai");
      this.LANGUAGE_MAP.put("tr", "Turkish");
      this.LANGUAGE_MAP.put("uk", "Ukrainian");
      this.LANGUAGE_MAP.put("ur", "Urdu");
      this.LANGUAGE_MAP.put("uz", "Uzbek");
      this.LANGUAGE_MAP.put("vi", "Vietnamese");
      this.LANGUAGE_MAP.put("cy", "Welsh");
      this.LANGUAGE_MAP.put("xh", "Xhosa");
      this.LANGUAGE_MAP.put("yi", "Yiddish");
      this.LANGUAGE_MAP.put("yo", "Yoruba");
      this.LANGUAGE_MAP.put("zu", "Zulu");
    }
    
    private boolean isSupport(String language) {
      return (this.LANGUAGE_MAP.get(language) != null);
    }
    
    public String getCode(String desiredLang) {
      if (isSupport(desiredLang))
        return desiredLang; 
      for (Map.Entry<String, String> enter : this.LANGUAGE_MAP.entrySet()) {
        if (((String)enter.getValue()).equalsIgnoreCase(desiredLang))
          return enter.getKey(); 
      } 
      return null;
    }
    
    public String translateText(String text, String sourceLang, String targetLang) {
      if (!isSupport(sourceLang) || !isSupport(targetLang))
        return text; 
      try {
        String str = peticionHttpGet(
            "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + text.replace("+", "%2B").replace(" ", "+").replace("&", "%26").replace("%", "%25"));
        String list1 = (new JSONArray(str)).get(0).toString();
        String list2 = (new JSONArray(list1)).get(0).toString();
        String list3 = (new JSONArray(list2)).get(0).toString();
        return list3.replace("%2B", "+").replace("%26", "&").replace("%25", "%");
      } catch (Exception e) {
        return "[NO INTERNET] " + text;
      } 
    }
    
    private String peticionHttpGet(String urlParaVisitar) throws Exception {
      StringBuilder resultado = new StringBuilder();
      URL url = new URL(urlParaVisitar);
      HttpURLConnection conexion = (HttpURLConnection)url.openConnection();
      conexion.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
      String linea;
      while ((linea = rd.readLine()) != null)
        resultado.append(linea); 
      rd.close();
      return resultado.toString();
    }
  }
}
