package majhrs16.ct.translator;

import java.io.IOException;
import java.net.MalformedURLException;

public interface Translator {
	public enum Languages {
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

	public String translate(String text, String sourceLang, String targetLang); // traduce msg con algun motor rapido y estable del idioma source al target.
	public boolean isSupport(String language); // Verifica si el idioma proporcionado es compatible con Lenguages.
//	public String getCode(String desiredLang); // Verifica si es compatible, en caso contrario busca al reves los idomas(Si es "Spanish" retorna "ES"), en cualquier caso deberia retornar un string.
	public String httpHandler(String url) throws MalformedURLException, IOException; // retorna el valor en bruto de la web.
	
}
