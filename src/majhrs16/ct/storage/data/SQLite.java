package majhrs16.ct.storage.data;

import java.sql.DriverManager;
import java.sql.SQLException;

import majhrs16.ct.storage.SQL;

public class SQLite extends SQL {
	public void connect() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		conn = DriverManager.getConnection("jdbc:sqlite:plugins/ChatTranslator/" + database + ".db");
	}
}