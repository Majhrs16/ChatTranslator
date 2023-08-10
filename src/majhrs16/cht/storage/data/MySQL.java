package majhrs16.cht.storage.data;

import majhrs16.cht.storage.SQL;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL {
	public void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
//			Class.forName("org.mariadb.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		conn = DriverManager.getConnection(url, user, password);
	}
}
