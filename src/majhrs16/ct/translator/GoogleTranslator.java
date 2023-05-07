package majhrs16.ct.translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

public class GoogleTranslator implements Translator {
	public int[] listConvertion = {
			32,  // ' '
			33,  // !
			34,  // "
			35,  // #
			36,  // $
			37,  // %
			38,  // &
			39,  // '
			40,  // (
			41,  // )
			42,  // *
			43,  // +
			44,  // ,
			45,  // -
			46,  // .
			47,  // /
			58,  // :
			59,  // ;
			60,  // <
			61,  // =
			62,  // >
			63,  // ?
			64,  // @
			91,  // [
			92,  // \
			93,  // ]
			94,  // ^
			95,  // _
			96,  // `
			123, // {
			124, // |
			125, // }
			126  // ~
		};

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

		String formatText = text;
		String parsedJson = "NULL";
		String json       = "NULL";
		String URL        = "NULL";

		try {
			for(int i : listConvertion) {
//				System.out.println("DEBUG: " + formatText);
				formatText = formatText.replace(Character.toString((char) i), "%" + Integer.toHexString(i));
			}

			URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + formatText;

			json = httpHandler(URL);                                  // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]
			parsedJson = new JSONArray(json).get(0).toString();       // [["hola","hola",null,null,5]]
			parsedJson = new JSONArray(parsedJson).get(0).toString(); // ["hola","hola",null,null,5]
			parsedJson = new JSONArray(parsedJson).get(0).toString(); // "hola"

			for(int i : listConvertion) {
//				System.out.println(parsedJson);
				parsedJson = parsedJson.replace("%" + Integer.toHexString(i), Character.toString((char) i));
			}

			return parsedJson;

		} catch (MalformedURLException e) {
//			e.printStackTrace();
			return "[Err0] " + text;

		} catch (org.json.JSONException e) {
			System.out.println("[Err 1 detectado]");
//			e.printStackTrace();
			
			System.out.println("DEBUG, URL: '" + URL + "'");
			System.out.println("DEBUG, Json: " + json);
			System.out.println("DEBUG, parsedJson: " + parsedJson);
			return text;

		} catch (IOException e) {
//			e.printStackTrace();
			return "[NO INTERNET] " + text;

		} catch (Exception e) {
//			e.printStackTrace();
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
