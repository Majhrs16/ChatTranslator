package majhrs16.ct.translator;

import java.net.MalformedURLException;
import java.io.IOException;

public interface Translator {
	public enum Languages {}                                                         // Aqui se guardan todos los idiomas soportados por tu API de traductor.
	public boolean isSupport(String language);                                       // Verifica si el idioma proporcionado es compatible con Lenguages.
	public String translate(String text, String sourceLang, String targetLang);      // traduce text con algun motor, del idioma source al target.
	public String httpHandler(String url) throws MalformedURLException, IOException; // retorna el valor en bruto de la web.
}
