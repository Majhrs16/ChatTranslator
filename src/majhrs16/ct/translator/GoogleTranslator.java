package majhrs16.ct.translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

public class GoogleTranslator implements Translator {
	public enum Languages {
		OFF("OFF"),
		disabled("disabled"),

		AUTO("Automatic"),
		AF("Afrikaans"),
		SQ("Albanian"),
		AM("Amharic"),
		AR("Arabic"),
		HY("Armenian"),
		AZ("Azerbaijani"),
		EU("Basque"),
		BE("Belarusian"),
		BN("Bengali"),
		BS("Bosnian"),
		BG("Bulgarian"),
		CA("Catalan"),
		CEB("Cebuano"),
		NY("Chichewa"),
		ZH_CN("Chinese Simplified"),
		ZH_TW("Chinese Traditional"),
		CO("Corsican"),
		HR("Croatian"),
		CS("Czech"),
		DA("Danish"),
		NL("Dutch"),
		EN("English"),
		EO("Esperanto"),
		ET("Estonian"),
		TL("Filipino"),
		FI("Finnish"),
		FR("French"),
		FY("Frisian"),
		GL("Galician"),
		KA("Georgian"),
		DE("German"),
		EL("Greek"),
		GU("Gujarati"),
		HT("Haitian Creole"),
		HA("Hausa"),
		HAW("Hawaiian"),
		IW("Hebrew"),
		HI("Hindi"),
		HMN("Hmong"),
		HU("Hungarian"),
		IS("Icelandic"),
		IG("Igbo"),
		ID("Indonesian"),
		GA("Irish"),
		IT("Italian"),
		JA("Japanese"),
		JW("Javanese"),
		KN("Kannada"),
		KK("Kazakh"),
		KM("Khmer"),
		KO("Korean"),
		KU("Kurdish (Kurmanji)"),
		KY("Kyrgyz"),
		LO("Lao"),
		LA("Latin"),
		LV("Latvian"),
		LT("Lithuanian"),
		LB("Luxembourgish"),
		MK("Macedonian"),
		MG("Malagasy"),
		MS("Malay"),
		ML("Malayalam"),
		MT("Maltese"),
		MI("Maori"),
		MR("Marathi"),
		MN("Mongolian"),
		MY("Myanmar (Burmese)"),
		NE("Nepali"),
		NO("Norwegian"),
		PS("Pashto"),
		FA("Persian"),
		PL("Polish"),
		PT("Portuguese"),
		MA("Punjabi"),
		RO("Romanian"),
		RU("Russian"),
		SM("Samoan"),
		GD("Scots Gaelic"),
		SR("Serbian"),
		ST("Sesotho"),
		SN("Shona"),
		SD("Sindhi"),
		SI("Sinhala"),
		SK("Slovak"),
		SL("Slovenian"),
		SO("Somali"),
		ES("Spanish"),
		SU("Sundanese"),
		SW("Swahili"),
		SV("Swedish"),
		TG("Tajik"),
		TA("Tamil"),
		TE("Telugu"),
		TH("Thai"),
		TR("Turkish"),
		UK("Ukrainian"),
		UR("Urdu"),
		UZ("Uzbek"),
		VI("Vietnamese"),
		CY("Welsh"),
		XH("Xhosa"),
		YI("Yiddish"),
		YO("Yoruba"),
		ZU("Zulu");

		private String value;

		private Languages(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public int[] listConvertion = {
		9,   // \t
		10,  // \n
		13,  // \r
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
		126, // ~
		161, // ¡
		162, // ¢
		163, // £
		164, // ¤
		165, // ¥
		166, // ¦
		167, // §
		168, // ¨
		169, // ©
		170, // ª
		171, // «
		172, // ¬
		173, // ­
		174, // ®
		175, // ¯
		176, // °
		177, // ±
		178, // ²
		179, // ³
		180, // ´
		181, // µ
		182, // ¶
		183, // ·
		184, // ¸
		185, // ¹
		186, // º
		187, // »
		188, // ¼
		189, // ½
		190, // ¾
		191, // ¿
		192, // À
		193, // Á
		194, // Â
		195, // Ã
		196, // Ä
		197, // Å
		198, // Æ
		199, // Ç
		200, // È
		201, // É
		202, // Ê
		203, // Ë
		204, // Ì
		205, // Í
		206, // Î
		207, // Ï
		208, // Ð
		209, // Ñ
		210, // Ò
		211, // Ó
		212, // Ô
		213, // Õ
		214, // Ö
		215, // ×
		216, // Ø
		217, // Ù
		218, // Ú
		219, // Û
		220, // Ü
		221, // Ý
		222, // Þ
		223, // ß
		224, // à
		225, // á
		226, // â
		227, // ã
		228, // ä
		229, // å
		230, // æ
		231, // ç
		232, // è
		233, // é
		234, // ê
		235, // ë
		236, // ì
		237, // í
		238, // î
		239, // ï
		240, // ð
		241, // ñ
		242, // ò
		243, // ó
		244, // ô
		245, // õ
		246, // ö
		247, // ÷
		248, // ø
		249, // ù
		250, // ú
		251, // û
		252, // ü
		253, // ý
		254, // þ
		255  // ÿ
	};

	public boolean isSupport(String lang) {
			// isSupport("xd") -> false
			// isSupport("es") -> true
		try {
			if (lang == null)
				throw new IllegalArgumentException("[Err 11 detectado]");

			Languages.valueOf(lang.toUpperCase());
			return true;

		} catch (IllegalArgumentException e) {
			System.out.println("[Err 10 detectado], Debug lang: '" + lang + "'");
			e.printStackTrace();
			return false;
		}
	}

	public String translate(String text, String sourceLang, String targetLang) {
			// translate("Hola", "es", "en") -> "hello"
		if(!(isSupport(sourceLang) && isSupport(targetLang)))
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

			formatText = formatText.replace(" ", "+");

			URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + formatText;

			json = httpHandler(URL);                                  // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]
			parsedJson = new JSONArray(json).get(0).toString();       // [["hola","hola",null,null,5]]
			parsedJson = new JSONArray(parsedJson).get(0).toString(); // ["hola","hola",null,null,5]
			parsedJson = new JSONArray(parsedJson).get(0).toString(); // "hola"

			for(int i : listConvertion) {
				parsedJson = parsedJson.replace("%" + Integer.toHexString(i), Character.toString((char) i));
			}

			return parsedJson;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "[Err00 detectado] " + text;

		} catch (org.json.JSONException e) {
			System.out.println("[Err 01 detectado]");
			System.out.println("  DEBUG, URL: '" + URL + "'");
			System.out.println("  DEBUG, Json: " + json);
			System.out.println("  DEBUG, parsedJson: " + parsedJson);
			return text;

		} catch (IOException e) {
			return "[NO INTERNET] " + text;

		} catch (Exception e) {
			e.printStackTrace();
			return "[Err 03 detectado] " + text;
		}
	}

	public String httpHandler(String url) throws MalformedURLException, IOException {
		StringBuilder resultado = new StringBuilder();
		HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
		int timed_out = 3000;
		conexion.setConnectTimeout(timed_out);
		conexion.setReadTimeout(timed_out);
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
