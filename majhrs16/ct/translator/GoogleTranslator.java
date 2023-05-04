package majhrs16.ct.translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;

public class GoogleTranslator implements Translator {
  public boolean isSupport(String lang) {
    try {
      Translator.Languages.valueOf(lang.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    } 
  }
  
  public String translate(String text, String sourceLang, String targetLang) {
    if (!isSupport(sourceLang) && !isSupport(targetLang))
      return text; 
    try {
      text = text
        .replace("ñ", "%F1")
        .replace("Ñ", "%D1")
        .replace("Ñ", "%d1")
        .replace("+", "%2B")
        .replace("&", "%26")
        .replace("%", "%25")
        .replace("\n", "%A")
        .replace(" ", "+");
      String str = httpHandler(
          "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + text);
      String list = (new JSONArray(str)).get(0).toString();
      String sublist = (new JSONArray(list)).get(0).toString();
      str = (new JSONArray(sublist)).get(0).toString();
      str = str.replace("+", " ")
        .replace("%2B", "+")
        .replace("%26", "&")
        .replace("%25", "%")
        .replace("%A", "\n")
        .replace("%D1", "Ñ")
        .replace("%F1", "ñ");
      return str;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return "[Err0] " + text;
    } catch (JSONException e) {
      e.printStackTrace();
      return "[Err1] " + text;
    } catch (IOException e) {
      return "[NO INTERNET] " + text;
    } catch (Exception e) {
      e.printStackTrace();
      return "[Err3] " + text;
    } 
  }
  
  public String httpHandler(String url) throws MalformedURLException, IOException {
    StringBuilder resultado = new StringBuilder();
    HttpURLConnection conexion = (HttpURLConnection)(new URL(url)).openConnection();
    conexion.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
    String linea;
    while ((linea = rd.readLine()) != null)
      resultado.append(linea); 
    rd.close();
    return resultado.toString();
  }
}
