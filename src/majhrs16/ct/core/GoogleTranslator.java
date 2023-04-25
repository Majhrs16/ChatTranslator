package majhrs16.ct.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;

public class GoogleTranslator {
   private enum Language {
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

        private Language(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
	
    public boolean isSupport(String language) {
        return Language.valueOf(language.toUpperCase()).getValue() != null;
    }

    public String getCode(String desiredLang) {
        if(isSupport(desiredLang)) {
            return desiredLang;
        }
        
        return null;
    }

    public String translateText(String text, String sourceLang, String targetLang) {
        if(!(isSupport(sourceLang) && isSupport(targetLang))){
            return text;
        }

        try {
	        String str = peticionHttpGet(
	        	"https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + (text.replace("+", "%2B").replace(" ", "+").replace("&", "%26").replace("%", "%25"))
	        ); // [[["hola","hola",null,null,5]],null,"es",null,null,null,null,[]]
	        
	        String list1 = new JSONArray(str).get(0).toString(); // [["hola","hola",null,null,5]]
	        String list2 = new JSONArray(list1).get(0).toString(); // ["hola","hola",null,null,5]
	        String list3 = new JSONArray(list2).get(0).toString(); // "hola"
	        return list3.replace("%2B", "+").replace("%26", "&").replace("%25", "%");

        } catch (Exception e) {
//					e.printStackTrace();
        	return "[NO INTERNET] " + text;
        }
    } 

    private String peticionHttpGet(String urlParaVisitar) throws Exception {
		StringBuilder resultado = new StringBuilder();
		URL url = new URL(urlParaVisitar);
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
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
