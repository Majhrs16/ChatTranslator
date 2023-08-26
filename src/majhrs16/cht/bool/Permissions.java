package majhrs16.cht.bool;

import org.bukkit.command.CommandSender;

public enum Permissions {
	;

	public enum chattranslator {
		ADMIN 		("ChatTranslator.admin");
		
		private String path;

		chattranslator(String string) {
			path = string;
		}

		public boolean IF(CommandSender sender) {
			return sender.hasPermission(path);
		}

		public enum Color {
			FROM_COLOR	("ChatTranslator.chat.from.color"),
			TO_COLOR	("ChatTranslator.chat.to.color");

			private String path;

			Color(String string) {
				path = string;
			}

			public boolean IF(CommandSender sender) {
				return sender.hasPermission(path);
			}
		}
	}
}