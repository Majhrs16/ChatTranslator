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

			private String path;

			Chat(String path) {
				this.path = path;
			}

			public boolean IF(Message original) {
				if (original.getSender() == null)
					return false;

				else if (original.getSender().hasPermission(String.format(path, "*")))
					return true;

				else if (path.endsWith("messages") && original.getMessagesFormats() != null && !original.getMessagesFormats().isEmpty())
					return original.getSender().hasPermission(String.format(path, original.getMessageFormat(0)));

				else if (path.endsWith("toolTips") && original.getToolTips() != null && !original.getToolTips().isEmpty())
					return original.getSender().hasPermission(String.format(path, original.getToolTip(0)));

				else if (path.endsWith("sounds") && original.getSounds() != null && !original.getSounds().isEmpty())
					return original.getSender().hasPermission(String.format(path, original.getSound(0)));

				else if (path.endsWith("color") && original.getMessagesFormats() != null && !original.getMessagesFormats().isEmpty())
					return original.getSender().hasPermission(String.format(path, original.getMessageFormat(0)));

				else
					return false;
			}
		}
	}
}