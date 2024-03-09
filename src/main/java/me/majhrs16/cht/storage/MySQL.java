package me.majhrs16.cht.storage;

import me.majhrs16.cht.ChatTranslator;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL {
	public MySQL() {
		super("com.mysql.jdbc.Driver", "mysql");
	}

	public void connect() throws SQLException {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			ChatTranslator.getInstance().logger.error(e.toString());
			return;
		}

		String url = "jdbc:" + type + "://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
		conn = DriverManager.getConnection(url, user, password);
	}
}