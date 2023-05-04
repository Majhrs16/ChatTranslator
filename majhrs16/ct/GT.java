package majhrs16.ct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GT {
  private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();
  
  public GT() {
    LANGUAGE_MAP.put("auto", "Automatic");
    LANGUAGE_MAP.put("af", "Afrikaans");
    LANGUAGE_MAP.put("sq", "Albanian");
    LANGUAGE_MAP.put("am", "Amharic");
    LANGUAGE_MAP.put("ar", "Arabic");
    LANGUAGE_MAP.put("hy", "Armenian");
    LANGUAGE_MAP.put("az", "Azerbaijani");
    LANGUAGE_MAP.put("eu", "Basque");
    LANGUAGE_MAP.put("be", "Belarusian");
    LANGUAGE_MAP.put("bn", "Bengali");
    LANGUAGE_MAP.put("bs", "Bosnian");
    LANGUAGE_MAP.put("bg", "Bulgarian");
    LANGUAGE_MAP.put("ca", "Catalan");
    LANGUAGE_MAP.put("ceb", "Cebuano");
    LANGUAGE_MAP.put("ny", "Chichewa");
    LANGUAGE_MAP.put("zh_cn", "Chinese Simplified");
    LANGUAGE_MAP.put("zh_tw", "Chinese Traditional");
    LANGUAGE_MAP.put("co", "Corsican");
    LANGUAGE_MAP.put("hr", "Croatian");
    LANGUAGE_MAP.put("cs", "Czech");
    LANGUAGE_MAP.put("da", "Danish");
    LANGUAGE_MAP.put("nl", "Dutch");
    LANGUAGE_MAP.put("en", "English");
    LANGUAGE_MAP.put("eo", "Esperanto");
    LANGUAGE_MAP.put("et", "Estonian");
    LANGUAGE_MAP.put("tl", "Filipino");
    LANGUAGE_MAP.put("fi", "Finnish");
    LANGUAGE_MAP.put("fr", "French");
    LANGUAGE_MAP.put("fy", "Frisian");
    LANGUAGE_MAP.put("gl", "Galician");
    LANGUAGE_MAP.put("ka", "Georgian");
    LANGUAGE_MAP.put("de", "German");
    LANGUAGE_MAP.put("el", "Greek");
    LANGUAGE_MAP.put("gu", "Gujarati");
    LANGUAGE_MAP.put("ht", "Haitian Creole");
    LANGUAGE_MAP.put("ha", "Hausa");
    LANGUAGE_MAP.put("haw", "Hawaiian");
    LANGUAGE_MAP.put("iw", "Hebrew");
    LANGUAGE_MAP.put("hi", "Hindi");
    LANGUAGE_MAP.put("hmn", "Hmong");
    LANGUAGE_MAP.put("hu", "Hungarian");
    LANGUAGE_MAP.put("is", "Icelandic");
    LANGUAGE_MAP.put("ig", "Igbo");
    LANGUAGE_MAP.put("id", "Indonesian");
    LANGUAGE_MAP.put("ga", "Irish");
    LANGUAGE_MAP.put("it", "Italian");
    LANGUAGE_MAP.put("ja", "Japanese");
    LANGUAGE_MAP.put("jw", "Javanese");
    LANGUAGE_MAP.put("kn", "Kannada");
    LANGUAGE_MAP.put("kk", "Kazakh");
    LANGUAGE_MAP.put("km", "Khmer");
    LANGUAGE_MAP.put("ko", "Korean");
    LANGUAGE_MAP.put("ku", "Kurdish (Kurmanji)");
    LANGUAGE_MAP.put("ky", "Kyrgyz");
    LANGUAGE_MAP.put("lo", "Lao");
    LANGUAGE_MAP.put("la", "Latin");
    LANGUAGE_MAP.put("lv", "Latvian");
    LANGUAGE_MAP.put("lt", "Lithuanian");
    LANGUAGE_MAP.put("lb", "Luxembourgish");
    LANGUAGE_MAP.put("mk", "Macedonian");
    LANGUAGE_MAP.put("mg", "Malagasy");
    LANGUAGE_MAP.put("ms", "Malay");
    LANGUAGE_MAP.put("ml", "Malayalam");
    LANGUAGE_MAP.put("mt", "Maltese");
    LANGUAGE_MAP.put("mi", "Maori");
    LANGUAGE_MAP.put("mr", "Marathi");
    LANGUAGE_MAP.put("mn", "Mongolian");
    LANGUAGE_MAP.put("my", "Myanmar (Burmese)");
    LANGUAGE_MAP.put("ne", "Nepali");
    LANGUAGE_MAP.put("no", "Norwegian");
    LANGUAGE_MAP.put("ps", "Pashto");
    LANGUAGE_MAP.put("fa", "Persian");
    LANGUAGE_MAP.put("pl", "Polish");
    LANGUAGE_MAP.put("pt", "Portuguese");
    LANGUAGE_MAP.put("ma", "Punjabi");
    LANGUAGE_MAP.put("ro", "Romanian");
    LANGUAGE_MAP.put("ru", "Russian");
    LANGUAGE_MAP.put("sm", "Samoan");
    LANGUAGE_MAP.put("gd", "Scots Gaelic");
    LANGUAGE_MAP.put("sr", "Serbian");
    LANGUAGE_MAP.put("st", "Sesotho");
    LANGUAGE_MAP.put("sn", "Shona");
    LANGUAGE_MAP.put("sd", "Sindhi");
    LANGUAGE_MAP.put("si", "Sinhala");
    LANGUAGE_MAP.put("sk", "Slovak");
    LANGUAGE_MAP.put("sl", "Slovenian");
    LANGUAGE_MAP.put("so", "Somali");
    LANGUAGE_MAP.put("es", "Spanish");
    LANGUAGE_MAP.put("su", "Sundanese");
    LANGUAGE_MAP.put("sw", "Swahili");
    LANGUAGE_MAP.put("sv", "Swedish");
    LANGUAGE_MAP.put("tg", "Tajik");
    LANGUAGE_MAP.put("ta", "Tamil");
    LANGUAGE_MAP.put("te", "Telugu");
    LANGUAGE_MAP.put("th", "Thai");
    LANGUAGE_MAP.put("tr", "Turkish");
    LANGUAGE_MAP.put("uk", "Ukrainian");
    LANGUAGE_MAP.put("ur", "Urdu");
    LANGUAGE_MAP.put("uz", "Uzbek");
    LANGUAGE_MAP.put("vi", "Vietnamese");
    LANGUAGE_MAP.put("cy", "Welsh");
    LANGUAGE_MAP.put("xh", "Xhosa");
    LANGUAGE_MAP.put("yi", "Yiddish");
    LANGUAGE_MAP.put("yo", "Yoruba");
    LANGUAGE_MAP.put("zu", "Zulu");
  }
  
  public boolean isSupport(String language) {
    return (LANGUAGE_MAP.get(language) != null);
  }
  
  public String getCode(String desiredLang) {
    if (isSupport(desiredLang))
      return desiredLang; 
    String tmp = desiredLang.toLowerCase();
    for (Map.Entry<String, String> enter : LANGUAGE_MAP.entrySet()) {
      if (((String)enter.getValue()).equals(tmp))
        return enter.getKey(); 
    } 
    return null;
  }
  
  public String translateText(String text, String sourceLang, String targetLang) {
    if (!isSupport(sourceLang) || !isSupport(targetLang))
      return text; 
    try {
      String str = peticionHttpGet(
          "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + text.replace(" ", "+"));
      return str.substring(4, str.indexOf("\","));
    } catch (Exception e) {
      e.printStackTrace();
      return text;
    } 
  }
  
  public static String peticionHttpGet(String urlParaVisitar) throws Exception {
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
