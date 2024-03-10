package me.majhrs16.cht.util.updater;

import org.bukkit.configuration.file.FileConfiguration;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.ArrayList;

public class CommandsUpdater {
    public int version;

    public CommandsUpdater() {
        ChatTranslator plugin = ChatTranslator.getInstance();
        FileConfiguration config = plugin.commands.get();

        version = config.getInt("config-version");
        int version_original = version;

        Message DC = new Message();;

        if (version < 2) {
            ArrayList<String> linker_tool_tips = new ArrayList<>();
                linker_tool_tips.add("&aVincula &7tu cuenta de &aMinecraft &7con tu cuenta de &9Discord&f.");

            ArrayList<String> private_tool_tips = new ArrayList<>();
                private_tool_tips.add("&aEnvia mensajes privados a alguien :D&f.");

            config.set("cht.link.type", "linker");
            config.set("cht.link.text", "&e    link");
            config.set("cht.link.suggest", "cht link");
            config.set("cht.link.toolTips", linker_tool_tips);

            config.set("cht.tell.type", "private");
            config.set("cht.tell.text", "&f/&etell");
            config.set("cht.tell.suggest", "tell %player_name% texto de ejemplo");
            config.set("cht.tell.toolTips", private_tool_tips);
            version = 2;
        }

        if (version > version_original) {
            config.set("config-version", version);
            plugin.commands.save();

            DC.getMessages().setTexts(String.format(
                "&eSe han actualizado los comandos de la version &b%s &ea la &b%s&f.",
                version_original, version
            ));

            ChatTranslatorAPI.getInstance().sendMessage(DC);
        }
    }
}