package me.majhrs16.cht.storage;

import me.majhrs16.cht.ChatTranslator;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class SQLite extends SQL {
	public SQLite() {
		super("org.sqlite.JDBC", "sqlite");
	}

	public void connect() throws SQLException {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			ChatTranslator.getInstance().logger.error(e.toString());
			return;
		}

		String url = "jdbc:sqlite:" + ChatTranslator.getInstance().getDataFolder() + File.separator + database + ".db";
		conn = DriverManager.getConnection(url);
	}
}