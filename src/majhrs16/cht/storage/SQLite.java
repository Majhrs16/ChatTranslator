package majhrs16.cht.storage;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQL {
	public SQLite() {
		super("org.sqlite.JDBC", "sqlite");
	}

	public void connect() throws SQLException {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String url = "jdbc:sqlite:" + database;
		conn = DriverManager.getConnection(url);
	}
}