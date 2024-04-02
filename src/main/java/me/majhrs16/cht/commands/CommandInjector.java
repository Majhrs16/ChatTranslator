package me.majhrs16.cht.commands;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.command.*;
import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

public class CommandInjector {
	public static void injectCommand(String commandName, CommandExecutor executor, TabCompleter completer) {
		try {
			// Obtener el gestor de comandos
			Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());

			// Crear y registrar el nuevo comando con su CommandExecutor
			BukkitCommand bukkitCommand = new BukkitCommand(commandName) {
				public boolean execute(
						@NotNull CommandSender sender,
						@NotNull String alias,
						@NotNull String[] args) {

					return executor.onCommand(sender, this, alias, args);
				}

				@NotNull
				public List<String> tabComplete(
						@NotNull CommandSender sender,
						@NotNull String alias,
						@NotNull String[] args) throws IllegalArgumentException {

					List<String> completions = completer == null ? null : completer.onTabComplete(sender, this, alias, args);
					return completions != null ? completions : super.tabComplete(sender, alias, args);
				}
			};

			commandMap.register(commandName, bukkitCommand);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void injectCommand(String commandName, CommandExecutor executor) {
		injectCommand(commandName, executor, null);
	}

	public static void unregisterCommand(String commandName) {
		try {
			// Obtener el gestor de comandos
			Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

			// Obtener el comando y eliminarlo de la lista interna
			Command command = commandMap.getCommand(commandName);
			if (command != null)
				command.unregister(commandMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
