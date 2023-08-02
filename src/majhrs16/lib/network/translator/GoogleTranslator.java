package majhrs16.lib.network.translator;

import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONArray;

import java.net.URLEncoder;
import java.net.URL;

public class GoogleTranslator implements Translator {
	public enum Languages {
		OFF("OFF"),
		AUTO("Auto"), // Generalmente da fallos...
		DISABLED("Disabled"),

		AF("Afrikaans"),
		AM("አማርኛ"),
		AR("عربي"),
		AZ("Azərbaycan"),
		BE("беларускі"),
		BG("български"),
		BN("বাংলা"),
		BS("bosanski"),
		CA("català"),
		CEB("Cebuano"),
		CO("Corsu"),
		CS("čeština"),
		CY("Cymraeg"),
		DA("dansk"),
		DE("Deutsch"),
		EL("Ελληνικά"),
		EN("English"),
		EO("Esperanto"),
		ES("Español"),
		ET("eesti keel"),
		EU("euskara"),
		FA("فارسی"),
		FI("Suomalainen"),
		FR("Français"),
		FY("Frysk"),
		GA("Gaeilge"),
		GD("Gàidhlig na h-Alba"),
		GL("galego"),
		GU("ગુજરાતી"),
		HA("Hawaiian"),
		HI("हिंदी"),
		HM("Hmong"),
		HR("Hrvatski"),
		HT("Kreyòl ayisyen"),
		HU("Magyar"),
		HY("հայերեն"),
		ID("bahasa Indonesia"),
		IG("Igbo"),
		IS("íslenskur"),
		IT("Italiano"),
		IW("עִברִית"),
		JA("日本"),
		JW("basa jawa"),
		KA("ქართული"),
		KK("қазақ"),
		KM("ខ្មែរ"),
		KN("ಕನ್ನಡ"),
		KO("한국인"),
		KU("Kurdî (Kurmancî)"),
		KY("Кыргызча"),
		LA("Latinus"),
		LB("lëtzebuergesch"),
		LO("ພາສາລາວ"),
		LT("lietuvių"),
		LV("latviski"),
		MA("Punjabi"),
		MG("Malagasy"),
		MI("Maori"),
		MK("македонски"),
		ML("മലയാളം"),
		MN("Монгол"),
		MR("मराठी"),
		MS("Melayu"),
		MT("Malti"),
		MY("မြန်မာ (ဗမာ)၊"),
		NE("नेपाली"),
		NL("Nederlands"),
		NO("norsk"),
		NY("Chichewa"),
		PL("Polski"),
		PS("پښتو"),
		PT("Português"),
		RO("Română"),
		RU("Русский"),
		SD("سنڌي"),
		SI("සිංහල"),
		SK("slovenský"),
		SL("Slovenščina"),
		SM("Samoa"),
		SN("Shona"),
		SO("Soomaali"),
		SQ("shqiptare"),
		SR("Српски"),
		ST("Sesotho"),
		SU("basa Sunda"),
		SV("svenska"),
		SW("kiswahili"),
		TA("தமிழ்"),
		TE("తెలుగు"),
		TG("тоҷикӣ"),
		TH("แบบไทย"),
		TL("Filipino"),
		TR("Türkçe"),
		UK("українська"),
		UR("اردو"),
		UZ("o'zbek"),
		VI("Tiếng Việt"),
		XH("isiXhosa"),
		YI("יידיש"),
		YO("Yoruba"),
		ZH_CN("简体中文"),
		ZH_TW("中國傳統的"),
		ZU("Zulu");

		private String value;

		private Languages(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public char[] preEncoding = {
		'?',
		'.',
		'&',
		'	',
		'\n'
	};

	public boolean isSupport(String lang) {
			// isSupport("xd") -> false
			// isSupport("es") -> true
		try {
			if (lang == null)
				throw new IllegalArgumentException("[Err011 detectado]");

			Languages.valueOf(lang.toUpperCase());
			return true;

		} catch (IllegalArgumentException e) {
			System.out.println("[Err010], Debug lang: '" + lang + "'\n\t" + e.toString());
			return false;
		}
	}

	public String translate(String text, String sourceLang, String targetLang) {
			// translate("Hola", "es", "en") -> "Hello"
		if(!(isSupport(sourceLang) && isSupport(targetLang)))
			return text;

		String json       = "NULL";
		String URL        = "NULL";

		try {
			String textEncoded = text;
			for (Character c : preEncoding)
				textEncoded = textEncoded.replace(c.toString(), "x" + (Integer.toHexString(c).toUpperCase()));
			textEncoded = URLEncoder.encode(textEncoded, "UTF-8"); // Para encodear el resto de caracteres especiales.

			URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + textEncoded;

			json = httpHandler(URL);
			JSONArray parsed = new JSONArray(json); // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]
			parsed = parsed.getJSONArray(0);        // [["hola","hola",null,null,5]]
			parsed = parsed.getJSONArray(0);        // ["hola","hola",null,null,5]
			text = parsed.getString(0);             // "hola"

			for (Character c : preEncoding)
				text = text.replace("x" + (Integer.toHexString(c).toUpperCase()), c.toString());

			return text;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "[Err002] " + text;

		} catch (JSONException e) {
			System.out.println("[Err001]");
			System.out.println("  DEBUG, URL: '" + URL + "'");
			System.out.println("  DEBUG, Json: " + json);
			return text;

		} catch (IOException e) {
			return "[!] " + text;

		} catch (Exception e) {
			e.printStackTrace();
			return "[Err000] " + text;
		}
	}

	public String httpHandler(String url) throws IOException, MalformedURLException {
		StringBuilder resultado = new StringBuilder();
		URL urlObj = new URL(url);
		HttpURLConnection conexion = (HttpURLConnection) urlObj.openConnection();
		int timed_out = 3000;
		conexion.setConnectTimeout(timed_out);
		conexion.setReadTimeout(timed_out);
		conexion.setRequestMethod("GET");

		try (BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {
			String linea;
			while ((linea = rd.readLine()) != null) {
				resultado.append(linea);
			}
		}

		return resultado.toString();
	}
}