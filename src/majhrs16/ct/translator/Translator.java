package majhrs16.ct.translator;

import java.io.IOException;
import java.net.MalformedURLException;

public interface Translator {
	public enum Languages {} // Aqui se guardan todos los idiomas soportados por tu API de traductor.
	public String translate(String text, String sourceLang, String targetLang); // traduce msg con algun motor rapido y estable del idioma source al target.
	public boolean isSupport(String language); // Verifica si el idioma proporcionado es compatible con Lenguages.
	public String httpHandler(String url) throws MalformedURLException, IOException; // retorna el valor en bruto de la web.
}
