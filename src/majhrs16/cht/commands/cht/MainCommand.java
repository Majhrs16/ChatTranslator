package majhrs16.cht.commands.cht;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.Updater;
import majhrs16.cht.util.util;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.lib.storages.ParseYamlException;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

@Deprecated
public class MainCommand implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Message DC = util.getDataConfigDefault();
			DC.setSender(sender);
			DC.setLangTarget(API.getLang(sender));

		if (Config.DEBUG.IF()) {
			System.out.println("Debug, Name: " + sender.getName());
			System.out.println("Debug, Lang: " + DC.getLangTarget());
		}

		if(args.length < 1) {
			if (plugin.isDisabled())
				return false;

			new HelperCommand().show(sender);
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "version":
				if (plugin.isDisabled())
					return false;

				DC.setMessages(Texts.PLUGIN.TITLE.TEXT + Texts.PLUGIN.VERSION);
					API.sendMessage(DC);
				return true;

			case "reload":
				if (plugin.isDisabled())
					return false;

				if (!Permissions.chattranslator.ADMIN.IF(sender)) {
					DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
						API.sendMessage(DC);
					return false;
				}

				new Reloader(DC).reloadConfig();
				return true;

			case "lang":
				if (plugin.isDisabled())
					return false;
				
				SetterLang setter = new SetterLang();

				try {
					switch (args.length) {
						case 2: // /ct lang es
							setter.setLang(DC, args[1]);
							return true;

						case 3:  // /ct lang Majhrs16 es
							if (!Permissions.chattranslator.ADMIN.IF(sender)) {
								DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
									API.sendMessage(DC);
								return false;
							}

							setter.setLangAnother(DC, args[1], args[2]);
							return true;

						default:
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
				if (!Permissions.chattranslator.ADMIN.IF(sender)) {
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

				try {
					plugin.config.reset();
					new Updater().updateConfig();
					DC.setMessages("&aSe ha restablecido la config exitosamente&f.");

				} catch (ParseYamlException e) {
					DC.setMessages("&cNO se ha podido restablecer la config&f.");
				}

				API.sendMessage(DC);
				return true;

			default:
				if (plugin.isDisabled())
					return false;

				DC.setMessages("&7Ese comando &cno &7existe&f!");
					API.sendMessage(DC);
				return false;
		}
	}
}