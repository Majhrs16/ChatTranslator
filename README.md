# ChatTranslator

ChatTranslator es un plugin de Minecraft que permite la traducción en tiempo real de mensajes entre jugadores que hablan diferentes idiomas. Con ChatTranslator, puedes comunicarte con jugadores de todo el mundo sin tener que preocuparte por las barreras del idioma.

## Características

- Los jugadores o consola pueden especificar su idioma utilizando el comando: `/cht lang [jugador] <código de idioma>`.
- Utiliza la API de [Google](https://www.google.com/) [Translate](https://translate.google.com/) para traducir el chat, la consola y mas al idioma establecido.
- En caso de no tener conexión a internet, se añade el prefijo `[!]` a la variable local: `$ct_messages$`.
- Configuración MUY personalizable para el formato del chat a través de config.formats.
- Opcionalmente se puede utilizar las dependencias:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)) Pero seria necesario instalar la extensión player para evitar
    problemas en el servidor y automatizar la deteccion del idioma :3
  - [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)) Para traducir carteles y futuramente mas cosas! (Por limitaciones externas, solo hasta la 1.19).
  - [ConditionalEvents](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/)) Para condicionar el chat.
- Detección automática del idioma del jugador(Requiere PlaceholderAPI).
- Funciones personalizables y desactivables.
- Soporte con Bungeecord con bases de datos.
- Soporte para otros plugins de chat:
  - Soporte parcial para [Chatty](https://www.spigotmc.org/resources/chatty-lightweight-universal-bukkit-chat-system-solution-1-7-10-1-19.59411/) y [ChatManager](https://www.spigotmc.org/resources/chat-manager-1-8-1-19-30-features-and-40-commands.52245/) atravez del ejemplo de config #3,
  - Atravez del evento majhrs16.cht.events.custom.Message,
  - Soporte para DiscordSRV.

## Instalación

Para instalar ChatTranslator, sigue los siguientes pasos:

1. Descarga la ultima version de ChatTranslator desde [Spigot](https://www.spigotmc.org/resources/chattranslator.106604/) o [GitHub](https://github.com/CreativeMD/ChatTranslator/releases).
2. Copia el archivo .jar de ChatTranslator en la carpeta de plugins de tu servidor.
3. Reinicia tu servidor.

Una vez instalado, puedes personalizar la configuración de ChatTranslator modificando el archivo `config.yml` en la carpeta del plugin.

## Uso

ChatTranslator se activa automáticamente en tu servidor cuando lo instalas. Y si estan cumplidas todas las dependencias opcionales tendras una experiencia increible! =D

Si deseas cambiar tu idioma al automatico, puedes hacerlo usando el comando `/cht lang [jugador] auto`.

## Solución de problemas

Si tienes problemas con ChatTranslator, revisa la documentación del plugin y los recursos de soporte en los siguientes enlaces:

- [ChatTranslator wiki](https://github.com/Majhrs16/ChatTranslator/wiki)
- [Discord](https://discord.gg/kZxHnSVPTg)

## Contribución

Si deseas contribuir al desarrollo de ChatTranslator, puedes enviar solicitudes de extracción o informes de problemas al [repositorio de GitHub](https://github.com/Majhrs16/ChatTranslator) o atravez del [Discord](https://discord.gg/kZxHnSVPTg). Tu contribución es muy apreciada.

## Licencia

ChatTranslator está licenciado bajo la Licencia GPL v3.0. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## Soporte

Para soporte o ayuda, únete a nuestro servidor de [Discord](https://discord.gg/kZxHnSVPTg).

¡Gracias por usar ChatTranslator! Si tienes alguna pregunta o necesitas ayuda, no dudes en preguntar.
