# ChatTranslator v1.5.4

ChatTranslator es un plugin de Minecraft que permite la traducción en tiempo real de mensajes entre jugadores que hablan diferentes idiomas. Con ChatTranslator, puedes comunicarte con jugadores de todo el mundo sin tener que preocuparte por las barreras del idioma.

## Características

- Utiliza la API de Google Translate para traducir el chat, la consola y más al idioma establecido.
- Detección automática del idioma del jugador.
- Los jugadores o la consola pueden especificar su idioma utilizando el comando: /lang [código de idioma].
- Admite una amplia gama de idiomas.
- Personalización completa del formato del chat a través de `config.formats`.
- Opcionalmente, se puede utilizar la dependencia PlaceholderAPI. Sin embargo, es necesario instalar la extensión player para evitar problemas en el servidor.
- En caso de no tener conexión a Internet, se añade el prefijo "[ NO INTERNET ]" a \$ct_messages\$.
- Funciones personalizables y desactivables.
- Compatible con BungeeCord.
- Soporte para otros plugins de chat, incluyendo:
  - Soporte parcial para Chatty y ChatManager a través del ejemplo de config #3.
  - Soporte para condiciones en `config.formats` a través de ConditionalEvents.
  - A través del evento majhrs16.ct.events.custom.Message.
  - Soporte para DiscordSRV.

## Instalación

Para instalar ChatTranslator, sigue los siguientes pasos:

1. Descarga ChatTranslator desde [Spigot](https://www.spigotmc.org/resources/chattranslator.106604/) o [GitHub](https://github.com/CreativeMD/ChatTranslator/releases).
2. Copia el archivo .jar de ChatTranslator en la carpeta de plugins de tu servidor.
3. Reinicia tu servidor.

Una vez instalado, puedes personalizar la configuración de ChatTranslator modificando el archivo `config.yml` en la carpeta del plugin.

## Uso

ChatTranslator se activa automáticamente en tu servidor cuando lo instalas y te permite comunicarte con jugadores que hablan diferentes idiomas. Si deseas cambiar tu idioma, puedes hacerlo ingresando el comando `/lang [código de idioma]`.

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