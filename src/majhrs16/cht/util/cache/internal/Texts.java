package majhrs16.cht.util.cache.internal;

import majhrs16.cht.ChatTranslator;
import majhrs16.lib.BaseLibrary;

public class Texts {
	public static class KERNEL {
		@Config("kernel.version")
		public static String VERSION;
	}

	public static class PLUGIN {
		@Config("plugin.name")
		public static String NAME;

		@Config("plugin.version")
		public static String VERSION;

		@Config("plugin.enable")
		public static String ON;

		@Config("plugin.disable")
		public static String OFF;

		public static class TITLE {
			@Config("plugin.title.text")
			public static String TEXT;

			@Config("plugin.title.UTF-8")
			public static String UTF_8;
		}

		public static class IS_UTF_8 {
			@Config("plugin.is-UTF-8.true")
			public static String YES;
	
			@Config("plugin.is-UTF-8.false")
			public static String NO;
		}
	}
	
	public static class STORAGE {
		public static class OPEN {
			public static class YAML {
				@Config("storage.open.yaml.ok")
				public static String OK;

				@Config("storage.open.yaml.error")
				public static String ERROR;
			}

			public static class SQLITE {
				@Config("storage.open.sqlite.ok")
				public static String OK;

				@Config("storage.open.sqlite.error")
				public static String ERROR;
			}

			public static class MYSQL {
				@Config("storage.open.mysql.ok")
				public static String OK;

				@Config("storage.open.mysql.error")
				public static String ERROR;
			}
		}
	
		public static class CLOSE {
			public static class YAML {
				@Config("storage.close.yaml.ok")
				public static String OK;

				@Config("storage.close.yaml.error")
				public static String ERROR;
			}

			public static class SQLITE {
				@Config("storage.close.sqlite.ok")
				public static String OK;

				@Config("storage.close.sqlite.error")
				public static String ERROR;
			}

			public static class MYSQL {
				@Config("storage.close.mysql.ok")
				public static String OK;

				@Config("storage.close.mysql.error")
				public static String ERROR;
			}
		}
	}

	public static class COMMANDS {
		public static class MAIN {
			public static class VERSION {
				@Config("commands.main.showVersion.text")
				public static String TEXT;

				@Config("commands.main.showVersion.toolTips")
				public static String TOOLTIPS;
			}
		}
	}

	@Config("separator")
	public static String SEPARATOR;

	public static String _VERSION = BaseLibrary.version;
	public static String VERSION  = ChatTranslator.getInstance().getDescription().getVersion();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reload() {
		new TextsHandler().reload();
	}
}