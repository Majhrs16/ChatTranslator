package majhrs16.ct.commands.cht;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.Updater;
import majhrs16.ct.util.util;

public class MainCommand implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		String lang = API.getLang(sender);

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(sender);
			DC.setLang(lang);

		if (util.IF(config, "debug")) {
			System.out.println("Debug, Name: " + sender.getName());
			System.out.println("Debug, Lang: " + lang);
		}

		if(args.length < 1) {
			if (!plugin.enabled)
				return false;
	
			new HelperCommand().show(sender);
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "version":
				if (!plugin.enabled)
					return false;

				DC.setMessages("&7Version&f: &a" + plugin.version);
					API.sendMessage(DC);
				return true;

			case "reload":
				if (!plugin.enabled)
					return false;

				if (!sender.hasPermission("ChatTranslator.admin")) {
					DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
						API.sendMessage(DC);
					return false;
				}

				new Reloader().reloadConfig(DC);
				return true;

			/*
//				Me parece innecesario, CoT puede esto y mas!!

				case "parse":
					if (!plugin.enabled)
						return false;

					if (!sender.hasPermission("ChatTranslator.admin")) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					String msgFormat = String.join(" ", args);

					DC.setMessageFormat(msgFormat.replaceFirst("parse", ""));
					DC.setMessages("&eDato de ejemplo");
						API.sendMessage(DC);
					return true;
				*/

			case "lang":
				if (!plugin.enabled)
					return false;
				
				SetterLang setter = new SetterLang();

				try {
					switch (args.length) {
						case 2: // /ct lang es
							setter.setLang(DC, args[1]);
							return true;

						case 3:  // /ct lang Majhrs16 es
							if (!sender.hasPermission("ChatTranslator.admin")) {
								DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
									API.sendMessage(DC);
								return false;
							}

							setter.setLangAnother(DC, args[2], args[3]);
							return true;

						default:
							util.assertLang(config.getString("default-lang"), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
							DC.setMessages("&cSintaxis invalida&f. &aPor favor use la sintaxis&f:\n    &e/ct lang &f[&6player&f] &f<&6codigo&f>&f.");
								API.sendMessage(DC);
							return false;
					}

				} catch (IllegalArgumentException e) {
					DC.setMessages(e.getMessage());
						API.sendMessage(DC);
					return false;
				}

			case "toggle":
				if (!sender.hasPermission("ChatTranslator.admin")) {
					DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
						API.sendMessage(DC);
					return false;
				}

				Toggler tog = new Toggler();

				switch (args.length) {
					case 1:
						tog.TogglePlugin(sender);
						return true;
					
					case 2:
						tog.ToggleOffPlayer(DC, args[1]);
						return true;
						
					default:
						return false;
				}

			case "reset":
				DC.setMessages("&aRestableciendo la config&f...");
					API.sendMessage(DC);

				plugin.resetConfig();
				new Updater().updateConfig();

				DC.setMessages("&aSe ha restablecido la config exitosamente&f.");
					API.sendMessage(DC);
				return true;

			default:
				if (!plugin.enabled)
					return false;

				DC.setMessages("&7Ese comando &cno &7existe&f!");
					API.sendMessage(DC);
				return false;
		}
	}
}