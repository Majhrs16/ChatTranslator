## Grupo de formato de chats.
Los "Grupos de formatos de chats" son increiblemente utiles y poderosos incluso sin la total modularidad como en las versiones anteriores.

```yaml
NombreDelGrupoDeFormato:
  messages:
    texts:
      - "Esto se traducira!"

    formats:
      - '{"text": "Aqui van los formatos de mensajes JSON que se enviaran", "color": "#FF0000"}'
      - "#00FF00 Aqui van los formatos de mensajes que se enviaran"
      - "&9Esto no se traducira, {0}"

  toolTips:
    formats:
      - "#00FF7F Aqui van los tooltips que se enviaran"

  sounds:
    <Aqui va el nombre del sonido>:
      volume: 1
      pitch: 1

    ORB_PICKUP:
      volume: 1
      pitch: 1
```

### ToolTips
Este se traducira siempre y cuando sea diferente el idioma entre el remitente y destinario. Pero al mismo tiempo tendras igualmente acceso a las variables.

--- 

## Variables locales y externas.
Hay que tener en cuenta unas cosas importantes:
- Estos 2 tipos de variable se pueden acceder desde los formatos de mensajes y tooltips entre remitente y destinario.
  - Las modernas `$variables$` seran del destinario tanto para PlaceholderAPI como para las locales.
  - Las `%variables%` clasicas seran del remitente tanto para PlaceholderAPI como para las locales.
- No importa las mAyUsCuLaS, siempre se procesaran(A peticion de DracoHero).
- ChT admite %sub {variables}%.


Variables locales: Sin necesidad de PlaceholderAPI...
- `%ct_expand%` Expande horizontalmente hasta el maximo del ancho del chat(Se puede usar varias veces).
- `%player_name%` Se remplazara por el nombre del remitente.
- `$player_name$` Se remplazara por el nombre del destinario.
- `%ct_messages%` Se remplazara normalmente por los mensajes originales sin traducir.
- `$ct_messages$` Se remplazara normalmente por los mensajes ya traducidos.
- `%ct_tooLTips%` Se remplazara normalmente por los mensajes originales sin traducir de lps toolTips.
- `$ct_tooLTips$` Se remplazara normalmente por los mensajes ya traducidos de los toolTips.
- `%ct_lang_source%` Se remplazara por el idioma inicial del remitente.
- `$ct_lang_source$` Se remplazara por el idioma inicial del destinario.
- `%ct_lang_target%` Se remplazara por el idioma destino del remitente.
- `$ct_lang_target$` Se remplazara por el idioma destino del destinario.

Variables externas:
- Consulta la sección de [CoreTranslator](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#coretranslator) para más detalles sobre las variables externas.
- Basicamente cualquier variable de PlaceholderAPI esta disponible en los formatos de mensajes y toolTips.

---

## Ejemplos de uso:
### Sin chat, ideal para lobbys de login ;D
```yaml
from:
  messages:
    texts:
      - "No esta permitido hablar"

    formats:
      - "&c{0}&f."
```

### Chat al estilo clasico!
```yaml
from:
  messages:
    formats:
      - "&f<&b%player_name%&f> &a%ct_messages%"

to:
  messages:
    formats:
      - "&f<&b%player_name%&f> &a$ct_messages$"

  toolTips:
    formats:
      - "&f[&6%ct_lang_source%&f] &a%ct_messages%"
```

### Soporte parcial para otros plugins de chat.

```yaml
# FORMATS.yml
to:
  messages:
    texts:
      - Traduccion

    formats:
      - '&e[&6{0}&e]'
      - ''

  toolTips:
    formats:
    - '&f[&6%ct_lang_source% &f-> &6$ct_lang_target$&f] &a%ct_messages%'

  sounds:
    ENTITY_EXPERIENCE_ORB_PICKUP:
      volume: 1
      pitch: 1

    ORB_PICKUP:
      volume: 1
      pitch: 1

# CONFIG.yml
show-native-chat:
  cancel-event: false
  clear-recipients: false
```

### Personalizacion al maximo con JSON!!

```yaml
to:
  messages:
    formats:
      - '{"text": "", "extra": [{"text": "&a%player_name%", "hoverEvent": {"action": "SHOW_TEXT", "value": "&f[&6%ct_lang_source% &f-> &6$ct_lang_target$&f]"}}, {"text": " &f> "}, {"text": "%ct_messages%", "hoverEvent": {"action": "SHOW_TEXT", "value": "&a%ct_messages%"}}]}'
  toolTips:
    texts: []
    formats: []
  sounds:
    ENTITY_EXPERIENCE_ORB_PICKUP:
      volume: 1
      pitch: 1

    ORB_PICKUP:
      volume: 1
      pitch: 1
```

### Chat condicionado con ConditionalEvents(Avanzado)
Para este ejemplo en concreto, se necesita unas cuantas dependencias:
- ConditionalEvents
- ChatTranslator
- PlaceholderAPI
  - Luckperms
- Luckperms

Para usar esta configuracion tal cual, debe de tener previamente 3 grupos en Luckperms:
- default) Contiene el prefijo user
- vip) Hereda de default, y contiene el prefijo vip
- owner) Hereda de vip, y contiene el prefijo owner

```yaml
  ChT_Enhancer:
    type: custom
    custom_event_data:
      event: me.majhrs16.cht.events.custom.Message
      player_variable: getSender()
      variables_to_capture:
      - '%uuid%;getUUID()'
      - '%type%;getSenderType()'

    conditions:
    - "'%luckperms_prefix%' != ''"
    - "%luckperms_prefix% != user"
    - '%type% == CONSOLE execute to_owner_console'
    - "%luckperms_prefix% == owner execute from_owner"
    
    actions:
      from_owner:
      - 'console_message: %cot_var; {uuid}; format("from_owner")% '
      - 'console_message: %cot_var; {uuid}; getTo().format("to_owner")%'

      to_owner_console:
      - 'console_message: %cot_var; {uuid}; getTo().format("to_owner_console")%'
```

---

## CoreTranslator
Es una expansion para PlaceholderAPI integrada en ChatTranslator, a continuacion se explicara a detalle como usarlo:

Tener en cuenta:
- CoreTranslator soporta %sub {variables}%
- Todo lo que está {encerrado} entre llaves son variables externas proporcionadas por ConditionalEvents(capturadas) o PlaceholderAPI.

### Modificar un mensaje al vuelo.
cot_var permite no solo re formatear un mensaje al vuelo, sino tambien acceder a **cada funcion** del evento. Esto significa que ahora hay rienda suelta para tu imagincion.

Sintaxis:
`%cot_var; <uuid>; <method>%`

Ejemplos:
`%cot_var; <uuid>; format("from_My_group_format")%`
`%cot_var; <uuid>; getTo().format("to_My_group_format")%`
`%cot_var; <uuid>; getTo().setLangTarget(null)%`
`%cot_var; <uuid>; getTo().setSender(null)%`

### Enviar un mensaje a Discord usando DST.
sendMessage permite enviar un mensaje a Discord(Si no se cancela ninguno antes) en formato JSON.

`%cot_sendDiscord; <uuid>%`

Cabe mencionar que:
- El remitente a pesar de no estar cancelado, nunca se enviara. Solo se usara para acceder a sus variables.
- El destinario seria el mensaje que le llegara a config.discord.channels.chat. Y **NO** A CADA USUARIO.

### Enviar un mensaje al remitente y destinario.
sendMessage permite enviar un mensaje nuevo al remitente y al destinario a la vez(Si no se cancela ninguno antes).

`%cot_send; <uuid>%`

### Función placeholder para broadcast personalizado
Esta función permite transmitir un model personalizado a múltiples jugadores de una manera muy configurable.

`%cot_broadcast; <uuid>%`

Recuerda que el modelo debe de estar construido asi:
- from: Un formato normal.
- to: El sender y targetLang deben ser `null`, ya que seran remplazados por el de cada destinario.

### Traducir un texto.
translate permite unicamente traducir un mensaje de un idioma a otro.

`%cot_translate; <sourceLang>; <targetLang>; <mensaje>%`

### Obtener el idioma de un jugador.
Para obtener el idioma de un jugador, puedes utilizar: `%cot_lang; <player_name>%`.

---

Estos enfoques brindan una forma flexible de transmitir mensajes con diferentes formatos y destinatarios, mientras aprovecha las capacidades de traducción y personalización proporcionadas por ChT. Para mas detalles acerca de los metodos disponibles, puedes consultar la [Wiki API](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-API#clase-message) seccion `Clase Message`.