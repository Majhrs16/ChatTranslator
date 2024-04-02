package me.majhrs16.cht.commands.cht;

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Test implements CommandExecutor, TabCompleter {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		API.sendMessage(DC.format("commands.main.version"));
		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
		plugin.logger.warn(String.join(" ", args));

		return null;
	}
}
