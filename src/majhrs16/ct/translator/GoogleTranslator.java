package majhrs16.ct.translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

public class GoogleTranslator implements Translator {
	public boolean isSupport(String lang) {
    	try {
    		Languages.valueOf(lang.toUpperCase());
            return true;

    	} catch (IllegalArgumentException e) {
    		e.printStackTrace(); ////////////////// PENDIENTE
            return false;
    	}
    }

	public String translate(String text, String sourceLang, String targetLang) {
        if(!(isSupport(sourceLang) || isSupport(targetLang)))
            return text;

        String parsedJson = "NULL";
        String json       = "NULL";

        try {
        	text = text
   	        	.replace("!", "%21")
       			.replace(".", "%2e")
        		.replace("%", "%25")
        		.replace("ñ", "%F1")
    			.replace("Ñ", "%D1")
    			.replace("+", "%2B")
    			.replace("&", "%26")
    			.replace("\n", "%A")
    			.replace(" ", "%20");

	        json = httpHandler(
	        	"https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + text
	        ); // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]

	        parsedJson = new JSONArray(json).get(0).toString();       // [["hola","hola",null,null,5]]
	        parsedJson = new JSONArray(parsedJson).get(0).toString(); // ["hola","hola",null,null,5]
	        parsedJson = new JSONArray(parsedJson).get(0).toString(); // "hola"

	        return parsedJson
	        	.replace("%21", "!")
       			.replace("%2e", ".")
	        	.replace("%20", " ")
	        	.replace("%2B", "+")
	        	.replace("%26", "&")
	        	.replace("%A", "\n")
	        	.replace("%D1", "Ñ")
	        	.replace("%F1", "ñ")
	        	.replace("%25", "%");

        } catch (MalformedURLException e) {
        	e.printStackTrace();
        	return "[Err0] " + text;

        } catch (org.json.JSONException e) {
        	System.out.println("[Err 1 detectado]");
        	e.printStackTrace();
        	System.out.println("Debug, text: '" + text + "'");
        	System.out.println("Debug, Json: " + json);
        	System.out.println("Debug, parsedJson: " + parsedJson);
        	System.out.println("Debug, SL: " + sourceLang);
        	System.out.println("Debug, TL: " + targetLang);
        	return text;

        } catch (IOException e) {
        	e.printStackTrace();
        	return "[NO INTERNET] " + text;

        } catch (Exception e) {
        	e.printStackTrace();
        	return "[Err3] " + text;
        }
    }

    public String httpHandler(String url) throws MalformedURLException, IOException {
		StringBuilder resultado = new StringBuilder();
		HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
		conexion.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
		String linea;
		while ((linea = rd.readLine()) != null) {
			resultado.append(linea);
		}
		rd.close();
		return resultado.toString();
	}
}
