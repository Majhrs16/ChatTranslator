package majhrs16.cht.util.cache;

import majhrs16.cht.events.custom.Message;
import org.bukkit.command.CommandSender;

public enum Permissions {
	;

	public enum ChatTranslator {
		ADMIN 		("ChatTranslator.admin");
		
		private String path;

		ChatTranslator(String path) {
			this.path = path;
		}

		public boolean IF(CommandSender sender) {
			return sender.hasPermission(path);
		}

		public enum Chat {
			MESSAGES	("ChatTranslator.chat.%s.messages"),
			TOOL_TIPS	("ChatTranslator.chat.%s.toolTips"),
			SOUNDS		("ChatTranslator.chat.%s.sounds"),
			COLOR		("ChatTranslator.chat.%s.color");

			// ChatTranslator.chat.FROM.toolTips

			private final String path;

			Chat(String path) {
				this.path = path;
			}

			public boolean IF(Message original) {
				if (original.getSender() == null)
					return false;

				else if (original.getSender().hasPermission(String.format(path, "*")))
					return true;

				else if (path.endsWith("messages"))
					return original.getSender().hasPermission(String.format(path, original.getLastFormatPath()));

				else if (path.endsWith("toolTips"))
					return original.getSender().hasPermission(String.format(path, original.getLastFormatPath()));

				else if (path.endsWith("sounds"))
					return original.getSender().hasPermission(String.format(path, original.getLastFormatPath()));

				else if (path.endsWith("color"))
					return original.getSender().hasPermission(String.format(path, original.getLastFormatPath()));

				else
					return false;
			}
		}
	}
}