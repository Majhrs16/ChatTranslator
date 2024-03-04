package majhrs16.dst.utils;

import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;

public class TerminalAnsiReader {
	public static void injectReader() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config  = context.getConfiguration();

		RollingRandomAccessFileAppender discordAppender = RollingRandomAccessFileAppender.newBuilder()
				.setConfiguration(config)
				.withName("TerminalLogger")
				.withFileName("logs/DST.log")
				.withFilePattern("logs/discord/%d{yyyy-MM-dd}-%i.log.gz")
				.withLayout(PatternLayout.newBuilder()
						.withPattern("[%d{HH:mm:ss}] [%t/%level]: %msg{nolookups}%n")
						.build())
				.withPolicy(OnStartupTriggeringPolicy.createPolicy(1L))
				.build();

		discordAppender.start();

		LoggerConfig terminalConsoleConfig = config.getLoggerConfig("TerminalConsole");
		terminalConsoleConfig.addAppender(discordAppender, Level.INFO, null);

		context.updateLoggers();
	}
}
