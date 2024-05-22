## Chat Format Group.
Chat Format Groups are incredibly useful and powerful even without full modularity as in previous versions.

```yaml
FormatGroupName:
  messages:
    texts:
      - "This will be translated!"

    formats:
      - '{"text": "Here are the JSON message formats that will be sent", "color": "#FF0000"}'
      - "#00FF00 Here are the message formats that will be sent"
      - "&9This will not be translated, {0}"

  toolTips:
    formats:
      - "#00FF7F Here are the tooltips that will be sent"

  sounds:
    <Here goes the sound name>:
      volume: 1
      pitch: 1

    ORB_PICKUP:
      volume: 1
      pitch: 1
```

### ToolTips
This will be translated as long as the language between the sender and recipient is different. But at the same time, you will still have access to the variables.

---

## Local and External Variables.
Important things to note:
- These 2 types of variables can be accessed from message formats and tooltips between sender and recipient.
  - Modern `$variables$` will be for the recipient for both PlaceholderAPI and local ones.
  - Classic `%variables%` will be for the sender for both PlaceholderAPI and local ones.
- Case doesn't matter, they will always be processed (At the request of DracoHero).
- ChT supports %sub {variables}%.


Local variables: Without the need for PlaceholderAPI...
- `%ct_expand%` Expands horizontally to the maximum width of the chat (Can be used multiple times).
- `%player_name%` Will be replaced by the sender's name.
- `$player_name$` Will be replaced by the recipient's name.
- `%ct_messages%` Will normally be replaced by the original untranslated messages.
- `$ct_messages$` Will normally be replaced by the already translated messages.
- `%ct_toolTips%` Will normally be replaced by the original untranslated tooltips.
- `$ct_toolTips$` Will normally be replaced by the already translated tooltips.
- `%ct_lang_source%` Will be replaced by the sender's initial language.
- `$ct_lang_source$` Will be replaced by the recipient's initial language.
- `%ct_lang_target%` Will be replaced by the sender's target language.
- `$ct_lang_target$` Will be replaced by the recipient's target language.

External variables:
- Consult the [CoreTranslator](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#coretranslator) section for more details on external variables.
- Basically any PlaceholderAPI variable is available in message formats and tooltips.

---

## Usage Examples:
### No chat, ideal for login lobbies ;D
```yaml
from:
  sourceLang: es
  messages:
    texts:
      - "No esta permitido hablar"

    formats:
      - "&c{0}&f."
```

### Classic style chat!

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

### Partial support for other chat plugins.
PlaceholderAPI is required for this example.

```yaml
# FORMATS.yml
to:
  messages:
    formats:
    - '&e[&6%cot_translate; en; %ct_lang_target%; Translated%&e]'
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

### Maximum customization with JSON!!

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

### Conditional chat with ConditionalEvents (Advanced)
For this particular example, a few dependencies are needed:
- ConditionalEvents
- ChatTranslator
- PlaceholderAPI
  - Luckperms
- Luckperms

To use this configuration as is, you must have 2 groups in Luckperms beforehand:
- default
- owner

```yaml
  ChT_Enhancer:
    type: custom
    custom_event_data:
      event: me.majhrs16.cht.events.custom.Message
      player_variable: getSender()
      variables_to_capture:
        - '%uuid%;getUUID()'
        - '%last_path%;getLastFormatPath()'

    conditions:
      - "%last_path% !contains mention"
      - "%luckperms_in_group_default% equals no"
      - "%luckperms_in_group_owner% equals yes execute owner"
    
    actions:
      owner:
        - 'console_message: %cot_var; {uuid}; #from.format("from_owner")%'
        - 'console_message: %cot_var; {uuid}; #to.format("to_owner")%'
```

---

## CoreTranslator
This is an expansion for PlaceholderAPI integrated into ChatTranslator, explained in detail below how to use it:

Keep in mind:
- CoreTranslator supports %sub {variables}%
- Everything {enclosed} in braces are external variables provided by ConditionalEvents (captured) or PlaceholderAPI.

### Modify a message on the fly.
`cot_var` allows not only reformatting a message on the fly, but also accessing **every function** of the event. This means there is now free rein for your imagination.

Syntax:
`%cot_var; <uuid>; <method>%`

Examples:
`%cot_var; <uuid>; #from.format("from_My_group_format")%`
`%cot_var; <uuid>; #to.format("to_My_group_format")%`
`%cot_var; <uuid>; #to.getMessages().getText(0)%`
`%cot_var; <uuid>; #to.getSenderName()%`

### Send a message to Discord using DST.
sendMessage allows sending a message to Discord (if none are canceled before) in JSON format.

`%cot_sendDiscord; <uuid>%`

It should be mentioned that:
- The sender, despite not being canceled, will never be sent. It will only be used to access its variables.
- The recipient would be the message that reaches config.discord.channels.chat. And **NOT** TO EACH USER.

### Send a message to the sender and recipient.
sendMessage allows sending a new message to both the sender and the recipient at the same time (if none are canceled before).

`%cot_send; <uuid>%`

### Placeholder function for custom broadcast
This function allows broadcasting a custom model to multiple players in a very configurable way.

`%cot_broadcast; <uuid>%`

Remember that the model must be built like this:
- from: A normal format.
- to: The sender and targetLang should be `null`, as they will be replaced by each recipient's.

### Translate a text.
Translate allows only translating a message from one language to another.

`%cot_translate; <sourceLang>; <targetLang>; <mensaje>%`

### Get a player's language.
To get a player's language, you can use: `%cot_lang; <player_name>%`.

---

These approaches provide a flexible way to convey messages with different formats and recipients while leveraging the translation and customization capabilities provided by ChT. For more details on the available methods, you can consult the [Wiki API](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-API#clase-message) section `Message Class`.
```