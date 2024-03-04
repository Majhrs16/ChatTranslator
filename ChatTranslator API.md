ChatTranslatorAPI es una herramienta muy poderosa tambien para desarrolladores, permite utilizar la funcionalidad del plugin en otros plugins de Spigot/Paper. Con esta API, podrás traducir mensajes de chat en tiempo real y personalizar el formato de los mensajes para adaptarlos a las necesidades de tu servidor.

---

## Uso de la API

Para utilizar la API de ChatTranslator, primero debes asegurarte de tener el complemento ChatTranslator instalado en tu servidor. Luego, puedes acceder a la API utilizando el siguiente código tipico:

```java
import majhrs16.cht.translator.ChatTranslatorAPI;
import org.bukkit.Bukkit;

class Ejemplo {
    private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

    public Ejemplo() {
      String lang_term = API.getLang(Bukkit.getConsoleSender());
    }
}
```


### Obtener el idioma de un jugador:

```java
String lang = API.getLang(Object sender);
```

Esta función te permite obtener el idioma del jugador(offline u online) o consola pasado como argumento. Si el jugador no tiene un idioma configurado, se intentara usar el idioma de su Minecraft. Para la consola se usara el uuid guardado en config.yml para intentar obtener el idioma del almacenamiento. Pero finalmente en los 2 casos sino se encuentra, se usara el idioma predeterminado definido en la configuración del plugin(`config.default-lang`).

### Enviar un mensaje traducido a un jugador:

1.0. `sendMessage(new Message(...))`

Esta funcion te permite enviar un mensaje personalizado con ciertos valores por defecto al remitente y destinario o solamente al remitente(o mejor dicho, tu destinario).

```java
Message from = new Message(); // Crear un mensaje personalizado.
    from.setSender(player_or_console);
    from.getMessages().setTexts("mensaje"); // Quiero aclarar que cada objeto Message puede traducir y enviar mensajes personalizados independientemente de si este tiene o no otro Message como destinario.
    from.setLangSource("es"); // Traducir el mensaje,
    from.setLangTarget("en"); // Del español al ingles.
API.sendMessage(from);
```

### Enviar un mensaje traducido a varios jugadores:

Esta función te permite enviar un mensaje personalizado al remitente y a los destinarios al mismo tiempo. Existen dos variantes del método `broadcast` con diferentes usos:

1.0. `Broadcast de bajo nivel.`

   Esta variante permite enviar un broadcast manualmente a todos los remitentes y/o destinarios dentro del parametro messages. Este ultimo es una lista de remitentes(Pueden ser todos iguales), cada uno con su propio destinario. Ejemplo de uso:

```java
List<Message> froms = new ArrayList<>();
String mensaje      = "Hola";

// Crear el mensaje para el remitente.
Message remitente = new Message();
  remitente.setSender(from_player);
  remitente.getMessages().setTexts(mensaje);

  // Al ser el mismo idioma origen y destino, no se traducira.
  remitente.setLangSource("es");
  remitente.setLangTarget("es");

// Crear el mensaje personalizado para el destinario 1
Message destinario1 = new Message();
  destinario1.setSender(to_player);
  destinario1.getMessages().setFormats("$ct_messages$");
  destinario1.getMessages().setTexts(mensaje);

  // Si traducir el mensaje para el destinario 1.
  destinario1.setLangSource("es");
  destinario1.setLangTarget("en");

  remitente.setTo(destinario1);
froms.add(remitente.clone());

// Es importante cancelar el from una vez usado para evitar duplicar el mensaje por cada destinario.
remitente1.setCancelledThis(true);

Message destinario2 = new Message(); // Crear el mensaje personalizado para el destinario 2
  ...

  remitente1.setTo(destinario2);
froms.add(remitente1.clone());

// Enviar los mensajes personalizados a los remitentes y destinatarios usando por defecto ChatLimiter.
// Si se especifica un Consumer<Message> se puede controlar cada remitente(y por ende cada destinario) manualmente.
API.broadcast(froms, ChatLimiter::add);
```

1.1. `Broadcast de medio nivel.`

   Esta variante de la anterior, es la simplificacion del segundo parametro, por defecto se usara ChatLimiter. Ejemplo:

```java
API.broadcast(froms);
```

2.0. `Broadcast de alto nivel.`

   Esta variante es similar a la primera. Te permite enviar un mensaje a todos los jugadores mas facilmente. El parámetro `model` es un modelo de mensaje que se enviará a todos los jugadores online. La función se encargará de clonar inicialmente el mensaje modelo y luego modificarlo en función de cada jugador antes de enviarlo, ademas de cancelar cada remitente tras su primer uso para evitar duplicaciones inesperadas del mensaje por cada destinario.

```java
// Crear el mensaje modelo para los destinarios.
Message to_model = new Message();
  ...
  to_model.getMessages().setFormats("$ct_messages$");

// Crear el mensaje para el remitente.
Message from_model = new Message();
  ...
from_model.setTo(to_model);

// Enviar el mensaje de cada objeto Message a todos los jugadores en línea(Incluyendo from) usando el mensaje modelo atravez del ChatLimiter.
API.broadcast(from_model, util.getOnlinePlayers(), API::broadcast);
```

2.0.1. `Broadcast junto con util.createChat.`

```java
Message model = util.createChat(
    sender,
    new String[] { messages },
    langSource,
    langTarget,
    null // null hace referencia al chat normal.
);

API.broadcast(model);
```

2.1. `Broadcast para tambien la consola!`

   Esta variante de la anterior, permite ejecutar demas acciones, permitiendo la oportunidad de agregar por ejemplo el message de la consola u otras acciones. Ejemplo:

```java
Message model = util.createChat(
    player,
    new String[] { mensajes },
    from_lang,
    from_lang,
    null
);

Message console = util.createChat(
        Bukkit.getConsoleSender(),
        new String[] { mensajes },
        from_lang,
        API.getLang(Bukkit.getConsoleSender()),
        "console")
    // Esto es para que sean accesibles las %varaibles% del remitente desde el destinario.
    .setSender(event.getPlayer());

    // Evitar duplicacion para el remitente.
    .setCancelledThis(true);

API.broadcast(model, util.getOnlinePlayers(), froms -> {
    froms.add(console);
    API.broadcast(froms);
});
```

En todos los casos, los mensajes se enviarán a los remitentes y destinatarios especificados con sus respectivas traducciones y personalizaciones.

3.0. `Lanzar el evento Message`

   Finalmente, si desea enviar al sendMessage y al mismo tiempo notificar a todos sobre este mensaje, puede hacerlo de la siguiente forma:
```java
Bukkit.getPluginManager().callEvent(from);
```

Recuerde que el objeto Message es tambien un evento ademas de contenedor tipo clase para el grupo de formato.

### Establecer el idioma de un jugador:

```java
void setLang(Object sender, String lang);
```

Este método te permite establecer el idioma de un jugador en específico tanto fuera como en linea o de la consola.

Parámetros:
- `sender`: El jugador(Player/OfflinePlayer) o consola(CommandSender) al que se le establecerá el idioma.
- `lang`: El idioma que deseas establecer. Debe ser un código de idioma válido, por ejemplo, "es" para español, "en" para inglés, etc.

**Ejemplo de uso:**

```java
Player player = ...        // Obtener el jugador al que se le establecerá el idioma
String lang = "es";        // Idioma español
API.setLang(player, lang); // Establecer el idioma del jugador

CommandSender consola = ... // Obtener la consola al que se le establecerá el idioma
String lang = "en";         // Idioma ingles
API.setLang(consola, lang); // Establecer el idioma del jugador

OfflinePlayer player = ... // Obtener el jugador offline al que se le establecerá el idioma
String lang = "auto";         // Idioma automatico
API.setLang(player, lang); // Establecer el idioma del jugador
```

Con este método, puedes asegurarte de que cada jugador o la consola tenga el idioma correcto configurado y recibir mensajes traducidos en su idioma preferido.

---

## Clase Message

La clase `Message` es un un evento custom que te permite construir y manipular mensajes de chat personalizados. Tiene varios métodos y atributos para facilitar la personalización de mensajes. A continuación, se describen los métodos más importantes:

### Constructor

```java
Message(); // Por defecto tendra el formato adecuado para la terminal / consola.
```

### Métodos del mensaje

```java
// Establece el destinario como otro objeto Message para este remitente.
Message setTo(Message to)
// Obtiene el destinario como otro objeto Message.
Message getTo()

// Establece el jugador(online) o consola.
Message setSender(CommandSender sender)
// Obtiene el sender.
CommandSender getSender()

// Cambia el nombre del sender(Solo a nivel de ChT).
String setSenderName()
// Obtiene el nombre del sender.
String getSenderName()

// Obtiene los formatos de los mensajes/toolTips.
Formats getMessages()
Formats getToolTips()

// Establece los sonidos.
Message setSounds(String sounds)
// Establece un sonido.
Message setSound(int index, String sound)
// Obtiene un sonido en especifico.
String getSound(int index)
// Obtiene todos los sonidos.
String getSounds()

// Indica si este mensaje se mostrará o no.
Message setCancelledThis(boolean isCancelled)

// Indica si este chat se mostrará o no.
Message setCancelled(boolean isCancelled)

// Establece el idioma origen.
Message setLangSource(String lang)
// Obtiene el idioma de origen.
String getLangSource()

// Establece el idioma destino.
Message setLangTarget(String lang)
// Obtiene el idioma destino.
String getLangTarget()

// Indica si se utilizará PlaceholderAPI para formatear el mensaje.
Message setFormatPAPI(boolean formatPAPI)
// Obtiene si se utilizará PlaceholderAPI.
Boolean getFormatPAPI()

// Indica si se forzara a usar los colores, caso contrario dependera del permiso.
Message setForceColor(boolean color)
// Obtiene si se forzara los colores.
Boolean isForceColor()
```

### Métodos de los formatos.
```java
void setFormat(int index, String format)
void setText(int index, String text)

void setFormats(String... formats)
void setTexts(String... texts)

String getFormat(int index)
String getText(int index)

String[] getFormats()
String[] getTexts()
```

### Clonación del mensaje

```java
Message clone();
```

Este método crea una copia exacta del mensaje actual, lo que te permite modificar la copia sin afectar al mensaje original.

### Método `toJson`

```java
String toJson();
```

Este método devuelve una representación en formato JSON del mensaje actual. Es útil para guardar y cargar mensajes desde y hacia archivos de configuración.

### Método `fromJson`

```java
Message fromJson(String json);
```

El método `fromJson` es una función estática que te permite crear una instancia de la clase `Message` a partir de una cadena de datos en formato JSON. Esta función es útil cuando deseas cargar mensajes de forma dinamica, desde archivos de configuración o guardar mensajes en una base de datos.

El parámetro `json` debe ser una cadena de datos en formato JSON que contenga la información necesaria para construir el mensaje. La cadena debe estar en el siguiente formato:

```json
{
  "from": {
    "senderName": "Juan",
    "messages": {
      "formats": ["..."],
      "texts": ["..."]
    },
    "toolTips": {
      "formats": ["..."],
      "texts": ["..."]
    },
    "sounds": ["..."],
    "isCancelled": false,
    "langSource": "es",
    "langTarget": "en",
    "isColor": true,
    "isPAPI": true
  },

  "to": {
    // ...
  }
}

```

Este método te permite cargar mensajes previamente guardados y utilizarlos como modelos para enviar mensajes personalizados a los jugadores.

---

## Clase util:

Primero que nada, todos los metodos de esta clase son estaticos.

### Obtener la version del servidor.

```java
double getMinecraftVersion();
```

Este devolvera la version como un double pero sin el primer `1.`, por ejemplo 16.5, 8.8, 20.1, etc. TENER CUIDADO CON LA 1."7.10", pues la unica forma de comprobarlo es `if (value > 7.9) {...}`
  Sorry, pequeña limitacion por ahora...

### Comprobar si existe y si es true una config booleana(Hoy dia en desuso).

```java
boolean IF(FileConfiguration cfg, String path);
```

### Lanzar excepcion junto con un texto si el idioma no esta soportado.

```java
void assertLang(String lang, String text);
```

O tambien se puede llamar sin el text pero este sera: `"El lenguaje '" + lang + "' no esta soportado."`:

```java
void assertLang(String lang);
```

### Obtener un Message por defecto ideal para la consola.

```java
public static Message getDataConfigDefault() {
    return new Message().setLangTarget(API.getLang(Bukkit.getConsoleSender()));
}
```

### Crear chat pre definido en config.yml.

```java
Message createChat(CommandSender sender, String[] messages, String langSource, String langTarget, String path)
```

A este punto, se deberia de entender todos los argumentos excepto `path`. Este ultimo sirve para acceder al grupo de formato solo nombrandolo sin el `from_` ni `to_`. Este metodo se encarga de crear el `from` y `to` necesario para ser usado en broadcast.

---