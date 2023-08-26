# ChatTranslator

ChatTranslator es un plugin para Minecraft 1.8 - 1.20.1 que permite la traducción en tiempo real de mensajes entre jugadores que hablan diferentes idiomas. Con ChatTranslator, puedes comunicarte con jugadores de todo el mundo sin tener que preocuparte por las barreras del idioma.

## Características

- Utilización de la API de [Google Translate](https://translate.google.com/) para llevar a cabo traducciones al idioma establecido, abarcando el chat y los mensajes de la consola, entre otros.
- Configuración altamente personalizable para adecuar el formato del chat a las preferencias individuales, a través de `config.formats`.
- En situaciones en las que la conexión a internet no esté disponible, se añade el prefijo `[!]` a la variable local `ct_messages`.
- Los jugadores y la consola pueden especificar su idioma utilizando el comando `/cht lang [jugador] <código de idioma>`.
- Capacidades para la traducción de carteles, y aún más características planificadas para futuras actualizaciones.
- Funciones altamente personalizables que se pueden activar o desactivar según las necesidades.
- Compatibilidad con BungeeCord y la integración de bases de datos para un rendimiento óptimo.
- Detección automática del idioma del jugador para facilitar la comunicación.
- Opcionalmente, se puede aprovechar el potencial de las siguientes dependencias:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).
  - [ConditionalEvents](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/), brindando una manera sencilla de condicionar los chats.
- Soporte parcial para otros plugins de chat, tales como:
  - [Chatty](https://www.spigotmc.org/resources/chatty-lightweight-universal-bukkit-chat-system-solution-1-7-10-1-19.59411/), [ChatManager](https://www.spigotmc.org/resources/chat-manager-1-8-1-19-30-features-and-40-commands.52245/) y [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/) a través del [ejemplo de configuración #3](https://github.com/Majhrs16/ChatTranslator/wiki/ChatTranslator-Wiki#soporte-parcial-para-otros-plugins-de-chat).
  - Mediante el evento `majhrs16.cht.events.custom.Message`.

## Instalación

Para instalar ChatTranslator, sigue los siguientes pasos:

1. Descarga la ultima version de ChatTranslator desde [Spigot](https://www.spigotmc.org/resources/chattranslator.106604/) o [GitHub](https://github.com/CreativeMD/ChatTranslator/releases).
2. Copia el archivo .jar de ChatTranslator en la carpeta de plugins de tu servidor.
3. Reinicia tu servidor.

Una vez instalado, puedes personalizar la configuración de ChatTranslator modificando el archivo `config.yml` en la carpeta del plugin.

## Uso

ChatTranslator se activa automáticamente en tu servidor cuando lo instalas. Y una vez empiezas a chatear, se traduciran entre si automaticamente(Por defecto, dependiendo del idioma de su Minecraft).

Si deseas cambiar tu idioma al automatico, puedes hacerlo usando el comando `/cht lang [jugador] auto`.

## Compilación(Para desarrolladores)

Para compilar el código fuente de ChatTranslator, sigue estos pasos:

1. Asegúrate de tener instalado Java Development Kit (JDK) 8 en tu sistema.
2. Clona este repositorio en tu máquina local utilizando el siguiente comando:

   ```
   git clone https://github.com/Majhrs16/ChatTranslator.git
   ```

3. Navega al directorio del proyecto:

   ```
   cd ChatTranslator
   ```

4. Descarga las dependencias requeridas y colócalas en la carpeta "libs" dentro del proyecto:

   - [ConditionalEvents.jar](https://www.spigotmc.org/resources/conditionalevents-custom-actions-for-certain-events-1-8-1-19-4.82271/)
   - [PlaceholderAPI.jar](https://www.spigotmc.org/resources/placeholderapi.6245/)
   - [Spigot 1.16.5.jar](https://getbukkit.org/download/spigot)

5. Y ya por ultimo, ejecuta el compile.bat:

   Esto compilará todos los archivos fuente y generará un ChatTranslator.jar.

Y eso es todo! :D

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
